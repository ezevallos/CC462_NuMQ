package middleware.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Victor
 */
public class Connection implements Runnable{
    private final int mPort;
    private ServerSocket mServerSocket;
    private boolean running = true;
    private NewConnCallBack mCallBack;
    
    public Connection(int port){
        mPort = port;
    }
      
    public void create() throws IOException{
        mServerSocket = new ServerSocket(mPort);
        System.out.println("Servidor creado en puerto: "+mPort);
    }
    
    public void listen(NewConnCallBack callBack){
        mCallBack = callBack;
        Thread thread = new Thread(this);
        thread.start();
        System.out.println("Servidor escuchando...");
    }
        
    public void stopListen(){
        running = false;
    }

    @Override
    public void run() {
        while(running){
            try {
                Socket newCon = mServerSocket.accept();
                mCallBack.newConnection(newCon);
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }
    
    public interface NewConnCallBack{
        void newConnection(Socket socket);
    }
    
}
