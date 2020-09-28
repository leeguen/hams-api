package com.iscreamedu.analytics.homelearn.api.hamsSales.service;

import java.util.Map;

/**
 * HAMS Sales API Service
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
public interface HamsSalesService {
	/**
	 * 학생 정보 (HAMS-S-C-002)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getStudInfo (Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 일일 학습 정보 (HAMS-S-TR-001)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getDailyLrnStt (Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 일일 학습 정보 (HAMS-S-TR-002)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getSettleInfoPrediction (Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 3일 학습 수행 요약 (HAMS-S-TR-003)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getThreeDayLrn (Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 3일 학습 상세 내역 (HAMS-S-TR-004)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getLrnPtn (Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 학습 수행 결과 (HAMS-S-TR-005)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getLrnExStt (Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 계획 (HAMS-S-TR-006)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getLrnPlanStt (Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 스스로 학습 (HAMS-S-TR-007)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getALrnStt (Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 홈런북카페 (HAMS-S-TR-008)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getHLBookCafe (Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 영어도서관 (HAMS-S-TR-009)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getEngLibrary (Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 평가 (HAMS-S-TR-010)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getExam (Map<String, Object> paramMap) throws Exception;
}
