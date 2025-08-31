package com.trbear9;

import org.slf4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Stack;

public class Util {

    private void test(){
    }

    public static void took(Logger logger, String message, Runnable runnable){
        long startTime = System.nanoTime();
        runnable.run();
        long endTime = System.nanoTime();
        String time = String.format("%.2f", (endTime - startTime) / 1_000_000.0);
        logger.info("{} menghabiskan waktu {} ms!", message, time);
    }

    public static String satuan(double health) {
        if (health >= 1_000_000_000f)
            return Util.round(health / 1_000_000_000, 1) + "B"; // milyar/billion
        else if (health >= 1_000_000)
            return Util.round(health / 1_000_000_000, 1) + "M";// juta
        else if (health >= 1_000)
            return Util.round(health / 1_000, 1) + "k"; // seribu
        else
            return Util.round(health, 0) + "";
    }

    public static float round(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value).setScale(places, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    public static String toRoman(int numbers) {
        return numberToRoman(numbers, 0, new Stack<>());
    }

    private static final String[] romanUnique = { "I", "V", "X", "L", "C", "D", "M", "V̅", "X̅" };

    private static String numberToRoman(int numbers, int digit, Stack<String> memory) {

        if (numbers <= 0) {
            StringBuilder builder = new StringBuilder();
            while (!memory.isEmpty()) {
                builder.append(memory.pop());
            }
            return builder.toString();
        }

        int last_digit = numbers % 10;
        StringBuilder roman = new StringBuilder();
        if (last_digit == 9) {
            roman.append(romanUnique[digit]);
            roman.append(romanUnique[digit + 2]);
            memory.add(roman.toString());
            return numberToRoman(numbers / 10, digit + 2, memory);
        }
        if (last_digit >= 5) {
            roman.append(romanUnique[digit + 1]);
            roman.append(romanUnique[digit].repeat(last_digit - 5));
            memory.add(roman.toString());
            return numberToRoman(numbers / 10, digit + 2, memory);
        }
        if (last_digit == 4) {
            roman.append(romanUnique[digit]);
            roman.append(romanUnique[digit + 1]);
            memory.add(roman.toString());
            return numberToRoman(numbers / 10, digit + 2, memory);
        }
        if (last_digit == 0) {
            return numberToRoman(numbers / 10, digit + 2, memory);
        }
        roman.append(romanUnique[digit].repeat(last_digit));
        memory.add(roman.toString());
        return numberToRoman(numbers / 10, digit + 2, memory);
    }

}
