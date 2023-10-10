package com.brilloConnectionz;

import java.time.LocalDateTime;
import java.util.Random;

public class PancakeShopNonConcurrent {
    private static final int MAX_PANCAKES_SHOPKEEPER = 12;
    private static final int MAX_PANCAKES_USER = 5;
    private static final int NUM_USERS = 3;

    public static void main(String[] args) {
        Random random = new Random();
        int shopkeeperPancakes = random.nextInt(MAX_PANCAKES_SHOPKEEPER + 1);
        int[] userPancakes = new int[NUM_USERS];

        int totalUserPancakes = 0;
        for (int i = 0; i < NUM_USERS; i++) {
            userPancakes[i] = random.nextInt(MAX_PANCAKES_USER + 1);
            totalUserPancakes += userPancakes[i];
        }

        int wastedPancakes = Math.max(0, shopkeeperPancakes - totalUserPancakes);
        boolean metAllNeeds = totalUserPancakes <= shopkeeperPancakes;

        // Print output
        System.out.println("Starting time: " + getCurrentTime());
        System.out.println("Ending time: " + getCurrentTime());
        System.out.println("Shopkeeper pancakes: " + shopkeeperPancakes);
        System.out.print("User pancakes: ");
        for (int i = 0; i < NUM_USERS; i++) {
            System.out.print(userPancakes[i] + " ");
        }
        System.out.println();
        System.out.println("Met all needs: " + metAllNeeds);
        System.out.println("Wasted pancakes: " + wastedPancakes);
        System.out.println("Unmet orders: " + Math.max(0, totalUserPancakes - shopkeeperPancakes));
    }

    private static String getCurrentTime() {
        // Simulating current time
        return LocalDateTime.now().toString();
    }
}
