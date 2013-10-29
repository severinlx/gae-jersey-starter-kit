package name.vysoky.example.context;

import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.ws.rs.core.Context;

/**
 * Provides JPA entity manager factory.
 *
 * @author Jiri Vysoky
 */
public class EntityManagerProvider extends SingletonTypeInjectableProvider<Context, EntityManager> {

    /**
     * Persistence unit name defined in persistence.xml file which is stored in META-INF directory.
     */
    public static final String PERSISTENCE_UNIT_NAME ="transactions-optional";

    public EntityManagerProvider() {
        super(EntityManager.class, Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager());
    }
}