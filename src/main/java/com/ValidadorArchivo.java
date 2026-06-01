package com;

import java.util.Arrays;
import java.util.List;

public class ValidadorArchivo {

    private List<String> formatosPermitidos = Arrays.asList(
            "pdf", "docx", "zip", "png", "txt", "jpg", "xlsx"
    );

    public boolean subirArchivo(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            return false;
        }

        int punto = nombre.lastIndexOf(".");

        if (punto == -1) {
            return false;
        }

        String ext = nombre.substring(punto + 1).toLowerCase();

        return formatosPermitidos.contains(ext);
    }

    public boolean esFormatoValido(String nombre) {
        return subirArchivo(nombre);
    }

    public List<String> getFormatos() {
        return formatosPermitidos;
    }
}