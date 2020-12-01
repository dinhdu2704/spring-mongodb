package vn.vnpt.stc.enterprise.event.amqp;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import vn.vnpt.stc.enterpise.commons.event.Event;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AMQPEventPublisher extends AMQPAbstractConfiguration {

    private ExecutorService pool;
    private LinkedBlockingQueue<Runnable> publishQueue = new LinkedBlockingQueue<Runnable>();

    public AMQPEventPublisher(CachingConnectionFactory connectionFactory) {
        pool = new ThreadPoolExecutor(2,2,0L, TimeUnit.MILLISECONDS,publishQueue);
        this.topicExchangeName = TOPIC_EXCHANGE_NAME_DEFAULT;
        this.CONNECTION_FACTORY = connectionFactory;
        amqpAdmin().declareExchange(topicExchange(topicExchangeName));
    }

    public AMQPEventPublisher(CachingConnectionFactory connectionFactory, String topicExchangeName) {
        pool = new ThreadPoolExecutor(2,2,0L, TimeUnit.MILLISECONDS,publishQueue);
        this.topicExchangeName = topicExchangeName;
        this.CONNECTION_FACTORY = connectionFactory;
        amqpAdmin().declareExchange(topicExchange(topicExchangeName));
    }
    @Override
    public void configureRabbitTemplate(RabbitTemplate template) {
        template.setExchange(topicExchangeName);
    }

    public Event publish(String routingKey, Event event) {
        if ((event.id == null)||(event.id.isEmpty())) {
            event.id = UUID.randomUUID().toString();
        }
        pool.submit(new PublishEventTask(routingKey, event));
        return event;
    }

    public Event publish(String routingKey, Event event, MessageProperties messageProperties) {
        if ((event.id == null)||(event.id.isEmpty())) {
            event.id = UUID.randomUUID().toString();
        }
        pool.submit(new PublishEventTask(routingKey, event, messageProperties));
        return event;
    }

    private class PublishEventTask implements Runnable {

        private String routingKey;
        private MessageProperties messageProperties;
        private Object message;

        public PublishEventTask(String routingKey, Event event) {
            this.routingKey = routingKey;
            this.message = event;
        }

        public PublishEventTask(String routingKey, Event event, MessageProperties messageProperties) {
            this.routingKey = routingKey;
            if (messageProperties != null) {
                this.messageProperties = messageProperties;
                this.message = rabbitTemplate().getMessageConverter().toMessage(event, messageProperties);
            } else {
                this.message = event;
            }
        }

        @Override
        public void run() {
                rabbitTemplate().convertAndSend(routingKey, message, new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message m) throws AmqpException {
                        m.getMessageProperties().setContentType("application/json");
                        return m;
                    }
                });
        }

    }
}
