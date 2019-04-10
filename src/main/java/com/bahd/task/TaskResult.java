package com.bahd.task;

/**
 * @description: 任务结果
 * @author: bahaidong
 * @date: 2019-04-09 17:27
 **/
public class TaskResult<R> {

    /** 结果类型 */
    private final TaskResultType resultType;
    /** 业务返回结果 */
    private final R returnValue;
    /** 失败原因 */
    private final String reason;

    /** 构造函数 */
    public TaskResult(TaskResultType resultType, R returnValue) {
        this(resultType, returnValue, "SUCCESS");
    }

    /** 构造函数 */
    public TaskResult(TaskResultType resultType, R returnValue, String reason) {
        this.resultType = resultType;
        this.returnValue = returnValue;
        this.reason = reason;
    }

    public TaskResultType getResultType() {
        return resultType;
    }

    public R getReturnValue() {
        return returnValue;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "TaskResult{" +
                "resultType=" + resultType +
                ", returnValue=" + returnValue +
                ", reason='" + reason + '\'' +
                '}';
    }
}
