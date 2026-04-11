package com.controller;

import com.model.*;
import java.util.*;

public class GestorUsuarios {

    private List<Usuario> usuarios = new ArrayList<>();
    private int contadorId = 1;

  
    private boolean existeUsername(String username) {
        for (Usuario u : usuarios) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

  
    public Alumno registrarAlumno(String nombre, String username) {

        if (nombre.isEmpty() || username.isEmpty()) return null;

        if (existeUsername(username)) return null;

        Alumno a = new Alumno(contadorId++, nombre, username);
        usuarios.add(a);

        return a;
    }

  
    public Profesor registrarProfesor(String nombre, String username) {

        if (nombre.isEmpty() || username.isEmpty()) return null;

        if (existeUsername(username)) return null;

        Profesor p = new Profesor(contadorId++, nombre, username);
        usuarios.add(p);

        return p;
    }
}
