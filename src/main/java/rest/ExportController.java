package rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import service.ExportService;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;

@Path("/export")
public class ExportController {

    @Inject
    ExportService exportService;

    @POST
    @Path("/all")
    @Produces("application/json")
    public Response exportAllData() {
        try {
            java.nio.file.Path exportDir = exportService.exportAllData();

            if (Files.exists(exportDir)) {
                String jsonResponse = "{\"message\": \"Data successfully exported to directory: " + exportDir + "\"}";
                return Response.ok(jsonResponse, "application/json").build();
            } else {
                String jsonResponse = "{\"message\": \"An error occurred during data export.\"}";
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(jsonResponse)
                        .type("application/json")
                        .build();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            String jsonResponse = "{\"message\": \"Error during data export: " + e.getMessage() + "\"}";
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(jsonResponse)
                    .type("application/json")
                    .build();
        }
    }
}

