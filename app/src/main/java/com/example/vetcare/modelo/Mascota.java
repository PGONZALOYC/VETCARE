package com.example.vetcare.modelo;

import com.example.vetcare.clases.MySQLConnector;
import com.google.gson.annotations.Expose;

import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Mascota {
    @Expose(serialize = false)
    private Connection connection;
    @Expose
    private int id_Mascota;
    @Expose
    private String nombre;
    @Expose
    private String tipo;
    @Expose
    private String raza;
    @Expose
    private Date fechaNacimiento;
    @Expose
    private byte[] imagen;
    @Expose
    private int edadAño;
    @Expose
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


    public ArrayList<Mascota> obtenerMascotasPorCorreo(String correo) {
        ArrayList<Mascota> listaMascotas = new ArrayList<>();
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
                        Blob blob = resultSet.getBlob("Imagen");
                        if(blob!=null){
                            mascota.setImagen(blob.getBytes(1, (int) blob.length()));
                        }
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
    public boolean agregarMascota(int idUsuario, String nombre, String tipo, String raza, Date fechaNacimiento, byte[] imagen, int edadAño, int edadMeses) {
        boolean exito = false;
        try {
            String sql = "{CALL AgregarMascota(?, ?, ?, ?, ?, ?, ?, ?)}";
            CallableStatement statement = connection.prepareCall(sql);

            // Establecer los valores de los parámetros en el statement
            statement.setInt(1, idUsuario);
            statement.setString(2, nombre);
            statement.setString(3, tipo);
            statement.setString(4, raza);
            statement.setDate(5, new java.sql.Date(fechaNacimiento.getTime()));
            Blob blob = connection.createBlob();
            blob.setBytes(1, imagen);
            statement.setBlob(6, blob);
            statement.setInt(7, edadAño);
            statement.setInt(8, edadMeses);

            // Ejecutar la inserción
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                exito = true; // Si se insertó correctamente
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exito;
    }

    public boolean editarMascota(int idMascota, String nombre, String tipo, String raza,byte[] imagen, int edadAño, int edadMeses) {
        boolean actualizado = false;
        try {
            // Llama al procedimiento almacenado
            String sql = "{CALL EditarMascota(?, ?, ?, ?, ?, ?, ?, ?)}";
            try (CallableStatement statement = connection.prepareCall(sql)) {
                // Configura los parámetros
                statement.setInt(1, idMascota);
                statement.setString(2, nombre);
                statement.setString(3, tipo);
                statement.setString(4, raza);
                Blob blob = connection.createBlob();
                blob.setBytes(1, imagen);
                statement.setBlob(5, blob);
                statement.setInt(6, edadAño);
                statement.setInt(7, edadMeses);

                // Ejecuta la actualización
                int filasActualizadas = statement.executeUpdate();
                actualizado = filasActualizadas > 0; // True si se actualizó al menos una fila
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return actualizado;
    }

}
