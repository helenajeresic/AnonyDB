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

    /**
     * Metoda koja primjenjuje hash funkciju na primarni ključ tablice.
     * @param tableName Naziv tablice u kojoj se hashira primarni ključ.
     * @param primaryKeyColumn Naziv stupca koji predstavlja primarni ključ.
     * @throws Exception Ako stupac nije primarni ključ ili je već anonimiziran.
     */
    public void hashPrimaryKey(String tableName, String primaryKeyColumn) throws Exception {
        if (!tablesService.isPrimaryKey(tableName, primaryKeyColumn)) {
            throw new Exception("Column is not a primary key.");
        }

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

    /**
     * Metoda koja generira SHA-256 hash vrijednost za zadani unos.
     * @param value Vrijednost koja se hashira.
     * @return SHA-256 hashirana vrijednost u obliku heksadekadnog stringa.
     * @throws Exception Ako dođe do greške prilikom hashiranja.
     */
    private String hashValue(String value) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(value.getBytes());
        StringBuilder hashString = new StringBuilder();
        for (byte b : hashBytes) {
            hashString.append(String.format("%02x", b));
        }
        return hashString.toString();
    }

    /**
     * Metoda koja ažurira sve vanjske ključeve vezane za anonimizirani primarni ključ.
     * @param primaryTableName Naziv tablice s primarnim ključem.
     * @param hashedValues Mapa originalnih i hashiranih vrijednosti.
     * @throws SQLException Ako dođe do greške prilikom ažuriranja.
     */
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

    /**
     * Metoda koja ažurira vrijednosti određenog vanjskog ključa u određenoj tablici za anonimizirani primarni ključ.
     * @param tableName Naziv tablice.
     * @param foreignKeyColumn Naziv stupca koji sadrži vanjski ključ.
     * @param hashedValues Mapa originalnih i hashiranih vrijednosti.
     * @throws SQLException Ako dođe do greške prilikom ažuriranja podataka.
     */
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

    /**
     * Metoda koja primjenjuje supresiju na određeni stupac u tablici.
     * @param tableName Naziv tablice u kojoj se primjenjuje supresija.
     * @param columnName Naziv stupca na kojem se vrši supresija.
     * @throws Exception Ako je stupac primarni ili vanjski ključ ili je već anonimiziran.
     */
    public void suppressColumn(String tableName, String columnName) throws Exception {
        if (tablesService.isPrimaryKey(tableName, columnName)) {
            throw new Exception("Column is a primary key or foreign key.");
        }

        if (tablesService.isForeignKey(tableName, columnName)) {
            throw new Exception("Column is a primary key or foreign key.");
        }

        List<Map<String, Object>> tableData = tablesService.getTableData(tableName);
        if (tablesService.isColumnAnonymized(tableName, columnName)) {
            throw new Exception("Technique already applied to this column.");
        }

        for (Map<String, Object> row : tableData) {
            row.put(columnName, "*");
        }

        tablesService.setColumnAnonymizationTechnique(tableName, columnName, "suppression");
    }

    /**
     * Metoda koja dodaje šum numeričkim podacima u tablici.
     * @param tableName Naziv tablice u kojoj se dodaje šum.
     * @param columnName Naziv stupca na koji se dodaje šum.
     * @param noiseParameter Parametar koji određuje raspon šuma.
     * @throws Exception Ako je stupac primarni ili vanjski ključ, već anonimiziran ili nije numerički.
     */
    public void applyNoise(String tableName, String columnName, String noiseParameter) throws Exception {
        if (tablesService.isPrimaryKey(tableName, columnName)) {
            throw new Exception("Column is a primary key or foreign key.");
        }

        if (tablesService.isForeignKey(tableName, columnName)) {
            throw new Exception("Column is a primary key or foreign key.");
        }

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

    /**
     * Metoda koja dodaje šum određenoj numeričkoj vrijednosti.
     * @param originalValue Originalna vrijednost.
     * @param noiseRange Raspon šuma.
     * @return Nova vrijednost s primijenjenim šumom.
     */
    private double applyNoiseToNumericValue(Object originalValue, double noiseRange) {

        return switch (originalValue) {
            case Integer i -> i + (int) (Math.random() * (2 * noiseRange + 1) - noiseRange);
            case Long l -> l + (int) (Math.random() * (2 * noiseRange + 1) - noiseRange);
            case Float v -> v + (Math.random() * noiseRange - noiseRange / 2);
            case Double v -> v + (Math.random() * noiseRange - noiseRange / 2);
            case BigDecimal bigDecimal -> bigDecimal.doubleValue() + (Math.random() * noiseRange - noiseRange / 2);
            case null, default -> throw new IllegalArgumentException("Unsupported numeric type.");
        };
    }

    /**
     * Metoda koja osigurava da numerička vrijednost zadrži isti format s decimalnim mjestima.
     * @param originalValue Originalna vrijednost.
     * @param noisyValue Nova vrijednost s primijenjenim šumom.
     * @return Nova vrijednost s istim formatom decimalnih mjesta.
     */
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
