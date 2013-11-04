package name.vysoky.example.context;

import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;

import javax.ws.rs.core.Context;


/**
 * Google App Engine File service provider.
 *
 * @author Jiri Vysoky
 */
public class FileServiceProvider extends SingletonTypeInjectableProvider<Context, FileService> {

    public FileServiceProvider() {
        super(FileService.class, FileServiceFactory.getFileService());
    }
}
