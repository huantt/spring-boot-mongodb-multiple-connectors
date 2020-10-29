package com.huantt.springbootmongodbmultipleconnectors.conf

import com.mongodb.MongoClientURI
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.mapping.model.SnakeCaseFieldNamingStrategy
import org.springframework.data.mongodb.MongoDbFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoDbFactory
import org.springframework.data.mongodb.core.convert.*
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@CompileStatic
@Configuration
class MultipleMongoConfig {

    @Value('${mongodb.first_db.uri}')
    private String firstDbUri
    @Value('${mongodb.second_db.uri}')
    private String secondDbUri
    @Value('${mongodb.third_db.uri}')
    private String thirdDbUri

    private MongoMappingContext mongoMappingContext
    {
        mongoMappingContext = new MongoMappingContext()
        this.mongoMappingContext.setFieldNamingStrategy(new SnakeCaseFieldNamingStrategy())
    }

    @Primary
    @Bean(name = "firstDbMongoTemplate")
    MongoTemplate firstDbMongoTemplate() throws Exception {
        MongoDbFactory dbFactory = new SimpleMongoDbFactory(new MongoClientURI(this.secondDbUri))
        return new MongoTemplate(dbFactory, this.getMongoConverter(dbFactory, this.mongoMappingContext))
    }

    @Bean(name = "secondDbMongoTemplate")
    MongoTemplate secondDbMongoTemplate() throws Exception {
        MongoDbFactory dbFactory = new SimpleMongoDbFactory(new MongoClientURI(this.firstDbUri))
        return new MongoTemplate(dbFactory, this.getMongoConverter(dbFactory, this.mongoMappingContext))
    }

    @Bean(name = "thirdDbMongoTemplate")
    MongoTemplate thirdDbMongoTemplate() throws Exception {
        MongoDbFactory dbFactory = new SimpleMongoDbFactory(new MongoClientURI(this.thirdDbUri))
        return new MongoTemplate(dbFactory, this.getMongoConverter(dbFactory, this.mongoMappingContext))
    }

    private MongoConverter getMongoConverter(MongoDbFactory dbFactory, MongoMappingContext mappingContext) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(dbFactory)
        MappingMongoConverter mongoConverter = new MappingMongoConverter(dbRefResolver, mappingContext)
        mongoConverter.setTypeMapper(new DefaultMongoTypeMapper(null))
        return mongoConverter
    }

    @Configuration
    @EnableMongoRepositories(basePackages = "com.huantt.springbootmongodbmultipleconnectors.repository.first", mongoTemplateRef = "firstDbMongoTemplate")
    class FirstDbMongoConfig {}

    @Configuration
    @EnableMongoRepositories(basePackages = "com.huantt.springbootmongodbmultipleconnectors.repository.second", mongoTemplateRef = "secondDbMongoTemplate")
    class SecondDbMongoConfig {}

    @Configuration
    @EnableMongoRepositories(basePackages = "com.huantt.springbootmongodbmultipleconnectors.repository.third", mongoTemplateRef = "thirdDbMongoTemplate")
    class ThirdDbMongoConfig {}

}