package com.iscreamedu.analytics.homelearn.api.student.service;

import java.util.Map;

public interface StudLrnTypeService {

    public Map getLrnTypeCheck (Map<String,Object> paramMap) throws Exception;

    public Map getLrnTypeDetail(Map<String, Object> paramMap) throws Exception;

    public Map getLrnTypeHistory(Map<String, Object> paramMap) throws Exception;
    
    public Map getLrnTypePathInfo(Map<String, Object> paramMap) throws Exception;
    
    public Map getLrnTypeDetailForAdmin(Map<String, Object> paramMap) throws Exception;

    public Map getLrnTypeHistoryForAdmin(Map<String, Object> paramMap) throws Exception;
    
    public Map getStudLrnTypeInfo(String studId) throws Exception;
}
