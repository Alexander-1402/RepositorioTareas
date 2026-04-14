package com.controller;

import com.db.MensajeDAO;
import com.model.Alumno;
import com.model.Mensaje;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestorMensajes {

    // Caché por curso — clave es el id del curso
    private Map<Integer, List<Mensaje>> cache = new HashMap<>();

    // ── ENVIAR MENSAJE — invalida caché de ese curso ──
    // HU-03: El estudiante puede comunicarse con el docente - KATERINE
    public Mensaje enviarMensaje(Alumno alumno, int cursoId, String contenido) {
        // se quito verificacion de inscripcion ya que no hay login real - KATERINE
        if (contenido == null || contenido.isBlank()) {
            System.err.println("El mensaje no puede estar vacío.");
            return null;
        }
        Mensaje m = MensajeDAO.enviar(alumno.getId(), cursoId, contenido);
        cache.remove(cursoId); // Invalida solo el curso afectado
        return m;
    }

    // ── OBTENER MENSAJES — usa caché ──────────────────
    public List<Mensaje> obtenerMensajesDeCurso(int cursoId) {
        if (!cache.containsKey(cursoId)) {
            // No está en caché — consulta BD y guarda
            cache.put(cursoId, MensajeDAO.listarPorCurso(cursoId));
        }
        return cache.get(cursoId); // Retorna instantáneo
    }

    // ── MARCAR LEIDO — invalida caché del curso ───────
    public void marcarMensajeLeido(int mensajeId) {
        MensajeDAO.marcarLeido(mensajeId);
        cache.clear(); // No sabemos qué curso — limpia todo
    }

    // ── INVALIDAR MANUALMENTE ─────────────────────────
    public void invalidarCache() {
        cache.clear();
    }
}
