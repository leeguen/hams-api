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
            
            Collections.reverse(lrnGrowthStt);
            
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
            paramMap.put("types",paramMap.get("types").toString().split(","));

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
        System.out.println("format getTime:::" + form.format(cal.getTime()));

        return form.format(cal.getTime());
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
        }
        else if(resultNullCheck((Map)data)) {
            throw new NoDataException(new Object[] {key,"null",ValidationCode.NO_DATA});
        }
        else {
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
