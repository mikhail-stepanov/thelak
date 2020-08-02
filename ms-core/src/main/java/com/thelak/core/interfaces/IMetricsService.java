package com.thelak.core.interfaces;

public interface IMetricsService {

    void sendMetric(String appName, String metricType, String metricJson);
}
