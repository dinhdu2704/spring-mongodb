package vn.vnpt.stc.enterprise.event.amqp;

import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.MessageConverter;

import java.util.HashMap;

public class CustomMessageListenerAdapter extends MessageListenerAdapter {

    public CustomMessageListenerAdapter(Object delegate, MessageConverter messageConverter) {
        super(delegate, messageConverter);
    }


    @Override
    protected Object[] buildListenerArguments(Object extractedMessage) {
        HashMap<String, Object> incomingMessage = (HashMap<String, Object>) extractedMessage;
        return new Object[]{incomingMessage.get("bodyObject"), incomingMessage.get("MessageProperties")};
    }
}
