package service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@ApplicationScoped
public class TablesService {

    @Inject
    DataSource dataSource;

    private final Map<String, List<Map<String, Object>>> modifiedData = new HashMap<>();
    private final Map<String, Map<String, String>> columnAnonymizationState = new HashMap<>();

    public List<String> getTables() throws SQLException {
        List<String> tables = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tablesResultSet = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            while (tablesResultSet.next()) {
                tables.add(tablesResultSet.getString("TABLE_NAME"));
            }
        }
        return tables;
    }

    public List<Map<String, Object>> getTableData(String tableName) throws SQLException {
        if (!modifiedData.containsKey(tableName)) {
            loadTableDataIntoMemory(tableName);
        }
        return modifiedData.get(tableName);
    }

    private void loadTableDataIntoMemory(String tableName) throws SQLException {
        List<Map<String, Object>> data = new ArrayList<>();
        String query = "SELECT * FROM " + tableName;

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), resultSet.getObject(i));
                }
                data.add(row);
            }
        }

        modifiedData.put(tableName, data);
    }

    // Dohvaća metapodatke za određenu tablicu, uključujući primarne i strane ključeve
    public List<Map<String, Object>> getTableMetadata(String tableName) throws SQLException {
        List<Map<String, Object>> columns = new ArrayList<>();

        if (!tableExists(tableName)) {
            throw new SQLException("Tablica '" + tableName + "' ne postoji.");
        }

        try (Connection connection = dataSource.getConnection();
             ResultSet columnsResultSet = connection.getMetaData().getColumns(null, null, tableName, null)) {

            while (columnsResultSet.next()) {
                Map<String, Object> column = new HashMap<>();
                column.put("columnName", columnsResultSet.getString("COLUMN_NAME"));
                column.put("dataType", columnsResultSet.getString("TYPE_NAME"));
                column.put("isNullable", columnsResultSet.getString("IS_NULLABLE").equals("YES"));
                column.put("isPrimaryKey", false);
                column.put("isForeignKey", false);
                column.put("referencedTable", null);
                column.put("referencedColumn", null);
                columns.add(column);
            }
        }

        // Dodaj informacije o primarnim ključevima
        try (Connection connection = dataSource.getConnection();
             ResultSet primaryKeys = connection.getMetaData().getPrimaryKeys(null, null, tableName)) {

            Set<String> primaryKeyColumns = new HashSet<>();
            while (primaryKeys.next()) {
                primaryKeyColumns.add(primaryKeys.getString("COLUMN_NAME"));
            }

            for (Map<String, Object> column : columns) {
                column.put("isPrimaryKey", primaryKeyColumns.contains(column.get("columnName")));
            }
        }

        try (Connection connection = dataSource.getConnection();
             ResultSet foreignKeys = connection.getMetaData().getImportedKeys(null, null, tableName)) {

            while (foreignKeys.next()) {
                String fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");
                String pkTableName = foreignKeys.getString("PKTABLE_NAME");
                String pkColumnName = foreignKeys.getString("PKCOLUMN_NAME");

                for (Map<String, Object> column : columns) {
                    if (column.get("columnName").equals(fkColumnName)) {
                        column.put("isForeignKey", true);
                        column.put("referencedTable", pkTableName);
                        column.put("referencedColumn", pkColumnName);
                    }
                }
            }
        }

        return columns;
    }

    // Resetiraj sve promjene
    public void resetAllChanges() {
        modifiedData.clear();
        columnAnonymizationState.clear();
    }

    private boolean tableExists(String tableName) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, tableName, new String[]{"TABLE"});
            return tables.next();
        }
    }

    public boolean isColumnAnonymized(String tableName, String columnName) {
        return columnAnonymizationState.getOrDefault(tableName, new HashMap<>()).containsKey(columnName);
    }

    public void setColumnAnonymizationTechnique(String tableName, String columnName, String technique) {
        columnAnonymizationState.computeIfAbsent(tableName, k -> new HashMap<>()).put(columnName, technique);
    }

    public boolean isPrimaryKey(String tableName, String columnName) throws SQLException {
        List<Map<String, Object>> metadata = getTableMetadata(tableName);
        for (Map<String, Object> column : metadata) {
            if (columnName.equals(column.get("columnName")) && Boolean.TRUE.equals(column.get("isPrimaryKey"))) {
                return true;
            }
        }
        return false;
    }

    public boolean isForeignKey(String tableName, String columnName) throws SQLException {
        List<Map<String, Object>> metadata = getTableMetadata(tableName);
        for (Map<String, Object> column : metadata) {
            if (columnName.equals(column.get("columnName")) && Boolean.TRUE.equals(column.get("isForeignKey"))) {
                return true;
            }
        }
        return false;
    }
}
