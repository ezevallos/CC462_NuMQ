package numq.libs;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Victor
 */
public class Message {
    public static final int CMD_DEC_EXCH = 1;
    public static final int CMD_DEC_QUEUE = 2;
    public static final int CMD_BIND_QUEUE = 3;
    public static final int CMD_SEND = 4;
    public static final int CMD_CONSUME = 5;
    public static final int CMD_CONS_ACK = 6;
    private int commandNum;
    private List<String> values;
    
    private Message(){}
    
    protected static Message createDecExchMsg(String xchName){
        Message msg = new Message();
        msg.setCommandNum(CMD_DEC_EXCH);
        msg.addValue(xchName);
        return msg;
    }
    
    protected static Message createDecQueueMsg(String queueName){
        Message msg = new Message();
        msg.setCommandNum(CMD_DEC_QUEUE);
        msg.addValue(queueName);
        return msg;
    }
    
    protected static Message createBindQueueMsg(String xchName,String queueName){
        Message msg = new Message();
        msg.setCommandNum(CMD_BIND_QUEUE);
        msg.addValue(xchName);
        msg.addValue(queueName);
        return msg;
    }
    
    protected static Message createSendMsg(String xchName,String queueName, String body){
        Message msg = new Message();
        msg.setCommandNum(CMD_SEND);
        msg.addValue(xchName);
        msg.addValue(queueName);
        msg.addValue(body);
        return msg;
    }
    
    protected static Message createConsumeMsg(String queueName){
        Message msg = new Message();
        msg.setCommandNum(CMD_CONSUME);
        msg.addValue(queueName);
        return msg;
    }
    
    protected static Message createConsAckMsg(String queueName){
        Message msg = new Message();
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
    
    public String toString(){
        //Retorna: "CMD_NUM,val1,val2,val3,..."
        String str = ""+commandNum;
        if(values!=null)
            str = values.stream().map((val) -> ","+val).reduce(str, String::concat);
        return str;
    }
}
