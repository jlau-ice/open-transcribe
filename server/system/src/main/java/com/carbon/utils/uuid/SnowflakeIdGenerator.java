package com.carbon.utils.uuid;

/**
 * 雪花ID生成工具类
 *
 * @author ganbin
 */
public class SnowflakeIdGenerator {
    // 数据中心ID（0-31）
    private long dataCenterId;
    // 机器ID（0-31）
    private long workerId;
    // 毫秒内序列（0-4095）
    private long sequence = 0L;
    // 上次生成ID的时间戳
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(long dataCenterId, long workerId) {
        if (dataCenterId < 0 || dataCenterId > 31 || workerId < 0 || workerId > 31) {
            throw new IllegalArgumentException("DataCenter ID and Worker ID must be between 0 and 31");
        }
        this.dataCenterId = dataCenterId;
        this.workerId = workerId;
    }

    public SnowflakeIdGenerator() {
    }

    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate ID");
        }

        if (timestamp == lastTimestamp) {
            // 序列掩码
            long sequenceMask = 4095L;
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = waitNextMillis(timestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        // 时间戳左移位数
        long timestampLeftShift = 22L;
        // 数据中心ID左移位数
        long dataCenterIdShift = 17L;
        // 机器ID左移位数
        long workerIdShift = 12L;
        return ((timestamp << timestampLeftShift) | (dataCenterId << dataCenterIdShift) | (workerId << workerIdShift) | sequence);
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    public static void main(String[] args) {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1, 0); // 假设机器ID为1
        for (int i = 0; i < 1000; i++) {
            long id = generator.nextId();
            System.out.println(Long.toBinaryString(id));
            System.out.println(id);
        }
    }
}
