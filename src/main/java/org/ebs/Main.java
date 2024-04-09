package org.ebs;

import org.ebs.publication.PublicationGenerator;
import org.ebs.subscription.SubscriptionGenerator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
        long subscriptionStartTime = System.currentTimeMillis();
//        var subscriptions = subscriptionGenerator.generateSubscriptions(10, fieldFreq, eqFreq);
        var subscriptions = subscriptionGenerator.generateSubscriptions(100000, fieldFreq, eqFreq);
        long subscriptionEndTime = System.currentTimeMillis();

        long subscriptionStartTimeMultiThread = System.currentTimeMillis();
//        var subscriptionsMultiThread = subscriptionGenerator.generateSubscriptionsMultiThread(10, fieldFreq, eqFreq);
        var subscriptionsMultiThread = subscriptionGenerator.generateSubscriptionsMultiThread(100000, fieldFreq, eqFreq);
        long subscriptionEndTimeMultiThread = System.currentTimeMillis();

        System.out.println("Subscriptions time: " + (subscriptionEndTime - subscriptionStartTime) + " ms");
        System.out.println("Subscriptions time MultiThread: " + (subscriptionEndTimeMultiThread - subscriptionStartTimeMultiThread) + " ms");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("subscriptions.txt"))) {
            for (var subscription : subscriptions) {
                writer.write(subscription.toString());
                writer.newLine();
            }
            System.out.println("Subscriptions written to subscriptions.txt");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("subscriptionsMultiThreaded.txt"))) {
            for (var subscription : subscriptionsMultiThread) {
                writer.write(subscription.toString());
                writer.newLine();
            }
            System.out.println("Subscriptions written to subscriptionsMultiThreaded.txt");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }

        System.out.println("-------------------------------------------------------------");

//        -------------------------------------------------------------------------------------------------------------

        Map<String, Double> pubFieldFreq = Map.of(
                "company", 1.0,
                "value", 1.0,
                "drop", 1.0,
                "variation", 1.0,
                "date", 1.0
        );
        PublicationGenerator publicationGenerator = new PublicationGenerator();
        long publicationStartTime = System.currentTimeMillis();
//        var publications = publicationGenerator.generatePublications(10, pubFieldFreq);
        var publications = publicationGenerator.generatePublications(100000, pubFieldFreq);
        long publicationEndTime = System.currentTimeMillis();

        long publicationStartTimeMultiThread = System.currentTimeMillis();
//        var publicationsMultiThread = publicationGenerator.generatePublicationsMultiThread(10, pubFieldFreq);
        var publicationsMultiThread = publicationGenerator.generatePublicationsMultiThread(100000, pubFieldFreq);
        long publicationEndTimeMultiThread = System.currentTimeMillis();

        System.out.println("Publications time: " + (publicationEndTime - publicationStartTime) + " ms");
        System.out.println("Publications time MultiThread: " + (publicationEndTimeMultiThread - publicationStartTimeMultiThread) + " ms");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("publications.txt"))) {
            for (var publication : publications) {
                writer.write(publication.toString());
                writer.newLine();
            }
            System.out.println("Publications written to publications.txt");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("publicationsMultiThreaded.txt"))) {
            for (var publication : publicationsMultiThread) {
                writer.write(publication.toString());
                writer.newLine();
            }
            System.out.println("Publications written to publicationsMultiThreaded.txt");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}