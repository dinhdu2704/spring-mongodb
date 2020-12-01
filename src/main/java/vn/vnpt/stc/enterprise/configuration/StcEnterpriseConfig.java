package vn.vnpt.stc.enterprise.configuration;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import vn.vnpt.stc.enterprise.event.EventBus;
import vn.vnpt.stc.enterprise.event.amqp.AMQPEventBus;
import vn.vnpt.stc.enterprise.event.cache.ObjectCache;
import vn.vnpt.stc.enterprise.event.cache.RedisCache;
import vn.vnpt.stc.enterprise.event.cache.cmd.MessageSubscriber;

@Configuration
@EnableAsync
@EnableAspectJAutoProxy
public class StcEnterpriseConfig {

    /* ========================= RestTemplate ============================ */
    @Bean
    public RestTemplate restTemplate() { return new RestTemplate(); }

    /* ========================= RABBIT MQ ============================ */
    @Bean
    @ConfigurationProperties(prefix = "spring.rabbitmq")
    public CachingConnectionFactory connectionFactory() {
        return new CachingConnectionFactory();
    }

    @Bean
    public EventBus eventBus(CachingConnectionFactory connectionFactory, ApplicationContext ctx) {
        return new AMQPEventBus(connectionFactory, ctx);
    }

    /* ========================= Redis ============================ */
    @Bean
    @ConfigurationProperties(prefix = "spring.redis")
    public RedisConnectionFactory redisConnectionFactory() {
        return new JedisConnectionFactory();
    }

//     sentinel config
//    @Bean
//    public RedisSentinelConfiguration getSentinelConfig() {
//        RedisSentinelConfiguration configuration = new RedisSentinelConfiguration();
//        String master = env.getProperty("spring.redis.sentinel.master");
//        String nodes = env.getProperty("spring.redis.sentinel.nodes");
//        configuration.master(master);
//        for (String node : nodes.split(",")) {
//            String split[] = node.split(":");
//            configuration.sentinel(split[0].trim(), Integer.parseInt(split[1].trim()));
//        }
//        return configuration;
//    }
//
//    @Bean
//    @ConfigurationProperties(prefix = "spring.redis")
//    public RedisConnectionFactory redisConnectionFactory(RedisSentinelConfiguration redisSentinelConfiguration) {
//        return new JedisConnectionFactory(redisSentinelConfiguration);
//    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @Bean
    public ObjectCache systemCache(RedisTemplate redisTemplate) {
        return new RedisCache(redisTemplate);
    }

    @Bean
    MessageListenerAdapter messageListener() {
        return new MessageListenerAdapter(new MessageSubscriber());
    }

    @Bean
    RedisMessageListenerContainer redisContainer() {
        RedisMessageListenerContainer container
                = new RedisMessageListenerContainer();
//        container.setConnectionFactory(redisConnectionFactory(getSentinelConfig()));
        container.setConnectionFactory(redisConnectionFactory());
        container.addMessageListener(messageListener(), new PatternTopic("__keyevent@*__:set"));
        return container;
    }
}
