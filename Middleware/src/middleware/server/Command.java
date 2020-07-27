package middleware.server;

/**
 * Comandos que se reciben en la comunicacion Cliente-Middleware
 * CMD_DEC_TOPIC: "1|topic_name"
 * CMD_DEC_QUEUE: "2|queue_name"
 * CMD_SUBS_QUEUE: "3|topic_name|queue_name"
 * CMD_PROD_SEND: "4|topic_name|queue_name|reply_queue|body"
 * CMD_CONSUME: "5|queue_name"
 * CMD_CONS_ACK: "6|queue_name"
 * @author Victor
 */
public class Command {
    //TODO: ver si es necesario usar mas campos a parte del contenido de mensaje
    public static final int CMD_DEC_TOPIC = 1;
    public static final int CMD_DEC_QUEUE = 2;
    public static final int CMD_SUBS_QUEUE = 3;
    public static final int CMD_PROD_SEND = 4;
    public static final int CMD_CONSUME = 5;
    public static final int CMD_CONS_ACK = 6;
    
    private int cmd;
    private String topicName;
    private String queueName;
    private Message message;
    
    private Command(){}
    
    public static Command parseCommand(String msg){
        Command cmmd = null;
        int cmd_num = 0;
        try{
            cmd_num = Integer.parseInt(msg.substring(0, 1));
        }catch(NumberFormatException ex){}
        switch(cmd_num){
            case CMD_DEC_TOPIC:
                cmmd = parseDecTopic(msg);
                break;
            case CMD_DEC_QUEUE:
                cmmd = parseDecQueue(msg);
                break;
            case CMD_SUBS_QUEUE:
                cmmd = parseBindQueue(msg);
                break;
            case CMD_PROD_SEND:
                cmmd = parseProdSend(msg);
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
    
    /**
     * Interpreta un comando Declaracion de Topico
     * Formato: "1,topic_name"
     * @param msg
     * @return 
     */
    private static Command parseDecTopic(String msg){
        Command cmd = new Command();
        String[] tokens = msg.split("\\|");
        cmd.setCmd(CMD_DEC_TOPIC);
        cmd.setTopicName(tokens[1]);
        return cmd;
    }
    
    /**
     * Interpreta un comando Declaracion de Queue
     * Formato: "2,queue_name"
     * @param msg
     * @return 
     */
    private static Command parseDecQueue(String msg){
        Command cmd = new Command();
        String[] tokens = msg.split("\\|");
        cmd.setCmd(CMD_DEC_QUEUE);
        cmd.setQueueName(tokens[1]);
        return cmd;
    }
    
    /**
     * Interpreta un comando subscribcion de queue
     * Formato: "3,topic_name,queue_name"
     * @param msg
     * @return 
     */
    private static Command parseBindQueue(String msg){
        Command cmd = new Command();
        String[] tokens = msg.split("\\|");
        cmd.setCmd(CMD_SUBS_QUEUE);
        cmd.setTopicName(tokens[1]);
        cmd.setQueueName(tokens[2]);
        return cmd;
    }
    
    /**
     * Interpreta un comando envio de mensaje desde producer
     * Formato: "4,topic_name,queue_name,body"
     * @param msg
     * @return 
     */
    private static Command parseProdSend(String msg){
        Command cmd = new Command();
        Message message = null;
        String[] tokens = msg.split("\\|");
        cmd.setCmd(CMD_PROD_SEND);
        if("".equals(tokens[1]))
            cmd.setTopicName(null);
        else
            cmd.setTopicName(tokens[1]);
        if("".equals(tokens[2]))
            cmd.setQueueName(null);
        else
            cmd.setQueueName(tokens[2]);
        if("".equals(tokens[3]))
            message = Message.createMessage(null, tokens[4]);
        else
            message = Message.createMessage(tokens[3], tokens[4]);
        cmd.setMessage(message);
        return cmd;
    }
    
    /**
     * Interpreta un comando de consumo de mensajes de una queue
     * Formato: "5,queue_name"
     * @param msg
     * @return 
     */
    private static Command parseConsume(String msg){
        Command cmd = new Command();
        String[] tokens = msg.split("\\|");
        cmd.setCmd(CMD_CONSUME);
        cmd.setQueueName(tokens[1]);
        return cmd;
    }
    
    /**
     * Interpreta un comando de ACK de consumo
     * Indica que el cliente esta disponible para el siguiente mensaje
     * Formato: "6,queue_name"
     * @param msg
     * @return 
     */
    private static Command parseConsAck(String msg){
        Command cmd = new Command();
        String[] tokens = msg.split("\\|");
        cmd.setCmd(CMD_CONS_ACK);
        cmd.setQueueName(tokens[1]);
        return cmd;
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

    public Message getMessage() {
        return message;
    }

    private void setMessage(Message message) {
        this.message = message;
    }
    
    
}
