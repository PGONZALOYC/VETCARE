package com.example.vetcare.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Vetcare extends SQLiteOpenHelper {
    //Datos para el constructor
    private static final String nombreDB ="Vetcare.db";
    private static final int versionDB= 1;
    //Cadenas para DDL(lenguaje de definicion de datos) en SQLite
    private static final String createTableUsuario = "create table if not exists Usuarios(id integer, correo varchar(100), clave varchar(100), nombres varchar(100),apellidos varchar(100), dni varchar(8),fechaNacimiento varchar(10),telefono varchar(9),sexo varchar(50));";
    private static final String dropTableUsuario ="drop table if exists Usuarios;";

    public Vetcare(@Nullable Context context) {super(context, nombreDB, null, versionDB);}

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //aca se recomienda crear el esquema de SQLite
        sqLiteDatabase.execSQL(createTableUsuario);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //Primero limpiamos
        sqLiteDatabase.execSQL(dropTableUsuario);
        sqLiteDatabase.execSQL(createTableUsuario);
    }

    //Funcion create en sqlite
    public boolean agregarUsuario(int id, String correo, String clave,String nombres,String apellidos,String dni ,String fechaNacimiento ,String telefono ,String sexo){
        //Abrir la BD para funciones -> en este caso se usa para escritura = grabar
        SQLiteDatabase db = getWritableDatabase();
        if(db!= null){
            //Si se ha guardado
            String sentencia = "insert into Usuarios values("+id+",'"+correo+"','"+clave+"','"+nombres+"','"+apellidos+"','"+dni+"','"+fechaNacimiento+"','"+telefono+"','"+sexo+"');";
            //Ejecutamos la sentencia ademas de que no devuelve nada
            db.execSQL(sentencia);
            db.close();
            return true;
        }
        return false;
    }
    //Funcion para validar si el usuario ya se creo
    public boolean usuarioAgregado(){
        SQLiteDatabase db = getReadableDatabase();
        if(db!=null){
            String consulta = "Select * from Usuarios;";
            //Para obtener datos debemos guardarlos en un cursor
            //Al rawQuery se le pasa la consulta y el argumento pero en este caso no habra
            Cursor cursor = db.rawQuery(consulta, null);
            //Si el cursor tiene datos
            if(cursor.moveToNext())
                return true;
        }
        return false;
    }

    //funcion para actualizar un campo
    public boolean actualizarValue(String llave, String valor){
        SQLiteDatabase db = getWritableDatabase();
        if(db != null){
            String consulta = "Update Usuarios set "+llave+" = '"+valor+"';";
            db.execSQL(consulta);
            db.close();
            return true;
        }
        return false;
    }

    // Función para actualizar la contraseña de un usuario basado en el correo
    public boolean actualizarClave(String correo, String nuevaClave) {
        SQLiteDatabase db = getWritableDatabase();
        if(db != null){
            String consulta = "UPDATE Usuarios SET clave = '"+nuevaClave+"' WHERE correo = '"+correo+"';";
            db.execSQL(consulta);
            db.close();
            return true;
        }
        return false;
    }

    //Funcion para obtener data
    public String getValue(String value){
        SQLiteDatabase db = getReadableDatabase();
        if(db!=null){
            String consulta = "Select "+value+" from Usuarios;";
            Cursor cursor = db.rawQuery(consulta,null);
            //Si el cursor tiene datos
            if(cursor.moveToNext())
                //Retornamos el cursor en valor 0, es el primer valor
                return cursor.getString(0);
        }
        return null;
    }
    // Función para obtener todos los datos del usuario (con un solo usuario):
    public Cursor obtenerDatosUsuario() {
        SQLiteDatabase db = getReadableDatabase();
        // No necesitas un WHERE, pues siempre habrá un único usuario.
        String consulta = "SELECT correo, nombres, apellidos, telefono FROM Usuarios;";
        return db.rawQuery(consulta, null);
    }

    //Funcion para eliminar datos de la tabla Usuarios
    public boolean eliminarUsuario(){
        SQLiteDatabase db = getWritableDatabase();
        if(db != null){
            //Elimina la tabla OJO
            String consulta = "Drop Table Usuarios";
            db.execSQL(consulta);
            db.close();
            return true;
        }
        return false;
    }
}
