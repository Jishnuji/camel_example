package camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class IllegalFileFormatExceptionThrower implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        throw new Exception("illegal file format");
    }
}
