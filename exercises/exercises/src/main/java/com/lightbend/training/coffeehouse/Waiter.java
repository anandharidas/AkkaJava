package com.lightbend.training.coffeehouse;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

public class Waiter extends AbstractLoggingActor {

    private final ActorRef coffeeHouse;

    private ActorRef barista;

    private int maxComplaintCount;

    private int complaintCount;

    public Waiter(ActorRef coffeeHouse,ActorRef barista, int maxComplaintCount) {
        this.coffeeHouse = coffeeHouse;
        this.barista = barista;
        this.maxComplaintCount = maxComplaintCount;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().
                match(ServeCoffee.class, serveCoffee -> coffeeHouse.tell
                        (new CoffeeHouse.ApproveCoffee(serveCoffee.coffee,sender()),self())).
                match(Barista.CoffeePrepared.class, coffeePrepared ->
                        coffeePrepared.guest.tell
                        (new CoffeeServed(coffeePrepared.coffee),self())).
                match(Complaint.class,
                        complaint -> complaintCount == this.maxComplaintCount,
                        complaint -> {throw new FrustratedException();} ).
                match(Complaint.class, complaint -> {
                   complaintCount++;
                   this.barista.tell(new Barista.PrepareCoffee(complaint.coffee,sender()),self());
                }).
                build();
    }

    public static Props props(ActorRef coffeeHouse,ActorRef barista, int maxComplaintCount) {
        return Props.create(Waiter.class, () -> new Waiter(coffeeHouse,barista,maxComplaintCount));
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

    public static final class Complaint {
        public final Coffee coffee;

        public Complaint(final Coffee coffee) {
            checkNotNull(coffee,"Coffee cannot be Null");
            this.coffee = coffee;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Complaint complaint = (Complaint) o;
            return Objects.equals(coffee, complaint.coffee);
        }

        @Override
        public int hashCode() {
            return Objects.hash(coffee);
        }

        @Override
        public String toString() {
            return "Complaint{" +
                    "coffee=" + coffee +
                    '}';
        }
    }

    public static final class FrustratedException extends IllegalStateException {
        static final long serialVersionUID = 1L;

        public FrustratedException() {
            super("Too many complaints!");
        }
    }
}




