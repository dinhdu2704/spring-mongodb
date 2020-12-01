package vn.vnpt.stc.enterprise.event.process;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vnpt.stc.enterpise.commons.constants.QueueConstants;
import vn.vnpt.stc.enterpise.commons.errors.ErrorInfo;
import vn.vnpt.stc.enterpise.commons.errors.ErrorKey;
import vn.vnpt.stc.enterpise.commons.event.Event;
import vn.vnpt.stc.enterpise.commons.utils.ObjectMapperUtil;
import vn.vnpt.stc.enterprise.configuration.SpringContext;
import vn.vnpt.stc.enterprise.event.EventBus;
import vn.vnpt.stc.enterprise.event.cache.cmd.MessageFactory;

import java.io.Serializable;

public class MessageProcess implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(MessageProcess.class);
    protected int timeout;
    public volatile boolean isFinished = false;
    public volatile boolean errorCheck = true;
    public volatile Integer returnCode;
    public volatile String errorMessage;
    protected String responseMsg;
    protected String requestId;
    protected String routingKey;

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    protected String exchange;
    protected Event event;

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getTimeout() {
        return timeout;
    }

    public boolean isIsFinished() {
        return isFinished;
    }

    public Integer getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(Integer returnCode) {
        this.returnCode = returnCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void process() throws Exception {
        long startTime = System.currentTimeMillis();
        synchronized (this) {
            try {
                preExecute();
                sendRequest();
                this.wait(timeout);
                if (System.currentTimeMillis() - startTime > timeout) {
                    logger.error("requestId= #{}, Timeout: REQUEST_TIMEOUT= #{} (ms)", requestId, timeout);
                    ErrorInfo errorInfo = new ErrorInfo(ErrorKey.CommonErrorKey.TIME_OUT_REQUEST);
                    receiveError("Timeout", ObjectMapperUtil.toJsonString(errorInfo));
                } else {
                    logger.info("requestId= #{}, Success. Execute time: #{}(ms)", requestId, (System.currentTimeMillis() - startTime));
                    postProcessResult();
                }
            } catch (Exception ex) {
                receiveError(ex.getMessage(), "{}");
                logger.error("requestId=#{}, Error: #{}", requestId, ex.getMessage());
                throw ex;
            } finally {
                MessageFactory.removeMessage(this);
            }
        }
    }

    protected void preExecute() {
        logger.debug("preExecute");
        if (timeout <= 0) {
            timeout = QueueConstants.TIME_OUT_ASYNCHRONOUS_REQUEST;
        }
    }

    private void postSendRequest() {
        try {
            this.isFinished = false;
            MessageFactory.addMessage(this);
            logger.info("Waiting for request: #{}, timeout: #{} ms", requestId, timeout);
        } catch (Exception ex) {
            logger.error("ERROR postSendRequest: ", ex);
        }
    }
    public void sendRequest() throws Exception{
        EventBus bus = SpringContext.getBean(EventBus.class);
        Event event = bus.publish(exchange, routingKey, this.event);
        this.setRequestId(event.id);
        postSendRequest();
    };

    protected void postProcessResult() {
        logger.debug("postProcessResult");
    }

    public synchronized void receiveError(String errorString, String payload) {
        isFinished = true;
        errorCheck = true;
        returnCode = -1;
        event.errorCode = returnCode;
        event.errorMsg = errorString;
        event.payload = payload;
        this.errorMessage = errorString;
        MessageFactory.saveMessage(this);
        logger.warn("receiveError: #{}", errorString);
    }

    public synchronized void receiveError(String code, String errorString, Event event) {
        isFinished = true;
        errorCheck = true;
        try {
            returnCode = Integer.parseInt(code);
            this.event = event;
        } catch (NumberFormatException ne) {
            returnCode = -1;
            logger.error("ERROR NumberFormatException  " + code + ": ", ne);
        }
        this.errorMessage = errorString;
        MessageFactory.saveMessage(this);
        logger.warn("receiveError: #{}", errorString);
    }

    public synchronized void receiveResult(Event event) {
        this.event = event;
        returnCode = 0;
        isFinished = true;
        errorCheck = false;
        MessageFactory.saveMessage(this);
        logger.info("receiveResult: #{}", getResponseMsg());
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
