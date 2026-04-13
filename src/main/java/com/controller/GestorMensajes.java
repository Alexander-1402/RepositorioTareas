package com.controller;

import com.db.CursoDAO;
import com.db.MensajeDAO;
import com.model.Alumno;
import com.model.Curso;
import com.model.Mensaje;

import java.util.List;

public class GestorMensajes {

    // HU-03: El estudiante puede comunicarse con el docente - KATERINE
    public Mensaje enviarMensaje(Alumno alumno, int cursoId, String contenido) {
        // se quito verificacion de inscripcion ya que no hay login real en el sistema - KATERINE
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
