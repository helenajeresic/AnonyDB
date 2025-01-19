package service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.*;
import java.nio.file.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ExportService {

    @Inject
    TablesService tablesService;

    /**
     * Izvoz svih anonimiziranih tablica u CSV format u export direktorij
     */
    public Path exportAllData() throws IOException, SQLException {
        Path exportDir = Paths.get("export");
        if (!Files.exists(exportDir)) {
            Files.createDirectory(exportDir);
        }

        for (String tableName : tablesService.getTables()) {
            List<Map<String, Object>> tableData = tablesService.getTableData(tableName);

            File csvFile = new File(exportDir.toFile(), tableName + ".csv");

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
                if (!tableData.isEmpty()) {
                    Map<String, Object> firstRow = tableData.getFirst();
                    String headers = String.join(";", firstRow.keySet());
                    writer.write(headers);
                    writer.newLine();
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

                    writer.write(rowData.toString());
                    writer.newLine();
                }
            }
        }

        return exportDir;
    }
}
