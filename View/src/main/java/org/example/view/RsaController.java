package org.example.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RsaController {

    @FXML
    private TextField keyEField;

    @FXML
    private TextField keyGField;

    @FXML
    private TextField keyKField;

    @FXML
    private Button keyLoader;

    @FXML
    private TextField keyModNField;

    @FXML
    private Button keySaver;

    @FXML
    private TextArea messageTextArea;

    @FXML
    private Button messageTextLoader;

    @FXML
    private Button messageTextSaver;

    @FXML
    private TextArea signatureTextArea;

    @FXML
    private Button signatureTextLoader;

    @FXML
    private Button signatureTextSaver;

    private double xxCord = 0;
    private double yyCord = 0;

    @FXML
    void close(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void minimize(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    void pressed(MouseEvent event) {
        xxCord = event.getSceneX();
        yyCord = event.getSceneY();
    }

    @FXML
    void dragged(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setX(event.getScreenX() - xxCord);
        stage.setY(event.getScreenY() - yyCord);
    }

    @FXML
    void showAuthors(ActionEvent event) throws IOException {
        MessageWindow.authorsWindow();
    }

    @FXML
    void sign(ActionEvent event) {

    }

    @FXML
    void verify(ActionEvent event) {

    }

    @FXML
    void fileChecker(ActionEvent event) {

    }

    @FXML
    void generateKeys(ActionEvent event) {

    }

    @FXML
    void loadFromFile(ActionEvent event) throws IOException {

    }

    @FXML
    void saveToFile(ActionEvent event) throws IOException {

    }

    @FXML
    void loadKeyFromFile(ActionEvent event) throws IOException {

    }

    @FXML
    void saveKeyToFile(ActionEvent event) throws IOException {

    }

    private String generateKey() {
        return "A";
    }

    private void setTextArea(TextArea ta, String str) {

    }
}
