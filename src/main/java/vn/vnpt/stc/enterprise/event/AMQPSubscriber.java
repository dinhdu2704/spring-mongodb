package vn.vnpt.stc.enterprise.event;

import vn.vnpt.stc.enterpise.commons.event.Event;

import java.lang.reflect.Method;

public class AMQPSubscriber {
    private String queue;
    private String routingKey;
    private String exchange;
    private int concurrency;
    private Method consumeMethod;
    private Class<?> instanceClass;
    private Class<? extends Event> eventType;

    public AMQPSubscriber(String queue, String routingKey, String exchange, Class<?> declaringClass, Method m, Class<? extends Event> eventType, int concurrency) {
        this.queue = queue;
        this.routingKey = routingKey;
        this.concurrency = concurrency;
        this.consumeMethod = m;
        this.instanceClass = declaringClass;
        this.eventType = eventType;
        this.exchange = exchange;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public String getExchange(){
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public int getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    public Method getConsumeMethod() {
        return consumeMethod;
    }

    public void setConsumeMethod(Method consumeMethod) {
        this.consumeMethod = consumeMethod;
    }

    public Class<?> getInstanceClass() {
        return instanceClass;
    }

    public void setInstanceClass(Class<?> instanceClass) {
        this.instanceClass = instanceClass;
    }

    public Class<? extends Event> getEventType() {
        return eventType;
    }

    public void setEventType(Class<? extends Event> eventType) {
        this.eventType = eventType;
    }
}
