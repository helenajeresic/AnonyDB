package rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import service.AnonymizationService;

@Path("/anonymization")
public class AnonymizationController {

    @Inject
    AnonymizationService anonymizationService;

    @POST
    @Path("/hash/{tableName}/{primaryKeyColumn}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response hashPrimaryKey(@PathParam("tableName") String tableName,
                                   @PathParam("primaryKeyColumn") String primaryKeyColumn) {
        try {
            anonymizationService.hashPrimaryKey(tableName, primaryKeyColumn);
            return Response.ok("Hashiranje završeno za tablicu: " + tableName).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Greška prilikom hashiranja: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/suppression/{tableName}/{columnName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response applySuppression(@PathParam("tableName") String tableName,
                                     @PathParam("columnName") String columnName) {
        try {
            anonymizationService.suppressColumn(tableName, columnName);
            return Response.ok("Supresija završena za stupac: " + columnName).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Greška prilikom primjene supresije: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/noise/{tableName}/{columnName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response applyNoise(@PathParam("tableName") String tableName,
                               @PathParam("columnName") String columnName,
                               @QueryParam("param") String noiseParameter) {
        try {
            anonymizationService.applyNoise(tableName, columnName, noiseParameter);
            return Response.ok("Šum primijenjen na stupac: " + columnName).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Greška prilikom primjene šuma: " + e.getMessage())
                    .build();
        }
    }
}
