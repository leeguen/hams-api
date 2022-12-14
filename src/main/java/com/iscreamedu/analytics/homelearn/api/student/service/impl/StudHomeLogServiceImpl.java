package com.iscreamedu.analytics.homelearn.api.student.service.impl;

import com.iscreamedu.analytics.homelearn.api.common.security.CipherUtil;
import com.iscreamedu.analytics.homelearn.api.common.service.ExternalAPIService;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationCode;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtil;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtilTutor;
import com.iscreamedu.analytics.homelearn.api.student.service.StudHomeLogService;
import com.iscreamedu.analytics.homelearn.api.student.service.StudLrnLogService;
import com.iscreamedu.analytics.homelearn.api.student.service.StudLrnTypeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iscreamedu.analytics.homelearn.api.common.exception.NoDataException;
import com.iscreamedu.analytics.homelearn.api.common.mapper.CommonMapperLrnDm;
import com.iscreamedu.analytics.homelearn.api.common.mapper.CommonMapperLrnType;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class StudHomeLogServiceImpl implements StudHomeLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudHomeLogServiceImpl.class);

    private LinkedHashMap<String, Object> result;
    private LinkedHashMap<String, Object> decodeResult;
    private String msgKey = "msg";
    private String dataKey = "data";

    private static final String LRN_MT_NAMESPACE = "LrnMt";

    @Autowired
    CommonMapperLrnDm studLrnAnalMapper;
    
    @Autowired
    CommonMapperLrnType commonMapperLrnType;
    
    @Autowired
	ExternalAPIService externalAPIservice;
    
    @Override
    public Map getHlogList(Map<String,Object> paramMap) throws Exception {
        Map<String,Object> data = new LinkedHashMap<>();
        
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        getStudId(paramMap);
        
        vu.checkRequired(new String[] {"studId"}, paramMap);
        if(vu.isValid()) {
        	Calendar month = Calendar.getInstance();
        	month.add(Calendar.MONTH , 0);
            String stringYymm = new java.text.SimpleDateFormat("yyyy").format(month.getTime());
    		int yyyy = Integer.parseInt(stringYymm);
        	
        	if(paramMap.get("yyyy") != null) {
        		paramMap.put("yyyy", (!paramMap.get("yyyy").toString().equals("")) ? Integer.parseInt(paramMap.get("yyyy").toString()): yyyy);
        	} else {
        		paramMap.put("yyyy", yyyy);
        	}
        	
            data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "Homelog.selectHomelogPageCnt");
            
            int totalCnt = Integer.parseInt(data.get("totalCnt").toString());
            
            if(paramMap.get("page") != null) {
        		int pageIndex = (!paramMap.get("page").toString().equals("")) ? Integer.parseInt(paramMap.get("page").toString()): 0;
        		paramMap.put("page", (pageIndex * 10) - 10);
        		
        		data.put("currPage", (totalCnt > 0) ? (pageIndex == 0) ? 1 : pageIndex : 0);
        	} else {
        		paramMap.put("page", 0);
        		data.put("currPage", (totalCnt > 0) ? 1 : 0);
        	}
            
            /*?????? ?????? ??? ??????*/
        	
        	/*data.put("totalCnt", 100);
            data.put("pageCnt", 10);
            data.put("currPage", 1);*/
            
            ArrayList<Map<String, Object>> homelogList = (ArrayList<Map<String, Object>>) commonMapperLrnType.getList(paramMap, "Homelog.selectHomelogAdminList");
            
            /*ArrayList<Map<String, Object>> homelogList = new ArrayList<>();
            Map<String, Object> homelogMap = new LinkedHashMap<>();
            homelogMap.put("cd", 22100001);
            homelogMap.put("grp", "????????? ???");
            homelogMap.put("grpCd", "M");
            homelogMap.put("name", "?????? ?????? ???");
            homelogMap.put("property", "??????");
            homelogMap.put("cont", "?????? ?????? ?????? ?????? ????????? ??????");
            homelogMap.put("tchrName", "???????????? (admturu)");
            homelogMap.put("status", "????????????");
            homelogMap.put("regDttm", "2022-10-06 10:30:00");
            
            Map<String, Object> homelogMap1 = new LinkedHashMap<>();
            homelogMap1.put("cd", 22990001);
            homelogMap1.put("grp", "???????????? ???");
            homelogMap.put("grpCd", "A");
            homelogMap1.put("name", "????????? - ????????????");
            homelogMap1.put("property", "??????");
            homelogMap1.put("cont", "?????? ????????? ?????? 1%");
            homelogMap1.put("tchrName", "AI???????????????");
            homelogMap1.put("status", "????????????");
            homelogMap1.put("regDttm", "2022-10-07 10:30:00");
            
            homelogList.add(homelogMap);
            homelogList.add(homelogMap1);*/
            
            data.put("hlogList", homelogList);
            
            setResult(dataKey,data);
        } else {
        	setResult(msgKey, vu.getResult());
        }
        
	    return result; 
    }
    
    @Override
    public Map getHlogCnt(Map<String,Object> paramMap) throws Exception {
        Map<String,Object> data = new LinkedHashMap<>();
        
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        getStudId(paramMap);
        
        vu.checkRequired(new String[] {"studId"}, paramMap);
        if(vu.isValid()) {
        	 /*?????? ??? ??????*/
        	if(paramMap.get("startYyyy") != null) {
        		paramMap.put("startYyyy", (!paramMap.get("startYyyy").toString().equals("")) ? Integer.parseInt(paramMap.get("startYyyy").toString()) : null);
        	}
        	
        	if(paramMap.get("endYyyy") != null) {
        		paramMap.put("endYyyy", (!paramMap.get("endYyyy").toString().equals("")) ? Integer.parseInt(paramMap.get("endYyyy").toString()) : null);
        	}
        	
            ArrayList<Map<String, Object>> homelogList = (ArrayList<Map<String, Object>>) commonMapperLrnType.getList(paramMap, "Homelog.selectHomelogCntList");
            /*?????? ??? ??????*/
            
            /*Map<String, Object> homelogMap = new LinkedHashMap<>();
            homelogMap.put("yyyy", 2022);
            homelogMap.put("hLogCount", 100);
            
            Map<String, Object> homelogMap1 = new LinkedHashMap<>();
            homelogMap1.put("yyyy", 2021);
            homelogMap1.put("hLogCount", 0);
            
            homelogList.add(homelogMap);
            homelogList.add(homelogMap1);*/
            
            data.put("hLogCountList", homelogList);
            
            setResult(dataKey,data);
        } else {
        	setResult(msgKey, vu.getResult());
        }
        
	    return result; 
    }
    
    @Override
    public Map getHlogDetailList(Map<String,Object> paramMap) throws Exception {
        Map<String,Object> data = new LinkedHashMap<>();
        
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        getStudId(paramMap);
        
        vu.checkRequired(new String[] {"studId"}, paramMap);
        if(vu.isValid()) {
        	 /*?????? ?????? ??? ??????*/
        	
        	Calendar month = Calendar.getInstance();
        	month.add(Calendar.MONTH , 0);
            String stringYymm = new java.text.SimpleDateFormat("yyyy").format(month.getTime());
    		int yyyy = Integer.parseInt(stringYymm);
        	
        	if(paramMap.get("yyyy") != null) {
        		paramMap.put("yyyy", (!paramMap.get("yyyy").toString().equals("")) ? Integer.parseInt(paramMap.get("yyyy").toString()): yyyy);
        	} else {
        		paramMap.put("yyyy", yyyy);
        	}
        	
            data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "Homelog.selectHomelogPageCnt");
            
            int totalCnt = Integer.parseInt(data.get("totalCnt").toString());
            
            if(paramMap.get("page") != null) {
        		int pageIndex = (!paramMap.get("page").toString().equals("")) ? Integer.parseInt(paramMap.get("page").toString()): 0;
        		paramMap.put("page", (pageIndex * 10) - 10);
        		
        		data.put("currPage", (totalCnt > 0) ? (pageIndex == 0) ? 1 : pageIndex : 0);
        	} else {
        		paramMap.put("page", 0);
        		data.put("currPage", (totalCnt > 0) ? 1 : 0);
        	}
            
            /*?????? ?????? ??? ??????*/
        	
        	/*data.put("totalCnt", 100);
            data.put("pageCnt", 10);
            data.put("currPage", 1);*/
            
            ArrayList<Map<String, Object>> homelogList = (ArrayList<Map<String, Object>>) commonMapperLrnType.getList(paramMap, "Homelog.selectHomelogDetailList");
            
            if(homelogList != null && homelogList.size() > 0) {
            	String studName = getStudNm(paramMap);
                
                if(studName != null) {
                	for(Map<String, Object> homelogItem : homelogList) {
                		homelogItem.put("studName", studName);
                	}
                }
            }
            
            /*Map<String, Object> homelogMap = new LinkedHashMap<>();
            homelogMap.put("cd", 22100001);
            homelogMap.put("id", "ISE-FFC-10-0001");
            homelogMap.put("grp", "????????? ???");
            homelogMap.put("grpCd", "M");
            homelogMap.put("name", "?????? ?????? ???");
            homelogMap.put("studName", "????????????");
            homelogMap.put("property", "?????? ????????? - ????????? ?????? ??????");
            homelogMap.put("period", "2022??? 10??? 01??? ~ 2022??? 10??? 11???");
            homelogMap.put("cont", "??? ?????????.... ???????????????.");
            homelogMap.put("tchrName", "????????????");
            homelogMap.put("templateUrl", "????????? URL");
            homelogMap.put("regDttm", "2022-10-06 10:30:00");
            
            Map<String, Object> homelogMap1 = new LinkedHashMap<>();
            homelogMap1.put("cd", 22990001);
            homelogMap1.put("id", "ISE-FFC-99-0001");
            homelogMap1.put("grp", "???????????? ???");
            homelogMap1.put("grpCd", "A");
            homelogMap1.put("name", "????????? - ????????????");
            homelogMap1.put("studName", "????????????");
            homelogMap1.put("property", "?????? ????????? - ????????? ?????? ??????");
            homelogMap1.put("period", "2022??? 10??? 01??? ~ 2022??? 10??? 11???");
            homelogMap1.put("cont", "??? ?????????.... ???????????????.");
            homelogMap1.put("tchrName", "????????????");
            homelogMap1.put("templateUrl", "????????? URL");
            homelogMap1.put("regDttm", "2022-10-07 10:30:00");
            
            homelogList.add(homelogMap);
            homelogList.add(homelogMap1);*/
            
            data.put("hlogList", homelogList);
            
            setResult(dataKey,data);
        } else {
        	setResult(msgKey, vu.getResult());
        }
        
	    return result; 
    }
    
    @Override
    public Map getHlogThnList(Map<String,Object> paramMap) throws Exception {
        Map<String,Object> data = new LinkedHashMap<>();
        
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        getStudId(paramMap);
        
        vu.checkRequired(new String[] {"studId"}, paramMap);
        if(vu.isValid()) {
        	 /*?????? ?????? ??? ??????*/
        	
        	Calendar month = Calendar.getInstance();
        	month.add(Calendar.MONTH , 0);
            String stringYymm = new java.text.SimpleDateFormat("yyyy").format(month.getTime());
    		int yyyy = Integer.parseInt(stringYymm);
        	
        	if(paramMap.get("yyyy") != null) {
        		paramMap.put("yyyy", (!paramMap.get("yyyy").toString().equals("")) ? Integer.parseInt(paramMap.get("yyyy").toString()): yyyy);
        	} else {
        		paramMap.put("yyyy", yyyy);
        	}
        	
            data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "Homelog.selectHomelogPageCnt");
            
            int totalCnt = Integer.parseInt(data.get("totalCnt").toString());
            
            if(paramMap.get("page") != null) {
        		int pageIndex = (!paramMap.get("page").toString().equals("")) ? Integer.parseInt(paramMap.get("page").toString()): 0;
        		paramMap.put("page", (pageIndex * 10) - 10);
        		
        		data.put("currPage", (totalCnt > 0) ? (pageIndex == 0) ? 1 : pageIndex : 0);
        	} else {
        		paramMap.put("page", 0);
        		data.put("currPage", (totalCnt > 0) ? 1 : 0);
        	}
            
            /*?????? ?????? ??? ??????*/
            
            ArrayList<Map<String, Object>> homelogList = (ArrayList<Map<String, Object>>) commonMapperLrnType.getList(paramMap, "Homelog.selectHomelogThumbList");
            
        	/*data.put("totalCnt", 100);
            data.put("pageCnt", 10);
            data.put("currPage", 1);*/
            
            /*ArrayList<Map<String, Object>> homelogList = new ArrayList<>();
            
            Map<String, Object> homelogMap = new LinkedHashMap<>();
            homelogMap.put("cd", 22100001);
            homelogMap.put("grp", "????????? ???");
            homelogMap.put("grpCd", "M");
            homelogMap.put("name", "?????? ?????? ???");
            homelogMap.put("property", "?????? ????????? - ????????? ?????? ??????");
            homelogMap.put("thumbnailUrl", "????????? URL");
            homelogMap.put("regDttm", "2022-10-06 10:30:00");
            
            Map<String, Object> homelogMap1 = new LinkedHashMap<>();
            homelogMap1.put("cd", 22990001);
            homelogMap1.put("grp", "???????????? ???");
            homelogMap1.put("grpCd", "A");
            homelogMap1.put("name", "????????? - ????????????");
            homelogMap1.put("property", "?????? ????????? - ????????? ?????? ??????");
            homelogMap1.put("thumbnailUrl", "????????? URL");
            homelogMap1.put("regDttm", "2022-10-07 10:30:00");
            
            homelogList.add(homelogMap);
            homelogList.add(homelogMap1);*/
            
            data.put("hlogList", homelogList);
            
            setResult(dataKey,data);
        } else {
        	setResult(msgKey, vu.getResult());
        }
        
	    return result; 
    }
    
    @Override
    public Map getHlogDetail(Map<String,Object> paramMap) throws Exception {
        Map<String,Object> data = new LinkedHashMap<>();
        
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        getStudId(paramMap);
        
        vu.checkRequired(new String[] {"studId"}, paramMap);
        if(vu.isValid()) {
        	vu1.checkRequired(new String[] {"cd", "grpCd"}, paramMap);
        	
        	if(vu1.isValid()) {
        		/*?????? ?????? ??????*/
        		data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "Homelog.selectHomelogDetail");
        		/*?????? ?????? ??????*/
        		
        		/*?????? ?????? ??????*/
        		if(data != null) {
        			String studNm = getStudNm(paramMap);
            		
            		if(studNm != null) {
            			data.put("studName", studNm);
            		}
        		}
                /*?????? ?????? ??????*/
        		
				/*data.put("cd", 22100001);
				data.put("id", "ISE-FFC-10-0001");
				data.put("grp", "????????? ???");
				data.put("grpCd", "M");
				data.put("name", "?????? ?????? ???");
				data.put("studName", "????????????");
				data.put("property", "?????? ????????? - ????????? ?????? ??????");
				data.put("period", "2022??? 10??? 01??? ~ 2022??? 10??? 11???");
				data.put("cont", "??? ?????????.... ???????????????.");
				data.put("tchrName", "????????????");
				data.put("templateUrl", "????????? URL");
				data.put("shortUrl", "?????? URL");
				data.put("regDttm", "2022-10-06 10:30:00");*/
               
				setResult(dataKey,data);
        	} else {
        		setResult(msgKey, vu1.getResult());
        	}
        } else {
        	setResult(msgKey, vu.getResult());
        }
        
	    return result; 
    }
    
    @Override
    public Map getHlogInfo(Map<String,Object> paramMap) throws Exception {
        Map<String,Object> data = new LinkedHashMap<>();
        
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        getTchrId(paramMap);
        
        vu.checkRequired(new String[] {"tchrId"}, paramMap);
        if(vu.isValid()) {
        	vu1.checkRequired(new String[] {"cd", "grpCd"}, paramMap);
        	
        	if(vu1.isValid()) {
        		 /*?????? ?????? ??? ??????*/
            	data = (Map<String, Object>) commonMapperLrnType.get(paramMap, "Homelog.selectHomelogInfo");
                /*?????? ?????? ??? ??????*/
            	
            	if(data != null) {
            		String studNm = null;
            		
            		if(paramMap.get("studId") != null && !paramMap.get("studId").toString().equals("")) {
            			studNm = getStudNm(paramMap);
            		}
            		
            		if(studNm != null) {
            			data.put("studName", studNm);
            		}
            	}
            	
            	/*data.put("cd", 22100001);
            	data.put("grp", "????????? ???");
            	data.put("grpCd", "M");
            	data.put("memo", "?????? ??????");
            	data.put("name", "?????? ?????? ???");
                data.put("studName", "????????????");
                data.put("propertyName", "??????");
                data.put("propertyContent", "?????? ????????? - ????????? ?????? ??????");
                data.put("periodName", "?????? ??????");
                data.put("startPeriod", "2022-10-01");
                data.put("endPeriod", "2022-10-31");
                data.put("cont", "??? ?????????.... ???????????????.");
                data.put("templateCd", 1);*/
                
                setResult(dataKey,data);
        	} else {
        		setResult(msgKey, vu1.getResult());
        	}
        } else {
        	setResult(msgKey, vu.getResult());
        }
        
	    return result; 
    }
    
    @Override
    public Map getTempInfo(Map<String,Object> paramMap) throws Exception {
        Map<String,Object> data = new LinkedHashMap<>();
        
        ArrayList<Map<String, Object>> templateList = (ArrayList<Map<String, Object>>) commonMapperLrnType.getList(paramMap, "Homelog.selectTemplateList");
        
        data.put("templateList", templateList);
        
        setResult(dataKey,data);
        
	    return result; 
    }
    
    @Override
    public Map getHlogTempList(Map<String,Object> paramMap) throws Exception {
        Map<String,Object> data = new LinkedHashMap<>();
        
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        getTchrId(paramMap);
        
        vu.checkRequired(new String[] {"tchrId"}, paramMap);
        if(vu.isValid()) {
        	vu1.checkRequired(new String[] {"grpCd"}, paramMap);
        	
        	if(vu1.isValid()) {
        		 /*?????? ?????? ??? ??????*/
        		ArrayList<Map<String, Object>> templateList = (ArrayList<Map<String, Object>>) commonMapperLrnType.getList(paramMap, "Homelog.selectHomelogTemplateList");
                /*?????? ?????? ??? ??????*/
        		
            	/*Map<String, Object> templateMap = new LinkedHashMap<String, Object>();
            	Map<String, Object> templateMap1 = new LinkedHashMap<String, Object>();
            	
            	templateMap.put("cd", 22100001);
            	templateMap.put("name", "[22??? 2?????? ??????] ????????? 100%");
            	
            	templateMap1.put("cd", 22100002);
            	templateMap1.put("name", "[22??? 2?????? ??????] ????????? 80%");
            	
            	templateList.add(templateMap);
            	templateList.add(templateMap1);*/
            	
            	data.put("templateList", templateList);
                
                setResult(dataKey,data);
        	} else {
        		setResult(msgKey, vu1.getResult());
        	}
        } else {
        	setResult(msgKey, vu.getResult());
        }
        
	    return result; 
    }
    
    @Override
    public Map delHlogTemp(Map<String,Object> paramMap) throws Exception {
        Map<String,Object> data = new LinkedHashMap<>();
        
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        getStudId(paramMap);
        
        vu.checkRequired(new String[] {"studId"}, paramMap);
        if(vu.isValid()) {
        	 /*?????? ?????? ??? ??????*/
            
            /*?????? ?????? ??? ??????*/
        	
        	data.put("resultMessage", "22100001 ?????? ?????? ??????");
            
            setResult(dataKey,data);
        } else {
        	setResult(msgKey, vu.getResult());
        }
        
	    return result; 
    }
    
    @Override
    public Map regHlog(Map<String,Object> paramMap) throws Exception {
        Map<String,Object> data = new LinkedHashMap<>();
        
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        getTchrId(paramMap);
        
        vu.checkRequired(new String[] {"tchrId"}, paramMap);
        if(vu.isValid()) {
        	 /*?????? ?????? ??? ??????*/
            
            /*?????? ?????? ??? ??????*/
        	
        	Calendar month = Calendar.getInstance();
        	month.add(Calendar.MONTH , 0);
            String stringYymm = new java.text.SimpleDateFormat("yyyyMM").format(month.getTime());
    		int yymm = Integer.parseInt(stringYymm);
        	
    		String stringYymmdd = new java.text.SimpleDateFormat("yyyyMMdd").format(month.getTime());
    		int yymmdd = Integer.parseInt(stringYymmdd);
    		
    		int tchrId = Integer.parseInt(paramMap.get("tchrId").toString());
    		
    		paramMap.put("yymm", yymm);
    		paramMap.put("yymmdd", yymmdd);
        	
    		int maxCd = 0;
    		String grpCd = null;
    		
    		if(paramMap.get("grpCd") != null) {
    			if(paramMap.get("grpCd").toString().equals("A")) {
    				grpCd = paramMap.get("grpCd").toString();
    				paramMap.put("tchrId", 0);
    			}
    		}
    		
    		if(paramMap.get("cd") != null) {
    			maxCd = Integer.parseInt(paramMap.get("cd").toString());
    		} else {
    			//Map<String,Object> tchrMaxCdData = (Map<String, Object>) commonMapperLrnType.get(paramMap, "Homelog.spTchrManualHomelogMaxCd");
    			Map<String,Object> tchrMaxCdData = (Map<String, Object>) commonMapperLrnType.get(paramMap, "Homelog.selectHomelogMaxCd");
    			
    			int cdCnt = Integer.parseInt(tchrMaxCdData.get("cnt").toString());
    			
    			if(cdCnt == 0) { 
    				Calendar cdMonth = Calendar.getInstance();
    				cdMonth.add(Calendar.MONTH , 0);
    				
    				String currYymm = null;
    				if(grpCd != null && grpCd.equals("A")) {
    					currYymm = new java.text.SimpleDateFormat("yyyyMM").format(cdMonth.getTime());
    				} else {
    					currYymm = new java.text.SimpleDateFormat("yyMM").format(cdMonth.getTime());
    				}
    				currYymm = currYymm + "0001";
    				
    				maxCd = Integer.parseInt(currYymm);
    				
    			} else {
    				maxCd = Integer.parseInt(tchrMaxCdData.get("maxCd").toString()) + 1;
    			}
    		}
        	
        	paramMap.put("cd", maxCd);
    		
    		if(paramMap.get("startPeriod") != null) {
        		paramMap.put("startPeriod", Integer.parseInt(paramMap.get("startPeriod").toString().replace("-", "")));
        	}
        	
        	if(paramMap.get("endPeriod") != null) {
        		paramMap.put("endPeriod", Integer.parseInt(paramMap.get("endPeriod").toString().replace("-", "")));
        	}
    		
    		int row = 0;
        	
    		if(grpCd != null && grpCd.equals("A")) {
    			try {
            		row = commonMapperLrnType.insert(paramMap,"Homelog.regHomelog");
            		//row = commonMapperLrnType.insert(paramMap,"Homelog.spRegTchrManualHomelog");
            	} catch (Exception e) {
            		System.out.println("Homelog.spRegTchrManualHomelog Insert Error");
    			}
    		} else {
    			try {
            		row = commonMapperLrnType.insert(paramMap,"Homelog.regHomelog");
            		//row = commonMapperLrnType.insert(paramMap,"Homelog.spRegTchrManualHomelog");
            	} catch (Exception e) {
            		System.out.println("Homelog.spRegTchrManualHomelog Insert Error");
    			}
    		}
        	
        	if(row > 0) {
        		data.put("resultMessage", maxCd + " ?????? ??????");
        	} else {
        		data.put("resultMessage", maxCd + " ?????? ?????? ??????");
        	}
    		
            setResult(dataKey,data);
        } else {
        	setResult(msgKey, vu.getResult());
        }
        
	    return result; 
    }
    
    @Override
    public Map setHlog(Map<String,Object> paramMap) throws Exception {
        Map<String,Object> data = new LinkedHashMap<>();
        
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        getTchrId(paramMap);
        
        vu.checkRequired(new String[] {"tchrId"}, paramMap);
        if(vu.isValid()) {
        	ArrayList<Map<String, Object>> studList = new ArrayList<>();
        	ArrayList<Integer> studIdList = new ArrayList<>();
        	ArrayList<Integer> cdList = new ArrayList<>();
        	
        	int tchrId = Integer.parseInt(paramMap.get("tchrId").toString());
        	String cd = paramMap.get("cd").toString();
        	
        	if(paramMap.get("studIds").getClass().getSimpleName().equals("Integer")) {
        		int studId = Integer.parseInt(paramMap.get("studIds").toString());
        		studIdList.add(studId);
        	} else {
        		studIdList = (ArrayList<Integer>) paramMap.get("studIds");
        	}
        	
        	if(paramMap.get("cd").getClass().getSimpleName().equals("Integer")) {
        		int cdData = Integer.parseInt(paramMap.get("cd").toString());
        		cdList.add(cdData);
        	} else {
        		cdList = (ArrayList<Integer>) paramMap.get("cd");
        	}
        	
        	String today = LocalDate.now(ZoneId.of("Asia/Seoul")).toString();
        	String todayDate = today.replace("-", "");
        	
        	Calendar month = Calendar.getInstance();
        	month.add(Calendar.MONTH , 0);
            String stringYymm = new java.text.SimpleDateFormat("yyyy").format(month.getTime());
    		int yyyy = Integer.parseInt(stringYymm);
        	
        	for(int studIds : studIdList) {
        		for(int cds : cdList) {
        			Map<String,Object> insertMap = new LinkedHashMap<String, Object>();
            		
        			String grpCd = (String.valueOf(cds).length() > 8) ? "A" : "M";
        			
            		insertMap.put("studId", studIds);
            		insertMap.put("dt", Integer.parseInt(todayDate));
            		insertMap.put("cd", cds);
            		insertMap.put("grpCd", grpCd);
            		insertMap.put("yyyy", yyyy);
            		insertMap.put("status", 1);
            		insertMap.put("prstDt", Integer.parseInt(todayDate));
            		insertMap.put("tchrId", tchrId);
            		
            		studList.add(insertMap);
        		}
        	}
        	
        	Map<String,Object> insertParamsMap = new LinkedHashMap<String, Object>();
        	
        	int row = 0;
        	
        	insertParamsMap.put("list", studList);
        	
        	try {
        		row = commonMapperLrnType.insert(insertParamsMap,"Homelog.setHomelog");
        	} catch (Exception e) {
        		System.out.println("Homelog.setHomelog Insert Error");
			}
        	
        	if(row > 0) {
        		data.put("resultMessage", row + "??? ?????? ??????");
        	} else {
        		data.put("resultMessage", "?????? ?????? ??????");
        	}
            
            setResult(dataKey,data);
        } else {
        	setResult(msgKey, vu.getResult());
        }
        
	    return result; 
    }
    
    @Override
    public Map delHlog(Map<String,Object> paramMap) throws Exception {
        Map<String,Object> data = new LinkedHashMap<>();
        
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        getTchrId(paramMap);
        
        vu.checkRequired(new String[] {"tchrId"}, paramMap);
        if(vu.isValid()) {
        	
        	String cd = paramMap.get("cd").toString();
        	
        	int row = 0;
        	
        	paramMap.put("cd", Integer.parseInt(cd));
        	
        	try {
        		row = commonMapperLrnType.update(paramMap,"Homelog.delHomelog");
        		//row = commonMapperLrnType.update(paramMap,"Homelog.spDelTchrManualHomelog");
        	} catch (Exception e) {
        		System.out.println("Homelog.spRegTchrManualHomelog Update Error");
			}
        	
        	if(row > 0) {
        		data.put("resultMessage", cd + " ?????? ??????");
        	} else {
        		data.put("resultMessage", cd + " ?????? ?????? ??????");
        	}
            
            setResult(dataKey,data);
        } else {
        	setResult(msgKey, vu.getResult());
        }
        
	    return result; 
    }

    /**
     * ?????????????????? ???????????? ??????(?????????,????????? object??? ????????? result)??????.
     * @param key
     * @param data
     */
    private void setResult(String key, Object data) {
    	LinkedHashMap message = new LinkedHashMap();
        result = null;
        result = new LinkedHashMap();

        if(data == null
                || (data instanceof List && ((List)data).size() == 0)
                || (data instanceof Map && ((Map)data).isEmpty())) {
//            throw new NoDataException(new Object[] {key,"null",ValidationCode.NO_DATA});

            message.put("resultCode", ValidationCode.NO_DATA.getCode());
            message.put("result", ValidationCode.NO_DATA.getMessage());
            result.put(msgKey, message);
        }
        else if(data instanceof Map && ((Map)data).containsKey("error")) {	// error ?????? ???????????? ?????? ??????
            result.put(msgKey, data);
        } else if(data instanceof Map && ((Map)data).containsKey("resultCode")) {	// resultCode ?????? ???????????? ?????? ??????
        	result.put(msgKey, data);
        } else {
            message.put("resultCode", ValidationCode.SUCCESS.getCode());
            result.put(msgKey, message);
            result.put(dataKey, data);
        }
    }
    
    /***
     * ?????????????????? studId ??????
     * @param params
     * @throws Exception
     */
	private void getStudId(Map<String, Object> params) throws Exception {
		decodeResult = new LinkedHashMap<String, Object>();
		if(!params.containsKey("studId") && params.containsKey("p")) {
			//?????????
			try {
				CipherUtil cp = CipherUtil.getInstance();
				String decodedStr = cp.AES_Decode(params.get("p").toString());
				
				int studId = (!decodedStr.contains("&")) ? Integer.parseInt(decodedStr) : Integer.parseInt(decodedStr.split("&")[1]) ;
				
				if(decodedStr != null) {
					//DB params
					params.put("studId",studId);
				}
			} catch (Exception e) {
				LOGGER.debug("p Parameter Incorrect");
				
				//p??? ????????? ??????
				decodeResult.put("resultCode", ValidationCode.REQUIRED.getCode());
				decodeResult.put("result", "p : Incorrect");
			}
		} else if(params.containsKey("studId")) {
			int studId = Integer.parseInt(params.get("studId").toString());
			params.put("studId",studId);
		}
	}
	
	/***
     * ?????????????????? tchrId ??????
     * @param params
     * @throws Exception
     */
	private void getTchrId(Map<String, Object> params) throws Exception {
		decodeResult = new LinkedHashMap<String, Object>();
		if(!params.containsKey("tchrId") && params.containsKey("p")) {
			//?????????
			try {
				CipherUtil cp = CipherUtil.getInstance();
				String decodedStr = cp.AES_Decode(params.get("p").toString());
				
				int studId = (!decodedStr.contains("&")) ? Integer.parseInt(decodedStr) : Integer.parseInt(decodedStr.split("&")[0]) ;
				
				if(decodedStr != null) {
					//DB params
					params.put("tchrId",studId);
				}
			} catch (Exception e) {
				LOGGER.debug("p Parameter Incorrect");
				
				//p??? ????????? ??????
				decodeResult.put("resultCode", ValidationCode.REQUIRED.getCode());
				decodeResult.put("result", "p : Incorrect");
			}
		} else if(params.containsKey("tchrId")) {
			int tchrId = Integer.parseInt(params.get("tchrId").toString());
			params.put("tchrId",tchrId);
		}
	}
	
	private String encodeStudId(String studId) throws Exception {
		String encodeStudId = null;
		
		CipherUtil cps = CipherUtil.getInstance();
		encodeStudId = cps.AES_Encode(studId);
		
		return encodeStudId;
	}
	
	/*?????? ?????? ??????*/
	private String getStudNm(Map<String, Object> params) throws Exception{
		String studName = null;
		
		Map<String,Object> studInfoParamMap = new HashMap<>();
		String p = encodeStudId("0&"+params.get("studId"));
		
		studInfoParamMap.put("p", p);
		studInfoParamMap.put("apiName", "aiReport.");
		
		LinkedHashMap<String,String> studInfo = new LinkedHashMap<>();
		Map<String,Object> studInfoMap = (Map<String, Object>) externalAPIservice.callExternalAPI(studInfoParamMap).get("data");
		
		if(studInfoMap != null) {
			studName = (studInfoMap.get("name") != null) ? studInfoMap.get("name").toString() : null;
		}
		
		return studName;
	}
}
