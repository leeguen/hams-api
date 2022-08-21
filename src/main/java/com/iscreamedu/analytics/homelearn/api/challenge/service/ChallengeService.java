package com.iscreamedu.analytics.homelearn.api.challenge.service;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ChallengeService {

	public LinkedHashMap getChMetaphorHistory(Map<String, Object> paramMap) throws Exception;
	
	public LinkedHashMap getChMetaphorObjectStt(Map<String, Object> paramMap) throws Exception;
	
	public LinkedHashMap getChHabitMissionInfo(Map<String, Object> paramMap) throws Exception;
	
	public LinkedHashMap getKoreanBookChallenge(Map<String, Object> paramMap) throws Exception;

	public LinkedHashMap getKoreanBookChReward(Map<String, Object> paramMap) throws Exception;

	public LinkedHashMap getKoreanBookChMissonList(Map<String, Object> paramMap) throws Exception;

	public LinkedHashMap getChStepUpMissionInfo(Map<String, Object> paramMap) throws Exception;

	public LinkedHashMap getChallengeSummaryCnt(Map<String, Object> paramMap) throws Exception;

}
