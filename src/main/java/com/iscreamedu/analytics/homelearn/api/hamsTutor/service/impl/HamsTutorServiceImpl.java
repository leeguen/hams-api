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
                LinkedHashMap<String,Object> lrnBasicInfo = new LinkedHashMap<>();
                LinkedHashMap<String,Object> msg = new LinkedHashMap<>();

                lrnBasicInfo.put("studId",194909);
                lrnBasicInfo.put("gender","M");
                lrnBasicInfo.put("studNm","김홈런");
                lrnBasicInfo.put("loginId","test01");
                lrnBasicInfo.put("schlNm","홈런초등학교");
                lrnBasicInfo.put("grade",5);

                msg.put("positiveLrnMsg","칭찬-학습태도 메시지");
                msg.put("positiveExamMsg","칭찬-평가 메시지");
                msg.put("negativeLrnMsg","처방-학습태도 메시지");
                msg.put("negativeExamMsg","처방-평가 메시지");

                lrnBasicInfo.put("msg",msg);

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
            ArrayList<Map<String,Object>> subjCrtRt = new ArrayList<>();
            LinkedHashMap<String,Object> dummyMap = new LinkedHashMap<>();



            for(int i = 0; i<4; i++) {
                if(i != 0) {
                    String paramDate = subDate((String) paramMap.get("endDt"),-7);
                    paramMap.put("endDt",paramDate);
                    Map<String,Object> item = (Map)mapper.get(paramMap ,TUTOR_NAMESPACE + ".getLrnGrowthStt");
                    lrnGrowthStt.add(item);
                }
                else {
                    Map<String,Object> item = (Map)mapper.get(paramMap ,TUTOR_NAMESPACE + ".getLrnGrowthStt");
                    lrnGrowthStt.add(item);
                }
                System.out.println("index :::" + i);
                System.out.println("endDt :::" + paramMap.get("endDt"));
            }


//            subjCrtRt.add(createSubjCrtRtMap("C01",85));
//            subjCrtRt.add(createSubjCrtRtMap("C02",80));
//            subjCrtRt.add(createSubjCrtRtMap("C03",90));
//            subjCrtRt.add(createSubjCrtRtMap("C04",95));
//            subjCrtRt.add(createSubjCrtRtMap("C05",70));
//            subjCrtRt.add(createSubjCrtRtMap("C06",75));
//
//            dummyMap.put("yymm",202012);
//            dummyMap.put("wk",2);
//            dummyMap.put("exRt",90);
//            dummyMap.put("crtRt",95);
//            dummyMap.put("subjCrtRt",subjCrtRt);
//            dummyMap.put("top10AvgExRt",80);
//            dummyMap.put("top10AvgCrtRt",90);
//            dummyMap.put("grpAvgExRt",80);
//            dummyMap.put("grpAvgCrtRt",90);
//
//            lrnGrowthStt.add(dummyMap);

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
//            LinkedHashMap<String,Object> dummyDayMap = new LinkedHashMap<>();
//            LinkedHashMap<String,Object> dummySubjMap = new LinkedHashMap<>();
//
//            ArrayList<Map<String,Object>> day = new ArrayList<>();
//            ArrayList<Map<String,Object>> subj = new ArrayList<>();
//
//            ArrayList<Map<String,Object>> dayLrnTmList = new ArrayList<>();
//            ArrayList<Map<String,Object>> subjLrnTmList = new ArrayList<>();
//
//            dummyDayMap.put("dt","2020-12-1");
//            dummyDayMap.put("totalLrnTm",1140);
//
//            dayLrnTmList.add(createDaySubjMap(
//                    "C01",
//                    200,
//                    "[과학(2015개정)][과학 3-2 학교공부예복습]",
//                    "2장. 탐구를 실행해 볼까요, 탐구 결과를 발표해 볼까요, 새로운 탐구를 시작해 볼까요",
//                    true));
//            dayLrnTmList.add(createDaySubjMap(
//                    "C02",
//                    270,
//                    "[과학(2015개정)][과학 3-2 학교공부예복습]",
//                    "2장. 탐구를 실행해 볼까요, 탐구 결과를 발표해 볼까요, 새로운 탐구를 시작해 볼까요",
//                    true));
//            dayLrnTmList.add(createDaySubjMap(
//                    "C03",
//                    300,
//                    "[과학(2015개정)][과학 3-2 학교공부예복습]",
//                    "2장. 탐구를 실행해 볼까요, 탐구 결과를 발표해 볼까요, 새로운 탐구를 시작해 볼까요",
//                    true));
//            dayLrnTmList.add(createDaySubjMap(
//                    "N01",
//                    370,
//                    "[과학(2015개정)][과학 3-2 학교공부예복습]",
//                    "2장. 탐구를 실행해 볼까요, 탐구 결과를 발표해 볼까요, 새로운 탐구를 시작해 볼까요",
//                    true));
//
//            dummyDayMap.put("subjLrnTm",dayLrnTmList);
//            day.add(dummyDayMap);
//
//            dummySubjMap.put("subjCd","C01");
//            dummySubjMap.put("totalLrnTm",1320);
//
//            subjLrnTmList.add(createDaySubjMap(
//                    "C0101",
//                    570,
//                    "[과학(2015개정)][과학 3-2 학교공부예복습]",
//                    "2장. 탐구를 실행해 볼까요, 탐구 결과를 발표해 볼까요, 새로운 탐구를 시작해 볼까요",
//                    false));
//            subjLrnTmList.add(createDaySubjMap(
//                    "C0102",
//                    650,
//                    "[과학(2015개정)][과학 3-2 학교공부예복습]",
//                    "2장. 탐구를 실행해 볼까요, 탐구 결과를 발표해 볼까요, 새로운 탐구를 시작해 볼까요",
//                    false));
//
//            dummySubjMap.put("subjLrnTm",subjLrnTmList);
//            subj.add(dummySubjMap);
//
//            lrnExChart.put("day",day);
//            lrnExChart.put("subj",subj);

            data.put("lrnExChart",lrnExChart);
            setResult(dataKey,data);




        return result;
    }

    @Override
    public Map getLrnTimeLineList(Map<String, Object> paramMap) throws Exception {
            Map<String,Object> data = new HashMap<>();
            checkRequiredWithDt(paramMap);

            //DB 조회
            ArrayList<Map<String,Object>> lrnTmList = new ArrayList<>();
            LinkedHashMap<String,Object> dummyMap = new LinkedHashMap<>();

            dummyMap.put("serviceId",43837);
            dummyMap.put("subjCd","C01");
            dummyMap.put("exDttm","2020.05.05(화) 12:03 ~ 12:17");
            dummyMap.put("category","예복습 > 5학년 1학기 > 국어 > 2단원 문단의 짜임 > 5장 문장 만들기");
            dummyMap.put("lrnSec",3412);
            dummyMap.put("stdLrnTmCd",1);
            dummyMap.put("exType","계획");
            dummyMap.put("planDt","2020.05.05(화)");

            lrnTmList.add(dummyMap);

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
//            examStt.put("crtRt",80);
//            examStt.put("top10AvgCrtRt",92);
//            examStt.put("grpAvgCrtRt",75);
//            examStt.put("incrtNoteNcCnt",0);
//            examStt.put("explCnt",10);
//            examStt.put("ansQuesCnt",83);
//            examStt.put("crtQuesCnt",62);
//            examStt.put("imprvSlvHabitCnt",12);
//            examStt.put("skipQuesCnt",4);
//            examStt.put("cursoryQuesCnt",4);
//            examStt.put("guessQuesCnt",4);
//            examStt.put("mistakenQuesCnt",4);

            data.put("examStt",examStt);
            setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getExamChart(Map<String, Object> paramMap) throws Exception {
            Map<String,Object> data = new HashMap<>();
            checkRequiredWithDt(paramMap);

            //DB 조회
            LinkedHashMap<String,Object> examChart = new LinkedHashMap<>();
            List<Map<String,Object>> chartData = (List)mapper.getList(paramMap ,TUTOR_NAMESPACE + ".getExamChart");
            //ArrayList<Map<String,Object>> chartData = new ArrayList<>();
            //LinkedHashMap<String,Object> dummyMap = new LinkedHashMap<>();
            //LinkedHashMap<String,Object> dummyMapTwo = new LinkedHashMap<>();

//            examChart.put("maxSubjCd","C02");
//            examChart.put("minSubjCd","C05");
//
//            dummyMap.put("subjCd","C01");
//            dummyMap.put("crtRt",80);
//            dummyMap.put("top10AvgCrtRt",90);
//            dummyMap.put("grpAvgCrtRt",85);
//            dummyMap.put("explCnt",10);
//            dummyMap.put("ansQuesCnt",83);
//            dummyMap.put("crtQuesCnt",62);
//            dummyMap.put("incrtNoteNcCnt",0);
//            dummyMap.put("skipQuesCnt",4);
//            dummyMap.put("cursoryQuesCnt",4);
//            dummyMap.put("guessQuesCnt",4);
//            dummyMap.put("mistakenQuesCnt",4);
//
//            dummyMapTwo.put("subjCd","C02");
//            dummyMapTwo.put("crtRt",85);
//            dummyMapTwo.put("top10AvgCrtRt",92);
//            dummyMapTwo.put("grpAvgCrtRt",80);
//            dummyMapTwo.put("explCnt",13);
//            dummyMapTwo.put("ansQuesCnt",90);
//            dummyMapTwo.put("crtQuesCnt",77);
//            dummyMapTwo.put("incrtNoteNcCnt",2);
//            dummyMapTwo.put("skipQuesCnt",1);
//            dummyMapTwo.put("cursoryQuesCnt",2);
//            dummyMapTwo.put("guessQuesCnt",3);
//            dummyMapTwo.put("mistakenQuesCnt",4);
//
//            chartData.add(dummyMap);
//            chartData.add(dummyMapTwo);
            examChart.put("chartData",chartData);

            data.put("examChart",examChart);
            setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getExamList(Map<String, Object> paramMap) throws Exception {
            Map<String,Object> data = new HashMap<>();
            checkRequiredWithDt(paramMap);

            //DB 조회
            ArrayList<Map<String,Object>> examList = new ArrayList<>();
            LinkedHashMap<String,Object> dummyMap = new LinkedHashMap<>();

            LinkedHashMap<String,Object> answer = new LinkedHashMap<>();
            LinkedHashMap<String,Object> wrongAnswer = new LinkedHashMap<>();

            dummyMap.put("examCd",9977);
            dummyMap.put("smtId", 83050805);
            dummyMap.put("stuId", 4958);
            dummyMap.put("subjCd","C01");
            dummyMap.put("smtDttm","2020-05-29 10:33:38");
            dummyMap.put("type","A");
            dummyMap.put("examNm","[3-1] 22장. 교통수단의 발달");
            dummyMap.put("crtRt",80);
            dummyMap.put("crtQuesCnt",4);
            dummyMap.put("quesCnt",5);

            answer.put("crtQues","1,3,5");
            answer.put("guessQues","4");

            wrongAnswer.put("skipQues",null);
            wrongAnswer.put("guessQues","1,2");
            wrongAnswer.put("cursoryQues","2");
            wrongAnswer.put("incrtQues","3");
            wrongAnswer.put("mistakenQues","1");

            dummyMap.put("answer",answer);
            dummyMap.put("wrongAnswer",wrongAnswer);
            examList.add(dummyMap);

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
            LinkedHashMap<String,Object> attPtnAnalysis = new LinkedHashMap<>();

            ArrayList<Map<String,Object>> attPtnChart = new ArrayList<>();
            LinkedHashMap<String,Object> attPtnChartDummyMap = new LinkedHashMap<>();
            LinkedHashMap<String,Object> attPtnChartDummyMapTwo = new LinkedHashMap<>();

            attPtnAnalysis.put("attRt",83);
            attPtnAnalysis.put("attRtMsg","주간 출석률이 높아요.");
            attPtnAnalysis.put("lrnPlanDtCnt",5);
            attPtnAnalysis.put("attDtCnt",4);
            attPtnAnalysis.put("loginPtn","규칙적");

            attPtnChartDummyMap.put("dt","2020-12-01");
            attPtnChartDummyMap.put("loginTm","09:32");
            attPtnChartDummyMap.put("planYn","Y");

            attPtnChartDummyMapTwo.put("dt","2020-12-02");
            attPtnChartDummyMapTwo.put("loginTm","09:12");
            attPtnChartDummyMapTwo.put("planYn","N");

            attPtnChart.add(attPtnChartDummyMap);
            attPtnChart.add(attPtnChartDummyMapTwo);

            attStt.put("attPtnAnalysis",attPtnAnalysis);
            attStt.put("attPtnChart",attPtnChart);

            data.put("attStt",attStt);
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
