package com.iscreamedu.analytics.homelearn.api.hamsTutor.service.impl;

import com.iscreamedu.analytics.homelearn.api.common.mapper.CommonMapperTutor;
import com.iscreamedu.analytics.homelearn.api.common.security.CipherUtil;
import com.iscreamedu.analytics.homelearn.api.common.service.ExternalAPIService;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationCode;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtilTutor;
import com.iscreamedu.analytics.homelearn.api.common.exception.NoDataException;
import com.iscreamedu.analytics.homelearn.api.hamsTutor.service.HamsTutorExService;
import com.iscreamedu.analytics.homelearn.api.hamsTutor.service.HamsTutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	ExternalAPIService externalAPIservice;

        @Override
        public Map getSettleInfoPredictionStt (Map<String,Object> paramMap) throws Exception {
            Map<String,Object> data = new HashMap<>();
            checkRequired(paramMap);

            //DB 조회
            LinkedHashMap<String,Object> settleInfoPredictionStt = new LinkedHashMap<>(); //DB결과값
            settleInfoPredictionStt.put("good",17);
            settleInfoPredictionStt.put("maintaining",10);
            settleInfoPredictionStt.put("encouragement",2);

            //DB 데이터 주입
            if(settleInfoPredictionStt != null) {
                data.put("settleInfoPredictionStt",settleInfoPredictionStt);
            }
            setResult(dataKey,data);

            //리턴
            return result;

        }

        @Override
        public Map getAiDiagnosisRst(Map<String,Object> paramMap) throws Exception{
                Map<String,Object> data = new HashMap<>();
                checkRequiredWithDt(paramMap);
                
                // 시연용 로직 추가 
                LinkedHashMap<String,Object> studInfo = (LinkedHashMap<String, Object>) commonMapperTutor.get(paramMap, "HamsTutorEx.selectStudInfo");
                
                int endDay = Integer.valueOf(paramMap.get("endDt").toString().replace("-", ""));
                int studStartDay = Integer.valueOf(studInfo.get("startDt").toString().replace("-", ""));
                
                if(endDay < studStartDay) {
                	String startDt = paramMap.get("startDt").toString();
                	String endDt = paramMap.get("endDt").toString();
                	String today = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1).toString();
                	
                	startDt = startDt.replace(startDt.substring(0,4), studInfo.get("startDt").toString().substring(0, 4));
                	endDt = endDt.replace(endDt.substring(0,4), studInfo.get("startDt").toString().substring(0, 4));
                	
                	if(Integer.valueOf(endDt.replace("-", "")) > Integer.valueOf(today.replace("-", ""))) {
                		endDt = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(7).toString();
                		endDt = today;
                	}
                	
                	paramMap.put("startDt", startDt);
                	paramMap.put("endDt", endDt);
                }
                // 시연용 로직 추가 
                
                //DB 조회
                LinkedHashMap<String,Object> aiDiagnosisRst = new LinkedHashMap<>();
                ArrayList<Map<String,Object>> msgCntList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorEx.selectAiDiagnosisRst");
                ArrayList<Map<String,Object>> msgList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorEx.selectAiDiagnosisRstList");
                LinkedHashMap<String,Object> msgInfo = (LinkedHashMap<String, Object>) commonMapperTutor.get(paramMap, "HamsTutorEx.selectAiDiagnosisRstMsg");
                ArrayList<String> positiveMsgList = new ArrayList<>();
                ArrayList<String> negativeMsgList = new ArrayList<>();
                
                int positivePointCnt = 0;
                int negativePointCnt = 0;
                
                if(msgCntList.size() > 1) {
                	positivePointCnt = (msgCntList.get(1).get("cnt") == null) ? 0 : Integer.valueOf(msgCntList.get(1).get("cnt").toString());
                	negativePointCnt = (msgCntList.get(0).get("cnt") == null) ? 0 : Integer.valueOf(msgCntList.get(0).get("cnt").toString());
                }else if(msgCntList.size() > 0) {
                	if("G".equals(msgCntList.get(0).get("msgType").toString())) {
                		positivePointCnt = Integer.valueOf(msgCntList.get(0).get("cnt").toString());
                	}else {
                		negativePointCnt = Integer.valueOf(msgCntList.get(0).get("cnt").toString());
                	}
                }
                
                
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
        				for(int i = 0; i < msgList.size(); i++) {
        					if("CPG0012".equals(msgList.get(i).get("msgCd"))) {
        						msgList.remove(i);
        					}
        				}
        				
        				for(int i = 0; i < msgList.size(); i++) {
        					if(positivePointCnt > 0 && "CPG0013".equals(msgList.get(i).get("msgCd"))) {
        						msgList.remove(i);
        					}
        				}
        				
        				if(positivePointCnt == 0) {
        					positivePointCnt += 1;
        				}
        			}else {
        				for(int i = 0; i < msgList.size(); i++) {
        					if(positivePointCnt == 5 && "CPG0012".equals(msgList.get(i).get("msgCd"))){
        						msgList.remove(i);
        					}
        				}
        				
        				for(int i = 0; i < msgList.size(); i++) {
        					if("CPG0013".equals(msgList.get(i).get("msgCd"))) {
        						msgList.remove(i);
        					}
        				}
        				
        				if(positivePointCnt < 5) {
        					positivePointCnt += 1;
        				}
        			}
                }
                
                for(Map<String, Object> item : msgList) {
                	if("G".equals(item.get("msgType"))) {
                		if("CPG0003".equals(item.get("msgCd")) || "CPG0004".equals(item.get("msgCd")) || "CPG0005".equals(item.get("msgCd")) || "CPG0006".equals(item.get("msgCd"))) {
                			item.put("msg", item.get("msg").toString()
                					.replace("{a}", msgInfo.get("explCnt").toString())
                					.replace("{b}", msgInfo.get("psExplCnt").toString())
                					.replace("{c}", msgInfo.get("crtRt").toString()));
                		}else if("CPG0010".equals(item.get("msgCd"))) {
                			item.put("msg", item.get("msg").toString()
                					.replace("{a}", msgInfo.get("aLrnExCnt").toString())
                					.replace("{b}", msgInfo.get("aLrnSubjCd").toString()));
                		}else if("CPG0012".equals(item.get("msgCd"))) {
                			item.put("msg", item.get("msg").toString()
                					.replace("{a}", String.valueOf(bookCnt)));
                		}
                		
                		positiveMsgList.add(item.get("msg").toString());
                	}else {
                		if("CPB0004".equals(item.get("msgCd")) ) {
                			item.put("msg", item.get("msg").toString()
                					.replace("{b}", msgInfo.get("exRt").toString()));
                		}else if("CPB0006".equals(item.get("msgCd"))) {
                			item.put("msg", item.get("msg").toString()
                					.replace("{b}", msgInfo.get("nLrnExCnt").toString()));
                		}else if("CPB0007".equals(item.get("msgCd"))) {
                			item.put("msg", item.get("msg").toString()
                					.replace("{b}", msgInfo.get("crtRt").toString()));
                		}else if("CPB0008".equals(item.get("msgCd"))) {
                			item.put("msg", item.get("msg").toString()
                					.replace("{b}", msgInfo.get("explCnt").toString()));
                		}else if("CPB0009".equals(item.get("msgCd"))) {
                			item.put("msg", item.get("msg").toString()
                					.replace("{c}", msgInfo.get("incrtNtCnt").toString()));
                		}else if("CPB0010".equals(item.get("msgCd"))) {
                			item.put("msg", item.get("msg").toString()
                					.replace("{c}", msgInfo.get("skpQuesCnt").toString()));
                		}else if("CPB0011".equals(item.get("msgCd"))) {
                			item.put("msg", item.get("msg").toString()
                					.replace("{c+d}", msgInfo.get("curQuesCnt").toString()));
                		}else if("CPB0012".equals(item.get("msgCd"))) {
                			item.put("msg", item.get("msg").toString()
                					.replace("{c}", msgInfo.get("gucQuesCnt").toString()));
                		}else if("CPB0013".equals(item.get("msgCd"))) {
                			item.put("msg", item.get("msg").toString()
                					.replace("{b}", msgInfo.get("over25MinLrnCnt").toString()));
                		}else if("CPB0014".equals(item.get("msgCd"))) {
                			item.put("msg", item.get("msg").toString()
                					.replace("{b}", msgInfo.get("below5MinLrnCnt").toString()));
                		}
                		
                		negativeMsgList.add(item.get("msg").toString());
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
                ArrayList<Map<String,Object>> aiRecommendQuestion = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorEx.selectAiRecommendQuestion");
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
                }

                data.put("aiRecommendQuestion",aiRecommendQuestion);
                setResult(dataKey,data);

            return result;
        }
        
        @Override
        public Map getAiRecommendCourse (Map<String,Object> paramMap) throws Exception {
            Map<String,Object> data = new HashMap<>();
            checkRequired(paramMap);
            
            LocalDate endDate = LocalDate.parse(paramMap.get("endDt").toString());
            LocalDate beforeDate = endDate.minusMonths(1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
            int yymm = 0;
            int beforeYymm = 0;
            System.out.println(endDate.getMonthValue());
            
            if(endDate.getMonthValue() % 2 == 1) {
            	yymm  = Integer.parseInt(endDate.format(formatter));
            	beforeYymm = Integer.parseInt(beforeDate.format(formatter));
            	
            	paramMap.put("yymm", yymm);
            	paramMap.put("exYymm", beforeYymm);
            }else {
            	beforeYymm = Integer.parseInt(beforeDate.format(formatter));
            	
            	paramMap.put("yymm", beforeYymm);
            	paramMap.put("exYymm", beforeYymm);
            }

            //DB 조회
            LinkedHashMap<String,Object> aiRecmmendCourseMap = (LinkedHashMap<String, Object>) commonMapperTutor.get(paramMap, "HamsTutorEx.selectAiRecommendCourse");
            ArrayList<Map<String,Object>> aiRecommendCourseList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorEx.selectAiRecommendCourseList");

            //DB 데이터 주입
            
            if(aiRecmmendCourseMap != null) {
            	if(aiRecmmendCourseMap.get("exRt") == null) {
            		aiRecmmendCourseMap.put("exRt", null);
            	}
            	
            	if(aiRecmmendCourseMap.get("crtRt") == null) {
            		aiRecmmendCourseMap.put("crtRt", null);
            	}
            	
            	aiRecmmendCourseMap.put("subjCourseList", aiRecommendCourseList);
            }
            
            data.put("aiRecommenCourse",aiRecmmendCourseMap);
            setResult(dataKey,data);

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
