package com.iscreamedu.analytics.homelearn.api.group.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iscreamedu.analytics.homelearn.api.common.service.ExternalAPIService;
import com.iscreamedu.analytics.homelearn.api.common.exception.NoDataException;
import com.iscreamedu.analytics.homelearn.api.common.mapper.CommonMapperTutor;
import com.iscreamedu.analytics.homelearn.api.common.security.CipherUtil;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationCode;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtilTutor;
import com.iscreamedu.analytics.homelearn.api.common.util.VersionUtil;
import com.iscreamedu.analytics.homelearn.api.group.service.GroupService;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtil;

@Service
public class GroupServiceImpl implements GroupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupServiceImpl.class);
    private static final String MAPPER_NAMESPACE = "Group_";
    private String dw_mapper_namespace;
    private String mapper_name;
    
    
    private VersionUtil versionUtil;

    private Map<String, Object> v_param;
    private Map<String, Object> v_result;
    private LinkedHashMap<String, Object> result;
    private String msgKey = "msg";
    private String dataKey = "data";

    @Autowired
    CommonMapperTutor mapper_1_0;
	@Autowired
//    CommonMapperTutor_1_5 mapper_1_5;	//차후 교체
    CommonMapperTutor mapper_1_5;
	@Autowired
//    CommonMapperTutor_2.0 mapper_2_0;	//차후 교체
    CommonMapperTutor mapper_2_0;
//    @Autowired
//	CommonMapper commonMapper;
	
	@Autowired
	ExternalAPIService externalAPIservice;
    
	/***
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
    private Map<String,Object> getDWVersion(Map<String,Object> params) throws Exception {        
    	//channel params
    	params.put("CHANNEL","HAMS-ORG");
        return versionUtil.getDataWareVersion(params);
    }
    
	/***
	 * 분기된 mapper를 통해 data 결과 추출
	 * @param param 구분 기준 파라미터
	 * @param sqlRequestType 리턴 타입
	 * @param paramMap 요청 파라미터 
	 * @param sqlId 매핑된 SQL 호출 id
	 * @return
	 */
	private Object getMapperResultData(Map<String, Object> param, String sqlRequestType, Map<String, Object> paramMap, String sqlId) {
		
		Object r_result;
		try {
			v_result = getDWVersion(param);
	    	
	    	dw_mapper_namespace = MAPPER_NAMESPACE + v_result.get("DW_VERSION").toString();    	
	    	mapper_name = v_result.get("MAPPER_NAME").toString();    	
	    	
			if (mapper_name == "CommonMapperTutor") {
				if(sqlRequestType.toLowerCase() == "list") {
					r_result = mapper_1_0.getList(paramMap, dw_mapper_namespace + sqlId);
				} else {
					r_result = mapper_1_0.get(paramMap, dw_mapper_namespace + sqlId);
				}
			} else if (mapper_name == "CommonMapperTutorTemp") {
				//mapper_1_5
				if(sqlRequestType.toLowerCase() == "list") {
					r_result = mapper_1_5.getList(paramMap, dw_mapper_namespace + sqlId);
				} else {
					r_result = mapper_1_5.get(paramMap, dw_mapper_namespace + sqlId);
				}
			} else {
				//mapper_2_0
				if(sqlRequestType.toLowerCase() == "list") {
					r_result = mapper_2_0.getList(paramMap, dw_mapper_namespace + sqlId);
				} else {
					r_result = mapper_2_0.get(paramMap, dw_mapper_namespace + sqlId);
				}
			}
			
			return r_result;
		} catch (Exception e) {
			
			ValidationUtil vu = new ValidationUtil();
			vu.setError("db mapper result data fail");
			LOGGER.debug(e.getMessage());
			return null; 
		}
	}
	
	/**
     * HAMS-ORG-CM-001
	 * 년월, 주차 산출 - 기간정보
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
    @Override
    public Map getPeriod(Map<String, Object> paramMap) throws Exception {
    	
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "PERIOD");
    	
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		ValidationUtil vu1 = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"lrnStartDt"}, paramMap);
		
		if(vu.isValid()) { 		
			//2. 유효성 체크
			vu1.isDate("lrnStartDt", paramMap.get("lrnStartDt").toString());

			if(vu1.isValid()) {
				
				if(!paramMap.containsKey("currCon") || paramMap.get("currCon").equals("")) {	// 주간+월간 합산
					Map<String,Object> data = new HashMap<>();
					data.put("weeks", (List)getMapperResultData(v_param, "list", paramMap, ".getPeriodWeeks"));
					data.put("months", (List)getMapperResultData(v_param, "list", paramMap, ".getPeriodMonths"));
					setResult(dataKey, data);
				} else {

					String currConCheck = paramMap.get("currCon").toString().toLowerCase();
					paramMap.put("currConCheck", currConCheck);
					if(currConCheck.equals("m")) {
						setResult(dataKey, (List)getMapperResultData(v_param, "list", paramMap, ".getPeriodMonths"));
					} else {
						setResult(dataKey, (List)getMapperResultData(v_param, "list", paramMap, ".getPeriodWeeks"));							
					}
				}
			} else {
				setResult(msgKey, vu1.getResult());
			}
		} else {
			setResult(msgKey, vu.getResult());
		}
		
    	return result;
    }

	/**
	 * HAMS-ORG-CM-002
	 * 학습분석 메인 - 학생 정보
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
    @Override
    public Map getStud(Map<String, Object> paramMap) throws Exception {
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "STUD");

		getStudId(paramMap);
		
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId"}, paramMap);
		
		if(vu.isValid()) { 		
			setResult(dataKey, getMapperResultData(v_param, "", paramMap, ".getStud"));
		} else {
			setResult(msgKey, vu.getResult());
		}
		
    	return result;
    }
    
    /**
	 * HAMS-ORG-CM-003	
	 * 학습분석 메인 - 학습분석 기본정보
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
    @Override
    public Map getLrnBasic(Map<String, Object> paramMap) throws Exception {
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "LRNBASIC");

		getStudId(paramMap);
		
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		ValidationUtil vu1 = new ValidationUtil();
		ValidationUtil vu2 = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"currCon","studId"}, paramMap);
		if(vu.isValid()) { 		
			Map<String,Object> cData = new HashMap<>();
			Map<String,Object> data = new HashMap<>();
            String startDate;
			String endDate;
			
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			ArrayList<Map<String,Object>> positive = new ArrayList<>();
			ArrayList<Map<String,Object>> negative = new ArrayList<>();
			Map<String,Object> msg = new HashMap<>();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {	// 월간
				//1-1.필수값 체크 
				vu1.checkRequired(new String[] {"yyyy","mm"}, paramMap);
				
				if(vu1.isValid()) { 	
					String yyyy = paramMap.get("yyyy").toString();
					int mm = Integer.valueOf(paramMap.get("mm").toString());
					String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
					String yymm = yyyy + convertMm;

					paramMap.put("yymm", yymm);
					//2. 유효성 체크
					vu2.isYearMonth("yyyy, mm", yymm);
					if(vu2.isValid()) {
						startDate = yyyy+"-"+convertMm+"-01";
						endDate = yyyy+"-"+convertMm+"-"+getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd"));
						paramMap.put("startDt", startDate);
						paramMap.put("endDt", endDate);
						cData = (Map<String, Object>)getMapperResultData(v_param, "", paramMap, ".getLrnBasicMonthly");
						if(cData != null) {
							Map<String, Object> currentMap = new LinkedHashMap<>();
							Map<String, Object> prevMap = new LinkedHashMap<>();
							
							currentMap.put("dt", cData.get("dt"));
							currentMap.put("dtStr", cData.get("dtStr"));
							currentMap.put("attRt", cData.get("attRt"));
							currentMap.put("exRt", cData.get("exRt"));
							currentMap.put("crtRt", cData.get("crtRt"));
							currentMap.put("lrnSignal", cData.get("lrnSignal"));
							currentMap.put("dayLrnTm", cData.get("dayLrnTm"));
							
							prevMap.put("dt", cData.get("prevDt"));
							prevMap.put("dtStr", cData.get("prevDtStr"));
							prevMap.put("attRt", cData.get("prevAttRt"));
							prevMap.put("exRt", cData.get("prevExRt"));
							prevMap.put("crtRt", cData.get("prevCrtRt"));
							prevMap.put("lrnSignal", cData.get("prevLrnSignal"));
							prevMap.put("dayLrnTm", cData.get("prevDayLrnTm"));
							
							data.put("current", currentMap);
							data.put("prev", prevMap);
						
							//current 기준 prev 구하기 
			                data.put("prevDtCnt", getCalendarLastDay(subDate(startDate,-1,false,false), new SimpleDateFormat("yyyy-MM-dd")));
				        	
				        	msg.put("positive", positive);
				        	msg.put("negative", negative);
				            data.put("msg",msg);	//추후 메시지기획안 적용 예정	
						}
						setResult(dataKey, data);
					} else {
						setResult(msgKey, vu2.getResult());				
					}
				} else {
					setResult(msgKey, vu1.getResult());
				}
	        } else {	// 주간 & 기간
	        	//1-1.필수값 체크
				vu.checkRequired(new String[] {"startDt","endDt"}, paramMap);
				
				if(vu.isValid()) { 	
					startDate = paramMap.get("startDt").toString();
					endDate = paramMap.get("endDt").toString();
					
					//2. 유효성 체크
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					
					if(vu1.isValid() && vu2.isValid()) {
						paramMap.put("yymm", startDate.substring(0,4)+startDate.substring(5,7));
						cData = (Map<String, Object>)getMapperResultData(v_param, "", paramMap, ".getLrnBasicPeriod");
						if(cData != null) {
							Map<String, Object> currentMap = new LinkedHashMap<>();
							Map<String, Object> prevMap = new LinkedHashMap<>();
							
							currentMap.put("dt", cData.get("dt"));
							currentMap.put("dtStr", cData.get("dtStr"));
							currentMap.put("attRt", cData.get("attRt"));
							currentMap.put("exRt", cData.get("exRt"));
							currentMap.put("crtRt", cData.get("crtRt"));
							currentMap.put("lrnSignal", cData.get("lrnSignal"));
							currentMap.put("dayLrnTm", cData.get("dayLrnTm"));
							
							prevMap.put("dt", cData.get("prevDt"));
							prevMap.put("dtStr", cData.get("prevDtStr"));
							prevMap.put("attRt", cData.get("prevAttRt"));
							prevMap.put("exRt", cData.get("prevExRt"));
							prevMap.put("crtRt", cData.get("prevCrtRt"));
							prevMap.put("lrnSignal", cData.get("prevLrnSignal"));
							prevMap.put("dayLrnTm", cData.get("prevDayLrnTm"));
							
							data.put("current", currentMap);
							data.put("prev", prevMap);
							
			                data.put("prevDtCnt", "7");
				        	
				        	msg.put("positive", positive);
				        	msg.put("negative", negative);
				            data.put("msg",msg);	//추후 메시지기획안 적용 예정	
						}
			    		setResult(dataKey, data);
					} else {
						if(!vu1.isValid()) {
							setResult(msgKey, vu1.getResult());
						} else if(!vu2.isValid()) {
							setResult(msgKey, vu2.getResult());						
						}
					}
				} else {
					setResult(msgKey, vu.getResult());
				}
			}
		} else {
			setResult(msgKey, vu.getResult());
		}
		
    	return result;
    }

    /***
     * 해당월의 마지막날짜 추출
     * @param curDate
     * @param transFormat
     * @return 오류발생시 0 리턴
     */
	private int getCalendarLastDay(String curDate, DateFormat transFormat) {
		try {
			Calendar dt = Calendar.getInstance();
			dt.setTime(transFormat.parse(curDate));
			return dt.getActualMaximum(Calendar.DATE);
		} catch(ParseException pe) {
			return 0;
		}
	}

    @Override
    public Map getOrgEnvConfig(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override
    public Map setOrgEnvConfig(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

	/**
	 * HAMS-ORG-LS-001 
	 * 학습분석 요약 - 학습 습관 차트
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
    @Override
    public Map getLrnHabitChart(Map<String, Object> paramMap) throws Exception {
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "LRNHABITCHART");

		getStudId(paramMap);
		
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		ValidationUtil vu1 = new ValidationUtil();
		ValidationUtil vu2 = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"currCon","studId"}, paramMap);
		
		if(vu.isValid()) { 		
			Map<String,Object> data = new HashMap<>();
            String startDate;
			String endDate;
			
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			ArrayList<Map<String,Object>> detailList = new ArrayList<>();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {	// 월간
				//1-1.필수값 체크 
				vu1.checkRequired(new String[] {"yyyy","mm"}, paramMap);
				
				if(vu1.isValid()) { 	
					String yyyy = paramMap.get("yyyy").toString();
					int mm = Integer.valueOf(paramMap.get("mm").toString());
					String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
					String yymm = yyyy + convertMm;

					paramMap.put("yymm", yymm);
					//2. 유효성 체크
					vu2.isYearMonth("yyyy, mm", yymm);
					if(vu2.isValid()) {
						startDate = yyyy+"-"+convertMm+"-01";
						endDate = yyyy+"-"+convertMm+"-"+getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd"));
						paramMap.put("startDt", startDate);
						paramMap.put("endDt", endDate);
						paramMap.put("limitDtCnt", getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd")));
						
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getLrnHabitMonthly");
						if(data != null) {
							detailList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getLrnHabitDetail");
							if(detailList.size() > 0 && detailList.get(0) != null) {								
								data.put("lrnHabitChart", detailList);
							}
						}
						setResult(msgKey, data);			
					} else {
						setResult(msgKey, vu2.getResult());				
					}
				} else {
					setResult(msgKey, vu1.getResult());
				}
	        } else {	// 주간 & 기간
	        	//1-1.필수값 체크
				vu.checkRequired(new String[] {"startDt","endDt"}, paramMap);
				
				if(vu.isValid()) { 	
					startDate = paramMap.get("startDt").toString();
					endDate = paramMap.get("endDt").toString();
					
					//2. 유효성 체크
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					paramMap.put("limitDtCnt", 7);
					
					if(vu1.isValid() && vu2.isValid()) {
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getLrnHabitPeriod");
						if(data != null) {
							detailList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getLrnHabitDetail");
							if(detailList.size() > 0 && detailList.get(0) != null) {								
								data.put("lrnHabitChart", detailList);
							}
						}
						setResult(msgKey, data);
					} else {
						if(!vu1.isValid()) {
							setResult(msgKey, vu1.getResult());
						} else if(!vu2.isValid()) {
							setResult(msgKey, vu2.getResult());						
						}				
					}
				} else {
					setResult(msgKey, vu.getResult());
				}				    		
			}
			
		} else {
			setResult(msgKey, vu.getResult());
		}
	
    	return result;
    }
    
    /***
     * * HAMS-ORG-LS-002
	 * 학습분석 요약 - AI 학습 추천
	 * @param paramMap
	 * @return Map
	 * @throws Exception
	 */
    @Override
    public Map getAiRecommendLrn(Map<String, Object> paramMap) throws Exception {
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "AIRECOMMENDLRN");

		getStudId(paramMap);
		
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId"}, paramMap);
		if(vu.isValid()) { 		    	
	        //홈런 API 조회
	        ArrayList<Map<String,Object>> visionPrintAiRecommendLrn = new ArrayList<>();	        
	        ArrayList<Map<String,Object>> externalApiList = new ArrayList<>();
	        
	        paramMap.put("apiName", "recommand.");
	        
	        externalApiList =  (ArrayList<Map<String,Object>>) externalAPIservice.callExternalAPI(paramMap).get("data");
	        
	        if(externalApiList != null && externalApiList.size() > 0) {
	        	for(Map<String, Object> item : externalApiList) {
	        		visionPrintAiRecommendLrn.add(createRecommendMap(item.get("imgUrl").toString(), item.get("categoryNm").toString(), item.get("serviceNm").toString()));
	        	}
	        }	
	        setResult(dataKey, visionPrintAiRecommendLrn);			
		} else {
			setResult(msgKey, vu.getResult());
		}

        return result;
    }

    private LinkedHashMap<String, Object> createRecommendMap(String thumUrl, String ctgr1, String ctgr2) {
        LinkedHashMap<String,Object> resultMap = new LinkedHashMap<>();

        resultMap.put("thumUrl",thumUrl);
        resultMap.put("ctgr1",ctgr1);
        resultMap.put("ctgr2",ctgr2);

        return  resultMap;
    }

    /**
	 * HAMS-ORG-LD-001 (학습분석 상세 - 진단검사/평가현황)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
    @Override
    public Map getDiagnosticEvalStt(Map<String, Object> paramMap) throws Exception {
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "DIGIAGNOSTICEVALSTT");

		getStudId(paramMap);

		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId"}, paramMap);
		if(vu.isValid()) { 	   	
			//홈런 API 조회
	        paramMap.put("apiName", "inspecion-present");	       	
	        setResult(dataKey,externalAPIservice.callExternalAPI(paramMap).get("data"));			
		} else {
			setResult(msgKey, vu.getResult());
		}

        return result;
    }	
    
    /**
	 * HAMS-ORG-LD-002 (학습분석 상세 - 출석률현황)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
    @Override
    public Map getAttRtStt(Map<String, Object> paramMap) throws Exception {
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "ATTRTSTT");

		getStudId(paramMap);
		
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		ValidationUtil vu1 = new ValidationUtil();
		ValidationUtil vu2 = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"currCon","studId"}, paramMap);
		
		if(vu.isValid()) { 		
			Map<String,Object> data = new HashMap<>();
            String startDate;
			String endDate;
			
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			Map<String,Object> msg = new HashMap<>();
			String msg_summary = null;
			String msg_detail = null;
			ArrayList<Map<String,Object>> detailList = new ArrayList<>();
			ArrayList<Map<String,Object>> detailChart = new ArrayList<>();
			ArrayList<Map<String,Object>> detail = new ArrayList<>();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {	// 월간
				//1-1.필수값 체크 
				vu1.checkRequired(new String[] {"yyyy","mm"}, paramMap);
				
				if(vu1.isValid()) { 	
					String yyyy = paramMap.get("yyyy").toString();
					int mm = Integer.valueOf(paramMap.get("mm").toString());
					String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
					String yymm = yyyy + convertMm;

					paramMap.put("yymm", yymm);
					//2. 유효성 체크
					vu2.isYearMonth("yyyy, mm", yymm);
					if(vu2.isValid()) {
						startDate = yyyy+"-"+convertMm+"-01";
						endDate = yyyy+"-"+convertMm+"-"+getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd"));
						paramMap.put("startDt", startDate);
						paramMap.put("endDt", endDate);
						paramMap.put("limitDtCnt", getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd")));
						
						// 출석률 attRt
						// 이전출석률 prevAttRt
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getAttRtSttMonthly");
						if (data != null ) { 							
							detailList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getAttRtSttDetail");
							if(detailList.size() > 0 && detailList.get(0) != null) {
								for(Map<String, Object> item : detailList) {
									Map<String, Object> chartMap = new LinkedHashMap<>();
									Map<String, Object> detailMap = new LinkedHashMap<>();
									
									chartMap.put("dt", item.get("dt"));
									chartMap.put("attYn", getItemValueCompare(item.get("attYn"), "1"));
									chartMap.put("lrnStt", item.get("lrnStt"));
									
									detailMap.put("dt", item.get("dt"));
									detailMap.put("lrnFnsh", getItemValueCompare(item.get("lrnFnsh"), "1"));
									detailMap.put("partFnsh", getItemValueCompare(item.get("partFnsh"), "1"));
									detailMap.put("lrnBefore", getItemValueCompare(item.get("lrnBefore"), "1"));
									detailMap.put("att", getItemValueCompare(item.get("attYn"), "1"));
									
									detailChart.add(chartMap);
									detail.add(detailMap);
								}
							}	
							// 일일학습정보 dailyLrnStt
							data.put("dailyLrnStt", detailChart);
							// 출석률 총평 attRtMsg
							msg.put("summary", msg_summary);
							msg.put("detail", msg_detail);
							data.put("attRtMsg", msg);
							// 출석률 상세정보 
							data.put("attRtDetail", detail);
						}
						setResult(msgKey, data);			
					} else {
						setResult(msgKey, vu2.getResult());				
					}
				} else {
					setResult(msgKey, vu1.getResult());
				}
	        } else {	// 주간 & 기간
	        	//1-1.필수값 체크
				vu.checkRequired(new String[] {"startDt","endDt"}, paramMap);
				
				if(vu.isValid()) { 	
					startDate = paramMap.get("startDt").toString();
					endDate = paramMap.get("endDt").toString();
					
					//2. 유효성 체크
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					paramMap.put("limitDtCnt", 7);
					
					if(vu1.isValid() && vu2.isValid()) {
						// 출석률 attRt
						// 이전출석률 prevAttRt
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getAttRtSttPeriod");
						if (data != null ) { 							
							detailList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getAttRtSttDetail");
							if(detailList.size() > 0 && detailList.get(0) != null) {
								for(Map<String, Object> item : detailList) {
									Map<String, Object> chartMap = new LinkedHashMap<>();
									Map<String, Object> detailMap = new LinkedHashMap<>();
									
									chartMap.put("dt", item.get("dt"));
									chartMap.put("attYn", getItemValueCompare(item.get("attYn"), "1"));
									chartMap.put("lrnStt", item.get("lrnStt"));
									
									detailMap.put("dt", item.get("dt"));
									detailMap.put("lrnFnsh", getItemValueCompare(item.get("lrnFnsh"), "1"));
									detailMap.put("partFnsh", getItemValueCompare(item.get("partFnsh"), "1"));
									detailMap.put("lrnBefore", getItemValueCompare(item.get("lrnBefore"), "1"));
									detailMap.put("att", getItemValueCompare(item.get("attYn"), "1"));
																		
									detailChart.add(chartMap);
									detail.add(detailMap);
								}
							}	
							// 일일학습정보 dailyLrnStt
							data.put("dailyLrnStt", detailChart);
							// 출석률 총평 attRtMsg
							msg.put("summary", msg_summary);
							msg.put("detail", msg_detail);
							data.put("attRtMsg", msg);
							// 출석률 상세정보 
							data.put("attRtDetail", detail);
						}
						setResult(msgKey, data);
					} else {
						if(!vu1.isValid()) {
							setResult(msgKey, vu1.getResult());
						} else if(!vu2.isValid()) {
							setResult(msgKey, vu2.getResult());						
						}				
					}
				} else {
					setResult(msgKey, vu.getResult());
				}				    		
			}
			
		} else {
			setResult(msgKey, vu.getResult());
		}
	
    	return result;
    }

    /**
	 * HAMS-ORG-LD-018 (학습분석 상세 - 출석률현황 - 일별출석히스토리)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
    @Override
    public Map getAttHistoryDaily(Map<String, Object> paramMap) throws Exception {
    	v_param = new HashMap<>();
		v_param.put("METHOD", "ATTHISTORYDAILY");
		
		getStudId(paramMap);
		
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId", "date"}, paramMap);
		if(vu.isValid()) { 	   	
			//홈런 API 조회
			paramMap.put("apiName", "connect-log-list");	       	
			setResult(dataKey,externalAPIservice.callExternalAPI(paramMap).get("data"));			
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;    
    }
    
    /**
	 * HAMS-ORG-LD-003 (학습분석 상세 - 학습타임라인)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
    @Override
    public Map getLrnTmList(Map<String, Object> paramMap) throws Exception {
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "LRNTMLIST");

		getStudId(paramMap);
		
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId", "date", "ordNm"}, paramMap);
		
		if(vu.isValid()) { 					
			//DB 조회
			setResult(dataKey, getMapperResultData(v_param, "list", paramMap, ".getLrnTmList"));
		} else {
			setResult(msgKey, vu.getResult());
		}
	
    	return result;
    }

	/**
	 * HAMS-ORG-LD-004 (학습분석 상세 - 학습상세현황)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
    @Override
	public Map getLrnDetail(Map<String, Object> paramMap) throws Exception {
		v_param = new HashMap<>();
		v_param.put("METHOD", "LRNDETAIL");
		
		getStudId(paramMap);
		
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId", "serviceId", "date"}, paramMap);
		if(vu.isValid()) { 	   	
			//홈런 API 조회
			paramMap.put("apiName", "act-element-detail");	       	
			setResult(dataKey,externalAPIservice.callExternalAPI(paramMap).get("data"));			
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
    }
    
    /**
	 * HAMS-ORG-LD-005 (학습분석 상세 - 출석일 수 현황)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
    @Override    
    public Map getAttCntStt(Map<String, Object> paramMap) throws Exception {
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "ATTCNTSTT");

		getStudId(paramMap);
		
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		ValidationUtil vu1 = new ValidationUtil();
		ValidationUtil vu2 = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"currCon","studId"}, paramMap);
		
		if(vu.isValid()) { 		
			Map<String,Object> data = new HashMap<>();
            String startDate;
			String endDate;
			
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			Map<String,Object> msg = new HashMap<>();
			String msg_summary = null;
			String msg_detail = null;
			ArrayList<Map<String,Object>> detailList = new ArrayList<>();
			ArrayList<Map<String,Object>> detail = new ArrayList<>();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {	// 월간
				//1-1.필수값 체크 
				vu1.checkRequired(new String[] {"yyyy","mm"}, paramMap);
				
				if(vu1.isValid()) { 	
					String yyyy = paramMap.get("yyyy").toString();
					int mm = Integer.valueOf(paramMap.get("mm").toString());
					String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
					String yymm = yyyy + convertMm;

					paramMap.put("yymm", yymm);
					//2. 유효성 체크
					vu2.isYearMonth("yyyy, mm", yymm);
					if(vu2.isValid()) {
						
						startDate = yyyy+"-"+convertMm+"-01";
						endDate = yyyy+"-"+convertMm+"-"+getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd"));
						paramMap.put("startDt", startDate);
						paramMap.put("endDt", endDate);
						paramMap.put("limitDtCnt", getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd")));
						
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getAttCntSttMonthly");
						if(data != null) {
							detailList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getAttCntSttDetail");
							if(detailList.size() > 0 && detailList.get(0) != null) {
								for(Map<String, Object> item : detailList) {
									Map<String, Object> detailMap = new LinkedHashMap<>();
									
									detailMap.put("dt", item.get("dt"));
									detailMap.put("planDt", getItemValueCompare(item.get("planDt"), "1"));
									detailMap.put("attDt", getItemValueCompare(item.get("attDt"), "1"));
									detailMap.put("topAttDt", getItemValueCompare(item.get("topAttDt"), "1"));
									detailMap.put("avgAttDt", getItemValueCompare(item.get("avgAttDt"), "1"));
																		
									detail.add(detailMap);
								}
							}	
							// 출석일 총평 attCntMsg
							msg.put("summary", msg_summary);
							msg.put("detail", msg_detail);
							data.put("attCntMsg", msg);
							// 출석일 상세정보 attCntDetail
							data.put("attCntDetail", detail);
						}
						setResult(msgKey, data);			
					} else {
						setResult(msgKey, vu2.getResult());				
					}
				} else {
					setResult(msgKey, vu1.getResult());
				}
	        } else {	// 주간 & 기간
	        	//1-1.필수값 체크
				vu.checkRequired(new String[] {"startDt","endDt"}, paramMap);
				
				if(vu.isValid()) { 	
					startDate = paramMap.get("startDt").toString();
					endDate = paramMap.get("endDt").toString();
					
					//2. 유효성 체크
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					paramMap.put("limitDtCnt", 7);
					
					if(vu1.isValid() && vu2.isValid()) {
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getAttCntSttPeriod");
						if(data != null) {
							detailList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getAttCntSttDetail");
							if(detailList.size() > 0 && detailList.get(0) != null) {
								for(Map<String, Object> item : detailList) {
									Map<String, Object> detailMap = new LinkedHashMap<>();
									
									detailMap.put("dt", item.get("dt"));
									detailMap.put("planDt", getItemValueCompare(item.get("planDt"),"1"));
									detailMap.put("attDt", getItemValueCompare(item.get("attDt"), "1"));
									detailMap.put("topAttDt", getItemValueCompare(item.get("topAttDt"), "1"));
									detailMap.put("avgAttDt", getItemValueCompare(item.get("avgAttDt"), "1"));
									
									detail.add(detailMap);
								}
							}	
							// 출석일 총평 attCntMsg
							msg.put("summary", msg_summary);
							msg.put("detail", msg_detail);
							data.put("attCntMsg", msg);
							// 출석일 상세정보 attCntDetail
							data.put("attCntDetail", detail);
						}
						setResult(msgKey, data);
					} else {
						if(!vu1.isValid()) {
							setResult(msgKey, vu1.getResult());
						}else if(!vu2.isValid()) {
							setResult(msgKey, vu2.getResult());
						}
					}
				} else {
					setResult(msgKey, vu.getResult());
				}				    		
			}
			
		} else {
			setResult(msgKey, vu.getResult());
		}
	
    	return result;
    }

    /**
	 * HAMS-ORG-LD-006
	 * 학습분석 상세 - 로그인 패턴 현황
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
    @Override    
    public Map getLoginPtnStt(Map<String, Object> paramMap) throws Exception {
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "LOGINPTNSTT");

		getStudId(paramMap);
		
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		ValidationUtil vu1 = new ValidationUtil();
		ValidationUtil vu2 = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"currCon","studId"}, paramMap);
		
		if(vu.isValid()) { 		
			Map<String,Object> data = new HashMap<>();
            String startDate;
			String endDate;
			
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			Map<String,Object> msg = new HashMap<>();
			String msg_summary = null;
			String msg_detail = null;
			ArrayList<Map<String,Object>> detailList = new ArrayList<>();
			ArrayList<Map<String,Object>> detailChart = new ArrayList<>();
			ArrayList<Map<String,Object>> detail = new ArrayList<>();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {	// 월간
				//1-1.필수값 체크 
				vu1.checkRequired(new String[] {"yyyy","mm"}, paramMap);
				
				if(vu1.isValid()) { 	
					String yyyy = paramMap.get("yyyy").toString();
					int mm = Integer.valueOf(paramMap.get("mm").toString());
					String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
					String yymm = yyyy + convertMm;

					paramMap.put("yymm", yymm);
					//2. 유효성 체크
					vu2.isYearMonth("yyyy, mm", yymm);
					if(vu2.isValid()) {
						
						startDate = yyyy+"-"+convertMm+"-01";
						endDate = yyyy+"-"+convertMm+"-"+getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd"));
						paramMap.put("startDt", startDate);
						paramMap.put("endDt", endDate);
						paramMap.put("limitDtCnt", getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd")));
						
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getLoginPtnSttMonthly");
						if(data != null) {
							data.put("loginPtn", data.get("loginPtn").toString().equals("1") ? "규칙적" : "불규칙적");
							data.put("prevLoginPtn", data.get("prevLoginPtn").toString().equals("1") ? "규칙적" : "불규칙적");
							detailList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getLoginPtnSttDetail");
							if(detailList.size() > 0 && detailList.get(0) != null) {
								for(Map<String, Object> item : detailList) {
									Map<String, Object> chartMap = new LinkedHashMap<>();
									Map<String, Object> detailMap = new LinkedHashMap<>();
									
									chartMap.put("dt", item.get("dt"));
									chartMap.put("prevDt", item.get("prevDt"));
									chartMap.put("loginTm", getConvertInteger(item.get("loginTm"), null));
									chartMap.put("prevLoginTm", getConvertInteger(item.get("prevLoginTm"), null));									
									detailChart.add(chartMap);
									
									detailMap.put("dt", item.get("dt"));
									detailMap.put("loginTm", getConvertInteger(item.get("loginTm"), null));
									detailMap.put("prevLoginTm", getConvertInteger(item.get("prevLoginTm"), null));								
									detail.add(detailMap);
								}
							}	
							// 로그인 패턴 총평 
							msg.put("summary", msg_summary);
							msg.put("detail", msg_detail);
							data.put("loginPtnMsg", msg);
							// 로그인패턴차트 
							data.put("loginPtnChart", detailChart);
							// 로그인 패턴 상세정보 
							data.put("loginPtnDetail", detail);
						}
						setResult(msgKey, data);			
					} else {
						setResult(msgKey, vu2.getResult());				
					}
				} else {
					setResult(msgKey, vu1.getResult());
				}
	        } else {	// 주간 & 기간
	        	//1-1.필수값 체크
				vu.checkRequired(new String[] {"startDt","endDt"}, paramMap);
				
				if(vu.isValid()) { 	
					startDate = paramMap.get("startDt").toString();
					endDate = paramMap.get("endDt").toString();
					
					//2. 유효성 체크
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					paramMap.put("limitDtCnt", 7);
					
					if(vu1.isValid() && vu2.isValid()) {
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getLoginPtnSttPeriod");
						if(data != null) {
							data.put("loginPtn", data.get("loginPtn").toString().equals("1") ? "규칙적" : "불규칙적");
							data.put("prevLoginPtn", data.get("prevLoginPtn").toString().equals("1") ? "규칙적" : "불규칙적");
							detailList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getLoginPtnSttDetail");
							if(detailList.size() > 0 && detailList.get(0) != null) {
								for(Map<String, Object> item : detailList) {
									Map<String, Object> chartMap = new LinkedHashMap<>();
									Map<String, Object> detailMap = new LinkedHashMap<>();
									
									chartMap.put("dt", item.get("dt"));
									chartMap.put("prevDt", item.get("prevDt"));
									chartMap.put("loginTm", getConvertInteger(item.get("loginTm"), null));
									chartMap.put("prevLoginTm", getConvertInteger(item.get("prevLoginTm"), null));									
									detailChart.add(chartMap);
									
									detailMap.put("dt", item.get("dt"));
									detailMap.put("loginTm", getConvertInteger(item.get("loginTm"), null));
									detailMap.put("prevLoginTm", getConvertInteger(item.get("prevLoginTm"), null));								
									detail.add(detailMap);
								}
							}	
							// 로그인 패턴 총평 
							msg.put("summary", msg_summary);
							msg.put("detail", msg_detail);
							data.put("loginPtnMsg", msg);
							// 로그인패턴차트 
							data.put("loginPtnChart", detailChart);
							// 로그인 패턴 상세정보 
							data.put("loginPtnDetail", detail);
						}
						setResult(msgKey, data);
					} else {
						if(!vu1.isValid()) {
							setResult(msgKey, vu1.getResult());
						}else if(!vu2.isValid()) {
							setResult(msgKey, vu2.getResult());
						}
					}
				} else {
					setResult(msgKey, vu.getResult());
				}				    		
			}
			
		} else {
			setResult(msgKey, vu.getResult());
		}
	
    	return result;
    }


    /**
	 * HAMS-ORG-LD-007
	 * 학습분석 상세 - 수행률 현황
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
    @Override    
    public Map getExRtStt(Map<String, Object> paramMap) throws Exception {
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "EXRTSTT");

		getStudId(paramMap);
		
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		ValidationUtil vu1 = new ValidationUtil();
		ValidationUtil vu2 = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"currCon","studId"}, paramMap);
		
		if(vu.isValid()) { 		
			Map<String,Object> data = new HashMap<>();
            String startDate;
			String endDate;
			
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			Map<String,Object> msg = new HashMap<>();
			String msg_summary = null;
			String msg_detail = null;
			ArrayList<Map<String,Object>> detailList = new ArrayList<>();
			ArrayList<Map<String,Object>> detailChart = new ArrayList<>();
			ArrayList<Map<String,Object>> detail = new ArrayList<>();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {	// 월간
				//1-1.필수값 체크 
				vu1.checkRequired(new String[] {"yyyy","mm"}, paramMap);
				
				if(vu1.isValid()) { 	
					String yyyy = paramMap.get("yyyy").toString();
					int mm = Integer.valueOf(paramMap.get("mm").toString());
					String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
					String yymm = yyyy + convertMm;

					paramMap.put("yymm", yymm);
					//2. 유효성 체크
					vu2.isYearMonth("yyyy, mm", yymm);
					if(vu2.isValid()) {
						
						startDate = yyyy+"-"+convertMm+"-01";
						endDate = yyyy+"-"+convertMm+"-"+getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd"));
						paramMap.put("startDt", startDate);
						paramMap.put("endDt", endDate);
						paramMap.put("limitDtCnt", getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd")));
						
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getExRtSttMonthly");
						if(data != null) {
							detailList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getExRtSttDetail");
							if(detailList.size() > 0 && detailList.get(0) != null) {
								for(Map<String, Object> item : detailList) {
									Map<String, Object> chartMap = new LinkedHashMap<>();
									Map<String, Object> detailMap = new LinkedHashMap<>();
									
									chartMap.put("dt", item.get("dt"));
									chartMap.put("prevDt", item.get("prevDt"));
									chartMap.put("exRt", item.get("exRt"));
									chartMap.put("prevExRt", item.get("prevExRt"));									
									detailChart.add(chartMap);
									
									detailMap.put("dt", item.get("dt"));
									detailMap.put("exRt", item.get("exRt"));
									detailMap.put("prevExRt", item.get("prevExRt"));	
									detailMap.put("topExRt", item.get("topExRt"));
									detailMap.put("avgExRt", item.get("avgExRt"));								
									detail.add(detailMap);
								}
							}	
							// 수행률 총평 
							msg.put("summary", msg_summary);
							msg.put("detail", msg_detail);
							data.put("exRtMsg", msg);
							// 수행률차트 
							data.put("exRtChart", detailChart);
							// 수행률 상세정보 
							data.put("exRtDetail", detail);
						}
						setResult(msgKey, data);			
					} else {
						setResult(msgKey, vu2.getResult());				
					}
				} else {
					setResult(msgKey, vu1.getResult());
				}
	        } else {	// 주간 & 기간
	        	//1-1.필수값 체크
				vu.checkRequired(new String[] {"startDt","endDt"}, paramMap);
				
				if(vu.isValid()) { 	
					startDate = paramMap.get("startDt").toString();
					endDate = paramMap.get("endDt").toString();
					
					//2. 유효성 체크
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					paramMap.put("limitDtCnt", 7);
					
					if(vu1.isValid() && vu2.isValid()) {
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getExRtSttPeriod");
						if(data != null) {
							detailList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getExRtSttDetail");
							if(detailList.size() > 0 && detailList.get(0) != null) {
								for(Map<String, Object> item : detailList) {
									Map<String, Object> chartMap = new LinkedHashMap<>();
									Map<String, Object> detailMap = new LinkedHashMap<>();
									
									chartMap.put("dt", item.get("dt"));
									chartMap.put("prevDt", item.get("prevDt"));
									chartMap.put("exRt", item.get("exRt"));
									chartMap.put("prevExRt", item.get("prevExRt"));									
									detailChart.add(chartMap);
									
									detailMap.put("dt", item.get("dt"));
									detailMap.put("exRt", item.get("exRt"));
									detailMap.put("prevExRt", item.get("prevExRt"));	
									detailMap.put("topExRt", item.get("topExRt"));
									detailMap.put("avgExRt", item.get("avgExRt"));								
									detail.add(detailMap);
								}
							}	
							// 수행률 총평 
							msg.put("summary", msg_summary);
							msg.put("detail", msg_detail);
							data.put("exRtMsg", msg);
							// 수행률차트 
							data.put("exRtChart", detailChart);
							// 수행률 상세정보 
							data.put("exRtDetail", detail);
						}
						setResult(msgKey, data);
					} else {
						if(!vu1.isValid()) {
							setResult(msgKey, vu1.getResult());
						}else if(!vu2.isValid()) {
							setResult(msgKey, vu2.getResult());
						}
					}
				} else {
					setResult(msgKey, vu.getResult());
				}				    		
			}
			
		} else {
			setResult(msgKey, vu.getResult());
		}
	
    	return result;
    }

    /**
	 * HAMS-ORG-LD-008 
	 * 학습분석 상세 - 완료한 학습 현황
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
    @Override    
    public Map getFnshLrnExStt(Map<String, Object> paramMap) throws Exception {
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "FNSHLRNEXSTT");

		getStudId(paramMap);
		
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		ValidationUtil vu1 = new ValidationUtil();
		ValidationUtil vu2 = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"currCon","studId"}, paramMap);
		
		if(vu.isValid()) { 		
			Map<String,Object> data = new HashMap<>();
            String startDate;
			String endDate;
			
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			Map<String,Object> msg = new HashMap<>();
			String msg_summary = null;
			String msg_detail = null;
			ArrayList<Map<String,Object>> detailList = new ArrayList<>();
			ArrayList<Map<String,Object>> detailChart = new ArrayList<>();
			ArrayList<Map<String,Object>> detail = new ArrayList<>();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {	// 월간
				//1-1.필수값 체크 
				vu1.checkRequired(new String[] {"yyyy","mm"}, paramMap);
				
				if(vu1.isValid()) { 	
					String yyyy = paramMap.get("yyyy").toString();
					int mm = Integer.valueOf(paramMap.get("mm").toString());
					String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
					String yymm = yyyy + convertMm;

					paramMap.put("yymm", yymm);
					//2. 유효성 체크
					vu2.isYearMonth("yyyy, mm", yymm);
					if(vu2.isValid()) {
						
						startDate = yyyy+"-"+convertMm+"-01";
						endDate = yyyy+"-"+convertMm+"-"+getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd"));
						paramMap.put("startDt", startDate);
						paramMap.put("endDt", endDate);
						paramMap.put("limitDtCnt", getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd")));
						
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getFnshLrnExSttMonthly");
						if(data != null) {
							detailList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getFnshLrnExSttDetail");
							if(detailList.size() > 0 && detailList.get(0) != null) {
								for(Map<String, Object> item : detailList) {
									Map<String, Object> chartMap = new LinkedHashMap<>();
									Map<String, Object> detailMap = new LinkedHashMap<>();
									
									chartMap.put("dt", item.get("dt"));
									chartMap.put("prevDt", item.get("prevDt"));
									chartMap.put("fnshLrnCnt", item.get("fnshLrnCnt"));
									chartMap.put("ncLrnCnt", item.get("ncLrnCnt"));
									chartMap.put("prevFnshLrnCnt", item.get("prevFnshLrnCnt"));
									chartMap.put("prevNcLrnCnt", item.get("prevNcLrnCnt"));									
									detailChart.add(chartMap);

									detailMap.put("dt", item.get("dt"));
									detailMap.put("fnshLrnCnt", item.get("fnshLrnCnt"));
									detailMap.put("prevFnshLrnCnt", item.get("prevFnshLrnCnt"));	
									detailMap.put("topFnshLrnCnt", item.get("topFnshLrnCnt"));
									detailMap.put("avgFnshLrnCnt", item.get("avgFnshLrnCnt"));								
									detail.add(detailMap);
								}
							}	
							// 수행률 총평 
							msg.put("summary", msg_summary);
							msg.put("detail", msg_detail);
							data.put("fnshLrnMsg", msg);
							// 완료한 학습 차트 
							data.put("fnshLrnChart", detailChart);
							// 완료한 학습 상세정보 
							data.put("fnshLrnDetail", detail);
						}
						setResult(msgKey, data);			
					} else {
						setResult(msgKey, vu2.getResult());				
					}
				} else {
					setResult(msgKey, vu1.getResult());
				}
	        } else {	// 주간 & 기간
	        	//1-1.필수값 체크
				vu.checkRequired(new String[] {"startDt","endDt"}, paramMap);
				
				if(vu.isValid()) { 	
					startDate = paramMap.get("startDt").toString();
					endDate = paramMap.get("endDt").toString();
					
					//2. 유효성 체크
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					paramMap.put("limitDtCnt", 7);
					
					if(vu1.isValid() && vu2.isValid()) {
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getFnshLrnExSttPeriod");
						if(data != null) {

							detailList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getFnshLrnExSttDetail");
							if(detailList.size() > 0 && detailList.get(0) != null) {
								for(Map<String, Object> item : detailList) {
									Map<String, Object> chartMap = new LinkedHashMap<>();
									Map<String, Object> detailMap = new LinkedHashMap<>();
									
									chartMap.put("dt", item.get("dt"));
									chartMap.put("prevDt", item.get("prevDt"));
									chartMap.put("fnshLrnCnt", item.get("fnshLrnCnt"));
									chartMap.put("ncLrnCnt", item.get("ncLrnCnt"));
									chartMap.put("prevFnshLrnCnt", item.get("prevFnshLrnCnt"));
									chartMap.put("prevNcLrnCnt", item.get("prevNcLrnCnt"));									
									detailChart.add(chartMap);

									detailMap.put("dt", item.get("dt"));
									detailMap.put("fnshLrnCnt", item.get("fnshLrnCnt"));
									detailMap.put("prevFnshLrnCnt", item.get("prevFnshLrnCnt"));	
									detailMap.put("topFnshLrnCnt", item.get("topFnshLrnCnt"));
									detailMap.put("avgFnshLrnCnt", item.get("avgFnshLrnCnt"));								
									detail.add(detailMap);
								}
							}	
							// 수행률 총평 
							msg.put("summary", msg_summary);
							msg.put("detail", msg_detail);
							data.put("fnshLrnMsg", msg);
							// 완료한 학습 차트 
							data.put("fnshLrnChart", detailChart);
							// 완료한 학습 상세정보 
							data.put("fnshLrnDetail", detail);
						}
						setResult(msgKey, data);
					} else {
						if(!vu1.isValid()) {
							setResult(msgKey, vu1.getResult());
						}else if(!vu2.isValid()) {
							setResult(msgKey, vu2.getResult());
						}
					}
				} else {
					setResult(msgKey, vu.getResult());
				}				    		
			}
			
		} else {
			setResult(msgKey, vu.getResult());
		}
	
    	return result;
    }

    /**
	 * HAMS-ORG-LD-009 
	 * 학습분석 상세 - 학습 수행 현황
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
    @Override    
    public Map getLrnExSttCompareSub(Map<String, Object> paramMap) throws Exception {
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "LRNEXSTTCOMPARESUB");

		getStudId(paramMap);
		
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		ValidationUtil vu1 = new ValidationUtil();
		ValidationUtil vu2 = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"currCon","studId"}, paramMap);
		
		if(vu.isValid()) { 		
			Map<String,Object> data = new HashMap<>();
            String startDate;
			String endDate;
			
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			Map<String,Object> msg = new HashMap<>();
			String msg_summary = null;
			String msg_detail = null;
			ArrayList<Map<String,Object>> detailList = new ArrayList<>();
			ArrayList<Map<String,Object>> detailChart = new ArrayList<>();
			ArrayList<Map<String,Object>> detail = new ArrayList<>();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {	// 월간
				//1-1.필수값 체크 
				vu1.checkRequired(new String[] {"yyyy","mm"}, paramMap);
				
				if(vu1.isValid()) { 	
					String yyyy = paramMap.get("yyyy").toString();
					int mm = Integer.valueOf(paramMap.get("mm").toString());
					String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
					String yymm = yyyy + convertMm;

					paramMap.put("yymm", yymm);
					//2. 유효성 체크
					vu2.isYearMonth("yyyy, mm", yymm);
					if(vu2.isValid()) {
						
						startDate = yyyy+"-"+convertMm+"-01";
						endDate = yyyy+"-"+convertMm+"-"+getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd"));
						paramMap.put("startDt", startDate);
						paramMap.put("endDt", endDate);
						paramMap.put("limitDtCnt", getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd")));
						
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getLrnExSttCompareSubMonthly");
						if(data != null) {
							detailList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getLrnExSttCompareSubDetail");
							if(detailList.size() > 0 && detailList.get(0) != null) {
								for(Map<String, Object> item : detailList) {
									Map<String, Object> chartMap = new LinkedHashMap<>();
									Map<String, Object> detailMap = new LinkedHashMap<>();
									
									chartMap.put("dt", item.get("dt"));
									chartMap.put("prevDt", item.get("prevDt"));
									chartMap.put("bLrnExCnt", item.get("bLrnExCnt"));
									chartMap.put("planLrnExCnt", item.get("planLrnExCnt"));
									chartMap.put("dLrnExCnt", item.get("dLrnExCnt"));
									chartMap.put("prevBLrnExCnt", item.get("prevBLrnExCnt"));
									chartMap.put("prevPlanLrnExCnt", item.get("prevPlanLrnExCnt"));
									chartMap.put("prevDLrnExCnt", item.get("prevDLrnExCnt"));									
									detailChart.add(chartMap);

									detailMap.put("dt", item.get("dt"));
									detailMap.put("bLrnExCnt", item.get("bLrnExCnt"));
									detailMap.put("planLrnExCnt", item.get("planLrnExCnt"));	
									detailMap.put("dLrnExCnt", item.get("dLrnExCnt"));
									detailMap.put("prevBLrnExCnt", item.get("prevBLrnExCnt"));	
									detailMap.put("prevPlanLrnExCnt", item.get("prevPlanLrnExCnt"));	
									detailMap.put("prevDLrnExCnt", item.get("prevDLrnExCnt"));								
									detail.add(detailMap);
								}
							}	
							// 학습 수행 총평
							msg.put("summary", msg_summary);
							msg.put("detail", msg_detail);
							data.put("lrnExMsg", msg);
							// 학습 수행 차트 
							data.put("lrnExChart", detailChart);
							// 학습 수행 상세정보
							data.put("lrnExDetail", detail);
						}
						setResult(msgKey, data);			
					} else {
						setResult(msgKey, vu2.getResult());				
					}
				} else {
					setResult(msgKey, vu1.getResult());
				}
	        } else {	// 주간 & 기간
	        	//1-1.필수값 체크
				vu.checkRequired(new String[] {"startDt","endDt"}, paramMap);
				
				if(vu.isValid()) { 	
					startDate = paramMap.get("startDt").toString();
					endDate = paramMap.get("endDt").toString();
					
					//2. 유효성 체크
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					paramMap.put("limitDtCnt", 7);
					
					if(vu1.isValid() && vu2.isValid()) {
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getLrnExSttCompareSubPeriod");
						if(data != null) {

							detailList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getLrnExSttCompareSubDetail");
							if(detailList.size() > 0 && detailList.get(0) != null) {
								for(Map<String, Object> item : detailList) {
									Map<String, Object> chartMap = new LinkedHashMap<>();
									Map<String, Object> detailMap = new LinkedHashMap<>();
									
									chartMap.put("dt", item.get("dt"));
									chartMap.put("prevDt", item.get("prevDt"));
									chartMap.put("bLrnExCnt", item.get("bLrnExCnt"));
									chartMap.put("planLrnExCnt", item.get("planLrnExCnt"));
									chartMap.put("dLrnExCnt", item.get("dLrnExCnt"));
									chartMap.put("prevBLrnExCnt", item.get("prevBLrnExCnt"));
									chartMap.put("prevPlanLrnExCnt", item.get("prevPlanLrnExCnt"));
									chartMap.put("prevDLrnExCnt", item.get("prevDLrnExCnt"));									
									detailChart.add(chartMap);

									detailMap.put("dt", item.get("dt"));
									detailMap.put("bLrnExCnt", item.get("bLrnExCnt"));
									detailMap.put("planLrnExCnt", item.get("planLrnExCnt"));	
									detailMap.put("dLrnExCnt", item.get("dLrnExCnt"));
									detailMap.put("prevBLrnExCnt", item.get("prevBLrnExCnt"));	
									detailMap.put("prevPlanLrnExCnt", item.get("prevPlanLrnExCnt"));	
									detailMap.put("prevDLrnExCnt", item.get("prevDLrnExCnt"));									
									detail.add(detailMap);
								}
							}	
							// 학습 수행 총평
							msg.put("summary", msg_summary);
							msg.put("detail", msg_detail);
							data.put("lrnExMsg", msg);
							// 학습 수행 차트 
							data.put("lrnExChart", detailChart);
							// 학습 수행 상세정보
							data.put("lrnExDetail", detail);
						}
						setResult(msgKey, data);
					} else {
						if(!vu1.isValid()) {
							setResult(msgKey, vu1.getResult());
						}else if(!vu2.isValid()) {
							setResult(msgKey, vu2.getResult());
						}
					}
				} else {
					setResult(msgKey, vu.getResult());
				}				    		
			}
			
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
    }

    /**
	 * HAMS-ORG-LD-010
	 * 학습분석 상세 - 스스로 학습 현황
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
    @Override    
    public Map getALrnExStt(Map<String, Object> paramMap) throws Exception {
    	
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "ALRNEXSTT");

		getStudId(paramMap);
		
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		ValidationUtil vuW = new ValidationUtil();
		ValidationUtil vuM = new ValidationUtil();
		ValidationUtil vu1 = new ValidationUtil();
		ValidationUtil vu2 = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"currCon","studId"}, paramMap);
		
		if(vu.isValid()) {
			Map<String, Object> data = new LinkedHashMap<>();
			Map<String, Object> aLrnData = new HashMap<>();
			Map<String, Object> msgMap = new LinkedHashMap<>();
			ArrayList<Map<String,Object>> aLrnList = new ArrayList<>();
			ArrayList<Map<String,Object>> chartList = new ArrayList<>();
			ArrayList<Map<String,Object>> detailList = new ArrayList<>();
			
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {
				
				vuM.checkRequired(new String[] {"yyyy","mm"}, paramMap);
				if(vuM.isValid()) {
					String yyyy = paramMap.get("yyyy").toString();
					int mm = Integer.valueOf(paramMap.get("mm").toString());
					String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
					
					paramMap.put("convertMm", convertMm);
					
					vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
					
					if(vu1.isValid()) {
						String yyMm = yyyy + convertMm;
						
						String startDt = yyyy + "-" + convertMm + "-01";
						int lastDay = getCalendarLastDay(startDt, new SimpleDateFormat("yyyy-MM-dd"));
						String endDt = yyyy + "-" +convertMm + "-" + String.valueOf(lastDay);
						
						paramMap.put("yyMm", yyMm);
						paramMap.put("startDt", startDt);
						paramMap.put("endDt", endDt);
						paramMap.put("lastDay", lastDay);
						
						aLrnData = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getALrnExStt");
						aLrnList = (ArrayList<Map<String, Object>>) getMapperResultData(v_param, "list", paramMap, ".getALrnExSttList");
						
						if(aLrnData.get("aLrnExCnt") != null || aLrnData.get("prevALrnExCnt") != null) {
							data.put("aLrnExCnt", aLrnData.get("aLrnExCnt"));
							data.put("prevALrnExCnt", aLrnData.get("prevALrnExCnt"));
							data.put("topALrnExCnt", aLrnData.get("topALrnExCnt"));
							data.put("avgALrnExCnt", aLrnData.get("avgALrnExCnt"));
							
							msgMap.put("summary", aLrnData.get("summary"));
							msgMap.put("detail", aLrnData.get("detail"));
							
							if(aLrnList.size() > 0 && aLrnList.get(0) != null) {
								for(Map<String, Object> item : aLrnList) {
									Map<String, Object> chartMap = new LinkedHashMap<>();
									Map<String, Object> detailMap = new LinkedHashMap<>();
									
									chartMap.put("dt", item.get("dt"));
									chartMap.put("prevDt", item.get("prevDt"));
									chartMap.put("aLrnExCnt", item.get("aLrnExCnt"));
									chartMap.put("prevALrnExCnt", item.get("prevALrnExCnt"));
									
									detailMap.put("dt", item.get("dt"));
									detailMap.put("aLrnExCnt", item.get("aLrnExCnt"));
									detailMap.put("prevALrnExCnt", item.get("prevALrnExCnt"));
									detailMap.put("topALrnExCnt", item.get("topALrnExCnt"));
									detailMap.put("avgALrnExCnt", item.get("avgALrnExCnt"));
									
									chartList.add(chartMap);
									detailList.add(detailMap);
								}
							}
							
							data.put("aLrnExChart", chartList);
							data.put("aLrnExMsg", msgMap);
							data.put("aLrnExDetail", detailList);
						}
						
					}else {
						setResult(msgKey, vu1.getResult());
					}
					
				}else {
					setResult(msgKey, vuM.getResult());
				}
			}else {
				vuW.checkRequired(new String[] {"startDt","endDt"}, paramMap);
				
				if(vuW.isValid()) {
					String startDate = paramMap.get("startDt").toString();
					String endDate = paramMap.get("endDt").toString();
					
					//2. 유효성 체크
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					
					if(vu1.isValid() && vu2.isValid()) {
						paramMap.put("lastDay", 7);
						
						aLrnData = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getALrnExStt");
						aLrnList = (ArrayList<Map<String, Object>>) getMapperResultData(v_param, "list", paramMap, ".getALrnExSttList");
						
						if(aLrnData.get("aLrnExCnt") != null || aLrnData.get("prevALrnExCnt") != null) {
							data.put("aLrnExCnt", aLrnData.get("aLrnExCnt"));
							data.put("prevALrnExCnt", aLrnData.get("prevALrnExCnt"));
							data.put("topALrnExCnt", aLrnData.get("topALrnExCnt"));
							data.put("avgALrnExCnt", aLrnData.get("avgALrnExCnt"));
							
							msgMap.put("summary", aLrnData.get("summary"));
							msgMap.put("detail", aLrnData.get("detail"));
							
							if(aLrnList.size() > 0 && aLrnList.get(0) != null) {
								for(Map<String, Object> item : aLrnList) {
									Map<String, Object> chartMap = new LinkedHashMap<>();
									Map<String, Object> detailMap = new LinkedHashMap<>();
									
									chartMap.put("dt", item.get("dt"));
									chartMap.put("prevDt", item.get("prevDt"));
									chartMap.put("aLrnExCnt", item.get("aLrnExCnt"));
									chartMap.put("prevALrnExCnt", item.get("prevALrnExCnt"));
									
									detailMap.put("dt", item.get("dt"));
									detailMap.put("aLrnExCnt", item.get("aLrnExCnt"));
									detailMap.put("prevALrnExCnt", item.get("prevALrnExCnt"));
									detailMap.put("topALrnExCnt", item.get("topALrnExCnt"));
									detailMap.put("avgALrnExCnt", item.get("avgALrnExCnt"));
									
									chartList.add(chartMap);
									detailList.add(detailMap);
								}
							}
							
							data.put("aLrnExChart", chartList);
							data.put("aLrnExMsg", msgMap);
							data.put("aLrnExDetail", detailList);
						}
					}else {
						if(!vu1.isValid()) {
							setResult(msgKey, vu1.getResult());
						}else if(!vu2.isValid()) {
							setResult(msgKey, vu2.getResult());
						}
					}
				}else {
					setResult(msgKey, vuW.getResult());
				}
			}
			setResult(dataKey, data);
		} else {
			setResult(msgKey, vu.getResult());
		}
    	
    	return result;
    }

    /**
	 * HAMS-ORG-LD-011
	 * 학습분석 상세 - 정답률 현황
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
    @Override    
    public Map getCrtRtStt(Map<String, Object> paramMap) throws Exception {
    	
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "CRTRTSTT");

		getStudId(paramMap);
		
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		ValidationUtil vuW = new ValidationUtil();
		ValidationUtil vuM = new ValidationUtil();
		ValidationUtil vu1 = new ValidationUtil();
		ValidationUtil vu2 = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"currCon","studId"}, paramMap);
		
		if(vu.isValid()) {
			Map<String, Object> data = new LinkedHashMap<>();
			Map<String, Object> crtData = new HashMap<>();
			Map<String, Object> msgMap = new LinkedHashMap<>();
			ArrayList<Map<String,Object>> crtList = new ArrayList<>();
			ArrayList<Map<String,Object>> chartList = new ArrayList<>();
			ArrayList<Map<String,Object>> detailList = new ArrayList<>();
			
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {
				
				vuM.checkRequired(new String[] {"yyyy","mm"}, paramMap);
				if(vuM.isValid()) {
					String yyyy = paramMap.get("yyyy").toString();
					int mm = Integer.valueOf(paramMap.get("mm").toString());
					String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
					
					paramMap.put("convertMm", convertMm);
					
					vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
					
					if(vu1.isValid()) {
						String yyMm = yyyy + convertMm;
						
						String startDt = yyyy + "-" + convertMm + "-01";
						int lastDay = getCalendarLastDay(startDt, new SimpleDateFormat("yyyy-MM-dd"));
						String endDt = yyyy + "-" +convertMm + "-" + String.valueOf(lastDay);
						
						paramMap.put("yyMm", yyMm);
						paramMap.put("startDt", startDt);
						paramMap.put("endDt", endDt);
						paramMap.put("lastDay", lastDay);
						
						crtData = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getCrtRtStt");
						crtList = (ArrayList<Map<String, Object>>) getMapperResultData(v_param, "list", paramMap, ".getCrtRtSttList");
						
						if(crtData.get("crtRt") != null || crtData.get("prevCrtRt") != null) {
							data.put("crtRt", crtData.get("crtRt"));
							data.put("prevCrtRt", crtData.get("prevCrtRt"));
							data.put("topCrtRt", crtData.get("topCrtRt"));
							data.put("avgCrtRt", crtData.get("avgCrtRt"));
							
							msgMap.put("summary", crtData.get("summary"));
							msgMap.put("detail", crtData.get("detail"));
							
							if(crtList.size() > 0 && crtList.get(0) != null) {
								for(Map<String, Object> item : crtList) {
									Map<String, Object> chartMap = new LinkedHashMap<>();
									Map<String, Object> detailMap = new LinkedHashMap<>();
									
									chartMap.put("dt", item.get("dt"));
									chartMap.put("prevDt", item.get("prevDt"));
									chartMap.put("crtRt", item.get("crtRt"));
									chartMap.put("prevCrtRt", item.get("prevCrtRt"));
									
									detailMap.put("dt", item.get("dt"));
									detailMap.put("crtRt", item.get("crtRt"));
									detailMap.put("prevCrtRt", item.get("prevCrtRt"));
									detailMap.put("topCrtRt", item.get("topCrtRt"));
									detailMap.put("avgCrtRt", item.get("avgCrtRt"));
									
									chartList.add(chartMap);
									detailList.add(detailMap);
								}
							}
							
							data.put("crtRtChart", chartList);
							data.put("crtRtMsg", msgMap);
							data.put("crtRtDetail", detailList);
						}
					}else {
						setResult(msgKey, vu1.getResult());
					}
					
				}else {
					setResult(msgKey, vuM.getResult());
				}
			}else {
				vuW.checkRequired(new String[] {"startDt","endDt"}, paramMap);
				
				if(vuW.isValid()) {
					String startDate = paramMap.get("startDt").toString();
					String endDate = paramMap.get("endDt").toString();
					
					//2. 유효성 체크
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					
					if(vu1.isValid() && vu2.isValid()) {
						paramMap.put("lastDay", 7);
						
						crtData = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getCrtRtStt");
						crtList = (ArrayList<Map<String, Object>>) getMapperResultData(v_param, "list", paramMap, ".getCrtRtSttList");
						
						if(crtData.get("crtRt") != null || crtData.get("prevCrtRt") != null) {
							data.put("crtRt", crtData.get("crtRt"));
							data.put("prevCrtRt", crtData.get("prevCrtRt"));
							data.put("topCrtRt", crtData.get("topCrtRt"));
							data.put("avgCrtRt", crtData.get("avgCrtRt"));
							
							msgMap.put("summary", crtData.get("summary"));
							msgMap.put("detail", crtData.get("detail"));
							
							if(crtList.size() > 0 && crtList.get(0) != null) {
								for(Map<String, Object> item : crtList) {
									Map<String, Object> chartMap = new LinkedHashMap<>();
									Map<String, Object> detailMap = new LinkedHashMap<>();
									
									chartMap.put("dt", item.get("dt"));
									chartMap.put("prevDt", item.get("prevDt"));
									chartMap.put("crtRt", item.get("crtRt"));
									chartMap.put("prevCrtRt", item.get("prevCrtRt"));
									
									detailMap.put("dt", item.get("dt"));
									detailMap.put("crtRt", item.get("crtRt"));
									detailMap.put("prevCrtRt", item.get("prevCrtRt"));
									detailMap.put("topCrtRt", item.get("topCrtRt"));
									detailMap.put("avgCrtRt", item.get("avgCrtRt"));
									
									chartList.add(chartMap);
									detailList.add(detailMap);
								}
							}
							
							data.put("crtRtChart", chartList);
							data.put("crtRtMsg", msgMap);
							data.put("crtRtDetail", detailList);
						}
					}else {
						if(!vu1.isValid()) {
							setResult(msgKey, vu1.getResult());
						}else if(!vu2.isValid()) {
							setResult(msgKey, vu2.getResult());
						}
					}
				}else {
					setResult(msgKey, vuW.getResult());
				}
			}
			setResult(dataKey, data);
		} else {
			setResult(msgKey, vu.getResult());
		}
    	
    	return result;
    }

    /**
	 * HAMS-ORG-LD-012
	 * 학습분석 상세 - 미완료 오답노트 현황
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
    @Override    
    public Map getIncrtNoteNcStt(Map<String, Object> paramMap) throws Exception {
    	
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "INCRTNOTENCSTT");

		getStudId(paramMap);
		
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		ValidationUtil vuW = new ValidationUtil();
		ValidationUtil vuM = new ValidationUtil();
		ValidationUtil vu1 = new ValidationUtil();
		ValidationUtil vu2 = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"currCon","studId"}, paramMap);
		
		if(vu.isValid()) {
			Map<String, Object> data = new LinkedHashMap<>();
			Map<String, Object> incrtData = new HashMap<>();
			Map<String, Object> msgMap = new LinkedHashMap<>();
			ArrayList<Map<String,Object>> incrtList = new ArrayList<>();
			ArrayList<Map<String,Object>> chartList = new ArrayList<>();
			ArrayList<Map<String,Object>> detailList = new ArrayList<>();
			
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {
				
				vuM.checkRequired(new String[] {"yyyy","mm"}, paramMap);
				if(vuM.isValid()) {
					String yyyy = paramMap.get("yyyy").toString();
					int mm = Integer.valueOf(paramMap.get("mm").toString());
					String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
					
					paramMap.put("convertMm", convertMm);
					
					vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
					
					if(vu1.isValid()) {
						String yyMm = yyyy + convertMm;
						
						String startDt = yyyy + "-" + convertMm + "-01";
						int lastDay = getCalendarLastDay(startDt, new SimpleDateFormat("yyyy-MM-dd"));
						String endDt = yyyy + "-" +convertMm + "-" + String.valueOf(lastDay);
						
						paramMap.put("yyMm", yyMm);
						paramMap.put("startDt", startDt);
						paramMap.put("endDt", endDt);
						paramMap.put("lastDay", lastDay);
						
						incrtData = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getIncrtNoteNcStt");
						incrtList = (ArrayList<Map<String, Object>>) getMapperResultData(v_param, "list", paramMap, ".getIncrtNoteNcSttList");

						if(incrtData.get("incrtNoteNcCnt") != null || incrtData.get("prevIncrtNoteNcCnt") != null) {
							data.put("incrtNoteNcCnt", incrtData.get("incrtNoteNcCnt"));
							data.put("prevIncrtNoteNcCnt", incrtData.get("prevIncrtNoteNcCnt"));
							data.put("topIncrtNoteNcCnt", incrtData.get("topIncrtNoteNcCnt"));
							data.put("avgIncrtNoteNcCnt", incrtData.get("avgIncrtNoteNcCnt"));
							
							msgMap.put("summary", incrtData.get("summary"));
							msgMap.put("detail", incrtData.get("detail"));
							
							if(incrtList.size() > 0 && incrtList.get(0) != null) {
								for(Map<String, Object> item : incrtList) {
									Map<String, Object> chartMap = new LinkedHashMap<>();
									Map<String, Object> detailMap = new LinkedHashMap<>();
									
									chartMap.put("dt", item.get("dt"));
									chartMap.put("prevDt", item.get("prevDt"));
									chartMap.put("incrtNoteNcCnt", item.get("incrtNoteNcCnt"));
									chartMap.put("crtNoteNcCnt", item.get("crtNoteNcCnt"));
									chartMap.put("prevIncrtNoteNcCnt", item.get("prevIncrtNoteNcCnt"));
									chartMap.put("prevCrtNoteNcCnt", item.get("prevCrtNoteNcCnt"));
									
									detailMap.put("dt", item.get("dt"));
									detailMap.put("incrtNoteNcCnt", item.get("incrtNoteNcCnt"));
									detailMap.put("prevIncrtNoteNcCnt", item.get("prevIncrtNoteNcCnt"));
									detailMap.put("topIncrtNoteNcCnt", item.get("topIncrtNoteNcCnt"));
									detailMap.put("avgIncrtNoteNcCnt", item.get("avgIncrtNoteNcCnt"));
									
									chartList.add(chartMap);
									detailList.add(detailMap);
								}
							}
							
							data.put("incrtNoteNcChart", chartList);
							data.put("incrtNoteNcMsg", msgMap);
							data.put("incrtNoteNcDetail", detailList);
						}
						
					}else {
						setResult(msgKey, vu1.getResult());
					}
					
				}else {
					setResult(msgKey, vuM.getResult());
				}
			}else {
				vuW.checkRequired(new String[] {"startDt","endDt"}, paramMap);
				
				if(vuW.isValid()) {
					String startDate = paramMap.get("startDt").toString();
					String endDate = paramMap.get("endDt").toString();
					
					//2. 유효성 체크
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					
					if(vu1.isValid() && vu2.isValid()) {
						paramMap.put("lastDay", 7);
						
						incrtData = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getIncrtNoteNcStt");
						incrtList = (ArrayList<Map<String, Object>>) getMapperResultData(v_param, "list", paramMap, ".getIncrtNoteNcSttList");
						
						if(incrtData.get("incrtNoteNcCnt") != null || incrtData.get("prevIncrtNoteNcCnt") != null) {
							data.put("incrtNoteNcCnt", incrtData.get("incrtNoteNcCnt"));
							data.put("prevIncrtNoteNcCnt", incrtData.get("prevIncrtNoteNcCnt"));
							data.put("topIncrtNoteNcCnt", incrtData.get("topIncrtNoteNcCnt"));
							data.put("avgIncrtNoteNcCnt", incrtData.get("avgIncrtNoteNcCnt"));
							
							msgMap.put("summary", incrtData.get("summary"));
							msgMap.put("detail", incrtData.get("detail"));
							
							if(incrtList.size() > 0 && incrtList.get(0) != null) {
								for(Map<String, Object> item : incrtList) {
									Map<String, Object> chartMap = new LinkedHashMap<>();
									Map<String, Object> detailMap = new LinkedHashMap<>();
									
									chartMap.put("dt", item.get("dt"));
									chartMap.put("prevDt", item.get("prevDt"));
									chartMap.put("incrtNoteNcCnt", item.get("incrtNoteNcCnt"));
									chartMap.put("crtNoteNcCnt", item.get("crtNoteNcCnt"));
									chartMap.put("prevIncrtNoteNcCnt", item.get("prevIncrtNoteNcCnt"));
									chartMap.put("prevCrtNoteNcCnt", item.get("prevCrtNoteNcCnt"));
									
									detailMap.put("dt", item.get("dt"));
									detailMap.put("incrtNoteNcCnt", item.get("incrtNoteNcCnt"));
									detailMap.put("prevIncrtNoteNcCnt", item.get("prevIncrtNoteNcCnt"));
									detailMap.put("topIncrtNoteNcCnt", item.get("topIncrtNoteNcCnt"));
									detailMap.put("avgIncrtNoteNcCnt", item.get("avgIncrtNoteNcCnt"));
									
									chartList.add(chartMap);
									detailList.add(detailMap);
								}
							}
							
							data.put("incrtNoteNcChart", chartList);
							data.put("incrtNoteNcMsg", msgMap);
							data.put("incrtNoteNcDetail", detailList);
						}
					}else {
						if(!vu1.isValid()) {
							setResult(msgKey, vu1.getResult());
						}else if(!vu2.isValid()) {
							setResult(msgKey, vu2.getResult());
						}
					}
				}else {
					setResult(msgKey, vuW.getResult());
				}
			}
			setResult(dataKey, data);
		} else {
			setResult(msgKey, vu.getResult());
		}
		
    	return result;
    }

    /**
	 * HAMS-ORG-LD-013
	 * 학습분석 상세 - 맞은 문제 수 현황
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
    @Override    
    public Map getCrtQuesCntStt(Map<String, Object> paramMap) throws Exception {
    	
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "CRTQUESCNTSTT");

		getStudId(paramMap);
		
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		ValidationUtil vuW = new ValidationUtil();
		ValidationUtil vuM = new ValidationUtil();
		ValidationUtil vu1 = new ValidationUtil();
		ValidationUtil vu2 = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"currCon","studId"}, paramMap);
		
		if(vu.isValid()) {
			Map<String, Object> data = new LinkedHashMap<>();
			Map<String, Object> crtQuesData = new HashMap<>();
			Map<String, Object> msgMap = new LinkedHashMap<>();
			ArrayList<Map<String,Object>> crtQuesList = new ArrayList<>();
			ArrayList<Map<String,Object>> chartList = new ArrayList<>();
			ArrayList<Map<String,Object>> detailList = new ArrayList<>();
			
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {
				
				vuM.checkRequired(new String[] {"yyyy","mm"}, paramMap);
				if(vuM.isValid()) {
					String yyyy = paramMap.get("yyyy").toString();
					int mm = Integer.valueOf(paramMap.get("mm").toString());
					String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
					
					paramMap.put("convertMm", convertMm);
					
					vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
					
					if(vu1.isValid()) {
						String yyMm = yyyy + convertMm;
						
						String startDt = yyyy + "-" + convertMm + "-01";
						int lastDay = getCalendarLastDay(startDt, new SimpleDateFormat("yyyy-MM-dd"));
						String endDt = yyyy + "-" +convertMm + "-" + String.valueOf(lastDay);
						
						paramMap.put("yyMm", yyMm);
						paramMap.put("startDt", startDt);
						paramMap.put("endDt", endDt);
						paramMap.put("lastDay", lastDay);
						
						crtQuesData = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getCrtQuesCntStt");
						crtQuesList = (ArrayList<Map<String, Object>>) getMapperResultData(v_param, "list", paramMap, ".getCrtQuesCntSttList");
						
						if(crtQuesData.get("crtQuesCnt") != null || crtQuesData.get("prevCrtQuesCnt") != null) {
							data.put("crtQuesCnt", crtQuesData.get("crtQuesCnt"));
							data.put("prevCrtQuesCnt", crtQuesData.get("prevCrtQuesCnt"));
							data.put("topCrtQuesCnt", crtQuesData.get("topCrtQuesCnt"));
							data.put("avgCrtQuesCnt", crtQuesData.get("avgCrtQuesCnt"));
							
							msgMap.put("summary", crtQuesData.get("summary"));
							msgMap.put("detail", crtQuesData.get("detail"));
							
							if(crtQuesList.size() > 0 && crtQuesList.get(0) != null) {
								for(Map<String, Object> item : crtQuesList) {
									Map<String, Object> chartMap = new LinkedHashMap<>();
									Map<String, Object> detailMap = new LinkedHashMap<>();
									
									chartMap.put("dt", item.get("dt"));
									chartMap.put("prevDt", item.get("prevDt"));
									chartMap.put("crtQuesCnt", item.get("crtQuesCnt"));
									chartMap.put("incrtQuesCnt", item.get("incrtQuesCnt"));
									chartMap.put("prevCrtQuesCnt", item.get("prevCrtQuesCnt"));
									chartMap.put("prevIncrtQuesCnt", item.get("prevIncrtQuesCnt"));
									
									detailMap.put("dt", item.get("dt"));
									detailMap.put("crtQuesCnt", item.get("crtQuesCnt"));
									detailMap.put("prevCrtQuesCnt", item.get("prevCrtQuesCnt"));
									detailMap.put("topCrtQuesCnt", item.get("topCrtQuesCnt"));
									detailMap.put("avgCrtQuesCnt", item.get("avgCrtQuesCnt"));
									
									chartList.add(chartMap);
									detailList.add(detailMap);
								}
							}
							
							data.put("crtQuesChart", chartList);
							data.put("crtQuesMsg", msgMap);
							data.put("crtQuesDetail", detailList);
						}
						
					}else {
						setResult(msgKey, vu1.getResult());
					}
					
				}else {
					setResult(msgKey, vuM.getResult());
				}
			}else {
				vuW.checkRequired(new String[] {"startDt","endDt"}, paramMap);
				
				if(vuW.isValid()) {
					String startDate = paramMap.get("startDt").toString();
					String endDate = paramMap.get("endDt").toString();
					
					//2. 유효성 체크
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					
					if(vu1.isValid() && vu2.isValid()) {
						paramMap.put("lastDay", 7);
						
						crtQuesData = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getCrtQuesCntStt");
						crtQuesList = (ArrayList<Map<String, Object>>) getMapperResultData(v_param, "list", paramMap, ".getCrtQuesCntSttList");
						
						if(crtQuesData.get("crtQuesCnt") != null || crtQuesData.get("prevCrtQuesCnt") != null) {
							data.put("crtQuesCnt", crtQuesData.get("crtQuesCnt"));
							data.put("prevCrtQuesCnt", crtQuesData.get("prevCrtQuesCnt"));
							data.put("topCrtQuesCnt", crtQuesData.get("topCrtQuesCnt"));
							data.put("avgCrtQuesCnt", crtQuesData.get("avgCrtQuesCnt"));
							
							msgMap.put("summary", crtQuesData.get("summary"));
							msgMap.put("detail", crtQuesData.get("detail"));
							
							if(crtQuesList.size() > 0 && crtQuesList.get(0) != null) {
								for(Map<String, Object> item : crtQuesList) {
									Map<String, Object> chartMap = new LinkedHashMap<>();
									Map<String, Object> detailMap = new LinkedHashMap<>();
									
									chartMap.put("dt", item.get("dt"));
									chartMap.put("prevDt", item.get("prevDt"));
									chartMap.put("crtQuesCnt", item.get("crtQuesCnt"));
									chartMap.put("incrtQuesCnt", item.get("incrtQuesCnt"));
									chartMap.put("prevCrtQuesCnt", item.get("prevCrtQuesCnt"));
									chartMap.put("prevIncrtQuesCnt", item.get("prevIncrtQuesCnt"));
									
									detailMap.put("dt", item.get("dt"));
									detailMap.put("crtQuesCnt", item.get("crtQuesCnt"));
									detailMap.put("prevCrtQuesCnt", item.get("prevCrtQuesCnt"));
									detailMap.put("topCrtQuesCnt", item.get("topCrtQuesCnt"));
									detailMap.put("avgCrtQuesCnt", item.get("avgCrtQuesCnt"));
									
									chartList.add(chartMap);
									detailList.add(detailMap);
								}
							}
							
							data.put("crtQuesChart", chartList);
							data.put("crtQuesMsg", msgMap);
							data.put("crtQuesDetail", detailList);
						}
					}else {
						if(!vu1.isValid()) {
							setResult(msgKey, vu1.getResult());
						}else if(!vu2.isValid()) {
							setResult(msgKey, vu2.getResult());
						}
					}
				}else {
					setResult(msgKey, vuW.getResult());
				}
			}
			setResult(dataKey, data);
		} else {
			setResult(msgKey, vu.getResult());
		}
		
    	return result;
    }

    @Override    
    public Map getSlvHabitStt(Map<String, Object> paramMap) throws Exception {
    	
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "SLVHABITSTT");

		getStudId(paramMap);
		
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		ValidationUtil vuW = new ValidationUtil();
		ValidationUtil vuM = new ValidationUtil();
		ValidationUtil vu1 = new ValidationUtil();
		ValidationUtil vu2 = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"currCon","studId"}, paramMap);
		
		if(vu.isValid()) {
			Map<String, Object> data = new LinkedHashMap<>();
			Map<String, Object> slvHabitData = new HashMap<>();
			Map<String, Object> msgMap = new LinkedHashMap<>();
			ArrayList<Map<String,Object>> slvHabitList = new ArrayList<>();
			ArrayList<Map<String,Object>> chartList = new ArrayList<>();
			ArrayList<Map<String,Object>> detailList = new ArrayList<>();
			
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {
				
				vuM.checkRequired(new String[] {"yyyy","mm"}, paramMap);
				if(vuM.isValid()) {
					String yyyy = paramMap.get("yyyy").toString();
					int mm = Integer.valueOf(paramMap.get("mm").toString());
					String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
					
					paramMap.put("convertMm", convertMm);
					
					vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
					
					if(vu1.isValid()) {
						String yyMm = yyyy + convertMm;
						
						String startDt = yyyy + "-" + convertMm + "-01";
						int lastDay = getCalendarLastDay(startDt, new SimpleDateFormat("yyyy-MM-dd"));
						String endDt = yyyy + "-" +convertMm + "-" + String.valueOf(lastDay);
						
						paramMap.put("yyMm", yyMm);
						paramMap.put("startDt", startDt);
						paramMap.put("endDt", endDt);
						paramMap.put("lastDay", lastDay);
						
						slvHabitData = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getSlvHabitStt");
						slvHabitList = (ArrayList<Map<String, Object>>) getMapperResultData(v_param, "list", paramMap, ".getSlvHabitSttList");
						
						if(slvHabitData.get("slvHabitCnt") != null || slvHabitData.get("prevSlvHabitCnt") != null) {
							data.put("slvHabitCnt", slvHabitData.get("slvHabitCnt"));
							data.put("prevSlvHabitCnt", slvHabitData.get("prevSlvHabitCnt"));
							
							msgMap.put("summary", slvHabitData.get("summary"));
							msgMap.put("detail", slvHabitData.get("detail"));
							
							if(slvHabitList.size() > 0 && slvHabitList.get(0) != null) {
								for(Map<String, Object> item : slvHabitList) {
									Map<String, Object> chartMap = new LinkedHashMap<>();
									Map<String, Object> detailMap = new LinkedHashMap<>();
									
									chartMap.put("dt", item.get("dt"));
									chartMap.put("prevDt", item.get("prevDt"));
									chartMap.put("skipQuesCnt", item.get("skipQuesCnt"));
									chartMap.put("cursoryQuesCnt", item.get("cursoryQuesCnt"));
									chartMap.put("guessQuesCnt", item.get("guessQuesCnt"));
									chartMap.put("mistakenQuesCnt", item.get("mistakenQuesCnt"));
									chartMap.put("prevSkipQuesCnt", item.get("prevSkipQuesCnt"));
									chartMap.put("prevCursoryQuesCnt", item.get("prevCursoryQuesCnt"));
									chartMap.put("prevGuessQuesCnt", item.get("prevGuessQuesCnt"));
									chartMap.put("prevMistakenQuesCnt", item.get("prevMistakenQuesCnt"));
									
									detailMap.put("dt", item.get("dt"));
									detailMap.put("skipQuesCnt", item.get("skipQuesCnt"));
									detailMap.put("cursoryQuesCnt", item.get("cursoryQuesCnt"));
									detailMap.put("guessQuesCnt", item.get("guessQuesCnt"));
									detailMap.put("mistakenQuesCnt", item.get("mistakenQuesCnt"));
									detailMap.put("slvHabitCnt", item.get("slvHabitCnt"));
									
									chartList.add(chartMap);
									detailList.add(detailMap);
								}
							}
							
							data.put("slvHabitChart", chartList);
							data.put("slvHabitMsg", msgMap);
							data.put("slvHabitDetail", detailList);
						}
						
					}else {
						setResult(msgKey, vu1.getResult());
					}
					
				}else {
					setResult(msgKey, vuM.getResult());
				}
			}else {
				vuW.checkRequired(new String[] {"startDt","endDt"}, paramMap);
				
				if(vuW.isValid()) {
					String startDate = paramMap.get("startDt").toString();
					String endDate = paramMap.get("endDt").toString();
					
					//2. 유효성 체크
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					
					if(vu1.isValid() && vu2.isValid()) {
						paramMap.put("lastDay", 7);
						
						slvHabitData = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getSlvHabitStt");
						slvHabitList = (ArrayList<Map<String, Object>>) getMapperResultData(v_param, "list", paramMap, ".getSlvHabitSttList");
						
						if(slvHabitData.get("slvHabitCnt") != null || slvHabitData.get("prevSlvHabitCnt") != null) {
							data.put("slvHabitCnt", slvHabitData.get("slvHabitCnt"));
							data.put("prevSlvHabitCnt", slvHabitData.get("prevSlvHabitCnt"));
							
							msgMap.put("summary", slvHabitData.get("summary"));
							msgMap.put("detail", slvHabitData.get("detail"));
							
							if(slvHabitList.size() > 0 && slvHabitList.get(0) != null) {
								for(Map<String, Object> item : slvHabitList) {
									Map<String, Object> chartMap = new LinkedHashMap<>();
									Map<String, Object> detailMap = new LinkedHashMap<>();
									
									chartMap.put("dt", item.get("dt"));
									chartMap.put("prevDt", item.get("prevDt"));
									chartMap.put("skipQuesCnt", item.get("skipQuesCnt"));
									chartMap.put("cursoryQuesCnt", item.get("cursoryQuesCnt"));
									chartMap.put("guessQuesCnt", item.get("guessQuesCnt"));
									chartMap.put("mistakenQuesCnt", item.get("mistakenQuesCnt"));
									chartMap.put("prevSkipQuesCnt", item.get("prevSkipQuesCnt"));
									chartMap.put("prevCursoryQuesCnt", item.get("prevCursoryQuesCnt"));
									chartMap.put("prevGuessQuesCnt", item.get("prevGuessQuesCnt"));
									chartMap.put("prevMistakenQuesCnt", item.get("prevMistakenQuesCnt"));
									
									detailMap.put("dt", item.get("dt"));
									detailMap.put("skipQuesCnt", item.get("skipQuesCnt"));
									detailMap.put("cursoryQuesCnt", item.get("cursoryQuesCnt"));
									detailMap.put("guessQuesCnt", item.get("guessQuesCnt"));
									detailMap.put("mistakenQuesCnt", item.get("mistakenQuesCnt"));
									detailMap.put("slvHabitCnt", item.get("slvHabitCnt"));
									
									chartList.add(chartMap);
									detailList.add(detailMap);
								}
							}
							
							data.put("slvHabitChart", chartList);
							data.put("slvHabitMsg", msgMap);
							data.put("slvHabitDetail", detailList);
						}
					}else {
						if(!vu1.isValid()) {
							setResult(msgKey, vu1.getResult());
						}else if(!vu2.isValid()) {
							setResult(msgKey, vu2.getResult());
						}
					}
				}else {
					setResult(msgKey, vuW.getResult());
				}
			}
			setResult(dataKey, data);
		} else {
			setResult(msgKey, vu.getResult());
		}
    	
    	return result;
    }

    @Override    
    public Map getDayAvgLrnStt (Map<String, Object> paramMap) throws Exception {
    	
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "DAYAVGLRNSTT");

		getStudId(paramMap);
		
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		ValidationUtil vuW = new ValidationUtil();
		ValidationUtil vuM = new ValidationUtil();
		ValidationUtil vu1 = new ValidationUtil();
		ValidationUtil vu2 = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"currCon","studId"}, paramMap);
		
		if(vu.isValid()) {
			Map<String, Object> data = new LinkedHashMap<>();
			Map<String, Object> dayAvgLrnData = new HashMap<>();
			Map<String, Object> msgMap = new LinkedHashMap<>();
			ArrayList<Map<String,Object>> dayAvgLrnList = new ArrayList<>();
			ArrayList<Map<String,Object>> dayAvgLrnDetailList = new ArrayList<>();
			ArrayList<Map<String,Object>> chartList = new ArrayList<>();
			ArrayList<Map<String,Object>> detailList = new ArrayList<>();
			
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {
				
				vuM.checkRequired(new String[] {"yyyy","mm"}, paramMap);
				if(vuM.isValid()) {
					String yyyy = paramMap.get("yyyy").toString();
					int mm = Integer.valueOf(paramMap.get("mm").toString());
					String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
					
					paramMap.put("convertMm", convertMm);
					
					vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
					
					if(vu1.isValid()) {
						String yyMm = yyyy + convertMm;
						
						String startDt = yyyy + "-" + convertMm + "-01";
						int lastDay = getCalendarLastDay(startDt, new SimpleDateFormat("yyyy-MM-dd"));
						String endDt = yyyy + "-" +convertMm + "-" + String.valueOf(lastDay);
						
						paramMap.put("yyMm", yyMm);
						paramMap.put("startDt", startDt);
						paramMap.put("endDt", endDt);
						paramMap.put("lastDay", lastDay);
						
						dayAvgLrnData = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getDayAvgLrnStt");
						dayAvgLrnList = (ArrayList<Map<String, Object>>) getMapperResultData(v_param, "list", paramMap, ".getDayAvgLrnSttList");
						dayAvgLrnDetailList = (ArrayList<Map<String, Object>>) getMapperResultData(v_param, "list", paramMap, ".getDayAvgLrnDetailList");
						
						if(dayAvgLrnData.get("lrnTm") != null || dayAvgLrnData.get("prevLrnTm") != null) {
							data.put("lrnTm", dayAvgLrnData.get("lrnTm"));
							data.put("prevLrnTm", dayAvgLrnData.get("prevLrnTm"));
							
							msgMap.put("summary", dayAvgLrnData.get("summary"));
							msgMap.put("detail", dayAvgLrnData.get("detail"));
							
							if(dayAvgLrnList.size() > 0 && dayAvgLrnList.get(0) != null) {
								for(Map<String, Object> item : dayAvgLrnList) {
									Map<String, Object> chartMap = new LinkedHashMap<>();
									
									chartMap.put("dt", item.get("dt"));
									chartMap.put("subjCd", item.get("subjCd"));
									chartMap.put("lrnTm", item.get("lrnTm"));
									
									chartList.add(chartMap);
								}
							}
							
							if(dayAvgLrnDetailList.size() > 0 && dayAvgLrnDetailList.get(0) != null) {
								for(Map<String, Object> item : dayAvgLrnDetailList) {
									Map<String, Object> detailMap = new LinkedHashMap<>();
									List<String> subjLrnTmList = new ArrayList<>();
									List<Integer> subjLrnTmIntList = new ArrayList<>();
									
									int lrnSec = Integer.valueOf(item.get("totalLrnSec").toString());
									
									if(lrnSec > 0) {
										subjLrnTmList = Arrays.asList(item.get("subjLrnTm").toString().split(","));
										subjLrnTmIntList = subjLrnTmList.stream().map(s -> Integer.parseInt(s)).collect(Collectors.toList());
									}
									
									detailMap.put("dt", item.get("dt"));
									detailMap.put("subjLrnTm", subjLrnTmIntList);
									
									detailList.add(detailMap);
								}
							}
							
							data.put("dayAvgLrnChart", chartList);
							data.put("dayAvgLrnMsg", msgMap);
							data.put("dayAvgLrnDetail", detailList);
						}
						
					}else {
						setResult(msgKey, vu1.getResult());
					}
					
				}else {
					setResult(msgKey, vuM.getResult());
				}
			}else {
				vuW.checkRequired(new String[] {"startDt","endDt"}, paramMap);
				
				if(vuW.isValid()) {
					String startDate = paramMap.get("startDt").toString();
					String endDate = paramMap.get("endDt").toString();
					
					//2. 유효성 체크
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					
					if(vu1.isValid() && vu2.isValid()) {
						paramMap.put("lastDay", 7);
						
						dayAvgLrnData = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getDayAvgLrnStt");
						dayAvgLrnList = (ArrayList<Map<String, Object>>) getMapperResultData(v_param, "list", paramMap, ".getDayAvgLrnSttList");
						dayAvgLrnDetailList = (ArrayList<Map<String, Object>>) getMapperResultData(v_param, "list", paramMap, ".getDayAvgLrnDetailList");
						
						if(dayAvgLrnData.get("lrnTm") != null || dayAvgLrnData.get("prevLrnTm") != null) {
							data.put("lrnTm", dayAvgLrnData.get("lrnTm"));
							data.put("prevLrnTm", dayAvgLrnData.get("prevLrnTm"));
							
							msgMap.put("summary", dayAvgLrnData.get("summary"));
							msgMap.put("detail", dayAvgLrnData.get("detail"));
							
							if(dayAvgLrnList.size() > 0 && dayAvgLrnList.get(0) != null) {
								for(Map<String, Object> item : dayAvgLrnList) {
									Map<String, Object> chartMap = new LinkedHashMap<>();
									
									chartMap.put("dt", item.get("dt"));
									chartMap.put("subjCd", item.get("subjCd"));
									chartMap.put("lrnTm", item.get("lrnTm"));
									
									chartList.add(chartMap);
								}
							}
							
							if(dayAvgLrnDetailList.size() > 0 && dayAvgLrnDetailList.get(0) != null) {
								for(Map<String, Object> item : dayAvgLrnDetailList) {
									Map<String, Object> detailMap = new LinkedHashMap<>();
									List<String> subjLrnTmList = new ArrayList<>();
									List<Integer> subjLrnTmIntList = new ArrayList<>();
									
									int lrnSec = Integer.valueOf(item.get("totalLrnSec").toString());
									
									if(lrnSec > 0) {
										subjLrnTmList = Arrays.asList(item.get("subjLrnTm").toString().split(","));
										subjLrnTmIntList = subjLrnTmList.stream().map(s -> Integer.parseInt(s)).collect(Collectors.toList());
									}
									
									detailMap.put("dt", item.get("dt"));
									detailMap.put("subjLrnTm", subjLrnTmIntList);
									
									detailList.add(detailMap);
								}
							}
							
							data.put("dayAvgLrnChart", chartList);
							data.put("dayAvgLrnMsg", msgMap);
							data.put("dayAvgLrnDetail", detailList);
						}
					}else {
						if(!vu1.isValid()) {
							setResult(msgKey, vu1.getResult());
						}else if(!vu2.isValid()) {
							setResult(msgKey, vu2.getResult());
						}
					}
				}else {
					setResult(msgKey, vuW.getResult());
				}
			}
			setResult(dataKey, data);
		} else {
			setResult(msgKey, vu.getResult());
		}
    	
    	return result;
    }

    @Override    
    public Map getTotalLrnTmStt(Map<String, Object> paramMap) throws Exception {
    	
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "TOTALLRNTMSTT");

		getStudId(paramMap);
		
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		ValidationUtil vuW = new ValidationUtil();
		ValidationUtil vuM = new ValidationUtil();
		ValidationUtil vu1 = new ValidationUtil();
		ValidationUtil vu2 = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"currCon","studId"}, paramMap);
		
		if(vu.isValid()) {
			Map<String, Object> data = new LinkedHashMap<>();
			Map<String, Object> totalLrnTmData = new HashMap<>();
			Map<String, Object> msgMap = new LinkedHashMap<>();
			ArrayList<Map<String,Object>> totalLrnTmDetailList = new ArrayList<>();
			ArrayList<Map<String,Object>> detailList = new ArrayList<>();
			
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {
				
				vuM.checkRequired(new String[] {"yyyy","mm"}, paramMap);
				if(vuM.isValid()) {
					String yyyy = paramMap.get("yyyy").toString();
					int mm = Integer.valueOf(paramMap.get("mm").toString());
					String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
					
					paramMap.put("convertMm", convertMm);
					
					vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
					
					if(vu1.isValid()) {
						String yyMm = yyyy + convertMm;
						
						String startDt = yyyy + "-" + convertMm + "-01";
						int lastDay = getCalendarLastDay(startDt, new SimpleDateFormat("yyyy-MM-dd"));
						String endDt = yyyy + "-" +convertMm + "-" + String.valueOf(lastDay);
						
						paramMap.put("yyMm", yyMm);
						paramMap.put("startDt", startDt);
						paramMap.put("endDt", endDt);
						paramMap.put("lastDay", lastDay);
						
						totalLrnTmData = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getTotalLrnTmStt");
						totalLrnTmDetailList = (ArrayList<Map<String, Object>>) getMapperResultData(v_param, "list", paramMap, ".getTotalLrnTmSttList");
						
						if(totalLrnTmData.get("totalLrnTm") != null || totalLrnTmData.get("prevTotalLrnTm") != null) {
							data.put("totalLrnTm", totalLrnTmData.get("totalLrnTm"));
							data.put("prevTotalLrnTm", totalLrnTmData.get("prevTotalLrnTm"));
							data.put("topTotalLrnTm", totalLrnTmData.get("topTotalLrnTm"));
							data.put("avgTotalLrnTm", totalLrnTmData.get("avgTotalLrnTm"));
							
							msgMap.put("summary", totalLrnTmData.get("summary"));
							msgMap.put("detail", totalLrnTmData.get("detail"));
							
							if(totalLrnTmDetailList.size() > 0 && totalLrnTmDetailList.get(0) != null) {
								for(Map<String, Object> item : totalLrnTmDetailList) {
									Map<String, Object> detailMap = new LinkedHashMap<>();
									
									detailMap.put("dt", item.get("dt"));
									detailMap.put("totalLrnTm", item.get("totalLrnTm"));
									detailMap.put("prevTotalLrnTm", item.get("prevTotalLrnTm"));
									detailMap.put("topTotalLrnTm", item.get("topTotalLrnTm"));
									detailMap.put("avgTotalLrnTm", item.get("avgTotalLrnTm"));
									
									detailList.add(detailMap);
								}
							}
							
							data.put("totalLrnTmMsg", msgMap);
							data.put("totalLrnTmDetail", detailList);
						}
						
					}else {
						setResult(msgKey, vu1.getResult());
					}
					
				}else {
					setResult(msgKey, vuM.getResult());
				}
			}else {
				vuW.checkRequired(new String[] {"startDt","endDt"}, paramMap);
				
				if(vuW.isValid()) {
					String startDate = paramMap.get("startDt").toString();
					String endDate = paramMap.get("endDt").toString();
					
					//2. 유효성 체크
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					
					if(vu1.isValid() && vu2.isValid()) {
						paramMap.put("lastDay", 7);
						
						totalLrnTmData = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getTotalLrnTmStt");
						totalLrnTmDetailList = (ArrayList<Map<String, Object>>) getMapperResultData(v_param, "list", paramMap, ".getTotalLrnTmSttList");
						
						if(totalLrnTmData.get("totalLrnTm") != null || totalLrnTmData.get("prevTotalLrnTm") != null) {
							data.put("totalLrnTm", totalLrnTmData.get("totalLrnTm"));
							data.put("prevTotalLrnTm", totalLrnTmData.get("prevTotalLrnTm"));
							data.put("topTotalLrnTm", totalLrnTmData.get("topTotalLrnTm"));
							data.put("avgTotalLrnTm", totalLrnTmData.get("avgTotalLrnTm"));
							
							msgMap.put("summary", totalLrnTmData.get("summary"));
							msgMap.put("detail", totalLrnTmData.get("detail"));
							
							if(totalLrnTmDetailList.size() > 0 && totalLrnTmDetailList.get(0) != null) {
								for(Map<String, Object> item : totalLrnTmDetailList) {
									Map<String, Object> detailMap = new LinkedHashMap<>();
									
									detailMap.put("dt", item.get("dt"));
									detailMap.put("totalLrnTm", item.get("totalLrnTm"));
									detailMap.put("prevTotalLrnTm", item.get("prevTotalLrnTm"));
									detailMap.put("topTotalLrnTm", item.get("topTotalLrnTm"));
									detailMap.put("avgTotalLrnTm", item.get("avgTotalLrnTm"));
									
									detailList.add(detailMap);
								}
							}
							
							data.put("totalLrnTmMsg", msgMap);
							data.put("totalLrnTmDetail", detailList);
						}
					}else {
						if(!vu1.isValid()) {
							setResult(msgKey, vu1.getResult());
						}else if(!vu2.isValid()) {
							setResult(msgKey, vu2.getResult());
						}
					}
				}else {
					setResult(msgKey, vuW.getResult());
				}
			}
			setResult(dataKey, data);
		} else {
			setResult(msgKey, vu.getResult());
		}
    	
    	return result;
    }

    @Override    
    public Map getLongLrnTmStt(Map<String, Object> paramMap) throws Exception {
    	
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "LONGLRNTMSTT");

		getStudId(paramMap);
		
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		ValidationUtil vuW = new ValidationUtil();
		ValidationUtil vuM = new ValidationUtil();
		ValidationUtil vu1 = new ValidationUtil();
		ValidationUtil vu2 = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"currCon","studId"}, paramMap);
		
		if(vu.isValid()) {
			Map<String, Object> data = new LinkedHashMap<>();
			Map<String, Object> longLrnTmData = new HashMap<>();
			Map<String, Object> msgMap = new LinkedHashMap<>();
			ArrayList<Map<String,Object>> longLrnTmList = new ArrayList<>();
			ArrayList<Map<String,Object>> longLrnTmDetailList = new ArrayList<>();
			ArrayList<Map<String,Object>> chartList = new ArrayList<>();
			ArrayList<Map<String,Object>> detailList = new ArrayList<>();
			
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {
				
				vuM.checkRequired(new String[] {"yyyy","mm"}, paramMap);
				if(vuM.isValid()) {
					String yyyy = paramMap.get("yyyy").toString();
					int mm = Integer.valueOf(paramMap.get("mm").toString());
					String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
					
					paramMap.put("convertMm", convertMm);
					
					vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
					
					if(vu1.isValid()) {
						String yyMm = yyyy + convertMm;
						
						String startDt = yyyy + "-" + convertMm + "-01";
						int lastDay = getCalendarLastDay(startDt, new SimpleDateFormat("yyyy-MM-dd"));
						String endDt = yyyy + "-" +convertMm + "-" + String.valueOf(lastDay);
						
						paramMap.put("yyMm", yyMm);
						paramMap.put("startDt", startDt);
						paramMap.put("endDt", endDt);
						paramMap.put("lastDay", lastDay);
						
						longLrnTmData = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getLongLrnTmStt");
						longLrnTmList = (ArrayList<Map<String, Object>>) getMapperResultData(v_param, "list", paramMap, ".getLongLrnTmSttList");
						longLrnTmDetailList = (ArrayList<Map<String, Object>>) getMapperResultData(v_param, "list", paramMap, ".getLongLrnTmDetailList");
						
						if(longLrnTmData.get("longLrnSubjCd") != null || longLrnTmData.get("prevLongLrnSubjLrnTm") != null) {
							data.put("longLrnSubjCd", longLrnTmData.get("longLrnSubjCd"));
							data.put("longLrnSubjLrnTm", longLrnTmData.get("longLrnSubjLrnTm"));
							data.put("prevLongLrnSubjLrnTm", longLrnTmData.get("prevLongLrnSubjLrnTm"));
							
							msgMap.put("summary", longLrnTmData.get("summary"));
							msgMap.put("detail", longLrnTmData.get("detail"));
							
							if(longLrnTmList.size() > 0 && longLrnTmList.get(0) != null) {
								for(Map<String, Object> item : longLrnTmList) {
									Map<String, Object> chartMap = new LinkedHashMap<>();
									
									chartMap.put("subjCd", item.get("subjCd"));
									chartMap.put("lrnTm", item.get("lrnTm"));
									chartMap.put("prevLrnTm", item.get("prevLrnTm"));
									
									chartList.add(chartMap);
								}
							}
							
							if(longLrnTmDetailList.size() > 0 && longLrnTmDetailList.get(0) != null) {
								for(Map<String, Object> item : longLrnTmDetailList) {
									Map<String, Object> detailMap = new LinkedHashMap<>();
									List<String> subjLrnTmList = new ArrayList<>();
									List<Integer> subjLrnTmIntList = new ArrayList<>();
									
									int lrnSec = Integer.valueOf(item.get("totalLrnSec").toString());
									
									if(lrnSec > 0) {
										subjLrnTmList = Arrays.asList(item.get("subjLrnTm").toString().split(","));
										subjLrnTmIntList = subjLrnTmList.stream().map(s -> Integer.parseInt(s)).collect(Collectors.toList());
										
										
									}
									
									detailMap.put("dt", item.get("dt"));
									detailMap.put("subjLrnTm", subjLrnTmIntList);
									
									detailList.add(detailMap);
								}
							}
							
							data.put("longLrnTmChart", chartList);
							data.put("longLrnTmMsg", msgMap);
							data.put("longLrnTmDetail", detailList);
						}
						
					}else {
						setResult(msgKey, vu1.getResult());
					}
					
				}else {
					setResult(msgKey, vuM.getResult());
				}
			}else {
				vuW.checkRequired(new String[] {"startDt","endDt"}, paramMap);
				
				if(vuW.isValid()) {
					String startDate = paramMap.get("startDt").toString();
					String endDate = paramMap.get("endDt").toString();
					
					//2. 유효성 체크
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					
					if(vu1.isValid() && vu2.isValid()) {
						paramMap.put("lastDay", 7);
						
						longLrnTmData = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getLongLrnTmStt");
						longLrnTmList = (ArrayList<Map<String, Object>>) getMapperResultData(v_param, "list", paramMap, ".getLongLrnTmSttList");
						longLrnTmDetailList = (ArrayList<Map<String, Object>>) getMapperResultData(v_param, "list", paramMap, ".getLongLrnTmDetailList");
						
						if(longLrnTmData.get("longLrnSubjCd") != null || longLrnTmData.get("prevLongLrnSubjLrnTm") != null) {
							data.put("longLrnSubjCd", longLrnTmData.get("longLrnSubjCd"));
							data.put("longLrnSubjLrnTm", longLrnTmData.get("longLrnSubjLrnTm"));
							data.put("prevLongLrnSubjLrnTm", longLrnTmData.get("prevLongLrnSubjLrnTm"));
							
							msgMap.put("summary", longLrnTmData.get("summary"));
							msgMap.put("detail", longLrnTmData.get("detail"));
							
							if(longLrnTmList.size() > 0 && longLrnTmList.get(0) != null) {
								for(Map<String, Object> item : longLrnTmList) {
									Map<String, Object> chartMap = new LinkedHashMap<>();
									
									chartMap.put("subjCd", item.get("subjCd"));
									chartMap.put("lrnTm", item.get("lrnTm"));
									chartMap.put("prevLrnTm", item.get("prevLrnTm"));
									
									chartList.add(chartMap);
								}
							}
							
							if(longLrnTmDetailList.size() > 0 && longLrnTmDetailList.get(0) != null) {
								for(Map<String, Object> item : longLrnTmDetailList) {
									Map<String, Object> detailMap = new LinkedHashMap<>();
									List<String> subjLrnTmList = new ArrayList<>();
									List<Integer> subjLrnTmIntList = new ArrayList<>();
									
									int lrnSec = Integer.valueOf(item.get("totalLrnSec").toString());
									
									if(lrnSec > 0) {
										subjLrnTmList = Arrays.asList(item.get("subjLrnTm").toString().split(","));
										subjLrnTmIntList = subjLrnTmList.stream().map(s -> Integer.parseInt(s)).collect(Collectors.toList());
									}
									
									detailMap.put("dt", item.get("dt"));
									detailMap.put("subjLrnTm", subjLrnTmIntList);
									
									detailList.add(detailMap);
								}
							}
							
							data.put("longLrnTmChart", chartList);
							data.put("longLrnTmMsg", msgMap);
							data.put("longLrnTmDetail", detailList);
						}
					}else {
						if(!vu1.isValid()) {
							setResult(msgKey, vu1.getResult());
						}else if(!vu2.isValid()) {
							setResult(msgKey, vu2.getResult());
						}
					}
				}else {
					setResult(msgKey, vuW.getResult());
				}
			}
			setResult(dataKey, data);
		} else {
			setResult(msgKey, vu.getResult());
		}
    	
    	return result;
    }

    @Override    
    public Map getSubjExam(Map<String, Object> paramMap) throws Exception {
    	
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "SUBJEXAM");

		getStudId(paramMap);
		
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		ValidationUtil vuW = new ValidationUtil();
		ValidationUtil vuM = new ValidationUtil();
		ValidationUtil vu1 = new ValidationUtil();
		ValidationUtil vu2 = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"currCon","studId"}, paramMap);
		
		if(vu.isValid()) {
			Map<String, Object> data = new HashMap<>();
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {
				vuM.checkRequired(new String[] {"yyyy","mm"}, paramMap);
				
				if(vuM.isValid()) {
					String yyyy = paramMap.get("yyyy").toString();
					int mm = Integer.valueOf(paramMap.get("mm").toString());
					String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
					
					paramMap.put("convertMm", convertMm);
					
					vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
					
					if(vu1.isValid()) {
						String startDt = yyyy + "-" + convertMm + "-01";
						int lastDay = getCalendarLastDay(startDt, new SimpleDateFormat("yyyy-MM-dd"));
						String endDt = yyyy + "-" +convertMm + "-" + String.valueOf(lastDay);
						
						paramMap.put("startDt", startDt);
						paramMap.put("endDt", endDt);
						
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getSubjExam");
					}else {
						setResult(msgKey, vu1.getResult());
					}
				}else {
					setResult(msgKey, vuM.getResult());
				}
			}else {
				vuW.checkRequired(new String[] {"startDt","endDt"}, paramMap);
				
				if(vuW.isValid()) {
					String startDate = paramMap.get("startDt").toString();
					String endDate = paramMap.get("endDt").toString();
					
					//2. 유효성 체크
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					
					if(vu1.isValid() && vu2.isValid()) {
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getSubjExam");
					}else {
						if(!vu1.isValid()) {
							setResult(msgKey, vu1.getResult());
						}else if(!vu2.isValid()) {
							setResult(msgKey, vu2.getResult());
						}
					}
				}else {
					setResult(msgKey, vuW.getResult());
				}
			}
			
			setResult(dataKey, data);
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		
    	
    	return result;
    }

    @Override    
    public Map getCompareSub(Map<String, Object> paramMap) throws Exception {
    	
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "COMPARESUB");

		getStudId(paramMap);
		
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		ValidationUtil vuW = new ValidationUtil();
		ValidationUtil vuM = new ValidationUtil();
		ValidationUtil vu1 = new ValidationUtil();
		ValidationUtil vu2 = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"currCon","studId"}, paramMap);
		
		if(vu.isValid()) {
			Map<String, Object> data = new LinkedHashMap<>();
			Map<String, Object> dataMap = new HashMap<>();
			Map<String, Object> positiveData = new LinkedHashMap<>();
			Map<String, Object> positiveCurrData = new LinkedHashMap<>();
			Map<String, Object> positivePrevData = new LinkedHashMap<>();
			Map<String, Object> positiveMsgData = new LinkedHashMap<>();
			Map<String, Object> negativeData = new LinkedHashMap<>();
			Map<String, Object> negativeCurrData = new LinkedHashMap<>();
			Map<String, Object> negativePrevData = new LinkedHashMap<>();
			Map<String, Object> negativeMsgData = new LinkedHashMap<>();
			
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {
				vuM.checkRequired(new String[] {"yyyy","mm"}, paramMap);
				
				if(vuM.isValid()) {
					String yyyy = paramMap.get("yyyy").toString();
					int mm = Integer.valueOf(paramMap.get("mm").toString());
					String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
					
					paramMap.put("convertMm", convertMm);
					
					vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
					
					if(vu1.isValid()) {
						String startDt = yyyy + "-" + convertMm + "-01";
						int lastDay = getCalendarLastDay(startDt, new SimpleDateFormat("yyyy-MM-dd"));
						String endDt = yyyy + "-" +convertMm + "-" + String.valueOf(lastDay);
						
						paramMap.put("startDt", startDt);
						paramMap.put("endDt", endDt);
						
						dataMap = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getCompareSub");
						
						if(dataMap.get("maxSubjCd") != null || dataMap.get("minSubjCd") != null) {
							positiveData.put("subjCd", dataMap.get("maxSubjCd"));
							
							positiveCurrData.put("dt", dataMap.get("dt").toString());
							positiveCurrData.put("crtRt", dataMap.get("maxCrtRt"));
							positivePrevData.put("dt", dataMap.get("preDt").toString());
							positivePrevData.put("crtRt", dataMap.get("preMaxCrtRt"));
							positiveMsgData.put("summary", null); // 메세지 기획안 확인 후 작업 예정
							positiveMsgData.put("detail", null); // 메세지 기획안 확인 후 작업 예정
							
							positiveData.put("current", positiveCurrData);
							positiveData.put("prev", positivePrevData);
							positiveData.put("msg", positiveMsgData);
							
							data.put("positive", positiveData);
							
							negativeData.put("subjCd", dataMap.get("minSubjCd"));
							
							negativeCurrData.put("dt", dataMap.get("dt").toString());
							negativeCurrData.put("crtRt", dataMap.get("minCrtRt"));
							negativePrevData.put("dt", dataMap.get("preDt").toString());
							negativePrevData.put("crtRt", dataMap.get("preMinCrtRt"));
							negativeMsgData.put("summary", null); // 메세지 기획안 확인 후 작업 예정
							negativeMsgData.put("detail", null); // 메세지 기획안 확인 후 작업 예정
							
							negativeData.put("current", negativeCurrData);
							negativeData.put("prev", negativePrevData);
							negativeData.put("msg", negativeMsgData);
							
							data.put("negative", negativeData);
						}
					}else {
						setResult(msgKey, vu1.getResult());
					}
				}else {
					setResult(msgKey, vuM.getResult());
				}
			}else {
				vuW.checkRequired(new String[] {"startDt","endDt"}, paramMap);
				
				if(vuW.isValid()) {
					String startDate = paramMap.get("startDt").toString();
					String endDate = paramMap.get("endDt").toString();
					
					//2. 유효성 체크
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					
					if(vu1.isValid() && vu2.isValid()) {
						dataMap = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getCompareSub");
						
						if(dataMap.get("maxSubjCd") != null || dataMap.get("minSubjCd") != null) {
							positiveData.put("subjCd", dataMap.get("maxSubjCd"));
							
							positiveCurrData.put("dt", dataMap.get("dt"));
							positiveCurrData.put("crtRt", dataMap.get("maxCrtRt"));
							positivePrevData.put("dt", dataMap.get("preDt"));
							positivePrevData.put("crtRt", dataMap.get("preMaxCrtRt"));
							positiveMsgData.put("summary", null); // 메세지 기획안 확인 후 작업 예정
							positiveMsgData.put("detail", null); // 메세지 기획안 확인 후 작업 예정
							
							positiveData.put("current", positiveCurrData);
							positiveData.put("prev", positivePrevData);
							positiveData.put("msg", positiveMsgData);
							
							data.put("positive", positiveData);
							
							negativeData.put("subjCd", dataMap.get("minSubjCd"));
							
							negativeCurrData.put("dt", dataMap.get("dt"));
							negativeCurrData.put("crtRt", dataMap.get("minCrtRt"));
							negativePrevData.put("dt", dataMap.get("preDt"));
							negativePrevData.put("crtRt", dataMap.get("preMinCrtRt"));
							negativeMsgData.put("summary", null); // 메세지 기획안 확인 후 작업 예정
							negativeMsgData.put("detail", null); // 메세지 기획안 확인 후 작업 예정
							
							negativeData.put("current", negativeCurrData);
							negativeData.put("prev", negativePrevData);
							negativeData.put("msg", negativeMsgData);
							
							data.put("negative", negativeData);
						}
					}else {
						if(!vu1.isValid()) {
							setResult(msgKey, vu1.getResult());
						}else if(!vu2.isValid()) {
							setResult(msgKey, vu2.getResult());
						}
					}
				}else {
					setResult(msgKey, vuW.getResult());
				}
			}
			
			setResult(dataKey, data);
		} else {
			setResult(msgKey, vu.getResult());
		}
		
    	return result;
    }

    @Override    
    public Map getExamChart(Map<String, Object> paramMap) throws Exception {
    	
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "EXAMCHART");

		getStudId(paramMap);
		
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		ValidationUtil vuW = new ValidationUtil();
		ValidationUtil vuM = new ValidationUtil();
		ValidationUtil vu1 = new ValidationUtil();
		ValidationUtil vu2 = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"currCon","studId"}, paramMap);
		
		if(vu.isValid()) {
			ArrayList<Map<String,Object>> data = new ArrayList<>();
			
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {
				vuM.checkRequired(new String[] {"yyyy","mm"}, paramMap);
				
				if(vuM.isValid()) {
					String yyyy = paramMap.get("yyyy").toString();
					int mm = Integer.valueOf(paramMap.get("mm").toString());
					String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
					
					paramMap.put("convertMm", convertMm);
					
					vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
					
					if(vu1.isValid()) {
						String startDt = yyyy + "-" + convertMm + "-01";
						int lastDay = getCalendarLastDay(startDt, new SimpleDateFormat("yyyy-MM-dd"));
						String endDt = yyyy + "-" +convertMm + "-" + String.valueOf(lastDay);
						
						paramMap.put("startDt", startDt);
						paramMap.put("endDt", endDt);
						
						ArrayList<Map<String,Object>> dataList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getExamChart");
						
						int nullCheck = 0;
						for(Map<String, Object> item : dataList) {
							if(item == null) {
								nullCheck++;
							}
						}
						
						if(nullCheck < 6) {
							data = dataList;
						}
					}else {
						setResult(msgKey, vu1.getResult());
					}
				}else {
					setResult(msgKey, vuM.getResult());
				}
			}else {
				vuW.checkRequired(new String[] {"startDt","endDt"}, paramMap);
				
				if(vuW.isValid()) {
					String startDate = paramMap.get("startDt").toString();
					String endDate = paramMap.get("endDt").toString();
					
					//2. 유효성 체크
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					
					if(vu1.isValid() && vu2.isValid()) {
						ArrayList<Map<String,Object>> dataList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getExamChart");
						
						int nullCheck = 0;
						for(Map<String, Object> item : dataList) {
							if(item == null) {
								nullCheck++;
							}
						}
						
						if(nullCheck < 6) {
							data = dataList;
						}
					}else {
						if(!vu1.isValid()) {
							setResult(msgKey, vu1.getResult());
						}else if(!vu2.isValid()) {
							setResult(msgKey, vu2.getResult());
						}
					}
				}else {
					setResult(msgKey, vuW.getResult());
				}
				
			}
			
			setResult(dataKey, data);
		} else {
			setResult(msgKey, vu.getResult());
		}
    	
    	return result;
    }

    @Override    
    public Map getSubjExamList(Map<String, Object> paramMap) throws Exception {
    	
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "SUBJEXAMLIST");
    	
    	getStudId(paramMap);
    	
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		ValidationUtil vuW = new ValidationUtil();
		ValidationUtil vuM = new ValidationUtil();
		ValidationUtil vu1 = new ValidationUtil();
		ValidationUtil vu2 = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"currCon","studId"}, paramMap);
		
		if(vu.isValid()) {
			Map<String,Object> data = new LinkedHashMap<>();
			Map<String,Object> dataMap = new LinkedHashMap<>();
			Map<String,Object> cntMap = new LinkedHashMap<>();
			ArrayList<Map<String,Object>> subjExamList = new ArrayList<>();
			
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			paramMap.put("currConCheck", currConCheck);
			
			data.put("examTypes", getMapperResultData(v_param, "list", paramMap, ".getExamCdList"));
			data.put("analysisTypes", getMapperResultData(v_param, "list", paramMap, ".getQuesCdList"));
			
			if(currConCheck.equals("m")) {
				vuM.checkRequired(new String[] {"yyyy","mm"}, paramMap);
				
				if(vuM.isValid()) {
					String yyyy = paramMap.get("yyyy").toString();
					int mm = Integer.valueOf(paramMap.get("mm").toString());
					String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
					
					paramMap.put("convertMm", convertMm);
					
					vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
					
					if(vu1.isValid()) {
						String startDt = yyyy + "-" + convertMm + "-01";
						int lastDay = getCalendarLastDay(startDt, new SimpleDateFormat("yyyy-MM-dd"));
						String endDt = yyyy + "-" +convertMm + "-" + String.valueOf(lastDay);
						
						paramMap.put("startDt", startDt);
						paramMap.put("endDt", endDt);
						
						if(paramMap.containsKey("types")) {
							paramMap.put("types",paramMap.get("types").toString().split(","));
						}
						
						subjExamList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getSubjExamList");
						cntMap = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getSubjExamListCnt");
						
						dataMap.put("totalCnt", cntMap.get("totalCnt"));
						dataMap.put("list", subjExamList);
						
						data.put("examList", dataMap);	
					}else {
						setResult(msgKey, vu1.getResult());
					}
				}else {
					setResult(msgKey, vuM.getResult());
				}
			}else {
				vuW.checkRequired(new String[] {"startDt","endDt"}, paramMap);
				
				if(vuW.isValid()) {
					String startDate = paramMap.get("startDt").toString();
					String endDate = paramMap.get("endDt").toString();
					
					//2. 유효성 체크
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					
					if(vu1.isValid() && vu2.isValid()) {
						if(paramMap.containsKey("types")) {
							paramMap.put("types",paramMap.get("types").toString().split(","));
						}
						
						subjExamList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getSubjExamList");
						cntMap = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getSubjExamListCnt");
						
						dataMap.put("totalCnt", cntMap.get("totalCnt"));
						dataMap.put("list", subjExamList);
						
						data.put("examList", dataMap);
					}else {
						if(!vu1.isValid()) {
							setResult(msgKey, vu1.getResult());
						}else if(!vu2.isValid()) {
							setResult(msgKey, vu2.getResult());
						}
					}
				}else {
					setResult(msgKey, vuW.getResult());
				}
				
			}
			
			setResult(dataKey, data);
		} else {
			setResult(msgKey, vu.getResult());
		}
		
    	return result;
    }

    @Override    
    public Map getIncrtNote(Map<String, Object> paramMap) throws Exception {
    	
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "INCRTNOTE");
    	
    	getStudId(paramMap);
    	
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		ValidationUtil vuW = new ValidationUtil();
		ValidationUtil vuM = new ValidationUtil();
		ValidationUtil vu1 = new ValidationUtil();
		ValidationUtil vu2 = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"currCon","studId"}, paramMap);
		
		if(vu.isValid()) {
			Map<String,Object> data = new LinkedHashMap<>();
			ArrayList<Map<String,Object>> incrtNtList = new ArrayList<>();
			
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			paramMap.put("currConCheck", currConCheck);
			
			data.put("examTypes", getMapperResultData(v_param, "", paramMap, ".getIncrtNote"));
			
			if(currConCheck.equals("m")) {
				vuM.checkRequired(new String[] {"yyyy","mm"}, paramMap);
				
				if(vuM.isValid()) {
					String yyyy = paramMap.get("yyyy").toString();
					int mm = Integer.valueOf(paramMap.get("mm").toString());
					String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
					
					paramMap.put("convertMm", convertMm);
					
					vu1.isYearMonth("yyyy, mm", yyyy+convertMm);
					
					if(vu1.isValid()) {
						String startDt = yyyy + "-" + convertMm + "-01";
						int lastDay = getCalendarLastDay(startDt, new SimpleDateFormat("yyyy-MM-dd"));
						String endDt = yyyy + "-" +convertMm + "-" + String.valueOf(lastDay);
						
						paramMap.put("startDt", startDt);
						paramMap.put("endDt", endDt);
						
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getIncrtNote");
						incrtNtList = (ArrayList<Map<String, Object>>) getMapperResultData(v_param, "list", paramMap, ".getIncrtNoteList");
						
						if(data != null) {
							data.put("list", incrtNtList);	
						}
						
					}else {
						setResult(msgKey, vu1.getResult());
					}
				}else {
					setResult(msgKey, vuM.getResult());
				}
			}else {
				vuW.checkRequired(new String[] {"startDt","endDt"}, paramMap);
				
				if(vuW.isValid()) {
					String startDate = paramMap.get("startDt").toString();
					String endDate = paramMap.get("endDt").toString();
					
					//2. 유효성 체크
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					
					if(vu1.isValid() && vu2.isValid()) {
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getIncrtNote");
						incrtNtList = (ArrayList<Map<String, Object>>) getMapperResultData(v_param, "list", paramMap, ".getIncrtNoteList");
						
						if(data != null) {
							data.put("list", incrtNtList);	
						}
					}else {
						if(!vu1.isValid()) {
							setResult(msgKey, vu1.getResult());
						}else if(!vu2.isValid()) {
							setResult(msgKey, vu2.getResult());
						}
					}
				}else {
					setResult(msgKey, vuW.getResult());
				}
			}
			
			setResult(dataKey, data);
		} else {
			setResult(msgKey, vu.getResult());
		}
    	
    	return result;
    }

    /**
	 * HAMS-ORG-ES-005
	 * 평가분석 요약 - 단원별 이해
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
    @Override    
    public Map getChapterStt(Map<String, Object> paramMap) throws Exception {
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "CHAPTERSTT");

		getStudId(paramMap);
		
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		ValidationUtil vu1 = new ValidationUtil();
		ValidationUtil vu2 = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"currCon","studId"}, paramMap);
		
		if(vu.isValid()) { 		
			Map<String,Object> data = new HashMap<>();
            String startDate;
			String endDate;
			
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			Map<String,Object> msg = new HashMap<>();
			String msg_summary = null;
			String msg_detail = null;
			ArrayList<Map<String,Object>> detailList = new ArrayList<>();
			Map<String,Object> detailChart = new HashMap<>();
			ArrayList<Map<String,Object>> detail = new ArrayList<>();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {	// 월간
				//1-1.필수값 체크 
				vu1.checkRequired(new String[] {"yyyy","mm"}, paramMap);
				
				if(vu1.isValid()) { 	
					String yyyy = paramMap.get("yyyy").toString();
					int mm = Integer.valueOf(paramMap.get("mm").toString());
					String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
					String yymm = yyyy + convertMm;

					paramMap.put("yymm", yymm);
					//2. 유효성 체크
					vu2.isYearMonth("yyyy, mm", yymm);
					if(vu2.isValid()) {
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getChapterStt");
						detailChart.put("subjCd", Arrays.asList(data.get("subjCdList").toString().split("\\|")));	//과목코드
						data.remove("subjCdList");
								
						if(data != null) {
							detailList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getChapterSttDetail");
							if(detailList.size() > 0 && detailList.get(0) != null) {
								for(Map<String, Object> item : detailList) {
									Map<String, Object> detailMap = new LinkedHashMap<>();									
									
									detailMap.put("grade", item.get("grade"));
									detailMap.put("term", item.get("term"));
									detailMap.put("chapter", item.get("chapter"));
									detailMap.put("chapterCd", item.get("chapterCd").toString().split("\\|", -1));
									detailMap.put("chapterNm", item.get("chapterNm").toString().split("\\|", -1));
									detailMap.put("understandingLv", Arrays.asList(item.get("understandingLv").toString().split("\\|", -1)).stream().mapToInt(Integer::parseInt).toArray());

									detail.add(detailMap);
								}
							}	
							detailChart.put("list", detail);		//목록
							data.put("chapter", detailChart);		//단원							
						}
						setResult(msgKey, data);			
					} else {
						setResult(msgKey, vu2.getResult());				
					}
				} else {
					setResult(msgKey, vu1.getResult());
				}
	        } else {	// 주간 & 기간
	        	//1-1.필수값 체크
				vu.checkRequired(new String[] {"startDt","endDt"}, paramMap);
				
				if(vu.isValid()) { 	
					startDate = paramMap.get("startDt").toString();
					endDate = paramMap.get("endDt").toString();
					
					//2. 유효성 체크
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					
					if(vu1.isValid() && vu2.isValid()) {
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getChapterStt");
						detailChart.put("subjCd", Arrays.asList(data.get("subjCdList").toString().split("\\|")));	//과목코드
						data.remove("subjCdList");
						if(data != null) {
							detailList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getChapterSttDetail");
							if(detailList.size() > 0 && detailList.get(0) != null) {
								for(Map<String, Object> item : detailList) {
									Map<String, Object> detailMap = new LinkedHashMap<>();									
									
									detailMap.put("grade", item.get("grade"));
									detailMap.put("term", item.get("term"));
									detailMap.put("chapter", item.get("chapter"));
									detailMap.put("chapterCd", item.get("chapterCd").toString().split("\\|", -1));
									detailMap.put("chapterNm", item.get("chapterNm").toString().split("\\|", -1));
									detailMap.put("understandingLv", Arrays.asList(item.get("understandingLv").toString().split("\\|", -1)).stream().mapToInt(Integer::parseInt).toArray());

									detail.add(detailMap);
								}
							}	
							detailChart.put("list", detail);		//목록
							data.put("chapter", detailChart);		//단원							
						}
						setResult(msgKey, data);
					} else {
						if(!vu1.isValid()) {
							setResult(msgKey, vu1.getResult());
						}else if(!vu2.isValid()) {
							setResult(msgKey, vu2.getResult());
						}
					}
				} else {
					setResult(msgKey, vu.getResult());
				}				    		
			}
			
		} else {
			setResult(msgKey, vu.getResult());
		}
	
    	return result;
    }

    /**
	 * HAMS-ORG-ED-002
	 * 평가분석 상세 - 단원별 연계학습
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
    @Override    
    public Map getChapterLrn(Map<String, Object> paramMap) throws Exception {
//		priorLrn 선수학습
//		chapterNm
//		examCrtRt
//		examDT
//	currentLrn 해당단원
//		chapterNm
//		examCrtRt
//		examDT

//		data.put("followUpLrn", detail);		//후속학습
//		data.put("supplementaryLrn", detail);	//보충학습
    	return result;
    }
    
    @Override
    public Map getCommMsgCd(Map<String, Object> paramMap) throws Exception {
    	
    	v_param.put("METHOD", "COMMMSGCD");
    	
        Map<String,Object> data = new HashMap<>();
        checkRequired(paramMap);

        //DB 조회
        //List<Map<String,Object>> commMsgCd = (List<Map<String,Object>>)mapper_1_0.getList(paramMap, dw_mapper_namespace + ".getCommMsgCd");
        List<Map<String,Object>> commMsgCd = (List)getMapperResultData(v_param, "list", paramMap, ".getCommMsgCd");
        LinkedHashMap<String,Object> commMsgCdMap = new LinkedHashMap<>();

        for(Map item : commMsgCd) {
            commMsgCdMap.put(item.get("msgCd").toString(),item.get("msg"));
        }

        data.put("commMsgCd",commMsgCdMap);
        setResult(dataKey,data);

        return result;
    }
    
    //p,startDt,endDt 비교 메서드
    private void checkRequired(Map<String,Object> params) throws Exception {
        ValidationUtilTutor vu = new ValidationUtilTutor();
        //필수값 체크
        vu.checkRequired(new String[] {"p"},params);

        getStudId(params);
    }

    //p,startDt,endDt 비교 메서드
    private void checkRequiredWithDt(Map<String,Object> params) throws Exception {
        ValidationUtilTutor vu = new ValidationUtilTutor();
        //1.필수값 체크
        vu.checkRequired(new String[] {"p","startDt","endDt"},params);

		//2. 유효성 체크
        vu.isDate("startDt",(String)params.get("startDt"));
        vu.isDate("endDt",(String)params.get("endDt"));

        //복호화
        String[] encodedArr = getDecodedParam(params.get("s").toString());
        String encodedStudId = encodedArr[1];

        //DB params
        params.put("studId",encodedStudId);
    }

    /***
     * 파라미터에서 studId 추출
     * @param params
     * @throws Exception
     */
	private void getStudId(Map<String, Object> params) throws Exception {
		//복호화
        try {
        	CipherUtil cp = CipherUtil.getInstance();
        	String decodedStr = cp.AES_Decode(params.get("s").toString());

            //DB params
            params.put("studId",decodedStr);
        } catch (Exception e) {
            LOGGER.debug("s Parameter Incorrect");
        }
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
     * @param linkedHashMap 
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
//        else if(resultNullCheck((Map)data)) {
//            throw new NoDataException(new Object[] {key,"null",ValidationCode.NO_DATA});
//        }
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
    
    /**
     * item value 값 비교
     * @param item
     * @param value
     * @return
     */
	private Object getItemValueCompare(Object item, Object value) {
		if(item == null) {
			return null;
		} else {
			if(value instanceof String) {
				return item.toString().equals(value);
			} else if(value instanceof Integer) {
				return Integer.valueOf(item.toString()).equals(value);
			} else {
				return item.equals(value);
			}
		}
	}
	

	/**
	 * item value Integer 값으로 변경
	 * @param item
	 * @param defaultValue
	 * @return
	 */
	private Object getConvertInteger(Object item, Object defaultValue) {
		try {
			if(item == null) {
				return defaultValue;
			} else {
				if(item instanceof String) {
					return Integer.valueOf(item.toString());
				} else if(item instanceof Integer) {
					return item;
				} else {
					return item;
				}
			}
		} catch (Exception e) {
			LOGGER.debug("[getConvertInteger]", e.getMessage());
			return defaultValue;
		}
	}


}
