package com.iscreamedu.analytics.homelearn.api.student.service.impl;

import com.iscreamedu.analytics.homelearn.api.common.security.CipherUtil;
import com.iscreamedu.analytics.homelearn.api.common.service.ExternalAPIService;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationCode;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtil;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtilTutor;
import com.iscreamedu.analytics.homelearn.api.student.service.StudLrnAnalService;
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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class StudLrnAnalServiceImpl implements StudLrnAnalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudLrnAnalServiceImpl.class);

    private LinkedHashMap<String, Object> result;
    private LinkedHashMap<String, Object> decodeResult;
    private String msgKey = "msg";
    private String dataKey = "data";
    
    // for QA
    private LinkedHashMap<String, Object> apiResult;

    @Autowired
    CommonMapperLrnDm studLrnAnalMapper;
    
    @Autowired
    CommonMapperLrnType commonMapperLrnType;
    
    @Autowired
	ExternalAPIService externalAPIservice;
    
    @Override
    public Map getYymmwk(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new LinkedHashMap<>();
    	
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        vu.checkRequired(new String[] {"p"}, paramMap);
        
        if(vu.isValid()) {
        	getStudId(paramMap);
        	
        	if(decodeResult.isEmpty()) {
        		
        		Map<String, Object> yymmwkMap = new LinkedHashMap<>();
				Map<String, Object> yymmwkDataMap = new LinkedHashMap<>();
				
				yymmwkDataMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getYymmwk");
				
				int yyData = Integer.parseInt(yymmwkDataMap.get("yyyymmKey").toString().substring(0, 4));
				int mmData = Integer.parseInt(yymmwkDataMap.get("yyyymmKey").toString().substring(4, 6));
				
				data.put("yyyy", yyData);
				data.put("mm", mmData);
				data.put("wk", yymmwkDataMap.get("wk"));
				
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
    public Map getYymm(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new LinkedHashMap<>();
    	
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        vu.checkRequired(new String[] {"p"}, paramMap);
        
        if(vu.isValid()) {
        	getStudId(paramMap);
        	
        	if(decodeResult.isEmpty()) {
        		
        		Map<String, Object> yymmwkMap = new LinkedHashMap<>();
				Map<String, Object> yymmwkDataMap = new LinkedHashMap<>();
				
				yymmwkDataMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getYymm");
				
				int yyData = Integer.parseInt(yymmwkDataMap.get("yyyymmKey").toString().substring(0, 4));
				int mmData = Integer.parseInt(yymmwkDataMap.get("yyyymmKey").toString().substring(4, 6));
				
				data.put("yyyy", yyData);
				data.put("mm", mmData);
				
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
    public Map getStudInfo(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new LinkedHashMap<>();
        Map<String,Object> studInfoParamMap = new LinkedHashMap<>();
        
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        vu.checkRequired(new String[] {"token"}, paramMap);
        
        if(vu.isValid()) {
        	paramMap.put("apiName", "studAuth");
        	
        	Map<String, Object> externalApiMap =  (Map<String, Object>) externalAPIservice.callExternalAPI(paramMap).get("data");
        	
        	String userId = (externalApiMap != null && externalApiMap.get("userId") != null) ? externalApiMap.get("userId").toString() : null;
        	int studId = (externalApiMap != null && externalApiMap.get("userId") != null) ? Integer.parseInt(externalApiMap.get("userId").toString()) : 0;
        	String p = encodeStudId("0&"+userId);
        	
        	studInfoParamMap.put("p", p);
        	studInfoParamMap.put("apiName", "aiReport.");
            
            LinkedHashMap<String,String> studInfo = new LinkedHashMap<>();
            Map<String,Object> studInfoMap = (Map<String, Object>) externalAPIservice.callExternalAPI(studInfoParamMap).get("data");
            
            String decodeStudId = encodeStudId(userId);
            
            paramMap.put("studId", studId);
            
            Map<String,Object> studData = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getStudInfo");
            Map<String,Object> studRecentData = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getStudRecentReport");
            
        	data.put("p", decodeStudId);
        	data.put("studId", studInfoMap.get("stuId"));
        	data.put("studNm", studInfoMap.get("name"));
        	data.put("gender", studInfoMap.get("gender"));
        	data.put("grade", studInfoMap.get("grade"));
        	data.put("studType", studInfoMap.get("divCdNm"));
        	data.put("sttDt", studData.get("sttDt"));
        	data.put("recentReport", studRecentData.get("recentReport"));
        	
        	setResult(dataKey,data);
        } else {
        	setResult(msgKey, vu.getResult());
        }
	
	    return result;
    }
    
    @Override
    public Map getStudInfoForTchr(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new LinkedHashMap<>();
        Map<String,Object> studInfoParamMap = new LinkedHashMap<>();
        
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        vu.checkRequired(new String[] {"p"}, paramMap);
        
        if(vu.isValid()) {
        	getStudId(paramMap);
        	
        	studInfoParamMap.put("p", paramMap.get("p"));
        	studInfoParamMap.put("apiName", "aiReport.");
            
            LinkedHashMap<String,String> studInfo = new LinkedHashMap<>();
            //Map<String,Object> studInfoMap = (Map<String, Object>) externalAPIservice.callExternalAPI(studInfoParamMap).get("data");
            Map<String,Object> studInfoMap = (Map<String, Object>) callExApi(studInfoParamMap).get("data");
            
            String studId = (paramMap.get("studId") != null) ? paramMap.get("studId").toString() : null;
            
            String decodeStudId = encodeStudId(studId);
            
            paramMap.put("studId", paramMap.get("studId"));
            
            Map<String,Object> studData = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getStudInfo");
            Map<String,Object> studRecentData = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getStudRecentReport");
            
        	data.put("p", decodeStudId);
        	data.put("studId", studInfoMap.get("stuId"));
        	data.put("studNm", studInfoMap.get("name"));
        	data.put("gender", studInfoMap.get("gender"));
        	data.put("grade", studInfoMap.get("grade"));
        	data.put("studType", studInfoMap.get("divCdNm"));
        	data.put("sttDt", studData.get("sttDt"));
        	data.put("recentReport", studRecentData.get("recentReport"));
        	
        	setResult(dataKey,data);
        } else {
        	setResult(msgKey, vu.getResult());
        }
	
	    return result;
    }
    
    @Override
    public Map getYymmwkList(Map<String, Object> paramMap) throws Exception {
    	ArrayList<Map<String, Object>> data = new ArrayList<>();
    	
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        vu.checkRequired(new String[] {"p"}, paramMap);
        
        if(vu.isValid()) {
        	getStudId(paramMap);
        	
        	if(decodeResult.isEmpty()) {
				
				data = (ArrayList<Map<String, Object>>) studLrnAnalMapper.getList(paramMap, "StudReport.getYymmwkList");
				
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
    public Map getReportList(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new LinkedHashMap<>();
    	
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        vu.checkRequired(new String[] {"p"}, paramMap);
        
        if(vu.isValid()) {
        	getStudId(paramMap);
        	
        	if(decodeResult.isEmpty()) {
        		Map<String, Object> monthMap = new LinkedHashMap<>();
				ArrayList<Map<String, Object>> reportList = new ArrayList<>();
				//Map<String, Object> monthDataMap = new LinkedHashMap<>();
				
				ArrayList<Map<String, Object>> reportYymmList = (ArrayList<Map<String, Object>>) studLrnAnalMapper.getList(paramMap, "StudReport.getReportYymmList");
				ArrayList<Map<String, Object>> monthList = (ArrayList<Map<String, Object>>) studLrnAnalMapper.getList(paramMap, "StudReport.getMonthReportList");
				ArrayList<Map<String, Object>> weekList = (ArrayList<Map<String, Object>>) studLrnAnalMapper.getList(paramMap, "StudReport.getWeeklyReportList");
				Map<String,Object> studRecentData = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getStudRecentReport");
				
				int recentYymm = (studRecentData != null) ? Integer.valueOf(studRecentData.get("recentReport").toString()) : 0;
				//monthDataMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getMonthReportYn");
				//weekList = (ArrayList<Map<String, Object>>) studLrnAnalMapper.getList(paramMap, "StudReport.getWeeklyReportYn");
				
				for(Map<String, Object> yymmList : reportYymmList) {
					ArrayList<Map<String, Object>> weekReportList = new ArrayList<>();
					Map<String, Object> reportMap = new LinkedHashMap<>();
					Map<String, Object> monthReportMap = new LinkedHashMap<>();
					
					int reportYymm = Integer.valueOf(yymmList.get("yymm").toString());
					int reportYyyy = Integer.valueOf(yymmList.get("yyyy").toString());
					int reportMm = Integer.valueOf(yymmList.get("mm").toString());
					
					for(Map<String, Object> weekReportItem : weekList) {
						int weekReportYymm = Integer.valueOf(weekReportItem.get("weekReportYymm").toString());
						int weekReportYymmwk = Integer.valueOf(weekReportItem.get("weekReportYymmwk").toString());
						
						if(reportYymm == weekReportYymm) {
							Map<String, Object> weekReportMap = new LinkedHashMap<>();
							
							String checkYn = "Y";
							
							weekReportMap.put("reportNm", weekReportItem.get("reportNm"));
							weekReportMap.put("publishYn", weekReportItem.get("reportYn"));
							
							if(recentYymm == weekReportYymmwk) {
								Map<String, Object> weekReportCheckParamMap = new LinkedHashMap<>();
				    			String recentReportValue = String.valueOf(recentYymm);
				    			
				    			int wk = Integer.parseInt(recentReportValue.substring(6));
			    				int yymm = Integer.parseInt(recentReportValue.substring(0,6));
			    				
			    				weekReportCheckParamMap.put("studId", paramMap.get("studId"));
			    				weekReportCheckParamMap.put("yymm", yymm);
			    				weekReportCheckParamMap.put("wk", wk);
			    				
			    				Map<String, Object> checkWeekDataMap = (Map<String, Object>) commonMapperLrnType.get(weekReportCheckParamMap, "StudLrnType.getReportCheck");
			    				
								String dataCheck = checkWeekDataMap.get("dataCheck").toString();
								
								checkYn = dataCheck;
							}
							
							weekReportMap.put("checkYn", checkYn);
							
							weekReportList.add(weekReportMap);
						}
					}
					
					for(Map<String, Object> monthReportItem : monthList) {
						int monthReportYymm = Integer.valueOf(monthReportItem.get("monthReportYymm").toString());
						
						if(reportYymm == monthReportYymm) {
							String checkYn = "Y";
							
							monthReportMap.put("reportNm", monthReportItem.get("reportNm"));
							monthReportMap.put("publishYn", monthReportItem.get("reportYn"));
							
							if(recentYymm == monthReportYymm) {
								Map<String, Object> monthReportCheckParamMap = new LinkedHashMap<>();
								
								monthReportCheckParamMap.put("studId", paramMap.get("studId"));
								monthReportCheckParamMap.put("yymm", recentYymm);
			    				
								Map<String, Object> checkMonthDataMap = (Map<String, Object>) commonMapperLrnType.get(monthReportCheckParamMap, "StudLrnType.getReportCheck");
								
								String dataCheck = checkMonthDataMap.get("dataCheck").toString();
								
								checkYn = dataCheck;
							}
							
							monthReportMap.put("checkYn", checkYn);
						}
					}
					
					reportMap.put("yyyy", reportYyyy);
					reportMap.put("month", reportMm);
					reportMap.put("monthReport", monthReportMap);
					reportMap.put("weekReportList", weekReportList);
					
					reportList.add(reportMap);
				}
				
				data.put("reportList", reportList);
				
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
    public Map getHomeSummary(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new LinkedHashMap<>();
    	
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vuM = new ValidationUtil();
        ValidationUtil vuW = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        ValidationUtil vu2 = new ValidationUtil();
        
        vu.checkRequired(new String[] {"currCon","p"}, paramMap);
        
        if(vu.isValid()) {
        	String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {
				vuM.checkRequired(new String[] {"yyyy","mm"}, paramMap);
	        	
	        	if(vuM.isValid()) {
	        		getStudId(paramMap);
	        		
	        		if(decodeResult.isEmpty()) {
	        			String yyyy = paramMap.get("yyyy").toString();
	        			int mm = Integer.valueOf(paramMap.get("mm").toString());
	        			String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
	        			
	        			int yymm = Integer.parseInt(yyyy+convertMm);
	        			
	        			paramMap.put("yymm", yymm);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			
	        			if(vu1.isValid()) {
	        				Map<String, Object> learnMap = new LinkedHashMap<String, Object>();
	        				ArrayList<Map<String, Object>> aLrnList = new ArrayList<>();
	        				Map<String, Object> examMap = new LinkedHashMap<String, Object>();
	        				ArrayList<Map<String, Object>> examSubjList = new ArrayList<>();
	        				Map<String, Object> examQuesMap = new LinkedHashMap<String, Object>();
	        				
	        				learnMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getLrnSummary");
	        				aLrnList = (ArrayList<Map<String, Object>>) studLrnAnalMapper.getList(paramMap, "StudReport.getALrnSummaryList");
	        				examSubjList = (ArrayList<Map<String, Object>>) studLrnAnalMapper.getList(paramMap, "StudReport.getExamSubjSummary");
	        				examQuesMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getExamQuesSummary");
	        				
	        				if(learnMap != null) {
	        					learnMap.put("aLrnStt1", null);
	        					learnMap.put("aLrnStt2", null);
	        					learnMap.put("aLrnStt3", null);
	        					
	        					if(aLrnList != null || aLrnList.size() > 0) {
	        						int aLrnItemIndex = 0;
	        						for(Map<String, Object> aLrnItem : aLrnList) {
	        							aLrnItemIndex++;
	        							
	        							String mapKeyNm = "aLrnStt"+aLrnItemIndex;
	        							Map<String, Object> aLrnMap = new HashMap<String, Object>();
	        							
	        							aLrnMap.put("subjNm", aLrnItem.get("subjNm"));
	        							aLrnMap.put("aLrnCnt", aLrnItem.get("aLrnCnt"));
	        							
	        							learnMap.put(mapKeyNm, aLrnMap);
	        						}
	        					}
	        				}
	        				
	        				/*과목별 평가 점수 초기 값*/
	        				examMap.put("examScore", null);
	        				examMap.put("c01examScore", null);
	        				examMap.put("c02examScore", null);
	        				examMap.put("c03examScore", null);
	        				examMap.put("c04examScore", null);
	        				examMap.put("c05examScore", null);
	        				examMap.put("c06examScore", null);
	        				
	        				for(Map<String, Object> examSubjItme : examSubjList) {
	        					String subjCd = (examSubjItme.get("subjCd").toString().equals("ALL")) ? "c00" : examSubjItme.get("subjCd").toString().replace("C", "c");
	        					Object score = (examSubjItme.get("exRt") != null) ? Integer.valueOf(examSubjItme.get("exRt").toString()) : null;
	        					
	        					if(subjCd.equals("c00")) {
	        						examMap.put("examScore", score);
	        					} else {
	        						examMap.put(subjCd+"examScore", score);
	        					}
	        				}
	        				
	        				examMap.put("incrtNoteCnt", (examQuesMap != null && examQuesMap.get("wnoteTotCnt") != null) ? examQuesMap.get("wnoteTotCnt") : null);
	        				examMap.put("incrtNoteFnshCnt", (examQuesMap != null && examQuesMap.get("wnoteFnshCnt") != null) ? examQuesMap.get("wnoteFnshCnt") : null);
	        				examMap.put("incrtNoteNcCnt", (examQuesMap != null && examQuesMap.get("wnoteUnfnshCnt") != null) ? examQuesMap.get("wnoteUnfnshCnt") : null);
	        				examMap.put("quesTotCnt", (examQuesMap != null && examQuesMap.get("quesTotCnt") != null) ? examQuesMap.get("quesTotCnt") : null);
	        				examMap.put("skipQuesCnt", (examQuesMap != null && examQuesMap.get("quesSkipCnt") != null) ? examQuesMap.get("quesSkipCnt") : null);
	        				examMap.put("cursoryQuesCnt", (examQuesMap != null && examQuesMap.get("quesHrryCnt") != null) ? examQuesMap.get("quesHrryCnt") : null);
	        				examMap.put("guessQuesCnt", (examQuesMap != null && examQuesMap.get("quesGussCnt") != null) ? examQuesMap.get("quesGussCnt") : null);
	        				examMap.put("mistakeQuesCnt", (examQuesMap != null && examQuesMap.get("quesMstkeCnt") != null) ? examQuesMap.get("quesMstkeCnt") : null);
	        				
	        				if(examMap != null && learnMap != null) {
	        					data.put("learn", learnMap);
	        					data.put("exam", examMap);
	        				}
	        				
	        				setResult(dataKey,data);
	        			} else {
	        				setResult(msgKey, vu1.getResult());
	        			}
	        			
	        		} else {
	        			setResult(msgKey, decodeResult);
	        		}
	        		
	        	} else {
	        		setResult(msgKey, vuM.getResult());
	        	}
			} else {
				vuW.checkRequired(new String[] {"yyyy","mm", "wk"}, paramMap);
				
				if(vuW.isValid()) {
					getStudId(paramMap);
	        		
	        		if(decodeResult.isEmpty()) {
	        			String yyyy = paramMap.get("yyyy").toString();
	        			int mm = Integer.valueOf(paramMap.get("mm").toString());
	        			String wk = paramMap.get("wk").toString();
	        			String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
	        			
	        			int yymmwk = Integer.parseInt(yyyy + convertMm + wk);
	        			
	        			paramMap.put("yymmwk", yymmwk);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			vu2.isNumeric("wk", wk);
	        			
	        			if(vu1.isValid() && vu2.isValid()) {
	        				Map<String, Object> learnMap = new LinkedHashMap<String, Object>();
	        				ArrayList<Map<String, Object>> aLrnList = new ArrayList<>();
	        				Map<String, Object> examMap = new LinkedHashMap<String, Object>();
	        				ArrayList<Map<String, Object>> examSubjList = new ArrayList<>();
	        				Map<String, Object> examQuesMap = new LinkedHashMap<String, Object>();
	        				
	        				learnMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getLrnSummary");
	        				aLrnList = (ArrayList<Map<String, Object>>) studLrnAnalMapper.getList(paramMap, "StudReport.getALrnSummaryList");
	        				examSubjList = (ArrayList<Map<String, Object>>) studLrnAnalMapper.getList(paramMap, "StudReport.getExamSubjSummary");
	        				examQuesMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getExamQuesSummary");
	        				
	        				if(learnMap != null) {
	        					learnMap.put("aLrnStt1", null);
	        					learnMap.put("aLrnStt2", null);
	        					learnMap.put("aLrnStt3", null);
	        					
	        					if(aLrnList != null || aLrnList.size() > 0) {
	        						int aLrnItemIndex = 0;
	        						for(Map<String, Object> aLrnItem : aLrnList) {
	        							aLrnItemIndex++;
	        							
	        							String mapKeyNm = "aLrnStt"+aLrnItemIndex;
	        							Map<String, Object> aLrnMap = new HashMap<String, Object>();
	        							
	        							aLrnMap.put("subjNm", aLrnItem.get("subjNm"));
	        							aLrnMap.put("aLrnCnt", aLrnItem.get("aLrnCnt"));
	        							
	        							learnMap.put(mapKeyNm, aLrnMap);
	        						}
	        					}
	        				}
	        				
	        				/*과목별 평가 점수 초기 값*/
	        				examMap.put("examScore", null);
	        				examMap.put("c01examScore", null);
	        				examMap.put("c02examScore", null);
	        				examMap.put("c03examScore", null);
	        				examMap.put("c04examScore", null);
	        				examMap.put("c05examScore", null);
	        				examMap.put("c06examScore", null);
	        				
	        				for(Map<String, Object> examSubjItme : examSubjList) {
	        					String subjCd = (examSubjItme.get("subjCd").toString().equals("ALL")) ? "c00" : examSubjItme.get("subjCd").toString().replace("C", "c");
	        					Object score = (examSubjItme.get("exRt") != null) ? Integer.valueOf(examSubjItme.get("exRt").toString()) : null;
	        					
	        					if(subjCd.equals("c00")) {
	        						examMap.put("examScore", score);
	        					} else {
	        						examMap.put(subjCd+"examScore", score);
	        					}
	        				}
	        				
	        				examMap.put("incrtNoteCnt", (examQuesMap != null && examQuesMap.get("wnoteTotCnt") != null) ? examQuesMap.get("wnoteTotCnt") : null);
	        				examMap.put("incrtNoteFnshCnt", (examQuesMap != null && examQuesMap.get("wnoteFnshCnt") != null) ? examQuesMap.get("wnoteFnshCnt") : null);
	        				examMap.put("incrtNoteNcCnt", (examQuesMap != null && examQuesMap.get("wnoteUnfnshCnt") != null) ? examQuesMap.get("wnoteUnfnshCnt") : null);
	        				examMap.put("quesTotCnt", (examQuesMap != null && examQuesMap.get("quesTotCnt") != null) ? examQuesMap.get("quesTotCnt") : null);
	        				examMap.put("skipQuesCnt", (examQuesMap != null && examQuesMap.get("quesSkipCnt") != null) ? examQuesMap.get("quesSkipCnt") : null);
	        				examMap.put("cursoryQuesCnt", (examQuesMap != null && examQuesMap.get("quesHrryCnt") != null) ? examQuesMap.get("quesHrryCnt") : null);
	        				examMap.put("guessQuesCnt", (examQuesMap != null && examQuesMap.get("quesGussCnt") != null) ? examQuesMap.get("quesGussCnt") : null);
	        				examMap.put("mistakeQuesCnt", (examQuesMap != null && examQuesMap.get("quesMstkeCnt") != null) ? examQuesMap.get("quesMstkeCnt") : null);
	        				
	        				if(examMap != null && learnMap != null) {
	        					data.put("learn", learnMap);
	        					data.put("exam", examMap);
	        				}
	        				
	        				setResult(dataKey,data);
	        			} else {
	        				if(!vu1.isValid()) {
								setResult(msgKey, vu1.getResult());
							}else if(!vu2.isValid()) {
								setResult(msgKey, vu2.getResult());
							}
	        			}
	        			
	        		} else {
	        			setResult(msgKey, decodeResult);
	        		}
					
				} else {
					setResult(msgKey, vuW.getResult());
				}
			}
        	
        } else {
        	setResult(msgKey, vu.getResult());
        }
	
	    return result;
    }
    
    @Override
    public Map getAttRt(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new LinkedHashMap<>();
    	
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vuM = new ValidationUtil();
        ValidationUtil vuW = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        ValidationUtil vu2 = new ValidationUtil();
        
        vu.checkRequired(new String[] {"currCon","p"}, paramMap);
        
        if(vu.isValid()) {
        	String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {
				vuM.checkRequired(new String[] {"yyyy","mm"}, paramMap);
	        	
	        	if(vuM.isValid()) {
	        		getStudId(paramMap);
	        		
	        		if(decodeResult.isEmpty()) {
	        			String yyyy = paramMap.get("yyyy").toString();
	        			int mm = Integer.valueOf(paramMap.get("mm").toString());
	        			String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
	        			
        				int yymm = Integer.parseInt(yyyy+convertMm);
	        			
	        			paramMap.put("yymm", yymm);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			
	        			if(vu1.isValid()) {
	        				Map<String, Object> attRtDataMap = new LinkedHashMap<String, Object>();
	        				ArrayList<Map<String, Object>> attRtDataList = new ArrayList<>();
	        				
	        				Map<String, Object> yymmDataMap = new LinkedHashMap<String, Object>();
	        				
        					yymmDataMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getPeriod");
	        				
	        				int startYymm = Integer.parseInt(yymmDataMap.get("startYymm").toString());
	        				int endYymm = Integer.parseInt(yymmDataMap.get("endYymm").toString());
	        				
	        				paramMap.put("startYymm", startYymm);
	        				paramMap.put("endYymm", endYymm);
	        				
	        				attRtDataMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getAttRt");
	        				
	        				int dataCnt = Integer.parseInt(attRtDataMap.get("dataCnt").toString());
	        				if(dataCnt > 0) {
	        					attRtDataList = (ArrayList<Map<String, Object>>) studLrnAnalMapper.getList(paramMap, "StudReport.getAttRtList");
	        				} else {
	        					attRtDataList = null;
	        				}
	        				
	        				data.put("msg", attRtDataMap.get("msg"));
	        				data.put("infoMsg", attRtDataMap.get("infoMsg"));
	        				data.put("imgUrl", attRtDataMap.get("imgUrl"));
	        				data.put("imgBgUrl", attRtDataMap.get("imgBgUrl"));
	        				data.put("attRtList", attRtDataList);
	        				
	        				setResult(dataKey,data);
	        			} else {
	        				setResult(msgKey, vu1.getResult());
	        			}
	        			
	        		} else {
	        			setResult(msgKey, decodeResult);
	        		}
	        		
	        	} else {
	        		setResult(msgKey, vuM.getResult());
	        	}
			} else {
				vuW.checkRequired(new String[] {"yyyy","mm", "wk"}, paramMap);
				
				if(vuW.isValid()) {
					getStudId(paramMap);
	        		
	        		if(decodeResult.isEmpty()) {
	        			String yyyy = paramMap.get("yyyy").toString();
	        			int mm = Integer.valueOf(paramMap.get("mm").toString());
	        			String wk = paramMap.get("wk").toString();
	        			String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
	        			
        				int yymmwk = Integer.parseInt(yyyy + convertMm + wk);
	        			
	        			paramMap.put("yymmwk", yymmwk);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			vu2.isNumeric("wk", wk);
	        			
	        			if(vu1.isValid() && vu2.isValid()) {
	        				Map<String, Object> attRtDataMap = new LinkedHashMap<String, Object>();
	        				ArrayList<Map<String, Object>> attRtDataList = new ArrayList<>();
	        				
	        				Map<String, Object> yymmDataMap = new LinkedHashMap<String, Object>();
	        				
	        				yymmDataMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getPeriod");
	        				
	        				int startYymmwk = Integer.parseInt(yymmDataMap.get("startYymmwk").toString());
	        				int endYymmwk = Integer.parseInt(yymmDataMap.get("endYymmwk").toString());
	        				
	        				paramMap.put("startYymmwk", startYymmwk);
	        				paramMap.put("endYymmwk", endYymmwk);
	        				
	        				attRtDataMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getAttRt");
	        				
	        				int dataCnt = Integer.parseInt(attRtDataMap.get("dataCnt").toString());
	        				if(dataCnt > 0) {
	        					attRtDataList = (ArrayList<Map<String, Object>>) studLrnAnalMapper.getList(paramMap, "StudReport.getAttRtList");
	        				} else {
	        					attRtDataList = null;
	        				}
	        				
	        				data.put("msg", attRtDataMap.get("msg"));
	        				data.put("infoMsg", attRtDataMap.get("infoMsg"));
	        				data.put("imgUrl", attRtDataMap.get("imgUrl"));
	        				data.put("imgBgUrl", attRtDataMap.get("imgBgUrl"));
	        				data.put("attRtList", attRtDataList);
	        				
	        				setResult(dataKey,data);
	        			} else {
	        				if(!vu1.isValid()) {
								setResult(msgKey, vu1.getResult());
							}else if(!vu2.isValid()) {
								setResult(msgKey, vu2.getResult());
							}
	        			}
	        			
	        		} else {
	        			setResult(msgKey, decodeResult);
	        		}
					
				} else {
					setResult(msgKey, vuW.getResult());
				}
			}
        	
        } else {
        	setResult(msgKey, vu.getResult());
        }
	
	    return result;
    }
    
    @Override
    public Map getLrnExRt(Map<String, Object> paramMap) throws Exception {
    	Map<String,Object> data = new LinkedHashMap<>();
    	
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vuM = new ValidationUtil();
        ValidationUtil vuW = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        ValidationUtil vu2 = new ValidationUtil();
        
        vu.checkRequired(new String[] {"currCon","p"}, paramMap);
        
        if(vu.isValid()) {
        	String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {
				vuM.checkRequired(new String[] {"yyyy","mm"}, paramMap);
	        	
	        	if(vuM.isValid()) {
	        		getStudId(paramMap);
	        		
	        		if(decodeResult.isEmpty()) {
	        			String yyyy = paramMap.get("yyyy").toString();
	        			int mm = Integer.valueOf(paramMap.get("mm").toString());
	        			String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
	        			
	        			int yymm = Integer.parseInt(yyyy+convertMm);
	        			
	        			paramMap.put("yymm", yymm);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			
	        			if(vu1.isValid()) {
	        				Map<String, Object> yymmDataMap = new LinkedHashMap<String, Object>();
	        				Map<String, Object> lrnExRtDataMap = new LinkedHashMap<String, Object>();
	        				
	        				ArrayList<Map<String, Object>> lrnExRtSubjList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> lrnExRtList = new ArrayList<>();
	        				
	        				yymmDataMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getPeriod");
	        				
	        				int startYymm = Integer.parseInt(yymmDataMap.get("startYymm").toString());
	        				int endYymm = Integer.parseInt(yymmDataMap.get("endYymm").toString());
	        				
	        				paramMap.put("startYymm", startYymm);
	        				paramMap.put("endYymm", endYymm);
	        				
	        				lrnExRtDataMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getlrnExRt");
	        				
	        				int exCnt = (lrnExRtDataMap.get("exCnt") != null) ? Integer.parseInt(lrnExRtDataMap.get("exCnt").toString()) : 0;
	        				
	        				if(exCnt > 0) {
	        					lrnExRtList = (ArrayList<Map<String, Object>>) studLrnAnalMapper.getList(paramMap, "StudReport.getSubjLrnExRt");
	        					
	        					for(Map<String, Object> lrnExRtItem : lrnExRtList) {
	        						Map<String, Object> lrnExRtSubjMap = new LinkedHashMap<String, Object>();
	        						ArrayList<Map<String, Object>> exRtSubjList = new ArrayList<>();
	        						
	        						int exRtCnt = Integer.parseInt(lrnExRtItem.get("exRtCnt").toString());
	        						
	        						String subjCd = (lrnExRtItem.get("subjCd").toString().equals("ALL")) ? "C00" : lrnExRtItem.get("subjCd").toString();
	        						
	        						lrnExRtSubjMap.put("subjCd", subjCd);
	        						lrnExRtSubjMap.put("subjNm", lrnExRtItem.get("subjNm"));
	        						lrnExRtSubjMap.put("msg", lrnExRtItem.get("msg"));
	        						lrnExRtSubjMap.put("infoMsg", lrnExRtItem.get("infoMsg"));
	        						lrnExRtSubjMap.put("imgUrl", lrnExRtItem.get("imgUrl"));
	        						lrnExRtSubjMap.put("imgBgUrl", lrnExRtItem.get("imgBgUrl"));
	        						
	        						/*if(exRtCnt > 0) {
	        							
	        							List<String> mmList = Arrays.asList(lrnExRtItem.get("mmSp").toString().split(","));
	        							List<String> scoreList = Arrays.asList(lrnExRtItem.get("exRtSp").toString().split(","));
	        							
	        							for(int i = 0; i < 5; i++) {
	        								Map<String, Object> exRtSubjMap = new LinkedHashMap<String, Object>();
	        								exRtSubjMap.put("mm", Integer.parseInt(mmList.get(i)));
	        								exRtSubjMap.put("lrnExRt", (scoreList.get(i).equals(" ")) ? null : scoreList.get(i));
	        								
	        								exRtSubjList.add(exRtSubjMap);
	        							}
	        						} else {
	        							exRtSubjList = null;
	        						}*/
	        						
	        						List<String> mmList = Arrays.asList(lrnExRtItem.get("mmSp").toString().split(","));
        							List<String> scoreList = Arrays.asList(lrnExRtItem.get("exRtSp").toString().split(","));
        							
        							for(int i = 0; i < 5; i++) {
        								Map<String, Object> exRtSubjMap = new LinkedHashMap<String, Object>();
        								exRtSubjMap.put("mm", Integer.parseInt(mmList.get(i)));
        								exRtSubjMap.put("lrnExRt", (scoreList.get(i).equals(" ")) ? null : scoreList.get(i));
        								
        								exRtSubjList.add(exRtSubjMap);
        							}
	        						
	        						lrnExRtSubjMap.put("lrnExRtList", exRtSubjList);
	        						
	        						lrnExRtSubjList.add(lrnExRtSubjMap);
	        					}
	        					
		        				data.put("lrnExRtSubjList", lrnExRtSubjList);
	        				} else {
	        					data.put("lrnExRtSubjList", lrnExRtSubjList);
	        				}
	        				
	        				setResult(dataKey,data);
	        			} else {
	        				setResult(msgKey, vu1.getResult());
	        			}
	        			
	        		} else {
	        			setResult(msgKey, decodeResult);
	        		}
	        		
	        	} else {
	        		setResult(msgKey, vuM.getResult());
	        	}
			} else {
				vuW.checkRequired(new String[] {"yyyy","mm", "wk"}, paramMap);
				
				if(vuW.isValid()) {
					getStudId(paramMap);
	        		
	        		if(decodeResult.isEmpty()) {
	        			String yyyy = paramMap.get("yyyy").toString();
	        			int mm = Integer.valueOf(paramMap.get("mm").toString());
	        			String wk = paramMap.get("wk").toString();
	        			String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
	        			
	        			int yymmwk = Integer.parseInt(yyyy + convertMm + wk);
	        			
	        			paramMap.put("yymmwk", yymmwk);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			vu2.isNumeric("wk", wk);
	        			
	        			if(vu1.isValid() && vu2.isValid()) {
	        				Map<String, Object> yymmDataMap = new LinkedHashMap<String, Object>();
	        				
        					Map<String, Object> lrnExRtDataMap = new LinkedHashMap<String, Object>();
	        				
	        				ArrayList<Map<String, Object>> lrnExRtSubjList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> lrnExRtList = new ArrayList<>();
	        				
	        				yymmDataMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getPeriod");
	        				
	        				int startYymmwk = Integer.parseInt(yymmDataMap.get("startYymmwk").toString());
	        				int endYymmwk = Integer.parseInt(yymmDataMap.get("endYymmwk").toString());
	        				
	        				paramMap.put("startYymmwk", startYymmwk);
	        				paramMap.put("endYymmwk", endYymmwk);
	        				
        					lrnExRtDataMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getlrnExRt");
	        				
	        				int exCnt = (lrnExRtDataMap.get("exCnt") != null) ? Integer.parseInt(lrnExRtDataMap.get("exCnt").toString()) : 0;
	        				
	        				if(exCnt > 0) {
	        					lrnExRtList = (ArrayList<Map<String, Object>>) studLrnAnalMapper.getList(paramMap, "StudReport.getSubjLrnExRt");
	        					
	        					for(Map<String, Object> lrnExRtItem : lrnExRtList) {
	        						Map<String, Object> lrnExRtSubjMap = new LinkedHashMap<String, Object>();
	        						ArrayList<Map<String, Object>> exRtSubjList = new ArrayList<>();
	        						
	        						int exRtCnt = Integer.parseInt(lrnExRtItem.get("exRtCnt").toString());
	        						
	        						String subjCd = (lrnExRtItem.get("subjCd").toString().equals("ALL")) ? "C00" : lrnExRtItem.get("subjCd").toString();
	        						
	        						lrnExRtSubjMap.put("subjCd", subjCd);
	        						lrnExRtSubjMap.put("subjNm", lrnExRtItem.get("subjNm"));
	        						lrnExRtSubjMap.put("msg", lrnExRtItem.get("msg"));
	        						lrnExRtSubjMap.put("infoMsg", lrnExRtItem.get("infoMsg"));
	        						lrnExRtSubjMap.put("imgUrl", lrnExRtItem.get("imgUrl"));
	        						lrnExRtSubjMap.put("imgBgUrl", lrnExRtItem.get("imgBgUrl"));
	        						
	        						/*if(exRtCnt > 0) {
	        							
	        							List<String> mmList = Arrays.asList(lrnExRtItem.get("mmSp").toString().split(","));
	        							List<String> wkList = Arrays.asList(lrnExRtItem.get("wkSp").toString().split(","));
	        							List<String> scoreList = Arrays.asList(lrnExRtItem.get("exRtSp").toString().split(","));
	        							
	        							for(int i = 0; i < 5; i++) {
	        								Map<String, Object> exRtSubjMap = new LinkedHashMap<String, Object>();
	        								exRtSubjMap.put("mm", Integer.parseInt(mmList.get(i)));
	        								exRtSubjMap.put("wk", Integer.parseInt(wkList.get(i)));
	        								exRtSubjMap.put("lrnExRt", (scoreList.get(i).equals(" ")) ? null : scoreList.get(i));
	        								
	        								exRtSubjList.add(exRtSubjMap);
	        							}
	        						} else {
	        							exRtSubjList = null;
	        						}*/
	        						List<String> mmList = Arrays.asList(lrnExRtItem.get("mmSp").toString().split(","));
        							List<String> wkList = Arrays.asList(lrnExRtItem.get("wkSp").toString().split(","));
        							List<String> scoreList = Arrays.asList(lrnExRtItem.get("exRtSp").toString().split(","));
        							
        							for(int i = 0; i < 5; i++) {
        								Map<String, Object> exRtSubjMap = new LinkedHashMap<String, Object>();
        								exRtSubjMap.put("mm", Integer.parseInt(mmList.get(i)));
        								exRtSubjMap.put("wk", Integer.parseInt(wkList.get(i)));
        								exRtSubjMap.put("lrnExRt", (scoreList.get(i).equals(" ")) ? null : scoreList.get(i));
        								
        								exRtSubjList.add(exRtSubjMap);
        							}
	        						
	        						lrnExRtSubjMap.put("lrnExRtList", exRtSubjList);
	        						
	        						lrnExRtSubjList.add(lrnExRtSubjMap);
	        					}
	        					
		        				data.put("lrnExRtSubjList", lrnExRtSubjList);
	        				} else {
	        					data.put("lrnExRtSubjList", lrnExRtSubjList);
	        				}
	        				
	        				setResult(dataKey,data);
	        			} else {
	        				if(!vu1.isValid()) {
								setResult(msgKey, vu1.getResult());
							}else if(!vu2.isValid()) {
								setResult(msgKey, vu2.getResult());
							}
	        			}
	        			
	        		} else {
	        			setResult(msgKey, decodeResult);
	        		}
					
				} else {
					setResult(msgKey, vuW.getResult());
				}
			}
        	
        } else {
        	setResult(msgKey, vu.getResult());
        }
	
	    return result;
    }
    
    @Override
    public Map getLrnHabit(Map<String, Object> paramMap) throws Exception {
    	Map<String,Object> data = new LinkedHashMap<>();
    	
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vuM = new ValidationUtil();
        ValidationUtil vuW = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        ValidationUtil vu2 = new ValidationUtil();
        
        vu.checkRequired(new String[] {"currCon","p"}, paramMap);
        
        if(vu.isValid()) {
        	String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {
				vuM.checkRequired(new String[] {"yyyy","mm"}, paramMap);
	        	
	        	if(vuM.isValid()) {
	        		getStudId(paramMap);
	        		
	        		if(decodeResult.isEmpty()) {
	        			String yyyy = paramMap.get("yyyy").toString();
	        			int mm = Integer.valueOf(paramMap.get("mm").toString());
	        			String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
	        			
	        			int yymm = Integer.parseInt(yyyy+convertMm);
	        			
	        			paramMap.put("yymm", yymm);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			
	        			if(vu1.isValid()) {
	        				Map<String, Object> learnMap = new LinkedHashMap<String, Object>();
	        				Map<String, Object> lrnhabitMap = new LinkedHashMap<String, Object>();
	        				
	        				learnMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getLrnHabit");
	        				lrnhabitMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getMaxLrnHabit");
	        				
	        				String maxLrnHabitCd = null;
	        				
	        				String msg = (learnMap.get("msg") != null) ? learnMap.get("msg").toString() : null;
	        				String infoMsg = (learnMap.get("infoMsg") != null) ? learnMap.get("infoMsg").toString() : null;
	        				String imgUrl = (learnMap.get("imgUrl") != null) ? learnMap.get("imgUrl").toString() : null;
	        				String imgBgUrl = (learnMap.get("imgBgUrl") != null) ? learnMap.get("imgBgUrl").toString() : null;
	        				
	        				data.put("msg", msg);
	        				data.put("infoMsg", infoMsg);
	        				data.put("imgUrl", imgUrl);
	        				data.put("imgBgUrl", imgBgUrl);
	        				data.put("bLrnCnt", learnMap.get("bLrnCnt"));
    						data.put("tLrnCnt", learnMap.get("tLrnCnt"));
    						data.put("dLrnCnt", learnMap.get("dLrnCnt"));
	        				
    						maxLrnHabitCd = (lrnhabitMap != null && lrnhabitMap.get("maxNm") != null) ? lrnhabitMap.get("maxNm").toString() : null;
        					
        					if(maxLrnHabitCd != null) {
        						String maxLrnHabitNm = (maxLrnHabitCd.startsWith("b")) ? "일찍 했어요" : (maxLrnHabitCd.startsWith("t")) ? "계획대로 했어요" : "나중에 했어요";
        						
        						data.put("maxLrnHabitNm", maxLrnHabitNm);
        						data.put("maxLrnHabitRt", learnMap.get(maxLrnHabitCd+"Rt"));
        					} else {
        						data.put("maxLrnHabitNm", null);
        						data.put("maxLrnHabitRt", null);
        					}
	        				
	        				setResult(dataKey,data);
	        			} else {
	        				setResult(msgKey, vu1.getResult());
	        			}
	        			
	        		} else {
	        			setResult(msgKey, decodeResult);
	        		}
	        		
	        	} else {
	        		setResult(msgKey, vuM.getResult());
	        	}
			} else {
				vuW.checkRequired(new String[] {"yyyy","mm", "wk"}, paramMap);
				
				if(vuW.isValid()) {
					getStudId(paramMap);
	        		
	        		if(decodeResult.isEmpty()) {
	        			String yyyy = paramMap.get("yyyy").toString();
	        			int mm = Integer.valueOf(paramMap.get("mm").toString());
	        			String wk = paramMap.get("wk").toString();
	        			String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
	        			
	        			int yymmwk = Integer.parseInt(yyyy + convertMm + wk);
	        			
	        			paramMap.put("yymmwk", yymmwk);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			vu2.isNumeric("wk", wk);
	        			
	        			if(vu1.isValid() && vu2.isValid()) {
	        				Map<String, Object> learnMap = new LinkedHashMap<String, Object>();
	        				Map<String, Object> lrnhabitMap = new LinkedHashMap<String, Object>();
	        				
	        				learnMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getLrnHabit");
	        				lrnhabitMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getMaxLrnHabit");
	        				
	        				String maxLrnHabitCd = null;
	        				
	        				String msg = (learnMap.get("msg") != null) ? learnMap.get("msg").toString() : null;
	        				String infoMsg = (learnMap.get("infoMsg") != null) ? learnMap.get("infoMsg").toString() : null;
	        				String imgUrl = (learnMap.get("imgUrl") != null) ? learnMap.get("imgUrl").toString() : null;
	        				String imgBgUrl = (learnMap.get("imgBgUrl") != null) ? learnMap.get("imgBgUrl").toString() : null;
	        				
	        				data.put("msg", msg);
	        				data.put("infoMsg", infoMsg);
	        				data.put("imgUrl", imgUrl);
	        				data.put("imgBgUrl", imgBgUrl);
	        				data.put("bLrnCnt", learnMap.get("bLrnCnt"));
    						data.put("tLrnCnt", learnMap.get("tLrnCnt"));
    						data.put("dLrnCnt", learnMap.get("dLrnCnt"));
	        				
    						maxLrnHabitCd = (lrnhabitMap != null && lrnhabitMap.get("maxNm") != null) ? lrnhabitMap.get("maxNm").toString() : null;
        					
        					if(maxLrnHabitCd != null) {
        						String maxLrnHabitNm = (maxLrnHabitCd.startsWith("b")) ? "일찍 했어요" : (maxLrnHabitCd.startsWith("t")) ? "계획대로 했어요" : "나중에 했어요";
        						
        						data.put("maxLrnHabitNm", maxLrnHabitNm);
        						data.put("maxLrnHabitRt", learnMap.get(maxLrnHabitCd+"Rt"));
        					} else {
        						data.put("maxLrnHabitNm", null);
        						data.put("maxLrnHabitRt", null);
        					}
	        				
	        				setResult(dataKey,data);
	        			} else {
	        				if(!vu1.isValid()) {
								setResult(msgKey, vu1.getResult());
							}else if(!vu2.isValid()) {
								setResult(msgKey, vu2.getResult());
							}
	        			}
	        			
	        		} else {
	        			setResult(msgKey, decodeResult);
	        		}
					
				} else {
					setResult(msgKey, vuW.getResult());
				}
			}
        	
        } else {
        	setResult(msgKey, vu.getResult());
        }
	
	    return result;
    }
    
    @Override
    public Map getConcn(Map<String, Object> paramMap) throws Exception {
    	Map<String,Object> data = new LinkedHashMap<>();
    	
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vuM = new ValidationUtil();
        ValidationUtil vuW = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        ValidationUtil vu2 = new ValidationUtil();
        
        vu.checkRequired(new String[] {"currCon","p"}, paramMap);
        
        if(vu.isValid()) {
        	String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {
				vuM.checkRequired(new String[] {"yyyy","mm"}, paramMap);
	        	
	        	if(vuM.isValid()) {
	        		getStudId(paramMap);
	        		
	        		if(decodeResult.isEmpty()) {
	        			String yyyy = paramMap.get("yyyy").toString();
	        			int mm = Integer.valueOf(paramMap.get("mm").toString());
	        			String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
	        			
	        			int yymm = Integer.parseInt(yyyy+convertMm);
	        			
	        			paramMap.put("yymm", yymm);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			
	        			if(vu1.isValid()) {
        					Map<String, Object> yymmDataMap = new LinkedHashMap<String, Object>();
	        				Map<String, Object> concnMap = new LinkedHashMap<String, Object>();
	        				
	        				ArrayList<Map<String, Object>> concnSubjList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> concnList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> concnDayList = new ArrayList<>();
	        				
	        				yymmDataMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getPeriod");
	        				
	        				int startYymm = Integer.parseInt(yymmDataMap.get("startYymm").toString());
	        				int endYymm = Integer.parseInt(yymmDataMap.get("endYymm").toString());
	        				
	        				paramMap.put("startYymm", startYymm);
	        				paramMap.put("endYymm", endYymm);
	        				
	        				concnMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getConcn");
	        				
	        				data.put("msg", concnMap.get("msg"));
	        				data.put("imgUrl", concnMap.get("imgUrl"));
	        				
	        				int dataCnt = Integer.parseInt(concnMap.get("dataCnt").toString());
	        				if(dataCnt > 0) {
	        					concnList = (ArrayList<Map<String, Object>>) studLrnAnalMapper.getList(paramMap, "StudReport.getConcnList");
	        				} else {
	        					concnList = null;
	        				}
	        				
	        				data.put("concnList", concnList);
	        				data.put("concnDayList", null);
	        				
	        				setResult(dataKey,data);
	        			} else {
	        				setResult(msgKey, vu1.getResult());
	        			}
	        			
	        		} else {
	        			setResult(msgKey, decodeResult);
	        		}
	        		
	        	} else {
	        		setResult(msgKey, vuM.getResult());
	        	}
			} else {
				vuW.checkRequired(new String[] {"yyyy","mm", "wk"}, paramMap);
				
				if(vuW.isValid()) {
					getStudId(paramMap);
	        		
	        		if(decodeResult.isEmpty()) {
	        			String yyyy = paramMap.get("yyyy").toString();
	        			int mm = Integer.valueOf(paramMap.get("mm").toString());
	        			String wk = paramMap.get("wk").toString();
	        			String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
	        			
        				int yymmwk = Integer.parseInt(yyyy + convertMm + wk);
	        			
	        			paramMap.put("yymmwk", yymmwk);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			vu2.isNumeric("wk", wk);
	        			
	        			if(vu1.isValid() && vu2.isValid()) {
	        				Map<String, Object> yymmDataMap = new LinkedHashMap<String, Object>();
	        				Map<String, Object> concnMap = new LinkedHashMap<String, Object>();
	        				ArrayList<Map<String, Object>> concnList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> concnDayList = new ArrayList<>();
	        				
	        				
        					yymmDataMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getPeriod");
	        				
	        				int startYymmwk = Integer.parseInt(yymmDataMap.get("startYymmwk").toString());
	        				int endYymmwk = Integer.parseInt(yymmDataMap.get("endYymmwk").toString());
	        				
	        				paramMap.put("startYymmwk", startYymmwk);
	        				paramMap.put("endYymmwk", endYymmwk);
	        				
        					concnMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getConcn");
	        				
	        				data.put("msg", concnMap.get("msg"));
	        				data.put("imgUrl", concnMap.get("imgUrl"));
	        				
	        				int dataCnt = Integer.parseInt(concnMap.get("dataCnt").toString());
	        				if(dataCnt > 0) {
	        					concnList = (ArrayList<Map<String, Object>>) studLrnAnalMapper.getList(paramMap, "StudReport.getConcnList");
	        					concnDayList = (ArrayList<Map<String, Object>>) studLrnAnalMapper.getList(paramMap, "StudReport.getConcnDayList");
	        				} else {
	        					concnList = null;
	        					concnDayList = null;
	        				}
	        				
	        				data.put("concnList", concnList);
	        				data.put("concnDayList", concnDayList);
	        				
	        				setResult(dataKey,data);
	        			} else {
	        				if(!vu1.isValid()) {
								setResult(msgKey, vu1.getResult());
							}else if(!vu2.isValid()) {
								setResult(msgKey, vu2.getResult());
							}
	        			}
	        			
	        		} else {
	        			setResult(msgKey, decodeResult);
	        		}
					
				} else {
					setResult(msgKey, vuW.getResult());
				}
			}
        	
        } else {
        	setResult(msgKey, vu.getResult());
        }
	
	    return result;
    }
    
    @Override
    public Map getAlrn(Map<String, Object> paramMap) throws Exception {
    	Map<String,Object> data = new LinkedHashMap<>();
    	
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vuM = new ValidationUtil();
        ValidationUtil vuW = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        ValidationUtil vu2 = new ValidationUtil();
        
        vu.checkRequired(new String[] {"currCon","p"}, paramMap);
        
        if(vu.isValid()) {
        	String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {
				vuM.checkRequired(new String[] {"yyyy","mm","p"}, paramMap);
				
				if(vuM.isValid()) {
					getStudId(paramMap);
					
					if(decodeResult.isEmpty()) {
						String yyyy = paramMap.get("yyyy").toString();
						int mm = Integer.valueOf(paramMap.get("mm").toString());
						String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
						
						int yymm = Integer.parseInt(yyyy+convertMm);
						
						paramMap.put("yymm", yymm);
						
						vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
						
						if(vu1.isValid()) {
							Map<String, Object> aLrnDataMap = new LinkedHashMap<String, Object>();
							ArrayList<Map<String, Object>> aLrnList = new ArrayList<>();
							
							aLrnDataMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getALrnStt");
							
							if(aLrnDataMap.get("subjNm") != null) {
								aLrnList = (ArrayList<Map<String, Object>>) studLrnAnalMapper.getList(paramMap, "StudReport.getALrnSttList");
							} else {
								aLrnList = null;
							}
							
							data.put("msg", aLrnDataMap.get("msg"));
							data.put("infoMsg", aLrnDataMap.get("infoMsg"));
							data.put("imgUrl", aLrnDataMap.get("imgUrl"));
							data.put("imgBgUrl", aLrnDataMap.get("imgBgUrl"));
							data.put("maxALrnSubjNm", aLrnDataMap.get("subjNm"));
							data.put("aLrnList", aLrnList);
							
							setResult(dataKey,data);
						} else {
							setResult(msgKey, vu1.getResult());
						}
						
					} else {
						setResult(msgKey, decodeResult);
					}
					
				} else {
					setResult(msgKey, vuM.getResult());
				}
			} else {
				vuW.checkRequired(new String[] {"yyyy","mm", "wk"}, paramMap);
				
				if(vuW.isValid()) {
					getStudId(paramMap);
	        		
	        		if(decodeResult.isEmpty()) {
	        			String yyyy = paramMap.get("yyyy").toString();
	        			int mm = Integer.valueOf(paramMap.get("mm").toString());
	        			String wk = paramMap.get("wk").toString();
	        			String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
	        			
        				int yymmwk = Integer.parseInt(yyyy + convertMm + wk);
	        			
	        			paramMap.put("yymmwk", yymmwk);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			vu2.isNumeric("wk", wk);
	        			
	        			if(vu1.isValid() && vu2.isValid()) {
	        				Map<String, Object> aLrnDataMap = new LinkedHashMap<String, Object>();
							ArrayList<Map<String, Object>> aLrnList = new ArrayList<>();
							
							aLrnDataMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getALrnStt");
							
							if(aLrnDataMap.get("subjNm") != null) {
								aLrnList = (ArrayList<Map<String, Object>>) studLrnAnalMapper.getList(paramMap, "StudReport.getALrnSttList");
							} else {
								aLrnList = null;
							}
							
							data.put("msg", aLrnDataMap.get("msg"));
							data.put("infoMsg", aLrnDataMap.get("infoMsg"));
							data.put("imgUrl", aLrnDataMap.get("imgUrl"));
							data.put("imgBgUrl", aLrnDataMap.get("imgBgUrl"));
							data.put("maxALrnSubjNm", aLrnDataMap.get("subjNm"));
							data.put("aLrnList", aLrnList);
							
							setResult(dataKey,data);
	        			} else {
	        				if(!vu1.isValid()) {
								setResult(msgKey, vu1.getResult());
							}else if(!vu2.isValid()) {
								setResult(msgKey, vu2.getResult());
							}
	        			}
	        			
	        		} else {
	        			setResult(msgKey, decodeResult);
	        		}
					
				} else {
					setResult(msgKey, vuW.getResult());
				}
			}
        } else {
        	setResult(msgKey, vu.getResult());
        }
	
	    return result;
    }
    
    @Override
    public Map getExamScore(Map<String, Object> paramMap) throws Exception {
    	Map<String,Object> data = new LinkedHashMap<>();
    	
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vuM = new ValidationUtil();
        ValidationUtil vuW = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        ValidationUtil vu2 = new ValidationUtil();
        
        vu.checkRequired(new String[] {"currCon","p"}, paramMap);
        
        if(vu.isValid()) {
        	String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {
				vuM.checkRequired(new String[] {"yyyy","mm"}, paramMap);
	        	
	        	if(vuM.isValid()) {
	        		getStudId(paramMap);
	        		
	        		if(decodeResult.isEmpty()) {
	        			String yyyy = paramMap.get("yyyy").toString();
	        			int mm = Integer.valueOf(paramMap.get("mm").toString());
	        			String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
	        			
	        			int yymm = Integer.parseInt(yyyy+convertMm);
	        			
	        			paramMap.put("yymm", yymm);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			
	        			if(vu1.isValid()) {
	        				Map<String, Object> yymmDataMap = new LinkedHashMap<String, Object>();
	        				Map<String, Object> examScoreDataMap = new LinkedHashMap<String, Object>();
	        				
	        				ArrayList<Map<String, Object>> examScoreSubjList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> examScoreList = new ArrayList<>();
	        				
	        				yymmDataMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getPeriod");
	        				
	        				int startYymm = Integer.parseInt(yymmDataMap.get("startYymm").toString());
	        				int endYymm = Integer.parseInt(yymmDataMap.get("endYymm").toString());
	        				
	        				paramMap.put("startYymm", startYymm);
	        				paramMap.put("endYymm", endYymm);
	        				
	        				examScoreDataMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getExamScore");
	        				
	        				int crtRtCnt = Integer.parseInt(examScoreDataMap.get("crtRtCnt").toString());
	        				
	        				if(crtRtCnt > 0) {
	        					examScoreList = (ArrayList<Map<String, Object>>) studLrnAnalMapper.getList(paramMap, "StudReport.getSubjExamScore");
	        					
	        					for(Map<String, Object> examScoreItem : examScoreList) {
	        						Map<String, Object> examScoreSubjMap = new LinkedHashMap<String, Object>();
	        						ArrayList<Map<String, Object>> crtRtSubjList = new ArrayList<>();
	        						
	        						int exRtCnt = Integer.parseInt(examScoreItem.get("exRtCnt").toString());
	        						
	        						String subjCd = (examScoreItem.get("subjCd").toString().equals("ALL")) ? "C00" : examScoreItem.get("subjCd").toString().replace("A", "C");
	        						
	        						examScoreSubjMap.put("subjCd", subjCd);
	        						examScoreSubjMap.put("subjNm", examScoreItem.get("subjNm"));
	        						examScoreSubjMap.put("msg", examScoreItem.get("msg"));
	        						examScoreSubjMap.put("infoMsg", examScoreItem.get("infoMsg"));
	        						examScoreSubjMap.put("imgUrl", examScoreItem.get("imgUrl"));
	        						examScoreSubjMap.put("imgBgUrl", examScoreItem.get("imgBgUrl"));
	        						
	        						/*if(exRtCnt > 0) {
	        							
	        							List<String> mmList = Arrays.asList(examScoreItem.get("mmSp").toString().split(","));
	        							List<String> scoreList = Arrays.asList(examScoreItem.get("crtRtSp").toString().split(","));
	        							
	        							for(int i = 0; i < 5; i++) {
	        								Map<String, Object> crtRtSubjMap = new LinkedHashMap<String, Object>();
	        								crtRtSubjMap.put("mm", Integer.parseInt(mmList.get(i)));
	        								crtRtSubjMap.put("examScore", (scoreList.get(i).equals(" ")) ? null : scoreList.get(i));
	        								
	        								crtRtSubjList.add(crtRtSubjMap);
	        							}
	        						} else {
	        							crtRtSubjList = null;
	        						}*/
	        						
	        						List<String> mmList = Arrays.asList(examScoreItem.get("mmSp").toString().split(","));
        							List<String> scoreList = Arrays.asList(examScoreItem.get("crtRtSp").toString().split(","));
        							
        							for(int i = 0; i < 5; i++) {
        								Map<String, Object> crtRtSubjMap = new LinkedHashMap<String, Object>();
        								crtRtSubjMap.put("mm", Integer.parseInt(mmList.get(i)));
        								crtRtSubjMap.put("examScore", (scoreList.get(i).equals(" ")) ? null : scoreList.get(i));
        								
        								crtRtSubjList.add(crtRtSubjMap);
        							}
	        						
	        						examScoreSubjMap.put("examScoreList", crtRtSubjList);
	        						
	        						examScoreSubjList.add(examScoreSubjMap);
	        					}
	        					
		        				data.put("examScoreSubjList", examScoreSubjList);
	        				} else {
	        					data.put("examScoreSubjList", examScoreSubjList);
	        				}
	        				
	        				
	        				setResult(dataKey,data);
	        			} else {
	        				setResult(msgKey, vu1.getResult());
	        			}
	        			
	        		} else {
	        			setResult(msgKey, decodeResult);
	        		}
	        		
	        	} else {
	        		setResult(msgKey, vuM.getResult());
	        	}
			} else {
				vuW.checkRequired(new String[] {"yyyy","mm", "wk"}, paramMap);
				
				if(vuW.isValid()) {
					getStudId(paramMap);
	        		
	        		if(decodeResult.isEmpty()) {
	        			String yyyy = paramMap.get("yyyy").toString();
	        			int mm = Integer.valueOf(paramMap.get("mm").toString());
	        			String wk = paramMap.get("wk").toString();
	        			String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
	        			
	        			int yymmwk = Integer.parseInt(yyyy + convertMm + wk);
	        			
	        			paramMap.put("yymmwk", yymmwk);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			vu2.isNumeric("wk", wk);
	        			
	        			if(vu1.isValid() && vu2.isValid()) {
	        				Map<String, Object> yymmDataMap = new LinkedHashMap<String, Object>();
	        				Map<String, Object> examScoreDataMap = new LinkedHashMap<String, Object>();
	        				
	        				ArrayList<Map<String, Object>> examScoreSubjList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> examScoreList = new ArrayList<>();
	        				
	        				yymmDataMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getPeriod");
	        				
	        				int startYymmwk = Integer.parseInt(yymmDataMap.get("startYymmwk").toString());
	        				int endYymmwk = Integer.parseInt(yymmDataMap.get("endYymmwk").toString());
	        				
	        				paramMap.put("startYymmwk", startYymmwk);
	        				paramMap.put("endYymmwk", endYymmwk);
	        				
        					examScoreDataMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getExamScore");
	        				
	        				int crtRtCnt = Integer.parseInt(examScoreDataMap.get("crtRtCnt").toString());
	        				
	        				if(crtRtCnt > 0) {
	        					examScoreList = (ArrayList<Map<String, Object>>) studLrnAnalMapper.getList(paramMap, "StudReport.getSubjExamScore");
	        					
	        					for(Map<String, Object> examScoreItem : examScoreList) {
	        						Map<String, Object> examScoreSubjMap = new LinkedHashMap<String, Object>();
	        						ArrayList<Map<String, Object>> crtRtSubjList = new ArrayList<>();
	        						
	        						int exRtCnt = Integer.parseInt(examScoreItem.get("exRtCnt").toString());
	        						
	        						String subjCd = (examScoreItem.get("subjCd").toString().equals("ALL")) ? "C00" : examScoreItem.get("subjCd").toString().replace("A", "C");
	        						
	        						examScoreSubjMap.put("subjCd", subjCd);
	        						examScoreSubjMap.put("subjNm", examScoreItem.get("subjNm"));
	        						examScoreSubjMap.put("msg", examScoreItem.get("msg"));
	        						examScoreSubjMap.put("infoMsg", examScoreItem.get("infoMsg"));
	        						examScoreSubjMap.put("imgUrl", examScoreItem.get("imgUrl"));
	        						examScoreSubjMap.put("imgBgUrl", examScoreItem.get("imgBgUrl"));
	        						
	        						/*if(exRtCnt > 0) {
	        							
	        							List<String> mmList = Arrays.asList(examScoreItem.get("mmSp").toString().split(","));
	        							List<String> wkList = Arrays.asList(examScoreItem.get("wkSp").toString().split(","));
	        							List<String> scoreList = Arrays.asList(examScoreItem.get("crtRtSp").toString().split(","));
	        							
	        							for(int i = 0; i < 5; i++) {
	        								Map<String, Object> crtRtSubjMap = new LinkedHashMap<String, Object>();
	        								crtRtSubjMap.put("mm", Integer.parseInt(mmList.get(i)));
	        								crtRtSubjMap.put("wk", Integer.parseInt(wkList.get(i)));
	        								crtRtSubjMap.put("examScore", (scoreList.get(i).equals(" ")) ? null : scoreList.get(i));
	        								
	        								crtRtSubjList.add(crtRtSubjMap);
	        							}
	        						} else {
	        							crtRtSubjList = null;
	        						}*/
	        						
	        						List<String> mmList = Arrays.asList(examScoreItem.get("mmSp").toString().split(","));
        							List<String> wkList = Arrays.asList(examScoreItem.get("wkSp").toString().split(","));
        							List<String> scoreList = Arrays.asList(examScoreItem.get("crtRtSp").toString().split(","));
        							
        							for(int i = 0; i < 5; i++) {
        								Map<String, Object> crtRtSubjMap = new LinkedHashMap<String, Object>();
        								crtRtSubjMap.put("mm", Integer.parseInt(mmList.get(i)));
        								crtRtSubjMap.put("wk", Integer.parseInt(wkList.get(i)));
        								crtRtSubjMap.put("examScore", (scoreList.get(i).equals(" ")) ? null : scoreList.get(i));
        								
        								crtRtSubjList.add(crtRtSubjMap);
        							}
	        						
	        						examScoreSubjMap.put("examScoreList", crtRtSubjList);
	        						
	        						examScoreSubjList.add(examScoreSubjMap);
	        					}
	        					
		        				data.put("examScoreSubjList", examScoreSubjList);
	        				} else {
	        					data.put("examScoreSubjList", examScoreSubjList);
	        				}
	        				
	        				setResult(dataKey,data);
	        			} else {
	        				if(!vu1.isValid()) {
								setResult(msgKey, vu1.getResult());
							}else if(!vu2.isValid()) {
								setResult(msgKey, vu2.getResult());
							}
	        			}
	        			
	        		} else {
	        			setResult(msgKey, decodeResult);
	        		}
					
				} else {
					setResult(msgKey, vuW.getResult());
				}
			}
        	
        } else {
        	setResult(msgKey, vu.getResult());
        }
	
	    return result;
    }
    
    @Override
    public Map getIncrtNoteStt(Map<String, Object> paramMap) throws Exception {
    	Map<String,Object> data = new LinkedHashMap<>();
    	
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vuM = new ValidationUtil();
        ValidationUtil vuW = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        ValidationUtil vu2 = new ValidationUtil();
        
        vu.checkRequired(new String[] {"currCon","p"}, paramMap);
        
        if(vu.isValid()) {
        	String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {
				vuM.checkRequired(new String[] {"yyyy","mm"}, paramMap);
	        	
	        	if(vuM.isValid()) {
	        		getStudId(paramMap);
	        		
	        		if(decodeResult.isEmpty()) {
	        			String yyyy = paramMap.get("yyyy").toString();
	        			int mm = Integer.valueOf(paramMap.get("mm").toString());
	        			String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
	        			
	        			int yymm = Integer.parseInt(yyyy+convertMm);
	        			
	        			paramMap.put("yymm", yymm);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			
	        			if(vu1.isValid()) {
	        				Map<String, Object> incrtNoteMap = new LinkedHashMap<String, Object>();
	        				
	        				incrtNoteMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getIncrtNoteStt");
	        				if(incrtNoteMap != null) {
	        					data.put("msg", incrtNoteMap.get("msg"));
	        					data.put("infoMsg", incrtNoteMap.get("infoMsg"));
	        					data.put("imgUrl", incrtNoteMap.get("imgUrl"));
	        					data.put("imgBgUrl", incrtNoteMap.get("imgBgUrl"));
	        					data.put("incrtNoteCnt", incrtNoteMap.get("wnoteTotCnt"));
		        				data.put("incrtNoteFnshCnt", incrtNoteMap.get("wnoteFnshCnt"));
		        				data.put("incrtNoteNcCnt", incrtNoteMap.get("wnoteUnfnshCnt"));
		        				data.put("incrtNoteFnshRt", incrtNoteMap.get("incrtNoteFnshRt"));
		        				data.put("incrtNoteNcRt", incrtNoteMap.get("incrtNoteNcRt"));
	        				}
	        				
	        				setResult(dataKey,data);
	        			} else {
	        				setResult(msgKey, vu1.getResult());
	        			}
	        			
	        		} else {
	        			setResult(msgKey, decodeResult);
	        		}
	        		
	        	} else {
	        		setResult(msgKey, vuM.getResult());
	        	}
			} else {
				vuW.checkRequired(new String[] {"yyyy","mm", "wk"}, paramMap);
				
				if(vuW.isValid()) {
					getStudId(paramMap);
	        		
	        		if(decodeResult.isEmpty()) {
	        			String yyyy = paramMap.get("yyyy").toString();
	        			int mm = Integer.valueOf(paramMap.get("mm").toString());
	        			String wk = paramMap.get("wk").toString();
	        			String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
	        			
	        			int yymmwk = Integer.parseInt(yyyy + convertMm + wk);
	        			
	        			paramMap.put("yymmwk", yymmwk);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			vu2.isNumeric("wk", wk);
	        			
	        			if(vu1.isValid() && vu2.isValid()) {
        					Map<String, Object> incrtNoteMap = new LinkedHashMap<String, Object>();
	        				
    						incrtNoteMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getIncrtNoteStt");
	        				
    						if(incrtNoteMap != null) {
	        					data.put("msg", incrtNoteMap.get("msg"));
	        					data.put("infoMsg", incrtNoteMap.get("infoMsg"));
	        					data.put("imgUrl", incrtNoteMap.get("imgUrl"));
	        					data.put("imgBgUrl", incrtNoteMap.get("imgBgUrl"));
	        					data.put("incrtNoteCnt", incrtNoteMap.get("wnoteTotCnt"));
		        				data.put("incrtNoteFnshCnt", incrtNoteMap.get("wnoteFnshCnt"));
		        				data.put("incrtNoteNcCnt", incrtNoteMap.get("wnoteUnfnshCnt"));
		        				data.put("incrtNoteFnshRt", incrtNoteMap.get("incrtNoteFnshRt"));
		        				data.put("incrtNoteNcRt", incrtNoteMap.get("incrtNoteNcRt"));
	        				}
	        				
	        				setResult(dataKey,data);
	        			} else {
	        				if(!vu1.isValid()) {
								setResult(msgKey, vu1.getResult());
							}else if(!vu2.isValid()) {
								setResult(msgKey, vu2.getResult());
							}
	        			}
	        			
	        		} else {
	        			setResult(msgKey, decodeResult);
	        		}
					
				} else {
					setResult(msgKey, vuW.getResult());
				}
			}
        	
        } else {
        	setResult(msgKey, vu.getResult());
        }
	
	    return result;
    }
    
    @Override
    public Map getSlvHabit2(Map<String, Object> paramMap) throws Exception {
    	Map<String,Object> data = new LinkedHashMap<>();
    	
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vuM = new ValidationUtil();
        ValidationUtil vuW = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        ValidationUtil vu2 = new ValidationUtil();
        
        vu.checkRequired(new String[] {"currCon","p"}, paramMap);
        
        if(vu.isValid()) {
        	String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {
				vuM.checkRequired(new String[] {"yyyy","mm"}, paramMap);
	        	
	        	if(vuM.isValid()) {
	        		getStudId(paramMap);
	        		
	        		if(decodeResult.isEmpty()) {
	        			String yyyy = paramMap.get("yyyy").toString();
	        			int mm = Integer.valueOf(paramMap.get("mm").toString());
	        			String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
	        			
	        			int yymm = Integer.parseInt(yyyy+convertMm);
	        			
	        			paramMap.put("yymm", yymm);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			
	        			if(vu1.isValid()) {
	        				Map<String, Object> yymmDataMap = new LinkedHashMap<String, Object>();
	        				Map<String, Object> slvHabitMap = new LinkedHashMap<String, Object>();
	        				Map<String, Object> slvHabitQuesMap = new LinkedHashMap<String, Object>();
	        				ArrayList<Map<String, Object>> slvHabitList = new ArrayList<>();
	        				
        					yymmDataMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getPeriod");
	        				
	        				int startYymm = Integer.parseInt(yymmDataMap.get("startYymm").toString());
	        				int endYymm = Integer.parseInt(yymmDataMap.get("endYymm").toString());
	        				
	        				paramMap.put("startYymm", startYymm);
	        				paramMap.put("endYymm", endYymm);
	        				
	        				slvHabitMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getSlvHabitStt");
	        				
	        				if(slvHabitMap != null) {
	        					Map<String, Object> slvHabitMonthDataMap = new LinkedHashMap<String, Object>();
	        					
	        					slvHabitMonthDataMap.put("skipQuesCnt", slvHabitMap.get("quesSkipCnt"));
	        					slvHabitMonthDataMap.put("cursoryQuesCnt", slvHabitMap.get("quesHrryCnt"));
	        					slvHabitMonthDataMap.put("guessQuesCnt", slvHabitMap.get("quesGussCnt"));
	        					slvHabitMonthDataMap.put("mistakeQuesCnt", slvHabitMap.get("quesMstkeCnt"));
	        					
	        					data.put("msg", slvHabitMap.get("msg"));
	        					data.put("imgUrl", slvHabitMap.get("imgUrl"));
	        					data.put("imgBgUrl", slvHabitMap.get("imgBgUrl"));
	        					data.put("slvHabitData", slvHabitMonthDataMap);
	        					
	        					slvHabitQuesMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getSlvHabitQuesStt");
	        					
	        					if(slvHabitQuesMap != null) {
	        						ArrayList<Map<String, Object>> slvHabitSkipList = new ArrayList<>();
	        						ArrayList<Map<String, Object>> slvHabitHrryList = new ArrayList<>();
	        						ArrayList<Map<String, Object>> slvHabitGussList = new ArrayList<>();
	        						ArrayList<Map<String, Object>> slvHabitMstkeList = new ArrayList<>();
	        						
	        						int skipCnt = Integer.parseInt(slvHabitQuesMap.get("skipCnt").toString());
	        						int hrryCnt = Integer.parseInt(slvHabitQuesMap.get("hrryCnt").toString());
	        						int gussCnt = Integer.parseInt(slvHabitQuesMap.get("gussCnt").toString());
	        						int mstkeCnt = Integer.parseInt(slvHabitQuesMap.get("mstkeCnt").toString());
	        						
	        						String mmList = slvHabitQuesMap.get("mmSp").toString();
	        						
	        						if(skipCnt > 0) {
	        							slvHabitSkipList = (ArrayList<Map<String, Object>>) createSlvHabitDataList(mmList, null, slvHabitQuesMap.get("skipSp").toString());
	        						} else {
	        							slvHabitSkipList = null;
	        						}
	        						
	        						if(hrryCnt > 0) {
	        							slvHabitHrryList = (ArrayList<Map<String, Object>>) createSlvHabitDataList(mmList, null, slvHabitQuesMap.get("hrrySp").toString());
	        						} else {
	        							slvHabitHrryList = null;
	        						}
	        						
	        						if(gussCnt > 0) {
	        							slvHabitGussList = (ArrayList<Map<String, Object>>) createSlvHabitDataList(mmList, null, slvHabitQuesMap.get("gussSp").toString());
	        						} else {
	        							slvHabitGussList = null;
	        						}
	        						
	        						if(mstkeCnt > 0) {
	        							slvHabitMstkeList = (ArrayList<Map<String, Object>>) createSlvHabitDataList(mmList, null, slvHabitQuesMap.get("mstkeSp").toString());
	        						} else {
	        							slvHabitMstkeList = null;
	        						}
	        						
	        						slvHabitList.add(createSlvHabitDataMap("skipQues", slvHabitSkipList));
	        						slvHabitList.add(createSlvHabitDataMap("cursoryQues", slvHabitHrryList));
	        						slvHabitList.add(createSlvHabitDataMap("guessQues", slvHabitGussList));
	        						slvHabitList.add(createSlvHabitDataMap("mistakeQues", slvHabitMstkeList));
	        					}
	        					
	        					data.put("slvHabitList", slvHabitList);
	        				}
	        				
	        				setResult(dataKey,data);
	        			} else {
	        				setResult(msgKey, vu1.getResult());
	        			}
	        			
	        		} else {
	        			setResult(msgKey, decodeResult);
	        		}
	        		
	        	} else {
	        		setResult(msgKey, vuM.getResult());
	        	}
			} else {
				vuW.checkRequired(new String[] {"yyyy","mm", "wk"}, paramMap);
				
				if(vuW.isValid()) {
					getStudId(paramMap);
	        		
	        		if(decodeResult.isEmpty()) {
	        			String yyyy = paramMap.get("yyyy").toString();
	        			int mm = Integer.valueOf(paramMap.get("mm").toString());
	        			String wk = paramMap.get("wk").toString();
	        			String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);

	        			int yymmwk = Integer.parseInt(yyyy + convertMm + wk);
	        			
	        			paramMap.put("yymmwk", yymmwk);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			vu2.isNumeric("wk", wk);
	        			
	        			if(vu1.isValid() && vu2.isValid()) {
	        				Map<String, Object> yymmDataMap = new LinkedHashMap<String, Object>();
	        				Map<String, Object> slvHabitMap = new LinkedHashMap<String, Object>();
	        				Map<String, Object> slvHabitQuesMap = new LinkedHashMap<String, Object>();
	        				ArrayList<Map<String, Object>> slvHabitList = new ArrayList<>();
	        				
        					yymmDataMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getPeriod");
	        				
	        				int startYymmwk = Integer.parseInt(yymmDataMap.get("startYymmwk").toString());
	        				int endYymmwk = Integer.parseInt(yymmDataMap.get("endYymmwk").toString());
	        				
	        				paramMap.put("startYymmwk", startYymmwk);
	        				paramMap.put("endYymmwk", endYymmwk);
	        				
        					slvHabitMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getSlvHabitStt");
	        				
	        				if(slvHabitMap != null) {
	        					Map<String, Object> slvHabitMonthDataMap = new LinkedHashMap<String, Object>();
	        					
	        					slvHabitMonthDataMap.put("skipQuesCnt", slvHabitMap.get("quesSkipCnt"));
	        					slvHabitMonthDataMap.put("cursoryQuesCnt", slvHabitMap.get("quesHrryCnt"));
	        					slvHabitMonthDataMap.put("guessQuesCnt", slvHabitMap.get("quesGussCnt"));
	        					slvHabitMonthDataMap.put("mistakeQuesCnt", slvHabitMap.get("quesMstkeCnt"));
	        					
	        					data.put("msg", slvHabitMap.get("msg"));
	        					data.put("imgUrl", slvHabitMap.get("imgUrl"));
	        					data.put("imgBgUrl", slvHabitMap.get("imgBgUrl"));
	        					data.put("slvHabitData", slvHabitMonthDataMap);
	        					
	        					slvHabitQuesMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getSlvHabitQuesStt");
	        					
	        					if(slvHabitQuesMap != null) {
	        						ArrayList<Map<String, Object>> slvHabitSkipList = new ArrayList<>();
	        						ArrayList<Map<String, Object>> slvHabitHrryList = new ArrayList<>();
	        						ArrayList<Map<String, Object>> slvHabitGussList = new ArrayList<>();
	        						ArrayList<Map<String, Object>> slvHabitMstkeList = new ArrayList<>();
	        						
	        						int skipCnt = Integer.parseInt(slvHabitQuesMap.get("skipCnt").toString());
	        						int hrryCnt = Integer.parseInt(slvHabitQuesMap.get("hrryCnt").toString());
	        						int gussCnt = Integer.parseInt(slvHabitQuesMap.get("gussCnt").toString());
	        						int mstkeCnt = Integer.parseInt(slvHabitQuesMap.get("mstkeCnt").toString());
	        						
	        						String mmList = slvHabitQuesMap.get("mmSp").toString();
	        						String wkList = slvHabitQuesMap.get("wkSp").toString();
	        						
	        						if(skipCnt > 0) {
	        							slvHabitSkipList = (ArrayList<Map<String, Object>>) createSlvHabitDataList(mmList, wkList, slvHabitQuesMap.get("skipSp").toString());
	        						} else {
	        							slvHabitSkipList = null;
	        						}
	        						
	        						if(hrryCnt > 0) {
	        							slvHabitHrryList = (ArrayList<Map<String, Object>>) createSlvHabitDataList(mmList, wkList, slvHabitQuesMap.get("hrrySp").toString());
	        						} else {
	        							slvHabitHrryList = null;
	        						}
	        						
	        						if(gussCnt > 0) {
	        							slvHabitGussList = (ArrayList<Map<String, Object>>) createSlvHabitDataList(mmList, wkList, slvHabitQuesMap.get("gussSp").toString());
	        						} else {
	        							slvHabitGussList = null;
	        						}
	        						
	        						if(mstkeCnt > 0) {
	        							slvHabitMstkeList = (ArrayList<Map<String, Object>>) createSlvHabitDataList(mmList, wkList, slvHabitQuesMap.get("mstkeSp").toString());
	        						} else {
	        							slvHabitMstkeList = null;
	        						}
	        						
	        						slvHabitList.add(createSlvHabitDataMap("skipQues", slvHabitSkipList));
	        						slvHabitList.add(createSlvHabitDataMap("cursoryQues", slvHabitHrryList));
	        						slvHabitList.add(createSlvHabitDataMap("guessQues", slvHabitGussList));
	        						slvHabitList.add(createSlvHabitDataMap("mistakeQues", slvHabitMstkeList));
	        					}
	        					
	        					data.put("slvHabitList", slvHabitList);
	        				}
	        				
	        				setResult(dataKey,data);
	        			} else {
	        				if(!vu1.isValid()) {
								setResult(msgKey, vu1.getResult());
							}else if(!vu2.isValid()) {
								setResult(msgKey, vu2.getResult());
							}
	        			}
	        			
	        		} else {
	        			setResult(msgKey, decodeResult);
	        		}
					
				} else {
					setResult(msgKey, vuW.getResult());
				}
			}
        	
        } else {
        	setResult(msgKey, vu.getResult());
        }
	
	    return result;
    }
    
    @Override
    public Map getSlvHabit(Map<String, Object> paramMap) throws Exception {
    	Map<String,Object> data = new LinkedHashMap<>();
    	
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vuM = new ValidationUtil();
        ValidationUtil vuW = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        ValidationUtil vu2 = new ValidationUtil();
        
        vu.checkRequired(new String[] {"currCon","p"}, paramMap);
        
        if(vu.isValid()) {
        	String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {
				vuM.checkRequired(new String[] {"yyyy","mm"}, paramMap);
	        	
	        	if(vuM.isValid()) {
	        		getStudId(paramMap);
	        		
	        		if(decodeResult.isEmpty()) {
	        			String yyyy = paramMap.get("yyyy").toString();
	        			int mm = Integer.valueOf(paramMap.get("mm").toString());
	        			String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
	        			
	        			int yymm = Integer.parseInt(yyyy+convertMm);
	        			
	        			paramMap.put("yymm", yymm);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			
	        			if(vu1.isValid()) {
	        				Map<String, Object> yymmDataMap = new LinkedHashMap<String, Object>();
	        				Map<String, Object> slvHabitMap = new LinkedHashMap<String, Object>();
	        				Map<String, Object> slvHabitMsgMap = new LinkedHashMap<String, Object>();
	        				ArrayList<Map<String, Object>> slvHabitList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> slvHabitDataList = new ArrayList<>();
	        				
        					yymmDataMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getPeriod");
	        				
	        				int startYymm = Integer.parseInt(yymmDataMap.get("startYymm").toString());
	        				int endYymm = Integer.parseInt(yymmDataMap.get("endYymm").toString());
	        				
	        				paramMap.put("startYymm", startYymm);
	        				paramMap.put("endYymm", endYymm);
	        				
	        				slvHabitMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getSlvHabitStt");
	        				slvHabitMsgMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getSlvHabitMsg");
	        				slvHabitDataList = (ArrayList<Map<String, Object>>) studLrnAnalMapper.getList(paramMap, "StudReport.getSlvHabitList");
	        				
	        				if(slvHabitMap != null) {
	        					Map<String, Object> slvHabitMonthDataMap = new LinkedHashMap<String, Object>();
	        					
	        					slvHabitMonthDataMap.put("skipQuesCnt", slvHabitMap.get("quesSkipCnt"));
	        					slvHabitMonthDataMap.put("cursoryQuesCnt", slvHabitMap.get("quesHrryCnt"));
	        					slvHabitMonthDataMap.put("guessQuesCnt", slvHabitMap.get("quesGussCnt"));
	        					slvHabitMonthDataMap.put("mistakeQuesCnt", slvHabitMap.get("quesMstkeCnt"));
	        					
	        					Map<String, Object> slvHabitMsgDataMap = new LinkedHashMap<String, Object>();
	        					
	        					slvHabitMsgDataMap.put("skipQuesMsg", slvHabitMsgMap.get("skipMsg"));
	        					slvHabitMsgDataMap.put("cursoryQuesMsg", slvHabitMsgMap.get("hurrMsg"));
	        					slvHabitMsgDataMap.put("guessQuesMsg", slvHabitMsgMap.get("gussMsg"));
	        					slvHabitMsgDataMap.put("mistakeQuesMsg", slvHabitMsgMap.get("mstkeMsg"));
	        					
	        					Map<String, Object> slvHabitImgDataMap = new LinkedHashMap<String, Object>();
	        					
	        					slvHabitImgDataMap.put("skipQuesImgUrl", slvHabitMsgMap.get("skipImgUrl"));
	        					slvHabitImgDataMap.put("skipQuesImgBgUrl", slvHabitMsgMap.get("skipImgBgUrl"));
	        					slvHabitImgDataMap.put("cursoryQuesImgUrl", slvHabitMsgMap.get("hurrImgUrl"));
	        					slvHabitImgDataMap.put("cursoryQuesImgBgUrl", slvHabitMsgMap.get("hurrImgBgUrl"));
	        					slvHabitImgDataMap.put("guessQuesImgUrl", slvHabitMsgMap.get("gussImgUrl"));
	        					slvHabitImgDataMap.put("guessQuesImgBgUrl", slvHabitMsgMap.get("gussImgBgUrl"));
	        					slvHabitImgDataMap.put("mistakeQuesImgUrl", slvHabitMsgMap.get("mstkeImgUrl"));
	        					slvHabitImgDataMap.put("mistakeQuesImgBgUrl", slvHabitMsgMap.get("mstkeImgBgUrl"));
	        					
	        					data.put("msg", slvHabitMsgMap.get("totMsg"));
	        					data.put("infoMsg", slvHabitMsgMap.get("infoMsg"));
	        					data.put("imgUrl", slvHabitMsgMap.get("totImgUrl"));
	        					data.put("imgBgUrl", slvHabitMsgMap.get("totImgBgUrl"));
	        					data.put("slvHabitData", slvHabitMonthDataMap);
	        					data.put("slvHabitMsgData", slvHabitMsgDataMap);
	        					data.put("slvHabitImgData", slvHabitImgDataMap);

	 	        				for(Map<String,Object> slvHavitItem : slvHabitDataList) {
	 	        					Map<String, Object> slvHabitDataMap = new LinkedHashMap<String, Object>();
	 	        					
	 	        					slvHabitDataMap.put("mm", slvHavitItem.get("mm"));
	 	        					slvHabitDataMap.put("skipQuesCnt", slvHavitItem.get("quesSkipCnt"));
	 	        					slvHabitDataMap.put("cursoryQuesCnt", slvHavitItem.get("quesHrryCnt"));
	 		        				slvHabitDataMap.put("guessQuesCnt", slvHavitItem.get("quesGussCnt"));
	 		        				slvHabitDataMap.put("mistakeQuesCnt", slvHavitItem.get("quesMstkeCnt"));
	 		        				
	 		        				slvHabitList.add(slvHabitDataMap);
	 	        				}
	        					
	        					data.put("slvHabitList", slvHabitList);
	        				}
	        				
	        				setResult(dataKey,data);
	        			} else {
	        				setResult(msgKey, vu1.getResult());
	        			}
	        			
	        		} else {
	        			setResult(msgKey, decodeResult);
	        		}
	        		
	        	} else {
	        		setResult(msgKey, vuM.getResult());
	        	}
			} else {
				vuW.checkRequired(new String[] {"yyyy","mm", "wk"}, paramMap);
				
				if(vuW.isValid()) {
					getStudId(paramMap);
	        		
	        		if(decodeResult.isEmpty()) {
	        			String yyyy = paramMap.get("yyyy").toString();
	        			int mm = Integer.valueOf(paramMap.get("mm").toString());
	        			String wk = paramMap.get("wk").toString();
	        			String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);

	        			int yymmwk = Integer.parseInt(yyyy + convertMm + wk);
	        			
	        			paramMap.put("yymmwk", yymmwk);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			vu2.isNumeric("wk", wk);
	        			
	        			if(vu1.isValid() && vu2.isValid()) {
	        				Map<String, Object> yymmDataMap = new LinkedHashMap<String, Object>();
	        				Map<String, Object> slvHabitMap = new LinkedHashMap<String, Object>();
	        				Map<String, Object> slvHabitMsgMap = new LinkedHashMap<String, Object>();
	        				ArrayList<Map<String, Object>> slvHabitList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> slvHabitDataList = new ArrayList<>();
	        				
        					yymmDataMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getPeriod");
	        				
	        				int startYymmwk = Integer.parseInt(yymmDataMap.get("startYymmwk").toString());
	        				int endYymmwk = Integer.parseInt(yymmDataMap.get("endYymmwk").toString());
	        				
	        				paramMap.put("startYymmwk", startYymmwk);
	        				paramMap.put("endYymmwk", endYymmwk);
	        				
	        				slvHabitMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getSlvHabitStt");
	        				slvHabitMsgMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getSlvHabitMsg");
	        				slvHabitDataList = (ArrayList<Map<String, Object>>) studLrnAnalMapper.getList(paramMap, "StudReport.getSlvHabitList");
	        				
	        				if(slvHabitMap != null) {
	        					Map<String, Object> slvHabitMonthDataMap = new LinkedHashMap<String, Object>();
	        					
	        					slvHabitMonthDataMap.put("skipQuesCnt", slvHabitMap.get("quesSkipCnt"));
	        					slvHabitMonthDataMap.put("cursoryQuesCnt", slvHabitMap.get("quesHrryCnt"));
	        					slvHabitMonthDataMap.put("guessQuesCnt", slvHabitMap.get("quesGussCnt"));
	        					slvHabitMonthDataMap.put("mistakeQuesCnt", slvHabitMap.get("quesMstkeCnt"));
	        					
	        					Map<String, Object> slvHabitMsgDataMap = new LinkedHashMap<String, Object>();
	        					
	        					slvHabitMsgDataMap.put("skipQuesMsg", slvHabitMsgMap.get("skipMsg"));
	        					slvHabitMsgDataMap.put("cursoryQuesMsg", slvHabitMsgMap.get("hurrMsg"));
	        					slvHabitMsgDataMap.put("guessQuesMsg", slvHabitMsgMap.get("gussMsg"));
	        					slvHabitMsgDataMap.put("mistakeQuesMsg", slvHabitMsgMap.get("mstkeMsg"));
	        					
        						Map<String, Object> slvHabitImgDataMap = new LinkedHashMap<String, Object>();
	        					
	        					slvHabitImgDataMap.put("skipQuesImgUrl", slvHabitMsgMap.get("skipImgUrl"));
	        					slvHabitImgDataMap.put("skipQuesImgBgUrl", slvHabitMsgMap.get("skipImgBgUrl"));
	        					slvHabitImgDataMap.put("cursoryQuesImgUrl", slvHabitMsgMap.get("hurrImgUrl"));
	        					slvHabitImgDataMap.put("cursoryQuesImgBgUrl", slvHabitMsgMap.get("hurrImgBgUrl"));
	        					slvHabitImgDataMap.put("guessQuesImgUrl", slvHabitMsgMap.get("gussImgUrl"));
	        					slvHabitImgDataMap.put("guessQuesImgBgUrl", slvHabitMsgMap.get("gussImgBgUrl"));
	        					slvHabitImgDataMap.put("mistakeQuesImgUrl", slvHabitMsgMap.get("mstkeImgUrl"));
	        					slvHabitImgDataMap.put("mistakeQuesImgBgUrl", slvHabitMsgMap.get("mstkeImgBgUrl"));
	        					
	        					data.put("msg", slvHabitMsgMap.get("totMsg"));
	        					data.put("infoMsg", slvHabitMsgMap.get("infoMsg"));
	        					data.put("imgUrl", slvHabitMsgMap.get("totImgUrl"));
	        					data.put("imgBgUrl", slvHabitMsgMap.get("totImgBgUrl"));
	        					data.put("slvHabitData", slvHabitMonthDataMap);
	        					data.put("slvHabitMsgData", slvHabitMsgDataMap);
	        					data.put("slvHabitImgData", slvHabitImgDataMap);

	 	        				for(Map<String,Object> slvHavitItem : slvHabitDataList) {
	 	        					Map<String, Object> slvHabitDataMap = new LinkedHashMap<String, Object>();
	 	        					
	 	        					slvHabitDataMap.put("mm", slvHavitItem.get("mm"));
	 	        					slvHabitDataMap.put("wk", slvHavitItem.get("wk"));
	 	        					slvHabitDataMap.put("skipQuesCnt", slvHavitItem.get("quesSkipCnt"));
	 	        					slvHabitDataMap.put("cursoryQuesCnt", slvHavitItem.get("quesHrryCnt"));
	 		        				slvHabitDataMap.put("guessQuesCnt", slvHavitItem.get("quesGussCnt"));
	 		        				slvHabitDataMap.put("mistakeQuesCnt", slvHavitItem.get("quesMstkeCnt"));
	 		        				
	 		        				slvHabitList.add(slvHabitDataMap);
	 	        				}
	        					
	        					data.put("slvHabitList", slvHabitList);
	        				}
	        				
	        				setResult(dataKey,data);
	        			} else {
	        				if(!vu1.isValid()) {
								setResult(msgKey, vu1.getResult());
							}else if(!vu2.isValid()) {
								setResult(msgKey, vu2.getResult());
							}
	        			}
	        			
	        		} else {
	        			setResult(msgKey, decodeResult);
	        		}
					
				} else {
					setResult(msgKey, vuW.getResult());
				}
			}
        	
        } else {
        	setResult(msgKey, vu.getResult());
        }
	
	    return result;
    }
    
    @Override
    public Map insertReportCheck(Map<String, Object> paramMap) throws Exception {
    	Map<String,Object> data = new LinkedHashMap<>();
    	
        ValidationUtil vu = new ValidationUtil();
        
        vu.checkRequired(new String[] {"p"}, paramMap);
        
        if(vu.isValid()) {
        	getStudId(paramMap);
    		
    		if(decodeResult.isEmpty()) {
    			Map<String, Object> checkDataMap = new LinkedHashMap<String, Object>();
    			Map<String, Object> insertParamMap = new LinkedHashMap<String, Object>();
    			String recentReportValue = paramMap.get("recentReport").toString();
    			
    			int paramLength = paramMap.get("recentReport").toString().length();
    			int row = 0;
    			String dataCheck = "Y";
    			
    			if(paramLength < 6) {
    				Map<String,Object> studRecentData = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getStudRecentReport");
    				int nerParamLength = studRecentData.get("recentReport").toString().length();
    				// recentReport 값의 길이로 월간 / 주간 구분
    				if(nerParamLength == 6) {
    					insertParamMap.put("studId", paramMap.get("studId"));
    					insertParamMap.put("yymm", studRecentData.get("recentReport"));
    					
    					checkDataMap = (Map<String, Object>) commonMapperLrnType.get(insertParamMap, "StudLrnType.getReportCheck");
    					
    				} else {
    					int wk = Integer.parseInt(studRecentData.get("recentReport").toString().substring(6));
    					int yymm = Integer.parseInt(studRecentData.get("recentReport").toString().substring(0,6));
    					
    					insertParamMap.put("studId", paramMap.get("studId"));
    					insertParamMap.put("yymm", yymm);
    					insertParamMap.put("wk", wk);
    					
    					checkDataMap = (Map<String, Object>) commonMapperLrnType.get(insertParamMap, "StudLrnType.getReportCheck");
    					
    				}
    			} else {
    				if(paramLength == 6) {
    					insertParamMap.put("studId", paramMap.get("studId"));
    					insertParamMap.put("yymm", paramMap.get("recentReport"));
    					
    					checkDataMap = (Map<String, Object>) commonMapperLrnType.get(insertParamMap, "StudLrnType.getReportCheck");
    					
    				} else {
    					int wk = Integer.parseInt(paramMap.get("recentReport").toString().substring(6));
    					int yymm = Integer.parseInt(paramMap.get("recentReport").toString().substring(0,6));
    					
    					insertParamMap.put("studId", paramMap.get("studId"));
    					insertParamMap.put("yymm", yymm);
    					insertParamMap.put("wk", wk);
    					
    					checkDataMap = (Map<String, Object>) commonMapperLrnType.get(insertParamMap, "StudLrnType.getReportCheck");
    					
    				}
    			}
    			
    			// 리포트 확인 데이터 등록 여부 확인 : 확인 - "Y" / 미확인 - "N"
    			dataCheck = checkDataMap.get("dataCheck").toString();
				
    			// 리포트 미확인 시 리포트 확인 값 등록
				if(dataCheck.equals("N")) {
					try {
						row = commonMapperLrnType.insert(insertParamMap, "StudLrnType.insertReportCheck");
						
						if(row > 0) {
							data.put("msg", "Success");
							data.put("insertYn", "Y");
							
						} else {
							data.put("msg", "Fail");
							data.put("insertYn", "N");
						}
						
					} catch (Exception e) {
						data.put("msg", "Fail");
						data.put("insertYn", "N");
					}
					
				} else {
					data.put("msg", "Duplicate");
					data.put("insertYn", "N");
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
    public Map deleteReportCheck(Map<String, Object> paramMap) throws Exception {
    	Map<String,Object> data = new LinkedHashMap<>();
    	
        ValidationUtil vu = new ValidationUtil();
        
        vu.checkRequired(new String[] {"p"}, paramMap);
        
        if(vu.isValid()) {
        	getStudId(paramMap);
    		
    		if(decodeResult.isEmpty()) {
    			Map<String, Object> checkDataMap = new LinkedHashMap<String, Object>();
    			Map<String, Object> insertParamMap = new LinkedHashMap<String, Object>();
    			String recentReportValue = paramMap.get("recentReport").toString();
    			
    			int paramLength = paramMap.get("recentReport").toString().length();
    			int row = 0;
    			String dataCheck = "Y";
    			
    			if(paramLength < 6) {
    				Map<String,Object> studRecentData = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getStudRecentReport");
    				int nerParamLength = studRecentData.get("recentReport").toString().length();
    				// recentReport 값의 길이로 월간 / 주간 구분
    				if(nerParamLength == 6) {
    					insertParamMap.put("studId", paramMap.get("studId"));
    					insertParamMap.put("yymm", studRecentData.get("recentReport"));
    					
    				} else {
    					int wk = Integer.parseInt(studRecentData.get("recentReport").toString().substring(6));
    					int yymm = Integer.parseInt(studRecentData.get("recentReport").toString().substring(0,6));
    					
    					insertParamMap.put("studId", paramMap.get("studId"));
    					insertParamMap.put("yymm", yymm);
    					insertParamMap.put("wk", wk);
    					
    				}
    			} else {
    				if(paramLength == 6) {
    					insertParamMap.put("studId", paramMap.get("studId"));
    					insertParamMap.put("yymm", paramMap.get("recentReport"));
    					
    				} else {
    					int wk = Integer.parseInt(paramMap.get("recentReport").toString().substring(6));
    					int yymm = Integer.parseInt(paramMap.get("recentReport").toString().substring(0,6));
    					
    					insertParamMap.put("studId", paramMap.get("studId"));
    					insertParamMap.put("yymm", yymm);
    					insertParamMap.put("wk", wk);
    				}
    			}
				
    			try {
					row = commonMapperLrnType.delete(insertParamMap, "StudLrnType.deleteReportCheck");
					
					if(row > 0) {
						data.put("msg", "Success");
						data.put("deleteYn", "Y");
						
					} else {
						data.put("msg", "Fail");
						data.put("deleteYn", "N");
					}
					
				} catch (Exception e) {
					data.put("msg", "Fail");
					data.put("deleteYn", "N");
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
	
	private String encodeStudId(String studId) throws Exception {
		String encodeStudId = null;
		
		CipherUtil cps = CipherUtil.getInstance();
		encodeStudId = cps.AES_Encode(studId);
		
		return encodeStudId;
	}
	
	private Map createTestDataMap(String dt, String day, Object planCnt, Object fnshCnt) {
        LinkedHashMap<String,Object> dummyMap = new LinkedHashMap<>();
        
        dummyMap.put("dt",dt);
        dummyMap.put("day",day);
        dummyMap.put("planCnt",planCnt);
        dummyMap.put("fnshCnt",fnshCnt);
        
        return dummyMap;

	}
	
	private Map createTestDataMap2(int mm, int wk, Object lrnExRt) {
        LinkedHashMap<String,Object> dummyMap = new LinkedHashMap<>();
        
        dummyMap.put("mm",mm);
        
        if(wk > 0) {
        	dummyMap.put("wk",wk);
        }
        
        dummyMap.put("lrnExRt",lrnExRt);
        
        return dummyMap;

	}
	
	private Map createLrnExRtDataMap(int mm, int wk, Object lrnExRt) {
        LinkedHashMap<String,Object> dummyMap = new LinkedHashMap<>();
        
        dummyMap.put("mm",mm);
        
        if(wk > 0) {
        	dummyMap.put("wk",wk);
        }
        
        dummyMap.put("lrnExRt",lrnExRt);
        
        return dummyMap;

	}
	
	private Map createSlvHabitDataMap(String quesType, ArrayList quesList) {
        LinkedHashMap<String,Object> dummyMap = new LinkedHashMap<>();
        
        dummyMap.put("quesType", quesType);
        dummyMap.put("quesList", quesList);
        
        return dummyMap;

	}
	
	private Map createExRtDataMap(int mm, int wk, Object exRt) {
        LinkedHashMap<String,Object> dummyMap = new LinkedHashMap<>();
        
        dummyMap.put("mm",mm);
        
        if(wk > 0) {
        	dummyMap.put("wk",wk);
        }
        
        dummyMap.put("exRt",exRt);
        
        return dummyMap;

	}
	
	private List createSlvHabitDataList(String mm, String wk, String slvList) {
		ArrayList<Map<String, Object>> dummyList = new ArrayList<>();
		
		List<String> mmList = Arrays.asList(mm.split(","));
		List<String> slvHabitList = Arrays.asList(slvList.split(","));
		
		if(wk != null) {
			List<String> wkList = Arrays.asList(wk.split(","));
			
			for(int i = 0; i < 5; i++) {
				LinkedHashMap<String,Object> dummyMap = new LinkedHashMap<>();
				dummyMap.put("mm", Integer.parseInt(mmList.get(i)));
				dummyMap.put("wk", Integer.parseInt(wkList.get(i)));
				dummyMap.put("quesCnt", (slvHabitList.get(i).equals(" ")) ? null : slvHabitList.get(i));
				
				dummyList.add(dummyMap);
			}
		} else {
			for(int i = 0; i < 5; i++) {
				LinkedHashMap<String,Object> dummyMap = new LinkedHashMap<>();
				dummyMap.put("mm", Integer.parseInt(mmList.get(i)));
				dummyMap.put("quesCnt", (slvHabitList.get(i).equals(" ")) ? null : slvHabitList.get(i));
				
				dummyList.add(dummyMap);
			}
		}
        return dummyList;

	}
	
	private Map createTestDataMap3(String subjCd, int cnt) {
        LinkedHashMap<String,Object> dummyMap = new LinkedHashMap<>();
        
        dummyMap.put("subjCd",subjCd);
        dummyMap.put("cnt",cnt);
        return dummyMap;

	}
	
	// for QA
    private Map callExApi(Map<String, Object> paramMap) throws Exception {
    	
    	String apiName = ((String)paramMap.remove("apiName")).replaceAll("\\.", "\\/"); // '.'을 '/'로 변환, 맵에서 삭제
		RestTemplate restTemplate = new RestTemplate();
		
    	try {
        	String studId = "";
    		String encodedStr = paramMap.get("p").toString();
    		
    		String[] paramList = getDecodedParam(encodedStr);
    		studId = paramList[1];
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
