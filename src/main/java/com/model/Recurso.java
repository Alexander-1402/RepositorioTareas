package com.model;

public class Recurso {
    public enum TipoRecurso { VIDEO, ARTICULO, REPOSITORIO, PDF }

    private int id;
    private String titulo;
    private TipoRecurso tipo;
    private String enlace;
    private int cursoId;

    public Recurso(String titulo, TipoRecurso tipo, String enlace, int cursoId) {
        this.titulo = titulo;
        this.tipo = tipo;
        this.enlace = enlace;
        this.cursoId = cursoId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public TipoRecurso getTipo() { return tipo; }
    public void setTipo(TipoRecurso tipo) { this.tipo = tipo; }

    public String getEnlace() { return enlace; }
    public void setEnlace(String enlace) { this.enlace = enlace; }

    public int getCursoId() { return cursoId; }
    public void setCursoId(int cursoId) { this.cursoId = cursoId; }
}
