package com.lightbend.training.coffeehouse;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import org.junit.Test;

public class CoffeeHouseTest extends BaseAkkaTestCase {

  @Test
  public void onReceiveOfMessageCoffeeHouseShouldLogMessage() {
    new TestKit(system) {{
      ActorRef coffeeHouse = system.actorOf(Props.create(CoffeeHouse.class));
      interceptInfoLogMessage(".*[Cc]offee.*", 1, () -> coffeeHouse.tell("Brew Coffee", ActorRef.noSender()));
    }};
  }

}
