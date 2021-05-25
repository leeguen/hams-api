package com.iscreamedu.analytics.homelearn.api.hamsTutor.service.impl;

import com.iscreamedu.analytics.homelearn.api.common.mapper.CommonMapperTutor;
import com.iscreamedu.analytics.homelearn.api.common.security.CipherUtil;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationCode;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtilTutor;
import com.iscreamedu.analytics.homelearn.api.common.exception.NoDataException;
import com.iscreamedu.analytics.homelearn.api.hamsTutor.service.HamsTutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

@Service
public class HamsTutorServiceImpl implements HamsTutorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HamsTutorServiceImpl.class);
    private static final String TUTOR_NAMESPACE = "HamsTutor";

    private LinkedHashMap<String, Object> result;
    private String msgKey = "msg";
    private String dataKey = "data";

    @Autowired
    CommonMapperTutor mapper;

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
                ArrayList<String> positiveMsgList = new ArrayList<>();
                ArrayList<String> negativeMsgList = new ArrayList<>();

                positiveMsgList.add("positiveMessage1");
                positiveMsgList.add("positiveMessage2");
                positiveMsgList.add("positiveMessage3");
                positiveMsgList.add("positiveMessage4");
                positiveMsgList.add("positiveMessage5");

                negativeMsgList.add("negativeMessage1");
                negativeMsgList.add("negativeMessage2");
                negativeMsgList.add("negativeMessage3");
                negativeMsgList.add("negativeMessage4");
                negativeMsgList.add("negativeMessage5");

                aiDiagnosisRst.put("positivePointCnt",2);
                aiDiagnosisRst.put("negativePointCnt",3);
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

        @Override
        public Map getLrnBasicInfo(Map<String, Object> paramMap) throws Exception {
                Map<String,Object> data = new HashMap<>();
                checkRequiredWithDt(paramMap);

                //DB 조회
            LinkedHashMap<String,Object> lrnBasicInfo = (LinkedHashMap)mapper.get(paramMap,TUTOR_NAMESPACE + ".getLrnBasicInfo");

                data.put("lrnBasicInfo",lrnBasicInfo);
                setResult(dataKey,data);



            return result;
        }

    @Override
    public Map getLrnGrowthStt(Map<String, Object> paramMap) throws Exception {
            Map<String,Object> data = new HashMap<>();
            checkRequiredWithDt(paramMap);

            //DB 조회
            ArrayList<Map<String,Object>> lrnGrowthStt = new ArrayList<>();

            for(int i = 0; i<4; i++) {
                if(i != 0) {
                    //월별일때와 주별일때를 나눠서 구한다.
                    // 주별일땐 STARTDT 와 ENDDT를 각각 7씩 빼고, 월별일땐 한달씩 빼서 계산한다.
                    String endDate;
                    String startDate;
                    if(paramMap.get("isWM").equals("W")) {
                        endDate = subDate((String) paramMap.get("endDt"),-7,true,false);
                        startDate = subDate((String) paramMap.get("startDt"),-7,true,false);
                        paramMap.put("endDt",endDate);
                        paramMap.put("startDt",startDate);
                    }
                    else {
                        endDate = subDate((String) paramMap.get("endDt"),-1,false,true);
                        startDate = subDate((String) paramMap.get("startDt"),-1,false,false);
                        paramMap.put("endDt",endDate);
                        paramMap.put("startDt",startDate);
                    }
                    Map<String,Object> item = (Map)mapper.get(paramMap ,TUTOR_NAMESPACE + ".getLrnGrowthStt");
                    lrnGrowthStt.add(item);
                }
                else {
                    Map<String,Object> item = (Map)mapper.get(paramMap ,TUTOR_NAMESPACE + ".getLrnGrowthStt");
                    lrnGrowthStt.add(item);
                }
            }
            data.put("lrnGrowthStt",lrnGrowthStt);
            setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getLrnExStt(Map<String, Object> paramMap) throws Exception {
            Map<String,Object> data = new HashMap<>();
            checkRequiredWithDt(paramMap);

            //DB 조회
            LinkedHashMap<String,Object> lrnExStt = new LinkedHashMap<>();
            Map<String,Object> exStt = (Map)mapper.get(paramMap ,TUTOR_NAMESPACE + ".getLrnExSttEx");
            Map<String,Object> tmStt = (Map)mapper.get(paramMap ,TUTOR_NAMESPACE + ".getLrnExSttTm");

            lrnExStt.put("exStt",exStt);
            lrnExStt.put("tmStt",tmStt);

            data.put("lrnExStt",lrnExStt);
            setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getLrnExChart(Map<String, Object> paramMap) throws Exception {
            Map<String,Object> data = new HashMap<>();
            checkRequiredWithDt(paramMap);

            //DB 조회
            LinkedHashMap<String,Object> lrnExChart = new LinkedHashMap<>();

            List<Map<String,Object>> dayLrnTmList = (List)mapper.getList(paramMap ,TUTOR_NAMESPACE + ".getLrnExChartDay");
            List<Map<String,Object>> dayLrnTmMsgList = (List)mapper.getList(paramMap ,TUTOR_NAMESPACE + ".getLrnExChartDayMsg");
            List<Map<String,Object>> subjLrnTmList = (List)mapper.getList(paramMap ,TUTOR_NAMESPACE + ".getLrnExChartSubj");
            List<Map<String,Object>> subjLrnTmMsgList = (List)mapper.getList(paramMap ,TUTOR_NAMESPACE + ".getLrnExChartSubjMsg");

            lrnExChart.put("dayLrnTmList",dayLrnTmList);
            lrnExChart.put("dayLrnTmMsgList",dayLrnTmMsgList);
            lrnExChart.put("subjLrnTmList",subjLrnTmList);
            lrnExChart.put("subjLrnTmMsgList",subjLrnTmMsgList);

            data.put("lrnExChart",lrnExChart);
            setResult(dataKey,data);




        return result;
    }

    @Override
    public Map getLrnTimeLineList(Map<String, Object> paramMap) throws Exception {
            Map<String,Object> data = new HashMap<>();
            checkRequiredWithDt(paramMap);

            //DB 조회
            List<Map<String,Object>> lrnTmList = (List)mapper.getList(paramMap,TUTOR_NAMESPACE + ".getLrnTimeLineList");

            data.put("lrnTmList",lrnTmList);
            setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getExamStt(Map<String, Object> paramMap) throws Exception {
            Map<String,Object> data = new HashMap<>();
            checkRequiredWithDt(paramMap);

            //DB 조회
            LinkedHashMap<String,Object> examStt = (LinkedHashMap)mapper.get(paramMap ,TUTOR_NAMESPACE + ".getExamStt");

            data.put("examStt",examStt);
            setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getExamChart(Map<String, Object> paramMap) throws Exception {
            Map<String,Object> data = new HashMap<>();
            checkRequiredWithDt(paramMap);

            //DB 조회
            List<Map<String,Object>> examChart = (List)mapper.getList(paramMap ,TUTOR_NAMESPACE + ".getExamChart");

            data.put("examChart",examChart);
            setResult(dataKey,data);
        return result;
    }

    @Override
    public Map getExamList(Map<String, Object> paramMap) throws Exception {
            Map<String,Object> data = new HashMap<>();
            checkRequiredWithDt(paramMap);
            if(paramMap.get("types").toString().equals("")) {
                String[] allTypes = {"A","B","C","D","E","F","G","H","I"};
                paramMap.put("types",allTypes);
            }
            else {
                paramMap.put("types",paramMap.get("types").toString().split(","));
            }


            //DB 조회
            LinkedHashMap<String,Object> examList = new LinkedHashMap<>();
            Map<String,Object> totalCnt = (Map)mapper.get(paramMap,TUTOR_NAMESPACE + ".getExamListCnt");
            List<Map<String,Object>> list = (List)mapper.getList(paramMap,TUTOR_NAMESPACE + ".getExamList");

            examList.put("totalCnt",totalCnt.get("totalCnt"));
            examList.put("list",list);

            data.put("examList",examList);
            setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getAttStt(Map<String, Object> paramMap) throws Exception {
            Map<String,Object> data = new HashMap<>();
            checkRequiredWithDt(paramMap);

            //DB 조회
            LinkedHashMap<String,Object> attStt = new LinkedHashMap<>();
            LinkedHashMap<String,Object> attPtnAnalysis = (LinkedHashMap<String, Object>)mapper.get(paramMap,TUTOR_NAMESPACE + ".getAttSttAnalysis");
            List<Map<String,Object>> attPtnChart = (List)mapper.getList(paramMap,TUTOR_NAMESPACE + ".getAttSttChart");


            attStt.put("attPtnAnalysis",attPtnAnalysis);
            attStt.put("attPtnChart",attPtnChart);

            data.put("attStt",attStt);
            setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getCommMsgCd(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequired(paramMap);

        //DB 조회
        List<Map<String,Object>> commMsgCd = (List<Map<String,Object>>)mapper.getList(paramMap,TUTOR_NAMESPACE + ".getCommMsgCd");
        LinkedHashMap<String,Object> commMsgCdMap = new LinkedHashMap<>();

        for(Map item : commMsgCd) {
            commMsgCdMap.put(item.get("msgCd").toString(),item.get("msg"));
        }

        data.put("commMsgCd",commMsgCdMap);
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

    private String subDate(String paramDt,int day,boolean isW,boolean isWEnd) throws ParseException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");

        Date dt = form.parse(paramDt);
        cal.setTime(dt);

        if(isW) {
            cal.add(Calendar.DATE,day);
        }
        else {
            if(isWEnd) {
                cal.add(Calendar.MONTH,day);
                int dayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                cal.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            }
            else {
                cal.add(Calendar.MONTH,day);
            }
        }
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
