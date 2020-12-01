package vn.vnpt.stc.enterprise.event;

import org.springframework.amqp.core.MessageProperties;
import vn.vnpt.stc.enterpise.commons.event.Event;
import vn.vnpt.stc.enterprise.event.amqp.AMQPEventPublisher;

public interface EventBus {
    public void registerSubscriber(AMQPSubscriber subscriber);
    public Event publish(String routingKey, Event event);
    public Event publish(String routingKey, Event event, MessageProperties messageProperties);
    public Event publish(String topicExchange, String routingKey, Event event);
    public Event publish(String topicExchange, String routingKey, Event event, MessageProperties messageProperties);
    public AMQPEventPublisher registerPublisher(String topicExchange);
}
