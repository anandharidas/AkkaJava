package com.lightbend.training.coffeehouse;

import scala.concurrent.duration.Duration;

import java.math.BigDecimal;

public class Busy {

    /**
     * Keeps the CPU busy for the given appoximate duration.
     */
    public static BigDecimal busy(Duration duration) {
        return pi(System.nanoTime() + duration.toNanos());
    }

    private static BigDecimal gregoryLeibnitz(int n) {
        return new BigDecimal(4.0 * (1 - (n % 2) * 2) / (n * 2 + 1));
    }

    private static BigDecimal pi(Long endNanos) {
        int n = 0;
        BigDecimal acc = new BigDecimal(0.0);
        while (System.nanoTime() < endNanos) {
            acc.add(gregoryLeibnitz(n));
            n++;
        }
        return acc;
    }

}
