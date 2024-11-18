package com.example.vetcare.modelo;

import com.example.vetcare.clases.MySQLConnector;
import com.google.gson.annotations.Expose;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Sede {
    @Expose(serialize = false)
    Connection connection;
    @Expose
    int id_Sede;
    @Expose
    String nombre;
    @Expose
    String latitud;
    @Expose
    String altitud;

    public Sede(int id_Sede, String nombre, String latitud, String altitud) {
        this.id_Sede = id_Sede;
        this.nombre = nombre;
        this.latitud = latitud;
        this.altitud = altitud;
    }

    public Sede() {
        try {
            MySQLConnector conexion = new MySQLConnector();
            connection = conexion.conecta();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public int getId_Sede() {
        return id_Sede;
    }

    public void setId_Sede(int id_Sede) {
        this.id_Sede = id_Sede;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getAltitud() {
        return altitud;
    }

    public void setAltitud(String altitud) {
        this.altitud = altitud;
    }

    public ArrayList<Sede> obtenerSedes(){
        ArrayList<Sede> listaSedes = new ArrayList<>();
        Sede sede = null;
        try {
            String sql = "{CALL obtenerSedes()}";

            try (CallableStatement statement = connection.prepareCall(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        sede = new Sede();
                        sede.setId_Sede(resultSet.getInt("id_Sede"));
                        sede.setNombre(resultSet.getString("nombre"));
                        sede.setLatitud(resultSet.getString("latitud"));
                        sede.setAltitud(resultSet.getString("altitud"));
                        listaSedes.add(sede);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaSedes;
    }

    // Función para obtener una sede por ID
    public Sede obtenerSedePorID(int idSede) {
        Sede sede = null;
        try {
            String sql = "{CALL obtenerSedePorID(?)}"; // Llamada al stored procedure
            try (CallableStatement statement = connection.prepareCall(sql)) {
                statement.setInt(1, idSede); // Establecer el parámetro para el procedimiento almacenado
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        sede = new Sede();
                        sede.setId_Sede(resultSet.getInt("id_Sede"));
                        sede.setNombre(resultSet.getString("nombre"));
                        sede.setLatitud(resultSet.getString("latitud"));
                        sede.setAltitud(resultSet.getString("altitud"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sede; // Devolver la sede encontrada
    }
}
