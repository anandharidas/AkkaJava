package com.lightbend.training.coffeehouse;

import akka.actor.AbstractActorWithUnboundedStash;
import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import scala.concurrent.duration.FiniteDuration;

import java.util.Objects;
import java.util.Random;

import static com.google.common.base.Preconditions.checkNotNull;

public class Barista extends AbstractActorWithUnboundedStash {

    private final FiniteDuration prepareCoffeeDuration;

    private final int accuracy ;

    public Barista(FiniteDuration prepareCoffeeDuration,int accuracy) {
        this.prepareCoffeeDuration = prepareCoffeeDuration;
        this.accuracy = accuracy;
    }

    public static Props props(FiniteDuration prepareCoffeeDuration,int accuracy) {
        return Props.create(Barista.class, () -> new Barista(prepareCoffeeDuration,accuracy));
    }

    private Coffee pickCoffee(Coffee coffee) {
        return new Random().nextInt(100) < accuracy ? coffee : Coffee.orderOther(coffee);
    }

    @Override
    public Receive createReceive() {
        return ready();
    }

    private Receive ready() {
        return receiveBuilder()
                .match(PrepareCoffee.class, prepareCoffee ->
                {
                        scheduleCoffeePrepared(prepareCoffee.coffee,prepareCoffee.guest);
                        getContext().become(busy(sender()));
                })
                .matchAny(this::unhandled)
                .build();
    }

    private Receive busy(ActorRef waiter) {
        return receiveBuilder()
                .match(CoffeePrepared.class, coffeePrepared -> {
                    waiter.tell(coffeePrepared,self());
                    getContext().become(ready());
                    unstashAll();
                })
                .matchAny( msg -> stash())
                .build();
    }

    private void scheduleCoffeePrepared(Coffee coffee, ActorRef guest) {
        context().system().scheduler().scheduleOnce(prepareCoffeeDuration,self(),
                new CoffeePrepared(pickCoffee(coffee),guest),context().dispatcher(),self());
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

    public static void main(String[] args) {
        System.out.println(Runtime.getRuntime().availableProcessors());
    }


}
