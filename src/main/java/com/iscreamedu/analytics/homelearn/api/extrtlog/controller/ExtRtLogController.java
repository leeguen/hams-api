package com.iscreamedu.analytics.homelearn.api.extrtlog.controller;

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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.iscreamedu.analytics.homelearn.api.challenge.service.ChallengeService;
import com.iscreamedu.analytics.homelearn.api.extrtlog.service.ExtRtLogService;

@RestController
@RequestMapping("/extRtLog")	
//@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ExtRtLogController {

	@Autowired
	private ExtRtLogService extRtLogService;

	@Autowired
	private ChallengeService challengeService;
	
    private HttpHeaders headers;
    private LinkedHashMap body;

	public ExtRtLogController() {
		headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","json"));
        headers.setAccessControlAllowOrigin("*");
        headers.setAccessControlAllowCredentials(true);
        headers.setCacheControl(CacheControl.noCache());
        headers.setCacheControl(CacheControl.noStore().mustRevalidate());
        headers.setExpires(0);
	}
	
	/***
	 * STUD-CH-001	실시간 미션 완료 등록
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value="/regCompleteMission", consumes = MediaType.APPLICATION_JSON_VALUE,  produces = "application/json; charset=utf8")
	@ResponseBody
	public ResponseEntity setRealTimeCompleteMission(@RequestBody Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		System.out.println("setRealTimeCompleteMission - Param : " + params);
		body = (LinkedHashMap)extRtLogService.setRealTimeCompleteMission(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/***
	 * STUD-CH-012	국어책읽기 > 챌린지_미션_상태_변경
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value="/setChMissonStatusChange", produces = "application/json; charset=utf8")
    @ResponseBody
    public ResponseEntity setRealTimeMissonStatusChange(@RequestBody Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)extRtLogService.setRealTimeMissonStatusChange(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/***
	 * STUD-CH-007	오늘의 미션  :  매일 홈런하는 습관 물주기 완료 등록
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value="/regFnWaterJug", produces = "application/json; charset=utf8")
    @ResponseBody
    public ResponseEntity setFnWaterJug(@RequestBody Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)extRtLogService.setFnWaterJug(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/***
	 * STUD-CH-008	메타포 오브젝트 현황 : 한 단계 UP 챌린지 보상 완료 등록
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value="/regFnObjectReward", produces = "application/json; charset=utf8")
    @ResponseBody
    public ResponseEntity setFnObjectReward(@RequestBody Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)extRtLogService.setFnObjectReward(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/***
	 * STUD-CH-013	오늘의 미션  :  매일 홈런하는 습관 물주기 등록 데이터 리셋 ( 테스트용 )
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/resetWaterJug")
	@ResponseBody
    public ResponseEntity resetWaterJug(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)extRtLogService.resetWaterJug(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/***
	 * STUD-CH-014	메타포 오브젝트 현황 : 한 단계 UP 챌린지 보상 완료 등록 데이터 리셋 ( 테스트용 )
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/resetObjectReward")
	@ResponseBody
    public ResponseEntity resetObjectReward(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)extRtLogService.resetObjectReward(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/***
	 * STUD-CH-015	오늘의 미션  :  매일 홈런하는 습관 파티클 완료 등록
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value="/regFnParticle", produces = "application/json; charset=utf8")
    @ResponseBody
    public ResponseEntity setFnParticle(@RequestBody Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)extRtLogService.setFnParticle(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/***
	 * STUD-CH-016	오늘의 미션  :  매일 홈런하는 습관 파티클 등록 데이터 리셋 ( 테스트용 )
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/resetParticle")
	@ResponseBody
    public ResponseEntity resetParticle(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)extRtLogService.resetParticle(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
}
