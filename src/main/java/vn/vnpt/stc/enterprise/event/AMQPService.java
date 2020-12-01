package vn.vnpt.stc.enterprise.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import vn.vnpt.stc.enterprise.event.amqp.AMQPEventBus;
import vn.vnpt.stc.enterprise.event.amqp.AnnotationProcessor;

import java.util.*;

@Component
@DependsOn({"springContext"})
//Add @DependsOn({"springContext"}) to sure SpringContext loading before this commpent => fix error: can not run jar was build in server
public class AMQPService {
    private static final Logger logger = LoggerFactory.getLogger(AMQPService.class);
    private Set<String> listeners = new HashSet<String>();
    private ApplicationContext ctx;
    private Map<String, AMQPEventBus> eventBuses = new HashMap<String, AMQPEventBus>();

    @Autowired
    public AMQPService(ApplicationContext ctx) {
        this.ctx = ctx;
        doInitialize();
    }

    public static void initialize(ApplicationContext ctx) {
        AMQPService service = ctx.getBean(AMQPService.class);
        service.doInitialize();
    }

    private synchronized void doInitialize() {
        logger.info("Initializing ampq listeners ....");
        List<AMQPSubscriber> subscribers = AnnotationProcessor.findSubscribers();
        EventBus eventBus = ctx.getBean(EventBus.class);
        for(AMQPSubscriber subscriber : subscribers) {
            String listenKey = subscriber.getInstanceClass().getSimpleName() + "_" + subscriber.getConsumeMethod().getName();
            if(!listeners.contains(listenKey)) {
                logger.info("Registering handler for routing key: {}, queue: {}", subscriber.getRoutingKey(), subscriber.getQueue());
                eventBus.registerSubscriber(subscriber);
                listeners.add(listenKey);
            }
        }
        logger.info("Registered {} amqp listeners", listeners.size());
    }
}
