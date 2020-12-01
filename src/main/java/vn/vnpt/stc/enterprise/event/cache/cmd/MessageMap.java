/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.vnpt.stc.enterprise.event.cache.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vnpt.stc.enterprise.configuration.SpringContext;
import vn.vnpt.stc.enterprise.event.cache.ObjectCache;
import vn.vnpt.stc.enterprise.event.process.MessageProcess;

public class MessageMap {

    private static final Logger logger = LoggerFactory.getLogger(MessageMap.class);
    public static final String REQUEST_DOMAIN = "REQUEST_CMD_";
    ObjectCache objectCache;

    MessageMap() {
        try {
            objectCache = SpringContext.getBean(ObjectCache.class);
        } catch (Exception ex) {
            logger.error("ERROR when init MessageMap: ", ex);
        }
    }

    public MessageProcess get(String key) {
        Object messageSaved = objectCache.get(createKey(key), MessageProcess.class);
        if (messageSaved == null) {
            return null;
        }
        if (messageSaved instanceof MessageProcess == false) {
            logger.warn("weird data of key: " + key);
        }
        return (MessageProcess) messageSaved;
    }

    public MessageProcess put(String key, MessageProcess value, int expireTime) {
        objectCache.put(createKey(key), value, expireTime, MessageProcess.class);
        Object messageSaved = objectCache.get(createKey(key), MessageProcess.class);
        if (messageSaved == null) {
            logger.warn("Put MessageProcess fail with key : " + key);
            return null;
        }
        if (messageSaved instanceof MessageProcess == false) {
            logger.warn("weird data of key: " + key);
            return null;
        }
        return value;
    }

    public void save(String key, MessageProcess value, int expireTime) {
        if (value == null) {
            return;
        }
        objectCache.put(createKey(key), value, expireTime, MessageProcess.class);
    }

    public MessageProcess remove(String key) {
        Object messageSaved = objectCache.get(createKey(key), MessageProcess.class);
        if (messageSaved == null) {
            return null;
        }
        if (messageSaved instanceof MessageProcess == false) {
            logger.warn("weird data of key: " + key);
            return null;
        }
        MessageProcess message = (MessageProcess) messageSaved;
        objectCache.remove(createKey(key), MessageProcess.class);
        return message;
    }

    public static String createKey(String key) {
        return REQUEST_DOMAIN + key;
    }
}
