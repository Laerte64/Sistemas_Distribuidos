package cSemaforo;

import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.CompletableFuture;

public class ProducerConsumerSemaphoreAsync {
    public static void main(String[] args) throws InterruptedException {
        final int BUFFER_SIZE = 10;
        BoundedBuffer buffer = new BoundedBuffer(BUFFER_SIZE);

        // dispara produtor e consumidor de forma assíncrona
        CompletableFuture<Void> producerTask = CompletableFuture.runAsync(new Producer(buffer));
        CompletableFuture<Void> consumerTask = CompletableFuture.runAsync(new Consumer(buffer));

        Thread.sleep(30_000);
        System.out.println("Simulação com semáforos finalizada.");
    }
}

class BoundedBuffer {
    private final int[] buffer;
    private int in = 0, out = 0;
    private final Semaphore mutex = new Semaphore(1);
    private final Semaphore spaces;
    private final Semaphore items = new Semaphore(0);

    public BoundedBuffer(int size) {
        buffer = new int[size];
        spaces = new Semaphore(size);
    }

    public void produce(int item) throws InterruptedException {
        spaces.acquire();      // espera espaço livre
        mutex.acquire();       // entra na região crítica
        buffer[in] = item;
        in = (in + 1) % buffer.length;
        System.out.printf("Produziu %d%n", item);
        mutex.release();
        items.release();       // sinaliza item disponível
    }

    public int consume() throws InterruptedException {
        items.acquire();       // espera item disponível
        mutex.acquire();       // entra na região crítica
        int item = buffer[out];
        out = (out + 1) % buffer.length;
        System.out.printf("Consumiu %d%n", item);
        mutex.release();
        spaces.release();      // sinaliza espaço livre
        return item;
    }
}

class Producer implements Runnable {
    private final BoundedBuffer buffer;
    public Producer(BoundedBuffer buffer) {
        this.buffer = buffer;
    }
    @Override
    public void run() {
        try {
            while (true) {
                int item = ThreadLocalRandom.current().nextInt(1, 101);
                Thread.sleep(ThreadLocalRandom.current().nextInt(0, 3000));
                buffer.produce(item);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class Consumer implements Runnable {
    private final BoundedBuffer buffer;
    public Consumer(BoundedBuffer buffer) {
        this.buffer = buffer;
    }
    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(ThreadLocalRandom.current().nextInt(0, 3000));
                buffer.consume();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
