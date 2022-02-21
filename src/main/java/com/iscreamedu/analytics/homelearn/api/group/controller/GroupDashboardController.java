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

import com.iscreamedu.analytics.homelearn.api.group.service.GroupDashboardService;

/**
 * 사회공헌 API Controller
 * @author 
 * @since 
 * @version 2.0
 * @see
 *  
 * <pre>
 * << 개정이력(Modification Information) >>
 * 
 *   수정일      		수정자		          수정내용
 *  ----------  --------    ---------------------------
 *  2020.05.19	shsong		초기생성
 *  2021.01.04  shoshu      고도화 추가
 *  2021.06.03  5heeteak    학년, 반 관리 기능 추가
 *  2022.01.18  jhlim   	hams api 통합
 *  </pre>
 */

@RestController
@RequestMapping("/org")
public class GroupDashboardController {

	@Autowired
    private GroupDashboardService groupDashboardService;

    private HttpHeaders headers;
    private LinkedHashMap body;

    public GroupDashboardController() {
        headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","json"));
        headers.setAccessControlAllowOrigin("*");
        headers.setAccessControlAllowCredentials(true);
        headers.setCacheControl(CacheControl.noCache());
        headers.setCacheControl(CacheControl.noStore().mustRevalidate());
        headers.setExpires(0);
    }
    
    /**
     * 기관 정보
     * @param params
     * @param req
     * @param res
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/getAgencyInfo")
	public ResponseEntity getAgencyInfo(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupDashboardService.getAgencyInfo(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
    
    /**
	 * 년월, 주차 산출
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getYymmWk")
	public ResponseEntity getYymmWk(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupDashboardService.selectGetYymmWk(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * 학년, 반 관리 정보
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getGradeClassYn")
	public ResponseEntity getGradeClassYn(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupDashboardService.getGradeClassYn(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * 학년, 반 정보 조회
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getGradeClassInfo")
	public ResponseEntity getGradeClassInfo(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupDashboardService.getGradeClassInfo(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * 현황 메인
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getGroupStatusMain")
	public ResponseEntity getGroupLrnAnalysis(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupDashboardService.selectGroupStatusMain(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * 학습 차트
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getGroupLrnChart")
	public ResponseEntity getGroupLrnChart(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupDashboardService.selectGroupLrnChart(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * 지도 정보
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getGroupMapInfo")
	public ResponseEntity getGroupMapInfo(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupDashboardService.selectGroupMapInfo(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * 온라인 개학
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getGroupOnlineSchool")
	public ResponseEntity getGroupOnlineSchool(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupDashboardService.selectGroupOnlineSchool(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * 학생 리스트
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getGroupStudList")
	public ResponseEntity getGroupStudList(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupDashboardService.selectGroupStudList(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * 지역 리스트
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getGroupLocalList")
	public ResponseEntity getGroupLocalList(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupDashboardService.selectGroupLocalList(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * 메인 지역
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getGroupMainLocal")
	public ResponseEntity getGroupMainLocal(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupDashboardService.selectGroupMainLocal(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * 학습지표 (ORG-LR-001)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getLrnStt")
	public ResponseEntity getLrnStt(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupDashboardService.getLrnStt(params);
		
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * 학습지표 추이 (ORG-LR-002)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getLrnSttTrend")
	public ResponseEntity getLrnSttTrend(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupDashboardService.getLrnSttTrend(params);
		
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * 기관학습현황 (ORG-LR-003)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getOrgLrnStt")
	public ResponseEntity getOrgLrnStt(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupDashboardService.getOrgLrnStt(params);
		
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * 홈런 활용 (ORG-LR-004)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getHlUtilization")
	public ResponseEntity getHlUtilization(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupDashboardService.getHlUtilization(params);
		
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * 학습 분석 신호 변화 (ORG-LR-005)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getLrnSignalTrend")
	public ResponseEntity getLrnSignalTrend(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupDashboardService.getLrnSignalTrend(params);
		
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * 강점 습관 분석 우수회원 (ORG-LR-006)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getStrengthHabitExcellentStud")
	public ResponseEntity getStrengthHabitExcellentStud(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupDashboardService.getStrengthHabitExcellentStud(params);
		
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * 강점 습관 분석 우수회원 학습특성 (ORG-LR-007)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getStrengthHabitExcellentStudHabit")
	public ResponseEntity getStrengthHabitExcellentStudHabit(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupDashboardService.getStrengthHabitExcellentStudHabit(params);
		
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * 강점 습관 분석 고성장 회원 (ORG-LR-008)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getStrengthHabitHighGrowthStud")
	public ResponseEntity getStrengthHabitHighGrowthStud(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupDashboardService.getStrengthHabitHighGrowthStud(params);
		
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * 강점 습관 분석 고성장 회원  학습특성 (ORG-LR-009)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getStrengthHabitHighGrowthStudHabit")
	public ResponseEntity getStrengthHabitHighGrowthStudHabit(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupDashboardService.getStrengthHabitHighGrowthStudHabit(params);
		
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * 학습계획 학생 학습 현황 (ORG-LP-001)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getLrnPlanStudLrnStt")
	public ResponseEntity getLrnPlanStudLrnStt(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupDashboardService.getLrnPlanStudLrnStt(params);
		
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
}
