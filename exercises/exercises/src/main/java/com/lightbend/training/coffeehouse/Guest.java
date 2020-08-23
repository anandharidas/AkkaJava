package com.lightbend.training.coffeehouse;

import akka.actor.ActorRef;
import akka.actor.Props;
import scala.concurrent.duration.FiniteDuration;

public class Guest extends AbstractLoggingActorWithTimers {

    private final ActorRef waiter;
    private final Coffee favoriteCoffee;
    private final FiniteDuration coffeeFinishedDuration;
    private final int caffeineLimit;

    private  int coffeeCount = 0;
    public Guest(ActorRef waiter, Coffee favoriteCoffee,
                 FiniteDuration coffeeFinishedDuration, int caffeineLimit) {
        this.waiter = waiter;
        this.favoriteCoffee = favoriteCoffee;
        this.coffeeFinishedDuration = coffeeFinishedDuration;
        this.caffeineLimit = caffeineLimit;
        orderFavoriteCoffee();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Waiter.CoffeeServed.class, coffeeServed -> {
                    coffeeCount++;
                    log().info("Enjoying my {} yummy {} ",coffeeCount,coffeeServed.coffee);
                    scheduleCoffeeFinished();
                })
                .match(CoffeeFinished.class, this::CaffeineLimitCheck,
                        coffeeFinished -> { throw new CaffeineException();} )
                .match(CoffeeFinished.class, coffeeFinished ->orderFavoriteCoffee()).build();
    }

    private boolean CaffeineLimitCheck(CoffeeFinished coffeeFinished) {
        return this.coffeeCount > this.caffeineLimit;
    }

    @Override
    public void postStop(){
        log().info("Goodbye!");
    }

    private void orderFavoriteCoffee() {
        this.waiter.tell(new Waiter.ServeCoffee(this.favoriteCoffee),self());
    }


    public static Props props(final ActorRef waiter, final Coffee favoriteCoffee,
                              final FiniteDuration coffeeFinsihedDuration,int caffeineLimit) {
        return Props.create(Guest.class, () ->
                new Guest(waiter,favoriteCoffee,coffeeFinsihedDuration,caffeineLimit));
    }

    private void scheduleCoffeeFinished() {
        getTimers().startSingleTimer("coffee-finished",CoffeeFinished.Instance,coffeeFinishedDuration);
    }

    public static final class CaffeineException extends IllegalStateException {
        static final long serialVersionUID = 1L;

        public CaffeineException() {
            super("Too much caffeine!");
        }
    }

    public static final class CoffeeFinished {
        public static final CoffeeFinished Instance = new CoffeeFinished();
        private CoffeeFinished() {}
    }
}
