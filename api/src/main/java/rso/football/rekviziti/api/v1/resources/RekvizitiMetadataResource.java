package rso.football.rekviziti.api.v1.resources;

import com.kumuluz.ee.cors.annotations.CrossOrigin;
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

    @GET
    public Response getImageMetadata() {

        List<RekvizitiMetadata> rekvizitiMetadata = rekvizitiMetadataBean.getRekvizitiMetadataFilter(uriInfo);

        return Response.status(Response.Status.OK).entity(rekvizitiMetadata).build();
    }

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

    @GET
    @Path("/{rekvizitiMetadataId}")
    public Response getImageMetadata(@PathParam("rekvizitiMetadataId") Integer rekvizitiMetadataId) {

        RekvizitiMetadata rekvizitiMetadata = rekvizitiMetadataBean.getRekvizitiMetadata(rekvizitiMetadataId);

        if (rekvizitiMetadata == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(rekvizitiMetadata).build();
    }

    @POST
    public Response createRekvizitiMetadata(RekvizitiMetadata rekvizitiMetadata) {

        if ((rekvizitiMetadata.getType() == null || rekvizitiMetadata.getCost() == null)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        else {
            rekvizitiMetadata = rekvizitiMetadataBean.createRekvizitiMetadata(rekvizitiMetadata);
        }

        return Response.status(Response.Status.CONFLICT).entity(rekvizitiMetadata).build();

    }

    @PUT
    @Path("{rekvizitiMetadataId}")
    public Response putImageMetadata(@PathParam("rekvizitiMetadataId") Integer rekvizitiMetadataId,
                                     RekvizitiMetadata rekvizitiMetadata) {

        rekvizitiMetadata = rekvizitiMetadataBean.putRekvizitiMetadata(rekvizitiMetadataId, rekvizitiMetadata);

        if (rekvizitiMetadata == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.NOT_MODIFIED).build();

    }

    @DELETE
    @Path("{rekvizitiMetadataId}")
    public Response deleteRekvizitiMetadata(@PathParam("rekvizitiMetadataId") Integer rekvizitiMetadataId) {

        boolean deleted = rekvizitiMetadataBean.deleteRekvizitiMetadata(rekvizitiMetadataId);

        if (deleted) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
