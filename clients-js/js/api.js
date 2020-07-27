/**
 * Api de cliente para comunicar con el Middleware
 */
class ClientApi {
    constructor(ipAddr){
        this.urlServer = 'ws://'+ipAddr+':4444';
        this.conn = new WebSocket(this.urlServer);
    }

    onOpen(callBack){
        this.conn.onopen = callBack;
    }

    onError(callBack){
        this.conn.onerror = callBack;
    }

    declareTopic(topicName){
        if(topicName===""){
            console.log("Error: Ingrese un Topico");
            return;
        }
        var msg = "1|"+topicName;
        this.conn.send(msg);
    }

    declareQueue(queueName){
        if(queueName===""){
            console.log("Error: Ingrese un Queue");
            return;
        }
        var msg = "2|"+queueName;
        this.conn.send(msg);
    }

    declareGenQueue(){
        var queueName = this.hash();
        this.declareQueue(queueName);
        return queueName;
    }

    subscribeQueue(topicName,queueName){
        if(topicName === "" || queueName===""){
            console.log("Error: Ingrese Topico y Queue");
            return;
        }
        var msg = "3|"+topicName+"|"+queueName;
        this.conn.send(msg);
    }

    producerSend(topicName,queueName,replyQueue,body){
        if(body === ""){
            console.log("Error: Ingrese un mensaje");
            return;
        }

        var mTopic = "", mQueue = "", mReplyQueue = "";

        if(topicName === ""){
            if(queueName === ""){
                console.log("Error: Debe especificar el queue");
                return;
            }
            mQueue = queueName;
        }else
            mTopic = topicName;
        if(replyQueue !== "")
            mReplyQueue = replyQueue;
        
        var msg = "4|"+mTopic+"|"+mQueue+"|"+mReplyQueue+"|"+body;
        this.conn.send(msg);
    }

    consume(queueName,autoAck,callBack){
        if(queueName===""){
            console.log("Error: Debe especificar el queue a consumir");
            return;
        }
        var client = this;

        this.conn.onmessage = function(e){
            var msg = e.data;
            console.log(msg);
            var tokens = msg.split(/\|/g);
            callBack(tokens[0],tokens[1]);
            if(autoAck)
                client.consAck(queueName);
        };
        
        var msg = "5|"+queueName;
        this.conn.send(msg);
    }

    consAck(queueName){
        var msg = "6|"+queueName;
        this.conn.send(msg);
    }

    getUrlServer(){
        return this.urlServer;
    }

    hash(){
        var result = '';
        var characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
        var charactersLength = characters.length;
        for ( var i = 0; i < 10; i++ ) {
            result += characters.charAt(Math.floor(Math.random() * charactersLength));
        }
        return result;
    }
}
