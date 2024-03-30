package org.example;

import org.example.ebs.SubscriptionGenerator;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String, Integer> fieldFreq = Map.of(
                "company", 90,
                "value", 50,
                "drop", 20,
                "variation", 40,
                "date", 30
        );

        SubscriptionGenerator subscriptionGenerator = new SubscriptionGenerator();
        long startTime = System.currentTimeMillis();
        var subscriptions = subscriptionGenerator.generateSubscriptions(100000, fieldFreq, null);
        long endTime = System.currentTimeMillis();

        long startTimeMultiThread = System.currentTimeMillis();
        var subscriptionsMultiThread = subscriptionGenerator.generateSubscriptionsMultiThread(100000, fieldFreq, null);
        long endTimeMultiThread = System.currentTimeMillis();

        for (var subscription: subscriptions){
            System.out.println(subscription);
        }
        System.out.println("Time: " + (endTime - startTime) + " ms");
        System.out.println("Time MultiThread: " + (endTimeMultiThread - startTimeMultiThread) + " ms");
    }
}