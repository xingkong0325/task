package com.bahd.task.core;

import com.bahd.task.ItemVo;

import java.util.concurrent.DelayQueue;

/**
 * @description: 任务完成后,在一定的时间供查询，之后为释放资源节约内存，需要定期处理过期的任务
 * 单例：懒汉模式
 * @author: bahaidong
 * @date: 2019-04-09 18:21
 **/
public class CkeckJobProcesser {

    /** 延迟队列 */
    private static DelayQueue<ItemVo<String>> queue = new DelayQueue<>();

    static {
        FetchJob fetchJob = new FetchJob();
        Thread thread = new Thread(fetchJob);
        thread.setDaemon(true);
        thread.start();
        System.out.println("开启任务过期检查守护线程................");
    }

    /** 私有构造方法 */
    private CkeckJobProcesser(){};

    private static class CheckJobHolder{
        /** 新建对象 */
        public static CkeckJobProcesser processer = new CkeckJobProcesser();
    }

    /** 获取实例 */
    public static CkeckJobProcesser getInstance(){
        return CheckJobHolder.processer;
    }

    /* 任务完成后，放入队列，经过expireTime时间后，从整个框架中移除 */
    public void putJob(String jobName, long expireTime){
        ItemVo<String> itemVo = new ItemVo<>(expireTime, jobName);
        queue.offer(itemVo);
        System.out.println("Job[ " + jobName + " 已经放入了过期检查缓存，过期时长:" + expireTime);
    }

    private static class FetchJob implements Runnable {

        @Override
        public void run() {
            while (true){
                try {
                    ItemVo<String> itemVo = queue.take();
                    String jobName = itemVo.getData();
                    // 删除过期任务
                    PendingJobPool.getMap().remove(jobName);
                    System.out.println(jobName + " is out of date,remove from map!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
