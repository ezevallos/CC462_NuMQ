package middleware.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cliente conectado via Socket TCP
 * @author Victor
 */
public class ClientSocket extends Client implements Runnable{
    private final Socket mSocket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private boolean running = true;
    
    public ClientSocket(Integer id, Socket socket) throws IOException{
        super(id,Client.SOCKET);
        mSocket = socket;
        in = new DataInputStream(mSocket.getInputStream());
        out = new DataOutputStream(mSocket.getOutputStream());
    }
    
    @Override
    public void sendMessage(Message msg){
        try {
            out.writeUTF(msg.toString());
        } catch (IOException ex) {
            System.err.println("Cliente"+getId().toString()+" desconectado, razon: "+ex.getMessage());
            getmListener().remove(getId());
        }
    }

    @Override
    public void listen(MessageListener listener){
        setmListener(listener);
        Thread thread = new Thread(this);
        thread.start();
        System.out.println("Escuchando a Cliente"+getId().toString());
    }
    
    @Override
    public void run() {
        try {
            while(running){
                String msg;

                msg = in.readUTF();
                //System.out.println("comando: "+msg);
                getmListener().onMessage(getId(),msg);
            }
        } catch (IOException ex) {
            System.err.println("Cliente"+getId().toString()+" desconectado, razon: "+ex.getMessage());
            getmListener().remove(getId());
        } finally{
            stopListen();
        }
    }
    
    private void stopListen(){
        running = false;
    }
    
}
