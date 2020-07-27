package middleware.server;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import org.java_websocket.WebSocket;

/**
 * Clase principal que gestiona los comandos y mensajes
 * recibidos por los clientes, y el envio a sus destinatarios
 * @author Victor
 */
public class Middleware{
    public static final int SOCKET_NUM_PORT = 5555;
    public static final int WS_NUM_PORT = 4444;
    private Map<String,Queue> mQueues;
    private Map<String,Topic> mTopics;
    private ConnSocketServer mConn;
    private ConnWebSocketServer mConnWS;
    private Map<Integer,Client> mClients;
    private Integer count;
    
    public Middleware(){
        mQueues = new HashMap<>();
        mTopics = new HashMap<>();
        mClients = new HashMap<>();
        mConn = new ConnSocketServer(SOCKET_NUM_PORT);
        mConnWS = new ConnWebSocketServer(WS_NUM_PORT, wsListener);
        count = 0;
    }

    public void run() {
        try {
            mConn.create();
            mConn.listen(connListener);
            mConnWS.start();
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
    private final ConnSocketServer.NewConnListener connListener = (Socket socket) -> {
        try{
            Client client = new ClientSocket(++count, socket);
            mClients.put(client.getId(), client);
            client.listen(this.clientListener);
            System.out.println("Nuevo cliente"+client.getId()+" desde "
                    +socket.getRemoteSocketAddress().toString());
        }catch(IOException ex){
            System.err.println(ex.getMessage());
        }
    };
    
    /**
     * Listener para el websocketServer
     */
    private final ConnWebSocketServer.WSListener wsListener = new ConnWebSocketServer.WSListener() {
        @Override
        public void onNewConnWS(WebSocket ws) {
            //System.out.println("nuevo ws:"+ws.toString());
            Client client = new ClienteWS(++count, ws);
            mClients.put(client.getId(), client);
            System.out.println("Nuevo cliente"+client.getId()+" desde "
                    +ws.getRemoteSocketAddress().getAddress().toString()
            +":"+ws.getRemoteSocketAddress().getPort());
        }

        @Override
        public void onMessage(WebSocket ws, String msg) {
            //System.out.println("msg:"+msg+"; ws:"+ws.toString());
            Integer idClient = buscarClient(ws);
            //int total = mClients.size();
            //System.out.println("Id de cliente: "+idClient+", Clientes totales: "+total);
            if(idClient!=null)
                ejecutarComando(idClient, msg);
        }

        @Override
        public void removeWS(WebSocket ws) {
            Integer idClient = buscarClient(ws);
            if(idClient!=null){
                Client client = mClients.get(idClient);
                Queue queue = mQueues.get(client.getQueueName());
                if(queue!=null)
                    queue.delConsumer(idClient);
                mClients.remove(idClient);
                System.out.println("Cliente"+idClient+" eliminado por desconexión");
            }
        }
    };
    
    /**
     * Listener que gestiona los comandos que envian los clientes de Socket
     */
    private final Client.MessageListener clientListener = new Client.MessageListener() {
        @Override
        public void onMessage(Integer idClient, String msg) {
            ejecutarComando(idClient, msg);
        }

        @Override
        public void remove(Integer idClient) {
            Client client = mClients.get(idClient);
            Queue queue = mQueues.get(client.getQueueName());
            if(queue!=null)
                queue.delConsumer(idClient);
            mClients.remove(idClient);
            System.out.println("Cliente"+idClient+" eliminado por desconexión");
        }
    };
    
    /**
     * Interpreta el comando recibido y lo ejecuta
     * @param idClient
     * @param msg El comando como una trama
     */
    private void ejecutarComando(Integer idClient,String msg){
        Command cmmd = Command.parseCommand(msg);
        if(cmmd!=null){
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

    private Integer buscarClient(WebSocket ws){
        Integer idClient = null;
        Map<Integer,Client> clients = new HashMap<>(mClients);
        for(Integer id : clients.keySet()){
            Client client = clients.get(id);
            if(client.getmTipo()==Client.WS){
                if(((ClienteWS)client).getWS()==ws){
                    idClient = id;
                    break;
                }    
            }
        }
        return idClient;
    }
    
}
