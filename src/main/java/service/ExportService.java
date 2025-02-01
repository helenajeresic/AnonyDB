package service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.*;
import java.nio.charset.StandardCharsets;
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
     * Metoda koja izvozi sve podatke iz baze u CSV formatu i sprema ih u ZIP datoteku.
     * Svaka tablica se sprema kao zasebna CSV datoteka unutar ZIP arhive.
     *
     * @return Putanja do generirane ZIP datoteke.
     * @throws IOException Ako dođe do greške pri pisanju datoteke.
     * @throws SQLException Ako dođe do greške pri dohvaćanju podataka iz baze.
     */
    public Path exportAllData() throws IOException, SQLException {
        Path zipFilePath = Paths.get("AnonyDB.zip");

        try (FileOutputStream fos = new FileOutputStream(zipFilePath.toFile());
             ZipOutputStream zipOut = new ZipOutputStream(fos, StandardCharsets.UTF_8)) {

            for (String tableName : tablesService.getTables()) {
                List<Map<String, Object>> tableData = tablesService.getTableData(tableName);

                if (tableData == null || tableData.isEmpty()) {
                    continue;
                }

                ZipEntry zipEntry = new ZipEntry(tableName + ".csv");
                zipOut.putNextEntry(zipEntry);

                Map<String, Object> firstRow = tableData.getFirst();
                String headers = String.join(";", firstRow.keySet());
                zipOut.write(headers.getBytes(StandardCharsets.UTF_8));
                zipOut.write("\n".getBytes());

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
                    zipOut.write(rowData.toString().getBytes(StandardCharsets.UTF_8));
                    zipOut.write("\n".getBytes());
                }

                zipOut.closeEntry();
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

        return zipFilePath;
    }
}
