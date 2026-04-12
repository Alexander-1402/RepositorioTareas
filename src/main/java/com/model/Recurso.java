package com.model;

public class Recurso {
    public enum TipoRecurso { VIDEO, ARTICULO, REPOSITORIO, PDF }

    private int id;
    private String titulo;
    private TipoRecurso tipo;
    private String enlace;
    private String rutaArchivo;
    private int cursoId;

    public Recurso(String titulo, TipoRecurso tipo, String enlace, int cursoId) {
        this.titulo = titulo;
        this.tipo = tipo;
        this.enlace = enlace;
        this.cursoId = cursoId;
    }

    public Recurso(String titulo, String rutaArchivo, int cursoId) {
        this.titulo = titulo;
        this.tipo = TipoRecurso.PDF;
        this.rutaArchivo = rutaArchivo;
        this.cursoId = cursoId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public TipoRecurso getTipo() { return tipo; }
    public String getEnlace() { return enlace; }
    public String getRutaArchivo() { return rutaArchivo; }
    public int getCursoId() { return cursoId; }
}
