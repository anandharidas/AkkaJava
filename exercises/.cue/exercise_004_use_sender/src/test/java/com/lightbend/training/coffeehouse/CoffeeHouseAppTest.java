package com.lightbend.training.coffeehouse;


import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.testkit.javadsl.TestKit;
import org.assertj.core.data.MapEntry;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CoffeeHouseAppTest extends BaseAkkaTestCase {

  @Test
  public void argsToOptsShouldConvertArgsToOpts() {
    final Map<String, String> result = CoffeeHouseApp.argsToOpts(Arrays.asList("a=1", "b", "-Dc=2"));
    assertThat(result).contains(MapEntry.entry("a", "1"), MapEntry.entry("-Dc", "2"));
  }


  @Test
  public void applySystemPropertiesShouldConvertOptsToSystemProps() {
    System.setProperty("c", "");
    Map<String, String> opts = new HashMap<>();
    opts.put("a", "1");
    opts.put("-Dc", "2");
    CoffeeHouseApp.applySystemProperties(opts);
    assertThat(System.getProperty("c")).isEqualTo("2");
  }

  @Test
  public void shouldCreateATopLevelActorCalledCoffeeHouse() {
    new TestKit(system) {{
      new CoffeeHouseApp(system);
      String path = "/user/coffee-house";
      expectActor(this, path);
    }};
  }

  @Test
  public void shouldSendMessageToCoffeeHouseAfterCreation() {
    new TestKit(system) {{
      new CoffeeHouseApp(system) {
        @Override
        protected ActorRef createCoffeeHouse() {
          return getRef();
        }
      };
      expectMsgClass(Object.class);
    }};
  }

  @Test
  public void shouldLogResponseFromCoffeeHouse() {
    new TestKit(system) {{
      interceptInfoLogMessage("stub response", 1, () -> {
        new CoffeeHouseApp(system) {
          @Override
          protected ActorRef createCoffeeHouse() {
            return createStubActor("stub-coffee-house", () -> new AbstractLoggingActor(){
              @Override public Receive createReceive() { return receiveBuilder().matchAny(o -> sender().tell("stub response", self())).build(); }
            });
          }
        };
      });
    }};
  }
}
