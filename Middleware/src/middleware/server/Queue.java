package middleware.server;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Almacena los mensajes para que el broker haga put y poll
 * @author Victor
 */
public class Queue {
    public static final int MAX_SIZE = 10; 
    private final BlockingQueue<String> mQueue; //Tipo String por mientras
    private final Set<Integer> consumersIds;
    private final String mName;
    
    public Queue(String name){
        mName = name;
        mQueue = new LinkedBlockingDeque<>(MAX_SIZE);
        consumersIds = new HashSet<>();
    }
    
    //Adiere mensaje de la cola
    public void putMsg(String msg) throws InterruptedException{
        mQueue.put(msg);
    }
    
    //Extrea mensaje de la cola
    public String pollMsg(){
        return mQueue.poll();
    }
    
    public Set<Integer> getConsumersIds(){
        return consumersIds;
    }
    
    public void subsConsumer(Integer id){
        consumersIds.add(id);
    }
    
    public void delConsumer(Integer id){
        consumersIds.remove(id);
    }
    
    public String getName(){
        return mName;
    }
}
