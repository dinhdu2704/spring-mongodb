package vn.vnpt.stc.enterprise.event.cache.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vnpt.stc.enterprise.event.process.MessageProcess;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by HIEUDT on 12/12/2019.
 */
public class MessageFactory {

    private static final Logger logger = LoggerFactory.getLogger(MessageFactory.class);

    private static final MessageMap cacheMessage = new MessageMap();
    private static final Map<String, MessageProcess> localMessage = new ConcurrentHashMap();

    public static void init(){
    }
    public static boolean removeMessage(MessageProcess mp) {
        if (mp == null) {
            return false;
        }

        logger.info("remove message with key: " + mp.getRequestId());

        localMessage.remove(mp.getRequestId());
        MessageProcess result = cacheMessage.remove(mp.getRequestId());
        if (result == null) {
            logger.warn("removeMessage fail with key: " + mp.getRequestId());
        }
        return false;

    }

    public static void addMessage(MessageProcess mp) throws Exception {
        if (mp == null) {
            throw new Exception("Message is NULL !");
        } else if (mp.getRequestId() == null) {
            throw new Exception("Message with NULL RequestId");
        }
        localMessage.put(mp.getRequestId(), mp);
        MessageProcess result = cacheMessage.put(mp.getRequestId(), mp, mp.getTimeout() / 1000);
        if (result == null) {
            throw new Exception("Cannot put message");
        }
    }

    public static void saveMessage(MessageProcess mp) {
        if (mp == null || (mp.getRequestId() == null)) {
            return;
        }
        cacheMessage.save(mp.getRequestId(), mp, mp.getTimeout() / 1000);
    }

    public static MessageProcess getMessage(String requestId) {
        if (requestId == null) {
            return null;
        }
        MessageProcess message = cacheMessage.get(requestId);
        return message;
    }

    public static MessageProcess getMessageLocal(String requestId) {
        if (requestId == null) {
            return null;
        }
        Object message = localMessage.get(requestId);
        if (message instanceof MessageProcess == false) {
            return null;
        }
        return (MessageProcess) message;
    }

    public static boolean localContain(String requestId) {
        if (requestId == null) {
            return false;
        }
        return localMessage.containsKey(requestId);
    }

    public static void clearAllKey() {
        try {
            logger.info("clearAllKey ...");
            for (Map.Entry<String, MessageProcess> entry : localMessage.entrySet()) {
                removeMessage(entry.getValue());
            }
        } catch (Exception ex) {
            logger.info("clearAllKey got exception:", ex);
        }
    }
}
