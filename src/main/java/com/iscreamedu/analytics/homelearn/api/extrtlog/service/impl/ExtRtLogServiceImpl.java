package com.iscreamedu.analytics.homelearn.api.extrtlog.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.iscreamedu.analytics.homelearn.api.challenge.service.ChallengeService;
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
    private LinkedHashMap<String, Object> decodeResult;
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
	public LinkedHashMap setRealTimeCompleteMission(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		String stud_planDiv = null;
		//1.필수값 체크
		getStudId(paramMap);
		vu.checkRequired(new String[] {"studId","dt","chCd","misStatusCd","regAdminId"}, paramMap);
				
		System.out.println("setRealTimeCompleteMission - Param : " + paramMap);
				
		// studType, grade, misNo 조회해서 저장
		// MHL 오늘의 학습, MWN 오답노트, MNL미완료학습 : 정회원 studType 1, grade -99 
		// MLG 출석하기: 구분없음(체험회원+정회원) studType -1, grade -99
		// ** MKB 국어책 챌린지 : 구분없음(체험회원+정회원) studType -1, grade 있음. -- 실시간 등록 호출 대상 아님!!
		if(vu.isValid()) {
			if(paramMap.get("chCd").toString().equals("MLG")) {
				// 출석하기(습관팝업만 call)
				Map<String, Object> studInfo = new HashMap<>();
				studInfo.put("stud", commonMapperLrnLog.get(paramMap, "LrnLog.spStudInfo"));
		        Integer newStudCnt = 0;
				if(studInfo.get("stud") == null) {
					// 1. 신규 학생 정보 call api -> 등록
					//홈런 API 조회
			        Map<String,Object> realTimeStudInfo = new HashMap<>();
		        	Map<String, Object> newStudInfoMap = new HashMap<>();
			        
			        // $$$$$$$$$$$$$$$$$$$ 추후 api 변경 예정. 
			        paramMap.put("apiName", "aiReport/");
			        
			        realTimeStudInfo =  (Map<String,Object>) externalAPIservice.callExternalAPI(paramMap).get("data");
			        if(realTimeStudInfo != null && realTimeStudInfo.size() > 0) {
			        	stud_planDiv = realTimeStudInfo.get("planDiv").toString();
			        	if(stud_planDiv.equals("E")) {
				        	newStudInfoMap.put("studId", realTimeStudInfo.get("stuId"));
				        	newStudInfoMap.put("tchrKey", null);
				        	newStudInfoMap.put("parKey", null);			        	
				        	newStudInfoMap.put("ssvcAkey", (realTimeStudInfo.containsKey("planDiv")?(realTimeStudInfo.get("planDiv").toString().equals("E")?4:3):4));
				        	newStudInfoMap.put("grade", (realTimeStudInfo.containsKey("grade") ? Integer.parseInt(realTimeStudInfo.get("grade").toString()) : null));
				        	newStudInfoMap.put("lrnStatusCd", (realTimeStudInfo.containsKey("statusCd") ? Integer.parseInt(realTimeStudInfo.get("statusCd").toString().replace("000","00")) : null));
				        	newStudInfoMap.put("lrnStatusNm", (realTimeStudInfo.containsKey("lrnStatusNm") ? realTimeStudInfo.get("lrnStatusNm").toString() : null));
				        	newStudInfoMap.put("sttDt", (realTimeStudInfo.containsKey("startDe") ? realTimeStudInfo.get("startDe").toString() : null));
				        	newStudInfoMap.put("endDt", null);
				        	newStudInfoMap.put("regAdminId", "STUD_EXTRTLOG");
			        	
							commonMapperLrnLog.insert(newStudInfoMap, "LrnLog.ispStudInfo");
							newStudCnt = Integer.valueOf(newStudInfoMap.get("outResultCnt").toString());
			        	
					        if(newStudCnt > 0) {
								// 2. 미션 생성
					        	if(newStudInfoMap.containsKey("lrnStatusCd")) {
					        		commonMapperLrnLog.insert(newStudInfoMap, "LrnLog.ispChMisDailyAddMission");
					        	}
					        }
			        	}
			        }
				} else {
					paramMap.put("studType", studInfo.get("studType"));
					paramMap.put("grade", studInfo.get("grade"));
				}
			}
			
			if(paramMap.get("chCd").toString().equals("MLG") && stud_planDiv != null && stud_planDiv.equals("M")) {
				// 로그인이자 중등인 상품은 미션 해당 없음!!
				LinkedHashMap message = new LinkedHashMap();			
				message.put("resultCode", ValidationCode.REG_FAILED.getCode());
				message.put("result", "중등상품 회원 미션대상아님");
				setResult(msgKey, message);
			} else {
				String strResultMsg = null;
				LinkedHashMap message = new LinkedHashMap();			
				try {
					
					commonMapperLrnLog.insert(paramMap, "LrnLog.ispCompleteMission");
					Integer nResultCnt = Integer.valueOf(paramMap.get("outResultCnt").toString());
					strResultMsg = paramMap.get("outResultMsg").toString();
					if(nResultCnt > 0) {
						
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
					strResultMsg = "Registration failed [ " + errorMsgList[errorMsgList.length-1] + " ]";
					message.put("resultCode", ValidationCode.REG_FAILED.getCode());
					message.put("result", strResultMsg);
					setResult(msgKey, message);
	//				try {
	//					JSONObject jsonMap = new JSONObject();
	//					Date nowDate = new Date();
	//					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
	//					jsonMap.putAll(paramMap);
	//					Map<String, Object> paramMap2 = new LinkedHashMap();
	//					paramMap2.put("inProcName", "ispCompleteMission");
	//					paramMap2.put("inProcStep", 0);
	//					paramMap2.put("inYyyymmdd", simpleDateFormat.format(nowDate));
	//					paramMap2.put("inParam", jsonMap.toJSONString());
	//					paramMap2.put("inErrorNo", 0);
	//					paramMap2.put("inErrorTitle", "insert error");
	//					paramMap2.put("inErrorMsg", strResultMsg);
	//					commonMapperLrnLog.insert(paramMap2, "LrnLog.ispErrorLog");
	//				} catch(Exception e2) {}
				}
			}
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		System.out.println("setRealTimeCompleteMission - result : " + result);
		
		return result;
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
			    commonMapperLrnLog.insert(paramMap, "LrnLog.ispChMisStepStatusChange");
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

}
