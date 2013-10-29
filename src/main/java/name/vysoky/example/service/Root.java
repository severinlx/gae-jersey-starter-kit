package name.vysoky.example.service;

import com.sun.jersey.api.view.Viewable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * HTML root handler.
 *
 * @author Jiri Vysoky
 */
@Path("/")
public class Root {

    private static final Logger logger = Logger.getLogger(Root.class.getName());

    // This method is called if HTML is request
    @GET
    @Produces(MediaType.APPLICATION_XHTML_XML)
    public Response indexHTML() {
        logger.log(Level.INFO, "Accessing HTML root.");
        Viewable view = new Viewable("/index", null);
        return Response.ok(view).build();
    }
}
