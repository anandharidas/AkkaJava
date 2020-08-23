package com.lightbend.training.coffeehouse;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import org.junit.Test;
import scala.concurrent.duration.FiniteDuration;

import java.time.Duration;

public class GuestTest extends BaseAkkaTestCase {

  @Test
  public void sendingCoffeeServedShouldIncreaseCoffeeCount() {
    new TestKit(system) {{
      ActorRef guest = system.actorOf(Guest.props(system.deadLetters(), new Coffee.Akkaccino(), duration("100 milliseconds")));
      interceptInfoLogMessage(".*[Ee]njoy.*1\\.*", 1, () -> {
        guest.tell(new Waiter.CoffeeServed(new Coffee.Akkaccino()), ActorRef.noSender());
      });
    }};
  }

  @Test
  public void serveCoffeeShouldBeSentAfterFinishCoffeeDuration() {
    new TestKit(system) {{
      ActorRef guest = createGuest(this, getRef());

      within(Duration.ofMillis(50), Duration.ofMillis(200), () -> {
        guest.tell(new Waiter.CoffeeServed(new Coffee.Akkaccino()), ActorRef.noSender());

        expectMsgEquals(new Waiter.ServeCoffee(new Coffee.Akkaccino()));

        return null;
      });
    }};
  }

  @Test
  public void sendingCoffeeFinishedShouldResultInServeCoffeeMessageToWaiter() {
    new TestKit(system) {{
      ActorRef guest = createGuest(this, getRef());
      guest.tell(Guest.CoffeeFinished.Instance, ActorRef.noSender());
      expectMsgEquals(new Waiter.ServeCoffee(new Coffee.Akkaccino()));
    }};
  }

  private ActorRef createGuest(TestKit kit, ActorRef waiter) {
    ActorRef guest = system.actorOf(Guest.props(waiter, new Coffee.Akkaccino(), kit.duration("100 milliseconds")));
    kit.expectMsgEquals(new Waiter.ServeCoffee(new Coffee.Akkaccino())); // Creating Guest immediately sends Waiter.ServeCoffee
    return guest;
  }


}
