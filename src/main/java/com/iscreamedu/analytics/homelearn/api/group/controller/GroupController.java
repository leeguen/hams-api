package com.iscreamedu.analytics.homelearn.api.group.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.iscreamedu.analytics.homelearn.api.group.service.GroupService;

@RestController
@RequestMapping("/hams-org")
public class GroupController {

	@Autowired
    private GroupService groupService;

    private HttpHeaders headers;
    private LinkedHashMap body;

    public GroupController() {
        headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","json"));
        headers.setAccessControlAllowOrigin("*");
        headers.setAccessControlAllowCredentials(true);
        headers.setCacheControl(CacheControl.noCache());
        headers.setCacheControl(CacheControl.noStore().mustRevalidate());
        headers.setExpires(0);
    }
    
    /**
     * HAMS-ORG-CM-001
	 * 년월, 주차 산출 - 기간정보
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getPeriod")
    @ResponseBody
	public ResponseEntity getPeriod(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupService.getPeriod(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * HAMS-ORG-CM-002
	 * 학습분석 메인 - 학생 정보
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getStud")
    @ResponseBody
	public ResponseEntity getStududent(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupService.getStud(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * HAMS-ORG-CM-003	
	 * 학습분석 메인 - 학습분석 기본정보
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getLrnBasic")
    @ResponseBody
	public ResponseEntity getLrnBasic(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupService.getLrnBasic(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * HAMS-ORG-LS-001 
	 * 학습분석 요약 - 학습 습관 차트
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getLrnHabitChart")
    @ResponseBody
	public ResponseEntity getLrnHabitChart(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupService.getLrnHabitChart(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * HAMS-ORG-ES-001
	 * 평가분석 요약 - 과목평가
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getSubjExam")
    @ResponseBody
	public ResponseEntity getSubjExam(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupService.getSubjExam(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * HAMS-ORG-ES-002
	 * 평가분석 요약 - 과목비교차트
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getCompareSub")
    @ResponseBody
	public ResponseEntity getCompareSub(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupService.getCompareSub(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * HAMS-ORG-ES-003
	 * 평가분석 요약 - 평가차트
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getExamChart")
    @ResponseBody
	public ResponseEntity getExamChart(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupService.getExamChart(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * HAMS-ORG-ED-001
	 * 평가분석 상세 - 과목평가지 목록
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getSubjExamList")
    @ResponseBody
	public ResponseEntity getSubjExamList(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupService.getSubjExamList(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * HAMS-ORG-ES-004
	 * 평가분석 요약 - 오답노트
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getIncrtNote")
    @ResponseBody
	public ResponseEntity getIncrtNote(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupService.getIncrtNote(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
    /**
     * 학습분석 메세지 코드 모음 (HAMS-T-LA-010)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getCommMsgCd")
    @ResponseBody
    public ResponseEntity getCommMsgCd(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) groupService.getCommMsgCd(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
}
