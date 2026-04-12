package com.controller;

import com.model.Tarea;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GestorIA {

    // Tu API key de Groq
    private static final String API_KEY = System.getenv("GROQ_API_KEY");
    private static final String URL     = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODELO  = "llama3-8b-8192";

    /**
     * Recibe el título de una tarea y retorna su dificultad
     * clasificada automáticamente por IA.
     */
    public static Tarea.Dificultad clasificar(String tituloTarea) {
        try {
            // 1 — Construir el cuerpo JSON de la petición
            String cuerpoJson = """
                {
                    "model": "%s",
                    "messages": [
                        {
                            "role": "system",
                            "content": "Eres un clasificador de tareas académicas. Responde ÚNICAMENTE con una sola palabra: FACIL, MEDIO o DIFICIL según la complejidad de la tarea. No expliques nada, solo responde con la palabra."
                        },
                        {
                            "role": "user",
                            "content": "Clasifica esta tarea: %s"
                        }
                    ],
                    "max_tokens": 10,
                    "temperature": 0.1
                }
                """.formatted(MODELO, tituloTarea.replace("\"", "'"));

            // 2 — Crear el cliente HTTP y la petición
            HttpClient cliente = HttpClient.newHttpClient();

            HttpRequest peticion = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(cuerpoJson))
                    .build();

            // 3 — Enviar la petición y obtener respuesta
            HttpResponse<String> respuesta = cliente.send(
                    peticion,
                    HttpResponse.BodyHandlers.ofString()
            );

            // 4 — Parsear la respuesta JSON manualmente
            String cuerpoRespuesta = respuesta.body();
            System.out.println("Respuesta Groq: " + cuerpoRespuesta);

            // Extraer el contenido del mensaje de respuesta
            String contenido = extraerContenido(cuerpoRespuesta);
            System.out.println("Dificultad sugerida por IA: " + contenido);

            // 5 — Convertir a enum
            contenido = contenido.trim().toUpperCase();
            if (contenido.contains("FACIL") || contenido.contains("FÁCIL")) {
                return Tarea.Dificultad.FACIL;
            } else if (contenido.contains("DIFICIL") || contenido.contains("DIFÍCIL")) {
                return Tarea.Dificultad.DIFICIL;
            } else {
                return Tarea.Dificultad.MEDIO;
            }

        } catch (Exception e) {
            System.err.println("Error al clasificar con IA: " + e.getMessage());
            // Si hay error retorna MEDIO por defecto
            return Tarea.Dificultad.MEDIO;
        }
    }

    /**
     * Extrae el texto del campo "content" de la respuesta JSON de Groq.
     * Ejemplo de respuesta:
     * {"choices":[{"message":{"content":"FACIL"}}]}
     */
    private static String extraerContenido(String json) {
        try {
            // Buscamos "content":"VALOR"
            String clave = "\"content\":\"";
            int inicio = json.indexOf(clave);
            if (inicio == -1) return "MEDIO";
            inicio += clave.length();
            int fin = json.indexOf("\"", inicio);
            if (fin == -1) return "MEDIO";
            return json.substring(inicio, fin);
        } catch (Exception e) {
            return "MEDIO";
        }
    }
}
