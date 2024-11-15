package com.example.vetcare.modelo;

import com.example.vetcare.clases.MySQLConnector;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Veterinario {
    private Connection connection;
    private int id_Veterinario;
    private String nombre;
    private String apellidos;
    private String codigoColegiatura;

    public Veterinario() {
        try {
            MySQLConnector conexion = new MySQLConnector();
            connection = conexion.conecta();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Veterinario(Connection connection, int id_Veterinario, String nombre, String apellidos, String codigoColegiatura) {
        this.connection = connection;
        this.id_Veterinario = id_Veterinario;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.codigoColegiatura = codigoColegiatura;
    }

    // Getters y Setters
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

    public List<Veterinario> obtenerVeterinarios() {
        List<Veterinario> listaVeterinarios = new ArrayList<>();
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
}

