package com.iscreamedu.analytics.homelearn.api.hamsTutor.service.impl;

import com.iscreamedu.analytics.homelearn.api.common.exception.NoDataException;
import com.iscreamedu.analytics.homelearn.api.common.mapper.CommonMapperTutor;
import com.iscreamedu.analytics.homelearn.api.common.mapper.CommonMapperTutorWr;
import com.iscreamedu.analytics.homelearn.api.common.security.CipherUtil;
import com.iscreamedu.analytics.homelearn.api.common.service.ExternalAPIService;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationCode;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtilTutor;
import com.iscreamedu.analytics.homelearn.api.hamsTutor.service.CommonLrnMtService;
import com.iscreamedu.analytics.homelearn.api.hamsTutor.service.HamsTutorVrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.Map.Entry;

@Service
public class HamsTutorVrServiceImpl implements HamsTutorVrService {
	
	@Autowired
	CommonMapperTutor commonMapperTutor;
	
	@Autowired
	CommonMapperTutorWr commonMapperTutorWr;
	
	@Autowired
	ExternalAPIService externalAPIservice;
	
	@Autowired
    CommonLrnMtService commonLrnMtService;

    private static final Logger LOGGER = LoggerFactory.getLogger(HamsTutorVrServiceImpl.class);

    private LinkedHashMap<String, Object> result;
    private String msgKey = "msg";
    private String dataKey = "data";
    
    // for QA
    private LinkedHashMap<String, Object> apiResult;


    @Override
    public Map getVisionReportPublishedInfo(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequired(paramMap,true);

        //DB 조회
        ArrayList<Map<String,Object>> visionReportPublishedInfo = (ArrayList<Map<String,Object>>) commonMapperTutor.getList(paramMap, "HamsTutorVr.selectVisionReportPublishedInfo");

        data.put("visionReportPublishedInfo",visionReportPublishedInfo);
        setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getVisionBasicInfo(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequired(paramMap,false);

        //DB 조회
        LinkedHashMap<String,Object> visionBasicInfo = (LinkedHashMap<String, Object>) commonMapperTutor.get(paramMap, "HamsTutorVr.selectVisionBasicInfo");
        LinkedHashMap<String,Object> msgInfo = (LinkedHashMap<String, Object>) commonMapperTutor.get(paramMap, "HamsTutorVr.selectVisionBasicMsgCondition");
        ArrayList<Map<String,Object>> msg = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorVr.selectVisionBasicMsgInfo");
        
        if(visionBasicInfo != null) {
        	if(msg != null && msg.size() > 0 && msg.get(0) != null) {
        		Map<String, Object> msgMap = msg.get(0);
        		
        		for(Entry<String, Object> item : msgMap.entrySet()) {
        			String key = item.getKey();
        			String value = (item.getValue() != null) ? item.getValue().toString() : null;
        			if(value != null) {
        				if(value.indexOf("studNm") > 0) {
        					value = value.replace("{studNm}", msgInfo.get("studNm").toString());
        				}
        				
        				if(value.indexOf("exRt") > 0) {
        					value = value.replace("{exRt}", msgInfo.get("exRt").toString());
        				}
        				
        				if(value.indexOf("attRt") > 0) {
        					value = value.replace("{attRt}", msgInfo.get("attRt").toString());
        				}
        				
        				if(value.indexOf("subjCd") > 0) {
        					value = value.replace("{subjCd}", msgInfo.get("subjCd").toString());
        				}
        				
        				if(value.indexOf("maxCrtRt") > 0) {
        					value = value.replace("{maxCrtRt}", msgInfo.get("maxCrtRt").toString());
        				}
        				
        				if(value.indexOf("minCrtRt") > 0) {
        					value = value.replace("{minCrtRt}", msgInfo.get("minCrtRt").toString());
        				}
        				
        				if(value.indexOf("spSubjCd") > 0) {
        					value = value.replace("{spSubjCd}", msgInfo.get("spSubjCd").toString());
        				}
        				
        				if(value.indexOf("spCnt") > 0) {
        					value = value.replace("{spCnt}", msgInfo.get("spCnt").toString());
        				}
        				
        				if(value.indexOf("fnshSubjCd") > 0) {
        					value = value.replace("{fnshSubjCd}", msgInfo.get("fnshSubjCd").toString());
        				}
        				
        				msgMap.put(key, value);
        			}
        		}
        		
        		msg.clear();
        		msg.add(msgMap);
        		
        		visionBasicInfo.put("msg",msg.get(0));
        	}else {
        		visionBasicInfo.put("msg",null);
        	}
        }
        
        data.put("visionBasicInfo",visionBasicInfo);
        setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getVisionGrowthStt(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequired(paramMap,false);

        //DB 조회
        ArrayList<Map<String,Object>> visionGrowthStt = new ArrayList<>();
        ArrayList<Map<String,Object>> growStt = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorVr.selectVisionGrowthStt");
        ArrayList<Map<String,Object>> subjCrtRt = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorVr.selectVisionGrowthSubjStt");
        
        for(Map<String, Object> item : growStt) {
        	ArrayList<Map<String,Object>> subjCrtRtList = new ArrayList<>();
        	subjCrtRtList.clear();
        	for(Map<String, Object> subj : subjCrtRt) {
        		if(item.get("yymm").equals(subj.get("yymm"))) {
        			//subj.remove("yymm");
        			//subjCrtRtList.add(subj);
        			item.put(subj.get("subjCd").toString(), subj.get("crtRt"));
        		}
        	}
        	
        	//item.put("subjCrtRt", subjCrtRtList);
        }
        
        if(growStt.size() > 0) {
        	data.put("visionGrowthStt",growStt);
        }else {
        	data.put("visionGrowthStt",null);
        }
        
        setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getVisionExamStt(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequired(paramMap,false);

        //DB 조회
        LinkedHashMap<String,Integer> visionExamStt = (LinkedHashMap<String, Integer>) commonMapperTutor.get(paramMap, "HamsTutorVr.selectVisionExamStt");

        
        if(visionExamStt == null) {
        	LinkedHashMap<String, Integer> visionExamSttMap = new LinkedHashMap<>();
        	
        	visionExamSttMap.put("ansQuesCnt", null);
        	visionExamSttMap.put("crtQuesCnt", null);
        	visionExamSttMap.put("incrtNtCnt", null);
        	visionExamSttMap.put("incrtNtFnshCnt", null);
        	
        	data.put("visionExamStt",visionExamSttMap);
        }else {
        	data.put("visionExamStt",visionExamStt);
        }
        
        setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getVisionExamChapterStt(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequired(paramMap,false);
        
        //DB 조회
        LinkedHashMap<String,Object> visionExamChapterStt = (LinkedHashMap<String, Object>) commonMapperTutor.get(paramMap, "HamsTutorVr.selectVisionExamChapterStt");
        ArrayList<Map<String,Object>> subjList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorVr.selectVisionExamChapterSubjList");
        ArrayList<Map<String,Object>> termList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorVr.selectVisionExamChapterTermList");
        
        LinkedHashMap<String,Object> chapter = new LinkedHashMap<>();
        
        if(visionExamChapterStt != null) {
        	ArrayList paramsSubjList = new ArrayList();
        	ArrayList subjNmList = new ArrayList();
        	if(subjList.size() > 0) {
        		for(Map<String, Object> subjItem : subjList) {
        			paramsSubjList.add(subjItem.get("subjCd"));
        			subjNmList.add(subjItem.get("subjCd"));
        		}
        	}else {
        		paramsSubjList = null;
        	}
        	
        	paramMap.put("subjs", paramsSubjList);
        	
        	ArrayList paramTermList = new ArrayList();
        	if(termList.size() > 0) {
        		for(Map<String, Object> termItem : termList) {
        			paramTermList.add(termItem.get("term"));
        		}
        	}else {
        		paramTermList = null;
        	}
        	
        	paramMap.put("terms", paramTermList);
        	
        	ArrayList<Map<String,Object>> chapterInfoList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorVr.selectVisionExamChapterNmList");
        	ArrayList<Map<String,Object>> visionExamChapterList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorVr.selectVisionExamChapterList");
        	
        	ArrayList<Map<String,Object>> chapterList = new ArrayList<>();
        	
        	
        	for(Map<String, Object> item : chapterInfoList) {
        		LinkedHashMap<String,Object> chapterListMap = new LinkedHashMap<>();
        		ArrayList chapternmList = new ArrayList();
        		ArrayList chapterScoreList = new ArrayList();
        		ArrayList chapterCdList = new ArrayList();
        		
        		for(Map<String, Object> chapterItem : visionExamChapterList) {
        			if(item.get("unitNo").toString().equals(chapterItem.get("unitNo").toString()) && item.get("term").toString().equals(chapterItem.get("term").toString())) {
        				chapternmList.add(chapterItem.get("unitNm").toString());
        				chapterScoreList.add(Integer.valueOf(chapterItem.get("score").toString()));
        				chapterCdList.add(chapterItem.get("unitCd"));
        			}
        		}
        		
        		if(chapternmList.size() != subjNmList.size()) {
        			int listDiff = subjNmList.size() - chapternmList.size();
        			
        			for(int i = 0; i < listDiff; i++) {
        				chapternmList.add("");
        			}
        		}
        		
        		if(chapterScoreList.size() != subjNmList.size()) {
        			int listDiff = subjNmList.size() - chapterScoreList.size();
        			
        			for(int i = 0; i < listDiff; i++) {
        				chapterScoreList.add("");
        			}
        		}
        		
        		if(chapterCdList.size() != subjNmList.size()) {
        			int listDiff = subjNmList.size() - chapterCdList.size();
        			
        			for(int i = 0; i < listDiff; i++) {
        				chapterCdList.add("");
        			}
        		}
        		
        		chapterListMap.put("grade", item.get("grade"));
        		chapterListMap.put("term", item.get("term"));
        		chapterListMap.put("chapter", Integer.valueOf(item.get("unitNo").toString()));
        		chapterListMap.put("chapterNm", chapternmList);
        		chapterListMap.put("understandingLv", chapterScoreList);
        		chapterListMap.put("chapterCd", chapterCdList);
        		
        		chapterList.add(chapterListMap);
        	}
        	
        	chapter.put("subjCd",subjNmList);
        	chapter.put("list",chapterList);
        	visionExamChapterStt.put("chapter",chapter);
        	
        }
        
        data.put("visionExamChapterStt",visionExamChapterStt);

        setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getVisionExamChapterLrn(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequired(paramMap,false);

        //DB 조회
        LinkedHashMap<String,Object> visionExamChapterLrn = (LinkedHashMap<String, Object>) commonMapperTutor.get(paramMap, "HamsTutorVr.selectVisionExamChapterLrn");
        LinkedHashMap<String,Object> visionExamChapter = new LinkedHashMap<>();
        LinkedHashMap<String,Object> priorLrn = new LinkedHashMap<>();
        LinkedHashMap<String,Object> currentLrn = new LinkedHashMap<>();
        
        ArrayList followUpLrnList = new ArrayList();
        ArrayList supplementaryLrnList = new ArrayList();
        
    	if(visionExamChapterLrn != null) {
    		
    		currentLrn.put("chapterNm",visionExamChapterLrn.get("unitInfo"));
    		currentLrn.put("examCrtRt",visionExamChapterLrn.get("crtRt"));
    		currentLrn.put("examDt",visionExamChapterLrn.get("dt"));
    		
    		priorLrn.put("chapterNm",visionExamChapterLrn.get("preUnitInfo"));
            priorLrn.put("examCrtRt",visionExamChapterLrn.get("preCrtRt"));
            priorLrn.put("examDt",visionExamChapterLrn.get("preDt"));
            
            if(visionExamChapterLrn.get("subUnitInfo") != null) {
            	if(visionExamChapterLrn.get("subUnitInfo").toString().contains(",")) {
            		String followInfo = visionExamChapterLrn.get("subUnitInfo").toString();
            		List<String> followList = Arrays.asList(followInfo.split(","));
            		
            		for(int i = 0; i < followList.size(); i++) {
            			followUpLrnList.add(followList.get(i));
            		}
            		
            	}else {
            		followUpLrnList.add(visionExamChapterLrn.get("subUnitInfo"));
            	}
            }
            
            
        	if(visionExamChapterLrn.get("spUnitInfo").toString().contains(",")) {
        		String spInfo = visionExamChapterLrn.get("spUnitInfo").toString();
        		List<String> spList = Arrays.asList(spInfo.split(","));
        		
        		for(int i = 0; i < spList.size(); i++) {
        			supplementaryLrnList.add(spList.get(i));
            	}
        		
        		
            }else {
            	supplementaryLrnList.add(visionExamChapterLrn.get("spUnitInfo"));
            }
        	
    	}

    	visionExamChapter.put("priorLrn",priorLrn);
    	visionExamChapter.put("currentLrn",currentLrn);
    	visionExamChapter.put("followUpLrn",followUpLrnList);
    	visionExamChapter.put("supplementaryLrn",supplementaryLrnList);
    	
    	data.put("visionExamChapterLrn",visionExamChapter);
    	setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getVisionExamFieldStt(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequired(paramMap,false);

        //DB 조회
        ArrayList<Map<String,Object>> visionExamFieldStt = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorVr.selectVisionExamFieldStt");
        ArrayList emptyList = new ArrayList<>();
        
        for(Map<String, Object> item : visionExamFieldStt) {
        	
        	for(Entry<String, Object> mapItem : item.entrySet()) {
        		String key = mapItem.getKey();
        		String value = (mapItem.getValue() != null) ? mapItem.getValue().toString() : null;
        		if(!"subjCd".equals(key) && value == null) {
        			mapItem.setValue(emptyList);
        		}
        		
        		if(!"subjCd".equals(key) && value != null) {
        			if(key.contains("CrtRt")) {
        				int[] crtRtList = Arrays.stream(value.split(",")).mapToInt(Integer::parseInt).toArray();
        				mapItem.setValue(crtRtList);
        			}else {
        				mapItem.setValue(value.split(","));
        			}
        		}
        	}
        }
        
        data.put("visionExamFieldStt",visionExamFieldStt);
        setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getVisionExamList(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequiredWithSubjCd(paramMap);
        
        int term =  Integer.valueOf(paramMap.get("term").toString());
        String yymm =  paramMap.get("yymm").toString().substring(0, 4);
        ArrayList yymmList = new ArrayList();
        if(term == 1) {
        	for(int i = 1; i < 8; i++) {
        		yymmList.add(Integer.valueOf(yymm+"0"+i));
        	}
        	
        	paramMap.put("yymms", yymmList);
        }else if(term == 2) {
        	for(int i = 8; i < 13; i++) {
        		yymmList.add(Integer.valueOf((i>9)?yymm+i : yymm+"0"+i));
        	}
        	
        	paramMap.put("yymms", yymmList);
        }else if(term == 0) {
        	ArrayList<Map<String,Object>> yymmwkList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorVr.selectYymmwk");
        	
        	for(Map<String, Object> item : yymmwkList) {
        		item.put("subjCd", paramMap.get("subjCd"));
        	}
        	
        	paramMap.put("yymms", yymmwkList);
        }
        
    	int idx = 10;
		
		if(paramMap.get("startIdx") != null && !"".equals(paramMap.get("startIdx"))) {
			if(paramMap.get("pageSize") != null && !"".equals(paramMap.get("pageSize"))) {
				idx = (Integer.parseInt(paramMap.get("startIdx").toString()) - 1) * Integer.parseInt(paramMap.get("pageSize").toString());
				paramMap.put("idx", idx);
			} else {
				idx = (Integer.parseInt(paramMap.get("startIdx").toString()) - 1) * 10;
				paramMap.put("idx", idx);
			}
        }
        
        //DB 조회
		LinkedHashMap<String,Object> visionExamData = new LinkedHashMap<>();
        LinkedHashMap<String,Object> visionExamMap = (LinkedHashMap<String, Object>) commonMapperTutor.get(paramMap, "HamsTutorVr.selectVisionExamCount");
        ArrayList<Map<String,Object>> visionExamList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorVr.selectVisionExamList");
        
        int totalCount = 0;
        if(visionExamMap.get("totalCount") != null) {
        	totalCount = Integer.parseInt(visionExamMap.get("totalCount").toString());
        }
        
        visionExamData.put("totalCnt", totalCount);
        visionExamData.put("list",visionExamList);
        
        data.put("visionExamList",visionExamData);
        setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getVisionAttPtn(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequired(paramMap,false);
        
        if(Integer.valueOf(paramMap.get("term").toString()) == 0) {
        	ArrayList<Map<String,Object>> yymmwkList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorVr.selectAttYymmwk"); 
        	
        	paramMap.put("yymmwk", yymmwkList);
        	
        }else {
        	LinkedHashMap<String,Object> dtMap = (LinkedHashMap<String, Object>) commonMapperTutor.get(paramMap, "HamsTutorVr.selectAttDt");
        	
        	paramMap.put("startDt", dtMap.get("startDt"));
        	paramMap.put("endDt", dtMap.get("endDt"));
        }

        //DB 조회
        LinkedHashMap<String,Object> visionAttPtn = (LinkedHashMap<String, Object>) commonMapperTutor.get(paramMap, "HamsTutorVr.selectVisionAttStt");
        ArrayList<Map<String,Object>> loginMap = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorVr.selectVisionAttLog");
        
        for(Map<String, Object> item : loginMap) {
        	if(item.get("loginTm") == null) {
        		item.put("loginTm", null);
        	}
        }
        
        visionAttPtn.put("attPtnChart",loginMap);

        data.put("visionAttPtn",visionAttPtn);
        setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getVisionLrnPtn(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequired(paramMap,false);

        //DB 조회
        LinkedHashMap<String,Object> visionLrnPtn = (LinkedHashMap<String, Object>) commonMapperTutor.get(paramMap, "HamsTutorVr.selectVisionLrnTm");
        ArrayList<Map<String,Object>> subjLrnTmData = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorVr.selectVisionSubjLrnTm");

        ArrayList<Map<String,Object>> subjLrnTm = new ArrayList<>();
        if(visionLrnPtn != null) {
        	if(subjLrnTmData != null) {
        		ArrayList subjNmList = new ArrayList<>();
        		
        		for(Map<String, Object> item : subjLrnTmData) {
        			if(!subjNmList.contains(item.get("subjCd"))) {
        				subjNmList.add(item.get("subjCd").toString());
        			}
        		}
        		
        		for(Object subjNmItem : subjNmList) {
        			ArrayList<Map<String, Object>> subjLrnList = new ArrayList<>();
        			for(Map<String, Object> item : subjLrnTmData) {
        				if(subjNmItem.toString().equals(item.get("subjCd"))) {
        					subjLrnList.add(createSubjLrnTmMap(item.get("subSubjCd").toString(), Integer.valueOf(item.get("lrnSec").toString())));
        				}
        			}
        			
        			subjLrnTm.add(createSubjLrnTm(subjNmItem.toString(), subjLrnList));
        		}
        		
        		visionLrnPtn.put("subjLrnTm", subjLrnTm);
        	}
        }
        
        data.put("visionLrnPtn",visionLrnPtn);
        setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getVisionPrintBasicInfo(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequired(paramMap,false);

        //DB 조회
        LinkedHashMap<String,Object> visionPrintBasicInfo = (LinkedHashMap<String, Object>) commonMapperTutor.get(paramMap, "HamsTutorVr.selectVisionPrintBasicInfo");
        LinkedHashMap<String,Object> msgInfo = (LinkedHashMap<String, Object>) commonMapperTutor.get(paramMap, "HamsTutorVr.selectVisionBasicMsgCondition");
        ArrayList<Map<String,Object>> msg = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorVr.selectVisionBasicMsgInfo");
        ArrayList<Map<String,Object>> consultinMsg = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorVr.selectVisionPrintBasicConsultingMsg");
        LinkedHashMap<String,Object> cpMsgInfo = (LinkedHashMap<String, Object>) commonMapperTutor.get(paramMap, "HamsTutorVr.selectVisionBasicConsultingMsgCondition");
        
        ArrayList positiveMsgList = new ArrayList();
        ArrayList negativeMsgList = new ArrayList();
        if(visionPrintBasicInfo != null) {
        	if(msg != null && msg.size() > 0 && msg.get(0) != null) {
        		Map<String, Object> msgMap = msg.get(0);
        		
        		for(Entry<String, Object> item : msgMap.entrySet()) {
        			String key = item.getKey();
        			String value = (item.getValue() != null) ? item.getValue().toString() : null;
        			if(value != null) {
        				if(value.indexOf("studNm") > 0) {
        					value = value.replace("{studNm}", msgInfo.get("studNm").toString());
        				}
        				
        				if(value.indexOf("exRt") > 0) {
        					value = value.replace("{exRt}", msgInfo.get("exRt").toString());
        				}
        				
        				if(value.indexOf("attRt") > 0) {
        					value = value.replace("{attRt}", msgInfo.get("attRt").toString());
        				}
        				
        				if(value.indexOf("subjCd") > 0) {
        					value = value.replace("{subjCd}", msgInfo.get("subjCd").toString());
        				}
        				
        				if(value.indexOf("maxCrtRt") > 0) {
        					value = value.replace("{maxCrtRt}", msgInfo.get("maxCrtRt").toString());
        				}
        				
        				if(value.indexOf("minCrtRt") > 0) {
        					value = value.replace("{minCrtRt}", msgInfo.get("minCrtRt").toString());
        				}
        				
        				if(value.indexOf("spSubjCd") > 0) {
        					value = value.replace("{spSubjCd}", msgInfo.get("spSubjCd").toString());
        				}
        				
        				if(value.indexOf("spCnt") > 0) {
        					value = value.replace("{spCnt}", msgInfo.get("spCnt").toString());
        				}
        				
        				if(value.indexOf("fnshSubjCd") > 0) {
        					value = value.replace("{fnshSubjCd}", msgInfo.get("fnshSubjCd").toString());
        				}
        				
        				msgMap.put(key, value);
        			}
        		}
        		
        		msg.clear();
        		msg.add(msgMap);
        	}
        	
        	if(consultinMsg !=null && consultinMsg.size() > 0) {
        		for(Map<String, Object> msgItem : consultinMsg) {
        			if("G".equals(msgItem.get("msgType"))) {
        				if("CPG0003".equals(msgItem.get("msgCd")) || "CPG0004".equals(msgItem.get("msgCd")) || "CPG0005".equals(msgItem.get("msgCd")) || "CPG0006".equals(msgItem.get("msgCd"))) {
        					msgItem.put("msg", msgItem.get("msg").toString()
        							.replace("{a}", cpMsgInfo.get("explCnt").toString())
        							.replace("{b}", cpMsgInfo.get("psExplCnt").toString())
        							.replace("{c}", cpMsgInfo.get("crtRt").toString()));
        				}else if("CPG0010".equals(msgItem.get("msgCd"))) {
        					msgItem.put("msg", msgItem.get("msg").toString()
        							.replace("{a}", cpMsgInfo.get("aLrnExCnt").toString())
        							.replace("{b}", cpMsgInfo.get("aLrnSubSubjCd").toString())
                					.replace("{c}", cpMsgInfo.get("aLrnSubjCd").toString())
        							);
        				}
        				
        				Boolean active = ("Y".equals(msgItem.get("active").toString())) ? true : false;
        				
        				positiveMsgList.add(createConsultingMsgMap(active, msgItem.get("msg").toString(), msgItem.get("msgCd").toString()));
        			}else {
        				if("CPB0004".equals(msgItem.get("msgCd")) ) {
        					msgItem.put("msg", msgItem.get("msg").toString()
        							.replace("{b}", cpMsgInfo.get("exRt").toString()));
        				}else if("CPB0006".equals(msgItem.get("msgCd"))) {
        					msgItem.put("msg", msgItem.get("msg").toString()
        							.replace("{b}", cpMsgInfo.get("nLrnExCnt").toString()));
        				}else if("CPB0007".equals(msgItem.get("msgCd"))) {
        					msgItem.put("msg", msgItem.get("msg").toString()
        							.replace("{b}", cpMsgInfo.get("crtRt").toString()));
        				}else if("CPB0008".equals(msgItem.get("msgCd"))) {
        					msgItem.put("msg", msgItem.get("msg").toString()
        							.replace("{b}", cpMsgInfo.get("explCnt").toString()));
        				}else if("CPB0009".equals(msgItem.get("msgCd"))) {
        					msgItem.put("msg", msgItem.get("msg").toString()
        							.replace("{c}", cpMsgInfo.get("incrtNtCnt").toString()));
        				}else if("CPB0010".equals(msgItem.get("msgCd"))) {
        					msgItem.put("msg", msgItem.get("msg").toString()
        							.replace("{c}", cpMsgInfo.get("skpQuesCnt").toString()));
        				}else if("CPB0011".equals(msgItem.get("msgCd"))) {
        					msgItem.put("msg", msgItem.get("msg").toString()
        							.replace("{c+d}", cpMsgInfo.get("curQuesCnt").toString()));
        				}else if("CPB0012".equals(msgItem.get("msgCd"))) {
        					msgItem.put("msg", msgItem.get("msg").toString()
        							.replace("{c}", cpMsgInfo.get("gucQuesCnt").toString()));
        				}else if("CPB0013".equals(msgItem.get("msgCd"))) {
        					msgItem.put("msg", msgItem.get("msg").toString()
        							.replace("{b}", cpMsgInfo.get("over25MinLrnCnt").toString()));
        				}else if("CPB0014".equals(msgItem.get("msgCd"))) {
        					msgItem.put("msg", msgItem.get("msg").toString()
        							.replace("{b}", cpMsgInfo.get("below5MinLrnCnt").toString()));
        				}
        				
        				Boolean active = ("Y".equals(msgItem.get("active").toString())) ? true : false;
        				
        				negativeMsgList.add(createConsultingMsgMap(active, msgItem.get("msg").toString(), msgItem.get("msgCd").toString()));
        			}
        		}
        	}
        	
        	String recommendJob = null;
        	Map<String, Object> apiMap = new HashMap<>();
        	Map<String, Object> paramData = new HashMap<>();
        	
        	paramData.put("p", paramMap.get("p"));
        	paramData.put("apiName", "intel-inspecion-strength");
        	
        	apiMap =  (Map<String, Object>) externalAPIservice.callExternalAPI(paramData).get("data");
        	
        	String intelNm = null;
        	List <Map> intelNmList = new ArrayList<>();
        	if(apiMap != null) {
        		intelNmList = (List<Map>) apiMap.get("items");
        		intelNm = intelNmList.get(0).get("intel_nm").toString().replace("지능", "");
        	}
        	
        	if(apiMap != null) {
        		String intelStrength = intelNm;
        		String maxSubjCd = null;
        		if(msgInfo.get("maxLrnSubjCd")!= null) {
        			
        			if("N02".equals(msgInfo.get("maxLrnSubjCd").toString())) {
        				maxSubjCd = "E01";
        			}else {
        				maxSubjCd = msgInfo.get("maxLrnSubjCd").toString();
        			}
        			
        			switch (intelStrength) {
        			case "언어":
        				intelNm = "RJ01" + maxSubjCd;
        				break;
        			case "논리수학":
        				intelNm = "RJ02" + maxSubjCd;
        				break;
        			case "공간":
        				intelNm = "RJ03" + maxSubjCd;
        				break;
        			case "신체운동":
        				intelNm = "RJ04" + maxSubjCd;
        				break;
        			case "대인":
        				intelNm = "RJ05" + maxSubjCd;
        				break;
        			case "음악":
        				intelNm = "RJ06" + maxSubjCd;
        				break;
        			case "개인내적":
        				intelNm = "RJ07" + maxSubjCd;
        				break;
        			case "자연친화":
        				intelNm = "RJ08" + maxSubjCd;
        				break;
        			default:
        				intelNm = "RJ09" + maxSubjCd;
        				break;
        			}
        		}else {
        			intelNm = "RJ01E02";
        		}
        	}else {
        		if(msgInfo != null) {
        			if(msgInfo.get("maxLrnSubjCd") != null) {
        				if("N02".equals(msgInfo.get("maxLrnSubjCd").toString())) {
        					intelNm = "RJ09E01";
        				}else {
        					intelNm = "RJ09" + msgInfo.get("maxLrnSubjCd");
        				}
        			}else {
        				intelNm = "RJ01E02";
        			}
        		}else {
        			intelNm = "RJ01E02";
        		}
        	}
        	
        	paramData.clear();
        	paramData.put("msgCd", intelNm);
        	paramData.put("grp", "RECOMMEND_JOB");
        	
        	Map<String, Object> recommandJobData = (Map) commonMapperTutor.get(paramData, "HamsTutorVr.selectRecommendJob");
        	
        	recommendJob = recommandJobData.get("cdNm").toString();
        	
        	visionPrintBasicInfo.put("title",recommendJob);
        	visionPrintBasicInfo.put("msg",msg.get(0));
        	visionPrintBasicInfo.put("positiveMsg",positiveMsgList);
        	visionPrintBasicInfo.put("negativeMsg",negativeMsgList);
        }
        
        data.put("visionPrintBasicInfo",visionPrintBasicInfo);

        setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getVisionPrintLrnStt(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequired(paramMap,false);

        //DB 조회
        LinkedHashMap<String,Object> visionPrintLrnStt = new LinkedHashMap<>();
        LinkedHashMap<String,Integer> lrnStt = (LinkedHashMap<String, Integer>) commonMapperTutor.get(paramMap, "HamsTutorVr.selectVisionPrintLrnStt");
        LinkedHashMap<String,Integer> examStt = (LinkedHashMap<String, Integer>) commonMapperTutor.get(paramMap, "HamsTutorVr.selectVisionPrintExamStt");
        LinkedHashMap<String,Integer> imprvSlvHabitDetail = new LinkedHashMap<>();
        
        if(lrnStt != null) {
        	visionPrintLrnStt.put("lrnStt",lrnStt);
        }
        
        if(examStt != null) {
        	visionPrintLrnStt.put("examStt",examStt);
        }
        
        if(!visionPrintLrnStt.isEmpty()) {
        	data.put("visionPrintLrnStt",visionPrintLrnStt);
        }

        setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getVisionPrintLrnDiagnosisRst(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequired(paramMap,false);
        
        //DB 조회
        ArrayList<Map<String,Object>> visionPrintLrnDiagnosisRst = new ArrayList<>();
        Map<String,Object> externalApiParamMap = new LinkedHashMap<>();
        ArrayList<Map<String,Object>> subjList = new ArrayList();
        ArrayList<Map<String,Object>> unsubjList = new ArrayList();
        
        ArrayList<Map<String,Object>> c01List = new ArrayList();
        ArrayList<Map<String,Object>> c02List = new ArrayList();
        ArrayList<Map<String,Object>> c03List = new ArrayList();
        ArrayList<Map<String,Object>> c04List = new ArrayList();
        ArrayList<Map<String,Object>> c05List = new ArrayList();
        ArrayList<Map<String,Object>> c06List = new ArrayList();
        
        ArrayList<Map<String,Object>> subjCdList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorVr.selectSubjCd");
        
        externalApiParamMap.put("p", paramMap.get("p"));
        
        externalApiParamMap.put("apiName", "aiReport.");
        Map<String,Object> studInfoMap = (Map<String, Object>) externalAPIservice.callExternalAPI(externalApiParamMap).get("data");

        String grade = (studInfoMap != null) ? studInfoMap.get("grade").toString() : "";
        
        externalApiParamMap.put("apiName", ".study.course-due-dates");
        ArrayList<Map<String,Object>> externalApiList =  (ArrayList<Map<String,Object>>) externalAPIservice.callExternalAPI(externalApiParamMap).get("data");
        if(externalApiList != null && externalApiList.size() > 0) {
        	for(Map<String, Object> item : externalApiList) {
        		if(item.get("courseCls") != null) {
        			Map<String, Object> courseInfo = (Map<String, Object>) item.get("courseCls"); 
        			if("P".equals(courseInfo.get("code"))) {
        				unsubjList = (ArrayList<Map<String, Object>>) item.get("courseDueDateInfoList");
        			}else {
        				subjList = (ArrayList<Map<String, Object>>) item.get("courseDueDateInfoList");
        			}
        		}
        	}
        	
        	if(subjList.size() > 0) {
        		String subjCdCheck = "";
        		int listSize = 0;
        		
        		for(Map<String, Object> subjItem : subjList) {
					String subjCd = null;
					List<String> titleList = Arrays.asList(subjItem.get("title").toString().replace("|", ",").split(","));
					
					if(subjItem.get("subjectName").toString().contains("국어")) {
						c01List.add(subjItem);
					} else if(subjItem.get("subjectName").toString().contains("수학")) { 
						c02List.add(subjItem);
					} else if(subjItem.get("subjectName").toString().contains("사회")) {
						c03List.add(subjItem);
					} else if(subjItem.get("subjectName").toString().contains("과학")) {
						c04List.add(subjItem);
					} else if(subjItem.get("subjectName").toString().contains("영어")) {
						c05List.add(subjItem);
					} else if(subjItem.get("subjectName").toString().contains("통합")) {
						c06List.add(subjItem);
					}
					
        			/*for(Map<String, Object> subjCdItem : subjCdList) {
        				
        				if(subjCdItem.get("subjNm").toString().replace(" ", "").equals(subjItem.get("subjectName").toString().replace(" ", ""))) {
        					subjCd = (Integer.valueOf(subjCdItem.get("depth").toString()) == 1) ? subjCdItem.get("subjCd").toString() : subjCdItem.get("upperSubjCd").toString();
        					
        					if(!subjCdCheck.equals(subjCd)) {
        						subjCdCheck = subjCd;
        					}
        				}
        			}*/
        			
        		}
        	}
        	
        	if(c01List.size() > 0) {
        		visionPrintLrnDiagnosisRst.add(getSubjCourseMap(c01List, grade, "C01"));
        	}
        	
        	if(c02List.size() > 0) { 
        		visionPrintLrnDiagnosisRst.add(getSubjCourseMap(c02List, grade, "C02"));
        	}
        	
        	if(c03List.size() > 0){
        		visionPrintLrnDiagnosisRst.add(getSubjCourseMap(c03List, grade, "C03"));
        	}
        	
        	if(c04List.size() > 0) {
        		visionPrintLrnDiagnosisRst.add(getSubjCourseMap(c04List, grade, "C04"));
        	}
        	
        	if(c05List.size() > 0) {
        		visionPrintLrnDiagnosisRst.add(getSubjCourseMap(c05List, grade, "C05"));
        	}
        	
        	if(c06List.size() > 0) {
        		visionPrintLrnDiagnosisRst.add(getSubjCourseMap(c06List, grade, "C06"));
        	}
        	
        	if(unsubjList.size() > 0) {
        		for(Map<String, Object> unsubjItem : unsubjList) {
        			String subjCd = null;
        			
        			for(Map<String, Object> subjCdItem : subjCdList) {
        				if(subjCdItem.get("subjNm").toString().replace(" ", "").equals(unsubjItem.get("subjectName").toString().replace(" ", ""))) {
        					subjCd = (Integer.valueOf(subjCdItem.get("depth").toString()) == 1) ? subjCdItem.get("subjCd").toString() : subjCdItem.get("upperSubjCd").toString();
        				} else if (subjCdItem.get("subjNm").toString().replace(" ", "").contains(unsubjItem.get("subjectName").toString().replace(" ", ""))) {
        					subjCd = (Integer.valueOf(subjCdItem.get("depth").toString()) == 1) ? subjCdItem.get("subjCd").toString() : subjCdItem.get("upperSubjCd").toString();
        				}
        			}
        			
        			visionPrintLrnDiagnosisRst.add(createSubjCourseMap(subjCd, unsubjItem.get("lastDay").toString(), unsubjItem.get("title").toString()));
        		}
        	}
        	data.put("visionPrintLrnDiagnosisRst",visionPrintLrnDiagnosisRst);
        }
        setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getVisionPrintAiRecommendLrn(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequired(paramMap,false);

        //DB 조회
        ArrayList<Map<String,Object>> visionPrintAiRecommendLrn = new ArrayList<>();
        LinkedHashMap<String,String> dummyMap = new LinkedHashMap<>();
        LinkedHashMap<String,String> dummyMapTwo = new LinkedHashMap<>();
        
        Map<String,Object> externalApiParamMap = new LinkedHashMap<>();
        ArrayList<Map<String,Object>> externalApiList = new ArrayList();
        
        externalApiParamMap.put("p", paramMap.get("p"));
        
        externalApiParamMap.put("apiName", "recommand.");
        
        externalApiList =  (ArrayList<Map<String,Object>>) externalAPIservice.callExternalAPI(externalApiParamMap).get("data");
        
        if(externalApiList != null && externalApiList.size() > 0) {
        	for(Map<String, Object> item : externalApiList) {
        		visionPrintAiRecommendLrn.add(createRecommendMap(item.get("imgUrl").toString(), item.get("categoryNm").toString(), item.get("serviceNm").toString()));
        	}
        	
        	data.put("visionPrintAiRecommendLrn",visionPrintAiRecommendLrn);
        }

        setResult(dataKey,data);

        return result;
    }
    
    @Override
    public Map getStudInfo(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequired(paramMap,true);

        //API 조회
        paramMap.put("apiName", "aiReport.");
        
        LinkedHashMap<String,String> studInfo = new LinkedHashMap<>();
        //Map<String,Object> studInfoMap = (Map<String, Object>) externalAPIservice.callExternalAPI(paramMap).get("data");
        try {
        	Map<String,Object> studInfoMap = (Map<String, Object>) callExApi(paramMap).get("data");
            
            if(studInfoMap != null) {
            	studInfo.put("studType", studInfoMap.get("divCdNm").toString());
            	studInfo.put("lrnStatusType", studInfoMap.get("statusCdNm").toString());
            	
            	data.put("studInfo", studInfo);
            }
        } catch (Exception e) {
        	//복호화
            String[] encodedArr = getDecodedParam(paramMap.get("p").toString());
            String encodedStudId = encodedArr[1];
            //DB params
            paramMap.put("studId",encodedStudId);
        	
        	//2.0 데이터
        	String[] sqlLists = {"StudInfo"};
            List<String> dwSqlList = Arrays.asList(sqlLists);
            
        	paramMap.put("period", "w");
        	paramMap.put("sqlList", dwSqlList);
        	paramMap.put("yymm", "0");
        	paramMap.put("wk", "0");
        	
        	Map<String,Object> studInfoMap = (Map<String, Object>) commonLrnMtService.getLrnMtData(paramMap);
        	
        	//수행률
    		try {
    			Map<String,Object> studInfoData = (Map<String, Object>) studInfoMap.get("StudInfo");
    			
    			studInfo.put("studType", (String) studInfoData.get("studTypeNm"));
    			studInfo.put("lrnStatusType", (String) studInfoData.get("lrnSttNm"));
    		} catch (Exception e1) {
    			LOGGER.debug("StudInfo : Error");
    			studInfo.put("studType", null);
    			studInfo.put("lrnStatusType", null);
			}
        	
        	data.put("studInfo", studInfo);
		}
        
        
        setResult(dataKey,data);

        return result;
    }
    
    @Override
    public Map getVisionPrintFeedbackUpdatePopup(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequired(paramMap,true);
        
        ArrayList positiveMsgActive = new ArrayList<>();
    	ArrayList positiveMsgCd = new ArrayList<>();
    	ArrayList<Map<String,String>> positiveMsgList = new ArrayList<>();
    	
    	ArrayList negativeMsgActive = new ArrayList<>();
    	ArrayList negativeMsgCd = new ArrayList<>();
    	ArrayList<Map<String,String>> negativeMsgList = new ArrayList<>();
    	
    	
    	int vrMsgUpdateCheck = 0;
    	int positiveMsgUpdateCheck = 0;
    	int negativeMsgUpdateCheck = 0;
        
        if(paramMap.get("positiveMsgActive") != null && paramMap.get("positiveMsgCd") != null) {
        	positiveMsgActive = (ArrayList) paramMap.get("positiveMsgActive");
        	positiveMsgCd = (ArrayList) paramMap.get("positiveMsgCd");
        	
        	for(int i = 0; i < positiveMsgActive.size(); i++) {
        		String active = (boolean) (positiveMsgActive.get(i)) ? "Y" : "N";
        		positiveMsgList.add(createConsultingMsgUpdateMap(active, "G", positiveMsgCd.get(i).toString()));
        	}
        	
        }
        
        if(paramMap.get("negativeMsgActive") != null && paramMap.get("negativeMsgCd") != null) {
        	negativeMsgActive = (ArrayList) paramMap.get("negativeMsgActive");
            negativeMsgCd = (ArrayList) paramMap.get("negativeMsgCd");
            
            for(int i = 0; i < negativeMsgActive.size(); i++) {
        		String active = (boolean) (negativeMsgActive.get(i)) ? "Y" : "N";
        		negativeMsgList.add(createConsultingMsgUpdateMap(active, "B", negativeMsgCd.get(i).toString()));
        	}
            
        }
        
        //DB 조회
        vrMsgUpdateCheck = commonMapperTutorWr.update(paramMap, "updateVisionPrintFeedback");
        
        if(positiveMsgList.size() > 0) {
        	for(Map<String, String> positiveItem : positiveMsgList) {
        		Map<String,Object> positiveMap = new HashMap<>();
        		positiveMap.put("studId", paramMap.get("studId"));
        		positiveMap.put("yymm", paramMap.get("yymm"));
        		positiveMap.put("term", paramMap.get("term"));
        		positiveMap.put("active", positiveItem.get("active"));
        		positiveMap.put("msgType", positiveItem.get("msgType"));
        		positiveMap.put("msgCd", positiveItem.get("msgCd"));
            	
            	positiveMsgUpdateCheck = commonMapperTutorWr.update(positiveMap, "updateVisionPrintFeedbackConsultingMsg");
        	}
        }
        
        if(negativeMsgList.size() > 0) {
        	for(Map<String, String> negativeItem : negativeMsgList) {
        		Map<String,Object> negativeMap = new HashMap<>();
        		negativeMap.put("studId", paramMap.get("studId"));
            	negativeMap.put("yymm", paramMap.get("yymm"));
            	negativeMap.put("term", paramMap.get("term"));
            	negativeMap.put("active", negativeItem.get("active"));
            	negativeMap.put("msgType", negativeItem.get("msgType"));
            	negativeMap.put("msgCd", negativeItem.get("msgCd"));
            	
            	negativeMsgUpdateCheck = commonMapperTutorWr.update(negativeMap, "updateVisionPrintFeedbackConsultingMsg");
        	}
        }
        
        setResult(dataKey, vrMsgUpdateCheck);
        
        return result;
    }

    //p 비교 메서드
    private void checkRequired(Map<String,Object> params,boolean onlyP) throws Exception{
        ValidationUtilTutor vu = new ValidationUtilTutor();
        if(onlyP) {
            //필수값 체크
            vu.checkRequired(new String[] {"p"},params);
        }
        else {
            //필수값 체크
            vu.checkRequired(new String[] {"p","yymm","term"},params);
            //숫자형 체크
            vu.isNumeric("yymm",(String) params.get("yymm"));
            vu.isNumeric("term",(String) params.get("term"));
        }
        //복호화
        String[] encodedArr = getDecodedParam(params.get("p").toString());
        String encodedStudId = encodedArr[1];
        //DB params
        params.put("studId",encodedStudId);


    }

    //subjCd 비교 메서드
    private void checkRequiredWithSubjCd(Map<String,Object> params) throws Exception{
        ValidationUtilTutor vu = new ValidationUtilTutor();

        //필수값 체크
        vu.checkRequired(new String[] {"p","yymm","term","subjCd"},params);
        //숫자형 체크
        vu.isNumeric("yymm",(String) params.get("yymm"));
        vu.isNumeric("term",(String) params.get("term"));

        //복호화
        String[] encodedArr = getDecodedParam(params.get("p").toString());
        String encodedStudId = encodedArr[1];

        //DB params
        params.put("studId",encodedStudId);


    }

    private Map createSubjCrtRtMap(String subj,int score) {
        LinkedHashMap<String,Object> resultMap = new LinkedHashMap<>();
        resultMap.put("subjCd",subj);
        resultMap.put("crtRt",score);
        return  resultMap;
    }
    
    private Map createConsultingMsgUpdateMap(String active, String msgType, String msgCd) {
        LinkedHashMap<String,Object> resultMap = new LinkedHashMap<>();
        resultMap.put("active",active);
        resultMap.put("msgType",msgType);
        resultMap.put("msgCd",msgCd);
        return  resultMap;
    }
    
    private Map createConsultingMsgMap(Boolean active, String msg, String msgCd) {
        LinkedHashMap<String,Object> resultMap = new LinkedHashMap<>();
        resultMap.put("active",active);
        resultMap.put("msg",msg);
        resultMap.put("msgCd",msgCd);
        return  resultMap;
    }

    private LinkedHashMap<String, Object> createSubjLrnTmMap(String subSubjCd, int lrnTm) {
        LinkedHashMap<String,Object> resultMap = new LinkedHashMap<>();

        resultMap.put("subSubjCd",subSubjCd);
        resultMap.put("lrnTm",lrnTm);

        return  resultMap;
    }
    
    private LinkedHashMap<String, Object> createSubjLrnTm(String subjCd, List subjList) {
        LinkedHashMap<String,Object> resultMap = new LinkedHashMap<>();

        resultMap.put("subjCd",subjCd);
        resultMap.put("subSubjLrnTm",subjList);

        return  resultMap;
    }
    
    private LinkedHashMap<String, Object> createSubjCourseMap(String subjCd, String lastDay, String title) {
        LinkedHashMap<String,Object> resultMap = new LinkedHashMap<>();

        resultMap.put("subjCd",subjCd);
        resultMap.put("endDt",lastDay);
        resultMap.put("curriculumNm",title);

        return  resultMap;
    }
    
    private LinkedHashMap<String, Object> createRecommendMap(String thumUrl, String ctgr1, String ctgr2) {
        LinkedHashMap<String,Object> resultMap = new LinkedHashMap<>();

        resultMap.put("thumUrl",thumUrl);
        resultMap.put("ctgr1",ctgr1);
        resultMap.put("ctgr2",ctgr2);

        return  resultMap;
    }
    
    private LinkedHashMap<String, Object> getSubjCourseMap(ArrayList<Map<String, Object>> subjLists, String grade, String subjCd) {
        LinkedHashMap<String,Object> resultMap = new LinkedHashMap<>();
        ArrayList<Map<String, Object>> checkList = new ArrayList<>();
        
        int courseId = 0;
        int index = 0;
        int checkIndex = 0;
        
        if(subjLists.size() > 1) {
        	for(Map<String, Object> cdItem : subjLists) {
        		List<String> titleList = Arrays.asList(cdItem.get("title").toString().replace("|", ",").split(","));
        		
        		int checkCourseId = Integer.valueOf(cdItem.get("courseId").toString());
        		
        		if(titleList.get(0).contains(grade)) {
        			checkList.add(cdItem);
    			}
        		
        		if(courseId == 0) {
        			courseId = checkCourseId;
        		} else if(courseId != 0 && courseId > checkCourseId) {
        			courseId = checkCourseId;
        			checkIndex = index;
        		}
        		
        		index++;
        	}
        	
        	if(checkList.size() > 1) {
        		List<String> titleList = Arrays.asList(checkList.get(checkIndex).get("title").toString().replace("|", ",").split(","));
				resultMap = createSubjCourseMap(subjCd, checkList.get(checkIndex).get("lastDay").toString(), titleList.get(titleList.size()-1));
        	} else if(checkList.size() > 0) {
        		List<String> titleList = Arrays.asList(checkList.get(checkIndex).get("title").toString().replace("|", ",").split(","));
				resultMap = createSubjCourseMap(subjCd, checkList.get(checkIndex).get("lastDay").toString(), titleList.get(titleList.size()-1));
        	} else {
        		List<String> titleList = Arrays.asList(subjLists.get(0).get("title").toString().replace("|", ",").split(","));
				resultMap = createSubjCourseMap(subjCd, subjLists.get(0).get("lastDay").toString(), titleList.get(titleList.size()-1));
        	}
        	
			
		}else {
			for(Map<String, Object> cdItem : subjLists) {
				List<String> titleList = Arrays.asList(cdItem.get("title").toString().replace("|", ",").split(","));
				resultMap = createSubjCourseMap(subjCd, cdItem.get("lastDay").toString(), titleList.get(titleList.size()-1));
        	}
		}

        return  resultMap;
    }
    
    /**
     * 서비스단에서 리턴되는 결과(메시지,데이터 object를 포함한 result)세팅.
     * @param key
     * @param data
     */
    private void setResult(String key, Object data) {
        LinkedHashMap message = new LinkedHashMap();
        result = new LinkedHashMap();
        
        if(data == null
                || (data instanceof List && ((List)data).size() == 0)
                || (data instanceof Map && ((Map)data).isEmpty())) {
            throw new NoDataException(new Object[] {key,"null",ValidationCode.NO_DATA});
        } else if(data instanceof Map && ((Map)data).values().toArray()[0] == null) {
        	throw new NoDataException(new Object[] {key,"null",ValidationCode.NO_DATA});
        } else {
            message.put("resultCode", ValidationCode.SUCCESS.getCode());
            result.put(msgKey, message);
            result.put(dataKey, data);
        }
    }

    /**
     * encoded parameter decode
     */
    public String[] getDecodedParam(String encodedParam) throws Exception {
        String[] decodedParamList = null;

        String studId = "";
        String decodedStr = "";

        CipherUtil cp = CipherUtil.getInstance();

        try {
            decodedStr = cp.AES_Decode(encodedParam);
            decodedParamList = decodedStr.split("&");
        } catch (Exception e) {
            LOGGER.debug("HL Parameter Incorrect");
        }

        return decodedParamList;
    }
    
 // for QA
    private Map callExApi(Map<String, Object> paramMap) throws Exception {
    	
    	String apiName = ((String)paramMap.remove("apiName")).replaceAll("\\.", "\\/"); // '.'을 '/'로 변환, 맵에서 삭제
		RestTemplate restTemplate = new RestTemplate();
		
    	try {
        	String studId = "";
    		String encodedStr = paramMap.get("p").toString();
    		
    		String[] paramList = getDecodedParam(encodedStr);
    		studId = paramList[1];
    		paramMap.put("studId", studId);
    		
    		String url = "https://sem.home-learn.com/sigong/clientsvc/admsys/v1/ai/tutor/" + apiName + paramMap.get("studId") + ".json";
        	
        	//파라미터 세팅
        	UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        	builder.queryParam("for", "aiReport");
        	URI apiUri = builder.build().encode().toUri();  
        	
        	LinkedHashMap responseData = restTemplate.getForObject(apiUri, LinkedHashMap.class);
        	
        	LOGGER.debug("code : " + responseData.get("code"));
        	LOGGER.debug("message : " + responseData.get("message"));
        	LOGGER.debug("data : " + responseData.get("data"));
        	
        	if("200".equals(responseData.get("code").toString())) {
        		setApiResult(dataKey, responseData.get("data"));
        	} else {
        		LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
        		msgMap.put("resultCode", ValidationCode.EX_API_ERROR.getCode());
        		msgMap.put("result", "(" + responseData.get("code") + ")" + responseData.get("message"));
        		setApiResult(msgKey, msgMap);
        	}
    	} catch(Exception e) {
    		LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
    		msgMap.put("resultCode", ValidationCode.EX_API_ERROR.getCode());
    		msgMap.put("result", ValidationCode.EX_API_ERROR.getMessage());
    		setApiResult(msgKey, msgMap);
    	}
    	
    	return apiResult;
    }
    
    // for QA
    private void setApiResult(String key, Object data) {
        LinkedHashMap message = new LinkedHashMap();
        apiResult = new LinkedHashMap();

        if(data == null
                || (data instanceof List && ((List)data).size() == 0)
                || (data instanceof Map && ((Map)data).isEmpty())) {
            throw new NoDataException(new Object[] {key,"null",ValidationCode.NO_DATA});
        }
        else if(resultNullCheck((Map)data)) {
            throw new NoDataException(new Object[] {key,"null",ValidationCode.NO_DATA});
        }
        else {
            message.put("resultCode", ValidationCode.SUCCESS.getCode());
            apiResult.put(msgKey, message);
            apiResult.put(dataKey, data);
        }
    }
    
    private boolean resultNullCheck(Map<String,Object> data) {
        int dataSize = data.size(); //data map의 크기
        int count = 0; //inner data들의 null 체킹 횟수
        for(String key : data.keySet()) {
            if(data.get(key) instanceof List) {

                //List일때
                if((((List) data.get(key)).size() == 0)) {
                    count += 1;
                }
            }
            else if(data.get(key) instanceof Map) {
                //Map일때
                if((((Map) data.get(key)).isEmpty())) {
                    count += 1;
                }
                else if(innerResultNullCheck((Map)data.get(key))) {
                    count += 1;
                }
            }
            else if(data.get(key) == null) {
                count += 1;
            }
        }
        if(dataSize == count) {
            return true;
        }
        return false;

    }
    private boolean innerResultNullCheck(Map<String,Object> data) {
        int dataSize = data.size(); //data map의 크기
        int count = 0; //inner data들의 null 체킹 횟수
        for(String key : data.keySet()) {
            if(
                    (data.get(key) instanceof List && (((List) data.get(key)).size() == 0)) ||
                            (data.get(key) instanceof Map && (((Map) data.get(key)).isEmpty()))) {
                count += 1;
            }
            else if(data.get(key) == null) {
                count += 1;
            }
        }
        if(dataSize == count) {
            return true;
        }
        return false;

    }
}
