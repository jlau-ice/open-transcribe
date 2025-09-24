package com.carbon.utils.uuid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * ID生成器工具类
 *
 * @author jkr
 */
@Component
public class IdUtils {
	/**
	 * 数据中心ID
	 */
	private static long dataCenterId;

	/**
	 * 机器ID
	 */
	private static long workerId;

	private static SnowflakeIdGenerator generator;
	/**
	 * 获取雪花生成器实例对象
	 *
	 * @return SnowflakeIdGenerator
	 */
	public static SnowflakeIdGenerator getSnowGenerator() {
		if (generator == null) {
			synchronized (SnowflakeIdGenerator.class) {
				if (generator == null) {
					generator = new SnowflakeIdGenerator(dataCenterId,workerId);
				}
			}
		}
		return generator;
	}

	@Value("${com.carbon.snowflake.dataCenterId}")
	public void setDataCenterId(long dataCenterId) {
		IdUtils.dataCenterId = dataCenterId;
	}

	@Value("${com.carbon.snowflake.workerId}")
	public void setWorkerId(long workerId) {
		IdUtils.workerId = workerId;
	}

	/**
	 * 获取随机UUID
	 *
	 * @return 随机UUID
	 */
	public static String randomUUID() {
		return UUID.randomUUID().toString();
	}

	/**
	 * 简化的UUID，去掉了横线
	 *
	 * @return 简化的UUID，去掉了横线
	 */
	public static String simpleUUID() {
		return UUID.randomUUID().toString(true);
	}

	/**
	 * 获取随机UUID，使用性能更好的ThreadLocalRandom生成UUID
	 *
	 * @return 随机UUID
	 */
	public static String fastUUID() {
		return UUID.fastUUID().toString();
	}

	/**
	 * 简化的UUID，去掉了横线，使用性能更好的ThreadLocalRandom生成UUID
	 *
	 * @return 简化的UUID，去掉了横线
	 */
	public static String fastSimpleUUID() {
		return UUID.fastUUID().toString(true);
	}

	/**
	 * 获取雪花算法ID
	 *
	 * @return ID
	 */
	public static Long getSnowflakeId(){
		return getSnowGenerator().nextId();
	}
}
