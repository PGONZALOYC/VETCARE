package com.example.vetcare.clases;

import com.example.vetcare.modelo.Cita;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class VistaCita{
    private Connection connection;
    private int id;
    private byte [] foto;
    private String nombreMascota;
    private String servicio;
    private String veterinario;
    private String sede;
    private Date fecha;
    private String horario;

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public VistaCita() {
        try {
            MySQLConnector conexion = new MySQLConnector();
            connection = conexion.conecta();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte [] getFoto() {
        return foto;
    }

    public void setFoto(byte foto []) {
        this.foto= foto;
    }

    public String getNombreMascota() {
        return nombreMascota;
    }

    public void setNombreMascota(String nombreMascota) {
        this.nombreMascota = nombreMascota;
    }

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }

    public String getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(String veterinario) {
        this.veterinario = veterinario;
    }

    public String getSede() {
        return sede;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }


    public ArrayList<VistaCita> obtenerCitasPorCorreo(String correo) {
        ArrayList<VistaCita> listaCitas = new ArrayList<>();

        try {
            // Llamada al procedimiento almacenado con el parámetro de correo
            String sql = "{CALL ObtenerCitasPorCorreo(?)}";  // Procedimiento con un parámetro

            try (CallableStatement statement = connection.prepareCall(sql)) {
                // Establecer el valor del parámetro de correo
                statement.setString(1, correo);

                // Ejecutar la consulta y obtener el ResultSet
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        VistaCita vistaCita = new VistaCita();

                        // Asignar los valores obtenidos del ResultSet a los atributos de la cita
                        vistaCita.setId(resultSet.getInt("id_Cita"));
                        vistaCita.setFecha(resultSet.getDate("Fecha"));
                        vistaCita.setHorario(resultSet.getString("horario"));
                        vistaCita.setServicio(resultSet.getString("Servicio"));
                        // Obtener los nombres directamente
                        vistaCita.setNombreMascota(resultSet.getString("NombreMascota"));
                        vistaCita.setSede(resultSet.getString("NombreSede"));
                        // Concatenar nombre y apellido del veterinario
                        String nombreCompletoVeterinario = resultSet.getString("NombreVeterinario") + " " +
                                resultSet.getString("ApellidosVeterinario");
                        vistaCita.setVeterinario(nombreCompletoVeterinario);
                        //Obtener la Imagen
                        vistaCita.setFoto(resultSet.getBytes("FotoMascota"));
                        // Añadir la cita a la lista
                        listaCitas.add(vistaCita);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Mostrar detalles del error en caso de excepciones
        }
        return listaCitas;  // Devolver la lista de citas
    }

}
