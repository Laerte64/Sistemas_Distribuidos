package threads.bar;

import java.util.ArrayList;
import java.util.List;

class Waiter implements Runnable {
    private final int id;
    private final Bar bar;

    public Waiter(int id, Bar bar) {
        this.id = id;
        this.bar = bar;
    }

    @Override
    public void run() {
        while (bar.isOpen()) {
            List<Order> batch = new ArrayList<>();
            for (int i = 0; i < bar.getWaiterCapacity(); i++) {
                Client c = bar.getOrder();
                if (c != null) {
                    Order order = new Order(c, bar.getCurrentRound());
                    batch.add(order);
                }
            }
            if (!batch.isEmpty()) {
                System.out.println("[Waiter " + id + "] delivering " + batch.size() + " orders.");
                for (Order o : batch) {
                    try {
                        Thread.sleep(500); // simulate preparation
                        o.setReady();
                        System.out.println("[Waiter " + id + "] delivered " + o);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                bar.nextRound();
            } else {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}