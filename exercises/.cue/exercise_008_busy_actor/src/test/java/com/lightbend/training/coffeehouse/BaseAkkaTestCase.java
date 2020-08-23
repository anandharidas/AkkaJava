package com.lightbend.training.coffeehouse;

import akka.actor.*;
import akka.event.Logging;
import akka.japi.Creator;
import akka.testkit.TestEvent;
import akka.testkit.javadsl.EventFilter;
import akka.testkit.TestProbe;
import akka.testkit.javadsl.TestKit;
import org.junit.After;
import org.junit.Before;
import scala.concurrent.Await;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

public abstract class BaseAkkaTestCase {

  protected ActorSystem system = ActorSystem.create();

  @Before
  public void setUp() {
    // This method is using a Scala API, however, the replacement
    // Java API has not been updated to include the required functionality.
    final akka.testkit.EventFilter defaultDebug = akka.testkit.EventFilter.debug(null, null, "", null, Integer.MAX_VALUE);
    final akka.testkit.EventFilter defaultInfo = akka.testkit.EventFilter.info(null, null, "", null, Integer.MAX_VALUE);
    final akka.testkit.EventFilter defaultWarning = akka.testkit.EventFilter.warning(null, null, "", null, Integer.MAX_VALUE);
    final akka.testkit.EventFilter defaultError = akka.testkit.EventFilter.error(null, null, "", null, Integer.MAX_VALUE);

    system.eventStream().publish(new TestEvent.Mute(Arrays.asList(defaultDebug)));
    system.eventStream().publish(new TestEvent.Mute(Arrays.asList(defaultInfo)));
    system.eventStream().publish(new TestEvent.Mute(Arrays.asList(defaultWarning)));
    system.eventStream().publish(new TestEvent.Mute(Arrays.asList(defaultError)));
  }

  @FunctionalInterface
  public static interface CodeUnderInspection {
    public void run();
  }


  public void interceptInfoLogMessage(String pattern, int occurrences, final CodeUnderInspection i) {
    eventFilter(Logging.Info.class, pattern, occurrences, i);
  }

  public void interceptDebugLogMessage(String pattern, int occurrences, final CodeUnderInspection i) {
    eventFilter(Logging.Debug.class, pattern, occurrences, i);
  }

  public void interceptErrorLogMessage(String pattern, int occurrences, final CodeUnderInspection i) {
    eventFilter(Logging.Error.class, pattern, occurrences, i);
  }

  public void eventFilter(Class clazz, String pattern, int occurrences, final CodeUnderInspection i){
    new EventFilter(clazz, system)
            .occurrences(occurrences)
            .matches(pattern)
            .intercept(() -> {
              i.run();
              return null;
            });
  }

  public ActorRef expectActor(TestKit kit, String path) {
    final ActorRef[] actor = {null};

    kit.awaitCond(Duration.ofSeconds(3), Duration.ofMillis(150), "No actor found with " + path, () -> {

      TestProbe probe = new TestProbe(system);

      system.actorSelection(path).tell(new akka.actor.Identify(101), probe.ref());

      ActorIdentity i = probe.expectMsgClass(kit.duration("100 millis"), ActorIdentity.class);

      actor[0] = i.getActorRef().orElse(null);

      return i.getActorRef().isPresent();
    });

    return actor[0];
  }

  public <T extends AbstractActor> ActorRef createActor(Class<T> clazz, String name, Creator<T> factory) {
    Props stub = Props.create(clazz, factory);
    return system.actorOf(stub, name);
  }

  public ActorRef createStubActor(String name, Creator<AbstractActor> factory) {
    return createActor(AbstractActor.class, name, factory);
  }

  @After
  public void tearDown() throws TimeoutException, InterruptedException {
    Await.ready(system.terminate(), scala.concurrent.duration.Duration.Inf());
  }
}
