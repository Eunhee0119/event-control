package com.example.api.service;

import com.example.api.domain.Coupon;
import com.example.api.repository.CouponRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class ApplyServiceTest {

    @Autowired
    private ApplyService applyService;

    @Autowired
    private CouponRepository couponRepository;

    @Test
    void 한번응모() {
        applyService.apply(1L);

        long count = couponRepository.count();

        assertThat(count).isEqualTo(1);
    }



    /**
     * redis를 사용하여 lazy
    * */
    @Test
    void 여러명응모() throws InterruptedException {
        int therdCount =1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        CountDownLatch latch = new CountDownLatch(therdCount);
        for(int i =0; i<therdCount;i++){
            long userId = i;
            executorService.submit(() ->{
                try {
                    applyService.apply(userId);
                }finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Thread.sleep(10000);  //컨슈머에서 작업 처리하는 시간이 필요

        long count = couponRepository.count();

        assertThat(count).isEqualTo(100);

    }
}