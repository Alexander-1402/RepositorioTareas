package com.controller;

import com.db.RecursoDAO;
import com.model.Curso;
import com.model.Recurso;
import java.util.List;

public class RecursosController {

    public void agregarRecurso(Curso curso, String titulo, Recurso.TipoRecurso tipo, String url) {
        Recurso nuevo = new Recurso(titulo, tipo, url, curso.getId());
        RecursoDAO.guardar(nuevo);
        curso.agregarRecurso(nuevo);
    }

    public List<Recurso> listarRecursosDeCurso(Curso curso) {
        return RecursoDAO.obtenerPorCurso(curso.getId());
    }
}
