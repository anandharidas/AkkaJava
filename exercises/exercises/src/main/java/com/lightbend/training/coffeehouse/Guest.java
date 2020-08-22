package com.lightbend.training.coffeehouse;

import akka.actor.ActorRef;
import akka.actor.Props;
import scala.concurrent.duration.FiniteDuration;

public class Guest extends AbstractLoggingActorWithTimers {

    private final ActorRef waiter;
    private final Coffee favoriteCoffee;
    private final FiniteDuration coffeeFinishedDuration;

    private  int coffeeCount = 0;
    public Guest(ActorRef waiter, Coffee favoriteCoffee,FiniteDuration coffeeFinishedDuration) {
        this.waiter = waiter;
        this.favoriteCoffee = favoriteCoffee;
        this.coffeeFinishedDuration = coffeeFinishedDuration;
        orderFavoriteCoffee();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(Waiter.CoffeeServed.class, coffeeServed -> {
            coffeeCount++;
            log().info("Enjoying my {} yummy {} ",coffeeCount,coffeeServed.coffee);
            scheduleCoffeeFinished();
        }).match(CoffeeFinished.class, coffeeFinished ->orderFavoriteCoffee()).build();
    }

    private void orderFavoriteCoffee() {
        this.waiter.tell(new Waiter.ServeCoffee(this.favoriteCoffee),self());
    }


    public static Props props(final ActorRef waiter, final Coffee favoriteCoffee,final FiniteDuration coffeeFinsihedDuration) {
        return Props.create(Guest.class, () -> new Guest(waiter,favoriteCoffee,coffeeFinsihedDuration));
    }

    private void scheduleCoffeeFinished() {
        getTimers().startSingleTimer("coffee-finished",CoffeeFinished.Instance,coffeeFinishedDuration);
    }

    public static final class CoffeeFinished {
        public static final CoffeeFinished Instance = new CoffeeFinished();
        private CoffeeFinished() {}
    }
}
