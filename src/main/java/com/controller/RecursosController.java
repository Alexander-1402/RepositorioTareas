package com.controller;

import com.db.RecursoDAO;
import com.model.Curso;
import com.model.Recurso;
import java.io.File;
import java.util.List;

public class RecursosController {

    public void vincularEnlace(Curso curso, String titulo, Recurso.TipoRecurso tipo, String url) {
        Recurso nuevo = new Recurso(titulo, tipo, url, curso.getId());
        RecursoDAO.guardar(nuevo);
        curso.agregarRecurso(nuevo);
    }

    public void vincularArchivo(Curso curso, String titulo, File archivoPDF) {
        Recurso nuevo = new Recurso(titulo, archivoPDF.getAbsolutePath(), curso.getId());
        RecursoDAO.guardar(nuevo);
        curso.agregarRecurso(nuevo);
    }

    public List<Recurso> listarRecursosDeCurso(Curso curso) {
        return RecursoDAO.obtenerPorCurso(curso.getId());
    }
}

