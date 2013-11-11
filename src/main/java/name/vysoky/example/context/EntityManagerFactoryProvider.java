package name.vysoky.example.context;

import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

/**
 * Entity manager factory provider.
 *
 * @author Jiri Vysoky
 */
public class EntityManagerFactoryProvider extends SingletonTypeInjectableProvider<PersistenceUnit, EntityManagerFactory> {

    public EntityManagerFactoryProvider() {
        super(EntityManagerFactory.class, Persistence.createEntityManagerFactory("transactions-optional"));
    }

}
