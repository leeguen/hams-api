package com.iscreamedu.analytics.homelearn.api.hamsSales.service.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iscreamedu.analytics.homelearn.api.common.mapper.CommonMapper;
import com.iscreamedu.analytics.homelearn.api.common.service.ExternalAPIService;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationCode;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtil;
import com.iscreamedu.analytics.homelearn.api.hamsSales.service.HamsSalesService;
import com.iscreamedu.analytics.homelearn.api.common.security.CipherUtil;

/**
 * HAMS Sales API ServiceImpl
 * @author shoshu
 * @since 2020.09.21
 * @version 1.0
 * @see
 *  
 * <pre>
 * << 개정이력(Modification Information) >>
 * 
 *  수정일      		수정자		수정내용
 *  ----------  --------    --------------------------
 *  2020.09.21	shoshu		초기생성 
 *  </pre>
 */
@Service
public class HamsSalesServiceImpl implements HamsSalesService {
	private static final Logger LOGGER = LoggerFactory.getLogger(HamsSalesServiceImpl.class);
	
	@Autowired
	CommonMapper commonMapper;
	
	@Autowired
	ExternalAPIService externalAPIservice;
	
	private LinkedHashMap<String, Object> result;
	private String msgKey = "msg";
	private String dataKey = "data";
	
	@Override
	public Map healthCheck(Map<String, Object> paramMap) throws Exception {
		setResult(dataKey, commonMapper.getList(paramMap, "Common.healthCheck"));
		return result;
	}
	
	@Override
	public Map getSubjCodeInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> data = new HashMap<>();
		List<Map<String, Object>> subjCodeInfo = new ArrayList();
		List<Map<String,Object>> subjCodeInfoList = (List) commonMapper.getList(paramMap, "HamsSales.subjCodeInfo");
		
		if(subjCodeInfoList.size() > 0) {
			for (Map<String,Object> item : subjCodeInfoList) {
				subjCodeInfo.add(item);
			}
			
			data.put("subjCodeInfo", subjCodeInfo);
		}
		
		setResult(dataKey, data);
		
		return result;
	}
	
	@Override
	public Map getStudInfo(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"p"}, paramMap);
		
		LocalDate dt = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1);
		dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		/*
		String studId = "";
		String encodedStr = paramMap.get("p").toString();
		String decodedStr = "";
		
		CipherUtil cp = CipherUtil.getInstance();
		
		try {
			decodedStr = cp.AES_Decode(encodedStr);
			
			String[] paramList = decodedStr.split("&");
			studId = paramList[1];
		} catch (Exception e) {
			LOGGER.debug("HL Parameter Incorrect");
		}

		paramMap.put("studId", studId);
		*/
		paramMap.put("studId", paramMap.get("p"));
		paramMap.put("dt", dt);
		
		Map<String, Object> data = new HashMap<>();
		Map<String,Object> studInfoList = (Map) commonMapper.get(paramMap, "HamsSales.selectStudInfo");
		
		if(studInfoList != null) {
			data.put("studInfo", studInfoList);
		}
		
		if(vu.isValid()) {
			setResult(dataKey, data);
		}else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	
	@Override
	public Map getDailyLrnStt(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> data = new HashMap<>();
		List<Map<String, Object>> dailyLrnStt = new ArrayList();
		
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"dt", "studId"}, paramMap);
		//2.dt 날짜형 체크
		if(vu.isValid()) vu.isDate("dt", (String)paramMap.get("dt"));
		//3.id 숫자형 체크
		if(vu.isValid()) vu.isNumeric("studId", String.valueOf(paramMap.get("studId")));
		
		List<Map<String,Object>> commWkDtList = (List) commonMapper.getList(paramMap, "Common.selectCommWkDt");
		
		paramMap.put("startDt", commWkDtList.get(0).get("dt").toString());
		paramMap.put("endDt", commWkDtList.get(commWkDtList.size() - 1).get("dt").toString());
		
		List<Map<String,Object>>  dayAttLogList = (List) commonMapper.getList(paramMap, "HamsSales.selectLoginLogList");
		
		int i =0;
		
		if(dayAttLogList.size() > 0 ) {
			for (Map<String,Object> item : commWkDtList) {
				Map<String, Object> dailyLrnSttMap = new HashMap<>();
				String dt = (String)item.get("dt");
				paramMap.put("dt", dt);
				
				dailyLrnSttMap.put("dt", dt);
				i+=1;
				
				LocalDate currDt = LocalDate.now(ZoneId.of("Asia/Seoul"));
				currDt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				
				if(LocalDate.parse(dt, DateTimeFormatter.ISO_DATE).isBefore(currDt)) {
					if(i%2 == 0) {
						dailyLrnSttMap.put("attYn", false); //HL API output으로 교체 필요
						dailyLrnSttMap.put("lrnStt", 3); //HL API output으로 교체 필요				
					} else {
						dailyLrnSttMap.put("attYn", true); //HL API output으로 교체 필요
						dailyLrnSttMap.put("lrnStt", new Random().nextInt(2)+1); //HL API output으로 교체 필요
					}
				} else {
					dailyLrnSttMap.put("attYn", false); //HL API output으로 교체 필요
					dailyLrnSttMap.put("lrnStt", 0); //HL API output으로 교체 필요
				}
				
				List<String> loginLogList = new ArrayList<String>();
				
				for(Map<String,Object> dayAttLogItem : dayAttLogList) {
					if(dayAttLogItem.get("dt").equals(dt)) {
						loginLogList.add((String)dayAttLogItem.get("loginDttm"));
					}
				}
				
				dailyLrnSttMap.put("loginLogList", loginLogList);
			
				dailyLrnStt.add(dailyLrnSttMap);
			}
			
			data.put("dailyLrnStt", dailyLrnStt);
		}
		
		if(vu.isValid()) {
			setResult(dataKey, data);
		}else {
			setResult(msgKey, vu.getResult()); 
		}
		
		return result;
	}
	
	@Override
	public Map getSettleInfoPrediction(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"dt", "studId"}, paramMap);
		//2.dt 날짜형 체크
		if(vu.isValid()) vu.isDate("dt", (String)paramMap.get("dt"));
		//3.id 숫자형 체크
		if(vu.isValid()) vu.isNumeric("studId", String.valueOf(paramMap.get("studId")));
		
		Map<String, Object> data = new HashMap<>();
		Map<String, Object> settleInfoPrediction = new HashMap<>();
		settleInfoPrediction.put("signal", 1);
		
		List<String> focusPointList = new ArrayList<>();
		focusPointList.add("수행률");
		focusPointList.add("계획된 학습 학습시간");
		focusPointList.add("정답을 맞힌 문제 수");
		
		settleInfoPrediction.put("focusPointList", focusPointList);
		data.put("settleInfoPrediction", settleInfoPrediction);
		
		if(vu.isValid()) {
			setResult(dataKey, data);
		}else {
			setResult(msgKey, vu.getResult()); 
		}
		
		return result;
	}
	
	@Override
	public Map getThreeDayLrn(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"dt", "studId"}, paramMap);
		//2.dt 날짜형 체크
		if(vu.isValid()) vu.isDate("dt", (String)paramMap.get("dt"));
		//3.id 숫자형 체크
		if(vu.isValid()) vu.isNumeric("studId", String.valueOf(paramMap.get("studId")));
		
		
		
		Map<String, Object> data = new LinkedHashMap<>();
		Map<String, Object> threeDayLrn = new LinkedHashMap<>();
		Map<String, Object> consultingMsg = new HashMap<>();
		
		Map<String, Object> threeDayLrnData = (Map) commonMapper.get(paramMap, "HamsSales.selectThreeDayLrn");
		if(threeDayLrnData != null) {
			Map<String, Object> paramData = new HashMap<>();
			Map<String, Object> apiMap = new HashMap<>();
			Map<String, Object> positiveMsgMap = new HashMap<>();
			Map<String, Object> negativeMsgMap = new HashMap<>();
			Map<String, Object> summaryMap = new HashMap<>();
			
			int bookCnt;
			
			String toDate = paramMap.get("dt").toString();
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
			
			Date date = format.parse(toDate);
			Calendar calendar = Calendar.getInstance();
			
			calendar.setTime(date);
			calendar.add(Calendar.DATE, -2);
			String fromDate = format.format(calendar.getTime());
			
			paramData.put("memId", paramMap.get("studId"));
			paramData.put("fromData", fromDate);
			paramData.put("toDate", paramMap.get("dt"));
			paramData.put("apiName", "read.complete");
			
			apiMap =  (Map<String, Object>) externalAPIservice.callExternalAPI(paramData).get("data");
			bookCnt = Integer.valueOf(apiMap.get("numberOfElements").toString());
			
			/*칭찬 메시지 / 칭찬 메시지 수*/
			
			int positiveMsgCnt = Integer.valueOf(threeDayLrnData.get("positiveMsgCnt").toString());
			
			List<String> complimentList = new ArrayList<>();
			List<String> complimentMsg = new ArrayList<>();
			String[] complimentListData = threeDayLrnData.get("positiveMsgCdSp").toString().split(",");
			
			for(String msg : complimentListData) {
				if(msg.indexOf("|") > 0) {
					complimentMsg.add(msg.substring(0, msg.indexOf("|")));
				}else {
					complimentMsg.add(msg);
				}
			}
			
			if(positiveMsgCnt == 0 && bookCnt > 1 ) {
				complimentMsg.clear();
				complimentMsg.add("D10.80000000");
				
				threeDayLrn.put("complimentCnt", 1);
			}else if(positiveMsgCnt < 5 && bookCnt > 1){
				complimentMsg.add("D10.80000000");
				
				threeDayLrn.put("complimentCnt", positiveMsgCnt + 1);
			}else {
				threeDayLrn.put("complimentCnt", threeDayLrnData.get("positiveMsgCnt"));
			}
			
			positiveMsgMap.put("msgCd", complimentMsg);
			positiveMsgMap.put("cdType", "D");
			
			List<Map<String,Object>> complimentMsgList = commonMapper.getList(positiveMsgMap, "HamsSales.selectThreeDayLrnMsg");
			
			for(int i = 0; i < complimentMsgList.size(); i++) {
				String msg = complimentMsgList.get(i).get("cdNm").toString();
				
				if(i < positiveMsgCnt) {
					String msgCd = complimentListData[i].toString();
					
					if(msg.contains("{1}")) {
						if(msg.contains("{2}")) {
							msg = msg.replace("{1}", msgCd.substring(msgCd.indexOf("|")+1, msgCd.lastIndexOf("|")));
							msg = msg.replace("{2}", msgCd.substring(msgCd.lastIndexOf("|")+1));
						}else {
							msg = msg.replace("{1}", msgCd.substring(msgCd.indexOf("|")+1));
						}
					}
					
				}
				
				if(complimentMsg.get(i).toString() == "D10.80000000") {
					msg = msg.replace("{1}", String.valueOf(bookCnt));
				}
				
				complimentList.add(msg);
			}
			
			threeDayLrn.put("complimentList", complimentList);
			/*칭찬 메시지 / 칭찬 메시지 수*/
			
			/*처방 메시지 / 처방 메시지 수*/
			threeDayLrn.put("prescriptionCnt", threeDayLrnData.get("negativeMsgCnt"));
			
			List<String> prescriptionList = new ArrayList<>();
			List<String> prescriptionMsg = new ArrayList<>();
			
			String[] prescriptionListData = threeDayLrnData.get("negativeMsgCdSp").toString().split(",");
			
			for(String msg : prescriptionListData) {
				if(msg.indexOf("|") > 0) {
					prescriptionMsg.add(msg.substring(0, msg.indexOf("|")));
				}else {
					prescriptionMsg.add(msg);
				}
			}
			
			negativeMsgMap.put("msgCd", prescriptionMsg);
			negativeMsgMap.put("cdType", "D");
			
			List<Map<String,Object>> prescriptionMsgList = commonMapper.getList(negativeMsgMap, "HamsSales.selectThreeDayLrnMsg");
			
			for(int i = 0; i < prescriptionMsgList.size(); i++) {
				String msg = prescriptionMsgList.get(i).get("cdNm").toString();
				
				if(i <= prescriptionListData.length) {
					String msgCd = prescriptionListData[i].toString();
					
					if(msg.contains("{1}")) {
						msg = msg.replace("{1}", msgCd.substring(msgCd.indexOf("|")+1));
					}
				}
				prescriptionList.add(msg);
			}
			
			threeDayLrn.put("prescriptionList", prescriptionList);
			/*처방 메시지 / 처방 메시지 수*/
			
			/*상담 메시지 / 상담 메시지 수*/
			List<String> consultMsgData = new ArrayList<>();
			
			String[] consultingListData = threeDayLrnData.get("summaryMsgCd").toString().split(",");
			
			for(String msg : consultingListData) {
				if(msg.indexOf("|") > 0) {
					consultMsgData.add(msg.substring(0, msg.indexOf("|")));
				}else {
					consultMsgData.add(msg);
				}
			}
			
			if(consultMsgData.size() > 3) {
				if("D31.11311000".equals(consultMsgData.get(1)) || "D31.11400000".equals(consultMsgData.get(1))) {
					if(bookCnt > 0) {
						consultMsgData.remove(1);
					}else {
						consultMsgData.remove(2);
					}
				}
			}
			
			summaryMap.put("msgCd", consultMsgData);
			summaryMap.put("cdType", "D");
			
			List<Map<String,Object>> consultingMsgList = commonMapper.getList(summaryMap, "HamsSales.selectThreeDayLrnMsg");
			
			String consultMsg = null;
			
			for(int i = 0; i < consultingMsgList.size(); i++) {
				String msg = consultingMsgList.get(i).get("cdNm").toString();
				String msgCd = consultingListData[i].toString();
				
				if(msg.contains("{1}")) {
					msg =  msg.replace("{1}", msgCd.substring(msgCd.indexOf("|")+1));
				}
				
				if(i == 0) {
					consultMsg = msg;
				}else {
					consultMsg = consultMsg + msg;
				}
			}
					
			consultingMsg.put("lrnExCnt", threeDayLrnData.get("lrnExCnt"));
			consultingMsg.put("msg", consultMsg);
			/*상담 메시지 / 상담 메시지 수*/
			
			data.put("threeDayLrn", threeDayLrn);
			data.put("consultingMsg", consultingMsg);
		}
		if(vu.isValid()) {
			setResult(dataKey, data);
		}else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	
	@Override
	public Map getLrnPtn(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"dt", "studId"}, paramMap);
		//2.dt 날짜형 체크
		if(vu.isValid()) vu.isDate("dt", (String)paramMap.get("dt"));
		//3.id 숫자형 체크
		if(vu.isValid()) vu.isNumeric("studId", String.valueOf(paramMap.get("studId")));
		
		Map<String, Object> data = new HashMap<>();
		Map<String, Object> lrnPtn = new HashMap<>();
		
		Map<String, Object> lrnPtnData = (Map) commonMapper.get(paramMap, "HamsSales.selectLrnPtn"); 
		if(lrnPtnData != null) {
			lrnPtn.put("totalLrnSec", lrnPtnData.get("totalLrnSec"));
		}
		
		
		List<Map> timeline = new ArrayList<>();
		Map<String, Object> timelineMap = new HashMap<>();
		List<Map> timeLineMapData = commonMapper.getList(paramMap, "HamsSales.selectLrnPtnTmln");
		
		if(timeLineMapData.size() != 0) {
			for(int i = 0; i < timeLineMapData.size(); i++) {
				timeline.add(timeLineMapData.get(i));
			}
			
			lrnPtn.put("timeline", timeline);
			data.put("lrnPtn", lrnPtn);
		}
		
		if(vu.isValid()) {
			setResult(dataKey, data);
		}else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	
	@Override
	public Map getLrnTmln(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"dt", "studId"}, paramMap);
		//2.dt 날짜형 체크
		if(vu.isValid()) vu.isDate("dt", (String)paramMap.get("dt"));
		//3.id 숫자형 체크
		if(vu.isValid()) vu.isNumeric("studId", String.valueOf(paramMap.get("studId")));
		
		Map<String, Object> data = new HashMap<>();
		Map<String, Object> lrnTmln = new HashMap<>();
		
		List<Map> lrnTimelineList = commonMapper.getList(paramMap, "HamsSales.selectLrnTmln");
		
		if(lrnTimelineList.size() != 0) {
			data.put("lrnTmln", lrnTimelineList);
		}
		
		if(vu.isValid()) {
			setResult(dataKey, data);
		}else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	
	@Override
	public Map getLrnExStt(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"dt", "studId"}, paramMap);
		//2.dt 날짜형 체크
		if(vu.isValid()) vu.isDate("dt", (String)paramMap.get("dt"));
		//3.id 숫자형 체크
		if(vu.isValid()) vu.isNumeric("studId", String.valueOf(paramMap.get("studId")));
		
		Map<String, Object> data = new LinkedHashMap<>();
		Map<String, Object> lrnExCntTop3 = new LinkedHashMap<>();
		Map<String, Object> grpAvg = new LinkedHashMap<>();
		ArrayList<String> subjList = new ArrayList<String>();
		List<Integer> exCntList = new ArrayList<Integer>();
		
		Map<String, Object> lrnExSttData = (Map) commonMapper.get(paramMap, "HamsSales.selectLrnExStt"); 
		
		if(lrnExSttData != null) {
			if(lrnExSttData.get("lrnSubjCdSp") != null) {
				String[] subjData = lrnExSttData.get("lrnSubjCdSp").toString().split(","); 
				
				for(int i = 0; i < subjData.length; i++) {
					subjList.add(subjData[i]);
				}
				
			}else {
				subjList = null;
			}
			
			String[] exCntData = lrnExSttData.get("lrnExCntSp").toString().split(","); 
			
			if(Integer.valueOf(exCntData[0].toString()) != 0) {
				for(int i = 0; i < exCntData.length; i++) {
					exCntList.add(Integer.valueOf(exCntData[i]));
				}
			}else {
				exCntList = null;
			}
			
			lrnExCntTop3.put("subjCd", subjList);
			lrnExCntTop3.put("exCnt", exCntList);
			
			grpAvg.put("exCnt", lrnExSttData.get("totalLrnExCnt"));
			grpAvg.put("grpAvgExCnt", lrnExSttData.get("grpLrnExCnt"));
			
			data.put("lrnExCntTop3", lrnExCntTop3);
			data.put("grpAvg", grpAvg);
		}
		
		if(vu.isValid()) {
			setResult(dataKey, data);
		}else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	
	@Override
	public Map getLrnPlanStt(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"dt", "studId"}, paramMap);
		//2.dt 날짜형 체크
		if(vu.isValid()) vu.isDate("dt", (String)paramMap.get("dt"));
		//3.id 숫자형 체크
		if(vu.isValid()) vu.isNumeric("studId", String.valueOf(paramMap.get("studId")));
		
		Map<String, Object> data = new LinkedHashMap<>();
		Map<String, Object> lrnEx = new LinkedHashMap<>();
		Map<String, Object> grpAvgEx = new LinkedHashMap<>();
		
		Map<String, Object> lrnExSttData = (Map) commonMapper.get(paramMap, "HamsSales.selectLrnPlanStt"); 
		
		if(lrnExSttData != null) {
			lrnEx.put("exRt", lrnExSttData.get("exRt"));
			lrnEx.put("planCnt", lrnExSttData.get("planCnt"));
			lrnEx.put("exCnt", lrnExSttData.get("exCnt"));
			
			grpAvgEx.put("grpAvgExRt", lrnExSttData.get("grpAvgExRt"));
			grpAvgEx.put("grpAvgPlanCnt", lrnExSttData.get("grpAvgPlanCnt"));
			grpAvgEx.put("grpAvgExCnt", lrnExSttData.get("grpAvgExCnt"));
			
			data.put("lrnEx", lrnEx);
			data.put("grpAvgEx", grpAvgEx);
		}
		
		if(vu.isValid()) {
			setResult(dataKey, data);
		}else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	
	@Override
	public Map getALrnStt(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"dt", "studId"}, paramMap);
		//2.dt 날짜형 체크
		if(vu.isValid()) vu.isDate("dt", (String)paramMap.get("dt"));
		//3.id 숫자형 체크
		if(vu.isValid()) vu.isNumeric("studId", String.valueOf(paramMap.get("studId")));
		
		Map<String, Object> data = new LinkedHashMap<>();
		Map<String, Object> lrnExCntTop3 = new LinkedHashMap<>();
		Map<String, Object> grpAvg = new LinkedHashMap<>();
		
		List<String> subjCd = new ArrayList<String>();
		List<Integer> exCnt = new ArrayList<Integer>();
		
		Map<String, Object> aLrnExSttData = (Map) commonMapper.get(paramMap, "HamsSales.selectALrnStt"); 
		
		if(aLrnExSttData != null) {
			if(aLrnExSttData.get("aLrnSubjCdSp") != null) {
				String[] subjData = aLrnExSttData.get("aLrnSubjCdSp").toString().split(","); 
				
				for(int i = 0; i < subjData.length; i++) {
					subjCd.add(subjData[i]);
				}
			}else {
				subjCd = null;
			}
			
			String[] exCntData = aLrnExSttData.get("aLrnExCntSp").toString().split(","); 
			if(Integer.valueOf(exCntData[0].toString()) != 0 ) {
				for(int i = 0; i < exCntData.length; i++) {
					exCnt.add(Integer.valueOf(exCntData[i]));
				}
			}else {
				exCnt = null;
			}
			
			lrnExCntTop3.put("subjCd", subjCd);
			lrnExCntTop3.put("exCnt", exCnt);
			
			grpAvg.put("exCnt", aLrnExSttData.get("aLrnExCnt"));
			grpAvg.put("grpAvgExCnt", aLrnExSttData.get("grpLrnExCnt"));
			
			data.put("lrnExCntTop3", lrnExCntTop3);
			data.put("grpAvg", grpAvg);
		}
		
		if(vu.isValid()) {
			setResult(dataKey, data);
		}else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	
	@Override
	public Map getExam(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"dt", "studId"}, paramMap);
		//2.dt 날짜형 체크
		if(vu.isValid()) vu.isDate("dt", (String)paramMap.get("dt"));
		//3.id 숫자형 체크
		if(vu.isValid()) vu.isNumeric("studId", String.valueOf(paramMap.get("studId")));
		
		Map<String, Object> data = new HashMap<>();
		Map<String, Object> exam = new LinkedHashMap<>();
		
		Map<String, Object> examData = (Map) commonMapper.get(paramMap, "HamsSales.selectExam");
		
		if(examData != null) {
			
			exam.put("crtRt", examData.get("crtRt"));
			exam.put("explCnt", examData.get("explCnt"));
			exam.put("ansQuesCnt", examData.get("ansQuesCnt"));
			exam.put("crtQuesCnt", examData.get("crtQuesCnt"));
			
			List<String> subjCdList = new ArrayList<String>();
			
			String[] subjCdData = examData.get("subjCdSp").toString().split(","); 
			
			for(String item : subjCdData) {
				subjCdList.add(item);
			}
			
			exam.put("subjCdList", subjCdList);
			
			String[] crtRtData = examData.get("crtRtSp").toString().split(","); 
			
			List<Integer> crtRtList = new ArrayList<Integer>();
			
			for(String item : crtRtData) {
				if(Integer.valueOf(item) == 0) {
					crtRtList.add(null);
				}else {
					crtRtList.add(Integer.valueOf(item));
				}
			}
			
			exam.put("crtRtList", crtRtList);
			
			String[] explCntData = examData.get("explCntSp").toString().split(","); 
			
			List<Integer> explCntList = new ArrayList<Integer>();
			
			for(String item : explCntData) {
				if(Integer.valueOf(item) == 0) {
					explCntList.add(null);
				}else {
					explCntList.add(Integer.valueOf(item));
				}
			}
			
			exam.put("explCntList", explCntList);
			
			
			exam.put("incrtNoteNcCnt", examData.get("incrtNtNcCnt"));
			exam.put("imprvSlvHabitCnt", examData.get("imprvSlvHabitCnt"));
			exam.put("skipQuesCnt", examData.get("skipQuesCnt"));
			exam.put("guessQuesCnt", examData.get("guessQuesCnt"));
			exam.put("cursoryQuesCnt", examData.get("cursoryQuesCnt"));
			
			data.put("exam", exam);
		}
		
		if(vu.isValid()) {
			setResult(dataKey, data);
		}else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	
	@Override
	public Map getExpl(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"dt", "studId"}, paramMap);
		//2.dt 날짜형 체크
		if(vu.isValid()) vu.isDate("dt", (String)paramMap.get("dt"));
		//3.id 숫자형 체크
		if(vu.isValid()) vu.isNumeric("studId", String.valueOf(paramMap.get("studId")));
		
		if(paramMap.get("startIdx") == null || "".equals(paramMap.get("startIdx"))) {
			paramMap.put("idx", 0);
		}else {
			paramMap.put("idx", Integer.valueOf(paramMap.get("startIdx").toString()) - 1);
		}
		
		if(paramMap.get("type") != null && !"".equals(paramMap.get("type"))) {
			String[] typeList = paramMap.get("type").toString().split(",");
			paramMap.put("listSize", typeList.length);
			paramMap.put("examType", typeList);
		}
		
		Map<String, Object> data = new LinkedHashMap<>();
		List<Map> expl = new ArrayList<>();
		Map<String, Object> explMap = new LinkedHashMap<>();
		Map<String, Object> explMapData = new LinkedHashMap<>();
		
		List<Map<String,Object>> explData = (List) commonMapper.getList(paramMap, "HamsSales.selectExpl");
		
		if(explData.size() > 0) {
			
			Map<String, Object> explPageData = (Map) commonMapper.get(paramMap, "HamsSales.selectExplCnt");
			
			for(int i = 0; i < explData.size(); i++) {
				
				explMap.put("examCd", explData.get(i).get("examCd"));
				explMap.put("smtId", explData.get(i).get("smtId"));
				explMap.put("stuId", explData.get(i).get("stuId"));
				explMap.put("subjNm", explData.get(i).get("subjCd"));
				explMap.put("smtDttm", explData.get(i).get("smtDttm"));
				explMap.put("type", explData.get(i).get("examType"));
				explMap.put("examNm", explData.get(i).get("examNm"));
				explMap.put("round", explData.get(i).get("round"));
				explMap.put("crtRt", explData.get(i).get("crtRt"));
				explMap.put("crtQuesCnt", explData.get(i).get("crtQuesCnt"));
				explMap.put("quesCnt", explData.get(i).get("quesCnt"));
				
				List<Integer> crtQues = new ArrayList<Integer>();
				List<Integer> guessQues = new ArrayList<Integer>();
				List<Integer> skipQues = new ArrayList<Integer>();
				List<Integer> cursoryQues = new ArrayList<Integer>();
				List<Integer> incrtQues = new ArrayList<Integer>();
				
				if(explData.get(i).get("crtQuesSp") != null) {
					String[] crtQuesData = explData.get(i).get("crtQuesSp").toString().split(",");
					for(String item : crtQuesData) {
						crtQues.add(Integer.valueOf(item));
					}
				}else {
					crtQues = null;
				}
					
				if(explData.get(i).get("guessQuesSp") != null) {
					String[] guessCrtQuesData = explData.get(i).get("guessQuesSp").toString().split(",");
					for(String item : guessCrtQuesData) {
						guessQues.add(Integer.valueOf(item));
					}
				}else {
					guessQues = null;
				}
				
				if(explData.get(i).get("skipQuesSp") != null) {
					String[] skipQuesData = explData.get(i).get("skipQuesSp").toString().split(",");
					for(String item : skipQuesData) {
						skipQues.add(Integer.valueOf(item));
					}
				}else {
					skipQues = null;
				}
				
				if(explData.get(i).get("cursoryQuesSp") != null) {
					String[] cursoryQuesData = explData.get(i).get("cursoryQuesSp").toString().split(",");
					for(String item : cursoryQuesData) {
						cursoryQues.add(Integer.valueOf(item));
					}
				}else {
					cursoryQues = null;
				}
				
				if(explData.get(i).get("incrtQuesSp") != null) {
					String[] incrtQuesData = explData.get(i).get("incrtQuesSp").toString().split(",");
					for(String item : incrtQuesData) {
						incrtQues.add(Integer.valueOf(item));
					}
				}else {
					incrtQues = null;
				}
				
				explMap.put("crtQues", crtQues);
				explMap.put("guessQues", guessQues);
				explMap.put("skipQues", skipQues);
				explMap.put("cursoryQues", cursoryQues);
				explMap.put("incrtQues", incrtQues);
				
				expl.add(explMap);
			}
			explMapData.put("totalCnt", explPageData.get("totalCnt"));
			explMapData.put("list", expl);
			
			data.put("expl", explMapData);
		}
		
		if(vu.isValid()) {
			setResult(dataKey, data);
		}else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	
	@Override
	public Map getIncrtNote(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"dt", "studId"}, paramMap);
		//2.dt 날짜형 체크
		if(vu.isValid()) vu.isDate("dt", (String)paramMap.get("dt"));
		//3.id 숫자형 체크
		if(vu.isValid()) vu.isNumeric("studId", String.valueOf(paramMap.get("studId")));
		
		if(paramMap.get("startIdx") == null && "".equals(paramMap.get("startIdx"))) {
			paramMap.put("idx", 0);
		}else {
			paramMap.put("idx", Integer.valueOf(paramMap.get("startIdx").toString()) - 1);
		}
		
		Map<String, Object> data = new LinkedHashMap<>();
		List<Map> incrtNote = new ArrayList<>();
		Map<String, Object> incrtNoteMap = new LinkedHashMap<>();
		
		List<Map<String,Object>> incrtNoteData = (List) commonMapper.getList(paramMap, "HamsSales.selectIncrtNote");
		
		if(incrtNoteData.size() > 0) {
			
			Map<String, Object> IncrtNotePageData = (Map) commonMapper.get(paramMap, "HamsSales.selectIncrtNoteCnt");
			
			
			incrtNoteMap.put("totalCnt", IncrtNotePageData.get("totalCnt"));
			incrtNoteMap.put("list", incrtNoteData);
			data.put("incrtNote", incrtNoteMap);
		}
		
		if(vu.isValid()) {
			setResult(dataKey, data);
		}else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	
	@Override
	public Map getFeedback(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId"}, paramMap);
		//2.id 숫자형 체크
		if(vu.isValid()) vu.isNumeric("studId", String.valueOf(paramMap.get("studId")));
		
		Map<String, Object> data = new LinkedHashMap();
		Map<String, Object> apiMap = new HashMap<>();
		Map<String, Object> paramData = new HashMap<>();
		Map<String, Object> feedbackMsgMap = new HashMap<>();
		Map<String, Object> feedback = new LinkedHashMap();
		Map<String, Object> feedbackMap = (Map) commonMapper.get(paramMap, "HamsSales.selectFeedback");
		
		if(feedbackMap != null) {
			String recommendJob = null;
			
			//msgCd 수정 여부 체크
			boolean sttMsgCheck = Pattern.matches("^E01[.][0-9]{6}", feedbackMap.get("sttMsg").toString());
			
			if(!sttMsgCheck) {
				if(feedbackMap.get("recommendJob") == null) {
					paramData.put("stuId", paramMap.get("studId"));
					paramData.put("apiName", "intel-inspecion-strength");
					
					//apiMap =  (Map<String, Object>) externalAPIservice.callExternalAPI(paramData).get("data");
					
					String intelNm = null;
					
					if(apiMap != null) {
						String intelStrength = "자연친화";
						String maxSubjCd = null;
						if(feedbackMap.get("maxSubjCd") != null) {
							
							if("N02".equals(feedbackMap.get("maxSubjCd").toString())) {
								maxSubjCd = "E01";
							}else {
								maxSubjCd = feedbackMap.get("maxSubjCd").toString();
							}
							
							switch (intelStrength) {
							case "언어":
								intelNm = "RJ01" + maxSubjCd;
								break;
							case "논리수학":
								intelNm = "RJ02" + maxSubjCd;
								break;
							case "공간":
								intelNm = "RJ03" + maxSubjCd;
								break;
							case "신체운동":
								intelNm = "RJ04" + maxSubjCd;
								break;
							case "대인":
								intelNm = "RJ05" + maxSubjCd;
								break;
							case "음악":
								intelNm = "RJ06" + maxSubjCd;
								break;
							case "개인내적":
								intelNm = "RJ07" + maxSubjCd;
								break;
							case "자연친화":
								intelNm = "RJ08" + maxSubjCd;
								break;
							default:
								intelNm = "RJ09" + maxSubjCd;
								break;
							}
						}else {
							intelNm = "RJ01E02";
						}
					}else {
						intelNm = "RJ01E02";
					}
					
					paramData.clear();
					paramData.put("msgCd", intelNm);
					paramData.put("grp", "RECOMMNAD_JOB");
					
					Map<String, Object> recommandJobData = (Map) commonMapper.get(paramData, "HamsSales.selectFeedbackCd");
					
					recommendJob = recommandJobData.get("cdNm").toString();
					
					paramData.clear();
					paramData.put("studId", paramMap.get("studId"));
					paramData.put("recommendJob", recommendJob);
					commonMapper.update(paramData, "updateFeedbackRecommendJob");
				}else {
					recommendJob = feedbackMap.get("recommendJob").toString();
				}
				
				List<String> feedbackMsg = new ArrayList<>();
				
				String sttMsgCd = feedbackMap.get("sttMsg").toString();
				String positiveMsgCd = feedbackMap.get("positiveMsg").toString();
				String negativeMsgCd = feedbackMap.get("negativeMsg").toString();
				
				if(sttMsgCd.indexOf("|") > 0) {
					feedbackMsg.add(sttMsgCd.substring(0, sttMsgCd.indexOf("|")));
				}else {
					feedbackMsg.add(sttMsgCd);
				}
				
				if(positiveMsgCd.indexOf("|") > 0) {
					feedbackMsg.add(positiveMsgCd.substring(0, positiveMsgCd.indexOf("|")));
				}else {
					feedbackMsg.add(positiveMsgCd);
				}
				
				if(negativeMsgCd.indexOf("|") > 0) {
					feedbackMsg.add(negativeMsgCd.substring(0, negativeMsgCd.indexOf("|")));
				}else {
					feedbackMsg.add(negativeMsgCd);
				}
				
				feedbackMsgMap.put("msgCd", feedbackMsg);
				feedbackMsgMap.put("cdType", "E");
				List<Map<String,Object>> feedbackMsgList = commonMapper.getList(feedbackMsgMap, "HamsSales.selectThreeDayLrnMsg");
				
				feedbackMsg.clear();
				
				for(int i = 0; i < feedbackMsgList.size(); i++) {
					String msg = feedbackMsgList.get(i).get("cdNm").toString(); 
					if(msg.contains("{1}")) {
						if(msg.contains("{2}")) { 
							msg = msg.replace("{1}", recommendJob);
							msg = msg.replace("{2}", feedbackMap.get("studNm").toString());
						}
						else {
							String subjNm = null;
							paramData.clear();
							
							if(i == 1) {
								if(positiveMsgCd.indexOf("C") > 0 || positiveMsgCd.indexOf("N") > 0) {
									paramData.put("msgCd", positiveMsgCd.substring(positiveMsgCd.indexOf("|")+1));
									paramData.put("grp", "SUBJ");
									
									Map<String, Object> subjData = (Map) commonMapper.get(paramData, "HamsSales.selectFeedbackCd");
									
									subjNm = subjData.get("cdNm").toString();
									
									msg = msg.replace("{1}", subjNm);
								}else {
									msg = msg.replace("{1}", positiveMsgCd.substring(positiveMsgCd.indexOf("|")+1));
								}
							}else {
								if(negativeMsgCd.indexOf("C") > 0 || negativeMsgCd.indexOf("N") > 0) {
									paramData.put("msgCd", negativeMsgCd.substring(negativeMsgCd.indexOf("|")+1));
									paramData.put("grp", "SUBJ");
									Map<String, Object> subjData = (Map) commonMapper.get(paramData, "HamsSales.selectFeedbackCd");
									subjNm = subjData.get("cdNm").toString();
									
									msg = msg.replace("{1}", subjNm);
								}else {
									msg = msg.replace("{1}", negativeMsgCd.substring(negativeMsgCd.indexOf("|")+1));
								}
							}
						}
					}
					
					feedbackMsg.add(msg);
				}
				
				feedback.put("recommendJob", recommendJob);
				feedback.put("studNm", feedbackMap.get("studNm"));
				feedback.put("expDt", feedbackMap.get("expDt"));
				feedback.put("guideMsg", feedbackMap.get("guideMsg"));
				feedback.put("sttMsg", feedbackMsg.get(0));
				feedback.put("positiveMsg", feedbackMsg.get(1));
				feedback.put("negativeMsg", feedbackMsg.get(2));
				feedback.put("expTchrChaNm", feedbackMap.get("expTchrChaNm"));
				feedback.put("expTchrChaCell", feedbackMap.get("expTchrChaCell"));
			}else {
				feedback.put("recommendJob", recommendJob);
				feedback.put("studNm", feedbackMap.get("studNm"));
				feedback.put("expDt", feedbackMap.get("expDt"));
				feedback.put("guideMsg", feedbackMap.get("guideMsg"));
				feedback.put("sttMsg", feedbackMap.get("sttMsg"));
				feedback.put("positiveMsg", feedbackMap.get("positiveMsg"));
				feedback.put("negativeMsg", feedbackMap.get("negativeMsg"));
				feedback.put("expTchrChaNm", feedbackMap.get("expTchrChaNm"));
				feedback.put("expTchrChaCell", feedbackMap.get("expTchrChaCell"));
			}
			
			data.put("feedback", feedback);
		}
		
		if(vu.isValid()) {
			setResult(dataKey, data);
		}else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	
	@Override
	public Map getSubjLrnPtn(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId"}, paramMap);
		//2.id 숫자형 체크
		if(vu.isValid()) vu.isNumeric("studId", String.valueOf(paramMap.get("studId")));
		
		Map<String, Object> data = new LinkedHashMap();
		Map<String, Object> subjLrnPtn = new LinkedHashMap();
		List<Map<String,Object>> subjLrnPtnResultList = (List) commonMapper.getList(paramMap, "HamsSales.selectSubjLrnPtn");
		List<Map<String,Object>> subjLrnPtnList = new ArrayList<>();
		
		if(subjLrnPtnResultList.size() > 0) {
			for (Map<String,Object> item : subjLrnPtnResultList) {
				Map<String, Object> subjLrnPtnListMap = new LinkedHashMap();
				subjLrnPtnListMap.put("subjCd", item.get("subjCd"));
				subjLrnPtnListMap.put("totalLrnSec", item.get("totalLrnSec"));
				
				String[] subSubjCd = item.get("subSubjCdSp").toString().split(",");
				String[] subSubjLrnSec = item.get("subSubjLrnSecSp").toString().split(",");
				String[] subSubjExCnt = item.get("subSubjLrnExCntSp").toString().split(",");
				
				subjLrnPtnListMap.put("subSubjCd", subSubjCd);
				subjLrnPtnListMap.put("subSubjLrnSec", subSubjLrnSec);
				subjLrnPtnListMap.put("subSubjExCnt", subSubjExCnt);
				
				subjLrnPtnList.add(subjLrnPtnListMap);
			}
			
			data.put("subjLrnPtn", subjLrnPtnList);
		}
		
		if(vu.isValid()) {
			setResult(dataKey, data);
		}else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	
	@Override
	public Map getLrnHabits(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId"}, paramMap);
		//2.id 숫자형 체크
		if(vu.isValid()) vu.isNumeric("studId", String.valueOf(paramMap.get("studId")));
		
		Map<String, Object> data = new LinkedHashMap<>();
		Map<String, Object> lrnHabits = new LinkedHashMap<>();
		Map<String, Object> attHabit = new LinkedHashMap<>();
		Map<String, Object> planHabit = new LinkedHashMap<>();
		Map<String, Object> incrtNoteHabit = new LinkedHashMap<>();
		Map<String, Object> aLrnHabit = new LinkedHashMap<>();
		Map<String, Object> concnHabit = new LinkedHashMap<>();
		Map<String, Object> slvHabit = new LinkedHashMap<>();
		
		Map<String, Object> lrnHabitsResult = (Map) commonMapper.get(paramMap, "HamsSales.selectLrnHabitStt");
		
		if(lrnHabitsResult != null) {
			attHabit.put("score", lrnHabitsResult.get("attHabitScore"));
			attHabit.put("attRt", lrnHabitsResult.get("attRt"));
			attHabit.put("planDayCnt", lrnHabitsResult.get("planDayCnt"));
			attHabit.put("attDayCnt", lrnHabitsResult.get("attDayCnt"));
			
			String[] dtList = lrnHabitsResult.get("dtSp").toString().split(",");
			String[] planDtList = lrnHabitsResult.get("planDtSp").toString().split(",");
			String[] attDtList = lrnHabitsResult.get("attDtSp").toString().split(",");
			
			attHabit.put("dtList", dtList);
			attHabit.put("planDtList", planDtList);
			attHabit.put("attDtList", attDtList);
			
			planHabit.put("score", lrnHabitsResult.get("planHabitScore"));
			planHabit.put("exRt", lrnHabitsResult.get("exRt"));
			planHabit.put("planCnt", lrnHabitsResult.get("planCnt"));
			planHabit.put("exCnt", lrnHabitsResult.get("exCnt"));
			planHabit.put("bLrnExCnt", lrnHabitsResult.get("bLrnExCnt"));
			planHabit.put("lrnExCnt", lrnHabitsResult.get("lrnExCnt"));
			planHabit.put("dLrnExCnt", lrnHabitsResult.get("dLrnExCnt"));
			planHabit.put("uLrnExCnt", lrnHabitsResult.get("uLrnExCnt"));
			
			incrtNoteHabit.put("score", lrnHabitsResult.get("incrtNtHabitScore"));
			incrtNoteHabit.put("incrtNtNcCnt", lrnHabitsResult.get("incrtNtNcCnt"));
		
			List<Map<String,Object>> incrtNoteHabitList = (List) commonMapper.getList(paramMap, "HamsSales.selectExamRstIncrtNTLog");
			
			if(incrtNoteHabitList.size() > 0) {
				incrtNoteHabit.put("list", incrtNoteHabitList);
			} else {
				incrtNoteHabit.put("list", new ArrayList<>());
			}
			
			aLrnHabit.put("score", lrnHabitsResult.get("aLrnHabitScore"));
			aLrnHabit.put("aLrnExCnt", lrnHabitsResult.get("aLrnExCnt"));
			
			String[] subjCd = lrnHabitsResult.get("subjCdSp").toString().split(",");
			int[] subjExCnt = Arrays.asList(lrnHabitsResult.get("subjExCntSp").toString().split(",")).stream().mapToInt(Integer::parseInt).toArray();
			
			aLrnHabit.put("subjCd", subjCd);		
			aLrnHabit.put("subjExCnt", subjExCnt);		
			
			concnHabit.put("score", lrnHabitsResult.get("concnHabitScore"));
			concnHabit.put("lowConcnCnt", lrnHabitsResult.get("lowConcnCnt"));
			
			List<Map<String,Object>> concnHabitTmlnList = (List) commonMapper.getList(paramMap, "HamsSales.selectConcnHabitTmln");
			
			if(concnHabitTmlnList.size() > 0) {
				concnHabit.put("list", concnHabitTmlnList);
			} else {
				concnHabit.put("list", new ArrayList<>());
			}
			slvHabit.put("score", lrnHabitsResult.get("slvHabitScore"));
			slvHabit.put("imprvSlvHabitCnt", lrnHabitsResult.get("imprvSlvHabitCnt"));
			slvHabit.put("skipQuesCnt", lrnHabitsResult.get("skipQuesCnt"));
			slvHabit.put("guessQuesCnt", lrnHabitsResult.get("guessQuesCnt"));
			slvHabit.put("cursoryQuesCnt", lrnHabitsResult.get("cursoryQuesCnt"));
			
			lrnHabits.put("attHabit", attHabit);
			lrnHabits.put("planHabit", planHabit);
			lrnHabits.put("incrtNoteHabit", incrtNoteHabit);
			lrnHabits.put("aLrnHabit", aLrnHabit);
			lrnHabits.put("concnHabit", concnHabit);
			lrnHabits.put("slvHabit", slvHabit);
			
			data.put("lrnHabits", lrnHabits);
		}
		
		if(vu.isValid()) {
			setResult(dataKey, data);
		}else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	
	@Override
	public Map getExamStt(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId"}, paramMap);
		//2.id 숫자형 체크
		if(vu.isValid()) vu.isNumeric("studId", String.valueOf(paramMap.get("studId")));
		
		Map<String, Object> data = new HashMap<>();
		
		Map<String, Object> examStt = new LinkedHashMap<>();
		Map<String, Object> examRst = new LinkedHashMap<>();
		Map<String, Object> maxSubj = new LinkedHashMap<>();
		Map<String, Object> minSubj = new LinkedHashMap<>();
		
		Map<String, Object> examRstResult = (Map) commonMapper.get(paramMap, "HamsSales.selectExamStt");
		
		if(examRstResult != null) {
			examRst.put("crtRt", examRstResult.get("crtRt"));
			examRst.put("top10AvgScore", examRstResult.get("top10AvgScore"));
			examRst.put("explCnt", examRstResult.get("explCnt"));
			examRst.put("ansQuesCnt", examRstResult.get("ansQuesCnt"));
			examRst.put("crtQuesCnt", examRstResult.get("crtQuesCnt"));
			
			maxSubj.put("maxSubjNm", examRstResult.get("maxSubjNm"));
			maxSubj.put("maxSubjCrtRt", examRstResult.get("maxSubjCrtRt"));
			maxSubj.put("maxSubjTop10AvgScore", examRstResult.get("maxSubjTop10AvgScore"));
			maxSubj.put("maxSubjExplCnt", examRstResult.get("maxSubjExplCnt"));
			maxSubj.put("maxSubjAnsQuesCnt", examRstResult.get("maxSubjAnsQuesCnt"));
			maxSubj.put("maxSubjCrtQuesCnt", examRstResult.get("maxSubjCrtQuesCnt"));
			
			String[] maxSubjActFieldNmSpList = examRstResult.get("maxSubjActFieldNmSp").toString().split(","); 
			String[] maxSubjActFieldCrtRtSpList = examRstResult.get("maxSubjActFieldCrtRtSp").toString().split(","); 
			String[] maxSubjContFieldNmSpList = examRstResult.get("maxSubjContFieldNmSp").toString().split(","); 
			String[] maxSubjContFieldCrtRtSpList = examRstResult.get("maxSubjContFieldCrtRtSp").toString().split(","); 
					
			maxSubj.put("maxSubjActFieldNmSp", maxSubjActFieldNmSpList);
			maxSubj.put("maxSubjActFieldCrtRtSp", maxSubjActFieldCrtRtSpList);
			maxSubj.put("maxSubjContFieldNmSp", maxSubjContFieldNmSpList);
			maxSubj.put("maxSubjContFieldCrtRtSp", maxSubjContFieldCrtRtSpList);
			
			minSubj.put("minSubjNm", examRstResult.get("minSubjNm"));
			minSubj.put("minSubjCrtRt", examRstResult.get("minSubjCrtRt"));
			minSubj.put("minSubjTop10AvgScore", examRstResult.get("minSubjTop10AvgScore"));
			minSubj.put("minSubjExplCnt", examRstResult.get("minSubjExplCnt"));
			minSubj.put("minSubjAnsQuesCnt", examRstResult.get("minSubjAnsQuesCnt"));
			minSubj.put("minSubjCrtQuesCnt", examRstResult.get("minSubjCrtQuesCnt"));
			
			String[] minSubjActFieldNmSpList = examRstResult.get("minSubjActFieldNmSp").toString().split(","); 
			String[] minSubjActFieldCrtRtSpList = examRstResult.get("minSubjActFieldCrtRtSp").toString().split(","); 
			String[] minSubjContFieldNmSpList = examRstResult.get("minSubjContFieldNmSp").toString().split(","); 
			String[] minSubjContFieldCrtRtSpList = examRstResult.get("minSubjContFieldCrtRtSp").toString().split(",");
			
			minSubj.put("minSubjActFieldNmSp", minSubjActFieldNmSpList);
			minSubj.put("minSubjActFieldCrtRtSp", minSubjActFieldCrtRtSpList);
			minSubj.put("minSubjContFieldNmSp", minSubjContFieldNmSpList);
			minSubj.put("minSubjContFieldCrtRtSp", minSubjContFieldCrtRtSpList);
		
			examStt.put("examRst", examRst);
			examStt.put("maxSubj", maxSubj);
			examStt.put("minSubj", minSubj);
			
			data.put("examStt", examStt);
		}
		
		if(vu.isValid()) {
			setResult(dataKey, data);
		}else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	
	@Override
	public Map getRecommendedContents(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId"}, paramMap);
		//2.id 숫자형 체크
		if(vu.isValid()) vu.isNumeric("studId", String.valueOf(paramMap.get("studId")));
		
		Map<String, Object> data = new HashMap<>();
		List<Map> recommendedContents = commonMapper.getList(paramMap, "HamsSales.selectRecommendedContents");
		
		if(recommendedContents.size() > 0) {
			data.put("recommendedContents", recommendedContents);
		}
		
		if(vu.isValid()) {
			setResult(dataKey, data);
		}else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	
	@Override
	public Map updateFeedback(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId"}, paramMap);
		//2.id 숫자형 체크
		if(vu.isValid()) vu.isNumeric("studId", String.valueOf(paramMap.get("studId")));
		
		if(vu.isValid()) {
			setResult(dataKey, commonMapper.update(paramMap, "updateFeedback"));
		}else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
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
}
