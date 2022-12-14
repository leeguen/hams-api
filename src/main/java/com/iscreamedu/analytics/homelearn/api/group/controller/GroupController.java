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
	 * HAMS-ORG-LS-002
	 * 학습분석 요약 - AI 학습 추천
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getAiRecommendLrn")
	@ResponseBody
	public ResponseEntity getAiRecommendLrn(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupService.getAiRecommendLrn(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * HAMS-ORG-LD-001 
	 * 학습분석 상세 - 진단검사/평가현황
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getDiagnosticEvalStt")
	@ResponseBody
	public ResponseEntity getDiagnosticEvalStt(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupService.getDiagnosticEvalStt(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * HAMS-ORG-LD-002 
	 * 학습분석 상세 - 출석률현황
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getAttRtStt")
	@ResponseBody
	public ResponseEntity getAttRtStt(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupService.getAttRtStt(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}

    /**
     * 학습분석 상세 - 출석률현황 - 일별출석히스토리 (HAMS-ORG-LD-018)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getAttHistoryDaily")
    @ResponseBody
    public ResponseEntity getAttHistoryDaily(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) groupService.getAttHistoryDaily(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
	/**
	 * HAMS-ORG-LD-003 
	 * 학습분석 상세 - 학습타임라인
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getLrnTmList")
	@ResponseBody
	public ResponseEntity getLrnTmList(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupService.getLrnTmList(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
		
	/**
	 * HAMS-ORG-LD-004 
	 * 학습분석 상세 - 학습상세현황
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getLrnDetail")
	@ResponseBody
	public ResponseEntity getLrnDetail(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupService.getLrnDetail(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * HAMS-ORG-LD-005 
	 * 학습분석 상세 - 출석일 수 현황
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getAttCntStt")
	@ResponseBody
	public ResponseEntity getAttCntStt(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupService.getAttCntStt(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	 
	/**
	 * HAMS-ORG-LD-006
	 * 학습분석 상세 - 로그인 패턴 현황
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getLoginPtnStt")
	@ResponseBody
	public ResponseEntity getLoginPtnStt(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupService.getLoginPtnStt(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * HAMS-ORG-LD-007
	 * 학습분석 상세 - 수행률 현황
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getExRtStt")
	@ResponseBody
	public ResponseEntity getExRtStt(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupService.getExRtStt(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	 
	/**
	 * HAMS-ORG-LD-008 
	 * 학습분석 상세 - 완료한 학습 현황
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getFnshLrnExStt")
	@ResponseBody
	public ResponseEntity getFnshLrnExStt(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupService.getFnshLrnExStt(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	 
	/**
	 * HAMS-ORG-LD-009 
	 * 학습분석 상세 - 학습 수행 현황
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getLrnExSttCompareSub")
	@ResponseBody
	public ResponseEntity getLrnExSttCompareSub(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupService.getLrnExSttCompareSub(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	 
	/**
	 * HAMS-ORG-LD-010
	 * 학습분석 상세 - 스스로 학습 현황
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getALrnExStt")
    @ResponseBody
	public ResponseEntity getALrnExStt(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupService.getALrnExStt(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * HAMS-ORG-LD-011
	 * 학습분석 상세 - 정답률 현황
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getCrtRtStt")
    @ResponseBody
	public ResponseEntity getCrtRtStt(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupService.getCrtRtStt(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * HAMS-ORG-LD-012
	 * 학습분석 상세 - 미완료 오답노트 현황
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getIncrtNoteNcStt")
    @ResponseBody
	public ResponseEntity getIncrtNoteNcStt(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupService.getIncrtNoteNcStt(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * HAMS-ORG-LD-013
	 * 학습분석 상세 - 맞은 문제 수 현황
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getCrtQuesCntStt")
    @ResponseBody
	public ResponseEntity getCrtQuesCntStt(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupService.getCrtQuesCntStt(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * HAMS-ORG-LD-014
	 * 학습분석 상세 - 고쳐야 할 습관 현황
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getSlvHabitStt")
    @ResponseBody
	public ResponseEntity getSlvHabitStt(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupService.getSlvHabitStt(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * HAMS-ORG-LD-015
	 * 학습분석 상세 - 일평균 학습 현황
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getDayAvgLrnStt")
    @ResponseBody
	public ResponseEntity getDayAvgLrnStt(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupService.getDayAvgLrnStt(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * HAMS-ORG-LD-016
	 * 학습분석 상세 - 가장 긴 학습시간 현황
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getLongLrnTmStt")
    @ResponseBody
	public ResponseEntity getLongLrnTmStt(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupService.getLongLrnTmStt(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * HAMS-ORG-LD-017
	 * 학습분석 상세 - 총 학습시간 현황
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getTotalLrnTmStt")
    @ResponseBody
	public ResponseEntity getTotalLrnTmStt(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupService.getTotalLrnTmStt(params);
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
	 * HAMS-ORG-ES-005 
	 * 평가분석 요약 - 단원별 이해
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getChapterStt")
    @ResponseBody
	public ResponseEntity getChapterStt(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupService.getChapterStt(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}	

	/**
	 * HAMS-ORG-ED-002 
	 * 평가분석 상세 - 단원별 연계학습
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getChapterLrn")
    @ResponseBody
	public ResponseEntity getChapterLrn(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupService.getChapterLrn(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}

	/**
	 * HAMS-ORG-DB-001
	 * 대시보드 - 선택한 리포트 생성여부 체크
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getCheckReport")
    @ResponseBody
	public ResponseEntity getCheckReport(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupService.getCheckReport(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}

	/**
	 * HAMS-ORG-DB-002
	 * 대시보드 - 선택한 리포트 리스트 출력
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getAiReportList")
    @ResponseBody
	public ResponseEntity getAiReportList(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)groupService.getAiReportList(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
}
