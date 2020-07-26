package middleware.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *  Conexion mediante ServerSocket
 * @author Victor
 */
public class ConnSocketServer implements Runnable{
    private final int mPort;
    private ServerSocket mServerSocket;
    private boolean running = true;
    private NewConnListener mCallBack;
    
    public ConnSocketServer(int port){
        mPort = port;
    }
      
    public void create() throws IOException{
        mServerSocket = new ServerSocket(mPort);
        System.out.println("ServerSocket en puerto: "+mPort);
    }
    
    public void listen(NewConnListener callBack){
        mCallBack = callBack;
        Thread thread = new Thread(this);
        thread.start();
        System.out.println("Inicia el servicio del ServerSocket");
    }
        
    public void stopListen(){
        running = false;
    }

    @Override
    public void run() {
        while(running){
            try {
                Socket newCon = mServerSocket.accept();
                mCallBack.onNewConnection(newCon);
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }
    
    public interface NewConnListener{
        void onNewConnection(Socket socket);
    }
    
}
