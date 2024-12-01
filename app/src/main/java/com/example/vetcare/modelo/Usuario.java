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

public class Usuario {
    private Connection connection;

    private int id_Usuario;
    private String dni;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String correo;
    private String contraseña;
    private byte[] imagen;
    private Date fechaNacimiento;
    private String sexo;

    public Usuario() {
        try {
            MySQLConnector conexion = new MySQLConnector();
            connection = conexion.conecta();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Usuario(Connection connection, int id_Usuario, String dni, String nombres, String apellidos, String telefono, String correo, String contraseña, byte[] imagen, Date fechaNacimiento, String sexo) {
        this.connection = connection;
        this.id_Usuario = id_Usuario;
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.correo = correo;
        this.contraseña = contraseña;
        this.imagen = imagen;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public int getId_Usuario() {
        return id_Usuario;
    }

    public void setId_Usuario(int id_Usuario) {
        this.id_Usuario = id_Usuario;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public boolean loginUsuario(String correo, String contraseña ){
        boolean existe = false;
        try {
            String sql = "{CALL ConsultarUsuario(?, ?)}";
            try (CallableStatement statement = connection.prepareCall(sql)) {
                statement.setString(1, correo);
                statement.setString(2, contraseña);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        if(resultSet.getInt("Cantidad") == 1){
                            existe = true;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return existe;
    }

    public Usuario obtenerInformacionUsuario(String correo) {
        Usuario usuario = null;
        try {
            String sql = "{CALL ObtenerUsuarioPorCorreo(?)}";
            try (CallableStatement statement = connection.prepareCall(sql)) {
                statement.setString(1, correo);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        usuario = new Usuario();
                        usuario.setId_Usuario(resultSet.getInt("id_Usuario"));
                        usuario.setNombres(resultSet.getString("nombres"));
                        usuario.setApellidos(resultSet.getString("apellidos"));
                        usuario.setCorreo(resultSet.getString("correo"));
                        usuario.setTelefono(resultSet.getString("telefono"));
                        usuario.setImagen(resultSet.getBytes("imagen"));
                        usuario.setFechaNacimiento(resultSet.getDate("FechaNacimiento"));
                        usuario.setSexo(resultSet.getString("sexo"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuario;
    }

    public boolean editarUsuario(int id, String nombres, String apellidos, String telefono, String correo) {
        boolean actualizado = false;
        try {
            // Llama al procedimiento almacenado
            String sql = "{CALL EditarUsuario(?, ?, ?, ?, ?)}";
            try (CallableStatement statement = connection.prepareCall(sql)) {
                // Configura los parámetros
                statement.setInt(1, id);
                statement.setString(2, nombres);
                statement.setString(3, apellidos);
                statement.setString(4, telefono);
                statement.setString(5, correo);

                // Ejecuta la actualización
                int filasActualizadas = statement.executeUpdate();
                actualizado = filasActualizadas > 0; // True si se actualizó al menos una fila
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return actualizado;
    }

    public boolean agregarUsuario(String dni, String nombres, String apellidos, String telefono, String correo, String contraseña,byte[] imagen, Date fechaNacimiento, String sexo ) {
        boolean exito = false;
        try {
            String sql = "{CALL AgregarUsuario(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
            try (CallableStatement statement = connection.prepareCall(sql)) {
                statement.setString(1, dni);
                statement.setString(2, nombres);
                statement.setString(3, apellidos);
                statement.setString(4, telefono);
                statement.setString(5, correo);
                statement.setString(6, contraseña);
                statement.setBytes(7, imagen);
                statement.setDate(8, new java.sql.Date(fechaNacimiento.getTime()));
                statement.setString(9, sexo);
                int filasInsertadas = statement.executeUpdate();
                exito = filasInsertadas > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exito;
    }


}
