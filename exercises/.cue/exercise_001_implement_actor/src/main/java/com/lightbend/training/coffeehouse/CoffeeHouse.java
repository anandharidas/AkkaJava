/**
 * Copyright © 2014, 2015, 2016 Lightbend, Inc. All rights reserved. [http://www.lightbend.com]
 */

package com.lightbend.training.coffeehouse;

import akka.actor.AbstractLoggingActor;

public class CoffeeHouse extends AbstractLoggingActor{

    @Override
    public Receive createReceive() {
        return receiveBuilder().matchAny(o -> log().info("Coffee Brewing")).build();
    }

}
