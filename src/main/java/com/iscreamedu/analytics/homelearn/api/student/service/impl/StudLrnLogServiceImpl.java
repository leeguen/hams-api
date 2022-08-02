package com.iscreamedu.analytics.homelearn.api.student.service.impl;

import com.iscreamedu.analytics.homelearn.api.common.security.CipherUtil;
import com.iscreamedu.analytics.homelearn.api.common.service.ExternalAPIService;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationCode;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtil;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtilTutor;
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
public class StudLrnLogServiceImpl implements StudLrnLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudLrnLogServiceImpl.class);

    private LinkedHashMap<String, Object> result;
    private LinkedHashMap<String, Object> decodeResult;
    private String msgKey = "msg";
    private String dataKey = "data";

    private static final String LRN_MT_NAMESPACE = "LrnMt";

    @Autowired
    CommonMapperLrnDm studLrnAnalMapper;
    
    @Override
    public Map getStudLrnExLog(String studId) throws Exception {
        Map<String,Object> data = new LinkedHashMap<>();
        Map<String,Object> paramMap = new HashMap<>();
        ArrayList<Map<String,Object>> lrnLogExList = new ArrayList();
        ArrayList<Map<String,Object>> dwLrnLogExList = new ArrayList();
        
        String today = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1).toString().replace("-","");
        String beforeYearDay = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(365).toString().replace("-","");
        
        int startDay = Integer.parseInt(beforeYearDay);
        int endDay = Integer.parseInt(today);
        
        paramMap.put("studId", Integer.parseInt(studId));
        paramMap.put("startDt", startDay);
        paramMap.put("endDt", endDay);
        paramMap.put("subjCd", "C02");
        
        dwLrnLogExList = (ArrayList<Map<String, Object>>) studLrnAnalMapper.getList(paramMap,LRN_MT_NAMESPACE + ".selectLrnExLog");
        
        if(dwLrnLogExList.size() > 0) {
        	for(Map<String,Object> lrnLogItem : dwLrnLogExList) {
        		LinkedHashMap<String, Object> lrnLogMap = new LinkedHashMap<>();
        		
        		lrnLogMap.put("grade", lrnLogItem.get("grade"));
        		lrnLogMap.put("sem", lrnLogItem.get("sem"));
        		lrnLogMap.put("textBookNm", lrnLogItem.get("textBookNm"));
        		lrnLogMap.put("studyNm", lrnLogItem.get("studyNm"));
        		lrnLogMap.put("serviceNm", lrnLogItem.get("serviceNm"));
        		lrnLogMap.put("chapterNm", lrnLogItem.get("chapterNm"));
        		lrnLogMap.put("chapterNo", lrnLogItem.get("chapterNo"));
        		
        		lrnLogExList.add(lrnLogMap);
        	}
        }
        
        data.put("lrnLogExList", lrnLogExList);
        
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
