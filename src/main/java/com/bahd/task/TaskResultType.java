package com.bahd.task;

/**
 * @description: 任务结果类型
 * @author: bahaidong
 * @date: 2019-04-09 17:30
 **/
public enum TaskResultType {

    /**
     * 方法执行成功，并且返回的是业务需要的结果
     */
    SUCCESS,
    /**
     * 方法执行成功，但是返回的是业务不需要的结果
     */
    FAIL,
    /**
     * 反方执行抛出了exception
     */
    EXCEPTION;

}
