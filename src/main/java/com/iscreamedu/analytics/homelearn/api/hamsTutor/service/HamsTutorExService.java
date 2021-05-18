package com.iscreamedu.analytics.homelearn.api.hamsTutor.service;

import java.util.Map;

public interface HamsTutorExService {

    public Map getSettleInfoPredictionStt (Map<String,Object> paramMap) throws Exception;

    public Map getAiDiagnosisRst(Map<String, Object> paramMap) throws Exception;

    public Map getAiWeakChapterGuide(Map<String, Object> paramMap) throws Exception;

    public Map getAiRecommendQuestion(Map<String, Object> paramMap) throws Exception;

}
