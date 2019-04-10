package com.bahd.task.core;

import com.bahd.task.JobInfo;
import com.bahd.task.TaskResult;
import com.bahd.task.TaskResultType;
import com.bahd.task.service.TaskProcessService;

import java.util.List;
import java.util.concurrent.*;

/**
 * @description: 框架核心类
 * @author: bahaidong
 * @date: 2019-04-09 19:52
 **/
public class PendingJobPool {

    /** 线程数 */
    private static final int THREAD_COUNTS;
    /** 默认线程数 */
    private static final int DEFAULT_COUNTS = 8;
    static {
        int count = Runtime.getRuntime().availableProcessors();
        if (count > DEFAULT_COUNTS) { // 如果线程数大于8，则指定线程数为8
            count = DEFAULT_COUNTS;
        }
        THREAD_COUNTS = count;
    }
    /** 任务队列 */
    private static BlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<>(5000);
    /** 定义线程池 */
    private static ExecutorService executor = new ThreadPoolExecutor(THREAD_COUNTS, THREAD_COUNTS, 60L, TimeUnit.SECONDS, taskQueue);
    /** 存放job的容器 */
    private static ConcurrentHashMap<String, JobInfo<?>> jobInfoMap = new ConcurrentHashMap<>();

    /** 私有构造方法 */
    private PendingJobPool(){}

    private static class PendingJobInfoHolder{
        /** 对象 */
        private static PendingJobPool pendingJobInfo = new PendingJobPool();
    }

    /** 获取单例对象 */
    public static PendingJobPool getInstance(){
        return PendingJobInfoHolder.pendingJobInfo;
    }

    /** 获取jobInfo容器 */
    public static ConcurrentHashMap<String, JobInfo<?>> getMap(){
        return jobInfoMap;
    }

    /**
     * 注册job
     * @param jobName
     * @param jobLength
     * @param taskProcessService
     * @param expireTime
     * @param <R>
     */
    public <R> void registerJob(String jobName, int jobLength, TaskProcessService<?, ?> taskProcessService, long expireTime){
        JobInfo<R> jobInfo = new JobInfo<>(jobName, jobLength, taskProcessService, expireTime);
        if(jobInfoMap.putIfAbsent(jobName, jobInfo) != null){
            throw new RuntimeException(jobName + "已经注册过了！");
        }
    }

    /** 提交任务 */
    public <T, R> void putTask(String jobName, T data){
        JobInfo<R> jobInfo = getJob(jobName);
        PendingTask<T, R> pendingTask = new PendingTask<>(jobInfo, data);
        executor.submit(pendingTask);
    }

    /** 返回任务详情 */
    public <R> List<TaskResult<R>> getTaskDetail(String jobName){
        JobInfo<R> jobInfo = getJob(jobName);
        return jobInfo.getTaskDetail();
    }

    /** 返回任务进度 */
    public <R> String getTaskProcess(String jobName){
        JobInfo<R> jobInfo = getJob(jobName);
        return jobInfo.getTotalProcess();
    }

    /** 获取job */
    private <R> JobInfo<R> getJob(String jobName){
        JobInfo<R> jobInfo = (JobInfo<R>)jobInfoMap.get(jobName);
        if(jobInfo == null){
            throw new RuntimeException(jobName + "是非法job！");
        }
        return jobInfo;
    }

    /** 包装任务提交给线程池使用，并处理任务的结果，写入缓存工查询 */
    private static class PendingTask<T, R> implements Runnable {

        /** 任务信息 */
        private JobInfo<R> jobInfo;
        /** 参数 */
        private T data;

        /** 构造方法 */
        public PendingTask(JobInfo<R> jobInfo, T data) {
            this.jobInfo = jobInfo;
            this.data = data;
        }

        @Override
        public void run() {
            R r = null;
            TaskProcessService<T, R> taskProcessService = (TaskProcessService<T, R>)jobInfo.getTaskProcessService();
            TaskResult<R> taskResult = null;
            try {
                // 实际执行任务
                taskResult = taskProcessService.execute(data);
                if(taskResult == null){
                    taskResult = new TaskResult<>(TaskResultType.EXCEPTION, r, "result is null");
                    return;
                }
                if(taskResult.getResultType() == null){
                    if(taskResult.getReason() == null){
                        taskResult = new TaskResult<>(TaskResultType.EXCEPTION, r, "resultType is null and reason is null");
                    }else{
                        taskResult = new TaskResult<>(TaskResultType.EXCEPTION, r, "resultType is null reason:" + taskResult.getReason());
                    }
                    return;
                }
            } catch (Exception e) {
                taskResult = new TaskResult<>(TaskResultType.EXCEPTION, r, e.getMessage());
                e.printStackTrace();
            } finally {
                jobInfo.addTaskResult(taskResult);
            }
        }
    }
}
