package com.iscreamedu.analytics.homelearn.api.hamsTutor.service;

import java.util.Map;

public interface HamsTutorService {

    public Map getSettleInfoPredictionStt (Map<String,Object> paramMap) throws Exception;

    public Map getAiDiagnosisRst(Map<String, Object> paramMap) throws Exception;

    public Map getAiWeakChapterGuide(Map<String, Object> paramMap) throws Exception;

    public Map getAiRecommendQuestion(Map<String, Object> paramMap) throws Exception;

    public Map getLrnBasicInfo(Map<String, Object> paramMap) throws Exception;

    public Map getLrnGrowthStt(Map<String, Object> paramMap) throws Exception;

    public Map getLrnExStt(Map<String, Object> paramMap) throws Exception;

    public Map getLrnExChart(Map<String, Object> paramMap) throws Exception;

    public Map getLrnTimeLineList(Map<String, Object> paramMap) throws Exception;

    public Map getExamStt(Map<String, Object> paramMap) throws Exception;

    public Map getExamChart(Map<String, Object> paramMap) throws Exception;

    public Map getExamList(Map<String, Object> paramMap) throws Exception;

    public Map getAttStt(Map<String, Object> paramMap) throws Exception;

    public Map getCommMsgCd(Map<String, Object> paramMap) throws Exception;

    

}
