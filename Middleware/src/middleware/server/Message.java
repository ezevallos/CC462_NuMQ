package middleware.server;

/**
 * Message: "reply_queue,body"
 * @author Victor
 */
public class Message {
    private String replyQueue;
    private String body;

    private Message(){}
    
    public static Message createMessage(String replyQueue,String body){
        Message msg = new Message();
        msg.setReplyQueue(replyQueue);
        msg.setBody(body);
        return msg;
    }

    /**
     * Convierte a trama el mensaje
     * @return "reply_queue|body"
     */
    @Override
    public String toString() {
        if(replyQueue==null)
            return "|"+body;
        else
            return replyQueue+"|"+body;
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
