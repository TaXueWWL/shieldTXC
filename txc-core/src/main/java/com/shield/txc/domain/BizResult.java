package com.shield.txc.domain;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/7/31 19:46
 * @className BizResult
 * @desc 内部业务返回体
 */
public class BizResult<T> {

    private int bizCode;
    private T bizData;

    public BizResult() {
    }

    public BizResult(int bizCode) {
        this.bizCode = bizCode;
        this.bizData = null;
    }

    public static BizResult bizResult(int bizCode) {
        return new BizResult(bizCode);
    }

    public static <T> BizResult bizResult(int bizCode, T bizData) {
        return new BizResult(bizCode, bizData);
    }

    public BizResult(int bizCode, T bizData) {
        this.bizCode = bizCode;
        this.bizData = bizData;
    }

    public int getBizCode() {
        return bizCode;
    }

    public BizResult<T> setBizCode(int bizCode) {
        this.bizCode = bizCode;
        return this;
    }

    public T getBizData() {
        return bizData;
    }

    public BizResult<T> setBizData(T bizData) {
        this.bizData = bizData;
        return this;
    }
}
