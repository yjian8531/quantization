package com.example.core.common.utils;

/**
 * 策略订单相关常量
 */
public class StrategyConstant {

    /**
     * 订单状态
     */
    public static class OrderStatus {
        /** 启动中 */
        public static final int STARTING = 0;
        /** 运行中 */
        public static final int RUNNING = 1;
        /** 暂停 */
        public static final int PAUSED = 2;
        /** 已结束 */
        public static final int ENDED = 3;
    }

    /**
     * 云服务器创建状态
     */
    public static class CloudServerStatus {
        /** 创建中 */
        public static final int CREATING = 0;
        /** 创建成功 */
        public static final int CREATED = 1;
        /** 创建失败 */
        public static final int FAILED = 2;
    }

    /**
     * 主机配置状态
     */
    public static class ConfigStatus {
        /** 正常 */
        public static final int NORMAL = 0;
        /** 禁用 */
        public static final int DISABLED = 1;
    }

    /**
     * 交易所平台
     */
    public static class Footplate {
        /** 币安 */
        public static final int BINANCE = 0;
        /** Gate */
        public static final int GATE = 1;
    }

    /**
     * APIKey类型
     */
    public static class ApikeyType {
        /** 现货 */
        public static final int SPOT = 0;
        /** 期货 */
        public static final int FUTURES = 1;
    }

    /**
     * 定时任务相关
     */
    public static class Task {
        /** 云服务器创建查询间隔(毫秒) */
        public static final long SERVER_CHECK_INTERVAL = 10 * 1000L;
        /** 最大查询次数 */
        public static final int MAX_CHECK_COUNT = 60;
    }

    /**
     * 策略订单流水号前缀
     */
    public static final String ORDER_NO_PREFIX = "CL";

    /**
     * 主机流水号前缀
     */
    public static final String MAIN_NO_PREFIX = "MAIN";
}
