package com.model;

import java.io.File;
import com.controller.GestorEntregas;

public class Alumno extends Usuario {

    public Alumno(int id, String nombre, String username) {
        super(id, nombre, username);
    }

    public int getId() { return id; }

    public void subirEntrega(GestorEntregas gestor, Tarea tarea, File archivo) {
        gestor.subirEntrega(this, tarea, archivo);
    }
}