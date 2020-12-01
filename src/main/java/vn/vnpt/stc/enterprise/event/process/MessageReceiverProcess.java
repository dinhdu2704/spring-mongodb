package vn.vnpt.stc.enterprise.event.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import vn.vnpt.stc.enterpise.commons.constants.QueueConstants.ResultStatus;
import vn.vnpt.stc.enterpise.commons.event.Event;
import vn.vnpt.stc.enterprise.event.cache.cmd.MessageFactory;

@Component
public class MessageReceiverProcess {
    private final Logger logger = LoggerFactory.getLogger(MessageReceiverProcess.class);

    public void process(Event event){
        String requestId = event.id;

        MessageProcess message = MessageFactory.getMessage(requestId);
        if (message != null) {
            message.setResponseMsg("OK");
            logger.info("ResponseMsg: #{}", message.getResponseMsg());
            if (ResultStatus.SUCCESS == event.errorCode) {
                message.receiveResult(event);
            } else {
                message.receiveError("1", "receiveError", event);
            }
        } else {
            logger.warn("Not found MessageProcess with #{}", requestId);
        }
    }
}
