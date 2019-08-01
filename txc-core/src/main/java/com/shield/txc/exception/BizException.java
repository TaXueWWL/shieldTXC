package com.shield.txc.exception;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/7/30 16:12
 * @className BizException
 * @desc 业务异常
 */
public class BizException extends RuntimeException {

    public BizException() {
        super();
    }

    public BizException(String msg) {
        super(msg);
    }

    public BizException(Throwable e) {
        super(e);
    }


    public BizException(String msg, Throwable e) {
        super(msg, e);
    }
}
