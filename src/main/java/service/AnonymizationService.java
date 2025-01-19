package service;

import jakarta.enterprise.context.ApplicationScoped;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.*;

@ApplicationScoped
public class AnonymizationService {

    public void hashPrimaryKey(String tableName, String primaryKeyColumn, List<Map<String, Object>> tableData) throws Exception {
        Map<Object, String> hashedValues = new HashMap<>();

        for (Map<String, Object> row : tableData) {
            Object primaryKeyValue = row.get(primaryKeyColumn);
            String hashedValue = hashValue(primaryKeyValue.toString());
            hashedValues.put(primaryKeyValue, hashedValue);
            row.put(primaryKeyColumn, hashedValue);
        }
    }

    private String hashValue(String value) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(value.getBytes());
        StringBuilder hashString = new StringBuilder();
        for (byte b : hashBytes) {
            hashString.append(String.format("%02x", b));
        }
        return hashString.toString();
    }
}
