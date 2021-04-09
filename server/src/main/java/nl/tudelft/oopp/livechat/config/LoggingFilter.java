package nl.tudelft.oopp.livechat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;

/**
 * Logging Filter configuration.
 * By default it reads the shouldLog variable in application.properties and
 * if that is true it ignores logging for requests to fetchPoll endpoints
 */
@Component
public class LoggingFilter extends AbstractRequestLoggingFilter {

    @Value("${shouldLog}")
    private boolean shouldLog;

    /**
     * Instantiates a new Logging filter.
     */
    public LoggingFilter() {
        setIncludeClientInfo(true);
        setIncludeHeaders(true);
        setIncludePayload(true);
        setIncludeQueryString(true);
        setBeforeMessagePrefix("Request started => ");
        setAfterMessagePrefix("Request ended => ");
    }

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        if (request.getServletPath().contains("/api/poll/fetch")) {
            return false;
        }
        return shouldLog;
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        logger.info(message);
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        logger.info(message);
    }
}