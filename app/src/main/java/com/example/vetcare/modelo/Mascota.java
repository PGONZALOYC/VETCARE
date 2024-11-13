package com.example.vetcare.modelo;

import com.example.vetcare.clases.MySQLConnector;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Mascota {
    private Connection connection;
    private int id_Mascota;
    private String nombre;
    private String tipo;
    private String raza;
    private Date fechaNacimiento;
    private byte[] imagen;
    private int edadAño;
    private int edadMeses;

    public Mascota() {
        try {
            MySQLConnector conexion = new MySQLConnector();
            connection = conexion.conecta();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Mascota(Connection connection, int id_Mascota, String nombre, String tipo, String raza, Date fechaNacimiento, byte[] imagen, int edadAño, int edadMeses) {
        this.connection = connection;
        this.id_Mascota = id_Mascota;
        this.nombre = nombre;
        this.tipo = tipo;
        this.raza = raza;
        this.fechaNacimiento = fechaNacimiento;
        this.imagen = imagen;
        this.edadAño = edadAño;
        this.edadMeses = edadMeses;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public int getId_Mascota() {
        return id_Mascota;
    }

    public void setId_Mascota(int id_Mascota) {
        this.id_Mascota = id_Mascota;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public int getEdadAño() {
        return edadAño;
    }

    public void setEdadAño(int edadAño) {
        this.edadAño = edadAño;
    }

    public int getEdadMeses() {
        return edadMeses;
    }

    public void setEdadMeses(int edadMeses) {
        this.edadMeses = edadMeses;
    }


    public List<Mascota> obtenerMascotasPorCorreo(String correo) {
        List<Mascota> listaMascotas = new ArrayList<>();
        Mascota mascota = null;
        try {
            String sql = "{CALL ObtenerMascotasPorUsuario(?)}";

            try (CallableStatement statement = connection.prepareCall(sql)) {
                statement.setString(1, correo);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        mascota = new Mascota();
                        mascota.setId_Mascota(resultSet.getInt("id_Mascota"));
                        mascota.setNombre(resultSet.getString("Nombre"));
                        mascota.setTipo(resultSet.getString("Tipo"));
                        mascota.setRaza(resultSet.getString("Raza"));
                        mascota.setFechaNacimiento(resultSet.getDate("FechaNacimiento"));
                        mascota.setImagen(resultSet.getBytes("imagen"));
                        mascota.setEdadAño(resultSet.getInt("EdadAño"));
                        mascota.setEdadMeses(resultSet.getInt("EdadMeses"));

                        listaMascotas.add(mascota);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaMascotas;
    }

    public Mascota obtenerDetallesMascotaPorID(int idMascota) {
        Mascota mascota = null;
        try {
            String sql = "{CALL ObtenerDetallesMascotaPorID(?)}";

            try (CallableStatement statement = connection.prepareCall(sql)) {
                statement.setInt(1, idMascota);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        mascota = new Mascota();
                        mascota.setId_Mascota(resultSet.getInt("IdMascota"));
                        mascota.setNombre(resultSet.getString("Nombre"));
                        mascota.setTipo(resultSet.getString("Tipo"));
                        mascota.setRaza(resultSet.getString("Raza"));
                        mascota.setFechaNacimiento(resultSet.getDate("FechaNacimiento"));
                        mascota.setImagen(resultSet.getBytes("imagen"));
                        mascota.setEdadAño(resultSet.getInt("EdadAño"));
                        mascota.setEdadMeses(resultSet.getInt("EdadMeses"));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mascota;
    }
}
