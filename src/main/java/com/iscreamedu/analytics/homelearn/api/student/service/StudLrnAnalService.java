package com.iscreamedu.analytics.homelearn.api.student.service;

import java.util.Map;

public interface StudLrnAnalService {
	
	public Map getYymmwk(Map<String,Object> paramMap) throws Exception;
	
	public Map getYymm(Map<String,Object> paramMap) throws Exception;
	
	public Map getStudInfo(Map<String,Object> paramMap) throws Exception;
	
    public Map getReportList(Map<String,Object> paramMap) throws Exception;

    public Map getHomeSummary(Map<String, Object> paramMap) throws Exception;

    public Map getLrnStt(Map<String, Object> paramMap) throws Exception;

    public Map getLrnExRt(Map<String, Object> paramMap) throws Exception;

    public Map getLrnHabit(Map<String, Object> paramMap) throws Exception;
    
    public Map getConcn(Map<String, Object> paramMap) throws Exception;
    
    public Map getLrnTm(Map<String, Object> paramMap) throws Exception;
    
    public Map getAlrn(Map<String, Object> paramMap) throws Exception;
    
    public Map getExamScore(Map<String, Object> paramMap) throws Exception;
    
    public Map getIncrtNoteStt(Map<String, Object> paramMap) throws Exception;
    
    public Map getSlvHabit(Map<String, Object> paramMap) throws Exception;
    
    public Map getReportEmotion(Map<String, Object> paramMap) throws Exception;
    
    public Map insertReportEmotion(Map<String, Object> paramMap) throws Exception;
}
