package chat;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import chat.model.ChatMessage;
import chat.model.Shout;

@Controller
public class GreetingController {


    @MessageMapping("/chat")
    @SendTo("/topic/shouts")
    public Shout greeting(ChatMessage message) throws Exception {
        System.out.println("Sending Shout");
        Thread.sleep(3000); // simulated delay
        return new Shout("Shouted: " + message.getChatText() + "!");
    }

}