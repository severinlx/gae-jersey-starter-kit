package name.vysoky.example.resources;

import name.vysoky.example.domain.Note;

import javax.persistence.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Greetings REST resources.
 *
 * @author Jiri Vysoky
 */
@Path("/notes")
public class Notes {

    private static final Logger logger = Logger.getLogger(Notes.class.getName());

    @PersistenceUnit(unitName = "transactions-optional")
    private EntityManagerFactory entityManagerFactory;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("unchecked")
    public List<Note> list() {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            Query query = entityManager.createQuery("SELECT n FROM Note AS n");
            List<Note> notes = (List<Note>) query.getResultList();
            logger.log(Level.INFO, "Listed {0}", notes);
            return notes;
        } finally {
            if (entityManager != null && entityManager.isOpen()) entityManager.close();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Note create(Note note) {
        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            entityManager.persist(note);
            entityTransaction.commit();
            logger.log(Level.INFO, "Created {0}", note);
            return note;
        } finally {
            if (entityTransaction != null && entityTransaction.isActive()) entityTransaction.rollback();
            if (entityManager != null && entityManager.isOpen()) entityManager.close();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Note retrieve(@PathParam("id") Long id) {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            Note note = entityManager.find(Note.class, id);
            if (note == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
            logger.log(Level.INFO, "Retrieved {0}", note);
            return note;
        } finally {
            if (entityManager != null && entityManager.isOpen()) entityManager.close();
        }
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Note update(Note note) {
        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            entityManager.persist(note);
            entityTransaction.commit();
            logger.log(Level.INFO, "Persisted {0}", note);
            return note;
        } finally {
            if (entityTransaction != null && entityTransaction.isActive()) entityTransaction.rollback();
            if (entityManager != null && entityManager.isOpen()) entityManager.close();
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public void delete(Note note) {
        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            entityManager.remove(note);
            entityTransaction.commit();
            logger.log(Level.INFO, "Deleted {0}", note);
        } finally {
            if (entityTransaction != null && entityTransaction.isActive()) entityTransaction.rollback();
            if (entityManager != null && entityManager.isOpen()) entityManager.close();
        }
    }
}
