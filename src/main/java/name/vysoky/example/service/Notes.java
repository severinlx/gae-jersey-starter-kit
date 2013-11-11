package name.vysoky.example.service;

import name.vysoky.example.domain.Note;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.logging.Logger;

/**
 * Greetings REST service.
 *
 * @author Jiri Vysoky
 */
@Path("/notes")
public class Notes {

    private static final Logger logger = Logger.getLogger(Notes.class.getName());

    @PersistenceUnit(unitName = "transactions-optional")
    private EntityManagerFactory entityManagerFactory;

    @GET
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    @Path("/{id}")
    public Note retrieve(@PathParam("id") Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return entityManager.find(Note.class, id);
        } finally {
            entityManager.close();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("unchecked")
    public List<Note> list() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            Query query = entityManager.createQuery("SELECT n FROM Note AS n");
            return (List<Note>) query.getResultList();
        } finally {
            entityManager.close();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Note create(Note greeting) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.persist(greeting);
            return greeting;
        } finally {
            entityManager.close();
        }
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Note update(Note greeting) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.persist(greeting);
            return greeting;
        } finally {
            entityManager.close();
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public void delete(Note greeting) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.remove(greeting);
        } finally {
            entityManager.close();
        }
    }
}
