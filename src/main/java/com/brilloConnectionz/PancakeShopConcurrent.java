package com.brilloConnectionz;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PancakeShopConcurrent {
    private static int MAX_PANCAKES_SHOPKEEPER = 12;
    private static int MAX_PANCAKES_USER = 5;
    private static int NUM_USERS = 3;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Random random = new Random();
        CompletableFuture<Integer> shopkeeperFuture = CompletableFuture.supplyAsync(() -> {
            return random.nextInt(MAX_PANCAKES_SHOPKEEPER + 1);
        });

        CompletableFuture<List<Integer>> userFutures = CompletableFuture.supplyAsync(()->{
            return new ArrayList<>(3);
        });
        for (int i = 0; i < NUM_USERS; i++) {
            userFutures.get().add(i,CompletableFuture.supplyAsync(() -> {
                return random.nextInt(MAX_PANCAKES_USER + 1);
            }).get());
        }

        CompletableFuture<Void> allOf = CompletableFuture.allOf(shopkeeperFuture, userFutures);

        allOf.join(); // Wait for all futures to complete

        int shopkeeperPancakes = shopkeeperFuture.get();
        int totalUserPancakes = 0;
        for (int i = 0; i < NUM_USERS; i++) {
            totalUserPancakes += userFutures.get().get(i);
        }

        int wastedPancakes = Math.max(0, shopkeeperPancakes - totalUserPancakes);
        boolean metAllNeeds = totalUserPancakes <= shopkeeperPancakes;

        // Print output
        System.out.println("Starting time: " + getCurrentTime());
        System.out.println("Ending time: " + getCurrentTime());
        System.out.println("Shopkeeper pancakes: " + shopkeeperPancakes);
        System.out.print("User pancakes: ");
        for (int i = 0; i < NUM_USERS; i++) {
            try {
                System.out.print(userFutures.get().get(i) + " ");
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        System.out.println();
        System.out.println("Met all needs: " + metAllNeeds);
        System.out.println("Wasted pancakes: " + wastedPancakes);
        System.out.println("Unmet orders: " + Math.max(0, totalUserPancakes - shopkeeperPancakes));
    }

    private static String getCurrentTime() {
        // Simulating current time
        return LocalDateTime.now().toString();    }
}

