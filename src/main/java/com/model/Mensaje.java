package com.model;

import java.time.LocalDateTime;

public class Mensaje {
    private int id;
    private int alumnoId;
    private int cursoId;
    private String contenido;
    private LocalDateTime fecha;
    private boolean leido;

    public Mensaje(int id, int alumnoId, int cursoId, String contenido, LocalDateTime fecha, boolean leido) {
        this.id        = id;
        this.alumnoId  = alumnoId;
        this.cursoId   = cursoId;
        this.contenido = contenido;
        this.fecha     = fecha;
        this.leido     = leido;
    }

    public int getId()                  { return id; }
    public int getAlumnoId()            { return alumnoId; }
    public int getCursoId()             { return cursoId; }
    public String getContenido()        { return contenido; }
    public LocalDateTime getFecha()     { return fecha; }
    public boolean isLeido()            { return leido; }
    public void setLeido(boolean leido) { this.leido = leido; }
}
