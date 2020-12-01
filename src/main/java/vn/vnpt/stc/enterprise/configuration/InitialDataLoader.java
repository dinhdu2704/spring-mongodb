package vn.vnpt.stc.enterprise.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

/**
 * Created by huyvv
 * Date: 05/02/2020
 * Time: 2:17 PM
 * for all issues, contact me: huyvv@vnpt-technology.vn
 **/
@Component
@Transactional
public class InitialDataLoader implements
        ApplicationListener<ContextRefreshedEvent> {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(InitialDataLoader.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

    }
}
