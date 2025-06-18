package b;

class ContaBancaria {
   private double saldo;
   private final int id;
   public ContaBancaria(int id, double saldoInicial) {
       this.id = id;
       this.saldo = saldoInicial;
   }
  
   public synchronized void extrato() {
       System.out.println("Saldo atual: R$" + saldo);
   }
   public synchronized void depositar(double valor) {
       saldo += valor;
       System.out.println("Conta " + id + " fez um deposito de " + valor + ". Saldo: R$" + saldo);
   }

      public synchronized void depositarTransferencia(double valor) {
       saldo += valor;
       System.out.println("Conta " + id + " recebeu " + valor + " atraves de uma transferencia. Saldo: R$" + saldo);
   }

   public synchronized void sacar(double valor) {
       if (saldo >= valor) {
           saldo -= valor;
           System.out.println("Conta " + id + " fez um saque de " + valor + ". Saldo: R$" + saldo);
       } else {
           System.out.println("Conta " + id + " tem saldo insuficiente para realizar o saque de " + valor);
       }
   }
   public synchronized void creditarJuros(double taxa) {
       double juros = saldo * taxa;
       saldo += juros;
       System.out.println("Conta " + id + " obteve juros no valor de " + juros + ". Saldo: R$" + saldo);
   }
   public void transferir(ContaBancaria destino, double valor) {
       synchronized (this) {
           if (saldo >= valor) {
               saldo -= valor;
               System.out.println("Conta " + id + " fez uma transferencia de " + valor + " para conta " + destino.id);
           } else {
               System.out.println("Conta " + id + " tem saldo insuficiente para realizar a transferencia de " + valor);
               return;
           }
       }
       destino.depositarTransferencia(valor);
   }
}


class Transferencia implements Runnable {
   private ContaBancaria origem, destino;
   private double valor;
   public Transferencia(ContaBancaria origem, ContaBancaria destino, double valor) {
       this.origem = origem;
       this.destino = destino;
       this.valor = valor;
   }
   public void run() {
       origem.transferir(destino, valor);
   }
}


class Deposito implements Runnable {
   private ContaBancaria conta;
   private double valor;
   public Deposito(ContaBancaria conta, double valor) {
       this.conta = conta;
       this.valor = valor;
   }
   public void run() {
       conta.depositar(valor);
   }
}


class Saque implements Runnable {
   private ContaBancaria conta;
   private double valor;
   public Saque(ContaBancaria conta, double valor) {
       this.conta = conta;
       this.valor = valor;
   }
   public void run() {
       conta.sacar(valor);
   }
}


class CreditoJuros implements Runnable {
   private ContaBancaria conta;
   private double taxa;
   public CreditoJuros(ContaBancaria conta, double taxa) {
       this.conta = conta;
       this.taxa = taxa;
   }
   public void run() {
       conta.creditarJuros(taxa);
   }
}


public class Main { //Simulação do Banco
   public static void main(String[] args) {
       ContaBancaria conta1 = new ContaBancaria(1, 1000);
       ContaBancaria conta2 = new ContaBancaria(2, 500);

       // i. Transferência, depósito, crédito de juros
       Thread t1 = new Thread(new Transferencia(conta1, conta2, 200));
       Thread t2 = new Thread(new Deposito(conta1, 300));
       Thread t3 = new Thread(new CreditoJuros(conta1, 0.05));
       // ii. Saque, Depósito, crédito de juros, transferência
       Thread t4 = new Thread(new Saque(conta2, 100));
       Thread t5 = new Thread(new Deposito(conta2, 150));
       Thread t6 = new Thread(new CreditoJuros(conta2, 0.02));
       Thread t7 = new Thread(new Transferencia(conta2, conta1, 50));
       // Iniciar todas as threads
       t1.start(); 
       t2.start(); 
       t3.start();
       t4.start(); 
       t5.start(); 
       t6.start(); 
       t7.start();
       try {
           t1.join(); 
           t2.join(); 
           t3.join(); 
           t4.join(); 
           t5.join(); 
           t6.join(); 
           t7.join();
       } catch (InterruptedException e) {
           e.printStackTrace();
       }
      
       System.out.println("Simulacao finalizada.");
       System.out.println("Saldos:");
       conta1.extrato();
       conta2.extrato();
   }
}
