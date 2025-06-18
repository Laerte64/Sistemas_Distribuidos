package threads.philosophers_diner;

public class Main {
    public static void main(String[] args) {
        int N = 5;

        Hashi firstHashi = new Hashi(1);
        Hashi lastHashi = firstHashi;
        for (int i = 1; i < N; i++) {
            Hashi newHashi = new Hashi(i + 1);
            Philosopher philosopher = new Philosopher(i, lastHashi, newHashi);
            lastHashi = newHashi;
            philosopher.start();
        }
        Philosopher lastPhilosopher = new Philosopher(N, lastHashi, firstHashi);
        lastPhilosopher.start();
    }
}