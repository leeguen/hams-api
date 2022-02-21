package com.iscreamedu.analytics.homelearn.api.group.service;

import java.util.ArrayList;
import java.util.Map;
/**
 * 사회공헌 API Service
 * 메시지 object,데이터 object를 담기때문에 모두 Map의 형태로 리턴한다. 
 * @author 
 * @since 
 * @version 1.0
 * @see
 *  
 * <pre>
 * << 개정이력(Modification Information) >>
 * 
 *   수정일      		수정자		          수정내용
 *  ----------  --------    ---------------------------
 *  2020.05.19	shsong		초기생성
 *  2021.01.04  shoshu      고도화 추가
 *  2021.06.03  5heeteak    학년, 반 관리 기능 추가
 *  2022.01.18  jhlim   	hams api 통합
 *  </pre>
 */
public interface GroupDashboardService {
	
	/**
	 * 기관 정보
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getAgencyInfo(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 년월, 주차 산출
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map selectGetYymmWk(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 학년, 반 관리 정보
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getGradeClassYn(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 학년, 반 정보 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getGradeClassInfo(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 현황 메인
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map selectGroupStatusMain(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 학습 차트
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map selectGroupLrnChart(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 지도 정보
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map selectGroupMapInfo(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 온라인 개학
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map selectGroupOnlineSchool(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 학생 리스트
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map selectGroupStudList(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 지역 리스트
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map selectGroupLocalList(Map<String, Object> paramMap) throws Exception;

	/**
	 * 메인 지역
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map selectGroupMainLocal(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 학습지표 (ORG-LR-001)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getLrnStt (Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 학습지표 추이 (ORG-LR-002)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getLrnSttTrend (Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 기관학습현황 (ORG-LR-003)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getOrgLrnStt (Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 홈런 활용 (ORG-LR-004)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getHlUtilization (Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 학습 분석 신호 변화 (ORG-LR-005)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getLrnSignalTrend (Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 강점 습관 분석 우수회원 (ORG-LR-006)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getStrengthHabitExcellentStud (Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 강점 습관 분석 우수회원 학습특성 (ORG-LR-007)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getStrengthHabitExcellentStudHabit (Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 강점 습관 분석 고성장 회원 (ORG-LR-008)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getStrengthHabitHighGrowthStud (Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 강점 습관 분석 고성장 회원 학습특성 (ORG-LR-009)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getStrengthHabitHighGrowthStudHabit (Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 학습계획 학생 학습 현황 (ORG-LP-001)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map getLrnPlanStudLrnStt (Map<String, Object> paramMap) throws Exception;
        
}
