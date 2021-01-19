package com.daria.learn.rentalhelper.common.metric;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import io.micrometer.core.instrument.step.StepRegistryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class LogMeterRegistry extends StepMeterRegistry {
    private final Logger logger = LoggerFactory.getLogger(LogMeterRegistry.class);
    private static final Duration step = Duration.ofSeconds(30);

    public LogMeterRegistry() {
        super(new StepRegistryConfig() {
            @Override
            public String prefix() {
                return "log";
            }

            @Override
            public String get(String key) {
                return null;
            }

            @Override
            public Duration step() {
                return step;
            }
        }, Clock.SYSTEM);
    }

    @Override
    @Scheduled(fixedRate = 900000)
    protected void publish() {
        logger.info("Start metrics pulling");
        String metersLog = getMeters().stream()
                .map(meter -> new MeterData(
                        meter.getId().toString(),
                        StreamSupport.stream(meter.measure().spliterator(), false)
                                .map(measurement -> String.valueOf(measurement.getValue()))
                                .collect(Collectors.toList())))
                .map(MeterData::toString)
                .collect(Collectors.joining("\n"));
        logger.info("[METRICS]:\n" + metersLog);
    }

    @Override
    protected TimeUnit getBaseTimeUnit() {
        return TimeUnit.SECONDS;
    }

    private static class MeterData {
        private final String meterId;
        private final List<String> measurements;

        public MeterData(String meterId, List<String> measurements) {
            this.meterId = meterId;
            this.measurements = measurements;
        }

        public List<String> getMeasurements() {
            return measurements;
        }

        public String toString() {
            return "Meter [" + meterId + "]. Measurements: " + String.join(", ", measurements);
        }
    }
}
