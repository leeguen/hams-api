package com.iscreamedu.analytics.homelearn.api.hamsTutor.service.impl;

import com.iscreamedu.analytics.homelearn.api.common.security.CipherUtil;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationCode;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtilTutor;
import com.iscreamedu.analytics.homelearn.api.hamsTutor.service.HamsTutorVrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HamsTutorVrServiceImpl implements HamsTutorVrService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HamsTutorVrServiceImpl.class);

    private LinkedHashMap<String, Object> result;
    private String msgKey = "msg";
    private String dataKey = "data";


    @Override
    public Map getVisionReportPublishedInfo(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequired(paramMap,true);

        //DB 조회
        LinkedHashMap<String,Integer> visionReportPublishedInfo = new LinkedHashMap<>();
        visionReportPublishedInfo.put("year",2020);
        visionReportPublishedInfo.put("term",1);

        data.put("visionReportPublishedInfo",visionReportPublishedInfo);
        setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getVisionBasicInfo(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequired(paramMap,false);

        //DB 조회
        LinkedHashMap<String,Object> visionBasicInfo = new LinkedHashMap<>();
        LinkedHashMap<String,String> msg = new LinkedHashMap<>();

        visionBasicInfo.put("studId",194909);
        visionBasicInfo.put("gender","M");
        visionBasicInfo.put("studNm","김홈런");
        visionBasicInfo.put("loginId","minny0608");
        visionBasicInfo.put("schlNm","홈런초등학교");
        visionBasicInfo.put("grade",5);

        msg.put("firstCommonMsg","첫 번째 공통 메시지");
        msg.put("positiveLrnMsg","진단 학습수행 메시지");
        msg.put("positiveExamMaxSubjMsg","진단 평가 잘하는 과목 메시지");
        msg.put("positiveExamMinSubjMsg","진단 평가 어려운 과목 메시지");
        msg.put("positiveExamReviewSubjMsg","진단 평가 복습 필요 과목 메시지");
        msg.put("positiveExamIncrtNtMsg","진단 평가 오답노트 메시지");
        msg.put("negativeLrnMsg","처방 학습량 메시지");
        msg.put("negativeNSubjRecommendMsg","처방 특별학습 추천 메시지");
        msg.put("negativeReviewSubjMsg","처방 과목 복습 추천");
        msg.put("negativePreviewSubjMsg","처방 과목 예습 추천");
        msg.put("lastCommonMsg","마지막 공통 메시지");

        visionBasicInfo.put("msg",msg);

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
        ArrayList<Map<String,Object>> subjCrtRt = new ArrayList<>();
        LinkedHashMap<String,Object> dummyMap = new LinkedHashMap<>();

        dummyMap.put("yymm",202012);
        dummyMap.put("exRt",90);
        dummyMap.put("crtRt",95);

        subjCrtRt.add(createSubjCrtRtMap("C01",85));
        subjCrtRt.add(createSubjCrtRtMap("C02",80));
        subjCrtRt.add(createSubjCrtRtMap("C03",90));
        subjCrtRt.add(createSubjCrtRtMap("C04",95));
        subjCrtRt.add(createSubjCrtRtMap("C05",70));
        subjCrtRt.add(createSubjCrtRtMap("C06",75));

        dummyMap.put("subjCrtRt",subjCrtRt);
        dummyMap.put("top10AvgExRt",80);
        dummyMap.put("top10AvgCrtRt",90);
        dummyMap.put("grpAvgExRt",80);
        dummyMap.put("grpAvgCrtRt",90);

        visionGrowthStt.add(dummyMap);

        data.put("visionGrowthStt",visionGrowthStt);
        setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getVisionExamStt(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequired(paramMap,false);

        //DB 조회
        LinkedHashMap<String,Integer> visionExamStt = new LinkedHashMap<>();

        visionExamStt.put("ansQuesCnt",5423);
        visionExamStt.put("crtQuesCnt",4313);
        visionExamStt.put("incrtNtCnt",398);
        visionExamStt.put("incrtNtFnshCnt",313);

        data.put("visionExamStt",visionExamStt);
        setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getVisionExamChapterStt(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequired(paramMap,false);

        //DB 조회
        LinkedHashMap<String,Object> visionExamChapterStt = new LinkedHashMap<>();
        ArrayList<Map<String,Object>> chapterList = new ArrayList<>();
        LinkedHashMap<String,Object> chapterListMap = new LinkedHashMap<>();

        visionExamChapterStt.put("lrnChapterCnt",243);
        visionExamChapterStt.put("fnshChapterCnt",47);
        visionExamChapterStt.put("supplementaryChapterCnt",26);

        chapterListMap.put("subjCd","C01");
        chapterListMap.put("grade",4);
        chapterListMap.put("term",1);
        chapterListMap.put("chapter",1);
        chapterListMap.put("chapterCd",new int[] {100,200,300,400});
        chapterListMap.put("chapterNm",new String[] {"","","",""});
        chapterListMap.put("understandingLv",new int[] {5,2,7,5});

        chapterList.add(chapterListMap);
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
        LinkedHashMap<String,Object> visionExamChapterLrn = new LinkedHashMap<>();
        LinkedHashMap<String,Object> priorLrn = new LinkedHashMap<>();
        LinkedHashMap<String,Object> currentLrn = new LinkedHashMap<>();

        priorLrn.put("chapterNm","4학년 2학기 수학 2단원 '여러 가지 도형'");
        priorLrn.put("examCrtRt",65);
        priorLrn.put("examDt","2019-04-03");

        currentLrn.put("chapterNm","5학년 1학기 수학 6단원 '분수와 소수'");
        currentLrn.put("examCrtRt",57);
        currentLrn.put("examDt","2019-05-20");

        visionExamChapterLrn.put("priorLrn",priorLrn);
        visionExamChapterLrn.put("currentLrn",currentLrn);
        visionExamChapterLrn.put("followUpLrn",new String[] {
                "5학년 2학기 수학 4단원'분수'",
                "6학년 1학기 수학 1단원 '분수의 덧셈과 뺄셈'",
                "6학년 1학기 수학 2단원 '소수의 덧셈과 뺄셈'"});
        visionExamChapterLrn.put("supplementaryLrn",new String[]
                {
                        "핵심전과 > 수학익힘책 > 6단원 분수와 소수 > 교과서 수학익힘 복습",
                        "수학완성 > 영역별수학 > 수와 연산 > 22단계 분수(2)",
                        "수학완성 > 영역별수학 > 수와 연산 > 26단계 소수"});

        data.put("visionExamChapterLrn",visionExamChapterLrn);
        setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getVisionExamFieldStt(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequired(paramMap,false);

        //DB 조회
        ArrayList<Map<String,Object>> visionExamFieldStt = new ArrayList<>();
        LinkedHashMap<String,Object> dummyMap = new LinkedHashMap<>();
        LinkedHashMap<String,Object> dummyMapTwo = new LinkedHashMap<>();

        dummyMap.put("subjCd","C01");
        dummyMap.put("actFieldNmSp",new String[] {"계산력","문제해결력","이해력","추론력"});
        dummyMap.put("actFieldCrtRtSp",new int[] {60,67,100,25});
        dummyMap.put("contFieldNmSp", new String[] {"규칙성","도형","수와 연산","측정","확률과 통계"});
        dummyMap.put("contFieldCrtRtSp",new int[] {0,0,80,80,0});

        dummyMapTwo.put("subjCd","C02");
        dummyMapTwo.put("actFieldNmSp",new String[] {"계산력","문제해결력","이해력","추론력"});
        dummyMapTwo.put("actFieldCrtRtSp",new int[] {70,87,90,55});
        dummyMapTwo.put("contFieldNmSp", new String[] {"규칙성","도형","수와 연산","측정","확률과 통계"});
        dummyMapTwo.put("contFieldCrtRtSp",new int[] {0,0,85,70,50});

        visionExamFieldStt.add(dummyMap);
        visionExamFieldStt.add(dummyMapTwo);

        data.put("visionExamFieldStt",visionExamFieldStt);
        setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getVisionExamList(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequiredWithSubjCd(paramMap);

        //DB 조회
        ArrayList<Map<String,Object>> visionExamList = new ArrayList<>();
        LinkedHashMap<String,Object> dummyMap = new LinkedHashMap<>();

        LinkedHashMap<String,String> answer = new LinkedHashMap<>();
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
        visionExamList.add(dummyMap);

        data.put("visionExamList",visionExamList);
        setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getVisionAttPtn(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequired(paramMap,false);

        //DB 조회
        LinkedHashMap<String,Object> visionAttPtn = new LinkedHashMap<>();
        LinkedHashMap<String,Object> loginDummyMap = new LinkedHashMap<>();
        LinkedHashMap<String,Object> loginDummyMapTwo = new LinkedHashMap<>();

        visionAttPtn.put("attRtMsg","출석률이 높아요");
        visionAttPtn.put("attRt",95);
        visionAttPtn.put("planDtCnt",108);
        visionAttPtn.put("attDtCnt",103);
        visionAttPtn.put("loginPtn","규칙적");

        loginDummyMap.put("dt","2020-12-01");
        loginDummyMap.put("loginTm","09:20");

        loginDummyMapTwo.put("dt","2020-12-02");
        loginDummyMapTwo.put("loginTm","08:50");

        visionAttPtn.put("loginTm",new LinkedHashMap[] {loginDummyMap,loginDummyMapTwo});

        data.put("visionAttPtn",visionAttPtn);
        setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getVisionLrnPtn(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequired(paramMap,false);

        //DB 조회
        LinkedHashMap<String,Object> visionLrnPtn = new LinkedHashMap<>();
        ArrayList<Map<String,Object>> subjLrnTm = new ArrayList<>();

        LinkedHashMap<String,Object> subjLrnTmMap = new LinkedHashMap<>();
        LinkedHashMap<String,Object> subjLrnTmMapTwo = new LinkedHashMap<>();

        subjLrnTmMap.put("subjCd","C01");
        subjLrnTmMap.put("subSubjLrnTm",new LinkedHashMap[] {
                createSubjLrnTmMap("C0101",120),
                createSubjLrnTmMap("C0102",155)});

        subjLrnTmMapTwo.put("subjCd","C02");
        subjLrnTmMapTwo.put("subSubjLrnTm",new LinkedHashMap[] {
                createSubjLrnTmMap("C0201",210),
                createSubjLrnTmMap("C0202",190)});

        subjLrnTm.add(subjLrnTmMap);
        subjLrnTm.add(subjLrnTmMapTwo);

        visionLrnPtn.put("lrnTm",4523);
        visionLrnPtn.put("maxSubjCd","C02");
        visionLrnPtn.put("subjLrnTm",subjLrnTm);

        data.put("visionLrnPtn",visionLrnPtn);
        setResult(dataKey,data);

        return result;
    }

    @Override
    public Map getVisionPrintBasicInfo(Map<String, Object> paramMap) throws Exception {
        Map<String,Object> data = new HashMap<>();
        checkRequired(paramMap,false);

        //DB 조회
        LinkedHashMap<String,Object> visionPrintBasicInfo = new LinkedHashMap<>();

        visionPrintBasicInfo.put("title","미래 우주 과학자");
        visionPrintBasicInfo.put("studNm","김홈런");
        visionPrintBasicInfo.put("dt","2020-01-01 ~ 2020-06-30");
        visionPrintBasicInfo.put("msg",new String[] {
                "미래 우주 과학자, 기특한 우리 홈런이가 칭찬과 격려를 들으며 홈런과 함께 성장한 기록입니다.",
                "홈런이는 그 동안 243개 단원 중 47개 단원을 마스터 했고, 26개 단원은 복습이 필요한 상태입니다. 진도는 국어, 수학 2과목의 경우 또래 상위 10%보다 앞서있지만, 과학,사회,한자 세 과목 진도는 권장지도 보다 늦어있습니다.",
                "홈런 선생님과 상담을 통해 우리 아이 약점을 보완할 수 있는 자세한 학습습관 처방을 받아보세요. 홈런이가 더 바른 습관, 더 다양한 재미, 더 높은 집중력으로 No.1이 될 수 있도록 홈런이 함께하겠습니다."
        });
        visionPrintBasicInfo.put("positiveMsg",new String[] {"칭찬 메시지1","칭찬 메시지2","칭찬 메시지3","칭찬 메시지4","칭찬 메시지5"});
        visionPrintBasicInfo.put("negativeMsg",new String[] {"처방 메시지1","처방 메시지2"});

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
        LinkedHashMap<String,Integer> lrnStt = new LinkedHashMap<>();
        LinkedHashMap<String,Object> examStt = new LinkedHashMap<>();
        LinkedHashMap<String,Integer> imprvSlvHabitDetail = new LinkedHashMap<>();

        lrnStt.put("planDtCnt",689);
        lrnStt.put("attDtCnt",548);
        lrnStt.put("lrnSec",45750);
        lrnStt.put("planCnt",2600);
        lrnStt.put("exCnt",2548);
        lrnStt.put("incrtNoteCnt",398);
        lrnStt.put("incrtNtFnshCnt",348);

        imprvSlvHabitDetail.put("incrtNtNtCnt",298);
        imprvSlvHabitDetail.put("skipQuesCnt",98);
        imprvSlvHabitDetail.put("cursoryQuesCnt",28);
        imprvSlvHabitDetail.put("guessQuesCnt",58);
        imprvSlvHabitDetail.put("mistakenQuesCnt",31);

        examStt.put("ansQuesCnt",1483);
        examStt.put("crtQuesCnt",1254);
        examStt.put("imprvSlvHabitCnt",483);
        examStt.put("imprvSlvHabitDetail",imprvSlvHabitDetail);

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
