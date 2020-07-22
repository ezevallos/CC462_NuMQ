package middleware.server;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Almacena los mensajes y envia a su consumidor si esta disponible
 * @author Victor
 */
public class Queue {
    public static final int MAX_SIZE = 100; 
    private BlockingQueue<String> mQueue; //Tipo String por mientras
    private Set<Integer> consumersIds;
    private final String mName;
    private Middleware mMiddleware;
    private boolean runningCons = true;
    private Thread consThr;
    
    public Queue(Middleware middleware,String name){
        mMiddleware = middleware;
        mName = name;
        mQueue = new LinkedBlockingDeque<>(MAX_SIZE);
        consumersIds = new HashSet<>();
    }
    
    public void consAck(Integer idClient){
        Client client = mMiddleware.getClients().get(idClient);
        client.setAvailable(true);
    }
    
    public void consume(Integer idClient){
        consumersIds.add(idClient);
        Client client = mMiddleware.getClients().get(idClient);
        client.setAvailable(true);  //habilita al consumidor
        
        if(consThr==null){
            consThr = new Thread(consRun);
            consThr.start();
        }
    }
    
    private Runnable consRun = () -> {
        while(runningCons){
            String msg = mQueue.peek(); //obtiene el mensaje
            if(msg!=null){
                boolean success = false;
                Integer[] consIdArr = consumersIds.toArray(new Integer[consumersIds.size()]);
                for(Integer id : consIdArr){
                    Client client = mMiddleware.getClients().get(id);
                    if(client.isAvailable()){   //Si el consumer esta disponible, recibira el mensaje
                        try{
                            client.sendMessage(msg);
                            success = true;
                            client.setAvailable(false);
                            break;
                        }catch(IOException ex){
                            //Si falla por desconexion se elimina
                            consumersIds.remove(id);
                        }
                    }
                }
                if(success)
                    mQueue.poll();  //Retira el mensaje que se envio exitosamente
            }
        }
    };
    
    //Adiere mensaje de la cola
    public void putMsg(String msg) throws InterruptedException{
        mQueue.put(msg);
    }
    
    //Extrea mensaje de la cola
    /*public String pollMsg(){
        return mQueue.poll();
    }*/
    
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
