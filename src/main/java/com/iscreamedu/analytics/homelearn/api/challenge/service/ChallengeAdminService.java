package com.iscreamedu.analytics.homelearn.api.challenge.service;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ChallengeAdminService {

	public LinkedHashMap getChRewardStt(Map<String, Object> paramMap) throws Exception;
	
	public LinkedHashMap getChStepUpMisStt(Map<String, Object> paramMap) throws Exception;
	
	public LinkedHashMap getChHabitMisStt(Map<String, Object> paramMap) throws Exception;
	
	public LinkedHashMap getChMetaphorHistory(Map<String, Object> paramMap) throws Exception;

	public LinkedHashMap getChHabitMisHistory(Map<String, Object> paramMap) throws Exception;

	public LinkedHashMap getChStepUpMisHistory(Map<String, Object> paramMap) throws Exception;
	
	public LinkedHashMap regAdminCompleteMission(Map<String, Object> paramMap) throws Exception;

}
