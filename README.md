# MiddleWare - CC462 Concurrent & Distributed Systems - Parcial
## Autores:
* ALEGRE IBAÑEZ, Víctor Augusto
* ZAVALETA BUENO, Romel Rolando
* ZEVALLOS LABARTHE, Enrique Martin

## Ejecución:
### Middleware:
El middleware usará los puertos 5555 (ServerSocket) y 4444 (ServerWebSocket).
En un terminal:
```
java -jar Middleware.jar
```
Necesita tener las dependencias de terceros:
* lib/Java-WebSocket-1.5.1.jar
* lib/slf4j-api-2.0.0-alpha1.jar

### Clientes
Los clientes en java necesitan la dependencia:
* lib/NuMQLib.jar
Para cada consumidor o productor hacer lo siguiente:
#### Hello World:
##### Java
Ejecutar en un terminal:
```
java -jar Prod1Hello.jar
java -jar Cons1Hello.jar
```
##### Javascript:
Abrir en un navegador
* prod1_hello.html
* cons1_hello.html

#### Work Queue:
##### Java
Ejecutar en un terminal:
```
java -jar Prod2Work.jar
java -jar Cons2Work.jar
```
##### Javascript:
Abrir en un navegador
* prod2_work.html
* cons2_work.html

#### Publish/Suscribers:
##### Java
Ejecutar en un terminal:
```
java -jar Prod3PubSub.jar
java -jar Cons3PubSub.jar
```
##### Javascript:
Abrir en un navegador
* prod3_pub_sub.html
* cons3_pub_sub.html

#### RPC Queue:
##### Java
Ejecutar en un terminal:
```
java -jar Prod4RPC.jar
java -jar Cons4RPC.jar
```
##### Javascript:
Abrir en un navegador
* prod4_rpc.html
* cons4_rpc.html
