package camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnErrorLogger implements Processor {
    Logger LOG = LoggerFactory.getLogger(OnErrorLogger.class);
    @Override
    public void process(Exchange exchange) throws Exception {
        String failure = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class).getMessage();
        exchange.getIn().setHeader("FailedBecause", failure);
        LOG.error("Some error occurred: " + failure);
    }
}