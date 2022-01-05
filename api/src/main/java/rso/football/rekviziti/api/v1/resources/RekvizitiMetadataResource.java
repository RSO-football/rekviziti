package rso.football.rekviziti.api.v1.resources;

import com.kumuluz.ee.cors.annotations.CrossOrigin;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import rso.football.rekviziti.lib.RekvizitiMetadata;
import rso.football.rekviziti.services.beans.RekvizitiMetadataBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;


@ApplicationScoped
@Path("/rekviziti")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@CrossOrigin(supportedMethods = "GET, POST, DELETE, PUT, HEAD, OPTIONS")
public class RekvizitiMetadataResource {

    private Logger log = Logger.getLogger(RekvizitiMetadataResource.class.getName());

    @Inject
    private RekvizitiMetadataBean rekvizitiMetadataBean;

    @Context
    protected UriInfo uriInfo;

    @Operation(description = "Get all rekviziti metadata.", summary = "Get all metadata")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of rekviziti metadata",
                    content = @Content(schema = @Schema(implementation = RekvizitiMetadata.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of objects in list")}
            )})
    @GET
    public Response getImageMetadata() {

        List<RekvizitiMetadata> rekvizitiMetadata = rekvizitiMetadataBean.getRekvizitiMetadataFilter(uriInfo);

        return Response.status(Response.Status.OK).entity(rekvizitiMetadata).build();
    }

    @Operation(description = "Get cena rekvizitov for trener.", summary = "Get cena for trener")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "Cena integer",
                    content = @Content(
                            schema = @Schema(implementation = Integer.class))
            )})
    @GET
    @Path("/cena/{trenerMetadataId}")
    public Response getCenaTrener(@PathParam("trenerMetadataId") Integer trenerMetadataId) {

        List<RekvizitiMetadata> rekvizitiMetadata = rekvizitiMetadataBean.getRekvizitiTrenerjaMetadata(trenerMetadataId);
        Integer skupnaCena = 0;
        for (RekvizitiMetadata r : rekvizitiMetadata){
            skupnaCena += r.getCost();
        }
        return Response.status(Response.Status.OK).entity(skupnaCena).build();
    }

    @Operation(description = "Get metadata for one rekvizit.", summary = "Get metadata for one rekvizit")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "Igrisce metadata",
                    content = @Content(
                            schema = @Schema(implementation = RekvizitiMetadata.class))
            )})
    @GET
    @Path("/{rekvizitiMetadataId}")
    public Response getImageMetadata(@Parameter(description = "Metadata ID.", required = true)
                                         @PathParam("rekvizitiMetadataId") Integer rekvizitiMetadataId) {

        RekvizitiMetadata rekvizitiMetadata = rekvizitiMetadataBean.getRekvizitiMetadata(rekvizitiMetadataId);

        if (rekvizitiMetadata == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(rekvizitiMetadata).build();
    }

    @Operation(description = "Add rekvizit metadata.", summary = "Add metadata")
    @APIResponses({
            @APIResponse(responseCode = "201",
                    description = "Metadata successfully added."
            ),
            @APIResponse(responseCode = "400", description = "Bad request.")
    })
    @POST
    public Response createRekvizitiMetadata(@RequestBody(
            description = "DTO object with rekviziti metadata.",
            required = true, content = @Content(
            schema = @Schema(implementation = RekvizitiMetadata.class))) RekvizitiMetadata rekvizitiMetadata) {

        if ((rekvizitiMetadata.getType() == null || rekvizitiMetadata.getCost() == null)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        else {
            rekvizitiMetadata = rekvizitiMetadataBean.createRekvizitiMetadata(rekvizitiMetadata);
            if (rekvizitiMetadata == null){
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }

        return Response.status(Response.Status.CREATED).entity(rekvizitiMetadata).build();

    }

    @Operation(description = "Update metadata for on rekvizit.", summary = "Update metadata")
    @APIResponses({
            @APIResponse(
                    responseCode = "204",
                    description = "Metadata successfully updated."
            ),
            @APIResponse(responseCode = "404", description = "Not found.")
    })
    @PUT
    @Path("{rekvizitiMetadataId}")
    public Response putImageMetadata(@Parameter(description = "Metadata ID.", required = true)
                                         @PathParam("rekvizitiMetadataId") Integer rekvizitiMetadataId,
                                     @RequestBody(
                                             description = "DTO object with rekvizit metadata.",
                                             required = true, content = @Content(
                                             schema = @Schema(implementation = RekvizitiMetadata.class)))
                                             RekvizitiMetadata rekvizitiMetadata) {

        rekvizitiMetadata = rekvizitiMetadataBean.putRekvizitiMetadata(rekvizitiMetadataId, rekvizitiMetadata);

        if (rekvizitiMetadata == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.NO_CONTENT).build();

    }

    @Operation(description = "Delete metadata for one rekvizit.", summary = "Delete metadata")
    @APIResponses({
            @APIResponse(
                    responseCode = "204",
                    description = "Metadata successfully deleted."
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Not found."
            )
    })
    @DELETE
    @Path("{rekvizitiMetadataId}")
    public Response deleteRekvizitiMetadata(@Parameter(description = "Metadata ID.", required = true)
                                                @PathParam("rekvizitiMetadataId") Integer rekvizitiMetadataId) {

        boolean deleted = rekvizitiMetadataBean.deleteRekvizitiMetadata(rekvizitiMetadataId);

        if (deleted) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
