package middleware.server;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase principal que gestiona los comandos y mensajes
 * recibidos por los clientes, y el envio a sus destinatarios
 * @author Victor
 */
public class Middleware{
    public static final int NUM_PORT = 5555;
    private Map<String,Queue> mQueues;
    private Map<String,Topic> mTopics;
    private Connection mConn;
    private Map<Integer,Client> mClients;
    private Integer count;
    
    public Middleware(){
        mQueues = new HashMap<>();
        mTopics = new HashMap<>();
        mClients = new HashMap<>();
        mConn = new Connection(NUM_PORT);
        count = 0;
    }

    public void run() {
        try {
            mConn.create();
            mConn.listen(connListener);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    public Map<String,Queue> getQueues(){
        return mQueues;
    }
    
    public Map<Integer,Client> getClients(){
        return mClients;
    }
    
    /**
     * Listener que gestiona nueva conexion de cliente
     * Registra el nuevo cliente a la lista
     */
    private final Connection.NewConnListener connListener = (Socket socket) -> {
        try{
            Client client = new Client(++count, socket);
            mClients.put(client.getId(), client);
            client.listen(this.clientListener);
            System.out.println("Nuevo cliente"+client.getId());
        }catch(IOException ex){
            System.err.println(ex.getMessage());
        }
    };
    
    /**
     * Listener que gestiona los comandos que envian los clientes
     */
    private final Client.ClientListener clientListener = (Integer idClient,String msg) -> {
        ejecutarComando(idClient,msg);
    };
    
    /**
     * Interpreta el comando recibido y lo ejecuta
     * @param idClient
     * @param msg El comando como una trama
     */
    private void ejecutarComando(Integer idClient,String msg){
        Command cmmd = Command.parseCommand(msg);
        switch(cmmd.getCmd()){
            case Command.CMD_DEC_TOPIC:
                declareTopic(idClient, cmmd.getTopicName());
                break;
            case Command.CMD_DEC_QUEUE:
                declareQueue(idClient,cmmd.getQueueName());
                break;
            case Command.CMD_SUBS_QUEUE:
                subscribeQueue(idClient,cmmd.getTopicName(),cmmd.getQueueName());
                break;
            case Command.CMD_PROD_SEND:
                producerSend(idClient,cmmd.getTopicName(),cmmd.getQueueName(),cmmd.getMessage());
                break;
            case Command.CMD_CONSUME:
                consume(idClient,cmmd.getQueueName());
            case Command.CMD_CONS_ACK:
                consAck(idClient,cmmd.getQueueName());
                break;
            default:
                break;
        }
    }
    
    /**
     * Declaracion de un Topico que el cliente usara
     * Creara uno nuevo si no exite
     * @param idClient
     * @param topicName 
     */
    private void declareTopic(Integer idClient,String topicName){
        Topic topic = mTopics.get(topicName);
        if(topic==null){
            topic = new Topic(this,topicName);
            mTopics.put(topicName, topic);
            System.out.println("Cliente"+idClient+" declara nuevo Topic: "+topicName);
        }
        System.out.println("Cliente"+idClient+" usa Topic: "+topicName);
    }

    /**
     * Declaracion de Queue que el usuario usara
     * Si no existe, se creara uno
     * @param idClient
     * @param queueName 
     */
    private void declareQueue(Integer idClient, String queueName) {
        Queue queue = mQueues.get(queueName);
        if(queue==null){
            queue = new Queue(this,queueName);
            mQueues.put(queueName, queue);
            System.out.println("Cliente"+idClient+" declara nuevo Queue: "+queueName);
        }
        System.out.println("Cliente"+idClient+" usa Queue: "+queueName);
    }

    /**
     * El cliente suscribe su queue al topico especifico
     * @param idClient
     * @param topicName
     * @param queueName 
     */
    private void subscribeQueue(Integer idClient, String topicName, String queueName) {
        Topic topic = mTopics.get(topicName);
        if(topic==null){
            System.err.println("No se declaro el topic: "+topicName+", declare antes de suscribir un queue");
            return;
        }
        Queue queue = mQueues.get(queueName);
        if(queue==null){
            System.err.println("No se declaro el queue: "+queueName+", declare antes de suscribir a topico");
            return;
        }
        topic.subscribe(queueName);
        System.out.println("Cliente"+idClient+" suscribe el queue: "+queueName+" al topic: "+topicName);
    }

    /**
     * Cliente producer envia mensaje al topico o queue especificado
     * @param idClient
     * @param topicName
     * @param queueName
     * @param body 
     */
    private void producerSend(Integer idClient, String topicName, String queueName, Message message) {
        //System.out.println("Cliente"+idClient+" envia mensaje a "+topicName+":"+queueName);
        if(topicName!=null){
            sendToTopic(idClient, topicName, message);
        }else if(queueName!=null){
            sendToQueue(idClient, queueName, message);
        }
    }
    
    /**
     * Envio de mensaje a topico para que este lo difunda a sus queues
     * @param idCliente
     * @param topicName
     * @param body 
     */
    private void sendToTopic(Integer idCliente,String topicName,Message message){
        Topic topic = mTopics.get(topicName);
        if(topic==null){
            System.err.println("No se declaro el topic: "+topicName+", declare antes de enviar mensaje");
            return;
        }
        topic.send(message);
        System.out.println("Cliente"+idCliente+" envia mensaje a topico: "+topicName);
    }
    
    /**
     * Envio de mensaje a queue especifica
     * @param idCliente
     * @param queueName
     * @param body 
     */
    private void sendToQueue(Integer idCliente,String queueName,Message message){
        Queue queue = mQueues.get(queueName);
        if(queue==null){
            System.err.println("No se declaro el queue: "+queueName+", declare antes de enviar mensaje");
            return;
        }
        try {
            queue.putMsg(message);
            System.out.println("Cliente"+idCliente+" envia mensaje a Queue:"+queueName);
        } catch (InterruptedException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    /**
     * Cliente consumer pide que se envie mensajes del queue
     * @param idClient
     * @param queueName 
     */
    private void consume(Integer idClient, String queueName) {
        Queue queue = mQueues.get(queueName);
        if(queue==null){
            System.err.println("No se declaro el queue: "+queueName+", declare antes de enviar mensaje");
            return;
        }
        queue.consume(idClient);
        System.out.println("Cliente"+idClient+" consume de queue: "+queueName);
    }

    /**
     * Cliente consumer notifica que esta disponible tras recibir un mensaje
     * @param idClient
     * @param queueName 
     */
    private void consAck(Integer idClient, String queueName) {
        Queue queue = mQueues.get(queueName);
        if(queue==null){
            System.err.println("No se declaro el queue: "+queueName+", declare antes de enviar mensaje");
            return;
        }
        queue.consAck(idClient);
    }

    
    
}
