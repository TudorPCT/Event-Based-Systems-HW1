package org.ebs;

import org.ebs.subscription.SubscriptionGenerator;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String, Double> fieldFreq = Map.of(
                "company", 0.9,
                "value", 0.50,
                "drop", 0.20,
                "variation", 0.40,
                "date", 0.30
        );

        Map<String, Double> eqFreq = Map.of(
                "company", 0.5,
                "value", 0.4
        );

        SubscriptionGenerator subscriptionGenerator = new SubscriptionGenerator();
        long startTime = System.currentTimeMillis();
        var subscriptions = subscriptionGenerator.generateSubscriptions(100000, fieldFreq, eqFreq);
        long endTime = System.currentTimeMillis();

        long startTimeMultiThread = System.currentTimeMillis();
        var subscriptionsMultiThread = subscriptionGenerator.generateSubscriptionsMultiThread(100000, fieldFreq, eqFreq);
        long endTimeMultiThread = System.currentTimeMillis();

        for (var subscription: subscriptionsMultiThread){
            System.out.println(subscription);
        }
        System.out.println("Time: " + (endTime - startTime) + " ms");
        System.out.println("Time MultiThread: " + (endTimeMultiThread - startTimeMultiThread) + " ms");
    }
}