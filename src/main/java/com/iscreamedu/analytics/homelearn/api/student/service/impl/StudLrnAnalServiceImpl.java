package com.iscreamedu.analytics.homelearn.api.student.service.impl;

import com.iscreamedu.analytics.homelearn.api.common.security.CipherUtil;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationCode;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtil;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtilTutor;
import com.iscreamedu.analytics.homelearn.api.student.service.StudLrnAnalService;
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
public class StudLrnAnalServiceImpl implements StudLrnAnalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudLrnAnalServiceImpl.class);

    private LinkedHashMap<String, Object> result;
    private LinkedHashMap<String, Object> decodeResult;
    private String msgKey = "msg";
    private String dataKey = "data";

    @Autowired
    CommonMapperLrnType commonMapperLrnType;

    @Override
    public Map getReportList(Map<String, Object> paramMap) throws Exception {
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
    				Map<String, Object> monthMap = new LinkedHashMap<>();
    				ArrayList<Map<String, Object>> weekList = new ArrayList<>();
    				
    				Map<String, Object> weekMap1 = new LinkedHashMap<>();
    				Map<String, Object> weekMap2 = new LinkedHashMap<>();
    				Map<String, Object> weekMap3 = new LinkedHashMap<>();
    				Map<String, Object> weekMap4 = new LinkedHashMap<>();
    				
    				//data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "StudLrnType.getLrnTypeSummary");
    				
    				monthMap.put("reportNm", "1월 월간 리포트");
    				monthMap.put("publishYn ", "Y");
    				
    				weekMap1.put("reportNm", "1월 1주 주간 리포트");
    				weekMap1.put("publishYn", "Y");
    				
    				weekMap2.put("reportNm", "1월 2주 주간 리포트");
    				weekMap2.put("publishYn", "Y");
    				
    				weekMap3.put("reportNm", "1월 3주 주간 리포트");
    				weekMap3.put("publishYn", "N");
    				
    				weekMap4.put("reportNm", "1월 4주 주간 리포트");
    				weekMap4.put("publishYn", "N");
    				
    				weekList.add(weekMap1);
    				weekList.add(weekMap2);
    				weekList.add(weekMap3);
    				weekList.add(weekMap4);
    				
    				data.put("month", monthMap);
    				data.put("week", weekList);
    				
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
	        			
	        			paramMap.put("yymm", yyyy+convertMm);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			
	        			if(vu1.isValid()) {
	        				Map<String, Object> learnMap = new LinkedHashMap<String, Object>();
	        				Map<String, Object> examMap = new LinkedHashMap<String, Object>();
	        				
	        				//data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "StudLrnType.getLrnTypeSummary");
	        				
	        				learnMap.put("planCnt", 100);
	        				learnMap.put("lrnCnt", 95);
	        				learnMap.put("exRt", 95);
	        				learnMap.put("bLrnCnt", 30);
	        				learnMap.put("tLrnCnt", 40);
	        				learnMap.put("dLrnCnt", 25);
	        				learnMap.put("bLrnRt", 30);
	        				learnMap.put("tLrnRt", 40);
	        				learnMap.put("dLrnRt", 25);
	        				learnMap.put("concnScore", 3);
	        				learnMap.put("concnMsg", "매우좋음");
	        				learnMap.put("lrnSec", 3666);
	        				learnMap.put("aLrnCnt", 20);
	        				
	        				examMap.put("examScore", 76);
	        				examMap.put("c01examScore", null);
	        				examMap.put("c02examScore", 77);
	        				examMap.put("c03examScore", 95);
	        				examMap.put("c04examScore", 0);
	        				examMap.put("c05examScore", 60);
	        				examMap.put("c06examScore", 80);
	        				examMap.put("incrtNoteNcCnt", 3);
	        				examMap.put("skipQuesCnt", 20);
	        				examMap.put("cursoryQuesCnt", 15);
	        				examMap.put("guessQuesCnt", 10);
	        				examMap.put("mistakeQuesCnt", 20);
	        				
	        				data.put("learn", learnMap);
	        				data.put("exam", examMap);
	        				
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
	        				Map<String, Object> learnMap = new LinkedHashMap<String, Object>();
	        				Map<String, Object> examMap = new LinkedHashMap<String, Object>();
	        				
	        				//data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "StudLrnType.getLrnTypeSummary");
	        				
	        				learnMap.put("planCnt", 100);
	        				learnMap.put("lrnCnt", 95);
	        				learnMap.put("exRt", 95);
	        				learnMap.put("bLrnCnt", 30);
	        				learnMap.put("tLrnCnt", 40);
	        				learnMap.put("dLrnCnt", 25);
	        				learnMap.put("bLrnRt", 30);
	        				learnMap.put("tLrnRt", 40);
	        				learnMap.put("dLrnRt", 25);
	        				learnMap.put("concnScore", 3);
	        				learnMap.put("concnMsg", "매우좋음");
	        				learnMap.put("lrnSec", 3666);
	        				learnMap.put("aLrnCnt", 20);
	        				
	        				examMap.put("examScore", 76);
	        				examMap.put("c01examScore", null);
	        				examMap.put("c02examScore", 77);
	        				examMap.put("c03examScore", 95);
	        				examMap.put("c04examScore", 0);
	        				examMap.put("c05examScore", 60);
	        				examMap.put("c06examScore", 80);
	        				examMap.put("incrtNoteNcCnt", 3);
	        				examMap.put("skipQuesCnt", 20);
	        				examMap.put("cursoryQuesCnt", 15);
	        				examMap.put("guessQuesCnt", 10);
	        				examMap.put("mistakeQuesCnt", 20);
	        				
	        				data.put("learn", learnMap);
	        				data.put("exam", examMap);
	        				
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
    public Map getLrnStt(Map<String, Object> paramMap) throws Exception {
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
	        				Map<String, Object> learnSttMap = new LinkedHashMap<String, Object>();
	        				ArrayList<Map<String, Object>> lrnSttList = new ArrayList<>();
	        				
	        				//data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "StudLrnType.getLrnTypeSummary");
	        				
	        				learnSttMap.put("msg", "학습현황 메시지");
	        				learnSttMap.put("planDtCnt", 20);
	        				learnSttMap.put("fnshDtCnt", 18);
	        				learnSttMap.put("planCnt", 90);
	        				learnSttMap.put("fnshCnt", 65);
	        				
	        				lrnSttList.add(createTestDataMap("2022-01-03", "월요일", 10, 5));
	        				lrnSttList.add(createTestDataMap("2022-01-04", "화요일", 10, 5));
	        				lrnSttList.add(createTestDataMap("2022-01-05", "수요일", 10, 5));
	        				lrnSttList.add(createTestDataMap("2022-01-06", "목요일", 10, 5));
	        				lrnSttList.add(createTestDataMap("2022-01-07", "금요일", 10, 5));
	        				lrnSttList.add(createTestDataMap("2022-01-08", "토요일", 10, 5));
	        				lrnSttList.add(createTestDataMap("2022-01-09", "일요일", 10, 5));
	        				lrnSttList.add(createTestDataMap("2022-01-10", "월요일", 10, 5));
	        				lrnSttList.add(createTestDataMap("2022-01-11", "화요일", 10, 5));
	        				lrnSttList.add(createTestDataMap("2022-01-12", "수요일", 10, 5));
	        				lrnSttList.add(createTestDataMap("2022-01-13", "목요일", 10, 5));
	        				lrnSttList.add(createTestDataMap("2022-01-14", "금요일", 10, 5));
	        				lrnSttList.add(createTestDataMap("2022-01-15", "토요일", 10, 5));
	        				lrnSttList.add(createTestDataMap("2022-01-16", "일요일", 10, 5));
	        				lrnSttList.add(createTestDataMap("2022-01-17", "월요일", 10, 5));
	        				lrnSttList.add(createTestDataMap("2022-01-18", "화요일", 10, 5));
	        				lrnSttList.add(createTestDataMap("2022-01-19", "수요일", 10, 5));
	        				lrnSttList.add(createTestDataMap("2022-01-20", "목요일", 10, 5));
	        				lrnSttList.add(createTestDataMap("2022-01-21", "금요일", 10, 5));
	        				lrnSttList.add(createTestDataMap("2022-01-22", "토요일", 10, 5));
	        				lrnSttList.add(createTestDataMap("2022-01-23", "일요일", 10, 5));
	        					        				
	        				data.put("lrnStt", learnSttMap);
	        				data.put("lrnSttList", lrnSttList);
	        				
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
	        				Map<String, Object> learnSttMap = new LinkedHashMap<String, Object>();
	        				ArrayList<Map<String, Object>> lrnSttList = new ArrayList<>();
	        				
	        				//data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "StudLrnType.getLrnTypeSummary");
	        				
	        				learnSttMap.put("msg", "학습현황 메시지");
	        				learnSttMap.put("planDtCnt", 20);
	        				learnSttMap.put("fnshDtCnt", 18);
	        				learnSttMap.put("planCnt", 90);
	        				learnSttMap.put("fnshCnt", 65);
	        				
	        				lrnSttList.add(createTestDataMap("2022-01-10", "월요일", 10, 5));
	        				lrnSttList.add(createTestDataMap("2022-01-11", "화요일", 10, 5));
	        				lrnSttList.add(createTestDataMap("2022-01-12", "수요일", 10, 5));
	        				lrnSttList.add(createTestDataMap("2022-01-13", "목요일", 10, 5));
	        				lrnSttList.add(createTestDataMap("2022-01-14", "금요일", 10, 5));
	        				lrnSttList.add(createTestDataMap("2022-01-15", "토요일", 10, 5));
	        				lrnSttList.add(createTestDataMap("2022-01-16", "일요일", 10, 5));
	        					        				
	        				data.put("lrnStt", learnSttMap);
	        				data.put("lrnSttList", lrnSttList);
	        				
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
	        			
	        			paramMap.put("yymm", yyyy+convertMm);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			
	        			if(vu1.isValid()) {
	        				Map<String, Object> lrnExRtMap = new LinkedHashMap<String, Object>();
	        				ArrayList<Map<String, Object>> lrnExRtSubjList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> lrnExRtList = new ArrayList<>();
	        				
	        				//data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "StudLrnType.getLrnTypeSummary");
	        				
	        				data.put("msg", "수행률 메시지");
	        				
	        				lrnExRtMap.put("subjCd", "C00");
	        				
	        				lrnExRtList.add(createTestDataMap2(10, 0, 12));
	        				lrnExRtList.add(createTestDataMap2(11, 0, 36));
	        				lrnExRtList.add(createTestDataMap2(12, 0, 55));
	        				lrnExRtList.add(createTestDataMap2(01, 0, 84));
	        				lrnExRtList.add(createTestDataMap2(02, 0, 90));
	        					        				
	        				lrnExRtMap.put("lrnExRtList", lrnExRtList);
	        				
	        				lrnExRtSubjList.add(lrnExRtMap);
	        				
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
	        			
	        			paramMap.put("yymm", yyyy+convertMm);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			vu2.isNumeric("wk", wk);
	        			
	        			if(vu1.isValid() && vu2.isValid()) {
	        				Map<String, Object> lrnExRtMap = new LinkedHashMap<String, Object>();
	        				ArrayList<Map<String, Object>> lrnExRtSubjList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> lrnExRtList = new ArrayList<>();
	        				
	        				//data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "StudLrnType.getLrnTypeSummary");
	        				
        					data.put("msg", "수행률 메시지");
	        				
	        				lrnExRtMap.put("subjCd", "C00");
	        				
	        				lrnExRtList.add(createTestDataMap2(01, 2, 12));
	        				lrnExRtList.add(createTestDataMap2(01, 3, 36));
	        				lrnExRtList.add(createTestDataMap2(01, 4, 55));
	        				lrnExRtList.add(createTestDataMap2(01, 5, 84));
	        				lrnExRtList.add(createTestDataMap2(02, 1, 90));
	        					        				
	        				lrnExRtMap.put("lrnExRtList", lrnExRtList);
	        				
	        				lrnExRtSubjList.add(lrnExRtMap);
	        				
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
	        			
	        			paramMap.put("yymm", yyyy+convertMm);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			
	        			if(vu1.isValid()) {
	        				Map<String, Object> lrnhabitMap = new LinkedHashMap<String, Object>();
	        				
	        				//data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "StudLrnType.getLrnTypeSummary");
	        				
	        				data.put("msg", "수행 습관 메시지");
	        				data.put("bLrnCnt", 6);
	        				data.put("tLrnCnt", 5);
	        				data.put("dLrnCnt", 10);
	        				data.put("maxLrnHabitNm", "나중에 했어요");
	        				data.put("maxLrnHabitRt", 33);
	        				
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
        					Map<String, Object> lrnhabitMap = new LinkedHashMap<String, Object>();
	        				
	        				//data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "StudLrnType.getLrnTypeSummary");
	        				
	        				data.put("msg", "수행 습관 메시지");
	        				data.put("bLrnCnt", 6);
	        				data.put("tLrnCnt", 5);
	        				data.put("dLrnCnt", 10);
	        				data.put("maxLrnHabitNm", "나중에 했어요");
	        				data.put("maxLrnHabitRt", 33);
	        				
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
	        			
	        			paramMap.put("yymm", yyyy+convertMm);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			
	        			if(vu1.isValid()) {
	        				Map<String, Object> concnMap = new LinkedHashMap<String, Object>();
	        				ArrayList<Map<String, Object>> concnSubjList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> concnList = new ArrayList<>();
	        				
	        				//data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "StudLrnType.getLrnTypeSummary");
	        				
	        				data.put("msg", "집중도 메시지");
	        				
	        				concnMap.put("subjCd", "C00");
	        				
	        				concnList.add(createTestDataMap2(10, 0, 3));
	        				concnList.add(createTestDataMap2(11, 0, 2));
	        				concnList.add(createTestDataMap2(12, 0, 9));
	        				concnList.add(createTestDataMap2(01, 0, 6));
	        				concnList.add(createTestDataMap2(02, 0, 5));
	        					        				
	        				concnMap.put("concnList", concnList);
	        				
	        				concnSubjList.add(concnMap);
	        				
	        				data.put("concnSubjList", concnSubjList);
	        				
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
	        				Map<String, Object> concnMap = new LinkedHashMap<String, Object>();
	        				ArrayList<Map<String, Object>> concnSubjList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> concnList = new ArrayList<>();
	        				
	        				//data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "StudLrnType.getLrnTypeSummary");
	        				
        					data.put("msg", "수행률 메시지");
	        				
        					concnMap.put("subjCd", "C00");
	        				
	        				concnList.add(createTestDataMap2(01, 2, 3));
	        				concnList.add(createTestDataMap2(01, 3, 9));
	        				concnList.add(createTestDataMap2(01, 4, 6));
	        				concnList.add(createTestDataMap2(01, 5, 2));
	        				concnList.add(createTestDataMap2(02, 1, 5));
	        					        				
	        				concnMap.put("concnList", concnList);
	        				
	        				concnSubjList.add(concnMap);
	        				
	        				data.put("concnSubjList", concnSubjList);
	        				
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
    public Map getLrnTm(Map<String, Object> paramMap) throws Exception {
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
	        				Map<String, Object> lrnTmMap = new LinkedHashMap<String, Object>();
	        				ArrayList<Map<String, Object>> lrnTmSubjList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> lrnTmList = new ArrayList<>();
	        				
	        				Map<String, Object> lrnTmMap1 = new LinkedHashMap<String, Object>();
	        				ArrayList<Map<String, Object>> lrnTmList1 = new ArrayList<>();
	        				
	        				//data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "StudLrnType.getLrnTypeSummary");
	        				
	        				data.put("msg", "홈런 타임 메시지");
	        				
	        				lrnTmMap.put("subjCd", "C00");
	        				
	        				lrnTmList.add(createTestDataMap2(10, 0, 3332));
	        				lrnTmList.add(createTestDataMap2(11, 0, 2848));
	        				lrnTmList.add(createTestDataMap2(12, 0, 1629));
	        				lrnTmList.add(createTestDataMap2(01, 0, 6566));
	        				lrnTmList.add(createTestDataMap2(02, 0, 5483));
	        					        				
	        				lrnTmMap.put("lrnTmList", lrnTmList);
	        				
        					lrnTmMap1.put("subjCd", "C01");
	        				
	        				lrnTmList1.add(createTestDataMap2(10, 0, 3332));
	        				lrnTmList1.add(createTestDataMap2(11, 0, 2848));
	        				lrnTmList1.add(createTestDataMap2(12, 0, 1629));
	        				lrnTmList1.add(createTestDataMap2(01, 0, 6566));
	        				lrnTmList1.add(createTestDataMap2(02, 0, 5483));
	        					        				
	        				lrnTmMap1.put("lrnTmList", lrnTmList1);
	        				
	        				lrnTmSubjList.add(lrnTmMap);
	        				lrnTmSubjList.add(lrnTmMap1);
	        				
	        				data.put("lrnTmSubjList", lrnTmSubjList);
	        				
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
	        				Map<String, Object> lrnTmMap = new LinkedHashMap<String, Object>();
	        				ArrayList<Map<String, Object>> lrnTmSubjList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> lrnTmList = new ArrayList<>();
	        				
	        				Map<String, Object> lrnTmMap1 = new LinkedHashMap<String, Object>();
	        				ArrayList<Map<String, Object>> lrnTmList1 = new ArrayList<>();
	        				
	        				//data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "StudLrnType.getLrnTypeSummary");
	        				
        					data.put("msg", "홈런 타임 메시지");
	        				
        					lrnTmMap.put("subjCd", "C00");
	        				
        					lrnTmList.add(createTestDataMap2(01, 2, 3215));
        					lrnTmList.add(createTestDataMap2(01, 3, 489));
        					lrnTmList.add(createTestDataMap2(01, 4, 663));
        					lrnTmList.add(createTestDataMap2(01, 5, 152));
        					lrnTmList.add(createTestDataMap2(02, 1, 533));
	        					        				
	        				lrnTmMap.put("lrnTmList", lrnTmList);
	        				
        					lrnTmMap1.put("subjCd", "C01");
	        				
        					lrnTmList1.add(createTestDataMap2(01, 2, 3215));
        					lrnTmList1.add(createTestDataMap2(01, 3, 489));
        					lrnTmList1.add(createTestDataMap2(01, 4, 663));
        					lrnTmList1.add(createTestDataMap2(01, 5, 152));
        					lrnTmList1.add(createTestDataMap2(02, 1, 533));
	        					        				
	        				lrnTmMap1.put("lrnTmList", lrnTmList1);
	        				
	        				lrnTmSubjList.add(lrnTmMap);
	        				lrnTmSubjList.add(lrnTmMap1);
	        				
	        				data.put("lrnTmSubjList", lrnTmSubjList);
	        				
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
    			
    			paramMap.put("yymm", yyyy+convertMm);
    			
    			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
    			
    			if(vu1.isValid()) {
    				Map<String, Object> lrnhabitMap = new LinkedHashMap<String, Object>();
    				ArrayList<Map<String, Object>> aLrnList = new ArrayList<>();
    				
    				//data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "StudLrnType.getLrnTypeSummary");
    				
    				data.put("msg", "수행 습관 메시지");
    				data.put("maxALrnSubjNm", 6);
    				
    				aLrnList.add(createTestDataMap3("C01", 20));
    				aLrnList.add(createTestDataMap3("C06", 15));
    				aLrnList.add(createTestDataMap3("C05", 10));
    				
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
	        			
	        			paramMap.put("yymm", yyyy+convertMm);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			
	        			if(vu1.isValid()) {
	        				Map<String, Object> examScoreMap = new LinkedHashMap<String, Object>();
	        				ArrayList<Map<String, Object>> examScoreSubjList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> examScoreList = new ArrayList<>();
	        				
	        				Map<String, Object> examScoreMap1 = new LinkedHashMap<String, Object>();
	        				ArrayList<Map<String, Object>> examScoreList1 = new ArrayList<>();
	        				
	        				//data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "StudLrnType.getLrnTypeSummary");
	        				
	        				data.put("msg", "평가점수 메시지");
	        				
	        				examScoreMap.put("subjCd", "C00");
	        				
	        				examScoreList.add(createTestDataMap2(10, 0, 80));
	        				examScoreList.add(createTestDataMap2(11, 0, 28));
	        				examScoreList.add(createTestDataMap2(12, 0, 69));
	        				examScoreList.add(createTestDataMap2(01, 0, 89));
	        				examScoreList.add(createTestDataMap2(02, 0, 95));
	        					        				
	        				examScoreMap.put("examScoreList", examScoreList);
	        				
	        				examScoreMap1.put("subjCd", "C01");
	        				
	        				examScoreList1.add(createTestDataMap2(10, 0, 33));
	        				examScoreList1.add(createTestDataMap2(11, 0, 48));
	        				examScoreList1.add(createTestDataMap2(12, 0, 99));
	        				examScoreList1.add(createTestDataMap2(01, 0, 66));
	        				examScoreList1.add(createTestDataMap2(02, 0, 83));
	        					        				
	        				examScoreMap1.put("examScoreList", examScoreList1);
	        				
	        				examScoreSubjList.add(examScoreMap);
	        				examScoreSubjList.add(examScoreMap1);
	        				
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
	        			
	        			paramMap.put("yymm", yyyy+convertMm);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			vu2.isNumeric("wk", wk);
	        			
	        			if(vu1.isValid() && vu2.isValid()) {
	        				Map<String, Object> examScoreMap = new LinkedHashMap<String, Object>();
	        				ArrayList<Map<String, Object>> examScoreSubjList = new ArrayList<>();
	        				ArrayList<Map<String, Object>> examScoreList = new ArrayList<>();
	        				
	        				Map<String, Object> examScoreMap1 = new LinkedHashMap<String, Object>();
	        				ArrayList<Map<String, Object>> examScoreList1 = new ArrayList<>();
	        				
	        				//data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "StudLrnType.getLrnTypeSummary");
	        				
        					data.put("msg", "평가점수 메시지");
	        				
        					examScoreMap.put("subjCd", "C00");
	        				
        					examScoreList.add(createTestDataMap2(01, 2, 33));
        					examScoreList.add(createTestDataMap2(01, 3, 59));
        					examScoreList.add(createTestDataMap2(01, 4, 63));
        					examScoreList.add(createTestDataMap2(01, 5, 89));
        					examScoreList.add(createTestDataMap2(02, 1, 93));
	        					        				
        					examScoreMap.put("examScoreList", examScoreList);
	        				
	        				examScoreMap1.put("subjCd", "C01");
	        				
        					examScoreList1.add(createTestDataMap2(01, 2, 35));
        					examScoreList1.add(createTestDataMap2(01, 3, 89));
        					examScoreList1.add(createTestDataMap2(01, 4, 63));
        					examScoreList1.add(createTestDataMap2(01, 5, 91));
        					examScoreList1.add(createTestDataMap2(02, 1, 53));
	        					        				
        					examScoreMap1.put("examScoreList", examScoreList1);
	        				
        					examScoreSubjList.add(examScoreMap);
	        				examScoreSubjList.add(examScoreMap1);
	        				
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
	        			
	        			paramMap.put("yymm", yyyy+convertMm);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			
	        			if(vu1.isValid()) {
	        				Map<String, Object> incrtNoteMap = new LinkedHashMap<String, Object>();
	        				
	        				//data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "StudLrnType.getLrnTypeSummary");
	        				
	        				data.put("msg", "오답노트 현황 메시지");
	        				data.put("incrtNoteCnt", 20);
	        				data.put("incrtNotefnshCnt", 18);
	        				data.put("incrtNoteNcCnt", 2);
	        				data.put("incrtNoteNcRt", 10);
	        				
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
        					Map<String, Object> incrtNoteMap = new LinkedHashMap<String, Object>();
	        				
	        				//data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "StudLrnType.getLrnTypeSummary");
	        				
	        				data.put("msg", "오답노트 현황 메시지");
	        				data.put("incrtNoteCnt", 20);
	        				data.put("incrtNotefnshCnt", 18);
	        				data.put("incrtNoteNcCnt", 2);
	        				data.put("incrtNoteNcRt", 10);
	        				
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
	        			
	        			paramMap.put("yymm", yyyy+convertMm);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			
	        			if(vu1.isValid()) {
	        				Map<String, Object> slvHabitMap = new LinkedHashMap<String, Object>();
	        				ArrayList<Map<String, Object>> slvHabitList = new ArrayList<>();
	        				
	        				Map<String, Object> slvHabitMap1 = new LinkedHashMap<String, Object>();
	        				
	        				//data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "StudLrnType.getLrnTypeSummary");
	        				
	        				data.put("msg", "문제풀이 습관 메시지");
	        				
	        				slvHabitMap.put("mm", 12);
	        				slvHabitMap.put("skipQuesCnt", 2);
	        				slvHabitMap.put("cursoryQuesCnt", 6);
	        				slvHabitMap.put("guessQuesCnt", 5);
	        				slvHabitMap.put("mistakeQuesCnt", 7);
	        				
	        				slvHabitMap1.put("mm", 1);
	        				slvHabitMap1.put("skipQuesCnt", 1);
	        				slvHabitMap1.put("cursoryQuesCnt", 8);
	        				slvHabitMap1.put("guessQuesCnt", 3);
	        				slvHabitMap1.put("mistakeQuesCnt", 5);
	        				
	        				slvHabitList.add(slvHabitMap);
	        				slvHabitList.add(slvHabitMap1);
	        				
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
	        			
	        			paramMap.put("yymm", yyyy+convertMm);
	        			
	        			vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
	        			vu2.isNumeric("wk", wk);
	        			
	        			if(vu1.isValid() && vu2.isValid()) {
	        				Map<String, Object> slvHabitMap = new LinkedHashMap<String, Object>();
	        				ArrayList<Map<String, Object>> slvHabitList = new ArrayList<>();
	        				
	        				Map<String, Object> slvHabitMap1 = new LinkedHashMap<String, Object>();
	        				
	        				//data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "StudLrnType.getLrnTypeSummary");
	        				
	        				data.put("msg", "문제풀이 습관 메시지");
	        				
	        				slvHabitMap.put("mm", 1);
	        				slvHabitMap.put("wk", 2);
	        				slvHabitMap.put("skipQuesCnt", 9);
	        				slvHabitMap.put("cursoryQuesCnt", 7);
	        				slvHabitMap.put("guessQuesCnt", 10);
	        				slvHabitMap.put("mistakeQuesCnt", 6);
	        				
	        				slvHabitMap1.put("mm", 1);
	        				slvHabitMap1.put("wk", 2);
	        				slvHabitMap1.put("skipQuesCnt", 1);
	        				slvHabitMap1.put("cursoryQuesCnt", 8);
	        				slvHabitMap1.put("guessQuesCnt", 3);
	        				slvHabitMap1.put("mistakeQuesCnt", 5);
	        				
	        				slvHabitList.add(slvHabitMap);
	        				slvHabitList.add(slvHabitMap1);
	        				
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
    public Map getReportImpression(Map<String, Object> paramMap) throws Exception {
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
	        				//data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "StudLrnType.getLrnTypeSummary");
	        				
	        				data.put("msg", "리포트 소감 메시지");
	        				data.put("impressionYn", "Y");
	        				data.put("emoticon", "이모티콘");
	        				
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
    						//data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "StudLrnType.getLrnTypeSummary");
	        				
	        				data.put("msg", "리포트 소감 메시지");
	        				data.put("impressionYn", "Y");
	        				data.put("emoticon", "이모티콘");
	        				
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
    public Map insertReportImpression(Map<String, Object> paramMap) throws Exception {
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
	        				//data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "StudLrnType.getLrnTypeSummary");
	        				
	        				data.put("msg", "Success");
	        				data.put("insertYn", "Y");
	        				
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
    						//data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "StudLrnType.getLrnTypeSummary");
	        				
	        				data.put("msg", "Success1");
	        				data.put("insertYn", "Y");
	        				
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
	
	private Map createTestDataMap(String dt, String day, int planCnt, int fnshCnt) {
        LinkedHashMap<String,Object> dummyMap = new LinkedHashMap<>();
        
        dummyMap.put("dt",dt);
        dummyMap.put("day",day);
        dummyMap.put("planCnt",planCnt);
        dummyMap.put("fnshCnth",fnshCnt);
        return dummyMap;

	}
	
	private Map createTestDataMap2(int mm, int wk, int lrnExRt) {
        LinkedHashMap<String,Object> dummyMap = new LinkedHashMap<>();
        
        dummyMap.put("mm",mm);
        
        if(wk > 0) {
        	dummyMap.put("wk",wk);
        }
        
        dummyMap.put("lrnExRt",lrnExRt);
        return dummyMap;

	}
	
	private Map createTestDataMap3(String subjCd, int cnt) {
        LinkedHashMap<String,Object> dummyMap = new LinkedHashMap<>();
        
        dummyMap.put("subjCd",subjCd);
        dummyMap.put("cnt",cnt);
        return dummyMap;

	}
}
