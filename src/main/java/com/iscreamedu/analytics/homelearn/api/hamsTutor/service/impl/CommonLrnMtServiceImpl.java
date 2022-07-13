package com.iscreamedu.analytics.homelearn.api.hamsTutor.service.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iscreamedu.analytics.homelearn.api.common.mapper.CommonMapperLrnDm;
import com.iscreamedu.analytics.homelearn.api.common.mapper.CommonMapperTutor;
import com.iscreamedu.analytics.homelearn.api.hamsTutor.service.CommonLrnMtService;

@Service
public class CommonLrnMtServiceImpl implements CommonLrnMtService{
	private static final Logger LOGGER = LoggerFactory.getLogger(CommonLrnMtServiceImpl.class);
    private static final String LRN_MT_NAMESPACE = "LrnMt";

    @Autowired
    CommonMapperLrnDm studLrnAnalMapper;

    @Override
    public Map getLrnMtData(Map<String, Object> paramMap) throws Exception {
    	LinkedHashMap<String,Object> result = new LinkedHashMap<>();
    	Map<String,Object> lrnMtParamMap = new HashMap<>();
    	
    	String period = paramMap.get("period").toString(); 
    	int studId = Integer.parseInt(paramMap.get("studId").toString());
    	
    	lrnMtParamMap.put("studId", studId);
    	lrnMtParamMap.put("period", period);
    	lrnMtParamMap.put("service", "ht");
    	
    	//기간별 파라미터 설정
    	if(period.equals("d")) {
    		int startDt = Integer.parseInt(paramMap.get("startDt").toString().replace("-", ""));
    		int endDt = Integer.parseInt(paramMap.get("endDt").toString().replace("-", ""));
    		
    		lrnMtParamMap.put("startDt", startDt);
    		lrnMtParamMap.put("endDt", endDt);
    	} else if (period.equals("m")) {
    		int yymm = (paramMap.get("yymm") != null) ? Integer.parseInt(paramMap.get("yymm").toString()) : Integer.parseInt(paramMap.get("endDt").toString().replace("-", "").substring(0,6));
    		int startDt = Integer.parseInt(paramMap.get("startDt").toString().replace("-", ""));
    		int endDt = Integer.parseInt(paramMap.get("endDt").toString().replace("-", ""));
    		
    		lrnMtParamMap.put("startDt", startDt);
    		lrnMtParamMap.put("endDt", endDt);
    		lrnMtParamMap.put("yymm", yymm);
    	} else {
    		int yymmwk = Integer.parseInt(paramMap.get("yymm").toString() + paramMap.get("wk").toString());
    		
    		lrnMtParamMap.put("yymmwk", yymmwk);
    	}
    	
    	//필요한 데이터 조회
    	List<String> sqlList = (List<String>) paramMap.get("sqlList");
    	for(String sqlListItem : sqlList) {
    		
    		try {
    			if(sqlListItem.contains("Log") || sqlListItem.contains("List")) {
    				
    				if(sqlListItem.equals("ExamLog")) {
    					lrnMtParamMap.put("types", String.join(",",(CharSequence[]) paramMap.get("types")));
    					
    					if(paramMap.get("search") != null) {
    						lrnMtParamMap.put("search", paramMap.get("search"));
    					}
    					
    					if(paramMap.get("startIdx") != null) {
    						lrnMtParamMap.put("startIdx", paramMap.get("startIdx"));
    					} else {
    						lrnMtParamMap.put("startIdx", 0);
    					}
    				}
    				
    				result.put(sqlListItem, studLrnAnalMapper.getList(lrnMtParamMap,LRN_MT_NAMESPACE + ".select" + sqlListItem));
    			} else {
    				result.put(sqlListItem, studLrnAnalMapper.get(lrnMtParamMap,LRN_MT_NAMESPACE + ".select" + sqlListItem));
    			}
    		} catch (Exception e) {
    			LOGGER.debug(sqlListItem + " : Error");			
			}
    		
    	}
    	
    	return result;
    }
}
