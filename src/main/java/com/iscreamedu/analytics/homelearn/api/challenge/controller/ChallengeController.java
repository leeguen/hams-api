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

import com.iscreamedu.analytics.homelearn.api.challenge.service.ChallengeService;

@RestController
@RequestMapping("/stud-chl")
public class ChallengeController {
	
	@Autowired
	private ChallengeService challengeService;
	
	private HttpHeaders headers;
    private LinkedHashMap body;

	public ChallengeController() {
		headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","json"));
        headers.setAccessControlAllowOrigin("*");
        headers.setAccessControlAllowCredentials(true);
        headers.setCacheControl(CacheControl.noCache());
        headers.setCacheControl(CacheControl.noStore().mustRevalidate());
        headers.setExpires(0);
	}
	
	/***
	 * STUD-CH-002
	 * 메타포 현황 월별 히스토리 : 습관 챌린지 
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getChMetaphorHistory")
    @ResponseBody
    public ResponseEntity getChMetaphorHistory(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)challengeService.getChMetaphorHistory(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/***
	 * STUD-CH-003
	 * 메타포 오브젝트 현황  : 한 단계 UP 챌린지
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getChMetaphorObjectStt")
    @ResponseBody
    public ResponseEntity getChMetaphorObjectStt(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)challengeService.getChMetaphorObjectStt(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/***
	 * STUD-CH-004
	 * 오늘의 미션 정보 : 매일 홈런하는 습관
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getChHabitMissionInfo")
    @ResponseBody
    public ResponseEntity getChHabitMissionInfo(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)challengeService.getChHabitMissionInfo(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/***
	 * STUD-CH-005
	 * 오늘의 미션 정보 : 한 단계 UP 챌린지 
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getChStepUpMissionInfo")
    @ResponseBody
    public ResponseEntity getChStepUpMissionInfo(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)challengeService.getChStepUpMissionInfo(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/***
	 * STUD-CH-009
	 * 국어책읽기 > 국어책_챌린지_정보
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getKoreanBookChallenge")
    @ResponseBody
    public ResponseEntity getKoreanBookChallenge(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)challengeService.getKoreanBookChallenge(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/***
	 * STUD-CH-010
	 * 국어책읽기 > 국어책_챌린지_보상_정보
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getKoreanBookChReward")
    @ResponseBody
    public ResponseEntity getChMissinRewardInfo(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)challengeService.getKoreanBookChReward(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
		
	/***
	 * STUD-CH-011
	 * 국어책읽기 > 국어책_챌린지_미션_리스트_정보
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getKoreanBookChMissonList")
    @ResponseBody
    public ResponseEntity getKoreanBookChMissonList(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)challengeService.getKoreanBookChMissonList(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
}
