package com.iscreamedu.analytics.homelearn.api.hamsSales.service.impl;

import java.util.ArrayList;
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
		
		for (Map<String,Object> item : subjCodeInfoList) {
			subjCodeInfo.add(item);
		}
		
		data.put("subjCodeInfo", subjCodeInfo);
		
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
		
		data.put("studInfo", studInfoList);
		
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
		
		data.put("lrnTmln", lrnTmln);
		
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
		
		Map<String, Object> data = new HashMap<>();
		Map<String, Object> lrnExCntTop3 = new HashMap<>();
		Map<String, Object> grpAvg = new HashMap<>();
		List<String> subjList = new ArrayList<String>();
		List<Integer> exCntList = new ArrayList<Integer>();
		
		subjList.add("C01");
		subjList.add("C02");
		subjList.add("C03");
		
		exCntList.add(7);
		exCntList.add(6);
		exCntList.add(5);
		
		lrnExCntTop3.put("subjCd", subjList);
		lrnExCntTop3.put("exCnt", exCntList);
		
		grpAvg.put("exCnt", 12);
		grpAvg.put("grpAvgExCnt", 15);
		
		data.put("lrnExCntTop3", lrnExCntTop3);
		data.put("grpAvg", grpAvg);
		
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
		
		Map<String, Object> data = new HashMap<>();
		Map<String, Object> lrnEx = new HashMap<>();
		Map<String, Object> grpAvgEx = new HashMap<>();
		
		lrnEx.put("exRt", 95);
		lrnEx.put("planCnt", 12);
		lrnEx.put("exCnt", 10);
		
		grpAvgEx.put("grpAvgExRt", 95);
		grpAvgEx.put("grpAvgPlanCnt", 15);
		grpAvgEx.put("grpAvgExCnt", 12);
		
		data.put("lrnEx", lrnEx);
		data.put("grpAvgEx", grpAvgEx);
		
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
		
		Map<String, Object> data = new HashMap<>();
		Map<String, Object> lrnExCntTop3 = new HashMap<>();
		Map<String, Object> grpAvg = new HashMap<>();
		
		List<String> subjCd = new ArrayList<String>();
		List<Integer> exCnt = new ArrayList<Integer>();
		subjCd.add("C01");
		subjCd.add("C02");
		subjCd.add("C03");
		
		exCnt.add(7);
		exCnt.add(6);
		exCnt.add(5);
		
		lrnExCntTop3.put("subjCd", subjCd);
		lrnExCntTop3.put("exCnt", exCnt);
		
		grpAvg.put("exCnt", 12);
		grpAvg.put("grpAvgExCnt", 15);
		
		data.put("lrnExCntTop3", lrnExCntTop3);
		data.put("grpAvg", grpAvg);
		
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
		Map<String, Object> exam = new HashMap<>();
		
		exam.put("crtRt", 84);
		exam.put("explCnt", 33);
		exam.put("ansQuesCnt", 21);
		exam.put("crtQuesCnt", 19);
		
		List<String> subjCdList = new ArrayList<String>();
		subjCdList.add("C01");
		subjCdList.add("C02");
		subjCdList.add("C03");
		subjCdList.add("C04");
		subjCdList.add("C05");
		subjCdList.add("C06");
		exam.put("subjCdList", subjCdList);
		
		List<Integer> crtRtList = new ArrayList<Integer>();
		crtRtList.add(98);
		crtRtList.add(75);
		crtRtList.add(84);
		crtRtList.add(82);
		crtRtList.add(null);
		crtRtList.add(null);
		exam.put("crtRtList", crtRtList);
		
		List<Integer> explCntList = new ArrayList<Integer>();
		explCntList.add(8);
		explCntList.add(10);
		explCntList.add(8);
		explCntList.add(7);
		explCntList.add(null);
		explCntList.add(null);
		exam.put("explCntList", explCntList);
		
		exam.put("incrtNoteNcCnt", 11);
		exam.put("imprvSlvHabitCnt", 40);
		exam.put("skipQuesCnt", 10);
		exam.put("guessQuesCnt", 20);
		exam.put("cursoryQuesCnt", 10);
		
		data.put("exam", exam);
		
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
		
		Map<String, Object> data = new HashMap<>();
		List<Map> expl = new ArrayList<>();
		Map<String, Object> explMap = new HashMap<>();
		
		explMap.put("subjNm", "사회");
		explMap.put("smtDttm", "2020-05-29 10:33:38");
		explMap.put("type", "실력평가");
		explMap.put("examNm", "[3-1] 22장. 교통수단의 발달로 달라질 미래의 생활 모습 예상하기");
		explMap.put("round", "1차");
		explMap.put("crtRt", 80);
		explMap.put("crtQuesCnt", 4);
		explMap.put("quesCnt", 5);
		
		List<Integer> crtQues = new ArrayList<Integer>();
		List<Integer> guessQues = new ArrayList<Integer>();
		List<Integer> skipQues = new ArrayList<Integer>();
		List<Integer> cursoryQues = new ArrayList<Integer>();
		List<Integer> incrtQues = new ArrayList<Integer>();
		
		crtQues.add(1);
		crtQues.add(3);
		crtQues.add(5);
		
		guessQues.add(4);
		
		explMap.put("crtQues", crtQues);
		explMap.put("guessQues", guessQues);
		explMap.put("skipQues", skipQues);
		explMap.put("cursoryQues", cursoryQues);
		explMap.put("incrtQues", incrtQues);
		
		expl.add(explMap);
		
		data.put("expl", expl);
		
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
		
		Map<String, Object> data = new HashMap<>();
		List<Map> incrtNote = new ArrayList<>();
		Map<String, Object> incrtNoteMap = new HashMap<>();
		
		incrtNoteMap.put("gradeTerm", "6-1");
		incrtNoteMap.put("subjNm", "사회(2015개정)");
		incrtNoteMap.put("unitNm", "27장.다른 나라와의 경제 교류 사례 알아보기(일반)");
		incrtNoteMap.put("type", "실력평가");
		incrtNoteMap.put("lrnStt", "학습완료");
		incrtNoteMap.put("quesCnt", 2);
		incrtNoteMap.put("crtQuesCnt", 2);
		incrtNoteMap.put("remainQuesCnt", 0);
		incrtNoteMap.put("incrtNoteRegDt", "2020-06-23");
		incrtNoteMap.put("smtDttm", "2020-06-23 07:41:09");
		
		incrtNote.add(incrtNoteMap);
		
		data.put("incrtNote", incrtNote);
		
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
	public Map getExpReportIntro(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId"}, paramMap);
		//2.id 숫자형 체크
		if(vu.isValid()) vu.isNumeric("studId", String.valueOf(paramMap.get("studId")));
		
		Map<String, Object> data = new HashMap<>();
		Map<String, Object> expReportIntro = new HashMap<>();
		
		expReportIntro.put("title", "짜파게티 요리사");
		expReportIntro.put("studNm", "양예슬");
		expReportIntro.put("expDt", "2020.04.06~2020.04.16");
		expReportIntro.put("linkUrl", "http://gw.i-screamedu.co.kr/");
		
		data.put("expReportIntro", expReportIntro);
		
		if(vu.isValid()) {
			setResult(dataKey, data);
		}else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	
	@Override
	public Map getTchrFeedback(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId"}, paramMap);
		//2.id 숫자형 체크
		if(vu.isValid()) vu.isNumeric("studId", String.valueOf(paramMap.get("studId")));
		
		Map<String, Object> data = new HashMap<>();
		Map<String, Object> tchrFeedback = new HashMap<>();
		
		tchrFeedback.put("msg", "안녕하세요...");
		tchrFeedback.put("tchrNm", "양예슬");
		tchrFeedback.put("tchrCell", "010-1234-1234");
		
		data.put("tchrFeedback", tchrFeedback);
		
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
		
		Map<String, Object> data = new HashMap<>();
		Map<String, Object> lrnHabits = new HashMap<>();
		Map<String, Object> planHabit = new HashMap<>();
		Map<String, Object> incrtNoteHabit = new HashMap<>();
		Map<String, Object> aLrnHabit = new HashMap<>();
		Map<String, Object> concnHabit = new HashMap<>();
		Map<String, Object> slvHabit = new HashMap<>();
		
		Map<String, Object> attHabit = new HashMap<>();
		List<String> dtList = new ArrayList<String>();
		List<String> planDtList = new ArrayList<String>();
		List<String> attDtList = new ArrayList<String>();
		
		attHabit.put("score", 4);
		attHabit.put("attRt", 96);
		attHabit.put("planDayCnt", 8);
		attHabit.put("attDayCnt", 9);
		
		dtList.add("2020-08-30");
		dtList.add("2020-08-31");
		dtList.add("2020-09-01");
		
		planDtList.add("2020-08-31");
		planDtList.add("2020-09-01");
		
		attDtList.add("2020-08-31");
		
		attHabit.put("dtList", dtList);
		attHabit.put("planDtList", planDtList);
		attHabit.put("attDtList", attDtList);
		
		lrnHabits.put("attHabit", attHabit);
		
		planHabit.put("score", 4);
		planHabit.put("exRt", 100);
		planHabit.put("planCnt", 34);
		planHabit.put("exCnt", 34);
		planHabit.put("bLrnExCnt", 1);
		planHabit.put("lrnExCnt", 1);
		planHabit.put("dLrnExCnt", 1);
		planHabit.put("nLrnExCnt", 1);
		
		List<Map> incrtNoteHabitList = new ArrayList<Map>();
		Map<String, Object> incrtNoteHabitListMap1 = new HashMap<>();
		Map<String, Object> incrtNoteHabitListMap2 = new HashMap<>();
		Map<String, Object> incrtNoteHabitListMap3 = new HashMap<>();
		
		incrtNoteHabit.put("score", 4);
		incrtNoteHabit.put("incrtNoteNcCnt", 1);
		
		incrtNoteHabitListMap1.put("regDt", "2020-09-08");
		incrtNoteHabitListMap1.put("subjNm", "과학(2015개정)");
		incrtNoteHabitListMap1.put("unitNm", "8장.날아다니는 동물에는 어떤 것이 있을까요(일반)");
		
		incrtNoteHabitList.add(incrtNoteHabitListMap1);
		System.out.println(incrtNoteHabitList);
		
		incrtNoteHabitListMap2.put("regDt", "2020-09-08");
		incrtNoteHabitListMap2.put("subjNm", "과학(2015개정)");
		incrtNoteHabitListMap2.put("unitNm", "3단원.자신의 경험을 글로 써요(일반)");
		
		incrtNoteHabitList.add(incrtNoteHabitListMap2);
		System.out.println(incrtNoteHabitList);
		
		incrtNoteHabitListMap3.put("regDt", "2020-09-08");
		incrtNoteHabitListMap3.put("subjNm", "과학(2015개정)");
		incrtNoteHabitListMap3.put("unitNm", "2단원.중심 생각을 찾아요(일반)");
		
		incrtNoteHabitList.add(incrtNoteHabitListMap3);
		System.out.println(incrtNoteHabitList);
		
		incrtNoteHabit.put("list", incrtNoteHabitList);
		
		aLrnHabit.put("score", 4);
		aLrnHabit.put("aLrnExCnt", 43);
		
		List<String> subjCd = new ArrayList<String>();
		List<Integer> subjExCnt = new ArrayList<Integer>();
		
		subjCd.add("C01");
		subjCd.add("C02");
		subjCd.add("C03");
		subjCd.add("C04");
		subjCd.add("C05");
		subjCd.add("C06");
		
		aLrnHabit.put("subjCd", subjCd);
		
		subjExCnt.add(9);
		subjExCnt.add(8);
		subjExCnt.add(7);
		subjExCnt.add(6);
		subjExCnt.add(5);
		subjExCnt.add(4);
		
		aLrnHabit.put("subjExCnt", subjExCnt);
		
		aLrnHabit.put("subjCd", subjCd);
		
		concnHabit.put("score", 4);
		concnHabit.put("lowConcnCnt", 5);
		
		List<String> concnHabitList = new ArrayList<String>();
		concnHabitList.add("20.08.24 11:03~11:06 수학>실력다지기>각뿔을 알아볼까요(2)");
		concnHabitList.add("20.08.24 11:03~11:06 사회>예복습>세계 여러 나라 사람들의 생…");
		concnHabitList.add("20.08.24 11:03~11:06 사회>예복습>세계 여러 나라 사람들의 생…");
		
		concnHabit.put("list", concnHabitList);
		
		slvHabit.put("score", 4);
		slvHabit.put("imprvSlvHabitCnt", 7);
		slvHabit.put("skipQuesCnt", 0);
		slvHabit.put("guessQuesCnt", 0);
		slvHabit.put("cursoryQuesCnt", 7);
		
		data.put("lrnHabits", lrnHabits);
		data.put("planHabit", planHabit);
		data.put("incrtNoteHabit", incrtNoteHabit);
		data.put("aLrnHabit", aLrnHabit);
		data.put("concnHabit", concnHabit);
		data.put("slvHabit", slvHabit);
		
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
		Map<String, Object> examRst = new HashMap<>();
		Map<String, Object> maxSubj = new HashMap<>();
		Map<String, Object> minSubj = new HashMap<>();
		
		examRst.put("studNm", "양예슬");
		examRst.put("crtRt", 85);
		examRst.put("top10AvgScore", 90);
		examRst.put("explCnt", 14);
		examRst.put("ansQuesCnt", 96);
		examRst.put("crtQuesCnt", 82);
		
		List<String> maxActFieldNm = new ArrayList<String>();
		List<Integer> maxActFieldCrtRt = new ArrayList<Integer>();
		List<String> maxContFieldNm = new ArrayList<String>();
		List<Integer> maxContFieldCrtRt = new ArrayList<Integer>();
		
		maxSubj.put("studNm", "양예슬");
		maxSubj.put("maxSubjNm", "수학");
		maxSubj.put("maxSubjcrtRt", 90);
		maxSubj.put("top10AvgScore", 90);
		maxSubj.put("explCnt", 3);
		maxSubj.put("ansQuesCnt", 21);
		maxSubj.put("crtQuesCnt", 19);
		
		maxActFieldNm.add("계산력");
		maxActFieldNm.add("문제해결력");
		maxActFieldNm.add("이해력");
		maxActFieldNm.add("추론력");
		
		maxActFieldCrtRt.add(60);
		maxActFieldCrtRt.add(67);
		maxActFieldCrtRt.add(100);
		maxActFieldCrtRt.add(25);
		
		maxContFieldNm.add("규칙성");
		maxContFieldNm.add("도형");
		maxContFieldNm.add("수와 연산");
		maxContFieldNm.add("측정");
		maxContFieldNm.add("확률과 통계");
		
		maxContFieldCrtRt.add(0);
		maxContFieldCrtRt.add(0);
		maxContFieldCrtRt.add(80);
		maxContFieldCrtRt.add(80);
		maxContFieldCrtRt.add(0);
		
		maxSubj.put("actFieldNm", maxActFieldNm);
		maxSubj.put("actFieldCrtRt", maxActFieldCrtRt);
		maxSubj.put("contFieldNm", maxContFieldNm);
		maxSubj.put("contFieldCrtRt", maxContFieldCrtRt);
		
		List<String> minActFieldNm = new ArrayList<String>();
		List<Integer> minActFieldCrtRt = new ArrayList<Integer>();
		List<String> minContFieldNm = new ArrayList<String>();
		List<Integer> minContFieldCrtRt = new ArrayList<Integer>();
		
		minSubj.put("studNm", "양예슬");
		minSubj.put("minSubjNm", "과학");
		minSubj.put("minSubjcrtRt", 83);
		minSubj.put("top10AvgScore", 96);
		minSubj.put("explCnt", 5);
		minSubj.put("ansQuesCnt", 40);
		minSubj.put("crtQuesCnt", 33);
		
		minActFieldNm.add("결론도출");
		minActFieldNm.add("관찰측정");
		minActFieldNm.add("자료수집");
		minActFieldNm.add("자료해석력");
		minActFieldNm.add("적용력");
		minActFieldNm.add("지식/이해력");
		minActFieldNm.add("탐구력");
		
		minActFieldCrtRt.add(0);
		minActFieldCrtRt.add(0);
		minActFieldCrtRt.add(0);
		minActFieldCrtRt.add(0);
		minActFieldCrtRt.add(0);
		minActFieldCrtRt.add(100);
		minActFieldCrtRt.add(0);
		
		minContFieldNm.add("물질");
		minContFieldNm.add("생명");
		minContFieldNm.add("운동과 에너지");
		minContFieldNm.add("지구와 우주");
		minContFieldNm.add("탐구");
		
		minContFieldCrtRt.add(0);
		minContFieldCrtRt.add(0);
		minContFieldCrtRt.add(0);
		minContFieldCrtRt.add(95);
		minContFieldCrtRt.add(100);
		
		minSubj.put("actFieldNm", minActFieldNm);
		minSubj.put("actFieldCrtRt", minActFieldCrtRt);
		minSubj.put("contFieldNm", minContFieldNm);
		minSubj.put("contFieldCrtRt", minContFieldCrtRt);		
		
		data.put("examRst", examRst);
		data.put("maxSubj", maxSubj);
		data.put("minSubj", minSubj);
		
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
