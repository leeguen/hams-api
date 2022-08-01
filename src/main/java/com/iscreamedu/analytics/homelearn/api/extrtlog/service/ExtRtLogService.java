package com.iscreamedu.analytics.homelearn.api.extrtlog.service;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ExtRtLogService {

	public LinkedHashMap setRealTimeCompleteMission(Map<String, Object> paramMap) throws Exception;

	public LinkedHashMap setRealTimeMissonStatusChange(Map<String, Object> params) throws Exception;
	

}
