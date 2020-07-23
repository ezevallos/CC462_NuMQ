package middleware.server;

import java.util.HashSet;
import java.util.Set;

/**
 * Clase que gestiona el proceso Publish/Subscribe
 * Los producers envian mensajes al topico,
 * los consumers suscriben queues al topico,
 * el topico difunde los mensajes a las queues suscritas
 * @author Victor
 */
public class Topic {
    private Set<String> subsQeueus;
    private final String mName;
    private Middleware mMiddleware;
    
    public Topic(Middleware middleware, String name){
        mMiddleware = middleware;
        mName = name;
        subsQeueus = new HashSet<>();
    }
    
    /**
     * Se suscribe un queue al topic
     * @param queueName 
     */
    public void subscribe(String queueName){
        subsQeueus.add(queueName);
    }
    
    /**
     * Envia el mensaje a los queues suscritos
     * @param msg 
     */
    public void send(Message msg){
        Set<String> subs = new HashSet<>(subsQeueus);
        for(String subId : subs){   //Itera todos los queues suscritos
            Queue queue = mMiddleware.getQueues().get(subId);
            if(queue!=null){
                try {
                    queue.putMsg(msg);  //envia mensaje a queue
                } catch (InterruptedException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
    }
    
}
