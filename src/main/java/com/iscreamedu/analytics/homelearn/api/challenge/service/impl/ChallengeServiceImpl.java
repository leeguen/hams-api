package com.iscreamedu.analytics.homelearn.api.challenge.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iscreamedu.analytics.homelearn.api.challenge.service.ChallengeService;
import com.iscreamedu.analytics.homelearn.api.common.mapper.CommonMapperLrnDm;
import com.iscreamedu.analytics.homelearn.api.common.mapper.CommonMapperLrnLog;
import com.iscreamedu.analytics.homelearn.api.common.service.ExternalAPIService;
import com.iscreamedu.analytics.homelearn.api.common.util.CommonUtil;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationCode;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtil;

@Service
public class ChallengeServiceImpl implements ChallengeService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChallengeServiceImpl.class);

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

	private void getStudId(Map<String, Object> paramMap) throws Exception {
		if(!paramMap.containsKey("studId") && paramMap.containsKey("p")) {
			Integer studId = -1;
			String encodedStr = paramMap.get("p").toString();
			
			String[] paramList = CommonUtil.getDecodedParam(encodedStr);
			studId = Integer.parseInt(paramList[0]);
			paramMap.put("studId", studId);
		}
	}

	@Override
	public LinkedHashMap getChallengeHabitCnt(Map<String, Object> paramMap) throws Exception {
		Map<String,Object> data = new HashMap<>();
		//Validation
		ValidationUtil vu = new ValidationUtil();
		getStudId(paramMap);
		
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId"}, paramMap);
		if(vu.isValid()) {			
			data = (Map<String, Object>) commonMapperLrnLog.get(paramMap, "LrnLog.spChallengeHabitCnt");
			setResult(dataKey, data);			
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
		getStudId(paramMap);
		
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId"}, paramMap);
		if(vu.isValid()) {			
			if(!paramMap.containsKey("startYyyyMm") || !paramMap.containsKey("endYyyyMm") || paramMap.get("startYyyyMm").toString().isEmpty() || paramMap.get("endYyyyMm").toString().isEmpty())
			{
				paramMap.put("startYyyyMm", null); 
				paramMap.put("endYyyyMm", null); 
			}
			mtpList = (ArrayList<Map<String, Object>>) commonMapperLrnLog.getList(paramMap, "LrnLog.spMonthyHistoryChallengeMtp");
			data.put("mtpList", mtpList);
			
			setResult(dataKey, data);			
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	
	@Override
	public LinkedHashMap getChMetaphorObjectStt(Map<String, Object> paramMap) throws Exception {
		Map<String,Object> data = new HashMap<>();
		ArrayList<Map<String, Object>> cluList = new ArrayList<>();
		ArrayList<Map<String, Object>> rewardList = new ArrayList<>();
		ArrayList<Map<String, Object>> rewardMotionList = new ArrayList<>();
		
		//Validation
		ValidationUtil vu = new ValidationUtil();
		getStudId(paramMap);		
		
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId"}, paramMap);
		if(vu.isValid()) {			
			
			rewardList = (ArrayList<Map<String, Object>>) commonMapperLrnLog.getList(paramMap, "LrnLog.spMonthyHistoryChallengeCluReward");
			if(rewardList != null && rewardList.size() > 0) {
				for(Map<String,Object> rewardMap : rewardList) {	     
					try {
						Map<String,Object> paramMap_motionList = new HashMap<>();
						paramMap_motionList.put("motionNoList", rewardMap.get("motionNoList").toString());
						rewardMotionList = (ArrayList<Map<String, Object>>) commonMapperLrnLog.getList(paramMap_motionList, "LrnLog.spRewardMotionList");
					}
					catch(Exception e) {
					}
					
//					if(rewardMotionList == null || rewardMotionList.size() == 0) {
//						rewardMotionList = new ArrayList<>();
//					}
					
					rewardMap.put("rewardMotionList", rewardMotionList);
					rewardList.remove("motionNoList");
				}
				data.put("rewardList", rewardList);
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
	public LinkedHashMap getChHabitMissionInfo(Map<String, Object> paramMap) throws Exception {
		Map<String,Object> data = new HashMap<>();
		ArrayList<Map<String, Object>> chlList = new ArrayList<>();
		//Validation
		ValidationUtil vu = new ValidationUtil();
		getStudId(paramMap);		
		
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId"}, paramMap);
		if(vu.isValid()) {	
			Map<String,Object> paramMap_summary = new HashMap<>();
			data = (Map<String, Object>) commonMapperLrnLog.get(paramMap, "LrnLog.spDailyHistoryChallengeChlSummary");
			if(data != null) {
				chlList = (ArrayList<Map<String, Object>>) commonMapperLrnLog.getList(paramMap, "LrnLog.spDailyHistoryChallengeChl");
//				if(chlList == null || chlList.size() == 0) {
//					chlList = new ArrayList<>();
//				}
				data.put("chlList", chlList);	
			}
			setResult(dataKey, data);
		} else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}


	@Override
	public LinkedHashMap getChStepUpMissionInfo(Map<String, Object> paramMap) throws Exception {
		Map<String,Object> data = new HashMap<>();
		ArrayList<Map<String, Object>> cluList = new ArrayList<>();
		
		//Validation
		ValidationUtil vu = new ValidationUtil();
		getStudId(paramMap);		
		
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId"}, paramMap);
		if(vu.isValid()) {			
		
			cluList = (ArrayList<Map<String, Object>>) commonMapperLrnLog.getList(paramMap, "LrnLog.spDailyHistoryChallengeClu");
			if(cluList != null && cluList.size() > 0) {
				data.put("cluList", cluList);
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
	public LinkedHashMap getKoreanBookChallenge(Map<String, Object> paramMap) throws Exception {
		Map<String,Object> data = new HashMap<>();
		ArrayList<Map<String, Object>> missionList = new ArrayList<>();
		int total_task_cnt = 0; 
        int total_comp_cnt = 0; 
        
        //Validation
		ValidationUtil vu = new ValidationUtil();
		getStudId(paramMap);
   
//		"data": {
//       		"misStep":1,
//				"misStatusCd":-1,
//				"misStartDt":null
//    }
		
		
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId"}, paramMap);
		if(vu.isValid()) {		
			try {	
				
				// 국어책 챌린지 진행 사항 외부 API 연동 추가 예정
				// 조회
				// 단계 & 상태값 & 보상지급일 insert
				
				// 오늘 추가된 실시간 정보에서 조회
				data = (Map<String, Object>) commonMapperLrnLog.get(paramMap, "LrnLog.spKoreanBookChallenge");
				
				if(data != null) {
					paramMap.put("misStep",data.get("misStep"));
					try {	
						missionList = (ArrayList<Map<String, Object>>) commonMapperLrnLog.getList(paramMap, "LrnLog.spKoreanBookChMissonList");			
				        
						if(missionList == null || missionList.size() == 0) {
							setNoDataMessage();
							LOGGER.debug("missionList is null...");
						} else { 
							total_task_cnt = missionList.size(); 
					        
							// 북카페 책읽기 상태 값 리스트
							// 홈런 API 조회 :: 국어책 API 연동 추가
							ArrayList<Map<String, Object>> bookStateInfo = new ArrayList<>();
					        ArrayList<Integer> bookIds_state = new ArrayList<Integer>();
					        String startDate = null;
					        int grade = -99;
					        for(Map<String, Object> item : missionList) {		
					        	if(!item.get("misStatusCd").toString().equals("2")) {
					        		bookIds_state.add(Integer.parseInt(item.get("misBookCd").toString()));
					        	}
						        if(grade == - 99) grade = Integer.parseInt(item.get("grade").toString());
					        	if(item.get("misCompleteDt") != null) startDate = item.get("misCompleteDt").toString();	// 마지막 미션 완료 일자가 시작일 기준!!	
					        	if(startDate == null && item.get("misStartDt") != null) startDate = item.get("misStartDt").toString();	
					        }
					        
					        
					        
//					        startDate 기준일이 없으면 step 시작 등록일자 .. 기준.. 
					        if(startDate != null && bookIds_state != null && bookIds_state.size() > 0) {
					        	LOGGER.debug("startDate : "+startDate);
						        Map<String, Object> extParamMap_2 = new HashMap<>();
						        extParamMap_2.put("apiName", "bookState");
						        extParamMap_2.put("studId", Integer.parseInt(paramMap.get("studId").toString()));
						        extParamMap_2.put("startDate", startDate.toString());
						       // 갱신 대상 :  misStartDt, misCompleteDt
						        String bookIdsTxt = "";
						        for (int bookId : bookIds_state) {
						        	if(bookIdsTxt != "") bookIdsTxt += ",";
						        	bookIdsTxt += bookId;
								}
						        extParamMap_2.put("bookIds", bookIdsTxt);
						        bookStateInfo =  (ArrayList<Map<String, Object>>) externalAPIservice.callExternalAPI(extParamMap_2).get("data");		
			//			        "data": [
			//			                 {
			//			                   "id": 2466,
			//			                   "title": "사람들이 세상을 바꾸기 시작했어요",
			//			                   "deleted": false,
			//			                   "recommend": false
			//			                 },... ]
						        for(Map<String, Object> item : missionList) {			      
							        boolean flag_mission = true;			        	  
							        if(bookStateInfo != null && bookStateInfo.size() > 0) {
							        	for(Map<String, Object> itemInfo : bookStateInfo) {	
							        		if(item.get("misBookCd").toString().equals(itemInfo.get("bookId").toString())) {
									        	if(itemInfo.containsKey("complete")) {
									        		if(flag_mission && (Boolean.parseBoolean(itemInfo.get("complete").toString()))) {
									        			// 완료한 미션순서 체크하여 순차 호출.. roof
									        			item.put("misStatusCd",2);			
									        			item.put("misCompleteDt",itemInfo.get("compDate").toString());
									        			
									        			String strContent = paramMap.get("misStep")+"|"+item.get("misNo")+"|"+item.get("misStatusCd")+"|"+itemInfo.get("bookId")+"|"+itemInfo.get("lastPage")+"|"+item.get("misSkimUrl")+"|"+startDate+"|"+itemInfo.get("compDate").toString();
									        			Map<String, Object> realTimeMKBInfo = new HashMap<>();
									        			realTimeMKBInfo.put("studId", paramMap.get("studId"));
									        			realTimeMKBInfo.put("chCd", "MKB");
									        			realTimeMKBInfo.put("misStep", paramMap.get("misStep"));
									        			realTimeMKBInfo.put("misNo", item.get("misNo"));
									        			realTimeMKBInfo.put("misStatusCd", 2);
									        			realTimeMKBInfo.put("misCompleteDt", itemInfo.get("compDate").toString());
									        			realTimeMKBInfo.put("misContents", strContent);
									        			realTimeMKBInfo.put("regAdminId", "KBChMissonList");
									        			commonMapperLrnLog.insert(realTimeMKBInfo, "LrnLog.ispChMisNoStatusChange");
									        		} else {
									        			flag_mission = false;
									        			if(itemInfo.containsKey("lastPage")) {
											        		//진행중
									        				item.put("misSkimUrl", (item.get("misSkimUrl").toString()+"&p="+itemInfo.get("lastPage")));
										        			item.put("misStatusCd",1);	
			
										        			String strContent = paramMap.get("misStep")+"|"+item.get("misNo")+"|"+item.get("misStatusCd")+"|"+itemInfo.get("bookId")+"|"+itemInfo.get("lastPage")+"|"+item.get("misSkimUrl")+"|"+startDate;
										        			Map<String, Object> realTimeMKBInfo = new HashMap<>();
										        			realTimeMKBInfo.put("studId", paramMap.get("studId"));
										        			realTimeMKBInfo.put("chCd", "MKB");
										        			realTimeMKBInfo.put("misStep", paramMap.get("misStep"));
										        			realTimeMKBInfo.put("misNo", item.get("misNo"));
										        			realTimeMKBInfo.put("misStatusCd", 1);
										        			realTimeMKBInfo.put("misCompleteDt", null);
										        			realTimeMKBInfo.put("misContents", strContent);
										        			realTimeMKBInfo.put("regAdminId", "KBChMissonList");
										        			// db 등록된게 없으면 이전 미션의 완료일시로 등록 // orderNo 1이면 미션 시작일 기준... 
										        			commonMapperLrnLog.insert(realTimeMKBInfo, "LrnLog.ispChMisNoStatusChange");
										        		}
									        		}
									        	} else {
									        		flag_mission = false;
								        		}
							        	
							        		}
							        	}
							        } else {
							        	LOGGER.debug("bookStateInfo is null...");		        		
							        }
							        
							    }
						        
						    }
					        for(Map<String, Object> item : missionList) {	
					        	if(item.get("misStatusCd").toString().equals("2")) {
					        		total_comp_cnt += 1;
					        	}
					        }
							// 조회 & 도서 비교해서 추천여부 return값 교체
							data.put("total_task_cnt", total_task_cnt);
							data.put("total_comp_cnt", total_comp_cnt);
							setResult(dataKey, data);
						}
					} catch (Exception e) {
						LOGGER.debug("bookStateInfo 연동 오류!");
					}
					setResult(dataKey, data);
				} else {
					setNoDataMessage();
				}
			} catch (Exception e) {
				setNoDataMessage();
			}
			
		} else {
			setResult(msgKey, vu.getResult());
		}
		return result;
	}
	
	@Override
	public LinkedHashMap getKoreanBookChReward(Map<String, Object> paramMap) throws Exception {
		Map<String,Object> data = new HashMap<>();
		Map<String,Object> rewardInfo = new LinkedHashMap<String, Object>();
		ArrayList<Map<String, Object>> rewardMotionList = new ArrayList<>();
        //Validation
		ValidationUtil vu = new ValidationUtil();
		getStudId(paramMap);
   
//		"data": {
//       		"rewardStep":1,
//				"rewardNo":1,
//				"misNo":1, 
//			    "rewardCd":"RK01BF",
//	 			"rewardNm":"개구리", 
//	 			"rewardCnt":1, 
//				"rewardDt":null, 
//				"rewardMotionList":[{
//								"motionNo":"",
//								"mtImg1Url":"", 
// 								"mtImg2Url":"",  
//		 						"mtSoundUrl":"",   
// 								"mtSpchBbImgUrl":"",   
// 								"mtSpchBbContent":""
//								"mtAdditions":null
//			}]		
//    }
		
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId","misStep"}, paramMap);
		if(vu.isValid()) {		
			try {	
				data = (Map<String, Object>) commonMapperLrnLog.get(paramMap, "LrnLog.spKoreanBookChReward");
				
				if(data != null) {
					try {
						Map<String,Object> paramMap_motionList = new HashMap<>();
						paramMap_motionList.put("motionNoList", data.get("motionNoList").toString());
						rewardMotionList = (ArrayList<Map<String, Object>>) commonMapperLrnLog.getList(paramMap_motionList, "LrnLog.spRewardMotionList");
					}
					catch(Exception e) {
					}
					
					if(rewardMotionList == null || rewardMotionList.size() == 0) {
						rewardMotionList = new ArrayList<>();
					}
					
					data.put("rewardMotionList", rewardMotionList);
					data.remove("motionNoList");
					setResult(dataKey, data);
				} else {
					setNoDataMessage();
				}
			} catch (Exception e) {
				setNoDataMessage();
			}
			
		} else {
			setResult(msgKey, vu.getResult());
		}
		return result;
	}

	@Override
	public LinkedHashMap getKoreanBookChMissonList(Map<String, Object> paramMap) throws Exception {
		Map<String,Object> data = new HashMap<>();
		ArrayList<Map<String, Object>> missionList = new ArrayList<>();
		int total_task_cnt = 0; 
        int total_comp_cnt = 0; 
        //Validation
		ValidationUtil vu = new ValidationUtil();
		getStudId(paramMap);
		//"bookList": [
//        {
//            "misNo": 203,
//            "misOrderNo": 1,
//            "misCd": "CLUMKB2610",
//            "misNm": "할머니의 가을 운동회",
//            "misStatusCd": 1,
//            "rewardNo": 9,
//            "misImgUrl": "https://xcdn.home-learn.com/data/origin_img/2022/04/27/220427021144954.jpg",
//            "grade": 3,
//            "misBookCd": "2610",
//            "misSkimUrl": "bookcafe://book?bookId=2610",
//            "misRcmYn": "N",
//            "misStartDt": "2022-08-18 11:22:42",
//            "misCompleteDt": null
//        }
//    ]
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId","misStep"}, paramMap);
		if(vu.isValid()) {		
			try {	
				missionList = (ArrayList<Map<String, Object>>) commonMapperLrnLog.getList(paramMap, "LrnLog.spKoreanBookChMissonList");
			
				if(missionList == null || missionList.size() == 0) {
					setNoDataMessage();
					LOGGER.debug("missionList is null...");
				} else { 
					total_task_cnt = missionList.size(); 
			        
					// misRcmYn : recommend 북카페 책제목 + 추천도서 리스트 
					// 북카페 책읽기 상태 값 리스트
					// 홈런 API 조회 :: 국어책 API 연동 추가
					ArrayList<Map<String, Object>> bookListInfo = new ArrayList<>();
					ArrayList<Map<String, Object>> bookStateInfo = new ArrayList<>();
			        ArrayList<Integer> bookIds_recommend = new ArrayList<Integer>();
			        ArrayList<Integer> bookIds_state = new ArrayList<Integer>();
			        String startDate = null;
			        int grade = -99;
			        for(Map<String, Object> item : missionList) {		
			        	bookIds_recommend.add(Integer.parseInt(item.get("misBookCd").toString()));
			        	if(!item.get("misStatusCd").toString().equals("2")) {
			        		bookIds_state.add(Integer.parseInt(item.get("misBookCd").toString()));
			        	}
				        if(grade == - 99) grade = Integer.parseInt(item.get("grade").toString());
			        	if(item.get("misCompleteDt") != null) startDate = item.get("misCompleteDt").toString();	// 마지막 미션 완료 일자가 시작일 기준!!	
			        	if(startDate == null && item.get("misStartDt") != null) startDate = item.get("misStartDt").toString();	
			        }
			        
			        if(bookIds_recommend != null && bookIds_recommend.size() > 0) {
				        Map<String, Object> extParamMap_1 = new HashMap<>();
				        extParamMap_1.put("apiName", "bookList");
				        extParamMap_1.put("bookIds", bookIds_recommend);
				        extParamMap_1.put("grade", grade);
				        
				        bookListInfo =  (ArrayList<Map<String, Object>>) externalAPIservice.callExternalAPI(extParamMap_1).get("data");	
				        for(Map<String, Object> item : missionList) {			      
					        if(bookListInfo != null && bookListInfo.size() > 0) {
					        	for(Map<String, Object> itemInfo : bookListInfo) {	
					        		if(item.get("misBookCd").toString().equals(itemInfo.get("id").toString())) {
							        	item.put("misRcmYn",(itemInfo.containsKey("recommend") ? (Boolean.parseBoolean(itemInfo.get("recommend").toString())?"Y":"N") : "N"));
					        		}
					        	}
					        } else {
					        	LOGGER.debug("bookListInfo is null...");
					        	item.put("misRcmYn","N");			        		
					        }
					    }
			        } else {
			        	LOGGER.debug("bookIds_recommend is null...");
			        }
			        
//			        startDate 기준일이 없으면 step 시작 등록일자 .. 기준.. 
			        if(startDate != null && bookIds_state != null && bookIds_state.size() > 0) {
			        	LOGGER.debug("startDate : "+startDate);
				        Map<String, Object> extParamMap_2 = new HashMap<>();
				        extParamMap_2.put("apiName", "bookState");
				        extParamMap_2.put("studId", Integer.parseInt(paramMap.get("studId").toString()));
				        extParamMap_2.put("startDate", startDate.toString());
				       // 갱신 대상 :  misStartDt, misCompleteDt
				        String bookIdsTxt = "";
				        for (int bookId : bookIds_state) {
				        	if(bookIdsTxt != "") bookIdsTxt += ",";
				        	bookIdsTxt += bookId;
						}
				        extParamMap_2.put("bookIds", bookIdsTxt);
				        bookStateInfo =  (ArrayList<Map<String, Object>>) externalAPIservice.callExternalAPI(extParamMap_2).get("data");		
	//			        "data": [
	//			                 {
	//			                   "id": 2466,
	//			                   "title": "사람들이 세상을 바꾸기 시작했어요",
	//			                   "deleted": false,
	//			                   "recommend": false
	//			                 },... ]
				        for(Map<String, Object> item : missionList) {			      
					        boolean flag_mission = true;			        	  
					        if(bookStateInfo != null && bookStateInfo.size() > 0) {
					        	for(Map<String, Object> itemInfo : bookStateInfo) {	
					        		if(item.get("misBookCd").toString().equals(itemInfo.get("bookId").toString())) {
							        	if(itemInfo.containsKey("complete")) {
							        		if(flag_mission && (Boolean.parseBoolean(itemInfo.get("complete").toString()))) {
							        			// 완료한 미션순서 체크하여 순차 호출.. roof
							        			item.put("misStatusCd",2);			
							        			item.put("misCompleteDt",itemInfo.get("compDate").toString());
							        			
							        			String strContent = paramMap.get("misStep")+"|"+item.get("misNo")+"|"+item.get("misStatusCd")+"|"+itemInfo.get("bookId")+"|"+itemInfo.get("lastPage")+"|"+item.get("misSkimUrl")+"|"+startDate+"|"+itemInfo.get("compDate").toString();
							        			Map<String, Object> realTimeMKBInfo = new HashMap<>();
							        			realTimeMKBInfo.put("studId", paramMap.get("studId"));
							        			realTimeMKBInfo.put("chCd", "MKB");
							        			realTimeMKBInfo.put("misStep", paramMap.get("misStep"));
							        			realTimeMKBInfo.put("misNo", item.get("misNo"));
							        			realTimeMKBInfo.put("misStatusCd", 2);
							        			realTimeMKBInfo.put("misCompleteDt", itemInfo.get("compDate").toString());
							        			realTimeMKBInfo.put("misContents", strContent);
							        			realTimeMKBInfo.put("regAdminId", "KBChMissonList");
							        			commonMapperLrnLog.insert(realTimeMKBInfo, "LrnLog.ispChMisNoStatusChange");
							        		} else {
							        			flag_mission = false;
							        			if(itemInfo.containsKey("lastPage")) {
									        		//진행중
	//						        				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
	//						        				ZonedDateTime zonedDateTime = ZonedDateTime.parse(new Date().toString(), formatter);
							        				item.put("misSkimUrl", (item.get("misSkimUrl").toString()+"&p="+itemInfo.get("lastPage")));
								        			item.put("misStatusCd",1);	
	
								        			String strContent = paramMap.get("misStep")+"|"+item.get("misNo")+"|"+item.get("misStatusCd")+"|"+itemInfo.get("bookId")+"|"+itemInfo.get("lastPage")+"|"+item.get("misSkimUrl")+"|"+startDate;
								        			Map<String, Object> realTimeMKBInfo = new HashMap<>();
								        			realTimeMKBInfo.put("studId", paramMap.get("studId"));
								        			realTimeMKBInfo.put("chCd", "MKB");
								        			realTimeMKBInfo.put("misStep", paramMap.get("misStep"));
								        			realTimeMKBInfo.put("misNo", item.get("misNo"));
								        			realTimeMKBInfo.put("misStatusCd", 1);
								        			realTimeMKBInfo.put("misCompleteDt", null);
								        			realTimeMKBInfo.put("misContents", strContent);
								        			realTimeMKBInfo.put("regAdminId", "KBChMissonList");
								        			// db 등록된게 없으면 이전 미션의 완료일시로 등록 // orderNo 1이면 미션 시작일 기준... 
								        			commonMapperLrnLog.insert(realTimeMKBInfo, "LrnLog.ispChMisNoStatusChange");
								        		}
							        		}
							        	} else {
							        		flag_mission = false;
						        		}
					        	
					        		}
					        	}
					        } else {
					        	LOGGER.debug("bookStateInfo is null...");		        		
					        }
					    }
			        }
			        for(Map<String, Object> item : missionList) {	
			        	if(item.get("misStatusCd").toString().equals("2")) {
			        		total_comp_cnt += 1;
			        	}
			        }
					// 조회 & 도서 비교해서 추천여부 return값 교체
					data.put("bookList", missionList);
					data.put("total_task_cnt", total_task_cnt);
					data.put("total_comp_cnt", total_comp_cnt);
					setResult(dataKey, data);
				}
			} catch (Exception e) {
				setNoDataMessage();
			}
			
		} else {
			setResult(msgKey, vu.getResult());
		}
		return result;
	}

}