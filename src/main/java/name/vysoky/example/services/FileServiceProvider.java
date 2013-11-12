package name.vysoky.example.services;

import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;

import javax.ws.rs.core.Context;


/**
 * Google MainView Engine File resources provider.
 *
 * @author Jiri Vysoky
 */
public class FileServiceProvider extends SingletonTypeInjectableProvider<Context, FileService> {

    public FileServiceProvider() {
        super(FileService.class, FileServiceFactory.getFileService());
    }
}
