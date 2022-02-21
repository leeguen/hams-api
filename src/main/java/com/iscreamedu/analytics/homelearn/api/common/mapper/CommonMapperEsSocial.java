package com.iscreamedu.analytics.homelearn.api.common.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 * CommonMapperEsSocial 클래스
 * @author jhlim
 * @since 2022.01.18
 * @version 1.0
 * @see
 *  
 * <pre>
 * << 개정이력(Modification Information) >>
 * 
 *  수정일      		수정자		수정내용
 *  ----------  --------    ---------------------------
 *  2022.01.18  jhlim		최초 생성 
 *  </pre>
 */
@Repository //DAO CLASS 에서 쓰인다.
public class CommonMapperEsSocial extends SqlSessionDaoSupport {

	@Autowired
	@Qualifier("essocialdbSessionFactory")
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory){
		super.setSqlSessionFactory(sqlSessionFactory); //Mybatis 연동
	}
	
	/**
	 * 목록 조회
	 * @param param
	 * @param sqlId
	 * @return
	 */
	public List getList(Map<String, Object> param, String sqlId) {
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
	
	/**
	 * 수정
	 * @param param
	 * @param sqlId
	 * @return
	 */
	public int update(Map<String, Object> param, String sqlId) {
		return getSqlSession().update(sqlId, param);
	}
	
	/**
	 * 삭제
	 * @param param
	 * @param sqlId
	 * @return
	 */
	public int delete(Map<String, Object> param, String sqlId) {
		return getSqlSession().delete(sqlId, param);
	}
}
