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
	public Map getThreeDayLrnDetail (Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 학습 수행 결과 (HAMS-S-TR-005)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getLrnExStt (Map<String, Object> paramMap) throws Exception;
}
