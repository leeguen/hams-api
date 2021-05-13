package com.iscreamedu.analytics.homelearn.api.hamsTutor.service.impl;

import com.iscreamedu.analytics.homelearn.api.common.mapper.CommonMapperTutor;
import com.iscreamedu.analytics.homelearn.api.common.security.CipherUtil;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(HamsTutorVrServiceImpl.class);

    private LinkedHashMap<String, Object> result;
    private String msgKey = "msg";
    private String dataKey = "data";


    @Override
    public Map getVisionReportPublishedInfo(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequired(paramMap,true);

        //DB 조회
        LinkedHashMap<String,Integer> visionReportPublishedInfo = (LinkedHashMap<String, Integer>) commonMapperTutor.get(paramMap, "HamsTutorVr.selectVisionReportPublishedInfo");

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
        
        if(msg != null) {
        	Map<String, Object> msgMap = msg.get(0);
        	
        	for(Entry<String, Object> item : msgMap.entrySet()) {
        		String key = item.getKey();
        		String value = item.getValue().toString();
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
        LinkedHashMap<String,Object> dummyMap = new LinkedHashMap<>();
        
        for(Map<String, Object> item : growStt) {
        	ArrayList<Map<String,Object>> subjCrtRtList = new ArrayList<>();
        	subjCrtRtList.clear();
        	for(Map<String, Object> subj : subjCrtRt) {
        		if(item.get("yymm").equals(subj.get("yymm"))) {
        			subjCrtRtList.add(subj);
        		}
        	}
        	
        	item.put("subjCrtRt", subjCrtRtList);
        }

        data.put("visionGrowthStt",growStt);
        setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getVisionExamStt(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequired(paramMap,false);

        //DB 조회
        LinkedHashMap<String,Integer> visionExamStt = (LinkedHashMap<String, Integer>) commonMapperTutor.get(paramMap, "HamsTutorVr.selectVisionExamStt");

        data.put("visionExamStt",visionExamStt);
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
        ArrayList<Map<String,Object>> chapterInfoList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorVr.selectVisionExamChapterNmList");
        ArrayList<Map<String,Object>> visionExamChapterList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorVr.selectVisionExamChapterList");
        
        ArrayList<Map<String,Object>> chapterList = new ArrayList<>();
        
        
        for(Map<String, Object> item : chapterInfoList) {
        	LinkedHashMap<String,Object> chapterListMap = new LinkedHashMap<>();
        	ArrayList chapternmList = new ArrayList();
            ArrayList chapterScoreList = new ArrayList();
            
        	for(Map<String, Object> chapterItem : visionExamChapterList) {
        		if(item.get("unitNo").equals(chapterItem.get("unitNo"))) {
        			chapternmList.add(chapterItem.get("unitNm").toString());
        			chapterScoreList.add(chapterItem.get("score").toString());
        		}
        	}
        	
        	chapterListMap.put("grade", item.get("grade"));
        	chapterListMap.put("term", item.get("term"));
        	chapterListMap.put("chapter", item.get("unitNo"));
        	chapterListMap.put("chapterNm", chapternmList);
        	chapterListMap.put("understandingLv", chapterScoreList);
        	
        	chapterList.add(chapterListMap);
        }
        
        visionExamChapterStt.put("subjCd",subjList);
        visionExamChapterStt.put("chapterList",chapterList);

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

        visionExamChapterLrn.put("priorLrn",priorLrn);
        visionExamChapterLrn.put("currentLrn",currentLrn);
        visionExamChapterLrn.put("followUpLrn",followUpLrnList);
        visionExamChapterLrn.put("supplementaryLrn",supplementaryLrnList);

        data.put("visionExamChapterLrn",visionExamChapterLrn);
        setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getVisionExamFieldStt(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequired(paramMap,false);

        //DB 조회
        ArrayList<Map<String,Object>> visionExamFieldStt = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorVr.selectVisionExamFieldStt");

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
        
        //DB 조회
        ArrayList<Map<String,Object>> visionExamList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorVr.selectVisionExamList");
//        LinkedHashMap<String,Object> dummyMap = new LinkedHashMap<>();
//
//        LinkedHashMap<String,String> answer = new LinkedHashMap<>();
//        LinkedHashMap<String,Object> wrongAnswer = new LinkedHashMap<>();
//
//        dummyMap.put("examCd",9977);
//        dummyMap.put("smtId", 83050805);
//        dummyMap.put("stuId", 4958);
//        dummyMap.put("subjCd","C01");
//        dummyMap.put("smtDttm","2020-05-29 10:33:38");
//        dummyMap.put("type","A");
//        dummyMap.put("examNm","[3-1] 22장. 교통수단의 발달");
//        dummyMap.put("crtRt",80);
//        dummyMap.put("crtQuesCnt",4);
//        dummyMap.put("quesCnt",5);
//
//        answer.put("crtQues","1,3,5");
//        answer.put("guessQues","4");
//
//        wrongAnswer.put("skipQues",null);
//        wrongAnswer.put("guessQues","1,2");
//        wrongAnswer.put("cursoryQues","2");
//        wrongAnswer.put("incrtQues","3");
//        wrongAnswer.put("mistakenQues","1");
//
//        dummyMap.put("answer",answer);
//        dummyMap.put("wrongAnswer",wrongAnswer);
//        visionExamList.add(dummyMap);

        data.put("visionExamList",visionExamList);
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

        visionAttPtn.put("loginTm",loginMap);

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
        ArrayList<Map<String,Object>> subjLrnTm = new ArrayList<>();
        
        ArrayList<Map<String,Object>> subjLrnTmData = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorVr.selectVisionSubjLrnTm");
        
        LinkedHashMap<String,Object> subjLrnTmMap = new LinkedHashMap<>();
        LinkedHashMap<String,Object> subjLrnTmMapTwo = new LinkedHashMap<>();
        
        String subjNm = null;
        
        if(subjLrnTmData != null) {
        	for(Map<String, Object> item : subjLrnTmData) {
        		if(subjNm == null) {
        			subjNm = item.get("subjCd").toString();
        			
        			
        		}
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
        LinkedHashMap<String,Object> visionBasicInfo = (LinkedHashMap<String, Object>) commonMapperTutor.get(paramMap, "HamsTutorVr.selectVisionBasicInfo");
        LinkedHashMap<String,Object> msgInfo = (LinkedHashMap<String, Object>) commonMapperTutor.get(paramMap, "HamsTutorVr.selectVisionBasicMsgCondition");
        ArrayList<Map<String,Object>> msg = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorVr.selectVisionBasicMsgInfo");
        
        if(msg != null) {
        	Map<String, Object> msgMap = msg.get(0);
        	
        	for(Entry<String, Object> item : msgMap.entrySet()) {
        		String key = item.getKey();
        		String value = item.getValue().toString();
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
        
//        visionPrintBasicInfo.put("title","미래 우주 과학자");
//        visionPrintBasicInfo.put("positiveMsg",new String[] {"칭찬 메시지1","칭찬 메시지2","칭찬 메시지3","칭찬 메시지4","칭찬 메시지5"});
//        visionPrintBasicInfo.put("negativeMsg",new String[] {"처방 메시지1","처방 메시지2"});
        visionBasicInfo.put("msg",msg.get(0));
        data.put("visionBasicInfo",visionBasicInfo);

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

//        lrnStt.put("planDtCnt",689);
//        lrnStt.put("attDtCnt",548);
//        lrnStt.put("lrnSec",45750);
//        lrnStt.put("planCnt",2600);
//        lrnStt.put("exCnt",2548);
//        lrnStt.put("incrtNoteCnt",398);
//        lrnStt.put("incrtNtFnshCnt",348);
//
//        imprvSlvHabitDetail.put("incrtNtNtCnt",298);
//        imprvSlvHabitDetail.put("skipQuesCnt",98);
//        imprvSlvHabitDetail.put("cursoryQuesCnt",28);
//        imprvSlvHabitDetail.put("guessQuesCnt",58);
//        imprvSlvHabitDetail.put("mistakenQuesCnt",31);
//
//        examStt.put("ansQuesCnt",1483);
//        examStt.put("crtQuesCnt",1254);
//        examStt.put("imprvSlvHabitCnt",483);
//        examStt.put("imprvSlvHabitDetail",imprvSlvHabitDetail);

        visionPrintLrnStt.put("lrnStt",lrnStt);
        visionPrintLrnStt.put("examStt",examStt);

        data.put("visionPrintLrnStt",visionPrintLrnStt);
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

    private LinkedHashMap<String, Object> createSubjLrnTmMap(String subSubjCd, int lrnTm) {
        LinkedHashMap<String,Object> resultMap = new LinkedHashMap<>();

        resultMap.put("subSubjCd",subSubjCd);
        resultMap.put("lrnTm",lrnTm);

        return  resultMap;
    }
    /**
     * 서비스단에서 리턴되는 결과(메시지,데이터 object를 포함한 result)세팅.
     * @param key
     * @param data
     */
    private void setResult(String key, Object data) {
        result = new LinkedHashMap();

        if(key.equals(dataKey)) {
            LinkedHashMap message = new LinkedHashMap();
            if(data == null
                    || (data instanceof List && ((List)data).size() == 0)
                    || (data instanceof Map && ((Map)data).isEmpty())) {
                message.put("resultCode", ValidationCode.NO_DATA.getCode());
                result.put(msgKey, message);
            } else {
                message.put("resultCode", ValidationCode.SUCCESS.getCode());
                result.put(msgKey, message);

                result.put(dataKey, data);
            }
        } else {
            result.put(msgKey, data); //validation 걸린 메시지, 데이터 없음
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
