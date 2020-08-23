package com.lightbend.training.coffeehouse;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoffeeHouseTest extends BaseAkkaTestCase {

  @Test
  public void onReceiveOfMessageCoffeeHouseShouldLogMessage() {
    new TestKit(system) {{
      ActorRef coffeeHouse = system.actorOf(Props.create(CoffeeHouse.class));
      coffeeHouse.tell("Brew Coffee", getRef());

      expectMsgPF("Some [Cc]offee response", (Object msg) -> {
        if(msg.toString().matches(".*[Cc]offee.*")) {
          return "match";
        } else {
          throw new RuntimeException("No match found for .*[Cc]offee.*");
        }
      });
    }};
  }

  @Test
  public void shouldLogMessageWhenCreated() {
    new TestKit(system) {{
      interceptDebugLogMessage(".*[Oo]pen.*", 1, () -> system.actorOf(CoffeeHouse.props()));
    }};
  }
}


