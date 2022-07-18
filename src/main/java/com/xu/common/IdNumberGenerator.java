package com.xu.common;


import java.util.Random;

public class IdNumberGenerator {
    public static String generate(int size) {
        Random random = new Random();
        long l = random.nextLong();
        l = Math.abs(l);
        return Long.toString(l).substring(19 - size);
    }
}
