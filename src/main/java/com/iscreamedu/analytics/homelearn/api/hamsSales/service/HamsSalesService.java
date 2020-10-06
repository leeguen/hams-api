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
	 * healthCheck
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map healthCheck (Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 과목 코드 정보 (HAMS-S-C-001)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getSubjCodeInfo (Map<String, Object> paramMap) throws Exception;
	
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
	 * 학습패턴 (HAMS-S-TR-004)
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
	
	/**
	 * 푼 평가지 (HAMS-S-TR-011)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getExpl (Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 오답노트 (HAMS-S-TR-012)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getIncrtNote (Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 학생 - 다중지능 검사 (HAMS-S-TR-013)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getMultipleIntelligenceTest (Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 학부모 - 양육태도,학습지원활동 검사 (HAMS-S-TR-014)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getParentingTest (Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 인트로 (HAMS-S-ER-001)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getExpReportIntro (Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 선생님 피드백 (HAMS-S-ER-002)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getTchrFeedback (Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 과목별 학습 패턴 (HAMS-S-ER-003)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getSubjLrnPtn (Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 바른 학습 습관 (HAMS-S-ER-004)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getLrnHabits (Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 평가 분석 (HAMS-S-ER-005)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getExamStt (Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 콘텐츠 추천 (HAMS-S-ER-006)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getRecommendedContents (Map<String, Object> paramMap) throws Exception;
}
