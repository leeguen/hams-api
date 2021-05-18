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

                //DB 조회
                LinkedHashMap<String,Object> aiDiagnosisRst = new LinkedHashMap<>();
                ArrayList<Map<String,Object>> msgCntList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorEx.selectAiDiagnosisRst");
                ArrayList<Map<String,Object>> msgList = (ArrayList<Map<String, Object>>) commonMapperTutor.getList(paramMap, "HamsTutorEx.selectAiDiagnosisRstList");
                LinkedHashMap<String,Object> msgInfo = (LinkedHashMap<String, Object>) commonMapperTutor.get(paramMap, "HamsTutorEx.selectAiDiagnosisRstMsg");
                ArrayList<String> positiveMsgList = new ArrayList<>();
                ArrayList<String> negativeMsgList = new ArrayList<>();
                
                int positivePointCnt = (msgCntList.get(1).get("cnt") == null) ? 0 : Integer.valueOf(msgCntList.get(1).get("cnt").toString());
                int negativePointCnt = (msgCntList.get(0).get("cnt") == null) ? 0 : Integer.valueOf(msgCntList.get(0).get("cnt").toString());
                
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
                					.replace("{c}", msgInfo.get("curQuesCnt").toString()));
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
                ArrayList<Map<String,Object>> aiWeakChapterGuide = new ArrayList<>();

                LinkedHashMap<String,Object> dummyMap = new LinkedHashMap<>();
                LinkedHashMap<String,Object> dummyMapTwo = new LinkedHashMap<>();
                ArrayList<String> dummyGuideMsgList = new ArrayList<>();

                dummyGuideMsgList.add("guideMessage1");
                dummyGuideMsgList.add("guideMessage2");
                dummyGuideMsgList.add("guideMessage3");
                dummyGuideMsgList.add("guideMessage4");

                dummyMap.put("subjCd","C01");
                dummyMap.put("title","수학 3학년 1학기 2단원 평면도형");
                dummyMap.put("guideMsgList",dummyGuideMsgList);

                dummyMapTwo.put("subjCd","C02");
                dummyMapTwo.put("title","국어 2학년 1학기 1단원 어휘");
                dummyMapTwo.put("guideMsgList",dummyGuideMsgList);

                aiWeakChapterGuide.add(dummyMap);
                aiWeakChapterGuide.add(dummyMapTwo);

                data.put("aiWeakChapterGuide",aiWeakChapterGuide);
                setResult(dataKey,data);

            return result;
        }

        @Override
        public Map getAiRecommendQuestion(Map<String, Object> paramMap) throws Exception {
                Map<String,Object> data = new HashMap<>();
                checkRequiredWithDt(paramMap);

                //DB 조회
                ArrayList<Map<String,Object>> aiRecommendQuestion = new ArrayList<>();
                LinkedHashMap<String,Object> dummyMap = new LinkedHashMap<>();
                LinkedHashMap<String,Object> dummyMapTwo = new LinkedHashMap<>();

                dummyMap.put("p","g4GeLOLo84wAaoI6uSHEuw==");
                dummyMap.put("subjCd","C01");
                dummyMap.put("recommendType","실수한 문제");
                dummyMap.put("smtDttm","2020-11-26 19:10");
                dummyMap.put("examType","실력평가");
                dummyMap.put("examNm","[3-2] 9단원_26장_글");
                dummyMap.put("examId",123456);

                dummyMapTwo.put("p","g4GeLOLo84wAaoI6uSHEuw==");
                dummyMapTwo.put("subjCd","C02");
                dummyMapTwo.put("recommendType","실수한 문제");
                dummyMapTwo.put("smtDttm","2020-11-26 19:10");
                dummyMapTwo.put("examType","실력평가");
                dummyMapTwo.put("examNm","[3-2] 9단원_26장_글");
                dummyMapTwo.put("examId",98765);

                aiRecommendQuestion.add(dummyMap);
                aiRecommendQuestion.add(dummyMapTwo);

                data.put("aiRecommendQuestion",aiRecommendQuestion);
                setResult(dataKey,data);

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
