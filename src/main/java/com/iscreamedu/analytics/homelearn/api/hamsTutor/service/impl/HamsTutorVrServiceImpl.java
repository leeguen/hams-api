package com.iscreamedu.analytics.homelearn.api.hamsTutor.service.impl;

import com.iscreamedu.analytics.homelearn.api.common.exception.NoDataException;
import com.iscreamedu.analytics.homelearn.api.common.mapper.CommonMapperTutor;
import com.iscreamedu.analytics.homelearn.api.common.security.CipherUtil;
import com.iscreamedu.analytics.homelearn.api.common.service.ExternalAPIService;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationCode;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtilTutor;
import com.iscreamedu.analytics.homelearn.api.hamsTutor.service.HamsTutorVrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Map.Entry;

@Service
public class HamsTutorVrServiceImpl implements HamsTutorVrService {
	
	@Autowired
	CommonMapperTutor commonMapperTutor;
	
	@Autowired
	ExternalAPIService externalAPIservice;

    private static final Logger LOGGER = LoggerFactory.getLogger(HamsTutorVrServiceImpl.class);

    private LinkedHashMap<String, Object> result;
    private String msgKey = "msg";
    private String dataKey = "data";


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
        
        visionBasicInfo.put("msg",msg.get(0));

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
        			subj.remove("yymm");
        			subjCrtRtList.add(subj);
        		}
        	}
        	
        	item.put("subjCrtRt", subjCrtRtList);
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
        	
        	ArrayList<Map<String,Object>> chapterInfoList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorVr.selectVisionExamChapterNmList");
        	ArrayList<Map<String,Object>> visionExamChapterList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorVr.selectVisionExamChapterList");
        	
        	ArrayList<Map<String,Object>> chapterList = new ArrayList<>();
        	
        	
        	for(Map<String, Object> item : chapterInfoList) {
        		LinkedHashMap<String,Object> chapterListMap = new LinkedHashMap<>();
        		ArrayList chapternmList = new ArrayList();
        		ArrayList chapterScoreList = new ArrayList();
        		ArrayList chapterCdList = new ArrayList();
        		
        		for(Map<String, Object> chapterItem : visionExamChapterList) {
        			if(item.get("unitNo").equals(chapterItem.get("unitNo"))) {
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
            
            if(visionExamChapterLrn.get("subUnitInfo").toString().contains(",")) {
            	String followInfo = visionExamChapterLrn.get("subUnitInfo").toString();
        		List<String> followList = Arrays.asList(followInfo.split(","));
        		
        		for(int i = 0; i < followList.size(); i++) {
        			followUpLrnList.add(followList.get(i));
            	}
            	
            }else {
            	followUpLrnList.add(visionExamChapterLrn.get("subUnitInfo"));
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
        	for(int i = 8; i < 6; i++) {
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
        							.replace("{b}", cpMsgInfo.get("aLrnSubjCd").toString()));
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
        							.replace("{c}", cpMsgInfo.get("curQuesCnt").toString()));
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
        ArrayList<Map<String,String>> visionPrintLrnDiagnosisRst = new ArrayList<>();
        LinkedHashMap<String,String> dummyMap = new LinkedHashMap<>();
        LinkedHashMap<String,String> dummyMapTwo = new LinkedHashMap<>();

        dummyMap.put("subjCd","C01");
        dummyMap.put("endDt","2021-01-18");
        dummyMap.put("curriculumNm","개념+실력+단원+서술형(1일 1장)");

        dummyMapTwo.put("subjCd","C02");
        dummyMapTwo.put("endDt","2021-01-08");
        dummyMapTwo.put("curriculumNm","개념+실력기본+단원+서술형(1일 1장)");

        visionPrintLrnDiagnosisRst.add(dummyMap);
        visionPrintLrnDiagnosisRst.add(dummyMapTwo);

        data.put("visionPrintLrnDiagnosisRst",visionPrintLrnDiagnosisRst);
        setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getVisionPrintAiRecommendLrn(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequired(paramMap,false);

        //DB 조회
        ArrayList<Map<String,String>> visionPrintAiRecommendLrn = new ArrayList<>();
        LinkedHashMap<String,String> dummyMap = new LinkedHashMap<>();
        LinkedHashMap<String,String> dummyMapTwo = new LinkedHashMap<>();

        dummyMap.put("thumUrl","https~~");
        dummyMap.put("ctgr1","홈런북카페");
        dummyMap.put("ctgr2","인문/교양");

        dummyMapTwo.put("thumUrl","https~~");
        dummyMapTwo.put("ctgr1","실험의 달인");
        dummyMapTwo.put("ctgr2","뚜루뚜루 배우기");

        visionPrintAiRecommendLrn.add(dummyMap);
        visionPrintAiRecommendLrn.add(dummyMapTwo);

        data.put("visionPrintAiRecommendLrn",visionPrintAiRecommendLrn);
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
        vrMsgUpdateCheck = commonMapperTutor.update(paramMap, "updateVisionPrintFeedback");
        
        if(positiveMsgList.size() > 0) {
        	for(Map<String, String> positiveItem : positiveMsgList) {
        		Map<String,Object> positiveMap = new HashMap<>();
        		positiveMap.put("studId", paramMap.get("studId"));
        		positiveMap.put("yymm", paramMap.get("yymm"));
        		positiveMap.put("term", paramMap.get("term"));
        		positiveMap.put("active", positiveItem.get("active"));
        		positiveMap.put("msgType", positiveItem.get("msgType"));
        		positiveMap.put("msgCd", positiveItem.get("msgCd"));
            	
            	positiveMsgUpdateCheck = commonMapperTutor.update(positiveMap, "updateVisionPrintFeedbackConsultingMsg");
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
            	
            	negativeMsgUpdateCheck = commonMapperTutor.update(negativeMap, "updateVisionPrintFeedbackConsultingMsg");
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
}
