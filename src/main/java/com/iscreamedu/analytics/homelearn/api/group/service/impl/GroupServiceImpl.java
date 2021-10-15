package com.iscreamedu.analytics.homelearn.api.group.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
		
		try {
			v_result = getDWVersion(param);
	    	
	    	dw_mapper_namespace = MAPPER_NAMESPACE + v_result.get("DW_VERSION").toString();    	
	    	mapper_name = v_result.get("MAPPER_NAME").toString();    	
	    	
			if (mapper_name == "CommonMapperTutor") {
				if(sqlRequestType.toLowerCase() == "list") {
					return mapper_1_0.getList(paramMap, dw_mapper_namespace + sqlId);
				} else {
					return mapper_1_0.get(paramMap, dw_mapper_namespace + sqlId);
				}
			} else if (mapper_name == "CommonMapperTutorTemp") {
				//mapper_1_5
				if(sqlRequestType.toLowerCase() == "list") {
					return mapper_1_5.getList(paramMap, dw_mapper_namespace + sqlId);
				} else {
					return mapper_1_5.get(paramMap, dw_mapper_namespace + sqlId);
				}
			} else {
				//mapper_2_0
				if(sqlRequestType.toLowerCase() == "list") {
					return mapper_2_0.getList(paramMap, dw_mapper_namespace + sqlId);
				} else {
					return mapper_2_0.get(paramMap, dw_mapper_namespace + sqlId);
				}
			}
		} catch (Exception e) {
			
			ValidationUtil vu = new ValidationUtil();
			vu.setError("db mapper result data fail");
			Map<String, Object> tempResult = vu.getResult();
			tempResult.put("error", e.getStackTrace());
			LOGGER.debug(tempResult.toString());
//			e.setStackTrace(null);
			return tempResult; 
		}
	}
	
    @Override
    public Map getPeriod(Map<String, Object> paramMap) throws Exception {
    	
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "PERIOD");
    	
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		ValidationUtil vu1 = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"svcOpenDe"}, paramMap);
		
		if(vu.isValid()) { 		
			//2. 유효성 체크
			vu1.isDate("svcOpenDe", paramMap.get("svcOpenDe").toString());

			if(vu1.isValid()) {
				
				if(!paramMap.containsKey("currCon") || paramMap.get("currCon").equals("")) {	// 주간+월간 합산
					Map<String,Object> data = new HashMap<>();
					data.put("weeks", (List)getMapperResultData(v_param, "list", paramMap, ".selectPeriodWeeks"));
					data.put("months", (List)getMapperResultData(v_param, "list", paramMap, ".selectPeriodMonths"));
					setResult(dataKey, data);
				} else {

					String currConCheck = paramMap.get("currCon").toString().toLowerCase();
					paramMap.put("currConCheck", currConCheck);
					if(currConCheck.equals("m")) {
						setResult(dataKey, (List)getMapperResultData(v_param, "list", paramMap, ".selectPeriodMonths"));
					} else {
						setResult(dataKey, (List)getMapperResultData(v_param, "list", paramMap, ".selectPeriodWeeks"));							
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
			setResult(dataKey, getMapperResultData(v_param, "", paramMap, ".selectStud"));
		} else {
			setResult(msgKey, vu.getResult());
		}
		
    	return result;
    }

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
			Map<String,Object> data = new HashMap<>();
            String startDate;
			String endDate;
			
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {	// 월간
				//1-1.필수값 체크 
				vu1.checkRequired(new String[] {"yymm"}, paramMap);
				
				if(vu1.isValid()) { 	
					String yymm = paramMap.get("yymm").toString();

					//2. 유효성 체크
					vu2.isYearMonth("yymm", yymm);
					if(vu2.isValid()) {
						startDate = yymm.substring(0,4)+"-"+yymm.substring(4,6)+"-01";
						endDate = yymm.substring(0,4)+"-"+yymm.substring(4,6)+"-"+getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd"));
						paramMap.put("startDt", startDate);
						paramMap.put("endDt", endDate);
						List resultMap = (List)getMapperResultData(v_param, "list", paramMap, ".selectLrnBasicMonthly");
			    		HashMap<String,Object> current = (HashMap<String,Object>)(resultMap.get(0));
			    		String lrnSignal = null;
			    		if(current.size() > 0) {
			    			if(current.containsKey("lrnSignal")) {
			    				lrnSignal = current.get("lrnSignal").toString();
			    				current.remove("lrnSignal");
			    			}
				    		data.put("current", current);
			    		
			                startDate = subDate(startDate,-1,false,false);
				        	endDate = subDate(endDate,-1,false,true);
			                paramMap.put("endDt",endDate);
			                paramMap.put("startDt",startDate);
			                paramMap.put("yymm", startDate.substring(0,4)+startDate.substring(5,7));
			                
				        	data.put("prevDtCnt", getCalendarLastDay(endDate, new SimpleDateFormat("yyyy-MM-dd")));
				        	
				        	resultMap = null;
				        	resultMap = (List)getMapperResultData(v_param, "list", paramMap, ".selectLrnBasicMonthly");
				    		HashMap<String,Object> prev = (HashMap<String,Object>)(resultMap.get(0));
				    		prev.remove("lrnSignal");
				        	data.put("prev", prev);
				        	
				        	data.put("lrnSignal", lrnSignal);
				            data.put("msg",null);	//추후 메시지기획안 적용 예정	     
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
						List resultMap = (List)getMapperResultData(v_param, "list", paramMap, ".selectLrnBasicPeriod");
			    		HashMap<String,Object> current = (HashMap<String,Object>)(resultMap.get(0));
			    		String lrnSignal = null;
			    		if(current.size() > 0) {
			    			if(current.containsKey("lrnSignal")) {
			    				lrnSignal = current.get("lrnSignal").toString();
			    				current.remove("lrnSignal");
			    			}
				    		data.put("current", current);
			    		
			                startDate = subDate(startDate,-7,true,false);
				        	endDate = subDate(endDate,-7,true,false);
			                paramMap.put("endDt",endDate);
			                paramMap.put("startDt",startDate);
			                
			                data.put("prevDtCnt", "7");
				        	
				        	resultMap = null;
				        	resultMap = (List)getMapperResultData(v_param, "list", paramMap, ".selectLrnBasicPeriod");
				    		HashMap<String,Object> prev = (HashMap<String,Object>)(resultMap.get(0));
				    		prev.remove("lrnSignal");
				        	data.put("prev", prev);
				        	
				        	data.put("lrnSignal", lrnSignal);
				            data.put("msg",null);	//추후 메시지기획안 적용 예정	     
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

	private int getCalendarLastDay(String endDate, DateFormat transFormat) {
		try {
			Calendar dt = Calendar.getInstance();
			dt.setTime(transFormat.parse(endDate));
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

    @Override
    public Map getLrnHabitChart(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override
    public Map getAiRecommendLrn(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override
    public Map getDiagnsticEvalStt(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override
    public Map getAttRtStt(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override
    public Map getLrnTmList(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getAttCntStt(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getloginPtnStt(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getExRtStt(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getFnshLrnExStt(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getLrnExSttCompareSub(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getALrnExStt(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getCrtRtStt(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getIncrtNoteNcStt(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getCrtQuesCntStt(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getSlvHabitStt(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getDayAvgLrnStt (Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getTotalLrnTmStt(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getLongLrnTmStt(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getSubjExam(Map<String, Object> paramMap) throws Exception {
    	
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "SUBJEXAM");

		getStudId(paramMap);
		
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"currCon","yyyy","mm","studId"}, paramMap);
		
		if(vu.isValid()) {
			Map<String, Object> data = new HashMap<>();
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {
				int mm = Integer.valueOf(paramMap.get("mm").toString());
				String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
				
				paramMap.put("convertMm", convertMm);
				
				data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getSubjExam");
			}else {
				data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getSubjExam");
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
		//1.필수값 체크
		vu.checkRequired(new String[] {"currCon","yyyy","mm","studId"}, paramMap);
		
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
				int mm = Integer.valueOf(paramMap.get("mm").toString());
				String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
				
				paramMap.put("convertMm", convertMm);
				
				dataMap = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getCompareSub");
				
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
				
			}else {
				dataMap = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getCompareSub");
				
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
		//1.필수값 체크
		vu.checkRequired(new String[] {"currCon","yyyy","mm","studId"}, paramMap);
		
		if(vu.isValid()) {
			ArrayList<Map<String,Object>> data = new ArrayList<>();
			
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {
				int mm = Integer.valueOf(paramMap.get("mm").toString());
				String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
				
				paramMap.put("convertMm", convertMm);
				
				data = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getExamChart");
				
			}else {
				data = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getExamChart");
				
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
		//1.필수값 체크
		vu.checkRequired(new String[] {"currCon","yyyy","mm","studId"}, paramMap);
		
		if(vu.isValid()) {
			Map<String,Object> data = new LinkedHashMap<>();
			Map<String,Object> dataMap = new LinkedHashMap<>();
			ArrayList<Map<String,Object>> subjExamList = new ArrayList<>();
			
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			paramMap.put("currConCheck", currConCheck);
			
			data.put("examTypes", getMapperResultData(v_param, "list", paramMap, ".getExamCdList"));
			data.put("analysisTypes", getMapperResultData(v_param, "list", paramMap, ".getQuesCdList"));
			
			if(currConCheck.equals("m")) {
				int mm = Integer.valueOf(paramMap.get("mm").toString());
				String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
				
				paramMap.put("convertMm", convertMm);
				
				subjExamList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getSubjExamList");
				
				dataMap.put("totalCnt", getMapperResultData(v_param, "", paramMap, ".getSubjExamListCnt"));
				dataMap.put("list", subjExamList);
				
				data.put("examList", dataMap);				
			}else {
				subjExamList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getSubjExamList");
				
				dataMap.put("totalCnt", getMapperResultData(v_param, "", paramMap, ".getSubjExamListCnt"));
				dataMap.put("list", subjExamList);
				
				data.put("examList", dataMap);
				
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
		//1.필수값 체크
		vu.checkRequired(new String[] {"currCon","yyyy","mm","studId"}, paramMap);
		
		if(vu.isValid()) {
			Map<String,Object> data = new LinkedHashMap<>();
			Map<String,Object> dataMap = new LinkedHashMap<>();
			ArrayList<Map<String,Object>> subjExamList = new ArrayList<>();
			
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			paramMap.put("currConCheck", currConCheck);
			
			data.put("examTypes", getMapperResultData(v_param, "", paramMap, ".getIncrtNote"));
			
			if(currConCheck.equals("m")) {
				int mm = Integer.valueOf(paramMap.get("mm").toString());
				String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
				
				paramMap.put("convertMm", convertMm);
				
				data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getIncrtNote");
				
				data.put("pageCnt", 0);
				data.put("list", null);		
			}else {
				data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getIncrtNote");
				
				data.put("pageCnt", 0);
				data.put("list", null);
				
			}
			setResult(dataKey, data);
		} else {
			setResult(msgKey, vu.getResult());
		}
    	
    	return result;
    }

    @Override    
    public Map getChapterStt(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getChapterLrn(Map<String, Object> paramMap) throws Exception {
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
            throw new NoDataException(new Object[] {key,"null",ValidationCode.NO_DATA});
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

}
