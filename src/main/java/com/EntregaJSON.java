package com;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class EntregaJSON {

    private static final String FILE = "entregas.json";
    private static final Gson gson = new Gson();

    public static void guardar(int entregaId, int nota, String comentario) {
        try {
            Map<Integer, DatosEntrega> datos = leer();

            datos.put(entregaId, new DatosEntrega(nota, comentario));

            Writer writer = new FileWriter(FILE);
            gson.toJson(datos, writer);
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DatosEntrega obtener(int entregaId) {
        Map<Integer, DatosEntrega> datos = leer();
        return datos.getOrDefault(entregaId, new DatosEntrega(0, ""));
    }

    private static Map<Integer, DatosEntrega> leer() {
        try {
            File file = new File(FILE);
            if (!file.exists()) return new HashMap<>();

            Reader reader = new FileReader(FILE);
            Type tipo = new TypeToken<Map<Integer, DatosEntrega>>(){}.getType();

            Map<Integer, DatosEntrega> datos = gson.fromJson(reader, tipo);
            reader.close();

            return datos != null ? datos : new HashMap<>();

        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

   
    public static class DatosEntrega {
        public int nota;
        public String comentario;

        public DatosEntrega(int nota, String comentario) {
            this.nota = nota;
            this.comentario = comentario;
        }
    }
    public static int obtenerNota(int entregaId) {
    return obtener(entregaId).nota;
}

public static String obtenerComentario(int entregaId) {
    return obtener(entregaId).comentario;
}

}
