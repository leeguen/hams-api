package com.iscreamedu.analytics.homelearn.api.common.service.impl;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.iscreamedu.analytics.homelearn.api.common.service.ExternalAPIService;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationCode;

/**
 * HAMS External API ServiceImpl
 * @author hy
 * @since 2020.10.13
 * @version 1.0
 * @see
 *  
 * <pre>
 * << 개정이력(Modification Information) >>
 * 
 *  수정일      		   수정자		수정내용
 *  ----------  --------    --------------------------
 *  2020.10.13	 hy		           초기생성 
 *  2020.10.16   shoshu      영어도서관 반영
 *  </pre>
 */
@Service
public class ExternalAPIServiceImpl implements ExternalAPIService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExternalAPIServiceImpl.class);
	
	private LinkedHashMap<String, Object> result;
	private String msgKey = "msg";
	private String dataKey = "data";
	
	@Value("${extapi.hl.url}")
	String HL_API; //홈런 기본API 주소
	
	@Value("${extapi.hlbook.url}")	
	String HLBOOK_API; //홈런도서관 기본API 주소
	
	@Value("${extapi.englib.url}")
	String ENGLIB_API; //영어도서관 기본API 주소

	/*********************** 
	 * sample ID Info.
	 * loginId: dobby1
	 * studId : 1571427
	 * stuNo  : 686541
	 ***********************/
	@Override
	public Map callExternalAPI(Map<String, Object> paramMap) throws Exception {

		if(paramMap.containsKey("apiName") && !"".equals(paramMap.get("apiName"))) {
			
			String apiName = ((String)paramMap.remove("apiName")).replaceAll("\\.", "\\/"); // '.'을 '/'로 변환, 맵에서 삭제
			RestTemplate restTemplate = new RestTemplate();
	        
	        //홈런도서관
	        if("read/complete".equals(apiName)) {
	        	
	        	String url = HLBOOK_API + apiName;
	        	
	        	//헤더에 memId 세팅
	        	HttpHeaders headers = new HttpHeaders();
	        	headers.setContentType(MediaType.APPLICATION_JSON);
	            headers.set("memId", paramMap.get("memId").toString());
	            
	            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(paramMap, headers);
	
	            ResponseEntity<LinkedHashMap> response = restTemplate.postForEntity(url, entity, LinkedHashMap.class);
	            LinkedHashMap responseData = response.getBody();
	            
	            LOGGER.debug("code : " + responseData.get("code"));
	        	LOGGER.debug("message : " + responseData.get("message"));
	        	LOGGER.debug("data : " + responseData.get("result"));
	        	
	        	if("200".equals(responseData.get("code").toString())) {
	        		setResult(dataKey, responseData.get("result"));
	        	} else {
	        		LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
	        		msgMap.put("resultCode", ValidationCode.EX_API_ERROR.getCode());
	        		msgMap.put("result", "(" + responseData.get("code") + ")" + responseData.get("message"));
	        		setResult(msgKey, msgMap);
	        	}
	        	
		    //영어도서관
	        } else if("studyHistoryGeneral".equals(apiName)) {
	        	String url = ENGLIB_API + apiName;
	        	
	        	//파라미터 세팅
	        	UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
	        	for( String key : paramMap.keySet() ){ 
	        		builder.queryParam(key, paramMap.get(key)); 
	        	}
	        	URI apiUri = builder.build().encode().toUri();  
	        	
	        	LinkedHashMap responseData = restTemplate.getForObject(apiUri, LinkedHashMap.class);
	        	
	        	LOGGER.debug("result : " + responseData.get("result"));
	        	LOGGER.debug("message : " + responseData.get("message"));
	        	LOGGER.debug("data : " + responseData.get("data"));
	        	
	        	if("true".equals(responseData.get("result").toString())) {
	        		LinkedHashMap dataMap = new LinkedHashMap<String, Object>();
	        		dataMap.put("total", responseData.get("total"));
	        		dataMap.put("list", responseData.get("data"));
	        		
	        		setResult(dataKey, dataMap);
	        	} else {
	        		LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
	        		msgMap.put("resultCode", ValidationCode.EX_API_ERROR.getCode());
	        		msgMap.put("result", "(" + responseData.get("code") + ")" + responseData.get("message"));
	        		setResult(msgKey, msgMap);
	        	}
	        	
	    	//HL POST
	        } else if("english-study/change-english-skip".equals(apiName) 
	        		|| apiName.indexOf("student-study-course-plan/reset-village/") == 0) {
	        	
	        	String url = HL_API + apiName + ".json";
	        	
	        	LinkedHashMap responseData = restTemplate.postForObject(url, paramMap, LinkedHashMap.class);
	        	
	        	LOGGER.debug("code : " + responseData.get("code"));
	        	LOGGER.debug("message : " + responseData.get("message"));
	        	LOGGER.debug("data : " + responseData.get("data"));
	        	
	        	if("200".equals(responseData.get("code").toString())) {
	        		if("english-study/change-english-skip".equals(apiName)) {
	        			setResult(dataKey, "SUCCESS");
	        		} else {
	        			setResult(dataKey, responseData.get("data"));
	        		}
	        	}else {
	        		LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
	        		msgMap.put("resultCode", ValidationCode.EX_API_ERROR.getCode());
	        		msgMap.put("result", "(" + responseData.get("code") + ")" + responseData.get("message"));
	        		setResult(msgKey, msgMap);
	        	}
	        	
	    	//HL GET
	        } else {
	        	
	        	String url = HL_API + apiName + ".json";
	        	
	        	//파라미터 세팅
	        	UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
	        	for( String key : paramMap.keySet() ){ 
	        		builder.queryParam(key, paramMap.get(key)); 
	        	}
	        	URI apiUri = builder.build().encode().toUri();  
	        	
	        	LinkedHashMap responseData = restTemplate.getForObject(apiUri, LinkedHashMap.class);
	        	
	        	LOGGER.debug("code : " + responseData.get("code"));
	        	LOGGER.debug("message : " + responseData.get("message"));
	        	LOGGER.debug("data : " + responseData.get("data"));
	        	
	        	if("200".equals(responseData.get("code").toString())) {
	        		setResult(dataKey, responseData.get("data"));
	        	} else {
	        		LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
	        		msgMap.put("resultCode", ValidationCode.EX_API_ERROR.getCode());
	        		msgMap.put("result", "(" + responseData.get("code") + ")" + responseData.get("message"));
	        		setResult(msgKey, msgMap);
	        	}
	        }
	        
		} else {
			LOGGER.debug("Empty External APIName !!!");
		}
		
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
}
