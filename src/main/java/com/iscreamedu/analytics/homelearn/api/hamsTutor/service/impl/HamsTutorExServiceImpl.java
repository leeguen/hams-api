package com.iscreamedu.analytics.homelearn.api.hamsTutor.service.impl;

import com.iscreamedu.analytics.homelearn.api.common.mapper.CommonMapperLrnDm;
import com.iscreamedu.analytics.homelearn.api.common.mapper.CommonMapperTutor;
import com.iscreamedu.analytics.homelearn.api.common.security.CipherUtil;
import com.iscreamedu.analytics.homelearn.api.common.service.ExternalAPIService;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationCode;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtilTutor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iscreamedu.analytics.homelearn.api.common.exception.NoDataException;
import com.iscreamedu.analytics.homelearn.api.hamsTutor.service.HamsTutorExService;

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
	ExternalAPIService externalAPIservice;
    
    @Value("${extapi.hl.tutor.new.ai.recommend.url}")
	String NEW_RECOMMEND_API; //AI 추천 정보 API 주소

        @Override
        public Map getSettleInfoPredictionStt (Map<String,Object> paramMap) throws Exception {
            Map<String,Object> data = new HashMap<>();
            checkTchrRequired(paramMap);

            //DB 조회
            ArrayList<Map<String,Object>> settleInfoPredictionStt = new ArrayList();

            ArrayList<Map<String,Object>> predictionCdList = new ArrayList();
            ArrayList<Map<String,Object>> predictionCnt = new ArrayList();
            ArrayList<Map<String,Object>> predictionStudList = new ArrayList();
            
            String today = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1).toString();
            paramMap.put("dt", today);
            
            if(paramMap.containsKey("schType") && paramMap.get("schType").toString().toLowerCase().equals("ms")) {
            	paramMap.put("dt", today.replace("-",""));
            	predictionCdList = (ArrayList<Map<String, Object>>) commonMapperLrnDm.getList(paramMap, "LrnDm.selectPredictionCd");
                predictionCnt = (ArrayList<Map<String, Object>>) commonMapperLrnDm.getList(paramMap, "LrnDm.selectPredictionCount");
                predictionStudList = (ArrayList<Map<String, Object>>) commonMapperLrnDm.getList(paramMap, "LrnDm.selectPredictionList");
            } else {
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
            

            //DB 데이터 주입
            data.put("settleInfoPredictionStt",settleInfoPredictionStt);
            setResult(dataKey,data);

            //리턴
            return result;

        }

        @Override
        public Map getAiDiagnosisRst(Map<String,Object> paramMap) throws Exception{
                Map<String,Object> data = new HashMap<>();
                checkRequiredWithDt(paramMap);
                
                //DB 조회
                LinkedHashMap<String,Object> aiDiagnosisRst = new LinkedHashMap<>();
                //ArrayList<Map<String,Object>> msgCntList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorEx.selectAiDiagnosisRst");
                ArrayList<Map<String,Object>> msgList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorEx.selectAiDiagnosisRstList");
                LinkedHashMap<String,Object> msgInfo = (LinkedHashMap<String, Object>) commonMapperTutor.get(paramMap, "HamsTutorEx.selectAiDiagnosisRstMsg");
                ArrayList<String> positiveMsgCdList = new ArrayList<>();
                ArrayList<String> positiveMsgList = new ArrayList<>();
                ArrayList<String> negativeMsgCdList = new ArrayList<>();
                ArrayList<String> negativeMsgList = new ArrayList<>();
                
                int positivePointCnt = 0;
                int negativePointCnt = 0;
                
                positiveMsgCdList = getPositiveMsgList(msgInfo);
                negativeMsgCdList = getNegativeMsgList(msgInfo);
                
                
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
                	negativePointCnt += 1;
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
                				msg = msg.replace("{c+d}", msgInfo.get("curQuesCnt").toString());
                			}else if("CPB0012".equals(msgCd)) {
                				msg = msg.replace("{c}", msgInfo.get("gucQuesCnt").toString());
                			}else if("CPB0013".equals(msgCd)) {
                				msg = msg.replace("{b}", msgInfo.get("oLrnCnt").toString());
                			}else if("CPB0014".equals(msgCd)) {
                				msg = msg.replace("{b}", msgInfo.get("bLrnCnt").toString());
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

                //DB 조회
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

                //DB 조회
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
		         		
		         		//DB 조회
		         		LinkedHashMap<String,Object> aiRecmmendCourseMap = (LinkedHashMap<String, Object>) commonMapperTutor.get(paramMap, "HamsTutorEx.selectAiRecommendCourse");
		         		ArrayList<Map<String,Object>> aiRecommendCourseList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorEx.selectAiRecommendCourseRstList");
		         		ArrayList<Map<String,Object>> resultList = new ArrayList();
		         		
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
//            //DB 조회
//            LinkedHashMap<String,Object> aiRecmmendCourseMap = (LinkedHashMap<String, Object>) commonMapperTutor.get(paramMap, "HamsTutorEx.selectAiRecommendCourse");
//            ArrayList<Map<String,Object>> aiRecommendCourseList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorEx.selectAiRecommendCourseList");
//            
//            // 테스트용 로직 -> 운영 배포 전 삭제 필요
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
//            //DB 데이터 주입
//            
//            if(aiRecmmendCourseMap != null) {
//            	aiRecmmendCourseMap.put("subjCourseList", aiRecommendCourseList);
//            }
//            

            //리턴
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
            

            //리턴
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

            //리턴
            return result;

        }
        

    //p,startDt,endDt 비교 메서드
    private void checkRequired(Map<String,Object> params) throws Exception {
        ValidationUtilTutor vu = new ValidationUtilTutor();
        //필수값 체크
        vu.checkRequired(new String[] {"p"},params);

        //복호화
        String[] encodedArr = getDecodedParam(params.get("p").toString());
        String encodedStudId = encodedArr[1];

        //DB params
        params.put("studId",encodedStudId);
    }
    
    //p,startDt,endDt 비교 메서드
    private void checkTchrRequired(Map<String,Object> params) throws Exception {
        ValidationUtilTutor vu = new ValidationUtilTutor();
        //필수값 체크
        vu.checkRequired(new String[] {"p"},params);

        //복호화
        String[] encodedArr = getDecodedParam(params.get("p").toString());
        String encodedTchrId = encodedArr[0];

        //DB params
        params.put("tchrId",encodedTchrId);
    }

    //p,startDt,endDt 비교 메서드
    private void checkRequiredWithDt(Map<String,Object> params) throws Exception {
        ValidationUtilTutor vu = new ValidationUtilTutor();
        //필수값 체크
        vu.checkRequired(new String[] {"p","startDt","endDt"},params);
        vu.isDate("startDt",(String)params.get("startDt"));
        vu.isDate("endDt",(String)params.get("endDt"));

        //복호화
        String[] encodedArr = getDecodedParam(params.get("p").toString());
        String encodedStudId = encodedArr[1];

        //DB params
        params.put("studId",encodedStudId);
    }
    // getLrnGrowthStt의 subjCrtRt dummy map을 만드는 메서드
    private Map createSubjCrtRtMap(String subj,int score) {
            LinkedHashMap<String,Object> resultMap = new LinkedHashMap<>();
            resultMap.put("subjCd",subj);
            resultMap.put("crtRt",score);
            return  resultMap;
    }
    //getLrnExChart의 day,subj dummy map을 만드는 메서드
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
    
  //getLrnExChart의 day,subj dummy map을 만드는 메서드
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
    
    //칭찬포인트 메시지 산출
    private ArrayList getPositiveMsgList(Map<String, Object> msgData) {
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
    	
    	if(ptnRt >= 50) {
    		msgCdList.add("CPG0009");
    	}
    	
    	if(aLrnCnt > 0 && msgCdList.size() < 6) {
    		msgCdList.add("CPG0010");
    	}
    	
    	if(slvCnt <= 5 && slvCnt > -1 && msgCdList.size() < 6) {
    		msgCdList.add("CPG0011");
    	}
    	
    	return msgCdList;
    }
    
    
    // 처방포인트 메시지 산출
    private ArrayList getNegativeMsgList(Map<String, Object> msgData) {
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
    		
    		if(exRt < 100) {
    			msgCdList.add("CPB0006");
    		}
    		
    		if(explCnt >= 1 && crtRt < 50) {
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
    	}
    	
    	return msgCdList;
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
        } else {
            message.put("resultCode", ValidationCode.SUCCESS.getCode());
            result.put(msgKey, message);
            result.put(dataKey, data);
        }
    }
    
    /**
     * 서비스단에서 리턴되는 결과(메시지,데이터 object를 포함한 result)세팅.
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
                //조회결과가 없는 경우 메시지만 나감.
                message.put("resultCode", ValidationCode.NO_DATA.getCode());
                result.put(msgKey, message);
            } else {
                //정상데이터, 정상메시지
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
