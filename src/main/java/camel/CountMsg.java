package camel;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import java.sql.Timestamp;

public class CountMsg  implements Processor{
    private int msgCounter = 0;
    private int txtFileCounter = 0;
    private int xmlFileCounter = 0;
    private int illegalFileCounter = 0;
    private long msgTime = 0;

    public void process(Exchange exchange) throws Exception {
        msgCounter++;
        Message message = exchange.getIn();

        if (msgCounter == 1) {
            Timestamp begin = new Timestamp(System.currentTimeMillis());
            msgTime = begin.getTime();
        }

        String fileName = message.getHeader("CamelFileName").toString();
        if (fileName.endsWith(".txt")) {
            txtFileCounter++;
        } else if (fileName.endsWith(".xml")) {
            xmlFileCounter++;
        } else {
            illegalFileCounter++;
        }

        if (msgCounter % 100 == 0) {
            message.setHeader("TotalMsgCounter", msgCounter);
            message.setHeader("TxtFileCount", txtFileCounter);
            message.setHeader("XmlFileCount", xmlFileCounter);
            message.setHeader("IllegalFileCount", illegalFileCounter);
            message.setHeader("ProcessingTime", setTimestamp());
            message.setHeader("StatMsg", true);
        }
    }

    public long setTimestamp() {
            Timestamp end = new Timestamp(System.currentTimeMillis());
        return end.getTime() - msgTime;
    }
}
