package com.iscreamedu.analytics.homelearn.api.student.service.impl;

import com.iscreamedu.analytics.homelearn.api.common.security.CipherUtil;
import com.iscreamedu.analytics.homelearn.api.common.service.ExternalAPIService;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationCode;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtil;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtilTutor;
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
import java.time.LocalDateTime;
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
    CommonMapperLrnDm studLrnTypeMapper;
    
    @Autowired
	ExternalAPIService externalAPIservice;

    @Override
    public Map getLrnTypeCheck(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
    	
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        getStudId(paramMap);
        
        vu.checkRequired(new String[] {"studId"}, paramMap);
        
        if(vu.isValid()) {
        	if(decodeResult.isEmpty()) {
                Calendar beforeMonth = Calendar.getInstance();
                beforeMonth.add(Calendar.MONTH , -1);
                String stringYymm = new java.text.SimpleDateFormat("yyyyMM").format(beforeMonth.getTime());
        		int yymm = Integer.parseInt(stringYymm);
    			
        		String expStatus = "N";
        		
    			paramMap.put("yymm", yymm);
    			
    			data = (Map<String, Object>) studLrnTypeMapper.get(paramMap, "StudLrnTypeMt.getLrnTypeCheck");
				
    			Map<String,Object> studInfoParamMap = new HashMap<>();
				String p = encodeStudId("0&"+paramMap.get("studId"));
		    	
		    	studInfoParamMap.put("p", p);
		    	studInfoParamMap.put("apiName", "aiReport.");
		        
		        LinkedHashMap<String,String> studInfo = new LinkedHashMap<>();
		        Map<String,Object> studInfoMap = (Map<String, Object>) externalAPIservice.callExternalAPI(studInfoParamMap).get("data");
		        
		        if(studInfoMap != null) {
		        	int lrnTypeCdApi = Integer.parseInt(studInfoMap.get("divCd").toString());
		        	//int studStatus = (lrnSttCdApi == 1003 || lrnSttCdApi == 1007) ? 1 : 0;
		        	expStatus = (lrnTypeCdApi == 10004) ? "Y" : "N";
		        }
    			
		        data.put("expYn", expStatus);
		    
		        
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
        
        getStudId(paramMap);
        
        vu.checkRequired(new String[] {"studId"}, paramMap);
        
        if(vu.isValid()) {
        	if(decodeResult.isEmpty()) {
        		Calendar beforeMonth = Calendar.getInstance();
                beforeMonth.add(Calendar.MONTH , -1);
                String stringYymm = new java.text.SimpleDateFormat("yyyyMM").format(beforeMonth.getTime());
        		int yymm = Integer.parseInt(stringYymm);
        		
        		paramMap.put("yymm", yymm);
        		
        		Map<String, Object> lrnTypeMap = new LinkedHashMap<String, Object>();
        		Map<String, Object> lrnTypeMsgMap = new LinkedHashMap<String, Object>();
        		Map<String, Object> lrnTypeInfoMsgMap = new LinkedHashMap<String, Object>();
        		
    			Map<String, Object> lrnTypeInfoMap = new LinkedHashMap<String, Object>();
    			Map<String, Object> lrnTypeDiffMap = new LinkedHashMap<String, Object>();
    			Map<String, Object> lrnTypeDetailMap = new LinkedHashMap<String, Object>();
    			Map<String, Object> lrnTypeHelpMap = new LinkedHashMap<String, Object>();
    			Map<String, Object> lrnTypePopupMap = new LinkedHashMap<String, Object>();
    			
    			lrnTypeMap = (Map<String, Object>) studLrnTypeMapper.get(paramMap, "StudLrnTypeMt.getLrnTypeDetail");
    			try {
    				lrnTypeMsgMap = (Map<String, Object>) studLrnTypeMapper.get(paramMap, "StudLrnTypeMt.getLrnTypeDetailMsg");
    			} catch (Exception e) {
    				System.out.println( LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")) + " getLrnTypeDetailMsg Error");
    				lrnTypeMsgMap.put("lrnTypeInfoMsg", null);
    				lrnTypeMsgMap.put("lrnTypeMsg", null);
    				lrnTypeMsgMap.put("lrnTypeActMsg", null);
    				lrnTypeMsgMap.put("lrnTypeStrMsg", null);
    				lrnTypeMsgMap.put("lrnTypePopupMsg", null);
				}
    			lrnTypeInfoMsgMap = (Map<String, Object>) studLrnTypeMapper.get(paramMap, "StudLrnTypeMt.getLrnTypeInfoMsg");
    			
    			if(lrnTypeMap != null) {
    				
    				// 현재 월 및 이전 월 유형 정보
    				lrnTypeInfoMap.put("lrnTypeCd", lrnTypeMap.get("lrnTypeCd"));
    				lrnTypeInfoMap.put("lrnTypeNm", lrnTypeMap.get("lrnTypeNm"));
    				lrnTypeInfoMap.put("lrnTypeImgUrl", lrnTypeMap.get("lrnTypeImgUrl"));
    				lrnTypeInfoMap.put("prevLrnTypeCd", lrnTypeMap.get("prevLrnTypeCd"));
    				lrnTypeInfoMap.put("prevLrnTypeNm", lrnTypeMap.get("prevLrnTypeNm"));
    				lrnTypeInfoMap.put("prevLrnTypeImgUrl", lrnTypeMap.get("prevLrnTypeImgUrl"));
    				lrnTypeInfoMap.put("lrnTypeInfoMsg", lrnTypeMsgMap.get("lrnTypeInfoMsg"));
    				
    				// 학습 성향 진단
    				lrnTypeDetailMap.put("actLevel", lrnTypeMap.get("actLevel"));
    				lrnTypeDetailMap.put("strLevel", lrnTypeMap.get("strLevel"));
    				lrnTypeDetailMap.put("lrnTypeMsg", lrnTypeMsgMap.get("lrnTypeMsg"));
    				lrnTypeDetailMap.put("lrnTypeActMsg", lrnTypeMsgMap.get("lrnTypeActMsg"));
    				lrnTypeDetailMap.put("lrnTypeStrMsg", lrnTypeMsgMap.get("lrnTypeStrMsg"));
    				
    				// 유형 도움말
    				lrnTypeHelpMap.put("lrnTypeHelpImg", lrnTypeInfoMsgMap.get("lrnTypeHelpImg"));
    				lrnTypeHelpMap.put("lrnTendHelpMsg", lrnTypeInfoMsgMap.get("lrnTendHelpMsg"));
    				
    				// 팝업 데이터
    				lrnTypePopupMap.put("popupLrnTypeCd", lrnTypeMap.get("lrnTypeCd"));
    				lrnTypePopupMap.put("popupImgUrl", lrnTypeMap.get("lrnTypeImgUrl"));
    				lrnTypePopupMap.put("popupMsg", lrnTypeMsgMap.get("lrnTypePopupMsg"));
    				
    				//전달 비교
    				lrnTypeDiffMap.put("lrnTypeLevelDiff", getDiffValue(lrnTypeMap.get("lrnTypeCd"), lrnTypeMap.get("prevLrnTypeCd")));
    				lrnTypeDiffMap.put("actLevelDiff", getDiffValue(lrnTypeMap.get("actLevel"), lrnTypeMap.get("prevActLevel")));
    				lrnTypeDiffMap.put("strLevelDiff", getDiffValue(lrnTypeMap.get("strLevel"), lrnTypeMap.get("prevStrLevel")));
    				
    				data.put("lrnType", lrnTypeInfoMap);
    				data.put("lrnTypeDiff", lrnTypeDiffMap);
    				data.put("lrnTypeInfo", lrnTypeDetailMap);
    				data.put("lrnTypeHelp", lrnTypeHelpMap);
    				data.put("lrnTypePopup", lrnTypePopupMap);
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
    public Map getLrnTypeHistory(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        getStudId(paramMap);
        
        vu.checkRequired(new String[] {"studId"}, paramMap);
        //vu.checkRequired(new String[] {"yymm","studId"}, paramMap);
        
        if(vu.isValid()) {
        	if(decodeResult.isEmpty()) {
        		Calendar beforeMonth = Calendar.getInstance();
                beforeMonth.add(Calendar.MONTH , -1);
                String stringYymm = new java.text.SimpleDateFormat("yyyyMM").format(beforeMonth.getTime());
        		int yymm = Integer.parseInt(stringYymm);
        		
        		paramMap.put("yymm", yymm);
        		
        		ArrayList<Map<String, Object>> lrnTypeHistoryList = new ArrayList<>();
        		
        		lrnTypeHistoryList = (ArrayList<Map<String, Object>>) studLrnTypeMapper.getList(paramMap, "StudLrnTypeMt.getLrnTypeHistory");
        		
        		data.put("lrnTypeList", lrnTypeHistoryList);
        		
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
    public Map getLrnTypePathInfo(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new LinkedHashMap<>();
        
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        getStudId(paramMap);
        
        vu.checkRequired(new String[] {"studId"}, paramMap);
        
        if(vu.isValid()) {
        	if(decodeResult.isEmpty()) {
        		Calendar beforeMonth = Calendar.getInstance();
                beforeMonth.add(Calendar.MONTH , -1);
                String stringYymm = new java.text.SimpleDateFormat("yyyyMM").format(beforeMonth.getTime());
        		int yymm = Integer.parseInt(stringYymm);
                
        		paramMap.put("yymm", yymm);
        		paramMap.put("lrnTypeCd", null);
        		
    			Map<String, Object> lrnTypePathMap = new LinkedHashMap<String, Object>();
    			Map<String, Object> lrnTypeMap = new LinkedHashMap<String, Object>();
    			
    			lrnTypeMap = (Map<String, Object>) studLrnTypeMapper.get(paramMap, "StudLrnTypeMt.getLrnTypePath");
    			
    			if(lrnTypeMap != null) {
    				
    				List<String> actList = Arrays.asList(lrnTypeMap.get("actPath").toString().split(","));
    				List<String> strList = Arrays.asList(lrnTypeMap.get("strPath").toString().split(","));
    				
    				data.put("lrnTypeLevel", lrnTypeMap.get("lrnTypeCd"));
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
        		
        		paramMap.put("yymm", yymm);
        		
        		Map<String, Object> lrnTypeMap = new LinkedHashMap<String, Object>();
        		Map<String, Object> lrnTypeMsgMap = new LinkedHashMap<String, Object>();
        		
    			Map<String, Object> lrnTypeInfoMap = new LinkedHashMap<String, Object>();
    			
    			lrnTypeMap = (Map<String, Object>) studLrnTypeMapper.get(paramMap, "StudLrnTypeMt.getLrnTypeDetail");
    			try {
    				lrnTypeMsgMap = (Map<String, Object>) studLrnTypeMapper.get(paramMap, "StudLrnTypeMt.getLrnTypeDetailMsg");
    			} catch (Exception e) {
    				System.out.println( LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")) + " getLrnTypeDetailMsg Error");
    				lrnTypeMsgMap.put("lrnTypeInfoMsg", null);
    				lrnTypeMsgMap.put("lrnTypeMsg", null);
    				lrnTypeMsgMap.put("lrnTypeActMsg", null);
    				lrnTypeMsgMap.put("lrnTypeStrMsg", null);
    				lrnTypeMsgMap.put("actPath", "-");
    				lrnTypeMsgMap.put("strPath", "-");
				}
    			//lrnTypeInfoMap = (Map<String, Object>) commonMapperLrnType.get(paramMap, "getLrnTypeInfoYn");
    			
    			if(lrnTypeMap != null) {
    				
    				lrnTypeInfoMap.put("lrnTypeNm", lrnTypeMap.get("lrnTypeNm"));
    				lrnTypeInfoMap.put("lrnTypeCd", lrnTypeMap.get("lrnTypeCd"));
    				lrnTypeInfoMap.put("lrnTypeImgUrl", lrnTypeMap.get("lrnTypeImgUrl"));
    				lrnTypeInfoMap.put("lrnTypeInfoMsg", lrnTypeMsgMap.get("lrnTypeInfoMsg"));
    				
    				lrnTypeInfoMap.put("strLevel", lrnTypeMap.get("strLevel"));
    				lrnTypeInfoMap.put("actLevel", lrnTypeMap.get("actLevel"));
    				
    				lrnTypeInfoMap.put("lrnTypeMsg", lrnTypeMsgMap.get("lrnTypeMsg"));
    				lrnTypeInfoMap.put("lrnTypeStrMsg", lrnTypeMsgMap.get("lrnTypeStrMsg"));
    				lrnTypeInfoMap.put("lrnTypeActMsg", lrnTypeMsgMap.get("lrnTypeActMsg"));
    				
    				List<String> actList = Arrays.asList(lrnTypeMsgMap.get("actPath").toString().split(","));
    				List<String> strList = Arrays.asList(lrnTypeMsgMap.get("strPath").toString().split(","));
    				
    				
    				lrnTypeInfoMap.put("strPath", getConvertLrnTypePath(strList));
    				lrnTypeInfoMap.put("actPath", getConvertLrnTypePath(actList));
    				
    				data.put("lrnTypeInfo", lrnTypeInfoMap);
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
        		
        		paramMap.put("studId", 3095);
        		
        		vu1.isYearMonth("yymm", paramMap.get("yymm").toString());
        		
        		if(vu1.isValid()) {
        			
        			ArrayList<Map<String, Object>> lrnTypeHistoryList = new ArrayList<>();
        			
        			ArrayList<Map<String, Object>> historyDataList = (ArrayList<Map<String, Object>>) studLrnTypeMapper.getList(paramMap, "StudLrnTypeMt.getLrnTypeHistoryForAdmin");
        			
        			if(historyDataList != null && historyDataList.size() > 0) {
        				for(Map<String, Object> historyItem : historyDataList) {
        					Map<String,Object> historyMap = new LinkedHashMap<>();
        					
        					historyMap.put("dt", historyItem.get("dt"));
        					historyMap.put("lrnTypeLevel", historyItem.get("lrnTypeLevel"));
        					historyMap.put("lrnTypeCd", historyItem.get("lrnTypeCd"));
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
        Map<String,Object> data = new LinkedHashMap<>();
        Map<String,Object> studData = new HashMap<>();
        Map<String,Object> paramMap = new HashMap<>();
        
        paramMap.put("studId", studId);
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
        	String studStatus = (lrnSttCdApi == 1002 || lrnSttCdApi == 1007) ? "진행중" : "진행중단";
    		String studStatusDetail = (lrnSttCdApi == 1002 || lrnSttCdApi == 1007) ? "L" : (lrnSttCdApi == 1008 || lrnSttCdApi == 1009 || lrnSttCdApi == 1010) ? "P" : "E";
    		int studTypeIds = (lrnSttCdApi == 1007) ? 1 : (lrnSttCdApi == 1002) ? 2 : 0;
    		
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
	
	private int getDiffValue(Object firstValue, Object secondValue) throws Exception {
		int diffFirstValue = (firstValue != null) ? Integer.parseInt(firstValue.toString()) : 0;
		int diffSecondValue = (secondValue != null) ? Integer.parseInt(secondValue.toString()) : 0;
		
		int diffValue = diffFirstValue - diffSecondValue;
		
		return diffValue;
	}
	
	private String getConvertLrnTypePath(List<String> pathList) {
		String convertPath = "-";
		
		List<String> convertList = new ArrayList<String>();
		
		if(pathList.size() > 0) {
			for(String item : pathList) {
				if(!item.equals("-")) {
					item = "유형 " + item;
				}
				convertList.add(item);
			}
			
			if(convertList.size() > 0) {
				convertPath = String.join(" -> ", convertList);
			}
		}
		
		return convertPath;
	}
}
