package com.iscreamedu.analytics.homelearn.api.extrtlog.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iscreamedu.analytics.homelearn.api.common.mapper.CommonMapperLrnLog;
import com.iscreamedu.analytics.homelearn.api.common.service.ExternalAPIService;
import com.iscreamedu.analytics.homelearn.api.common.util.CommonUtil;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationCode;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtil;
import com.iscreamedu.analytics.homelearn.api.extrtlog.service.ExtRtLogService;
import com.iscreamedu.analytics.homelearn.api.student.service.impl.StudLrnTypeServiceImpl;

@Service
public class ExtRtLogServiceImpl implements ExtRtLogService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StudLrnTypeServiceImpl.class);

    private LinkedHashMap<String, Object> result;
    private String msgKey = "msg";
    private String dataKey = "data";

    @Autowired
    CommonMapperLrnLog commonMapperLrnLog;
    
	@Autowired
	ExternalAPIService externalAPIservice;
    
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
				result.put(msgKey, message);
			} else {
				//정상데이터, 정상메시지
				message.put("resultCode", ValidationCode.SUCCESS.getCode());
				result.put(msgKey, message);
				
				result.put(dataKey, data);
			}
		} else {
			result.put(msgKey, data); //validation 걸린 메시지, 데이터 없음
		}
	}
    
	private void getStudId(Map<String, Object> paramMap) throws Exception {
		if(!paramMap.containsKey("studId") && paramMap.containsKey("p")) {
			String studId = "";
			String encodedStr = paramMap.get("p").toString();
			
			String[] paramList = CommonUtil.getDecodedParam(encodedStr);
			studId = paramList[0];
			paramMap.put("studId", studId);
		}
	}
	
	@Override
	public LinkedHashMap setRealTimeCompleteMission(Map<String, Object> paramMap, HttpServletRequest req) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		Map<String, Object> studInfo = new HashMap<>();
        Map<String,Object> realTimeStudInfo = new HashMap<>();
        String realTimeStud_LrnStatusCd = null;
        int realTimeStud_studType = -1;
        Integer newStudCnt = 0;
        Integer resetStudCnt = 0;
		//1.필수값 체크
		getStudId(paramMap);
		vu.checkRequired(new String[] {"studId","dt","chCd","misStatusCd","regAdminId"}, paramMap);
				
		LOGGER.debug("setRealTimeCompleteMission - Param : " + paramMap);
				
		// studType, grade, misNo 조회해서 저장
		// MHL 오늘의 학습, MWN 오답노트, MNL미완료학습 : 정회원 studType 1, grade -99 
		// MLG 출석하기: 구분없음(체험회원+정회원) studType -1, grade -99
		// MEN 체험회원 일차별 미션 ( now - 오늘 미션 )
		// ** MKB 국어책 챌린지 : 구분없음(체험회원+정회원) studType -1, grade 있음. -- 실시간 등록 호출 대상 아님!!
		if(vu.isValid()) {

			if(paramMap.get("chCd").toString().equals("MLG") ) { // || paramMap.get("chCd").toString().equals("MEN")
				// 출석하기 완료 호출시 (습관팝업만 call)
				
				// 1. db : 학생 정보 조회 
				studInfo = (Map<String, Object>) commonMapperLrnLog.get(paramMap, "LrnLog.spStudInfo");

		        // 2. 홈런 API 조회 : 학생 정보 조회 call  
				Map<String,Object> apiParamMap = new HashMap<>();
		        apiParamMap.put("studId", paramMap.get("studId"));
		        apiParamMap.put("apiName", "aiReport/");
		        realTimeStudInfo =  (Map<String,Object>) externalAPIservice.callExternalAPI(apiParamMap).get("data");
        		realTimeStud_LrnStatusCd = getDivCdToLrnStatusCd(realTimeStudInfo);
        		realTimeStud_studType = (realTimeStud_LrnStatusCd == "1007" ? 1 : (realTimeStud_LrnStatusCd == "1003" ? 0 : -1));
        		
    			try {
					if(studInfo == null && realTimeStudInfo != null && realTimeStudInfo.size() > 0 && realTimeStud_LrnStatusCd != null) {
						// 3. db 없는 학생 정보 신규 등록
			        	Map<String, Object> newStudInfoMap = new HashMap<>();			        
				        
			        	newStudInfoMap.put("studId", realTimeStudInfo.get("stuId"));
			        	newStudInfoMap.put("tchrKey", null);
			        	newStudInfoMap.put("parKey", null);			        	
			        	newStudInfoMap.put("ssvcAkey", (realTimeStudInfo.containsKey("planDiv")?(realTimeStudInfo.get("planDiv").toString().equals("E")?4:3):-1));
			        	newStudInfoMap.put("grade", (realTimeStudInfo.containsKey("grade") ? Integer.parseInt(realTimeStudInfo.get("grade").toString()) : null));
			        	newStudInfoMap.put("lrnStatusCd", Integer.parseInt(realTimeStud_LrnStatusCd));
			        	newStudInfoMap.put("lrnStatusNm", (realTimeStudInfo.containsKey("statusCdNm") ? realTimeStudInfo.get("statusCdNm").toString() : null));
			        	newStudInfoMap.put("sttDt", (realTimeStudInfo.containsKey("startDe") ? realTimeStudInfo.get("startDe").toString() : null));
			        	newStudInfoMap.put("endDt", null);
			        	newStudInfoMap.put("regAdminId", "STUD_EXTRTLOG");
		        	
						commonMapperLrnLog.insert(newStudInfoMap, "LrnLog.ispStudInfo");
						newStudCnt = Integer.valueOf(newStudInfoMap.get("outResultCnt").toString());
						LOGGER.debug("신규 등록 학생의 오늘의 미션 생성 : ispStudInfo 호출 : " + newStudInfoMap);
						
						paramMap.put("studType", realTimeStud_studType);
						paramMap.put("grade", (realTimeStudInfo.containsKey("grade") ? Integer.parseInt(realTimeStudInfo.get("grade").toString()) : studInfo.get("grade")));
						
				        if(newStudCnt > 0) {
							// 3-1. 신규 등록 학생의 오늘의 미션 생성
				        	commonMapperLrnLog.insert(paramMap, "LrnLog.ispChMisDailyAddMission");
							LOGGER.debug("신규 등록 학생의 오늘의 미션 생성 : ispChMisDailyAddMission 호출 : " + paramMap.get("studId"));
				        }
					} else {
						// 실시간(realTimeStudInfo) 초등 상품 등록한 학생 정보 조회하여 db(studInfo)와 차이 있으면 갱신!!
						if(realTimeStudInfo != null && realTimeStudInfo.size() > 0) {

	//						"grade": 2,
	//				        "divCd": 10003,
	//				        "divCdNm": "정회원",
	//				        "statusCd": 10007,
	//				        "statusCdNm": "학습 진행",
	//				        "planDiv": "E",
	//				        "startDe": "2022-09-16"
							//초-중등 구분(서비스대체키), 학년, 학습상태코드, 학습시작일 비교하여 학생정보 갱신
							if((realTimeStudInfo.containsKey("planDiv") && !realTimeStudInfo.get("planDiv").toString().equals("E"))
								|| (realTimeStudInfo.containsKey("grade") && studInfo.containsKey("grade") && studInfo.get("grade").toString() != realTimeStudInfo.get("grade").toString())
								|| (realTimeStudInfo.containsKey("statusCd") && studInfo.containsKey("lrnStsCd") && studInfo.get("lrnStsCd").toString() != realTimeStud_LrnStatusCd)
								|| (realTimeStudInfo.containsKey("startDe") && studInfo.containsKey("sttDt") && studInfo.get("sttDt").toString() != realTimeStudInfo.get("startDe").toString())
							) {

				        		LOGGER.debug("lrnStsCd : "+ studInfo.get("lrnStsCd"));
				        		LOGGER.debug("grade : "+ studInfo.get("grade"));
				        		LOGGER.debug("sttDt : "+ studInfo.get("sttDt").toString() );
				        		LOGGER.debug("db 학생 정보와 다를 시 갱신 호출");

				        		LOGGER.debug("statusCd : "+  realTimeStud_LrnStatusCd);
				        		LOGGER.debug("grade : "+ realTimeStudInfo.get("grade").toString());
				        		LOGGER.debug("startDe : "+realTimeStudInfo.get("startDe").toString());

								// db 학생 정보와 다를 시 갱신 호출
								Map<String, Object> resetStudInfoMap = new HashMap<>();			        
								resetStudInfoMap.put("studId", realTimeStudInfo.get("stuId"));					        	       	
								resetStudInfoMap.put("ssvcAkey", (realTimeStudInfo.containsKey("planDiv")?(realTimeStudInfo.get("planDiv").toString().equals("E")?4:3):-1));
								resetStudInfoMap.put("grade", (realTimeStudInfo.containsKey("grade") ? Integer.parseInt(realTimeStudInfo.get("grade").toString()) : null));
								resetStudInfoMap.put("lrnStatusCd", realTimeStud_LrnStatusCd);
								resetStudInfoMap.put("lrnStatusNm", (realTimeStudInfo.containsKey("statusCdNm") ? realTimeStudInfo.get("statusCdNm").toString() : null));
								resetStudInfoMap.put("sttDt", (realTimeStudInfo.containsKey("startDe") ? realTimeStudInfo.get("startDe").toString() : null));
								resetStudInfoMap.put("regAdminId", "STUD_EXTRTLOG");

				        		commonMapperLrnLog.insert(resetStudInfoMap, "LrnLog.uspStudInfo");
								resetStudCnt = Integer.valueOf(resetStudInfoMap.get("outResultCnt").toString());
								LOGGER.debug("db 학생 정보와 실시간 정보 불일치 - 갱신 : uspStudInfo 호출 : " + resetStudInfoMap);
				        		LOGGER.debug("studType : "+ paramMap.get("studType") + " -> " + realTimeStud_studType);
				        		LOGGER.debug("grade : "+ paramMap.get("grade") + " -> " + resetStudInfoMap.get("grade"));
								paramMap.put("studType", realTimeStud_studType);
								paramMap.put("grade", resetStudInfoMap.get("grade"));
								
								// 미션 삭제('MLG','MEN','MKB','MMC') 후 재생성 (because... 재체험-체험 미션 재등록 or 중등상품 변경으로 미션 대상 아님)
								if(resetStudCnt > 0) {
						        	commonMapperLrnLog.insert(paramMap, "LrnLog.ispChMisDailyAddMission");
									LOGGER.debug("갱신된 학생 정보로 오늘의 미션 생성 : ispChMisDailyAddMission 호출 : " + paramMap.get("studId"));
					        	} 
					        	
							} else {
				        		LOGGER.debug("db 학생 정보와 실시간 정보 동일");
								paramMap.put("studType", studInfo.get("studType"));
								paramMap.put("grade", studInfo.get("grade"));
							}
						} else {
							paramMap.put("studType", studInfo.get("studType"));
							paramMap.put("grade", studInfo.get("grade"));
							LOGGER.debug("학생정보 실시간 조회 실패");	
						}
	
		        		LOGGER.debug("token 체크 : 실시간 미션 갱신용");
						//미션 실시간 갱신
						if(req.getHeader("token") != null && !req.getHeader("token").toString().isEmpty()) {
							
			        		LOGGER.debug("token : "+req.getHeader("token").toString());
							Map<String,Object> missionCondition = new HashMap<>();
							ArrayList<Map<String, Object>> missionList = new ArrayList<>();
							
							// 3. 미션 실시간 정보 call api -> 등록
							// 홈런 API 조회
							Map<String, Object> missionConditionMap = new HashMap<>();			
							missionConditionMap.put("token", req.getHeader("token").toString());
							missionConditionMap.put("apiName","studyStatus");
							missionCondition = (Map<String, Object>) externalAPIservice.callExternalAPI(missionConditionMap).get("data");	
							if(missionCondition != null) {
							
	//							"data": {
	//						        "todayStudy": true,
	//						        "incompleteStudy": true,
	//						        "errnote": true
	//						    }
								// 미션 갱신
								if(missionCondition.containsKey("todayStudy") && missionCondition.containsKey("incompleteStudy") && missionCondition.containsKey("errnote")) 
								{
									//키값 모두 존재시 호출
									missionCondition.put("studId", paramMap.get("studId"));
			        			
									// 4. 오늘 미션 갱신
									commonMapperLrnLog.insert(missionCondition, "LrnLog.ispRealTimeAddMission");
								}
							}
						}
					}
    			} catch(Exception e) {
    				String strResultMsg = null;
    				LinkedHashMap message = new LinkedHashMap();			
    				String[] errorMsgList = e.getMessage().split(": ");
					
					strResultMsg = "실시간 갱신 failed [ " + e.getMessage()+ " ]";
					message.put("resultCode", ValidationCode.REG_FAILED.getCode());
					message.put("result", strResultMsg);
					setResult(msgKey, message);
					try {
						JSONObject jsonMap = new JSONObject();
						Date nowDate = new Date();
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
						
						jsonMap.putAll(paramMap);
						Map<String, Object> paramMap2 = new LinkedHashMap();
						paramMap2.put("inProcName", "setRealTimeComplete");
						paramMap2.put("inProcStep", 0);
						paramMap2.put("inYyyymmdd", simpleDateFormat.format(nowDate));
						paramMap2.put("inParam", jsonMap.toJSONString());
						paramMap2.put("inErrorNo", 0);
						paramMap2.put("inErrorTitle", "error");
						paramMap2.put("inErrorMsg", strResultMsg);
						commonMapperLrnLog.insert(paramMap2, "LrnLog.ispErrorLog");
					} catch(Exception e2) {}
    			}
			}
			
			if(paramMap.get("chCd").toString().equals("MLG") && (realTimeStudInfo.containsKey("planDiv") && realTimeStudInfo.get("planDiv").toString().equals("M"))) {
				// 로그인이자 중등인 상품은 미션 해당 없음!!
				LinkedHashMap message = new LinkedHashMap();			
				message.put("resultCode", ValidationCode.REG_FAILED.getCode());
				message.put("result", "중등상품 회원 미션대상아님");
				setResult(msgKey, message);
			} else {
				String strResultMsg = null;
				LinkedHashMap message = new LinkedHashMap();			
				try {
					//dt형 변환
					String paramDate = paramMap.get("dt").toString();
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date = format.parse(paramDate);
					SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					
					paramMap.put("dt", format2.format(date));
					
					commonMapperLrnLog.insert(paramMap, "LrnLog.ispCompleteMission");
					Integer nResultCnt = Integer.valueOf(paramMap.get("outResultCnt").toString());
					strResultMsg = paramMap.get("outResultMsg").toString();
					if(nResultCnt > 0) {
						
						
						if(paramMap.get("chCd").toString().equals("MEN")) {
							// 체험회원
			        		LOGGER.debug("token 체크 : 체험회원 쌤과톡 발송용");
							//미션 실시간 갱신
							if(req.getHeader("token") != null && !req.getHeader("token").toString().isEmpty()) {
								
								Map<String, Object> experienceTutorWithTalk = new HashMap<>();
				        		LOGGER.debug("token : "+req.getHeader("token").toString());
								paramMap.put("token", req.getHeader("token").toString());
				        		experienceTutorWithTalk = (Map<String, Object>) commonMapperLrnLog.get(paramMap, "LrnLog.spExperienceTutorWithTalk");
				        		if (experienceTutorWithTalk != null ) {
					        		String missionName = experienceTutorWithTalk.get("misNm").toString();
					        		int waterDropCnt = Integer.parseInt(experienceTutorWithTalk.get("monthlyWaterDropCnt").toString());
					        		
									
									ArrayList<Map<String, Object>> missionList = new ArrayList<>();
									
									StringBuffer sb= new StringBuffer("[$name] 친구 ~<br/>");
									sb.append("매일 홈런하는 습관을 기르도록 도와주는 홈런 선생님입니다^^<br/><br/>");
									sb.append("[$name] 친구가 오늘의 미션을 완료해 내 나무를 키울 수 있는 물방울1개를 받았어요.<br/>");
									sb.append("우리 친구 참 잘했어요!! 앞으로도 매일 차근차근 오늘의 미션으로 물방울을 모아, 멋진 내 나무도 만들고 더 나은 학습 습관도 만드는  [$name] 친구가 될 수 있어요<br/><br/>");
									sb.append("■ 오늘의 완료 미션: " + missionName + "<br/>-------------------------------------------------<br/>");
									sb.append("■ 누적 개수: 물방울 " + waterDropCnt + "개<br/>");
									sb.append("※ 물방울 5개가 모이면 물을 줘서 쑥쑥 자라는 내 나무를 볼 수 있어요.");
	
					        		LOGGER.debug("stuMsg : " + sb.toString());
									// 3. 미션 실시간 정보 call api -> 홈런톡 발송 호출 
									// 홈런 API 호출
									Map<String, Object> sendMessageMap = new HashMap<>();			   
									sendMessageMap.put("token", req.getHeader("token").toString());
									sendMessageMap.put("apiName","sendMessage");
									sendMessageMap.put("stuMsg", sb.toString());
									externalAPIservice.callExternalAPI(sendMessageMap).get("data");
				        		}
							}
						}

						message.put("resultCode", ValidationCode.REG_SUCCESS.getCode());
						message.put("result", nResultCnt+"건 등록 : "+strResultMsg);
						setResult(msgKey, message);
					} else {
						message.put("resultCode", ValidationCode.REG_FAILED.getCode());
						message.put("result", strResultMsg);
						setResult(msgKey, message);
					}
				} catch(Exception e) {
					String[] errorMsgList = e.getMessage().split(": ");
					
//						strResultMsg = "Registration failed [ " + errorMsgList[errorMsgList.length-1] + " ]";
					strResultMsg = "Registration failed [ " + e.getMessage()+ " ]";
					message.put("resultCode", ValidationCode.REG_FAILED.getCode());
					message.put("result", strResultMsg);
					setResult(msgKey, message);
					try {
						JSONObject jsonMap = new JSONObject();
						Date nowDate = new Date();
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
						
						jsonMap.putAll(paramMap);
						Map<String, Object> paramMap2 = new LinkedHashMap();
						paramMap2.put("inProcName", "setRealTimeComplete");
						paramMap2.put("inProcStep", 0);
						paramMap2.put("inYyyymmdd", simpleDateFormat.format(nowDate));
						paramMap2.put("inParam", jsonMap.toJSONString());
						paramMap2.put("inErrorNo", 0);
						paramMap2.put("inErrorTitle", "error");
						paramMap2.put("inErrorMsg", strResultMsg);
						commonMapperLrnLog.insert(paramMap2, "LrnLog.ispErrorLog");
					} catch(Exception e2) {}
				}
				
				
			}
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		LOGGER.debug("setRealTimeCompleteMission - result : " + result);
		
		return result;
	}

	private String getDivCdToLrnStatusCd(Map<String, Object> realTimeStudInfo) {
		//    	divCd 회원상태 : 10002 : 체험진행 -> 체험회원  
		//		10007(학습진행), 10008(학습중지) -> 정회원
		//statusCd	statusCdNm
		//10001	체험 대기		-> 1000
		//10002	체험 진행		-> 1003
		//10003	체험 취소		-> 1002
		//10004	체험 완료		-> 1001
		//10005	학습 대기		-> 1004
		//10006	학습 만료		-> 1005
		//10007	학습 진행		-> 1007
		//10008	학습 중지		-> 1008
		//10009	학습 취소		-> 1006
		//10010	체험 미신청		-> 1000
		//>>>  LRN_STT_CD 로 변환 등록 
		//1000	LRN_STT_CD	체험 대기
		//1001	LRN_STT_CD	체험 만료
		//1002	LRN_STT_CD	체험 취소
		//1003	LRN_STT_CD	체험 진행
		//1004	LRN_STT_CD	학습 대기
		//1005	LRN_STT_CD	학습 만료
		//1006	LRN_STT_CD	학습 취소
		//1007	LRN_STT_CD	학습 진행
		//1008	LRN_STT_CD	학습 중지-휴지
		//1009	LRN_STT_CD	학습 중지-환불
		//1010	LRN_STT_CD	학습 중지-미납
		//3000	LRN_STT_CD	중등상품 변경에 따른 만료	2022-09-27 추가~
		// 추후 매칭 테이블로 관리 예정.
		if(realTimeStudInfo.containsKey("planDiv") && !realTimeStudInfo.get("planDiv").toString().equals("E"))
			return "3000";
		return realTimeStudInfo.containsKey("statusCd") ? 
				realTimeStudInfo.get("statusCd").toString()
						.replace("10001","1000")
						.replace("10002","1003")
						.replace("10003","1002")
						.replace("10004","1001")
						.replace("10005","1004")
						.replace("10006","1005")
						.replace("10007","1007")
						.replace("10008","1008")
						.replace("10009","1006")
						.replace("10010","1000") : null;
	}
	
	@Override
	public LinkedHashMap setRealTimeMissonStatusChange(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		getStudId(paramMap);
		paramMap.put("regAdminId", "STUD_EXTRTLOG");
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId","chCd","misStep","stepStatusCd"}, paramMap);
		
		if(vu.isValid()) {
			// studType, grade, misNo 조회해서 저장
			// MHL 오늘의 학습, MWN 오답노트, MNL미완료학습 : 정회원 studType 1, grade -99 
			// MLG 출석하기: 구분없음(체험회원+정회원) studType -1, grade -99
			// ** MKB 국어책 챌린지 : 구분없음(체험회원+정회원) studType -1, grade 있음. -- 실시간 등록 호출 대상 아님!!
//			switch(paramMap.get("chCd").toString())
//			{
//				case "MHL" :
//				case "MWN" :
//				case "MNL" :
//					paramMap.put("studType", 1);
//					paramMap.put("grade", -99);
//					break;
//				case "MLG" : 
//					paramMap.put("studType", -1);
//					paramMap.put("grade", -99);
//					break;
//				case "MKB" :
//					//paramMap.put("studType", -1);
//					break;
//			}
			// ** 파라미터 받기 때문에 예외 처리 안함!! 입력받은대로~ 
			
			String strResultMsg = null;
			LinkedHashMap message = new LinkedHashMap();			
			try {
				
				if(paramMap.get("stepStatusCd").toString().equals("-1") || paramMap.get("stepStatusCd").toString().equals("1")) {
					// 1. 실시간 학생 정보 call api -> 학년정보로 미션 시작전/시작하기 등록~!
					//홈런 API 조회
			        Map<String,Object> realTimeStudInfo = new HashMap<>();
			        Map<String,Object> apiParamMap = new HashMap<>();
			        apiParamMap.put("studId", paramMap.get("studId"));
			        apiParamMap.put("apiName", "aiReport/");
			        
			        realTimeStudInfo =  (Map<String,Object>) externalAPIservice.callExternalAPI(apiParamMap).get("data");
			        
					if(realTimeStudInfo != null && realTimeStudInfo.containsKey("grade")) {
		        			// 실시간 학년 정보 정상으로 들어왔을 시만 해당 값으로 호출 / 그외에는 기존 db 기준으로 호출
		        			paramMap.put("grade", Integer.parseInt(realTimeStudInfo.get("grade").toString()));
		        			commonMapperLrnLog.insert(paramMap, "LrnLog.ispChMisStepStatusChangeGrade");
		        	} else {
		        		commonMapperLrnLog.insert(paramMap, "LrnLog.ispChMisStepStatusChange");
		        	}
				} else {
					commonMapperLrnLog.insert(paramMap, "LrnLog.ispChMisStepStatusChange");
				}
				Integer nResultCnt = Integer.valueOf(paramMap.get("outResultCnt").toString());
				strResultMsg = paramMap.get("outResultMsg").toString();
				if(nResultCnt > 0) {					
					message.put("resultCode", ValidationCode.REG_SUCCESS.getCode());
					message.put("result","등록 완료 : "+strResultMsg);
					setResult(msgKey, message);			
//					갱신된 미션 정보 리로드					
				} else {
					message.put("resultCode", ValidationCode.REG_FAILED.getCode());
					message.put("result", strResultMsg);
					setResult(msgKey, message);
				}
			} catch(Exception e) {
				String[] errorMsgList = e.getMessage().split(": ");
				strResultMsg = "Registration failed [ " + errorMsgList[errorMsgList.length-1] + " ]";
				message.put("resultCode", ValidationCode.REG_FAILED.getCode());
				message.put("result", strResultMsg);
				setResult(msgKey, message);
				try {
					JSONObject jsonMap = new JSONObject();
					Date nowDate = new Date();
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
					
					jsonMap.putAll(paramMap);
					Map<String, Object> paramMap2 = new LinkedHashMap();
					paramMap2.put("inProcName", "isp_ch_mis_step_status_change");
					paramMap2.put("inProcStep", 0);
					paramMap2.put("inYyyymmdd", simpleDateFormat.format(nowDate));
					paramMap2.put("inParam", jsonMap.toJSONString());
					paramMap2.put("inErrorNo", 0);
					paramMap2.put("inErrorTitle", "insert error");
					paramMap2.put("inErrorMsg", strResultMsg);
					commonMapperLrnLog.insert(paramMap2, "LrnLog.ispErrorLog");
				} catch(Exception e2) {}
			}
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}

	@Override
	public LinkedHashMap setFnWaterJug(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		getStudId(paramMap);
		paramMap.put("regAdminId", "STUD_EXTRTLOG");
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId","misStep"}, paramMap);
		
		if(vu.isValid()) {
			String strResultMsg = null;
			LinkedHashMap message = new LinkedHashMap();			
			try {
			    commonMapperLrnLog.insert(paramMap, "LrnLog.ispChCHLWaterZugAction");
				Integer nResultCnt = Integer.valueOf(paramMap.get("outResultCnt").toString());
				strResultMsg = paramMap.get("outResultMsg").toString();
				if(nResultCnt > 0) {					
					message.put("resultCode", ValidationCode.REG_SUCCESS.getCode());
					message.put("result","등록 완료 : "+strResultMsg);
					setResult(msgKey, message);			
//							갱신된 미션 정보 리로드					
				} else {
					message.put("resultCode", ValidationCode.REG_FAILED.getCode());
					message.put("result", strResultMsg);
					setResult(msgKey, message);
				}
			} catch(Exception e) {
				String[] errorMsgList = e.getMessage().split(": ");
				strResultMsg = "Registration failed [ " + errorMsgList[errorMsgList.length-1] + " ]";
				message.put("resultCode", ValidationCode.REG_FAILED.getCode());
				message.put("result", strResultMsg);
				setResult(msgKey, message);
				try {
					JSONObject jsonMap = new JSONObject();
					Date nowDate = new Date();
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
					
					jsonMap.putAll(paramMap);
					Map<String, Object> paramMap2 = new LinkedHashMap();
					paramMap2.put("inProcName", "isp_ch_chl_waterzug_action");
					paramMap2.put("inProcStep", 0);
					paramMap2.put("inYyyymmdd", simpleDateFormat.format(nowDate));
					paramMap2.put("inParam", jsonMap.toJSONString());
					paramMap2.put("inErrorNo", 0);
					paramMap2.put("inErrorTitle", "insert error");
					paramMap2.put("inErrorMsg", strResultMsg);
					commonMapperLrnLog.insert(paramMap2, "LrnLog.ispErrorLog");
				} catch(Exception e2) {}
			}
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	
	@Override
	public LinkedHashMap setFnObjectReward(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		getStudId(paramMap);
		paramMap.put("regAdminId", "STUD_EXTRTLOG");
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId","rewardStep"}, paramMap);
		
		if(vu.isValid()) {
			String strResultMsg = null;
			LinkedHashMap message = new LinkedHashMap();			
			try {
			    commonMapperLrnLog.insert(paramMap, "LrnLog.ispChCLURewardAction");
				Integer nResultCnt = Integer.valueOf(paramMap.get("outResultCnt").toString());
				strResultMsg = paramMap.get("outResultMsg").toString();
				if(nResultCnt > 0) {					
					message.put("resultCode", ValidationCode.REG_SUCCESS.getCode());
					message.put("result","등록 완료 : "+strResultMsg);
					setResult(msgKey, message);			
//							갱신된 미션 정보 리로드					
				} else {
					message.put("resultCode", ValidationCode.REG_FAILED.getCode());
					message.put("result", strResultMsg);
					setResult(msgKey, message);
				}
			} catch(Exception e) {
				String[] errorMsgList = e.getMessage().split(": ");
				strResultMsg = "Registration failed [ " + errorMsgList[errorMsgList.length-1] + " ]";
				message.put("resultCode", ValidationCode.REG_FAILED.getCode());
				message.put("result", strResultMsg);
				setResult(msgKey, message);
				try {
					JSONObject jsonMap = new JSONObject();
					Date nowDate = new Date();
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
					
					jsonMap.putAll(paramMap);
					Map<String, Object> paramMap2 = new LinkedHashMap();
					paramMap2.put("inProcName", "isp_ch_clu_reward_action");
					paramMap2.put("inProcStep", 0);
					paramMap2.put("inYyyymmdd", simpleDateFormat.format(nowDate));
					paramMap2.put("inParam", jsonMap.toJSONString());
					paramMap2.put("inErrorNo", 0);
					paramMap2.put("inErrorTitle", "insert error");
					paramMap2.put("inErrorMsg", strResultMsg);
					commonMapperLrnLog.insert(paramMap2, "LrnLog.ispErrorLog");
				} catch(Exception e2) {}
			}
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
@Override
	public LinkedHashMap setFnParticle(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		getStudId(paramMap);
		paramMap.put("regAdminId", "STUD_EXTRTLOG");
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId","misStep"}, paramMap);
		
		if(vu.isValid()) {
			String strResultMsg = null;
			LinkedHashMap message = new LinkedHashMap();			
			try {
			    commonMapperLrnLog.insert(paramMap, "LrnLog.ispChCHLParticleAction");
				Integer nResultCnt = Integer.valueOf(paramMap.get("outResultCnt").toString());
				strResultMsg = paramMap.get("outResultMsg").toString();
				if(nResultCnt > 0) {					
					message.put("resultCode", ValidationCode.REG_SUCCESS.getCode());
					message.put("result","등록 완료 : "+strResultMsg);
					setResult(msgKey, message);			
//							갱신된 미션 정보 리로드					
				} else {
					message.put("resultCode", ValidationCode.REG_FAILED.getCode());
					message.put("result", strResultMsg);
					setResult(msgKey, message);
				}
			} catch(Exception e) {
				String[] errorMsgList = e.getMessage().split(": ");
				strResultMsg = "Registration failed [ " + errorMsgList[errorMsgList.length-1] + " ]";
				message.put("resultCode", ValidationCode.REG_FAILED.getCode());
				message.put("result", strResultMsg);
				setResult(msgKey, message);
				try {
					JSONObject jsonMap = new JSONObject();
					Date nowDate = new Date();
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
					
					jsonMap.putAll(paramMap);
					Map<String, Object> paramMap2 = new LinkedHashMap();
					paramMap2.put("inProcName", "isp_ch_chl_particle_action");
					paramMap2.put("inProcStep", 0);
					paramMap2.put("inYyyymmdd", simpleDateFormat.format(nowDate));
					paramMap2.put("inParam", jsonMap.toJSONString());
					paramMap2.put("inErrorNo", 0);
					paramMap2.put("inErrorTitle", "insert error");
					paramMap2.put("inErrorMsg", strResultMsg);
					commonMapperLrnLog.insert(paramMap2, "LrnLog.ispErrorLog");
				} catch(Exception e2) {}
			}
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	
	@Override
	public LinkedHashMap resetParticle(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		getStudId(paramMap);
		paramMap.put("regAdminId", "STUD_EXTRTLOG");
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId"}, paramMap);
		
		if(vu.isValid()) {
			String strResultMsg = null;
			LinkedHashMap message = new LinkedHashMap();			
			try {
			    commonMapperLrnLog.insert(paramMap, "LrnLog.dspChCHLParticleActionDel");
				Integer nResultCnt = Integer.valueOf(paramMap.get("outResultCnt").toString());
				strResultMsg = paramMap.get("outResultMsg").toString();
				if(nResultCnt > 0) {					
					message.put("resultCode", ValidationCode.REG_SUCCESS.getCode());
					message.put("result","삭제 완료 : "+strResultMsg);
					setResult(msgKey, message);							
				} else {
					message.put("resultCode", ValidationCode.REG_FAILED.getCode());
					message.put("result", strResultMsg);
					setResult(msgKey, message);
				}
			} catch(Exception e) {
				String[] errorMsgList = e.getMessage().split(": ");
				strResultMsg = "Delete failed [ " + errorMsgList[errorMsgList.length-1] + " ]";
				message.put("resultCode", ValidationCode.REG_FAILED.getCode());
				message.put("result", strResultMsg);
				setResult(msgKey, message);
				try {
					JSONObject jsonMap = new JSONObject();
					Date nowDate = new Date();
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
					
					jsonMap.putAll(paramMap);
					Map<String, Object> paramMap2 = new LinkedHashMap();
					paramMap2.put("inProcName", "dsp_ch_chl_particle_action");
					paramMap2.put("inProcStep", 0);
					paramMap2.put("inYyyymmdd", simpleDateFormat.format(nowDate));
					paramMap2.put("inParam", jsonMap.toJSONString());
					paramMap2.put("inErrorNo", 0);
					paramMap2.put("inErrorTitle", "insert error");
					paramMap2.put("inErrorMsg", strResultMsg);
					commonMapperLrnLog.insert(paramMap2, "LrnLog.ispErrorLog");
				} catch(Exception e2) {}
			}
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}

	@Override
	public LinkedHashMap resetWaterJug(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		getStudId(paramMap);
		paramMap.put("regAdminId", "STUD_EXTRTLOG");
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId"}, paramMap);
		
		if(vu.isValid()) {
			String strResultMsg = null;
			LinkedHashMap message = new LinkedHashMap();			
			try {
			    commonMapperLrnLog.insert(paramMap, "LrnLog.dspChCHLWaterZugActionDel");
				Integer nResultCnt = Integer.valueOf(paramMap.get("outResultCnt").toString());
				strResultMsg = paramMap.get("outResultMsg").toString();
				if(nResultCnt > 0) {					
					message.put("resultCode", ValidationCode.REG_SUCCESS.getCode());
					message.put("result","삭제 완료 : "+strResultMsg);
					setResult(msgKey, message);							
				} else {
					message.put("resultCode", ValidationCode.REG_FAILED.getCode());
					message.put("result", strResultMsg);
					setResult(msgKey, message);
				}
			} catch(Exception e) {
				String[] errorMsgList = e.getMessage().split(": ");
				strResultMsg = "Delete failed [ " + errorMsgList[errorMsgList.length-1] + " ]";
				message.put("resultCode", ValidationCode.REG_FAILED.getCode());
				message.put("result", strResultMsg);
				setResult(msgKey, message);
				try {
					JSONObject jsonMap = new JSONObject();
					Date nowDate = new Date();
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
					
					jsonMap.putAll(paramMap);
					Map<String, Object> paramMap2 = new LinkedHashMap();
					paramMap2.put("inProcName", "dsp_ch_clu_reward_action");
					paramMap2.put("inProcStep", 0);
					paramMap2.put("inYyyymmdd", simpleDateFormat.format(nowDate));
					paramMap2.put("inParam", jsonMap.toJSONString());
					paramMap2.put("inErrorNo", 0);
					paramMap2.put("inErrorTitle", "insert error");
					paramMap2.put("inErrorMsg", strResultMsg);
					commonMapperLrnLog.insert(paramMap2, "LrnLog.ispErrorLog");
				} catch(Exception e2) {}
			}
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}

	@Override
	public LinkedHashMap resetObjectReward(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		getStudId(paramMap);
		paramMap.put("regAdminId", "STUD_EXTRTLOG");
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId"}, paramMap);
		
		if(vu.isValid()) {
			String strResultMsg = null;
			LinkedHashMap message = new LinkedHashMap();			
			try {
			    commonMapperLrnLog.insert(paramMap, "LrnLog.dspDailyHistoryChallengeCluDel");
				Integer nResultCnt = Integer.valueOf(paramMap.get("outResultCnt").toString());
				strResultMsg = paramMap.get("outResultMsg").toString();
				if(nResultCnt > 0) {					
					message.put("resultCode", ValidationCode.REG_SUCCESS.getCode());
					message.put("result","삭제 완료 : "+strResultMsg);
					setResult(msgKey, message);					
				} else {
					message.put("resultCode", ValidationCode.REG_FAILED.getCode());
					message.put("result", strResultMsg);
					setResult(msgKey, message);
				}
			} catch(Exception e) {
				String[] errorMsgList = e.getMessage().split(": ");
				strResultMsg = "Delete failed [ " + errorMsgList[errorMsgList.length-1] + " ]";
				message.put("resultCode", ValidationCode.REG_FAILED.getCode());
				message.put("result", strResultMsg);
				setResult(msgKey, message);
				try {
					JSONObject jsonMap = new JSONObject();
					Date nowDate = new Date();
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
					
					jsonMap.putAll(paramMap);
					Map<String, Object> paramMap2 = new LinkedHashMap();
					paramMap2.put("inProcName", "dsp_ch_clu_reward_action");
					paramMap2.put("inProcStep", 0);
					paramMap2.put("inYyyymmdd", simpleDateFormat.format(nowDate));
					paramMap2.put("inParam", jsonMap.toJSONString());
					paramMap2.put("inErrorNo", 0);
					paramMap2.put("inErrorTitle", "insert error");
					paramMap2.put("inErrorMsg", strResultMsg);
					commonMapperLrnLog.insert(paramMap2, "LrnLog.ispErrorLog");
				} catch(Exception e2) {}
			}
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	

	@Override
	public LinkedHashMap setFnObjectRewardMmc(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		getStudId(paramMap);
		paramMap.put("regAdminId", "STUD_EXTRTLOG");
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId"}, paramMap);
		
		if(vu.isValid()) {
			String strResultMsg = null;
			LinkedHashMap message = new LinkedHashMap();			
			try {
			    commonMapperLrnLog.insert(paramMap, "LrnLog.ispChCLUMmcRewardAction");
				Integer nResultCnt = Integer.valueOf(paramMap.get("outResultCnt").toString());
				strResultMsg = paramMap.get("outResultMsg").toString();
				if(nResultCnt > 0) {					
					message.put("resultCode", ValidationCode.REG_SUCCESS.getCode());
					message.put("result","등록 완료 : "+strResultMsg);
					setResult(msgKey, message);			
//							갱신된 미션 정보 리로드					
				} else {
					message.put("resultCode", ValidationCode.REG_FAILED.getCode());
					message.put("result", strResultMsg);
					setResult(msgKey, message);
				}
			} catch(Exception e) {
				String[] errorMsgList = e.getMessage().split(": ");
				strResultMsg = "Registration failed [ " + errorMsgList[errorMsgList.length-1] + " ]";
				message.put("resultCode", ValidationCode.REG_FAILED.getCode());
				message.put("result", strResultMsg);
				setResult(msgKey, message);
				try {
					JSONObject jsonMap = new JSONObject();
					Date nowDate = new Date();
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
					
					jsonMap.putAll(paramMap);
					Map<String, Object> paramMap2 = new LinkedHashMap();
					paramMap2.put("inProcName", "isp_ch_clu_mmc_reward_action");
					paramMap2.put("inProcStep", 0);
					paramMap2.put("inYyyymmdd", simpleDateFormat.format(nowDate));
					paramMap2.put("inParam", jsonMap.toJSONString());
					paramMap2.put("inErrorNo", 0);
					paramMap2.put("inErrorTitle", "insert error");
					paramMap2.put("inErrorMsg", strResultMsg);
					commonMapperLrnLog.insert(paramMap2, "LrnLog.ispErrorLog");
				} catch(Exception e2) {}
			}
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}


	@Override
	public LinkedHashMap resetObjectRewardMmc(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		getStudId(paramMap);
		paramMap.put("regAdminId", "STUD_EXTRTLOG");
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId"}, paramMap);
		
		if(vu.isValid()) {
			String strResultMsg = null;
			LinkedHashMap message = new LinkedHashMap();			
			try {
			    commonMapperLrnLog.insert(paramMap, "LrnLog.dspDailyHistoryChallengeCluMmcDel");
				Integer nResultCnt = Integer.valueOf(paramMap.get("outResultCnt").toString());
				strResultMsg = paramMap.get("outResultMsg").toString();
				if(nResultCnt > 0) {					
					message.put("resultCode", ValidationCode.REG_SUCCESS.getCode());
					message.put("result","삭제 완료 : "+strResultMsg);
					setResult(msgKey, message);					
				} else {
					message.put("resultCode", ValidationCode.REG_FAILED.getCode());
					message.put("result", strResultMsg);
					setResult(msgKey, message);
				}
			} catch(Exception e) {
				String[] errorMsgList = e.getMessage().split(": ");
				strResultMsg = "Delete failed [ " + errorMsgList[errorMsgList.length-1] + " ]";
				message.put("resultCode", ValidationCode.REG_FAILED.getCode());
				message.put("result", strResultMsg);
				setResult(msgKey, message);
				try {
					JSONObject jsonMap = new JSONObject();
					Date nowDate = new Date();
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
					
					jsonMap.putAll(paramMap);
					Map<String, Object> paramMap2 = new LinkedHashMap();
					paramMap2.put("inProcName", "dsp_ch_clu_mmc_reward_action");
					paramMap2.put("inProcStep", 0);
					paramMap2.put("inYyyymmdd", simpleDateFormat.format(nowDate));
					paramMap2.put("inParam", jsonMap.toJSONString());
					paramMap2.put("inErrorNo", 0);
					paramMap2.put("inErrorTitle", "insert error");
					paramMap2.put("inErrorMsg", strResultMsg);
					commonMapperLrnLog.insert(paramMap2, "LrnLog.ispErrorLog");
				} catch(Exception e2) {}
			}
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	

}
