package consumer;

import java.io.IOException;
import java.util.Scanner;
import numq.libs.Channel;
import numq.libs.Connection;
import numq.libs.Message;

/**
 * Consumidor o worker, recibira una tarea a la vez, notifica cuando termina
 * @author Victor
 */
public class ConsumerEjm2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Consumidor - Ejmp 2: Work Queue");
        Scanner sc = new Scanner(System.in);
        System.out.println("Ingrese la direccion del middleware:");
        String hostAddr = sc.nextLine();
        
        System.out.println("Ingrese el número de puerto del middleware:");
        int numPort = Integer.parseInt(sc.nextLine());
        
        String queueName = "work_queue";
        try {
             //Conecta al middleware
            Connection connection = new Connection(hostAddr, numPort);
            
            //Obtiene canal o sesion para comunicar al middleware
            Channel channel = connection.getChannel();
            
            //Declara la queue que usara
            channel.declareQueue(queueName);
            
            System.out.println("Consumiendo desde Queue: "+queueName);
            //Consume desde la queue, autoacknowledge activado
            channel.consume(queueName, true, (Message message) -> {
                System.out.println("Tarea recibida de queue: "+queueName);
                trabajar(message.getBody());
            });
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    /**
     * Simula realizar una tarea corta y una larga
     * @param tarea 
     */
    public static void trabajar(String tarea){
        switch(tarea){
            case "tarea1":  //Tarea corta
                System.out.println("Realizando tarea corta...");
                delay(500);
                System.out.println("Tarea corta finalizada");
                break;
            case "tarea2":  //Tarea larga
                System.out.println("Realizando tarea larga...");
                delay(2000);
                System.out.println("Tarea larga finalizada");
                break;
            default:
                break;
        }
    }
    
    public static void delay(long milis){
        try {
            Thread.sleep(milis);
        } catch (InterruptedException ex) {}
    }
    
}
