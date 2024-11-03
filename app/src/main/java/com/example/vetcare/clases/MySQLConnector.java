package com.example.vetcare.clases;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySQLConnector {
    private static final String URL = "jdbc:mysql://54.87.3.7:3306/VetCare";
    private static final String USER = "admin";
    private static final String PASSWORD = "abc123$";

    public int probarConexion() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver"); // Carga el driver MySQL
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            return 1; // Conexión exitosa
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // Error en la conexión
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Connection conecta(){
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver"); // Carga el driver MySQL
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            return connection;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}

