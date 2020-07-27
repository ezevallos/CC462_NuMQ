var addrInput = document.getElementById("addrInput");
var numInput = document.getElementById("numInput");
var reqBtn = document.getElementById("reqBtn");
var output = document.getElementById("output");
var client;
var queueName = "rpc_queue";
var replyQueue;

function conectar(){
    var ipServer = addrInput.value;
    if(ipServer===""){
        alert("Ingrese la dirección del middleware");
        return;
    }
    client = new ClientApi(ipServer);
    client.onOpen(onOpenCB);
}

function request(){
    var msg = numInput.value;
    if(msg===""){
        alert("Ingrese un numero");
        return;
    }
    client.producerSend("",queueName,replyQueue,msg);
    print("Solicita: fib("+msg+")")
}

var onOpenCB = function(e){
    print("Conectado a "+client.getUrlServer());
    
    client.declareQueue(queueName);
    print("Se solicita a la cola: "+queueName);

    replyQueue = client.declareGenQueue();
    print("Cola de respuesta: "+replyQueue);

    client.consume(replyQueue,false,onResponse);
    print("Se consume respuestas desde cola: "+replyQueue);

    addrInput.disabled = true;
    connBtn.disabled = true;
    numInput.disabled = false;
    reqBtn.disabled = false;
    
}

var onResponse = function(rq,body){
    print("Respuesta: "+body);
    client.consAck(replyQueue);
}

function print(text){
    output.innerHTML += "<br/>" + text;
}