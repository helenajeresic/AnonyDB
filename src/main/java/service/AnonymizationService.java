package service;

import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.sql.*;
import java.util.*;
import jakarta.inject.Inject;

@ApplicationScoped
public class AnonymizationService {

    @Inject
    TablesService tablesService;

    public void hashPrimaryKey(String tableName, String primaryKeyColumn) throws Exception {
        List<Map<String, Object>> tableData = tablesService.getTableData(tableName);
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

        updateForeignKeys(tableName, hashedValues);
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

    private void updateForeignKeys(String primaryTableName, Map<Object, String> hashedValues) throws SQLException {
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

    public void suppressColumn(String tableName, String columnName) throws Exception {
        List<Map<String, Object>> tableData = tablesService.getTableData(tableName);
        if (tablesService.isColumnAnonymized(tableName, columnName)) {
            throw new Exception("Technique already applied to this column.");
        }

        for (Map<String, Object> row : tableData) {
            row.put(columnName, "*");
        }

        tablesService.setColumnAnonymizationTechnique(tableName, columnName, "suppression");
    }

    public void applyNoise(String tableName, String columnName, String noiseParameter) throws Exception {
        List<Map<String, Object>> tableData = tablesService.getTableData(tableName);
        if (tablesService.isColumnAnonymized(tableName, columnName)) {
            throw new Exception("Technique already applied to this column.");
        }

        double noiseRange = Double.parseDouble(noiseParameter);

        for (Map<String, Object> row : tableData) {
            Object originalValue = row.get(columnName);

            if (originalValue instanceof Number) {
                double noisyValue = applyNoiseToNumericValue(originalValue, noiseRange);
                row.put(columnName, preserveDecimalFormat(originalValue, noisyValue));
            } else {
                throw new Exception("Unsupported numeric type.");
            }
        }

        tablesService.setColumnAnonymizationTechnique(tableName, columnName, "noise");
    }

    private double applyNoiseToNumericValue(Object originalValue, double noiseRange) {
        double noisyValue;

        if (originalValue instanceof Integer) {
            noisyValue = (Integer) originalValue + (int) (Math.random() * (2 * noiseRange + 1) - noiseRange);
        } else if (originalValue instanceof Long) {
            noisyValue = (Long) originalValue + (int) (Math.random() * (2 * noiseRange + 1) - noiseRange);
        } else if (originalValue instanceof Float) {
            noisyValue = (Float) originalValue + (Math.random() * noiseRange - noiseRange / 2);
        } else if (originalValue instanceof Double) {
            noisyValue = (Double) originalValue + (Math.random() * noiseRange - noiseRange / 2);
        } else if (originalValue instanceof BigDecimal) {
            noisyValue = ((BigDecimal) originalValue).doubleValue() + (Math.random() * noiseRange - noiseRange / 2);
        } else {
            throw new IllegalArgumentException("Unsupported numeric type.");
        }

        return noisyValue;
    }

    private Object preserveDecimalFormat(Object originalValue, double noisyValue) {
        String originalValueStr = String.valueOf(originalValue);
        int decimalIndex = originalValueStr.indexOf(".");

        if (decimalIndex == -1) {
            return (int) noisyValue;
        } else {
            int decimalPlaces = originalValueStr.length() - decimalIndex - 1;
            String format = "%." + decimalPlaces + "f";
            return String.format(format, noisyValue);
        }
    }
}

