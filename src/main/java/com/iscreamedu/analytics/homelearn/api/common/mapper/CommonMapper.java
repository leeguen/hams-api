package com.iscreamedu.analytics.homelearn.api.common.mapper;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;

/**
 * 공통 Mapper 클래스
 * @author hy
 * @since 2019.09.05
 * @version 1.0
 * @see
 *  
 * <pre>
 * << 개정이력(Modification Information) >>
 * 
 *  수정일      		수정자		수정내용
 *  ----------  --------    ---------------------------
 *  2019.09.05  hy        	최초 생성 
 *  </pre>
 */
@Repository
public class CommonMapper extends SqlSessionDaoSupport {
	
	@Resource
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory){
		super.setSqlSessionFactory(sqlSessionFactory);
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
