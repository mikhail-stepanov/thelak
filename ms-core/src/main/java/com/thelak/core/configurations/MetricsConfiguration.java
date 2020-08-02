package com.thelak.core.configurations;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.config.NamingConvention;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;
import io.micrometer.jmx.JmxConfig;
import io.micrometer.jmx.JmxMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfiguration {

    @Bean
    public JmxMeterRegistry jmxMeterRegistry(JmxConfig config, Clock clock) {
        JmxMeterRegistry meterRegistry = new JmxMeterRegistry(config, clock, HierarchicalNameMapper.DEFAULT);
        meterRegistry.config().namingConvention(NamingConvention.dot);
        return meterRegistry;
    }
}
