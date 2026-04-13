package com.view;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import com.controller.GestorIA;
import com.controller.RepositorioController;
import com.model.*;

import java.util.List;

public class MainUI {

    private String rolActual   = null;
    private String vistaActual = "inicio";

    public MainUI(Stage stage, RepositorioController repo) {
        stage.setScene(buildScene(stage, repo));
        stage.setTitle("TaskRepo");
        stage.setWidth(760);
        stage.setHeight(520);
        stage.show();
    }

    private Scene buildScene(Stage stage, RepositorioController repo) {
        VBox sidebar  = buildSidebar(stage, repo);
        HBox topbar   = buildTopbar(stage, repo);

        // Contenedor principal que empieza con spinner
        StackPane contenedorPrincipal = new StackPane();
        contenedorPrincipal.setStyle("-fx-background-color: #111111;");

        // Mostrar spinner mientras carga
        VBox spinner = buildSpinner();
        contenedorPrincipal.getChildren().add(spinner);

        // Cargar contenido en hilo secundario
        Task<VBox> tarea = new Task<>() {
            @Override
            protected VBox call() {
                // Esto corre en segundo plano — consultas a BD aquí
                return buildContenido(stage, repo);
            }
        };

        // Cuando termina, actualizar UI en hilo principal
        tarea.setOnSucceeded(e -> {
            VBox contenido = tarea.getValue();
            ScrollPane scroll = new ScrollPane(contenido);
            scroll.setFitToWidth(true);
            scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
            contenedorPrincipal.getChildren().clear();
            contenedorPrincipal.getChildren().add(scroll);
        });

        tarea.setOnFailed(e -> {
            Label error = new Label("Error al cargar datos");
            error.setStyle("-fx-text-fill: #f87171;");
            contenedorPrincipal.getChildren().clear();
            contenedorPrincipal.getChildren().add(error);
        });

        // Arrancar el hilo secundario
        new Thread(tarea).start();

        VBox mainArea = new VBox(topbar, contenedorPrincipal);
        VBox.setVgrow(contenedorPrincipal, Priority.ALWAYS);
        mainArea.setStyle("-fx-background-color: #111111;");

        HBox root = new HBox(sidebar, mainArea);
        HBox.setHgrow(mainArea, Priority.ALWAYS);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
        return scene;
    }

    // ── SPINNER DE CARGA ───────────────────────────────────────────────────────
    private VBox buildSpinner() {
        Label cargando = new Label("Cargando...");
        cargando.setStyle("-fx-text-fill: #555555; -fx-font-size: 13px;");

        // Círculo animado simple
        ProgressIndicator pi = new ProgressIndicator();
        pi.setStyle("-fx-progress-color: #666666;");
        pi.setPrefSize(36, 36);

        VBox box = new VBox(12, pi, cargando);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #111111;");
        return box;
    }

    // ── SIDEBAR ────────────────────────────────────────────────────────────────
    private VBox buildSidebar(Stage stage, RepositorioController repo) {
        Label logoTitle = new Label("TaskRepo");
        logoTitle.setStyle("-fx-text-fill: #a0a0a0; -fx-font-size: 15px; -fx-font-weight: bold;");
        Label logoSub = new Label(rolActual == null ? "Sistema de Tareas"
                : rolActual.equals("DOCENTE") ? "Panel Docente" : "Panel Estudiante");
        logoSub.setStyle("-fx-text-fill: #444444; -fx-font-size: 10px;");
        VBox logoBox = new VBox(2, logoTitle, logoSub);
        logoBox.setPadding(new Insets(18, 16, 14, 16));
        logoBox.setStyle("-fx-border-color: transparent transparent #252525 transparent; -fx-border-width: 1px;");

        Label lblGeneral = sectionLabel("GENERAL");
        Button btnInicio  = navBtn("◈  Inicio",      vistaActual.equals("inicio"));
        Button btnCursos  = navBtn("⊞  Ver Cursos",  vistaActual.equals("verCursos"));
        Button btnTareas  = navBtn("✦  Ver Tareas",  vistaActual.equals("verTareas"));

        btnInicio.setOnAction(e  -> { vistaActual = "inicio";    refresh(stage, repo); });
        btnCursos.setOnAction(e  -> { vistaActual = "verCursos"; refresh(stage, repo); });
        btnTareas.setOnAction(e  -> { vistaActual = "verTareas"; refresh(stage, repo); });

        VBox secGeneral = new VBox(2, lblGeneral, btnInicio, btnCursos, btnTareas);

        VBox secRol = new VBox(2);
        if (rolActual != null) {
            Label lblRol = sectionLabel(rolActual.equals("DOCENTE") ? "DOCENTE" : "ESTUDIANTE");
            secRol.getChildren().add(lblRol);

            if (rolActual.equals("DOCENTE")) {
                Button btnCrearCurso = navBtn("＋  Crear Curso",      vistaActual.equals("crearCurso"));
                Button btnAsignar    = navBtn("📋  Asignar Tarea",    vistaActual.equals("asignarTarea"));
                Button btnEntregas   = navBtn("↓  Ver Entregas",      vistaActual.equals("verEntregas"));
                Button btnAlumnos    = navBtn("◎  Gestionar Alumnos", vistaActual.equals("alumnos"));
                Button btnVincular   = navBtn("🔗  Vincular Recurso",    vistaActual.equals("vincularRecurso"));
                btnVincular.setOnAction(e -> { vistaActual = "vincularRecurso"; refresh(stage, repo); });
                btnCrearCurso.setOnAction(e -> { vistaActual = "crearCurso";   refresh(stage, repo); });
                btnAsignar.setOnAction(e    -> { vistaActual = "asignarTarea"; refresh(stage, repo); });
                btnEntregas.setOnAction(e   -> { vistaActual = "verEntregas";  refresh(stage, repo); });
                btnAlumnos.setOnAction(e    -> { vistaActual = "alumnos";      refresh(stage, repo); });
                secRol.getChildren().addAll(btnCrearCurso, btnAsignar, btnEntregas, btnVincular, btnAlumnos);
            } else {
                Button btnMisTareas = navBtn("✦  Mis Tareas",     vistaActual.equals("misTareas"));
                Button btnEntregar  = navBtn("↑  Entregar Tarea", vistaActual.equals("entregar"));
                Button btnUnirse    = navBtn("＋  Unirse a Clase", false);
                btnMisTareas.setOnAction(e -> { vistaActual = "misTareas"; refresh(stage, repo); });
                btnEntregar.setOnAction(e  -> { vistaActual = "entregar";  refresh(stage, repo); });
                btnUnirse.setOnAction(e    -> { accionUnirse(repo, stage); });
                // se creo el boton Contactar Docente para HU-03 - KATERINE
                Button btnMensajes = navBtn("✉  Contactar Docente", vistaActual.equals("mensajes"));
                btnMensajes.setOnAction(e -> { vistaActual = "mensajes"; refresh(stage, repo); });
                // se añadio btnMensajes al sidebar del estudiante - KATERINE
                secRol.getChildren().addAll(btnMisTareas, btnEntregar, btnUnirse, btnMensajes);
            }
        } else {
            Label ph = new Label("Selecciona un rol para\nver más opciones");
            ph.setStyle("-fx-text-fill: #333333; -fx-font-size: 11px; -fx-padding: 12 16 8 16;");
            ph.setWrapText(true);
            secRol.getChildren().add(ph);
        }

        VBox bottom = new VBox();
        if (rolActual != null) {
            Button btnCambiar = new Button("⇄  Cambiar rol");
            btnCambiar.setMaxWidth(Double.MAX_VALUE);
            btnCambiar.setAlignment(Pos.CENTER_LEFT);
            btnCambiar.setStyle("-fx-background-color: transparent; -fx-text-fill: #444444; -fx-font-size: 11px; -fx-padding: 10 16; -fx-cursor: hand; -fx-border-color: transparent; -fx-background-radius: 0;");
            btnCambiar.setOnAction(e -> { rolActual = null; vistaActual = "inicio"; refresh(stage, repo); });
            bottom.getChildren().add(btnCambiar);
        }
        bottom.setStyle("-fx-border-color: #252525 transparent transparent transparent; -fx-border-width: 1px;");

        VBox navItems = new VBox(secGeneral, new Region() {{ setMinHeight(8); }}, secRol);
        VBox.setVgrow(navItems, Priority.ALWAYS);

        VBox sidebar = new VBox(logoBox, navItems, bottom);
        sidebar.setPrefWidth(210);
        sidebar.setStyle("-fx-background-color: #1a1a1a;");
        return sidebar;
    }

    // ── TOPBAR ─────────────────────────────────────────────────────────────────
    private HBox buildTopbar(Stage stage, RepositorioController repo) {
        String titulo = switch (vistaActual) {
            case "verCursos"    -> "Ver Cursos";
            case "verTareas"    -> "Ver Tareas";
            case "crearCurso"   -> "Crear Curso";
            case "asignarTarea" -> "Asignar Tarea";
            case "verEntregas"  -> "Ver Entregas";
            case "alumnos"      -> "Gestionar Alumnos";
            case "misTareas"    -> "Mis Tareas Pendientes";
            case "entregar"     -> "Entregar Tarea";
            case "mensajes"     -> "Contactar Docente"; // se añadio titulo para HU-03 - KATERINE
            default             -> "Bienvenido a TaskRepo";
        };

        Label topTitle = new Label(titulo);
        topTitle.setStyle("-fx-text-fill: #e0e0e0; -fx-font-size: 14px; -fx-font-weight: bold;");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox topbar = new HBox(topTitle, spacer);
        topbar.setAlignment(Pos.CENTER_LEFT);

        if (vistaActual.equals("crearCurso") || vistaActual.equals("verCursos")) {
            Button btn = topBtn("+ Nuevo Curso");
            btn.setOnAction(e -> mostrarDialogoCurso(stage, repo));
            topbar.getChildren().add(btn);
        }

        topbar.setPadding(new Insets(13, 20, 13, 20));
        topbar.setStyle("-fx-background-color: #181818; -fx-border-color: transparent transparent #252525 transparent; -fx-border-width: 1px;");
        return topbar;
    }

    // ── CONTENIDO — corre en hilo secundario ───────────────────────────────────
    private VBox buildContenido(Stage stage, RepositorioController repo) {
        return switch (vistaActual) {
            case "verCursos"    -> buildVerCursos(repo);
            case "verTareas"    -> buildVerTareas(repo);
            case "crearCurso"   -> buildCrearCurso(stage, repo);
            case "asignarTarea" -> buildAsignarTarea(stage, repo);
            case "verEntregas"  -> buildVerEntregas(repo);
            case "alumnos"      -> buildAlumnos(stage, repo);
            case "misTareas"    -> buildMisTareas(repo);
            case "entregar"     -> buildEntregar(stage, repo);
            case "mensajes"     -> buildVistaMensajes(stage, repo); // se añadio vista mensajes HU-03 - KATERINE
            case "vincularRecurso" -> buildVincularRecurso(repo, stage);
            default             -> buildInicio(stage, repo);
        };
    }

    // ── INICIO ─────────────────────────────────────────────────────────────────
    private VBox buildInicio(Stage stage, RepositorioController repo) {
        Label sub = new Label("¿Qué deseas hacer hoy?");
        sub.setStyle("-fx-text-fill: #555555; -fx-font-size: 13px;");

        VBox cardDocente    = accionCard("📋", "Asignar Tarea",  "Crea cursos y gestiona\nlas tareas del grupo",  "DOCENTE");
        VBox cardEstudiante = accionCard("📤", "Entregar Tarea", "Únete a un curso y\nentrega tus trabajos", "ESTUDIANTE");

        cardDocente.setOnMouseClicked(e    -> { rolActual = "DOCENTE";    vistaActual = "crearCurso"; refresh(stage, repo); });
        cardEstudiante.setOnMouseClicked(e -> { rolActual = "ESTUDIANTE"; vistaActual = "misTareas";  refresh(stage, repo); });

        HBox cards = new HBox(20, cardDocente, cardEstudiante);
        cards.setAlignment(Pos.CENTER);

        VBox center = new VBox(16, sub, cards);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(60, 20, 20, 20));
        center.setStyle("-fx-background-color: #111111;");
        return center;
    }

    // ── VER CURSOS ─────────────────────────────────────────────────────────────
    private VBox buildVerCursos(RepositorioController repo) {
        VBox lista = new VBox(10);
        lista.setPadding(new Insets(20));
        List<Curso> cursos = repo.gestorCurso.listarCursos();
        if (cursos.isEmpty()) {
            lista.getChildren().add(emptyLabel("No hay cursos creados aún."));
        } else {
            for (Curso c : cursos) {
                Label nombre = new Label(c.getNombre());
                nombre.setStyle("-fx-text-fill: #e0e0e0; -fx-font-size: 13px; -fx-font-weight: bold;");
                Label meta = new Label("Código: " + c.getCodigo() + "  ·  " + c.getAlumnos().size() + " alumnos  ·  " + c.getTareas().size() + " tareas");
                meta.setStyle("-fx-text-fill: #555555; -fx-font-size: 11px;");
                Label badge = new Label(c.getCodigo());
                badge.setStyle("-fx-background-color: #2a2a2a; -fx-text-fill: #777777; -fx-font-size: 10px; -fx-padding: 3 10 3 10; -fx-background-radius: 20;");
                Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
                HBox card = new HBox(12, new VBox(3, nombre, meta), sp, badge);
                card.setAlignment(Pos.CENTER_LEFT);
                card.setPadding(new Insets(13, 16, 13, 16));
                card.setStyle("-fx-background-color: #1e1e1e; -fx-border-color: #2a2a2a; -fx-border-width: 1px; -fx-border-radius: 10; -fx-background-radius: 10;");
                lista.getChildren().add(card);
            }
        }
        lista.setStyle("-fx-background-color: #111111;");
        return lista;
    }

    // ── VER TAREAS ─────────────────────────────────────────────────────────────
    private VBox buildVerTareas(RepositorioController repo) {
        VBox lista = new VBox(10);
        lista.setPadding(new Insets(20));
        List<Curso> cursos = repo.gestorCurso.listarCursos();
        boolean hayTareas = false;
        for (Curso c : cursos) {
            if (!c.getTareas().isEmpty()) {
                Label cursoLabel = new Label(c.getNombre().toUpperCase());
                cursoLabel.setStyle("-fx-text-fill: #3a3a3a; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 8 0 4 0;");
                lista.getChildren().add(cursoLabel);
                for (Tarea t : c.getTareas()) {
                    lista.getChildren().add(tareaFila(t));
                    hayTareas = true;
                }
            }
        }
        if (!hayTareas) lista.getChildren().add(emptyLabel("No hay tareas creadas aún."));
        lista.setStyle("-fx-background-color: #111111;");
        return lista;
    }

    // ── VINCULAR RECURSO (docente) ────────────────────────────────────────────
    private VBox buildVincularRecurso(RepositorioController repo, Stage stage) {
        Label info = new Label("Vincula material de apoyo (Videos, PDFs o Enlaces) a tus cursos mediante URLs.");
        info.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px;");

        Label lC = fieldLbl("Curso");
        ComboBox<Curso> cursosBox = new ComboBox<>();
        cursosBox.getItems().addAll(repo.gestorCurso.listarCursos());
        cursosBox.setPromptText("Selecciona un curso");
        cursosBox.setMaxWidth(Double.MAX_VALUE);

        Label lT = fieldLbl("Título del recurso");
        TextField tituloField = new TextField();
        tituloField.setPromptText("Ej: Guía de estudio PDF");
        tituloField.setMaxWidth(Double.MAX_VALUE);

        Label lTipo = fieldLbl("Tipo de recurso");
        ComboBox<Recurso.TipoRecurso> tipoBox = new ComboBox<>();
        tipoBox.getItems().addAll(Recurso.TipoRecurso.values());
        tipoBox.setPromptText("Selecciona el tipo");
        tipoBox.setMaxWidth(Double.MAX_VALUE);

        Label lUrl = fieldLbl("Enlace / URL");
        TextField urlField = new TextField();
        urlField.setPromptText("https://mi-enlace.com/archivo.pdf o youtube.com/...");
        urlField.setMaxWidth(Double.MAX_VALUE);

        Label msg = new Label();
        Button vincularBtn = new Button("Vincular Recurso →");
        vincularBtn.setMaxWidth(Double.MAX_VALUE);
        vincularBtn.setPrefHeight(42);

        vincularBtn.setOnAction(e -> {
            Curso c = cursosBox.getValue();
            String tit = tituloField.getText().trim();
            Recurso.TipoRecurso tipo = tipoBox.getValue();
            String url = urlField.getText().trim();

            if (c == null || tit.isEmpty() || tipo == null || url.isEmpty()) {
                msg.setStyle("-fx-text-fill: #f87171; -fx-font-size: 12px;");
                msg.setText("⚠ Completa todos los campos");
                return;
            }
            repo.recursosController.agregarRecurso(c, tit, tipo, url);
            msg.setStyle("-fx-text-fill: #4ade80; -fx-font-size: 12px;");
            msg.setText("✓ Recurso vinculado correctamente");
            tituloField.clear();
            urlField.clear();
        });
        VBox card = new VBox(12, info, lC, cursosBox, lT, tituloField, lTipo, tipoBox, lUrl, urlField, msg, vincularBtn);
        card.setPadding(new Insets(20));
        card.setMaxWidth(420);
        card.setStyle("-fx-background-color: #1e1e1e; -fx-border-color: #2a2a2a; -fx-border-width: 1px; -fx-border-radius: 14; -fx-background-radius: 14;");

        VBox wrap = new VBox(card);
        wrap.setPadding(new Insets(20));
        wrap.setStyle("-fx-background-color: #111111;");
        return wrap;
    }

    // ── CREAR CURSO ────────────────────────────────────────────────────────────
    private VBox buildCrearCurso(Stage stage, RepositorioController repo) {
        Label info = new Label("Crea un nuevo curso para empezar a asignar tareas.");
        info.setStyle("-fx-text-fill: #555555; -fx-font-size: 12px;");

        Label lN = fieldLbl("Nombre del curso");
        TextField nombreField = new TextField();
        nombreField.setPromptText("Ej: Matemáticas I");
        nombreField.setMaxWidth(Double.MAX_VALUE);

        Label msg = new Label();
        Button crearBtn = new Button("Crear Curso →");
        crearBtn.setMaxWidth(Double.MAX_VALUE);
        crearBtn.setPrefHeight(42);
        crearBtn.setOnAction(e -> {
            if (nombreField.getText().isBlank()) {
                msg.setStyle("-fx-text-fill: #f87171; -fx-font-size: 12px;");
                msg.setText("⚠ Ingresa un nombre"); return;
            }
            Curso c = repo.gestorCurso.crearCurso(nombreField.getText(), null);
            msg.setStyle("-fx-text-fill: #4ade80; -fx-font-size: 12px;");
            msg.setText("✓ Curso creado — Código: " + c.getCodigo());
            nombreField.clear();
        });

        VBox card = new VBox(12, info, lN, nombreField, msg, crearBtn);
        card.setPadding(new Insets(20));
        card.setMaxWidth(420);
        card.setStyle("-fx-background-color: #1e1e1e; -fx-border-color: #2a2a2a; -fx-border-width: 1px; -fx-border-radius: 14; -fx-background-radius: 14;");

        VBox wrap = new VBox(card);
        wrap.setPadding(new Insets(20));
        wrap.setStyle("-fx-background-color: #111111;");
        return wrap;
    }

    // ── ASIGNAR TAREA ──────────────────────────────────────────────────────────
    private VBox buildAsignarTarea(Stage stage, RepositorioController repo) {
        Label lC = fieldLbl("Curso");
        ComboBox<Curso> cursosBox = new ComboBox<>();
        cursosBox.getItems().addAll(repo.gestorCurso.listarCursos());
        cursosBox.setPromptText("Selecciona un curso");
        cursosBox.setMaxWidth(Double.MAX_VALUE);

        Label lT = fieldLbl("Título de la tarea");
        TextField titulo = new TextField();
        titulo.setPromptText("Ej: Proyecto final de algoritmos");
        titulo.setMaxWidth(Double.MAX_VALUE);

        Label infoIA = new Label("🤖 La dificultad será asignada automáticamente por IA");
        infoIA.setStyle("-fx-text-fill: #555555; -fx-font-size: 11px; -fx-padding: 4 0;");

        Label msg = new Label();
        Button crearBtn = new Button("Asignar Tarea →");
        crearBtn.setMaxWidth(Double.MAX_VALUE);
        crearBtn.setPrefHeight(42);
        crearBtn.setOnAction(e -> {
            Curso c = cursosBox.getValue();
            if (c == null || titulo.getText().isBlank()) {
                msg.setStyle("-fx-text-fill: #f87171; -fx-font-size: 12px;");
                msg.setText("⚠ Completa todos los campos"); return;
            }
            msg.setStyle("-fx-text-fill: #fbbf24; -fx-font-size: 12px;");
            msg.setText("⏳ Clasificando con IA...");
            crearBtn.setDisable(true);
            String tituloTarea = titulo.getText();
            new Thread(() -> {
                Tarea.Dificultad dif = GestorIA.clasificar(tituloTarea);
                repo.tareasController.crearTarea(null, c, tituloTarea, null, dif);
                Platform.runLater(() -> {
                    String emoji = switch (dif) {
                        case FACIL   -> "🟢";
                        case MEDIO   -> "🟡";
                        case DIFICIL -> "🔴";
                    };
                    msg.setStyle("-fx-text-fill: #4ade80; -fx-font-size: 12px;");
                    msg.setText("✓ Tarea creada — IA clasificó como: " + emoji + " " + dif.toString());
                    titulo.clear();
                    cursosBox.getSelectionModel().clearSelection();
                    crearBtn.setDisable(false);
                });
            }).start();
        });

        VBox card = new VBox(12, lC, cursosBox, lT, titulo, infoIA, msg, crearBtn);
        card.setPadding(new Insets(20));
        card.setMaxWidth(420);
        card.setStyle("-fx-background-color: #1e1e1e; -fx-border-color: #2a2a2a; -fx-border-width: 1px; -fx-border-radius: 14; -fx-background-radius: 14;");

        VBox wrap = new VBox(card);
        wrap.setPadding(new Insets(20));
        wrap.setStyle("-fx-background-color: #111111;");
        return wrap;
    }

    // ── VER ENTREGAS ───────────────────────────────────────────────────────────
    private VBox buildVerEntregas(RepositorioController repo) {
        Label lC = fieldLbl("Curso");
        ComboBox<Curso> cursosBox = new ComboBox<>();
        cursosBox.getItems().addAll(repo.gestorCurso.listarCursos());
        cursosBox.setPromptText("Selecciona un curso");
        cursosBox.setMaxWidth(Double.MAX_VALUE);

        Label lT = fieldLbl("Tarea");
        ComboBox<Tarea> tareasBox = new ComboBox<>();
        tareasBox.setPromptText("Selecciona una tarea");
        tareasBox.setMaxWidth(Double.MAX_VALUE);
        cursosBox.setOnAction(e -> {
            Curso c = cursosBox.getValue();
            if (c != null) { tareasBox.getItems().clear(); tareasBox.getItems().addAll(c.getTareas()); }
        });

        VBox listaEntregas = new VBox(8);
        Button verBtn = new Button("Ver Entregas");
        verBtn.setMaxWidth(Double.MAX_VALUE);
        verBtn.setPrefHeight(42);
        verBtn.setOnAction(e -> {
            listaEntregas.getChildren().clear();
            Tarea t = tareasBox.getValue();
            if (t == null) return;
            List<Entrega> entregas = repo.gestorEntregas.obtenerPorTarea(t);
            if (entregas.isEmpty()) {
                listaEntregas.getChildren().add(emptyLabel("Sin entregas para esta tarea."));
            } else {
                for (Entrega en : entregas) {
                    Label alumnoLbl = new Label(en.getAlumno().getNombre());
                    alumnoLbl.setStyle("-fx-text-fill: #e0e0e0; -fx-font-size: 13px; -fx-font-weight: bold;");
                    Label archivoLbl = new Label("↳ " + en.getArchivo().getName());
                    archivoLbl.setStyle("-fx-text-fill: #555555; -fx-font-size: 11px;");
                    HBox row = new HBox(new VBox(2, alumnoLbl, archivoLbl));
                    row.setPadding(new Insets(10, 14, 10, 14));
                    row.setStyle("-fx-background-color: #1e1e1e; -fx-border-color: #2a2a2a; -fx-border-width: 1px; -fx-border-radius: 10; -fx-background-radius: 10;");
                    listaEntregas.getChildren().add(row);
                }
            }
        });

        VBox card = new VBox(12, lC, cursosBox, lT, tareasBox, verBtn, listaEntregas);
        card.setPadding(new Insets(20));
        card.setMaxWidth(460);
        card.setStyle("-fx-background-color: #1e1e1e; -fx-border-color: #2a2a2a; -fx-border-width: 1px; -fx-border-radius: 14; -fx-background-radius: 14;");

        VBox wrap = new VBox(card);
        wrap.setPadding(new Insets(20));
        wrap.setStyle("-fx-background-color: #111111;");
        return wrap;
    }

    // ── GESTIONAR ALUMNOS ──────────────────────────────────────────────────────
    private VBox buildAlumnos(Stage stage, RepositorioController repo) {
        Label lC = fieldLbl("Curso");
        ComboBox<Curso> cursosBox = new ComboBox<>();
        cursosBox.getItems().addAll(repo.gestorCurso.listarCursos());
        cursosBox.setPromptText("Selecciona un curso");
        cursosBox.setMaxWidth(Double.MAX_VALUE);

        VBox listaAlumnos = new VBox(8);
        cursosBox.setOnAction(e -> {
            listaAlumnos.getChildren().clear();
            Curso c = cursosBox.getValue();
            if (c == null) return;
            if (c.getAlumnos().isEmpty()) {
                listaAlumnos.getChildren().add(emptyLabel("No hay alumnos en este curso."));
            } else {
                for (Alumno a : c.getAlumnos()) {
                    Label nombre = new Label(a.getNombre());
                    nombre.setStyle("-fx-text-fill: #e0e0e0; -fx-font-size: 13px; -fx-font-weight: bold;");
                    Label user = new Label("@" + a.getUsername());
                    user.setStyle("-fx-text-fill: #555555; -fx-font-size: 11px;");
                    Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
                    Button expBtn = new Button("Expulsar");
                    expBtn.setStyle("-fx-background-color: rgba(239,68,68,0.1); -fx-text-fill: #f87171; -fx-font-size: 11px; -fx-padding: 4 12; -fx-background-radius: 8; -fx-border-color: transparent; -fx-cursor: hand;");
                    expBtn.setOnAction(ev -> { c.getAlumnos().remove(a); refresh(stage, repo); });
                    HBox row = new HBox(10, new VBox(2, nombre, user), sp, expBtn);
                    row.setAlignment(Pos.CENTER_LEFT);
                    row.setPadding(new Insets(10, 14, 10, 14));
                    row.setStyle("-fx-background-color: #1e1e1e; -fx-border-color: #2a2a2a; -fx-border-width: 1px; -fx-border-radius: 10; -fx-background-radius: 10;");
                    listaAlumnos.getChildren().add(row);
                }
            }
        });

        VBox card = new VBox(12, lC, cursosBox, listaAlumnos);
        card.setPadding(new Insets(20));
        card.setMaxWidth(460);
        card.setStyle("-fx-background-color: #1e1e1e; -fx-border-color: #2a2a2a; -fx-border-width: 1px; -fx-border-radius: 14; -fx-background-radius: 14;");

        VBox wrap = new VBox(card);
        wrap.setPadding(new Insets(20));
        wrap.setStyle("-fx-background-color: #111111;");
        return wrap;
    }

    // ── MIS TAREAS — HU-05 ─────────────────────────────────────────────────────
    private VBox buildMisTareas(RepositorioController repo) {
        java.util.List<Tarea> tareas = new java.util.ArrayList<>();
        for (Curso c : repo.gestorCurso.listarCursos()) tareas.addAll(c.getTareas());
        tareas.sort(java.util.Comparator.comparing(Tarea::getDificultad));

        long facil   = tareas.stream().filter(t -> t.getDificultad() == Tarea.Dificultad.FACIL).count();
        long medio   = tareas.stream().filter(t -> t.getDificultad() == Tarea.Dificultad.MEDIO).count();
        long dificil = tareas.stream().filter(t -> t.getDificultad() == Tarea.Dificultad.DIFICIL).count();

        HBox stats = new HBox(12,
                statCard("Total",   String.valueOf(tareas.size()), "#a0a0a0"),
                statCard("Fácil",   String.valueOf(facil),         "#4ade80"),
                statCard("Medio",   String.valueOf(medio),         "#fbbf24"),
                statCard("Difícil", String.valueOf(dificil),       "#f87171")
        );
        stats.setPadding(new Insets(20, 20, 8, 20));

        Label recomendacion = new Label();
        if (!tareas.isEmpty()) {
            if (facil > 0) recomendacion.setText("💡 Te recomendamos empezar por las tareas marcadas como Fácil");
            else if (medio > 0) recomendacion.setText("💡 Te recomendamos empezar por las tareas de dificultad Media");
            else recomendacion.setText("💪 Solo tienes tareas difíciles — ¡ánimo, empieza cuanto antes!");
        } else {
            recomendacion.setText("No tienes tareas aún. Únete a una clase primero.");
        }
        recomendacion.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px; -fx-background-color: #1e1e1e; -fx-padding: 10 14; -fx-background-radius: 8;");
        recomendacion.setWrapText(true);

        VBox recBox = new VBox(recomendacion);
        recBox.setPadding(new Insets(0, 20, 8, 20));

        VBox lista = new VBox(8);
        lista.setPadding(new Insets(4, 20, 20, 20));

        if (!tareas.isEmpty()) {
            boolean addF = false, addM = false, addD = false;
            for (Tarea t : tareas) {
                switch (t.getDificultad()) {
                    case FACIL   -> { if (!addF) { lista.getChildren().add(secLabel("FÁCIL"));   addF = true; } lista.getChildren().add(tareaFila(t)); }
                    case MEDIO   -> { if (!addM) { lista.getChildren().add(secLabel("MEDIO"));   addM = true; } lista.getChildren().add(tareaFila(t)); }
                    case DIFICIL -> { if (!addD) { lista.getChildren().add(secLabel("DIFÍCIL")); addD = true; } lista.getChildren().add(tareaFilaDificil(t)); }
                }
            }
        }

        VBox content = new VBox(stats, recBox, lista);
        content.setStyle("-fx-background-color: #111111;");
        return content;
    }

    // ── ENTREGAR TAREA ─────────────────────────────────────────────────────────
    private VBox buildEntregar(Stage stage, RepositorioController repo) {
        Label lNombre = fieldLbl("Tu nombre");
        TextField nombreField = new TextField();
        nombreField.setPromptText("Ej: Ana López");
        nombreField.setMaxWidth(Double.MAX_VALUE);

        Label lC = fieldLbl("Código del curso");
        TextField codigoField = new TextField();
        codigoField.setPromptText("Ej: C1");
        codigoField.setMaxWidth(Double.MAX_VALUE);

        Label lT = fieldLbl("Tarea");
        ComboBox<Tarea> tareasBox = new ComboBox<>();
        tareasBox.setPromptText("Primero ingresa el código del curso");
        tareasBox.setMaxWidth(Double.MAX_VALUE);

        Label msgBuscar = new Label();
        Button buscarBtn = new Button("Buscar curso");
        buscarBtn.setMaxWidth(Double.MAX_VALUE);
        buscarBtn.getStyleClass().add("button-ghost");
        buscarBtn.setOnAction(e -> {
            tareasBox.getItems().clear();
            String cod = codigoField.getText().trim();
            Curso encontrado = null;
            for (Curso c : repo.gestorCurso.listarCursos()) {
                if (c.getCodigo().equalsIgnoreCase(cod)) { encontrado = c; break; }
            }
            if (encontrado == null) {
                msgBuscar.setStyle("-fx-text-fill: #f87171; -fx-font-size: 11px;");
                msgBuscar.setText("⚠ Curso no encontrado");
            } else {
                tareasBox.getItems().addAll(encontrado.getTareas());
                msgBuscar.setStyle("-fx-text-fill: #4ade80; -fx-font-size: 11px;");
                msgBuscar.setText("✓ Curso: " + encontrado.getNombre());
            }
        });

        Label lA = fieldLbl("Archivo PDF");
        Button selBtn = new Button("Seleccionar PDF...");
        selBtn.setMaxWidth(Double.MAX_VALUE);
        selBtn.getStyleClass().add("button-ghost");
        Label archivoLabel = new Label("Ningún archivo seleccionado");
        archivoLabel.setStyle("-fx-text-fill: #555555; -fx-font-size: 11px;");
        final java.io.File[] archivoSel = new java.io.File[1];
        selBtn.setOnAction(e -> {
            javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
            fc.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            java.io.File f = fc.showOpenDialog(stage);
            if (f != null) {
                archivoSel[0] = f;
                archivoLabel.setText("✓ " + f.getName());
                archivoLabel.setStyle("-fx-text-fill: #4ade80; -fx-font-size: 11px;");
            }
        });

        Label msg = new Label();
        Button subirBtn = new Button("Entregar Tarea →");
        subirBtn.setMaxWidth(Double.MAX_VALUE);
        subirBtn.setPrefHeight(42);
        subirBtn.setOnAction(e -> {
            String nombre = nombreField.getText().trim();
            Tarea tarea   = tareasBox.getValue();
            if (nombre.isBlank() || tarea == null || archivoSel[0] == null) {
                msg.setStyle("-fx-text-fill: #f87171; -fx-font-size: 12px;");
                msg.setText("⚠ Completa todos los campos"); return;
            }
            Alumno alumno = new Alumno(System.identityHashCode(nombre), nombre, nombre.toLowerCase().replace(" ", "."));
            repo.gestorEntregas.subirEntrega(alumno, tarea, archivoSel[0]);
            msg.setStyle("-fx-text-fill: #4ade80; -fx-font-size: 12px;");
            msg.setText("✓ Tarea entregada correctamente");
        });

        VBox card = new VBox(12, lNombre, nombreField, lC, codigoField, buscarBtn, msgBuscar, lT, tareasBox, lA, selBtn, archivoLabel, msg, subirBtn);
        card.setPadding(new Insets(20));
        card.setMaxWidth(420);
        card.setStyle("-fx-background-color: #1e1e1e; -fx-border-color: #2a2a2a; -fx-border-width: 1px; -fx-border-radius: 14; -fx-background-radius: 14;");

        VBox wrap = new VBox(card);
        wrap.setPadding(new Insets(20));
        wrap.setStyle("-fx-background-color: #111111;");
        return wrap;
    }

    // ── CONTACTAR DOCENTE — HU-03 (estudiante) — metodo creado por KATERINE ──────
    private VBox buildVistaMensajes(Stage stage, RepositorioController repo) {
        VBox box = new VBox(16);
        box.setPadding(new Insets(24));
        box.setStyle("-fx-background-color: #111111;");

        // se añadio campo nombre para identificar al alumno - KATERINE
        Label lNombre = new Label("Tu nombre:");
        lNombre.setStyle("-fx-text-fill: #a0a0a0; -fx-font-size: 12px;");
        TextField nombreField = new TextField();
        nombreField.setPromptText("Ej: Juan Pérez");
        nombreField.setMaxWidth(420);

        // se añadio selector de curso - KATERINE
        Label lblCurso = new Label("Selecciona el curso:");
        lblCurso.setStyle("-fx-text-fill: #a0a0a0; -fx-font-size: 12px;");
        ComboBox<Curso> comboCurso = new ComboBox<>();
        comboCurso.getItems().addAll(repo.gestorCurso.listarCursos());
        comboCurso.setPromptText("Elige un curso...");
        comboCurso.setMaxWidth(420);

        // se añadio campo de texto para el mensaje - KATERINE
        Label lblMsg = new Label("Escribe tu mensaje:");
        lblMsg.setStyle("-fx-text-fill: #a0a0a0; -fx-font-size: 12px;");
        TextArea campoMensaje = new TextArea();
        campoMensaje.setPromptText("Escribe aquí tu consulta al docente...");
        campoMensaje.setPrefRowCount(4);
        campoMensaje.setWrapText(true);
        campoMensaje.setMaxWidth(420);

        // se añadio boton enviar - KATERINE
        Button btnEnviar = new Button("Enviar mensaje →");
        btnEnviar.setPrefHeight(42);
        btnEnviar.setMaxWidth(420);
        btnEnviar.setStyle("-fx-background-color: #2a2a2a; -fx-text-fill: #e0e0e0; -fx-border-color: #3a3a3a; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");

        Label lblStatus = new Label("");
        lblStatus.setStyle("-fx-font-size: 12px;");

        // se añadio historial de mensajes - KATERINE
        Label lblHistorial = new Label("Mensajes en este curso:");
        lblHistorial.setStyle("-fx-text-fill: #a0a0a0; -fx-font-size: 12px;");
        VBox historial = new VBox(8);

        comboCurso.setOnAction(e -> {
            historial.getChildren().clear();
            Curso c = comboCurso.getValue();
            if (c == null) return;
            List<com.model.Mensaje> msgs = repo.gestorMensajes.obtenerMensajesDeCurso(c.getId());
            if (msgs.isEmpty()) {
                Label empty = new Label("Sin mensajes aún.");
                empty.setStyle("-fx-text-fill: #444444; -fx-font-size: 12px;");
                historial.getChildren().add(empty);
            } else {
                String nombreEscrito = nombreField.getText().trim();
                for (com.model.Mensaje m : msgs) {
                    Alumno remitente = com.db.AlumnoDAO.buscarPorId(m.getAlumnoId());
                    String nombreRemitente = (remitente != null) ? remitente.getNombre() : "Alumno";
                    String prefijo = nombreRemitente.equalsIgnoreCase(nombreEscrito) ? "Tú" : nombreRemitente;
                    Label lm = new Label(prefijo + ": " + m.getContenido() + "\n" + m.getFecha());
                    lm.setStyle("-fx-text-fill: #888; -fx-font-size: 11px; -fx-padding: 6 10; -fx-background-color: #1e1e1e; -fx-background-radius: 6;");
                    lm.setWrapText(true);
                    lm.setMaxWidth(420);
                    historial.getChildren().add(lm);
                }
            }
        });

        // se añadio logica del boton enviar - KATERINE
        // se quito verificacion de inscripcion ya que no hay login real - KATERINE
        btnEnviar.setOnAction(e -> {
            Curso c = comboCurso.getValue();
            if (c == null) { lblStatus.setText("Selecciona un curso."); lblStatus.setStyle("-fx-text-fill: orange; -fx-font-size: 12px;"); return; }
            String txt = campoMensaje.getText().trim();
            if (txt.isEmpty()) { lblStatus.setText("El mensaje no puede estar vacío."); lblStatus.setStyle("-fx-text-fill: orange; -fx-font-size: 12px;"); return; }
            String nombre = nombreField.getText().trim();
            if (nombre.isEmpty()) { lblStatus.setText("Ingresa tu nombre."); lblStatus.setStyle("-fx-text-fill: orange; -fx-font-size: 12px;"); return; }
            // se agrego estas 2 lineas para obtener o crear el alumno - KATERINE
            Alumno alumno = com.db.AlumnoDAO.registrarORecuperar(nombre, nombre.toLowerCase().replace(" ", "."));
            com.model.Mensaje m = repo.gestorMensajes.enviarMensaje(alumno, c.getId(), txt);
            if (m != null) {
                // se cambio mensaje a confirmacion positiva - KATERINE
                lblStatus.setText("✓ Mensaje enviado correctamente.");
                lblStatus.setStyle("-fx-text-fill: #4ade80; -fx-font-size: 12px;");
                campoMensaje.clear();
                comboCurso.fireEvent(new javafx.event.ActionEvent());
            } else {
                lblStatus.setText("No se pudo enviar el mensaje, intenta de nuevo.");
                lblStatus.setStyle("-fx-text-fill: #f87171; -fx-font-size: 12px;");
            }
        });

        box.getChildren().addAll(lNombre, nombreField, lblCurso, comboCurso, lblMsg, campoMensaje, btnEnviar, lblStatus, lblHistorial, historial);
        return box;
    }

    // ── HELPERS ────────────────────────────────────────────────────────────────
    private void refresh(Stage stage, RepositorioController repo) {
        stage.setScene(buildScene(stage, repo));
    }

    private void mostrarDialogoCurso(Stage stage, RepositorioController repo) {
        TextInputDialog d = new TextInputDialog();
        d.setTitle("Crear Curso");
        d.setHeaderText("Nombre del nuevo curso");
        d.showAndWait().ifPresent(nombre -> {
            if (!nombre.isBlank()) {
                repo.gestorCurso.crearCurso(nombre, null);
                vistaActual = "verCursos";
                refresh(stage, repo);
            }
        });
    }

    private void accionUnirse(RepositorioController repo, Stage stage) {
        TextInputDialog d = new TextInputDialog();
        d.setTitle("Unirse a clase");
        d.setHeaderText("Ingresa el código de la clase (Ej: C1)");
        d.showAndWait().ifPresent(codigo -> {
            Alumno alumno = new Alumno(0, "Estudiante", "estudiante");
            repo.gestorCurso.unirseClase(alumno, codigo);
            refresh(stage, repo);
        });
    }

    private VBox accionCard(String emoji, String titulo, String desc, String rol) {
        Label icon      = new Label(emoji); icon.setStyle("-fx-font-size: 28px;");
        Label tituloLbl = new Label(titulo); tituloLbl.setStyle("-fx-text-fill: #e0e0e0; -fx-font-size: 14px; -fx-font-weight: bold;");
        Label descLbl   = new Label(desc);   descLbl.setStyle("-fx-text-fill: #555555; -fx-font-size: 11px; -fx-text-alignment: center;"); descLbl.setAlignment(Pos.CENTER); descLbl.setWrapText(true);
        String tagStyle = rol.equals("DOCENTE") ? "-fx-background-color: rgba(100,100,220,0.12); -fx-text-fill: #7777cc;" : "-fx-background-color: rgba(80,180,80,0.12); -fx-text-fill: #55aa55;";
        Label tag = new Label(rol.equals("DOCENTE") ? "Docente" : "Estudiante");
        tag.setStyle(tagStyle + " -fx-font-size: 10px; -fx-padding: 2 10 2 10; -fx-background-radius: 20;");
        VBox card = new VBox(10, icon, tituloLbl, descLbl, tag);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(175, 185);
        card.setPadding(new Insets(22, 16, 22, 16));
        card.setStyle("-fx-background-color: #1e1e1e; -fx-border-color: #2a2a2a; -fx-border-width: 1px; -fx-border-radius: 14; -fx-background-radius: 14; -fx-cursor: hand;");
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #252525; -fx-border-color: #444444; -fx-border-width: 1px; -fx-border-radius: 14; -fx-background-radius: 14; -fx-cursor: hand;"));
        card.setOnMouseExited(e  -> card.setStyle("-fx-background-color: #1e1e1e; -fx-border-color: #2a2a2a; -fx-border-width: 1px; -fx-border-radius: 14; -fx-background-radius: 14; -fx-cursor: hand;"));
        return card;
    }

    private HBox tareaFila(Tarea t) {
        Circle dot = new Circle(5);
        switch (t.getDificultad()) {
            case FACIL   -> dot.setFill(Color.web("#4ade80"));
            case MEDIO   -> dot.setFill(Color.web("#fbbf24"));
            case DIFICIL -> dot.setFill(Color.web("#f87171"));
        }
        Label nombre = new Label(t.getTitulo()); nombre.setStyle("-fx-text-fill: #e0e0e0; -fx-font-size: 13px; -fx-font-weight: bold;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        HBox row = new HBox(10, dot, nombre, sp, difBadge(t.getDificultad()));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(11, 14, 11, 14));
        row.setStyle("-fx-background-color: #1e1e1e; -fx-border-color: #2a2a2a; -fx-border-width: 1px; -fx-border-radius: 10; -fx-background-radius: 10;");
        return row;
    }

    private HBox tareaFilaDificil(Tarea t) {
        HBox row = tareaFila(t);
        row.setStyle("-fx-background-color: #1e1e1e; -fx-border-color: rgba(239,68,68,0.3); -fx-border-width: 1px; -fx-border-radius: 10; -fx-background-radius: 10;");
        return row;
    }

    private Label difBadge(Tarea.Dificultad d) {
        Label l = new Label();
        switch (d) {
            case FACIL   -> { l.setText("Fácil");   l.setStyle("-fx-background-color: rgba(74,222,128,0.12);  -fx-text-fill: #4ade80; -fx-font-size: 10px; -fx-padding: 3 10 3 10; -fx-background-radius: 20;"); }
            case MEDIO   -> { l.setText("Medio");   l.setStyle("-fx-background-color: rgba(251,191,36,0.12);  -fx-text-fill: #fbbf24; -fx-font-size: 10px; -fx-padding: 3 10 3 10; -fx-background-radius: 20;"); }
            case DIFICIL -> { l.setText("Difícil"); l.setStyle("-fx-background-color: rgba(248,113,113,0.12); -fx-text-fill: #f87171; -fx-font-size: 10px; -fx-padding: 3 10 3 10; -fx-background-radius: 20;"); }
        }
        return l;
    }

    private VBox statCard(String label, String value, String color) {
        Label lbl = new Label(label); lbl.setStyle("-fx-text-fill: #555555; -fx-font-size: 10px;");
        Label val = new Label(value); val.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 22px; -fx-font-weight: bold;");
        VBox c = new VBox(3, lbl, val);
        c.setPadding(new Insets(12, 14, 12, 14));
        c.setStyle("-fx-background-color: #1e1e1e; -fx-border-color: #2a2a2a; -fx-border-width: 1px; -fx-border-radius: 10; -fx-background-radius: 10;");
        HBox.setHgrow(c, Priority.ALWAYS);
        return c;
    }

    private Button navBtn(String text, boolean active) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setStyle(active
                ? "-fx-background-color: #222222; -fx-text-fill: #dddddd; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 9 16; -fx-cursor: hand; -fx-border-color: transparent transparent transparent #666666; -fx-border-width: 2px; -fx-background-radius: 0; -fx-border-radius: 0;"
                : "-fx-background-color: transparent; -fx-text-fill: #555555; -fx-font-size: 12px; -fx-padding: 9 16; -fx-cursor: hand; -fx-border-color: transparent; -fx-background-radius: 0; -fx-border-radius: 0;");
        return b;
    }

    private Button topBtn(String text) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: #2a2a2a; -fx-text-fill: #a0a0a0; -fx-font-size: 12px; -fx-padding: 6 14; -fx-border-color: #333333; -fx-border-width: 1px; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");
        return b;
    }

    private Label sectionLabel(String t) { Label l = new Label(t); l.setStyle("-fx-text-fill: #3a3a3a; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 10 16 4 16;"); return l; }
    private Label secLabel(String t)     { Label l = new Label(t); l.setStyle("-fx-text-fill: #444444; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 8 0 2 0;"); return l; }
    private Label fieldLbl(String t)     { Label l = new Label(t); l.setStyle("-fx-text-fill: #777777; -fx-font-size: 12px;"); return l; }
    private Label emptyLabel(String t)   { Label l = new Label(t); l.setStyle("-fx-text-fill: #444444; -fx-font-size: 12px; -fx-padding: 8 0;"); return l; }
}
