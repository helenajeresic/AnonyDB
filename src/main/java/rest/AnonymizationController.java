package rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import service.AnonymizationService;
import service.TablesService;

import java.util.List;
import java.util.Map;

@Path("/anonymization")
public class AnonymizationController {

    @Inject
    AnonymizationService anonymizationService;

    @Inject
    TablesService tablesService;

    @POST
    @Path("/hash/{tableName}/{primaryKeyColumn}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response hashPrimaryKey(@PathParam("tableName") String tableName,
                                   @PathParam("primaryKeyColumn") String primaryKeyColumn) {
        try {
            List<Map<String, Object>> tableData = tablesService.getTableData(tableName);
            anonymizationService.hashPrimaryKey(tableName, primaryKeyColumn, tableData);
            return Response.ok("Hashiranje završeno za tablicu: " + tableName).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Greška prilikom hashiranja: " + e.getMessage())
                    .build();
        }
    }
}
