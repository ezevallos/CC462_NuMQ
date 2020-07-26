package numq.libs;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Victor
 */
public class Connection{
    
    private final String mHostAddr;
    private final int mPortNum;
    private Socket mSocket;
    private Channel mChannel;
    
    public Connection(String hostAddr,int portNum) throws IOException{
        mHostAddr = hostAddr;
        mPortNum = portNum;
        connect();
    }
    
    private void connect() throws IOException{
        mSocket = new Socket(mHostAddr, mPortNum);
        mChannel = new Channel(mSocket);
        System.out.println("Conectado a: "+mHostAddr+":"+mPortNum);
    }
    
    public Channel getChannel(){
        return mChannel;
    }
    
    public void closeConn(){
        try {
            mSocket.close();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
