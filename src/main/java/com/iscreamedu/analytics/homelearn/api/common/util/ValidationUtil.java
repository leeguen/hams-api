package com.iscreamedu.analytics.homelearn.api.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SplittableRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * 정합성 체크 유틸
 * @author hy
 * @since 2019.09.05
 * @version 1.0
 * @see
 *  
 * <pre>
 * << 개정이력(Modification Information) >>
 * 
 *  수정일      		수정자		수정내용
 *  ----------  --------    ---------------------------
 *  2019.09.05	hy			초기생성 
 *  </pre>
 */
public class ValidationUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ValidationUtil.class);
	
	//정합성 체크 결과
	private LinkedHashMap<String, Object> result;
	// 데모 계정 여부 : demo 계정 이면 true, 리얼 계정이면 false
	private boolean isDemo = false;
	private String alternativeAccount = null;
	private SplittableRandom splittableRandom = null;
	
	@Value("${validation-util.org.demo-account.studid}")
	private String ORG_DEMO_ACCOUNT_STUD_ID; //기관 데모 계정 - 고정용 학생 아이디
	
	
	/**
	 * 데모 계정 Info 리턴
	 * @return
	 */
	public String getDemoAccountInfo(String target, String item) {
		if(target.equals("ORG")) {
			if(item.equals("STUD_ID")) return ORG_DEMO_ACCOUNT_STUD_ID;
			else if(item.equals("LOGIN_ID")) return "***test" + splittableRandom.nextInt(1, 10);
			else if(item.equals("STUD_NM")) return "김*" + splittableRandom.nextInt(1, 10);
			else return null;
		}
		else return null;
	}
	
	/**
	 * 정합성 체크결과 리턴
	 * @return
	 */
	public LinkedHashMap<String, Object> getResult() {
		return result;
	}
	
	/**
	 * 정합성 체크결과 정상이면 true, 오류이면 false
	 * @return
	 */
	public boolean isValid() {
		if(result == null) {
			return true; //결과 log값이 없을때 true 반환
		} else {
			return false; //log값이 있을때 false 반환
		}
	}
	
	/**
	 * 데모 계정 여부 : demo 계정 이면 true, 리얼 계정이면 false
	 * @return
	 */
	public boolean isDemoAccount() {
		return isDemo;
	}
	
	/**
	 * 데모 계정 : 대체 계정 추출
	 * @return
	 */
	public String getAlternativeAccount() {
		return alternativeAccount;
	}

	
	/**
	 * 데모 계정 여부 체크
	 * @param value : 계정
	 * @return
	 */
	public void checkDemoAccount(String accountId) {
		try {
			if(accountId.contains("demo")) {
				isDemo = true;
				alternativeAccount = accountId.replaceAll("demo", "");
				splittableRandom = new SplittableRandom();
			}
			else {
				isDemo = false;
				alternativeAccount = null;
				splittableRandom = null;
			}
		} catch (NumberFormatException e) {
			isDemo = false;
			alternativeAccount = null;
			splittableRandom = null;
		} 
	}
	
	/*
	 * 시스템 error 처리
	 */
    public void setError(String key) {
		showMessage(ValidationCode.SYSTEM_ERROR, key, ValidationCode.SYSTEM_ERROR.getMessage());
    }
    
	/**
	 * Vaildation log를 찍고, 해당메시지를 결과에 담는다.
	 * @param v_code : Validation Code
	 * @param key : 오류가 되는 키
	 * @param value : 오류가 되는 값
	 * @return
	 */
	public void showMessage(ValidationCode v_code, String key, String value) {
		LOGGER.warn( "===== Validation Code : " + v_code.getCode() + "(" + v_code.getMessage() + ")" + ", param : " + key + ", " + value );
		
		result = new LinkedHashMap<String, Object>();
		result.put("resultCode", v_code.getCode());
		result.put("result", key + " : " + value);
    }
	
	/**
	 * 필수 파라미터 체크
	 * @param requieredKeyArr
	 * @param paramMap
	 * @return
	 */
	public void checkRequired(String[] requieredKeyArr, Map<String, Object> paramMap) {
		for(String key : requieredKeyArr) {
			if(paramMap.containsKey(key)){
				isBlank(key, paramMap.get(key).toString()); //파라미터에 require key가 있을때
			} else {
				showMessage(ValidationCode.REQUIRED, key, "required");
			}
			
			if(!this.isValid()) {
				break;
			}
		}
    }
	
	/**
	 * 빈값 체크
	 * @param key
	 * @param value
	 * @return
	 */
	public void isBlank(String key, String value) {
		//필수값이 있지만 비었을때
		if(value == null || value.trim().length() == 0) {
			showMessage(ValidationCode.REQUIRED, key, "blank");
		}
    }
	
	/**
	 * 숫자형 체크
	 * @param key
	 * @param value
	 * @return
	 */
	public void isNumeric(String key, String value) {
		try {
			Double.parseDouble(value);
		} catch (NumberFormatException e) {
			showMessage(ValidationCode.NUMERIC, key, value);
		}
	}
	
	/**
	 * Date형 체크(yyyy-MM-dd)
	 * @param key
	 * @param value
	 * @return
	 */
	public void isDate(String key, String value) {
		try {
			SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyy-MM-dd");
			dateFormat.setLenient(false);
			dateFormat.parse(value);
		}catch(ParseException e) {
			showMessage(ValidationCode.DATE, key, value + " ( " + ValidationCode.DATE.getMessage() + " )");
		}
	}
	
	/**
	 * Date형 체크(yyyyMM)
	 * @param key
	 * @param value
	 * @return
	 */
	public void isYearMonth(String key, String value) {
		try {
			SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyyMM");
			dateFormat.setLenient(false);
			dateFormat.parse(value);
		}catch(ParseException e) {
			showMessage(ValidationCode.DATE, key, value + " ( " + ValidationCode.DATE.getMessage() + " )");
		}
	}
	
	/**
	 * Date형 체크(yyyy)
	 * @param key
	 * @param value
	 * @return
	 */
	public void isYear(String key, String value) {
		try {
			SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyy");
			dateFormat.setLenient(false);
			dateFormat.parse(value);
		}catch(ParseException e) {
			showMessage(ValidationCode.DATE, key, value);
		}
	}
	
	/**
	 * 길이 체크
	 * @param key
	 * @param value
	 * @param length
	 * @return
	 */
	public void checkLength(String key, String value, int length) {
		if(value.length() != length) {
			showMessage(ValidationCode.LENGTH, key, value);
		}
	}
	
	/**
	 * 숫자형 범위 체크
	 * @param key
	 * @param value
	 * @param min
	 * @param max
	 * @return
	 */
	public void checkNumericRange(String key, String value, int min, int max) {
		isNumeric(key, value);
		if(result == null) {
			int num = Integer.parseInt(value);
			if(min > num || num > max) {
				showMessage(ValidationCode.RANGE, key, value);
			}
		}
	}
	
}
