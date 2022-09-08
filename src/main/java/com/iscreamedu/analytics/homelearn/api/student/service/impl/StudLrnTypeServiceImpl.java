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
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
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

    // for QA
    private LinkedHashMap<String, Object> apiResult;
    
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
		        } else { // For QA
	            	studInfoParamMap.put("apiName", "aiReport.");
	            	studInfoMap = (Map<String, Object>) callExApi(studInfoParamMap).get("data");
	            	
	            	if(studInfoMap != null) {
	            		int lrnTypeCdApi = 10003;
	            		try {
	            			lrnTypeCdApi = Integer.parseInt(studInfoMap.get("divCd").toString());
	            		} catch (Exception e) {
	            			System.out.println("Stud Info API Error - OPS");
						}
			        	expStatus = (lrnTypeCdApi == 10004) ? "Y" : "N";
	            	}
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
    			//Map<String, Object> lrnTypeDiffMap = new LinkedHashMap<String, Object>();
    			Map<String, Object> lrnTypeDetailMap = new LinkedHashMap<String, Object>();
    			//Map<String, Object> lrnTypeHelpMap = new LinkedHashMap<String, Object>();
    			Map<String, Object> lrnTypePopupMap = new LinkedHashMap<String, Object>();
    			
    			lrnTypeMap = (Map<String, Object>) studLrnTypeMapper.get(paramMap, "StudLrnTypeMt.getLrnTypeDetail");
    			lrnTypeMsgMap = (Map<String, Object>) studLrnTypeMapper.get(paramMap, "StudLrnTypeMt.getLrnTypeDetailMsg");
    			//lrnTypeInfoMsgMap = (Map<String, Object>) studLrnTypeMapper.get(paramMap, "StudLrnTypeMt.getLrnTypeInfoMsg");
    			
    			if(lrnTypeMap != null) {
    				
    				// 현재 월 및 이전 월 유형 정보
    				lrnTypeInfoMap.put("lrnTypeCd", lrnTypeMap.get("lrnTypeCd"));
    				lrnTypeInfoMap.put("lrnTypeNm", lrnTypeMap.get("lrnTypeNm"));
    				//lrnTypeInfoMap.put("lrnTypeImgUrl", lrnTypeMap.get("lrnTypeImgUrl"));
    				lrnTypeInfoMap.put("prevLrnTypeCd", lrnTypeMap.get("prevLrnTypeCd"));
    				lrnTypeInfoMap.put("prevLrnTypeNm", lrnTypeMap.get("prevLrnTypeNm"));
    				//lrnTypeInfoMap.put("prevLrnTypeImgUrl", lrnTypeMap.get("prevLrnTypeImgUrl"));
    				lrnTypeInfoMap.put("lrnTypeInfoMsg", lrnTypeMsgMap.get("lrnTypeInfoMsg"));
    				
    				// 학습 성향 진단
    				lrnTypeDetailMap.put("strScore", lrnTypeMap.get("strLevel"));
    				lrnTypeDetailMap.put("actScore", lrnTypeMap.get("actLevel"));
    				lrnTypeDetailMap.put("lrnTypeMsg", lrnTypeMsgMap.get("lrnTypeMsg"));
    				lrnTypeDetailMap.put("lrnTypeStrMsg", lrnTypeMsgMap.get("lrnTypeStrMsg"));
    				lrnTypeDetailMap.put("lrnTypeActMsg", lrnTypeMsgMap.get("lrnTypeActMsg"));
    				
    				// 유형 도움말
    				/*lrnTypeHelpMap.put("lrnTypeHelpImg", lrnTypeInfoMsgMap.get("lrnTypeHelpImg"));
    				lrnTypeHelpMap.put("lrnTendHelpMsg", lrnTypeInfoMsgMap.get("lrnTendHelpMsg"));*/
    				
    				// 팝업 데이터
    				lrnTypePopupMap.put("popupLrnTypeCd", lrnTypeMap.get("lrnTypeCd"));
    				//lrnTypePopupMap.put("popupImgUrl", lrnTypeMap.get("lrnTypeImgUrl"));
    				lrnTypePopupMap.put("popupMsg", lrnTypeMsgMap.get("lrnTypePopupMsg"));
    				
    				//전달 비교
    				/*lrnTypeDiffMap.put("lrnTypeLevelDiff", getDiffValue(lrnTypeMap.get("lrnTypeCd"), lrnTypeMap.get("prevLrnTypeCd")));
    				lrnTypeDiffMap.put("actLevelDiff", getDiffValue(lrnTypeMap.get("actLevel"), lrnTypeMap.get("prevActLevel")));
    				lrnTypeDiffMap.put("strLevelDiff", getDiffValue(lrnTypeMap.get("strLevel"), lrnTypeMap.get("prevStrLevel")));*/
    				
    				data.put("lrnType", lrnTypeInfoMap);
    				//data.put("lrnTypeDiff", lrnTypeDiffMap);
    				data.put("lrnTypeInfo", lrnTypeDetailMap);
    				//data.put("lrnTypeHelp", lrnTypeHelpMap);
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
        		
    			//Map<String, Object> lrnTypeInfoMap = new LinkedHashMap<String, Object>();
    			
    			lrnTypeMap = (Map<String, Object>) studLrnTypeMapper.get(paramMap, "StudLrnTypeMt.getLrnTypeDetail");
    			lrnTypeMsgMap = (Map<String, Object>) studLrnTypeMapper.get(paramMap, "StudLrnTypeMt.getLrnTypeDetailMsg");
    			if(lrnTypeMap != null) {
    				
    				data.put("lrnTypeNm", lrnTypeMap.get("lrnTypeNm"));
    				data.put("lrnTypeCd", lrnTypeMap.get("lrnTypeCd"));
    				//lrnTypeInfoMap.put("lrnTypeImgUrl", lrnTypeMap.get("lrnTypeImgUrl"));
    				data.put("lrnTypeInfoMsg", lrnTypeMsgMap.get("lrnTypeInfoMsg"));
    				
    				data.put("strScore", lrnTypeMap.get("strLevel"));
    				data.put("actScore", lrnTypeMap.get("actLevel"));
    				
    				data.put("lrnTypeMsg", lrnTypeMsgMap.get("lrnTypeMsg"));
    				data.put("lrnTypeStrMsg", lrnTypeMsgMap.get("lrnTypeStrMsg"));
    				data.put("lrnTypeActMsg", lrnTypeMsgMap.get("lrnTypeActMsg"));
    				
    				// 유형 경로 변환
    				List<String> actList = (lrnTypeMsgMap.get("actPath") != null) ? Arrays.asList(lrnTypeMsgMap.get("actPath").toString().split(",")) : null;
    				List<String> strList = (lrnTypeMsgMap.get("strPath") != null) ? Arrays.asList(lrnTypeMsgMap.get("strPath").toString().split(",")) : null;
    				
    				data.put("strPath", getConvertLrnTypePath(strList));
    				data.put("actPath", getConvertLrnTypePath(actList));
    				
    				//data.put("lrnTypeInfo", lrnTypeInfoMap);
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
        
        vu.checkRequired(new String[] {"yyyy","p"}, paramMap);
        
        if(vu.isValid()) {
        	getStudId(paramMap);
        	
        	if(decodeResult.isEmpty()) {
        		Calendar beforeMonth = Calendar.getInstance();
                beforeMonth.add(Calendar.MONTH , 0);
                String stringYyyy = new java.text.SimpleDateFormat("yyyy").format(beforeMonth.getTime());
                
        		int curYyyy = Integer.parseInt(stringYyyy);
        		int yyyy = Integer.valueOf(paramMap.get("yyyy").toString());
        		
        		paramMap.put("yyyy", yyyy);
        		paramMap.put("yymm", getHistoryYymm(yyyy, curYyyy));
        		
        		vu1.isYear("yyyy", paramMap.get("yyyy").toString());
        		
        		if(vu1.isValid()) {
        			
        			ArrayList<Map<String, Object>> lrnTypeHistoryList = new ArrayList<>();
        			
        			ArrayList<Map<String, Object>> historyDataList = (ArrayList<Map<String, Object>>) studLrnTypeMapper.getList(paramMap, "StudLrnTypeMt.getLrnTypeHistoryDetail");
        			
        			if(historyDataList != null && historyDataList.size() > 0) {
        				for(Map<String, Object> historyItem : historyDataList) {
        					Map<String,Object> historyMap = new LinkedHashMap<>();
        					
        					historyMap.put("yymm", historyItem.get("yymm"));
        					historyMap.put("lrnTypeCd", historyItem.get("lrnTypeCd"));
        					historyMap.put("lrnTypeNm", historyItem.get("lrnTypeNm"));
        					historyMap.put("lrnTypeInfoMsg", historyItem.get("lrnTypeInfoMsg"));
        					historyMap.put("strScore", historyItem.get("strLevel"));
        					historyMap.put("actScore", historyItem.get("actLevel"));
        					historyMap.put("lrnTypeMsg", historyItem.get("lrnTypeMsg"));
        					historyMap.put("lrnTypeStrMsg", historyItem.get("lrnTypeStrMsg"));
        					historyMap.put("lrnTypeActMsg", historyItem.get("lrnTypeActMsg"));
        					
        					List<String> actList = (historyItem.get("actPath") != null) ? Arrays.asList(historyItem.get("actPath").toString().split(",")) : null;
            				List<String> strList = (historyItem.get("strPath") != null) ? Arrays.asList(historyItem.get("strPath").toString().split(",")) : null;
            				
            				historyMap.put("strPath", getConvertLrnTypePath(strList));
            				historyMap.put("actPath", getConvertLrnTypePath(actList));
            				
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
        int studIds = Integer.parseInt(paramMap.get("studId").toString());
        paramMap.put("studIds", studIds);
        
        Calendar month = Calendar.getInstance();
    	month.add(Calendar.MONTH , -1);
        String stringYymm = new java.text.SimpleDateFormat("yyyyMM").format(month.getTime());
		int yymm = Integer.parseInt(stringYymm);
		
		paramMap.put("yymm", yymm);
        
        data = (Map<String, Object>) studLrnTypeMapper.get(paramMap, "StudLrnTypeMt.getStudLrnTypeStudInfo");
        
        //학생 구분 값 관련 ID
        int studTypeId = (data != null && data.get("studTypeId") != null) ? Integer.parseInt(data.get("studTypeId").toString()) : 0;
        
        Map<String,Object> studInfoParamMap = new HashMap<>();
		String p = encodeStudId("0&"+studId);
    	
    	studInfoParamMap.put("p", p);
    	studInfoParamMap.put("apiName", "aiReport.");
    	studInfoParamMap.put("studId", studId);
        
        LinkedHashMap<String,String> studInfo = new LinkedHashMap<>();
        Map<String,Object> studInfoMap = (Map<String, Object>) externalAPIservice.callExternalAPI(studInfoParamMap).get("data");
        //Map<String,Object> studInfoMap = (Map<String, Object>) callExApi(studInfoParamMap).get("data");
        
        if(studInfoMap != null) {
        	int lrnSttCdApi = Integer.parseInt(studInfoMap.get("statusCd").toString().replace("000", "00"));
        	//int studStatus = (lrnSttCdApi == 1003 || lrnSttCdApi == 1007) ? 1 : 0;
        	String studStatus = (lrnSttCdApi == 1002 || lrnSttCdApi == 1007) ? "진행중" : "진행중단";
    		String studStatusDetail = (lrnSttCdApi == 1002 || lrnSttCdApi == 1007) ? "L" : (lrnSttCdApi == 1008 || lrnSttCdApi == 1009 || lrnSttCdApi == 1010) ? "P" : "E";
    		int studTypeIds = (lrnSttCdApi == 1007) ? 1 : (lrnSttCdApi == 1002) ? 2 : 0;
    		
    		if(data == null) {
    			data = new LinkedHashMap<>();
    			
    			data.put("studId", studId);
    			
            	data.put("lrnTypeCd", "나를 알아 맞춰봐형 물음표");
        		data.put("lrnTypeGroupCd", 0);
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
    
    /*@Override
    public Map getStudLrnTypeInfo(String studId) throws Exception {
        Map<String,Object> data = new LinkedHashMap<>();
        Map<String,Object> studData = new HashMap<>();
        Map<String,Object> paramMap = new HashMap<>();
        
        paramMap.put("studId", studId);
        data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "StudLrnType.getStudLrnTypeInfo");
        
        int studIds = Integer.parseInt(paramMap.get("studId").toString());
        paramMap.put("studIds", studIds);
        Map<String,Object> studGenderMap = (Map<String, Object>) studLrnTypeMapper.get(paramMap, "StudLrnTypeMt.getStudGender");
        String gender = (studGenderMap != null && studGenderMap.get("gender") != null) ? studGenderMap.get("gender").toString() : null;
        
        if(data != null) {
        	data.put("gender", gender);
        }
        
        
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
    }*/


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
		} else {
			int studId = Integer.parseInt(params.get("studId").toString());
			params.put("studId",studId);
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
	
	/*유형 경로 전환*/
	private String getConvertLrnTypePath(List<String> pathList) {
		String convertPath = null;
		
		List<String> convertList = new ArrayList<String>();
		
		if(pathList != null && pathList.size() > 0) {
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
	
	/*유형 히스코리(관리자) 목록 조회 시 필요한 yymm 추출*/
	private int getHistoryYymm(int yyyy, int curYyyy) {
		int yymm = 0;
		
		Calendar MonthCheck = Calendar.getInstance();
		MonthCheck.add(Calendar.MONTH , -2);
		String stringYymm = new java.text.SimpleDateFormat("yyyyMM").format(MonthCheck.getTime());
		String stringYyyy = new java.text.SimpleDateFormat("yyyy").format(MonthCheck.getTime());
		String stringMm = new java.text.SimpleDateFormat("MM").format(MonthCheck.getTime());
		int mm = Integer.parseInt(stringMm);
		
		if(curYyyy == yyyy) {
			yymm = Integer.parseInt(stringYymm);
		} else if(yyyy > curYyyy) {
			if(mm > 1) {
				yymm =  Integer.parseInt(stringYyyy+"12");
			} else {
				yymm =  Integer.parseInt(stringYymm);
			}
		} else {
			yymm =  Integer.parseInt(stringYyyy+"12");
		}
		return yymm;
	}
	
	// for QA
    private Map callExApi(Map<String, Object> paramMap) throws Exception {
    	
    	String apiName = ((String)paramMap.remove("apiName")).replaceAll("\\.", "\\/"); // '.'을 '/'로 변환, 맵에서 삭제
		RestTemplate restTemplate = new RestTemplate();
		
    	try {
        	/*String studId = "";
    		String encodedStr = paramMap.get("p").toString();
    		
    		String[] paramList = getDecodedParam(encodedStr);
    		studId = paramList[1];*/
    		String studId = paramMap.get("studId").toString();
    		paramMap.put("studId", studId);
    		
    		String url = "https://sem.home-learn.com/sigong/clientsvc/admsys/v1/ai/tutor/" + apiName + paramMap.get("studId") + ".json";
        	
        	//파라미터 세팅
        	UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        	builder.queryParam("for", "aiReport");
        	URI apiUri = builder.build().encode().toUri();  
        	
        	LinkedHashMap responseData = restTemplate.getForObject(apiUri, LinkedHashMap.class);
        	
        	LOGGER.debug("code : " + responseData.get("code"));
        	LOGGER.debug("message : " + responseData.get("message"));
        	LOGGER.debug("data : " + responseData.get("data"));
        	
        	if("200".equals(responseData.get("code").toString())) {
        		setApiResult(dataKey, responseData.get("data"));
        	} else {
        		LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
        		msgMap.put("resultCode", ValidationCode.EX_API_ERROR.getCode());
        		msgMap.put("result", "(" + responseData.get("code") + ")" + responseData.get("message"));
        		setApiResult(msgKey, msgMap);
        	}
    	} catch(Exception e) {
    		LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
    		msgMap.put("resultCode", ValidationCode.EX_API_ERROR.getCode());
    		msgMap.put("result", ValidationCode.EX_API_ERROR.getMessage());
    		setApiResult(msgKey, msgMap);
    	}
    	
    	return apiResult;
    }
    
    // for QA
    private void setApiResult(String key, Object data) {
        LinkedHashMap message = new LinkedHashMap();
        apiResult = new LinkedHashMap();

        if(data == null
                || (data instanceof List && ((List)data).size() == 0)
                || (data instanceof Map && ((Map)data).isEmpty())) {
            throw new NoDataException(new Object[] {key,"null",ValidationCode.NO_DATA});
        }
        else if(resultNullCheck((Map)data)) {
            throw new NoDataException(new Object[] {key,"null",ValidationCode.NO_DATA});
        }
        else {
            message.put("resultCode", ValidationCode.SUCCESS.getCode());
            apiResult.put(msgKey, message);
            apiResult.put(dataKey, data);
        }
    }
    
	// for QA
    private String[] getDecodedParam(String encodedParam) throws Exception {
        String[] decodedParamList = null;

        String studId = "";
        String decodedStr = "";

        CipherUtil cp = CipherUtil.getInstance();

        try {
            decodedStr = cp.AES_Decode(encodedParam);
            decodedParamList = decodedStr.split("&");
        } catch (Exception e) {
            LOGGER.debug("HL Parameter Incorrect");
        }

        return decodedParamList;
    }
    
    private boolean resultNullCheck(Map<String,Object> data) {
        int dataSize = data.size(); //data map의 크기
        int count = 0; //inner data들의 null 체킹 횟수
        for(String key : data.keySet()) {
            if(data.get(key) instanceof List) {

                //List일때
                if((((List) data.get(key)).size() == 0)) {
                    count += 1;
                }
            }
            else if(data.get(key) instanceof Map) {
                //Map일때
                if((((Map) data.get(key)).isEmpty())) {
                    count += 1;
                }
                else if(innerResultNullCheck((Map)data.get(key))) {
                    count += 1;
                }
            }
            else if(data.get(key) == null) {
                count += 1;
            }
        }
        if(dataSize == count) {
            return true;
        }
        return false;

    }
    private boolean innerResultNullCheck(Map<String,Object> data) {
        int dataSize = data.size(); //data map의 크기
        int count = 0; //inner data들의 null 체킹 횟수
        for(String key : data.keySet()) {
            if(
                    (data.get(key) instanceof List && (((List) data.get(key)).size() == 0)) ||
                            (data.get(key) instanceof Map && (((Map) data.get(key)).isEmpty()))) {
                count += 1;
            }
            else if(data.get(key) == null) {
                count += 1;
            }
        }
        if(dataSize == count) {
            return true;
        }
        return false;

    }
}
