package com.controller;

import com.db.EntregaDAO;
import com.model.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestorEntregas {

    // Caché por tarea — clave es el id de la tarea
    private Map<Integer, List<Entrega>> cache = new HashMap<>();

    // ── SUBIR ENTREGA — invalida caché de esa tarea ───
    public void subirEntrega(Alumno alumno, Tarea tarea, File archivo) {
        EntregaDAO.registrar(alumno.getId(), tarea.getId(), archivo.getAbsolutePath());
        cache.remove(tarea.getId()); // Invalida solo la tarea afectada
    }

    // ── OBTENER POR TAREA — usa caché ─────────────────
    public List<Entrega> obtenerPorTarea(Tarea tarea) {
        if (!cache.containsKey(tarea.getId())) {
            // No está en caché — consulta BD y guarda
            cache.put(tarea.getId(), EntregaDAO.obtenerPorTarea(tarea.getId()));
        }
        return cache.get(tarea.getId()); // Retorna instantáneo
    }

    // ── OBTENER TODAS ─────────────────────────────────
    public List<Entrega> obtenerEntregas() {
        return EntregaDAO.obtenerTodas();
    }

    // ── INVALIDAR MANUALMENTE ─────────────────────────
    public void invalidarCache() {
        cache.clear();
    }
}

