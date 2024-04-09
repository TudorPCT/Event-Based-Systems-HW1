package org.ebs.publication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class PublicationGenerator {
    public List<Publication> generatePublications(
            int count, Map<String, Double> fieldFreq){

        List<Publication> publications = new ArrayList<>();

        int publicationLine = 0;

        for (int i = 0; i < count; i++) {
            publications.add(new Publication());
        }

        for (var fieldFreqEntry: fieldFreq.entrySet()) {
            String fieldName = fieldFreqEntry.getKey();
            Double freq = fieldFreqEntry.getValue();
            int fieldCount = (int) (count * freq);

            new PublicationsWorker(
                    count, publications, fieldName, publicationLine, fieldCount
            ).run();

            publicationLine += fieldCount;
        }

        return publications;
    }

    public List<Publication> generatePublicationsMultiThread(
            int count, Map<String, Double> fieldFreq) {

        List<Publication> publications = new ArrayList<>();

        int publicationLine = 0;

        for (int i = 0; i < count; i++) {
            publications.add(new Publication());
        }

        ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
        while (rootGroup.getParent() != null) {
            rootGroup = rootGroup.getParent();
        }

        // Find active threads
        int numOfAvailableThreads = rootGroup.activeCount();

        try (ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors
                .newFixedThreadPool(Math.min(Math.max(count / 100, fieldFreq.size()), numOfAvailableThreads))
        ) {
            for (var fieldFreqEntry : fieldFreq.entrySet()) {
                String fieldName = fieldFreqEntry.getKey();
                Double freq = fieldFreqEntry.getValue();

                if (freq == 0.0) {
                    continue;
                }

                int fieldCount = (int) (count * freq);
                int step = Math.min(Math.max(fieldCount / 10, 10), fieldCount);

                for(int i = 0; i < fieldCount; i += step ) {
                    executor.execute(new PublicationsWorker(
                            count, publications, fieldName, publicationLine, step
                    ));

                    publicationLine += step;
                }

                if (fieldCount % step != 0) {
                    executor.execute(new PublicationsWorker(
                            count, publications, fieldName, publicationLine, fieldCount % step
                    ));

                    publicationLine += fieldCount % step;
                }
            }
        }

        return publications;
    }
}
