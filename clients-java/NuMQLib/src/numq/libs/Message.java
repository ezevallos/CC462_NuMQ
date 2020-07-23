/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package numq.libs;

/**
 * Message: "reply_queue,body"
 * @author Victor
 */
public class Message {
    private String replyQueue;
    private String body;

    private Message(){}
    
    public static Message parseMessage(String text){
        Message msg = new Message();
        String[] tokens = text.split(",");
        if("".equals(tokens[0]))
            msg.setReplyQueue(null);
        else
            msg.setReplyQueue(tokens[0]);
        msg.setBody(tokens[1]);
        return msg;
    }

    @Override
    public String toString() {
        if(replyQueue==null)
            return ","+body;
        else
            return replyQueue+","+body;
    }
    
    public String getReplyQueue() {
        return replyQueue;
    }

    private void setReplyQueue(String replyQueue) {
        this.replyQueue = replyQueue;
    }

    public String getBody() {
        return body;
    }

    private void setBody(String body) {
        this.body = body;
    }
}
