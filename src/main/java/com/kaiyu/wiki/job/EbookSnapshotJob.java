package com.kaiyu.wiki.job;

import com.kaiyu.wiki.service.EbookSnapshotService;
import com.kaiyu.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class EbookSnapshotJob {

    private static final Logger LOG = LoggerFactory.getLogger(EbookSnapshotJob.class);

    @Resource
    private EbookSnapshotService ebookSnapshotService;

    @Resource
    private SnowFlake snowFlake;

    /**
     * Run batch processing using a custom cron expression
     * Execution at the next scheduled time will only occur if the previous execution has completed. Otherwise, it will be missed.
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void doSnapshot() {
        // Add a log serial number
        MDC.put("LOG_ID", String.valueOf(snowFlake.nextId()));
        LOG.info("Generating today's e-book snapshot has started.");
        Long start = System.currentTimeMillis();
        ebookSnapshotService.genSnapshot();
        LOG.info("Generating today's e-book snapshot has finished. Time elapsed: {} milliseconds.", System.currentTimeMillis() - start);
    }

}
