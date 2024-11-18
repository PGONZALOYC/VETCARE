package com.example.vetcare.modelo;

import com.example.vetcare.clases.MySQLConnector;
import com.google.gson.annotations.Expose;

import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date; // Asegúrate de importar la clase Date para manejar fechas
import java.util.ArrayList;

public class Cita {
    @Expose(serialize = false)
    private Connection connection;
    @Expose
    private int idCita;
    @Expose
    private int idUsuario;
    @Expose
    private int idMascota;
    @Expose
    private int idSede;
    @Expose
    private String servicio;
    @Expose
    private int idVeterinario;
    @Expose
    private String sede;

    public String getMascota() {
        return mascota;
    }

    public void setMascota(String mascota) {
        this.mascota = mascota;
    }

    public String getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(String veterinario) {
        this.veterinario = veterinario;
    }

    private String mascota;
    private String veterinario;
    private String estado;
    private String horaInicio;
    private String horaFinal;
    private Date fecha;

    // Constructor vacío
    public Cita() {
        try {
            MySQLConnector conexion = new MySQLConnector();
            connection = conexion.conecta();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Constructor con parámetros, incluyendo la fecha


    public Cita(Connection connection, int idCita, int idUsuario, int idMascota, int idSede, String servicio, int idVeterinario, String sede, String estado, String horaInicio, String horaFinal, Date fecha) {
        this.connection = connection;
        this.idCita = idCita;
        this.idUsuario = idUsuario;
        this.idMascota = idMascota;
        this.idSede = idSede;
        this.servicio = servicio;
        this.idVeterinario = idVeterinario;
        this.sede = sede;
        this.estado = estado;
        this.horaInicio = horaInicio;
        this.horaFinal = horaFinal;
        this.fecha = fecha;
    }

    public Cita(int idUsuario, int idMascota, int idSede, int idVeterinario, String servicio, String horaInicio, Date fecha, String estado) {
        this.idUsuario = idUsuario;
        this.idMascota = idMascota;
        this.idSede = idSede;
        this.idVeterinario = idVeterinario;
        this.servicio = servicio;
        this.horaInicio = horaInicio;
        this.fecha = fecha;
        this.estado = estado;
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

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public int getIdSede() {
        return idSede;
    }

    public void setIdSede(int idSede) {
        this.idSede = idSede;
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


    public boolean agregarCita(Cita cita) {
        boolean creado = false;
        try {
            String sql = "{CALL InsertarCita(?, ?, ?, ?, ?, ?, ?, ?)}";
            try (CallableStatement statement = connection.prepareCall(sql)) {
                statement.setInt(1, cita.getIdUsuario());
                statement.setDate(2, cita.getFecha());
                statement.setInt(3, cita.getIdMascota());
                statement.setString(4, cita.getServicio());
                statement.setInt(5, cita.getIdVeterinario());
                statement.setString(6, cita.getEstado());
                statement.setInt(7, cita.getIdSede());
                switch (cita.getHoraInicio()){
                    case "9:00":
                        statement.setInt(8, 1);
                        break;
                    case "10:30":
                        statement.setInt(8, 2);
                        break;
                    case "12:00":
                        statement.setInt(8, 3);
                        break;
                    case "14:00":
                        statement.setInt(8, 4);
                        break;
                    case "15:30":
                        statement.setInt(8, 5);
                        break;
                    case "17:00":
                        statement.setInt(8, 6);
                        break;
                }
                // Ejecuta la actualización
                int filasActualizadas = statement.executeUpdate();
                creado = filasActualizadas > 0; // True si se actualizó al menos una fila
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return creado;
    }

    public ArrayList<Cita> obtenerCitas(){
        ArrayList<Cita> listaCitas = new ArrayList<>();
        Cita cita = null;
        try {
            String sql = "{CALL obtenerCitas()}";

            try (CallableStatement statement = connection.prepareCall(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        cita = new Cita();
                        cita.setIdCita(resultSet.getInt("id_Cita"));
                        cita.setIdUsuario(resultSet.getInt("id_Usuario"));
                        cita.setFecha(resultSet.getDate("Fecha"));
                        cita.setIdMascota(resultSet.getInt("id_Mascota"));
                        cita.setServicio(resultSet.getString("Servicio"));
                        cita.setIdVeterinario(resultSet.getInt("id_Veterinario"));
                        cita.setServicio(resultSet.getString("Estado"));
                        cita.setIdSede(resultSet.getInt("id_Sede"));
                        cita.setHoraInicio(resultSet.getString("horaInicio"));
                        cita.setHoraFinal(resultSet.getString("horaFinal"));
                        listaCitas.add(cita);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaCitas;
    }

    public ArrayList<Cita> obtenerCitasPorCorreo(String correo) {
        ArrayList<Cita> listaCitas = new ArrayList<>();
        Cita cita;

        try {
            // Llamada al procedimiento almacenado con el parámetro de correo
            String sql = "{CALL ObtenerCitasPorCorreo(?)}";  // Procedimiento con un parámetro

            try (CallableStatement statement = connection.prepareCall(sql)) {
                // Establecer el valor del parámetro de correo
                statement.setString(1, correo);

                // Ejecutar la consulta y obtener el ResultSet
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        cita = new Cita();

                        // Asignar los valores obtenidos del ResultSet a los atributos de la cita
                        cita.setIdCita(resultSet.getInt("id_Cita"));
                        cita.setFecha(resultSet.getDate("Fecha"));
                        cita.setServicio(resultSet.getString("Servicio"));
                        cita.setEstado(resultSet.getString("Estado"));
                        // Obtener los nombres directamente
                        cita.setMascota(resultSet.getString("NombreMascota"));
                        cita.setSede(resultSet.getString("NombreSede"));
                        // Concatenar nombre y apellido del veterinario
                        String nombreCompletoVeterinario = resultSet.getString("NombreVeterinario") + " " +
                                resultSet.getString("ApellidosVeterinario");
                        cita.setVeterinario(nombreCompletoVeterinario);


                        // Añadir la cita a la lista
                        listaCitas.add(cita);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Mostrar detalles del error en caso de excepciones
        }
        return listaCitas;  // Devolver la lista de citas
    }
}
