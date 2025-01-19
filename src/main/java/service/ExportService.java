package service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.*;
import java.nio.file.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@ApplicationScoped
public class ExportService {

    @Inject
    TablesService tablesService;

    /**
     * Izvoz svih anonimiziranih tablica u CSV format unutar ZIP arhive
     */
    public Path exportAllData() throws IOException, SQLException {
        Path zipFilePath = Paths.get("anonymized database.zip");

        try (FileOutputStream fos = new FileOutputStream(zipFilePath.toFile());
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {

            for (String tableName : tablesService.getTables()) {
                List<Map<String, Object>> tableData = tablesService.getTableData(tableName);

                ZipEntry zipEntry = new ZipEntry(tableName + ".csv");
                zipOut.putNextEntry(zipEntry);

                if (!tableData.isEmpty()) {
                    Map<String, Object> firstRow = tableData.getFirst();
                    String headers = String.join(";", firstRow.keySet());
                    zipOut.write(headers.getBytes());
                    zipOut.write("\n".getBytes());
                }

                for (Map<String, Object> row : tableData) {
                    StringBuilder rowData = new StringBuilder();
                    for (Object value : row.values()) {
                        if (value != null) {
                            String valueStr = value.toString().replace(";", "\\;").replace("\n", " ").replace("\r", " ");
                            rowData.append(valueStr).append(";");
                        } else {
                            rowData.append(";");
                        }
                    }
                    if (!rowData.isEmpty()) {
                        rowData.deleteCharAt(rowData.length() - 1);
                    }

                    zipOut.write(rowData.toString().getBytes());
                    zipOut.write("\n".getBytes());
                }

                zipOut.closeEntry();
            }
        }

        return zipFilePath;
    }
}
