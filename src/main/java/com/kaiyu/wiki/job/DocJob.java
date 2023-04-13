package com.kaiyu.wiki.job;

import com.kaiyu.wiki.service.DocService;
import com.kaiyu.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class DocJob {

    private static final Logger LOG = LoggerFactory.getLogger(DocJob.class);

    @Resource
    private DocService docService;

    @Resource
    private SnowFlake snowFlake;

    /**
     * Update e-book information every 30 seconds.
     */
    @Scheduled(cron = "5/30 * * * * ?")
    public void cron() {
        // Add a log serial number
        MDC.put("LOG_ID", String.valueOf(snowFlake.nextId()));
        LOG.info("Updating document data under the e-book has started");
        long start = System.currentTimeMillis();
        docService.updateEbookInfo();
        LOG.info("Updating document data under the e-book has finished. Time elapsed: {} milliseconds.", System.currentTimeMillis() - start);
    }

}
