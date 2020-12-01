package vn.vnpt.stc.enterprise;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import vn.vnpt.stc.enterprise.configuration.ApplicationProperties;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class })
@EnableScheduling
@EnableConfigurationProperties({ApplicationProperties.class})
public class StcEnterpriseCoreLog {
    private static Logger logger = LoggerFactory.getLogger(StcEnterpriseCoreLog.class);

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        logger.info("#{} have just started up", StcEnterpriseCoreLog.class.getSimpleName());
    }
    public static void main(String[] args) {
        SpringApplication.run(StcEnterpriseCoreLog.class, args);
    }

}
