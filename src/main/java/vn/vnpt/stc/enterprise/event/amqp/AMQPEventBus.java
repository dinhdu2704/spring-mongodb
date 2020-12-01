package vn.vnpt.stc.enterprise.event.amqp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.context.ApplicationContext;
import vn.vnpt.stc.enterpise.commons.event.Event;
import vn.vnpt.stc.enterprise.event.AMQPSubscriber;
import vn.vnpt.stc.enterprise.event.EventBus;

import java.util.HashMap;
import java.util.Map;

import static vn.vnpt.stc.enterpise.commons.constants.QueueConstants.TOPIC_EXCHANGE_NAME_DEFAULT;

public class AMQPEventBus implements EventBus {

    private static final Logger logger = LoggerFactory.getLogger(AMQPEventBus.class);
    private final Object lock = new Object();
    private Map<String, AMQPEventPublisher> publishers;
    private Map<String, AMQPEventListener> subscribers;
    private ApplicationContext ctx;
    private CachingConnectionFactory connectionFactory;

    public AMQPEventBus(CachingConnectionFactory connectionFactory, ApplicationContext ctx) {
        this.connectionFactory = connectionFactory;
        this.ctx = ctx;
        publishers = new HashMap<String, AMQPEventPublisher>();
        subscribers = new HashMap<String, AMQPEventListener>();
        publishers.put(TOPIC_EXCHANGE_NAME_DEFAULT, new AMQPEventPublisher(connectionFactory, TOPIC_EXCHANGE_NAME_DEFAULT));
    }

    @Override
    public void registerSubscriber(AMQPSubscriber subscriber) {
        AMQPEventListener listener = new AMQPEventListener(connectionFactory,ctx,subscriber);
        String key = subscriber.getRoutingKey() + "." + subscriber.getQueue() + "." + subscriber.getExchange() ;
        logger.info("Registering amqp listener with key {}",key);
        synchronized (lock) {
            subscribers.put(key,listener);
        }
    }

    @Override
    public Event publish(String routingKey, Event event) {
        AMQPEventPublisher publisher = publishers.get(TOPIC_EXCHANGE_NAME_DEFAULT);
        return publisher.publish(routingKey,event);
    }

    @Override
    public Event publish(String routingKey, Event event, MessageProperties messageProperties) {
        AMQPEventPublisher publisher = publishers.get(TOPIC_EXCHANGE_NAME_DEFAULT);
        return publisher.publish(routingKey,event, messageProperties);
    }

    @Override
    public Event publish(String topicExchange, String routingKey, Event event) {
        AMQPEventPublisher publisher = publishers.get(topicExchange);
        if (publisher == null) {
            publisher = new AMQPEventPublisher(connectionFactory, topicExchange);
            publishers.put(topicExchange, publisher);
        }
        return publisher.publish(routingKey,event);
    }

    @Override
    public Event publish(String topicExchange, String routingKey, Event event, MessageProperties messageProperties) {
        AMQPEventPublisher publisher = publishers.get(topicExchange);
        if (publisher == null) {
            publisher = new AMQPEventPublisher(connectionFactory, topicExchange);
            publishers.put(topicExchange, publisher);
        }
        return publisher.publish(routingKey,event, messageProperties);
    }

    @Override
    public AMQPEventPublisher registerPublisher(String topicExchange) {
        AMQPEventPublisher publisher = publishers.get(topicExchange);
        if (publisher == null) {
            publisher = new AMQPEventPublisher(connectionFactory, topicExchange);
            publishers.put(topicExchange, publisher);
        }
        return publisher;
    }

}
