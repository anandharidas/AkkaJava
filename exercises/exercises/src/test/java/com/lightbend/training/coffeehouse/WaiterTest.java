package com.lightbend.training.coffeehouse;

import akka.actor.ActorRef;
import akka.testkit.javadsl.TestKit;
import akka.testkit.TestProbe;
import org.junit.Test;

public class WaiterTest extends BaseAkkaTestCase {

  @Test
  public void sendingServeCoffeeShouldResultInApproveCoffeeToCoffeeHouse() {
    new TestKit(system) {{
      ActorRef coffeeHouse = getRef();
      TestProbe guest = new TestProbe(system);
      ActorRef waiter = system.actorOf(Waiter.props(coffeeHouse));
      waiter.tell(new Waiter.ServeCoffee(new Coffee.Akkaccino()), guest.ref());
      expectMsgEquals(new CoffeeHouse.ApproveCoffee(new Coffee.Akkaccino(), guest.ref()));
    }};
  }

  @Test
  public void sendingCoffeePreparedShouldResultInCoffeeServedToGuest() {
    new TestKit(system) {{
      ActorRef guest = getRef();
      ActorRef waiter = system.actorOf(Waiter.props(system.deadLetters()));
      waiter.tell(new Barista.CoffeePrepared(new Coffee.Akkaccino(), guest), system.deadLetters());
      expectMsgEquals(new Waiter.CoffeeServed(new Coffee.Akkaccino()));
    }};
  }
}
