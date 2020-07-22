package middleware.server;

/**
 *
 * @author Victor
 */
public class Command {
    //TODO: ver si es necesario usar mas campos a parte del contenido de mensaje
    public static final int CMD_DEC_TOPIC = 1;
    public static final int CMD_DEC_QUEUE = 2;
    public static final int CMD_BIND_QUEUE = 3;
    public static final int CMD_SEND = 4;
    public static final int CMD_CONSUME = 5;
    public static final int CMD_CONS_ACK = 6;
    
    private int cmd;
    private String topicName;
    private String queueName;
    private String body;
    
    private Command(){}
    
    public static Command parseCommand(String msg){
        Command cmmd = null;
        int cmd_num = Integer.parseInt(msg.substring(0, 1));
        switch(cmd_num){
            case CMD_DEC_TOPIC:
                cmmd = parseDecTopic(msg);
                break;
            case CMD_DEC_QUEUE:
                cmmd = parseDecQueue(msg);
                break;
            case CMD_BIND_QUEUE:
                cmmd = parseBindQueue(msg);
                break;
            case CMD_SEND:
                cmmd = parseSend(msg);
                break;    
            case CMD_CONSUME:
                cmmd = parseConsume(msg);
                break;
            case CMD_CONS_ACK:
                cmmd = parseConsAck(msg);
                break;
            default:
                break;
        }
        return cmmd;
    }
    
    private static Command parseDecTopic(String msg){
        Command message = new Command();
        String[] tokens = msg.split(",");
        message.setCmd(CMD_DEC_TOPIC);
        message.setTopicName(tokens[1]);
        return message;
    }
    
    private static Command parseDecQueue(String msg){
        Command message = new Command();
        String[] tokens = msg.split(",");
        message.setCmd(CMD_DEC_QUEUE);
        message.setQueueName(tokens[1]);
        return message;
    }
    
    private static Command parseBindQueue(String msg){
        Command message = new Command();
        String[] tokens = msg.split(",");
        message.setCmd(CMD_BIND_QUEUE);
        message.setTopicName(tokens[1]);
        message.setQueueName(tokens[2]);
        return message;
    }
    
    private static Command parseSend(String msg){
        Command message = new Command();
        String[] tokens = msg.split(",");
        message.setCmd(CMD_SEND);
        if("".equals(tokens[1]))
            message.setTopicName(null);
        else
            message.setTopicName(tokens[1]);
        if("".equals(tokens[2]))
            message.setQueueName(null);
        else
            message.setQueueName(tokens[2]);
        message.setBody(tokens[3]);
        return message;
    }
    
    private static Command parseConsume(String msg){
        Command message = new Command();
        String[] tokens = msg.split(",");
        message.setCmd(CMD_CONSUME);
        message.setQueueName(tokens[1]);
        return message;
    }
    
    private static Command parseConsAck(String msg){
        Command message = new Command();
        String[] tokens = msg.split(",");
        message.setCmd(CMD_CONS_ACK);
        message.setQueueName(tokens[1]);
        return message;
    }

    public int getCmd() {
        return cmd;
    }

    private void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public String getTopicName() {
        return topicName;
    }

    private void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getQueueName() {
        return queueName;
    }

    private void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getBody() {
        return body;
    }

    private void setBody(String body) {
        this.body = body;
    }
    
    
}
