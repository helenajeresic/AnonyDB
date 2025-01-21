package rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import service.TablesService;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Path("/tables")
public class TablesController {

    @Inject
    TablesService tablesService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTables() {
        try {
            List<String> tables = tablesService.getTables();
            return Response.ok(tables).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error fetching tables: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{tableName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTableData(@PathParam("tableName") String tableName) {
        try {
            List<Map<String, Object>> data = tablesService.getTableData(tableName);
            return Response.ok(data).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error fetching data for table: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/metadata/{tableName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTableMetadata(@PathParam("tableName") String tableName) {
        try {
            List<Map<String, Object>> metadata = tablesService.getTableMetadata(tableName);
            return Response.ok(metadata).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error fetching metadata: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/reset")
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetAllChanges() {
        tablesService.resetAllChanges();
        return Response.ok().build();
    }
}
