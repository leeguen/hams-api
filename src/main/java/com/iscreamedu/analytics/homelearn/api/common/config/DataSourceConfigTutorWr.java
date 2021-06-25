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

import javax.sql.DataSource;

@Configuration
public class DataSourceConfigTutorWr {

    //dataSource 생성
    @Bean(name = "tutorWrDataSource")
    @ConfigurationProperties("spring.datasource.hikari.tutorwrdb")
    public DataSource tutordbDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    //sqlSessionFactory 생성
    @Bean(name = "tutorwrdbSessionFactory")
    public SqlSessionFactory tutorWrSessionFactory(@Qualifier("tutorWrDataSource") DataSource tutordbDataSource, ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();

        factoryBean.setDataSource(tutordbDataSource);
        factoryBean.setTypeAliasesPackage("com.iscreamedu.analytics.homelearn.api.common.mapper");
        factoryBean.setConfigLocation(applicationContext.getResource("classpath:/sqlmap/sql-mapper-config.xml"));
        factoryBean.setMapperLocations(applicationContext.getResources("classpath:/sqlmap/mappers/*.xml"));
        return factoryBean.getObject();
    }
    //sqlSession 생성
    @Bean(name = "tutorwrdbSessionTemplate")
    public SqlSessionTemplate tutorWrSessionTemplate(SqlSessionFactory tutorWrSessionFactory) throws Exception {
        return new SqlSessionTemplate(tutorWrSessionFactory);
    }
}
