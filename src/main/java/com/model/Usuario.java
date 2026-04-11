package com.model;

public class Usuario {
    protected int id;
    protected String nombre;
    protected String username;

    public Usuario(int id, String nombre, String username) {
        this.id       = id;
        this.nombre   = nombre;
        this.username = username;
    }

    public int getId()          { return id; }
    public String getNombre()   { return nombre; }
    public String getUsername() { return username; }
}