package vn.vnpt.stc.enterprise.event.cache.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import vn.vnpt.stc.enterprise.event.process.MessageProcess;

public class MessageSubscriber implements MessageListener {
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(MessageSubscriber.class);

    @Override
    public void onMessage(Message message, byte[] bytes) {
        String key = new String(message.getBody());
        String channel = new String(message.getChannel());
        if(channel.contains("set") && key.contains(MessageMap.REQUEST_DOMAIN)) {
            String requestId = "";
            String[] keyArray = key.split(":");
            for(String keyElement:keyArray) {
                if(keyElement.contains(MessageMap.REQUEST_DOMAIN)) requestId = keyElement.replaceAll(MessageMap.REQUEST_DOMAIN, "");
            }
            if(!"".equals(requestId)) {
                MessageProcess msgLocal = MessageFactory.getMessageLocal(requestId);
                if (msgLocal != null) {
                    MessageProcess msgProcess = MessageFactory.getMessage(requestId);
                    if (msgProcess != null && msgProcess.isIsFinished() == true) {
                        msgLocal.returnCode = msgProcess.getReturnCode();
                        msgLocal.errorCheck = msgProcess.errorCheck;
                        msgLocal.errorMessage = msgProcess.errorMessage;
                        msgLocal.setEvent(msgProcess.getEvent());
                        msgLocal.isFinished = true;
                        synchronized (msgLocal) {
                            msgLocal.notifyAll();
                        }
                    }
                }
            }
        }
    }
}
