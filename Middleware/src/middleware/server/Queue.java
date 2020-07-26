package middleware.server;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Clase que almacena los mensajes y envia a su consumidor si esta disponible
 * @author Victor
 */
public class Queue {
    public static final int MAX_SIZE = 100; 
    private BlockingQueue<Message> mQueue; //Tipo String por mientras
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
    
    /**
     * Cuando el cliente notifica que recibio el mensaje y termino su tarea
     * @param idClient 
     */
    public void consAck(Integer idClient){
        Client client = mMiddleware.getClients().get(idClient);
        client.setAvailable(true);
    }
    
    /**
     * Cuando el cliente indica que consumira de la cola
     * Se agrega el id del cliente al conjunto de consumidores del queue
     * @param idClient 
     */
    public void consume(Integer idClient){
        consumersIds.add(idClient);
        Client client = mMiddleware.getClients().get(idClient);
        client.setAvailable(true);  //habilita al consumidor
        client.setQueueName(mName);
        
        if(consThr==null || !consThr.isAlive()){
            consThr = new Thread(consRun);
            runningCons = true;
            consThr.start();
        }
    }
    
    /**
     * Hilo que maneja el consumo en un loop
     * Distribuye los mensajes a los consumers disponibles
     */
    private Runnable consRun = () -> {
        while(runningCons){
            Message msg = mQueue.peek(); //obtiene el mensaje
            if(msg!=null){
                boolean success = false;
                Set<Integer> consIdArr = new HashSet<>(consumersIds);
                for(Integer id : consIdArr){
                    Client client = mMiddleware.getClients().get(id);
                        if(client.isAvailable()){   //Si el consumer esta disponible, recibira el mensaje
                            client.setAvailable(false);
                            client.sendMessage(msg);
                            success = true;
                            break;
                        }
                }
                if(success)
                    mQueue.poll();  //Retira el mensaje que se envio exitosamente
            }
        }
    };
    
    /**
     * Cuando un consumer o topic agrega un mensaje a la cola
     * @param msg
     * @throws InterruptedException 
     */
    public void putMsg(Message msg) throws InterruptedException{
        mQueue.put(msg);
    }
    
    public Set<Integer> getConsumersIds(){
        return consumersIds;
    }
    
    public void subsConsumer(Integer id){
        consumersIds.add(id);
    }
    
    public void delConsumer(Integer id){
        consumersIds.remove(id);
        if(consumersIds.isEmpty())
            runningCons = false;
        System.out.println("Cliente"+id+" eliminado de Queue: "+mName);
    }
    
    public String getName(){
        return mName;
    }
}
