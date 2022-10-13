package com.iscreamedu.analytics.homelearn.api.student.service;

import java.util.Map;

public interface StudHomeLogService {

    public Map getHlogList (Map<String,Object> paramMap) throws Exception;
    
    public Map getHlogDetailList (Map<String,Object> paramMap) throws Exception;
    
    public Map getHlogThnList (Map<String,Object> paramMap) throws Exception;
    
    public Map getHlogDetail (Map<String,Object> paramMap) throws Exception;

    public Map getHlogInfo (Map<String,Object> paramMap) throws Exception;
}
