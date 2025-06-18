package threads.bar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bar {
    private final List<Client> waitingClients = new ArrayList<>();
    private final Random random = new Random();
    private boolean open = true;
    private final int numClients;
    private final int numWaiters;
    private final int waiterCapacity;
    private final int rounds;
    private int currentRound = 0;
    private final List<Thread> clientThreads = new ArrayList<>();
    private final List<Thread> waiterThreads = new ArrayList<>();

    public Bar(int numClients, int numWaiters, int waiterCapacity, int rounds) {
        this.numClients = numClients;
        this.numWaiters = numWaiters;
        this.waiterCapacity = waiterCapacity;
        this.rounds = rounds;
    }

    public synchronized void addWaitingClient(Client c) {
        if (!waitingClients.contains(c)) {
            waitingClients.add(c);
        }
    }

    public synchronized Client getOrder() {
        if (waitingClients.isEmpty()) return null;
        return waitingClients.remove(random.nextInt(waitingClients.size()));
    }

    public synchronized boolean isOpen() {
        return open;
    }

    public synchronized int getWaiterCapacity() {
        return waiterCapacity;
    }

    public synchronized int getCurrentRound() {
        return currentRound;
    }

    public synchronized void nextRound() {
        currentRound++;
        if (currentRound >= rounds) {
            open = false;
            System.out.println("[Bar] Closed after " + rounds + " rounds.");
        } else {
            System.out.println("[Bar] Starting round " + currentRound);
        }
    }

    public void startSimulation() {
        for (int i = 0; i < numClients; i++) {
            Client c = new Client(i, this);
            Thread t = new Thread(c);
            clientThreads.add(t);
            t.start();
        }
        for (int i = 0; i < numWaiters; i++) {
            Waiter w = new Waiter(i, this);
            Thread t = new Thread(w);
            waiterThreads.add(t);
            t.start();
        }
        for (Thread t : clientThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        for (Thread t : waiterThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
