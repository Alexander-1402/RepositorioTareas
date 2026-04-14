package com.controller;

import com.db.CursoDAO;
import com.db.TareaDAO;
import com.model.*;

import java.util.List;

public class GestorCurso {

    private int contadorCodigo = 1;
    private List<Curso> cache = null; // null = no cargado aún

    public GestorCurso() {
        // Ajustar contador según cursos existentes en BD
        List<Curso> existentes = CursoDAO.listar();
        contadorCodigo = existentes.size() + 1;
    }

    // ── LEER — usa caché ──────────────────────────────
    public List<Curso> listarCursos() {
        if (cache == null) {
            // Primera vez o después de un cambio — consulta BD
            cache = CursoDAO.listarConTareasYAlumnos();
        }
        return cache; // Retorna instantáneo si ya está cargado
    }

    // ── CREAR — invalida caché ────────────────────────
    public Curso crearCurso(String nombre, Profesor profesor) {
        String codigo = "C" + contadorCodigo++;
        Curso c = CursoDAO.crear(nombre, codigo);
        cache = null; // Fuerza recarga en próxima consulta
        return c;
    }

    // ── UNIRSE — invalida caché ───────────────────────
    public void unirseClase(Alumno alumno, String codigo) {
        Curso c = CursoDAO.buscarPorCodigo(codigo);
        if (c == null) { System.out.println("Curso no encontrado"); return; }
        CursoDAO.inscribirAlumno(alumno.getId(), c.getId());
        cache = null; // El curso ahora tiene un alumno nuevo
    }

    // ── OBTENER CLASES DE ALUMNO — usa caché ──────────
    public List<Curso> obtenerClases(Alumno alumno) {
        // Filtra desde el caché en lugar de ir a BD
        return listarCursos().stream()
                .filter(c -> c.getAlumnos().stream()
                        .anyMatch(a -> a.getId() == alumno.getId()))
                .collect(java.util.stream.Collectors.toList());
    }

    // ── ABANDONAR — invalida caché ────────────────────
    public void abandonarClase(Alumno alumno, String nombreCurso) {
        for (Curso c : CursoDAO.listar()) {
            if (c.getNombre().equalsIgnoreCase(nombreCurso)) {
                CursoDAO.expulsarAlumno(alumno.getId(), c.getId());
                cache = null; // El curso perdió un alumno
                return;
            }
        }
    }

    // ── INVALIDAR MANUALMENTE ─────────────────────────
    // Llama esto si otro componente modifica cursos
    public void invalidarCache() {
        cache = null;
    }
}
