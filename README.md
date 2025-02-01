# AnonyDB

**AnonyDB** is an application for anonymizing data stored in relational databases. It was developed as part of the thesis titled **"Anonymization of Data Stored in Relational Databases"**.  
The application implements various data anonymization techniques, including **hashing**, **suppression**, and **adding noise**. Additionally, the application provides the ability to reset all changes and export data to CSV format, along with logging functionality to track all operations.

## Features

### **Table Selection**
- Users can select a table from the database that they want to anonymize.

### **Anonymization Techniques**

- **Hashing**:  
 Allows the replacement of sensitive data with hashed values. Hashing is specifically applied to **primary keys** to ensure privacy and to preserve the integrity of relationships within the database.

- **Suppression**:  
  Provides the option to mask sensitive data in specific columns.

- **Adding Noise**:  
  Adds random values to numeric data to protect privacy while maintaining the overall distribution and usability of the data.

### **Reset Changes**
- Allows users to revert all changes and restore the data to its original state before any anonymization was applied.

### **Export Data to CSV**
- After processing, the anonymized data can be exported to **CSV format** for further analysis, reporting, or storage.

### **Logging**
- All actions and operations performed within the application are logged to track and audit changes made to the data.

---

