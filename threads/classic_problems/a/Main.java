package a;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

class Casino {
    private final boolean[] roletas;
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    public Casino(int numRoletas) {
        roletas = new boolean[numRoletas];
        Arrays.fill(roletas, true);
    }

    public int adquirirRoleta(int jogadorId) throws InterruptedException {
        lock.lock();
        try {
            while (!temRoletaLivre()) {
                System.out.println("Jogador " + jogadorId + " esta esperando por uma roleta livre.");
                condition.await();
            }
            int idx = primeiroIndiceLivre();
            roletas[idx] = false;
            System.out.println("Jogador " + jogadorId + " ocupou a roleta " + idx + ".");
            return idx;
        } finally {
            lock.unlock();
        }
    }

    public void liberarRoleta(int idx, int jogadorId) {
        lock.lock();
        try {
            roletas[idx] = true;
            System.out.println("Jogador " + jogadorId + " liberou a roleta " + idx + ".");
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private boolean temRoletaLivre() {
        for (boolean r : roletas) if (r) return true;
        return false;
    }

    private int primeiroIndiceLivre() {
        for (int i = 0; i < roletas.length; i++)
            if (roletas[i]) return i;
        return -1;
    }
}

class Jogador implements Runnable {
    private final Casino casino;
    private final int id;
    private final int numJogadas;

    public Jogador(Casino casino, int id, int numJogadas) {
        this.casino = casino;
        this.id = id;
        this.numJogadas = numJogadas;
    }

    @Override
    public void run() {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        for (int t = 1; t <= numJogadas; t++) {
            try {
                int idx = casino.adquirirRoleta(id);
                System.out.println("Jogador " + id + " esta jogando na roleta " + idx + " (tentativa " + t + ").");
                Thread.sleep((long)((1 + rand.nextDouble() * 2) * 1000));
                System.out.println("Jogador " + id + " terminou de jogar na roleta " + idx + " (tentativa " + t + ").");
                casino.liberarRoleta(idx, id);
                Thread.sleep((long)((0.5 + rand.nextDouble() * 1.5) * 1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Jogador " + id + " finalizou suas jogadas.");
    }
}

public class Main {
    public static void main(String[] args) {
        int numRoletas   = 3;
        int numJogadores = 10;
        Casino casino    = new Casino(numRoletas);

        // Dispara todas as tarefas de forma assíncrona
        CompletableFuture<?>[] futures = IntStream.range(0, numJogadores)
            .mapToObj(i -> CompletableFuture.runAsync(
                new Jogador(casino, i, 3)
            ))
            .toArray(CompletableFuture[]::new);

        // Aguarda todas terminarem
        CompletableFuture.allOf(futures).join();

        System.out.println("Simulacao de roletas concluida.");
    }
}
