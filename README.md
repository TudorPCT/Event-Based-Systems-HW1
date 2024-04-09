# Event-Based-Systems-HW1

### Paralelization type
We opted for threads for the parallel approach of generating subscriptions and publications. 

Threads within the same process share the same memory space, which makes inter-thread communication and data sharing much more efficient compared to inter-process communication (IPC).

Threads add fields in a specific publication/subscription using a synchronized method:
```java
public void addField(PublicationField field) {
    synchronized (fields) {
        fields.add(field);
    }
}

public void addField(SubscriptionField field) {
    synchronized (fields) {
        fields.add(field);
    }
}
```
Where fields is a synchronized list for both cases and it is instantiated in constructor.
```java
this.fields = Collections.synchronizedList(new ArrayList<>());
```
The number of generated threads in the ThreadPoolExecutor is computed using a formula, considering the number of fields to be generated, the number of fields and the number of fields available in the context of current JVM.
```java
try (ThreadPoolExecutor executor = 
(ThreadPoolExecutor) Executors
                .newFixedThreadPool(Math.min(Math.max(count / 100, fieldFreq.size()), numOfAvailableThreads))
```
Formula is meant to be balanced in a variety of cases, including edge cases, such as:
1. A small number of fields, but a large number of subscriptions/publications
2. A large number of fields and a relatively close number of subscriptions/publications (subs/pubs are dispersed).

The max between the number of threads for each extreme case is then computed and number of available threads in JVM context is considered as upper bound.
Then for each field there is another batching process in order to avoid using a single thread on a field that has a higher percent. Batches are computed as 10% of number of entries for that field with a lower bound of 10, in order to avoid splitting into small bathes.

### Results obtained
For the first runs we considered 100.000 

Context in which the aforementioned results were obtained is represented in the below picture. The usage of both CPU and memory were low in the moment of benchmark.

![alt text](image.png)

| Task              | Single Thread Time (ms) | Multi-Thread Time (ms) | Output File                    | Multi-Threaded Output File           |
|-------------------|-------------------------|------------------------|-------------------------------|--------------------------------------|
| Subscriptions     | 299                     | 98                     | subscriptions.txt             | subscriptionsMultiThreaded.txt       |
| Publications      | 190                     | 29                     | publications.txt              | publicationsMultiThreaded.txt        |


Our solution clearly comes with an improvement in both areas of subscriptions and publications: 6 times faster publications generation and 3 times subscriptions generation.
