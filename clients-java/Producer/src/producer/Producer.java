package producer;

import java.io.IOException;
import java.util.Scanner;
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
        String queueName = "hello";
        String body = "Hello world!";
        try {
            Connection connection = new Connection(hostAddr, numPort);
            Channel channel = connection.getChannel();
            
            //Declara queue con el que trabajara
            channel.declareQueue(queueName);
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
        String end = sc.nextLine(); //Enter para terminar
    }
}
