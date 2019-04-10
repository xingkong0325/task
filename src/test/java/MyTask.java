import com.bahd.task.TaskResult;
import com.bahd.task.TaskResultType;
import com.bahd.task.service.TaskProcessService;

import java.util.Random;

/**
 * @description: 测试任务
 * @author: bahaidong
 * @date: 2019-04-09 20:35
 **/
public class MyTask implements TaskProcessService<Integer, Integer> {

    @Override
    public TaskResult<Integer> execute(Integer data) {
        Random random = new Random();
        int flag = random.nextInt(500);
        try {
            Thread.sleep(flag);
        } catch (InterruptedException e) {
        }
        if(flag <= 300){ // 正常处理的情况
            Integer returnValue = data.intValue() + flag;
            return new TaskResult<>(TaskResultType.SUCCESS, returnValue);
        }else if(flag > 301 && flag < 400){ // 任务处理失败
            return new TaskResult<>(TaskResultType.FAIL, -1, "FAIL");
        }else{
            try {
                throw new RuntimeException("发生异常了！");
            } catch (RuntimeException e) {
                return new TaskResult<>(TaskResultType.EXCEPTION, -1, e.getMessage());
            }
        }
    }
}
