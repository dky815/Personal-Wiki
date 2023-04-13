package com.kaiyu.wiki.util;

import org.springframework.stereotype.Component;

import java.text.ParseException;

/**
 * Twitter's distributed unique ID generator - Snowflake algorithm.
 **/
@Component
public class SnowFlake {

    /**
     * Starting timestamp
     */
    private final static long START_STMP = 1609459200000L; // 2021-01-01 00:00:00

    /**
     * The number of bits used for each component
     */
    private final static long SEQUENCE_BIT = 12; // The number of bits occupied by the serial number.
    private final static long MACHINE_BIT = 5;   // The number of bits occupied by the machine identifier.
    private final static long DATACENTER_BIT = 5;// The number of bits used for the data center identifier.

    /**
     * The maximum value of each part
     */
    private final static long MAX_DATACENTER_NUM = -1L ^ (-1L << DATACENTER_BIT);
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

    /**
     * The left shift of each section
     */
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

    private long datacenterId = 1;  // datacenter identifier
    private long machineId = 1;     // machine identifier
    private long sequence = 0L; // The sequence number
    private long lastStmp = -1L;// The last timestamp

    public SnowFlake() {
    }

    public SnowFlake(long datacenterId, long machineId) {
        if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
            throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }
        this.datacenterId = datacenterId;
        this.machineId = machineId;
    }

    /**
     * To generate the next ID
     *
     * @return
     */
    public synchronized long nextId() {
        long currStmp = getNewstmp();
        if (currStmp < lastStmp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }

        if (currStmp == lastStmp) {
            // The sequence number increments within the same millisecond.
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // The sequence number for the same millisecond has reached the maximum value.
            if (sequence == 0L) {
                currStmp = getNextMill();
            }
        } else {
            // Different milliseconds have different sequence numbers.
            sequence = 0L;
        }

        lastStmp = currStmp;

        return (currStmp - START_STMP) << TIMESTMP_LEFT // The timestamp part.
                | datacenterId << DATACENTER_LEFT       // The datacenter part.
                | machineId << MACHINE_LEFT             // The machine identifier part.
                | sequence;                             // The sequence number part.
    }

    private long getNextMill() {
        long mill = getNewstmp();
        while (mill <= lastStmp) {
            mill = getNewstmp();
        }
        return mill;
    }

    private long getNewstmp() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) throws ParseException {
        // Timestamp
        // System.out.println(System.currentTimeMillis());
        // System.out.println(new Date().getTime());
        //
        // String dateTime = "2021-01-01 08:00:00";
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        // System.out.println(sdf.parse(dateTime).getTime());

        SnowFlake snowFlake = new SnowFlake(1, 1);

        long start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            System.out.println(snowFlake.nextId());
            System.out.println(System.currentTimeMillis() - start);
        }
    }
}
