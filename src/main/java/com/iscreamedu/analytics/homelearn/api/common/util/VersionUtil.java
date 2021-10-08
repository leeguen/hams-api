package com.iscreamedu.analytics.homelearn.api.common.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VersionUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(VersionUtil.class);

	
	// Version Change Period ( 임의로 2021-11-01 09:00:00 ~ 2021-11-08 09:00:00 으로 지정 ) 1.0
	private static LocalDateTime start_datetime = LocalDateTime.of(2021, 11, 1, 9, 0, 0);	
	private static LocalDateTime end_datetime = LocalDateTime.of(2021, 11, 8, 9, 0, 0);

	// Version Change Period ( 임의로 2021-10-06 09:00:00 ~ 2021-10-08 09:00:00 으로 지정 ) 1.5 
//	private static LocalDateTime start_datetime = LocalDateTime.of(2021, 10, 6, 9, 0, 0);	
//	private static LocalDateTime end_datetime = LocalDateTime.of(2021, 10, 8, 9, 0, 0);
	
	// Version Change Period ( 임의로 2021-10-01 09:00:00 ~ 2021-10-06 16:00:00 으로 지정 ) 2.0 으로 전환 후
//	private static LocalDateTime start_datetime = LocalDateTime.of(2021, 10, 1, 9, 0, 0);	
//	private static LocalDateTime end_datetime = LocalDateTime.of(2021, 10, 6, 16, 16, 0);
	
	private static List<String> dw_method_list = new ArrayList<>(Arrays.asList("PERIOD","OTHER"));
	
	/* 
	 * API별 DataWare Version Check 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> getDataWareVersion(Map<String, Object> paramMap)  throws Exception {
		LOGGER.debug("getDataWareVersion called..... paramMap : " + paramMap);
		String strVersion = "1.0";
		String strMapperName = "CommonMapperTutor";
		LocalDateTime today = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
		LOGGER.debug("#####################################");
		LOGGER.debug("today : " + today);
		LOGGER.debug("start_datetime : " + start_datetime);
		LOGGER.debug("end_datetime : " + end_datetime);
		LOGGER.debug("dw_method_list : " + dw_method_list);
		LOGGER.debug("#####################################");
		
		if(paramMap.get("CHANNEL").toString().toUpperCase().equals("GROUP")) {	// 기관용
			
			// 버전체크 대상 method 구분 
			if(dw_method_list.contains(paramMap.get("METHOD"))) { 
				if(today.isAfter(start_datetime) && today.isBefore(end_datetime)) {
					strVersion = "1.5";
					strMapperName = "CommonMapperTutor";
				} else if(today.isAfter(end_datetime)) {
					strVersion = "2.0";
					strMapperName = "CommonMapperTutor";
				} else {
					strVersion = "1.0";
					strMapperName = "CommonMapperTutor";
				}
			}
			
		} 
		Map<String, Object> result = new HashMap<>();
		result.put("DW_VERSION", strVersion);
		result.put("MAPPER_NAME", strMapperName);
		LOGGER.debug("getDataWareVersion return..... result : " + result);
		return result;
	}
}
