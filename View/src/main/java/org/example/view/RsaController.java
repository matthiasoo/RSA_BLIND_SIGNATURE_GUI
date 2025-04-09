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
import org.example.model.*;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
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

    private RSA rsa;

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
        updateKeys();
        String str=this.messageTextArea.getText();
        BigInteger bi=rsa.podpisujSlepo(str);
        this.signatureTextArea.setText(bi.toString(16));
    }

    @FXML
    void verify(ActionEvent event) {
        updateKeys();
        String tekst_jawny=this.messageTextArea.getText();
        String podpis=this.signatureTextArea.getText();
        boolean state=this.rsa.weryfikujStringSlepo(tekst_jawny,podpis);
        try {
            MessageWindow.infoMessageWindow("Verify: "+state);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void fileChecker(ActionEvent event) {

    }

    void updateKeys(){
        this.rsa=new RSA(
                new BigInteger(this.keyEField.getText(),16),
                new BigInteger(this.keyGField.getText(),16),
                new BigInteger(this.keyKField.getText(),16),
                new BigInteger(this.keyModNField.getText(),16)
        );

    }

    @FXML
    void generateKeys(ActionEvent event) {
        this.rsa=new RSA();
        this.keyKField.setText(this.rsa.getK().toString(16));
        this.keyEField.setText(this.rsa.getE().toString(16));
        this.keyGField.setText(this.rsa.getD().toString(16));
        this.keyModNField.setText(this.rsa.getN().toString(16));
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

    private void setTextArea(TextArea ta, String str) {

    }
}
