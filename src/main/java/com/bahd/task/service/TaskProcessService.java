package com.bahd.task.service;

import com.bahd.task.TaskResult;

/**
 * @description: 任务处理，外部需要实现该接口
 * @author: bahaidong
 * @date: 2019-04-09 17:25
 **/
public interface TaskProcessService<T, R> {

    /**
     * 任务实际处理方法
     * @param data 业务参数
     * @return 返回业务结果
     */
    TaskResult<R> execute(T data);

}
