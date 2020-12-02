package vn.vnpt.stc.enterprise;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.stereotype.Component;
import vn.vnpt.stc.enterpise.commons.constants.QueueConstants;
import vn.vnpt.stc.enterpise.commons.constants.RoutingKeyConstants;
import vn.vnpt.stc.enterpise.commons.event.Event;
import vn.vnpt.stc.enterpise.commons.event.EventType;
import vn.vnpt.stc.enterprise.event.AMQPSubscribes;
import vn.vnpt.stc.enterprise.event.EventBus;
import vn.vnpt.stc.enterprise.event.process.MessageReceiverProcess;
import vn.vnpt.stc.enterprise.mongo.umgr.endpoints.MovieEndpoint;
import vn.vnpt.stc.enterprise.mongo.umgr.services.MovieService;

/**
 * Created by huyvv
 * Date: 22/04/2020
 * Time: 11:00 AM
 * for all issues, contact me: huyvv@vnpt-technology.vn
 **/
@Component
public class RootEndpoint {
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(RootEndpoint.class);
    private MessageReceiverProcess messageReceiverProcess;
    private MovieEndpoint movieEndpoint;

    public RootEndpoint(MessageReceiverProcess messageReceiverProcess,
                        EventBus eventBus,
                        MovieService movieService) {
        this.messageReceiverProcess = messageReceiverProcess;
        this.movieEndpoint = new MovieEndpoint(movieService, eventBus);
    }

    /**
     * FOR REQUEST EVENT
     */
    @AMQPSubscribes(exchange = QueueConstants.TOPIC_EXCHANGE_REQUEST, queue = QueueConstants.QUEUE_REQUEST_CORE_LOG,
            routingKey = RoutingKeyConstants.ROUTING_KEY_REQUEST_CORE_LOG, concurrency = 4)
    public void processExternal(Event event, MessageProperties messageProperties) {
        //only accept request event
        if (!EventType.REQUEST.equals(event.type)) return;

        switch (messageProperties.getReceivedRoutingKey()) {
            case RoutingKeyConstants.ROUTING_KEY_CORE_LOG:
                movieEndpoint.process(event);
                break;
            default:
                logger.info("hh");
                break;
                //do something else
        }
    }

    /**
     * FOR RESPONSE EVENT
     */
    @AMQPSubscribes(exchange = QueueConstants.TOPIC_EXCHANGE_RESPONSE, queue = QueueConstants.QUEUE_RESPONSE_CORE_LOG,
            routingKey = RoutingKeyConstants.ROUTING_KEY_RESPONSE_CORE_LOG, concurrency = 4)
    public void processInternal(Event event, MessageProperties messageProperties) {
        //only accept response event
        if (!EventType.RESPONSE.equals(event.type)) return;

        //free function call RequestUtils AMQP, for thread continue running
        messageReceiverProcess.process(event);
    }
}
