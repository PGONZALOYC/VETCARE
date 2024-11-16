package com.example.vetcare.modelo;

import java.sql.Connection;

public class Horario {
    private Connection connection;
    private int id_Horario;
    private String horaInicio;
    private String horaFinal;
    private int id_Veterinario;
    private boolean disponible;
}
