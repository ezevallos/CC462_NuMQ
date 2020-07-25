package producer;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
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
        Scanner sc = new Scanner(System.in);
        System.out.println("Ingrese la direccion del middleware:");
        String hostAddr = sc.nextLine();
        
        System.out.println("Ingrese el número de puerto del middleware:");
        int numPort = Integer.parseInt(sc.nextLine());
        
        try {
            RPCClient rpcc = new RPCClient(hostAddr, numPort);
            rpcc.receivResponse();
            for(int i=1;i<11;i++){
                String i_str = Integer.toString(i);
                System.out.println("Solicito fib("+i_str+")");
                rpcc.sendRequest(i_str);
            }
            
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        System.out.println("Presione ENTER para salir");
        String end = sc.nextLine();
    }
    
    public static class RPCClient{
        Connection connection;
        Channel channel;
        String queueName = "rpc_queue";
        String replyQueue;
        BlockingQueue<String> response;
        
        public RPCClient(String hostAddr,int numPort) throws IOException{
            //Conecta al middleware
            connection = new Connection(hostAddr, numPort);

            //Obtiene canal o sesion para comunicar al middleware
            channel = connection.getChannel();

            //Declara queue de request
            channel.declareQueue(queueName);
            replyQueue = channel.declareQueue();
            response = new ArrayBlockingQueue<>(1);
        }
        
        public void sendRequest(String msg) throws Exception{
            //Envia request
            channel.producerSend(null, queueName, replyQueue, msg);
            String resp = response.take();
            System.out.println("Respuesta: "+resp);
        }
        
        public void receivResponse(){
            channel.consume(replyQueue, true, (Message message) -> {
                response.offer(message.getBody());
            });
        }
    } 
}
