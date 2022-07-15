package com.iscreamedu.analytics.homelearn.api.extrtlog.service.impl;

import java.util.LinkedHashMap;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.iscreamedu.analytics.homelearn.api.common.mapper.CommonMapperLrnLog;
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
    
	@Override
	public LinkedHashMap setCompleteMission(Map<String, Object> paramMap) throws Exception {
		//Validation
		ValidationUtil vu = new ValidationUtil();
		
		if(!paramMap.containsKey("studId") && paramMap.containsKey("p")) {
			String studId = "";
			String encodedStr = paramMap.get("p").toString();
			
			String[] paramList = CommonUtil.getDecodedParam(encodedStr);
			studId = paramList[1];
			paramMap.put("studId", studId);
		}

		paramMap.put("misContents", null);
		
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId","yyyymmdd","hhmm","chCd","misStatusCd","regAdminId"}, paramMap);
	
		//studType, grade, misNo 조회해서 저장
		// MHL 오늘의 학습, MWN 오답노트, MNL미완료학습 : 정회원 studType 1, grade -99 
		// MLG 출석하기: 구분없음(체험회원+정회원) studType -1, grade -99
		// ** MKB 국어책 챌린지 : 구분없음(체험회원+정회원) studType -1, grade 있음. -- 실시간 등록 호출 대상 아님!!
		if(vu.isValid()) {
			switch(paramMap.get("chCd").toString())
			{
				case "MHL" :
				case "MWN" :
				case "MNL" :
					paramMap.put("studType", 1);
					paramMap.put("grade", -99);
					break;
				case "MLG" : 
					paramMap.put("studType", -1);
					paramMap.put("grade", -99);
					break;
			}
			String strResultMsg = null;
			LinkedHashMap message = new LinkedHashMap();			
			try {
			    commonMapperLrnLog.insert(paramMap, "LrnLog.ispCompleteMission");
				Integer nResultCnt = Integer.valueOf(paramMap.get("outResultCnt").toString());
				strResultMsg = paramMap.get("outResultMsg").toString();
				if(nResultCnt > 0) {
					
					message.put("resultCode", ValidationCode.REG_SUCCESS.getCode());
					message.put("resulst", nResultCnt+"건 등록 : "+strResultMsg);
					setResult(msgKey, message);
				} else {
					message.put("resultCode", ValidationCode.REG_FAILED.getCode());
					message.put("resulst", strResultMsg);
					setResult(msgKey, message);
				}
			} catch(Exception e) {
				String[] errorMsgList = e.getMessage().split(": ");
				strResultMsg = "Registration failed [ " + errorMsgList[errorMsgList.length-1] + " ]";
				message.put("resultCode", ValidationCode.REG_FAILED.getCode());
				message.put("resulst", strResultMsg);
				setResult(msgKey, message);
				try {
					JSONObject jsonMap = new JSONObject();
					jsonMap.putAll(paramMap);
					Map<String, Object> paramMap2 = new LinkedHashMap();
					paramMap2.put("inProcName", "ispCompleteMission");
					paramMap2.put("inProcStep", 0);
					paramMap2.put("inYyyymmdd", paramMap.get("yyyymmdd"));
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
