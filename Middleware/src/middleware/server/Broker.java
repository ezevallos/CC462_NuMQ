package middleware.server;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Victor
 */
public class Broker implements Runnable{
    public static final int NUM_PORT = 5555;
    private Map<String,Queue> mQueues;
    private Connection mConn;
    private Map<Integer,Client> mClients;
    private Integer count;
    
    public Broker(){
        mQueues = new HashMap<>();
        mClients = new HashMap<>();
        mConn = new Connection(NUM_PORT);
        count = 0;
    }

    @Override
    public void run() {
        try {
            mConn.create();
            mConn.listen(connCallBack);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    public Map<Integer,Client> getClients(){
        return mClients;
    }
    
    private final Connection.NewConnCallBack connCallBack = (Socket socket) -> {
        try{
            Client client = new Client(++count, socket);
            mClients.put(client.getId(), client);
            client.listen(this.clientListener);
        }catch(IOException ex){
            System.err.println(ex.getMessage());
        }
    };
    
    private final Client.ClientListener clientListener = (Integer idClient,String msg) -> {
        leerMsg(msg);
    };
    
    // "nombreCola"
    private void leerMsg(String msg){
        Command cmmd = Command.parseCommand(msg);
        //TODO: ejecutar comando
    }
}
