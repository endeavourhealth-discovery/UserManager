package org.endeavourhealth.datasharingmanager.api.metrics;


import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.servlet.InstrumentedFilterContextListener;

public class DataSharingManagerInstrumentedFilterContextListener extends InstrumentedFilterContextListener {

    public static final MetricRegistry REGISTRY = SharedMetricRegistries.getOrCreate("dataSharingManagerMetricRegistry");

    @Override
    protected MetricRegistry getMetricRegistry() {
        return REGISTRY;
    }
}
