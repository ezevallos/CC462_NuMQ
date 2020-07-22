package consumer;

import java.io.IOException;
import numq.libs.Channel;
import numq.libs.Connection;

/**
 *
 * @author Victor
 */
public class Consumer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String hostAddr = "localhost";
        int numPort = 5555;
        String queueName = "hello";
        try {
            Connection connection = new Connection(hostAddr, numPort);
            Channel channel = connection.getChannel();
            channel.declareQueue(queueName);
            System.out.println("Consumiendo desde Queue: "+queueName);
            channel.consume(queueName, true, (String body) -> {
                System.out.println(queueName+" > "+body);
            });
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
}
