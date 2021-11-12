package camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyExceptionThrower implements Processor {
    Logger LOG = LoggerFactory.getLogger(MyExceptionThrower.class);

    @Override
    public void process(Exchange exchange) {
        try {
            throw new Exception("Illegal file format");
        } catch (Exception e) {
            LOG.error("Some error occurred: " + e);
        }
    }
}
