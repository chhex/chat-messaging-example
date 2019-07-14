package chat;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import chat.model.ChatMessage;
import chat.model.Shout;

@Controller
public class ChatController {
	
	private static long idNr = 0;

	@MessageMapping("/chat/all")
	@SendTo("/topic/shouts/all")
	public Shout shoutToAll(ChatMessage message) throws Exception {
		System.out.println("Sending Shout to all , message: " + message.toString());
		return new Shout("Shouted: " + message.getChatText() + " to All!");

	}

	@MessageMapping("/chat/{id}")
	@SendTo("/topic/shouts/usersession.{id}")
	public Shout shoutToSessionId(@DestinationVariable String id, ChatMessage message) throws Exception {
		System.out.println("Sending Shout to : " + id + ", message: " + message.toString());
		return new Shout("Shouted: " + message.getChatText() + " to " + id + "!");

	}
	@RequestMapping(value = "/getUserSessionId", method = RequestMethod.GET)
	@ResponseBody
	public String getUserSessionId() {
		return "" + ++idNr;
	}
}