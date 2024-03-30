package org.example.ebs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SubscriptionGenerator {
    private static final String[] COMPANIES = {"Google", "Microsoft", "Apple", "Amazon"};
    private static final String[] OPERATORS = {"=", ">", "<", ">=", "<="};
    private static final double PRECISION = 100.0;

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

            for (int i = 0; i < fieldCount; i++) {
                Object fieldValue;

                if (fieldName.equals("company")){
                    fieldValue = COMPANIES[ThreadLocalRandom.current().nextInt(0, COMPANIES.length)];
                } else if(fieldName.equals("date")){
                    fieldValue = getRandomDate();
                }
                else{
                    fieldValue = Math.round(ThreadLocalRandom.current().nextDouble() * PRECISION) / PRECISION;
                }

                subscriptions.get(subscriptionLine % count).addField(
                        new SubscriptionField(
                                fieldName,
                                OPERATORS[ThreadLocalRandom.current().nextInt(0, OPERATORS.length)],
                                fieldValue
                        )
                );

                subscriptionLine++;
            }
        }

        return subscriptions;
    }

    public List<Subscription> generateSubscriptionsMultiThread(
            int count, Map<String, Integer> fieldFreq, Map<String, Integer> eqFreq) throws InterruptedException {

        //use try with resources to close the executor

        List<Subscription> subscriptions = new ArrayList<>();

//        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(fieldFreq.size());

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

//        executor.shutdown();
//        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        return subscriptions;
    }

    private static String getRandomDate() {
        int year = ThreadLocalRandom.current().nextInt(2000, 2023);
        int month = ThreadLocalRandom.current().nextInt(1, 13);
        int day = ThreadLocalRandom.current().nextInt(1, 29);
        return String.format("%02d.%02d.%d", day, month, year);
    }
}
