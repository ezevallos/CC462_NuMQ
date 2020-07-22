package numq.libs;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

/**
 *
 * @author Victor
 */
public class Channel {
    private final DataInputStream in;
    private final DataOutputStream out;
    private String topicName;
    private String queueName;
    
    public Channel(Socket mSocket) throws IOException{
        in = new DataInputStream(mSocket.getInputStream());
        out = new DataOutputStream(mSocket.getOutputStream());
    }
    
    private void sendMsg(Command msg) throws IOException{
        out.writeUTF(msg.toString());
    }
    
    public void declareTopic(String topicName) throws IOException{
        this.topicName = topicName;
        Command msg = Command.createDecTopicMsg(topicName);
        sendMsg(msg);
    }
    
    public void declareQueue() throws IOException{
        String genQueue = hash();
        declareTopic(genQueue);
    }
    
    public void declareQueue(String queueName) throws IOException{
        this.queueName = queueName;
        Command msg = Command.createDecQueueMsg(queueName);
        sendMsg(msg);
    }
    
    public void bindQueue(String topicName, String queueName) throws IOException{
        Command msg = Command.createBindQueueMsg(topicName, queueName);
        sendMsg(msg);
    }
    
    public void send(String topicName,String queueName,String body) throws Exception{
        if(body==null || body.isEmpty())
            throw new Exception("Body no debe ser vacio o nulo");
        
        Command msg;
        
        if(topicName==null || topicName.isEmpty()){
            if(queueName==null || queueName.isEmpty())
                throw new Exception("Debe especificar el queue");
            msg = Command.createSendMsg("", queueName, body);
        }else{
            msg = Command.createSendMsg(topicName, "", body);
        }
        sendMsg(msg);
        System.out.println("Enviado a "+topicName+":"+queueName);
        System.out.println("\tMensaje: "+body);
    }

    public void consume(String queueName,boolean autoAck,CallBack callBack){
        Consume mConsume = new Consume(queueName, autoAck, callBack);
        mConsume.start();
    }
    
    public void sendConsAck(String queueName) throws IOException{
        Command msg = Command.createConsAckMsg(queueName);
        sendMsg(msg);
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
                sendConsAck(queueName); //Avisa al servidor que inicia consumo y pide que se envie
                System.out.println("Consumiendo desde: "+queueName);
                while(running){
                    String msg = in.readUTF();
                    mCallBack.onResponse(msg);
                    
                    if(autoAck){
                        sendConsAck(queueName);
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
        void onResponse(String body);
    }
}
