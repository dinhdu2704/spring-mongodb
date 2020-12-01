package vn.vnpt.stc.enterprise.event.amqp;


import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import vn.vnpt.stc.enterpise.commons.event.Event;

import java.util.HashMap;

public class JsonMessageConverter implements MessageConverter {

    private static final Logger logger = LoggerFactory.getLogger(JsonMessageConverter.class);

    private Gson gson;
    private Class<? extends Event> clazz;

    public JsonMessageConverter() {
        this.gson = new Gson();
    }

    public JsonMessageConverter(Class<? extends Event> clazz) {
        this.clazz = clazz;
        this.gson = new Gson();
    }

    @Override
    public Message toMessage(Object o, MessageProperties messageProperties) throws MessageConversionException {
        Message message = new Message(this.gson.toJson(o).getBytes(),messageProperties);
        return message;
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        if(clazz == null) {
            throw new UnsupportedOperationException("Class must be set");
        }
        HashMap<String, Object> incomingMessage = new HashMap<>();
        Object object = this.gson.fromJson(new String(message.getBody()),clazz);
        incomingMessage.put("bodyObject", object);
        incomingMessage.put("MessageProperties", message.getMessageProperties());

        return incomingMessage;
    }
}
