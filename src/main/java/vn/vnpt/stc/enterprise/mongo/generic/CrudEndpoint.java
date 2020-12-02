package vn.vnpt.stc.enterprise.mongo.generic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vnpt.stc.enterpise.commons.event.Event;
import vn.vnpt.stc.enterprise.event.EventBus;

/**
 * Created by huyvv
 * Date: 07/03/2020
 * Time: 9:50 AM
 * for all issues, contact me: huyvv@vnpt-technology.vn
 **/
public abstract class CrudEndpoint<T extends IdEntity, ID> {

    @SuppressWarnings("unused")
    private static Logger logger = LoggerFactory.getLogger(CrudEndpoint.class);

    protected CrudService<T, ID> service;
    protected EventBus eventBus;

    public CrudEndpoint(CrudService mgCrudService, EventBus eventBus){
        this.service = mgCrudService;
        this.eventBus = eventBus;
    }

    public void process(Event event){
        event = service.process(event);
        eventBus.publish(event.topicExchangeResponse, event.routingKeyResponse, event);
    }
}
