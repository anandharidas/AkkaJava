package com.lightbend.training.coffeehouse;

import akka.actor.ActorRef;
import akka.testkit.javadsl.TestKit;
import akka.testkit.TestProbe;
import akka.testkit.javadsl.TestKit;
import org.junit.Test;

public class WaiterTest extends BaseAkkaTestCase {

  @Test
  public void sendingServeCoffeeShouldResultInApproveCoffeeToCoffeeHouse() {
    new TestKit(system) {{
      ActorRef coffeeHouse = getRef();
      TestProbe guest = new TestProbe(system);
      ActorRef waiter = system.actorOf(Waiter.props(coffeeHouse, system.deadLetters(), Integer.MAX_VALUE));
      waiter.tell(new Waiter.ServeCoffee(new Coffee.Akkaccino()), guest.ref());
      expectMsgEquals(new CoffeeHouse.ApproveCoffee(new Coffee.Akkaccino(), guest.ref()));
    }};
  }

  @Test
  public void sendingCoffeePreparedShouldResultInCoffeeServedToGuest() {
    new TestKit(system) {{
      ActorRef guest = getRef();
      ActorRef waiter = system.actorOf(Waiter.props(system.deadLetters(), system.deadLetters(), 1));
      waiter.tell(new Barista.CoffeePrepared(new Coffee.Akkaccino(), guest), system.deadLetters());
      expectMsgEquals(new Waiter.CoffeeServed(new Coffee.Akkaccino()));
    }};
  }

  @Test
  public void sendingComplaintShouldResultInPrepareCoffeeToBarista() {
    new TestKit(system) {{
      ActorRef barista = getRef();
      TestProbe guest = new TestProbe(system);
      ActorRef waiter = system.actorOf(Waiter.props(system.deadLetters(), barista, 1));

      waiter.tell(new Waiter.Complaint(new Coffee.Akkaccino()), guest.ref());
      expectMsgEquals(new Barista.PrepareCoffee(new Coffee.Akkaccino(), guest.ref()));
    }};
  }

  @Test
  public void shouldThrowFrustratedExceptionWhenMaxComplaintReached() {
    new TestKit(system) {{
      ActorRef waiter = system.actorOf(Waiter.props(system.deadLetters(), system.deadLetters(), 0));
      eventFilter(Waiter.FrustratedException.class, "", 1, () -> {
        waiter.tell(new Waiter.Complaint(new Coffee.Akkaccino()), ActorRef.noSender());
      });
    }};
  }
}
