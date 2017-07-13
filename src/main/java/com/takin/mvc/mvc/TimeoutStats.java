package com.takin.mvc.mvc;
//
//package com.takin.mvc.mvc.monitor;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.takin.mvc.mvc.Dispatcher;
//import com.takin.mvc.mvc.inject.MVCDI;
//
///**
// * 
// * 
// * @author WangYazhou
// * @date  2016年8月3日 下午4:33:03
// * @see
// */
//public class TimeoutStats {
//
//    public static TimeoutStats instance() {
//        return SingletonHolder.instance;
//    }
//
//    protected TimeoutStats() {
//    }
//
//    private static final Logger timeoutLog = LoggerFactory.getLogger("TIMEOUT");
//    private static final Logger statsLog = LoggerFactory.getLogger("STATS");
//
//    private final int interval = 1000 * 60; // 1 min
//
//    private RequestStats lastRequestStats = null;
//
//    private final int TIME_VALVE = 499;
//
//    public void log(long runtime, RequestStats currentRequestStats) {
//
//        if (lastRequestStats == null) {
//            lastRequestStats = currentRequestStats;
//            return;
//        }
//
//        RequestStats freezeLastRequestStats = this.lastRequestStats; //冻结
//
//        if (currentRequestStats.getCurrentTime() <= freezeLastRequestStats.getCurrentTime())
//            return;
//
//        // 1min, log stats
//        if ((currentRequestStats.getCurrentTime() - this.lastRequestStats.getCurrentTime()) >= interval) {
//            this.lastRequestStats = currentRequestStats;
//            statsLog.info(statsLog(currentRequestStats, freezeLastRequestStats));
//
//        }
//
//        if (hasTimeout(runtime, currentRequestStats, freezeLastRequestStats)) {
//            String timeOutMessage = timeoutLog(runtime, currentRequestStats, freezeLastRequestStats);
//            timeoutLog.info(timeOutMessage);
//            //            udpClient.send(appName + ", " + timeOutMessage);
//        }
//
//    }
//
//    protected boolean hasTimeout(long runtime, RequestStats currentRequestStats, RequestStats lastRequestStats) {
//
//        if (runtime > TIME_VALVE)
//            return true;
//
//        long timeDifference = currentRequestStats.getTotalTime() - lastRequestStats.getTotalTime();
//        long countDifference = currentRequestStats.getTotalCount() - lastRequestStats.getTotalCount();
//
//        return runtime > ((timeDifference * 2) / countDifference); // 2倍平均时长
//
//    }
//
//    protected String timeoutLog(long runtime, RequestStats currentRequestStats, RequestStats lastRequestStats) {
//        return String.format("time: %sms, concurrent: %s, url: %s.", runtime, currentRequestStats.getConcurrentRequest(), MVCDI.getInstance(Dispatcher.class).currentBeatContext().getRelativeUrl());
//    }
//
//    protected String statsLog(RequestStats currentRequestStats, RequestStats lastRequestStats) {
//
//        long totalTime = currentRequestStats.getTotalTime() - lastRequestStats.getTotalTime();
//        int count = currentRequestStats.getTotalCount() - lastRequestStats.getTotalCount();
//
//        long averageTime = totalTime / count;
//
//        long interval = currentRequestStats.getCurrentTime() - lastRequestStats.getCurrentTime();
//        float count1000 = count * 1000.0f;
//        float qps = (count1000) / interval;
//        float concurrent = (totalTime * 1.0f) / interval;
//
//        return String.format("average time: %s, qps: %.1f, concurrent: %.1f, total time: %sms, request count: %s.", averageTime, qps, concurrent, totalTime, count);
//    }
//
//    static class SingletonHolder {
//        static TimeoutStats instance = new TimeoutStats();
//    }
//}
