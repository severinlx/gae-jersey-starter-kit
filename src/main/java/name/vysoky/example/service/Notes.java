package name.vysoky.example.service;

import name.vysoky.example.domain.Note;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
    EntityManager entityManager;

//
//    @PersistenceContext(unitName = "transactions-optional", type = PersistenceContextType.TRANSACTION)
//    EntityManager entityManager;

//    @GET
//    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
//    public Note retrieve(@PathParam("id") Long id) {
//        Note greeting = null;
//        try {
//            return entityManager.find(Note.class, id);
//        } catch (Exception e) {
//            logger.log(Level.WARNING, "Unable to retrieve greeting!", e);
//        }
//        return greeting;
//    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("unchecked")
    public List<Note> list() {
        String hql = "SELECT n FROM Note AS n";
        logger.log(Level.FINEST, hql);
        Query query = entityManager.createQuery(hql);
        List<Note> greetings = null;
        try {
            greetings = (List<Note>) query.getResultList();
            greetings.size();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unable to list greetings!", e);
        }
        return greetings;
    }

//    @POST
//    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
//    public Note create(Note greeting) {
//        try {
//            entityManager.persist(greeting);
//        } catch (Exception e) {
//            logger.log(Level.WARNING, "Unable to create greeting!", e);
//        }
//        return greeting;
//    }
//
//    @PUT
//    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
//    public Note update(Note greeting) {
//        try {
//            entityManager.persist(greeting);
//        } catch (Exception e) {
//            logger.log(Level.WARNING, "Unable to replace greeting!", e);
//        }
//        return greeting;
//    }
//
//    @DELETE
//    @Produces(MediaType.APPLICATION_JSON)
//    public void delete(Note greeting) {
//        try {
//            entityManager.remove(greeting);
//        } catch (Exception e) {
//            logger.log(Level.WARNING, "Unable to delete greeting!", e);
//        }
//    }
}
