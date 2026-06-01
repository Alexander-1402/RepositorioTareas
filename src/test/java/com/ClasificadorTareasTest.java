package com;

import org.junit.Test;
import static org.junit.Assert.*;

public class ClasificadorTareasTest {

    @Test
    public void test1_pocosEjercicios_retornaFacil() {
        ClasificadorTareas c = new ClasificadorTareas();
        assertEquals("FACIL", c.clasificar("5 ejercicios de suma"));
    }

    @Test
    public void test2_muchosEjercicios_retornaDificil() {
        ClasificadorTareas c = new ClasificadorTareas();
        assertEquals("DIFICIL", c.clasificar("500 ejercicios de álgebra"));
    }

    @Test
    public void test3_proyectoFinal_retornaDificil() {
        ClasificadorTareas c = new ClasificadorTareas();
        assertEquals("DIFICIL", c.clasificar("Proyecto final de programación"));
    }

    @Test
    public void test4_resumenCorto_retornaFacil() {
        ClasificadorTareas c = new ClasificadorTareas();
        assertEquals("FACIL", c.clasificar("Resumen de 2 páginas"));
    }

    @Test
    public void test5_cantidadMedia_retornaMedio() {
        ClasificadorTareas c = new ClasificadorTareas();
        assertEquals("MEDIO", c.clasificar("50 ejercicios de cálculo"));
    }

    @Test
    public void test6_tituloVacio_retornaMedio() {
        ClasificadorTareas c = new ClasificadorTareas();
        assertEquals("MEDIO", c.clasificar(""));
    }

    @Test
    public void test7_tituloNulo_retornaMedio() {
        ClasificadorTareas c = new ClasificadorTareas();
        assertEquals("MEDIO", c.clasificar(null));
    }
}
