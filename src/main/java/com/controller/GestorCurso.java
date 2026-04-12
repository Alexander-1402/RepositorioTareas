package com.controller;

import com.db.CursoDAO;
import com.db.RecursoDAO;
import com.db.TareaDAO;
import com.model.*;

import java.util.List;

public class GestorCurso {

    private int contadorCodigo = 1;

    public GestorCurso() {
        // Ajustar contador según cursos existentes en BD
        List<Curso> existentes = CursoDAO.listar();
        contadorCodigo = existentes.size() + 1;
    }

    public Curso crearCurso(String nombre, Profesor profesor) {
        String codigo = "C" + contadorCodigo++;
        Curso c = CursoDAO.crear(nombre, codigo);
        return c;
    }

    public void unirseClase(Alumno alumno, String codigo) {
        Curso c = CursoDAO.buscarPorCodigo(codigo);
        if (c == null) { System.out.println("Curso no encontrado"); return; }
        CursoDAO.inscribirAlumno(alumno.getId(), c.getId());
    }

    public List<Curso> obtenerClases(Alumno alumno) {
        List<Curso> cursos = CursoDAO.obtenerCursosDeAlumno(alumno.getId());
        // Cargar tareas de cada curso
        for (Curso c : cursos) {
            List<Tarea> tareas = TareaDAO.listarPorCurso(c.getId());
            for (Tarea t : tareas) c.agregarTarea(t);
        }
        return cursos;
    }

    public void abandonarClase(Alumno alumno, String nombreCurso) {
        for (Curso c : CursoDAO.listar()) {
            if (c.getNombre().equalsIgnoreCase(nombreCurso)) {
                CursoDAO.expulsarAlumno(alumno.getId(), c.getId());
                return;
            }
        }
    }

    public List<Curso> listarCursos() {
        List<Curso> cursos = CursoDAO.listar();
        for (Curso c : cursos) {
            List<Tarea> tareas = TareaDAO.listarPorCurso(c.getId());
            for (Tarea t : tareas) c.agregarTarea(t);
            List<Alumno> alumnos = CursoDAO.obtenerAlumnosDeCurso(c.getId());
            for (Alumno a : alumnos) c.agregarAlumno(a);
            List<Recurso> recursos = RecursoDAO.obtenerPorCurso(c.getId());
            for(Recurso r : recursos) c.agregarRecurso(r);
        }
        return cursos;
    }
}
