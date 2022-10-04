package com.iscreamedu.analytics.homelearn.api.challenge.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iscreamedu.analytics.homelearn.api.challenge.service.ChallengeAdminService;
import com.iscreamedu.analytics.homelearn.api.challenge.service.ChallengeService;
import com.iscreamedu.analytics.homelearn.api.common.mapper.CommonMapperLrnDm;
import com.iscreamedu.analytics.homelearn.api.common.mapper.CommonMapperLrnLog;
import com.iscreamedu.analytics.homelearn.api.common.service.ExternalAPIService;
import com.iscreamedu.analytics.homelearn.api.common.util.CommonUtil;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationCode;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtil;

@Service
public class ChallengeAdminServiceImpl implements ChallengeAdminService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChallengeAdminServiceImpl.class);

    private LinkedHashMap<String, Object> result;
    private LinkedHashMap<String, Object> decodeResult;
    private String msgKey = "msg";
    private String dataKey = "data";

    @Autowired
    CommonMapperLrnLog commonMapperLrnLog;

    @Autowired
    CommonMapperLrnDm studLrnAnalMapper;
    
    @Autowired
    CommonMapperLrnDm commonMapperLrnDm;
	
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
				message.put("result", ValidationCode.NO_DATA.getMessage());
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
 
	private void setNoDataMessage() {
		LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
		msgMap.put("resultCode", ValidationCode.NO_DATA.getCode());
		msgMap.put("result", ValidationCode.NO_DATA.getMessage());
		setResult(msgKey, msgMap);
	}
	
	private void setExceptionErrorMessage() {
		LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
		msgMap.put("resultCode", ValidationCode.SYSTEM_ERROR.getCode());
		msgMap.put("result", ValidationCode.SYSTEM_ERROR.getMessage());
		setResult(msgKey, msgMap);
	}

	private void getStudId(Map<String, Object> paramMap) throws Exception {
		if(!paramMap.containsKey("studId") && paramMap.containsKey("p")) {
			Integer studId = -1;
			String encodedStr = paramMap.get("p").toString();
			
			String[] paramList = CommonUtil.getDecodedParam(encodedStr);
			studId = Integer.parseInt(paramList[1]);
			paramMap.put("studId", studId);
		}
	}
	
	private void getTchrId(Map<String, Object> paramMap) throws Exception {
		if(!paramMap.containsKey("tchrId") && paramMap.containsKey("p")) {
			String tchrId = null;
			String encodedStr = paramMap.get("p").toString();
			
			String[] paramList = CommonUtil.getDecodedParam(encodedStr);
			tchrId = paramList[2];
			paramMap.put("tchrId", tchrId);
		}
	}

	@Override
	public LinkedHashMap getChRewardStt(Map<String, Object> paramMap) throws Exception {
		Map<String,Object> data = new LinkedHashMap<>();
		//Validation
		ValidationUtil vu = new ValidationUtil();
		getStudId(paramMap);
		
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId"}, paramMap);
		if(vu.isValid()) {
			String today = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
			String curYymm = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyyMM"));
			String curMm = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("MM"));
			
			paramMap.put("today", Integer.parseInt(today));
			paramMap.put("yyyymm", Integer.parseInt(curYymm));
			paramMap.put("startYyyyMm", Integer.parseInt(curYymm));
			paramMap.put("endYyyyMm", Integer.parseInt(curYymm));
			
			/*물방울 개수 및 나무 이미지 URL 조회*/
			//Map<String, Object> waterDropData = (Map<String, Object>) commonMapperLrnLog.get(paramMap, "LrnLogAdm.spAdminMonthlyWaterDropStt");
			Map<String, Object> waterDropData = (Map<String, Object>) commonMapperLrnLog.get(paramMap, "LrnLogAdm.spMonthyHistoryChallengeMtp");
			
			if(waterDropData != null) {
				/*String imgUrl = waterDropData.get("imgUrl").toString();
				String waterJugYn = waterDropData.get("waterJugYn").toString();
				String waterJugActionYn = waterDropData.get("waterJugActionYn").toString();
				String rewardStep = (waterDropData.get("curRewardStep") != null) ? waterDropData.get("curRewardStep").toString() : "0";
				int rewardStepInteger = Integer.parseInt(rewardStep);
				
				나무 이미지 URL - 해당 단계에 맞춰 URL 주소 변경
				imgUrl = imgUrl.replace("{mm}", curMm);
				if(rewardStep.equals("6")) {
					imgUrl = imgUrl.replace("{step}", "6");
				} else {
					if(waterJugYn.equals("N")) {
						imgUrl = imgUrl.replace("{step}", rewardStep);
					} else {
						if(waterJugActionYn.equals("Y")) {
							imgUrl = imgUrl.replace("{step}", String.valueOf(rewardStepInteger + 1));
						} else {
							imgUrl = imgUrl.replace("{step}", rewardStep);
						}
					}
				}*/
				data.put("mtpImgUrl", waterDropData.get("mtpImgUrl").toString());
				data.put("waterDropCnt", waterDropData.get("monthlyWaterDropCnt"));
				data.put("mm", Integer.parseInt(curMm));
				
				/*챌린지 보상 개수 조회*/
				Map<String, Object> rewardData = (Map<String, Object>) commonMapperLrnLog.get(paramMap, "LrnLogAdm.spAdminMonthlyChRewardStt");
				
				try {
					data.put("mathCellCnt", rewardData.get("mathCellRewardCnt"));
					data.put("korBookCnt", rewardData.get("korBookRewardCnt"));
					data.put("engBookCnt", rewardData.get("engBookRewardCnt"));
				} catch (Exception e) {
					System.out.println("LrnLogAdm.spAdminMonthlyChRewardSt Error : " + e);
					data.put("mathCellCnt", 0);
					data.put("korBookCnt", 0);
					data.put("engBookCnt", 0);
				}
				setResult(dataKey, data);		
			} else {
				setNoDataMessage();
			}
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	
	@Override
	public LinkedHashMap getChStepUpMisStt(Map<String, Object> paramMap) throws Exception {
		Map<String,Object> data = new HashMap<>();
		ArrayList<Map<String, Object>> misList = new ArrayList<>();
		
		//Validation
		ValidationUtil vu = new ValidationUtil();
		getStudId(paramMap);		
		
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId"}, paramMap);
		if(vu.isValid()) {
			
			Map<String, Object> chlData = (Map<String, Object>) commonMapperLrnLog.get(paramMap, "LrnLogAdm.spAdminChallengeStt");
			
			if(chlData != null) {
				Map<String, Object> korMap = new LinkedHashMap<>();
				Map<String, Object> engMap = new LinkedHashMap<>();
				Map<String, Object> mathMap = new LinkedHashMap<>();
				ArrayList<Map<String, Object>> korBookMisList = new ArrayList<>();
				ArrayList<Map<String, Object>> mathCellMisList = new ArrayList<>();
				ArrayList<Map<String, Object>> engBookMisList = new ArrayList<>();

				String currYy =  LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy"));
				String currYyyymm =  LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyyMM"));
				String sttYymm = currYy + "01";
				int startYymm = Integer.parseInt(sttYymm);
		        int endYymm = Integer.parseInt(currYyyymm);
		        String today = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
				
		        /*한글 독서습관*/
				try {
					korMap.put("chCd", chlData.get("korChCd"));
					korMap.put("chNm", chlData.get("korChNm"));
					
					Map<String, Object> korParamMap = new HashMap<>();
			        
			        korParamMap.put("studId", Integer.parseInt(paramMap.get("studId").toString()));
			        korParamMap.put("today", Integer.parseInt(today));
			        korParamMap.put("startYymm", startYymm);
					korParamMap.put("endYymm", endYymm);
					korParamMap.put("cd", chlData.get("korChCd"));
					
					ArrayList<Map<String, Object>> korMisList = (ArrayList<Map<String, Object>>) commonMapperLrnLog.getList(korParamMap, "LrnLogAdm.spAdminChallengeMisList");
					if(korMisList.size() > 1) {
						for(Map<String, Object> bookMisItem : korMisList) {
							Map<String, Object> korMisMap = new LinkedHashMap<>();
							
							korMisMap.put("misStep", bookMisItem.get("misStep") + "단계");
							korMisMap.put("misStepStatusCd", bookMisItem.get("sttCd"));
							korMisMap.put("misTotalCnt", bookMisItem.get("totalCnt"));
							korMisMap.put("misCompCnt", bookMisItem.get("fnshCnt"));
							korMisMap.put("rewardNm", bookMisItem.get("rewardNo"));
							
							Map<String, Object> korBookParam = new HashMap<>();
							korBookParam.put("chCd", chlData.get("korChCd"));
							korBookParam.put("grade", bookMisItem.get("grade"));
							korBookParam.put("misStep", bookMisItem.get("misStep"));
							korBookParam.put("misNo", bookMisItem.get("step"));
							
							ArrayList<Map<String, Object>> korBookList = (ArrayList<Map<String, Object>>) commonMapperLrnLog.getList(korBookParam, "LrnLogAdm.spAdminChallengeBookList");
							ArrayList<String> korBookNmList = new ArrayList<>();
							
							/*한글 독서습관 완료한 책 제목 데이터*/
							for(Map<String, Object> bookItem : korBookList) {
								String bookNm = (bookItem.get("bookNm") != null) ? bookItem.get("bookNm").toString() : null;
								
								if(bookNm != null) {
									korBookNmList.add(bookNm);
								}
							}
							
							korMisMap.put("compList", korBookNmList);
							korBookMisList.add(korMisMap);
						}
						
						korMap.put("misList", korBookMisList);
						
					} else if (korMisList.size() == 1) {
						Map<String, Object> korListIndexOne = korMisList.get(0);
						
						if(korListIndexOne.get("misStep") != null) {
							Map<String, Object> korMisMap = new LinkedHashMap<>();
							
							korMisMap.put("misStep", korListIndexOne.get("misStep") + "단계");
							korMisMap.put("misStepStatusCd", korListIndexOne.get("sttCd"));
							korMisMap.put("misTotalCnt", korListIndexOne.get("totalCnt"));
							korMisMap.put("misCompCnt", korListIndexOne.get("fnshCnt"));
							korMisMap.put("rewardNm", korListIndexOne.get("rewardNo"));
							
							Map<String, Object> korBookParam = new HashMap<>();
							korBookParam.put("chCd", chlData.get("korChCd"));
							korBookParam.put("grade", korListIndexOne.get("grade"));
							korBookParam.put("misStep", korListIndexOne.get("misStep"));
							korBookParam.put("misNo", korListIndexOne.get("step"));
							
							ArrayList<Map<String, Object>> korBookList = (ArrayList<Map<String, Object>>) commonMapperLrnLog.getList(korBookParam, "LrnLogAdm.spAdminChallengeBookList");
							ArrayList<String> korBookNmList = new ArrayList<>();
							
							/*한글 독서습관 완료한 책 제목 데이터*/
							for(Map<String, Object> bookItem : korBookList) {
								String bookNm = (bookItem.get("bookNm") != null) ? bookItem.get("bookNm").toString() : null;
								
								if(bookNm != null) {
									korBookNmList.add(bookNm);
								}
							}
							
							korMisMap.put("compList", korBookNmList);
							korBookMisList.add(korMisMap);
							
							korMap.put("misList", korBookMisList);
						} else {
							korMap.put("chCd", chlData.get("korChCd"));
							korMap.put("chNm", chlData.get("korChNm"));
							korMap.put("misList", null);
						}
						
					} else {
						korMap.put("chCd", chlData.get("korChCd"));
						korMap.put("chNm", chlData.get("korChNm"));
						
						/*Map<String, Object> korDefaulMap = new LinkedHashMap<>();
						
						korDefaulMap.put("misStep", null);
						korDefaulMap.put("misStepStatusCd", -1);
						korDefaulMap.put("misTotalCnt", null);
						korDefaulMap.put("misCompCnt", null);
						korDefaulMap.put("rewardNm", chlData.get("korRewardNo"));
						korDefaulMap.put("compList", new ArrayList<>());
						
						korBookMisList.add(korDefaulMap);*/
						
						korMap.put("misList", null);
					}
					
				} catch (Exception e) {
					System.out.println("getChStepUpMisStt > korBook Error : " + e);
					/*korMap.put("chCd", chlData.get("korChCd"));
					korMap.put("chNm", chlData.get("korChNm"));
					
					Map<String, Object> korDefaulMap = new LinkedHashMap<>();
					
					korDefaulMap.put("misStep", null);
					korDefaulMap.put("misStepStatusCd", chlData.get("korSttCd"));
					korDefaulMap.put("misTotalCnt", null);
					korDefaulMap.put("misCompCnt", null);
					korDefaulMap.put("rewardNm", chlData.get("korRewardNo"));
					korDefaulMap.put("compList", new ArrayList<>());
					
					korBookMisList.add(korDefaulMap);
					
					korMap.put("misList", korBookMisList);*/
				}
				
				/*수학의 세포들*/
				try {
					/*Map<String, Object> studInfoParamMap = new HashMap<>();
					
					studInfoParamMap.put("p", paramMap.get("p"));
		        	studInfoParamMap.put("apiName", "aiReport.");
		            
		            Map<String,Object> studInfoMap = (Map<String, Object>) externalAPIservice.callExternalAPI(studInfoParamMap).get("data");
		            
		            int grade = (studInfoMap != null && studInfoMap.get("grade") != null) ? Integer.parseInt(studInfoMap.get("grade").toString()) : 0;
		            
		            if(grade > 2 && grade < 7) {
		            	
		            }*/
					
		            mathMap.put("chCd", chlData.get("mathChCd"));
					mathMap.put("chNm", chlData.get("mathChNm"));
					
					Map<String, Object> mathParamMap = new HashMap<>();
					
					String mathYymm = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyMM"));
					String mathmm = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("MM"));
					int mathMm = Integer.parseInt(mathmm); 
			        
					mathParamMap.put("studId", Integer.parseInt(paramMap.get("studId").toString()));
					mathParamMap.put("apiName", "chlg/");
					mathParamMap.put("yymm", Integer.parseInt(mathYymm));
					
					Map<String, Object> mathCellData =  (Map<String, Object>) externalAPIservice.callExternalAPI(mathParamMap).get("data");	
					
					if(mathCellData != null) {
						Map<String, Object> mathCellDetailData = (Map<String, Object>) mathCellData.get("progressData");
						
						int mathStudType = (mathCellData.get("mcStudType") != null) ? Integer.parseInt(mathCellData.get("mcStudType").toString()) : 0;
						int mathPeriod = (mathCellData.get("periodData") != null) ? Integer.parseInt(mathCellData.get("periodData").toString()) : 0;
						String mathStatusNm = (mathCellData.get("mcStudStatus") != null) ? mathCellData.get("mcStudStatus").toString() : "미신청";
						
						int mathSttCd = -1;
						String mathSttNm = "신청하기";
						
						Map<String, Object> mathRewardData = (Map<String, Object>) commonMapperLrnLog.get(paramMap, "LrnLogAdm.spAdminChallengeMathCellRewardStt");
						int mathRewardCnt = Integer.parseInt(mathRewardData.get("rewardNm").toString());
						String mathReward = (mathRewardCnt == 0 && mathPeriod == 2 ) ? "실패" : mathRewardData.get("rewardNm").toString();
						
						if(mathStudType == 1) { /*함께하기*/
							if(mathPeriod == 2) {
								mathSttNm = "결과확인";
								mathSttCd = 2;
							} else if(mathPeriod == 1) {
								if(mathStatusNm.equals("신청")) {
									mathSttNm = "진행중";
									mathSttCd = 1;
								}
							} else {
								if(mathStatusNm.equals("신청")) {
									mathSttNm = "신청완료";
									mathSttCd = 1;
								}
							}
							
						} else if(mathStudType == 2) { /*혼자하기*/
							if(mathPeriod == 2) {
								mathSttNm = "완료";
								mathSttCd = 2;
							} else {
								if(mathStatusNm.equals("신청")) {
									mathSttNm = "진행중";
									mathSttCd = 1;
								}
							}
						}
						
						
						mathMap.put("chCd", chlData.get("mathChCd"));
						mathMap.put("chNm", chlData.get("mathChNm"));
						mathMap.put("misStep", String.valueOf(mathMm)+ "월");
						mathMap.put("misStepStatusCd", mathSttCd);
						mathMap.put("misStepStatusNm", mathSttNm);
						mathMap.put("misTotalCnt", mathCellDetailData.get("totalMissionNmb"));
						mathMap.put("misCompCnt", mathCellDetailData.get("solvedQuestionNmb"));
						mathMap.put("rewardNm", mathReward);
						mathMap.put("compList", new ArrayList<>());
					} else {
						mathMap.put("chCd", chlData.get("mathChCd"));
						mathMap.put("chNm", chlData.get("mathChNm"));
						mathMap.put("misStep", null);
						mathMap.put("misStepStatusCd", -1);
						mathMap.put("misStepStatusNm", "신청하기");
						mathMap.put("misTotalCnt", null);
						mathMap.put("misCompCnt", null);
						mathMap.put("rewardNm", "-");
						mathMap.put("compList", new ArrayList<>());
					}
		            
					/*mathParamMap.put("studId", Integer.parseInt(paramMap.get("studId").toString()));
					mathParamMap.put("today", Integer.parseInt(today));
					mathParamMap.put("startYymm", startYymm);
			        mathParamMap.put("endYymm", endYymm);
			        mathParamMap.put("cd", chlData.get("mathChCd"));
					
					Map<String, Object> mathMisList = (Map<String, Object>) commonMapperLrnLog.get(mathParamMap, "LrnLogAdm.spAdminChallengeMisList");
					if(mathMisList != null) {
						mathMap.put("chCd", chlData.get("mathChCd"));
						mathMap.put("chNm", chlData.get("mathChNm"));
						mathMap.put("misStep", chlData.get("misStep") + "월");
						mathMap.put("misStepStatusCd", chlData.get("sttCd"));
						mathMap.put("misTotalCnt", chlData.get("totalCnt"));
						mathMap.put("misCompCnt", chlData.get("fnshCnt"));
						mathMap.put("rewardNm", chlData.get("rewardNo"));
						mathMap.put("compList", new ArrayList<>());
						
					} else {
						mathMap.put("chCd", chlData.get("mathChCd"));
						mathMap.put("chNm", chlData.get("mathChNm"));
						mathMap.put("misStep", null);
						mathMap.put("misStepStatusCd", -1);
						mathMap.put("misTotalCnt", null);
						mathMap.put("misCompCnt", null);
						mathMap.put("rewardNm", "-");
						mathMap.put("compList", new ArrayList<>());
					}*/
					
				} catch (Exception e) {
					System.out.println("getChStepUpMisStt > mathCell Error : " + e);
				}
				
				try {
					engMap.put("chCd", chlData.get("engChCd"));
					engMap.put("chNm", chlData.get("engChNm"));
					
					Map<String, Object> engParamMap = new HashMap<>();
			        
					engParamMap.put("studId", Integer.parseInt(paramMap.get("studId").toString()));
					engParamMap.put("today", Integer.parseInt(today));
			        engParamMap.put("startYymm", startYymm);
			        engParamMap.put("endYymm", endYymm);
			        engParamMap.put("cd", chlData.get("engChCd"));
					
					ArrayList<Map<String, Object>> engMisList = (ArrayList<Map<String, Object>>) commonMapperLrnLog.getList(engParamMap, "LrnLogAdm.spAdminChallengeMisList");
					if(engMisList.size() > 1) {
						for(Map<String, Object> bookMisItem : engMisList) {
							Map<String, Object> engMisMap = new LinkedHashMap<>();
							
							engMisMap.put("misStep", bookMisItem.get("misStep") + "단계");
							engMisMap.put("misStepStatusCd", bookMisItem.get("sttCd"));
							engMisMap.put("misTotalCnt", bookMisItem.get("totalCnt"));
							engMisMap.put("misCompCnt", bookMisItem.get("fnshCnt"));
							engMisMap.put("rewardNm", bookMisItem.get("rewardNo"));
							
							Map<String, Object> engBookParam = new HashMap<>();
							engBookParam.put("chCd", chlData.get("engChCd"));
							engBookParam.put("grade", bookMisItem.get("grade"));
							engBookParam.put("misStep", bookMisItem.get("misStep"));
							engBookParam.put("misNo", bookMisItem.get("step"));
							
							ArrayList<Map<String, Object>> engBookList = (ArrayList<Map<String, Object>>) commonMapperLrnLog.getList(engBookParam, "LrnLogAdm.spAdminChallengeBookList");
							ArrayList<String> engBookNmList = new ArrayList<>();
							
							/*한글 독서습관 완료한 책 제목 데이터*/
							for(Map<String, Object> bookItem : engBookList) {
								String bookNm = (bookItem.get("bookNm") != null) ? bookItem.get("bookNm").toString() : null;
								
								if(bookNm != null) {
									engBookNmList.add(bookNm);
								}
							}
							
							engMisMap.put("compList", engBookNmList);
							engBookMisList.add(engMisMap);
						}
						
						engMap.put("misList", engBookMisList);
						
					} else if (engMisList.size() == 1) {
						Map<String, Object> engListIndexOne = engMisList.get(0);
						
						if(engListIndexOne.get("misStep") != null) {
							Map<String, Object> engMisMap = new LinkedHashMap<>();
							
							engMisMap.put("misStep", engListIndexOne.get("misStep") + "단계");
							engMisMap.put("misStepStatusCd", engListIndexOne.get("sttCd"));
							engMisMap.put("misTotalCnt", engListIndexOne.get("totalCnt"));
							engMisMap.put("misCompCnt", engListIndexOne.get("fnshCnt"));
							engMisMap.put("rewardNm", engListIndexOne.get("rewardNo"));
							
							Map<String, Object> engBookParam = new HashMap<>();
							engBookParam.put("chCd", chlData.get("engChCd"));
							engBookParam.put("grade", engListIndexOne.get("grade"));
							engBookParam.put("misStep", engListIndexOne.get("misStep"));
							engBookParam.put("misNo", engListIndexOne.get("step"));
							
							ArrayList<Map<String, Object>> engBookList = (ArrayList<Map<String, Object>>) commonMapperLrnLog.getList(engBookParam, "LrnLogAdm.spAdminChallengeBookList");
							ArrayList<String> engBookNmList = new ArrayList<>();
							
							/*한글 독서습관 완료한 책 제목 데이터*/
							for(Map<String, Object> bookItem : engBookList) {
								String bookNm = (bookItem.get("bookNm") != null) ? bookItem.get("bookNm").toString() : null;
								
								if(bookNm != null) {
									engBookNmList.add(bookNm);
								}
							}
							
							engMisMap.put("compList", engBookNmList);
							engBookMisList.add(engMisMap);
							
							engMap.put("misList", engBookMisList);
						} else {
							engMap.put("chCd", chlData.get("engChCd"));
							engMap.put("chNm", chlData.get("engChNm"));
							
							engMap.put("misList", null);
						}
					} else {
						engMap.put("chCd", chlData.get("engChCd"));
						engMap.put("chNm", chlData.get("engChNm"));
						
						/*Map<String, Object> engDefaulMap = new LinkedHashMap<>();
						
						engDefaulMap.put("misStep", null);
						engDefaulMap.put("misStepStatusCd", -1);
						engDefaulMap.put("misTotalCnt", null);
						engDefaulMap.put("misCompCnt", null);
						engDefaulMap.put("rewardNm", "-");
						engDefaulMap.put("compList", new ArrayList<>());
						
						engBookMisList.add(engDefaulMap);*/
						
						engMap.put("misList", null);
					}
					
				} catch (Exception e) {
					System.out.println("getChStepUpMisStt > engBook Error : " + e);
				}
				
				misList.add(mathMap);
				misList.add(korMap);
				misList.add(engMap);
				
				data.put("misList", misList);
				setResult(dataKey, data);
				
				/*try {
					한글 독서습관 챌린지 데이터
					if(chlData.get("korStep") != null) {
						korMap.put("chCd", chlData.get("korChCd"));
						korMap.put("chNm", chlData.get("korChNm"));
						korMap.put("misStep", chlData.get("korMisStep") + "단계");
						korMap.put("misStepStatusCd", chlData.get("korSttCd"));
						korMap.put("misTotalCnt", chlData.get("korTotalCnt"));
						korMap.put("misCompCnt", chlData.get("korFnshCnt"));
						korMap.put("rewardNm", chlData.get("korRewardNo"));
						
						Map<String, Object> korBookParam = new HashMap<>();
						paramMap.put("chCd", chlData.get("korChCd"));
						paramMap.put("grade", chlData.get("korGrade"));
						paramMap.put("misStep", chlData.get("korMisStep"));
						paramMap.put("misNo", chlData.get("korStep"));
						
						ArrayList<Map<String, Object>> korBookList = (ArrayList<Map<String, Object>>) commonMapperLrnLog.getList(paramMap, "LrnLogAdm.spAdminChallengeBookList");
						ArrayList<String> korBookNmList = new ArrayList<>();
						
						한글 독서습관 완료한 책 제목 데이터
						for(Map<String, Object> bookItem : korBookList) {
							String bookNm = (bookItem.get("bookNm") != null) ? bookItem.get("bookNm").toString() : null;
							
							if(bookNm != null) {
								korBookNmList.add(bookNm);
							}
						}
						
						korMap.put("compList", korBookNmList);
					} else {
						korMap.put("chCd", chlData.get("korChCd"));
						korMap.put("chNm", chlData.get("korChNm"));
						korMap.put("misStep", null);
						korMap.put("misStepStatusCd", chlData.get("korSttCd"));
						korMap.put("misTotalCnt", null);
						korMap.put("misCompCnt", null);
						korMap.put("rewardNm", chlData.get("korRewardNo"));
						korMap.put("compList", new ArrayList<>());
					}
					
					수학의 세포들 챌린지 데이터
					if(chlData.get("mathStep") != null) {
						mathMap.put("chCd", chlData.get("mathChCd"));
						mathMap.put("chNm", chlData.get("mathChNm"));
						mathMap.put("misStep", chlData.get("mathMisStep") + "월");
						mathMap.put("misStepStatusCd", chlData.get("mathSttCd"));
						mathMap.put("misTotalCnt", chlData.get("mathTotalCnt"));
						mathMap.put("misCompCnt", chlData.get("mathFnshCnt"));
						mathMap.put("rewardNm", chlData.get("mathRewardNo"));
						mathMap.put("compList", new ArrayList<>());
						
					} else {
						mathMap.put("chCd", chlData.get("mathChCd"));
						mathMap.put("chNm", chlData.get("mathChNm"));
						mathMap.put("misStep", null);
						mathMap.put("misStepStatusCd", chlData.get("mathSttCd"));
						mathMap.put("misTotalCnt", null);
						mathMap.put("misCompCnt", null);
						mathMap.put("rewardNm", chlData.get("mathRewardNo"));
						mathMap.put("compList", new ArrayList<>());
					}
					
					영어 독서습관 챌린지 데이터
					if(chlData.get("engStep") != null) {
						engMap.put("chCd", chlData.get("engChCd"));
						engMap.put("chNm", chlData.get("engChNm"));
						engMap.put("misStep", chlData.get("engMisStep") + "단계");
						engMap.put("misStepStatusCd", chlData.get("engSttCd"));
						engMap.put("misTotalCnt", chlData.get("engTotalCnt"));
						engMap.put("misCompCnt", chlData.get("engFnshCnt"));
						engMap.put("rewardNm", chlData.get("engRewardNo"));
						engMap.put("compList", new ArrayList<>());
						
					} else {
						engMap.put("chCd", chlData.get("engChCd"));
						engMap.put("chNm", chlData.get("engChNm"));
						engMap.put("misStep", null);
						engMap.put("misStepStatusCd", chlData.get("engSttCd"));
						engMap.put("misTotalCnt", null);
						engMap.put("misCompCnt", null);
						engMap.put("rewardNm", chlData.get("engRewardNo"));
						engMap.put("compList", new ArrayList<>());
					}
					
					misList.add(mathMap);
					misList.add(korMap);
					misList.add(engMap);
					
					data.put("misList", misList);
					setResult(dataKey, data);
				} catch (Exception e) {
					System.out.println("getChStepUpMisStt Error : " + e);
					setExceptionErrorMessage();
				}*/
				
			} else {
				setNoDataMessage();
			}
			
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	
	@Override
	public LinkedHashMap getChHabitMisStt(Map<String, Object> paramMap) throws Exception {
		Map<String,Object> data = new LinkedHashMap<>();
		ArrayList<Map<String, Object>> chlList = new ArrayList<>();
		//Validation
		ValidationUtil vu = new ValidationUtil();
		getStudId(paramMap);		
		
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId"}, paramMap);
		if(vu.isValid()) {	
			String today = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
			String curYymm = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyyMM"));
			
			paramMap.put("today", Integer.parseInt(today));
			paramMap.put("yymm", Integer.parseInt(curYymm));
			
			ArrayList<Map<String, Object>> habitList = (ArrayList<Map<String, Object>>) commonMapperLrnLog.getList(paramMap, "LrnLogAdm.spAdminHabitStt");
			
			if(habitList != null && habitList.size() > 0) {
				try {
					Map<String, Object> waterDropData = (Map<String, Object>) commonMapperLrnLog.get(paramMap, "LrnLogAdm.spAdminDailyWaterDropStt");
					
					String rewardNm = "-";
					if(waterDropData != null) {
						rewardNm = (waterDropData.get("rewardNm") != null ) ? waterDropData.get("rewardNm").toString() : "-";
					}
					
					data.put("rewardNm", rewardNm);
					data.put("habitList", habitList);
					setResult(dataKey, data);
				} catch (Exception e) {
					System.out.println("getChHabitMisStt Error : " + e);
					setExceptionErrorMessage();
				}
			} else {
				//setNoDataMessage();
				try {
					String rewardNm = "-";
					
					/*Map<String,Object> defaultMap = new LinkedHashMap<>();
					
					defaultMap.put("chCd", null);
					defaultMap.put("misNm", null);
					defaultMap.put("misStatusCd", null);
					defaultMap.put("misCompDttm", null);
					
					habitList.add(defaultMap);*/
					habitList = null;
					
					//data.put("rewardNm", rewardNm);
					data.put("habitList", habitList);
					setResult(dataKey, data);
				} catch (Exception e) {
					System.out.println("getChHabitMisStt Error : " + e);
					setExceptionErrorMessage();
				}
			}
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	
	@Override
	public LinkedHashMap getChMetaphorHistory(Map<String, Object> paramMap) throws Exception {
		Map<String,Object> data = new HashMap<>();
		ArrayList<Map<String, Object>> mtpList = new ArrayList<>();
		//Validation
		ValidationUtil vu = new ValidationUtil();
		ValidationUtil vu1 = new ValidationUtil();
		getStudId(paramMap);
		
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId"}, paramMap);
		if(vu.isValid()) {			
			
			vu1.checkRequired(new String[] {"yyyy"}, paramMap);
			
			if(vu1.isValid()) {
				
				String sttYymm = paramMap.get("yyyy").toString() + "01";
				int startYymm = Integer.parseInt(sttYymm);
				int endYymm = getEndYymmForMeta(paramMap.get("yyyy").toString()); 
				
				paramMap.put("startYymm", startYymm);
				paramMap.put("endYymm", endYymm);
				
				ArrayList<Map<String, Object>> metaphorList = (ArrayList<Map<String, Object>>) commonMapperLrnLog.getList(paramMap, "LrnLogAdm.spAdminMonthlyMetaphorHistory");
				
				if(metaphorList != null && metaphorList.size() > 0) {
					for(Map<String, Object> metapItem : metaphorList) {
						Map<String, Object> metaItemMap = new LinkedHashMap<>();
						
						String imgUrl = null;
						if(metapItem.get("imgUrl") != null) {
							imgUrl = metapItem.get("imgUrl").toString();
							String curMm = metapItem.get("mm").toString();
							String rewardStep = metapItem.get("rewardStep").toString();
							
							/*나무 이미지 URL - 해당 단계에 맞춰 URL 주소 변경*/
							imgUrl = imgUrl.replace("{mm}", curMm);
							imgUrl = imgUrl.replace("{step}", rewardStep);
						}
						
						metaItemMap.put("yyyymm", metapItem.get("yyyymm"));
						metaItemMap.put("mtpImgUrl", imgUrl);
						
						mtpList.add(metaItemMap);
					}
					
					data.put("mtpImgList", mtpList);
					
					setResult(dataKey, data);	
				} else {
					setNoDataMessage();
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
	public LinkedHashMap getChHabitMisHistory(Map<String, Object> paramMap) throws Exception {
		Map<String,Object> data = new LinkedHashMap<>();
		ArrayList<Map<String, Object>> habitList = new ArrayList<>();
		
		//Validation
		ValidationUtil vu = new ValidationUtil();
		ValidationUtil vu1 = new ValidationUtil();
		getStudId(paramMap);		
		
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId"}, paramMap);
		if(vu.isValid()) {			
			
			vu1.checkRequired(new String[] {"yyyymm"}, paramMap);
			
			if(vu1.isValid()) {
				/*월별 물방울 수 조회*/
				Map<String, Object> waterDropData = (Map<String, Object>) commonMapperLrnLog.get(paramMap, "LrnLogAdm.spAdminMonthlyWaterDropStt");
				/*일별 미션 조회*/
				habitList = (ArrayList<Map<String, Object>>) commonMapperLrnLog.getList(paramMap, "LrnLogAdm.spAdminMonthlyHabitStt");
				
				if(habitList != null && habitList.size() > 0) {
					/*일별 미션 리스트 조회*/
					ArrayList<Map<String, Object>> habitMisList = (ArrayList<Map<String, Object>>) commonMapperLrnLog.getList(paramMap, "LrnLogAdm.spAdminMonthlyHabitHistory"); 
					
					try {
						/*일별 미션*/
						for(Map<String, Object> habitItem : habitList) {
							ArrayList<Map<String, Object>> misItemList = new ArrayList<>();
							int dt = (habitItem.get("dt") != null) ? Integer.parseInt(habitItem.get("dt").toString()) : 0;
							
							/*일별 미션 리스트*/
							for(Map<String, Object> habitMisItem : habitMisList) {
								int misDt = (habitMisItem.get("dt") != null) ? Integer.parseInt(habitMisItem.get("dt").toString()) : 0;
								
								if(dt == misDt) {
									habitMisItem.remove("dt");
									misItemList.add(habitMisItem);
									continue;
								}
							}
							habitItem.put("misList", misItemList);
						}
						
						data.put("waterDropCnt", waterDropData.get("waterDropCnt"));
						data.put("habitList", habitList);
						setResult(dataKey, data);
					} catch (Exception e) {
						System.out.println("getChHabitMisHistory Error : " + e);
						setExceptionErrorMessage();
					}
					
				} else {
					setNoDataMessage();
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
	public LinkedHashMap getChStepUpMisHistory(Map<String, Object> paramMap) throws Exception {
		Map<String,Object> data = new HashMap<>();
        
        //Validation
		ValidationUtil vu = new ValidationUtil();
		ValidationUtil vu1 = new ValidationUtil();
		getStudId(paramMap);		
		
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId"}, paramMap);
		if(vu.isValid()) {		
			vu1.checkRequired(new String[] {"yyyy"}, paramMap);
			
			if(vu1.isValid()) {
				String sttYymm = paramMap.get("yyyy").toString() + "01";
				int startYymm = Integer.parseInt(sttYymm);
		        int endYymm = getEndYymm(paramMap.get("yyyy").toString());
		        String today = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		        
		        paramMap.put("today", Integer.parseInt(today));
				paramMap.put("startYymm", startYymm);
				paramMap.put("endYymm", endYymm);
				
				Map<String, Object> challData = (Map<String, Object>) commonMapperLrnLog.get(paramMap, "LrnLogAdm.spAdminMonthlyChallengeStt");
				
				if(challData != null) {
					ArrayList<Map<String, Object>> misList = new ArrayList<>();
					Map<String, Object> korMap = new LinkedHashMap<>();
					Map<String, Object> engMap = new LinkedHashMap<>();
					Map<String, Object> mathMap = new LinkedHashMap<>();
					
					try {
						/*한글 독서습관 챌린지 데이터*/
						if(challData.get("korRewardCnt") != null) {
							korMap.put("chCd", challData.get("korChCd"));
							korMap.put("chNm", challData.get("korChNm"));
							korMap.put("rewardCnt", challData.get("korRewardCnt"));
							
							paramMap.put("chCd", "MKB");
							ArrayList<Map<String, Object>> korBookMisList = new ArrayList<>();
							ArrayList<Map<String, Object>> korMisList = (ArrayList<Map<String, Object>>) commonMapperLrnLog.getList(paramMap, "LrnLogAdm.spAdminMonthlyChallengeHistory");
							
							if(korMisList != null && korMisList.size() > 0) {
								for(Map<String, Object> korMisItem : korMisList) {
									Map<String, Object> korBookMap = new LinkedHashMap<>();
									
									int sttCd = (korMisItem.get("sttCd") != null) ? Integer.parseInt(korMisItem.get("sttCd").toString()) : -1;
									
									korBookMap.put("dt", korMisItem.get("dt"));
									korBookMap.put("misStep", korMisItem.get("misStep") + "단계");
									korBookMap.put("misStepStatusCd", korMisItem.get("sttCd"));
									korBookMap.put("misTotalCnt", korMisItem.get("totalCnt"));
									korBookMap.put("misCompCnt", korMisItem.get("fnshCnt"));
									
									
									if(sttCd == 2 || sttCd == 4) {
										korBookMap.put("rewardNm", korMisItem.get("rewardNm"));
										
										Map<String, Object> korBookParam = new HashMap<>();
										paramMap.put("chCd", "MKB");
										paramMap.put("grade", korMisItem.get("grade"));
										paramMap.put("misStep", korMisItem.get("misStep"));
										paramMap.put("misNo", korMisItem.get("step"));
										
										ArrayList<Map<String, Object>> korBookList = (ArrayList<Map<String, Object>>) commonMapperLrnLog.getList(paramMap, "LrnLogAdm.spAdminChallengeBookList");
										ArrayList<String> korBookNmList = new ArrayList<>();
										
										/*한글 독서습관 완료한 책 제목 데이터*/
										for(Map<String, Object> bookItem : korBookList) {
											String bookNm = (bookItem.get("bookNm") != null) ? bookItem.get("bookNm").toString() : null;
											
											if(bookNm != null) {
												korBookNmList.add(bookNm);
											}
										}
										
										korBookMap.put("compList", korBookNmList);
									} else {
										korBookMap.put("rewardNm", "-");
										
										korBookMap.put("compList", new ArrayList<>());
									}
									
									korBookMisList.add(korBookMap);
								}
								
								korMap.put("monthList", korBookMisList);
							} else {
								korMap.put("monthList", new ArrayList<>());
							}
							
						} else {
							korMap.put("chCd", challData.get("korChCd"));
							korMap.put("chNm", challData.get("korChNm"));
							korMap.put("rewardCnt", challData.get("korRewardCnt"));
							korMap.put("monthList", new ArrayList<>());
						}
						
						/*수학의 세포들 챌린지 데이터*/
						if(challData.get("mathRewardCnt") != null) {
							mathMap.put("chCd", challData.get("mathChCd"));
							mathMap.put("chNm", challData.get("mathChNm"));
							mathMap.put("rewardCnt", challData.get("mathRewardCnt"));
							mathMap.put("monthList", new ArrayList<>());
							
						} else {
							mathMap.put("chCd", challData.get("mathChCd"));
							mathMap.put("chNm", challData.get("mathChNm"));
							mathMap.put("rewardCnt", challData.get("mathRewardCnt"));
							mathMap.put("monthList", new ArrayList<>());
						}
						
						/*영어 독서습관 챌린지 데이터*/
						if(challData.get("engRewardCnt") != null) {
							engMap.put("chCd", challData.get("engChCd"));
							engMap.put("chNm", challData.get("engChNm"));
							engMap.put("rewardCnt", challData.get("engRewardCnt"));
							engMap.put("monthList", new ArrayList<>());
							
						} else {
							engMap.put("chCd", challData.get("engChCd"));
							engMap.put("chNm", challData.get("engChNm"));
							engMap.put("rewardCnt", challData.get("engRewardCnt"));
							engMap.put("monthList", new ArrayList<>());
						}
						
						misList.add(mathMap);
						misList.add(korMap);
						misList.add(engMap);
						
						data.put("chList", misList);
						
						
						setResult(dataKey, data);
					} catch (Exception e) {
						System.out.println("getChStepUpMisHistory Error : " + e);
						setExceptionErrorMessage();
					}
				} else {
					setNoDataMessage();
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
	public LinkedHashMap regAdminCompleteMission(Map<String, Object> paramMap) throws Exception {
		Map<String,Object> data = new HashMap<>();
		
		//Validation
		ValidationUtil vu = new ValidationUtil();
		ValidationUtil vu1 = new ValidationUtil();
		getStudId(paramMap);		
		
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId"}, paramMap);
		
		if(vu.isValid()) {
			vu1.checkRequired(new String[] {"chCd"}, paramMap);
			
			if(vu1.isValid()) {
				getTchrId(paramMap);
				
				String strResultMsg = null;
				LinkedHashMap message = new LinkedHashMap();			
				try {
					//dt형 변환
					String today = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
					String paramDate = today;
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date = format.parse(paramDate);
					SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					
					Map<String,Object> insertParams = new HashMap<>();
					insertParams.put("studId", paramMap.get("studId"));
					insertParams.put("dt", format2.format(date));
					insertParams.put("chCd", paramMap.get("chCd"));
					insertParams.put("misStatusCd", 2);
					insertParams.put("misContents", null);
					insertParams.put("regAdminId", paramMap.get("tchrId"));
					
					commonMapperLrnLog.insert(insertParams, "LrnLog.ispCompleteMission");
					Integer nResultCnt = Integer.valueOf(insertParams.get("outResultCnt").toString());
					strResultMsg = insertParams.get("outResultMsg").toString();
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
					/*try {
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
					} catch(Exception e2) {}*/
				}
			} else {
				setResult(msgKey, vu1.getResult());
			}
			
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	
	private int getEndYymm(String yyyy) {
		int yymm = 0;
		
		Calendar month = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
    	month.add(Calendar.MONTH , 0);
        String edYyyy = new java.text.SimpleDateFormat("yyyy").format(month.getTime());
        String edYymms = new java.text.SimpleDateFormat("yyyyMM").format(month.getTime());
        String edYymm = yyyy+"12";
        
        int endYymms = Integer.parseInt(edYymms);
        int endYymm = Integer.parseInt(edYymm);
        
        if(yyyy.equals(edYyyy)) {
        	if(endYymms < endYymm) {
        		yymm = endYymms;
        	} else {
        		yymm = endYymm;
        	}
        } else {
        	yymm = endYymm;
        }
		
		return yymm;
	}
	
	private int getEndYymmForMeta(String yyyy) {
		int yymm = 0;
		
		Calendar month = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
    	month.add(Calendar.MONTH , -1);
        String edYyyy = new java.text.SimpleDateFormat("yyyy").format(month.getTime());
        String edYymms = new java.text.SimpleDateFormat("yyyyMM").format(month.getTime());
        String edYymm = yyyy+"12";
        
        int endYymms = Integer.parseInt(edYymms);
        int endYymm = Integer.parseInt(edYymm);
        
        if(yyyy.equals(edYyyy)) {
        	if(endYymms < endYymm) {
        		yymm = endYymms;
        	} else {
        		yymm = endYymm;
        	}
        } else {
        	yymm = endYymm;
        }
		
		return yymm;
	}
}