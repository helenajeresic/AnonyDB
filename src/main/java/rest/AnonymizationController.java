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

    /**
     * Metoda koja primjenjuje hashiranje na primarni ključ u određenoj tablici.
     * @param tableName Naziv tablice u kojoj se hashira primarni ključ.
     * @param primaryKeyColumn Naziv stupca koji predstavlja primarni ključ.
     * @return HTTP odgovor s porukom o uspješnosti ili grešci.
     */
    @POST
    @Path("/hash/{tableName}/{primaryKeyColumn}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response hashPrimaryKey(@PathParam("tableName") String tableName,
                                   @PathParam("primaryKeyColumn") String primaryKeyColumn) {
        try {
            anonymizationService.hashPrimaryKey(tableName, primaryKeyColumn);
            return Response.ok("Hashing completed for table: " + tableName).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error during hashing: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Metoda koja primjenjuje supresiju na određeni stupac u tablici.
     * @param tableName Naziv tablice u kojoj se primjenjuje supresija.
     * @param columnName Naziv stupca na kojem se vrši supresija.
     * @return HTTP odgovor s porukom o uspješnosti ili grešci.
     */
    @POST
    @Path("/suppression/{tableName}/{columnName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response applySuppression(@PathParam("tableName") String tableName,
                                     @PathParam("columnName") String columnName) {
        try {
            anonymizationService.suppressColumn(tableName, columnName);
            return Response.ok("Suppression completed for column: " + columnName).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error applying suppression: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Metoda koja primjenjuje dodavanje šuma na određeni stupac u tablici..
     * @param tableName Naziv tablice u kojoj se dodaje šum.
     * @param columnName Naziv stupca na koji se dodaje šum.
     * @param noiseParameter Parametar koji određuje raspon šuma.
     * @return HTTP odgovor s porukom o uspješnosti ili grešci.
     */
    @POST
    @Path("/noise/{tableName}/{columnName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response applyNoise(@PathParam("tableName") String tableName,
                               @PathParam("columnName") String columnName,
                               @QueryParam("param") String noiseParameter) {
        try {
            anonymizationService.applyNoise(tableName, columnName, noiseParameter);
            return Response.ok("Noise applied to column: " + columnName).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error applying noise: " + e.getMessage())
                    .build();
        }
    }
}
