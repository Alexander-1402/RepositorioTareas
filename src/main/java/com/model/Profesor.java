package com.model;

import java.io.File;

public class Profesor extends Usuario {

    public Profesor(int id, String nombre, String username) {
        super(id, nombre, username);
    }

  
    public void crearTareaEnCurso(Curso curso, String titulo, File archivo) {
        Tarea tarea = new Tarea(titulo, archivo);
        curso.agregarTarea(tarea);
    }
}
