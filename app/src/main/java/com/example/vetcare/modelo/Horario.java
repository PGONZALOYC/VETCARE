package com.example.vetcare.modelo;

import com.example.vetcare.clases.MySQLConnector;

import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Horario {
    private Connection connection;
    private String horaInicio;
    private String horaFinal;
    private int id_Veterinario;
    private boolean disponible;
    private Date fecha;

    public Horario(Connection connection, String horaInicio, String horaFinal, int id_Veterinario, boolean disponible, Date fecha) {
        this.connection = connection;
        this.horaInicio = horaInicio;
        this.horaFinal = horaFinal;
        this.id_Veterinario = id_Veterinario;
        this.disponible = disponible;
        this.fecha = fecha;
    }

    public Horario() {
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

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFinal() {
        return horaFinal;
    }

    public void setHoraFinal(String horaFinal) {
        this.horaFinal = horaFinal;
    }

    public int getId_Veterinario() {
        return id_Veterinario;
    }

    public void setId_Veterinario(int id_Veterinario) {
        this.id_Veterinario = id_Veterinario;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public ArrayList<Horario> obtenerHorarios(){
        ArrayList<Horario> listaHorarios = new ArrayList<>();
        Horario horario = null;
        try {
            String sql = "{CALL obtenerHorariosVeterinario()}";

            try (CallableStatement statement = connection.prepareCall(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        horario = new Horario();
                        horario.setId_Veterinario(resultSet.getInt("id_Veterinario"));
                        horario.setHoraInicio(resultSet.getString("horaInicio"));
                        horario.setHoraFinal(resultSet.getString("horaFinal"));
                        horario.setDisponible(resultSet.getBoolean("disponible"));
                        horario.setFecha(resultSet.getDate("fecha"));
                        listaHorarios.add(horario);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaHorarios;
    }
}
