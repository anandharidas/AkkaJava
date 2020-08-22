package com.lightbend.training.coffeehouse;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import java.util.Objects;

public class Waiter extends AbstractLoggingActor {

    private final ActorRef barista;

    public Waiter(ActorRef barista) {
        this.barista = barista;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().
                match(ServeCoffee.class, serveCoffee -> barista.tell
                        (new Barista.PrepareCoffee(serveCoffee.coffee,sender()),self())).
                match(Barista.CoffeePrepared.class, coffeePrepared ->
                        coffeePrepared.guest.tell
                        (new CoffeeServed(coffeePrepared.coffee),self())).build();
    }

    public static Props props(ActorRef barista) {
        return Props.create(Waiter.class, () -> new Waiter(barista));
    }

    public static final class ServeCoffee {
        public  final Coffee coffee;

        protected ServeCoffee(Coffee coffee) {
            this.coffee = coffee;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ServeCoffee that = (ServeCoffee) o;
            return Objects.equals(coffee, that.coffee);
        }

        @Override
        public int hashCode() {
            return Objects.hash(coffee);
        }

        @Override
        public String toString() {
            return "ServeCoffee{" +
                    "coffee=" + coffee +
                    '}';
        }
    }

    public static final class CoffeeServed {
        public final Coffee coffee;

        public CoffeeServed(Coffee coffee) {
            this.coffee = coffee;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CoffeeServed that = (CoffeeServed) o;
            return Objects.equals(coffee, that.coffee);
        }

        @Override
        public int hashCode() {
            return Objects.hash(coffee);
        }

        @Override
        public String toString() {
            return "CoffeeServed{" +
                    "coffee=" + coffee +
                    '}';
        }
    }

}




