package com.controller;

import com.db.EntregaDAO;
import com.model.*;

import java.io.File;
import java.util.List;

public class GestorEntregas {

    public Entrega subirEntrega(Alumno alumno, Tarea tarea, File archivo) {

   
    Entrega e = new Entrega(alumno, tarea, archivo);

   
    EntregaDAO.registrar(alumno.getId(), tarea.getId(), archivo.getAbsolutePath());

  
    return e;
}

    public List<Entrega> obtenerPorTarea(Tarea tarea) {
        return EntregaDAO.obtenerPorTarea(tarea.getId());
    }

    public List<Entrega> obtenerEntregas() {
        return EntregaDAO.obtenerTodas();
    }
    public void calificarEntrega(Entrega en, int nota, String comentario) {
    en.setNota(nota);
    en.setComentario(comentario);

    EntregaDAO.actualizarNota(en.getId(), nota, comentario);
}


}

