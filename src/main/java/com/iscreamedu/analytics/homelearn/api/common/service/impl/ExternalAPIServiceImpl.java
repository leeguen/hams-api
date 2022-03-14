package com.iscreamedu.analytics.homelearn.api.common.service.impl;

import java.io.Serializable;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
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
import org.springframework.web.util.UriComponentsBuilder;

import com.iscreamedu.analytics.homelearn.api.common.service.ExternalAPIService;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationCode;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtil;
import com.iscreamedu.analytics.homelearn.api.hamsSales.service.impl.HamsSalesServiceImpl;

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
 *  2020.11.06   shoshu      예외 처리 추가
 *  </pre>
 */
@Service
public class ExternalAPIServiceImpl implements ExternalAPIService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExternalAPIServiceImpl.class);
	
	@Autowired
	HamsSalesServiceImpl hamsSalesServiceImpl;
	
	private LinkedHashMap<String, Object> result;
	private String msgKey = "msg";
	private String dataKey = "data";
	
	@Value("${extapi.hl.url}")
	String HL_API; //홈런 기본API 주소
	
	@Value("${extapi.hlbook.url}")	
	String HLBOOK_API; //홈런도서관 기본API 주소
	
	@Value("${extapi.englib.url}")
	String ENGLIB_API; //영어도서관 기본API 주소
	
	@Value("${extapi.hllogin.url}")
	String HLLOGIN_API; //일일 로그인 기록 기본API 주소
	
	@Value("${extapi.hl.tutor.studinfo.url}")
	String TUTORSTUDINFO_API; //학생 정보 API 주소
	
	@Value("${extapi.hl.tutor.course.url}")
	String TUTORCOURSE_API; //학교공부, 특별학습 코스 정보 API 주소
	
	@Value("${extapi.hl.tutor.recommend.url}")
	String TUTORRECOMMEND_API; //AI 추천 정보 API 주소
	
	@Value("${extapi.hlmarketing.url}")
	String HLMARKETING_API; //마케팅 - 기관조회 API 주소
	
	@Value("${extapi.hlfast.url}")
	String HLDW_API; //학생,교사 정보 - DW FAST API 주소
	
	@Override
	public Map callExternalAPI(Map<String, Object> paramMap) throws Exception {

		if(paramMap.containsKey("apiName") && !"".equals(paramMap.get("apiName"))) {
			
			String apiName = ((String)paramMap.remove("apiName")).replaceAll("\\.", "\\/"); // '.'을 '/'로 변환, 맵에서 삭제
			RestTemplate restTemplate = new RestTemplate();
	        
	        //홈런도서관
	        if("read/complete".equals(apiName)) {
	    		//Validation
	    		ValidationUtil vu = new ValidationUtil();
	    		//1.필수값 체크
	    		vu.checkRequired(new String[] {"p"}, paramMap);
	    		vu.checkRequired(new String[] {"fromDate"}, paramMap);
	    		vu.checkRequired(new String[] {"toDate"}, paramMap);
	    		vu.checkRequired(new String[] {"page"}, paramMap);
	    		vu.checkRequired(new String[] {"size"}, paramMap);
	    		
	    		if(vu.isValid()) {
		        	String url = HLBOOK_API + apiName;
		        	
		    		String studId = "";
		    		String encodedStr = paramMap.get("p").toString();
		    		
		    		String[] paramList = hamsSalesServiceImpl.getDecodedParam(encodedStr);
		    		studId = paramList[1];
		    		
		    		paramMap.remove("p");
		        	
		        	//헤더에 memId 세팅
		        	HttpHeaders headers = new HttpHeaders();
		        	headers.setContentType(MediaType.APPLICATION_JSON);
		            headers.set("memId", studId);
		            
		            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(paramMap, headers);
		            
		            try {
		
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
			        	
		            } catch(Exception e) {
		            	LOGGER.error("code : " + ValidationCode.EX_API_ERROR.getCode());
			        	LOGGER.error("message : " + "HomeLearn Book Cafe API Error");
		            	
		        		LinkedHashMap temp = new LinkedHashMap<String, Object>();
		        		temp.put("content", new ArrayList());
		        		temp.put("totalPages", 0);
		        		temp.put("totalElements", 0);
		        		temp.put("numberOfElements", 0);
		        		
		        		setResult(dataKey, temp);
		            }
	    		}else {
	    			setResult(msgKey, vu.getResult());
	    		}
	        	
		    //영어도서관
	        } else if("studyHistoryGeneral".equals(apiName)) {
	    		//Validation
	    		ValidationUtil vu = new ValidationUtil();
	    		//1.필수값 체크
	    		vu.checkRequired(new String[] {"p"}, paramMap);
	    		vu.checkRequired(new String[] {"sd"}, paramMap);
	    		vu.checkRequired(new String[] {"ed"}, paramMap);
	    		vu.checkRequired(new String[] {"page"}, paramMap);
	    		vu.checkRequired(new String[] {"rpp"}, paramMap);
	    		
	    		if(vu.isValid()) {
		        	String url = ENGLIB_API + apiName;
		        	
		    		String studId = "";
		    		String encodedStr = paramMap.get("p").toString();
		    		
		    		String[] paramList = hamsSalesServiceImpl.getDecodedParam(encodedStr);
		    		studId = paramList[1];
		    		
		    		paramMap.remove("p");
		        	
		        	//파라미터 세팅
		        	UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
		        	for( String key : paramMap.keySet() ){ 
		        		builder.queryParam(key, paramMap.get(key)); 
		        	}
		        	
		        	builder.queryParam("member_idx", studId);
		        		        	
		        	URI apiUri = builder.build().encode().toUri();
		        	
		        	try {
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
		        	} catch (Exception e) {
		        		LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
		        		msgMap.put("resultCode", ValidationCode.EX_API_ERROR.getCode());
		        		msgMap.put("result", "Homelearn English Library API Error");
		        		setResult(msgKey, msgMap);
		        	}
	    		}else {
	    			setResult(msgKey, vu.getResult());
	    		}
	        	
	    	//HL POST
	        } else if("english-study/change-english-skip".equals(apiName) 
	        		|| apiName.indexOf("student-study-course-plan/reset-village") == 0) {
	    		//Validation
	    		ValidationUtil vu = new ValidationUtil();
	    		
	        	if("english-study/change-english-skip".equals(apiName)) {
		    		//1.필수값 체크
		    		vu.checkRequired(new String[] {"p"}, paramMap);
		    		vu.checkRequired(new String[] {"skipYn"}, paramMap);
	        	}
	        	
	        	if(apiName.indexOf("student-study-course-plan/reset-village") == 0) {
		    		//1.필수값 체크
		    		vu.checkRequired(new String[] {"p"}, paramMap);
		    		vu.checkRequired(new String[] {"villageCd"}, paramMap);
	        	}
	        	
	        	if(vu.isValid()) {
	        		try {
			        	String url = HL_API + apiName + ".json";
			        	
			    		String studId = "";
			    		String encodedStr = paramMap.get("p").toString();
			    		
			    		String[] paramList = hamsSalesServiceImpl.getDecodedParam(encodedStr);
			    		studId = paramList[1];
			    		paramMap.put("stuId", studId);
			    		
			    		paramMap.remove("p");
			    		
			    		if("student-study-course-plan/reset-village".equals(apiName)) {
			    			url = HL_API + "student-study-course-plan/" + studId + "/reset-village.json";
			    		}
			        	
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
	        		} catch(Exception e) {
		        		LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
		        		msgMap.put("resultCode", ValidationCode.EX_API_ERROR.getCode());
		        		msgMap.put("result", ValidationCode.EX_API_ERROR.getMessage());
		        		setResult(msgKey, msgMap);
	        		}
	    		}else {
	    			setResult(msgKey, vu.getResult());
	    		}
	        	
	    	//HL GET
	        } else if(apiName.equals("step-code-list") || apiName.equals("set-code-list")){
	        	try {
		        	String url = HL_API + apiName + ".json";
		        	UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
		        	
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
	        	} catch(Exception e) {
	        		LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
	        		msgMap.put("resultCode", ValidationCode.EX_API_ERROR.getCode());
	        		msgMap.put("result", ValidationCode.EX_API_ERROR.getMessage());
	        		setResult(msgKey, msgMap);
	        	}
	        // 일일 로그인 기록
	        } else if(apiName.equals("connect-log-list")) { 
	        	try {
		        	String url = HLLOGIN_API + apiName + ".json";
		        	if(paramMap.containsKey("studId")) { 
//		        		url = "https://sem.home-learn.com/sigong/clientsvc/admsys/v1/comm/" + apiName + ".json";	//임시 라이브 api test
			        	paramMap.put("stuId", paramMap.get("studId"));
		        		paramMap.remove("studId");		
		        		paramMap.remove("s");		
		        	} else if(!paramMap.containsKey("stuId") && paramMap.containsKey("p")) {
			        	String studId = "";
			    		String encodedStr = paramMap.get("p").toString();
			    		
			    		String[] paramList = hamsSalesServiceImpl.getDecodedParam(encodedStr);
			    		studId = paramList[1];
			    		paramMap.put("stuId", studId);
			    		
			    		paramMap.remove("p");
		        	}
		        	
		        	//파라미터 세팅
		        	UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
		        	for( String key : paramMap.keySet() ){ 
		        		builder.queryParam(key, paramMap.get(key));
		        	}
		        	
		        	URI apiUri = builder.build().encode().toUri();  
		        	
		        	LinkedHashMap responseData = restTemplate.getForObject(apiUri, LinkedHashMap.class);

		        	LOGGER.debug("apiUri : " + apiUri);
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
	        	} catch(Exception e) {
	        		LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
	        		msgMap.put("resultCode", ValidationCode.EX_API_ERROR.getCode());
	        		msgMap.put("result", ValidationCode.EX_API_ERROR.getMessage());
	        		setResult(msgKey, msgMap);
	        	}
	        } else if(apiName.equals("aiReport/")){
	        	try {
		        	String studId = "";
		    		String encodedStr = paramMap.get("p").toString();
		    		
		    		String[] paramList = hamsSalesServiceImpl.getDecodedParam(encodedStr);
		    		studId = paramList[1];
		    		paramMap.put("studId", studId);
		    		//paramMap.put("studId", "1006753");
		    		
		    		String url = TUTORSTUDINFO_API + apiName + paramMap.get("studId") + ".json";
		        	
		        	//파라미터 세팅
		        	UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
		        	builder.queryParam("for", "aiReport");
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
	        	} catch(Exception e) {
	        		LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
	        		msgMap.put("resultCode", ValidationCode.EX_API_ERROR.getCode());
	        		msgMap.put("result", ValidationCode.EX_API_ERROR.getMessage());
	        		setResult(msgKey, msgMap);
	        	}
	        }else if(apiName.equals("recommand/")){
	        	try {

	        		String url = TUTORRECOMMEND_API + apiName;
	        		//GroupServiceImpl >> getAiRecommendLrn 에서 p 파라미터 대신 studId 파라미터를 가지고 호출 
	        		//studId 추출 코드 예외 추가
	        		if(paramMap.containsKey("studId") && paramMap.containsKey("s")) {
//	        			url = "https://sem.home-learn.com/sigong/clientsvc/admsys/v1/ai/tutor/weekly/" + apiName;	//임시 라이브 api test
 		        		paramMap.remove("s");	   	
	        		} else if(paramMap.containsKey("p")) 
	        		{	        		
			        	String studId = "";
			    		String encodedStr = paramMap.get("p").toString();
			    		
			    		String[] paramList = hamsSalesServiceImpl.getDecodedParam(encodedStr);
			    		studId = paramList[1];
			    		paramMap.put("studId", studId);
			    		//paramMap.put("studId", "1006753");
	        		} 
		    		
		        	url += paramMap.get("studId") + ".json";
		        	
		        	//파라미터 세팅
		        	UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
		        	
		        	URI apiUri = builder.build().encode().toUri();  
		        	
		        	LinkedHashMap responseData = restTemplate.getForObject(apiUri, LinkedHashMap.class);

		        	LOGGER.debug("apiUri : " + apiUri);
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
	        	} catch(Exception e) {
	        		LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
	        		msgMap.put("resultCode", ValidationCode.EX_API_ERROR.getCode());
	        		msgMap.put("result", ValidationCode.EX_API_ERROR.getMessage());
	        		setResult(msgKey, msgMap);
	        	}
	        }else if(apiName.equals("/study/course-due-dates")){
	        	try {
		        	String studId = "";
		    		String encodedStr = paramMap.get("p").toString();
		    		
		    		String[] paramList = hamsSalesServiceImpl.getDecodedParam(encodedStr);
		    		studId = paramList[1];
		    		paramMap.put("studId", studId);
		    		//paramMap.put("studId", "1006753");
		    		
		    		String url = TUTORCOURSE_API + paramMap.get("studId") + apiName + ".json";

		        	//파라미터 세팅
		        	UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
		        	
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
	        	} catch(Exception e) {
	        		LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
	        		msgMap.put("resultCode", ValidationCode.EX_API_ERROR.getCode());
	        		msgMap.put("result", ValidationCode.EX_API_ERROR.getMessage());
	        		setResult(msgKey, msgMap);
	        	}
	        } else if(apiName.equals("agencyServiceApiDetail")){
	        	try {
	        		String url = HLMARKETING_API + apiName + ".json";
	        		
		        	//파라미터 세팅
		        	UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
		        	builder.queryParam("agn_code", paramMap.get("orgId"));
		        	
		        	URI apiUri = builder.build().encode().toUri();  
		        	
		        	LinkedHashMap responseData = restTemplate.getForObject(apiUri, LinkedHashMap.class);

		        	LOGGER.debug("apiUri : " + apiUri);
		        	LOGGER.debug("code : " + responseData.get("code"));
		        	LOGGER.debug("message : " + responseData.get("message"));
		        	LOGGER.debug("data : " + responseData.get("data"));
		        	
		        	if("200".equals(responseData.get("code").toString())) {
		        		setResult(dataKey, responseData.get("data"));
		        	} else if("400".equals(responseData.get("code").toString())) {
		        		LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
						msgMap.put("resultCode", ValidationCode.EX_API_NO_DATA.getCode());
						msgMap.put("result", ValidationCode.EX_API_NO_DATA.getMessage());
						setResult(msgKey, msgMap);
					} else {
		        		LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
		        		msgMap.put("resultCode", ValidationCode.EX_API_ERROR.getCode());
		        		msgMap.put("result", ValidationCode.EX_API_NO_DATA.getMessage() + ":(" + responseData.get("code") + ")" + responseData.get("message"));
		        		setResult(msgKey, msgMap);
		        	}
	        	} catch(Exception e) {
	        		LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
	        		msgMap.put("resultCode", ValidationCode.EX_API_ERROR.getCode());
	        		msgMap.put("result", ValidationCode.EX_API_ERROR.getMessage());
	        		setResult(msgKey, msgMap);
	        	}
	        } else if(apiName.equals("student")){
	        	try {
	        		String url = HLDW_API + apiName + "?item_id={itemId}";
	        		if(paramMap.containsKey("studId") || paramMap.containsKey("loginId") || paramMap.containsKey("stuNm")) {
	        			if(paramMap.containsKey("studId")) {
	        				paramMap.put("itemId","I"); 
	        				url += "&stu_id={studId}";
	        			} else if(paramMap.containsKey("loginId")) {
	        				paramMap.put("itemId","L"); 
	        				url += "&stu_lgn={loginId}";
	        			} else if(paramMap.containsKey("stuNm")) {
	        				paramMap.put("itemId","N"); 
	        				url += "&stu_nm={stuNm}";
	        			} 
	        			LOGGER.debug("url : " + url);
			        	LOGGER.debug("item_id : " + paramMap.get("itemId"));
			        	LOGGER.debug("stu_id : " + paramMap.get("studId"));
			        	LOGGER.debug("stu_lgn : " + paramMap.get("loginId"));
			        	LOGGER.debug("stu_nm : " + paramMap.get("stuNM"));
			            
						JSONParser parser = new JSONParser();
						HttpHeaders headers = new HttpHeaders();
						headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
						HttpEntity<String> entity = new HttpEntity<>(headers);
						ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class, paramMap);
						int statusCode = Integer.valueOf(response.getStatusCode().toString());
						Object responseData = parser.parse(response.getBody());
						
						if(statusCode == 200) {
			        		LOGGER.debug("statusCode : "+statusCode);
//				        	LOGGER.debug("response : " + response.getBody());
							setResult(dataKey, responseData);
						} else {
							LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
							msgMap.put("resultCode", ValidationCode.EX_API_ERROR.getCode());
							msgMap.put("result", ValidationCode.EX_API_ERROR.getMessage() + ":(" + statusCode + ")");
							setResult(msgKey, msgMap);
						}
	        		} else {
		        		LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
		        		msgMap.put("resultCode", ValidationCode.REQUIRED.getCode());
		        		msgMap.put("result", ValidationCode.REQUIRED.getClass());
		        		setResult(msgKey, msgMap);
	        		}
	        	} catch(Exception e) {
	        		LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
	        		msgMap.put("resultCode", ValidationCode.EX_API_ERROR.getCode());
	        		msgMap.put("result", ValidationCode.EX_API_ERROR.getMessage());
	        		setResult(msgKey, msgMap);
	        	}
	        } else if(apiName.equals("students")){
	        	try {
	        		String url = HLDW_API + apiName + "?item_id={itemId}";
        			Map<String,Object> paramData = new HashMap<>();			    
	        		if(paramMap.containsKey("stu_ids")) {
	        			paramData.put("itemId","I"); 
	        			paramData.put("studIds", paramMap.get("stu_ids").toString());   
        				url += "&stu_ids={studIds}";
        			} else if(paramMap.containsKey("login_ids")) {
        				paramData.put("itemId","L");
        				paramData.put("loginIds", paramMap.get("login_ids").toString());   
        				url += "&stu_lgns={loginIds}";
        			} else if(paramMap.containsKey("stu_nms")) {
        				paramData.put("itemId","N"); 
        				paramData.put("stuNms", paramMap.get("stu_nms").toString());   
        				url += "&stu_nms={stuNms}";
        			} 
	        		if(paramMap.containsKey("stu_ids") || paramMap.containsKey("login_ids") || paramMap.containsKey("stu_nms")) {
				    	LOGGER.debug("url : " + url);
			        	LOGGER.debug("item_id : " + paramData.get("itemId"));
			        	LOGGER.debug("stu_ids : " + paramData.get("studIds"));
			        	LOGGER.debug("stu_lgns : " + paramData.get("loginIds"));
			        	LOGGER.debug("stu_nms : " + paramData.get("stuNms"));
			            
						JSONParser parser = new JSONParser();
						HttpHeaders headers = new HttpHeaders();
						headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
						HttpEntity<String> entity = new HttpEntity<>(headers);
						ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class, paramData);
						int statusCode = Integer.valueOf(response.getStatusCode().toString());
						Object responseData = parser.parse(response.getBody());
						
						if(statusCode == 200) {
							JSONArray jsonList = (JSONArray) responseData;
							List<HashMap<String,Object>> studLists = (List) jsonList;

			        		LOGGER.debug("statusCode : "+statusCode);
							setResult(dataKey, studLists);
						}
						else {
							LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
							msgMap.put("resultCode", ValidationCode.EX_API_ERROR.getCode());
							msgMap.put("result", ValidationCode.EX_API_ERROR.getMessage() + ":(" + statusCode + ")");
			        		setResult(msgKey, msgMap);
						}
			        	
	        		} else {
		        		LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
		        		msgMap.put("resultCode", ValidationCode.REQUIRED.getCode());
		        		msgMap.put("result", ValidationCode.REQUIRED.getClass());
		        		setResult(msgKey, msgMap);
	        		}
	        	} catch(Exception e) {
	        		LOGGER.debug("error:" + e.getMessage());
	        		LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
	        		msgMap.put("resultCode", ValidationCode.EX_API_ERROR.getCode());
	        		msgMap.put("result", ValidationCode.EX_API_ERROR.getMessage());
	        		setResult(msgKey, msgMap);
	        	}
	        } else {
	        	try {
		        	String url = HL_API + apiName + ".json";
		        	//GroupServiceImpl >> getDiagnosticEvalStt 에서 p 파라미터 대신 studId 파라미터를 가지고 호출 
	        		//studId 추출 코드 예외 추가		        	
		        	if(apiName.equals("inspecion-present") && paramMap.containsKey("studId")) {
//		        		url = "https://sem.home-learn.com/sigong/cldsvc/admsys/v1/ai/" + apiName + ".json";		//임시 라이브 api test
		        		paramMap.put("stuId", paramMap.get("studId"));
		        		paramMap.remove("studId");		
		        		paramMap.remove("s");		
		        	} else if(apiName.equals("act-element-detail") && paramMap.containsKey("studId")) {
//		        		url = "https://sem.home-learn.com/sigong/cldsvc/admsys/v1/ai/" + apiName + ".json";		//임시 라이브 api test
		        		paramMap.put("stuId", paramMap.get("studId"));
		        		paramMap.remove("studId");	
		        		paramMap.remove("s");	        		
		        	} else if(!apiName.equals("step-list/study-goal-text")) {
			    		String studId = "";
			    		String encodedStr = paramMap.get("p").toString();
			    		
			    		String[] paramList = hamsSalesServiceImpl.getDecodedParam(encodedStr);
			    		studId = paramList[1];
			    		
			    		if(apiName.equals("multi-intel-inspection") || apiName.equals("parent-nurture-attitude-inspection")) {
			    			paramMap.put("userId", studId);
			    		} else {
			    			paramMap.put("stuId", studId);
			    		}
			    		
			    		paramMap.remove("p");
		        	}
			    		
		        	//파라미터 세팅
		        	UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
		        	for( String key : paramMap.keySet() ){ 
		        		builder.queryParam(key, paramMap.get(key));
		        	}
		        	
		        	URI apiUri = builder.build().encode().toUri();  
		        	
		        	LinkedHashMap responseData = restTemplate.getForObject(apiUri, LinkedHashMap.class);

		        	LOGGER.debug("apiUri : " + apiUri);
		        	LOGGER.debug("code : " + responseData.get("code"));
		        	LOGGER.debug("message : " + responseData.get("message"));
		        	LOGGER.debug("data : " + responseData.get("data"));
		        	
		        	if("200".equals(responseData.get("code").toString())) {
		        		setResult(dataKey, responseData.get("data"));
		        	} else {
		        		LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
		        		msgMap.put("resultCode", ValidationCode.EX_API_ERROR.getCode());
		        		msgMap.put("result", ValidationCode.EX_API_ERROR.getMessage() + ":(" + responseData.get("code") + ")" + responseData.get("message"));
		        		setResult(msgKey, msgMap);
		        	}
	        	} catch(Exception e) {
	        		LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
	        		msgMap.put("resultCode", ValidationCode.EX_API_ERROR.getCode());
	        		msgMap.put("result", ValidationCode.EX_API_ERROR.getMessage());
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
