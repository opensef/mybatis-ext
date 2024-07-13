package com.opensef.mybatisext.idhandler;

/**
 * 生成id（单利模式）
 */
public class IdGen {

    private IdGen() {

    }

    private static IdWorker idWorker = null;

    private static synchronized void syncInit() {
        if (null == idWorker) {
            // 如下方式如果在多台机器部署，可能会出现id重复问题，概率极低，可忽略。如果是集群环境，可用集群分布式id向外提供服务
            idWorker = new IdWorker(0, 0);
        }
    }

    public static long getId() {
        if (null == idWorker) {
            syncInit();
        }
        return idWorker.nextId();
    }

}
