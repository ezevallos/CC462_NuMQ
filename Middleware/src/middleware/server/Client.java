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
    
    public Client(Integer id, Socket socket) throws IOException{
        mId = id;
        mSocket = socket;
        in = new DataInputStream(mSocket.getInputStream());
        out = new DataOutputStream(mSocket.getOutputStream());
        //in.read
    }
    
    public void sendMessage(String msg) throws IOException{
        out.writeUTF(msg);
    }

    public void listen(ClientListener listener){
        mListener = listener;
        Thread thread = new Thread(this);
        thread.start();
    }
    
    @Override
    public void run() {
        while(running){
            String msg;
            try {
                msg = in.readUTF();
                mListener.messageRcvd(mId,msg);
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }finally{
                stopListen();
            }
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
    
}
