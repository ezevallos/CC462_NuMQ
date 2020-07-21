package producer;

import java.io.IOException;
import numq.libs.Channel;
import numq.libs.Connection;

/**
 *
 * @author Victor
 */
public class Producer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String hostAddr = "localhost";
        int numPort = 5555;
        try {
            Connection connection = new Connection(hostAddr, numPort);
            Channel channel = connection.getChannel();
            
            //Declara queue con el que trabajara
            channel.declareQueue("hello");
            for(int i=0;i<10;i++){
                try {
                    channel.send(null, "hello", "Hello world!");
                } catch (Exception ex) {
                    System.err.println(ex);
                }
            }
            
        } catch (IOException ex) {
            System.err.println(ex);
        }
        
    }
    
}
