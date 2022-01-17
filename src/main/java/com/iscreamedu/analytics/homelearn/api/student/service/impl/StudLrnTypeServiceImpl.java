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
    private String msgKey = "msg";
    private String dataKey = "data";

    @Autowired
    CommonMapperLrnType commonMapperLrnType;

    @Override
    public Map getLrnTypeSummary(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        
        getStudId(paramMap);
    	
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        vu.checkRequired(new String[] {"yyyy","mm","studId"}, paramMap);
        
        if(vu.isValid()) {
        	String yyyy = paramMap.get("yyyy").toString();
			int mm = Integer.valueOf(paramMap.get("mm").toString());
			String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
        	
			paramMap.put("yymm", yyyy+convertMm);
			
			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
			
			if(vu1.isValid()) {
				data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "StudLrnType.getLrnTypeSummary");
			} else {
				setResult(msgKey, vu1.getResult());
			}
        	
        } else {
        	setResult(msgKey, vu.getResult());
        }

        setResult(dataKey,data);
	
	    return result;
    }
    
    @Override
    public Map getLrnTypeInfo(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        
        getStudId(paramMap);
    	
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        vu.checkRequired(new String[] {"yyyy","mm","studId"}, paramMap);
        
        if(vu.isValid()) {
        	String yyyy = paramMap.get("yyyy").toString();
			int mm = Integer.valueOf(paramMap.get("mm").toString());
			String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
        	
			paramMap.put("yymm", yyyy+convertMm);
			
			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
			
			if(vu1.isValid()) {
				Map<String, Object> lrnTypeMap = new LinkedHashMap<String, Object>();
				
				lrnTypeMap = (Map<String, Object>) commonMapperLrnType.get(paramMap, "getLrnTypeInfo");
				
				data.put("lrnType", lrnTypeMap);
			} else {
				setResult(msgKey, vu1.getResult());
			}
        	
        } else {
        	setResult(msgKey, vu.getResult());
        }

        setResult(dataKey,data);
	
	    return result;
    }
    
    @Override
    public Map getLrnTypeDetail(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        
        getStudId(paramMap);
    	
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        vu.checkRequired(new String[] {"yyyy","mm","studId"}, paramMap);
        
        if(vu.isValid()) {
        	String yyyy = paramMap.get("yyyy").toString();
			int mm = Integer.valueOf(paramMap.get("mm").toString());
			String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
        	
			paramMap.put("yymm", yyyy+convertMm);
			
			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
			
			if(vu1.isValid()) {
				data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "");
			} else {
				setResult(msgKey, vu1.getResult());
			}
        	
        } else {
        	setResult(msgKey, vu.getResult());
        }

        setResult(dataKey,data);
	
	    return result;
    }
    
    @Override
    public Map getLrnTypeHistory(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        
        getStudId(paramMap);
    	
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        vu.checkRequired(new String[] {"yyyy","mm","studId"}, paramMap);
        
        if(vu.isValid()) {
        	String yyyy = paramMap.get("yyyy").toString();
			int mm = Integer.valueOf(paramMap.get("mm").toString());
			String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
        	
			paramMap.put("yymm", yyyy+convertMm);
			
			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
			
			if(vu1.isValid()) {
				data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "");
			} else {
				setResult(msgKey, vu1.getResult());
			}
        	
        } else {
        	setResult(msgKey, vu.getResult());
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
        result = new LinkedHashMap();
        
        if(key.equals(dataKey)) {
            LinkedHashMap message = new LinkedHashMap();            
            if(data == null 
                    || (data instanceof List && ((List)data).size() == 0) 
                    || (data instanceof Map && ((Map)data).isEmpty())) {
                //조회결과가 없는 경우 메시지만 나감.
                message.put("resultCode", ValidationCode.NO_DATA.getCode());
                result.put(msgKey, message);
            } else {
                //정상데이터, 정상메시지
                message.put("resultCode", ValidationCode.SUCCESS.getCode());
                result.put(msgKey, message);
                
                result.put(dataKey, data);
            }
        } else {
            result.put(msgKey, data); //validation 걸린 메시지, 데이터 없음
        }
    }
    
    /***
     * 파라미터에서 studId 추출
     * @param params
     * @throws Exception
     */
	private void getStudId(Map<String, Object> params) throws Exception {
		//복호화
        try {
        	CipherUtil cp = CipherUtil.getInstance();
    		String decodedStr = cp.AES_Decode(params.get("p").toString());

            //DB params
            params.put("studId",decodedStr);
        } catch (Exception e) {
            LOGGER.debug("p Parameter Incorrect");
        }
	}
}
