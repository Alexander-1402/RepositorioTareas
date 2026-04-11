package com.controller;

import com.db.TareaDAO;
import com.model.*;

import java.io.File;
import java.util.List;

public class TareasController {

    public Tarea crearTarea(Profesor profesor, Curso curso, String titulo,
                            File archivo, Tarea.Dificultad dificultad) {
        return TareaDAO.crear(titulo, dificultad, curso.getId());
    }

    public List<Tarea> listarTareas(Curso curso) {
        return TareaDAO.listarPorCurso(curso.getId());
    }
}