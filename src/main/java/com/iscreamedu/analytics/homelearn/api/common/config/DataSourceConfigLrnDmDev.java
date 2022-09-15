package com.iscreamedu.analytics.homelearn.api.common.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfigLrnDmDev {

    //dataSource 생성
    @Bean(name = "lrnDmDevDatasource")
    @ConfigurationProperties("spring.datasource.hikari.lrndmtdevdb")
    public DataSource martdbDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    //sqlSessionFactory 생성
    @Bean(name = "lrndmdevdbSessionFactory")
    public SqlSessionFactory martdbSessionFactory(@Qualifier("lrnDmDevDatasource") DataSource martdbDataSource, ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();

        factoryBean.setDataSource(martdbDataSource);
        factoryBean.setTypeAliasesPackage("com.iscreamedu.analytics.homelearn.api.common.mapper");
        factoryBean.setConfigLocation(applicationContext.getResource("classpath:/sqlmap/sql-mapper-config.xml"));
        factoryBean.setMapperLocations(applicationContext.getResources("classpath:/sqlmap/mappers/*.xml"));
        return factoryBean.getObject();
    }
    //sqlSession 생성
    @Bean(name = "lrndmdevdbSessionTemplate")
    public SqlSessionTemplate martdbSessionTemplate(SqlSessionFactory lrndmdevdbSessionFactory) throws Exception {
        return new SqlSessionTemplate(lrndmdevdbSessionFactory);
    }
}
