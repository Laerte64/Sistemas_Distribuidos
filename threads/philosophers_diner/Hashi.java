package threads.philosophers_diner;

public class Hashi {
    private boolean aquired;
    private final int id;
    
    Hashi(int id) {
        this.id = id;
        aquired = false;
    }

    public synchronized void aquire() throws InterruptedException {
        if (aquired) {
            wait();
        }
        aquired = true;
    }

    public synchronized void release() {
        aquired = false;
        notify();
    }

    public int getId() {
        return id;
    }
}
