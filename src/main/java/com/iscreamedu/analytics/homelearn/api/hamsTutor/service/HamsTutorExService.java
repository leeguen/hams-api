package com.iscreamedu.analytics.homelearn.api.hamsTutor.service;

import java.util.Map;

public interface HamsTutorExService {

    public Map getSettleInfoPredictionStt (Map<String,Object> paramMap) throws Exception;

    public Map getAiDiagnosisRst(Map<String, Object> paramMap) throws Exception;

    public Map getAiWeakChapterGuide(Map<String, Object> paramMap) throws Exception;

    public Map getAiRecommendQuestion(Map<String, Object> paramMap) throws Exception;
    
    public Map getAiRecommendCourse(Map<String, Object> paramMap) throws Exception;
    
    public Map getAiRecommendCourseConfirm(Map<String, Object> paramMap) throws Exception;
    
    public Map getAiRecommendCourseApply(Map<String, Object> paramMap) throws Exception;
}
