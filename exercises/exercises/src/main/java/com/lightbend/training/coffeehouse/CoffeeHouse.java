package com.lightbend.training.coffeehouse;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import scala.concurrent.duration.FiniteDuration;

import java.util.Objects;
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

    private final ActorRef barista = createBarista();
    private final ActorRef waiter = createWaiter();

    private ActorRef createWaiter() {
        return getContext().actorOf(Waiter.props(barista),"waiter");
    }


    private ActorRef createBarista() {
        return getContext().actorOf(Barista.props(prepareCoffeeDuration),"barista");
    }

    public CoffeeHouse() {
        log().debug("CoffeeHouse Open");
    }
    public static Props props() {
       return  Props.create(CoffeeHouse.class,CoffeeHouse::new);
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create().
                match(CreateGuest.class,
                        createGuest -> createGuest(createGuest.favoriteCoffee)).
                build();
    }

    protected void createGuest(Coffee coffee) {
        context().actorOf(Guest.props(waiter,coffee,coffeeFinishedDuration));
    }

    public static final class CreateGuest {
        public final Coffee favoriteCoffee;
        protected CreateGuest(Coffee favoriteCoffee) {this.favoriteCoffee = favoriteCoffee ;}

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CreateGuest that = (CreateGuest) o;
            return Objects.equals(favoriteCoffee, that.favoriteCoffee);
        }

        @Override
        public int hashCode() {
            return Objects.hash(favoriteCoffee);
        }

        @Override
        public String toString() {
            return "CreateGuest{" +
                    "favoriteCoffee=" + favoriteCoffee +
                    '}';
        }
    }

}
