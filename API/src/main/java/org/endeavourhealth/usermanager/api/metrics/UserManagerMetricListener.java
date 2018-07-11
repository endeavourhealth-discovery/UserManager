package org.endeavourhealth.usermanager.api.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlets.MetricsServlet;


public class UserManagerMetricListener extends MetricsServlet.ContextListener {
    public static final MetricRegistry userManagerMetricRegistry = UserManagerInstrumentedFilterContextListener.REGISTRY;

    @Override
    protected MetricRegistry getMetricRegistry() {
        return userManagerMetricRegistry;
    }
}
