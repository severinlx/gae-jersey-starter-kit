package name.vysoky.example.resources;

import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileReadChannel;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileWriteChannel;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;
import name.vysoky.example.domain.File;
import org.apache.commons.io.IOUtils;

import javax.persistence.*;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.nio.channels.Channels;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/files")
public class Files {

    private static final Logger logger = Logger.getLogger(Files.class.getName());

    @PersistenceUnit(unitName = "transactions-optional")
    private EntityManagerFactory entityManagerFactory;

    @Context
    FileService fileService;

//    @Context
//    UriInfo uriInfo;
//
//    @Context
//    HttpHeaders headers;
//
//    @Context
//    HttpServletResponse response;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("unchecked")
    public List<File> list() {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            String jpql = "SELECT r FROM File AS r";
            logger.log(Level.FINEST, jpql);
            Query query = entityManager.createQuery(jpql);
            return query.getResultList();
        } finally {
            if (entityManager != null && entityManager.isOpen()) entityManager.close();
        }
    }

    /**
     * On Google MainView Engine - add file jersey-multipart-config.properties and bufferThreshold = -1 parameter
     * @param inputStream uploaded input stream
     * @return result
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public File create(
            @FormDataParam("file") InputStream inputStream,
            @FormDataParam("file") FormDataBodyPart body) {
        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;
        try {
            File file = new File();
            file.setName(body.getContentDisposition().getFileName());
            file.setType(body.getMediaType().toString());
            entityManager = entityManagerFactory.createEntityManager();
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            write(file, inputStream);
            entityManager.persist(file);
            entityTransaction.commit();
            String output = "File uploaded to: " + file.getPath();
            logger.log(Level.INFO, output);
            return file;
        } catch (IOException e) {
            throw new RuntimeException("Unable to store BLOB!", e);
        } finally {
            if (entityTransaction != null && entityTransaction.isActive()) entityTransaction.rollback();
            if (entityManager != null && entityManager.isOpen()) entityManager.close();
        }
    }

    @GET
    @Path("/{id}")
    public Response retrieve(@PathParam("id") Long id) {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            final File file = entityManager.find(File.class, id);
            if (file == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
            logger.log(Level.INFO, "Retrieved {0}", file);
            FileStreamingOutput stream = new FileStreamingOutput(file);
            return Response.ok(stream, file.getType()).build();
        } finally {
            if (entityManager != null && entityManager.isOpen()) entityManager.close();
        }
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public void delete(File file) {
        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            file = entityManager.find(File.class, file.getId()); // reload
            entityManager.remove(file);
            drop(file);
            entityTransaction.commit();
        } catch (IOException e) {
            throw new RuntimeException("Unable to drop BLOB!", e);
        } finally {
            if (entityTransaction != null && entityTransaction.isActive()) entityTransaction.rollback();
            if (entityManager != null && entityManager.isOpen()) entityManager.close();
        }
    }

    void write(File file, InputStream inputStream) throws IOException {
        logger.log(Level.INFO, "Writing file: " + file);
        FileWriteChannel writeChannel = null;
        OutputStream outputStream = null;
        try {
            AppEngineFile appEngineFile = fileService.createNewBlobFile(file.getType(), file.getName());
            writeChannel = fileService.openWriteChannel(appEngineFile, true);
            outputStream = new BufferedOutputStream(Channels.newOutputStream(writeChannel));
            int bytes = IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
            writeChannel.closeFinally();
            // add missing values to file
            file.setPath(appEngineFile.getFullPath());
            file.setSize((long) bytes);
        } finally {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            if (writeChannel != null && writeChannel.isOpen()) writeChannel.closeFinally();
        }
    }

    void read(File file, OutputStream outputStream) throws IOException {
        logger.log(Level.INFO, "Reading file: " + file);
        FileReadChannel readChannel = null;
        InputStream inputStream = null;
        try {
            AppEngineFile appEngineFile = new AppEngineFile(file.getPath());
            readChannel = fileService.openReadChannel(appEngineFile, false);
            inputStream = new BufferedInputStream(Channels.newInputStream(readChannel));
            IOUtils.copy(inputStream, outputStream);
        } finally {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            if (readChannel != null && readChannel.isOpen()) readChannel.close();
        }
    }

    void drop(File file) throws IOException {
        logger.log(Level.INFO, "Deleting file: " + file);
        AppEngineFile appEngineFile = new AppEngineFile(file.getPath());
        fileService.delete(appEngineFile);
    }

    private class FileStreamingOutput implements StreamingOutput {
        private File file;

        public FileStreamingOutput(File file) {
            this.file = file;
        }
        @Override
        public void write(OutputStream output) throws IOException, WebApplicationException {
            read(file, output);
        }
    }
}
