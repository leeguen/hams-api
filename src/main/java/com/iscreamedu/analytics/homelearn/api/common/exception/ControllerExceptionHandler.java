package com.iscreamedu.analytics.homelearn.api.common.exception;

import com.iscreamedu.analytics.homelearn.api.common.util.ValidationCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler(ParameterException.class)
    public final ResponseEntity ParameterNotFoundExceptionHandling(ParameterException ex, WebRequest request) {
        return new ResponseEntity(setMsgResult(ex.getDataArr()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoDataException.class)
    public final ResponseEntity NoDataExceptionHandling(ParameterException ex, WebRequest request) {
        return new ResponseEntity(setMsgResult(ex.getDataArr()), HttpStatus.NO_CONTENT);
    }

    /**
     * 예외 결과값 리턴
     * @param key -> 0:발생값, 1:원인, 2: 에러코드
     * @return Map
     */
    private Map setMsgResult(Object[] key) {
        HashMap msg = new HashMap();
        LinkedHashMap result = new LinkedHashMap();
        ValidationCode vC = (ValidationCode) key[2];

        result.put("status",vC.getStatus());
        result.put("resultCode", vC.getCode());
        result.put("result", key[0] + " : " + key[1]);

        msg.put("msg",result);

        return msg;
    }
}
