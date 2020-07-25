package consumer;

import java.io.IOException;
import java.util.Scanner;
import numq.libs.Channel;
import numq.libs.Connection;
import numq.libs.Message;

/**
 *
 * @author Victor
 */
public class ConsumerEjm1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Ingrese la direccion del middleware:");
        String hostAddr = sc.nextLine();
        
        System.out.println("Ingrese el número de puerto del middleware:");
        int numPort = Integer.parseInt(sc.nextLine());
        
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
            channel.consume(queueName, true, (Message message) -> {
                System.out.println(queueName+" > "+message.getBody());
            });
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
}
