package com.example.vetcare.actividades.clases;

public class Mascota {
    private String nombre;
    private String tipoMascota;
    private int edad;
    private int fotoResId;

    //Constructor de prueba para el Item
    public Mascota(String nombre, int fotoResId) {
        this.nombre = nombre;
        this.fotoResId = fotoResId;
    }

    //Constructor para la clase
    public Mascota(int fotoResId, int edad, String tipoMascota, String nombre) {
        this.fotoResId = fotoResId;
        this.edad = edad;
        this.tipoMascota = tipoMascota;
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipoMascota() {
        return tipoMascota;
    }

    public int getEdad() {
        return edad;
    }
    public int getFotoResId() {
        return fotoResId;
    }

}
