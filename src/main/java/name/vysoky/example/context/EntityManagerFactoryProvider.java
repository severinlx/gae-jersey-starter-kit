package name.vysoky.example.context;

import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.core.Context;

/**
 * @author Jiri Vysoky
 */
public class EntityManagerFactoryProvider extends SingletonTypeInjectableProvider<Context, EntityManagerFactory> {

    /**
     * Persistence unit name defined in persistence.xml file which is stored in META-INF directory.
     */
    public static final String PERSISTENCE_UNIT_NAME ="transactions-optional";

    public EntityManagerFactoryProvider() {
        super(EntityManagerFactory.class, Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME));
    }
}
