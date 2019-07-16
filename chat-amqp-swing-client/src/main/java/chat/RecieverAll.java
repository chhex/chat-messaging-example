package chat;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

public class RecieverAll implements MessageListener {

//	@RabbitListener(queues = "#{autoDeleteQueueUserSession.name}")
//	public void receiveUserSession(Object in) throws InterruptedException {
//		System.out.println("Recieved for ME: " + in.toString());
//	}

	@Override
	public void onMessage(Message message) {
		System.out.println("Recieved for ALL: " + message.toString());
		byte[] msgBody = message.getBody();
		String msg = new String(msgBody);
		System.out.println("Recieved for ALL: " + msg.toString());


	}

}
