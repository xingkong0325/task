package com.bahd.task;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @description: 存放延迟队列的元素
 * @author: bahaidong
 * @date: 2019-04-09 18:10
 **/
public class ItemVo<T> implements Delayed {

    /** 任务到期时间 */
    private final long activeTime;
    /** 元素 */
    private final T data;

    /** 构造方法 */
    public ItemVo(long activeTime, T data) {
        this.activeTime = TimeUnit.NANOSECONDS.convert(activeTime, TimeUnit.MILLISECONDS) + System.nanoTime();
        this.data = data;
    }

    public long getActiveTime() {
        return activeTime;
    }

    public T getData() {
        return data;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(activeTime - System.nanoTime(), TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        long d = getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS);
        return (d==0)?0:((d>0)?1:-1);
    }
}
