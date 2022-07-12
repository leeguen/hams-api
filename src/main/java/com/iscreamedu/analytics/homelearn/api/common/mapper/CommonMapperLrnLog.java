package com.iscreamedu.analytics.homelearn.api.common.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class CommonMapperLrnLog extends SqlSessionDaoSupport {

	@Autowired
	@Qualifier("lrnlogdbSessionFactory")
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory){
		super.setSqlSessionFactory(sqlSessionFactory); //Mybatis 연동
	}
	
	/**
	 * 목록 조회
	 * @param param
	 * @param sqlId
	 * @return
	 */
	public List<Object> getList(Map<String, Object> param, String sqlId) {
        return getSqlSession().selectList(sqlId, param);
	}
    
	/**
	 * 단건 조회
	 * @param param
	 * @param sqlId
	 * @return
	 */
	public Object get(Map<String, Object> param, String sqlId) {
        return getSqlSession().selectOne(sqlId, param);
	}
	
	/**
	 * 등록
	 * @param param
	 * @param sqlId
	 * @return
	 */
	public int insert(Map<String, Object> param, String sqlId) {
		return getSqlSession().insert(sqlId, param);
	}


}
