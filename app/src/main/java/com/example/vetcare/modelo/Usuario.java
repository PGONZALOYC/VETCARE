package com.example.vetcare.modelo;

import com.example.vetcare.clases.MySQLConnector;

import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}
