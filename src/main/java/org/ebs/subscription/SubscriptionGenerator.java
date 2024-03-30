package org.ebs.subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class SubscriptionGenerator {

    public List<Subscription> generateSubscriptions(
            int count, Map<String, Double> fieldFreq, Map<String, Double> eqFreq){

        List<Subscription> subscriptions = new ArrayList<>();

        int subscriptionLine = 0;

        for (int i = 0; i < count; i++) {
            subscriptions.add(new Subscription());
        }

        for (var fieldFreqEntry: fieldFreq.entrySet()) {
            String fieldName = fieldFreqEntry.getKey();
            Double freq = fieldFreqEntry.getValue();
            int fieldCount = (int) (count * freq);

            new SubscriptionsWorker(
                    count, subscriptions, fieldName, subscriptionLine,
                    fieldCount, eqFreq.getOrDefault(fieldName, null)
            ).run();

            subscriptionLine += fieldCount;
        }

        return subscriptions;
    }

    public List<Subscription> generateSubscriptionsMultiThread(
            int count, Map<String, Double> fieldFreq, Map<String, Double> eqFreq) {

        //use try with resources to close the executor

        List<Subscription> subscriptions = new ArrayList<>();

        int subscriptionLine = 0;

        for (int i = 0; i < count; i++) {
            subscriptions.add(new Subscription());
        }

        try (ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(fieldFreq.size())) {
            for (var fieldFreqEntry : fieldFreq.entrySet()) {
                String fieldName = fieldFreqEntry.getKey();
                Double freq = fieldFreqEntry.getValue();
                int fieldCount = (int) (count * freq);
//                int step = (int) Math.min(Math.max(Math.round(fieldCount * 0.1), 10), fieldCount);

//                for(int i = step; i < fieldCount; i += step ) {
                    executor.execute(new SubscriptionsWorker(
                            count, subscriptions, fieldName, subscriptionLine,
                            fieldCount, eqFreq.getOrDefault(fieldName, null)
                    ));

                    subscriptionLine += fieldCount;
//                }
            }
        }

        return subscriptions;
    }
}
