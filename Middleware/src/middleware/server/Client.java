package middleware.server;

import java.io.IOException;
/**
 * Cliente conectado via Socket TCP
 * @author Victor
 */
public abstract class Client{
    private Integer mId;
    private MessageListener mListener;
    private boolean available;  //Para consumo
    public static final int SOCKET = 1;
    public static final int WS = 2;
    private final int mTipo;
    private String queueName;
    
    public Client(Integer id,int tipo){
        mId = id;
        mTipo = tipo;
        available = false;
    }

    public int getmTipo() {
        return mTipo;
    }
    
    public abstract void sendMessage(Message msg);

    public abstract void listen(MessageListener listener);
    
    public final Integer getId(){
        return mId;
    }
    
    public final void setAvailable(boolean available){
        this.available = available;
    }
    
    public final boolean isAvailable(){
        return available;
    }

    public MessageListener getmListener() {
        return mListener;
    }

    public void setmListener(MessageListener mListener) {
        this.mListener = mListener;
    }
    
    public interface MessageListener{
        void onMessage(Integer idClient,String msg);
        void remove(Integer idClient);
    }

    public final String getQueueName() {
        return queueName;
    }

    public final void setQueueName(String queueName) {
        this.queueName = queueName;
    }
    
    
}
