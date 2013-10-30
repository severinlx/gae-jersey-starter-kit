package name.vysoky.example.service;

import name.vysoky.example.domain.Note;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.ws.rs.*;
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

//    @Context
//    EntityManagerFactory entityManagerFactory;

   // Functional only on EJB
    @PersistenceContext(unitName = "transactions-optional", type = PersistenceContextType.TRANSACTION)
    EntityManager entityManager;

    @GET
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    @Path("/{id}")
    public Note retrieve(@PathParam("id") Long id) {
        Note greeting = null;
        try {
            entityManager.find(Note.class, id);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unable to retrieve greeting!", e);
        }
        return greeting;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("unchecked")
    public List<Note> list() {
        List<Note> greetings = null;
        try {
            String jpql = "SELECT n FROM Note AS n";
            logger.log(Level.FINEST, jpql);
            Query query = entityManager.createQuery(jpql);
            greetings = (List<Note>) query.getResultList();
            greetings.size();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unable to list notes!", e);
        }
        return greetings;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Note create(Note greeting) {
        try {
            entityManager.persist(greeting);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unable to create note!", e);
        }
        return greeting;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Note update(Note greeting) {
        try {
            entityManager.persist(greeting);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unable to replace note!", e);
        }
        return greeting;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public void delete(Note greeting) {
        try {
            entityManager.remove(greeting);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unable to delete note!", e);
        }
    }
}
