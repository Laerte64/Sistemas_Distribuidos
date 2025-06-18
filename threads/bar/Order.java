package threads.bar;

class Order {
    private final Client client;
    private final int round;
    private boolean ready;

    public Order(Client client, int round) {
        this.client = client;
        this.round = round;
    }

    public Client getClient() {
        return client;
    }

    public int getRound() {
        return round;
    }

    public synchronized boolean isReady() {
        return ready;
    }

    public synchronized void setReady() {
        ready = true;
        notifyAll();
    }

    public synchronized void waitUntilReady() throws InterruptedException {
        while (!ready) wait();
    }

    @Override
    public String toString() {
        return "[Order] from Client " + client.getId() + " at round " + round;
    }
}