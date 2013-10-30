package name.vysoky.example.context;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
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
 * @author Jiri Vysoky
 */
public class EntityManagerProvider
        extends PerRequestTypeInjectableProvider<PersistenceContext, EntityManager>
        implements ContainerResponseFilter {

    private static final Logger logger = Logger.getLogger(EntityManagerProvider.class.getName());


    private Map<String, EntityManagerFactory> entityManagerFactoryPool = new HashMap<String, EntityManagerFactory>(1);
    private Map<String, EntityManager> entityMangerPool = new HashMap<String, EntityManager>(100);


    public EntityManagerProvider() {
        super(EntityManager.class);
        logger.log(Level.INFO, "Created entity manager provider.");
    }

    @Override
    public Injectable<EntityManager> getInjectable(ComponentContext componentContext, PersistenceContext persistenceContext) {
        final String unitName = persistenceContext.unitName();
        return new AbstractHttpContextInjectable<EntityManager>() {
            @Override
            public EntityManager getValue(HttpContext httpContext) {
                return createAndPoolEntityManager(httpContext.getRequest(), getEntityManagerFactory(unitName));
            }
        };
    }

    @Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        closeAndEvictEntityManager(request);
        return response;
    }

    private String getKey(HttpRequestContext request) {
        String key = request.toString();
        key = key.substring(key.length() - 8);
        return key;
    }

    private EntityManagerFactory getEntityManagerFactory(String unitName) {
        if (!entityManagerFactoryPool.containsKey(unitName)) {
            entityManagerFactoryPool.put(unitName, Persistence.createEntityManagerFactory(unitName));
            logger.log(Level.INFO,  "Created entity manager factory from unit name {0}", unitName);
        }
        return entityManagerFactoryPool.get(unitName);
    }

    private EntityManager createAndPoolEntityManager(HttpRequestContext request, EntityManagerFactory entityManagerFactory) {
        String key = getKey(request);
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityMangerPool.put(key, entityManager);
        String[] params = { key, Integer.toString(entityMangerPool.size()) };
        logger.log(Level.INFO,  "Created entity manager for request {0} - current pool size is {1}", params);
        return entityManager;
    }

    private void closeAndEvictEntityManager(HttpRequestContext request) {
        String key = getKey(request);
        if (entityMangerPool.containsKey(key)) {
            EntityManager entityManager = entityMangerPool.get(key);
            if (entityManager != null && entityManager.isOpen()) entityManager.close();
            entityMangerPool.remove(key);
            String[] params = { key, Integer.toString(entityMangerPool.size()) };
            logger.log(Level.INFO,  "Closed entity manager for request {0} - current pool size is {1}", params);
        }
    }
}
