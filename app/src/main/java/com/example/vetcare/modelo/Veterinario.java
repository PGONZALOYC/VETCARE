package com.example.vetcare.modelo;

import com.example.vetcare.clases.MySQLConnector;
import com.google.gson.annotations.Expose;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Veterinario {
    @Expose(serialize = false)
    private Connection connection;
    @Expose
    private int id_Veterinario;
    @Expose
    private String nombre;
    @Expose
    private String apellidos;
    @Expose
    private String codigoColegiatura;
    @Expose
    private int id_Sede;

    public Veterinario() {
        try {
            MySQLConnector conexion = new MySQLConnector();
            connection = conexion.conecta();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Veterinario(Connection connection, int id_Veterinario, String nombre, String apellidos, String codigoColegiatura, int id_Sede) {
        this.connection = connection;
        this.id_Veterinario = id_Veterinario;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.codigoColegiatura = codigoColegiatura;
        this.id_Sede = id_Sede;
    }
    // Getters y Setters
    public int getId_Sede() {
        return id_Sede;
    }

    public void setId_Sede(int id_Sede) {
        this.id_Sede = id_Sede;
    }


    public int getId_Veterinario() {
        return id_Veterinario;
    }

    public void setId_Veterinario(int id_Veterinario) {
        this.id_Veterinario = id_Veterinario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCodigoColegiatura() {
        return codigoColegiatura;
    }

    public void setCodigoColegiatura(String codigoColegiatura) {
        this.codigoColegiatura = codigoColegiatura;
    }

    public ArrayList<Veterinario> obtenerVeterinarios() {
        ArrayList<Veterinario> listaVeterinarios = new ArrayList<>();
        Veterinario veterinario= null;
        try{
            String sql = "{CALL obtenerVeterinarios()}";
            try (CallableStatement statement = connection.prepareCall(sql)) {

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        veterinario = new Veterinario();
                        veterinario.setId_Veterinario(resultSet.getInt("id_Veterinario"));
                        veterinario.setNombre(resultSet.getString("Nombre"));
                        veterinario.setApellidos(resultSet.getString("Apellidos"));
                        veterinario.setCodigoColegiatura(resultSet.getString("CodigoColegiatura"));
                        veterinario.setId_Sede(resultSet.getInt("id_Sede"));
                        listaVeterinarios.add(veterinario);
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return listaVeterinarios;
    }
    // Función para obtener un veterinario por ID
    public Veterinario obtenerVeterinarioPorID(int idVeterinario) {
        Veterinario veterinario = null;
        try {
            String sql = "{CALL obtenerVeterinarioPorID(?)}"; // Llamar al procedimiento almacenado con un parámetro
            try (CallableStatement statement = connection.prepareCall(sql)) {
                statement.setInt(1, idVeterinario); // Establecer el ID del veterinario

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        veterinario = new Veterinario();
                        veterinario.setId_Veterinario(resultSet.getInt("id_Veterinario"));
                        veterinario.setNombre(resultSet.getString("Nombre"));
                        veterinario.setApellidos(resultSet.getString("Apellidos"));
                        veterinario.setCodigoColegiatura(resultSet.getString("CodigoColegiatura"));
                        veterinario.setId_Sede(resultSet.getInt("id_Sede"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return veterinario; // Devolver el veterinario encontrado
    }
}

