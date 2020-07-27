var addrInput = document.getElementById("addrInput");
var connBtn = document.getElementById("connBtn");
var output = document.getElementById("output");
var client;
var queueName = "work_queue";

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
    
    client.declareQueue(queueName);
    print("Se usa la cola: "+queueName);
    client.consume(queueName,false,consumeCB)
    print("Se consume desde cola: "+queueName);

    addrInput.disabled = true;
    connBtn.disabled = true;
    
}

var consumeCB = function(replyQueue,body){
    print(queueName+" > "+body);
    switch(body){
        case 'tarea1':
            task1();
            break;
        case 'tarea2':
            task2();
            break;
        default:
            break;
    }
}

function task1(){
    print("Realizando tarea corta...");
    setTimeout(function(){
        print("Tarea corta finalizada");
        client.consAck(queueName);
    },500);
}

function task2(){
    print("Realizando tarea larga...");
    setTimeout(function(){
        print("Tarea larga finalizada");
        client.consAck(queueName);
    },2000);
}

function print(text){
    output.innerHTML += "<br/>" + text;
}