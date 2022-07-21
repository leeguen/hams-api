package com.iscreamedu.analytics.homelearn.api.hamsTutor.service.impl;

import com.iscreamedu.analytics.homelearn.api.common.mapper.CommonMapperTutor;
import com.iscreamedu.analytics.homelearn.api.common.security.CipherUtil;
import com.iscreamedu.analytics.homelearn.api.common.service.ExternalAPIService;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationCode;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtilTutor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iscreamedu.analytics.homelearn.api.common.exception.NoDataException;
import com.iscreamedu.analytics.homelearn.api.hamsTutor.service.CommonLrnMtService;
import com.iscreamedu.analytics.homelearn.api.hamsTutor.service.HamsTutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class HamsTutorServiceImpl implements HamsTutorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HamsTutorServiceImpl.class);
    private static final String TUTOR_NAMESPACE = "HamsTutor";

    private LinkedHashMap<String, Object> result;
    private String msgKey = "msg";
    private String dataKey = "data";
    
    // for QA
    private LinkedHashMap<String, Object> apiResult;
    
    @Autowired
    CommonMapperTutor mapper;
    
    @Autowired
	ExternalAPIService externalAPIservice;
    
    @Autowired
    CommonLrnMtService commonLrnMtService;

    @Override
    public Map getLrnBasicInfo(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequiredWithDt(paramMap);
        
        Map<String,Object> studInfoParamMap = new HashMap<>();
		String p = paramMap.get("p").toString();
    	
    	studInfoParamMap.put("p", p);
    	studInfoParamMap.put("apiName", "aiReport.");
        
        LinkedHashMap<String,String> studInfo = new LinkedHashMap<>();
        //Map<String,Object> studInfoMap = (Map<String, Object>) externalAPIservice.callExternalAPI(studInfoParamMap).get("data");
        Map<String,Object> studInfoMap = (Map<String, Object>) callExApi(studInfoParamMap).get("data");
        
        if(studInfoMap != null) {
        	//DB 조회
        	LinkedHashMap<String,Object> lrnBasicInfo = (LinkedHashMap)mapper.get(paramMap,TUTOR_NAMESPACE + ".getLrnBasicInfo");
        	
        	//학생 정보
        	lrnBasicInfo.put("studId", studInfoMap.get("stuId"));
        	lrnBasicInfo.put("gender", studInfoMap.get("gender"));
        	lrnBasicInfo.put("studNm", studInfoMap.get("name"));
        	lrnBasicInfo.put("loginId", studInfoMap.get("loginId"));
        	lrnBasicInfo.put("schlNm", studInfoMap.get("schoolName"));
        	lrnBasicInfo.put("grade", studInfoMap.get("grade"));
        	
        	//2.0 데이터
        	String[] sqlLists = {"LrnExStt","ALrnExStt","AttStt","ExamStt","ExamSubjList","IncrtNtStt","SlvHabitStt"};
            List<String> dwSqlList = Arrays.asList(sqlLists);
            
        	paramMap.put("period", "d");
        	paramMap.put("sqlList", dwSqlList);
        	
        	Map<String,Object> lrnBasicInfoMap = (Map<String, Object>) commonLrnMtService.getLrnMtData(paramMap);
        	
        	//수행률
    		try {
    			Map<String,Object> lrnExSttMap = (Map<String, Object>) lrnBasicInfoMap.get("LrnExStt");
    			
    			lrnBasicInfo.put("exRt", lrnExSttMap.get("exRt"));
    			lrnBasicInfo.put("dLrnExCnt", lrnExSttMap.get("dLrnCnt"));
    		} catch (Exception e) {
    			LOGGER.debug("LrnExStt : Error");
			}
    		
    		//스스로학습
    		try {
    			Map<String,Object> aLrnExStt = (Map<String, Object>) lrnBasicInfoMap.get("ALrnExStt");
    			
    			lrnBasicInfo.put("aLrnExCnt", aLrnExStt.get("aLrnCnt"));
    			lrnBasicInfo.put("aLrnNm", aLrnExStt.get("maxSubSubjNm"));
    		} catch (Exception e) {
    			LOGGER.debug("ALrnExStt : Error");
			}
    		
    		//출석률
    		try {
    			Map<String,Object> attStt = (Map<String, Object>) lrnBasicInfoMap.get("AttStt");
    			
    			lrnBasicInfo.put("attRt", attStt.get("attRt"));
    		} catch (Exception e) {
    			LOGGER.debug("AttStt : Error");
			}
    		
    		//평가
    		try {
    			Map<String,Object> examStt = (Map<String, Object>) lrnBasicInfoMap.get("ExamStt");
    			
    			lrnBasicInfo.put("explCnt", examStt.get("explCnt"));
    			lrnBasicInfo.put("crtRt", examStt.get("crtRt"));
    			lrnBasicInfo.put("psExplCnt", examStt.get("psExplCnt"));
    		} catch (Exception e) {
    			LOGGER.debug("ExamStt : Error");
			}
    		
    		//과목별 평가
    		try {
    			ArrayList<Map<String,Object>> examSubjList = (ArrayList<Map<String, Object>>) lrnBasicInfoMap.get("ExamSubjList");
    			
    			for(Map<String, Object> subjitem : examSubjList) {
    				String subjCd = subjitem.get("subjCd").toString().replace("C", "c");
    				
    				lrnBasicInfo.put(subjCd + "IncrtCnt", (subjitem.get("incrtCnt") != null) ? subjitem.get("incrtCnt") : 0);
    			}
    		} catch (Exception e) {
    			LOGGER.debug("ExamSubjList : Error");
			}
    		
    		//오답노트
    		try {
    			Map<String,Object> incrtNtStt = (Map<String, Object>) lrnBasicInfoMap.get("IncrtNtStt");
    			
    			lrnBasicInfo.put("incrtNoteNcCnt", incrtNtStt.get("incrtNtNcCnt"));
    		} catch (Exception e) {
    			LOGGER.debug("IncrtNtStt : Error");
			}
    		
    		//문제풀이 습관
    		try {
    			Map<String,Object> slvHabitStt = (Map<String, Object>) lrnBasicInfoMap.get("SlvHabitStt");
    			
    			lrnBasicInfo.put("cursoryQues", slvHabitStt.get("hrryCnt"));
    			lrnBasicInfo.put("skipQues", slvHabitStt.get("skipCnt"));
    			lrnBasicInfo.put("mistakenQues", slvHabitStt.get("mistakeCnt"));
    			lrnBasicInfo.put("guessQues", slvHabitStt.get("guessCnt"));
    		} catch (Exception e) {
    			LOGGER.debug("SlvHabitStt : Error");
			}
        	
        	
        	data.put("lrnBasicInfo",lrnBasicInfo);
        }
        

        setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getLrnGrowthStt(Map<String, Object> paramMap) throws Exception {
            Map<String,Object> data = new HashMap<>();
            checkRequiredWithDt(paramMap);

            //DB 조회
            ArrayList<Map<String,Object>> lrnGrowthStt = new ArrayList<>();
            
            String[] sqlLists = {"LrnExStt","ExamStt","ExamSubjList"};
            List<String> dwSqlList = Arrays.asList(sqlLists);
            
        	paramMap.put("period", (paramMap.get("isWM").equals("W")) ? "d" : "m");
        	paramMap.put("sqlList", dwSqlList);

            for(int i = 0; i<4; i++) {
                if(i != 0) {
                    //월별일때와 주별일때를 나눠서 구한다.
                    // 주별일땐 STARTDT 와 ENDDT를 각각 7씩 빼고, 월별일땐 한달씩 빼서 계산한다.
                    String endDate;
                    String startDate;
                    if(paramMap.get("isWM").equals("W")) {
                        endDate = subDate((String) paramMap.get("endDt"),-7,true,false);
                        startDate = subDate((String) paramMap.get("startDt"),-7,true,false);
                        paramMap.put("endDt",endDate);
                        paramMap.put("startDt",startDate);
                    }
                    else {
                        endDate = subDate((String) paramMap.get("endDt"),-1,false,true);
                        startDate = subDate((String) paramMap.get("startDt"),-1,false,false);
                        paramMap.put("endDt",endDate);
                        paramMap.put("startDt",startDate);
                    }
                    Map<String,Object> item = (Map)mapper.get(paramMap ,TUTOR_NAMESPACE + ".getLrnGrowthStt");
                    
                	Map<String,Object> lrnGrowthSttMap = (Map<String, Object>) commonLrnMtService.getLrnMtData(paramMap);
                    
                	//수행률
            		try {
            			Map<String,Object> lrnExSttMap = (Map<String, Object>) lrnGrowthSttMap.get("LrnExStt");
            			
            			item.put("exRt", lrnExSttMap.get("exRt"));
            		} catch (Exception e) {
            			LOGGER.debug("LrnExStt : Error");
        			}
            		
            		//평가
            		try {
            			Map<String,Object> examStt = (Map<String, Object>) lrnGrowthSttMap.get("ExamStt");
            			
            			item.put("crtRt", examStt.get("crtRt"));
            		} catch (Exception e) {
            			LOGGER.debug("ExamStt : Error");
        			}
            		
            		//과목별 평가
            		try {
            			ArrayList<Map<String,Object>> examSubjList = (ArrayList<Map<String, Object>>) lrnGrowthSttMap.get("ExamSubjList");
            			
            			for(Map<String, Object> subjitem : examSubjList) {
            				String subjCd = subjitem.get("subjCd").toString().replace("C", "c");
            				
            				item.put(subjCd, subjitem.get("crtRt"));
            			}
            		} catch (Exception e) {
            			LOGGER.debug("ExamSubjList : Error");
        			}
                	
                    lrnGrowthStt.add(item);
                }
                else {
                    Map<String,Object> item = (Map)mapper.get(paramMap ,TUTOR_NAMESPACE + ".getLrnGrowthStt");
                    
                    Map<String,Object> lrnGrowthSttMap = (Map<String, Object>) commonLrnMtService.getLrnMtData(paramMap);
                    
                	//수행률
            		try {
            			Map<String,Object> lrnExSttMap = (Map<String, Object>) lrnGrowthSttMap.get("LrnExStt");
            			
            			item.put("exRt", lrnExSttMap.get("exRt"));
            		} catch (Exception e) {
            			LOGGER.debug("LrnExStt : Error");
        			}
            		
            		//평가
            		try {
            			Map<String,Object> examStt = (Map<String, Object>) lrnGrowthSttMap.get("ExamStt");
            			
            			item.put("crtRt", examStt.get("crtRt"));
            		} catch (Exception e) {
            			LOGGER.debug("ExamStt : Error");
        			}
            		
            		//과목별 평가
            		try {
            			ArrayList<Map<String,Object>> examSubjList = (ArrayList<Map<String, Object>>) lrnGrowthSttMap.get("ExamSubjList");
            			
            			for(Map<String, Object> subjitem : examSubjList) {
            				String subjCd = subjitem.get("subjCd").toString().replace("C", "c");
            				
            				item.put(subjCd, subjitem.get("crtRt"));
            			}
            		} catch (Exception e) {
            			LOGGER.debug("ExamSubjList : Error");
        			}
                    
                    lrnGrowthStt.add(item);
                }
            }
            
            Collections.reverse(lrnGrowthStt);
            
            data.put("lrnGrowthStt",lrnGrowthStt);
            setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getLrnExStt(Map<String, Object> paramMap) throws Exception {
            Map<String,Object> data = new HashMap<>();
            checkRequiredWithDt(paramMap);

            //DB 조회
            LinkedHashMap<String,Object> lrnExStt = new LinkedHashMap<>();
            Map<String,Object> exStt = (Map)mapper.get(paramMap ,TUTOR_NAMESPACE + ".getLrnExSttEx");
            Map<String,Object> tmStt = (Map)mapper.get(paramMap ,TUTOR_NAMESPACE + ".getLrnExSttTm");
        	
            //2.0 데이터
        	String[] sqlLists = {"LrnExStt","ALrnExStt","LrnTmStt"};
            List<String> dwSqlList = Arrays.asList(sqlLists);
            
            LocalDate startDate = LocalDate.parse(paramMap.get("startDt").toString());
            LocalDate endDate = LocalDate.parse(paramMap.get("endDt").toString());
            
            int dateDiff = (int) startDate.until(endDate, ChronoUnit.DAYS);
            
        	paramMap.put("period", (dateDiff < 10) ? "d" : "m");
        	paramMap.put("sqlList", dwSqlList);
        	
        	Map<String,Object> lrnLrnExSttMap = (Map<String, Object>) commonLrnMtService.getLrnMtData(paramMap);
            
        	//수행률
    		try {
    			Map<String,Object> lrnExSttMap = (Map<String, Object>) lrnLrnExSttMap.get("LrnExStt");
    			
    			if(exStt == null) {
    				exStt = new HashMap<>();
    			}
    			
    			exStt.put("exRt", lrnExSttMap.get("exRt"));
    			exStt.put("planCnt", lrnExSttMap.get("planCnt"));
    			exStt.put("dLrnExCnt", lrnExSttMap.get("dLrnCnt"));
    			exStt.put("lrnExCnt", lrnExSttMap.get("tLrnCnt"));
    			exStt.put("bLrnExCnt", lrnExSttMap.get("bLrnCnt"));
    		} catch (Exception e) {
    			LOGGER.debug("LrnExStt : Error");
			}
        	
    		//스스로학습
    		try {
    			Map<String,Object> aLrnExStt = (Map<String, Object>) lrnLrnExSttMap.get("ALrnExStt");
    			
    			exStt.put("aLrnExCnt", aLrnExStt.get("aLrnCnt"));
    			exStt.put("aLrnUpperNm", aLrnExStt.get("maxSubjNm"));
    			exStt.put("aLrnNm", aLrnExStt.get("maxSubSubjNm"));
    		} catch (Exception e) {
    			LOGGER.debug("ALrnExStt : Error");
			}
    		
    		//학습시간
    		try {
    			Map<String,Object> lrnTmStt = (Map<String, Object>) lrnLrnExSttMap.get("LrnTmStt");
    			
    			tmStt.put("lrnTm", lrnTmStt.get("totalLrnTm"));
    		} catch (Exception e) {
    			LOGGER.debug("LrnTmStt : Error");
			}
    		
            lrnExStt.put("exStt",exStt);
            lrnExStt.put("tmStt",tmStt);

            data.put("lrnExStt",lrnExStt);
            setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getLrnExChart(Map<String, Object> paramMap) throws Exception {
            Map<String,Object> data = new HashMap<>();
            checkRequiredWithDt(paramMap);

            //DB 조회
            LinkedHashMap<String,Object> lrnExChart = new LinkedHashMap<>();

            //List<Map<String,Object>> dayLrnTmList = (List)mapper.getList(paramMap ,TUTOR_NAMESPACE + ".getLrnExChartDay");
            //List<Map<String,Object>> dayLrnTmMsgList = (List)mapper.getList(paramMap ,TUTOR_NAMESPACE + ".getLrnExChartDayMsg");
            //List<Map<String,Object>> subjLrnTmList = (List)mapper.getList(paramMap ,TUTOR_NAMESPACE + ".getLrnExChartSubj");
            //List<Map<String,Object>> subjLrnTmMsgList = (List)mapper.getList(paramMap ,TUTOR_NAMESPACE + ".getLrnExChartSubjMsg");
            
            List<Map<String,Object>> dayLrnTmList = new ArrayList<Map<String,Object>>();
            List<Map<String,Object>> dayLrnTmMsgList = new ArrayList<Map<String,Object>>();
            List<Map<String,Object>> subjLrnTmList = new ArrayList<Map<String,Object>>();
            List<Map<String,Object>> subjLrnTmMsgList = new ArrayList<Map<String,Object>>();
            
            //2.0 데이터
        	String[] sqlLists = {"LrnTmDayList", "LrnTmDayLog", "LrnTmSubjList", "LrnTmSubjLog"};
            List<String> dwSqlList = Arrays.asList(sqlLists);
            
        	paramMap.put("period", "d");
        	paramMap.put("sqlList", dwSqlList);
        	
        	Map<String,Object> lrnExChartMap = (Map<String, Object>) commonLrnMtService.getLrnMtData(paramMap);
            
        	//일별 과목별 학습시간
    		try {
    			ArrayList<Map<String,Object>> lrnTmDayList = (ArrayList<Map<String, Object>>) lrnExChartMap.get("LrnTmDayList");
    			
    			for(Map<String, Object> dayitem : lrnTmDayList) {
    				Map<String, Object> dayItemMap = new LinkedHashMap<String, Object>();
				
    				dayItemMap.put("dt", dayitem.get("dt"));
    				dayItemMap.put("subjCd", dayitem.get("subjCd"));
    				dayItemMap.put("lrnTm", dayitem.get("lrnTm"));
    				dayItemMap.put("totalLrnTm", dayitem.get("totalLrnTm"));
    				
    				dayLrnTmList.add(dayItemMap);
				}
    			
    		} catch (Exception e) {
    			LOGGER.debug("LrnTmDayList : Error");
			}
    		
    		//일별 과목별 학습시간
    		try {
    			ArrayList<Map<String,Object>> lrnTmDayLogList = (ArrayList<Map<String, Object>>) lrnExChartMap.get("LrnTmDayLog");
    			
    			for(Map<String, Object> dayLogitem : lrnTmDayLogList) {
    				Map<String, Object> dayLogItemMap = new LinkedHashMap<String, Object>();
				
    				dayLogItemMap.put("dt", dayLogitem.get("dt"));
    				dayLogItemMap.put("subjCd", dayLogitem.get("subjCd"));
    				dayLogItemMap.put("subSubjCd", dayLogitem.get("subSubjCd"));
    				dayLogItemMap.put("msg", dayLogitem.get("ctgr"));
    				
    				dayLrnTmMsgList.add(dayLogItemMap);
				}
    			
    		} catch (Exception e) {
    			LOGGER.debug("LrnTmDayList : Error");
			}
    		
    		//과목별 학습시간
    		try {
    			ArrayList<Map<String,Object>> lrnTmSubjList = (ArrayList<Map<String, Object>>) lrnExChartMap.get("LrnTmSubjList");
    			
    			for(Map<String, Object> subjitem : lrnTmSubjList) {
    				Map<String, Object> subjItemMap = new LinkedHashMap<String, Object>();
				
    				subjItemMap.put("subjCd", subjitem.get("subjCd"));
    				subjItemMap.put("totalLrnTm", subjitem.get("totalLrnTm"));
    				subjItemMap.put("subSubjCd", subjitem.get("subSubjCd"));
    				subjItemMap.put("lrnTm", subjitem.get("lrnTm"));
    				
    				subjLrnTmList.add(subjItemMap);
				}
    			
    		} catch (Exception e) {
    			LOGGER.debug("LrnTmDayList : Error");
			}
    		
    		//과목별 학습시간
    		try {
    			ArrayList<Map<String,Object>> lrnTmSubjLogList = (ArrayList<Map<String, Object>>) lrnExChartMap.get("LrnTmSubjLog");
    			
    			for(Map<String, Object> subjitem : lrnTmSubjLogList) {
    				Map<String, Object> subjItemMap = new LinkedHashMap<String, Object>();
				
    				subjItemMap.put("subjCd", subjitem.get("subjCd"));
    				subjItemMap.put("subSubjCd", subjitem.get("subSubjCd"));
    				subjItemMap.put("msg", subjitem.get("ctgr"));
    				
    				subjLrnTmMsgList.add(subjItemMap);
				}
    			
    		} catch (Exception e) {
    			LOGGER.debug("LrnTmDayList : Error");
			}
        	
            lrnExChart.put("dayLrnTmList",dayLrnTmList);
            lrnExChart.put("dayLrnTmMsgList",dayLrnTmMsgList);
            lrnExChart.put("subjLrnTmList",subjLrnTmList);
            lrnExChart.put("subjLrnTmMsgList",subjLrnTmMsgList);

            data.put("lrnExChart",lrnExChart);
            setResult(dataKey,data);
            
        return result;
    }

    @Override
    public Map getLrnTimeLineList(Map<String, Object> paramMap) throws Exception {
            Map<String,Object> data = new HashMap<>();
            checkRequiredWithDt(paramMap);

            //DB 조회
            List<Map<String,Object>> lrnTmList = (List)mapper.getList(paramMap,TUTOR_NAMESPACE + ".getLrnTimeLineList");
            
            //2.0 데이터
        	String[] sqlLists = {"LrnTmlnList"};
            List<String> dwSqlList = Arrays.asList(sqlLists);
            
        	paramMap.put("period", "d");
        	paramMap.put("sqlList", dwSqlList);
        	
        	Map<String,Object> lrnExChartMap = (Map<String, Object>) commonLrnMtService.getLrnMtData(paramMap);
        	
        	//타임라인
        	try {
        		lrnTmList = (ArrayList<Map<String, Object>>) lrnExChartMap.get("LrnTmlnList");
    			
    			
    		} catch (Exception e) {
    			LOGGER.debug("ExamSubjStt : Error");
			}
            
            data.put("lrnTmList",lrnTmList);
            setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getExamStt(Map<String, Object> paramMap) throws Exception {
            Map<String,Object> data = new HashMap<>();
            checkRequiredWithDt(paramMap);

            //DB 조회
            LinkedHashMap<String,Object> examStt = (LinkedHashMap)mapper.get(paramMap ,TUTOR_NAMESPACE + ".getExamStt");
            
            //2.0 데이터
        	String[] sqlLists = {"ExamStt","IncrtNtStt","SlvHabitStt"};
            List<String> dwSqlList = Arrays.asList(sqlLists);
            
            paramMap.put("period", (paramMap.get("isWM").equals("W")) ? "d" : "m");
        	paramMap.put("sqlList", dwSqlList);
        	
        	Map<String,Object> examSttMap = (Map<String, Object>) commonLrnMtService.getLrnMtData(paramMap);
        	
        	//평가
    		try {
    			Map<String,Object> examSttData = (Map<String, Object>) examSttMap.get("ExamStt");
    			
    			examStt.put("explCnt", examSttData.get("explCnt"));
    			examStt.put("crtRt", examSttData.get("crtRt"));
    			examStt.put("ansQuesCnt", examSttData.get("quesCnt"));
    			examStt.put("crtQuesCnt", examSttData.get("crtCnt"));
    		} catch (Exception e) {
    			LOGGER.debug("ExamStt : Error");
			}
    		
    		//오답노트
    		try {
    			Map<String,Object> incrtNtStt = (Map<String, Object>) examSttMap.get("IncrtNtStt");
    			
    			examStt.put("incrtNoteNcCnt", incrtNtStt.get("incrtNtNcCnt"));
    		} catch (Exception e) {
    			LOGGER.debug("IncrtNtStt : Error");
			}
    		
    		//문제풀이 습관
    		try {
    			Map<String,Object> slvHabitStt = (Map<String, Object>) examSttMap.get("SlvHabitStt");
    			
    			examStt.put("cusoryQuesCnt", slvHabitStt.get("hrryCnt"));
    			examStt.put("skipQuesCnt", slvHabitStt.get("skipCnt"));
    			examStt.put("mistakeQuesCnt", slvHabitStt.get("mistakeCnt"));
    			examStt.put("guessQuesCnt", slvHabitStt.get("guessCnt"));
    		} catch (Exception e) {
    			LOGGER.debug("SlvHabitStt : Error");
			}
        	
            data.put("examStt",examStt);
            setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getExamChart(Map<String, Object> paramMap) throws Exception {
            Map<String,Object> data = new HashMap<>();
            checkRequiredWithDt(paramMap);

            //DB 조회
            List<Map<String,Object>> examChart = (List)mapper.getList(paramMap ,TUTOR_NAMESPACE + ".getExamChart");
            
            //2.0 데이터
        	String[] sqlLists = {"ExamSubjList","IncrtNtSubjList","SlvHabitSubjList"};
            List<String> dwSqlList = Arrays.asList(sqlLists);
            
            paramMap.put("period", (paramMap.get("isWM").equals("W")) ? "d" : "m");
        	paramMap.put("sqlList", dwSqlList);
        	
        	Map<String,Object> examChartMap = (Map<String, Object>) commonLrnMtService.getLrnMtData(paramMap);
        	
        	//과목별 평가
    		try {
    			ArrayList<Map<String,Object>> examSubjList = (ArrayList<Map<String, Object>>) examChartMap.get("ExamSubjList");
    			
    			for(Map<String, Object> examChartItem : examChart) {
    				String subjCd = examChartItem.get("subjCd").toString();
    				
    				for(Map<String, Object> examItem : examSubjList) {
    					if(subjCd.equals(examItem.get("subjCd"))) {
    						examChartItem.put("crtRt", examItem.get("crtRt"));
    						examChartItem.put("explCnt", examItem.get("explCnt"));
    						examChartItem.put("ansQuesCnt", examItem.get("quesCnt"));
    						examChartItem.put("crtQuesCnt", examItem.get("crtCnt"));
    						
    						continue;
    					}
    					
    				}
    			}
    			
    		} catch (Exception e) {
    			LOGGER.debug("ExamSubjList : Error");
			}
    		
    		//과목별 오답노트
    		try {
    			ArrayList<Map<String,Object>> incrtNtSubjList = (ArrayList<Map<String, Object>>) examChartMap.get("IncrtNtSubjList");
    			
    			for(Map<String, Object> examChartItem : examChart) {
    				String subjCd = examChartItem.get("subjCd").toString();
    				
    				for(Map<String, Object> incrtNtItem : incrtNtSubjList) {
    					if(subjCd.equals(incrtNtItem.get("subjCd"))) {
    						examChartItem.put("incrtNoteNcCnt", (incrtNtItem.get("incrtNtNcCnt") != null) ? incrtNtItem.get("incrtNtNcCnt") : 0);
    						
    						continue;
    					}
    					
    				}
    			}
    			
    		} catch (Exception e) {
    			LOGGER.debug("IncrtNtSubjList : Error");
			}
			
    		//과목별 문제풀이 습관
    		try {
    			ArrayList<Map<String,Object>> slvHabitSubjList = (ArrayList<Map<String, Object>>) examChartMap.get("SlvHabitSubjList");
    			
    			for(Map<String, Object> examChartItem : examChart) {
    				String subjCd = examChartItem.get("subjCd").toString();
    				
    				for(Map<String, Object> slvHabitItem : slvHabitSubjList) {
    					if(subjCd.equals(slvHabitItem.get("subjCd"))) {
    						examChartItem.put("cusoryQuesCnt", slvHabitItem.get("hrryCnt"));
    						examChartItem.put("skipQuesCnt", slvHabitItem.get("skipCnt"));
    						examChartItem.put("mistakeQuesCnt", slvHabitItem.get("mistakeCnt"));
    						examChartItem.put("guessQuesCnt", slvHabitItem.get("guessCnt"));
    						
    						continue;
    					}
    					
    				}
    			}
    			
    		} catch (Exception e) {
    			LOGGER.debug("SlvHabitSubjList : Error");
			}
    		
            data.put("examChart",examChart);
            setResult(dataKey,data);
        return result;
    }

    @Override
    public Map getExamList(Map<String, Object> paramMap) throws Exception {
            Map<String,Object> data = new HashMap<>();
            checkRequiredWithDt(paramMap);
            paramMap.put("types",paramMap.get("types").toString().split(","));

            //DB 조회
            LinkedHashMap<String,Object> examList = new LinkedHashMap<>();
            Map<String,Object> totalCnt = (Map)mapper.get(paramMap,TUTOR_NAMESPACE + ".getExamListCnt");
            List<Map<String,Object>> list = (List)mapper.getList(paramMap,TUTOR_NAMESPACE + ".getExamList");
            
            //2.0 데이터
        	String[] sqlLists = {"ExamLog"};
            List<String> dwSqlList = Arrays.asList(sqlLists);
            
            paramMap.put("period", "d");
        	paramMap.put("sqlList", dwSqlList);
        	
        	Map<String,Object> examLogList = (Map<String, Object>) commonLrnMtService.getLrnMtData(paramMap);
        	
        	//과목별 평가
    		try {
    			list = (ArrayList<Map<String, Object>>) examLogList.get("ExamLog");
    			
    			totalCnt.put("totalCnt", list.size());
    			
    			
    		} catch (Exception e) {
    			LOGGER.debug("ExamLog : Error");
			}

            examList.put("totalCnt",totalCnt.get("totalCnt"));
            examList.put("list",list);

            data.put("examList",examList);
            setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getAttStt(Map<String, Object> paramMap) throws Exception {
            Map<String,Object> data = new HashMap<>();
            checkRequiredWithDt(paramMap);

            //DB 조회
            LinkedHashMap<String,Object> attStt = new LinkedHashMap<>();
            LinkedHashMap<String,Object> attPtnAnalysis = (LinkedHashMap<String, Object>)mapper.get(paramMap,TUTOR_NAMESPACE + ".getAttSttAnalysis");
            List<Map<String,Object>> attPtnChart = (List)mapper.getList(paramMap,TUTOR_NAMESPACE + ".getAttSttChart");
            
            //2.0 데이터
        	String[] sqlLists = {"AttStt","AttLog"};
            List<String> dwSqlList = Arrays.asList(sqlLists);
            
            paramMap.put("period", (paramMap.get("isWM").equals("W")) ? "d" : "m");
        	paramMap.put("sqlList", dwSqlList);
        	
        	Map<String,Object> attDataMap = (Map<String, Object>) commonLrnMtService.getLrnMtData(paramMap);
        	
        	//출석률
    		try {
    			Map<String,Object> attSttMap = (Map<String, Object>) attDataMap.get("AttStt");
    			
    			attPtnAnalysis.put("attRt", attSttMap.get("attRt"));
    			attPtnAnalysis.put("lrnPlanDtCnt", attSttMap.get("planDtCnt"));
    			attPtnAnalysis.put("attDtCnt", attSttMap.get("attDtCnt"));
    		} catch (Exception e) {
    			LOGGER.debug("AttStt : Error");
			}
    		
    		//일별 출석 내역
    		try {
    			attPtnChart = (ArrayList<Map<String, Object>>) attDataMap.get("AttLog");
    			
    		} catch (Exception e) {
    			LOGGER.debug("AttLog : Error");
			}
            
            attStt.put("attPtnAnalysis",attPtnAnalysis);
            attStt.put("attPtnChart",attPtnChart);

            data.put("attStt",attStt);

            setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getCommMsgCd(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequired(paramMap);

        //DB 조회
        List<Map<String,Object>> commMsgCd = (List<Map<String,Object>>)mapper.getList(paramMap,TUTOR_NAMESPACE + ".getCommMsgCd");
        LinkedHashMap<String,Object> commMsgCdMap = new LinkedHashMap<>();

        for(Map item : commMsgCd) {
            commMsgCdMap.put(item.get("msgCd").toString(),item.get("msg"));
        }

        data.put("commMsgCd",commMsgCdMap);
        setResult(dataKey,data);

        return result;
    }
    
    @Override
    public Map getSubjCd(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequired(paramMap);

        //DB 조회
        List<Map<String,Object>> subjCdList = (List<Map<String,Object>>)mapper.getList(paramMap,TUTOR_NAMESPACE + ".getSubjCd");
        List<Map<String, Object>> subjCodeInfo = new ArrayList();

        if(subjCdList.size() > 0) {
			for (Map<String,Object> item : subjCdList) {
				subjCodeInfo.add(item);
			}
			
			data.put("subjCodeInfo", subjCodeInfo);
		}

        setResult(dataKey,data);

        return result;
    }

    //p,startDt,endDt 비교 메서드
    private void checkRequired(Map<String,Object> params) throws Exception {
        ValidationUtilTutor vu = new ValidationUtilTutor();
        //필수값 체크
        vu.checkRequired(new String[] {"p"},params);

        //복호화
        String[] encodedArr = getDecodedParam(params.get("p").toString());
        String encodedStudId = encodedArr[1];

        //DB params
        params.put("studId",encodedStudId);
    }

    //p,startDt,endDt 비교 메서드
    private void checkRequiredWithDt(Map<String,Object> params) throws Exception {
        ValidationUtilTutor vu = new ValidationUtilTutor();
        //필수값 체크
        vu.checkRequired(new String[] {"p","startDt","endDt"},params);
        vu.isDate("startDt",(String)params.get("startDt"));
        vu.isDate("endDt",(String)params.get("endDt"));

        //복호화
        String[] encodedArr = getDecodedParam(params.get("p").toString());
        String encodedStudId = encodedArr[1];

        //DB params
        params.put("studId",encodedStudId);
    }

    private String subDate(String paramDt,int day,boolean isW,boolean isWEnd) throws ParseException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");

        Date dt = form.parse(paramDt);
        cal.setTime(dt);

        if(isW) {
            cal.add(Calendar.DATE,day);
        }
        else {
            if(isWEnd) {
                cal.add(Calendar.MONTH,day);
                int dayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                cal.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            }
            else {
                cal.add(Calendar.MONTH,day);
            }
        }
        System.out.println("format getTime:::" + form.format(cal.getTime()));

        return form.format(cal.getTime());
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
    /**
     * 서비스단에서 리턴되는 결과(메시지,데이터 object를 포함한 result)세팅.
     * @param key
     * @param data
     */
    private void setResult(String key, Object data) {
        LinkedHashMap message = new LinkedHashMap();
        result = new LinkedHashMap();

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
            result.put(msgKey, message);
            result.put(dataKey, data);
        }
    }

    /**
     * encoded parameter decode
     */
    public String[] getDecodedParam(String encodedParam) throws Exception {
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
}
