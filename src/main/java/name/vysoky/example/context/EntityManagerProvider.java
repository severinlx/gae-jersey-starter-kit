package name.vysoky.example.context;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.PerRequestTypeInjectableProvider;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Provide injectable persistence context which produce entity manager per request.
 *
 * @author Jiri Vysoky
 */
public class EntityManagerProvider extends PerRequestTypeInjectableProvider<PersistenceContext, EntityManager> {

    private static final Logger logger = Logger.getLogger(EntityManagerProvider.class.getName());


    private Map<String, EntityManagerFactory> factoryMap = new HashMap<String, EntityManagerFactory>(1);

    public EntityManagerProvider() {
        super(EntityManager.class);
    }

    @Override
    public Injectable<EntityManager> getInjectable(ComponentContext componentContext, PersistenceContext persistenceContext) {
        final String unitName = persistenceContext.unitName();
        return new AbstractHttpContextInjectable<EntityManager>() {
            @Override
            public EntityManager getValue(HttpContext httpContext) {
                EntityManager entityManager = getFactory(unitName).createEntityManager();
                HttpRequestContext requestContext = httpContext.getRequest();
                requestContext

                monitoredRequest.setEntityManager(entityManager);
                logger.log(Level.INFO, "Created entity manager \"{0}\"!", entityManager);
                return entityManager;
            }
        };
    }

    private EntityManagerFactory getFactory(String unitName) {
        if (factoryMap.containsKey(unitName)) {
            logger.log(Level.INFO, "Used previously cached entity manager factory for unit name \"{0}\"!", unitName);
            return factoryMap.get(unitName);
        } else {
            EntityManagerFactory factory = Persistence.createEntityManagerFactory(unitName);
            factoryMap.put(unitName, factory);
            logger.log(Level.INFO, "Created new entity manager factory for unit name \"{0}\"", unitName);
            return factory;
        }
    }
}
