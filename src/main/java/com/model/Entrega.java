package com.model;

import java.io.File;

public class Entrega {
    private Alumno alumno;
    private Tarea tarea;
    private File archivo;

    public Entrega(Alumno alumno, Tarea tarea, File archivo) {
        this.alumno = alumno;
        this.tarea = tarea;
        this.archivo = archivo;
    }

    public Alumno getAlumno() { return alumno; }
    public Tarea getTarea() { return tarea; }
    public File getArchivo() { return archivo; }
}
