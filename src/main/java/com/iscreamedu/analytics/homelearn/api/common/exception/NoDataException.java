package com.iscreamedu.analytics.homelearn.api.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NO_CONTENT)
public class NoDataException extends RuntimeException{
    final Object[] dataArr;
    public NoDataException(Object[] data) {
        super((String) data[0]);
        this.dataArr = data;
    }

    public Object[] getDataArr() {
        return dataArr;
    }
}
