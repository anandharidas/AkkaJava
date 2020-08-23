package com.lightbend.training.coffeehouse;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import org.junit.Test;

public class GuestTest extends BaseAkkaTestCase {

  @Test
  public void sendingCoffeeServedShouldIncreaseCoffeeCount() {
    new TestKit(system) {{
      ActorRef guest = system.actorOf(Guest.props(system.deadLetters(), new Coffee.Akkaccino()));
      interceptInfoLogMessage(".*[Ee]njoy.*1\\.*", 1, () -> {
        guest.tell(new Waiter.CoffeeServed(new Coffee.Akkaccino()), ActorRef.noSender());
      });
    }};
  }

  @Test
  public void sendingCoffeeFinishedShouldResultInServeCoffeeMessageToWaiter() {
    new TestKit(system) {{
      ActorRef guest = system.actorOf(Guest.props(getRef(), new Coffee.Akkaccino()));
      guest.tell(Guest.CoffeeFinished.Instance, ActorRef.noSender());
      expectMsgEquals(new Waiter.ServeCoffee(new Coffee.Akkaccino()));
    }};
  }
}
