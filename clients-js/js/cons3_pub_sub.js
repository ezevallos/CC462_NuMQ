var addrInput = document.getElementById("addrInput");
var connBtn = document.getElementById("connBtn");
var output = document.getElementById("output");
var client;
var topicName = "logs";
var queueName;

function conectar(){
    var ipServer = addrInput.value;
    if(ipServer===""){
        alert("Ingrese la dirección del middleware");
        return;
    }
    client = new ClientApi(ipServer);
    client.onOpen(onOpenCB);
}

var onOpenCB = function(e){
    print("Conectado a "+client.getUrlServer());
    client.declareTopic(topicName);
    print("Se usa el topico: "+topicName);

    queueName = client.declareGenQueue();
    print("Se usa la cola: "+queueName);

    client.subscribeQueue(topicName,queueName);
    print("Se suscribe "+queueName+" al topico "+topicName);

    client.consume(queueName,true,consumeCB)
    print("Se consume desde cola: "+queueName);

    addrInput.disabled = true;
    connBtn.disabled = true;
    
}

var consumeCB = function(replyQueue,body){
    print(topicName+":"+queueName+" > "+body);
}

function print(text){
    output.innerHTML += "<br/>" + text;
}