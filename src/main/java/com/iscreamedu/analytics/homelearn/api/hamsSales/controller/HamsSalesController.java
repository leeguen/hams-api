package com.iscreamedu.analytics.homelearn.api.hamsSales.controller;

import java.util.LinkedHashMap; //순서가 있는 Map구조
import java.util.Map; //key, value 쌍의 자료구조 -> js의 객체랑 비슷할 수도

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.iscreamedu.analytics.homelearn.api.common.service.ExternalAPIService;
import com.iscreamedu.analytics.homelearn.api.hamsSales.service.HamsSalesService;

/**
 * HAMS Sales API Controller
 * @author shoshu
 * @since 2020.09.21
 * @version 1.0
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일      		수정자		수정내용
 *  ----------  --------    --------------------------
 *  2020.09.21	shoshu		초기생성
 *  </pre>
 */
@RequestMapping("/sales") //요청 url을 어떤 메소드가 처리할지 매핑해주는 어노테이션(형식 정의안하면 get 자동설정)
@RestController //@controller + responsebody
public class HamsSalesController {
	private static final Logger LOGGER = LoggerFactory.getLogger(HamsSalesController.class);

	@Autowired
	private HamsSalesService hamsSalesService;
	@Autowired
	private ExternalAPIService externalAPIService;

	public HttpHeaders headers;
	public LinkedHashMap body;

	public HamsSalesController() {
		headers = new HttpHeaders();
		headers.setContentType(new MediaType("application","json"));
		headers.setAccessControlAllowOrigin("*");
		headers.setAccessControlAllowCredentials(true);
	}

	/**
	 * healthCheck
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/healthCheck") //상태 체크용
	public ResponseEntity healthCheck(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)hamsSalesService.healthCheck(params);

		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	/**
	 * 과목 코드 정보 (HAMS-S-C-001)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getSubjCodeInfo")
	public ResponseEntity getSubjCodeInfo(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)hamsSalesService.getSubjCodeInfo(params);

		return new ResponseEntity(body, headers, HttpStatus.OK);
	}

	/**
	 * 학생 정보 (HAMS-S-C-002)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getStudInfo")
	public ResponseEntity getStudInfo(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)hamsSalesService.getStudInfo(params);

		return new ResponseEntity(body, headers, HttpStatus.OK);
	}

	/**
	 * 일일 학습 정보 (HAMS-S-TR-001)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getDailyLrnStt")
	public ResponseEntity getDailyLrnStt(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)hamsSalesService.getDailyLrnStt(params);

		return new ResponseEntity(body, headers, HttpStatus.OK);
	}

	/**
	 * 전환 예측 (HAMS-S-TR-002)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getSettleInfoPrediction")
	public ResponseEntity getSettleInfoPrediction(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)hamsSalesService.getSettleInfoPrediction(params);

		return new ResponseEntity(body, headers, HttpStatus.OK);
	}

	/**
	 * 3일 학습 수행 요약 (HAMS-S-TR-003)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getThreeDayLrn")
	public ResponseEntity getThreeDayLrn(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)hamsSalesService.getThreeDayLrn(params);

		return new ResponseEntity(body, headers, HttpStatus.OK);
	}

	/**
	 * 학습패턴 (HAMS-S-TR-004)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getLrnPtn")
	public ResponseEntity getLrnPtn(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)hamsSalesService.getLrnPtn(params);

		return new ResponseEntity(body, headers, HttpStatus.OK);
	}

	/**
	 * 학습 타임라인 상세 (HAMS-S-TR-005)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getLrnTmln")
	public ResponseEntity getLrnTmln(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)hamsSalesService.getLrnTmln(params);

		return new ResponseEntity(body, headers, HttpStatus.OK);
	}

	/**
	 * 학습 수행 결과 (HAMS-S-TR-006)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getLrnExStt")
	public ResponseEntity getLrnExStt(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)hamsSalesService.getLrnExStt(params);

		return new ResponseEntity(body, headers, HttpStatus.OK);
	}

	/**
	 * 학습 수행 결과 (HAMS-S-TR-007)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getLrnPlanStt")
	public ResponseEntity getLrnPlanStt(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)hamsSalesService.getLrnPlanStt(params);

		return new ResponseEntity(body, headers, HttpStatus.OK);
	}

	/**
	 * 스스로 학습 (HAMS-S-TR-008)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getALrnStt")
	public ResponseEntity getALrnStt(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)hamsSalesService.getALrnStt(params);

		return new ResponseEntity(body, headers, HttpStatus.OK);
	}

	/**
	 * 평가 (HAMS-S-TR-011)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getExam")
	public ResponseEntity getExam(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)hamsSalesService.getExam(params);

		return new ResponseEntity(body, headers, HttpStatus.OK);
	}

	/**
	 * 푼 평가지 (HAMS-S-TR-012)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getExpl")
	public ResponseEntity getExpl(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)hamsSalesService.getExpl(params);

		return new ResponseEntity(body, headers, HttpStatus.OK);
	}

	/**
	 * 오답노트 (HAMS-S-TR-013)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getIncrtNote")
	public ResponseEntity getIncrtNote(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)hamsSalesService.getIncrtNote(params);

		return new ResponseEntity(body, headers, HttpStatus.OK);
	}

	/**
	 * HL API (홈런 API 데이터 조회)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getHl/{apiName}")
	public ResponseEntity getHlApi(@PathVariable String apiName, @RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		params.put("apiName", apiName);

		body = (LinkedHashMap)externalAPIService.callExternalAPI(params);

		return new ResponseEntity(body, headers, HttpStatus.OK);
	}

	/**
	 * 피드백 (HAMS-S-ER-001)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getFeedback")
	public ResponseEntity getFeedback(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)hamsSalesService.getFeedback(params);

		return new ResponseEntity(body, headers, HttpStatus.OK);
	}

	/**
	 * 과목별 학습 패턴 (HAMS-S-ER-002)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getSubjLrnPtn")
	public ResponseEntity getSubjLrnPtn(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)hamsSalesService.getSubjLrnPtn(params);

		return new ResponseEntity(body, headers, HttpStatus.OK);
	}

	/**
	 * 바른 학습 습관 (HAMS-S-ER-003)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getLrnHabits")
	public ResponseEntity getLrnHabits(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)hamsSalesService.getLrnHabits(params);

		return new ResponseEntity(body, headers, HttpStatus.OK);
	}

	/**
	 * 평가 분석 (HAMS-S-ER-004)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getExamStt")
	public ResponseEntity getExamStt(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)hamsSalesService.getExamStt(params);

		return new ResponseEntity(body, headers, HttpStatus.OK);
	}

	/**
	 * 콘텐츠 추천 (HAMS-S-ER-005)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getRecommendedContents")
	public ResponseEntity getRecommendedContents(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)hamsSalesService.getRecommendedContents(params);

		return new ResponseEntity(body, headers, HttpStatus.OK);
	}

	/**
	 * 피드백 수정 (HAMS-S-ER-007)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/updateFeedback")
	@ResponseBody
	public ResponseEntity updateFeedback(@RequestBody Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)hamsSalesService.updateFeedback(params);

		return new ResponseEntity(body, headers, HttpStatus.OK);
	}

	/**
	 * 전환예측 결과 (HAMS-S-SP-001)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getSettleInfoPredictionRst")
	public ResponseEntity getSettleInfoPredictionRst(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)hamsSalesService.getSettleInfoPredictionRst(params);

		return new ResponseEntity(body, headers, HttpStatus.OK);
	}

	/**
	 * 전환예측 통계 (HAMS-S-SP-002)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getSettleInfoPredictionStt")
	public ResponseEntity getSettleInfoPredictionStt(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)hamsSalesService.getSettleInfoPredictionStt(params);

		return new ResponseEntity(body, headers, HttpStatus.OK);
	}

	/**
	 * 전환예측 학생 목록	(HAMS-S-SP-003)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getSettleInfoPredictionStudList")
	public ResponseEntity getSettleInfoPredictionStudList(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)hamsSalesService.getSettleInfoPredictionStudList(params);

		return new ResponseEntity(body, headers, HttpStatus.OK);
	}

	@RequestMapping("/getTest")
	public ResponseEntity getTest(@RequestParam Map<String, Object> params) throws Exception {
		body = (LinkedHashMap)hamsSalesService.getTest(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
}
