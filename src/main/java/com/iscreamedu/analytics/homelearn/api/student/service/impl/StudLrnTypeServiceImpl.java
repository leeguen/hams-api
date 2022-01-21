package com.iscreamedu.analytics.homelearn.api.student.service.impl;

import com.iscreamedu.analytics.homelearn.api.common.security.CipherUtil;
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

    @Override
    public Map getLrnTypeSummary(Map<String, Object> paramMap) throws Exception {
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
    				data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "StudLrnType.getLrnTypeSummary");
    				
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
    public Map getLrnTypeInfo(Map<String, Object> paramMap) throws Exception {
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
        			Map<String, Object> lrnTypeMap = new LinkedHashMap<String, Object>();
        			Map<String, Object> lrnTypeCheckMap = new LinkedHashMap<String, Object>();
        			
        			lrnTypeMap = (Map<String, Object>) commonMapperLrnType.get(paramMap, "getLrnTypeInfo");
        			lrnTypeCheckMap = (Map<String, Object>) commonMapperLrnType.get(paramMap, "getLrnTypeInfoYn");
        			
        			data.put("lrnType", lrnTypeMap);
        			data.put("lrnTypeCheck", lrnTypeCheckMap);
        			
        			String lrnTypeAnalCheck = lrnTypeCheckMap.get("lrnTypeAnalYn").toString();
        			String lrnTypeCheck = lrnTypeCheckMap.get("lrnTypeCheckYn").toString();
        			
        			if(lrnTypeCheck.equals("N") && lrnTypeAnalCheck.equals("Y")) {
        				
        				paramMap.put("guideValue", 1);
        				commonMapperLrnType.update(paramMap, "updateLrnTypeInfo");
        			}
        			
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
            		Map<String, Object> grpMap = new HashMap<>(); 
            		
            		grpMap = (Map<String, Object>) commonMapperLrnType.get(paramMap, "getLrnTypeGrp");
            		data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "getLrnTypeDetail");
            		
            		data.put("maxLrnTypeNm", grpMap.get("maxLrnTypeNm"));
            		
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
}
