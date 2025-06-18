package threads.philosophers_diner;

public class Philosopher extends Thread {
    private final int id;
    private final Hashi leftHashi;
    private final Hashi rightHashi;
    public Philosopher(int id, Hashi leftHashi, Hashi rightHashi) {
        this.id = id;
        this.leftHashi = leftHashi;
        this.rightHashi = rightHashi;
    }
    private void meditate() throws InterruptedException {
        System.out.println("Filósofo " + id + " está meditando");
        Thread.sleep((long) (Math.random() * 1000));
    }

    private void eat() throws InterruptedException {
        System.out.println("Filósofo " + id + " está comendo com os palitos " + leftHashi.getId() + " e " + rightHashi.getId());
        Thread.sleep((long) (Math.random() * 1000));
    }

    @Override
    public void run() {
        try {
            Hashi firstHashi = id % 2 == 0 ? rightHashi : leftHashi;
            Hashi secondHashi = id % 2 == 0 ? leftHashi : rightHashi;
            while (true) {
                meditate();
                System.out.println("Filósofo " + id + " está esperando o primeiro palito");
                synchronized (firstHashi) {
                    firstHashi.aquire();
                    System.out.println("Filósofo " + id + " está esperando o segundo palito");
                    synchronized (secondHashi) {
                        secondHashi.aquire();
                        eat();
                        secondHashi.release();
                    }
                    firstHashi.release();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
