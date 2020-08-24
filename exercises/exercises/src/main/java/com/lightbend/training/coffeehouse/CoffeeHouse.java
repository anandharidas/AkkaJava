package com.lightbend.training.coffeehouse;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import akka.japi.pf.ReceiveBuilder;
import akka.routing.FromConfig;
import scala.concurrent.duration.FiniteDuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class CoffeeHouse extends AbstractLoggingActor {


    private final FiniteDuration coffeeFinishedDuration =
            FiniteDuration.create(context().system().settings().config().
                            getDuration("coffee-house.guest.finish-coffee-duration",MILLISECONDS),
                    MILLISECONDS);
    private final FiniteDuration prepareCoffeeDuration =
            FiniteDuration.create(context().system().settings().config().
                            getDuration("coffee-house.barista.prepare-coffee-duration",MILLISECONDS),
                    MILLISECONDS);
    private final int baristaAccuracy =
            context().system().settings().config().getInt("coffee-house.barista.accuracy");


    private final int waiterMaxComplaintCount =
            context().system().settings().config().getInt("coffee-house.waiter.max-complaint-count");


    private final ActorRef barista = createBarista();
    private final ActorRef waiter = createWaiter();
    private final Map<ActorRef,Integer> guestBook = new HashMap<>();
    private final int caffeineLimit;

    protected ActorRef createWaiter() {
        log().warning("WARNING waiterMaxComplaintCount {}",waiterMaxComplaintCount);
        return getContext().actorOf(Waiter.props(self(),barista,waiterMaxComplaintCount),"waiter");
    }

    protected ActorRef createBarista() {
        log().warning("WARNING prepareCoffeeDuration {} and baristaAccuracy {}",
                prepareCoffeeDuration,baristaAccuracy);
        return getContext().actorOf(FromConfig.getInstance().props(
                Barista.props(prepareCoffeeDuration,baristaAccuracy)),
                "barista");
    }

    public CoffeeHouse(int caffeineLimit) {
        this.caffeineLimit = caffeineLimit;
        log().debug("CoffeeHouse Open");
    }
    public static Props props(int caffeineLimit) {
       return  Props.create(CoffeeHouse.class,
               () -> new CoffeeHouse(caffeineLimit));
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create().
                match(CreateGuest.class,
                        createGuest -> {
                               final ActorRef guest =
                                       createGuest(createGuest.favoriteCoffee,createGuest.guestCaffeineLimit);
                               addToGuestBook(guest);
                               context().watch(guest);
                }).
                match(ApproveCoffee.class, this::cofeeApproval, approveCoffee ->
                    barista.forward(new Barista.PrepareCoffee
                            (approveCoffee.coffee,approveCoffee.guest),context())).
                match(ApproveCoffee.class, approveCoffee -> {
                    log().info("Sorry {}, but you have reached your limit.",approveCoffee.guest);
                    context().stop(approveCoffee.guest);
                }).
                match(Terminated.class, terminated -> {
                    log().info("Thanks {}, for being our guest!",terminated.actor());
                    removeGuestFromGuestBook(terminated.actor());
                }).
                build();
    }


    @Override
    public SupervisorStrategy supervisorStrategy() {
        return new OneForOneStrategy(true,
                DeciderBuilder
                        .match(Guest.CaffeineException.class ,
                                e -> (SupervisorStrategy.Directive) SupervisorStrategy.stop())
                        .match(Waiter.FrustratedException.class,
                                (Waiter.FrustratedException e) -> {
                                barista.tell(new Barista.PrepareCoffee(e.coffee,e.guest),sender());
                                return (SupervisorStrategy.Directive)SupervisorStrategy.restart(); }
                                ).build().
                                            orElse(super.supervisorStrategy().decider()));
    }

    private boolean cofeeApproval(ApproveCoffee approveCoffee) {
        final int guestCaffeineCount = guestBook.get(approveCoffee.guest);
        if (guestCaffeineCount < caffeineLimit) {
            guestBook.put(approveCoffee.guest,guestCaffeineCount+1);
            log().info("Guest caffeine count incremented.",approveCoffee.guest);
            return true;
        } else {
            return false;
        }
    }

    private void addToGuestBook(ActorRef guest) {
        guestBook.put(guest,0);
        log().debug("Guest {} add to book", guest);
    }

    private void removeGuestFromGuestBook(ActorRef guest) {
        guestBook.remove(guest);
        log().debug("Removed guest {} from bookkeeper",guest);
    }

    protected ActorRef createGuest(Coffee coffee,int guestCaffeineLimit) {
        return context().actorOf(Guest.props(waiter,coffee,coffeeFinishedDuration,guestCaffeineLimit));
    }

    public static final class CreateGuest {
        public final Coffee favoriteCoffee;
        public final int guestCaffeineLimit;
        protected CreateGuest(Coffee favoriteCoffee,int guestCaffeineLimit)
        {
            checkNotNull(favoriteCoffee,"Favorite coffee cannot be null");
            checkNotNull(guestCaffeineLimit, "CaffeineLimit cannot be null");
            this.favoriteCoffee = favoriteCoffee ;
            this.guestCaffeineLimit = guestCaffeineLimit;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CreateGuest that = (CreateGuest) o;
            return guestCaffeineLimit == that.guestCaffeineLimit &&
                    Objects.equals(favoriteCoffee, that.favoriteCoffee);
        }

        @Override
        public int hashCode() {
            return Objects.hash(favoriteCoffee, guestCaffeineLimit);
        }

        @Override
        public String toString() {
            return "CreateGuest{" +
                    "favoriteCoffee=" + favoriteCoffee +
                    ", guestCaffeineLimit=" + guestCaffeineLimit +
                    '}';
        }
    }

    public static final class ApproveCoffee {
        public final Coffee coffee;
        public final ActorRef guest;

        public ApproveCoffee(Coffee coffee, ActorRef guest) {
            checkNotNull(coffee,"Coffee cannot be null");
            checkNotNull(guest, "Guest cannot be null");
            this.coffee = coffee;
            this.guest = guest;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ApproveCoffee that = (ApproveCoffee) o;
            return Objects.equals(coffee, that.coffee) &&
                    Objects.equals(guest, that.guest);
        }

        @Override
        public int hashCode() {
            return Objects.hash(coffee, guest);
        }

        @Override
        public String toString() {
            return "ApproveCoffee{" +
                    "coffee=" + coffee +
                    ", guest=" + guest +
                    '}';
        }
    }
}
