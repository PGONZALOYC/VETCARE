package com.example.vetcare.clases;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySQLConnector {
    // URL de conexión a la base de datos MySQL en el servidor remoto, incluyendo el nombre de la base de datos "VetCare"
    private static final String URL = "jdbc:mysql://54.87.3.7:3306/VetCare";
    // Nombre de usuario para autenticar la conexión a la base de datos
    private static final String USER = "admin";
    // Contraseña del usuario para la conexión
    private static final String PASSWORD = "abc123$";

    // Método para probar la conexión a la base de datos
    public int probarConexion() {
        Connection connection = null;
        try {
            // Carga el driver de MySQL para permitir la conexión a la base de datos
            Class.forName("com.mysql.jdbc.Driver");
            // Establece la conexión usando la URL, usuario y contraseña proporcionados
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            return 1; // Conexión exitosa
        } catch (Exception e) {
            // Imprime cualquier error de conexión en la consola
            e.printStackTrace();
            return 0; // Error en la conexión
        } finally {
            try {
                // Cierra la conexión si fue establecida, para liberar los recursos
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Método para obtener una conexión a la base de datos
    public Connection conecta(){
        Connection connection = null;
        try {
            // Carga el driver de MySQL
            Class.forName("com.mysql.jdbc.Driver"); // Carga el driver MySQL
            // Intenta establecer la conexión a la base de datos
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            return connection;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null; // Retorna null si no se pudo establecer la conexión
    }
}

