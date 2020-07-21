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
    private String exchName;
    private String queueName;
    
    public Channel(Socket mSocket) throws IOException{
        in = new DataInputStream(mSocket.getInputStream());
        out = new DataOutputStream(mSocket.getOutputStream());
    }
    
    private void sendMsg(Message msg) throws IOException{
        out.writeUTF(msg.toString());
    }
    
    public void declareExchange(String exchName) throws IOException{
        this.exchName = exchName;
        Message msg = Message.createDecExchMsg(exchName);
        sendMsg(msg);
    }
    
    public void declareQueue() throws IOException{
        String genQueue = hash();
        declareExchange(genQueue);
    }
    
    public void declareQueue(String queueName) throws IOException{
        this.queueName = queueName;
        Message msg = Message.createDecQueueMsg(queueName);
        sendMsg(msg);
    }
    
    public void bindQueue(String exchName, String queueName) throws IOException{
        Message msg = Message.createBindQueueMsg(exchName, queueName);
        sendMsg(msg);
    }
    
    public void send(String exchName,String queueName,String body) throws Exception{
        if(body==null || body.isEmpty())
            throw new Exception("Body no debe ser vacio o nulo");
        
        Message msg;
        
        if(exchName==null || exchName.isEmpty()){
            if(queueName==null || queueName.isEmpty())
                throw new Exception("Debe especificar el queue");
            msg = Message.createSendMsg("", queueName, body);
        }else{
            msg = Message.createSendMsg(exchName, "", body);
        }
        sendMsg(msg);
        System.out.println("Enviado a "+exchName+":"+queueName);
        System.out.println("\tMensaje: "+body);
    }

    public void consume(String queueName,boolean autoAck,CallBack callBack){
        Consume mConsume = new Consume(queueName, autoAck, callBack);
        mConsume.start();
    }
    
    public void sendConsAck(String queueName) throws IOException{
        Message msg = Message.createConsAckMsg(queueName);
        sendMsg(msg);
    }
    
    public String getExchName() {
        return exchName;
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
    
    /*public static void main(String[] args){
        hash();
        hash();
        hash();
        hash();
        hash();
    }*/
    
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
