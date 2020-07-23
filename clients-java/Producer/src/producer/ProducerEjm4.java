package producer;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import numq.libs.Channel;
import numq.libs.Connection;
import numq.libs.Message;

/**
 * Ejemplo 4
 * Cliente de RPC, realiza request y espera response
 * las queues suscritas
 * @author Victor
 */
public class ProducerEjm4 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        String hostAddr = "localhost";
        int numPort = 5555;
        try {
            RPCClient rpcc = new RPCClient(hostAddr, numPort);
            for(int i=0;i<10;i++){
                String i_str = Integer.toString(i);
                System.out.println("Solicito fib("+i_str+")");
                String response = rpcc.call(i_str);
                System.out.println("Respuesta: "+response);
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        
    }
    
    public static class RPCClient{
        Connection connection;
        Channel channel;
        String queueName = "rpc_queue";
        
        public RPCClient(String hostAddr,int numPort) throws IOException{
            //Conecta al middleware
            connection = new Connection(hostAddr, numPort);

            //Obtiene canal o sesion para comunicar al middleware
            channel = connection.getChannel();

            //Declara queue de request
            channel.declareQueue(queueName);
        }
        
        public String call(String msg) throws Exception{
            //Declara queue de respuesta
            String replyQueue = channel.declareQueue();
                
            //Envia request
            channel.producerSend(null, queueName, replyQueue, msg);
               
            final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);
                
            channel.consume(replyQueue, true, (Message message) -> {
                response.offer(message.getBody());
            });
                
            String result = response.take();
            channel.cancelConsume();
                
            return result;
        }
    } 
}
