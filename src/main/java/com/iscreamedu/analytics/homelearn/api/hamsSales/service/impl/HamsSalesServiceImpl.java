package com.iscreamedu.analytics.homelearn.api.hamsSales.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iscreamedu.analytics.homelearn.api.common.mapper.CommonMapper;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationCode;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtil;
import com.iscreamedu.analytics.homelearn.api.hamsSales.service.HamsSalesService;

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
		vu.checkRequired(new String[] {"dt", "studId"}, paramMap);
		//2.dt 날짜형 체크
		if(vu.isValid()) vu.isDate("dt", (String)paramMap.get("dt"));
		//3.id 숫자형 체크
		if(vu.isValid()) vu.isNumeric("studId", String.valueOf(paramMap.get("studId")));
		
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
				if(i%2 == 0) {
					dailyLrnSttMap.put("attYn", false); //HL API output으로 교체 필요
					dailyLrnSttMap.put("lrnStt", 3); //HL API output으로 교체 필요				
				} else {
					dailyLrnSttMap.put("attYn", true); //HL API output으로 교체 필요
					dailyLrnSttMap.put("lrnStt", new Random().nextInt(2)+1); //HL API output으로 교체 필요
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
		
		Map<String, Object> data = new HashMap<>();
		Map<String, Object> threeDayLrn = new HashMap<>();
		Map<String, Object> consultingMsg = new HashMap<>();
		threeDayLrn.put("complimentCnt", 5);
		
		List<String> complimentList = new ArrayList<>();
		complimentList.add("3일 연속 수행률이 100%에요");
		complimentList.add("홈런도서관 읽은 책 1권");
		complimentList.add("최대 몇 줄까지 표시될까요?");
		complimentList.add("다섯 줄입니다");
		complimentList.add("포인트는 최대 5개까지 노출됩니다");
		
		threeDayLrn.put("complimentList", complimentList);
		
		threeDayLrn.put("prescriptionCnt", 5);
		
		List<String> prescriptionList = new ArrayList<>();
		prescriptionList.add("스스로 학습 수행 0개");
		prescriptionList.add("타학년 학습 수행 4개");
		prescriptionList.add("오답노트 미완료 6개");
		prescriptionList.add("포인트는 최대 5개까지 노출됩니다");
		prescriptionList.add("포인트는 최대 5개까지 노출됩니다");
		
		threeDayLrn.put("prescriptionList", prescriptionList);
		
		consultingMsg.put("lrnExCnt", 20);
		consultingMsg.put("msg", "3일 동안 총 학습한 개수가 20개 입니다.");
		
		data.put("threeDayLrn", threeDayLrn);
		data.put("consultingMsg", consultingMsg);
		
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
	public Map getHLBookCafe(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"dt", "studId"}, paramMap);
		//2.dt 날짜형 체크
		if(vu.isValid()) vu.isDate("dt", (String)paramMap.get("dt"));
		//3.id 숫자형 체크
		if(vu.isValid()) vu.isNumeric("studId", String.valueOf(paramMap.get("studId")));
		
		Map<String, Object> data = new HashMap<>();
		Map<String, Object> hLBookCafe = new HashMap<>();
		List<Map> list = new ArrayList<>();
		Map<String, Object> listMap = new HashMap<>();
		
		hLBookCafe.put("cnt", 2);
		hLBookCafe.put("grpAvgCnt", 3);
		
		listMap.put("dt", "2020-09-25");
		listMap.put("title", "산에 산에 누가 살까");
		listMap.put("readTm", "00:00:43");
		listMap.put("reviewYn", true);
		listMap.put("review", "200자평 내용 텍스트");
		
		list.add(listMap);
		
		hLBookCafe.put("list", list);
		
		data.put("hLBookCafe", hLBookCafe);
		
		if(vu.isValid()) {
			setResult(dataKey, data);
		}else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	
	@Override
	public Map getEngLibrary(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"dt", "studId"}, paramMap);
		//2.dt 날짜형 체크
		if(vu.isValid()) vu.isDate("dt", (String)paramMap.get("dt"));
		//3.id 숫자형 체크
		if(vu.isValid()) vu.isNumeric("studId", String.valueOf(paramMap.get("studId")));
		
		Map<String, Object> data = new HashMap<>();
		Map<String, Object> engLibrary = new HashMap<>();
		List<Map> list = new ArrayList<>();
		Map<String, Object> listMap = new HashMap<>();
		
		engLibrary.put("cnt", 2);
		engLibrary.put("grpAvgCnt", 3);
		
		listMap.put("dt", "2020-04-08");
		listMap.put("title", "Who Can Go In?");
		listMap.put("readTm", "00:00:43");
		listMap.put("lvl", 5);
		listMap.put("quiz", "4/4");
		
		list.add(listMap);
		engLibrary.put("list", list);
		
		data.put("engLibrary", engLibrary);
		
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
		
		paramMap.put("idx", Integer.valueOf(paramMap.get("startIdx").toString()) - 1);
		
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
		
		paramMap.put("idx", Integer.valueOf(paramMap.get("startIdx").toString()) - 1);
		
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
	public Map getMultipleIntelligenceTest(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"dt", "studId"}, paramMap);
		//2.dt 날짜형 체크
		if(vu.isValid()) vu.isDate("dt", (String)paramMap.get("dt"));
		//3.id 숫자형 체크
		if(vu.isValid()) vu.isNumeric("studId", String.valueOf(paramMap.get("studId")));
		
		Map<String, Object> data = new HashMap<>();
		Map<String, Object> multipleIntelligenceTest = new HashMap<>();
		
		multipleIntelligenceTest.put("examDt", "2020-09-21");
		multipleIntelligenceTest.put("examRound", "4회차");
		multipleIntelligenceTest.put("examRegDt", "2020-09-21");
		multipleIntelligenceTest.put("examStt", "검사완료");
		
		data.put("multipleIntelligenceTest", multipleIntelligenceTest);
		
		if(vu.isValid()) {
			setResult(dataKey, data);
		}else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	
	@Override
	public Map getParentingTest(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"dt", "studId"}, paramMap);
		//2.dt 날짜형 체크
		if(vu.isValid()) vu.isDate("dt", (String)paramMap.get("dt"));
		//3.id 숫자형 체크
		if(vu.isValid()) vu.isNumeric("studId", String.valueOf(paramMap.get("studId")));
		
		Map<String, Object> data = new HashMap<>();
		Map<String, Object> multipleIntelligenceTest = new HashMap<>();
		
		multipleIntelligenceTest.put("examDt", "2020-09-21");
		multipleIntelligenceTest.put("examRound", "4회차");
		multipleIntelligenceTest.put("examRegDt", "2020-09-21");
		multipleIntelligenceTest.put("examStt", "검사완료");
		
		data.put("multipleIntelligenceTest", multipleIntelligenceTest);
		
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
		
		Map<String, Object> data = new HashMap<>();
		Map<String, Object> feedback = new HashMap<>();
		
		feedback.put("title", "짜파게티 요리사");
		feedback.put("studNm", "양예슬");
		feedback.put("expDt", "2020.04.06~2020.04.16");
		feedback.put("linkUrl", "http://gw.i-screamedu.co.kr/");
		feedback.put("msg", "안녕하세요...");
		feedback.put("tchrNm", "양예슬");
		feedback.put("tchrCell", "010-1234-1234");
		
		data.put("feedback", feedback);
		
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
		
		Map<String, Object> data = new HashMap<>();
		List<Map> subjLrnPtn = new ArrayList<>();
		Map<String, Object> subjLrnPtnMap = new HashMap<>();
		List<String> subSubjCd = new ArrayList<>();
		List<Integer> subSubjLrnSec = new ArrayList<>();
		List<Integer> subSubjExCnt = new ArrayList<>();
		
		subjLrnPtnMap.put("subjCd", "C01");
		subjLrnPtnMap.put("totalLrnSec", 248);
		
		subSubjCd.add("C01_01");
		subSubjCd.add("C01_02");
		subSubjCd.add("C01_03");
		subSubjCd.add("C01_04");
		
		subSubjLrnSec.add(88);
		subSubjLrnSec.add(90);
		subSubjLrnSec.add(70);
		subSubjLrnSec.add(82);
		
		subSubjExCnt.add(3);
		subSubjExCnt.add(4);
		subSubjExCnt.add(3);
		subSubjExCnt.add(1);
		
		subjLrnPtnMap.put("subSubjCd", subSubjCd);
		subjLrnPtnMap.put("subSubjLrnSec", subSubjLrnSec);
		subjLrnPtnMap.put("subSubjExCnt", subSubjExCnt);
		
		subjLrnPtn.add(subjLrnPtnMap);
		
		data.put("subjLrnPtn", subjLrnPtn);
		
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
		List<Map> recommendedContents = new ArrayList<Map>();
		Map<String, Object> recommendedContentsMap1 = new HashMap<>();
		Map<String, Object> recommendedContentsMap2 = new HashMap<>();
		Map<String, Object> recommendedContentsMap3 = new HashMap<>();
		Map<String, Object> recommendedContentsMap4 = new HashMap<>();
		
		recommendedContentsMap1.put("type", "학교공부");
		recommendedContentsMap1.put("subjNm", "수학완성");
		recommendedContentsMap1.put("ctgr", "홈런연산 테마파크 > 곱셉나눗셈 어드벤처");
		recommendedContentsMap1.put("cont", "나만의 맞춤 시험지를 놀이공원에서 놀듯이 풀며 연산 실력을 쑥쑥 늘려갑니다!");
		recommendedContentsMap1.put("thum", "https://xcdn.home-learn.com/data/thumbnail_img/2013/12/03/2131203j362178.jpg");
		
		recommendedContentsMap2.put("type", "학교공부");
		recommendedContentsMap2.put("subjNm", "수학완성");
		recommendedContentsMap2.put("ctgr", "홈런연산 테마파크 > 곱셉나눗셈 어드벤처");
		recommendedContentsMap2.put("cont", "나만의 맞춤 시험지를 놀이공원에서 놀듯이 풀며 연산 실력을 쑥쑥 늘려갑니다!");
		recommendedContentsMap2.put("thum", "https://xcdn.home-learn.com/data/thumbnail_img/2013/12/03/2131203j362178.jpg");
		
		recommendedContentsMap3.put("type", "특별학습");
		recommendedContentsMap3.put("subjNm", "글로벌리더십");
		recommendedContentsMap3.put("ctgr", "진로 > 미래 직업 속으로");
		recommendedContentsMap3.put("cont", "초콜릿의 연금술사 쇼콜라티에");
		recommendedContentsMap3.put("thum", "https://xcdn.home-learn.com/data/thumbnail_img/2013/12/03/2131203j362178.jpg");
		
		recommendedContentsMap4.put("type", "특별학습");
		recommendedContentsMap4.put("subjNm", "글로벌리더십");
		recommendedContentsMap4.put("ctgr", "과학 > 위기탈출 넘버원");
		recommendedContentsMap4.put("cont", "올바른 안전벨트 착용법");
		recommendedContentsMap4.put("thum", "https://xcdn.home-learn.com/data/thumbnail_img/2013/12/03/2131203j362178.jpg");
		
		recommendedContents.add(recommendedContentsMap1);
		recommendedContents.add(recommendedContentsMap2);
		recommendedContents.add(recommendedContentsMap3);
		recommendedContents.add(recommendedContentsMap4);
		
		data.put("recommendedContents", recommendedContents);
		
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
		
		setResult(dataKey, commonMapper.update(paramMap, "updateFeedback"));
		
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
