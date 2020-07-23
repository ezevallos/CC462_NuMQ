package consumer;

import java.io.IOException;
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
        String hostAddr = "localhost";
        int numPort = 5555;
        String topicName = "logs";
        try {
             //Conecta al middleware
            Connection connection = new Connection(hostAddr, numPort);
            
            //Obtiene canal o sesion para comunicar al middleware
            Channel channel = connection.getChannel();
            
            //Declara el topic que usara
            channel.declareTopic(topicName);
            
            //Autogenera una queue y la declara
            String queueName = channel.declareQueue();
            
            //Subscribe la queue al topic
            channel.subscribeQueue(topicName, queueName);
            
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
