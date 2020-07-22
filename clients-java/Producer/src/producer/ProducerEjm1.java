package producer;

import java.io.IOException;
import java.util.Scanner;
import numq.libs.Channel;
import numq.libs.Connection;

/**
 * Ejemplo 1
 * Producer envia un mensaje a una cola
 * @author Victor
 */
public class ProducerEjm1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String hostAddr = "localhost";
        int numPort = 5555;
        String queueName = "hello";
        String body = "Hello world!";
        try {
            //Conecta al middleware
            Connection connection = new Connection(hostAddr, numPort);
            
            //Obtiene canal o sesion para comunicar al middleware
            Channel channel = connection.getChannel();
            
            //Declara queue con el que trabajara
            channel.declareQueue(queueName);
            
            //Envia mensajes
            for(int i=0;i<10;i++){
                try {
                    channel.producerSend(null, queueName, body);
                    System.out.println("Enviado a queue: "+queueName+", Mensaje: "+body);
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
