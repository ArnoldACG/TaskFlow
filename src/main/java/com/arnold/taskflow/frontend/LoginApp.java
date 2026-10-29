package com.arnold.taskflow.frontend;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LoginApp extends Application {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #1a1a1a;");

        Label titleLabel = new Label("TaskFlow Login");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");

        TextField userInput = new TextField();
        userInput.setPromptText("Usuario");
        userInput.setStyle("-fx-background-color: #2b2b35; -fx-text-fill: white; -fx-prompt-text-fill: #888; -fx-padding: 10px;");

        PasswordField passInput = new PasswordField();
        passInput.setPromptText("Contraseña");
        passInput.setStyle("-fx-background-color: #2b2b35; -fx-text-fill: white; -fx-prompt-text-fill: #888; -fx-padding: 10px;");

        Button loginButton = new Button("Iniciar Sesión");
        loginButton.setStyle("-fx-background-color: #5a8cff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px;");
        loginButton.setMaxWidth(Double.MAX_VALUE);

        Button registerButton = new Button("Crear Cuenta");
        registerButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-underline: true;");

        loginButton.setOnAction(e -> {
            String username = userInput.getText();
            String password = passInput.getText();
            if (!username.isEmpty() && !password.isEmpty()) {
                performLogin(username, password, primaryStage);
            } else {
                showAlert("Error", "Por favor, llena todos los campos.");
            }
        });

        registerButton.setOnAction(e -> {
            String username = userInput.getText();
            String password = passInput.getText();
            if (!username.isEmpty() && !password.isEmpty()) {
                performRegister(username, password);
            } else {
                showAlert("Error", "Por favor, llena todos los campos para registrarte.");
            }
        });

        root.getChildren().addAll(titleLabel, userInput, passInput, loginButton, registerButton);

        Scene scene = new Scene(root, 350, 400);
        primaryStage.setTitle("TaskFlow - Iniciar Sesión");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void performLogin(String username, String password, Stage currentStage) {
        String json = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        String body = response.body();
                        int start = body.indexOf("\"token\":\"") + 9;
                        int end = body.indexOf("\"", start);
                        String token = body.substring(start, end);

                        Platform.runLater(() -> {
                            TaskApp.tokenGlobal = token;
                            currentStage.close();
                            try {
                                new TaskApp().start(new Stage());
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        });
                    } else {
                        Platform.runLater(() -> showAlert("Error", "Usuario o contraseña incorrectos."));
                    }
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> showAlert("Error de conexión", "No se pudo conectar con el servidor."));
                    return null;
                });
    }

    private void performRegister(String username, String password) {
        String json = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/auth/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        Platform.runLater(() -> showAlert("Éxito", "Usuario registrado. ¡Ahora puedes iniciar sesión!"));
                    } else {
                        Platform.runLater(() -> showAlert("Error", "El usuario ya existe o hubo un problema."));
                    }
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> showAlert("Error de conexión", "No se pudo conectar con el servidor."));
                    return null;
                });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}