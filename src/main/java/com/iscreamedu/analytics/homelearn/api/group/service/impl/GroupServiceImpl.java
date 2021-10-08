package com.iscreamedu.analytics.homelearn.api.group.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iscreamedu.analytics.homelearn.api.common.exception.NoDataException;
import com.iscreamedu.analytics.homelearn.api.common.mapper.CommonMapperTutor;
import com.iscreamedu.analytics.homelearn.api.common.security.CipherUtil;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationCode;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtilTutor;
import com.iscreamedu.analytics.homelearn.api.common.util.VersionUtil;
import com.iscreamedu.analytics.homelearn.api.group.service.GroupService;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationUtil;

@Service
public class GroupServiceImpl implements GroupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupServiceImpl.class);
    private static final String MAPPER_NAMESPACE = "Group_";
    private String dw_mapper_namespace;
    private String mapper_name;
    
    
    private VersionUtil versionUtil;

    private Map<String, Object> v_param;
    private Map<String, Object> v_result;
    private LinkedHashMap<String, Object> result;
    private String msgKey = "msg";
    private String dataKey = "data";

    @Autowired
    CommonMapperTutor mapper_1_0;
	@Autowired
//    CommonMapperTutor_1_5 mapper_1_5;	//차후 교체
    CommonMapperTutor mapper_1_5;
	@Autowired
//    CommonMapperTutor_2.0 mapper_2_0;	//차후 교체
    CommonMapperTutor mapper_2_0;
//    @Autowired
//	CommonMapper commonMapper;
    
    private Map<String,Object> getDWVersion(Map<String,Object> params) throws Exception {        
    	//channel params
    	params.put("CHANNEL","GROUP");
        return versionUtil.getDataWareVersion(params);
    }

	private Object getMapperResultData(Map<String, Object> param, String sqlRequestType, Map<String, Object> paramMap, String sqlId) {
		
		try {
			v_result = getDWVersion(param);
	    	
	    	dw_mapper_namespace = MAPPER_NAMESPACE + v_result.get("DW_VERSION").toString();    	
	    	mapper_name = v_result.get("MAPPER_NAME").toString();    	
	    	
			if (mapper_name == "CommonMapperTutor") {
				if(sqlRequestType.toLowerCase() == "list") {
					return mapper_1_0.getList(paramMap, dw_mapper_namespace + sqlId);
				} else {
					return mapper_1_0.get(paramMap, dw_mapper_namespace + sqlId);
				}
			} else if (mapper_name == "CommonMapperTutorTemp") {
				//mapper_1_5
				if(sqlRequestType.toLowerCase() == "list") {
					return mapper_1_5.getList(paramMap, dw_mapper_namespace + sqlId);
				} else {
					return mapper_1_5.get(paramMap, dw_mapper_namespace + sqlId);
				}
			} else {
				//mapper_2_0
				if(sqlRequestType.toLowerCase() == "list") {
					return mapper_2_0.getList(paramMap, dw_mapper_namespace + sqlId);
				} else {
					return mapper_2_0.get(paramMap, dw_mapper_namespace + sqlId);
				}
			}
		} catch (Exception e) {
			
			ValidationUtil vu = new ValidationUtil();
			vu.setError("db mapper result data fail");
			Map<String, Object> tempResult = vu.getResult();
			tempResult.put("error", e.getStackTrace());
			LOGGER.debug(tempResult.toString());
//			e.setStackTrace(null);
			return tempResult; 
		}
	}
	
    @Override
    public Map getPeriod(Map<String, Object> paramMap) throws Exception {
    	
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "PERIOD");
    	
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"svcOpenDe"}, paramMap);
		
		if(vu.isValid()) { 		
			if(!paramMap.containsKey("currCon") || paramMap.get("currCon").equals("")) {	// 주간+월간 합산
				Map<String,Object> data = new HashMap<>();
				data.put("weeks", (List)getMapperResultData(v_param, "list", paramMap, ".selectPeriodWeeks"));
				data.put("months", (List)getMapperResultData(v_param, "list", paramMap, ".selectPeriodMonths"));
				setResult(dataKey, data);
			} else if(paramMap.get("currCon").equals("w")) {
				setResult(dataKey, (List)getMapperResultData(v_param, "list", paramMap, ".selectPeriodWeeks"));
			} else if(paramMap.get("currCon").equals("m")) {
				setResult(dataKey, (List)getMapperResultData(v_param, "list", paramMap, ".selectPeriodMonths"));	
			}
		} else {
			setResult(msgKey, vu.getResult());
		}
		
    	return result;
    }
    
    @Override
    public Map getStud(Map<String, Object> paramMap) throws Exception {
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "STUD");
    	
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"studId"}, paramMap);
		
		if(vu.isValid()) { 		
			setResult(dataKey, getMapperResultData(v_param, "", paramMap, ".selectStud"));
		} else {
			setResult(msgKey, vu.getResult());
		}
		
    	return result;
    }

    @Override
    public Map getLrnBasic(Map<String, Object> paramMap) throws Exception {
    	v_param = new HashMap<>();
    	v_param.put("METHOD", "LRNBASIC");
    	
    	//Validation
		ValidationUtil vu = new ValidationUtil();
		//1.필수값 체크
		vu.checkRequired(new String[] {"currCon","studId","startDt","endDt"}, paramMap);
		
		if(vu.isValid()) { 		
			Map<String,Object> data = new HashMap<>();
//			if(paramMap.get("currCon").equals("m")) {
//				data.put("current", getMapperResultData(v_param, "list", paramMap, ".selectLrnBasicMonthly"));
//	            data.put("prev", getMapperResultData(v_param, "list", paramMap, ".selectLrnBasicMonthly"));
//	            data.put("msg",null);	//추후 메시지기획안 적용 예정	            
//	        } else 
//	        	data.put("current", getMapperResultData(v_param, "list", paramMap, ".selectLrnBasicPeriod"));
//	            data.put("prev", getMapperResultData(v_param, "list", paramMap, ".selectLrnBasicPeriod")); 
//	            data.put("msg",null);	//추후 메시지기획안 적용 예정	               
//			}
			setResult(dataKey,data);
		} else {
			setResult(msgKey, vu.getResult());
		}
		
    	return result;
    }

    @Override
    public Map getOrgEnvConfig(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override
    public Map setOrgEnvConfig(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override
    public Map getLrnHabitChart(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override
    public Map getAiRecommendLrn(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override
    public Map getDiagnsticEvalStt(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override
    public Map getAttRtStt(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override
    public Map getLrnTmList(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getAttCntStt(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getloginPtnStt(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getExRtStt(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getFnshLrnExStt(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getLrnExSttCompareSub(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getALrnExStt(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getCrtRtStt(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getIncrtNoteNcStt(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getCrtQuesCntStt(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getSlvHabitStt(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getDayAvgLrnStt (Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getTotalLrnTmStt(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getLongLrnTmStt(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getSubjExam(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getCompareSub(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getExamChart(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getSubjExamList(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getIncrtNote(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getChapterStt(Map<String, Object> paramMap) throws Exception {
    	return result;
    }

    @Override    
    public Map getChapterLrn(Map<String, Object> paramMap) throws Exception {
    	return result;
    }
    
    @Override
    public Map getCommMsgCd(Map<String, Object> paramMap) throws Exception {
    	
    	v_param.put("METHOD", "COMMMSGCD");
    	
        Map<String,Object> data = new HashMap<>();
        checkRequired(paramMap);

        //DB 조회
        //List<Map<String,Object>> commMsgCd = (List<Map<String,Object>>)mapper_1_0.getList(paramMap, dw_mapper_namespace + ".getCommMsgCd");
        List<Map<String,Object>> commMsgCd = (List)getMapperResultData(v_param, "list", paramMap, ".getCommMsgCd");
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
     * @param linkedHashMap 
     */    
    private void setResult(String key, Object data) {
        LinkedHashMap message = new LinkedHashMap();
        result = null;
        result = new LinkedHashMap();

        if(data == null
                || (data instanceof List && ((List)data).size() == 0)
                || (data instanceof Map && ((Map)data).isEmpty())) {
            throw new NoDataException(new Object[] {key,"null",ValidationCode.NO_DATA});
        }
//        else if(resultNullCheck((Map)data)) {
//            throw new NoDataException(new Object[] {key,"null",ValidationCode.NO_DATA});
//        }
        else if(data instanceof Map && ((Map)data).containsKey("error")) {	// error 키값 존재하면 예외 처리
            result.put(msgKey, data);
        } else if(data instanceof Map && ((Map)data).containsKey("resultCode")) {	// resultCode 키값 존재하면 예외 처리
        	result.put(msgKey, data);
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
