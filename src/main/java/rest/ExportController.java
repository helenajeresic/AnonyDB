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
        System.out.println("Exporting all data...");

        try {
            java.nio.file.Path exportDir = exportService.exportAllData();

            if (Files.exists(exportDir)) {
                String jsonResponse = "{\"message\": \"Podaci su uspješno izvezeni u direktorij: " + exportDir + "\"}";
                return Response.ok(jsonResponse, "application/json").build();
            } else {
                String jsonResponse = "{\"message\": \"Došlo je do pogreške prilikom izvoza podataka.\"}";
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(jsonResponse)
                        .type("application/json")
                        .build();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            String jsonResponse = "{\"message\": \"Greška prilikom izvoza podataka: " + e.getMessage() + "\"}";
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(jsonResponse)
                    .type("application/json")
                    .build();
        }
    }
}
