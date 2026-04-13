package com.model;

import java.util.ArrayList;
import java.util.List;

public class Curso {
    private int id;
    private String nombre;
    private Profesor profesor;
    private String codigo;

    private List<Alumno> alumnos = new ArrayList<>();
    private List<Tarea> tareas   = new ArrayList<>();
    private List<Recurso> recursos = new ArrayList<>();

    public Curso(String nombre, Profesor profesor, String codigo) {
        this.nombre   = nombre;
        this.profesor = profesor;
        this.codigo   = codigo;
    }
    public Curso(int id, String nombre, String codigo) {
        this.id = id;
        this.nombre = nombre;
        this.codigo = codigo;
    }

    public void agregarAlumno(Alumno a)   { alumnos.add(a);  }
    public void agregarTarea(Tarea t)     { tareas.add(t);   }
    public void agregarRecurso(Recurso r) { recursos.add(r); }

    public int getId()              { return id; }
    public void setId(int id)       { this.id = id; }
    public String getNombre()       { return nombre; }
    public String getCodigo()       { return codigo; }
    public List<Alumno> getAlumnos(){ return alumnos; }
    public List<Tarea> getTareas()  { return tareas; }
    public void setTareas(List<Tarea> tareas) { this.tareas = tareas; }
    public List<Recurso> getRecursos() { return recursos; }
    public void setRecursos(List<Recurso> recursos) { this.recursos = recursos; }


    @Override
    public String toString() { return nombre + " (" + codigo + ")"; }
}
