package threads.sleepy_barber;

class Main {
    public static void main(String[] args) {
        Shop shop = new Shop(5);
        Barber barber = new Barber(shop);
        Thread barberThread = new Thread(barber, "Barbeiro");
        barberThread.start();

        int i = 1;
        long closeTime = System.currentTimeMillis() + 5_000;
        for (long time = 0; time <= closeTime; time = System.currentTimeMillis()) {
            Client client = new Client("Cliente " + i++, shop);
            Thread thread = new Thread(client, client.getName());
            thread.start();
            try {
                Thread.sleep(200 + (int) (Math.random() * 800));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        synchronized (shop) {
            shop.timeToClose = true;
            shop.notify();
        }
    }
}