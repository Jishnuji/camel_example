package camel;

import org.apache.camel.builder.RouteBuilder;

public class FileRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        errorHandler(deadLetterChannel("activemq:invalid.queue").onExceptionOccurred(new OnErrorLogger()));
        from("file:{{from}}")
                        .transacted()
                        .choice()
                        .when(header("CamelFileName").endsWith(".xml"))
                            .to("activemq:xml.queue")
                        .when(header("CamelFileName").endsWith(".txt"))
                            .to("activemq:txt.queue")
                            .setBody(simple("'${body}'"))
                            .to("sql:{{query}}?dataSource=#xaDataSource")
//                Решение через jdbc
//                            .setBody(simple("INSERT INTO file_message (file_name, file_body) VALUES('${header.CamelFileName}','${body}')"))
//                            .to("jdbc:xaDataSource");
                        .otherwise()
                            .to("log:?level=ERROR&showBody=true&showHeaders=true")
                            .process(new IllegalFileFormatExceptionThrower())

                        .end()
                        .process(new CountMsg())
                        .filter(header("StatMsg").isEqualTo(true))
                            .process(new StatMsg())
                            .setHeader("subject", simple("Files statistic message"))
                            .to("{{email.uri}}");
    }
}
