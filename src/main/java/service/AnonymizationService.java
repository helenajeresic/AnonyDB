package service;

import jakarta.enterprise.context.ApplicationScoped;
import java.security.MessageDigest;
import java.sql.*;
import java.util.*;
import jakarta.inject.Inject;

@ApplicationScoped
public class AnonymizationService {

    @Inject
    TablesService tablesService;

    public void hashPrimaryKey(String tableName, String primaryKeyColumn, List<Map<String, Object>> tableData) throws Exception {
        if (tablesService.isColumnAnonymized(tableName, primaryKeyColumn)) {
            throw new Exception("Technique already applied to this column.");
        }

        Map<Object, String> hashedValues = new HashMap<>();
        for (Map<String, Object> row : tableData) {
            Object primaryKeyValue = row.get(primaryKeyColumn);
            String hashedValue = hashValue(primaryKeyValue.toString());
            hashedValues.put(primaryKeyValue, hashedValue);
            row.put(primaryKeyColumn, hashedValue);
        }

        tablesService.setColumnAnonymizationTechnique(tableName, primaryKeyColumn, "hash");

        updateForeignKeysInMemory(tableName, hashedValues);
    }

    private String hashValue(String value) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(value.getBytes());
        StringBuilder hashString = new StringBuilder();
        for (byte b : hashBytes) {
            hashString.append(String.format("%02x", b));
        }
        return hashString.substring(0, 15);
    }

    private void updateForeignKeysInMemory(String primaryTableName, Map<Object, String> hashedValues) throws SQLException {
        List<String> tables = tablesService.getTables();

        for (String table : tables) {
            List<Map<String, Object>> metadata = tablesService.getTableMetadata(table);
            for (Map<String, Object> column : metadata) {
                if ("true".equals(String.valueOf(column.get("isForeignKey")))) {
                    String fkColumn = (String) column.get("columnName");
                    String referencedTable = (String) column.get("referencedTable");

                    if (primaryTableName.equals(referencedTable)) {
                        updateForeignKeyValues(table, fkColumn, hashedValues);
                    }
                }
            }
        }
    }

    private void updateForeignKeyValues(String tableName, String foreignKeyColumn, Map<Object, String> hashedValues) throws SQLException {
        List<Map<String, Object>> tableData = tablesService.getTableData(tableName);

        for (Map<String, Object> row : tableData) {
            Object foreignKeyValue = row.get(foreignKeyColumn);

            if (hashedValues.containsKey(foreignKeyValue)) {
                String hashedForeignKey = hashedValues.get(foreignKeyValue);
                row.put(foreignKeyColumn, hashedForeignKey);
            }
        }
    }
}

