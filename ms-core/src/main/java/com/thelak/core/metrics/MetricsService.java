package com.thelak.core.metrics;

import org.springframework.scheduling.annotation.Scheduled;
import com.thelak.core.interfaces.IMetricsService;

import java.util.concurrent.ArrayBlockingQueue;

//@Component
public class MetricsService implements IMetricsService {

    private static class Metric {

        public Metric(String appName, String metricType, String metricJson){
            this.appName = appName;
            this.metricType = metricType;
            this.metricJson = metricJson;
        }

        String appName;

        String metricType;

        String metricJson;
    }

    final private ArrayBlockingQueue metrics = new ArrayBlockingQueue<Metric>(1000 * 1000);

    @Override
    public void sendMetric(String appName, String metricType, String metricJson) {
        metrics.add(new Metric(appName, metricType, metricJson));
    }


    @Scheduled(fixedDelay = 5*1000)
    private void sendMetrics(){
        if (metrics.isEmpty())
            return;
    }
}
