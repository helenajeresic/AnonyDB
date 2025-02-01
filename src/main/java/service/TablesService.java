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

    /**
     * Dohvaća popis svih tablica u bazi podataka.
     *
     * @return Lista imena tablica.
     * @throws SQLException Ako dođe do greške prilikom dohvaćanja podataka.
     */
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

    /**
     * Dohvaća podatke iz određene tablice.
     *
     * @param tableName Ime tablice.
     * @return Lista mapa koje predstavljaju retke tablice.
     * @throws SQLException Ako dođe do greške pri dohvaćanju podataka.
     */
    public List<Map<String, Object>> getTableData(String tableName) throws SQLException {
        if (!modifiedData.containsKey(tableName)) {
            loadTableDataIntoMemory(tableName);
        }
        return modifiedData.get(tableName);
    }

    /**
     * Učitava podatke tablice u memoriju kako bi se mogli mijenjati i obrađivati.
     *
     * @param tableName Ime tablice.
     * @throws SQLException Ako dođe do greške pri dohvaćanju podataka.
     */
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

    /**
     * Dohvaća metapodatke za određenu tablicu, uključujući nazive stupaca, tipove podataka,
     * primarne i strane ključeve.
     *
     * @param tableName Ime tablice.
     * @return Lista mapa koje sadrže metapodatke o svakom stupcu.
     * @throws SQLException Ako dođe do greške pri dohvaćanju podataka.
     */
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

        try (Connection connection = dataSource.getConnection();
             ResultSet primaryKeys = connection.getMetaData().getPrimaryKeys(null, null, tableName)) {

            Set<String> primaryKeyColumns = new HashSet<>();
            while (primaryKeys.next()) {
                primaryKeyColumns.add(primaryKeys.getString("COLUMN_NAME"));
            }

            for (Map<String, Object> column : columns) {
                String columnName = (String) column.get("columnName");
                column.put("isPrimaryKey", primaryKeyColumns.contains(columnName));
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

    /**
     * Resetira sve promjene nad podacima u memoriji.
     */
    public void resetAllChanges() {
        modifiedData.clear();
        columnAnonymizationState.clear();
    }

    /**
     * Provjerava postoji li tablica u bazi podataka.
     *
     * @param tableName Ime tablice.
     * @return True ako tablica postoji, inače false.
     * @throws SQLException Ako dođe do greške pri provjeri.
     */
    private boolean tableExists(String tableName) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, tableName, new String[]{"TABLE"});
            return tables.next();
        }
    }

    /**
     * Provjerava je li stupac već anonimiziran.
     *
     * @param tableName  Ime tablice.
     * @param columnName Ime stupca.
     * @return True ako je stupac već anonimiziran, inače false.
     */
    public boolean isColumnAnonymized(String tableName, String columnName) {
        return columnAnonymizationState.getOrDefault(tableName, new HashMap<>()).containsKey(columnName);
    }

    /**
     * Postavlja tehniku anonimizacije za određeni stupac.
     *
     * @param tableName  Ime tablice.
     * @param columnName Ime stupca.
     * @param technique  Tehnika anonimizacije.
     */
    public void setColumnAnonymizationTechnique(String tableName, String columnName, String technique) {
        columnAnonymizationState.computeIfAbsent(tableName, k -> new HashMap<>()).put(columnName, technique);
    }

    /**
     * Provjerava je li određeni stupac primarni ključ.
     *
     * @param tableName  Ime tablice.
     * @param columnName Ime stupca.
     * @return True ako je stupac primarni ključ, inače false.
     * @throws SQLException Ako dođe do greške pri dohvaćanju podataka.
     */
    public boolean isPrimaryKey(String tableName, String columnName) throws SQLException {
        List<Map<String, Object>> metadata = getTableMetadata(tableName);
        for (Map<String, Object> column : metadata) {
            if (columnName.equals(column.get("columnName")) && Boolean.TRUE.equals(column.get("isPrimaryKey"))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Provjerava je li određeni stupac strani ključ.
     *
     * @param tableName  Ime tablice.
     * @param columnName Ime stupca.
     * @return True ako je stupac strani ključ, inače false.
     * @throws SQLException Ako dođe do greške pri dohvaćanju podataka.
     */
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
