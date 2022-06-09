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
								checkYn = "N";
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
								checkYn = "N";
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
	        				Map<String, Object> examMap = new LinkedHashMap<String, Object>();
	        				ArrayList<Map<String, Object>> examSubjList = new ArrayList<>();
	        				Map<String, Object> examQuesMap = new LinkedHashMap<String, Object>();
	        				
	        				learnMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getLrnSummary");
	        				examSubjList = (ArrayList<Map<String, Object>>) studLrnAnalMapper.getList(paramMap, "StudReport.getExamSubjSummary");
	        				examQuesMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getExamQuesSummary");
	        				
	        				/*과목별 평가 점수 초기 값*/
	        				examMap.put("examScore", null);
	        				examMap.put("c01examScore", null);
	        				examMap.put("c02examScore", null);
	        				examMap.put("c03examScore", null);
	        				examMap.put("c04examScore", null);
	        				examMap.put("c05examScore", null);
	        				examMap.put("c06examScore", null);
	        				
	        				for(Map<String, Object> examSubjItme : examSubjList) {
	        					String subjCd = examSubjItme.get("subjCd").toString();
	        					int score = Integer.valueOf(examSubjItme.get("exRt").toString());
	        					
	        					if(subjCd.equals("c00")) {
	        						examMap.put("examScore", score);
	        					} else {
	        						examMap.put(subjCd+"examScore", score);
	        					}
	        				}
	        				
	        				examMap.put("incrtNoteNcCnt", null);
	        				examMap.put("skipQuesCnt", (examQuesMap != null) ? examQuesMap.get("quesSkipCnt") : null);
	        				examMap.put("cursoryQuesCnt", (examQuesMap != null) ? examQuesMap.get("quesHrryCnt") : null);
	        				examMap.put("guessQuesCnt", (examQuesMap != null) ? examQuesMap.get("quesGussCnt") : null);
	        				examMap.put("mistakeQuesCnt", (examQuesMap != null) ? examQuesMap.get("quesMstkeCnt") : null);
	        				
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
	        				Map<String, Object> examMap = new LinkedHashMap<String, Object>();
	        				ArrayList<Map<String, Object>> examSubjList = new ArrayList<>();
	        				Map<String, Object> examQuesMap = new LinkedHashMap<String, Object>();
	        				
	        				learnMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getLrnSummary");
	        				examSubjList = (ArrayList<Map<String, Object>>) studLrnAnalMapper.getList(paramMap, "StudReport.getExamSubjSummary");
	        				examQuesMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getExamQuesSummary");
	        				
	        				/*과목별 평가 점수 초기 값*/
	        				examMap.put("examScore", null);
	        				examMap.put("c01examScore", null);
	        				examMap.put("c02examScore", null);
	        				examMap.put("c03examScore", null);
	        				examMap.put("c04examScore", null);
	        				examMap.put("c05examScore", null);
	        				examMap.put("c06examScore", null);
	        				
	        				for(Map<String, Object> examSubjItme : examSubjList) {
	        					String subjCd = examSubjItme.get("subjCd").toString();
	        					int score = Integer.valueOf(examSubjItme.get("exRt").toString());
	        					
	        					if(subjCd.equals("c00")) {
	        						examMap.put("examScore", score);
	        					} else {
	        						examMap.put(subjCd+"examScore", score);
	        					}
	        				}
	        				
	        				examMap.put("incrtNoteCnt", (examQuesMap != null) ? examQuesMap.get("wnoteTotCnt") : null);
	        				examMap.put("incrtNoteNcCnt", (examQuesMap != null) ? examQuesMap.get("wnoteUnfnshCnt") : null);
	        				examMap.put("quesTotCnt", (examQuesMap != null) ? examQuesMap.get("quesTotCnt") : null);
	        				examMap.put("skipQuesCnt", (examQuesMap != null) ? examQuesMap.get("quesSkipCnt") : null);
	        				examMap.put("cursoryQuesCnt", (examQuesMap != null) ? examQuesMap.get("quesHrryCnt") : null);
	        				examMap.put("guessQuesCnt", (examQuesMap != null) ? examQuesMap.get("quesGussCnt") : null);
	        				examMap.put("mistakeQuesCnt", (examQuesMap != null) ? examQuesMap.get("quesMstkeCnt") : null);
	        				
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
	        				data.put("imgUrl", attRtDataMap.get("imgUrl"));
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
	        				data.put("imgUrl", attRtDataMap.get("imgUrl"));
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
	        				
	        				ArrayList<Map<String, Object>> c00LrnExRtList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> c01LrnExRtList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> c02LrnExRtList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> c03LrnExRtList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> c04LrnExRtList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> c05LrnExRtList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> c06LrnExRtList = new ArrayList<>();
	        				
	        				String[] subjList = {"C00", "C01", "C02", "C03", "C04", "C05", "C06"};
	        				List<String> convertSubjList = Arrays.asList(subjList);
	        				
	        				yymmDataMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getPeriod");
	        				
	        				int startYymm = Integer.parseInt(yymmDataMap.get("startYymm").toString());
	        				int endYymm = Integer.parseInt(yymmDataMap.get("endYymm").toString());
	        				
	        				paramMap.put("startYymm", startYymm);
	        				paramMap.put("endYymm", endYymm);
	        				
	        				lrnExRtDataMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getlrnExRt");
	        				
	        				lrnExRtList = (ArrayList<Map<String, Object>>) studLrnAnalMapper.getList(paramMap, "StudReport.getSubjLrnExRt");
	        				
	        				for(Map<String, Object> lrnExRtItem : lrnExRtList) {
	        					
	        					String subjCd = lrnExRtItem.get("subjCd").toString();
	        					int mmData = Integer.parseInt(lrnExRtItem.get("yyyymmKey").toString().substring(4, 6));
	        					
	        					switch (subjCd) {
								case "C00":
									c00LrnExRtList.add(createLrnExRtDataMap(mmData, 0, lrnExRtItem.get("exRt")));
									break;
								case "C01":
									c01LrnExRtList.add(createLrnExRtDataMap(mmData, 0, lrnExRtItem.get("exRt")));
									break;
								case "C02":
									c02LrnExRtList.add(createLrnExRtDataMap(mmData, 0, lrnExRtItem.get("exRt")));
									break;
								case "C03":
									c03LrnExRtList.add(createLrnExRtDataMap(mmData, 0, lrnExRtItem.get("exRt")));
									break;
								case "C04":
									c04LrnExRtList.add(createLrnExRtDataMap(mmData, 0, lrnExRtItem.get("exRt")));
									break;
								case "C05":
									c05LrnExRtList.add(createLrnExRtDataMap(mmData, 0, lrnExRtItem.get("exRt")));
									break;
								case "C06":
									c06LrnExRtList.add(createLrnExRtDataMap(mmData, 0, lrnExRtItem.get("exRt")));
									break;
								default:
									break;
								}
	        				}
	        				
	        				for(String subjCdItem : convertSubjList) {
	        					Map<String, Object> lrnExRtMap = new LinkedHashMap<String, Object>();
	        					lrnExRtMap.put("subjCd", subjCdItem);
	        					
	        					switch (subjCdItem) {
								case "C00":
									lrnExRtMap.put("lrnExRtList", c00LrnExRtList);
									break;
								case "C01":
									lrnExRtMap.put("lrnExRtList", c01LrnExRtList);
									break;
								case "C02":
									lrnExRtMap.put("lrnExRtList", c02LrnExRtList);
									break;
								case "C03":
									lrnExRtMap.put("lrnExRtList", c03LrnExRtList);
									break;
								case "C04":
									lrnExRtMap.put("lrnExRtList", c04LrnExRtList);
									break;
								case "C05":
									lrnExRtMap.put("lrnExRtList", c05LrnExRtList);
									break;
								default:
									lrnExRtMap.put("lrnExRtList", c06LrnExRtList);
									break;
	        					}
	        					
								lrnExRtSubjList.add(lrnExRtMap);
	        				}
	        				
	        				String msg = (lrnExRtDataMap.get("msg") != null) ? lrnExRtDataMap.get("msg").toString() : null;
	        				String imgUrl = (lrnExRtDataMap.get("imgUrl") != null) ? lrnExRtDataMap.get("imgUrl").toString() : null;
	        				
	        				data.put("msg", msg);
	        				data.put("imgUrl", imgUrl);
	        				
	        				data.put("lrnExRtSubjList", lrnExRtSubjList);
	        				
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
	        				
	        				ArrayList<Map<String, Object>> lrnExRtSubjList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> lrnExRtList = new ArrayList<>();
	        				
	        				ArrayList<Map<String, Object>> c00LrnExRtList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> c01LrnExRtList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> c02LrnExRtList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> c03LrnExRtList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> c04LrnExRtList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> c05LrnExRtList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> c06LrnExRtList = new ArrayList<>();
	        				
	        				String[] subjList = {"C00", "C01", "C02", "C03", "C04", "C05", "C06"};
	        				List<String> convertSubjList = Arrays.asList(subjList);
	        				
	        				yymmDataMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getPeriod");
	        				
	        				int startYymmwk = Integer.parseInt(yymmDataMap.get("startYymmwk").toString());
	        				int endYymmwk = Integer.parseInt(yymmDataMap.get("endYymmwk").toString());
	        				
	        				paramMap.put("startYymmwk", startYymmwk);
	        				paramMap.put("endYymmwk", endYymmwk);
	        				
	        				lrnExRtList = (ArrayList<Map<String, Object>>) studLrnAnalMapper.getList(paramMap, "StudReport.getSubjLrnExRt");
	        				
	        				for(Map<String, Object> lrnExRtItem : lrnExRtList) {
	        					
	        					String subjCd = lrnExRtItem.get("subjCd").toString();
	        					int mmData = Integer.parseInt(lrnExRtItem.get("yyyymmwKey").toString().substring(4, 6));
	        					int wkData = Integer.parseInt(lrnExRtItem.get("yyyymmwKey").toString().substring(6));
	        					
	        					switch (subjCd) {
								case "C00":
									c00LrnExRtList.add(createLrnExRtDataMap(mmData, wkData, lrnExRtItem.get("exRt")));
									break;
								case "C01":
									c01LrnExRtList.add(createLrnExRtDataMap(mmData, wkData, lrnExRtItem.get("exRt")));
									break;
								case "C02":
									c02LrnExRtList.add(createLrnExRtDataMap(mmData, wkData, lrnExRtItem.get("exRt")));
									break;
								case "C03":
									c03LrnExRtList.add(createLrnExRtDataMap(mmData, wkData, lrnExRtItem.get("exRt")));
									break;
								case "C04":
									c04LrnExRtList.add(createLrnExRtDataMap(mmData, wkData, lrnExRtItem.get("exRt")));
									break;
								case "C05":
									c05LrnExRtList.add(createLrnExRtDataMap(mmData, wkData, lrnExRtItem.get("exRt")));
									break;
								case "C06":
									c06LrnExRtList.add(createLrnExRtDataMap(mmData, wkData, lrnExRtItem.get("exRt")));
									break;
								default:
									break;
								}
	        				}
	        				
	        				for(String subjCdItem : convertSubjList) {
	        					Map<String, Object> lrnExRtMap = new LinkedHashMap<String, Object>();
	        					lrnExRtMap.put("subjCd", subjCdItem);
	        					
	        					switch (subjCdItem) {
								case "C00":
									lrnExRtMap.put("lrnExRtList", c00LrnExRtList);
									break;
								case "C01":
									lrnExRtMap.put("lrnExRtList", c01LrnExRtList);
									break;
								case "C02":
									lrnExRtMap.put("lrnExRtList", c02LrnExRtList);
									break;
								case "C03":
									lrnExRtMap.put("lrnExRtList", c03LrnExRtList);
									break;
								case "C04":
									lrnExRtMap.put("lrnExRtList", c04LrnExRtList);
									break;
								case "C05":
									lrnExRtMap.put("lrnExRtList", c05LrnExRtList);
									break;
								default:
									lrnExRtMap.put("lrnExRtList", c06LrnExRtList);
									break;
	        					}
	        					
								lrnExRtSubjList.add(lrnExRtMap);
	        				}
	        				
	        				data.put("msg", "수행률 메시지");
	        				
	        				data.put("lrnExRtSubjList", lrnExRtSubjList);
	        				
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
	        				String imgUrl = (learnMap.get("imgUrl") != null) ? learnMap.get("imgUrl").toString() : null;
	        				
	        				data.put("msg", msg);
	        				data.put("imgUrl", imgUrl);
	        				data.put("bLrnCnt", learnMap.get("bLrnCnt"));
    						data.put("tLrnCnt", learnMap.get("tLrnCnt"));
    						data.put("dLrnCnt", learnMap.get("dLrnCnt"));
	        				
    						maxLrnHabitCd = (lrnhabitMap.get("maxNm") != null) ? lrnhabitMap.get("maxNm").toString() : null;
        					
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
	        				String imgUrl = (learnMap.get("imgUrl") != null) ? learnMap.get("imgUrl").toString() : null;
	        				
	        				data.put("msg", msg);
	        				data.put("imgUrl", imgUrl);
	        				data.put("bLrnCnt", learnMap.get("bLrnCnt"));
    						data.put("tLrnCnt", learnMap.get("tLrnCnt"));
    						data.put("dLrnCnt", learnMap.get("dLrnCnt"));
	        				
    						maxLrnHabitCd = (lrnhabitMap.get("maxNm") != null) ? lrnhabitMap.get("maxNm").toString() : null;
        					
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
        
        vu.checkRequired(new String[] {"yyyy","mm","p"}, paramMap);
        
        if(vu.isValid()) {
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
    				data.put("imgUrl", aLrnDataMap.get("imgUrl"));
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
	        				Map<String, Object> examScoreMap = new LinkedHashMap<String, Object>();
	        				
	        				ArrayList<Map<String, Object>> examScoreSubjList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> examScoreList = new ArrayList<>();
	        				
	        				ArrayList<Map<String, Object>> c00ExamScoreList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> c01ExamScoreList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> c02ExamScoreList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> c03ExamScoreList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> c04ExamScoreList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> c05ExamScoreList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> c06ExamScoreList = new ArrayList<>();
	        				
	        				String[] subjList = {"C00", "C01", "C02", "C03", "C04", "C05", "C06"};
	        				List<String> convertSubjList = Arrays.asList(subjList);
	        				
	        				yymmDataMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getPeriod");
	        				
	        				int startYymm = Integer.parseInt(yymmDataMap.get("startYymm").toString());
	        				int endYymm = Integer.parseInt(yymmDataMap.get("endYymm").toString());
	        				
	        				paramMap.put("startYymm", startYymm);
	        				paramMap.put("endYymm", endYymm);
	        				
	        				examScoreMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getALrnStt");
	        				
	        				data.put("msg", examScoreMap.get("msg"));
	        				data.put("imgUrl", examScoreMap.get("imgUrl"));
	        				
	        				int subjCnt = Integer.parseInt(examScoreMap.get("subjCnt").toString());
	        				
	        				if(subjCnt > 1) {
	        					
	        				} else if(subjCnt == 1) {
	        					
	        				} else {
	        					examScoreList = null;
	        				}
	        				
	        				examScoreList = (ArrayList<Map<String, Object>>) studLrnAnalMapper.getList(paramMap, "StudReport.getSubjExamScore");
	        				
	        				for(Map<String, Object> examScoreItem : examScoreList) {
	        					
	        					String subjCd = examScoreItem.get("subjCd").toString();
	        					int mmData = Integer.parseInt(examScoreItem.get("yyyymmKey").toString().substring(4, 6));
	        					
	        					switch (subjCd) {
								case "C00":
									c00ExamScoreList.add(createExRtDataMap(mmData, 0, examScoreItem.get("exRt")));
									break;
								case "C01":
									c01ExamScoreList.add(createExRtDataMap(mmData, 0, examScoreItem.get("exRt")));
									break;
								case "C02":
									c02ExamScoreList.add(createExRtDataMap(mmData, 0, examScoreItem.get("exRt")));
									break;
								case "C03":
									c03ExamScoreList.add(createExRtDataMap(mmData, 0, examScoreItem.get("exRt")));
									break;
								case "C04":
									c04ExamScoreList.add(createExRtDataMap(mmData, 0, examScoreItem.get("exRt")));
									break;
								case "C05":
									c05ExamScoreList.add(createExRtDataMap(mmData, 0, examScoreItem.get("exRt")));
									break;
								case "C06":
									c06ExamScoreList.add(createExRtDataMap(mmData, 0, examScoreItem.get("exRt")));
									break;
								default:
									break;
								}
	        				}
	        				
	        				for(String subjCdItem : convertSubjList) {
	        					Map<String, Object> examScoreMap = new LinkedHashMap<String, Object>();
	        					examScoreMap.put("subjCd", subjCdItem);
	        					
	        					switch (subjCdItem) {
								case "C00":
									examScoreMap.put("lrnExRtList", c00ExamScoreList);
									break;
								case "C01":
									examScoreMap.put("lrnExRtList", c01ExamScoreList);
									break;
								case "C02":
									examScoreMap.put("lrnExRtList", c02ExamScoreList);
									break;
								case "C03":
									examScoreMap.put("lrnExRtList", c03ExamScoreList);
									break;
								case "C04":
									examScoreMap.put("lrnExRtList", c04ExamScoreList);
									break;
								case "C05":
									examScoreMap.put("lrnExRtList", c05ExamScoreList);
									break;
								default:
									examScoreMap.put("lrnExRtList", c06ExamScoreList);
									break;
	        					}
	        					
	        					examScoreSubjList.add(examScoreMap);
	        				}
	        				
	        				data.put("msg", "평가점수 메시지");
	        				
	        				data.put("examScoreSubjList", examScoreSubjList);
	        				
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
	        				
	        				ArrayList<Map<String, Object>> examScoreSubjList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> examScoreList = new ArrayList<>();
	        				
	        				ArrayList<Map<String, Object>> c00ExamScoreList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> c01ExamScoreList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> c02ExamScoreList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> c03ExamScoreList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> c04ExamScoreList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> c05ExamScoreList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> c06ExamScoreList = new ArrayList<>();
	        				
	        				String[] subjList = {"C00", "C01", "C02", "C03", "C04", "C05", "C06"};
	        				List<String> convertSubjList = Arrays.asList(subjList);
	        				
	        				yymmDataMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getPeriod");
	        				
	        				int startYymmwk = Integer.parseInt(yymmDataMap.get("startYymmwk").toString());
	        				int endYymmwk = Integer.parseInt(yymmDataMap.get("endYymmwk").toString());
	        				
	        				paramMap.put("startYymmwk", startYymmwk);
	        				paramMap.put("endYymmwk", endYymmwk);
	        				
	        				examScoreList = (ArrayList<Map<String, Object>>) studLrnAnalMapper.getList(paramMap, "StudReport.getSubjExamScore");
	        				
	        				for(Map<String, Object> examScoreItem : examScoreList) {
	        					
	        					String subjCd = examScoreItem.get("subjCd").toString();
	        					int mmData = Integer.parseInt(examScoreItem.get("yyyymmwKey").toString().substring(4, 6));
	        					int wkData = Integer.parseInt(examScoreItem.get("yyyymmwKey").toString().substring(6));
	        					
	        					switch (subjCd) {
								case "C00":
									c00ExamScoreList.add(createExRtDataMap(mmData, wkData, examScoreItem.get("exRt")));
									break;
								case "C01":
									c01ExamScoreList.add(createExRtDataMap(mmData, wkData, examScoreItem.get("exRt")));
									break;
								case "C02":
									c02ExamScoreList.add(createExRtDataMap(mmData, wkData, examScoreItem.get("exRt")));
									break;
								case "C03":
									c03ExamScoreList.add(createExRtDataMap(mmData, wkData, examScoreItem.get("exRt")));
									break;
								case "C04":
									c04ExamScoreList.add(createExRtDataMap(mmData, wkData, examScoreItem.get("exRt")));
									break;
								case "C05":
									c05ExamScoreList.add(createExRtDataMap(mmData, wkData, examScoreItem.get("exRt")));
									break;
								case "C06":
									c06ExamScoreList.add(createExRtDataMap(mmData, wkData, examScoreItem.get("exRt")));
									break;
								default:
									break;
								}
	        				}
	        				
	        				for(String subjCdItem : convertSubjList) {
	        					Map<String, Object> examScoreMap = new LinkedHashMap<String, Object>();
	        					examScoreMap.put("subjCd", subjCdItem);
	        					
	        					switch (subjCdItem) {
								case "C00":
									examScoreMap.put("lrnExRtList", c00ExamScoreList);
									break;
								case "C01":
									examScoreMap.put("lrnExRtList", c01ExamScoreList);
									break;
								case "C02":
									examScoreMap.put("lrnExRtList", c02ExamScoreList);
									break;
								case "C03":
									examScoreMap.put("lrnExRtList", c03ExamScoreList);
									break;
								case "C04":
									examScoreMap.put("lrnExRtList", c04ExamScoreList);
									break;
								case "C05":
									examScoreMap.put("lrnExRtList", c05ExamScoreList);
									break;
								default:
									examScoreMap.put("lrnExRtList", c06ExamScoreList);
									break;
	        					}
	        					
	        					examScoreSubjList.add(examScoreMap);
	        				}
	        				data.put("msg", "평가점수 메시지");
	        				
	        				data.put("examScoreSubjList", examScoreSubjList);
	        				
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
	        					data.put("msg", "오답노트 현황 메시지");
	        					data.put("incrtNoteCnt", incrtNoteMap.get("wnoteTotCnt"));
		        				data.put("incrtNotefnshCnt", incrtNoteMap.get("wnoteFnshCnt"));
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
	        					data.put("msg", "오답노트 현황 메시지");
	        					data.put("incrtNoteCnt", incrtNoteMap.get("wnoteTotCnt"));
		        				data.put("incrtNotefnshCnt", incrtNoteMap.get("wnoteFnshCnt"));
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
	        				ArrayList<Map<String, Object>> slvHabitList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> slvHabitDataList = new ArrayList<>();
	        				
        					yymmDataMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getPeriod");
	        				
	        				int startYymm = Integer.parseInt(yymmDataMap.get("startYymm").toString());
	        				int endYymm = Integer.parseInt(yymmDataMap.get("endYymm").toString());
	        				
	        				paramMap.put("startYymm", startYymm);
	        				paramMap.put("endYymm", endYymm);
	        				
	        				slvHabitDataList = (ArrayList<Map<String, Object>>) studLrnAnalMapper.getList(paramMap, "StudReport.getSlvHabitStt");
	        				
	        				for(Map<String,Object> slvHavitItem : slvHabitDataList) {
	        					Map<String, Object> slvHabitMap = new LinkedHashMap<String, Object>();
	        					
	        					int mmData = Integer.parseInt(slvHavitItem.get("yyyymmKey").toString().substring(4, 6));
	        					
	        					slvHabitMap.put("mm", mmData);
		        				slvHabitMap.put("skipQuesCnt", slvHavitItem.get("quesSkipCnt"));
		        				slvHabitMap.put("cursoryQuesCnt", slvHavitItem.get("quesHrryCnt"));
		        				slvHabitMap.put("guessQuesCnt", slvHavitItem.get("quesGussCnt"));
		        				slvHabitMap.put("mistakeQuesCnt", slvHavitItem.get("quesMstkeCnt"));
	        					
		        				slvHabitList.add(slvHabitMap);
	        				}
	        				
	        				data.put("msg", "문제풀이 습관 메시지");
	        				
	        				data.put("slvHabitList", slvHabitList);
	        				
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
	        				ArrayList<Map<String, Object>> slvHabitList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> slvHabitDataList = new ArrayList<>();
	        				
        					yymmDataMap = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "StudReport.getPeriod");
	        				
	        				int startYymmwk = Integer.parseInt(yymmDataMap.get("startYymmwk").toString());
	        				int endYymmwk = Integer.parseInt(yymmDataMap.get("endYymmwk").toString());
	        				
	        				paramMap.put("startYymmwk", startYymmwk);
	        				paramMap.put("endYymmwk", endYymmwk);
	        				
	        				slvHabitDataList = (ArrayList<Map<String, Object>>) studLrnAnalMapper.getList(paramMap, "StudReport.getSlvHabitStt");
	        				
	        				for(Map<String,Object> slvHavitItem : slvHabitDataList) {
	        					Map<String, Object> slvHabitMap = new LinkedHashMap<String, Object>();
	        					
	        					int mmData = Integer.parseInt(slvHavitItem.get("yyyymmwKey").toString().substring(4, 6));
	        					int wkData = Integer.parseInt(slvHavitItem.get("yyyymmwKey").toString().substring(6));
	        					
	        					slvHabitMap.put("mm", mmData);
	        					slvHabitMap.put("wk", wkData);
		        				slvHabitMap.put("skipQuesCnt", slvHavitItem.get("quesSkipCnt"));
		        				slvHabitMap.put("cursoryQuesCnt", slvHavitItem.get("quesHrryCnt"));
		        				slvHabitMap.put("guessQuesCnt", slvHavitItem.get("quesGussCnt"));
		        				slvHabitMap.put("mistakeQuesCnt", slvHavitItem.get("quesMstkeCnt"));
	        					
		        				slvHabitList.add(slvHabitMap);
	        				}
	        				
	        				data.put("msg", "문제풀이 습관 메시지");
	        				
	        				data.put("slvHabitList", slvHabitList);
	        				
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
    public Map getReportEmotion(Map<String, Object> paramMap) throws Exception {
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
	        			
	        			paramMap.put("yymm", yyyy+convertMm);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			
	        			if(vu1.isValid()) {
	        				data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "StudLrnType.getReportEmotion");
	        				
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
	        			
	        			paramMap.put("yymm", yyyy+convertMm);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			vu2.isNumeric("wk", wk);
	        			
	        			if(vu1.isValid() && vu2.isValid()) {
	        				data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "StudLrnType.getReportEmotion");
	        				
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
    public Map insertReportEmotion(Map<String, Object> paramMap) throws Exception {
    	Map<String,Object> data = new LinkedHashMap<>();
    	Map<String, Object> msg = new LinkedHashMap<>();
    	
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
	        			
	        			paramMap.put("yymm", yyyy+convertMm);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			
	        			if(vu1.isValid()) {
	        				Map<String,Object> emotionData = new LinkedHashMap<>();
	        				emotionData = (Map<String, Object>) commonMapperLrnType.get(paramMap, "StudLrnType.getReportEmotion");
	        				
	        				String emotionCheck = emotionData.get("emotionYn").toString();
	        				
	        				if(emotionCheck.equals("N")) {
	        					Map<String,Object> insertParamMap = new LinkedHashMap<>();
	        					
	        					insertParamMap.put("yymm", paramMap.get("yymm"));
	        					insertParamMap.put("studId", paramMap.get("studId"));
	        					insertParamMap.put("emoticon", paramMap.get("emoticon"));
	        					
	        					int row = 0;
	        					row = commonMapperLrnType.insert(paramMap, "StudLrnType.insertReportEmotion");
	        					
	        					if(row > 0) {
	        						data.put("msg", "Success");
	        						data.put("insertYn", "Y");
	        						
	        						setResult(dataKey,data);
	        					}else {
	        						msg.put("resultCode", 9999);
		        					msg.put("result", "Insert Error");
		        					setResult(msgKey, msg);
	        					}
	        				} else {
	        					msg.put("resultCode", 9999);
	        					msg.put("result", "Duplicate Error");
	        					setResult(msgKey, msg);
	        				}
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
	        			
	        			paramMap.put("yymm", yyyy+convertMm);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			vu2.isNumeric("wk", wk);
	        			
	        			if(vu1.isValid() && vu2.isValid()) {
	        				Map<String,Object> emotionData = new LinkedHashMap<>();
	        				emotionData = (Map<String, Object>) commonMapperLrnType.get(paramMap, "StudLrnType.getReportEmotion");
	        				
	        				String emotionCheck = emotionData.get("emotionYn").toString();
	        				
	        				if(emotionCheck.equals("N")) {
	        					Map<String,Object> insertParamMap = new LinkedHashMap<>();
	        					
	        					insertParamMap.put("yymm", paramMap.get("yymm"));
	        					insertParamMap.put("wk", paramMap.get("wk"));
	        					insertParamMap.put("studId", paramMap.get("studId"));
	        					insertParamMap.put("emoticon", paramMap.get("emoticon"));
	        					
	        					int row = 0;
	        					row = commonMapperLrnType.insert(paramMap, "StudLrnType.insertReportEmotion");
	        					
	        					if(row > 0) {
	        						data.put("msg", "Success");
	        						data.put("insertYn", "Y");
	        						
	        						setResult(dataKey,data);
	        					}else {
	        						msg.put("resultCode", 9999);
		        					msg.put("result", "Insert Error");
		        					setResult(msgKey, msg);
	        					}
	        				} else {
	        					msg.put("resultCode", 9999);
	        					msg.put("result", "Duplicate Error");
	        					setResult(msgKey, msg);
	        				}
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
    		
    		int studId = Integer.parseInt(decodedStr);
    		
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
	
	private Map createLrnSecDataMap(int mm, int wk, Object lrnSec) {
        LinkedHashMap<String,Object> dummyMap = new LinkedHashMap<>();
        
        dummyMap.put("mm",mm);
        
        if(wk > 0) {
        	dummyMap.put("wk",wk);
        }
        
        dummyMap.put("lrnSec",lrnSec);
        
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
	
	private Map createTestDataMap3(String subjCd, int cnt) {
        LinkedHashMap<String,Object> dummyMap = new LinkedHashMap<>();
        
        dummyMap.put("subjCd",subjCd);
        dummyMap.put("cnt",cnt);
        return dummyMap;

	}
}
