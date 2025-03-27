package io.hhplus.tdd.user;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ConcurrencyTest {


    @Test
    @DisplayName("동시성 테스트")
    void test() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CountDownLatch countDownLatch = new CountDownLatch(5);
        for(int i = 0; i < 5; i++){
            executorService.execute(() -> {

                Test1.call();
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        System.out.println("메인 스레드");
    }

    public static class Test1 {
        static int count;

         static void call(){
            System.out.println("count = " + count++);
        }

    }
}
