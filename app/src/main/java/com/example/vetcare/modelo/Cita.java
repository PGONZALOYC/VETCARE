package com.example.vetcare.modelo;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Date; // Asegúrate de importar la clase Date para manejar fechas

public class Cita {
    private Connection connection;
    private int idCita;
    private int idUsuario;
    private int idMascota;
    private String servicio;
    private int idVeterinario;
    private String horaCita;
    private String sede;
    private String estado;
    private Date fecha;

    // Constructor vacío
    public Cita() {}

    // Constructor con parámetros, incluyendo la fecha
    public Cita(int idCita, int idUsuario, Date fecha, int idMascota, String servicio, int idVeterinario, String horaCita, String sede, String estado) {
        this.idCita = idCita;
        this.idUsuario = idUsuario;
        this.fecha = fecha;
        this.idMascota = idMascota;
        this.servicio = servicio;
        this.idVeterinario = idVeterinario;
        this.horaCita = horaCita;
        this.sede = sede;
        this.estado = estado;
    }

    // Getters y setters
    public int getIdCita() {
        return idCita;
    }

    public void setIdCita(int idCita) {
        this.idCita = idCita;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdMascota() {
        return idMascota;
    }

    public void setIdMascota(int idMascota) {
        this.idMascota = idMascota;
    }

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }

    public int getIdVeterinario() {
        return idVeterinario;
    }

    public void setIdVeterinario(int idVeterinario) {
        this.idVeterinario = idVeterinario;
    }

    public String getHoraCita() {
        return horaCita;
    }

    public void setHoraCita(String horaCita) {
        this.horaCita = horaCita;
    }

    public String getSede() {
        return sede;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFecha() {
        return fecha; // Getter para la fecha
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha; // Setter para la fecha
    }

    // Método para crear una cita, ahora con la fecha
    public boolean crearCita() {
        boolean exito = false;
        CallableStatement stmt = null;

        try {
            // Preparar el llamado al procedimiento almacenado CrearCita
            stmt = connection.prepareCall("{CALL CrearCita(?, ?, ?, ?, ?, ?, ?, ?)}");

            // Asignar los valores a los parámetros
            stmt.setInt(1, this.idUsuario);
            stmt.setDate(2, this.fecha);
            stmt.setInt(3, this.idMascota);
            stmt.setString(4, this.servicio);
            stmt.setInt(5, this.idVeterinario);
            stmt.setString(6, this.horaCita);
            stmt.setString(7, this.sede);
            stmt.setString(8, this.estado);

            // Ejecutar el procedimiento
            stmt.executeUpdate();

            exito = true; // Si no hay excepciones, la cita se creó exitosamente
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Asegúrate de cerrar los recursos
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return exito;
    }
}
