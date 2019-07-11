package chat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {

    private String chatText;
    private String to;
   
   	@Override
	public String toString() {
		return "ChatMessage [chatText=" + chatText + ", to=" + to + "]";
	}
   	
   	public static class Builder {

	}
   	
   	


}