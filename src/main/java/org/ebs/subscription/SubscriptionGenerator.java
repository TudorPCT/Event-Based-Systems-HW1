package org.ebs.subscription;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class SubscriptionGenerator {
    private static final String[] OPERATORS = {"=", ">", "<"};

    public List<Subscription> generateSubscriptions(
            int count, Map<String, Double> fieldFreq, Map<String, Double> eqFreq) {

        List<Subscription> subscriptions = new ArrayList<>();

        int subscriptionLine = 0;

        for (int i = 0; i < count; i++) {
            subscriptions.add(new Subscription());
        }

        for (var fieldFreqEntry : fieldFreq.entrySet()) {
            String fieldName = fieldFreqEntry.getKey();
            Double freq = fieldFreqEntry.getValue();
            int fieldCount = (int) (count * freq);

            new SubscriptionsWorker(
                    count, subscriptions, fieldName, subscriptionLine, fieldCount,
                    buildDistribution(eqFreq.getOrDefault(fieldName, null))
            ).run();

            subscriptionLine += fieldCount;
        }

        return subscriptions;
    }

    public List<Subscription> generateSubscriptionsMultiThread(
            int count, Map<String, Double> fieldFreq, Map<String, Double> eqFreq) {

        ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
        while (rootGroup.getParent() != null) {
            rootGroup = rootGroup.getParent();
        }

        // Find active threads
        int numOfAvailableThreads = rootGroup.activeCount();

        List<Subscription> subscriptions = new ArrayList<>();

        int subscriptionLine = 0;

        for (int i = 0; i < count; i++) {
            subscriptions.add(new Subscription());
        }

        try (ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors
                .newFixedThreadPool(Math.min(Math.max(count / 100, fieldFreq.size()), numOfAvailableThreads))) {
            for (var fieldFreqEntry : fieldFreq.entrySet()) {
                String fieldName = fieldFreqEntry.getKey();
                Double freq = fieldFreqEntry.getValue();

                if (freq == 0.0) {
                    continue;
                }

                SynchronizedEnumeratedDistribution distribution = buildDistribution(eqFreq.getOrDefault(fieldName, null));

                int fieldCount = (int) (count * freq);
                int step = Math.min(Math.max(fieldCount / 10, 10), fieldCount);

                for (int i = 0; i < fieldCount; i += step) {
                    executor.execute(new SubscriptionsWorker(
                            count, subscriptions, fieldName, subscriptionLine, step,
                            distribution
                    ));

                    subscriptionLine += step;
                }

                if (fieldCount % step != 0) {
                    executor.execute(new SubscriptionsWorker(
                            count, subscriptions, fieldName, subscriptionLine, fieldCount % step,
                            buildDistribution(eqFreq.getOrDefault(fieldName, null))
                    ));

                    subscriptionLine += fieldCount % step;
                }
            }
        }

        return subscriptions;
    }

    private static SynchronizedEnumeratedDistribution buildDistribution(Double eqFreq) {
        if (eqFreq == null) {
            eqFreq = 1.0 / OPERATORS.length;
        }

        List<Pair<String, Double>> operatorProbabilities = new ArrayList<>();
        double otherOperatorsProb = (1.0 - eqFreq) / (OPERATORS.length - 1);

        operatorProbabilities.add(new Pair<>("=", eqFreq));

        for (String operator : OPERATORS) {
            if (!operator.equals("=")) {
                operatorProbabilities.add(new Pair<>(operator, otherOperatorsProb));
            }
        }

        return new SynchronizedEnumeratedDistribution(new EnumeratedDistribution<>(operatorProbabilities));
    }
}
