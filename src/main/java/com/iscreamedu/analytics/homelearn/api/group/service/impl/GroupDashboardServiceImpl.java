package com.iscreamedu.analytics.homelearn.api.group.service.impl;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iscreamedu.analytics.homelearn.api.common.mapper.CommonMapperEsSocial;
import com.iscreamedu.analytics.homelearn.api.common.mapper.CommonMapperLrnDm;
import com.iscreamedu.analytics.homelearn.api.common.security.CipherUtil;
import com.iscreamedu.analytics.homelearn.api.common.service.ExternalAPIService;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationCode;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtil;
import com.iscreamedu.analytics.homelearn.api.group.service.GroupDashboardService;

@Service
public class GroupDashboardServiceImpl implements GroupDashboardService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GroupDashboardServiceImpl.class);
	
	@Autowired
	ExternalAPIService externalAPIservice;
	
	@Autowired
	CommonMapperEsSocial es_mapper;
	@Autowired
	CommonMapperLrnDm ms_mapper;
	
	private LinkedHashMap<String, Object> result; //Output Object
	private String msgKey = "msg";   //메시지 object key
	private String dataKey = "data"; //데이터 object key
	
	/**
	 * 기관정보
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map getAgencyInfo(Map<String, Object> paramMap) throws Exception{
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"orgId"}, paramMap);

		if(vu.isValid()) { 
			Map<String,Object> data = new HashMap<>();
			Map<String,Object> data_hl = new HashMap<>();
			Map<String,Object> data_es = new HashMap<>();
			Map<String,Object> data_es_org = new HashMap<>();
			Map<String,Object> data_ms = new HashMap<>();	 
			try {		
				//홈런 API 조회
				paramMap.put("apiName", "agencyServiceApiDetail");	       	
				data_hl = (Map<String, Object>) externalAPIservice.callExternalAPI(paramMap).get("data");
				if(data_hl != null && ((Map<String, Object>) externalAPIservice.callExternalAPI(paramMap).get(msgKey)).get("resultCode").equals(ValidationCode.SUCCESS.getCode())) {
					data.put("agn_nm", data_hl.get("agn_nm"));
					data.put("agn_id", data_hl.get("agn_id"));
					data.put("img_url", data_hl.get("img_url"));
					data.put("svc_open_de", data_hl.get("svc_open_de"));
					data.put("map_yn", data_hl.get("map_yn"));
					data.put("agn_code", data_hl.get("agn_code"));
					data.put("area_code1", data_hl.get("area_code1"));
					data.put("area_code2", data_hl.get("area_code2"));
					
					//demo 계정 관련 로직 
					String mapperName = (paramMap.get("orgId").toString().contains("demo")) ? "Group_ES_Demo" : "Group_ES";
					
					data_es = (Map<String, Object>) es_mapper.get(paramMap, mapperName + ".getSchoolType");
					data_ms = (Map<String, Object>) ms_mapper.get(paramMap, "Group_MS.getSchoolType");
					int cnt_es = (data_es != null && data_es.get("esTotStudCnt") != null && !data_es.get("esTotStudCnt").equals("")) ? (int) data_es.get("esTotStudCnt") : 0;
					int cnt_ms = (data_ms != null && data_ms.get("msTotStudCnt") != null && !data_ms.get("msTotStudCnt").equals("")) ? (int) data_ms.get("msTotStudCnt") : 0;
					if(mapperName.equals("Group_ES_Demo")) {
						data.put("sch_type", "es");
						data.put("ms_type_cnt", 0);
					} else {
						data.put("sch_type", (cnt_es >= cnt_ms ? "es" : "ms"));
						data.put("ms_type_cnt", cnt_ms);
					}
					data.put("es_type_cnt", cnt_es);
					setResult(dataKey, data);
				} else {
					setResult(msgKey, externalAPIservice.callExternalAPI(paramMap).get(msgKey));
				}
			} catch (Exception e) {
				LOGGER.error("[agencyServiceApiDetail] Error");
				LinkedHashMap message = new LinkedHashMap();	
				message.put("resultCode", ValidationCode.SYSTEM_ERROR.getCode());
				message.put("result", ValidationCode.SYSTEM_ERROR.getMessage());
				setResult(msgKey, message);
			}
		} else {
			setResult(msgKey, vu.getResult());
		}
		return result;
	}
	
	/**
	 * 년월, 주차 산출
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map selectGetYymmWk(Map<String, Object> paramMap) throws Exception{
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"currCon", "svcOpenDe"}, paramMap);

		if(vu.isValid()) { 			
			if(paramMap.get("currCon").toString().toLowerCase().equals("w")) {
				setResult(dataKey, ms_mapper.getList(paramMap, "Group_MS.selectGetYymmWk") );
			} else if(paramMap.get("currCon").toString().toLowerCase().equals("m")){
				setResult(dataKey, ms_mapper.getList(paramMap, "Group_MS.selectGetYymm") );	
			}
		} else {
			setResult(msgKey, vu.getResult());
		}
		return result;
	}
	
	/**
	 * 학년, 반 정보 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map getGradeClassYn(Map<String, Object> paramMap) throws Exception{
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"orgId"}, paramMap);
		
		if(vu.isValid()) {
			//demo 계정 관련 로직 
			String mapperName = (paramMap.get("orgId").toString().contains("demo")) ? "Group_ES_Demo" : "Group_ES";
			
			setResult(dataKey, es_mapper.getList(paramMap, mapperName + ".selectGradeClassYn"));
		} else {
			setResult(msgKey, vu.getResult());
		}
		return result;
	}
	
	/**
	 * 학년, 반 정보 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map getGradeClassInfo(Map<String, Object> paramMap) throws Exception{
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"orgId"}, paramMap);
		
		if(vu.isValid()) {
			//demo 계정 관련 로직 
			String mapperName = (paramMap.get("orgId").toString().contains("demo")) ? "Group_ES_Demo" : "Group_ES";
			if(paramMap.get("schType").toString().equals("ms")) {
				mapperName = "Group_MS";
				setResult(dataKey, ms_mapper.getList(paramMap, mapperName + ".selectGradeClassInfo"));
			}
			else {
				setResult(dataKey, es_mapper.getList(paramMap, mapperName + ".selectGradeClassInfo"));
			}
		} else {
			setResult(msgKey, vu.getResult());
		}
		return result;
	}
	
	/**
	 * 현황 메인
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map selectGroupStatusMain(Map<String, Object> paramMap) throws Exception{
		ValidationUtil vu = commonValidation(new ValidationUtil(), paramMap);
		if(vu.isValid()) {
			//demo 계정 관련 로직 
			String mapperName = (paramMap.get("orgId").toString().contains("demo")) ? "Group_ES_Demo" : "Group_ES";
			if(paramMap.get("schType").toString().equals("ms")) {
				mapperName = "Group_MS";
				if(paramMap.get("monthWord").equals("w")) {
					setResult(dataKey, ms_mapper.get(paramMap, mapperName + ".selectWeeklyGroupStatusMain") );
				} else if(paramMap.get("monthWord").equals("m")){
					setResult(dataKey, ms_mapper.get(paramMap, mapperName + ".selectMonthlyGroupStatusMain") );	
				}
			} else {
				if(paramMap.get("monthWord").equals("w")) {
					setResult(dataKey, es_mapper.get(paramMap, mapperName + ".selectWeeklyGroupStatusMain") );
				} else if(paramMap.get("monthWord").equals("m")){
					setResult(dataKey, es_mapper.get(paramMap, mapperName + ".selectMonthlyGroupStatusMain") );	
				}
			}
		} else {
			setResult(msgKey, vu.getResult());
		}
		return result;
	}
	
	/**
	 * 학습 차트
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map selectGroupLrnChart(Map<String, Object> paramMap) throws Exception{
		ValidationUtil vu = commonValidation(new ValidationUtil(), paramMap);
		if(vu.isValid()) { 
			//demo 계정 관련 로직 
			String mapperName = (paramMap.get("orgId").toString().contains("demo")) ? "Group_ES_Demo" : "Group_ES";
			if(paramMap.get("schType").toString().equals("ms")) { 
				mapperName = "Group_MS";
				if(paramMap.get("monthWord").equals("w")) {
					setResult(dataKey, ms_mapper.get(paramMap, mapperName + ".selectWeeklyGroupLrnChart"));
				} else if(paramMap.get("monthWord").equals("m")){
					setResult(dataKey, ms_mapper.get(paramMap, mapperName + ".selectMonthlyGroupLrnChart"));
				}
			} else {			
				if(paramMap.get("monthWord").equals("w")) {
					setResult(dataKey, es_mapper.get(paramMap, mapperName + ".selectWeeklyGroupLrnChart"));
				} else if(paramMap.get("monthWord").equals("m")){
					setResult(dataKey, es_mapper.get(paramMap, mapperName + ".selectMonthlyGroupLrnChart"));
				}
			}
		} else {
			setResult(msgKey, vu.getResult());
		}
		return result;
	}
	
	/**
	 * 메인 지도 정보
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map selectGroupMapInfo(Map<String, Object> paramMap) throws Exception{
		ValidationUtil vu = commonValidation(new ValidationUtil(), paramMap);
		if(vu.isValid()) { 
			//demo 계정 관련 로직 
			String mapperName = (paramMap.get("orgId").toString().contains("demo")) ? "Group_ES_Demo" : "Group_ES";
			if(paramMap.get("schType").toString().equals("ms")) {
				mapperName = "Group_MS";
				if(paramMap.get("monthWord").equals("w")) {
					setResult(dataKey, ms_mapper.getList(paramMap, mapperName + ".selectWeeklyGroupMapInfo"));
				} else if(paramMap.get("monthWord").equals("m")){
					setResult(dataKey, ms_mapper.getList(paramMap, mapperName + ".selectMonthlyGroupMapInfo") );	
				} 
			} else {
				if(paramMap.get("gcYn") != null) {
					if("Y".equals(paramMap.get("gcYn"))) {
						ArrayList<Map<String,Object>> orgList = (ArrayList<Map<String, Object>>) es_mapper.getList(paramMap, mapperName + ".selectGradeClassOrgId");
						ArrayList paramOrgList = new ArrayList<>();
						if(orgList.size() > 0) {
							for(Map<String, Object> item : orgList) {
								paramOrgList.add(item.get("depthOrgId"));
							}
							
							paramMap.put("orgList", paramOrgList);
						}
					}
				}
				
				if(paramMap.get("monthWord").equals("w")) {
					setResult(dataKey, es_mapper.getList(paramMap, mapperName + ".selectWeeklyGroupMapInfo"));
				} else if(paramMap.get("monthWord").equals("m")){
					setResult(dataKey, es_mapper.getList(paramMap, mapperName + ".selectMonthlyGroupMapInfo") );	
				} 
			}	
			
		} else {
			setResult(msgKey, vu.getResult());
		}
		return result;

	}
	
	/**
	 * 온라인 개학
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map selectGroupOnlineSchool(Map<String, Object> paramMap) throws Exception {
		ValidationUtil vu = commonValidation(new ValidationUtil(), paramMap);
		if(vu.isValid()) { 
			//demo 계정 관련 로직 
			String mapperName = (paramMap.get("orgId").toString().contains("demo")) ? "Group_ES_Demo" : "Group_ES";
			if(paramMap.get("schType").toString().equals("ms")) {
//				mapperName = "Group_MS";
				LinkedHashMap message = new LinkedHashMap(); // 메시지 코드
				message.put("resultCode", ValidationCode.NO_DATA.getCode());
				message.put("result", ValidationCode.NO_DATA.getMessage());
				setResult(msgKey, message);
			} else {
				if(paramMap.get("monthWord").equals("w")) {
					setResult(dataKey, es_mapper.getList(paramMap, mapperName + ".selectWeeklyGroupOnlineSchool") );
				} else if(paramMap.get("monthWord").equals("m")){
					setResult(dataKey, es_mapper.getList(paramMap, mapperName + ".selectMonthlyGroupOnlineSchool") );	
				}
			}
		} else {
			setResult(msgKey, vu.getResult());
		}
		return result;
	}
	
	/**
	 * 학생 리스트
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map selectGroupStudList(Map<String, Object> paramMap) throws Exception {
		ValidationUtil vu = commonValidation(new ValidationUtil(), paramMap);
		vu.checkRequired(new String[] {"startIdx", "schType"}, paramMap);		
		if(vu.isValid()) { 
			LinkedHashMap data = new LinkedHashMap(); // data 결과
			LinkedHashMap message = new LinkedHashMap(); // 메시지 코드
			
			//demo 계정 관련 로직 
			String mapperName = (paramMap.get("orgId").toString().contains("demo")) ? "Group_ES_Demo" : "Group_ES";
			if(paramMap.get("schType").toString().equals("ms")) {
				mapperName = "Group_MS";
			} else {			
				if(paramMap.get("gcYn") != null) {
					if("Y".equals(paramMap.get("gcYn"))) {
						ArrayList<Map<String,Object>> orgList = (ArrayList<Map<String, Object>>) es_mapper.getList(paramMap, mapperName + ".selectGradeClassOrgId");
						ArrayList paramOrgList = new ArrayList<>();
						if(orgList.size() > 0) {
							for(Map<String, Object> item : orgList) {
								paramOrgList.add(item.get("depthOrgId"));
							}
							
							paramMap.put("orgList", paramOrgList);
						}
					}
				}
			}

	        // keyword : LOGIN_ID 로 검색 -> dw-api 에서 학생아이디 추출후 검색
	        // keyword : STUD_NM 로 검색  -> dw-api 에서 학생아이디 리스트 추출후 검색
			// 검색 : keyword/searchType : studId(LOGIN_ID), STUD_NM		
			if(!mapperName.equals("Group_ES_Demo")) {
		        if(paramMap.get("keyword") != null && paramMap.get("keyword") != "" ) {
		        	//홈런 API 조회
	    			ArrayList<Map<String,Object>> data_hl = new ArrayList();
					Map<String, Object> paramMap_ex = new HashMap<>();
					paramMap_ex.put("apiName", "students");	      
					if(paramMap.get("searchType").equals("studId")) {
			        	paramMap_ex.put("login_ids", paramMap.get("keyword"));
					} else {
						paramMap_ex.put("stu_nms", paramMap.get("keyword"));
					}
					
					try {	
						data_hl = (ArrayList<Map<String,Object>>) externalAPIservice.callExternalAPI(paramMap_ex).get("data");
						if(data_hl != null) {
							//paramMap.put("studId", data_hl.get("STU_ID").toString());
							ArrayList paramStudList = new ArrayList<>();
							if(data_hl.size() > 0) {
								for(Map<String, Object> item : data_hl) {
									paramStudList.add(item.get("STU_ID"));
								}								
								paramMap.put("studList", paramStudList);
							}
						} 
					} catch (Exception e) {
						LOGGER.error("selectGroupStudList 홈런 API 조회[students] Error");
						paramMap.put("studList", null);
					}	        	
		        } else {
		        	paramMap.put("studList", null);
		        }
			} else {
				paramMap.put("studList", null);
			}
			
			// 검색대상 조회 실패시 NO_DATA
			if(!mapperName.equals("Group_ES_Demo") && paramMap.get("keyword") != null && paramMap.get("keyword") != "" && paramMap.get("studList") == null) {
				message.put("resultCode", ValidationCode.NO_DATA.getCode());
				message.put("result", ValidationCode.NO_DATA.getMessage());
				setResult(msgKey,message);
			} else {
		        // selectWeeklyGroupStudList / selectMonthlyGroupStudList (2022-01-27)
		     	// LOGIN_ID, STUD_NM DB 조회 및 정렬 부분 제외 
		        // default 정렬 lrnSignal, studId 순 임의 지정함. (2022-01-27)
				Map<String, Object> page = new LinkedHashMap<>();
				if(paramMap.get("monthWord").equals("w")) { // 주간
					if(paramMap.get("schType").toString().equals("ms")) {
						data.put("list", ms_mapper.getList(paramMap, mapperName + ".selectWeeklyGroupStudList"));
						
					} else {
						data.put("list", es_mapper.getList(paramMap, mapperName + ".selectWeeklyGroupStudList"));
					}
					if(data.get("list") instanceof List && ((List)data.get("list")).size() != 0) {
						try {
							listEncodeS( (List<HashMap<String, Object>>) data.get("list"), mapperName, paramMap);
							
							message.put("resultCode", ValidationCode.SUCCESS.getCode());
							message.put("result", ValidationCode.SUCCESS.getMessage());
							if(paramMap.get("studList") == null) {
								if(paramMap.get("schType").toString().equals("ms")) {
									page = (Map<String, Object>) ms_mapper.get(paramMap, mapperName + ".selectWeeklyGroupStudListCnt");
								} else {
									page = (Map<String, Object>) es_mapper.get(paramMap, mapperName + ".selectWeeklyGroupStudListCnt");
								}
								data.put("pageCount", page.get("pageCnt"));
								data.put("totalCount", page.get("studCnt"));
							} else {
								data.put("pageCount", 1);
								data.put("totalCount", ((List)data.get("list")).size());								
							}
							setResult(msgKey,message);
							setResult(dataKey,data);
						} catch (Exception e) {
							LOGGER.error("Error : "+e.getMessage());
							message.put("resultCode", ValidationCode.SYSTEM_ERROR.getCode());
							message.put("result", ValidationCode.SYSTEM_ERROR.getMessage());
							setResult(msgKey, message);
						} 
					} else {
						message.put("resultCode", ValidationCode.NO_DATA.getCode());
						message.put("result", ValidationCode.NO_DATA.getMessage());
						setResult(msgKey,message);
					}
					
				} else if(paramMap.get("monthWord").equals("m")){ // 월간
					if(paramMap.get("schType").toString().equals("ms")) {
						data.put("list", ms_mapper.getList(paramMap, mapperName + ".selectMonthlyGroupStudList"));
					} else {
						data.put("list", es_mapper.getList(paramMap, mapperName + ".selectMonthlyGroupStudList"));
					}
					if(data.get("list") instanceof List && ((List)data.get("list")).size() != 0) {
						try {
							listEncodeS( (List<HashMap<String, Object>>) data.get("list"), mapperName, paramMap);
						
							message.put("resultCode", ValidationCode.SUCCESS.getCode());
							message.put("result", ValidationCode.SUCCESS.getMessage());
							if(paramMap.get("studList") == null) {
								if(paramMap.get("schType").toString().equals("ms")) {
									page = (Map<String, Object>) ms_mapper.get(paramMap, mapperName + ".selectMonthlyGroupStudListCnt");
								} else {
									page = (Map<String, Object>) es_mapper.get(paramMap, mapperName + ".selectMonthlyGroupStudListCnt");
								}
								data.put("pageCount", page.get("pageCnt"));
								data.put("totalCount", page.get("studCnt"));
							} else {
								data.put("pageCount", 1);
								data.put("totalCount", ((List)data.get("list")).size());								
							}
							setResult(msgKey,message);
							setResult(dataKey,data);
						} catch (Exception e) {
							LOGGER.error("Error : "+e.getMessage());
							message.put("resultCode", ValidationCode.SYSTEM_ERROR.getCode());
							message.put("result", ValidationCode.SYSTEM_ERROR.getMessage());
							setResult(msgKey, message);
						} 
					} else {
						message.put("resultCode", ValidationCode.NO_DATA.getCode());
						message.put("result", ValidationCode.NO_DATA.getMessage());
						setResult(msgKey,message);
					}
				}
			}
			
		} else {
			setResult(msgKey, vu.getResult());
		}
		return result;
	}
	
	/**
	 * 지역 리스트
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map selectGroupLocalList(Map<String, Object> paramMap) throws Exception{
		ValidationUtil vu = commonValidation(new ValidationUtil(), paramMap);
		if(vu.isValid()) { 
			//demo 계정 관련 로직 
			String mapperName = (paramMap.get("orgId").toString().contains("demo")) ? "Group_ES_Demo" : "Group_ES";
			if(paramMap.get("schType").toString().equals("ms")) mapperName = "Group_MS";
			
			if(paramMap.get("schType").toString().equals("es")) {
				if(paramMap.get("gcYn") != null) {
					if("Y".equals(paramMap.get("gcYn"))) {
						ArrayList<Map<String,Object>> orgList = (ArrayList<Map<String, Object>>) es_mapper.getList(paramMap, mapperName + ".selectGradeClassOrgId");
						ArrayList paramOrgList = new ArrayList<>();
						if(orgList.size() > 0) {
							for(Map<String, Object> item : orgList) {
								paramOrgList.add(item.get("depthOrgId"));
							}
							
							paramMap.put("orgList", paramOrgList);
						}
					}
				}
			}
			
			if(paramMap.get("monthWord").equals("w")) {
				if(paramMap.get("schType").toString().equals("ms")) {
					setResult(dataKey, ms_mapper.getList(paramMap, mapperName + ".selectWeeklyGroupLocalList") );
				} else {
					setResult(dataKey, es_mapper.getList(paramMap, mapperName + ".selectWeeklyGroupLocalList") );
				}
			} else if(paramMap.get("monthWord").equals("m")){
				if(paramMap.get("schType").toString().equals("ms")) {
					setResult(dataKey, ms_mapper.getList(paramMap, mapperName + ".selectMonthlyGroupLocalList") );	
				} else {
					setResult(dataKey, es_mapper.getList(paramMap, mapperName + ".selectMonthlyGroupLocalList") );	
				}
			}
		} else {
			setResult(msgKey, vu.getResult());
		}
		return result;
	}
	
	/**
	 * 메인 지역
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map selectGroupMainLocal(Map<String, Object> paramMap){
		
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"schType"}, paramMap);
		if(vu.isValid()) { 
			if((paramMap.containsKey("areaCode1") && paramMap.containsKey("areaCode2"))) {
				if(!paramMap.get("areaCode1").equals("") && paramMap.get("areaCode1").toString().length() != 0) {
					if(paramMap.get("schType").toString().equals("ms")) {
						setResult(dataKey, ms_mapper.get(paramMap, "Group_MS.selectGroupMainLocal"));
					} else {
						setResult(dataKey, es_mapper.get(paramMap, "Group_ES.selectGroupMainLocal"));
	
						if((String) ((HashMap<String,Object>) result.get("msg")).get("resultCode")!="2000") { // DB 조회 결과 null 아닐 경우
							Object li_hash_map =  new HashMap(); // DB 조회 값
							li_hash_map = result.get("data");
							String ctpCode = ((String) ((Map<String,Object>) li_hash_map).get("ctpCode"));
						
							ctpCode = checkCode(ctpCode);
							((HashMap<String, Object>) li_hash_map).put("ctpCode",ctpCode);
						}
					}
				} else {
					// input "areaCode1", "areaCode2" key 값  존재  && "areaCode1", "areaCode2" value null
					LinkedHashMap message = new LinkedHashMap();
					LinkedHashMap data = new LinkedHashMap();
					result = new LinkedHashMap();
					data.put("grade",0);
					data.put("level",0);
					data.put("ctpCode","0");
					data.put("localNm","전국");
					message.put("resultCode", ValidationCode.SUCCESS.getCode());
					message.put("result", ValidationCode.SUCCESS.getMessage());
					result.put(msgKey,message);
					result.put(dataKey,data);
					return result;
				}
			}
		} else {
			setResult(msgKey, vu.getResult());
		}
		return result;
	}
	
	/**
	 * 학습지표 (ORG-LR-001)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map getLrnStt(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"searchStartYymm", "searchEndYymm", "orgId", "schType"}, paramMap);
		if(vu.isValid()) { 
			int searchStartYymm =Integer.parseInt(paramMap.get("searchStartYymm").toString());
			int searchEndYymm =Integer.parseInt(paramMap.get("searchEndYymm").toString());
			Boolean wkYn = false;
			
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			
			String toDateStr = paramMap.get("searchStartYymm").toString() + "01";
			String fromDateStr = paramMap.get("searchEndYymm").toString() + "01";
			
			Date toDate = format.parse(toDateStr);
			Date fromDate = format.parse(fromDateStr);
			
			long baseDay = 24 * 60 * 60 * 1000;
			long baseMonth = baseDay * 30;
			long baseYear = baseMonth * 12;
			
			long calDate = fromDate.getTime() - toDate.getTime();
			long diffMonth = calDate / baseMonth;
			
			if(searchStartYymm == searchEndYymm || diffMonth < 2) {
				wkYn = true;
			}
			
			Map<String, Object> data = new LinkedHashMap<>();
			Map<String, Object> lrnStt = new LinkedHashMap<>();
			Map<String,Object> lrnSttResult = new LinkedHashMap<>();
			
			//demo 계정 관련 로직 
			String mapperName = (paramMap.get("orgId").toString().contains("demo")) ? "Group_ES_Demo" : "Group_ES";
			if(paramMap.get("schType").toString().equals("ms")) mapperName = "Group_MS";		
			if(paramMap.get("schType").toString().equals("ms")) {
				if(wkYn) {
					lrnSttResult = (Map) ms_mapper.get(paramMap, mapperName + ".selectWeekLrnStt");
				} else {
					lrnSttResult = (Map) ms_mapper.get(paramMap, mapperName + ".selectMonthLrnStt");
				}
			} else {
				if(wkYn) {
					lrnSttResult = (Map) es_mapper.get(paramMap, mapperName + ".selectWeekLrnStt");
				} else {
					lrnSttResult = (Map) es_mapper.get(paramMap, mapperName + ".selectMonthLrnStt");
				}
			}
			if(lrnSttResult != null) {
				String[] categoryList = new String[] {};
				String[] studCntList = new String[] {};
				String[] avgAttRtList = new String[] {};
				String[] avgExRtList = new String[] {};
				String[] avgCrtRtList = new String[] {};
				String[] avgDayLrnSecList = new String[] {};
				
				if(lrnSttResult.get("categorySp") != null && !lrnSttResult.get("categorySp").equals("")) {
					categoryList = lrnSttResult.get("categorySp").toString().split(",");
				}
				if(lrnSttResult.get("studCntSp") != null && !lrnSttResult.get("studCntSp").equals("")) {
					studCntList = lrnSttResult.get("studCntSp").toString().split(",");
				}
				if(lrnSttResult.get("avgAttRtSp") != null && !lrnSttResult.get("avgAttRtSp").equals("")) {
					avgAttRtList = lrnSttResult.get("avgAttRtSp").toString().split(",");
				}
				if(lrnSttResult.get("avgExRtSp") != null && !lrnSttResult.get("avgExRtSp").equals("")) {
					avgExRtList = lrnSttResult.get("avgExRtSp").toString().split(",");
				}
				if(lrnSttResult.get("avgCrtRtSp") != null && !lrnSttResult.get("avgCrtRtSp").equals("")) {
					avgCrtRtList = lrnSttResult.get("avgCrtRtSp").toString().split(",");
				}
				if(lrnSttResult.get("avgDayLrnSecSp") != null && !lrnSttResult.get("avgDayLrnSecSp").equals("")) {
					avgDayLrnSecList = lrnSttResult.get("avgDayLrnSecSp").toString().split(",");
				}
				
				lrnStt.put("category", categoryList);
				lrnStt.put("studCnt", studCntList);
				lrnStt.put("avgAttRt", avgAttRtList);
				lrnStt.put("avgExRt", avgExRtList);
				lrnStt.put("avgCrtRt", avgCrtRtList);
				lrnStt.put("avgDayLrnSec", avgDayLrnSecList);
				
				data.put("lrnStt", lrnStt);
			}
			setResult(dataKey, data);
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	
	/**
	 * 학습지표 추이 (ORG-LR-002)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map getLrnSttTrend(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"searchStartYymm", "searchEndYymm", "orgId", "schType"}, paramMap);
		if(vu.isValid()) { 
		
			int searchStartYymm =Integer.parseInt(paramMap.get("searchStartYymm").toString());
			int searchEndYymm =Integer.parseInt(paramMap.get("searchEndYymm").toString());
			Boolean wkYn = false;
			
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			
			String toDateStr = paramMap.get("searchStartYymm").toString() + "01";
			String fromDateStr = paramMap.get("searchEndYymm").toString() + "01";
			
			Date toDate = format.parse(toDateStr);
			Date fromDate = format.parse(fromDateStr);
			
			long baseDay = 24 * 60 * 60 * 1000;
			long baseMonth = baseDay * 30;
			long baseYear = baseMonth * 12;
			
			long calDate = fromDate.getTime() - toDate.getTime();
			long diffMonth = calDate / baseMonth;
			
			if(searchStartYymm == searchEndYymm || diffMonth < 2) {
				wkYn = true;
			}
			
			Map<String, Object> data = new LinkedHashMap<>();
			List<Map<String, Object>> lrnSttTrend = new ArrayList();
			List<Map<String, Object>> lrnSttTrendResult = new ArrayList();
			
			//demo 계정 관련 로직 
			String mapperName = (paramMap.get("orgId").toString().contains("demo")) ? "Group_ES_Demo" : "Group_ES";
			if(paramMap.get("schType").toString().equals("ms")) {
				mapperName = "Group_MS";
				if(wkYn) {
					lrnSttTrendResult = (List) ms_mapper.getList(paramMap, mapperName + ".selectWeekLrnSttTrend");
				} else {
					lrnSttTrendResult = (List) ms_mapper.getList(paramMap, mapperName + ".selectMonthLrnSttTrend");
				}
			} else {			
				if(wkYn) {
					lrnSttTrendResult = (List) es_mapper.getList(paramMap, mapperName + ".selectWeekLrnSttTrend");
				} else {
					lrnSttTrendResult = (List) es_mapper.getList(paramMap, mapperName + ".selectMonthLrnSttTrend");
				}
			}
			
			if(lrnSttTrendResult.size() > 0) {
				
				for(int i = 0; i < lrnSttTrendResult.size(); i++) {
					Map<String, Object> lrnSttTrendMap = new LinkedHashMap<>();
					
					lrnSttTrendMap.put("category", lrnSttTrendResult.get(i).get("category"));
					lrnSttTrendMap.put("avgAttRt", lrnSttTrendResult.get(i).get("avgAttRt"));
					lrnSttTrendMap.put("avgExRt", lrnSttTrendResult.get(i).get("avgExRt"));
					lrnSttTrendMap.put("avgCrtRt", lrnSttTrendResult.get(i).get("avgCrtRt"));
					lrnSttTrendMap.put("studCnt", lrnSttTrendResult.get(i).get("studCnt"));
					
					lrnSttTrend.add(lrnSttTrendMap);
				}
				
				data.put("lrnSttTrend", lrnSttTrend);
			}
		
			setResult(dataKey, data);
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}

	/**
	 * 기관학습현황 (ORG-LR-003)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map getOrgLrnStt(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"searchStartYymm", "searchEndYymm", "orgId", "schType"}, paramMap);
		if(vu.isValid()) { //1.필수값 체크
		
			Map<String, Object> data = new LinkedHashMap<>();
			ArrayList<Object> orgLrnStt = new ArrayList<Object>();
			
			Map<String, Object> attRtMap = new LinkedHashMap<>();
			Map<String, Object> exRtMap = new LinkedHashMap<>();
			Map<String, Object> crtRtMap = new LinkedHashMap<>();
			Map<String, Object> avgDayLrnSecMap = new LinkedHashMap<>();

			Map<String, Object> orgLrnSttResult = new LinkedHashMap();
			
			//demo 계정 관련 로직 
			String mapperName = (paramMap.get("orgId").toString().contains("demo")) ? "Group_ES_Demo" : "Group_ES";
			if(paramMap.get("schType").toString().equals("ms")) {
				mapperName = "Group_MS";
				orgLrnSttResult = (Map) ms_mapper.get(paramMap, mapperName + ".selectOrgLrnStt");
			} else {
				orgLrnSttResult = (Map) es_mapper.get(paramMap, mapperName + ".selectOrgLrnStt");
			}
			
			if(orgLrnSttResult != null) {
				attRtMap.put("category", "attRt");
				exRtMap.put("category", "exRt");
				crtRtMap.put("category", "crtRt");
				avgDayLrnSecMap.put("category", "avgDayLrnSec");
				
				if(orgLrnSttResult.get("orgAvgAttRt") != null && !orgLrnSttResult.get("orgAvgAttRt").equals("")) {
					attRtMap.put("orgAvgAttRt", orgLrnSttResult.get("orgAvgAttRt"));
				}
				if(orgLrnSttResult.get("top10AvgAttRt") != null && !orgLrnSttResult.get("top10AvgAttRt").equals("")) {
					attRtMap.put("top10AvgAttRt", orgLrnSttResult.get("top10AvgAttRt"));
				}
				
				if(orgLrnSttResult.get("orgAvgExRt") != null && !orgLrnSttResult.get("orgAvgExRt").equals("")) {
					exRtMap.put("orgAvgExRt", orgLrnSttResult.get("orgAvgExRt"));
				}
				if(orgLrnSttResult.get("top10AvgExRt") != null && !orgLrnSttResult.get("top10AvgExRt").equals("")) {
					exRtMap.put("top10AvgExRt", orgLrnSttResult.get("top10AvgExRt"));
				}
				
				if(orgLrnSttResult.get("orgAvgCrtRt") != null && !orgLrnSttResult.get("orgAvgCrtRt").equals("")) {
					crtRtMap.put("orgAvgCrtRt", orgLrnSttResult.get("orgAvgCrtRt"));
				}
				if(orgLrnSttResult.get("top10AvgCrtRt") != null && !orgLrnSttResult.get("top10AvgCrtRt").equals("")) {
					crtRtMap.put("top10AvgCrtRt", orgLrnSttResult.get("top10AvgCrtRt"));
				}
				
				if(orgLrnSttResult.get("orgAvgDayLrnSec") != null && !orgLrnSttResult.get("orgAvgDayLrnSec").equals("")) {
					avgDayLrnSecMap.put("orgAvgDayLrnSec", orgLrnSttResult.get("orgAvgDayLrnSec"));
				}
				if(orgLrnSttResult.get("top10AvgDayLrnSec") != null && !orgLrnSttResult.get("top10AvgDayLrnSec").equals("")) {
					avgDayLrnSecMap.put("top10AvgDayLrnSec", orgLrnSttResult.get("top10AvgDayLrnSec"));
				}
				
				orgLrnStt.add(attRtMap);
				orgLrnStt.add(exRtMap);
				orgLrnStt.add(crtRtMap);
				orgLrnStt.add(avgDayLrnSecMap);
				
				data.put("orgLrnStt", orgLrnStt);
			}
			
			setResult(dataKey, data);
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	
	/**
	 * 홈런 활용 (ORG-LR-004)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map getHlUtilization(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"searchStartYymm", "searchEndYymm", "orgId", "schType"}, paramMap);
		if(vu.isValid()) { //1.필수값 체크
		
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			
			String toDateStr = paramMap.get("searchStartYymm").toString() + "01";
			String fromDateStr = paramMap.get("searchEndYymm").toString() + "01";
			
			Date toDate = format.parse(toDateStr);
			Date fromDate = format.parse(fromDateStr);
			
			long baseDay = 24 * 60 * 60 * 1000;
			long baseMonth = baseDay * 30;
			long baseYear = baseMonth * 12;
			
			long calDate = fromDate.getTime() - toDate.getTime();
			long diffMonth = calDate / baseMonth;
			
			paramMap.put("diffMonth", diffMonth);
			
			Map<String, Object> data = new LinkedHashMap<>();
			Map<String, Object> hlUtilization = new LinkedHashMap<>();
			
			ArrayList<Object> crtRtExcellentGrp = new ArrayList<Object>();
			ArrayList<Object> crtRtNeedEffortGrp = new ArrayList<Object>();
			ArrayList<Object> exRtExcellentGrp = new ArrayList<Object>();
			ArrayList<Object> exRtNeedEffortGrp = new ArrayList<Object>();
			List<Map<String,Object>> crtRtExcellentGrpResult = new ArrayList();
			List<Map<String,Object>> crtRtNeedEffortGrpResult = new ArrayList();
			List<Map<String,Object>> exRtExcellentGrpResult = new ArrayList();
			List<Map<String,Object>> exRtNeedEffortGrpResult = new ArrayList();
			
			//demo 계정 관련 로직 
			String mapperName = (paramMap.get("orgId").toString().contains("demo")) ? "Group_ES_Demo" : "Group_ES";
			if(paramMap.get("schType").toString().equals("ms")) { 
				mapperName = "Group_MS";
			
				crtRtExcellentGrpResult = (List) ms_mapper.getList(paramMap, mapperName + ".selectHlUtilizationCrtRtExcellentGrp");
				crtRtNeedEffortGrpResult = (List) ms_mapper.getList(paramMap, mapperName + ".selectHlUtilizationCrtRtNeedEffortGrp");
				exRtExcellentGrpResult = (List) ms_mapper.getList(paramMap, mapperName + ".selectHlUtilizationExRtExcellentGrp");
				exRtNeedEffortGrpResult = (List) ms_mapper.getList(paramMap, mapperName + ".selectHlUtilizationExRtNeedEffortGrp");
			
			} else {
			
				if(paramMap.get("gcYn") != null) {
					if("Y".equals(paramMap.get("gcYn"))) {
						ArrayList<Map<String,Object>> orgList = (ArrayList<Map<String, Object>>) es_mapper.getList(paramMap, mapperName + ".selectGradeClassOrgId");
						ArrayList paramOrgList = new ArrayList<>();
						if(orgList.size() > 0) {
							for(Map<String, Object> item : orgList) {
								paramOrgList.add(item.get("depthOrgId"));
							}
							
							paramMap.put("orgList", paramOrgList);
						}
					}
				}
				
				crtRtExcellentGrpResult = (List) es_mapper.getList(paramMap, mapperName + ".selectHlUtilizationCrtRtExcellentGrp");
				crtRtNeedEffortGrpResult = (List) es_mapper.getList(paramMap, mapperName + ".selectHlUtilizationCrtRtNeedEffortGrp");
				exRtExcellentGrpResult = (List) es_mapper.getList(paramMap, mapperName + ".selectHlUtilizationExRtExcellentGrp");
				exRtNeedEffortGrpResult = (List) es_mapper.getList(paramMap, mapperName + ".selectHlUtilizationExRtNeedEffortGrp");
				
			}
			
			//홈런 API 조회
	        ArrayList<Map<String,Object>> data_hl = new ArrayList();
			Map<String, Object> paramMap_ex= new HashMap<>();
			try {
					
				if(crtRtExcellentGrpResult.size() > 0) {
					if(!mapperName.equals("Group_ES_Demo")) {
						paramMap_ex.put("apiName", "students");	     
						ArrayList<String> stu_ids_list = new ArrayList();
						String stu_ids = "";
						for(Map<String,Object> studMap : crtRtExcellentGrpResult) {
				        	if(studMap.get("studId") != null && !"".equals(studMap.get("studId"))) {
				        		String sId = studMap.get("studId").toString();
				        		stu_ids_list.add(sId);       
				        	}
				        }
						for(int i = 0; i < stu_ids_list.size(); i++) {
							if(i == 0) {
								stu_ids += stu_ids_list.get(i);
							} else {
								stu_ids += ","+stu_ids_list.get(i);
							}
						}
						paramMap_ex.put("stu_ids", stu_ids);
						data_hl = (ArrayList<Map<String, Object>>) externalAPIservice.callExternalAPI(paramMap_ex).get("data");
						for(Map<String,Object> studMap : crtRtExcellentGrpResult) {
							if(studMap.get("studId") != null && !"".equals(studMap.get("studId"))) {
				        		String sId = studMap.get("studId").toString();
				        		if(data_hl != null) {
						    		for(Map<String, Object> item : data_hl) {
						    			if(sId.equals(item.get("STU_ID").toString())) {
						        			studMap.put("studNm", new String(item.get("STUD_NM").toString().getBytes("8859_1"), "UTF-8"));
						        			studMap.remove("studId");
						    			}
									}
								} else {
									LOGGER.error("getHlUtilization > crtRtExcellentGrpResult 홈런 API 조회[students] 매칭 실패 sId : "+sId);
									studMap.put("studNm", null);
				        			studMap.remove("studId");
								}
				        	}
						}
					}
					
					for(int i = 0; i < crtRtExcellentGrpResult.size(); i++) {
						Map<String, Object> crtRtExcellentGrpResultMap = new LinkedHashMap<>();
						crtRtExcellentGrpResultMap.put("studNm", crtRtExcellentGrpResult.get(i).get("studNm"));
						crtRtExcellentGrpResultMap.put("avgCrtRt", crtRtExcellentGrpResult.get(i).get("avgCrtRt"));
						
						crtRtExcellentGrp.add(crtRtExcellentGrpResultMap);
					}
				}
				hlUtilization.put("crtRtExcellentGrp", crtRtExcellentGrp);
				
				if(crtRtNeedEffortGrpResult.size() > 0) {
					paramMap_ex.put("apiName", "students");	     
					ArrayList<String> stu_ids_list = new ArrayList();
					String stu_ids = "";
					for(Map<String,Object> studMap : crtRtNeedEffortGrpResult) {
			        	if(studMap.get("studId") != null && !"".equals(studMap.get("studId"))) {
			        		String sId = studMap.get("studId").toString();
			        		stu_ids_list.add(sId);       
			        	}
			        }
					for(int i = 0; i < stu_ids_list.size(); i++) {
						if(i == 0) {
							stu_ids += stu_ids_list.get(i);
						} else {
							stu_ids += ","+stu_ids_list.get(i);
						}
					}
					paramMap_ex.put("stu_ids", stu_ids);
					data_hl = (ArrayList<Map<String, Object>>) externalAPIservice.callExternalAPI(paramMap_ex).get("data");
					for(Map<String,Object> studMap : crtRtNeedEffortGrpResult) {
						if(studMap.get("studId") != null && !"".equals(studMap.get("studId"))) {
			        		String sId = studMap.get("studId").toString();
			        		if(data_hl != null) {
					    		for(Map<String, Object> item : data_hl) {
					    			if(sId.equals(item.get("STU_ID").toString())) {
					        			studMap.put("studNm", new String(item.get("STUD_NM").toString().getBytes("8859_1"), "UTF-8"));
					        			studMap.remove("studId");
					    			}
								}
							} else {
								LOGGER.error("getHlUtilization > crtRtNeedEffortGrpResult 홈런 API 조회[students] 매칭 실패 sId : "+sId);
								studMap.put("studNm", null);
			        			studMap.remove("studId");
							}
			        	}
					}
					for(int i = 0; i < crtRtNeedEffortGrpResult.size(); i++) {
						Map<String, Object> crtRtNeedEffortGrpResultMap = new LinkedHashMap<>();
						crtRtNeedEffortGrpResultMap.put("studNm", crtRtNeedEffortGrpResult.get(i).get("studNm"));
						crtRtNeedEffortGrpResultMap.put("avgCrtRt", crtRtNeedEffortGrpResult.get(i).get("avgCrtRt"));
						
						crtRtNeedEffortGrp.add(crtRtNeedEffortGrpResultMap);
					}
				}
				hlUtilization.put("crtRtNeedEffortGrp", crtRtNeedEffortGrp);
				
				if(exRtExcellentGrpResult.size() > 0) {
					paramMap_ex.put("apiName", "students");	     
					ArrayList<String> stu_ids_list = new ArrayList();
					String stu_ids = "";
					for(Map<String,Object> studMap : exRtExcellentGrpResult) {
			        	if(studMap.get("studId") != null && !"".equals(studMap.get("studId"))) {
			        		String sId = studMap.get("studId").toString();
			        		stu_ids_list.add(sId);       
			        	}
			        }
					for(int i = 0; i < stu_ids_list.size(); i++) {
						if(i == 0) {
							stu_ids += stu_ids_list.get(i);
						} else {
							stu_ids += ","+stu_ids_list.get(i);
						}
					}
					paramMap_ex.put("stu_ids", stu_ids);
					data_hl = (ArrayList<Map<String, Object>>) externalAPIservice.callExternalAPI(paramMap_ex).get("data");
					for(Map<String,Object> studMap : exRtExcellentGrpResult) {
						if(studMap.get("studId") != null && !"".equals(studMap.get("studId"))) {
			        		String sId = studMap.get("studId").toString();
			        		if(data_hl != null) {
					    		for(Map<String, Object> item : data_hl) {
					    			if(sId.equals(item.get("STU_ID").toString())) {
					        			studMap.put("studNm", new String(item.get("STUD_NM").toString().getBytes("8859_1"), "UTF-8"));
					        			studMap.remove("studId");
					    			}
								}
							} else {
								LOGGER.error("getHlUtilization > exRtExcellentGrpResult 홈런 API 조회[students] 매칭 실패 sId : "+sId);
								studMap.put("studNm", null);
			        			studMap.remove("studId");
							}
			        	}
					}
					for(int i = 0; i < exRtExcellentGrpResult.size(); i++) {
						Map<String, Object> exRtExcellentGrpResultMap = new LinkedHashMap<>();
						exRtExcellentGrpResultMap.put("studNm", exRtExcellentGrpResult.get(i).get("studNm"));
						exRtExcellentGrpResultMap.put("avgExRt", exRtExcellentGrpResult.get(i).get("avgExRt"));
						
						exRtExcellentGrp.add(exRtExcellentGrpResultMap);
					}
				}
				hlUtilization.put("exRtExcellentGrp", exRtExcellentGrp);
				
				if(exRtNeedEffortGrpResult.size() > 0) {
					paramMap_ex.put("apiName", "students");	     
					ArrayList<String> stu_ids_list = new ArrayList();
					String stu_ids = "";
					for(Map<String,Object> studMap : exRtNeedEffortGrpResult) {
			        	if(studMap.get("studId") != null && !"".equals(studMap.get("studId"))) {
			        		String sId = studMap.get("studId").toString();
			        		stu_ids_list.add(sId);       
			        	}
			        }
					for(int i = 0; i < stu_ids_list.size(); i++) {
						if(i == 0) {
							stu_ids += stu_ids_list.get(i);
						} else {
							stu_ids += ","+stu_ids_list.get(i);
						}
					}
					paramMap_ex.put("stu_ids", stu_ids);
					data_hl = (ArrayList<Map<String, Object>>) externalAPIservice.callExternalAPI(paramMap_ex).get("data");
					for(Map<String,Object> studMap : exRtNeedEffortGrpResult) {
						if(studMap.get("studId") != null && !"".equals(studMap.get("studId"))) {
			        		String sId = studMap.get("studId").toString();
			        		if(data_hl != null) {
					    		for(Map<String, Object> item : data_hl) {
					    			if(sId.equals(item.get("STU_ID").toString())) {
					        			studMap.put("studNm", new String(item.get("STUD_NM").toString().getBytes("8859_1"), "UTF-8"));
					        			studMap.remove("studId");
					    			}
								}
							} else {
								LOGGER.error("getHlUtilization > exRtNeedEffortGrpResult 홈런 API 조회[students] 매칭 실패 sId : "+sId);
								studMap.put("studNm", null);
			        			studMap.remove("studId");
							}
			        	}
					}
					for(int i = 0; i < exRtNeedEffortGrpResult.size(); i++) {
						Map<String, Object> exRtNeedEffortGrpResultMap = new LinkedHashMap<>();
						exRtNeedEffortGrpResultMap.put("studNm", exRtNeedEffortGrpResult.get(i).get("studNm"));
						exRtNeedEffortGrpResultMap.put("avgExRt", exRtNeedEffortGrpResult.get(i).get("avgExRt"));
						
						exRtNeedEffortGrp.add(exRtNeedEffortGrpResultMap);
					}
				}
				hlUtilization.put("exRtNeedEffortGrp", exRtNeedEffortGrp);
							
				data.put("hlUtilization", hlUtilization);
			
				setResult(dataKey, data);
			} catch (Exception e) {
				LOGGER.error("getHlUtilization Error");
				LinkedHashMap message = new LinkedHashMap();	
				message.put("resultCode", ValidationCode.SYSTEM_ERROR.getCode());
				message.put("result", ValidationCode.SYSTEM_ERROR.getMessage());
				setResult(msgKey, message);
			}
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}

	/**
	 * 학습 분석 신호 변화 (ORG-LR-005)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map getLrnSignalTrend(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"searchStartYymm", "searchEndYymm", "orgId", "schType"}, paramMap);
		if(vu.isValid()) { //1.필수값 체크
				
			int searchStartYymm =Integer.parseInt(paramMap.get("searchStartYymm").toString());
			int searchEndYymm =Integer.parseInt(paramMap.get("searchEndYymm").toString());
			Boolean wkYn = false;
			
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			
			String toDateStr = paramMap.get("searchStartYymm").toString() + "01";
			String fromDateStr = paramMap.get("searchEndYymm").toString() + "01";
			
			Date toDate = format.parse(toDateStr);
			Date fromDate = format.parse(fromDateStr);
			
			long baseDay = 24 * 60 * 60 * 1000;
			long baseMonth = baseDay * 30;
			long baseYear = baseMonth * 12;
			
			long calDate = fromDate.getTime() - toDate.getTime();
			long diffMonth = calDate / baseMonth;
			
			if(searchStartYymm == searchEndYymm || diffMonth < 2) {
				wkYn = true;
			}
			
			Map<String, Object> data = new LinkedHashMap<>();
			List<Map<String, Object>> lrnSignalTrend = new ArrayList();
			List<Map<String, Object>> lrnSignalTrendResult = new ArrayList();
			Map<String, Object> lrnSignalTrendMap = new LinkedHashMap<>();
			
			//demo 계정 관련 로직 
			String mapperName = (paramMap.get("orgId").toString().contains("demo")) ? "Group_ES_Demo" : "Group_ES";
			if(paramMap.get("schType").toString().equals("ms"))  {
				mapperName = "Group_MS";
				if(wkYn) {
					lrnSignalTrendResult = (List) ms_mapper.getList(paramMap, mapperName + ".selectWeekLrnSignalTrend");
				} else {
					lrnSignalTrendResult = (List) ms_mapper.getList(paramMap, mapperName + ".selectMonthLrnSignalTrend");
				}
			} else {
			
				if(paramMap.get("gcYn") != null) {
					if("Y".equals(paramMap.get("gcYn"))) {
						ArrayList<Map<String,Object>> orgList = (ArrayList<Map<String, Object>>) es_mapper.getList(paramMap, mapperName + ".selectGradeClassOrgId");
						ArrayList paramOrgList = new ArrayList<>();
						if(orgList.size() > 0) {
							for(Map<String, Object> item : orgList) {
								paramOrgList.add(item.get("depthOrgId"));
							}
							
							paramMap.put("orgList", paramOrgList);
						}
					}
				}
			
				if(wkYn) {
					lrnSignalTrendResult = (List) es_mapper.getList(paramMap, mapperName + ".selectWeekLrnSignalTrend");
				} else {
					lrnSignalTrendResult = (List) es_mapper.getList(paramMap, mapperName + ".selectMonthLrnSignalTrend");
				}
			}
			
			if(lrnSignalTrendResult.size() > 0) {
				for(int i = 0; i < lrnSignalTrendResult.size(); i++) {
					Map<String, Object> crtRtNeedEffortGrpResultMap = new LinkedHashMap<>();
					crtRtNeedEffortGrpResultMap.put("category", lrnSignalTrendResult.get(i).get("category"));
					crtRtNeedEffortGrpResultMap.put("signal1", lrnSignalTrendResult.get(i).get("signal1"));
					crtRtNeedEffortGrpResultMap.put("signal2", lrnSignalTrendResult.get(i).get("signal2"));
					crtRtNeedEffortGrpResultMap.put("signal3", lrnSignalTrendResult.get(i).get("signal3"));
					crtRtNeedEffortGrpResultMap.put("total", lrnSignalTrendResult.get(i).get("total"));
					
					lrnSignalTrend.add(crtRtNeedEffortGrpResultMap);
				}
				
				data.put("lrnSignalTrend", lrnSignalTrend);
			}
			
			setResult(dataKey, data);
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	
	/**
	 * 강점 습관 분석 우수회원
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map getStrengthHabitExcellentStud(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"searchStartYymm", "searchEndYymm", "orgId", "schType"}, paramMap);
		if(vu.isValid()) { //1.필수값 체크
			
			Map<String, Object> data = new LinkedHashMap<>();
			Map<String, Object> strengthHabitExcellentStud = new LinkedHashMap<>();
			ArrayList<Object> lrnData = new ArrayList<Object>();
			List<Map<String,Object>> strengthHabitExcellentStudResult =  new ArrayList();
			
			//demo 계정 관련 로직 
			String mapperName = (paramMap.get("orgId").toString().contains("demo")) ? "Group_ES_Demo" : "Group_ES";
			if(paramMap.get("schType").toString().equals("ms")) {
				mapperName = "Group_MS";
				strengthHabitExcellentStudResult = (List) ms_mapper.getList(paramMap, mapperName + ".selectStrengthHabitExcellentStud");
			} else {
				if(paramMap.get("gcYn") != null) {
					if("Y".equals(paramMap.get("gcYn"))) {
						ArrayList<Map<String,Object>> orgList = (ArrayList<Map<String, Object>>) es_mapper.getList(paramMap, mapperName + ".selectGradeClassOrgId");
						ArrayList paramOrgList = new ArrayList<>();
						if(orgList.size() > 0) {
							for(Map<String, Object> item : orgList) {
								paramOrgList.add(item.get("depthOrgId"));
							}
							
							paramMap.put("orgList", paramOrgList);
						}
					}
				}
				strengthHabitExcellentStudResult = (List) es_mapper.getList(paramMap, mapperName + ".selectStrengthHabitExcellentStud");
			}
			
			if(strengthHabitExcellentStudResult.size() > 0) {
				Map<String, Object> studInfo = new LinkedHashMap<>();
				
				if(mapperName.equals("Group_ES_Demo")) {
					studInfo.put("studId", strengthHabitExcellentStudResult.get(strengthHabitExcellentStudResult.size() - 1).get("studId"));
					studInfo.put("studNm", strengthHabitExcellentStudResult.get(strengthHabitExcellentStudResult.size() - 1).get("studNm"));
				} else {
					getStudentInfo(strengthHabitExcellentStudResult, studInfo);
				}
				strengthHabitExcellentStud.put("studInfo", studInfo);
				
				for(Map<String,Object> item : strengthHabitExcellentStudResult) {
					Map<String, Object> lrnDataMap = new LinkedHashMap<>();
					lrnDataMap.put("category", item.get("category"));
					lrnDataMap.put("avgExRt", item.get("avgExRt"));
					lrnDataMap.put("avgCrtRt", item.get("avgCrtRt"));
					lrnDataMap.put("avgDayLrnSec", item.get("avgDayLrnSec"));
					
					lrnData.add(lrnDataMap);
				}
				
				strengthHabitExcellentStud.put("lrnData", lrnData);
				
				if(strengthHabitExcellentStud != null) {
					data.put("strengthHabitExcellentStud", strengthHabitExcellentStud);
				}
			}
		
			setResult(dataKey, data);
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}

	/***
	 * 홈런 API 호출 :: 학생 개인정보 조회 (보안)
	 * @param studResultList
	 * @param studInfo
	 * @throws Exception
	 */
	private void getStudentInfo(List<Map<String, Object>> studResultList, Map<String, Object> studInfo) throws Exception {
		try {	
			//홈런 API 조회
			Map<String,Object> data_hl = new HashMap<>();
			Map<String, Object> paramMap_ex= new HashMap<>();
			paramMap_ex.put("apiName", "student");	       	
			paramMap_ex.put("studId", studResultList.get(studResultList.size() - 1).get("studId"));
			data_hl = (Map<String, Object>) externalAPIservice.callExternalAPI(paramMap_ex).get("data");
			if(data_hl != null) {
				studInfo.put("studNm", new String(data_hl.get("STUD_NM").toString().getBytes("8859_1"), "UTF-8"));
				studInfo.put("studId", data_hl.get("LOGIN_ID").toString());
			} else {
				studInfo.put("studNm", null);
				studInfo.put("studId", null);
			}
		} catch (Exception e) {
			LOGGER.error("getStudentInfo 홈런 API 조회[student] Error");
			studInfo.put("studNm", null);
			studInfo.put("studId", null);
		}
	}
	
	/**
	 * 강점 습관 분석 우수회원 학습특성
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map getStrengthHabitExcellentStudHabit(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"searchStartYymm", "searchEndYymm", "orgId", "schType"}, paramMap);
		if(vu.isValid()) { //1.필수값 체크
		
			Map<String, Object> data = new LinkedHashMap<>();
			Map<String, Object> strengthHabitExcellentStudHabit = new LinkedHashMap<>();
			ArrayList<Object> lrnData = new ArrayList<Object>();
			
			Map<String,Object> strengthHabitExcellentStudResult = new LinkedHashMap<>();
			//demo 계정 관련 로직 
			String mapperName = (paramMap.get("orgId").toString().contains("demo")) ? "Group_ES_Demo" : "Group_ES";
			if(paramMap.get("schType").toString().equals("ms")) {
				mapperName = "Group_MS";
				strengthHabitExcellentStudResult = (Map) ms_mapper.get(paramMap, mapperName + ".selectStrengthHabitExcellentStudHabit");
			} else {
				if(paramMap.get("gcYn") != null) {
					if("Y".equals(paramMap.get("gcYn"))) {
						ArrayList<Map<String,Object>> orgList = (ArrayList<Map<String, Object>>) es_mapper.getList(paramMap, mapperName + ".selectGradeClassOrgId");
						ArrayList paramOrgList = new ArrayList<>();
						if(orgList.size() > 0) {
							for(Map<String, Object> item : orgList) {
								paramOrgList.add(item.get("depthOrgId"));
							}
							
							paramMap.put("orgList", paramOrgList);
						}
					}
				}
				strengthHabitExcellentStudResult = (Map) es_mapper.get(paramMap, mapperName + ".selectStrengthHabitExcellentStudHabit");
			}
			
			if(strengthHabitExcellentStudResult != null) {
				
				strengthHabitExcellentStudHabit.put("attHabit", strengthHabitExcellentStudResult.get("attHabit"));
				strengthHabitExcellentStudHabit.put("planHabit", strengthHabitExcellentStudResult.get("planHabit"));
				strengthHabitExcellentStudHabit.put("incrtNoteHabit", strengthHabitExcellentStudResult.get("incrtNoteHabit"));
				strengthHabitExcellentStudHabit.put("aLrnHabit", strengthHabitExcellentStudResult.get("aLrnHabit"));
				strengthHabitExcellentStudHabit.put("slvHabit", strengthHabitExcellentStudResult.get("slvHabit"));
				strengthHabitExcellentStudHabit.put("concnHabit", strengthHabitExcellentStudResult.get("concnHabit"));
								
				data.put("strengthHabitExcellentStudHabit", strengthHabitExcellentStudHabit);
			}
			
			setResult(dataKey, data);
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	
	/**
	 * 강점 습관 분석 고성장 회원
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map getStrengthHabitHighGrowthStud(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"searchStartYymm" ,"searchEndYymm", "orgId", "schType"}, paramMap);
		if(vu.isValid()) { //1.필수값 체크		
		
			Map<String, Object> data = new LinkedHashMap<>();
			Map<String, Object> strengthHabitHighGrowthStud = new LinkedHashMap<>();
			ArrayList<Object> lrnData = new ArrayList<Object>();
			
			List<Map<String,Object>> strengthHabitHighGrowthStudResult = new ArrayList();
			//demo 계정 관련 로직 
			String mapperName = (paramMap.get("orgId").toString().contains("demo")) ? "Group_ES_Demo" : "Group_ES";
			if(paramMap.get("schType").toString().equals("ms")) {
				mapperName = "Group_MS";
				strengthHabitHighGrowthStudResult = (List) ms_mapper.getList(paramMap, mapperName + ".selectStrengthHabitHighGrowthStud");
			} else {
				if(paramMap.get("gcYn") != null) {
					if("Y".equals(paramMap.get("gcYn"))) {
						ArrayList<Map<String,Object>> orgList = (ArrayList<Map<String, Object>>) es_mapper.getList(paramMap, mapperName + ".selectGradeClassOrgId");
						ArrayList paramOrgList = new ArrayList<>();
						if(orgList.size() > 0) {
							for(Map<String, Object> item : orgList) {
								paramOrgList.add(item.get("depthOrgId"));
							}
							
							paramMap.put("orgList", paramOrgList);
						}
					}
				}
				strengthHabitHighGrowthStudResult = (List) es_mapper.getList(paramMap, mapperName + ".selectStrengthHabitHighGrowthStud");
			}
			
			if(strengthHabitHighGrowthStudResult.size() > 0) {
				Map<String, Object> studInfo = new LinkedHashMap<>();
				if(mapperName.equals("Group_ES_Demo")) {
					studInfo.put("studId", strengthHabitHighGrowthStudResult.get(strengthHabitHighGrowthStudResult.size() - 1).get("studId"));
					studInfo.put("studNm", strengthHabitHighGrowthStudResult.get(strengthHabitHighGrowthStudResult.size() - 1).get("studNm"));
				} else {
					getStudentInfo(strengthHabitHighGrowthStudResult, studInfo);
				}
				strengthHabitHighGrowthStud.put("studInfo", studInfo);
				
				for(Map<String,Object> item : strengthHabitHighGrowthStudResult) {
					Map<String, Object> lrnDataMap = new LinkedHashMap<>();
					lrnDataMap.put("category", item.get("category"));
					lrnDataMap.put("avgCrtRt", item.get("avgCrtRt"));
					
					lrnData.add(lrnDataMap);
				}
				
				strengthHabitHighGrowthStud.put("lrnData", lrnData);
				
				if(strengthHabitHighGrowthStud != null) {
					data.put("strengthHabitHighGrowthStud", strengthHabitHighGrowthStud);
				}
			}
			
			setResult(dataKey, data);
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	
	/**
	 * 강점 습관 분석 고성장 회원 학습특성
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map getStrengthHabitHighGrowthStudHabit(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"searchStartYymm", "searchEndYymm", "orgId", "schType"}, paramMap);
		if(vu.isValid()) { //1.필수값 체크
				
			Map<String, Object> data = new LinkedHashMap<>();
			Map<String, Object> strengthHabitHighGrowthStudHabit = new LinkedHashMap<>();
			ArrayList<Object> lrnData = new ArrayList<Object>();
			Map<String,Object> strengthHabitHighGrowthStudHabitResult = new LinkedHashMap<>();
			//demo 계정 관련 로직 
			String mapperName = (paramMap.get("orgId").toString().contains("demo")) ? "Group_ES_Demo" : "Group_ES";
			if(paramMap.get("schType").toString().equals("ms")) {
				mapperName = "Group_MS";
				strengthHabitHighGrowthStudHabitResult = (Map) ms_mapper.get(paramMap, mapperName + ".selectStrengthHabitHighGrowthStudHabit");
			} else {
				if(paramMap.get("gcYn") != null) {
					if("Y".equals(paramMap.get("gcYn"))) {
						ArrayList<Map<String,Object>> orgList = (ArrayList<Map<String, Object>>) es_mapper.getList(paramMap, mapperName + ".selectGradeClassOrgId");
						ArrayList paramOrgList = new ArrayList<>();
						if(orgList.size() > 0) {
							for(Map<String, Object> item : orgList) {
								paramOrgList.add(item.get("depthOrgId"));
							}
							
							paramMap.put("orgList", paramOrgList);
						}
					}
				}
				strengthHabitHighGrowthStudHabitResult = (Map) es_mapper.get(paramMap, mapperName + ".selectStrengthHabitHighGrowthStudHabit");
			}
			
			
			if(strengthHabitHighGrowthStudHabitResult != null) {
				
				strengthHabitHighGrowthStudHabit.put("attHabit", strengthHabitHighGrowthStudHabitResult.get("attHabit"));
				strengthHabitHighGrowthStudHabit.put("planHabit", strengthHabitHighGrowthStudHabitResult.get("planHabit"));
				strengthHabitHighGrowthStudHabit.put("incrtNoteHabit", strengthHabitHighGrowthStudHabitResult.get("incrtNoteHabit"));
				strengthHabitHighGrowthStudHabit.put("aLrnHabit", strengthHabitHighGrowthStudHabitResult.get("aLrnHabit"));
				strengthHabitHighGrowthStudHabit.put("slvHabit", strengthHabitHighGrowthStudHabitResult.get("slvHabit"));
				strengthHabitHighGrowthStudHabit.put("concnHabit", strengthHabitHighGrowthStudHabitResult.get("concnHabit"));
				
				
				data.put("strengthHabitHighGrowthStudHabit", strengthHabitHighGrowthStudHabit);
			}
		
			setResult(dataKey, data);
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	
	/*
	 * 학습계획 학생 학습 현황 (ORG-LP-001)
	 */
	@Override
	public Map getLrnPlanStudLrnStt(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"searchYymm", "studId", "orgId", "schType"}, paramMap);
		if(vu.isValid()) {
		
			Map<String, Object> data = new LinkedHashMap<>();
			Map<String, Object> lrnPlanStudLrnStt = new LinkedHashMap<>();
			Map<String, Object> lrnPlanStudLrnSttResult = new LinkedHashMap<>();
			Map<String, Object> lrnPlanStudLrnSttMap = new LinkedHashMap<>();
			
			//demo 계정 관련 로직 
			String mapperName = (paramMap.get("orgId") != null && paramMap.get("orgId").toString().contains("demo")) ? "Group_ES_Demo" : "Group_ES";
			if(paramMap.containsKey("schType") && paramMap.get("schType").toString().equals("ms")) {
				mapperName = "Group_MS";
				if(paramMap.containsKey("searchWk") && paramMap.get("searchWk") != null && !paramMap.get("searchWk").equals("")) {
					lrnPlanStudLrnSttResult = (Map) ms_mapper.get(paramMap, mapperName + ".selectWeekLrnPlanStudLrnStt");
				} else {
					lrnPlanStudLrnSttResult = (Map) ms_mapper.get(paramMap, mapperName + ".selectMonthLrnPlanStudLrnStt");
				}
			} else {
				if(paramMap.containsKey("searchWk") && paramMap.get("searchWk") != null && !paramMap.get("searchWk").equals("")) {
					lrnPlanStudLrnSttResult = (Map) es_mapper.get(paramMap, mapperName + ".selectWeekLrnPlanStudLrnStt");
				} else {
					lrnPlanStudLrnSttResult = (Map) es_mapper.get(paramMap, mapperName + ".selectMonthLrnPlanStudLrnStt");
				}
			}
			
			if(lrnPlanStudLrnSttResult != null) {
				String searchYymm = "";
				String searchYy = "";
				String searchMm = "";
				String searchWk = "";
				String studId = "";
				String studNm = "";
				String grade = "";
				String lrnSignal = "";
				String attRt = "";
				String exRt = "";
				String dayAvgLrnSec = "";
				String crtRt = "";
				
				if(mapperName.equals("Group_ES_Demo")) {
					studNm = lrnPlanStudLrnSttResult.get("studNm").toString(); 
				} else {
					try {	
						//홈런 API 조회
						Map<String,Object> data_hl = new HashMap<>();

						Map<String, Object> paramMap_ex= new HashMap<>();
						paramMap_ex.put("apiName", "student");	       	
						paramMap_ex.put("studId", paramMap.get("studId"));
						data_hl = (Map<String, Object>) externalAPIservice.callExternalAPI(paramMap_ex).get("data");
						if(data_hl != null) {
							studNm = new String(data_hl.get("STUD_NM").toString().getBytes("8859_1"), "UTF-8");
						} else {
							studNm = null;
						}
					} catch (Exception e) {
						LOGGER.error("getLrnPlanStudLrnStt 홈런 API 조회[student] Error");
						studNm = null;
					}
				}
				
				if(paramMap.get("searchYymm") != null && !paramMap.get("searchYymm").equals("")) {
					searchYymm = paramMap.get("searchYymm").toString();
					searchYy = searchYymm.substring(0,4);
					searchMm = searchYymm.substring(4,6);
				}
				if(paramMap.get("searchWk") != null && !paramMap.get("searchWk").equals("")) {
					searchWk = paramMap.get("searchWk").toString();
				}
				if(lrnPlanStudLrnSttResult.get("studId") != null && !lrnPlanStudLrnSttResult.get("studId").equals("")) {
					studId = lrnPlanStudLrnSttResult.get("studId").toString();
				}
				if(lrnPlanStudLrnSttResult.get("studGrade") != null && !lrnPlanStudLrnSttResult.get("studGrade").equals("")) {
					grade = lrnPlanStudLrnSttResult.get("studGrade").toString();
				}
				if(lrnPlanStudLrnSttResult.get("lrnSignal") != null && !lrnPlanStudLrnSttResult.get("lrnSignal").equals("")) {
					lrnSignal = lrnPlanStudLrnSttResult.get("lrnSignal").toString();
				}
				if(lrnPlanStudLrnSttResult.get("attRt") != null && !lrnPlanStudLrnSttResult.get("attRt").equals("")) {
					attRt = lrnPlanStudLrnSttResult.get("attRt").toString();
				}
				if(lrnPlanStudLrnSttResult.get("exRt") != null && !lrnPlanStudLrnSttResult.get("exRt").equals("")) {
					exRt = lrnPlanStudLrnSttResult.get("exRt").toString();
				}
				if(lrnPlanStudLrnSttResult.get("dayAvgLrnSec") != null && !lrnPlanStudLrnSttResult.get("dayAvgLrnSec").equals("")) {
					dayAvgLrnSec = lrnPlanStudLrnSttResult.get("dayAvgLrnSec").toString();
				}
				if(lrnPlanStudLrnSttResult.get("crtRt") != null && !lrnPlanStudLrnSttResult.get("crtRt").equals("")) {
					crtRt = lrnPlanStudLrnSttResult.get("crtRt").toString();
				}
				
				/*** ( HL-26999 : 최초 추가 )
				==  학습계획 팝업 호출 시 파라미터 정보  ==
				a. Key : p
				b. Value : AES 암호화된 값
				c. 복호화 시 값 : 조회년도&조회월&조회주차(월단위시공백)&학생ID&학생명&학년&학습신호&출석률&수행률&일일학습시간(초단위)&정답률&&&학교구분코드(ES/MS) 
				( HL-41327 : &&학교구분코드(ES/MS) 추가 )
				d. 복호화 시 값 : 조회년도&조회월&조회주차(월단위시공백)&학생ID&학생명&학년&학습신호&출석률&수행률&일일학습시간(초단위)&정답률&기관코드&기관아이디&학교구분코드(ES/MS) 
				( HL-31959 : 학습계획에 특별학습을 추가 기능 : 특정 학교만 허용(오정초) )
				*/
				String beforEncoding = searchYy + "&" + searchMm + "&" + searchWk + "&" + studId + "&" + studNm + "&" + grade + "&" + lrnSignal + "&" + attRt + "&" + exRt + "&" + dayAvgLrnSec + "&" + crtRt;
				
				if(paramMap.get("agnId") != null && paramMap.get("orgId") != null) {
					String orgId = paramMap.get("orgId").toString();
					String agnId = paramMap.get("agnId").toString();
					beforEncoding += "&" + orgId + "&" + agnId;
				} else {
					beforEncoding += "&&";
				}
				
				if(mapperName.equals("Group_MS")) beforEncoding += "&" + "MS";
				else beforEncoding += "&" + "ES";
				
				String afterEncoding = getEncodedStr(beforEncoding);
				lrnPlanStudLrnSttMap.put("데이터확인용_beforEncoding", beforEncoding);
				lrnPlanStudLrnSttMap.put("p", afterEncoding);
				data.put("lrnPlanStudLrnStt", lrnPlanStudLrnSttMap);
			}
			setResult(dataKey, data);
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	

	/**
	 * 공통 validation
	 * @param vu validation객체
	 * @param paramMap 
	 * @return
	 */
	private ValidationUtil commonValidation(ValidationUtil vu, Map<String, Object> paramMap) {
		vu.checkRequired(new String[] {"currCon"}, paramMap);
		String monthWord = paramMap.get("currCon").toString().toLowerCase(); // 월간, 주간 구분 코드 대문자 -> 소문자
		paramMap.put("monthWord", monthWord);
		if(vu.isValid() && monthWord.equals("w")) {
			//2.필수값 체크 (주간)
			vu.checkRequired(new String[] {"yyyy", "mm", "wk", "orgId", "schType"}, paramMap);
			//3.yyyy 날짜형 체크
			if(vu.isValid()) vu.isYear("yyyy", (String)paramMap.get("yyyy"));
			//4.월 범위 체크
			if(vu.isValid()) vu.checkNumericRange("mm", (String)paramMap.get("mm"), 1, 12);
			//5.mm 날짜형 체크
			if(vu.isValid() && ((String) paramMap.get("mm")).length()==1) {
				String month =  "0".concat((String) paramMap.get("mm"));
				paramMap.put("mm", month);
			}
			if(vu.isValid()) vu.checkNumericRange("mm", (String)paramMap.get("mm"), 1, 12);
			//6.년,월 문자열 합치기
			String yymm = paramMap.get("yyyy").toString().concat(paramMap.get("mm").toString());
			paramMap.put("yymm", yymm);
			//7.주간 범위 체크
			if(vu.isValid()) vu.checkNumericRange("wk", (String)paramMap.get("wk"), 1, 6);
		} else if(vu.isValid() && monthWord.equals("m")){
			//2.필수값 체크 (월간)
			vu.checkRequired(new String[] {"yyyy", "mm", "orgId", "schType"}, paramMap);
			//3.yyyy 날짜형 체크
			if(vu.isValid()) vu.isYear("yyyy", (String)paramMap.get("yyyy"));
			//4.월 범위 체크
			if(vu.isValid()) vu.checkNumericRange("mm", (String)paramMap.get("mm"), 1, 12);
			//5.mm 날짜형 체크
			if(vu.isValid() && ((String) paramMap.get("mm")).length()==1) {
				String month =  "0".concat((String) paramMap.get("mm"));
				paramMap.put("mm", month);
			}
			//6.년,월 문자열 합치기
			String yymm = paramMap.get("yyyy").toString().concat(paramMap.get("mm").toString());
			paramMap.put("yymm", yymm);
		}
		return vu;
	}
	
	/**
	 * localCode로 조회해서 ctpCode 반환
	 * @param key
	 * @param data
	 */
	public String checkCode(String ctpCode){
		Map<String,String> codeValue = new HashMap<>();
		codeValue.put("0","0"); // 전국
		codeValue.put("10","11"); // 서울특별시
		codeValue.put("20","42"); // 강원도
		codeValue.put("30","30"); // 대전광역시
		codeValue.put("31","44"); // 충청남도
		codeValue.put("33","36"); // 세종특별자치시
		codeValue.put("36","43"); // 충청북도
		codeValue.put("40","28"); // 인천광역시
		codeValue.put("41","41"); // 경기도
		codeValue.put("50","29"); // 광주광역시
		codeValue.put("51","46"); // 전라남도
		codeValue.put("56","45"); // 전라북도
		codeValue.put("60","26"); // 부산광역시
		codeValue.put("62","48"); // 경상남도
		codeValue.put("68","31"); // 울산광역시
		codeValue.put("69","50"); // 제주특별자치도
		codeValue.put("70","27"); // 대구광역시
		codeValue.put("71","47"); // 경상북도
		return codeValue.get(ctpCode);
	}
	

	/**
	 * 서비스단에서 리턴되는 결과(메시지,데이터 object를 포함한 result)세팅.
	 * @param key
	 * @param data
	 */
	private void setResult(String key, Object data) {
		result = new LinkedHashMap();
		
		if(key.equals(dataKey)) {
			LinkedHashMap message = new LinkedHashMap();			
			if(data == null 
					|| (data instanceof List && ((List)data).size() == 0) 
					|| (data instanceof Map && ((Map)data).isEmpty())) {
				//조회결과가 없는 경우 메시지만 나감.
				message.put("resultCode", ValidationCode.NO_DATA.getCode());
				message.put("result", ValidationCode.NO_DATA.getMessage());
				result.put(msgKey, message);
			} else {
				//정상데이터, 정상메시지
				message.put("resultCode", ValidationCode.SUCCESS.getCode());
				message.put("result", ValidationCode.SUCCESS.getMessage());
				result.put(msgKey, message);
				
				result.put(dataKey, data);
			}
		} else {
			result.put(msgKey, data); //validation 걸린 메시지, 데이터 없음
		}
	}
	
	/**
	 * Encoding studId & 홈런 API 조회 : loginId, studNm 추출 
	 * @param studList
	 * @throws Exception
	 */
	private void listEncodeS(List<HashMap<String,Object>> studList, String mapperName, Map<String, Object> paramMap) throws Exception{
        CipherUtil cp = CipherUtil.getInstance();
        String retStr = "";
        
		ArrayList<String> stu_ids_list = new ArrayList();
        for(Map<String,Object> studMap : studList) {
        	if(studMap.get("studId") != null && !"".equals(studMap.get("studId"))) {
        		String sId = studMap.get("studId").toString();
        		stu_ids_list.add(sId);        		
        		retStr = java.net.URLEncoder.encode( cp.AES_Encode(sId), "utf-8");        		
        	}
        	studMap.put("s", retStr);
        }
        
        ArrayList<Map<String,Object>> data_hl = new ArrayList();
		Map<String, Object> paramMap_ex = new HashMap<>();
        if(!mapperName.equals("Group_ES_Demo")) {
        	
	        //홈런 API 조회
			paramMap_ex.put("apiName", "students");	       
			String stu_ids = "";
			for(int i=0 ; i<stu_ids_list.size() ; i++) {
				if(i == 0) {
					stu_ids += stu_ids_list.get(i);
				} else {
					stu_ids += ","+stu_ids_list.get(i);
				}
			}
			paramMap_ex.put("stu_ids", stu_ids);
			data_hl = (ArrayList<Map<String, Object>>) externalAPIservice.callExternalAPI(paramMap_ex).get("data");
			for(HashMap<String,Object> studMap : studList) {
	        	if(studMap.get("studId") != null && !"".equals(studMap.get("studId"))) {
	        		String sId = studMap.get("studId").toString();
	        		if(data_hl != null) {
			    		for(Map<String, Object> item : data_hl) {
			    			if(sId.equals(item.get("STU_ID").toString())) {
			        			studMap.put("loginId", item.get("LOGIN_ID"));
			        			studMap.put("studNm", new String(item.get("STUD_NM").toString().getBytes("8859_1"), "UTF-8"));
			    			}
						}
					} else {
						LOGGER.error("listEncodeS 홈런 API 조회[students] 매칭 실패 sId : "+sId);
						studMap.put("loginId", null);
						studMap.put("studNm", null);
					}
	        	}
			}
			
		}
	
    }
	
	/**
	 * Encoding studLrnStt
	 * @param key
	 * @param data
	 */
	public String getEncodedStr(String encodedParam) throws Exception {
		String encodedStr = "";
		
		CipherUtil cp = CipherUtil.getInstance();
		
		try {
			encodedStr = cp.AES_Encode(encodedParam);
		} catch(Exception e) {
			LOGGER.error("studLrnStt Encoding Error");
		}
				
		return encodedStr;
	}
    
}
