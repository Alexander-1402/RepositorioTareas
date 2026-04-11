package com;

import javafx.application.Application;
import javafx.stage.Stage;
import com.db.ConexionDB;
import com.view.MainUI;
import com.controller.RepositorioController;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        // Migración automática — crea tablas si no existen
        ConexionDB.inicializarTablas();

        RepositorioController repo = new RepositorioController();
        new MainUI(stage, repo);
    }

    public static void main(String[] args) {
        launch();
    }
}