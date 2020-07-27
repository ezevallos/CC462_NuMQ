var addrInput = document.getElementById("addrInput");
var connBtn = document.getElementById("connBtn");
var output = document.getElementById("output");
var client;
var queueName = "rpc_queue";

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
    client.consume(queueName,true,consumeCB)
    print("Se consume desde cola: "+queueName);

    addrInput.disabled = true;
    connBtn.disabled = true;
    
}

var consumeCB = function(replyQueue,body){
    print(queueName+" > fib("+body+")");
    var n = parseInt(body);
    var resp = "fib("+n+") = "+fib(n);
    client.producerSend("",replyQueue,"",resp);
    print("resp: "+resp+" > "+replyQueue);
}

function print(text){
    output.innerHTML += "<br/>" + text;
}

function fib(n){
    if (n == 0) return 0;
    if (n == 1) return 1;
    return fib(n - 1) + fib(n - 2);
}