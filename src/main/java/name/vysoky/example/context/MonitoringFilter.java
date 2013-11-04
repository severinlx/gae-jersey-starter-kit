package name.vysoky.example.context;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Persistence context filter.
 *
 * @author Jiri Vysoky
 */
public class MonitoringFilter implements ContainerResponseFilter {

    private static final Logger logger = Logger.getLogger(MonitoringFilter.class.getName());

    public MonitoringFilter() {
        super();
        logger.log(Level.FINEST, "Constructed monitoring filter.");
    }

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        MonitoredRequest monitoredRequest = (MonitoredRequest) request;
        logger.log(Level.FINEST, "Container request explicitly retyped to {0}", monitoredRequest);
        return monitoredRequest;
    }

    @Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        if (request instanceof MonitoredRequest) {
            MonitoredRequest monitoredRequest = (MonitoredRequest) request;
            monitoredRequest.finalizeMonitoredObjects();
            logger.log(Level.FINEST, "Monitored request finalized.");
        }
        return response;
    }
}
