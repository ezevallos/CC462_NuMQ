package middleware.server;

import java.net.InetSocketAddress;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

/**
 *
 * @author Victor
 */
public class ConnWebSocketServer extends WebSocketServer{
    private final WSListener mListener;
    
    public ConnWebSocketServer(int numPort, WSListener listener){
        super(new InetSocketAddress(numPort));
        mListener = listener;
    }

    @Override
    public void onOpen(WebSocket ws, ClientHandshake ch) {
        mListener.onNewConnWS(ws);
    }

    @Override
    public void onClose(WebSocket ws, int i, String string, boolean bln) {
        mListener.removeWS(ws);
    }

    @Override
    public void onMessage(WebSocket ws, String string) {
        mListener.onMessage(ws, string);
    }

    @Override
    public void onError(WebSocket ws, Exception excptn) {
        if(ws!=null){
            mListener.removeWS(ws);
        }
    }

    @Override
    public void onStart() {
        System.out.println("Inicia el servicio del ServerWebSocket");
    }
    
    public interface WSListener{
        void onNewConnWS(WebSocket ws);
        void onMessage(WebSocket ws, String msg);
        void removeWS(WebSocket ws);
    }
    
}
