package name.vysoky.example.service;

import name.vysoky.example.domain.Note;

import javax.persistence.*;
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
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            return entityManager.find(Note.class, id);
        } finally {
            if (entityManager != null && entityManager.isOpen()) entityManager.close();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("unchecked")
    public List<Note> list() {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            Query query = entityManager.createQuery("SELECT n FROM Note AS n");
            return (List<Note>) query.getResultList();
        } finally {
            if (entityManager != null && entityManager.isOpen()) entityManager.close();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Note create(Note greeting) {
        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            entityManager.persist(greeting);
            entityTransaction.commit();
            return greeting;
        } finally {
            if (entityTransaction != null  && entityTransaction.isActive()) entityTransaction.rollback();
            if (entityManager != null && entityManager.isOpen()) entityManager.close();
        }
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Note update(Note greeting) {
        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            entityManager.persist(greeting);
            entityTransaction.commit();
            return greeting;
        } finally {
            if (entityTransaction != null  && entityTransaction.isActive()) entityTransaction.rollback();
            if (entityManager != null && entityManager.isOpen()) entityManager.close();
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public void delete(Note greeting) {
        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            entityManager.remove(greeting);
            entityTransaction.commit();
        } finally {
            if (entityTransaction != null  && entityTransaction.isActive()) entityTransaction.rollback();
            if (entityManager != null && entityManager.isOpen()) entityManager.close();
        }
    }
}
