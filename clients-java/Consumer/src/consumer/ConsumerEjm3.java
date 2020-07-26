package consumer;

import java.io.IOException;
import java.util.Scanner;
import numq.libs.Channel;
import numq.libs.Connection;
import numq.libs.Message;

/**
 * Consumer subscribe su propia queue a un topico
 * @author Victor
 */
public class ConsumerEjm3 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Consumidor - Ejmp 3: Publish/Subscriber");
        Scanner sc = new Scanner(System.in);
        System.out.println("Ingrese la direccion del middleware:");
        String hostAddr = sc.nextLine();
        
        System.out.println("Ingrese el número de puerto del middleware:");
        int numPort = Integer.parseInt(sc.nextLine());
        
        String topicName = "logs";
        try {
             //Conecta al middleware
            Connection connection = new Connection(hostAddr, numPort);
            
            //Obtiene canal o sesion para comunicar al middleware
            Channel channel = connection.getChannel();
            
            //Declara el topic que usara
            channel.declareTopic(topicName);
            System.out.println("Tópico a suscribir: "+topicName);
            
            //Autogenera una queue y la declara
            String queueName = channel.declareQueue();
            System.out.println("Queue generada: "+queueName);
            
            //Subscribe la queue al topic
            channel.subscribeQueue(topicName, queueName);
            System.out.println("Queue: "+queueName+ " suscrita a Topico: "+topicName);
            
            System.out.println("Consumiendo desde Queue: "+queueName);
            //Consume desde la queue, autoacknowledge activado
            channel.consume(queueName, true, (Message message) -> {
                System.out.println(topicName+":"+queueName+" > "+message.getBody());
            });
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
}
