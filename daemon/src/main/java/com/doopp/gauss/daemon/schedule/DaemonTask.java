package com.doopp.gauss.daemon.schedule;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class DaemonTask {

    @Scheduled(cron = "0/1 * * * * ?")
    public void executeMakePublishTask() {
        System.out.println("hello daemon !");
    }
}
