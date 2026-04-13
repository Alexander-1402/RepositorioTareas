package com.model;

import java.io.File;

public class Tarea {

    public enum Dificultad { FACIL, MEDIO, DIFICIL }

    private int id;
    private int cursoId;
    private String titulo;
    private File archivo;
    private Dificultad dificultad;

    private static int contador = 1;

    public Tarea(String titulo, File archivo) {
    this.id = contador++; 
    this.titulo = titulo;
    this.archivo = archivo;
    this.dificultad = Dificultad.MEDIO;
}
    private String descripcion;

public String getDescripcion() { return descripcion; }
public void setDescripcion(String descripcion) { this.descripcion = descripcion; }


    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }
    public int getCursoId()                     { return cursoId; }
    public void setCursoId(int cursoId)         { this.cursoId = cursoId; }
    public String getTitulo()                   { return titulo; }
    public Dificultad getDificultad()           { return dificultad; }
    public void setDificultad(Dificultad d)     { this.dificultad = d; }

    @Override
    public String toString() { return titulo; }
}
