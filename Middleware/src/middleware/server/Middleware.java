package middleware.server;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 *
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
            mConn.listen(connCallBack);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    public Map<Integer,Client> getClients(){
        return mClients;
    }
    
    private final Connection.NewConnCallBack connCallBack = (Socket socket) -> {
        try{
            Client client = new Client(++count, socket);
            mClients.put(client.getId(), client);
            client.listen(this.clientListener);
            System.out.println("Nuevo cliente"+client.getId());
        }catch(IOException ex){
            System.err.println(ex.getMessage());
        }
    };
    
    private final Client.ClientListener clientListener = (Integer idClient,String msg) -> {
        ejecutarComando(idClient,msg);
    };
    
    // Interpreta el comando recibido
    private void ejecutarComando(Integer idClient,String msg){
        Command cmmd = Command.parseCommand(msg);
        switch(cmmd.getCmd()){
            case Command.CMD_DEC_TOPIC:
                declareTopic(idClient, cmmd.getTopicName());
                break;
            case Command.CMD_DEC_QUEUE:
                declareQueue(idClient,cmmd.getQueueName());
                break;
            case Command.CMD_BIND_QUEUE:
                bindQueue(idClient,cmmd.getTopicName(),cmmd.getQueueName());
                break;
            case Command.CMD_SEND:
                send(idClient,cmmd.getTopicName(),cmmd.getQueueName(),cmmd.getBody());
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
    
    private void declareTopic(Integer idClient,String topicName){
        Topic topic = mTopics.get(topicName);
        if(topic==null){
            topic = new Topic();
            mTopics.put(topicName, topic);
            System.out.println("Cliente"+idClient+" declara nuevo Topic: "+topicName);
        }
        System.out.println("Cliente"+idClient+" usa Topic: "+topicName);
    }

    private void declareQueue(Integer idClient, String queueName) {
        Queue queue = mQueues.get(queueName);
        if(queue==null){
            queue = new Queue(this,queueName);
            mQueues.put(queueName, queue);
            System.out.println("Cliente"+idClient+" declara nuevo Queue: "+queueName);
        }
        System.out.println("Cliente"+idClient+" usa Queue: "+queueName);
    }

    private void bindQueue(Integer idClient, String topicName, String queueName) {
        //TODO: suscribir queue al topico especificado
    }

    private void send(Integer idClient, String topicName, String queueName, String body) {
        //System.out.println("Cliente"+idClient+" envia mensaje a "+topicName+":"+queueName);
        if(topicName!=null){
            sendToTopic(idClient, topicName, body);
        }else if(queueName!=null){
            sendToQueue(idClient, queueName, body);
        }
    }
    
    private void sendToTopic(Integer idCliente,String topicName,String body){
        //TODO: enviar a topic
        System.out.println("Cliente"+idCliente+" envia mensaje a topico: "+topicName);
    }
    
    private void sendToQueue(Integer idCliente,String queueName,String body){
        Queue queue = mQueues.get(queueName);
        if(queue==null){
            System.err.println("No se declaro el queue: "+queueName+", declare antes de enviar mensaje");
            return;
        }
        try {
            queue.putMsg(body);
            System.out.println("Cliente"+idCliente+" envia mensaje a Queue:"+queueName);
        } catch (InterruptedException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    private void consume(Integer idClient, String queueName) {
        Queue queue = mQueues.get(queueName);
        if(queue==null){
            System.err.println("No se declaro el queue: "+queueName+", declare antes de enviar mensaje");
            return;
        }
        queue.consume(idClient);
        System.out.println("Cliente"+idClient+" consume de queue: "+queueName);
    }

    private void consAck(Integer idClient, String queueName) {
        Queue queue = mQueues.get(queueName);
        if(queue==null){
            System.err.println("No se declaro el queue: "+queueName+", declare antes de enviar mensaje");
            return;
        }
        queue.consAck(idClient);
    }

    
    
}
