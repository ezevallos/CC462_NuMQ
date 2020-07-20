package middleware.server;

/**
 *
 * @author Victor
 */
public class Message {
    //TODO: ver si es necesario usar mas campos a parte del contenido de mensaje
    private String exchangeName;
    private String queueName;
    private String message;

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    
}
