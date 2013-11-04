package name.vysoky.example.context;

import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.WebApplication;

import javax.persistence.EntityManager;
import java.io.InputStream;
import java.net.URI;
import java.util.logging.Logger;

/**
 * Decorated request used to store additional request informations.
 * @author Jiri Vysoky
 */
public class MonitoredRequest extends ContainerRequest {

    private static final Logger logger = Logger.getLogger(MonitoredRequest.class.getName());

    private EntityManager entityManager;

    public MonitoredRequest(EntityManager entityManager) {
        super(entityManager);

    }

    /**
     * Setter for monitored entity manager.
     * @param entityManager entity manager
     */
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Finalize all monitored objects.
     * This should be called when response is returned.
     */
    void finalizeMonitoredObjects() {
         if (entityManager != null) {
             if (entityManager.isOpen()) {
                 entityManager.flush();
                 entityManager.close();
             }
             entityManager = null;
         }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        finalizeMonitoredObjects();
    }
}
