package chat.model;

public class ChatMessage {

    private String chatText;
    private String to;

    public String getChatText() {
        return chatText;
    }

	public String getTo() {
		return to;
	}

	@Override
	public String toString() {
		return "ChatMessage [chatText=" + chatText + ", to=" + to + "]";
	}

}