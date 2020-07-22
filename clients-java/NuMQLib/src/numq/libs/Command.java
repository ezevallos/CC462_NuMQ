package numq.libs;

import java.util.LinkedList;
import java.util.List;

/**
 * Comandos que se envian en la comunicacion Cliente-Middleware
 * CMD_DEC_TOPIC: "1,topic_name"
 * CMD_DEC_QUEUE: "2,queue_name"
 * CMD_SUBS_QUEUE: "3,topic_name,queue_name"
 * CMD_PROD_SEND: "4,topic_name,queue_name,body"
 * CMD_CONSUME: "5,queue_name"
 * CMD_CONS_ACK: "6,queue_name"
 * @author Victor
 */
public class Command {
    public static final int CMD_DEC_TOPIC = 1;
    public static final int CMD_DEC_QUEUE = 2;
    public static final int CMD_SUBS_QUEUE = 3;
    public static final int CMD_PROD_SEND = 4;
    public static final int CMD_CONSUME = 5;
    public static final int CMD_CONS_ACK = 6;
    private int commandNum;
    private List<String> values;
    
    private Command(){}
    
    protected static Command createDecTopicMsg(String topicName){
        Command msg = new Command();
        msg.setCommandNum(CMD_DEC_TOPIC);
        msg.addValue(topicName);
        return msg;
    }
    
    protected static Command createDecQueueMsg(String queueName){
        Command msg = new Command();
        msg.setCommandNum(CMD_DEC_QUEUE);
        msg.addValue(queueName);
        return msg;
    }
    
    protected static Command createBindQueueMsg(String topicName,String queueName){
        Command msg = new Command();
        msg.setCommandNum(CMD_SUBS_QUEUE);
        msg.addValue(topicName);
        msg.addValue(queueName);
        return msg;
    }
    
    protected static Command createSendMsg(String topicName,String queueName, String body){
        Command msg = new Command();
        msg.setCommandNum(CMD_PROD_SEND);
        msg.addValue(topicName);
        msg.addValue(queueName);
        msg.addValue(body);
        return msg;
    }
    
    protected static Command createConsumeMsg(String queueName){
        Command msg = new Command();
        msg.setCommandNum(CMD_CONSUME);
        msg.addValue(queueName);
        return msg;
    }
    
    protected static Command createConsAckMsg(String queueName){
        Command msg = new Command();
        msg.setCommandNum(CMD_CONS_ACK);
        msg.addValue(queueName);
        return msg;
    }

    private void setCommandNum(int commandNum) {
        this.commandNum = commandNum;
    }

    private void addValue(String value){
        if(values==null){
            values = new LinkedList<>();
        }
        values.add(value);
    }
    
    @Override
    public String toString(){
        //Retorna: "CMD_NUM,val1,val2,val3,..."
        String str = ""+commandNum;
        if(values!=null)
            str = values.stream().map((val) -> ","+val).reduce(str, String::concat);
        return str;
    }
}
