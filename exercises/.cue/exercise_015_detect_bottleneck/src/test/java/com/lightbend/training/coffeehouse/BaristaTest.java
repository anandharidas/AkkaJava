package com.lightbend.training.coffeehouse;

import akka.actor.ActorRef;
import akka.testkit.javadsl.TestKit;
import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

public class BaristaTest extends BaseAkkaTestCase {

  @Test
  public void sendingPrepareCoffeeShouldResultInCoffeePreparedResponse() {
    new TestKit(system) {{

      ActorRef barista = system.actorOf(Barista.props(duration("100 milliseconds"), 100));

      within(Duration.ofMillis(50), Duration.ofMillis(1000), () -> {
        barista.tell(new Barista.PrepareCoffee(new Coffee.Akkaccino(), system.deadLetters()),getRef());

        expectMsgEquals(new Barista.CoffeePrepared(new Coffee.Akkaccino(), system.deadLetters()));

        return null;
      });
    }};
  }


  @Test
  public void shouldSendCoffeePreparedWithRandomCoffeeForInaccurateResponse() {
    new TestKit(system) {{
      Integer accuracy = 50;
      Long runs = 1000L;

      ActorRef guest = system.deadLetters();
      ActorRef barista = system.actorOf(Barista.props(duration("0 milliseconds"), accuracy));

      List<Coffee> coffees = new ArrayList<>();
      for(int i = 0; i < runs; i++) {
        barista.tell(new Barista.PrepareCoffee(new Coffee.Akkaccino(), guest), getRef());
        Barista.CoffeePrepared cp = expectMsgClass(Duration.ofMillis(50), Barista.CoffeePrepared.class);
        coffees.add(cp.coffee);
      }
      Long expectedCount = runs * accuracy / 100;
      Long variation = expectedCount / 10;
      Long numberOfCorrectCoffee = coffees.stream().filter(c -> c.equals(new Coffee.Akkaccino())).count();
      assertThat(numberOfCorrectCoffee).isBetween(expectedCount - variation, expectedCount + variation);
    }};
  }
}
