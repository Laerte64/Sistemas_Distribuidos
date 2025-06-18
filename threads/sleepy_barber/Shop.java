package threads.sleepy_barber;
import java.util.concurrent.ArrayBlockingQueue;

class Shop {
    public boolean timeToClose;
    private final ArrayBlockingQueue<Client> queue;
    public Shop(int chairsNum) {
        this.queue = new ArrayBlockingQueue<Client>(chairsNum);
        timeToClose = false;
    }

    public synchronized boolean empty() {
        return queue.isEmpty();
    }

    public boolean enterShop(Client client) {
        synchronized (this) {
            if (timeToClose || !queue.offer(client)) {
                return false;
            }
            notify();
            return true;
        }
    }

    public synchronized Client nextClient() {
        return queue.poll();
    }
}