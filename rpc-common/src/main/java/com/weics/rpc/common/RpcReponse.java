package com.weics.rpc.common;



/**
 * 封装RPC的响应
 * Created by weics on 2017/7/3.
 */
public class RpcReponse {

    private String requestId;
    private Throwable error;
    private Object result;

    public boolean isError(){
        return error != null;
    }




    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
