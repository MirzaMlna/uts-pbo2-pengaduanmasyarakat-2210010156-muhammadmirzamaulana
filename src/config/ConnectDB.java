    package config;
    import java.sql.Connection;
    import java.sql.Driver;
    import java.sql.DriverManager;
    import java.sql.SQLException;
    import java.sql.Statement;
    import java.sql.ResultSet;
    import javax.swing.JOptionPane;

public class ConnectDB {
    private String jdbcURL = "jdbc:mysql://localhost:3306/2210010156_pbo2";
    private String username = "root";
    private String password = "";

    // Method to establish database connection
    public Connection getConnectDB() throws SQLException {
        try {
            Driver mysqldriver = new com.mysql.cj.jdbc.Driver();
            DriverManager.registerDriver(mysqldriver);
        } catch (SQLException e) {
            System.out.println("Failed to connect to DB: " + e.getMessage());
        }
        return DriverManager.getConnection(this.jdbcURL, this.username, this.password);
    }

    // Method to check if a record with the primary key exists
    public boolean duplicateKey(String tableName, String primaryKey, String dataValue) {
        boolean result = false;
        try (Connection connection = getConnectDB()) {
            Statement statement = connection.createStatement();
            ResultSet dataResult = statement.executeQuery("SELECT * FROM " + tableName + " WHERE " + primaryKey + " = '" + dataValue + "'");
            result = dataResult.next();
            statement.close();
            dataResult.close();
        } catch (SQLException e) {
            System.err.println(e.toString());
        }
        return result;
    }

    public void addDataDynamically(String tableName, String primaryKey, String primaryKeyValue, String[] fields, String[] values) {
        try (Connection connection = getConnectDB()) {
            // Check if a record with the primary key already exists
            String checkSQL = "SELECT COUNT(*) FROM " + tableName + " WHERE " + primaryKey + " = '" + primaryKeyValue + "'";
            Statement statement = connection.createStatement();
            ResultSet dataResult = statement.executeQuery(checkSQL);

            if (dataResult.next() && dataResult.getInt(1) > 0) { // If record with the primary key exists
                JOptionPane.showMessageDialog(null, "Data with this ID already exists.");
            } else {
                StringBuilder fieldsBuilder = new StringBuilder();
                StringBuilder valuesBuilder = new StringBuilder();

                for (int i = 0; i < fields.length; i++) {
                    fieldsBuilder.append(fields[i]);
                    valuesBuilder.append("'").append(values[i]).append("'");

                    if (i < fields.length - 1) {
                        fieldsBuilder.append(", ");
                        valuesBuilder.append(", ");
                    }
                }

                String insertSQL = "INSERT INTO " + tableName + " (" + primaryKey + ", " + fieldsBuilder.toString() + ") VALUES ('" + primaryKeyValue + "', " + valuesBuilder.toString() + ")";
                statement.executeUpdate(insertSQL);
                JOptionPane.showMessageDialog(null, "Data successfully added");
            }

            dataResult.close();
            statement.close();
        } catch (SQLException e) {
            System.err.println(e.toString());
        }
    }

     public static String getFieldValueEdit(String[] fields, String[] value){
        String result = "";
        int fieldLength = fields.length-1;
        try {
            for (int i = 0; i < fields.length; i++) {
                if (i == fieldLength){
                    result = result +fields[i]+" ='"+value[i]+"'";
                }else{
                   result = result +fields[i]+" ='"+value[i]+"',";  
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        
        return result;
    }
        public void updateDataDynamically(String tableName, String primaryKey, String primaryKeyValue, String[] fields, String[] values) {
            try (Connection connection = getConnectDB()) {
            // Check if the record with the given primary key exists
            String checkSQL = "SELECT COUNT(*) FROM " + tableName + " WHERE " + primaryKey + " = '" + primaryKeyValue + "'";
            Statement statement = connection.createStatement();
            ResultSet dataResult = statement.executeQuery(checkSQL);

            if (dataResult.next() && dataResult.getInt(1) > 0) { // If the ID exists
                // Perform the update if the record is found
                String updateSQL = "UPDATE " + tableName + " SET " + getFieldValueEdit(fields, values) + " WHERE " + primaryKey + "='" + primaryKeyValue + "'";
                statement.executeUpdate(updateSQL);
                JOptionPane.showMessageDialog(null, "Data successfully updated.");
            } else { // If the record with the primary key does not exist
                JOptionPane.showMessageDialog(null, "Data not found.");
            }

            dataResult.close();
            statement.close();

        } catch (Exception e) {
            System.err.println(e.toString());
        }     
    }
        
        public void deleteDataDynamically(String tableName, String primaryKey, String primaryKeyValue) {
            try (Connection connection = getConnectDB()) {
            String checkSQL = "SELECT COUNT(*) FROM " + tableName + " WHERE " + primaryKey + " = '" + primaryKeyValue + "'";
            Statement statement = connection.createStatement();
            ResultSet dataResult = statement.executeQuery(checkSQL);

            if (dataResult.next() && dataResult.getInt(1) > 0) {
                String deleteSQL = "DELETE FROM " + tableName + " WHERE " + primaryKey + "='" + primaryKeyValue + "'";
                statement.executeUpdate(deleteSQL);
                JOptionPane.showMessageDialog(null, "Data successfully deleted.");
            } else { 
                JOptionPane.showMessageDialog(null, "Data not found.");
            }
            dataResult.close();
            statement.close();

        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
}
