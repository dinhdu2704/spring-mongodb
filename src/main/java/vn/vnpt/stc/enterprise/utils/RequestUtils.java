package vn.vnpt.stc.enterprise.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vnpt.stc.enterpise.commons.constants.Constants;
import vn.vnpt.stc.enterpise.commons.constants.QueueConstants;
import vn.vnpt.stc.enterpise.commons.constants.RoutingKeyConstants;
import vn.vnpt.stc.enterpise.commons.event.Event;
import vn.vnpt.stc.enterpise.commons.event.EventType;
import vn.vnpt.stc.enterprise.event.process.MessageProcess;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;


/**
 * Created by huyvv
 * Date: 19/01/2020
 * Time: 9:06 PM
 * for all issues, contact me: huyvv@vnpt-technology.vn
 **/
public class RequestUtils {

    private static Logger logger = LoggerFactory.getLogger(RequestUtils.class);

    private RequestUtils() {
    }

    public static Event amqp(String exchange, String routingKey, Event event) {
        event.token = SecurityUtils.getCurrentUserJWT();
        event.from = Constants.FROM_SOUTHBOUND_ADAPTER;
        event.type = EventType.REQUEST;
        event.routingKeyResponse = RoutingKeyConstants.ROUTING_KEY_RESPONSE_SOUTHBOUND_ADAPTER;
        event.topicExchangeResponse = QueueConstants.TOPIC_EXCHANGE_RESPONSE;
        MessageProcess messageProcess = new MessageProcess();
        messageProcess.setRoutingKey(routingKey);
        messageProcess.setExchange(exchange);
        messageProcess.setEvent(event);
        try {
            messageProcess.process();
        } catch (Exception ex) {
            logger.error(ex.toString());
            return event;
        }
        return messageProcess.getEvent();
    }

    public static String postRest(String urlRequest,
                              String body,
                              String token,
                              String contentType,
                              String appId
    ){
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(urlRequest);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Type", contentType);
            conn.setRequestProperty("X-M2M-Origin", appId);
            conn.setRequestProperty("X-M2M-RI", UUID.randomUUID().toString());

            conn.setDoOutput(true);
            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = body.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            if (conn.getResponseCode() != 200 && conn.getResponseCode() != 201 && conn.getResponseCode() != 203) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            while ((output = br.readLine()) != null) {
                result.append(output);
            }
            conn.disconnect();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return result.toString();
    }

}
