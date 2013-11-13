package name.vysoky.example.resources;

import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileReadChannel;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileWriteChannel;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import name.vysoky.example.domain.File;
import org.apache.commons.io.IOUtils;

import javax.persistence.*;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.nio.channels.Channels;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/files")
public class Files {

    public static final String CHANNEL_ENCODING = "UTF8";

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
     * @param uploadedInputStream uploaded input stream
     * @param fileDetail file detail
     * @return result
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public File create(
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {
        try {
            File file = new File(fileDetail.getName(), fileDetail.getSize(), fileDetail.getType());
            write(file, uploadedInputStream);
            String output = "File uploaded to: " + file.getPath();
            logger.log(Level.INFO, output);
            return file;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unable to upload file!");
            return null;
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
            file = entityManager.find(File.class, file.getId()); // reload
            entityManager.remove(file);
            //TODO: Delete also from blobstore!
            entityTransaction.commit();
        } finally {
            if (entityTransaction != null && entityTransaction.isActive()) entityTransaction.rollback();
            if (entityManager != null && entityManager.isOpen()) entityManager.close();
        }
    }

    void write(File file, InputStream inputStream) throws IOException {
        logger.log(Level.INFO, "Writing resource: " + file);
        FileWriteChannel writeChannel = null;
        EntityManager entityManager = null;
        InputStreamReader inputStreamReader = null;
        PrintWriter printWriter = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            // Create a new Blob file with mime-type
            AppEngineFile appEngineFile = fileService.createNewBlobFile(file.getType());
            writeChannel = fileService.openWriteChannel(appEngineFile, true);
            inputStreamReader = new InputStreamReader(inputStream);
            // Different standard Java ways of writing to the channel are possible. Here we use a PrintWriter:
            printWriter = new PrintWriter(Channels.newWriter(writeChannel, CHANNEL_ENCODING));
            IOUtils.copy(inputStreamReader, printWriter);
            // Set path to resource
            file.setPath(appEngineFile.getFullPath());
            entityManager.persist(file);
        } finally {
            if (entityManager != null && entityManager.isOpen()) entityManager.close();
            if (inputStreamReader != null) inputStreamReader.close();
            if (inputStream != null) inputStream.close();
            if (printWriter != null) printWriter.close();
            if (writeChannel != null && writeChannel.isOpen()) writeChannel.close();
        }
    }

    void read(File file, OutputStream outputStream) throws IOException {
        logger.log(Level.INFO, "Reading resource: " + file);
        FileReadChannel readChannel = null;
        BufferedReader bufferedReader = null;
        try {
            //AppEngineFile file = fileService.getBlobFile(new BlobKey(resource.getPath()));
            AppEngineFile appEngineFile = new AppEngineFile(file.getPath());
            // Lock = false et other people read at the same time
            readChannel = fileService.openReadChannel(appEngineFile, false);
            // Again, different standard Java ways of reading from the channel.
            bufferedReader = new BufferedReader(Channels.newReader(readChannel, CHANNEL_ENCODING));
            IOUtils.copy(bufferedReader, outputStream);
        } finally {
            if (readChannel != null && readChannel.isOpen()) readChannel.close();
            if (bufferedReader != null) bufferedReader.close();
            if (outputStream != null) outputStream.close();
        }
    }
}
