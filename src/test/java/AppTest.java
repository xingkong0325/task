import com.bahd.task.TaskResult;
import com.bahd.task.core.PendingJobPool;

import java.util.List;
import java.util.Random;

/**
 * @description: 测试类
 * @author: bahaidong
 * @date: 2019-04-09 20:41
 **/
public class AppTest {

    /***/
    private final static String JOB_NAME = "计算数值";
    /***/
    private final static int JOB_LENGTH = 1000;

    public static void main(String[] args) {
        MyTask myTask = new MyTask();
        PendingJobPool pool = PendingJobPool.getInstance();
        pool.registerJob(JOB_NAME, JOB_LENGTH, myTask, 5000);
        Random r = new Random();
        for(int i=0; i<JOB_LENGTH; i++){
            pool.putTask(JOB_NAME, r.nextInt(1000));
        }
        QueryResult queryResult = new QueryResult(pool);
        Thread thread = new Thread(queryResult);
        thread.start();
    }

    private static class QueryResult implements Runnable {

        /***/
        private PendingJobPool pool;

        public QueryResult(PendingJobPool pool) {
            this.pool = pool;
        }

        @Override
        public void run() {
            int i = 0;
            while(i < 350){
                try {
                    List<TaskResult<String>> taskDetails = pool.getTaskDetail(JOB_NAME);
                    if(!taskDetails.isEmpty()){
                        System.out.println(pool.getTaskProcess(JOB_NAME));
                        System.out.println(taskDetails);
                    }
                    Thread.sleep(100);
                } catch(Exception e) {

                }finally {
                    i++;
                }
            }
        }

    }

}
