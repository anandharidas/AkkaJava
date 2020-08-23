/*
 * Copyright © 2014, 2015, 2016 Lightbend, Inc. All rights reserved.
 */

package com.lightbend.training.coffeehouse;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;

public class Guest extends AbstractLoggingActor{

    @Override
    public Receive createReceive() { return emptyBehavior(); }

    public static Props props(){
        return Props.create(Guest.class, Guest::new);
    }
}
