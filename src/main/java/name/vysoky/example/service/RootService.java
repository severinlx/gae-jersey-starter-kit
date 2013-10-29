package name.vysoky.example.service;

import com.sun.jersey.api.view.Viewable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Jiri Vysoky
 */
@Path("/")
public class RootService {

    // This method is called if HTML is request
    @GET
    @Produces(MediaType.APPLICATION_XHTML_XML)
    public Response indexHTML() {
        Viewable view = new Viewable("/index", null);
        return Response.ok(view).build();
    }
}
