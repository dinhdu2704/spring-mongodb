//package vn.vnpt.stc.enterprise.configuration;
//
//import com.mongodb.Mongo;
//import com.mongodb.MongoClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//import org.springframework.data.mongodb.MongoDbFactory;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
//import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
//import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
//import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
//
//@Configuration
//public class SpringMongoConfig {
//    @Autowired
//    private Environment env;
//
//    @Bean
//    public MongoDbFactory mongoDbFactory() throws Exception {
//        return new SimpleMongoDbFactory(new MongoClient(), env.getProperty("spring.data.mongodb.database"));
//    }
//
//    @Bean
//    public MongoTemplate mongoTemplate() throws Exception {
//
//        //remove _class
//        MappingMongoConverter converter =
//                new MappingMongoConverter(mongoDbFactory(), new MongoMappingContext());
//        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
//        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory(), converter);
//        return mongoTemplate;
//    }
//}
