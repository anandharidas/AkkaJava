package com.lightbend.training.coffeehouse;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoffeeHouseTest extends BaseAkkaTestCase {

  @Test
  public void shouldLogMessageWhenCreated() {
    new TestKit(system) {{
      interceptDebugLogMessage(".*[Oo]pen.*", 1, () -> system.actorOf(CoffeeHouse.props(Integer.MAX_VALUE)));
    }};
  }

  @Test
  public void shouldCreateChildActorCalledBaristaWhenCreated() {
    new TestKit(system) {{
      system.actorOf(CoffeeHouse.props(Integer.MAX_VALUE), "create-barista");
      expectActor(this, "/user/create-barista/waiter");
    }};
  }

  @Test
  public void shouldCreateChildActorCalledWaiterWhenCreated() {
    new TestKit(system) {{
      system.actorOf(CoffeeHouse.props(Integer.MAX_VALUE), "create-waiter");
      expectActor(this, "/user/create-waiter/waiter");
    }};
  }

  @Test
  public void shouldCreateGuestActorsWhenCreateGuestMessageSent() {
    new TestKit(system) {{
      ActorRef coffeeHouse = system.actorOf(CoffeeHouse.props(Integer.MAX_VALUE), "create-guest");
      coffeeHouse.tell(new CoffeeHouse.CreateGuest(new Coffee.Akkaccino(), Integer.MAX_VALUE), ActorRef.noSender());
      expectActor(this, "/user/create-guest/$*");
    }};
  }

  @Test
  public void sendingApproveCoffeeShouldForwardPrepareCoffeeIfCaffeineLimitNotReached() {
    new TestKit(system) {{
      ActorRef coffeeHouse = createActor(CoffeeHouse.class, "prepare-coffee", () -> new CoffeeHouse(Integer.MAX_VALUE) {
        @Override
        protected ActorRef createBarista() {
          return getRef();
        }
      });
      coffeeHouse.tell(new CoffeeHouse.CreateGuest(new Coffee.Akkaccino(), Integer.MAX_VALUE), ActorRef.noSender());
      ActorRef guest = expectActor(this, "/user/prepare-coffee/$*");
      coffeeHouse.tell(new CoffeeHouse.ApproveCoffee(new Coffee.Akkaccino(), guest), getRef());
      expectMsgEquals(new Barista.PrepareCoffee(new Coffee.Akkaccino(), guest));
    }};
  }

  @Test
  public void sendingApproveCoffeeShouldResultInLoggingStatusMessageWhenLimitReached() {
    new TestKit(system) {{
      ActorRef coffeeHouse = system.actorOf(CoffeeHouse.props(1), "caffeine-limit");
      coffeeHouse.tell(new CoffeeHouse.CreateGuest(new Coffee.Akkaccino(), Integer.MAX_VALUE), ActorRef.noSender());
      ActorRef guest = expectActor(this, "/user/caffeine-limit/$*");
      interceptInfoLogMessage(".*[Ss]orry.*", 1, () -> coffeeHouse.tell(
              new CoffeeHouse.ApproveCoffee(new Coffee.Akkaccino(), guest), ActorRef.noSender()));
    }};
  }

  @Test
  public void sendingApproveCoffeeShouldResultInStoppingGuestWhenLimitReached() {
    new TestKit(system) {{
      ActorRef coffeeHouse = system.actorOf(CoffeeHouse.props(1), "guest-terminated");
      coffeeHouse.tell(new CoffeeHouse.CreateGuest(new Coffee.Akkaccino(), Integer.MAX_VALUE), ActorRef.noSender());
      ActorRef guest = expectActor(this, "/user/guest-terminated/$*");
      watch(guest);
      coffeeHouse.tell(new CoffeeHouse.ApproveCoffee(new Coffee.Akkaccino(), guest), ActorRef.noSender());
      expectTerminated(guest);
    }};
  }

  @Test
  public void onTerminationOfGuestCoffeeHouseShouldRemoveGuestFromBookkeeper() {
    new TestKit(system) {{
      ActorRef coffeeHouse = system.actorOf(CoffeeHouse.props(1), "guest-removed");
      coffeeHouse.tell(new CoffeeHouse.CreateGuest(new Coffee.Akkaccino(), Integer.MAX_VALUE), ActorRef.noSender());
      ActorRef guest = expectActor(this, "/user/guest-removed/$*");
      interceptInfoLogMessage(".*[Tt]hanks.*", 1, () -> {
        coffeeHouse.tell(new CoffeeHouse.ApproveCoffee(new Coffee.Akkaccino(), guest), ActorRef.noSender());
      });
    }};
  }
}


