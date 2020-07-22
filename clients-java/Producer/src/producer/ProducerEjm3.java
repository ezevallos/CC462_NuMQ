package producer;

import java.io.IOException;
import java.util.Scanner;
import numq.libs.Channel;
import numq.libs.Connection;

/**
 * Ejemplo 3
 * Producer envia un mensaje un topico para que este lo distribuya 
 * las queues suscritas
 * @author Victor
 */
public class ProducerEjm3 {

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
            
            //Declara queue con el que trabajara
            channel.declareTopic(topicName);
            
            //Envia mensajes
            for(int i=0;i<10;i++){
                String msg = "Registro nro "+(i+1);
                try {
                    channel.producerSend(topicName, null, msg);
                    System.out.println("Enviado a topic: "+topicName+", Mensaje: "+msg);
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
