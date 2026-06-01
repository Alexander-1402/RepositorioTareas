package com;

import org.junit.Test;
import static org.junit.Assert.*;

public class ValidadorArchivoTest {

    @Test
    public void test1_aceptarPDF() {
        ValidadorArchivo v = new ValidadorArchivo();
        assertTrue(v.subirArchivo("tarea.pdf"));
    }

    @Test
    public void test2_aceptarDOCX() {
        ValidadorArchivo v = new ValidadorArchivo();
        assertTrue(v.subirArchivo("informe.docx"));
    }

    @Test
    public void test3_aceptarZIP() {
        ValidadorArchivo v = new ValidadorArchivo();
        assertTrue(v.subirArchivo("proyecto.zip"));
    }

    @Test
    public void test4_rechazarEXE() {
        ValidadorArchivo v = new ValidadorArchivo();
        assertFalse(v.subirArchivo("virus.exe"));
    }

    @Test
    public void test5_rechazarNombreVacio() {
        ValidadorArchivo v = new ValidadorArchivo();
        assertFalse(v.subirArchivo(""));
    }

    @Test
    public void test6_aceptarPNG() {
        ValidadorArchivo v = new ValidadorArchivo();
        assertTrue(v.subirArchivo("imagen.png"));
    }

    @Test
    public void test7_aceptarTXT() {
        ValidadorArchivo v = new ValidadorArchivo();
        assertTrue(v.subirArchivo("notas.txt"));
    }
}