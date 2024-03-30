package org.example.ebs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class SubscriptionGenerator {

    public List<Subscription> generateSubscriptions(
            int count, Map<String, Integer> fieldFreq, Map<String, Integer> eqFreq){

        List<Subscription> subscriptions = new ArrayList<>();

        int subscriptionLine = 0;

        for (int i = 0; i < count; i++) {
            subscriptions.add(new Subscription());
        }

        for (var fieldFreqEntry: fieldFreq.entrySet()) {
            String fieldName = fieldFreqEntry.getKey();
            int freq = fieldFreqEntry.getValue();
            int fieldCount = count * freq / 100;

            new SubscriptionsWorker(
                    count, subscriptions, fieldName, subscriptionLine, fieldCount, null
            ).run();

            subscriptionLine += fieldCount;
        }

        return subscriptions;
    }

    public List<Subscription> generateSubscriptionsMultiThread(
            int count, Map<String, Integer> fieldFreq, Map<String, Integer> eqFreq) {

        //use try with resources to close the executor

        List<Subscription> subscriptions = new ArrayList<>();

        int subscriptionLine = 0;

        for (int i = 0; i < count; i++) {
            subscriptions.add(new Subscription());
        }

        try (ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(fieldFreq.size())) {
            for (var fieldFreqEntry : fieldFreq.entrySet()) {
                String fieldName = fieldFreqEntry.getKey();
                int freq = fieldFreqEntry.getValue();
                int fieldCount = count * freq / 100;

                executor.execute(new SubscriptionsWorker(
                        count, subscriptions, fieldName, subscriptionLine, fieldCount, null
                ));

                subscriptionLine += fieldCount;
            }
        }

        return subscriptions;
    }
}
