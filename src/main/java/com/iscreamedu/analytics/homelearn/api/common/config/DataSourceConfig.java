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
public class DataSourceConfig {

    //dataSource 생성
    @Primary
    @Bean(name = "salesDatasource")
    @ConfigurationProperties("spring.datasource.hikari.salesdb")
    public DataSource salesdbDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    //sqlSessionFactory 생성
    @Primary
    @Bean(name = "salesdbSessionFactory")
    public SqlSessionFactory salesSessionFactory(@Qualifier("salesDatasource") DataSource salesdbDataSource, ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();

        factoryBean.setDataSource(salesdbDataSource);
        factoryBean.setTypeAliasesPackage("com.iscreamedu.analytics.homelearn.api.common.mapper");
        factoryBean.setMapperLocations(applicationContext.getResources("classpath:/sqlmap/mappers/*.xml"));
        return factoryBean.getObject();
    }
    //sqlSession 생성
    @Primary
    @Bean(name = "salesdbSessionTemplate")
    public SqlSessionTemplate salesSessionTemplate(SqlSessionFactory salesSessionFactory) throws Exception {
        return new SqlSessionTemplate(salesSessionFactory);
    }
}
