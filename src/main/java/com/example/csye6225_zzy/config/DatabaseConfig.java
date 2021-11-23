package com.example.csye6225_zzy.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.example.csye6225_zzy.mapper", sqlSessionFactoryRef = "sqlSessionFactory1")
public class DatabaseConfig {

    private static String MAPPER_PATH = "classpath*:/mybatis/mapper/*.xml";

    @Bean(name = "datasource1")
    @ConfigurationProperties("database1.datasource")
    @Primary
    public DataSource dataSource1(){
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "sqlSessionFactory1")
    @Primary
    public SqlSessionFactory sqlSessionFactoryBean(@Qualifier("datasource1") DataSource dataSource) throws Exception {

        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(MAPPER_PATH));
        return sqlSessionFactoryBean.getObject();
    }

    @Bean(name = "transactionManager1")
    @Primary
    public DataSourceTransactionManager transactionManager(@Qualifier("datasource1") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("sqlSessionTemplate1")
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory1") SqlSessionFactory sqlSessionFactory){
        return  new SqlSessionTemplate(sqlSessionFactory);
    }
}
