package threads.bar;

import java.util.Random;

class Client implements Runnable {
    private final int id;
    private final Bar bar;
    private final Random random = new Random();

    public Client(int id, Bar bar) {
        this.id = id;
        this.bar = bar;
    }

    public int getId() {
        return id;
    }

    @Override
    public void run() {
        while (bar.isOpen()) {
            if (random.nextBoolean()) {
                Order order = new Order(this, bar.getCurrentRound());
                bar.addWaitingClient(this);
                System.out.println("[Client " + id + "] placed order.");
                try {
                    order.waitUntilReady();
                    System.out.println("[Client " + id + "] received order and is consuming it.");
                    Thread.sleep(random.nextInt(2000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}