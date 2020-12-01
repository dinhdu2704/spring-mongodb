package vn.vnpt.stc.enterprise.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static vn.vnpt.stc.enterpise.commons.constants.QueueConstants.TOPIC_EXCHANGE_NAME_DEFAULT;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AMQPSubscribes {
    public String queue() default "";
    public String routingKey() default "";
    public int concurrency() default 1;
    public String exchange() default TOPIC_EXCHANGE_NAME_DEFAULT;
}
