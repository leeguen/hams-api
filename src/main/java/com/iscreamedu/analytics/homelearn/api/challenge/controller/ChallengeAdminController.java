package com.iscreamedu.analytics.homelearn.api.challenge.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.iscreamedu.analytics.homelearn.api.challenge.service.ChallengeAdminService;

@RestController
@RequestMapping("/admin-chl")
public class ChallengeAdminController {
	
	@Autowired
	private ChallengeAdminService challengeAdminService;
	
	private HttpHeaders headers;
    private LinkedHashMap body;

	public ChallengeAdminController() {
		headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","json"));
        headers.setAccessControlAllowOrigin("*");
        headers.setAccessControlAllowCredentials(true);
        headers.setCacheControl(CacheControl.noCache());
        headers.setCacheControl(CacheControl.noStore().mustRevalidate());
        headers.setExpires(0);
	}
	
	/***
	 * ADMIN-CH-001
	 * 관리자 페이지 > 이번달 보상 현황
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getChRewardStt")
    @ResponseBody
    public ResponseEntity getChRewardStt(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)challengeAdminService.getChRewardStt(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/***
	 * ADMIN-CH-002
	 * 관리자 페이지 > 이번달 챌린지 현황
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getChStepUpMisStt")
    @ResponseBody
    public ResponseEntity getChStepUpMisStt(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)challengeAdminService.getChStepUpMisStt(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/***
	 * ADMIN-CH-003
	 * 관리자 페이지 > 매일 홈런하는 습관 현황
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getChHabitMisStt")
    @ResponseBody
    public ResponseEntity getChHabitMisStt(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)challengeAdminService.getChHabitMisStt(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/***
	 * ADMIN-CH-004
	 * 관리자 페이지 > 메타포 월별 이력
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getChMetaphorHistory")
    @ResponseBody
    public ResponseEntity getChMetaphorHistory(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)challengeAdminService.getChMetaphorHistory(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/***
	 * ADMIN-CH-005
	 * 관리자 페이지 > 월별 홈런하는 습관 이력
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getChHabitMisHistory")
    @ResponseBody
    public ResponseEntity getChHabitMisHistory(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)challengeAdminService.getChHabitMisHistory(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	
	/***
	 * ADMIN-CH-006
	 * 관리자 페이지 > 월별 챌린지 이력
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getChStepUpMisHistory")
    @ResponseBody
    public ResponseEntity getChStepUpMisHistory(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)challengeAdminService.getChStepUpMisHistory(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/***
	 * ADMIN-CH-007
	 * 관리자 페이지 > 매일 홈런하는 습관 등록
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/regAdminCompleteMission")
    @ResponseBody
    public ResponseEntity regAdminCompleteMission(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)challengeAdminService.regAdminCompleteMission(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
}
