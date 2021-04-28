package com.iscreamedu.analytics.homelearn.api.hamsTutor.service;

import java.util.Map;

public interface HamsTutorVrService {
    public Map getVisionReportPublishedInfo(Map<String, Object> paramMap) throws Exception;

    public Map getVisionBasicInfo(Map<String, Object> paramMap) throws Exception;

    public Map getVisionGrowthStt(Map<String, Object> paramMap) throws Exception;

    public Map getVisionExamStt(Map<String, Object> paramMap) throws Exception;

    public Map getVisionExamChapterStt(Map<String, Object> paramMap) throws Exception;

    public Map getVisionExamChapterLrn(Map<String, Object> paramMap) throws Exception;

    public Map getVisionExamFieldStt(Map<String, Object> paramMap) throws Exception;

    public Map getVisionExamList(Map<String, Object> paramMap) throws Exception;

    public Map getVisionAttPtn(Map<String, Object> paramMap) throws Exception;

    public Map getVisionLrnPtn(Map<String, Object> paramMap) throws Exception;

    public Map getVisionPrintBasicInfo(Map<String, Object> paramMap) throws Exception;

    public Map getVisionPrintLrnStt(Map<String, Object> paramMap) throws Exception;

    public Map getVisionPrintLrnDiagnosisRst(Map<String, Object> paramMap) throws Exception;

    public Map getVisionPrintAiRecommendLrn(Map<String, Object> paramMap) throws Exception;


}
