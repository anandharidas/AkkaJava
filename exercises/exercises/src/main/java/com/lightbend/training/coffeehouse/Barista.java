package com.lightbend.training.coffeehouse;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import scala.concurrent.duration.FiniteDuration;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

public class Barista extends AbstractLoggingActor {

    private final FiniteDuration prepareCoffeeDuration;

    public Barista(FiniteDuration prepareCoffeeDuration) {
        this.prepareCoffeeDuration = prepareCoffeeDuration;
    }

    public static Props props(FiniteDuration prepareCoffeeDuration) {
        return Props.create(Barista.class, () -> new Barista(prepareCoffeeDuration));
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder().match(PrepareCoffee.class, prepareCoffee ->
        {
                Busy.busy(prepareCoffeeDuration);
                sender().tell(new CoffeePrepared(prepareCoffee.coffee,prepareCoffee.guest),self());
        }).build();
    }

    public static final class PrepareCoffee {
        public final Coffee coffee;
        public final ActorRef guest;

        public PrepareCoffee(Coffee coffee, ActorRef guest) {
            checkNotNull(coffee,"Coffee cannot be Null");
            checkNotNull(guest, "Guest cannot be Null");
            this.coffee = coffee;
            this.guest = guest;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PrepareCoffee that = (PrepareCoffee) o;
            return Objects.equals(coffee, that.coffee) &&
                    Objects.equals(guest, that.guest);
        }

        @Override
        public int hashCode() {
            return Objects.hash(coffee, guest);
        }

        @Override
        public String toString() {
            return "PrepareCoffee{" +
                    "coffee=" + coffee +
                    ", guest=" + guest +
                    '}';
        }
    }


    public static final class CoffeePrepared {
        public final Coffee coffee;
        public final ActorRef guest;

        public CoffeePrepared(Coffee coffee, ActorRef guest) {
            checkNotNull(coffee,"Coffee cannot be Null");
            checkNotNull(guest, "Guest cannot be Null");
            this.coffee = coffee;
            this.guest = guest;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CoffeePrepared that = (CoffeePrepared) o;
            return Objects.equals(coffee, that.coffee) &&
                    Objects.equals(guest, that.guest);
        }

        @Override
        public int hashCode() {
            return Objects.hash(coffee, guest);
        }

        @Override
        public String toString() {
            return "CoffeePrepared{" +
                    "coffee=" + coffee +
                    ", guest=" + guest +
                    '}';
        }
    }



}
