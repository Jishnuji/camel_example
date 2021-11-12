package camel;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultMessage;


public class StatMsg implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        Message inputMsg = exchange.getIn();
        DefaultMessage message = new DefaultMessage();
        message.setHeaders(inputMsg.getHeaders());

        String processingTime = inputMsg.getHeader("ProcessingTime").toString();
        String txtFileCount = inputMsg.getHeader("TxtFileCount").toString();
        String xmlFileCount = inputMsg.getHeader("XmlFileCount").toString();
        String illegalFileCount = inputMsg.getHeader("IllegalFileCount").toString();
        String msgCounter = inputMsg.getHeader("TotalMsgCounter").toString();

        message.setBody("Number of all files: " + msgCounter +  "\n" + "Processing time (ms): " + processingTime + "\n"
                        + "Number of text files: " + txtFileCount + "\n"
                        + "Number of xml files: " + xmlFileCount + "\n" + "Number of other files: " + illegalFileCount);
        exchange.setIn(message);
    }
}
