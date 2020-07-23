package producer;

import java.io.IOException;
import java.util.Scanner;
import numq.libs.Channel;
import numq.libs.Connection;

/**
 * Ejemplo 2 "work queue"
 * Producer envia una tarea a la cola para que esta la distribuya
 * a los workers disponibles
 * @author Victor
 */
public class ProducerEjm2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String hostAddr = "localhost";
        int numPort = 5555;
        String queueName = "work_queue";
        int cantTareas = 20;
        String tareas[] = {"tarea1","tarea2"};
        try {
            //Conecta al middleware
            Connection connection = new Connection(hostAddr, numPort);
            
            //Obtiene canal o sesion para comunicar al middleware
            Channel channel = connection.getChannel();
            
            //Declara queue con el que trabajara
            channel.declareQueue(queueName);
            
            //Envia mensajes
            for(int i=0;i<cantTareas;i++){
                int numTarea = (int) (Math.random() * tareas.length);
                try {
                    channel.producerSend(null, queueName, null, tareas[numTarea]);
                    System.out.println("Enviado a queue: "+queueName+", tarea: "+tareas[numTarea]);
                } catch (Exception ex) {
                    System.err.println(ex);
                }
            }
            
        } catch (IOException ex) {
            System.err.println(ex);
        }
        
        Scanner sc = new Scanner(System.in);
        System.out.println("Pesione ENTER para acabar");
        String end = sc.nextLine(); //Enter para terminar
    }
}
