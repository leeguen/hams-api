package com.iscreamedu.analytics.homelearn.api.common.mapper;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
@Repository //DAO CLASS 에서 쓰인다.
public class CommonMapperTutorWr extends SqlSessionDaoSupport {

    @Autowired
    @Qualifier("tutorwrdbSessionFactory")
    public void setSqlSessionFactory( SqlSessionFactory sqlSessionFactory){
        super.setSqlSessionFactory(sqlSessionFactory); //Mybatis 연동
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

