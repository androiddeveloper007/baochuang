package com.szbc.front.mine.orderdone;

import org.xutils.http.annotation.HttpResponse;

/**
 * Created by ZP on 2017/5/25.
 */

@HttpResponse(parser = ResultParser.class)
public class ResponseEntity {
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
