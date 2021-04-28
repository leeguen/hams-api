package com.iscreamedu.analytics.homelearn.api.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.LinkedHashMap;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ParameterException extends RuntimeException{
    final Object[] dataArr;
    public ParameterException(Object[] data) {
        super((String) data[0]);
        this.dataArr = data;
    }

    public Object[] getDataArr() {
        return dataArr;
    }

}
