package com.iscreamedu.analytics.homelearn.api.hamsSales.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

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
	public Map getStudInfo(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"dt", "studId"}, paramMap);
		//2.dt 날짜형 체크
		if(vu.isValid()) vu.isDate("dt", (String)paramMap.get("dt"));
		//3.id 숫자형 체크
		if(vu.isValid()) vu.isNumeric("studId", String.valueOf(paramMap.get("studId")));

		//Dummy Start
		Map<String, Object> data = new HashMap<>();
		Map<String, Object> studInfo = new HashMap<>();
		studInfo.put("studId", 654321);
		studInfo.put("studNm", "김홈런");
		studInfo.put("grade", 5);
		studInfo.put("schlNm", "홈런초등학교");
		studInfo.put("gender", "F");
		studInfo.put("tchrId", 123456);
		studInfo.put("expStartDt", "2020-09-23");
		studInfo.put("expEndDt", "2020-09-23");
		
		data.put("studInfo", studInfo);
		//Dummy End
		
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
		
		int i =0;
		
		for (Map<String,Object> item : commWkDtList) {
			Map<String, Object> dailyLrnSttMap = new HashMap<>();
			List<String> loginLoglist = new ArrayList<String>(); //실 데이터 조회로 교체 필요
			
			dailyLrnSttMap.put("dt",item.get("dt"));
			i+=1;
			if(i%2 == 0) {
				dailyLrnSttMap.put("attYn",false); //HL API output으로 교체 필요
				dailyLrnSttMap.put("lrnStt",0); //HL API output으로 교체 필요				
			} else {
				dailyLrnSttMap.put("attYn",true); //HL API output으로 교체 필요
				dailyLrnSttMap.put("lrnStt",i+1); //HL API output으로 교체 필요
				
				loginLoglist.add(item.get("dt") + " 20:56:33");
				loginLoglist.add(item.get("dt") + " 15:58:26");
				loginLoglist.add(item.get("dt") + " 15:22:06");
				loginLoglist.add(item.get("dt") + " 13:18:03");
			}
			
			dailyLrnSttMap.put("loginLogList", loginLoglist);
		
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
		Map<String, Object> threeDayLrnDetail = new HashMap<>();
		threeDayLrnDetail.put("totalLrnSec", 12840);
		
		List<Map> timeline = new ArrayList<>();
		Map<String, Object> timelineMap = new HashMap<>();
		timelineMap.put("dt", "2020.04.07");
		timelineMap.put("expDay", 3);
		
		List<Map> timelineDetailList = new ArrayList<>();
		Map<String, Object> timelineDetailMap = new HashMap<>();
		timelineDetailMap.put("subjCd", "C01");
		timelineDetailMap.put("exTm", "12:03 ~ 12:17");
		timelineDetailMap.put("lrnNm", "예복습 > 3학년 1학기 > 국어 > 2단원 문단의 짜임 > 5장 중심 문장과 뒷받침 문장 알기");
		timelineDetailMap.put("lrnSec", 841);
		timelineDetailMap.put("stdLrnSec", 578);
		timelineDetailMap.put("exType", "P");
		timelineDetailMap.put("planDt", "2020.04.07");
		
		timelineDetailList.add(timelineDetailMap);
		
		timelineMap.put("timelineDetailList", timelineDetailList);
		
		timeline.add(timelineMap);
		
		threeDayLrnDetail.put("timeline", timeline);
		data.put("threeDayLrnDetail", threeDayLrnDetail);
		
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
		
		grpAvg.put("exCnt",12);
		grpAvg.put("grpAvgExCnt",15);
		
		data.put("lrnExCntTop3",lrnExCntTop3);
		data.put("grpAvg",grpAvg);
		
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
		
		lrnEx.put("exRt",95);
		lrnEx.put("planCnt",12);
		lrnEx.put("exCnt",10);
		
		grpAvgEx.put("grpAvgExRt",95);
		grpAvgEx.put("grpAvgPlanCnt",15);
		grpAvgEx.put("grpAvgExCnt",12);
		
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
		
		grpAvg.put("exCnt",12);
		grpAvg.put("grpAvgExCnt",15);
		
		data.put("lrnExCntTop3", lrnExCntTop3);
		data.put("grpAvg", grpAvg);
		
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
