package org.endeavourhealth.datasharingmanager.api.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlets.MetricsServlet;


public class DataSharingManagerMetricListener extends MetricsServlet.ContextListener {
    public static final MetricRegistry dataSharingManagerMetricRegistry = DataSharingManagerInstrumentedFilterContextListener.REGISTRY;

    @Override
    protected MetricRegistry getMetricRegistry() {
        return dataSharingManagerMetricRegistry;
    }
}
