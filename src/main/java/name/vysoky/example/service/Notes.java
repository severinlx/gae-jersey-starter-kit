package name.vysoky.example.service;

import name.vysoky.example.domain.Note;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
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
@Path("/notes")
public class Notes {

    private static final Logger logger = Logger.getLogger(Notes.class.getName());

    @Context
    EntityManagerFactory entityManagerFactory;

//    Functional only on EJB
//    @PersistenceContext(unitName = "transactions-optional", type = PersistenceContextType.TRANSACTION)
//    EntityManager entityManager;

    @GET
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    @Path("/{id}")
    public Note retrieve(@PathParam("id") Long id) {
        Note greeting = null;
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.find(Note.class, id);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unable to retrieve greeting!", e);
        } finally {
            if (entityManager != null && entityManager.isOpen()) entityManager.close();
        }
        return greeting;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("unchecked")
    public List<Note> list() {
        String hql = "SELECT n FROM Note AS n";
        logger.log(Level.FINEST, hql);
        EntityManager entityManager = null;
        List<Note> greetings = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            Query query = entityManager.createQuery(hql);
            greetings = (List<Note>) query.getResultList();
            greetings.size();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unable to list greetings!", e);
        } finally {
            if (entityManager != null && entityManager.isOpen()) entityManager.close();
        }
        return greetings;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Note create(Note greeting) {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.persist(greeting);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unable to create greeting!", e);
        } finally {
            if (entityManager != null && entityManager.isOpen()) entityManager.close();
        }
        return greeting;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Note update(Note greeting) {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.persist(greeting);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unable to replace greeting!", e);
        } finally {
            if (entityManager != null && entityManager.isOpen()) entityManager.close();
        }
        return greeting;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public void delete(Note greeting) {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.remove(greeting);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unable to delete greeting!", e);
        } finally {
            if (entityManager != null && entityManager.isOpen()) entityManager.close();
        }
    }
}
