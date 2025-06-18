package threads.sleepy_barber;

class Client implements Runnable {
    private final Shop shop;
    private final String name;
    public Client(String name, Shop shop) {
        this.shop = shop;
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    @Override
    public void run() {
        System.out.println(name + " chegou na barbearia.");
        try {
            if (shop.enterShop(this)) {
                synchronized (this) {
                    wait();
                }
                System.out.println(name + " saiu satifeito.");
            }
            else
                System.out.println(name + " saiu insatisfeito.");
        } 
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}