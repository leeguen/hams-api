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
	
	private HttpSession session;

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
		Map<String, Object> studInfo = new HashMap<>();
		Map<String, Object> data = new HashMap<>();
		data.put("studId", 654321);
		data.put("studNm", "김홈런");
		data.put("grade", 5);
		data.put("schlNm", "홈런초등학교");
		data.put("gender", "F");
		data.put("tchrId", 123456);
		data.put("expStartDt", "2020-09-23");
		data.put("expEndDt", "2020-09-23");
		
		studInfo.put("studInfo", data);
		//Dummy End
		
		if(vu.isValid()) {
			setResult(dataKey, studInfo);
		}else {
			setResult(msgKey, vu.getResult());
		}
		
		return result;
	}
	
	@Override
	public Map getDailyLrnStt(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> dailyLrnStt = new HashMap<>();
		Map<String, Object> depth2DataMap = new HashMap<>();
		ArrayList<Map> dailyLrnSttlist = new ArrayList<Map>();
		
		//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"dt", "studId"}, paramMap);
		//2.dt 날짜형 체크
		if(vu.isValid()) vu.isDate("dt", (String)paramMap.get("dt"));
		//3.id 숫자형 체크
		if(vu.isValid()) vu.isNumeric("studId", String.valueOf(paramMap.get("studId")));
		
		List commWkDtList = (List) commonMapper.getList(paramMap, "Common.selectCommWkDt");
		for(int i = 0; i < commWkDtList.size(); i++) {
			depth2DataMap.put("dt",commWkDtList.get(i));
			depth2DataMap.put("attYn",false); //HL API output으로 교체 필요
			depth2DataMap.put("lrnStt",3); //HL API output으로 교체 필요
			
			ArrayList<String> loginLoglist = new ArrayList<String>(); //실 데이터 조회로 교체 필요
			loginLoglist.add("2020-09-07 20:56:33");
			loginLoglist.add("2020-09-07 15:58:26");
			loginLoglist.add("2020-09-07 15:22:06");
			loginLoglist.add("2020-09-07 13:18:03");
			
			depth2DataMap.put("loginLog", loginLoglist);
		
		dailyLrnSttlist.add(depth2DataMap);
		}
		
		dailyLrnStt.put("dailyLrnStt", dailyLrnSttlist);
		
		if(vu.isValid()) {
			setResult(dataKey, dailyLrnStt);
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
		
		Map<String, Object> settleInfoPrediction = new HashMap<>();
		Map<String, Object> data = new HashMap<>();
		data.put("signal", 1);
		
		List<String> list = new ArrayList<>();
		list.add("수행률");
		list.add("계획된 학습 학습시간");
		list.add("정답을 맞힌 문제 수");
		
		data.put("list", list);
		settleInfoPrediction.put("settleInfoPrediction", data);
		
		if(vu.isValid()) {
			setResult(dataKey, settleInfoPrediction);
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
		
		Map<String, Object> threeDayLrn = new HashMap<>();
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
		
		if(vu.isValid()) {
			setResult(dataKey, threeDayLrn);
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
