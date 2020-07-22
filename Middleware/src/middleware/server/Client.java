package middleware.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Victor
 */
public class Client implements Runnable{
    private final Integer mId;
    private final Socket mSocket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private boolean running = true;
    private ClientListener mListener;
    private boolean available;  //Para consumo
    
    public Client(Integer id, Socket socket) throws IOException{
        mId = id;
        mSocket = socket;
        in = new DataInputStream(mSocket.getInputStream());
        out = new DataOutputStream(mSocket.getOutputStream());
        //in.read
        available = false;
    }
    
    public void sendMessage(String msg) throws IOException{
        out.writeUTF(msg);
    }

    public void listen(ClientListener listener){
        mListener = listener;
        Thread thread = new Thread(this);
        thread.start();
        System.out.println("Escuchando a Cliente"+mId);
    }
    
    @Override
    public void run() {
        while(running){
            String msg;
            try {
                msg = in.readUTF();
                //System.out.println("comando: "+msg);
                mListener.messageRcvd(mId,msg);
            } catch (IOException ex) {
                System.err.println("Cliente"+mId+" desconectado, razon: "+ex.getMessage());
                stopListen();
            }/*finally{
                stopListen();
            }*/
        }
    }
    
    public void stopListen(){
        running = false;
    }
    
    public Integer getId(){
        return mId;
    }
    
    public interface ClientListener{
        void messageRcvd(Integer idClient,String msg);
    }
    
    public void setAvailable(boolean available){
        this.available = available;
    }
    
    public boolean isAvailable(){
        return available;
    }
}
