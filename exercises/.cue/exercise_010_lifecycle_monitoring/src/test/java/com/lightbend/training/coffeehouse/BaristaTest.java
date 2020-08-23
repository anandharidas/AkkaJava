package com.lightbend.training.coffeehouse;

import akka.actor.ActorRef;
import akka.testkit.javadsl.TestKit;
import org.junit.Test;

import java.time.Duration;

public class BaristaTest extends BaseAkkaTestCase {

  @Test
  public void sendingPrepareCoffeeShouldResultInCoffeePreparedResponse() {
    new TestKit(system) {{

      ActorRef barista = system.actorOf(Barista.props(duration("100 milliseconds")));

      within(Duration.ofMillis(50), Duration.ofMillis(1000), () -> {
        barista.tell(new Barista.PrepareCoffee(new Coffee.Akkaccino(), system.deadLetters()),getRef());

        expectMsgEquals(new Barista.CoffeePrepared(new Coffee.Akkaccino(), system.deadLetters()));

        return null;
      });
    }};
  }
}
