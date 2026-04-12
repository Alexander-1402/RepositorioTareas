package com.controller;

import com.db.CursoDAO;
import com.db.MensajeDAO;
import com.model.Alumno;
import com.model.Curso;
import com.model.Mensaje;

import java.util.List;

public class GestorMensajes {

    // HU-03: El alumno debe estar inscrito en el curso para poder enviar - KATERINE
    public Mensaje enviarMensaje(Alumno alumno, int cursoId, String contenido) {
        // Verificar que el alumno este inscrito en el curso - KATERINE
        List<Curso> cursosDelAlumno = CursoDAO.obtenerCursosDeAlumno(alumno.getId());
        boolean inscrito = cursosDelAlumno.stream().anyMatch(c -> c.getId() == cursoId);

        if (!inscrito) {
            System.err.println("El alumno no está inscrito en este curso.");
            return null;
        }
        if (contenido == null || contenido.isBlank()) {
            System.err.println("El mensaje no puede estar vacío.");
            return null;
        }
        return MensajeDAO.enviar(alumno.getId(), cursoId, contenido);
    }

    // Obtener mensajes de un curso - KATERINE
    public List<Mensaje> obtenerMensajesDeCurso(int cursoId) {
        return MensajeDAO.listarPorCurso(cursoId);
    }

    // Marcar un mensaje como leido - KATERINE
    public void marcarMensajeLeido(int mensajeId) {
        MensajeDAO.marcarLeido(mensajeId);
    }
}
