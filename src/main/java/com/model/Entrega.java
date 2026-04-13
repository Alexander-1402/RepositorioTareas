package com.model;

import java.io.File;

public class Entrega {
    private Alumno alumno;
    private Tarea tarea;
    private File archivo;

    private static int c= 1;

    private int nota;
    private String comentario;
     private int id;
    public Entrega(Alumno alumno, Tarea tarea, File archivo) {
        this.alumno = alumno;
        this.tarea = tarea;
        this.archivo = archivo;
        this.id=c++;
        this.nota = 0;
        this.comentario = "";
    }

    public Alumno getAlumno() { return alumno; }
    public Tarea getTarea() { return tarea; }
    public File getArchivo() { return archivo; }

    
    public int getNota() { return nota; }
    public void setNota(int nota) { this.nota = nota; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public int getId() { return id; }
public void setId(int id) { this.id = id; }
}
