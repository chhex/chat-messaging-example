package chat;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfig {
	
	@Value("${rabbit.host}")
	String rabbitHost;

	@Value("${rabbit.user}")
	String rabbitHostUser;

	@Value("${rabbit.pw}")
	String rabbitHostPw;

	@Bean
	public TopicExchange topic() {
		return new TopicExchange("amq.topic");
	}

	@Bean
	public RecieverAll receiverAll() {
		return new RecieverAll();
	}
	

	@Bean
	public Queue autoDeleteQueueUserSession() {
		return new AnonymousQueue();
	}

	@Bean
	public Queue autoDeleteQueueAll() {
		return new AnonymousQueue();
	}

	@Bean
	public MessageConverter messageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitHost);
		connectionFactory.setUsername(rabbitHostUser);
		connectionFactory.setPassword(rabbitHostPw);
		return connectionFactory;
	}

	@Bean
	public SimpleMessageListenerContainer messageListenerContainerAll() {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
		container.setQueues(autoDeleteQueueAll());
		container.setAutoStartup(true);
		container.setAcknowledgeMode(AcknowledgeMode.AUTO);
		container.setMessageListener(receiverAll());
		return container;
	}

	@Bean()
	public RabbitTemplate rabbitTemplateAll() {
		RabbitTemplate template = new RabbitTemplate(connectionFactory());
		template.setMessageConverter(messageConverter());
		template.setExchange(topic().getName());
		return template;
	}
	

	@Bean
	public SimpleMessageListenerContainer messageListenerContainerSpecific() {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
		container.setQueues(autoDeleteQueueUserSession());
		container.setAutoStartup(true);
		container.setAcknowledgeMode(AcknowledgeMode.AUTO);
		container.setMessageListener(receiverAll());
		return container;
	}

	@Bean
	public Binding bindingUserSession(TopicExchange topic, Queue autoDeleteQueueUserSession) {
		return BindingBuilder.bind(autoDeleteQueueUserSession).to(topic).with("shouts.usersession.1");
	}

	@Bean
	public Binding bindingAll(TopicExchange topic, Queue autoDeleteQueueAll) {
		return BindingBuilder.bind(autoDeleteQueueAll).to(topic).with("shouts.all");
	}

	@Bean
	public Sender sender() {
		return new Sender();
	}

}
