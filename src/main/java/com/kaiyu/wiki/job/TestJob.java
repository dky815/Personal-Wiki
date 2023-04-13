// package com.kaiyu.wiki.job;
//
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;
//
// import java.text.SimpleDateFormat;
// import java.util.Date;
//
// @Component
// public class TestJob {
//
//    private static final Logger LOG = LoggerFactory.getLogger(TestJob.class);
//
//    /**
//     * Fixed time interval, with fixedRate in milliseconds.
//     */
//    @Scheduled(fixedRate = 1000)
//    public void simple() throws InterruptedException {
//        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
//        String dateString = formatter.format(new Date());
//        Thread.sleep(2000);
//        LOG.info("Execute every 5 seconds: {}", dateString);
//    }
//
//    /**
//     * Run batch processing using a custom cron expression
//     * Execution at the next scheduled time will only occur if the previous execution has completed. Otherwise, it will be missed.
//     */
//    @Scheduled(cron = "*/1 * * * * ?")
//    public void cron() throws InterruptedException {
//        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss SSS");
//        String dateString = formatter.format(new Date());
//        Thread.sleep(1500);
//        LOG.info("Execute every 1 second: {}", dateString);
//    }
//
// }
