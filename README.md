# Stomp Websockets Example with Java Client

## General 

This is a fork of https://nickebbutt/stomp-websockets-java-client added with the following aspects:

  * Spring Security : Login & In Memory Authentification
  * Selective User based notification 
  * Optional notification to all logged in Users
 
## Usage

1. Start /stomp-greeting-server/src/main/java/chat/TestChatServer.java
2. Open various Browser Instance. Macos in Terminal: open -n /Applications/Safari.app
3. http://localhost:8080
4. Login either as tu01, tu11 or tu21. With password being _pass suffixed to the username
5. Connect 
6. Enter a shout with a to = being a logged in user or to being empty ->  to all 
7. The shout should be displayed, after a couple of secs for the respective user(s)


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
