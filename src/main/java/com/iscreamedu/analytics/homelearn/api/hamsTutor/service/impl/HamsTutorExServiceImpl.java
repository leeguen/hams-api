package com.iscreamedu.analytics.homelearn.api.hamsTutor.service.impl;

import com.iscreamedu.analytics.homelearn.api.common.mapper.CommonMapperLrnDm;
import com.iscreamedu.analytics.homelearn.api.common.mapper.CommonMapperTutor;
import com.iscreamedu.analytics.homelearn.api.common.security.CipherUtil;
import com.iscreamedu.analytics.homelearn.api.common.service.ExternalAPIService;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationCode;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtil;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtilTutor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iscreamedu.analytics.homelearn.api.common.exception.NoDataException;
import com.iscreamedu.analytics.homelearn.api.hamsTutor.service.CommonLrnMtService;
import com.iscreamedu.analytics.homelearn.api.hamsTutor.service.HamsTutorExService;

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
public class HamsTutorExServiceImpl implements HamsTutorExService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HamsTutorExServiceImpl.class);
    private static final String TUTOR_NAMESPACE = "HamsTutor";

    private LinkedHashMap<String, Object> result;
    private String msgKey = "msg";
    private String dataKey = "data";

    @Autowired
    CommonMapperTutor commonMapperTutor;
    
    @Autowired
    CommonMapperLrnDm commonMapperLrnDm;
    
    @Autowired
    CommonMapperLrnDm studLrnAnalMapper;

    @Autowired
    CommonLrnMtService commonLrnMtService;
    
    @Autowired
	ExternalAPIService externalAPIservice;
    
    @Value("${extapi.hl.tutor.new.ai.recommend.url}")
	String NEW_RECOMMEND_API; //AI ?????? ?????? API ??????

    
    @Value("${extapi.ai.tutor.intent.check.url}")
	String AI_TUTOR_INTENT_CHECK_API; //AI tutor ????????? ?????? API ??????

        @Override
        public Map getSettleInfoPredictionStt (Map<String,Object> paramMap) throws Exception {
            Map<String,Object> data = new HashMap<>();
            checkTchrRequired(paramMap);

            //DB ??????
            ArrayList<Map<String,Object>> settleInfoPredictionStt = new ArrayList();
            
            ArrayList<Map<String,Object>> predictionCdList = new ArrayList();
            ArrayList<Map<String,Object>> predictionCnt = new ArrayList();
            ArrayList<Map<String,Object>> predictionStudList = new ArrayList();
            
            String today = null;
            
            if(paramMap.containsKey("schType") && paramMap.get("schType").toString().toLowerCase().equals("ms")) {
            	// ????????? ?????? ?????? ????????? ??????
            	today = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(2).toString();
            	paramMap.put("dt", today.replace("-",""));
            	predictionCdList = (ArrayList<Map<String, Object>>) commonMapperLrnDm.getList(paramMap, "LrnDm.selectPredictionCd");
                predictionCnt = (ArrayList<Map<String, Object>>) commonMapperLrnDm.getList(paramMap, "LrnDm.selectPredictionCount");
                predictionStudList = (ArrayList<Map<String, Object>>) commonMapperLrnDm.getList(paramMap, "LrnDm.selectPredictionList");
            } else {
            	today = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1).toString();
            	paramMap.put("dt", today);
            	predictionCdList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorEx.selectPredictionCd");
                predictionCnt = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorEx.selectPredictionCount");
                predictionStudList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorEx.selectPredictionList");
            }
            
            for(Map<String, Object> cdItem : predictionCdList) {
            	LinkedHashMap<String, Object> predictionMap = new LinkedHashMap<>();
            	ArrayList<Integer> studList = new ArrayList<>();
            	
            	String cd = cdItem.get("cd").toString();
            	int studCnt = 0;
            	
            	predictionMap.put("type", cdItem.get("cdNm"));
            	if(predictionCnt.size() > 0) {
            		for(Map<String, Object> cntItem : predictionCnt) {
            			if(cd.equals(cntItem.get("predictionType"))) {
            				studCnt = Integer.valueOf(cntItem.get("predictionCnt").toString());
            			}
            		}
            	}
            	
            	if(predictionStudList != null && predictionStudList.size() > 0) {
            		for(Map<String, Object> studItem : predictionStudList) {
            			if(cd.equals(studItem.get("predictionType"))) {
            				studList.add(Integer.valueOf(studItem.get("studId").toString()));
            			}
            		}
            	}
            	
            	predictionMap.put("typeDetail", createTypeDetailjMap(studCnt, studList));
            	
            	settleInfoPredictionStt.add(predictionMap);
            }
            

            //DB ????????? ??????
            data.put("settleInfoPredictionStt",settleInfoPredictionStt);
            setResult(dataKey,data);

            //??????
            return result;

        }

        @Override
        public Map getAiDiagnosisRst(Map<String,Object> paramMap) throws Exception{
            Map<String,Object> data = new HashMap<>();
            checkRequiredWithDt(paramMap);
            
            //DB ??????
            LinkedHashMap<String,Object> aiDiagnosisRst = new LinkedHashMap<>();
            //ArrayList<Map<String,Object>> msgCntList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorEx.selectAiDiagnosisRst");
            ArrayList<Map<String,Object>> msgList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorEx.selectAiDiagnosisRstList");
            LinkedHashMap<String,Object> msgInfo = (LinkedHashMap<String, Object>) commonMapperTutor.get(paramMap, "HamsTutorEx.selectAiDiagnosisRstMsg");
            ArrayList<String> positiveMsgCdList = new ArrayList<>();
            ArrayList<String> positiveMsgList = new ArrayList<>();
            ArrayList<String> negativeMsgCdList = new ArrayList<>();
            ArrayList<String> negativeMsgList = new ArrayList<>();
            
            Map<String,Object> intentMap = getIntentCheckCnt(paramMap); 
            
            String attentionGCheck = (intentMap.get("EndingAttentionG") != null) ? intentMap.get("EndingAttentionG").toString() : "N";
            String attentionCCheck = (intentMap.get("EndingAttentionC") != null) ? intentMap.get("EndingAttentionC").toString() : "N";
            
            String attetionGMsg = (intentMap.get("EndingAttentionGValue") != null) ? intentMap.get("EndingAttentionGValue").toString() : null;
            String attetionCMsg = (intentMap.get("EndingAttentionCValue") != null) ? intentMap.get("EndingAttentionCValue").toString() : null;
            
            int positivePointCnt = 0;
            int negativePointCnt = 0;
            
            //2.0 ?????????
        	String[] sqlLists = {"LrnExStt","ALrnExStt","AttStt","ExamStt","IncrtNtStt","SlvHabitStt"};
            List<String> dwSqlList = Arrays.asList(sqlLists);
            
        	paramMap.put("period", "d");
        	paramMap.put("sqlList", dwSqlList);
        	
        	Map<String,Object> diagnosisMap = (Map<String, Object>) commonLrnMtService.getLrnMtData(paramMap);
        	
        	//?????????
    		try {
    			Map<String,Object> lrnExSttMap = (Map<String, Object>) diagnosisMap.get("LrnExStt");
    			
    			msgInfo.put("exRt", lrnExSttMap.get("exRt"));
    			msgInfo.put("dLrnCnt", lrnExSttMap.get("dLrnCnt"));
    			msgInfo.put("nLrnCnt", lrnExSttMap.get("nLrnCnt"));
    		} catch (Exception e) {
    			LOGGER.debug("LrnExStt : Error");
			}
    		
    		//?????????
    		try {
    			Map<String,Object> attStt = (Map<String, Object>) diagnosisMap.get("AttStt");
    			
    			msgInfo.put("loginCnt", attStt.get("attDtCnt"));
    		} catch (Exception e) {
    			LOGGER.debug("AttStt : Error");
			}
    		
    		//???????????????
    		try {
    			Map<String,Object> aLrnExStt = (Map<String, Object>) diagnosisMap.get("ALrnExStt");
    			
    			msgInfo.put("aLrnCnt", aLrnExStt.get("aLrnCnt"));
    		} catch (Exception e) {
    			LOGGER.debug("ALrnExStt : Error");
			}
    		
    		//??????
    		try {
    			Map<String,Object> examStt = (Map<String, Object>) diagnosisMap.get("ExamStt");
    			
    			msgInfo.put("explCnt", examStt.get("explCnt"));
    			msgInfo.put("crtRt", examStt.get("crtRt"));
    			msgInfo.put("psExplCnt", examStt.get("psExplCnt"));
    			msgInfo.put("npsExplCnt", examStt.get("npsExplCnt"));
    		} catch (Exception e) {
    			LOGGER.debug("ExamStt : Error");
			}
    		
    		//????????????
    		try {
    			Map<String,Object> incrtNtStt = (Map<String, Object>) diagnosisMap.get("IncrtNtStt");
    			
    			msgInfo.put("incrtNtCnt", incrtNtStt.get("incrtNtNcCnt"));
    		} catch (Exception e) {
    			LOGGER.debug("IncrtNtStt : Error");
			}
    		
    		//???????????? ??????
    		try {
    			Map<String,Object> slvHabitStt = (Map<String, Object>) diagnosisMap.get("SlvHabitStt");
    			
    			int hrryCnt = (slvHabitStt.get("hrryCnt") != null) ? Integer.parseInt(slvHabitStt.get("hrryCnt").toString()) : 0;
    			int mistakeCnt = (slvHabitStt.get("mistakeCnt") != null) ? Integer.parseInt(slvHabitStt.get("mistakeCnt").toString()) : 0; 
    			
    			msgInfo.put("slvCnt", slvHabitStt.get("slvCnt"));
    			msgInfo.put("gucQuesCnt", slvHabitStt.get("guessCnt"));
    			msgInfo.put("skpQuesCnt", slvHabitStt.get("skipCnt"));
    			msgInfo.put("curQuesCnt", slvHabitStt.get("hrryCnt"));
    			msgInfo.put("mistakeQuesCnt", slvHabitStt.get("mistakeCnt"));
    			msgInfo.put("curMistakeQuesCnt", (hrryCnt + mistakeCnt));
    		} catch (Exception e) {
    			LOGGER.debug("SlvHabitStt : Error");
			}
            
            positiveMsgCdList = getPositiveMsgList(msgInfo, attentionGCheck);
            negativeMsgCdList = getNegativeMsgList(msgInfo, attentionCCheck);
            
            
            positivePointCnt = positiveMsgCdList.size();
            negativePointCnt = negativeMsgCdList.size();
            
            int bookCnt = 0;
            
            if(positivePointCnt < 5) {
            	
            	LinkedHashMap<String,Object> apiMap = new LinkedHashMap<>();
            	LinkedHashMap<String,Object> paramData = new LinkedHashMap<>();
            	
    			paramData.put("p", paramMap.get("p").toString());
    			paramData.put("fromDate", paramMap.get("startDt"));
    			paramData.put("toDate", paramMap.get("endDt"));
    			paramData.put("apiName", "read.complete");
    			paramData.put("page", "1");
    			paramData.put("size", "3");
    			
    			apiMap =  (LinkedHashMap<String, Object>) externalAPIservice.callExternalAPI(paramData).get("data");
    			bookCnt = Integer.valueOf(apiMap.get("numberOfElements").toString()); // !!
    			
    			if(bookCnt == 0) {
    				if(positivePointCnt == 0) {
    					positiveMsgCdList.add("CPG0013");
    					positivePointCnt = 1;
    				}
    			}else {
    				positiveMsgCdList.add("CPG0012");
    				positivePointCnt += 1;
    			}
    			
    			if(positivePointCnt == 1 && positiveMsgCdList.contains("CPG0013")) {
    				positivePointCnt = 0;
    			}
            }
            
            if(negativePointCnt == 0) {
            	negativeMsgCdList.add("CPB0017");
            }
            
            for(Map<String, Object> item : msgList) {
            	
            	String msgCd = item.get("msgCd").toString();
            	String msg = item.get("msg").toString();
            	
            	if("G".equals(item.get("msgType"))) {
            		if(positiveMsgCdList.contains(msgCd)) {
            			if("CPG0003".equals(msgCd) || "CPG0004".equals(msgCd) || "CPG0005".equals(msgCd) || "CPG0006".equals(msgCd)) {
            				msg = msg.replace("{a}", msgInfo.get("explCnt").toString())
                					.replace("{b}", msgInfo.get("psExplCnt").toString())
                					.replace("{c}", msgInfo.get("crtRt").toString());
                		}else if("CPG0010".equals(msgCd)) {
                			msg = msg.replace("{a}", msgInfo.get("aLrnCnt").toString())
                					.replace("{b}", msgInfo.get("subSubjCd").toString())
                					.replace("{c}", msgInfo.get("subjCd").toString());
                		}else if("CPG0012".equals(msgCd)) {
                			msg = msg.replace("{a}", String.valueOf(bookCnt));
                		} else if ("CPG0014".equals(msgCd)) {
                			msg = msg.replace("{a}", attetionGMsg.replace("|", ", "));
                		}
            			positiveMsgList.add(msg);
            		}
            	}else {
            		if(negativeMsgCdList.contains(msgCd)) {
            			if("CPB0004".equals(msgCd) ) {
            				msg = msg.replace("{b}", msgInfo.get("exRt").toString());
            			}else if("CPB0006".equals(msgCd)) {
            				msg = msg.replace("{b}", msgInfo.get("nLrnCnt").toString());
            			}else if("CPB0007".equals(msgCd)) {
            				msg = msg.replace("{b}", msgInfo.get("crtRt").toString());
            			}else if("CPB0008".equals(msgCd)) {
            				int explCnt = Integer.valueOf(msgInfo.get("explCnt").toString());
            				int psExplCnt = Integer.valueOf(msgInfo.get("psExplCnt").toString());
            				int diff = explCnt - psExplCnt;
            				msg = msg.replace("{b}", String.valueOf(diff));
            			}else if("CPB0009".equals(msgCd)) {
            				msg = msg.replace("{c}", msgInfo.get("incrtNtCnt").toString());
            			}else if("CPB0010".equals(msgCd)) {
            				msg = msg.replace("{c}", msgInfo.get("skpQuesCnt").toString());
            			}else if("CPB0011".equals(msgCd)) {
            				msg = msg.replace("{c+d}", msgInfo.get("curMistakeQuesCnt").toString());
            			}else if("CPB0012".equals(msgCd)) {
            				msg = msg.replace("{c}", msgInfo.get("gucQuesCnt").toString());
            			}else if("CPB0013".equals(msgCd)) {
            				msg = msg.replace("{b}", msgInfo.get("oLrnCnt").toString());
            			}else if("CPB0014".equals(msgCd)) {
            				msg = msg.replace("{b}", msgInfo.get("bLrnCnt").toString());
            			}else if("CPB0018".equals(msgCd)) {
            				msg = msg.replace("{a}", attetionCMsg.replace("|", ", "));
            			}
            			
            			negativeMsgList.add(msg);
            		}
            	}
            }

            aiDiagnosisRst.put("positivePointCnt",positivePointCnt);
            aiDiagnosisRst.put("negativePointCnt",negativePointCnt);
            aiDiagnosisRst.put("positiveMsgList",positiveMsgList);
            aiDiagnosisRst.put("negativeMsgList",negativeMsgList);

            data.put("aiDiagnosisRst",aiDiagnosisRst);
            setResult(dataKey,data);

            return result;
        }

        @Override
        public Map getAiWeakChapterGuide(Map<String, Object> paramMap) throws Exception {
            Map<String,Object> data = new HashMap<>();
            checkRequiredWithDt(paramMap);

            //DB ??????
            ArrayList<Map<String,Object>> aiWeakChapterGuide = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorEx.selectAiWeakChapterGuide");

            if(aiWeakChapterGuide != null || aiWeakChapterGuide.size() > 0) {
            	for(Map<String, Object> item : aiWeakChapterGuide) {
            		if("".equals(item.get("title"))) {
            			item.put("title", null);
            		}
            		if("".equals(item.get("guideMsgList"))) {
            			item.put("guideMsgList", null);
            		}else {
            			
            			if(item.get("guideMsgList").toString().contains("\n")) {
            				List<String> msgList = Arrays.asList(item.get("guideMsgList").toString().replace("<br>", "").split("\n"));
                			item.put("guideMsgList", msgList);
            			}else if(item.get("guideMsgList").toString().contains("\r")) {
            				List<String> msgList = Arrays.asList(item.get("guideMsgList").toString().replace("<br>", "").split("\r"));
                			item.put("guideMsgList", msgList);
            			}else {
            				List<String> msgList = new ArrayList<String>();
            				msgList.add(item.get("guideMsgList").toString());
            				item.put("guideMsgList", msgList);
            			}
            		}
            	}
            }

            data.put("aiWeakChapterGuide",aiWeakChapterGuide);
            setResult(dataKey,data);

            return result;
        }

        @Override
        public Map getAiRecommendQuestion(Map<String, Object> paramMap) throws Exception {
            Map<String,Object> data = new HashMap<>();
            checkRequiredWithDt(paramMap);

            //DB ??????
            ArrayList<Map<String,String>> subjList = (ArrayList<Map<String, String>>) commonMapperTutor.getList(paramMap, "HamsTutorEx.selectAiRecommendQuestionSubjList");
            ArrayList<Map<String,Object>> aiRecommendQuestion = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorEx.selectAiRecommendQuestion");
            ArrayList<Map<String,Object>> aiRecommendQuestionData = new ArrayList<>();
            
            for(Map<String, String> subjItem : subjList) {
            	String subjNm = subjItem.get("subjCd");
            	String msg = subjItem.get("msg");
            	String subMsg = subjItem.get("subMsg");
            	
            	LinkedHashMap<String, Object> subjMap = new LinkedHashMap<>();
            	ArrayList<Map<String,Object>> subjDetailList = new ArrayList<>();
            	
            	for(Map<String, Object> item : aiRecommendQuestion) {
            		if(item.get("examId") == null) {
            			item.put("examId", null);
            			item.put("quesCd", null);
            			item.put("smtId", null);
            			item.put("stuNo", null);
            			item.put("examNm", null);
            			item.put("smtDttm", null);
            			item.put("recommendType", null);
            		}
            		
            		if(subjNm.equals(item.get("subjCd").toString())) {
            			Map<String, Object> itemTemp = new HashMap<String, Object>();
            			itemTemp.putAll(item);
            			itemTemp.remove("subjCd");
            			
            			subjDetailList.add(itemTemp);
            		}
            	}

            	subjMap.put("subjCd", subjNm);
            	subjMap.put("msg", msg);
            	subjMap.put("subMsg", subMsg);
            	subjMap.put("subjDetail", subjDetailList);
            	
            	aiRecommendQuestionData.add(subjMap);
            }
            
            data.put("aiRecommendQuestion",aiRecommendQuestionData);
            setResult(dataKey,data);

            return result;
        }
        
        @Override
        public Map getAiRecommendCourse (Map<String,Object> paramMap) throws Exception {
            Map<String,Object> data = new HashMap<>();
            Map<String,Object> paramData = new HashMap<>();
            checkRequired(paramMap);
            
            RestTemplate restTemplate = new RestTemplate();
            
        	try {
	        	
	        	RestTemplate recommendRestTemplate = new RestTemplate();
	        	JSONParser parser = new JSONParser();
	    		HttpHeaders recommendHeaders = new HttpHeaders();
	    		
	            String url = NEW_RECOMMEND_API + "?stud_id={stud_id}";
	            HttpEntity<String> entity = new HttpEntity<>(recommendHeaders);

	            paramData.put("stud_id", paramMap.get("studId"));
	            
	         	ResponseEntity<String> response = recommendRestTemplate.exchange(url, HttpMethod.GET, entity, String.class, paramData);
	         	
	         	int statusCode = Integer.valueOf(response.getStatusCode().toString());
	         	
	         	if(statusCode == 200) {
	         		String convertResponse = response.getBody();
		         	
		         	Object obj = parser.parse(response.getBody());
		         	ObjectMapper mapper = new ObjectMapper();
		         	Map<String, Object> objMap = mapper.readValue(obj.toString(), Map.class);
		         	
		         	if(objMap.get("subjCourseList") != null) {
		         		ArrayList<Map<String,Object>> subjList = (ArrayList<Map<String, Object>>) objMap.get("subjCourseList");
		         		
		         		LocalDate endDate = LocalDate.parse(paramMap.get("endDt").toString());
		         		LocalDate beforeDate = endDate.minusMonths(1);
		         		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
		         		
		         		int beforeYymm = 0;
		         		
		         		beforeYymm = Integer.parseInt(endDate.format(formatter));
		         		
		         		String yy = String.valueOf(beforeYymm).substring(0,4);
		         		String mm = String.valueOf(beforeYymm).substring(4);
		         		String startDt = yy + "-" + mm + "-01";  
		         		
		         		paramMap.put("yymm", beforeYymm);
		         		paramMap.put("startDt", startDt);
		         		
		         		//DB ??????
		         		LinkedHashMap<String,Object> aiRecmmendCourseMap = (LinkedHashMap<String, Object>) commonMapperTutor.get(paramMap, "HamsTutorEx.selectAiRecommendCourse");
		         		ArrayList<Map<String,Object>> aiRecommendCourseList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorEx.selectAiRecommendCourseRstList");
		         		ArrayList<Map<String,Object>> resultList = new ArrayList();
		         		
		         		//2.0 ?????????
		            	String[] sqlLists = {"LrnExSubjList","ExamSubjList"};
		                List<String> dwSqlList = Arrays.asList(sqlLists);
		                
		            	paramMap.put("period", "m");
		            	paramMap.put("sqlList", dwSqlList);
		            	
		            	Map<String,Object> lrnBasicInfoMap = (Map<String, Object>) commonLrnMtService.getLrnMtData(paramMap);
		            	
		            	//????????? ??????
		        		try {
		        			ArrayList<Map<String,Object>> lrnExSubjList = (ArrayList<Map<String, Object>>) lrnBasicInfoMap.get("LrnExSubjList");
		        			
		        			for(Map<String, Object> lrnChartItem : aiRecommendCourseList) {
		        				String subjCd = lrnChartItem.get("subjCd").toString();
		        				
		        				for(Map<String, Object> lrnExItem : lrnExSubjList) {
		        					if(subjCd.equals(lrnExItem.get("subjNm"))) {
		        						int exRt  = (lrnExItem.get("exRt") != null) ? Integer.parseInt(lrnExItem.get("exRt").toString()) : -1;
		        						String exRtTypeValue = null;
		        						
		        						if(0 <= exRt && exRt < 30) {
		        							exRtTypeValue = "R";
		        						} else if(30 <= exRt && exRt < 90) {
		        							exRtTypeValue = "Y";
		        						} else if(90 <= exRt && exRt <= 100) {
		        							exRtTypeValue = "G";
		        						}
		        						
		        						lrnChartItem.put("exRt", lrnExItem.get("exRt"));
		        						lrnChartItem.put("exRtType", exRtTypeValue);
		        						
		        						continue;
		        					}
		        					
		        				}
		        			}
		        			
		        		} catch (Exception e) {
		        			LOGGER.debug("LrnExSubjList : Error");
		    			}
		            	
		            	//????????? ??????
		        		try {
		        			ArrayList<Map<String,Object>> examSubjList = (ArrayList<Map<String, Object>>) lrnBasicInfoMap.get("ExamSubjList");
		        			
		        			for(Map<String, Object> examChartItem : aiRecommendCourseList) {
		        				String subjCd = examChartItem.get("subjCd").toString();
		        				
		        				for(Map<String, Object> examItem : examSubjList) {
		        					if(subjCd.equals(examItem.get("subjNm"))) {
		        						int crtRt  = (examItem.get("crtRt") != null) ? Integer.parseInt(examItem.get("crtRt").toString()) : -1;
		        						String crtRtTypeValue = null;
		        						
		        						if(0 <= crtRt && crtRt < 50) {
		        							crtRtTypeValue = "R";
		        						} else if(50 <= crtRt && crtRt < 80) {
		        							crtRtTypeValue = "Y";
		        						} else if(80 <= crtRt && crtRt <= 100) {
		        							crtRtTypeValue = "G";
		        						}
		        						
		        						examChartItem.put("crtRt", examItem.get("crtRt"));
		        						examChartItem.put("crtRtType", crtRtTypeValue);
		        						
		        						continue;
		        					}
		        					
		        				}
		        			}
		        			
		        		} catch (Exception e) {
		        			LOGGER.debug("ExamSubjList : Error");
		    			}
		         		
		         		for(Map<String, Object> item : subjList) {
		         			Map<String, Object> courseTempMap = new LinkedHashMap<>();
		         			
		         			courseTempMap.put("subjCd", item.get("subjCd"));
		         			courseTempMap.put("updated", item.get("updated"));
		         			courseTempMap.put("updatedDt", item.get("updatedDt"));
		         			
		         			for(Map<String, Object> courseItem : aiRecommendCourseList) {
		         				if(item.get("subjCd").toString().equals(courseItem.get("subjCd").toString())) {
		         					courseTempMap.put("exRt", courseItem.get("exRt"));
		         					courseTempMap.put("exRtType", courseItem.get("exRtType"));
		         					courseTempMap.put("crtRt", courseItem.get("crtRt"));
		         					courseTempMap.put("crtRtType", courseItem.get("crtRtType"));
		         					courseTempMap.put("compre", courseItem.get("compre"));
		         					courseTempMap.put("compreType", courseItem.get("compreType"));
		         				}
		         			}
		         			
		         			courseTempMap.put("comment", item.get("comment"));
		         			courseTempMap.put("imperfect1", item.get("imperfect1"));
		         			courseTempMap.put("learning1", item.get("learning1"));
		         			courseTempMap.put("courseDiv1", item.get("courseDiv1"));
		         			courseTempMap.put("courseTitle1", item.get("courseTitle1"));
		         			courseTempMap.put("courseNm1", item.get("courseNm1"));
		         			courseTempMap.put("courseId1", item.get("courseId1"));
		         			courseTempMap.put("imperfect2", item.get("imperfect2"));
		         			courseTempMap.put("learning2", item.get("learning2"));
		         			courseTempMap.put("courseDiv2", item.get("courseDiv2"));
		         			courseTempMap.put("courseTitle2", item.get("courseTitle2"));
		         			courseTempMap.put("courseNm2", item.get("courseNm2"));
		         			courseTempMap.put("courseId2", item.get("courseId2"));
		         			courseTempMap.put("imperfect3", item.get("imperfect3"));
		         			courseTempMap.put("learning3", item.get("learning3"));
		         			courseTempMap.put("courseDiv3", item.get("courseDiv3"));
		         			courseTempMap.put("courseTitle3", item.get("courseTitle3"));
		         			courseTempMap.put("courseNm3", item.get("courseNm3"));
		         			courseTempMap.put("courseId3", item.get("courseId3"));
		         			courseTempMap.put("prevCourseId", item.get("prevCourseId"));
		         			courseTempMap.put("prevCourseNm", item.get("prevCourseNm"));
		         			courseTempMap.put("prevCourseDiff", item.get("prevCourseDiff"));
		         			
		         			resultList.add(courseTempMap);
		         		}
		         		aiRecmmendCourseMap.put("subjCourseList", resultList);
		         		data.put("aiRecommenCourse",aiRecmmendCourseMap);
		         		setResult(dataKey,data);
		         	}else {
		         		LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
		        		msgMap.put("resultCode", ValidationCode.NO_DATA.getCode());
		        		msgMap.put("result", "NO_DATA");
		        		setResult2(msgKey, msgMap);
		         	}
		         	
	        	} else {
	        		LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
	        		msgMap.put("resultCode", ValidationCode.SYSTEM_ERROR.getCode());
	        		msgMap.put("result", "(" + statusCode + ")");
	        		setResult2(msgKey, msgMap);
	        	}
	         	
        	} catch(Exception e) {
        		LOGGER.debug(e.toString());
        		LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
        		msgMap.put("resultCode", ValidationCode.NO_DATA.getCode());
        		msgMap.put("result", "NO_DATA");
        		setResult2(msgKey, msgMap);
        	}
//            LocalDate endDate = LocalDate.parse(paramMap.get("endDt").toString());
//            LocalDate beforeDate = endDate.minusMonths(1);
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
//            
//            int beforeYymm = 0;
//            
//            beforeYymm = Integer.parseInt(endDate.format(formatter));
//        	
//            String yy = String.valueOf(beforeYymm).substring(0,4);
//            String mm = String.valueOf(beforeYymm).substring(4);
//            String startDt = yy + "-" + mm + "-01";  
//            
//        	paramMap.put("yymm", beforeYymm);
//        	paramMap.put("startDt", startDt);
//
//            //DB ??????
//            LinkedHashMap<String,Object> aiRecmmendCourseMap = (LinkedHashMap<String, Object>) commonMapperTutor.get(paramMap, "HamsTutorEx.selectAiRecommendCourse");
//            ArrayList<Map<String,Object>> aiRecommendCourseList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorEx.selectAiRecommendCourseList");
//            
//            // ???????????? ?????? -> ?????? ?????? ??? ?????? ??????
//            if(aiRecommendCourseList.size() < 1) {
//            	int beforeYymm1 = Integer.parseInt(beforeDate.format(formatter));
//            	
//            	String yy1 = String.valueOf(beforeYymm1).substring(0,4);
//                String mm1 = String.valueOf(beforeYymm1).substring(4);
//                String startDt1 = yy1 + "-" + mm1 + "-01"; 
//            	paramMap.put("yymm", beforeYymm1);
//            	paramMap.put("startDt", startDt1);
//            	
//            	aiRecommendCourseList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorEx.selectAiRecommendCourseList");
//            }
//
//            //DB ????????? ??????
//            
//            if(aiRecmmendCourseMap != null) {
//            	aiRecmmendCourseMap.put("subjCourseList", aiRecommendCourseList);
//            }
//            

            //??????
            return result;

        }
        
        @Override
        public Map getAiRecommendCourseConfirm (Map<String,Object> paramMap) throws Exception {
            Map<String,Object> data = new HashMap<>();
            checkRequired(paramMap);
            
            int studId = Integer.valueOf(paramMap.get("studId").toString());
            String subjCd = paramMap.get("subjCd").toString();
            JSONObject confirmJson = new JSONObject();
            
            confirmJson.put("subjCd", subjCd);
            confirmJson.put("checked", true);
            
            RestTemplate confirmRestTemplate = new RestTemplate();
            
            try {
            	JSONParser parser = new JSONParser();
            	HttpHeaders confirmHeaders = new HttpHeaders();
            	confirmHeaders.setContentType(MediaType.APPLICATION_JSON);
            	
            	Map<String,Object> paramData = new HashMap<>();
            	String url = NEW_RECOMMEND_API + "?stud_id={stud_id}";
            	HttpEntity<?> entity = new HttpEntity<>(confirmJson, confirmHeaders);
            	
            	paramData.put("stud_id", paramMap.get("studId"));
            	
            	ResponseEntity<String> response = confirmRestTemplate.exchange(url, HttpMethod.PUT, entity, String.class, paramData);
            	
            	int statusCode = Integer.valueOf(response.getStatusCode().toString());
        		Object obj = parser.parse(response.getBody());
        		
	         	if(statusCode == 200 || statusCode == 201) {
	         		data.put("studId", studId);
	         		data.put("msg", obj);
	         		
	         		setResult(dataKey,data);
	         	}else {
         			LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
         			msgMap.put("resultCode", ValidationCode.SYSTEM_ERROR.getCode());
         			msgMap.put("result", "Fail");
         			setResult(msgKey, msgMap);
	         	}
            }catch(Exception e) {
        	   LOGGER.debug(e.toString());
        	   LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
        	   msgMap.put("resultCode", ValidationCode.EX_API_ERROR.getCode());
        	   msgMap.put("result", "Fail");
        	   setResult(msgKey, msgMap);
			}
            

            //??????
            return result;

        }
        
        @Override
        public Map getAiRecommendCourseApply (Map<String,Object> paramMap) throws Exception {
            Map<String,Object> data = new HashMap<>();
            checkRequired(paramMap);
            
            int studId = Integer.valueOf(paramMap.get("studId").toString());
            String subjCd = paramMap.get("subjCd").toString();
            int courseId = Integer.valueOf(paramMap.get("courseId").toString());
            JSONObject confirmJson = new JSONObject();
            
            confirmJson.put("subjCd", subjCd);
            confirmJson.put("courseId", courseId);
            
            RestTemplate confirmRestTemplate = new RestTemplate();
            
            try {
            	JSONParser parser = new JSONParser();
            	HttpHeaders confirmHeaders = new HttpHeaders();
            	confirmHeaders.setContentType(MediaType.APPLICATION_JSON);
            	
            	Map<String,Object> paramData = new HashMap<>();
            	String url = NEW_RECOMMEND_API + "?stud_id={stud_id}";
            	HttpEntity<?> entity = new HttpEntity<>(confirmJson, confirmHeaders);
            	
            	paramData.put("stud_id", paramMap.get("studId"));
            	
            	ResponseEntity<String> response = confirmRestTemplate.exchange(url, HttpMethod.PUT, entity, String.class, paramData);
            	
        		int statusCode = Integer.valueOf(response.getStatusCode().toString());
        		Object obj = parser.parse(response.getBody());
        		
	         	if(statusCode == 200 || statusCode == 201) {
	         		data.put("studId", studId);
	         		data.put("courseId", courseId);
	         		data.put("msg", obj);
	         		
	         		setResult(dataKey,data);
	         	}else {
         			LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
         			msgMap.put("resultCode", ValidationCode.SYSTEM_ERROR.getCode());
         			msgMap.put("result", "Fail");
         			setResult(msgKey, msgMap);
	         	}
            	
            }catch(Exception e) {
            	LinkedHashMap msgMap = new LinkedHashMap<String, Object>();
            	msgMap.put("resultCode", ValidationCode.EX_API_ERROR.getCode());
            	msgMap.put("result", "Fail");
            	setResult(msgKey, msgMap);
			}

            //??????
            return result;

        }
        
    @Override
    public Map getVisionReportNftList(Map<String,Object> paramMap) throws Exception {
    	LinkedHashMap<String,Object> data = new LinkedHashMap<>();
    	
    	ValidationUtil vu = new ValidationUtil();
        
        vu.checkRequired(new String[] {"studId"}, paramMap);
        
        if(vu.isValid()) {
        	
        	/*For Qa*/
        	/*int studId = Integer.valueOf(paramMap.get("studId").toString());
        	
        	if(studId == 2074128) {
        		studId = 1105985;
        		paramMap.put("studId", studId);
    		}
        	
        	if(studId == 2071547) {
        		studId = 786575;
        		paramMap.put("studId", studId);
    		}
        	
        	if(studId == 2085360) {
        		studId = 124400;
        		paramMap.put("studId", studId);
    		}*/
        	/*For Qa*/
        	
        	ArrayList<Map<String,Object>> visionReportList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorNft.selectVisionNftList");
            
            data.put("reportCnt", visionReportList.size());
            data.put("reportList", visionReportList);
            
            setResult(dataKey,data);
        } else {
        	setResult(msgKey, vu.getResult());
        }

        //??????
        return result;

    }
        
    @Override
    public Map getVisionReportNft (Map<String,Object> paramMap) throws Exception {
    	LinkedHashMap<String,Object> data = new LinkedHashMap<>();
        
        ValidationUtil vu = new ValidationUtil();
        ValidationUtil vu1 = new ValidationUtil();
        
        vu.checkRequired(new String[] {"studId", "yymm", "term"}, paramMap);
        
        if(vu.isValid()) {
        	vu1.checkRequired(new String[] {"yymm", "term"}, paramMap);
        	
        	if(vu1.isValid()) {
        		int studId = Integer.valueOf(paramMap.get("studId").toString());
        		paramMap.put("studId", studId);
        		
        		/*For Qa*/
            	/*if(studId == 2074128) {
            		studId = 1105985;
            		paramMap.put("studId", studId);
        		}
            	
            	if(studId == 2071547) {
            		studId = 786575;
            		paramMap.put("studId", studId);
        		}
            	
            	if(studId == 2085360) {
            		studId = 124400;
            		paramMap.put("studId", studId);
        		}*/
            	/*For Qa*/
        		
        		data = (LinkedHashMap<String, Object>) commonMapperTutor.get(paramMap, "HamsTutorNft.selectVisionNft");
                
                String term = paramMap.get("term").toString();
                String yymm = paramMap.get("yymm").toString().substring(0,4);
                
                if(term.equals("1")) {
                	
                	paramMap.put("startYymm", Integer.parseInt(yymm+"01"));
                	paramMap.put("endYymm", Integer.parseInt(yymm+"07"));
                } else {
                	paramMap.put("startYymm", Integer.parseInt(yymm+"08"));
                	paramMap.put("endYymm", Integer.parseInt(yymm+"12"));
                }
                
                Map<String, Object> lrnTypeData = (Map<String, Object>) studLrnAnalMapper.get(paramMap, "HamsTutorNft.selectLrnTypeInfoForNft");
                ArrayList<Map<String,Object>> attRtList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorNft.selectVisionNftAttRt");
                ArrayList<Map<String,Object>> exRtList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorNft.selectVisionNftExRt");
                ArrayList<Map<String,Object>> crtRtList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorNft.selectVisionNftCrtRt");
                
                ArrayList<Object> attRt = new ArrayList<>();
                ArrayList<Object> exRt = new ArrayList<>();
                ArrayList<Object> crtRt = new ArrayList<>();
                
                for(Map<String, Object> attRtItem : attRtList) {
                	attRt.add(attRtItem.get("attRt"));
                }
                
                for(Map<String, Object> exRtItem : exRtList) {
                	exRt.add(exRtItem.get("exRt"));
                }
                
                for(Map<String, Object> crtRtItem : crtRtList) {
                	crtRt.add(crtRtItem.get("crtRt"));
                }
                
                if(lrnTypeData != null) {
                	data.put("lrnTypeNm", lrnTypeData.get("lrnTypeCd"));
                	data.put("lrnTypeImg", lrnTypeData.get("lrnTypeImg"));
                } else {
                	data.put("lrnTypeNm", null);
                	data.put("lrnTypeImg", null);
                }
                
                
                data.put("attRtList", attRt);
                data.put("exRtList", exRt);
                data.put("crtRtList", crtRt);
                
                
                setResult(dataKey,data);
        	} else {
        		setResult(msgKey, vu1.getResult());
        	}
        } else {
        	setResult(msgKey, vu.getResult());
        }

        //??????
        return result;

    }
        

    //p,startDt,endDt ?????? ?????????
    private void checkRequired(Map<String,Object> params) throws Exception {
        ValidationUtilTutor vu = new ValidationUtilTutor();
        //????????? ??????
        vu.checkRequired(new String[] {"p"},params);

        //?????????
        String[] encodedArr = getDecodedParam(params.get("p").toString());
        String encodedStudId = encodedArr[1];

        //DB params
        params.put("studId",encodedStudId);
    }
    
    //p,startDt,endDt ?????? ?????????
    private void checkTchrRequired(Map<String,Object> params) throws Exception {
        ValidationUtilTutor vu = new ValidationUtilTutor();
        //????????? ??????
        vu.checkRequired(new String[] {"p"},params);

        //?????????
        String[] encodedArr = getDecodedParam(params.get("p").toString());
        String encodedTchrId = encodedArr[0];

        //DB params
        params.put("tchrId",encodedTchrId);
    }

    //p,startDt,endDt ?????? ?????????
    private void checkRequiredWithDt(Map<String,Object> params) throws Exception {
        ValidationUtilTutor vu = new ValidationUtilTutor();
        //????????? ??????
        vu.checkRequired(new String[] {"p","startDt","endDt"},params);
        vu.isDate("startDt",(String)params.get("startDt"));
        vu.isDate("endDt",(String)params.get("endDt"));

        //?????????
        String[] encodedArr = getDecodedParam(params.get("p").toString());
        String encodedStudId = encodedArr[1];

        //DB params
        params.put("studId",encodedStudId);
    }
    // getLrnGrowthStt??? subjCrtRt dummy map??? ????????? ?????????
    private Map createSubjCrtRtMap(String subj,int score) {
            LinkedHashMap<String,Object> resultMap = new LinkedHashMap<>();
            resultMap.put("subjCd",subj);
            resultMap.put("crtRt",score);
            return  resultMap;
    }
    //getLrnExChart??? day,subj dummy map??? ????????? ?????????
    private Map createDaySubjMap(String subj,int lrnTm, String ctgr,String msg,boolean day) {
            LinkedHashMap<String,Object> dummyMap = new LinkedHashMap<>();
            if(day) {
                dummyMap.put("subjCd",subj);
            }
            else {
                dummyMap.put("subSubjCd",subj);
            }
            dummyMap.put("lrnTm",lrnTm);
            dummyMap.put("ctgr",ctgr);
            dummyMap.put("msg",msg);
            return dummyMap;

    }
    
  //getLrnExChart??? day,subj dummy map??? ????????? ?????????
    private Map createTypeDetailjMap(int studCnt, ArrayList<Integer> studList) {
		LinkedHashMap<String,Object> dummyMap = new LinkedHashMap<>();
		
		dummyMap.put("studCnt",studCnt);
		dummyMap.put("studList",studList);
		return dummyMap;

    }

    private String subDate(String paramDt,int day) throws ParseException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");

        Date dt = form.parse(paramDt);
        cal.setTime(dt);
        cal.add(Calendar.DATE,day);
        System.out.println("day:::" + day);
        System.out.println("getTime:::" + cal.getTime());
        System.out.println("format getTime:::" + form.format(cal.getTime()));

        return form.format(cal.getTime());
    }
    
    //??????????????? ????????? ??????
    private ArrayList getPositiveMsgList(Map<String, Object> msgData, String attentionCheck) {
    	ArrayList<String> msgCdList = new ArrayList<>();
    	
    	int exRt = (msgData.get("exRt") != null) ? Integer.valueOf(msgData.get("exRt").toString()) : 0;
    	int dLrnCnt = (msgData.get("dLrnCnt") != null) ? Integer.valueOf(msgData.get("dLrnCnt").toString()) : -1;
    	int aLrnCnt = (msgData.get("aLrnCnt") != null) ? Integer.valueOf(msgData.get("aLrnCnt").toString()) : 0;
    	int explCnt = (msgData.get("explCnt") != null) ? Integer.valueOf(msgData.get("explCnt").toString()) : 0;
    	int psExplCnt = (msgData.get("psExplCnt") != null) ? Integer.valueOf(msgData.get("psExplCnt").toString()) : -1;
    	int crtRt = (msgData.get("crtRt") != null) ? Integer.valueOf(msgData.get("crtRt").toString()) : 0;
    	int incrtNtCnt = (msgData.get("incrtNtCnt") != null) ? Integer.valueOf(msgData.get("incrtNtCnt").toString()) : -1;
    	int ptnRt = (msgData.get("ptnRt") != null) ? Integer.valueOf(msgData.get("ptnRt").toString()) : 0;
    	int slvCnt = (msgData.get("slvCnt") != null) ? Integer.valueOf(msgData.get("slvCnt").toString()) : -1;
    	
    	if(attentionCheck.equals("Y")) {
    		msgCdList.add("CPG0014");
    	}
    	
    	if(exRt >= 90 && exRt <= 100) {
    		if(dLrnCnt > 0 && dLrnCnt <= 3) {
    			msgCdList.add("CPG0001");
    		}else if(dLrnCnt > 3) {
    			msgCdList.add("CPG0002");
    		}
    	}
    	
    	if(explCnt >= 1) {
    		if(explCnt == psExplCnt) {
    			msgCdList.add("CPG0003");
    		}
    	}
    	
    	if(explCnt >= 2) {
    		if(explCnt > psExplCnt && psExplCnt >= 1) {
    			if(incrtNtCnt > 0) {
    				msgCdList.add("CPG0004");
    			}else if(incrtNtCnt == 0) {
    				msgCdList.add("CPG0005");
    			}
    		}
    	}
    	
    	if(explCnt >= 1) {
    		if(psExplCnt == 0) {
    			if(crtRt >= 80) {
    				msgCdList.add("CPG0006");
    			}else if(crtRt < 50 && incrtNtCnt == 0) {
    				msgCdList.add("CPG0007");
    			}else if(crtRt >= 50 && incrtNtCnt == 0) {
    				msgCdList.add("CPG0008");
    			}
    		}
    	}
    	
    	if(ptnRt >= 50 && msgCdList.size() < 6) {
//    		msgCdList.add("CPG0009");	// 2022-05-24 ?????? ????????? by jhlim, jhkim    		
    	}
    	
    	if(aLrnCnt > 0 && msgCdList.size() < 6) {
    		msgCdList.add("CPG0010");
    	}
    	
    	if(slvCnt <= 5 && slvCnt > -1 && msgCdList.size() < 6) {
    		msgCdList.add("CPG0011");
    	}
    	
    	return msgCdList;
    }
    
    
    // ??????????????? ????????? ??????
    private ArrayList getNegativeMsgList(Map<String, Object> msgData, String attentionCheck) {
    	ArrayList<String> msgCdList = new ArrayList<>();
    	
    	int loginCnt = (msgData.get("loginCnt") != null) ? Integer.valueOf(msgData.get("loginCnt").toString()) : 0;
    	
    	int exRt = (msgData.get("exRt") != null) ? Integer.valueOf(msgData.get("exRt").toString()) : 0;
    	int dLrnCnt = (msgData.get("dLrnCnt") != null) ? Integer.valueOf(msgData.get("dLrnCnt").toString()) : -1;
    	int aLrnCnt = (msgData.get("aLrnCnt") != null) ? Integer.valueOf(msgData.get("aLrnCnt").toString()) : 0;
    	int nLrnCnt = (msgData.get("nLrnCnt") != null) ? Integer.valueOf(msgData.get("nLrnCnt").toString()) : 0;
    	
    	int rLrnCnt = (msgData.get("rLrnCnt") != null) ? Integer.valueOf(msgData.get("rLrnCnt").toString()) : 0;
    	int bLrnCnt = (msgData.get("bLrnCnt") != null) ? Integer.valueOf(msgData.get("bLrnCnt").toString()) : 0;
    	int oLrnCnt = (msgData.get("oLrnCnt") != null) ? Integer.valueOf(msgData.get("oLrnCnt").toString()) : 0;
    	
    	int explCnt = (msgData.get("explCnt") != null) ? Integer.valueOf(msgData.get("explCnt").toString()) : 0;
    	int psExplCnt = (msgData.get("psExplCnt") != null) ? Integer.valueOf(msgData.get("psExplCnt").toString()) : -1;
    	int npsExplCnt = (msgData.get("npsExplCnt") != null) ? Integer.valueOf(msgData.get("npsExplCnt").toString()) : -1;
    	int crtRt = (msgData.get("crtRt") != null) ? Integer.valueOf(msgData.get("crtRt").toString()) : 0;
    	int incrtNtCnt = (msgData.get("incrtNtCnt") != null) ? Integer.valueOf(msgData.get("incrtNtCnt").toString()) : -1;
    	
    	int slvCnt = (msgData.get("slvCnt") != null) ? Integer.valueOf(msgData.get("slvCnt").toString()) : 0;
    	int gucQuesCnt = (msgData.get("gucQuesCnt") != null) ? Integer.valueOf(msgData.get("gucQuesCnt").toString()) : 0;
    	int skpQuesCnt = (msgData.get("skpQuesCnt") != null) ? Integer.valueOf(msgData.get("skpQuesCnt").toString()) : 0;
    	int curQuesCnt = (msgData.get("curQuesCnt") != null) ? Integer.valueOf(msgData.get("curQuesCnt").toString()) : 0;
    	
    	if(loginCnt > 0) {
    		
    		if(attentionCheck.equals("Y")) {
        		msgCdList.add("CPB0018");
        	}
    		
    		if(exRt == 0) {
    			if(aLrnCnt == 0) {
    				msgCdList.add("CPB0002");
    			}else if(aLrnCnt > 0) {
    				msgCdList.add("CPB0003");
    			}
    		}
    		
    		if(exRt > 0 && exRt < 30) {
    			msgCdList.add("CPB0004");
    		}
    		
    		if(exRt >= 30 && exRt < 60 && dLrnCnt >= 10) {
    			msgCdList.add("CPB0005");
    		}
    		
    		if(exRt < 100 && nLrnCnt > 0) {
    			msgCdList.add("CPB0006");
    		}
    		
    		if(explCnt >= 1 && crtRt < 50 && msgCdList.size() < 6) {
    			msgCdList.add("CPB0007");
    		}
    		
    		if(explCnt >= 2 && msgCdList.size() < 6) {
    			if(explCnt > psExplCnt && psExplCnt >= 1) {
    				msgCdList.add("CPB0008");
    			}
    		}
    		
    		if(explCnt >= 1 && msgCdList.size() < 6) {
    			if(psExplCnt == 0 && incrtNtCnt > 0) {
    				msgCdList.add("CPB0009");
    			}
    		}
    		
    		if(explCnt >= 1 && msgCdList.size() < 6) {
    			if(slvCnt > 5) {
    				if(skpQuesCnt > 1) {
    					msgCdList.add("CPB0010");
    				}
    				
    				if(curQuesCnt > 1 && msgCdList.size() < 6) {
    					msgCdList.add("CPB0011");
    				}
    				
    				if(gucQuesCnt > 1 && msgCdList.size() < 6) {
    					msgCdList.add("CPB0012");
    				}
    			}
    		}
    		
    		if(oLrnCnt > 0 && msgCdList.size() < 6) {
    			msgCdList.add("CPB0013");
    		}
    		
    		if(bLrnCnt > 0 && msgCdList.size() < 6) {
    			msgCdList.add("CPB0014");
    		}
    		
    		if(aLrnCnt == 0 && msgCdList.size() < 6) {
    			msgCdList.add("CPB0015");
    		}
    		
    		if(rLrnCnt > 0 && msgCdList.size() < 6) {
    			msgCdList.add("CPB0016");
    		}
    		
    	}else {
    		msgCdList.add("CPB0001");
    		
    		if(attentionCheck.equals("Y")) {
        		msgCdList.add("CPB0018");
        	}
    	}
    	
    	return msgCdList;
    }
    
    private Map<String, Object> getIntentCheckCnt(Map<String, Object> paramMap){
    	Map<String, Object> intentReuslt = new HashMap<>();
    	
    	int studId = Integer.valueOf(paramMap.get("studId").toString());
    	String startDay = paramMap.get("startDt").toString();
    	String endDay = paramMap.get("endDt").toString();
    	
        ArrayList<String> intentList = new ArrayList<>();
        
        JSONArray intentArray = new JSONArray();
        
        intentArray.add("EndingAttentionG");
        intentArray.add("EndingAttentionC");
        JSONObject intentJson = new JSONObject();
        
        intentJson.put("studId", studId);
        intentJson.put("startDay", startDay);
        intentJson.put("endDay", endDay);
        intentJson.put("intentNm", intentArray);
        
        RestTemplate intentRestTemplate = new RestTemplate();
        
        try {
        	JSONParser parser = new JSONParser();
        	HttpHeaders intentHeaders = new HttpHeaders();
        	//intentHeaders.setContentType(MediaType.APPLICATION_JSON);
        	
        	Map<String,Object> paramData = new HashMap<>();
        	//String url = "https://aitutor.adm.i-screamreport.com/client/intent-check";
        	String url = AI_TUTOR_INTENT_CHECK_API;
        	HttpEntity<?> entity = new HttpEntity<>(intentJson, intentHeaders);
        	
        	ResponseEntity<String> response = intentRestTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        	
        	int statusCode = Integer.valueOf(response.getStatusCode().toString());
        	
    		Object obj = parser.parse(response.getBody());
    		
         	if(statusCode == 200) {
         		ObjectMapper mapper = new ObjectMapper();
	         	Map<String, Object> objMap = mapper.readValue(obj.toString(), Map.class);
	         	
	         	String resultMsg = objMap.get("resultMsg").toString();
	         	
	         	if(resultMsg.equals("success")) {
	         		intentReuslt = (Map<String, Object>) objMap.get("data");
	         	}
	         	
         	}
        }catch(Exception e) {
    	   LOGGER.debug(e.toString());
		}
    	
    	return intentReuslt;
    }
    
    /**
     * ?????????????????? ???????????? ??????(?????????,????????? object??? ????????? result)??????.
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
        } else {
            message.put("resultCode", ValidationCode.SUCCESS.getCode());
            result.put(msgKey, message);
            result.put(dataKey, data);
        }
    }
    
    /**
     * ?????????????????? ???????????? ??????(?????????,????????? object??? ????????? result)??????.
     * @param key
     * @param data
     */
    private void setResult2(String key, Object data) {
        result = new LinkedHashMap();
        
        if(key.equals(dataKey)) {
            LinkedHashMap message = new LinkedHashMap();            
            if(data == null 
                    || (data instanceof List && ((List)data).size() == 0) 
                    || (data instanceof Map && ((Map)data).isEmpty())) {
                //??????????????? ?????? ?????? ???????????? ??????.
                message.put("resultCode", ValidationCode.NO_DATA.getCode());
                result.put(msgKey, message);
            } else {
                //???????????????, ???????????????
                message.put("resultCode", ValidationCode.SUCCESS.getCode());
                result.put(msgKey, message);
                
                result.put(dataKey, data);
            }
        } else {
            result.put(msgKey, data); //validation ?????? ?????????, ????????? ??????
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
