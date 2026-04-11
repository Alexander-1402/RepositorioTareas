package com.controller;

import com.db.EntregaDAO;
import com.model.*;

import java.io.File;
import java.util.List;

public class GestorEntregas {

    public void subirEntrega(Alumno alumno, Tarea tarea, File archivo) {
        EntregaDAO.registrar(alumno.getId(), tarea.getId(), archivo.getAbsolutePath());
    }

    public List<Entrega> obtenerPorTarea(Tarea tarea) {
        return EntregaDAO.obtenerPorTarea(tarea.getId());
    }

    public List<Entrega> obtenerEntregas() {
        return EntregaDAO.obtenerTodas();
    }
}

