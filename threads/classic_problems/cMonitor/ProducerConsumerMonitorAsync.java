package cMonitor;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class ProducerConsumerMonitorAsync {
    public static void main(String[] args) throws InterruptedException {
        final int BUFFER_SIZE = 10;
        MonitorBuffer buffer = new MonitorBuffer(BUFFER_SIZE);

        // dispara produtor e consumidor de forma assíncrona
        CompletableFuture<Void> producerTask = CompletableFuture.runAsync(new Producer(buffer));
        CompletableFuture<Void> consumerTask = CompletableFuture.runAsync(new Consumer(buffer));

        Thread.sleep(30_000);
        System.out.println("Simulacao com monitores finalizada.");
    }
}

class MonitorBuffer {
    private final Queue<Integer> queue = new LinkedList<>();
    private final int capacity;

    public MonitorBuffer(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void produce(int item) throws InterruptedException {
        while (queue.size() == capacity) {
            wait();
        }
        queue.offer(item);
        System.out.printf("Produziu %d%n", item);
        notifyAll();
    }

    public synchronized int consume() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        int item = queue.poll();
        System.out.printf("Consumiu %d%n", item);
        notifyAll();
        return item;
    }
}

class Producer implements Runnable {
    private final MonitorBuffer buffer;
    public Producer(MonitorBuffer buffer) {
        this.buffer = buffer;
    }
    @Override
    public void run() {
        try {
            while (true) {
                int item = ThreadLocalRandom.current().nextInt(1, 101);
                Thread.sleep(ThreadLocalRandom.current().nextInt(0, 2000));
                buffer.produce(item);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class Consumer implements Runnable {
    private final MonitorBuffer buffer;
    public Consumer(MonitorBuffer buffer) {
        this.buffer = buffer;
    }
    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(ThreadLocalRandom.current().nextInt(0, 2000));
                buffer.consume();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
