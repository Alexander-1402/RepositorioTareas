package com;

import java.util.HashMap;
import java.util.Map;

public class ClasificadorTareas {

    private Map<String, String> reglas = new HashMap<>();

    public ClasificadorTareas() {
        // Reglas FACIL
        reglas.put("resumen",      "FACIL");
        reglas.put("lectura",      "FACIL");
        reglas.put("2 páginas",    "FACIL");
        reglas.put("5 ejercicios", "FACIL");

        // Reglas DIFICIL
        reglas.put("proyecto final", "DIFICIL");
        reglas.put("500",            "DIFICIL");
        reglas.put("100",            "DIFICIL");

        // Reglas MEDIO
        reglas.put("50 ejercicios", "MEDIO");
        reglas.put("análisis",      "MEDIO");
    }

    public String clasificar(String titulo) {
        if (titulo == null || titulo.isEmpty()) {
            return "MEDIO";
        }
        String tituloLower = titulo.toLowerCase();
        for (Map.Entry<String, String> regla : reglas.entrySet()) {
            if (tituloLower.contains(regla.getKey())) {
                return regla.getValue();
            }
        }
        return "MEDIO";
    }

    public boolean esValido(String titulo) {
        return titulo != null && !titulo.isEmpty();
    }
}