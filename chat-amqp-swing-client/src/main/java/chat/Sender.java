package chat;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import chat.model.ChatMessage;
import chat.model.Shout;

public class Sender {

	@Autowired
	private RabbitTemplate template;

	@Autowired
	private TopicExchange topic;

	AtomicInteger index = new AtomicInteger(0);

	AtomicInteger count = new AtomicInteger(0);

	private final String[] keys = { "shouts.all","shouts.usersession.4", "shouts.usersession.3" ,"shouts.usersession.2" , "shouts.usersession.1"};

	@Scheduled(fixedDelay = 10000, initialDelay = 2000)
	public void send() {
		int cnt = count.incrementAndGet();
		StringBuilder builder = new StringBuilder(cnt + " Hello from ChatAmqpClientServer to ");
		if (this.index.incrementAndGet() == keys.length) {
			this.index.set(0);
		}
		String key = keys[this.index.get()];
		if (key.contains("all")) {
			builder.append("ALL");
		} else {
			builder.append(key.charAt(key.length() -1));
		}
		String message = builder.toString();
		Shout shout = new Shout(message);
		template.convertAndSend(topic.getName(), key, shout);
		System.out.println(" [x] Sent '" + shout.toString() + "'");
	}

}
