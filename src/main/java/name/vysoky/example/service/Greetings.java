package name.vysoky.example.service;

import name.vysoky.example.domain.Greeting;

import javax.persistence.*;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Greetings REST service.
 *
 * @author Jiri Vysoky
 */
public class Greetings {

    private static final Logger logger = Logger.getLogger(Greetings.class.getName());

    //@Context
    //EntityManagerFactory entityManagerFactory;

    @PersistenceContext(unitName = "transactions-optional", type = PersistenceContextType.TRANSACTION)
    EntityManager entityManager;

    @GET
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Greeting retrieve(@PathParam("id") Long id) {
        Greeting greeting = null;
        try {
            return entityManager.find(Greeting.class, id);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unable to retrieve greeting!", e);
        } finally {
            entityManager.close();
        }
        return greeting;
    }

    @GET
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    @SuppressWarnings("unchecked")
    public List<Greeting> list() {
        String hql = "from Greeting g";
        logger.log(Level.FINEST, hql);
        Query query = entityManager.createQuery(hql);
        List<Greeting> greetings = null;
        try {
            greetings = (List<Greeting>) query.getResultList();
            greetings.size();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unable to list greetings!", e);
        }finally {
            entityManager.close();
        }
        return greetings;
    }

    @POST
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Greeting create(Greeting greeting) {
        try {
            entityManager.persist(greeting);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unable to create greeting!", e);
        } finally {
            entityManager.close();
        }
        return greeting;
    }

    @PUT
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Greeting update(Greeting greeting) {
        try {
            entityManager.persist(greeting);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unable to replace greeting!", e);
        } finally {
            entityManager.close();
        }
        return greeting;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public void delete(Greeting greeting) {
        try {
            entityManager.remove(greeting);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unable to delete greeting!", e);
        } finally {
            entityManager.close();
        }
    }
}
