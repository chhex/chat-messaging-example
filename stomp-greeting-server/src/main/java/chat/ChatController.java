package chat;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import chat.model.ChatMessage;
import chat.model.Shout;

@Controller
public class ChatController {

	@MessageMapping("/chat/all")
	@SendTo("/topic/shouts/all")
	public Shout shoutToAll(ChatMessage message) throws Exception {
		System.out.println("Sending Shout: " + message.toString());
		Thread.sleep(3000); // simulated delay
		return new Shout("Shouted: " + message.getChatText() + " to All!");

	}

	@MessageMapping("/chat/{sessionId}")
	@SendTo("/topic/shouts/all/{sessionId}")
	public Shout shoutToUser(@DestinationVariable String sessionId, ChatMessage message) throws Exception {
		System.out.println("Sending Shout: " + message.toString());
		Thread.sleep(3000); // simulated delay
		return new Shout("Shouted: " + message.getChatText() + " to " + sessionId + "!");

	}
}