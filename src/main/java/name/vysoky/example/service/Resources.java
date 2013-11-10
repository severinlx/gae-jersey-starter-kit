package name.vysoky.example.service;

import com.google.appengine.api.files.*;
import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import name.vysoky.example.domain.Resource;
import org.apache.commons.io.IOUtils;

import javax.persistence.*;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.*;
import java.nio.channels.Channels;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/resources")
public class Resources {

    private static final Logger logger = Logger.getLogger(Resources.class.getName());

    @PersistenceUnit(unitName = "transactions-optional")
    private EntityManagerFactory entityManagerFactory;

    @Context
    FileService fileService;

    @Context
    UriInfo uriInfo;

    @Context
    HttpHeaders headers;

    @Context
    HttpServletResponse response;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("unchecked")
    public List<Resource> list() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            String jpql = "SELECT r FROM Resource AS r";
            logger.log(Level.FINEST, jpql);
            Query query = entityManager.createQuery(jpql);
            return query.getResultList();
        } finally {
            entityManager.close();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_XHTML_XML)
    public Response listHTML() {
        Viewable view = new Viewable("/resources/list", list());
        return Response.ok(view).build();
    }

    /**
     * On Google App Engine - add file jersey-multipart-config.properties and bufferThreshold = -1 parameter
     * @param uploadedInputStream uploaded input stream
     * @param fileDetail file detail
     * @return result
     */
    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    //@Produces(MediaType.APPLICATION_XHTML_XML)
    public Response createHTML(
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {
        try {
            Resource resource = new Resource(fileDetail.getName(), fileDetail.getSize(), fileDetail.getType());
            write(resource, uploadedInputStream);
            String output = "File uploaded to: " + resource.getPath();
            logger.log(Level.INFO, output);
            return Response.status(200).entity(output).build();
        } catch (Exception e) {
            String output = "Unable to upload file!";
            logger.log(Level.SEVERE, output, e);
            return Response.status(200).entity(output).build();
        }
    }

    protected void write(Resource resource, InputStream inputStream) throws IOException {
        logger.log(Level.INFO, "Writing resource: " + resource);
        FileWriteChannel writeChannel = null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            // Create a new Blob file with mime-type
            AppEngineFile file = fileService.createNewBlobFile(resource.getType());
            writeChannel = fileService.openWriteChannel(file, true);
            // Different standard Java ways of writing to the channel are possible. Here we use a PrintWriter:
            PrintWriter out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
            InputStreamReader in = new InputStreamReader(inputStream);
            // Now finalize
            IOUtils.copy(in, out);
            out.close();
            writeChannel.closeFinally();
            // Set path to resource
            resource.setPath(file.getFullPath());
            entityManager.persist(resource);
        } finally {
            entityManager.close();
            if (writeChannel != null && writeChannel.isOpen()) writeChannel.close();
            if (inputStream != null) inputStream.close();
        }
    }

    protected void read(Resource resource, OutputStream outputStream) throws IOException {
        logger.log(Level.INFO, "Reading resource: " + resource);
        FileReadChannel readChannel = null;
        try {
            //AppEngineFile file = fileService.getBlobFile(new BlobKey(resource.getPath()));
            AppEngineFile file = new AppEngineFile(resource.getPath());
            // Lock = false et other people read at the same time
            readChannel = fileService.openReadChannel(file, false);
            // Again, different standard Java ways of reading from the channel.
            BufferedReader reader = new BufferedReader(Channels.newReader(readChannel, "UTF8"));
            IOUtils.copy(reader, outputStream);
//            readChannel.close();
//            outputStream.close();
        }  finally {
            if (readChannel != null && readChannel.isOpen()) readChannel.close();
            if (outputStream != null) outputStream.close();
        }
    }
}
