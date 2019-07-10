package chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ResponseStatus;

import chat.model.ChatMessage;
import chat.model.Shout;

@Controller
public class ChatController {

	private static final String TOPIC_SHOUTS = "/topic/shouts";
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@MessageMapping("/chat")
	@ResponseStatus(value = HttpStatus.OK)
	public void greeting(ChatMessage message) throws Exception {
		System.out.println("Sending Shout: " + message.toString());
		Thread.sleep(3000); // simulated delay
		boolean all = StringUtils.isEmpty(message.getTo()) || message.equals("ALL"); 
		Shout shout = new Shout("Shouted: " + message.getChatText() + (all ? " to All " :  " to " + message.getTo()) + "!");
		if (all) {
			messagingTemplate.convertAndSend(TOPIC_SHOUTS,shout);
		} else {
			messagingTemplate.convertAndSendToUser(message.getTo().trim(),TOPIC_SHOUTS, shout);
		}
	}

}