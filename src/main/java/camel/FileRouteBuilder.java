package camel;

import org.apache.camel.builder.RouteBuilder;

public class FileRouteBuilder extends RouteBuilder {
    private final static String EMAIL_URI = "smtps://smtp.gmail.com:465?username=jmsocialproject@gmail.com&password=pynkbdswxcvzgwvj";

    @Override
    public void configure() throws Exception {
        from("file:files/from?noop=true")
                        .transacted()
                        .choice()
                        .when(header("CamelFileName").endsWith(".xml"))
                            .to("activemq:xml.queue")
                        .when(header("CamelFileName").endsWith(".txt"))
                            .to("activemq:txt.queue")
                            .setBody(simple("'${body}'"))
                            .to("sql:insert into file_message values(:#CamelFileName, :#${body})?dataSource=#xaDataSource")
//                            .setBody(simple("INSERT INTO file_message (file_name, file_body) VALUES('${header.CamelFileName}','${body}')"))
//                            .to("jdbc:xaDataSource");
                        .otherwise()
                            .process(new MyExceptionThrower())
                            .to("activemq:invalid.queue")
                        .end()
                        .process(new CountMsg())
                        .choice()
                        .when(header("StatMsg").isEqualTo(true))
                            .process(new StatMsg())
                            .setHeader("subject", simple("Files statistic message"))
                            .to(EMAIL_URI);
    }
}
