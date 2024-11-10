package com.example.vetcare.modelo;

import com.example.vetcare.clases.MySQLConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Producto {

    private Connection connection;

    private int id_Producto;
    private String Nombre;
    private double Precio;
    private int Cantidad;
    private byte[] imagen;
    private int id_Categoria;

    public Producto() {
        try {
            MySQLConnector conexion = new MySQLConnector();
            connection = conexion.conecta();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Producto(Connection connection, int id_Producto, String Nombre, double Precio, int Cantidad, byte[] imagen, int id_Categoria) {
        this.connection = connection;
        this.id_Producto = id_Producto;
        this.Nombre = Nombre;
        this.Precio = Precio;
        this.Cantidad = Cantidad;
        this.imagen = imagen;
        this.id_Categoria = id_Categoria;
    }

    public int getId_Categoria() {
        return id_Categoria;
    }

    public void setId_Categoria(int id_Categoria) {
        this.id_Categoria = id_Categoria;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public int getId_Producto() {
        return id_Producto;
    }

    public void setId_Producto(int id_Producto) {
        this.id_Producto = id_Producto;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

    public double getPrecio() {
        return Precio;
    }

    public void setPrecio(double Precio) {
        this.Precio = Precio;
    }

    public int getCantidad() {
        return Cantidad;
    }

    public void setCantidad(int Cantidad) {
        this.Cantidad = Cantidad;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public void agregarProducto(Producto producto) {
        try {
            String sql = "{CALL InsertarProducto(?, ?, ?, ?, ?)}";
            try (CallableStatement statement = connection.prepareCall(sql)) {
                statement.setString(1, producto.getNombre());
                statement.setDouble(2, producto.getPrecio());
                statement.setInt(3, producto.getCantidad());
                Blob blob = connection.createBlob();
                blob.setBytes(1, producto.getImagen());
                statement.setBlob(4, blob);
                statement.setInt(5, producto.getId_Categoria());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Producto obtenerProducto(int id_Producto) {
        Producto producto = null;
        try {
            String sql = "{CALL ObtenerProducto(?)}";
            try (CallableStatement statement = connection.prepareCall(sql)) {
                statement.setInt(1, id_Producto);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        producto = new Producto();
                        producto.setId_Producto(resultSet.getInt("id_Producto"));
                        producto.setNombre(resultSet.getString("Nombre"));
                        producto.setPrecio(resultSet.getDouble("Precio"));
                        producto.setCantidad(resultSet.getInt("Cantidad"));
                        Blob blob = resultSet.getBlob("Imagen");
                        producto.setImagen(blob.getBytes(1, (int) blob.length()));
                        producto.setId_Categoria(resultSet.getInt("id_Categoria"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return producto;
    }

    public List<Integer> obtenerIdsProducto(){
        List<Integer> ids = new ArrayList<>();
        try {
            String sql = "{CALL ObtenerIds()}";
            try (CallableStatement statement = connection.prepareCall(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ids.add(resultSet.getInt("id_Producto"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }
    public List<Producto> obtenerProductos(int id_Categoria) {
        List<Producto> listaProductos = new ArrayList<>();
        Producto producto = null;
        try {
            String sql = "{CALL ObtenerProductosPorCateg(?)}";

            try (CallableStatement statement = connection.prepareCall(sql)) {
                statement.setInt(1, id_Categoria);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        producto = new Producto();
                        producto.setId_Producto(resultSet.getInt("id_Producto"));
                        producto.setNombre(resultSet.getString("Nombre"));
                        producto.setPrecio(resultSet.getDouble("Precio"));
                        producto.setCantidad(resultSet.getInt("Cantidad"));
                        Blob blob = resultSet.getBlob("Imagen");
                        producto.setImagen(blob.getBytes(1, (int) blob.length()));
                        producto.setId_Categoria(resultSet.getInt("id_Categoria"));
                        listaProductos.add(producto);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaProductos;
    }

}


