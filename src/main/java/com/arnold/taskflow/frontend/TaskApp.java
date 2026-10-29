package com.arnold.taskflow.frontend;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TaskApp extends Application {

    // Variable global para guardar el token
    public static String tokenGlobal = "";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObservableList<TaskItem> tasks = FXCollections.observableArrayList();
    private FilteredList<TaskItem> filteredTasks;
    private String currentFilter = "TODAS";

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #1a1a1a;");

        Label titleLabel = new Label("TaskFlow Desktop");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        TextField searchInput = new TextField();
        searchInput.setPromptText("🔍 Buscar tarea...");
        searchInput.setStyle("-fx-background-color: #2b2b35; -fx-text-fill: white; -fx-prompt-text-fill: #888;");

        Button btnTodas = new Button("Todas");
        Button btnPendientes = new Button("Pendientes");
        Button btnCompletadas = new Button("Completadas");

        String styleBtn = "-fx-background-color: #3d3d4a; -fx-text-fill: white; -fx-background-radius: 5;";
        btnTodas.setStyle(styleBtn);
        btnPendientes.setStyle(styleBtn);
        btnCompletadas.setStyle(styleBtn);

        HBox filterBox = new HBox(10, searchInput, btnTodas, btnPendientes, btnCompletadas);
        filterBox.setAlignment(Pos.CENTER);

        filteredTasks = new FilteredList<>(tasks, p -> true);

        searchInput.textProperty().addListener((observable, oldValue, newValue) -> {
            aplicarFiltro(searchInput.getText(), currentFilter);
        });

        btnTodas.setOnAction(e -> { currentFilter = "TODAS"; aplicarFiltro(searchInput.getText(), currentFilter); });
        btnPendientes.setOnAction(e -> { currentFilter = "PENDIENTES"; aplicarFiltro(searchInput.getText(), currentFilter); });
        btnCompletadas.setOnAction(e -> { currentFilter = "COMPLETADAS"; aplicarFiltro(searchInput.getText(), currentFilter); });

        TextField taskInput = new TextField();
        taskInput.setPromptText("Escribe una nueva tarea...");
        taskInput.setStyle("-fx-background-color: #2b2b35; -fx-text-fill: white; -fx-prompt-text-fill: #888;");

        ComboBox<String> priorityBox = new ComboBox<>();
        priorityBox.getItems().addAll("ALTA", "MEDIA", "BAJA");
        priorityBox.setValue("MEDIA");
        priorityBox.setStyle("-fx-background-color: #2b2b35; -fx-text-fill: white; -fx-font-size: 13px;");
        priorityBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item);
                    setStyle("-fx-text-fill: white;");
                }
            }
        });

        Button addButton = new Button("Añadir");
        addButton.setStyle("-fx-background-color: #5a8cff; -fx-text-fill: white; -fx-font-weight: bold;");

        HBox inputBox = new HBox(10, taskInput, priorityBox, addButton);
        inputBox.setAlignment(Pos.CENTER);

        ListView<TaskItem> taskList = new ListView<>(filteredTasks);
        taskList.setStyle("-fx-background-color: #2b2b35; -fx-control-inner-background: #2b2b35; -fx-text-fill: white;");

        taskList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(TaskItem task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    ComboBox<String> statusBox = new ComboBox<>();
                    statusBox.getItems().addAll("PENDIENTE", "EN_PROCESO", "COMPLETADA");
                    statusBox.setValue(task.getStatus());
                    statusBox.setStyle("-fx-background-color: #3d3d4a; -fx-text-fill: white; -fx-font-size: 11px;");

                    if ("COMPLETADA".equals(task.getStatus())) {
                        statusBox.setDisable(true);
                    }

                    statusBox.setButtonCell(new ListCell<>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item != null) {
                                setText(item);
                                setStyle("-fx-text-fill: white;");
                            }
                        }
                    });
                    statusBox.setOnAction(e -> updateTaskStatus(task, statusBox.getValue()));

                    Label taskLabel = new Label(task.getTitle());
                    if ("COMPLETADA".equals(task.getStatus())) {
                        taskLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 14px; -fx-strikethrough: true;");
                    } else {
                        taskLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                    }

                    Label dateLabel = new Label("Creado: " + task.getCreatedAt());
                    dateLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 11px;");

                    Label priorityLabel = new Label(" [" + task.getPriority() + "] ");
                    String color = switch (task.getPriority()) {
                        case "ALTA" -> "-fx-text-fill: #ff4d4d; -fx-font-weight: bold;";
                        case "MEDIA" -> "-fx-text-fill: #ffb84d;";
                        default -> "-fx-text-fill: #4dff88;";
                    };
                    priorityLabel.setStyle(color + " -fx-font-size: 11px;");

                    Button deleteButton = new Button("X");
                    deleteButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
                    deleteButton.setOnAction(e -> deleteTask(task.getId()));

                    VBox textVBox = new VBox(2, taskLabel, new HBox(5, dateLabel, priorityLabel));
                    HBox hbox = new HBox(10, statusBox, textVBox, deleteButton);
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    HBox.setHgrow(textVBox, Priority.ALWAYS);

                    setGraphic(hbox);
                }
            }
        });

        addButton.setOnAction(e -> {
            String title = taskInput.getText();
            String priority = priorityBox.getValue();
            if (!title.isEmpty()) {
                sendTaskToBackend(title, priority, taskInput);
            }
        });

        loadTasksFromBackend();

        root.getChildren().addAll(titleLabel, filterBox, inputBox, taskList);

        Scene scene = new Scene(root, 750, 550);
        primaryStage.setTitle("TaskFlow - Gestor de Tareas");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void aplicarFiltro(String busqueda, String filtro) {
        filteredTasks.setPredicate(task -> {
            boolean coincideBusqueda = task.getTitle().toLowerCase().contains(busqueda.toLowerCase());
            boolean coincideBoton = switch (filtro) {
                case "PENDIENTES" -> "PENDIENTE".equals(task.getStatus()) || "EN_PROCESO".equals(task.getStatus());
                case "COMPLETADAS" -> "COMPLETADA".equals(task.getStatus());
                default -> true;
            };
            return coincideBusqueda && coincideBoton;
        });
    }

    private void sendTaskToBackend(String title, String priority, TextField taskInput) {
        String json = String.format("{\"title\": \"%s\", \"description\": \"\", \"status\": \"PENDIENTE\", \"priority\": \"%s\"}", title, priority);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/tasks"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenGlobal) // Enviamos el token
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        Platform.runLater(() -> {
                            taskInput.clear();
                            loadTasksFromBackend();
                        });
                    } else {
                        Platform.runLater(() -> mostrarAlerta("Error", "Código: " + response.statusCode()));
                    }
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> mostrarAlerta("Error de conexión", "No se pudo conectar."));
                    return null;
                });
    }

    private void deleteTask(Long id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/tasks/" + id))
                .header("Authorization", "Bearer " + tokenGlobal) // Enviamos el token
                .DELETE()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        Platform.runLater(() -> loadTasksFromBackend());
                    }
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> mostrarAlerta("Error", "No se pudo borrar la tarea."));
                    return null;
                });
    }

    private void updateTaskStatus(TaskItem task, String newStatus) {
        String json = String.format(
                "{\"title\": \"%s\", \"description\": \"%s\", \"status\": \"%s\", \"priority\": \"%s\"}",
                task.getTitle(), task.getDescription(), newStatus, task.getPriority()
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/tasks/" + task.getId()))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenGlobal) // Enviamos el token
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        Platform.runLater(() -> loadTasksFromBackend());
                    }
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> mostrarAlerta("Error", "No se pudo actualizar la tarea."));
                    return null;
                });
    }

    private void loadTasksFromBackend() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/tasks"))
                .header("Authorization", "Bearer " + tokenGlobal) // Enviamos el token
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        String body = response.body();
                        Platform.runLater(() -> {
                            tasks.clear();
                            if (!body.equals("[]")) {
                                String[] items = body.split("\\{");
                                for (int i = 1; i < items.length; i++) {
                                    String item = items[i];

                                    int idStart = item.indexOf("\"id\":") + 5;
                                    int idEnd = item.indexOf(",", idStart);
                                    Long id = Long.parseLong(item.substring(idStart, idEnd).trim());

                                    int titleStart = item.indexOf("\"title\":\"") + 9;
                                    int titleEnd = item.indexOf("\"", titleStart);
                                    String title = item.substring(titleStart, titleEnd);

                                    String priority = "MEDIA";
                                    int prioStart = item.indexOf("\"priority\":\"") + 12;
                                    if (prioStart > 11) {
                                        int prioEnd = item.indexOf("\"", prioStart);
                                        priority = item.substring(prioStart, prioEnd);
                                    }

                                    String status = "PENDIENTE";
                                    int statusStart = item.indexOf("\"status\":\"") + 10;
                                    if (statusStart > 9) {
                                        int statusEnd = item.indexOf("\"", statusStart);
                                        status = item.substring(statusStart, statusEnd);
                                    }

                                    String date = "";
                                    int dateStart = item.indexOf("\"createdAt\":\"") + 13;
                                    if (dateStart > 12) {
                                        int dateEnd = item.indexOf("\"", dateStart);
                                        date = item.substring(dateStart, dateEnd).replace("T", " ").substring(0, 16);
                                    }

                                    tasks.add(new TaskItem(id, title, "", status, priority, date));
                                }
                            }
                        });
                    }
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> mostrarAlerta("Error de conexión", "No se pudo cargar la lista."));
                    return null;
                });
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        LoginApp.main(args);
    }
}