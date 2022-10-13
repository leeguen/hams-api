package com.iscreamedu.analytics.homelearn.api.student.service.impl;

import com.iscreamedu.analytics.homelearn.api.common.security.CipherUtil;
import com.iscreamedu.analytics.homelearn.api.common.service.ExternalAPIService;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationCode;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtil;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtilTutor;
import com.iscreamedu.analytics.homelearn.api.student.service.StudHomeLogService;
import com.iscreamedu.analytics.homelearn.api.student.service.StudLrnLogService;
import com.iscreamedu.analytics.homelearn.api.student.service.StudLrnTypeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iscreamedu.analytics.homelearn.api.common.exception.NoDataException;
import com.iscreamedu.analytics.homelearn.api.common.mapper.CommonMapperLrnDm;
import com.iscreamedu.analytics.homelearn.api.common.mapper.CommonMapperLrnType;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class StudLrnHomeLogServiceImpl implements StudHomeLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudLrnHomeLogServiceImpl.class);

    private LinkedHashMap<String, Object> result;
    private LinkedHashMap<String, Object> decodeResult;
    private String msgKey = "msg";
    private String dataKey = "data";

    private static final String LRN_MT_NAMESPACE = "LrnMt";

    @Autowired
    CommonMapperLrnDm studLrnAnalMapper;
    
    @Override
    public Map getHlogList(Map<String,Object> paramMap) throws Exception {
        Map<String,Object> data = new LinkedHashMap<>();
        
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        getStudId(paramMap);
        
        vu.checkRequired(new String[] {"studId"}, paramMap);
        if(vu.isValid()) {
        	 /*상장 목록 수 조회*/
            
            /*상장 목록 수 조회*/
        	
        	data.put("totalCnt", 100);
            data.put("pageCnt", 10);
            data.put("currPage", 1);
            
            ArrayList<Map<String, Object>> homelogList = new ArrayList<>();
            Map<String, Object> homelogMap = new LinkedHashMap<>();
            homelogMap.put("cd", 22100001);
            homelogMap.put("grp", "선생님 상");
            homelogMap.put("grpCd", "M");
            homelogMap.put("name", "성실 만렙 상");
            homelogMap.put("property", "목표");
            homelogMap.put("cont", "특정 기간 동안 공부 열심히 하기");
            homelogMap.put("tchrName", "아이뚜루 (admturu)");
            homelogMap.put("status", "수여완료");
            homelogMap.put("regDttm", "2022-10-06 10:30:00");
            
            Map<String, Object> homelogMap1 = new LinkedHashMap<>();
            homelogMap1.put("cd", 22990001);
            homelogMap1.put("grp", "학습분석 상");
            homelogMap.put("grpCd", "A");
            homelogMap1.put("name", "평가상 - 국어부문");
            homelogMap1.put("property", "도전");
            homelogMap1.put("cont", "평균 수행률 상위 1%");
            homelogMap1.put("tchrName", "AI생활기록부");
            homelogMap1.put("status", "삭제완료");
            homelogMap1.put("regDttm", "2022-10-07 10:30:00");
            
            homelogList.add(homelogMap);
            homelogList.add(homelogMap1);
            
            data.put("hlogList", homelogList);
            
            setResult(dataKey,data);
        } else {
        	setResult(msgKey, vu.getResult());
        }
        
	    return result; 
    }
    
    @Override
    public Map getHlogDetailList(Map<String,Object> paramMap) throws Exception {
        Map<String,Object> data = new LinkedHashMap<>();
        
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        getStudId(paramMap);
        
        vu.checkRequired(new String[] {"studId"}, paramMap);
        if(vu.isValid()) {
        	 /*상장 목록 수 조회*/
            
            /*상장 목록 수 조회*/
        	
        	data.put("totalCnt", 100);
            data.put("pageCnt", 10);
            data.put("currPage", 1);
            
            ArrayList<Map<String, Object>> homelogList = new ArrayList<>();
            Map<String, Object> homelogMap = new LinkedHashMap<>();
            homelogMap.put("cd", 22100001);
            homelogMap.put("id", "ISE-FFC-10-0001");
            homelogMap.put("grp", "선생님 상");
            homelogMap.put("grpCd", "M");
            homelogMap.put("name", "성실 만렙 상");
            homelogMap.put("studName", "뚜루뚜루");
            homelogMap.put("property", "홈런 챌린지 - 영어책 모두 읽기");
            homelogMap.put("period", "2022년 10월 01일 ~ 2022년 10월 11일");
            homelogMap.put("cont", "위 학생은.... 칭찬합니다.");
            homelogMap.put("tchrName", "아이뚜루");
            homelogMap.put("templateUrl", "템플릿 URL");
            homelogMap.put("regDttm", "2022-10-06 10:30:00");
            
            Map<String, Object> homelogMap1 = new LinkedHashMap<>();
            homelogMap1.put("cd", 22990001);
            homelogMap1.put("id", "ISE-FFC-99-0001");
            homelogMap1.put("grp", "학습분석 상");
            homelogMap1.put("grpCd", "A");
            homelogMap1.put("name", "평가상 - 국어부문");
            homelogMap1.put("studName", "뚜루뚜루");
            homelogMap1.put("property", "홈런 챌린지 - 영어책 모두 읽기");
            homelogMap1.put("period", "2022년 10월 01일 ~ 2022년 10월 11일");
            homelogMap1.put("cont", "위 학생은.... 칭찬합니다.");
            homelogMap1.put("tchrName", "아이뚜루");
            homelogMap1.put("templateUrl", "템플릿 URL");
            homelogMap1.put("regDttm", "2022-10-07 10:30:00");
            
            homelogList.add(homelogMap);
            homelogList.add(homelogMap1);
            
            data.put("hlogList", homelogList);
            
            setResult(dataKey,data);
        } else {
        	setResult(msgKey, vu.getResult());
        }
        
	    return result; 
    }
    
    @Override
    public Map getHlogThnList(Map<String,Object> paramMap) throws Exception {
        Map<String,Object> data = new LinkedHashMap<>();
        
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        getStudId(paramMap);
        
        vu.checkRequired(new String[] {"studId"}, paramMap);
        if(vu.isValid()) {
        	 /*상장 목록 수 조회*/
            
            /*상장 목록 수 조회*/
        	
        	data.put("totalCnt", 100);
            data.put("pageCnt", 10);
            data.put("currPage", 1);
            
            ArrayList<Map<String, Object>> homelogList = new ArrayList<>();
            Map<String, Object> homelogMap = new LinkedHashMap<>();
            homelogMap.put("cd", 22100001);
            homelogMap.put("grp", "선생님 상");
            homelogMap.put("grpCd", "M");
            homelogMap.put("name", "성실 만렙 상");
            homelogMap.put("property", "홈런 챌린지 - 영어책 모두 읽기");
            homelogMap.put("thumbnailUrl", "썸네일 URL");
            homelogMap.put("regDttm", "2022-10-06 10:30:00");
            
            Map<String, Object> homelogMap1 = new LinkedHashMap<>();
            homelogMap1.put("cd", 22990001);
            homelogMap1.put("grp", "학습분석 상");
            homelogMap1.put("grpCd", "A");
            homelogMap1.put("name", "평가상 - 국어부문");
            homelogMap1.put("property", "홈런 챌린지 - 영어책 모두 읽기");
            homelogMap1.put("thumbnailUrl", "썸네일 URL");
            homelogMap1.put("regDttm", "2022-10-07 10:30:00");
            
            homelogList.add(homelogMap);
            homelogList.add(homelogMap1);
            
            data.put("hlogList", homelogList);
            
            setResult(dataKey,data);
        } else {
        	setResult(msgKey, vu.getResult());
        }
        
	    return result; 
    }
    
    @Override
    public Map getHlogDetail(Map<String,Object> paramMap) throws Exception {
        Map<String,Object> data = new LinkedHashMap<>();
        
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        getStudId(paramMap);
        
        vu.checkRequired(new String[] {"studId"}, paramMap);
        if(vu.isValid()) {
        	 /*상장 목록 수 조회*/
            
            /*상장 목록 수 조회*/
        	
        	data.put("cd", 22100001);
        	data.put("id", "ISE-FFC-10-0001");
        	data.put("grp", "선생님 상");
        	data.put("grpCd", "M");
        	data.put("name", "성실 만렙 상");
            data.put("studName", "뚜루뚜루");
            data.put("property", "홈런 챌린지 - 영어책 모두 읽기");
            data.put("period", "2022년 10월 01일 ~ 2022년 10월 11일");
            data.put("cont", "위 학생은.... 칭찬합니다.");
            data.put("tchrName", "아이뚜루");
            data.put("templateUrl", "템플릿 URL");
            data.put("shortUrl", "단축 URL");
            data.put("regDttm", "2022-10-06 10:30:00");
            
            setResult(dataKey,data);
        } else {
        	setResult(msgKey, vu.getResult());
        }
        
	    return result; 
    }
    
    @Override
    public Map getHlogInfo(Map<String,Object> paramMap) throws Exception {
        Map<String,Object> data = new LinkedHashMap<>();
        
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        getStudId(paramMap);
        
        vu.checkRequired(new String[] {"studId"}, paramMap);
        if(vu.isValid()) {
        	 /*상장 목록 수 조회*/
            
            /*상장 목록 수 조회*/
        	
        	data.put("cd", 22100001);
        	data.put("grp", "선생님 상");
        	data.put("grpCd", "M");
        	data.put("memo", "상장 메모");
        	data.put("name", "성실 만렙 상");
            data.put("studName", "뚜루뚜루");
            data.put("propertyName", "목표");
            data.put("propertyContent", "홈런 챌린지 - 영어책 모두 읽기");
            data.put("periodName", "독서 기간");
            data.put("startPeriod", "2022-10-01");
            data.put("endPeriod", "2022-10-31");
            data.put("cont", "위 학생은.... 칭찬합니다.");
            data.put("templateCd", 1);
            
            setResult(dataKey,data);
        } else {
        	setResult(msgKey, vu.getResult());
        }
        
	    return result; 
    }

    /**
     * 서비스단에서 리턴되는 결과(메시지,데이터 object를 포함한 result)세팅.
     * @param key
     * @param data
     */
    private void setResult(String key, Object data) {
    	LinkedHashMap message = new LinkedHashMap();
        result = null;
        result = new LinkedHashMap();

        if(data == null
                || (data instanceof List && ((List)data).size() == 0)
                || (data instanceof Map && ((Map)data).isEmpty())) {
//            throw new NoDataException(new Object[] {key,"null",ValidationCode.NO_DATA});

            message.put("resultCode", ValidationCode.NO_DATA.getCode());
            message.put("result", ValidationCode.NO_DATA.getMessage());
            result.put(msgKey, message);
        }
        else if(data instanceof Map && ((Map)data).containsKey("error")) {	// error 키값 존재하면 예외 처리
            result.put(msgKey, data);
        } else if(data instanceof Map && ((Map)data).containsKey("resultCode")) {	// resultCode 키값 존재하면 예외 처리
        	result.put(msgKey, data);
        } else {
            message.put("resultCode", ValidationCode.SUCCESS.getCode());
            result.put(msgKey, message);
            result.put(dataKey, data);
        }
    }
    
    /***
     * 파라미터에서 studId 추출
     * @param params
     * @throws Exception
     */
	private void getStudId(Map<String, Object> params) throws Exception {
		decodeResult = new LinkedHashMap<String, Object>();
		if(!params.containsKey("studId") && params.containsKey("p")) {
			//복호화
			try {
				CipherUtil cp = CipherUtil.getInstance();
				String decodedStr = cp.AES_Decode(params.get("p").toString());
				
				int studId = (!decodedStr.contains("&")) ? Integer.parseInt(decodedStr) : Integer.parseInt(decodedStr.split("&")[1]) ;
				
				if(decodedStr != null) {
					//DB params
					params.put("studId",studId);
				}
			} catch (Exception e) {
				LOGGER.debug("p Parameter Incorrect");
				
				//p값 복호화 실패
				decodeResult.put("resultCode", ValidationCode.REQUIRED.getCode());
				decodeResult.put("result", "p : Incorrect");
			}
		}
	}
	
	private String encodeStudId(String studId) throws Exception {
		String encodeStudId = null;
		
		CipherUtil cps = CipherUtil.getInstance();
		encodeStudId = cps.AES_Encode(studId);
		
		return encodeStudId;
	}
}
