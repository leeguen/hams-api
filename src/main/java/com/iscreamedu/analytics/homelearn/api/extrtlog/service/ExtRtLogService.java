package com.iscreamedu.analytics.homelearn.api.extrtlog.service;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface ExtRtLogService {

	public LinkedHashMap setRealTimeCompleteMission(Map<String, Object> params, HttpServletRequest req) throws Exception;

	public LinkedHashMap setRealTimeMissonStatusChange(Map<String, Object> params) throws Exception;
	
	public LinkedHashMap setFnWaterJug(Map<String, Object> params) throws Exception;

	public LinkedHashMap setFnObjectReward(Map<String, Object> params) throws Exception;
	
	public LinkedHashMap resetWaterJug(Map<String, Object> params) throws Exception;

	public LinkedHashMap resetObjectReward(Map<String, Object> params) throws Exception;

	public LinkedHashMap setFnParticle(Map<String, Object> params) throws Exception;

	public LinkedHashMap resetParticle(Map<String, Object> params) throws Exception;
	

}
