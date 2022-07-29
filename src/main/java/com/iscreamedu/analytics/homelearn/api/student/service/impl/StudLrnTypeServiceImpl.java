package com.iscreamedu.analytics.homelearn.api.student.service.impl;

import com.iscreamedu.analytics.homelearn.api.common.security.CipherUtil;
import com.iscreamedu.analytics.homelearn.api.common.service.ExternalAPIService;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationCode;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtil;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtilTutor;
import com.iscreamedu.analytics.homelearn.api.student.service.StudLrnTypeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iscreamedu.analytics.homelearn.api.common.exception.NoDataException;
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
public class StudLrnTypeServiceImpl implements StudLrnTypeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudLrnTypeServiceImpl.class);

    private LinkedHashMap<String, Object> result;
    private LinkedHashMap<String, Object> decodeResult;
    private String msgKey = "msg";
    private String dataKey = "data";

    @Autowired
    CommonMapperLrnType commonMapperLrnType;
    
    @Autowired
	ExternalAPIService externalAPIservice;

    @Override
    public Map getLrnTypeCheck(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
    	
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        vu.checkRequired(new String[] {"p"}, paramMap);
        
        if(vu.isValid()) {
        	getStudId(paramMap);
        	
        	if(decodeResult.isEmpty()) {
                Calendar beforeMonth = Calendar.getInstance();
                beforeMonth.add(Calendar.MONTH , -1);
                String yymm = new java.text.SimpleDateFormat("yyyyMM").format(beforeMonth.getTime());
    			
    			paramMap.put("yymm", yymm);
    			
    			data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "StudLrnType.getLrnTypeCheck");
				
				setResult(dataKey,data);
        			
        	} else {
        		setResult(msgKey, decodeResult);
        	}
        	
        } else {
        	setResult(msgKey, vu.getResult());
        }
	
	    return result;
    }
    
    @Override
    public Map getLrnTypeDetail(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new LinkedHashMap<>();
        
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        vu.checkRequired(new String[] {"yyyy","mm","p"}, paramMap);
        
        if(vu.isValid()) {
        	getStudId(paramMap);
        	
        	if(decodeResult.isEmpty()) {
        		String yyyy = paramMap.get("yyyy").toString();
        		int mm = Integer.valueOf(paramMap.get("mm").toString());
        		String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
        		
        		paramMap.put("yymm", yyyy+convertMm);
        		
        		vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
        		
        		if(vu1.isValid()) {
        			Map<String, Object> lrnTypeInfoMap = new LinkedHashMap<String, Object>();
        			Map<String, Object> lrnTypeMap = new LinkedHashMap<String, Object>();
        			Map<String, Object> lrnTypeDetailMap = new LinkedHashMap<String, Object>();
        			
        			lrnTypeMap = (Map<String, Object>) commonMapperLrnType.get(paramMap, "getLrnTypeInfo");
        			//lrnTypeInfoMap = (Map<String, Object>) commonMapperLrnType.get(paramMap, "getLrnTypeInfoYn");
        			
        			if(lrnTypeMap != null) {
        				int actDiff = Integer.parseInt(lrnTypeMap.get("actDiff").toString());
        				int strDiff = Integer.parseInt(lrnTypeMap.get("strDiff").toString());
        				
        				String levelNm = (actDiff > strDiff) ? "행동영역" : (actDiff == strDiff) ? "행동영역" : "전략영역";
        				int levelDiff = (actDiff > strDiff) ? actDiff : (actDiff == strDiff) ? actDiff : strDiff;
        				
        				
        				lrnTypeInfoMap.put("lrnTypeNm", lrnTypeMap.get("lrnTypeNm"));
        				lrnTypeInfoMap.put("prevLrnTypeNm", lrnTypeMap.get("prevLrnTypeNm"));
        				lrnTypeInfoMap.put("levelNm", levelNm);
        				lrnTypeInfoMap.put("levelDiff", 0);
        				
        				lrnTypeDetailMap.put("actLevel", lrnTypeMap.get("actLevel"));
        				lrnTypeDetailMap.put("strLevel", lrnTypeMap.get("strLevel"));
        				lrnTypeDetailMap.put("lrnTypeMsg", "학습 성향 진단 메세지");
        				lrnTypeDetailMap.put("lrnTypeActMsg", "전략 지수 메세지");
        				lrnTypeDetailMap.put("lrnTypeStrMsg", "행동 지수 메세지");
        			}
        			
        			data.put("lrnType", lrnTypeInfoMap);
        			data.put("lrnTypeInfo", lrnTypeDetailMap);
        			
        			setResult(dataKey,data);
        			
        		} else {
        			setResult(msgKey, vu1.getResult());
        		}
        	} else {
        		setResult(msgKey, decodeResult);
        	}
        	
        } else {
        	setResult(msgKey, vu.getResult());
        }
	
	    return result;
    }
    
    
    @Override
    public Map getLrnTypeHistory(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        vu.checkRequired(new String[] {"yyyy","mm","p"}, paramMap);
        
        if(vu.isValid()) {
        	getStudId(paramMap);
        	
        	if(decodeResult.isEmpty()) {
        		String yyyy = paramMap.get("yyyy").toString();
        		int mm = Integer.valueOf(paramMap.get("mm").toString());
        		String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
        		
        		paramMap.put("yymm", yyyy+convertMm);
        		
        		vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
        		
        		if(vu1.isValid()) {
        			
        			ArrayList<Map<String, Object>> lrnTypeHistoryList = new ArrayList<>();
        			
        			lrnTypeHistoryList = (ArrayList<Map<String, Object>>) commonMapperLrnType.getList(paramMap, "getLrnTypeHistory");
        			
        			data.put("lrnTypeList", lrnTypeHistoryList);
        			
        			setResult(dataKey,data);
        		} else {
        			setResult(msgKey, vu1.getResult());
        		}
        	} else {
        		setResult(msgKey, decodeResult);
        	}
        	
        } else {
        	setResult(msgKey, vu.getResult());
        }

	    return result;
    }
    
    @Override
    public Map getStudLrnTypeInfo(String studId) throws Exception {
    	Map<String,Object> data = new LinkedHashMap<>();
        Map<String,Object> studData = new HashMap<>();
        Map<String,Object> paramMap = new HashMap<>();
        
        paramMap.put("studId", studId);
        
        /*studData = (Map<String, Object>) commonMapperLrnType.get(paramMap, "getStudLrnTypeInfoCheck");
        
        if(studData != null) {
        	int lrnSttCd = Integer.parseInt(studData.get("lrnSttCd").toString());
        	String endDt = studData.get("endDt").toString();
        	String yesterDay = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1).toString();
        	
        	if((lrnSttCd != 1003 && lrnSttCd != 1007) && !endDt.equals(yesterDay)) {
        		Map<String,Object> studInfoParamMap = new HashMap<>();
        		String p = encodeStudId("0&"+studId);
            	
            	studInfoParamMap.put("p", p);
            	studInfoParamMap.put("apiName", "aiReport.");
                
                LinkedHashMap<String,String> studInfo = new LinkedHashMap<>();
                Map<String,Object> studInfoMap = (Map<String, Object>) externalAPIservice.callExternalAPI(studInfoParamMap).get("data");
                
                if(studInfoMap != null) {
                	int lrnSttCdApi = Integer.parseInt(studInfoMap.get("statusCd").toString().replace("000", "00"));
                	
                	if(lrnSttCdApi != lrnSttCd ) { 
                		Map<String,Object> insertParamMap = new HashMap<>();
                		
                		int studStatus = (lrnSttCdApi == 1003 || lrnSttCdApi == 1007) ? 1 : 0;
                		String studStatusDetail = (lrnSttCdApi == 1003 || lrnSttCdApi == 1007) ? "L" : (lrnSttCdApi == 1008 || lrnSttCdApi == 1009 || lrnSttCdApi == 1010) ? "P" : "E";
                		
                		insertParamMap.put("studId", studId);
                		insertParamMap.put("lrnSttCd", lrnSttCdApi);
                		insertParamMap.put("studStatus", studStatus);
                		insertParamMap.put("studStatusDetail", studStatusDetail);
                		
                		int rows = commonMapperLrnType.update(insertParamMap, "updateStudLrnTypeInfo");
                	}
                }
        	}
        	
        }*/
        data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "getStudLrnTypeInfo");
        
        //학생 구분 값 관련 ID
        int studTypeId = (data != null && data.get("studTypeId") != null) ? Integer.parseInt(data.get("studTypeId").toString()) : 0;
        
        Map<String,Object> studInfoParamMap = new HashMap<>();
		String p = encodeStudId("0&"+studId);
    	
    	studInfoParamMap.put("p", p);
    	studInfoParamMap.put("apiName", "aiReport.");
        
        LinkedHashMap<String,String> studInfo = new LinkedHashMap<>();
        Map<String,Object> studInfoMap = (Map<String, Object>) externalAPIservice.callExternalAPI(studInfoParamMap).get("data");
        
        if(studInfoMap != null) {
        	int lrnSttCdApi = Integer.parseInt(studInfoMap.get("statusCd").toString().replace("000", "00"));
        	//int studStatus = (lrnSttCdApi == 1003 || lrnSttCdApi == 1007) ? 1 : 0;
        	String studStatus = (lrnSttCdApi == 1003 || lrnSttCdApi == 1007) ? "진행중" : "진행중단";
    		String studStatusDetail = (lrnSttCdApi == 1003 || lrnSttCdApi == 1007) ? "L" : (lrnSttCdApi == 1008 || lrnSttCdApi == 1009 || lrnSttCdApi == 1010) ? "P" : "E";
    		int studTypeIds = (lrnSttCdApi == 1007) ? 1 : (lrnSttCdApi == 1003) ? 2 : 0;
    		
    		if(data == null) {
    			data = new LinkedHashMap<>();
    			
    			data.put("studId", studId);
    			
            	data.put("lrnTypeCd", null);
        		data.put("lrnTypeGroupCd", null);
        		data.put("lrnSttCd", lrnSttCdApi);
        		
        		if(studTypeId < 3) {
        			data.put("studType", studInfoMap.get("divCdNm"));
        		}
        		
        		data.put("studTypeId", studTypeIds);
        		data.put("studStatus", studStatus);
        		data.put("studStatusDetail", studStatusDetail);
        		data.put("pkgNm", null);
            } else {
            	
            	data.put("studId", studId);
            	
            	if(studTypeId < 3) {
            		data.put("studType", studInfoMap.get("divCdNm"));
            	}
            	data.put("lrnSttCd", lrnSttCdApi);
            	data.put("studStatus", studStatus);
            	data.put("studStatusDetail", studStatusDetail);
            }
    		
        }
        
        setResult(dataKey,data);

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
		//복호화
        try {
        	CipherUtil cp = CipherUtil.getInstance();
    		String decodedStr = cp.AES_Decode(params.get("p").toString());
    		
    		if(decodedStr != null) {
    			//DB params
    			params.put("studId",decodedStr);
    		}
        } catch (Exception e) {
            LOGGER.debug("p Parameter Incorrect");
            
            //p값 복호화 실패
            decodeResult.put("resultCode", ValidationCode.REQUIRED.getCode());
            decodeResult.put("result", "p : Incorrect");
        }
	}
	
	private String encodeStudId(String studId) throws Exception {
		String encodeStudId = null;
		
		CipherUtil cps = CipherUtil.getInstance();
		encodeStudId = cps.AES_Encode(studId);
		
		return encodeStudId;
	}
}
