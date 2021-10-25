package com.iscreamedu.analytics.homelearn.api.group.service;

import java.util.Map;

public interface GroupService {


    public Map getPeriod(Map<String, Object> paramMap) throws Exception;

    public Map getStud(Map<String, Object> paramMap) throws Exception;

    public Map getLrnBasic(Map<String, Object> paramMap) throws Exception;

    public Map getOrgEnvConfig(Map<String, Object> paramMap) throws Exception;

    public Map setOrgEnvConfig(Map<String, Object> paramMap) throws Exception;

    public Map getLrnHabitChart(Map<String, Object> paramMap) throws Exception;

    public Map getAiRecommendLrn(Map<String, Object> paramMap) throws Exception;

    public Map getDiagnosticEvalStt(Map<String, Object> paramMap) throws Exception;

    public Map getAttRtStt(Map<String, Object> paramMap) throws Exception;

    public Map getLrnTmList(Map<String, Object> paramMap) throws Exception;

    public Map getLrnDetail(Map<String, Object> paramMap) throws Exception;
    
    public Map getAttCntStt(Map<String, Object> paramMap) throws Exception;
    
    public Map getloginPtnStt(Map<String, Object> paramMap) throws Exception;
    
    public Map getExRtStt(Map<String, Object> paramMap) throws Exception;
    
    public Map getFnshLrnExStt(Map<String, Object> paramMap) throws Exception;
    
    public Map getLrnExSttCompareSub(Map<String, Object> paramMap) throws Exception;
    
    public Map getALrnExStt(Map<String, Object> paramMap) throws Exception;
    
    public Map getCrtRtStt(Map<String, Object> paramMap) throws Exception;
    
    public Map getIncrtNoteNcStt(Map<String, Object> paramMap) throws Exception;
    
    public Map getCrtQuesCntStt(Map<String, Object> paramMap) throws Exception;
    
    public Map getSlvHabitStt(Map<String, Object> paramMap) throws Exception;
    
    public Map getDayAvgLrnStt (Map<String, Object> paramMap) throws Exception;
    
    public Map getTotalLrnTmStt(Map<String, Object> paramMap) throws Exception;
    
    public Map getLongLrnTmStt(Map<String, Object> paramMap) throws Exception;
    
    public Map getSubjExam(Map<String, Object> paramMap) throws Exception;
    
    public Map getCompareSub(Map<String, Object> paramMap) throws Exception;
    
    public Map getExamChart(Map<String, Object> paramMap) throws Exception;
    
    public Map getSubjExamList(Map<String, Object> paramMap) throws Exception;
    
    public Map getIncrtNote(Map<String, Object> paramMap) throws Exception;
    
    public Map getChapterStt(Map<String, Object> paramMap) throws Exception;
    
    public Map getChapterLrn(Map<String, Object> paramMap) throws Exception;

    public Map getCommMsgCd(Map<String, Object> paramMap) throws Exception;
    
    
}
