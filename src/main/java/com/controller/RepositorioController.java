package com.controller;

public class RepositorioController {

    public GestorUsuarios gestorUsuarios;
    public GestorCurso gestorCurso;
    public GestorEntregas gestorEntregas;
    public TareasController tareasController;

    public RepositorioController() {
        gestorUsuarios = new GestorUsuarios();
        gestorCurso = new GestorCurso();
        gestorEntregas = new GestorEntregas();
        tareasController = new TareasController();
    }
}
