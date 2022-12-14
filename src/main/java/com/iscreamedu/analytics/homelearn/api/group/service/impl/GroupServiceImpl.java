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
import com.iscreamedu.analytics.homelearn.api.common.util.CommonUtil;

@Service
public class GroupServiceImpl implements GroupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupServiceImpl.class);
    private static final String MAPPER_NAMESPACE = "AiReport_";
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
//    CommonMapperTutor_1_5 mapper_1_5;	//?????? ??????
    CommonMapperTutor mapper_1_5;
	@Autowired
//    CommonMapperTutor_2.0 mapper_2_0;	//?????? ??????
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
	 * ????????? mapper??? ?????? data ?????? ??????
	 * @param param ?????? ?????? ????????????
	 * @param sqlRequestType ?????? ??????
	 * @param paramMap ?????? ???????????? 
	 * @param sqlId ????????? SQL ?????? id
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
	 * ??????, ?????? ?????? - ????????????
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
		//1.????????? ??????
		vu.checkRequired(new String[] {"lrnStartDt"}, paramMap);
		
		if(vu.isValid()) { 		
			//2. ????????? ??????
			vu1.isDate("lrnStartDt", paramMap.get("lrnStartDt").toString());

			if(vu1.isValid()) {
				
				if(!paramMap.containsKey("currCon") || paramMap.get("currCon").equals("")) {	// ??????+?????? ??????
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
	 * ???????????? ?????? - ?????? ??????
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
		//1.????????? ??????
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
	 * ???????????? ?????? - ???????????? ????????????
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
		//1.????????? ??????
		vu.checkRequired(new String[] {"currCon","studId"}, paramMap);
		if(vu.isValid()) { 		
			Map<String,Object> cData = new HashMap<>();
			Map<String,Object> data = new HashMap<>();
            String startDate;
			String endDate;
			
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			ArrayList<String> positive = new ArrayList<>();
			ArrayList<String> negative = new ArrayList<>();
			Map<String,Object> msg = new HashMap<>();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {	// ??????
				//1-1.????????? ?????? 
				vu1.checkRequired(new String[] {"yyyy","mm"}, paramMap);
				
				if(vu1.isValid()) { 	
					String yyyy = paramMap.get("yyyy").toString();
					int mm = Integer.valueOf(paramMap.get("mm").toString());
					String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
					String yymm = yyyy + convertMm;

					paramMap.put("yymm", yymm);
					//2. ????????? ??????
					vu2.isYearMonth("yyyy, mm", yymm);
					if(vu2.isValid()) {
						startDate = yyyy + "-" + convertMm + "-01";
						endDate = yyyy + "-" + convertMm + "-" + CommonUtil.getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd"));
						paramMap.put("startDt", startDate);
						paramMap.put("endDt", endDate);
						cData = (Map<String, Object>)getMapperResultData(v_param, "", paramMap, ".getLrnBasicMonthly");
						if(cData != null) {
							//current ?????? prev ????????? 
			                data.put("prevDtCnt", CommonUtil.getCalendarLastDay(subDate(startDate,-1,false,false), new SimpleDateFormat("yyyy-MM-dd")));
				        }
						else {
							setResult(dataKey, data);
						}
					} else {
						setResult(msgKey, vu2.getResult());				
					}
				} else {
					setResult(msgKey, vu1.getResult());
				}
	        } else {	// ?????? & ??????
	        	//1-1.????????? ??????
				vu.checkRequired(new String[] {"startDt","endDt"}, paramMap);
				
				if(vu.isValid()) { 	
					startDate = paramMap.get("startDt").toString();
					endDate = paramMap.get("endDt").toString();
					
					//2. ????????? ??????
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					
					if(vu1.isValid() && vu2.isValid()) {
						long limitDtCnt = CommonUtil.getCalendarDiff(startDate, endDate, "DAY") + 1;
						if(limitDtCnt < 7) paramMap.put("notSevenDayCheck", true);
						
						paramMap.put("yymm", startDate.substring(0,4)+startDate.substring(5,7));
						cData = (Map<String, Object>)getMapperResultData(v_param, "", paramMap, ".getLrnBasicPeriod");
						if(cData != null) {
							data.put("prevDtCnt", limitDtCnt);
						}
						else {
							setResult(dataKey, data);
						}
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
			
			if(cData != null) {
				ArrayList<Integer> msgNoList = new ArrayList<Integer>();
				Map<String, Object> msgRequestMap = new LinkedHashMap<>();
				msgRequestMap.put("version", "1.0");
				msgRequestMap.put("sheet", "C");
				ArrayList<Map<String,Object>> msgTemplate = new ArrayList<>();
				msgTemplate = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", msgRequestMap, ".getCommMsgTemplate");
	            	
				getLrnBasicResult(cData, data, positive, negative, msg, msgTemplate);		            
	            setResult(dataKey, data);
			}
		} else {
			setResult(msgKey, vu.getResult());
		}
		
    	return result;
    }

	private void getLrnBasicResult(Map<String, Object> cData, Map<String, Object> data, ArrayList<String> positive,
		ArrayList<String> negative, Map<String, Object> msg, ArrayList<Map<String,Object>> msgTemplate) {
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

		int[] positive_msgNo = {13, 23} ;	// ??????-????????????, ??????
		int[] negative_msgNo = {34, 233};	// ??????-????????????, ??????
		int a_lrn_ex_cnt = cData.get("aLrnExCnt") == null ? 0 : Integer.valueOf(cData.get("aLrnExCnt").toString());			// ??????????????? ?????? ???
		String subj_nm_a_lrn = cData.get("subjNmALrn") == null ? "" : cData.get("subjNmALrn").toString();					// ??????????????? ?????? ?????? ?????? ?????? ???????????????
		int att_cnt = cData.get("attCnt") == null ? 0 : Integer.valueOf(cData.get("attCnt").toString());					// ?????? ??????
		int att_rt = cData.get("attRt") == null ? 0 : Integer.valueOf(cData.get("attRt").toString());						// ?????? ??????(????????? ?????? ??? ????????? ??? ???)
		int d_lrn_ex_cnt = cData.get("planDLrnExCnt") == null ? 0 : Integer.valueOf(cData.get("planDLrnExCnt").toString());	// ????????? ????????? ??????
		int ex_rt = 0;																										// ????????? ( !! null ??? ?????? !!)
		int crt_rt = cData.get("crtRt") == null ? 0 : Integer.valueOf(cData.get("crtRt").toString());						// ?????????
		int expl_cnt = cData.get("explCnt") == null ? 0 : Integer.valueOf(cData.get("explCnt").toString());					// ????????? ????????? ??????	
		int ps_expl_cnt = cData.get("psExplCnt") == null ? 0 : Integer.valueOf(cData.get("psExplCnt").toString());			// 100?????? ????????? ??????
		int guess_ques_cnt = cData.get("guessQuesCnt") == null ? 0 : Integer.valueOf(cData.get("guessQuesCnt").toString());			// ?????? ????????? ????????? ?????? ???
		int incrt_nt_cnt = cData.get("incrtNtCnt") == null ? 0 : Integer.valueOf(cData.get("incrtNtCnt").toString());		// ???????????? ????????? ??????
		int imprv_slv_habit_cnt = cData.get("imprvSlvHabitCnt") == null ? 0 : Integer.valueOf(cData.get("imprvSlvHabitCnt").toString());		// ????????? ??? ???????????? ?????? ??????
		int skip_ques_cnt = cData.get("skipQuestCnt") == null ? 0 : Integer.valueOf(cData.get("skipQuestCnt").toString());	//  ????????? ?????? ???
		int cm_habit_cnt = cData.get("cmHabitCnt") == null ? 0 : Integer.valueOf(cData.get("cmHabitCnt").toString());		//  ????????? ?????? ?????? ?????? ??? + ????????? ?????? ??? 
		try {
			// ?????????-????????????
		    if(cData.get("exRt") == null) {
		    	if(a_lrn_ex_cnt > 0) {
		        	// ?????? ???????????? - 10. ????????? null && ??????????????? ?????? ??? c > 0 && ??????????????? ?????? ?????? ?????? ?????? ??????????????? d
		        	positive_msgNo[0] = 12;
		    	}
		    } else {
				ex_rt =  Integer.valueOf(cData.get("exRt").toString());	// ?????????
		    	if(ex_rt >= 90) {				// ????????? 90 <= a <= 100
		    		if(att_rt < 10) {
		    			// ?????? ???????????? - 1. ????????? 90 <= a <= 100 && ????????? ????????? ?????? b < 10
		    			positive_msgNo[0] = 3;
					}
					else if(att_rt >= 10) {
						// ?????? ???????????? - 2. ????????? 90 <= a <= 100 && ????????? ????????? ?????? b >= 10
						positive_msgNo[0] = 4;
					}
		    	}
		    	else if(0 < ex_rt && ex_rt < 90) {	// ????????? 0 < a < 90
		    		if(att_rt >= 80) {
		    			if(a_lrn_ex_cnt > 0) {
		    				// ?????? ???????????? - 3. ????????? b >= 80 && ??????????????? ?????? ??? c > 0 && ??????????????? ?????? ?????? ?????? ?????? ??????????????? d
		    				positive_msgNo[0] = 5;
		    			} else if(a_lrn_ex_cnt == 0) {
		        			// ?????? ???????????? - 4. ????????? b >= 80 && ??????????????? ?????? ??? c = 0
		    				positive_msgNo[0] = 6;
		    			}
		    		} else {
		    			if(a_lrn_ex_cnt > 0) {
		    				// ?????? ???????????? - 5. ????????? b < 80 && ??????????????? ?????? ??? c > 0, ??????????????? ?????? ?????? ?????? ?????? ??????????????? d
		    				positive_msgNo[0] = 7;
		    			} else if(a_lrn_ex_cnt == 0) {
		    				// ?????? ???????????? - 6. ????????? b < 80 && ??????????????? ?????? ??? c = 0
		    				positive_msgNo[0] = 8;
		    			}
		    		}
		    	}
		    	else if(ex_rt == 0) { 	// ????????? a = 0 
		    		if(att_rt >= 80) {
		        		if(a_lrn_ex_cnt > 0) {
		        			// ?????? ???????????? - 7. ????????? b >= 80 && ??????????????? ?????? ??? c > 0 && ??????????????? ?????? ?????? ?????? ?????? ??????????????? d
		        			positive_msgNo[0] = 9;
		        		} else if(a_lrn_ex_cnt == 0) {
		        			// ?????? ???????????? - 8. ????????? b >= 80 && ??????????????? ?????? ??? c = 0
		        			positive_msgNo[0] = 10;
		        		}
		    		} else {
		    			if(a_lrn_ex_cnt > 0) {
		    				// ?????? ???????????? - 9. ????????? b < 80 && ??????????????? ?????? ??? c > 0 && ??????????????? ?????? ?????? ?????? ?????? ??????????????? d
		    				positive_msgNo[0] = 11;
		    			}
		    		}
		    	}
			
		    }
		    // ?????????-??????
		    if(expl_cnt >= 2) {	// ????????? ????????? ?????? a >= 2 
		    	if(expl_cnt > ps_expl_cnt && ps_expl_cnt >= 1) {	// 100?????? ????????? ?????? b,   	a > b >= 1   
		    		if(incrt_nt_cnt > 0) {
		        		// ?????? ???????????? - 3. ????????? ????????? ?????? a >= 2 
		        		//				&& 100?????? ????????? ?????? b,   	a > b >= 1   
		        		// 				&& ???????????? ????????? ?????? c > 0
		            	positive_msgNo[1] = 16;
		    		} else if(incrt_nt_cnt == 0) {
		        		// ?????? ???????????? - 4. ????????? ????????? ?????? a >= 2 
		        		//				&& 100?????? ????????? ?????? b,   	a > b >= 1   
		        		// 				&& ???????????? ????????? ?????? c = 0
		    			positive_msgNo[1] = 17;
		    		}
		    	}	                	
		    } else if(expl_cnt >= 1) {	                	
		    	if(expl_cnt == ps_expl_cnt) { // ????????? ????????? ?????? a >= 1 && ?????? ???????????? ????????? 100?????? ??????
		    		if(guess_ques_cnt > 0) {
		    			// ?????? ???????????? - 1. ????????? ????????? ?????? a >= 1 && ?????? ???????????? ????????? 100?????? ?????? && ?????? ????????? ????????? ?????? ??? c > 0
		    			positive_msgNo[1] = 14;
		    		} else if(guess_ques_cnt == 0) {
		    			// ?????? ???????????? - 2. ?????? ????????? ????????? ?????? ??? c = 0
		    			positive_msgNo[1] = 16;
		    		}
		    	}
		    	else {
		    		if(ps_expl_cnt == 0) {	// 100?????? ????????? b= 0??? 
		    			if(crt_rt >= 80) {	// ?????? ????????? f >= 80
		    				// ?????? ???????????? - 5. ????????? ????????? ?????? a >= 1 && 100?????? ????????? b= 0??? 
		    				//	&& ?????? ????????? f >= 80
		    				positive_msgNo[1] = 18;
		    			} else if(crt_rt >= 50) {	// ?????? ????????? f >= 50 
		    				if(incrt_nt_cnt == 0) {
		    					// ?????? ???????????? - 6. ????????? ????????? ?????? a >= 1 && 100?????? ????????? b= 0??? 
		    					// 	&& ?????? ????????? f >= 50 && ???????????? ????????? ?????? d = 0
		        				positive_msgNo[1] = 19;
		    				} else if(incrt_nt_cnt > 0 && imprv_slv_habit_cnt <= 5) {
		    					// ?????? ???????????? - 7. ????????? ????????? ?????? a >= 1 && 100?????? ????????? b= 0??? 
		    					// 	&& ?????? ????????? f >= 50 && ???????????? ????????? ?????? d > 0 && ????????? ??? ???????????? ?????? ?????? e <= 5
		    					positive_msgNo[1] = 20;
		    				}
		    			} else {	// ?????? ????????? f < 50
		    				if(incrt_nt_cnt == 0) {
		    					// ?????? ???????????? - 8. ????????? ????????? ?????? a >= 1 && 100?????? ????????? b= 0??? 
		    					// 	&& ?????? ????????? f < 50 && ???????????? ????????? ?????? d = 0
		        				positive_msgNo[1] = 21;
		    				} else if(incrt_nt_cnt > 0 && imprv_slv_habit_cnt <= 5) {
		    					// ?????? ???????????? - 9. ????????? ????????? ?????? a >= 1 && 100?????? ????????? b= 0??? 
		    					// 	&& ?????? ????????? f < 50 && ???????????? ????????? ?????? d > 0 && ????????? ??? ???????????? ?????? ?????? e <= 5
		    					positive_msgNo[1] = 22;
		    				}
		    				
		    			}
		    		}
		    	}
		    } 
		    
		    // ?????????-????????????
		    if(att_cnt == 0) {	// ?????? ???????????? - 1. ?????? ?????? 0???
		    	negative_msgNo[0] = 24 ;
		    } else {	// ?????? ?????? 1??? ??????
		    	if(cData.get("exRt") == null) {	// ?????? ???????????? - 2. ????????? = null				                	
		    		negative_msgNo[0] = 25;
		    	} else {
		    		ex_rt = Integer.valueOf(cData.get("exRt").toString());	// ?????????
		        	if(ex_rt == 0) {
		    			if(a_lrn_ex_cnt == 0) {
		    				// ?????? ???????????? - 3. ????????? a = 0 && ??????????????? ??? b = 0
		    				negative_msgNo[0] = 26;
		    			} else if(a_lrn_ex_cnt > 0) {
		            		// ?????? ???????????? - 4. ????????? a = 0 && ??????????????? ??? b > 0
		    				negative_msgNo[0] = 27;
		    			}
		    		}
		    		else if(0 < ex_rt && ex_rt < 30) {
		    			// ?????? ???????????? - 5. ????????? 0 < a < 30
						negative_msgNo[0] = 28;
		    		}
		    		else if(30 <= ex_rt && ex_rt < 60) {				                		
						if(d_lrn_ex_cnt >= 10) {
							// ?????? ???????????? - 6. ????????? 30 <= a < 60 && ????????? ????????? ?????? c >= 10
							negative_msgNo[0] = 29;
						} else {
							// ?????? ???????????? - 7. ????????? 30 <= a < 60 && ????????? ????????? ?????? c < 10
							negative_msgNo[0] = 30;
						}				                		
		    		}
		    		else if(60 <= ex_rt && ex_rt < 100) {			// ????????? 60 <= a < 100
		    			if(d_lrn_ex_cnt >= 10) {
							// ?????? ???????????? - 8. ????????? 60 <= a < 100 &&  ????????? ????????? ?????? c >= 10
		        			negative_msgNo[0] = 31;
						} else {
							// ?????? ???????????? - 9. ????????? 60 <= a < 100 &&  ????????? ????????? ?????? c < 10
							negative_msgNo[0] = 32;
						}	
		    		} else if(ex_rt == 100){
		    			// ?????? ???????????? - 10. ????????? 100 = a
		    			negative_msgNo[0] = 33;
		    		}
		    	}
		    }
		    
		    // ?????????-??????
		    if(expl_cnt == 0) {
		    	// ?????? ???????????? - 1. ?????? ????????? ?????? ??????
		    	negative_msgNo[1] = 35;
		    } else if(expl_cnt >= 1) {	
		    	if(crt_rt < 100) {
		    		if(ps_expl_cnt == 0) {
		        		if(incrt_nt_cnt > 0) {
		                	// ?????? ???????????? - 2. ????????? ????????? ?????? a >= 1
		                	// && ????????? ????????? b < 100 && 100?????? ????????? ?????? c = 0
		            		// && ???????????? ????????? ?????? d > 0
		        			negative_msgNo[1] = 36;
		        		} else if(incrt_nt_cnt == 0) {
		        			if(imprv_slv_habit_cnt > 5) {
		        				if(skip_ques_cnt > 1) {
		            				// ?????? ???????????? - 3. ????????? ????????? ?????? a >= 1
				                	// && ????????? ????????? b < 100 && 100?????? ????????? ?????? c = 0
			                		// && ???????????? ????????? ?????? d = 0 && ????????? ??? ???????????? ?????? ?????? e > 5
		            				// && ????????? ?????? ??? f > 1	 
		                			negative_msgNo[1] = 37; 
		        				} else if(cm_habit_cnt > 1) {
		            				// ?????? ???????????? - 4. ????????? ????????? ?????? a >= 1
				                	// && ????????? ????????? b < 100 && 100?????? ????????? ?????? c = 0
			                		// && ???????????? ????????? ?????? d = 0 && ????????? ??? ???????????? ?????? ?????? e > 5
		            				// && ????????? ?????? ?????? ?????? ??? f + ????????? ?????? ??? g, f + g > 1
		                			negative_msgNo[1] = 38; 
		        				} else if(guess_ques_cnt > 1) {
		            				// ?????? ???????????? - 5. ????????? ????????? ?????? a >= 1
				                	// && ????????? ????????? b < 100 && 100?????? ????????? ?????? c = 0
			                		// && ???????????? ????????? ?????? d = 0 && ????????? ??? ???????????? ?????? ?????? e > 5
		            				// && ?????? ????????? ????????? ?????? ??? f > 1
		                			negative_msgNo[1] = 39; 
		        				}	
		        			}
		        		}
		        	} else if(ps_expl_cnt > 0) {
		        		if(incrt_nt_cnt > 0) {
		                	// ?????? ???????????? - 6. ????????? ????????? ?????? a >= 1
		                	// && ????????? ????????? b < 100 && 100?????? ????????? ?????? c > 0
		            		// && ???????????? ????????? ?????? d > 0
		        			negative_msgNo[1] = 228; 
		        		} else if(incrt_nt_cnt == 0 && imprv_slv_habit_cnt > 5) {
		        			if(skip_ques_cnt > 1) {
			                	// ?????? ???????????? - 7. ????????? ????????? ?????? a >= 1
			                	// && ????????? ????????? b < 100 && 100?????? ????????? ?????? c > 0
		                		// && ???????????? ????????? ?????? d = 0 && ????????? ??? ???????????? ?????? ?????? e > 5
		            			// && ????????? ?????? ??? f > 1
		            			negative_msgNo[1] = 229; 
		        			} else if(cm_habit_cnt > 1) {		                			
			                	// ?????? ???????????? - 8. ????????? ????????? ?????? a >= 1
			                	// && ????????? ????????? b < 100 && 100?????? ????????? ?????? c > 0
		                		// && ???????????? ????????? ?????? d = 0 && ????????? ??? ???????????? ?????? ?????? e > 5
		            			// && ????????? ?????? ?????? ?????? ??? f + ????????? ?????? ??? g, f + g > 1
		            			negative_msgNo[1] = 230; 
		        			} else if(guess_ques_cnt > 1) {
			                	// ?????? ???????????? - 9. ????????? ????????? ?????? a >= 1
			                	// && ????????? ????????? b < 100 && 100?????? ????????? ?????? c > 0
		                		// && ???????????? ????????? ?????? d = 0 && ????????? ??? ???????????? ?????? ?????? e > 5
		            			// && ?????? ????????? ????????? ?????? ??? f > 1
		            			negative_msgNo[1] = 231; 
		        			}
		        		}
		        	}
		    	} else if(crt_rt == 100 && guess_ques_cnt > 0) {
		    		// ?????? ???????????? - 10. ????????? ????????? ?????? a >= 1
		    		// && ????????? ????????? b = 100	?????? ????????? ????????? ?????? ??? c > 0
		    		negative_msgNo[1] = 232; 
		    	}
		    }
		    
		} catch (Exception e) {
			LOGGER.debug(v_param.toString(), " :: error :: ", e.getMessage());
		}

		if(msgTemplate.size() > 0 && msgTemplate.get(0) != null) {
			// ????????????
			for(Map<String, Object> item : msgTemplate) {
				if(Integer.valueOf(item.get("msgNo").toString()) == positive_msgNo[0])
				{
					positive.add(item.get("msg").toString().replace("{a}", String.valueOf(ex_rt)).replace("{b}", String.valueOf(att_rt)).replace("{c}", String.valueOf(a_lrn_ex_cnt)).replace("{d}", subj_nm_a_lrn));
				}
				else if(Integer.valueOf(item.get("msgNo").toString()) == negative_msgNo[0])
				{
					negative.add(item.get("msg").toString().replace("{a}", String.valueOf(ex_rt)).replace("{b}", String.valueOf(a_lrn_ex_cnt)).replace("{c}", String.valueOf(d_lrn_ex_cnt)));
				}									
			}
			// ??????
			for(Map<String, Object> item : msgTemplate) {
				if(Integer.valueOf(item.get("msgNo").toString()) == positive_msgNo[1])
				{
					positive.add(item.get("msg").toString().replace("{a}", String.valueOf(expl_cnt)).replace("{b}", String.valueOf(ps_expl_cnt)).replace("{c}", String.valueOf(incrt_nt_cnt)).replace("{d}", String.valueOf(crt_rt)).replace("{e}", String.valueOf(imprv_slv_habit_cnt)));
				}
				else if(Integer.valueOf(item.get("msgNo").toString()) == negative_msgNo[1])
				{
					negative.add(item.get("msg").toString().replace("{a}", String.valueOf(expl_cnt)).replace("{b}", String.valueOf(crt_rt)).replace("{c}", String.valueOf(ps_expl_cnt)).replace("{d}", String.valueOf(incrt_nt_cnt)).replace("{e}", String.valueOf(imprv_slv_habit_cnt)).replace("{f}", String.valueOf(skip_ques_cnt)).replace("{f+g}", String.valueOf(cm_habit_cnt)).replace("{a-c}", String.valueOf(expl_cnt-ps_expl_cnt)).replace("{h}", String.valueOf(guess_ques_cnt)));
				}									
			}
		}
		msg.put("positive", positive);
		msg.put("negative", negative);
		data.put("msg",msg);
	}

	/**
	 * HAMS-ORG-LS-001 
	 * ???????????? ?????? - ?????? ?????? ??????
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
		//1.????????? ??????
		vu.checkRequired(new String[] {"currCon","studId"}, paramMap);
		
		if(vu.isValid()) { 		
			Map<String,Object> data = new HashMap<>();
            String startDate;
			String endDate;
			
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			ArrayList<Map<String,Object>> detailList = new ArrayList<>();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {	// ??????
				//1-1.????????? ?????? 
				vu1.checkRequired(new String[] {"yyyy","mm"}, paramMap);
				
				if(vu1.isValid()) { 	
					String yyyy = paramMap.get("yyyy").toString();
					int mm = Integer.valueOf(paramMap.get("mm").toString());
					String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
					String yymm = yyyy + convertMm;

					paramMap.put("yymm", yymm);
					//2. ????????? ??????
					vu2.isYearMonth("yyyy, mm", yymm);
					if(vu2.isValid()) {
						startDate = yyyy + "-" + convertMm + "-01";
						endDate = yyyy + "-" + convertMm + "-" + CommonUtil.getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd"));
						paramMap.put("startDt", startDate);
						paramMap.put("endDt", endDate);
						paramMap.put("limitDtCnt", CommonUtil.getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd")));
						
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getLrnHabitMonthly");
						if(data != null) {
							detailList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getLrnHabitDetail");
							if(detailList.size() > 0 && detailList.get(0) != null) {								
								data.put("lrnHabitChart", detailList);
							}
							data.remove("studId");
						}
						setResult(msgKey, data);			
					} else {
						setResult(msgKey, vu2.getResult());				
					}
				} else {
					setResult(msgKey, vu1.getResult());
				}
	        } else {	// ?????? & ??????
	        	//1-1.????????? ??????
				vu.checkRequired(new String[] {"startDt","endDt"}, paramMap);
				
				if(vu.isValid()) { 	
					startDate = paramMap.get("startDt").toString();
					endDate = paramMap.get("endDt").toString();
					
					//2. ????????? ??????
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
										
					if(vu1.isValid() && vu2.isValid()) {
						long limitDtCnt = CommonUtil.getCalendarDiff(startDate, endDate, "DAY") + 1;
						paramMap.put("limitDtCnt", limitDtCnt);
						if(limitDtCnt < 7) paramMap.put("notSevenDayCheck", true);
						
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getLrnHabitPeriod");
						if(data != null) {
//							data.remove("studId");
							detailList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getLrnHabitDetail");
							if(detailList.size() > 0 && detailList.get(0) != null) {								
								data.put("lrnHabitChart", detailList);
							}
							data.remove("studId");
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
	 * ???????????? ?????? - AI ?????? ??????
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
		//1.????????? ??????
		vu.checkRequired(new String[] {"studId"}, paramMap);
		if(vu.isValid()) { 		    	
	        //?????? API ??????
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

    private LinkedHashMap<String, Object> createRecommendMap(String thumUrl, String categoryNm, String serviceNm) {
        LinkedHashMap<String,Object> resultMap = new LinkedHashMap<>();

        resultMap.put("thumUrl",thumUrl);
        resultMap.put("categoryNm",categoryNm);
        resultMap.put("serviceNm",serviceNm);

        return  resultMap;
    }

    /**
	 * HAMS-ORG-LD-001 (???????????? ?????? - ????????????/????????????)
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
		//1.????????? ??????
		vu.checkRequired(new String[] {"studId"}, paramMap);
		if(vu.isValid()) { 	   	
			//?????? API ??????
	        paramMap.put("apiName", "inspecion-present");	       	
	        
	        Map<String,Object> resultMap = new LinkedHashMap<>();
	        Map<String,Object> data = (Map<String,Object>) externalAPIservice.callExternalAPI(paramMap).get("data");
	        resultMap.put("studyInspecDivision", data.get("studyInspecDivision"));
	        resultMap.put("parentNurtureInspecDivision", data.get("parentNurtureInspecDivision"));
	        resultMap.put("multiInspecDivision", data.get("multiInspecDivision"));

	        setResult(dataKey, resultMap);			
		} else {
			setResult(msgKey, vu.getResult());
		}

        return result;
    }	
    
    /**
	 * HAMS-ORG-LD-002 (???????????? ?????? - ???????????????)
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
		//1.????????? ??????
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
			
			if(currConCheck.equals("m")) {	// ??????
				//1-1.????????? ?????? 
				vu1.checkRequired(new String[] {"yyyy","mm"}, paramMap);
				
				if(vu1.isValid()) { 	
					String yyyy = paramMap.get("yyyy").toString();
					int mm = Integer.valueOf(paramMap.get("mm").toString());
					String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
					String yymm = yyyy + convertMm;

					paramMap.put("yymm", yymm);
					//2. ????????? ??????
					vu2.isYearMonth("yyyy, mm", yymm);
					if(vu2.isValid()) {
						startDate = yyyy+"-"+convertMm+"-01";
						endDate = yyyy+"-"+convertMm+"-"+ CommonUtil.getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd"));
						paramMap.put("startDt", startDate);
						paramMap.put("endDt", endDate);
						paramMap.put("limitDtCnt", CommonUtil.getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd")));
						
						// ????????? attRt
						// ??????????????? prevAttRt
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getAttRtStt");
						if (data != null ) { 							
							detailList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getAttRtSttDetail");
							if(detailList.size() > 0 && detailList.get(0) != null) {
								for(Map<String, Object> item : detailList) {
									Map<String, Object> chartMap = new LinkedHashMap<>();
									Map<String, Object> detailMap = new LinkedHashMap<>();
									
									chartMap.put("dt", item.get("dt"));
									chartMap.put("attYn", getItemValueCompare(item.get("attYn"), "1"));
									chartMap.put("lrnStt", item.get("lrnStt"));
									chartMap.put("lrnTmYn", getItemValueCompare(item.get("lrnTmYn"), "1"));
									
									detailMap.put("dt", item.get("dt"));
									detailMap.put("lrnFnsh", getItemValueCompare(item.get("lrnFnsh"), "1"));
									detailMap.put("partFnsh", getItemValueCompare(item.get("partFnsh"), "1"));
									detailMap.put("lrnBefore", getItemValueCompare(item.get("lrnBefore"), "1"));
									detailMap.put("att", getItemValueCompare(item.get("attYn"), "1"));
									
									detailChart.add(chartMap);
									detail.add(detailMap);
								}
							}	
							// ?????????????????? dailyLrnStt
							data.put("dailyLrnStt", detailChart);
							// ????????? ?????? attRtMsg
							msg.put("summary", data.get("summary"));
							msg.put("detail", data.get("detail"));
							data.put("attRtMsg", msg);
							data.remove("summary");
							data.remove("detail");
							// ????????? ???????????? 
							data.put("attRtDetail", detail);
						} 
						setResult(msgKey, data);							
					} else {
						setResult(msgKey, vu2.getResult());				
					}
				} else {
					setResult(msgKey, vu1.getResult());
				}
	        } else {	// ?????? & ??????
	        	//1-1.????????? ??????
				vu.checkRequired(new String[] {"startDt","endDt"}, paramMap);
				
				if(vu.isValid()) { 	
					startDate = paramMap.get("startDt").toString();
					endDate = paramMap.get("endDt").toString();
					
					//2. ????????? ??????
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					long limitDtCnt = CommonUtil.getCalendarDiff(startDate, endDate, "DAY") + 1;
					paramMap.put("limitDtCnt", limitDtCnt);
					if(limitDtCnt < 7) paramMap.put("notSevenDayCheck", true);
					
					if(vu1.isValid() && vu2.isValid()) {
						// ????????? attRt
						// ??????????????? prevAttRt
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getAttRtStt");
						if (data != null ) { 							
							detailList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getAttRtSttDetail");
							if(detailList.size() > 0 && detailList.get(0) != null) {
								for(Map<String, Object> item : detailList) {
									Map<String, Object> chartMap = new LinkedHashMap<>();
									Map<String, Object> detailMap = new LinkedHashMap<>();
									
									chartMap.put("dt", item.get("dt"));
									chartMap.put("attYn", getItemValueCompare(item.get("attYn"), "1"));
									chartMap.put("lrnStt", item.get("lrnStt"));
									chartMap.put("lrnTmYn", getItemValueCompare(item.get("lrnTmYn"), "1"));
									
									detailMap.put("dt", item.get("dt"));
									detailMap.put("lrnFnsh", getItemValueCompare(item.get("lrnFnsh"), "1"));
									detailMap.put("partFnsh", getItemValueCompare(item.get("partFnsh"), "1"));
									detailMap.put("lrnBefore", getItemValueCompare(item.get("lrnBefore"), "1"));
									detailMap.put("att", getItemValueCompare(item.get("attYn"), "1"));
																		
									detailChart.add(chartMap);
									detail.add(detailMap);
								}
							}	
							// ?????????????????? dailyLrnStt
							data.put("dailyLrnStt", detailChart);
							
							// ????????? ?????? attRtMsg
							msg.put("summary", data.get("summary"));
							msg.put("detail", data.get("detail"));
							data.put("attRtMsg", msg);
							data.remove("summary");
							data.remove("detail");
							// ????????? ???????????? 
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
	 * HAMS-ORG-LD-018 (???????????? ?????? - ??????????????? - ????????????????????????)
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
		//1.????????? ??????
		vu.checkRequired(new String[] {"studId", "date"}, paramMap);
		if(vu.isValid()) { 	   	
			//?????? API ??????
			paramMap.put("apiName", "connect-log-list");	       	
			setResult(dataKey,externalAPIservice.callExternalAPI(paramMap).get("data"));			
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;    
    }
    
    /**
	 * HAMS-ORG-LD-003 (???????????? ?????? - ??????????????????)
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
		//1.????????? ??????
		vu.checkRequired(new String[] {"studId", "date", "ordNm"}, paramMap);
		
		if(vu.isValid()) { 					
			//DB ??????
			setResult(dataKey, getMapperResultData(v_param, "list", paramMap, ".getLrnTmList"));
		} else {
			setResult(msgKey, vu.getResult());
		}
	
    	return result;
    }

	/**
	 * HAMS-ORG-LD-004 (???????????? ?????? - ??????????????????)
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
		//1.????????? ??????
		vu.checkRequired(new String[] {"studId", "serviceId", "date"}, paramMap);
		if(vu.isValid()) { 	   	
			//?????? API ??????
			paramMap.put("apiName", "act-element-detail");	       	
			setResult(dataKey,externalAPIservice.callExternalAPI(paramMap).get("data"));			
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
    }
    
    /**
	 * HAMS-ORG-LD-005 (???????????? ?????? - ????????? ??? ??????)
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
		//1.????????? ??????
		vu.checkRequired(new String[] {"currCon","studId"}, paramMap);
		
		if(vu.isValid()) { 		
			Map<String,Object> data = new HashMap<>();
            String startDate;
			String endDate;
			
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			Map<String,Object> msg = new HashMap<>();
			ArrayList<Map<String,Object>> detailList = new ArrayList<>();
			ArrayList<Map<String,Object>> detail = new ArrayList<>();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {	// ??????
				//1-1.????????? ?????? 
				vu1.checkRequired(new String[] {"yyyy","mm"}, paramMap);
				
				if(vu1.isValid()) { 	
					String yyyy = paramMap.get("yyyy").toString();
					int mm = Integer.valueOf(paramMap.get("mm").toString());
					String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
					String yymm = yyyy + convertMm;

					paramMap.put("yymm", yymm);
					//2. ????????? ??????
					vu2.isYearMonth("yyyy, mm", yymm);
					if(vu2.isValid()) {
						
						startDate = yyyy+"-"+convertMm+"-01";
						endDate = yyyy+"-"+convertMm+"-"+ CommonUtil.getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd"));
						paramMap.put("startDt", startDate);
						paramMap.put("endDt", endDate);
						paramMap.put("limitDtCnt", CommonUtil.getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd")));
						
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getAttCntStt");
						if(data != null) {
							detailList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getAttCntSttDetail");
							if(detailList.size() > 0 && detailList.get(0) != null) {
								for(Map<String, Object> item : detailList) {
									Map<String, Object> detailMap = new LinkedHashMap<>();
									
									detailMap.put("dt", item.get("dt"));
									detailMap.put("planDt", getItemValueCompare(item.get("planDt"), "1"));
									detailMap.put("attDt", getItemValueCompare(item.get("attDt"), "1"));
																		
									detail.add(detailMap);
								}
							}	
							// ????????? ?????? attCntMsg
							msg.put("summary", data.get("summary"));
							msg.put("detail", data.get("detail"));
							
							data.put("attCntMsg", msg);
							// ????????? ???????????? attCntDetail
							data.put("attCntDetail", detail);
							
							data.remove("summary");
							data.remove("detail");
						}
						setResult(msgKey, data);			
					} else {
						setResult(msgKey, vu2.getResult());				
					}
				} else {
					setResult(msgKey, vu1.getResult());
				}
	        } else {	// ?????? & ??????
	        	//1-1.????????? ??????
				vu.checkRequired(new String[] {"startDt","endDt"}, paramMap);
				
				if(vu.isValid()) { 	
					startDate = paramMap.get("startDt").toString();
					endDate = paramMap.get("endDt").toString();
					
					//2. ????????? ??????
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					long limitDtCnt = CommonUtil.getCalendarDiff(startDate, endDate, "DAY") + 1;
					paramMap.put("limitDtCnt", limitDtCnt);
					if(limitDtCnt < 7) paramMap.put("notSevenDayCheck", true);
					
					if(vu1.isValid() && vu2.isValid()) {
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getAttCntStt");
						if(data != null) {
							detailList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getAttCntSttDetail");
							if(detailList.size() > 0 && detailList.get(0) != null) {
								for(Map<String, Object> item : detailList) {
									Map<String, Object> detailMap = new LinkedHashMap<>();
									
									detailMap.put("dt", item.get("dt"));
									detailMap.put("planDt", getItemValueCompare(item.get("planDt"),"1"));
									detailMap.put("attDt", getItemValueCompare(item.get("attDt"), "1"));
									
									detail.add(detailMap);
								}
							}	
							// ????????? ?????? attCntMsg
							msg.put("summary", data.get("summary"));
							msg.put("detail", data.get("detail"));
							
							data.put("attCntMsg", msg);
							// ????????? ???????????? attCntDetail
							data.put("attCntDetail", detail);
							
							data.remove("summary");
							data.remove("detail");
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
	 * ???????????? ?????? - ????????? ?????? ??????
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
		//1.????????? ??????
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
			
			if(currConCheck.equals("m")) {	// ??????
				//1-1.????????? ?????? 
				vu1.checkRequired(new String[] {"yyyy","mm"}, paramMap);
				
				if(vu1.isValid()) { 	
					String yyyy = paramMap.get("yyyy").toString();
					int mm = Integer.valueOf(paramMap.get("mm").toString());
					String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
					String yymm = yyyy + convertMm;

					paramMap.put("yymm", yymm);
					//2. ????????? ??????
					vu2.isYearMonth("yyyy, mm", yymm);
					if(vu2.isValid()) {
						
						startDate = yyyy+"-"+convertMm+"-01";
						endDate = yyyy+"-"+convertMm+"-"+ CommonUtil.getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd"));
						paramMap.put("startDt", startDate);
						paramMap.put("endDt", endDate);
						paramMap.put("limitDtCnt", CommonUtil.getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd")));
						
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getLoginPtnStt");
						if(data != null) {
							data.put("loginPtn", data.get("loginPtn").toString().equals("1") ? "?????????" : "????????????");
							data.put("prevLoginPtn", data.get("prevLoginPtn").toString().equals("1") ? "?????????" : "????????????");
							detailList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getLoginPtnSttDetail");
							if(detailList.size() > 0 && detailList.get(0) != null) {
								for(Map<String, Object> item : detailList) {
									Map<String, Object> chartMap = new LinkedHashMap<>();
									Map<String, Object> detailMap = new LinkedHashMap<>();
									
									chartMap.put("dt", item.get("dt"));
									chartMap.put("prevDt", item.get("prevDt"));
									chartMap.put("loginTm", item.get("loginTm"));
									chartMap.put("prevLoginTm", item.get("prevLoginTm"));									
									detailChart.add(chartMap);
									
									detailMap.put("dt", item.get("dt"));
									detailMap.put("loginTm", item.get("loginTm"));
									detailMap.put("prevLoginTm", item.get("prevLoginTm"));								
									detail.add(detailMap);
								}
							}	
							// ????????? ?????? ?????? 
							msg.put("summary", data.get("summary"));
							msg.put("detail", data.get("detail"));
							data.put("loginPtnMsg", msg);
							// ????????????????????? 
							data.put("loginPtnChart", detailChart);
							// ????????? ?????? ???????????? 
							data.put("loginPtnDetail", detail);
							
							data.remove("summary");
							data.remove("detail");
						}
						setResult(msgKey, data);			
					} else {
						setResult(msgKey, vu2.getResult());				
					}
				} else {
					setResult(msgKey, vu1.getResult());
				}
	        } else {	// ?????? & ??????
	        	//1-1.????????? ??????
				vu.checkRequired(new String[] {"startDt","endDt"}, paramMap);
				
				if(vu.isValid()) { 	
					startDate = paramMap.get("startDt").toString();
					endDate = paramMap.get("endDt").toString();
					
					//2. ????????? ??????
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					long limitDtCnt = CommonUtil.getCalendarDiff(startDate, endDate, "DAY") + 1;
					paramMap.put("limitDtCnt", limitDtCnt);
					if(limitDtCnt < 7) paramMap.put("notSevenDayCheck", true);
					
					if(vu1.isValid() && vu2.isValid()) {
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getLoginPtnStt");
						if(data != null) {
							data.put("loginPtn", data.get("loginPtn").toString().equals("1") ? "?????????" : "????????????");
							data.put("prevLoginPtn", data.get("prevLoginPtn").toString().equals("1") ? "?????????" : "????????????");
							detailList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getLoginPtnSttDetail");
							if(detailList.size() > 0 && detailList.get(0) != null) {
								for(Map<String, Object> item : detailList) {
									Map<String, Object> chartMap = new LinkedHashMap<>();
									Map<String, Object> detailMap = new LinkedHashMap<>();
									
									chartMap.put("dt", item.get("dt"));
									chartMap.put("prevDt", item.get("prevDt"));
									chartMap.put("loginTm", item.get("loginTm"));
									chartMap.put("prevLoginTm", item.get("prevLoginTm"));									
									detailChart.add(chartMap);
									
									detailMap.put("dt", item.get("dt"));
									detailMap.put("loginTm", item.get("loginTm"));
									detailMap.put("prevLoginTm", item.get("prevLoginTm"));	
									detail.add(detailMap);
								}
							}	
							// ????????? ?????? ?????? 
							msg.put("summary", data.get("summary"));
							msg.put("detail", data.get("detail"));
							data.put("loginPtnMsg", msg);
							// ????????????????????? 
							data.put("loginPtnChart", detailChart);
							// ????????? ?????? ???????????? 
							data.put("loginPtnDetail", detail);
							
							data.remove("summary");
							data.remove("detail");
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
	 * ???????????? ?????? - ????????? ??????
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
		//1.????????? ??????
		vu.checkRequired(new String[] {"currCon","studId"}, paramMap);
		
		if(vu.isValid()) { 		
			Map<String,Object> data = new HashMap<>();
            String startDate;
			String endDate;
			
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			Map<String,Object> msg = new HashMap<>();
			ArrayList<Map<String,Object>> detailList = new ArrayList<>();
			ArrayList<Map<String,Object>> detailChart = new ArrayList<>();
			ArrayList<Map<String,Object>> detail = new ArrayList<>();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {	// ??????
				//1-1.????????? ?????? 
				vu1.checkRequired(new String[] {"yyyy","mm"}, paramMap);
				
				if(vu1.isValid()) { 	
					String yyyy = paramMap.get("yyyy").toString();
					int mm = Integer.valueOf(paramMap.get("mm").toString());
					String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
					String yymm = yyyy + convertMm;

					paramMap.put("yymm", yymm);
					//2. ????????? ??????
					vu2.isYearMonth("yyyy, mm", yymm);
					if(vu2.isValid()) {
						
						startDate = yyyy+"-"+convertMm+"-01";
						endDate = yyyy+"-"+convertMm+"-"+ CommonUtil.getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd"));
						paramMap.put("startDt", startDate);
						paramMap.put("endDt", endDate);
						paramMap.put("limitDtCnt", CommonUtil.getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd")));
						
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getExRtStt");
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
									detailMap.put("avgExRt", item.get("avgExRt"));								
									detail.add(detailMap);
								}
							}								
							// ????????? ?????? 
							msg.put("summary", data.get("summary"));
							msg.put("detail", data.get("detail"));
							data.put("exRtMsg", msg);
							// ??????????????? 
							data.put("exRtChart", detailChart);
							// ????????? ???????????? 
							data.put("exRtDetail", detail);

							data.remove("summary");
							data.remove("detail");
						}
						setResult(msgKey, data);			
					} else {
						setResult(msgKey, vu2.getResult());				
					}
				} else {
					setResult(msgKey, vu1.getResult());
				}
	        } else {	// ?????? & ??????
	        	//1-1.????????? ??????
				vu.checkRequired(new String[] {"startDt","endDt"}, paramMap);
				
				if(vu.isValid()) { 	
					startDate = paramMap.get("startDt").toString();
					endDate = paramMap.get("endDt").toString();
					
					//2. ????????? ??????
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					long limitDtCnt = CommonUtil.getCalendarDiff(startDate, endDate, "DAY") + 1;
					paramMap.put("limitDtCnt", limitDtCnt);
					if(limitDtCnt < 7) paramMap.put("notSevenDayCheck", true);
					
					if(vu1.isValid() && vu2.isValid()) {
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getExRtStt");
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
									detailMap.put("avgExRt", item.get("avgExRt"));								
									detail.add(detailMap);
								}
							}								
							// ????????? ?????? 
							msg.put("summary", data.get("summary"));
							msg.put("detail", data.get("detail"));
							data.put("exRtMsg", msg);
							// ??????????????? 
							data.put("exRtChart", detailChart);
							// ????????? ???????????? 
							data.put("exRtDetail", detail);

							data.remove("summary");
							data.remove("detail");
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
	 * ???????????? ?????? - ????????? ?????? ??????
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
		//1.????????? ??????
		vu.checkRequired(new String[] {"currCon","studId"}, paramMap);
		
		if(vu.isValid()) { 		
			Map<String,Object> data = new HashMap<>();
            String startDate;
			String endDate;
			
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			Map<String,Object> msg = new HashMap<>();
			ArrayList<Map<String,Object>> detailList = new ArrayList<>();
			ArrayList<Map<String,Object>> detailChart = new ArrayList<>();
			ArrayList<Map<String,Object>> detail = new ArrayList<>();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {	// ??????
				//1-1.????????? ?????? 
				vu1.checkRequired(new String[] {"yyyy","mm"}, paramMap);
				
				if(vu1.isValid()) { 	
					String yyyy = paramMap.get("yyyy").toString();
					int mm = Integer.valueOf(paramMap.get("mm").toString());
					String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
					String yymm = yyyy + convertMm;

					paramMap.put("yymm", yymm);
					//2. ????????? ??????
					vu2.isYearMonth("yyyy, mm", yymm);
					if(vu2.isValid()) {
						
						startDate = yyyy+"-"+convertMm+"-01";
						endDate = yyyy+"-"+convertMm+"-"+ CommonUtil.getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd"));
						paramMap.put("startDt", startDate);
						paramMap.put("endDt", endDate);
						paramMap.put("limitDtCnt", CommonUtil.getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd")));
						
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getFnshLrnExStt");
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
							// ????????? ?????? ??????
							msg.put("summary", data.get("summary"));
							msg.put("detail", data.get("detail"));
							data.put("fnshLrnMsg", msg);
							// ????????? ?????? ?????? 
							data.put("fnshLrnChart", detailChart);
							// ????????? ?????? ???????????? 
							data.put("fnshLrnDetail", detail);

							data.remove("summary");
							data.remove("detail");
						}
						setResult(msgKey, data);			
					} else {
						setResult(msgKey, vu2.getResult());				
					}
				} else {
					setResult(msgKey, vu1.getResult());
				}
	        } else {	// ?????? & ??????
	        	//1-1.????????? ??????
				vu.checkRequired(new String[] {"startDt","endDt"}, paramMap);
				
				if(vu.isValid()) { 	
					startDate = paramMap.get("startDt").toString();
					endDate = paramMap.get("endDt").toString();
					
					//2. ????????? ??????
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					long limitDtCnt = CommonUtil.getCalendarDiff(startDate, endDate, "DAY") + 1;
					paramMap.put("limitDtCnt", limitDtCnt);
					if(limitDtCnt < 7) paramMap.put("notSevenDayCheck", true);
					
					if(vu1.isValid() && vu2.isValid()) {
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getFnshLrnExStt");
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
							// ????????? ?????? ??????
							msg.put("summary", data.get("summary"));
							msg.put("detail", data.get("detail"));
							data.put("fnshLrnMsg", msg);
							// ????????? ?????? ?????? 
							data.put("fnshLrnChart", detailChart);
							// ????????? ?????? ???????????? 
							data.put("fnshLrnDetail", detail);

							data.remove("summary");
							data.remove("detail");
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
	 * ???????????? ?????? - ?????? ?????? ??????
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
		//1.????????? ??????
		vu.checkRequired(new String[] {"currCon","studId"}, paramMap);
		
		if(vu.isValid()) { 		
			Map<String,Object> data = new HashMap<>();
            String startDate;
			String endDate;
			
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			Map<String,Object> msg = new HashMap<>();
			ArrayList<Map<String,Object>> detailList = new ArrayList<>();
			ArrayList<Map<String,Object>> detailChart = new ArrayList<>();
			ArrayList<Map<String,Object>> detail = new ArrayList<>();
			paramMap.put("currConCheck", currConCheck);
			
			if(currConCheck.equals("m")) {	// ??????
				//1-1.????????? ?????? 
				vu1.checkRequired(new String[] {"yyyy","mm"}, paramMap);
				
				if(vu1.isValid()) { 	
					String yyyy = paramMap.get("yyyy").toString();
					int mm = Integer.valueOf(paramMap.get("mm").toString());
					String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
					String yymm = yyyy + convertMm;

					paramMap.put("yymm", yymm);
					//2. ????????? ??????
					vu2.isYearMonth("yyyy, mm", yymm);
					if(vu2.isValid()) {
						
						startDate = yyyy+"-"+convertMm+"-01";
						endDate = yyyy+"-"+convertMm+"-"+ CommonUtil.getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd"));
						paramMap.put("startDt", startDate);
						paramMap.put("endDt", endDate);
						paramMap.put("limitDtCnt", CommonUtil.getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd")));
						
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getLrnExSttCompareSub");
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
							// ?????? ?????? ??????
							msg.put("summary", data.get("summary"));
							msg.put("detail", data.get("detail"));
							data.put("lrnExMsg", msg);
							// ?????? ?????? ?????? 
							data.put("lrnExChart", detailChart);
							// ?????? ?????? ????????????
							data.put("lrnExDetail", detail);						

							data.remove("summary");
							data.remove("detail");
						}
						setResult(msgKey, data);			
					} else {
						setResult(msgKey, vu2.getResult());				
					}
				} else {
					setResult(msgKey, vu1.getResult());
				}
	        } else {	// ?????? & ??????
	        	//1-1.????????? ??????
				vu.checkRequired(new String[] {"startDt","endDt"}, paramMap);
				
				if(vu.isValid()) { 	
					startDate = paramMap.get("startDt").toString();
					endDate = paramMap.get("endDt").toString();
					
					//2. ????????? ??????
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					long limitDtCnt = CommonUtil.getCalendarDiff(startDate, endDate, "DAY") + 1;
					paramMap.put("limitDtCnt", limitDtCnt);
					if(limitDtCnt < 7) paramMap.put("notSevenDayCheck", true);
					
					if(vu1.isValid() && vu2.isValid()) {
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getLrnExSttCompareSub");
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
							// ?????? ?????? ??????
							msg.put("summary", data.get("summary"));
							msg.put("detail", data.get("detail"));
							data.put("lrnExMsg", msg);
							// ?????? ?????? ?????? 
							data.put("lrnExChart", detailChart);
							// ?????? ?????? ????????????
							data.put("lrnExDetail", detail);						

							data.remove("summary");
							data.remove("detail");
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
	 * ???????????? ?????? - ????????? ?????? ??????
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
		//1.????????? ??????
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
						int lastDay = CommonUtil.getCalendarLastDay(startDt, new SimpleDateFormat("yyyy-MM-dd"));
						String endDt = yyyy + "-" +convertMm + "-" + String.valueOf(lastDay);
						
						paramMap.put("yyMm", yyMm);
						paramMap.put("startDt", startDt);
						paramMap.put("endDt", endDt);
						paramMap.put("lastDay", lastDay);
						
						aLrnData = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getALrnExStt");
						aLrnList = (ArrayList<Map<String, Object>>) getMapperResultData(v_param, "list", paramMap, ".getALrnExSttList");
						
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
						setResult(dataKey, data);
						
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
					
					//2. ????????? ??????
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					
					if(vu1.isValid() && vu2.isValid()) {
						long limitDtCnt = CommonUtil.getCalendarDiff(startDate, endDate, "DAY") + 1;
						paramMap.put("lastDay", limitDtCnt);
						if(limitDtCnt < 7) paramMap.put("notSevenDayCheck", true);
						
						aLrnData = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getALrnExStt");
						aLrnList = (ArrayList<Map<String, Object>>) getMapperResultData(v_param, "list", paramMap, ".getALrnExSttList");
						
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
							
							data.put("aLrnExChart", chartList);
							data.put("aLrnExMsg", msgMap);
							data.put("aLrnExDetail", detailList);
						}
						setResult(dataKey, data);
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
			
		} else {
			setResult(msgKey, vu.getResult());
		}
    	
    	return result;
    }

    /**
	 * HAMS-ORG-LD-011
	 * ???????????? ?????? - ????????? ??????
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
		//1.????????? ??????
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
						int lastDay = CommonUtil.getCalendarLastDay(startDt, new SimpleDateFormat("yyyy-MM-dd"));
						String endDt = yyyy + "-" +convertMm + "-" + String.valueOf(lastDay);
						
						paramMap.put("yyMm", yyMm);
						paramMap.put("startDt", startDt);
						paramMap.put("endDt", endDt);
						paramMap.put("lastDay", lastDay);
						
						crtData = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getCrtRtStt");
						crtList = (ArrayList<Map<String, Object>>) getMapperResultData(v_param, "list", paramMap, ".getCrtRtSttList");
						
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
						setResult(dataKey, data);
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
					
					//2. ????????? ??????
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					
					if(vu1.isValid() && vu2.isValid()) {
						long limitDtCnt = CommonUtil.getCalendarDiff(startDate, endDate, "DAY") + 1;
						if(limitDtCnt < 7) paramMap.put("notSevenDayCheck", true);
						paramMap.put("lastDay", limitDtCnt);
												
						crtData = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getCrtRtStt");
						crtList = (ArrayList<Map<String, Object>>) getMapperResultData(v_param, "list", paramMap, ".getCrtRtSttList");
						
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
						setResult(dataKey, data);
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
		} else {
			setResult(msgKey, vu.getResult());
		}
    	
    	return result;
    }

    /**
	 * HAMS-ORG-LD-012
	 * ???????????? ?????? - ????????? ???????????? ??????
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
		//1.????????? ??????
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
						int lastDay = CommonUtil.getCalendarLastDay(startDt, new SimpleDateFormat("yyyy-MM-dd"));
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
						setResult(dataKey, data);
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
					
					//2. ????????? ??????
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					
					if(vu1.isValid() && vu2.isValid()) {
						long limitDtCnt = CommonUtil.getCalendarDiff(startDate, endDate, "DAY") + 1;
						paramMap.put("lastDay", limitDtCnt);
						if(limitDtCnt < 7) paramMap.put("notSevenDayCheck", true);
						
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
						setResult(dataKey, data);
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
		} else {
			setResult(msgKey, vu.getResult());
		}
		
    	return result;
    }

    /**
	 * HAMS-ORG-LD-013
	 * ???????????? ?????? - ?????? ?????? ??? ??????
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
		//1.????????? ??????
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
						int lastDay = CommonUtil.getCalendarLastDay(startDt, new SimpleDateFormat("yyyy-MM-dd"));
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
						setResult(dataKey, data);
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
					
					//2. ????????? ??????
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					
					if(vu1.isValid() && vu2.isValid()) {
						long limitDtCnt = CommonUtil.getCalendarDiff(startDate, endDate, "DAY") + 1;
						paramMap.put("lastDay", limitDtCnt);
						if(limitDtCnt < 7) paramMap.put("notSevenDayCheck", true);
												
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
						setResult(dataKey, data);
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
		//1.????????? ??????
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
						int lastDay = CommonUtil.getCalendarLastDay(startDt, new SimpleDateFormat("yyyy-MM-dd"));
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
						setResult(dataKey, data);
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
					
					//2. ????????? ??????
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					
					if(vu1.isValid() && vu2.isValid()) {
						long limitDtCnt = CommonUtil.getCalendarDiff(startDate, endDate, "DAY") + 1;
						paramMap.put("lastDay", limitDtCnt);
						if(limitDtCnt < 7) paramMap.put("notSevenDayCheck", true);
												
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
						setResult(dataKey, data);
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
		//1.????????? ??????
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
						int lastDay = CommonUtil.getCalendarLastDay(startDt, new SimpleDateFormat("yyyy-MM-dd"));
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
						setResult(dataKey, data);
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
					
					//2. ????????? ??????
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					
					if(vu1.isValid() && vu2.isValid()) {
						long limitDtCnt = CommonUtil.getCalendarDiff(startDate, endDate, "DAY") + 1;
						paramMap.put("lastDay", limitDtCnt);
						if(limitDtCnt < 7) paramMap.put("notSevenDayCheck", true);
												
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
						setResult(dataKey, data);
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
		//1.????????? ??????
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
						int lastDay = CommonUtil.getCalendarLastDay(startDt, new SimpleDateFormat("yyyy-MM-dd"));
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
						setResult(dataKey, data);
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
					
					//2. ????????? ??????
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					
					if(vu1.isValid() && vu2.isValid()) {
						long limitDtCnt = CommonUtil.getCalendarDiff(startDate, endDate, "DAY") + 1;
						paramMap.put("lastDay", limitDtCnt);
						if(limitDtCnt < 7) paramMap.put("notSevenDayCheck", true);
												
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
						setResult(dataKey, data);
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
		//1.????????? ??????
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
						int lastDay = CommonUtil.getCalendarLastDay(startDt, new SimpleDateFormat("yyyy-MM-dd"));
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
						setResult(dataKey, data);
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
					
					//2. ????????? ??????
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					
					if(vu1.isValid() && vu2.isValid()) {
						long limitDtCnt = CommonUtil.getCalendarDiff(startDate, endDate, "DAY") + 1;
						paramMap.put("lastDay", limitDtCnt);
						if(limitDtCnt < 7) paramMap.put("notSevenDayCheck", true);
						
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
						setResult(dataKey, data);
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
		//1.????????? ??????
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
						int lastDay = CommonUtil.getCalendarLastDay(startDt, new SimpleDateFormat("yyyy-MM-dd"));
						String endDt = yyyy + "-" +convertMm + "-" + String.valueOf(lastDay);
						
						paramMap.put("startDt", startDt);
						paramMap.put("endDt", endDt);
						
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getSubjExam");
						setResult(dataKey, data);
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
					
					//2. ????????? ??????
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					
					if(vu1.isValid() && vu2.isValid()) {
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getSubjExam");
						setResult(dataKey, data);
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
		//1.????????? ??????
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
						int lastDay = CommonUtil.getCalendarLastDay(startDt, new SimpleDateFormat("yyyy-MM-dd"));
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
							positiveMsgData.put("summary", dataMap.get("maxSummary")); // ????????? ????????? ?????? ??? ?????? ??????
							positiveMsgData.put("detail", dataMap.get("maxDetail")); // ????????? ????????? ?????? ??? ?????? ??????
							
							positiveData.put("current", positiveCurrData);
							positiveData.put("prev", positivePrevData);
							positiveData.put("msg", positiveMsgData);
							
							data.put("positive", positiveData);
							
							negativeData.put("subjCd", dataMap.get("minSubjCd"));
							
							negativeCurrData.put("dt", dataMap.get("dt").toString());
							negativeCurrData.put("crtRt", dataMap.get("minCrtRt"));
							negativePrevData.put("dt", dataMap.get("preDt").toString());
							negativePrevData.put("crtRt", dataMap.get("preMinCrtRt"));
							negativeMsgData.put("summary", dataMap.get("minSummary")); // ????????? ????????? ?????? ??? ?????? ??????
							negativeMsgData.put("detail", dataMap.get("minDetail")); // ????????? ????????? ?????? ??? ?????? ??????
							
							negativeData.put("current", negativeCurrData);
							negativeData.put("prev", negativePrevData);
							negativeData.put("msg", negativeMsgData);
							
							data.put("negative", negativeData);
						}
						setResult(dataKey, data);
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
					
					//2. ????????? ??????
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
							positiveMsgData.put("summary", dataMap.get("maxSummary")); // ????????? ????????? ?????? ??? ?????? ??????
							positiveMsgData.put("detail", dataMap.get("maxDetail")); // ????????? ????????? ?????? ??? ?????? ??????
							
							positiveData.put("current", positiveCurrData);
							positiveData.put("prev", positivePrevData);
							positiveData.put("msg", positiveMsgData);
							
							data.put("positive", positiveData);
							
							negativeData.put("subjCd", dataMap.get("minSubjCd"));
							
							negativeCurrData.put("dt", dataMap.get("dt"));
							negativeCurrData.put("crtRt", dataMap.get("minCrtRt"));
							negativePrevData.put("dt", dataMap.get("preDt"));
							negativePrevData.put("crtRt", dataMap.get("preMinCrtRt"));
							negativeMsgData.put("summary", dataMap.get("minSummary")); // ????????? ????????? ?????? ??? ?????? ??????
							negativeMsgData.put("detail", dataMap.get("minDetail")); // ????????? ????????? ?????? ??? ?????? ??????
							
							negativeData.put("current", negativeCurrData);
							negativeData.put("prev", negativePrevData);
							negativeData.put("msg", negativeMsgData);
							
							data.put("negative", negativeData);
						}
						setResult(dataKey, data);
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
		//1.????????? ??????
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
						String yyMm = yyyy + convertMm;
						
						String startDt = yyyy + "-" + convertMm + "-01";
						int lastDay = CommonUtil.getCalendarLastDay(startDt, new SimpleDateFormat("yyyy-MM-dd"));
						String endDt = yyyy + "-" +convertMm + "-" + String.valueOf(lastDay);
						
						paramMap.put("yyMm", yyMm);
						paramMap.put("startDt", startDt);
						paramMap.put("endDt", endDt);
						
						ArrayList<Map<String,Object>> dataList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getExamChart");
						
						int nullCheck = 0;
						for(Map<String, Object> item : dataList) {
                            if(item.get("crtRt") == null && item.get("prevCrtRt") == null) {
								nullCheck++;
							}
						}
						
						if(nullCheck < 6) {
							data = dataList;
						}
						
						setResult(dataKey, data);
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
					
					//2. ????????? ??????
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					
					if(vu1.isValid() && vu2.isValid()) {
						ArrayList<Map<String,Object>> dataList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getExamChart");
						
						int nullCheck = 0;
						for(Map<String, Object> item : dataList) {
                            if(item.get("crtRt") == null && item.get("prevCrtRt") == null) {
								nullCheck++;
							}
						}
						
						if(nullCheck < 6) {
							data = dataList;
						}
						
						setResult(dataKey, data);
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
		//1.????????? ??????
		vu.checkRequired(new String[] {"currCon","studId"}, paramMap);
		
		if(vu.isValid()) {
			Map<String,Object> data = new LinkedHashMap<>();
			Map<String,Object> dataMap = new LinkedHashMap<>();
			Map<String,Object> cntMap = new LinkedHashMap<>();
			ArrayList<Map<String,Object>> subjExamList = new ArrayList<>();
			
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
						int lastDay = CommonUtil.getCalendarLastDay(startDt, new SimpleDateFormat("yyyy-MM-dd"));
						String endDt = yyyy + "-" +convertMm + "-" + String.valueOf(lastDay);
						
						paramMap.put("startDt", startDt);
						paramMap.put("endDt", endDt);
						
						if(paramMap.containsKey("types")) {
							paramMap.put("types",paramMap.get("types").toString().split(","));
						}
						
						subjExamList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getSubjExamList");
						cntMap = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getSubjExamListCnt");
						
						if(subjExamList.size() > 0) {
							data.put("totalCnt", cntMap.get("totalCnt"));
							data.put("list", subjExamList);
						}
						
						setResult(dataKey, data);
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
					
					//2. ????????? ??????
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					
					if(vu1.isValid() && vu2.isValid()) {
						if(paramMap.containsKey("types")) {
							paramMap.put("types",paramMap.get("types").toString().split(","));
						}
						
						subjExamList = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getSubjExamList");
						cntMap = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getSubjExamListCnt");
						
						if(subjExamList.size() > 0) {
							data.put("totalCnt", cntMap.get("totalCnt"));
							data.put("list", subjExamList);
						}
						
						setResult(dataKey, data);
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
		//1.????????? ??????
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
						int lastDay = CommonUtil.getCalendarLastDay(startDt, new SimpleDateFormat("yyyy-MM-dd"));
						String endDt = yyyy + "-" +convertMm + "-" + String.valueOf(lastDay);
						
						paramMap.put("startDt", startDt);
						paramMap.put("endDt", endDt);
						
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getIncrtNote");
						incrtNtList = (ArrayList<Map<String, Object>>) getMapperResultData(v_param, "list", paramMap, ".getIncrtNoteList");
						
						if(data != null) {
							data.put("list", incrtNtList);	
						}
						
						setResult(dataKey, data);
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
					
					//2. ????????? ??????
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					
					if(vu1.isValid() && vu2.isValid()) {
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getIncrtNote");
						incrtNtList = (ArrayList<Map<String, Object>>) getMapperResultData(v_param, "list", paramMap, ".getIncrtNoteList");
						
						if(data != null) {
							data.put("list", incrtNtList);	
						}
						
						setResult(dataKey, data);
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
		} else {
			setResult(msgKey, vu.getResult());
		}
    	
    	return result;
    }

    /**
	 * HAMS-ORG-ES-005
	 * ???????????? ?????? - ????????? ??????
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
		//1.????????? ??????
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
			
			if(currConCheck.equals("m")) {	// ??????
				//1-1.????????? ?????? 
				vu1.checkRequired(new String[] {"yyyy","mm"}, paramMap);
				
				if(vu1.isValid()) { 	
					String yyyy = paramMap.get("yyyy").toString();
					int mm = Integer.valueOf(paramMap.get("mm").toString());
					String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
					String yymm = yyyy + convertMm;

					paramMap.put("yymm", yymm);
					//2. ????????? ??????
					vu2.isYearMonth("yyyy, mm", yymm);
					if(vu2.isValid()) {
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getChapterStt");
						if(data != null) {
							if(data.get("subjCdList") != null) {
								detailChart.put("subjCd", Arrays.asList(data.get("subjCdList").toString().split("\\|")));	//????????????
								data.remove("subjCdList");
							}
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
							detailChart.put("list", detail);		//??????
							data.put("chapter", detailChart);		//??????							
						}
						setResult(msgKey, data);			
					} else {
						setResult(msgKey, vu2.getResult());				
					}
				} else {
					setResult(msgKey, vu1.getResult());
				}
	        } else {	// ?????? & ??????
	        	//1-1.????????? ??????
				vu.checkRequired(new String[] {"startDt","endDt"}, paramMap);
				
				if(vu.isValid()) { 	
					startDate = paramMap.get("startDt").toString();
					endDate = paramMap.get("endDt").toString();
					
					//2. ????????? ??????
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					
					if(vu1.isValid() && vu2.isValid()) {
						data = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getChapterStt");
						if(data != null) {
							if(data.get("subjCdList") != null) {
								detailChart.put("subjCd", Arrays.asList(data.get("subjCdList").toString().split("\\|")));	//????????????
								data.remove("subjCdList");
							}
						
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
							detailChart.put("list", detail);		//??????
							data.put("chapter", detailChart);		//??????							
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
	 * ???????????? ?????? - ????????? ????????????
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
    @Override    
    public Map getChapterLrn(Map<String, Object> paramMap) throws Exception {
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "CHAPTERLRN");

		getStudId(paramMap);
		
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		ValidationUtil vu1 = new ValidationUtil();
		ValidationUtil vu2 = new ValidationUtil();
		//1.????????? ??????
		vu.checkRequired(new String[] {"currCon","studId", "chapterCd"}, paramMap);
		
		if(vu.isValid()) { 		
			Map<String,Object> data = new HashMap<>();
            String startDate;
			String endDate;
			
			String currConCheck = paramMap.get("currCon").toString().toLowerCase();
			paramMap.put("currConCheck", currConCheck);
			Map<String,Object> detail = new HashMap<>();
			Map<String,Object> currentDetail = new HashMap<>();
			Map<String,Object> prevDetail = new HashMap<>();

			if(currConCheck.equals("m")) {	// ??????
				//1-1.????????? ?????? 
				vu1.checkRequired(new String[] {"yyyy","mm"}, paramMap);
				
				if(vu1.isValid()) { 	
					String yyyy = paramMap.get("yyyy").toString();
					int mm = Integer.valueOf(paramMap.get("mm").toString());
					String convertMm = (mm < 10) ? "0" + mm : String.valueOf(mm);
					String yymm = yyyy + convertMm;

					paramMap.put("yymm", yymm);
					//2. ????????? ??????
					vu2.isYearMonth("yyyy, mm", yymm);
					if(vu2.isValid()) {
						detail = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getChapterLrn");
						if(detail != null) {
							// Mybatis null ?????? ?????? ???????????? ?????? ?????? ???.
							currentDetail.put("chapterNm", detail.get("chapterNm").toString().equals("") ? null : detail.get("chapterNm"));
							currentDetail.put("examCrtRt", detail.get("examCrtRt").toString().equals("") ? null : detail.get("examCrtRt"));
							currentDetail.put("examDt", detail.get("examDt").toString().equals("") ? null : detail.get("examDt"));
							prevDetail.put("chapterNm", detail.get("preChapterNm").toString().equals("") ? null : detail.get("preChapterNm"));
							prevDetail.put("examCrtRt", detail.get("preExamCrtRt").toString().equals("") ? null : detail.get("preExamCrtRt"));
							prevDetail.put("examDt", detail.get("preExamDt").toString().equals("") ? null : detail.get("preExamDt"));
							data.put("priorLrn", prevDetail);			//????????????		
							data.put("currentLrn", currentDetail);		//????????????	
							data.put("followUpLrn", detail.get("followUpLrn").toString().equals("") ? Arrays.asList() : Arrays.asList(detail.get("followUpLrn").toString().split("\\|")));		//????????????		
							data.put("supplementaryLrn", detail.get("supplementaryLrn").toString().equals("") ? Arrays.asList() : Arrays.asList(detail.get("supplementaryLrn").toString().split("\\|")));		//????????????								
						}	
						setResult(msgKey, data);			
					} else {
						setResult(msgKey, vu2.getResult());				
					}
				} else {
					setResult(msgKey, vu1.getResult());
				}
	        } else {	// ?????? & ??????
	        	//1-1.????????? ??????
				vu.checkRequired(new String[] {"startDt","endDt"}, paramMap);
				
				if(vu.isValid()) { 	
					startDate = paramMap.get("startDt").toString();
					endDate = paramMap.get("endDt").toString();
					
					//2. ????????? ??????
					vu1.isDate("startDt", startDate);
					vu2.isDate("endDt", endDate);
					
					if(vu1.isValid() && vu2.isValid()) {
						detail = (Map<String, Object>) getMapperResultData(v_param, "", paramMap, ".getChapterLrn");
						if(detail != null) {
							// Mybatis null ?????? ?????? ???????????? ?????? ?????? ???.
							currentDetail.put("chapterNm", detail.get("chapterNm").toString().equals("") ? null : detail.get("chapterNm"));
							currentDetail.put("examCrtRt", detail.get("examCrtRt").toString().equals("") ? null : detail.get("examCrtRt"));
							currentDetail.put("examDt", detail.get("examDt").toString().equals("") ? null : detail.get("examDt"));
							prevDetail.put("chapterNm", detail.get("preChapterNm").toString().equals("") ? null : detail.get("preChapterNm"));
							prevDetail.put("examCrtRt", detail.get("preExamCrtRt").toString().equals("") ? null : detail.get("preExamCrtRt"));
							prevDetail.put("examDt", detail.get("preExamDt").toString().equals("") ? null : detail.get("preExamDt"));
							data.put("priorLrn", prevDetail);			//????????????		
							data.put("currentLrn", currentDetail);		//????????????	
							data.put("followUpLrn", detail.get("followUpLrn").toString().equals("") ? Arrays.asList() : Arrays.asList(detail.get("followUpLrn").toString().split("\\|")));		//????????????		
							data.put("supplementaryLrn", detail.get("supplementaryLrn").toString().equals("") ? Arrays.asList() : Arrays.asList(detail.get("supplementaryLrn").toString().split("\\|")));		//????????????								
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
    
    //p,startDt,endDt ?????? ?????????
    private void checkRequired(Map<String,Object> params) throws Exception {
        ValidationUtilTutor vu = new ValidationUtilTutor();
        //????????? ??????
        vu.checkRequired(new String[] {"p"},params);

        getStudId(params);
    }

    //p,startDt,endDt ?????? ?????????
    private void checkRequiredWithDt(Map<String,Object> params) throws Exception {
        ValidationUtilTutor vu = new ValidationUtilTutor();
        //1.????????? ??????
        vu.checkRequired(new String[] {"p","startDt","endDt"},params);

		//2. ????????? ??????
        vu.isDate("startDt",(String)params.get("startDt"));
        vu.isDate("endDt",(String)params.get("endDt"));

        //?????????
        String[] encodedArr = getDecodedParam(params.get("s").toString());
        String encodedStudId = encodedArr[1];

        //DB params
        params.put("studId",encodedStudId);
    }

    /***
     * ?????????????????? studId ??????
     * @param params
     * @throws Exception
     */
	private void getStudId(Map<String, Object> params) throws Exception {
		//?????????
        try {
        	CipherUtil cp = CipherUtil.getInstance();
// ???????????? ??????id ???????????? ????????????,,,
//        	if(params.containsKey("s") && params.get("s").toString().split(",").length > 1) {
//        		List<String> sList = new ArrayList<>();
//        		ArrayList<String> studIdList = new ArrayList<>();
//				sList = Arrays.asList(params.get("s").toString().split(","));
//				for(String s : sList) {							
//					studIdList.add(cp.AES_Decode(s));		
//				}
//				params.put("studIdList", studIdList);
//        	} else {

        		String decodedStr = cp.AES_Decode(params.get("s").toString());
	
	            //DB params
	            params.put("studId",decodedStr);
//        	}
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
            int dataSize = data.size(); //data map??? ??????
            int count = 0; //inner data?????? null ?????? ??????
            for(String key : data.keySet()) {
                if(data.get(key) instanceof List) {

                    //List??????
                    if((((List) data.get(key)).size() == 0)) {
                        count += 1;
                    }
                }
                else if(data.get(key) instanceof Map) {
                    //Map??????
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
        int dataSize = data.size(); //data map??? ??????
        int count = 0; //inner data?????? null ?????? ??????
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
     * ?????????????????? ???????????? ??????(?????????,????????? object??? ????????? result)??????.
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
        else if(data instanceof Map && ((Map)data).containsKey("error")) {	// error ?????? ???????????? ?????? ??????
            result.put(msgKey, data);
        } else if(data instanceof Map && ((Map)data).containsKey("resultCode")) {	// resultCode ?????? ???????????? ?????? ??????
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
     * item value ??? ??????
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
	 * HAMS-ORG-DB-001
	 * ???????????? - ????????? ????????? ?????? ?????? ??????
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
    @Override
    public Map getCheckReport(Map<String, Object> paramMap) throws Exception {
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "CHECKREPORT");
// studIdList studId ???????????? ???????????? ????????????,,
//		getStudId(paramMap);
		
    	//Validation
//		ValidationUtil vu1 = new ValidationUtil();
//		ValidationUtil vu2 = new ValidationUtil();
//		//1.????????? ??????
//		vu1.checkRequired(new String[] {"yymm","studId"}, paramMap);
//		vu2.checkRequired(new String[] {"yymm","studIdList"}, paramMap);
//		
//		if(vu1.isValid()) { 		
//			setResult(dataKey, getMapperResultData(v_param, "", paramMap, ".getCheckReport"));			
//		} else if(vu2.isValid()) {
//			setResult(dataKey, getMapperResultData(v_param, "list", paramMap, ".getCheckReport"));
//		} else {
//			if(!vu1.isValid()) {
//				setResult(msgKey, vu1.getResult());
//			} else if(!vu2.isValid()) {
//				setResult(msgKey, vu2.getResult());						
//			}
//		}
    	
    	Map<String,Object> data = new HashMap<>();
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.????????? ??????
		vu.checkRequired(new String[] {"yymm","studId"}, paramMap);
		if(vu.isValid()) { 		
    		List<String> sList = new ArrayList<>();
    		List<String> srList = new ArrayList<>();
    		ArrayList<String> studIdList = new ArrayList<>();
    		ArrayList<String> studIdListResult = new ArrayList<>();
			sList = Arrays.asList(paramMap.get("studId").toString().split(","));
			for(String s : sList) {							
				studIdList.add(s);		
			}
			paramMap.put("studIdList", studIdList);
			data = (Map<String,Object>)getMapperResultData(v_param, "", paramMap, ".getCheckReport");
			if(data != null) {				
				srList = Arrays.asList(data.get("studId").toString().split(","));
				
				for(String s : studIdList) {		
					for(String sr : srList) {		
						if(s.equals(sr)) studIdListResult.add(sr);		
					}
				}
				setResult(dataKey, studIdListResult.stream().mapToInt(Integer::parseInt).toArray());
			} else {
				setResult(dataKey, null);
			}
		} else {
			setResult(msgKey, vu.getResult());
		}
    	return result;
    }
    
    /**
	 * HAMS-ORG-DB-002
	 * ???????????? - ????????? ????????? ????????? ??????
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
    @Override
    public Map getAiReportList(Map<String, Object> paramMap) throws Exception {
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "AIREPORTLIST");

    	Map<String,Object> data = new HashMap<>();
    	Map<String,Object> data_period = new HashMap<>();
    	ArrayList<Map<String,Object>> data_lrnBasic = new ArrayList<>();
    	ArrayList<Map<String,Object>> data_lrnHabit = new ArrayList<>();
    	ArrayList<Map<String,Object>> data_lrnHabitChart = new ArrayList<>();
    	ArrayList<Map<String,Object>> stud = new ArrayList<>();
    	ArrayList<Map<String,Object>> lrnBasic = new ArrayList<>();
    	ArrayList<Map<String,Object>> lrnHabit = new ArrayList<>();
		String startDate = null;
		String endDate = null;
		
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		ValidationUtil vu2 = new ValidationUtil();
		//1.????????? ??????
		vu.checkRequired(new String[] {"yymm","studId"}, paramMap);
		//2. ????????? ??????
		if(vu.isValid()) { 		
    		List<String> sList = new ArrayList<>();
    		ArrayList<String> studIdList = new ArrayList<>();
			sList = Arrays.asList(paramMap.get("studId").toString().split(","));
			for(String s : sList) {							
				studIdList.add(s);		
			}
			paramMap.put("studIdList", studIdList);
			stud = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getStud");
			
			
			String yymm = paramMap.get("yymm").toString();
			//2. ????????? ??????
			vu2.isYearMonth("yyyy, mm", yymm);
			if(vu2.isValid()) {
				String yyyy = yymm.substring(0,4);
				String mm = yymm.substring(4,6);
				
				if(!paramMap.containsKey("wk")) {
					// startDate, endDate ?????? ??????, ????????????
					startDate = yyyy + "-" + mm + "-01";
					endDate = yyyy + "-" + mm + "-" + CommonUtil.getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd"));
					paramMap.put("startDt", startDate);
					paramMap.put("endDt", endDate);
					paramMap.put("currConCheck", "m");
					data_lrnBasic = (ArrayList<Map<String,Object>>)getMapperResultData(v_param, "list", paramMap, ".getLrnBasicMonthly");
					data_lrnHabit = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getLrnHabitMonthly");
					paramMap.put("limitDtCnt", CommonUtil.getCalendarLastDay(startDate, new SimpleDateFormat("yyyy-MM-dd")));					
				} else {
					// startDate, endDate ????????? ?????? db?????? ??????
					data_period = (Map<String,Object>)getMapperResultData(v_param, "", paramMap, ".getPeriod");
					startDate = data_period.get("startDt").toString();
					endDate = data_period.get("endDt").toString();
					paramMap.put("startDt", startDate);
					paramMap.put("endDt", endDate);
					paramMap.put("currConCheck", "w");	
					long limitDtCnt = CommonUtil.getCalendarDiff(startDate, endDate, "DAY") + 1;
					if(limitDtCnt < 7) paramMap.put("notSevenDayCheck", true);
					paramMap.put("limitDtCnt", limitDtCnt);		
					data_lrnBasic = (ArrayList<Map<String,Object>>)getMapperResultData(v_param, "list", paramMap, ".getLrnBasicPeriod");
					data_lrnHabit = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getLrnHabitPeriod");
								
				}
			}
			
			if(data_lrnBasic != null) {
				if(data_lrnBasic.size() > 0 && data_lrnBasic.get(0) != null) {
					Map<String, Object> msgRequestMap = new LinkedHashMap<>();
					msgRequestMap.put("version", "1.0");
					msgRequestMap.put("sheet", "C");
					ArrayList<Map<String,Object>> msgTemplate = new ArrayList<>();
					msgTemplate = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", msgRequestMap, ".getCommMsgTemplate");
					long limitDtCnt = CommonUtil.getCalendarDiff(startDate, endDate, "DAY") + 1;
					if(limitDtCnt < 7) paramMap.put("notSevenDayCheck", true);
					
					for(Map<String, Object> item : data_lrnBasic) {
						Map<String, Object> detailMap = new LinkedHashMap<>();									
						ArrayList<String> positive = new ArrayList<>();
						ArrayList<String> negative = new ArrayList<>();
						Map<String,Object> msg = new HashMap<>();
						
						getLrnBasicResult(item, detailMap, positive, negative, msg, msgTemplate);
						
						if(!paramMap.containsKey("wk")) {
							detailMap.put("prevDtCnt", CommonUtil.getCalendarLastDay(subDate(startDate,-1,false,false), new SimpleDateFormat("yyyy-MM-dd")));
						} else {
							detailMap.put("prevDtCnt", limitDtCnt);								
						}
						detailMap.put("studId", item.get("studId"));
						lrnBasic.add(detailMap);
					}
				}
			} 
			
			if(data_lrnHabit != null) {
				if(data_lrnHabit.size() > 0 && data_lrnHabit.get(0) != null) {
					data_lrnHabitChart = (ArrayList<Map<String,Object>>) getMapperResultData(v_param, "list", paramMap, ".getLrnHabitDetailReport");
					if(data_lrnHabitChart.size() > 0 && data_lrnHabitChart.get(0) != null) {	
						for(Map<String, Object> item : data_lrnHabit) {
							ArrayList<Map<String, Object>> detailChart = new ArrayList<>();			
							for(Map<String, Object> chart_item : data_lrnHabitChart) {
								if(item.get("studId").toString().equals(chart_item.get("studId").toString())) {
									detailChart.add(chart_item);									
								}								
							}
							item.put("lrnHabitChart", detailChart);					
							lrnHabit.add(item);
						}
					}
				}
			}
			
			data.put("stud", stud);
			data.put("lrnBasic", lrnBasic);
			data.put("lrnHabit", lrnHabit);
			setResult(dataKey, data);
		
		} else {
			setResult(msgKey, vu.getResult());
		}
    	return result;
    }
    

    
}
