package name.vysoky.example.context;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Injectable JPA entity manager.
 *
 * @author Jiri Vysoky
 */
public class InjectableEntityManager extends AbstractHttpContextInjectable<EntityManager> {

    private static final Logger logger = Logger.getLogger(InjectableEntityManager.class.getName());

    /**
     * Persistence unit name defined in persistence.xml file which is stored in META-INF directory.
     */
    public static final String PERSISTENCE_UNIT_NAME ="transactions-optional";

    private static EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;


    public InjectableEntityManager() {
        if (entityManagerFactory == null) {
            logger.log(Level.INFO, "Creating new entity manager factory for persistence unit {0}", PERSISTENCE_UNIT_NAME);
            entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        }
        entityManager = entityManagerFactory.createEntityManager();
        logger.log(Level.INFO, "Created new entity manager {0}", entityManager.toString());
    }

    @Override
    public EntityManager getValue(HttpContext c) {
        logger.log(Level.INFO, "Returning injectable entity manager as value.");
        return entityManager;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (entityManager.isOpen()) {
            logger.log(Level.INFO, "Closing entity manager {0}.", entityManager.toString());
            entityManager.close();
        }
    }
}
