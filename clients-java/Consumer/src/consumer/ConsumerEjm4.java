package consumer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import numq.libs.Channel;
import numq.libs.Channel.CallBack;
import numq.libs.Connection;
import numq.libs.Message;

/**
 * Servidor que consume de queue rpc
 * Envia la respuesta al replyQueue
 * @author Victor
 */
public class ConsumerEjm4 {
    
    
    public static void main(String[] args){
        String serverAddr = "localhost";
        int numPort = 5555;
        String queueName = "rpc_queue";
        
        try {
            Connection connection = new Connection(serverAddr, numPort);
            Channel channel = connection.getChannel();
            
            channel.declareQueue(queueName);
            
            Channel.CallBack callBack = (Message message) -> {
                try {
                    String response = "";
                    String msg = message.getBody();
                    int n = Integer.parseInt(msg);
                    System.out.println("fib("+n+")");
                    response += fib(n);
                    
                    channel.producerSend(null, message.getReplyQueue(), null, response);
                    channel.consAck(queueName);
                    System.out.println("Resp: "+response+" > "+message.getReplyQueue());
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                }
            };
            
            channel.consume(queueName, false, callBack);
            
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
