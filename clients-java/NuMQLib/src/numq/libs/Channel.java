package numq.libs;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

/**
 * Clase para manejar el envio y recepcion de comandos
 * del cliente al middleware y viceversa
 * @author Victor
 */
public class Channel {
    private final DataInputStream in;
    private final DataOutputStream out;
    private String topicName;
    private String queueName;
    private Consume consumeThread;
    
    public Channel(Socket mSocket) throws IOException{
        in = new DataInputStream(mSocket.getInputStream());
        out = new DataOutputStream(mSocket.getOutputStream());
    }
    
    /**
     * Envia comando al middleware como texto plano
     * @param msg
     * @throws IOException 
     */
    private void sendCommand(Command msg) throws IOException{
        out.writeUTF(msg.toString());
        //System.out.println("Enviando comando: "+msg);
    }
    
    /**
     * Declara un Topic en el middleware que se usara
     * El middleware lo creara si no existe
     * @param topicName
     * @throws IOException 
     */
    public void declareTopic(String topicName) throws IOException{
        this.topicName = topicName;
        Command msg = Command.createDecTopicMsg(topicName);
        sendCommand(msg);
    }
    
    /**
     * Declara un Queue en el middleware
     * El middlware lo creara si no existe
     * @return El nombre queue autogenerado
     * @throws IOException 
     */
    public String declareQueue() throws IOException{
        String genQueue = hash();
        declareQueue(genQueue);
        return genQueue;
    }
    
    /**
     * Declara un Queue en el middleware
     * El middlware lo creara si no existe
     * @param queueName
     * @throws IOException 
     */
    public void declareQueue(String queueName) throws IOException{
        this.queueName = queueName;
        Command msg = Command.createDecQueueMsg(queueName);
        sendCommand(msg);
    }
    
    /**
     * Suscribe un queue a un topic en el middleware
     * @param topicName
     * @param queueName
     * @throws IOException 
     */
    public void subscribeQueue(String topicName, String queueName) throws IOException{
        Command msg = Command.createBindQueueMsg(topicName, queueName);
        sendCommand(msg);
    }
    
    /**
     * Producer envia mensaje a un topic o queue
     * @param topicName
     * @param queueName
     * @param replyQueue
     * @param body
     * @throws Exception 
     */
    public void producerSend(String topicName,String queueName,String replyQueue,String body) throws Exception{
        if(body==null || body.isEmpty())
            throw new Exception("Body no debe ser vacio o nulo");
        
        Command msg;
        String mTopic = "",mQueue = "", mReplyQ = "";
        
        if(topicName==null || topicName.isEmpty()){
            if(queueName==null || queueName.isEmpty())
                throw new Exception("Debe especificar el queue");
            //msg = Command.createSendMsg("", queueName, body);
            mQueue = queueName;
        }else{
            //msg = Command.createSendMsg(topicName, "", body);
            mTopic = topicName;
        }
        if(replyQueue != null && !replyQueue.isEmpty())
            mReplyQ = replyQueue;
        msg = Command.createSendMsg(mTopic, mQueue, mReplyQ, body);
        sendCommand(msg);
        //System.out.println("Enviado a "+topicName+":"+queueName+", Mensaje: "+body);
    }

    /**
     * Consumidor indica al middleware que consumira de la queue especificada
     * @param queueName
     * @param autoAck Indica si se notificara automaticamente que esta disponible despues de recibir
     * @param callBack 
     */
    public void consume(String queueName,boolean autoAck,CallBack callBack){
        consumeThread = new Consume(queueName, autoAck, callBack);
        consumeThread.start();
    }
    
    public void consAck(String queueName) throws IOException{
        sendConsAck(queueName);
    }
    
    public void cancelConsume(){
        consumeThread.stopConsume();
    }
    
    /**
     * Notifica al middleware que el consumidor completo su trabajo y esta disponible
     * @param queueName
     * @throws IOException 
     */
    private void sendConsAck(String queueName) throws IOException{
        Command msg = Command.createConsAckMsg(queueName);
        sendCommand(msg);
    }
    
    /**
     * Envia coman
     * @param queueName
     * @throws IOException 
     */
    private void sendConsume(String queueName) throws IOException{
        Command msg = Command.createConsumeMsg(queueName);
        sendCommand(msg);
    }
    
    public String getTopicName() {
        return topicName;
    }

    public String getQueueName() {
        return queueName;
    }
    
    
    
    public static String hash() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
          .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
          .limit(targetStringLength)
          .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
          .toString();

        //System.out.println(generatedString);
        return generatedString;
    }
    
    public class Consume extends Thread{
        private final CallBack mCallBack;
        private boolean running = true;
        private final boolean autoAck;
        private final String queueName;
        
        public Consume(String queueName,boolean autoAck,CallBack callBack){
            mCallBack = callBack;
            this.autoAck = autoAck;
            this.queueName = queueName;
        }
        
        @Override
        public void run() {
            try {
                sendConsume(queueName); //Avisa al servidor que inicia consumo
                while(running){
                    String msg = in.readUTF();
                    mCallBack.onResponse(Message.parseMessage(msg));
                    
                    if(autoAck){
                        sendConsAck(queueName); //Confirma que acabo su tarea y esta disponible
                    }      
                }
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
        
        public void stopConsume(){
            running = false;
        }
        
    }
    
    public interface CallBack{
        void onResponse(Message message);
    }
}
