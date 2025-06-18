package threads.bar;

public class Main {
    public static void main(String[] args) {
        int clients = 10;
        int waiters = 2;
        int capacity = 3;
        int rounds = 5;

        Bar bar = new Bar(clients, waiters, capacity, rounds);
        bar.startSimulation();
    }
}