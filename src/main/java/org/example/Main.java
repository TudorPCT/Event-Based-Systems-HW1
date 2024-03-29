package org.example;

import org.example.ebs.SubscriptionGenerator;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String, Integer> fieldFreq = Map.of(
                "company", 50,
                "value", 50,
                "drop", 0,
                "variation", 10,
                "date", 0
        );


        SubscriptionGenerator subscriptionGenerator = new SubscriptionGenerator();
        for (var subscription: subscriptionGenerator.generateSubscriptions(
                        10, fieldFreq, null)){
            System.out.println(subscription);
        }
    }
}