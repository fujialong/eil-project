import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-09-27 09:02
 **/
//@Slf4j
public class ThreadTest {

    //线程池中核心线程数
    private final static int THREAD_POOL_SIZE = 3;
    //线程最大数
    private static final int MAX_THREAD_POOL_SIZE = 5;

    @Test
    public void testMyThread() throws ExecutionException, InterruptedException {
       // testMethod();
    }

    public void testMethod() throws InterruptedException, ExecutionException {
        BlockingQueue<Runnable> workQuene = new ArrayBlockingQueue<>(10);

        ThreadPoolExecutor executor = new ThreadPoolExecutor(THREAD_POOL_SIZE, MAX_THREAD_POOL_SIZE,
                0, TimeUnit.MICROSECONDS, workQuene);

        CompletionService completionService = new ExecutorCompletionService(executor);
        List<String> list = Arrays.asList("A", "B", "C");
        for (String type : list) {
            //将任务提交到阻塞队列
            completionService.submit(new CumputerRu(type));
        }
        //不在接收新的任务
        executor.shutdown();
        final long start = System.nanoTime();
        double computerResult = 0.0d;
        for (String type : list) {
            //如果有任务完成那么就从take中返回
            Future<Double> f = completionService.take();
            computerResult += f.get();
          //  log.info("result:" + f.get() + "----after" + (System.nanoTime() - start) / 1000 / 1000 + "秒");
        }
       // log.info("computerResult:" + computerResult);
    }

    @Data
    class CumputerRu implements Callable {

        private String type;

        private double result;

        public CumputerRu(String type) {
            this.type = type;
        }

        @Override
        public Object call() throws Exception {
            switch (type) {
                case "A":
                    result = 1.1;
                   // log.info("sleep 1 mills");
                    Thread.sleep(1000);
                    break;
                case "B":
                    result = 2.2;
                   // log.info("sleep 2 mills");
                    Thread.sleep(2000);
                    break;
                case "C":
                    result = 3.3;
                   // log.info("sleep 3 mills");
                    Thread.sleep(3000);
                    break;
                default:
                    break;
            }
            return result;
        }
    }

    @Test
    public void testComputer() {
        System.out.println("============="+testAdd());
    }

    public static double testAdd() {
        BlockingQueue<Runnable> workQuene = new ArrayBlockingQueue<>(10);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(THREAD_POOL_SIZE, MAX_THREAD_POOL_SIZE,
                0, TimeUnit.MICROSECONDS, workQuene);
        CompletionService completionService = new ExecutorCompletionService(executor);
        List<String> list = Arrays.asList("R1", "R2", "R3", "R4");
        for (String type : list) {
            completionService.submit(new Callable() {
                double result = 0;
                @Override
                public Object call() throws Exception {
                    switch (type) {
                        case "R1":
                            result = 5;
                            break;
                        case "R2":
                            result = 5;
                            break;
                        case "R3":
                            result = 5;
                            break;
                        case "R4":
                            result = 5;
                            break;
                        default:
                            break;
                    }
                    return result;
                }

            });
        }
        executor.shutdown();
        double computerResult = 0.0d;
        for (String type : list) {
            try {
                Future<Double> f = completionService.take();
                computerResult += f.get() == null ? 0 : f.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return computerResult;
    }



}
