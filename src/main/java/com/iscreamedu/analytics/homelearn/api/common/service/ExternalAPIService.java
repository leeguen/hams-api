package com.iscreamedu.analytics.homelearn.api.common.service;

import java.util.Map;

/**
 * HAMS External API Service
 * 외부 API를 호출하여 프로젝트에 정의된 Object형태로 리턴한다. 
 * @author hy
 * @since 2020.10.13
 * @version 1.0
 * @see
 *  
 * <pre>
 * << 개정이력(Modification Information) >>
 * 
 *  수정일      		   수정자		수정내용
 *  ----------  --------    --------------------------
 *  2020.10.13	 hy		           초기생성 
 *  </pre>
 */
public interface ExternalAPIService {
	/**
	 * 외부 API 호출 (홈런,홈런북카페,영어도서관)
	 * 기본 API URL 이후의 파라미터로 넘겨받는 path는 '/' 대신 '.'로 넘겨줘야 함.
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map callExternalAPI (Map<String, Object> paramMap) throws Exception;
	
}
