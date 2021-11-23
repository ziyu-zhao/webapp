package com.example.csye6225_zzy.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.example.csye6225_zzy.mapper_re", sqlSessionFactoryRef = "sqlSessionFactory2")
public class DatabaseConfig_re {

    private static String MAPPER_PATH = "classpath*:/mybatis/mapper_re/*.xml";

    @Value("${database2.datasource.url}")
    private String url;

    @Value("${database2.datasource.password}")
    private String password;

    @Value("${database2.datasource.username}")
    private String username;

    @Value("${database2.datasource.driver}")
    private String driverClass;

    @Bean(name = "datasource2")
    @ConfigurationProperties("database2.datasource")
    public DataSource dataSource1(){
        return DataSourceBuilder.create().
                url(url)
                .username(username)
                .password(password)
                .driverClassName(driverClass)
                .build();
    }

    @Bean(name = "sqlSessionFactory2")
    public SqlSessionFactory sqlSessionFactoryBean(@Qualifier("datasource2") DataSource dataSource) throws Exception {

        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(MAPPER_PATH));
        return sqlSessionFactoryBean.getObject();
    }

    @Bean(name = "transactionManager2")
    @Primary
    public DataSourceTransactionManager transactionManager(@Qualifier("datasource2") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("sqlSessionTemplate2")
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory2") SqlSessionFactory sqlSessionFactory){
        return  new SqlSessionTemplate(sqlSessionFactory);
    }
}
