package com.bahd.task;

import com.bahd.task.core.CkeckJobProcesser;
import com.bahd.task.service.TaskProcessService;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description: 任务信息实体类
 * @author: bahaidong
 * @date: 2019-04-09 17:47
 **/
public class JobInfo<R> {

    /** 任务名称 */
    private final String jobName;
    /** 任务长度 */
    private final int jobLength;
    /** 任务处理器 */
    private final TaskProcessService<?, ?> taskProcessService;
    /** 成功任务数 */
    private final AtomicInteger successCount;
    /** 已处理的任务数 */
    private final AtomicInteger taskProcessCount;
    /** 任务结果队列，从头拿结果，结果放在尾部 */
    private final LinkedBlockingDeque<TaskResult<R>> taskDetailQueue;
    /** 任务结果保存的时间，超过这个时间清除任务结果，单位ms */
    private final long expireTime;

    /** 构造函数 */
    public JobInfo(String jobName, int jobLength, TaskProcessService<?, ?> taskProcessService, long expireTime) {
        this.jobName = jobName;
        this.jobLength = jobLength;
        this.taskProcessService = taskProcessService;
        this.successCount = new AtomicInteger(0);
        this.taskProcessCount = new AtomicInteger(0);
        this.taskDetailQueue = new LinkedBlockingDeque<>(jobLength);
        this.expireTime = expireTime;
    }

    public String getJobName() {
        return jobName;
    }

    public int getJobLength() {
        return jobLength;
    }

    public TaskProcessService<?, ?> getTaskProcessService() {
        return taskProcessService;
    }

    public AtomicInteger getSuccessCount() {
        return successCount;
    }

    public AtomicInteger getTaskProcessCount() {
        return taskProcessCount;
    }

    public LinkedBlockingDeque<TaskResult<R>> getTaskDetailQueue() {
        return taskDetailQueue;
    }

    public long getExpireTime() {
        return expireTime;
    }

    /**
     * 任务处理进度
     * @return
     */
    public String getTotalProcess() {
        return "Success[" + successCount.get() + "]/Current[" + taskProcessCount.get() + "] Total[" + jobLength + "]";
    }

    /**
     * 获取任务结果列表
     * @return
     */
    public List<TaskResult<R>> getTaskDetail(){
        List<TaskResult<R>> result = new LinkedList<>();
        TaskResult<R> taskResult;
        while((taskResult = taskDetailQueue.pollFirst()) != null){
            result.add(taskResult);
        }
        return result;
    }

    /**
     * 放任务的结果
     * @param taskResult
     */
    public void addTaskResult(TaskResult<R> taskResult){
        if(taskResult == null){
            return;
        }
        // 任务执行成功
        if(TaskResultType.SUCCESS.equals(taskResult.getResultType())){
            successCount.incrementAndGet();
        }
        taskDetailQueue.addLast(taskResult);
        taskProcessCount.incrementAndGet();
        // 放入延迟队列检查任务结果是否过期
        if(taskProcessCount.get() == jobLength){
            CkeckJobProcesser.getInstance().putJob(jobName, expireTime);
        }
    }

}
