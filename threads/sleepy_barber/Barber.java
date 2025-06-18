package threads.sleepy_barber;

class Barber implements Runnable {
    private final Shop shop;
    public Barber(Shop shop) {
        this.shop = shop;
    }

    @Override
    public void run() {
        try {
            while (!shop.timeToClose || !shop.empty()) {
                Client nextClient;
                synchronized (shop) {
                    while ((nextClient = shop.nextClient()) == null && !shop.timeToClose) {
                        System.out.println("O barbeiro vai tirar uma soneca");
                        shop.wait();
                    }
                }

                while (nextClient != null) {
                    cutHair(nextClient);
                    synchronized (shop) {
                        nextClient = shop.nextClient();
                    }
                }
            }

            System.out.println("O barbeiro fechou a barbearia");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void cutHair(Client client) throws InterruptedException {
        System.out.println("O barbeiro esta cortando o cabelo de " + client.getName());
        Thread.sleep(800);
        synchronized(client) {
            client.notify();
        }
    }
}

