# Spring Messaging Example wih Stomp Websockets & Spring amqp

## General 


This is a Spring Boot Messaging Example serving different Websockets clients: a Swing client using the the Spring Stomp Java Client and a Javascript client using the stomp.js libarary, 
with dynamic / selective Endpoints with different ClientExample with dynamic / selective Endpoints.  Rabbitmq is used as Relay host for the Stomp Messaging. 

Also there is a Spring Boot Server, which uses Spring amqp Messaging to send & retrieve Messages to/from the same Topics as the Websocket Clients


One can send notifications over Stomp to all clients or selective Clients, using a unique id for each Client instance.

The Websockets are unsecured, since i have'nt found out, how to use the Spring Java Stomp Client with secured Websockets.

The Web clients require a login, the Swing client does'nt. 


## Usage

In for example Eclipe

1. Start /stomp-server/src/main/java/chat/TestChatServer.java

For the Javascript stomp.js clients 

1. Open various Browser Instance. Macos in Terminal: open -n /Applications/Safari.app
2. http://localhost:8080
3. Login either as u01, u11 or u21. With password being same as username
4. Connect 
5. Enter a shout with a to = being a logged in user or to being empty ->  to all 
6. The shout should be displayed, after a couple of secs for the respective user(s)
7. Ener a shout with being one of the ids displayed 
8. The shout should be displayed only for the respective client with the id

For the Swing client

1. Run  /stomp-swing-client/src/main/java/chat/ChatUi.groovy as Groovy Script
2. Similar UI as with the browser


## Preconditions 

A running Rabbitmq host, with the Stomp Plugin enabled, see also https://www.rabbitmq.com/stomp.html

```java
rabbitmq-plugins enable rabbitmq_stomp

```


## Implementation Notes


For for Stomp Messaging, see /chat-stomp-server/src/main/java/chat/ChatController.java and /chat-stomp-server/src/main/java/chat/WebSocketRelayConfig.java
