package consumer;

import java.io.IOException;
import numq.libs.Channel;
import numq.libs.Connection;

/**
 *
 * @author Victor
 */
public class ConsumerEjm1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String hostAddr = "localhost";
        int numPort = 5555;
        String queueName = "hello";
        try {
             //Conecta al middleware
            Connection connection = new Connection(hostAddr, numPort);
            
            //Obtiene canal o sesion para comunicar al middleware
            Channel channel = connection.getChannel();
            
            //Declara la queue que usara
            channel.declareQueue(queueName);
            
            System.out.println("Consumiendo desde Queue: "+queueName);
            //Consume desde la queue, autoacknowledge activado
            channel.consume(queueName, true, (String body) -> {
                System.out.println(queueName+" > "+body);
            });
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
}
