package com.iscreamedu.analytics.homelearn.api.common.mapper;

import java.util.LinkedHashMap;

/**
 * HashMap 의 키 값에 CamelCase를 적용한 클래스
 * @author hy
 * @since 2019.07.19
 * @version 1.0
 * @see
 *  
 * <pre>
 * << 개정이력(Modification Information) >>
 * 
 *  수정일      		수정자		수정내용
 *  ----------  --------    ---------------------------
 *  2019.07.19  hy        	최초 생성 
 *  </pre>
 */
public class ResultMap extends LinkedHashMap {
	
	private static final long serialVersionUID = 6723431239565859871L;

    @Override
    public Object put(Object key, Object value) {
        return super.put( this.convert2CamelCase((String)key), value);
    }
	
	private String convert2CamelCase(String underScore) {
		//'_'가 없고 첫문자가 소문자이면 그냥 리턴
        if (underScore.indexOf('_') < 0 && Character.isLowerCase(underScore.charAt(0))) {
            return underScore;
        }
        
        StringBuilder result = new StringBuilder();
        boolean nextUpper = false;
        int len = underScore.length();

        for (int i = 0; i < len; i++) {
            char currentChar = underScore.charAt(i);
            if (currentChar == '_') {
                nextUpper = true;
            } else {
                if (nextUpper) {
                    result.append(Character.toUpperCase(currentChar));
                    nextUpper = false;
                } else {
                    result.append(Character.toLowerCase(currentChar));
                }
            }
        }
        return result.toString();
    }
}
