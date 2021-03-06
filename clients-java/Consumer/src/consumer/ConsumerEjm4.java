package consumer;

import java.io.IOException;
import java.util.Scanner;
import numq.libs.Channel;
import numq.libs.Connection;
import numq.libs.Message;

/**
 * Servidor que consume de queue rpc
 * Envia la respuesta al replyQueue
 * @author Victor
 */
public class ConsumerEjm4 {
    
    
    public static void main(String[] args){
        System.out.println("Consumidor - Ejmp 4: RPC-Queue");
        Scanner sc = new Scanner(System.in);
        System.out.println("Ingrese la direccion del middleware:");
        String hostAddr = sc.nextLine();
        
        //System.out.println("Ingrese el número de puerto del middleware:");
        int numPort = 5555;//Integer.parseInt(sc.nextLine());
        
        String queueName = "rpc_queue";
        
        try {
            Connection connection = new Connection(hostAddr, numPort);
            Channel channel = connection.getChannel();
            
            channel.declareQueue(queueName);
            
            Channel.CallBack callBack = (Message message) -> {
                try {
                    String response = "";
                    String msg = message.getBody();
                    int n = Integer.parseInt(msg);
                    System.out.println("fib("+n+")");
                    response += "fib("+n+") = "+fib(n);
                    
                    channel.producerSend(null, message.getReplyQueue(), null, response);
                    channel.consAck(queueName);
                    System.out.println("Resp: "+response+" > "+message.getReplyQueue());
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                }
            };
            
            channel.consume(queueName, false, callBack);
            System.out.println("Consumiendo desde queue: "+queueName);
            
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
       
    }
    
    public static int fib(int n){
        if (n == 0) return 0;
        if (n == 1) return 1;
        return fib(n - 1) + fib(n - 2);
    }
}
