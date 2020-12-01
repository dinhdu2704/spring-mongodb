package vn.vnpt.stc.enterprise.event.amqp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.context.ApplicationContext;
import vn.vnpt.stc.enterpise.commons.event.Event;
import vn.vnpt.stc.enterprise.event.AMQPSubscriber;

import java.lang.reflect.InvocationTargetException;

public class MessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    private ApplicationContext ctx;
    private AMQPSubscriber subscriber;

    public MessageHandler(ApplicationContext ctx, AMQPSubscriber subscriber) {
        this.ctx = ctx;
        this.subscriber = subscriber;
    }

    public void handleMessage(Event event, MessageProperties messageProperties) {
        Object instance = ctx.getBean(subscriber.getInstanceClass());

        try {
            subscriber.getConsumeMethod().invoke(instance, event, messageProperties);
        } catch (IllegalAccessException e) {
            logger.error("Error handling event IllegalAccessException", e);
        } catch (InvocationTargetException e) {
            logger.error("Error handling event InvocationTargetException", e);
        } catch (Exception e){
            logger.error("Exception ", e);
        }
    }
}
