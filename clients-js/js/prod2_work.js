var addrInput = document.getElementById("addrInput");
var task1Btn = document.getElementById("task1Btn");
var task2Btn = document.getElementById("task2Btn");
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

function task1(){
    var msg = "tarea1";
    client.producerSend("",queueName,"",msg);
    print(queueName+" > Tarea corta")
}

function task2(){
    var msg = "tarea2";
    client.producerSend("",queueName,"",msg);
    print(queueName+" > Tarea larga")
}

var onOpenCB = function(e){
    print("Conectado a "+client.getUrlServer());
    
    client.declareQueue(queueName);
    print("Se usa la cola: "+queueName);

    addrInput.disabled = true;
    connBtn.disabled = true;
    task1Btn.disabled = false;
    task2Btn.disabled = false;
    
}

function print(text){
    output.innerHTML += "<br/>" + text;
}