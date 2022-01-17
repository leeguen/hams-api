package com.iscreamedu.analytics.homelearn.api.student.service;

import java.util.Map;

public interface StudLrnTypeService {

    public Map getLrnTypeSummary (Map<String,Object> paramMap) throws Exception;

    public Map getLrnTypeInfo(Map<String, Object> paramMap) throws Exception;

    public Map getLrnTypeDetail(Map<String, Object> paramMap) throws Exception;

    public Map getLrnTypeHistory(Map<String, Object> paramMap) throws Exception;
}
