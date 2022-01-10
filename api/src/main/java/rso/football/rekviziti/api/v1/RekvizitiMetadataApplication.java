package rso.football.rekviziti.api.v1;


import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

//TODO: change url to not localhost
@OpenAPIDefinition(info = @Info(title = "Rekviziti API", version = "v1",
        contact = @Contact(email = "rb2600@student.uni-lj.si"),
        license = @License(name = "dev"), description = "API for managing rekviziti metadata."),
        servers = @Server(url = "https://20.88.176.138:8085/"))
@ApplicationPath("/v1")
public class RekvizitiMetadataApplication extends Application {
}
