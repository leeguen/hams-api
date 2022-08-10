package com.iscreamedu.analytics.homelearn.api.challenge.service.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
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
import com.iscreamedu.analytics.homelearn.api.common.mapper.CommonMapperTutor;
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
		        String defaultYyyyMm = new java.text.SimpleDateFormat("yyyyMM").format(new Date());
				paramMap.put("startYyyyMm", defaultYyyyMm); 
				paramMap.put("endYyyyMm", defaultYyyyMm); 
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
		vu.checkRequired(new String[] {"studId","startYyyyMm","endYyyyMm"}, paramMap);
		if(vu.isValid()) {			
			
			rewardList = (ArrayList<Map<String, Object>>) commonMapperLrnLog.getList(paramMap, "LrnLog.spMonthyHistoryChallengeCluReward");
			if(rewardList != null) {
				for(Map<String,Object> rewardMap : rewardList) {	     
					try {
						Map<String,Object> paramMap_motionList = new HashMap<>();
						paramMap_motionList.put("motionCdList", rewardMap.get("motionCdList").toString());
						rewardMotionList = (ArrayList<Map<String, Object>>) commonMapperLrnLog.getList(paramMap_motionList, "LrnLog.spRewardMotionList");
					}
					catch(Exception e) {
					}
					
					if(rewardMotionList == null || rewardMotionList.size() == 0) {
						rewardMotionList = new ArrayList<>();
					}
					
					rewardMap.put("rewardMotionList", rewardMotionList);
					rewardList.remove("motionCdList");
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
				if(chlList == null || chlList.size() == 0) {
					chlList = new ArrayList<>();
				}
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
			if(cluList != null) {
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
//								"motionCd":"",
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
						paramMap_motionList.put("motionCdList", data.get("motionCdList").toString());
						rewardMotionList = (ArrayList<Map<String, Object>>) commonMapperLrnLog.getList(paramMap_motionList, "LrnLog.spRewardMotionList");
					}
					catch(Exception e) {
					}
					
					if(rewardMotionList == null || rewardMotionList.size() == 0) {
						rewardMotionList = new ArrayList<>();
					}
					
					data.put("rewardMotionList", rewardMotionList);
					data.remove("motionCdList");
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
		ArrayList<Map<String, Object>> bookList = new ArrayList<>();
        //Validation
		ValidationUtil vu = new ValidationUtil();
		getStudId(paramMap);
		//"bookList":[
//  	{
//			  "misNo":1,
//			  "misOrderNo":1,
//			  "misCd":"CLUMKB2098",
//			  "misNm":"숲속 사진관",
//			  "misStatusCd":0,
//			  "misImgUrl":"https://xcdn.home-learn.com/bookcafe/9788992505529/assets/thumbs/1.jpg",
//			  "misSkimUrl":"OOOOO",
//			  "misRcmYn":"N",
//			  "misStartDt":null,
//			  "misCompleteDt":null                    
	//  }
	//]
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId","misStep"}, paramMap);
		if(vu.isValid()) {		
			try {	
				bookList = (ArrayList<Map<String, Object>>) commonMapperLrnLog.getList(paramMap, "LrnLog.spKoreanBookChMissonList");
			
				if(bookList == null || bookList.size() == 0) {
					setNoDataMessage();
				} else { 
					// misRcmYn : 추천여부 
					// 국어책 API 연동 추가 예정
					// 조회 & 도서 비교해서 추천여부 return값 교체
					data.put("bookList", bookList);
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
