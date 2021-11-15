package camel;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.Configuration;
import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import bitronix.tm.resource.jms.PoolingConnectionFactory;
import org.apache.camel.*;

import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.springframework.transaction.jta.JtaTransactionManager;

import java.util.Properties;

public class Main {
    public static void main(String[] args) throws Exception {
        Configuration BTMconfig = TransactionManagerServices.getConfiguration();
        BTMconfig.setServerId("spring-btm");
        BitronixTransactionManager BitronixTM =  TransactionManagerServices.getTransactionManager();
        JtaTransactionManager JTAtm = new JtaTransactionManager();
        JTAtm.setUserTransaction(BitronixTM);
        JTAtm.setTransactionManager(BitronixTM);

        PoolingDataSource xaDataSource = new PoolingDataSource();

        xaDataSource.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");
        xaDataSource.setUniqueName("xaDataSource");
        xaDataSource.setMinPoolSize(1);
        xaDataSource.setMaxPoolSize(5);
        xaDataSource.setAllowLocalTransactions(true);
        xaDataSource.setAutomaticEnlistingEnabled(false);

        Properties xaDataSourceProp = new Properties();
        xaDataSourceProp.put("user", "postgres");
        xaDataSourceProp.put("password", "root");
        xaDataSourceProp.put("url", "jdbc:postgresql://localhost/mydb?currentSchema=public");
        xaDataSourceProp.put("driverClassName", "org.postgresql.Driver");

        xaDataSource.setDriverProperties(xaDataSourceProp);
        xaDataSource.init();

//        ----------

        PoolingConnectionFactory myConnectionFactory = new PoolingConnectionFactory ();

        myConnectionFactory.setClassName("org.apache.activemq.ActiveMQXAConnectionFactory");
        myConnectionFactory.setUniqueName("activemq");
        myConnectionFactory.setMaxPoolSize(5);
        myConnectionFactory.setAllowLocalTransactions(true);
        myConnectionFactory.getDriverProperties().setProperty("brokerURL", "tcp://localhost:61616");
        myConnectionFactory.init();


        SimpleRegistry registry = new SimpleRegistry();
        registry.put("transactionManager", JTAtm);
        registry.put("xaDataSource", xaDataSource);
        registry.put("activemq", myConnectionFactory);

//        ----------

        CamelContext context = new DefaultCamelContext(registry);
        context.getComponent("properties", PropertiesComponent.class).setLocation("classpath:application.properties");
        FileRouteBuilder fileRouteBuilder = new FileRouteBuilder();
        context.addRoutes(fileRouteBuilder);

        context.start();
        Thread.sleep(2000);
        context.stop();
        myConnectionFactory.close();

    }
}
