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
        System.out.println("Producer - Ejmp 3: Publisher/Subscriber");
        Scanner sc = new Scanner(System.in);
        System.out.println("Ingrese la direccion del middleware:");
        String hostAddr = sc.nextLine();
        
        //System.out.println("Ingrese el número de puerto del middleware:");
        int numPort = 5555;//Integer.parseInt(sc.nextLine());
        
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
                    channel.producerSend(topicName, null, null,msg);
                    System.out.println("Enviado a topic: "+topicName+", Mensaje: "+msg);
                } catch (Exception ex) {
                    System.err.println(ex);
                }
            }
            
        } catch (IOException ex) {
            System.err.println(ex);
        }
        
        System.out.println("Pesione ENTER para acabar");
        String end = sc.nextLine(); //Enter para terminar
    }
}
