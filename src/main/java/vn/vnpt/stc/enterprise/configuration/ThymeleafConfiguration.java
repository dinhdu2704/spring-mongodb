package vn.vnpt.stc.enterprise.configuration;

import org.apache.commons.lang3.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

/**
 * Created by huyvv
 * Date: 03/02/2020
 * Time: 2:48 PM
 * for all issues, contact me: huyvv@vnpt-technology.vn
 **/
@Configuration
public class ThymeleafConfiguration {

    @SuppressWarnings("unused")
    private final Logger log = LoggerFactory.getLogger(ThymeleafConfiguration.class);

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(templateResolver());
        return templateEngine;
    }

    @Bean
    public TemplateResolver templateResolver() {
        TemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("template/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setCharacterEncoding(CharEncoding.UTF_8);
        resolver.setOrder(1);
        resolver.setCacheable(false);
        return resolver;
    }
}
