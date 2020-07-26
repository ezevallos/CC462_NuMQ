/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package middleware.server;

import org.java_websocket.WebSocket;

/**
 *
 * @author Victor
 */
public class ClienteWS extends Client{
    private final WebSocket mWS;

    public ClienteWS(Integer id,WebSocket ws) {
        super(id,Client.WS);
        mWS = ws;
    }

    @Override
    public void sendMessage(Message msg){
        mWS.send(msg.toString());
    }

    @Override
    public void listen(MessageListener listener) {
        //WebSocketServer se encarga
    }

    public WebSocket getWS() {
        return mWS;
    }
    
}
