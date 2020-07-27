var addrInput = document.getElementById("addrInput");
var msgInput = document.getElementById("msgInput");
var connBtn = document.getElementById("connBtn");
var sendBtn = document.getElementById("sendBtn");
var output = document.getElementById("output");
var client;
var topicName = "logs"

function conectar(){
    var ipServer = addrInput.value;
    if(ipServer===""){
        alert("Ingrese la dirección del middleware");
        return;
    }
    client = new ClientApi(ipServer);
    client.onOpen(onOpenCB);
}

function send(){
    var msg = msgInput.value;
    if(msg === ""){
        alert("Ingrese un mensaje antes");
        return;
    }
    client.producerSend(topicName,"","",msg);
    print(topicName+" > "+msg)
}

var onOpenCB = function(e){
    print("Conectado a "+client.getUrlServer());
    
    client.declareTopic(topicName);
    print("Se usa el Topico: "+topicName);

    addrInput.disabled = true;
    connBtn.disabled = true;
    msgInput.disabled = false;
    sendBtn.disabled = false;
    
}

function print(text){
    output.innerHTML += "<br/>" + text;
}