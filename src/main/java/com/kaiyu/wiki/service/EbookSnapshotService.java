package com.kaiyu.wiki.service;

import com.kaiyu.wiki.mapper.EbookSnapshotMapperCust;
import com.kaiyu.wiki.resp.StatisticResp;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EbookSnapshotService {

    @Resource
    private EbookSnapshotMapperCust ebookSnapshotMapperCust;

    public void genSnapshot() {
        ebookSnapshotMapperCust.genSnapshot();
    }

    /**
     * "Get home page numerical data: total number of reads, total number of likes, number of reads today, number of likes today, estimated number of reads today, and expected growth in number of reads today.
     */
    public List<StatisticResp> getStatistic() {
        return ebookSnapshotMapperCust.getStatistic();
    }

    /**
     * 30-day numerical statistics
     */
    public List<StatisticResp> get30Statistic() {
        return ebookSnapshotMapperCust.get30Statistic();
    }

}
