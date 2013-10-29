package name.vysoky.example.context;

import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.PerRequestTypeInjectableProvider;
import org.codehaus.jackson.type.TypeReference;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Context;

/**
 * Provides JPA entity manager factory.
 *
 * @author Jiri Vysoky
 */
public class EntityManagerProvider extends PerRequestTypeInjectableProvider<Context, EntityManager> {

    public EntityManagerProvider() {
        super(new TypeReference<EntityManager>() {}.getType());
    }

    @Override
    public Injectable<EntityManager> getInjectable(ComponentContext ic, Context context) {
        return new InjectableEntityManager();
    }
}