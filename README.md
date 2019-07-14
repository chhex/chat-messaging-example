# Stomp Websockets Example with dynamic / selective Endpoints with different Clients

## General 

This is a Spring Boot Websocket Example serving different clients: a Swing client using the the Spring Stomp Java Client and a Javascript client using the stomp.js libarary.

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




## Implementation Notes

In order to recieve notifications for all and user specfic, the Stomp client must subscribe to two topics:

```javascript
stompClient.subscribe('/topic/shouts', function(shout){
       showShout(JSON.parse(shout.body).content);
    });
    stompClient.subscribe('/user/topic/shouts', function(shout){
        showShout(JSON.parse(shout.body).content);
    });
```
   
In chat.ChatController the messaging is done not with the Annotation @SendTo but using org.springframework.messaging.simp.SimpMessagingTemplate, which provides also and API for sending messages to a specific user, in order to cover both cases:

```java 
	if (all) {
		messagingTemplate.convertAndSend(TOPIC_SHOUTS,shout);
	} else {
		messagingTemplate.convertAndSendToUser(message.getTo().trim(),TOPIC_SHOUTS, shout);
	}
```
