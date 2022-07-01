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
                String stringYymm = new java.text.SimpleDateFormat("yyyyMM").format(beforeMonth.getTime());
        		int yymm = Integer.parseInt(stringYymm);
    			
    			//paramMap.put("yymm", yymm);
        		paramMap.put("yymm", 202205);
    			
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
        
        vu.checkRequired(new String[] {"p"}, paramMap);
        
        if(vu.isValid()) {
        	getStudId(paramMap);
        	
        	if(decodeResult.isEmpty()) {
        		Calendar beforeMonth = Calendar.getInstance();
                beforeMonth.add(Calendar.MONTH , -1);
                String stringYymm = new java.text.SimpleDateFormat("yyyyMM").format(beforeMonth.getTime());
        		int yymm = Integer.parseInt(stringYymm);
                
        		//paramMap.put("yymm", yymm);
        		paramMap.put("yymm", 202205);
        		
    			Map<String, Object> lrnTypeInfoMap = new LinkedHashMap<String, Object>();
    			Map<String, Object> lrnTypeMap = new LinkedHashMap<String, Object>();
    			Map<String, Object> lrnTypeDiffMap = new LinkedHashMap<String, Object>();
    			Map<String, Object> lrnTypeDetailMap = new LinkedHashMap<String, Object>();
    			
    			lrnTypeMap = (Map<String, Object>) commonMapperLrnType.get(paramMap, "getLrnTypeInfo");
    			//lrnTypeInfoMap = (Map<String, Object>) commonMapperLrnType.get(paramMap, "getLrnTypeInfoYn");
    			
    			if(lrnTypeMap != null) {
    				
    				lrnTypeInfoMap.put("lrnTypeCd", lrnTypeMap.get("lrnTypeCd"));
    				lrnTypeInfoMap.put("lrnTypeNm", lrnTypeMap.get("lrnTypeNm"));
    				lrnTypeInfoMap.put("prevLrnTypeCd", lrnTypeMap.get("prevLrnTypeCd"));
    				lrnTypeInfoMap.put("prevLrnTypeNm", lrnTypeMap.get("prevLrnTypeNm"));
    				lrnTypeInfoMap.put("lrnTypeInfoMsg", lrnTypeMap.get("lrnTypeDef"));
    				
    				lrnTypeDiffMap.put("lrnTypeLevelDiff", lrnTypeMap.get("lrnTypeLevelDiff"));
    				lrnTypeDiffMap.put("actLevelDiff", lrnTypeMap.get("actLevelDiff"));
    				lrnTypeDiffMap.put("actScoreDiff", lrnTypeMap.get("actScoreDiff"));
    				lrnTypeDiffMap.put("strLevelDiff", lrnTypeMap.get("strLevelDiff"));
    				lrnTypeDiffMap.put("strScoreDiff", lrnTypeMap.get("strScoreDiff"));
    				
    				lrnTypeDetailMap.put("actLevel", lrnTypeMap.get("actLevel"));
    				lrnTypeDetailMap.put("actScore", lrnTypeMap.get("actScore"));
    				lrnTypeDetailMap.put("strLevel", lrnTypeMap.get("strLevel"));
    				lrnTypeDetailMap.put("strScore", lrnTypeMap.get("strScore"));
    				lrnTypeDetailMap.put("lrnTypeMsg", lrnTypeMap.get("msg"));
    				lrnTypeDetailMap.put("lrnTypeActMsg", lrnTypeMap.get("actMsg"));
    				lrnTypeDetailMap.put("lrnTypeStrMsg", lrnTypeMap.get("strMsg"));
    			}
    			
    			data.put("lrnType", lrnTypeInfoMap);
    			data.put("lrnTypeDiff", lrnTypeDiffMap);
    			data.put("lrnTypeInfo", lrnTypeDetailMap);
    			
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
    public Map getLrnTypeHistory(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        vu.checkRequired(new String[] {"yymm","p"}, paramMap);
        
        if(vu.isValid()) {
        	getStudId(paramMap);
        	
        	if(decodeResult.isEmpty()) {
        		int yymm = Integer.valueOf(paramMap.get("yymm").toString());
        		
        		paramMap.put("yymm", yymm);
        		
        		vu1.isYearMonth("yymm", paramMap.get("yymm").toString());
        		
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
    public Map getLrnTypePathInfo(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new LinkedHashMap<>();
        
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        vu.checkRequired(new String[] {"p"}, paramMap);
        
        if(vu.isValid()) {
        	getStudId(paramMap);
        	
        	if(decodeResult.isEmpty()) {
        		Calendar beforeMonth = Calendar.getInstance();
                beforeMonth.add(Calendar.MONTH , -1);
                String stringYymm = new java.text.SimpleDateFormat("yyyyMM").format(beforeMonth.getTime());
        		int yymm = Integer.parseInt(stringYymm);
                
        		//paramMap.put("yymm", yymm);
        		paramMap.put("yymm", 202205);
        		
    			Map<String, Object> lrnTypePathMap = new LinkedHashMap<String, Object>();
    			Map<String, Object> lrnTypeMap = new LinkedHashMap<String, Object>();
    			
    			lrnTypeMap = (Map<String, Object>) commonMapperLrnType.get(paramMap, "getLrnTypePath");
    			
    			if(lrnTypeMap != null) {
    				
    				List<String> actList = Arrays.asList(lrnTypeMap.get("actPath").toString().split(","));
    				List<String> strList = Arrays.asList(lrnTypeMap.get("strPath").toString().split(","));
    				
    				data.put("lrnTypeLevel", lrnTypeMap.get("lrnTypeLevel"));
    				data.put("actPathList", actList);
    				data.put("strPathList", strList);
    			}
    			
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
    public Map getLrnTypeDetailForAdmin(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new LinkedHashMap<>();
        
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        vu.checkRequired(new String[] {"p"}, paramMap);
        
        if(vu.isValid()) {
        	getStudId(paramMap);
        	
        	if(decodeResult.isEmpty()) {
        		Calendar beforeMonth = Calendar.getInstance();
                beforeMonth.add(Calendar.MONTH , -1);
                String stringYymm = new java.text.SimpleDateFormat("yyyyMM").format(beforeMonth.getTime());
        		int yymm = Integer.parseInt(stringYymm);
        		
        		//paramMap.put("yymm", yymm);
        		paramMap.put("yymm", 202205);
        		
    			Map<String, Object> lrnTypeInfoMap = new LinkedHashMap<String, Object>();
    			Map<String, Object> lrnTypeMap = new LinkedHashMap<String, Object>();
    			Map<String, Object> lrnTypeDetailMap = new LinkedHashMap<String, Object>();
    			Map<String, Object> lrnTypeActDetailMap = new LinkedHashMap<String, Object>();
    			Map<String, Object> lrnTypeStrDetailMap = new LinkedHashMap<String, Object>();
    			
    			lrnTypeMap = (Map<String, Object>) commonMapperLrnType.get(paramMap, "getLrnTypeInfoForAdmin");
    			//lrnTypeInfoMap = (Map<String, Object>) commonMapperLrnType.get(paramMap, "getLrnTypeInfoYn");
    			
    			if(lrnTypeMap != null) {
    				
    				lrnTypeInfoMap.put("lrnTypeNm", lrnTypeMap.get("lrnTypeNm"));
    				lrnTypeInfoMap.put("lrnTypeInfoMsg", lrnTypeMap.get("lrnTypeDef"));
    				lrnTypeInfoMap.put("strScore", lrnTypeMap.get("strScore"));
    				lrnTypeInfoMap.put("actScore", lrnTypeMap.get("actScore"));
    				lrnTypeInfoMap.put("lrnTypeMsg", lrnTypeMap.get("msg"));
    				lrnTypeInfoMap.put("lrnTypeStrMsg", lrnTypeMap.get("strMsg"));
    				lrnTypeInfoMap.put("lrnTypeActMsg", lrnTypeMap.get("actMsg"));
    				lrnTypeInfoMap.put("strPath", lrnTypeMap.get("strPath"));
    				lrnTypeInfoMap.put("actPath", lrnTypeMap.get("actPath"));
    				
    				
    				lrnTypeActDetailMap.put("dtCnt", 31);
    				lrnTypeActDetailMap.put("attCnt", 28);
    				lrnTypeActDetailMap.put("attLevel", 4);
    				lrnTypeActDetailMap.put("attScore", 1);
    				lrnTypeActDetailMap.put("planCnt", 19);
    				lrnTypeActDetailMap.put("fnshCnt", 19);
    				lrnTypeActDetailMap.put("exRtLevel", 5);
    				lrnTypeActDetailMap.put("exRtScore", 1.25);
    				lrnTypeActDetailMap.put("aLrnCnt", 0);
    				lrnTypeActDetailMap.put("tLrnCnt", 9);
    				lrnTypeActDetailMap.put("dLrnCnt", 2);
    				lrnTypeActDetailMap.put("nLrnCnt", 20);
    				lrnTypeActDetailMap.put("lrnHabitLevel", 3);
    				lrnTypeActDetailMap.put("lrnHabitScore", 0.75);
    				lrnTypeActDetailMap.put("incrtNtCnt", 30);
    				lrnTypeActDetailMap.put("incrtNtFnshCnt", 12);
    				lrnTypeActDetailMap.put("incrtNtNcCnt", 18);
    				lrnTypeActDetailMap.put("incrtNtLevel", 2);
    				lrnTypeActDetailMap.put("incrtNtScore", 0.5);
    				lrnTypeActDetailMap.put("totalScore", 3.5);
    				
    				
    				lrnTypeStrDetailMap.put("quesCnt", 20);
    				lrnTypeStrDetailMap.put("skipQuesCnt", 3);
    				lrnTypeStrDetailMap.put("cursoryQuesCnt", 2);
    				lrnTypeStrDetailMap.put("mistakeQuesCnt", 4);
    				lrnTypeStrDetailMap.put("crtQuesCnt", 5);
    				lrnTypeStrDetailMap.put("guessQuesCnt", 6);
    				lrnTypeStrDetailMap.put("slvHabitLevel", 4);
    				lrnTypeStrDetailMap.put("slvHabitScore", 1.5);
    				lrnTypeStrDetailMap.put("totalScore", 1.5);
    				
    				lrnTypeDetailMap.put("lrnTypeAct", lrnTypeActDetailMap);
    				lrnTypeDetailMap.put("lrnTypeStr", lrnTypeStrDetailMap);
    				
    				data.put("lrnTypeInfo", lrnTypeInfoMap);
    				data.put("lrnTypeDetail", lrnTypeDetailMap);
    			}
    			
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
    public Map getLrnTypeHistoryForAdmin(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        vu.checkRequired(new String[] {"yymm","p"}, paramMap);
        
        if(vu.isValid()) {
        	getStudId(paramMap);
        	
        	if(decodeResult.isEmpty()) {
        		int yymm = Integer.valueOf(paramMap.get("yymm").toString());
        		
        		paramMap.put("yymm", yymm);
        		
        		vu1.isYearMonth("yymm", paramMap.get("yymm").toString());
        		
        		if(vu1.isValid()) {
        			
        			ArrayList<Map<String, Object>> lrnTypeHistoryList = new ArrayList<>();
        			
        			ArrayList<Map<String, Object>> historyDataList = (ArrayList<Map<String, Object>>) commonMapperLrnType.getList(paramMap, "getLrnTypeHistoryForAdmin");
        			
        			if(historyDataList != null && historyDataList.size() > 0) {
        				for(Map<String, Object> historyItem : historyDataList) {
        					Map<String,Object> historyMap = new LinkedHashMap<>();
        					
        					historyMap.put("dt", historyItem.get("dt"));
        					historyMap.put("lrnTypeLevel", historyItem.get("lrnTypeLevel"));
        					historyMap.put("lrnTypeImg", historyItem.get("lrnTypeImg"));
        					historyMap.put("lrnTypeNm", historyItem.get("lrnTypeNm"));
        					historyMap.put("lrnTypeInfoMsg", historyItem.get("lrnTypeDef"));
        					historyMap.put("strScore", historyItem.get("strScore"));
        					historyMap.put("actScore", historyItem.get("actScore"));
        					historyMap.put("lrnTypeMsg", historyItem.get("msg"));
        					historyMap.put("lrnTypeStrMsg", historyItem.get("strMsg"));
        					historyMap.put("lrnTypeActMsg", historyItem.get("actMsg"));
        					historyMap.put("strPath", historyItem.get("strPath"));
        					historyMap.put("actPath", historyItem.get("actPath"));
        					
        					lrnTypeHistoryList.add(historyMap);
        				}
        			}
        			
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
        Map<String,Object> data = new HashMap<>();
        Map<String,Object> paramMap = new HashMap<>();
        
        paramMap.put("studId", studId);
        
        /*QA 계정용 로직 > QA 진행 완료 후 주석 필요 - 오희택*/
        /*String[] dkt_check_stud = {"2083366", "2083367", "2083374", "2083378", "2083381", "2083377"};
        List<String> dktCheckStudList = Arrays.asList(dkt_check_stud);
        
        if(!dktCheckStudList.contains(String.valueOf(studId))) {
        }*/
        data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "getStudLrnTypeInfo");
        			
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
