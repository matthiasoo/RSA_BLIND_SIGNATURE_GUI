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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RsaController {

    @FXML
    private TextField keyEField;

    @FXML
    private TextField keyDField;

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

    private byte[] messageBytes = new byte[]{};
    private byte[] signatureBytes = new byte[]{};
    private boolean fileCheck = false;
    final int maxTextAreaLength = 1024;

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
    void sign(ActionEvent event) throws IOException {
        updateKeys();
        String str=this.messageTextArea.getText();

        if(str==null || str.isEmpty()){
            MessageWindow.errorMessageWindow("Empty message buffer");
            return;
        }

        this.messageBytes = str.getBytes();
        BigInteger bi=rsa.podpisujSlepo(str);
        this.signatureTextArea.setText(bi.toString(16));
        this.signatureBytes = bi.toString(16).getBytes();
    }

    @FXML
    void verify(ActionEvent event) throws IOException {
        updateKeys();
        String tekst_jawny=this.messageTextArea.getText();

        if(tekst_jawny==null || tekst_jawny.isEmpty()){
            MessageWindow.errorMessageWindow("Empty message buffer");
            return;
        }

        this.messageBytes = tekst_jawny.getBytes();
        String podpis=this.signatureTextArea.getText();

        if(podpis==null || podpis.isEmpty()){
            MessageWindow.errorMessageWindow("Empty signature buffer");
            return;
        }

        this.signatureBytes = podpis.getBytes();
        boolean state=this.rsa.weryfikujStringSlepo(tekst_jawny,podpis);
        try {
            MessageWindow.infoMessageWindow("Verify: "+state);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void fileChecker(ActionEvent event) {
        messageTextArea.setEditable(fileCheck);

        this.messageBytes = new byte[]{};
        setTextArea(messageTextArea, RSA.bytesToHexString(this.messageBytes));
        this.signatureBytes = new byte[]{};
        setTextArea(signatureTextArea, RSA.bytesToHexString(this.signatureBytes));

        this.fileCheck = !this.fileCheck;
    }

    //sprawdzenie czy klucz jest poprawny
    private boolean checkKey(String key) {
        String regex = "^[0-9a-fA-F]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(key);
        return !matcher.matches();
    }

    void updateKeys() throws IOException{
        String str;

        str=this.keyEField.getText();
        if(str==null || str.isEmpty()){
            MessageWindow.errorMessageWindow("Empty E key");
            return;
        }
        if(checkKey(str)){
            MessageWindow.errorMessageWindow("E key wrong format");
            return;
        }

        str=this.keyDField.getText();
        if(str==null || str.isEmpty()){
            MessageWindow.errorMessageWindow("Empty D key");
            return;
        }
        if(checkKey(str)){
            MessageWindow.errorMessageWindow("D key wrong format");
            return;
        }

        str=this.keyKField.getText();
        if(str==null || str.isEmpty()){
            MessageWindow.errorMessageWindow("Empty K factor");
            return;
        }
        if(checkKey(str)){
            MessageWindow.errorMessageWindow("K factor wrong format");
            return;
        }

        str=this.keyModNField.getText();
        if(str==null || str.isEmpty()){
            MessageWindow.errorMessageWindow("Empty N");
            return;
        }
        if(checkKey(str)){
            MessageWindow.errorMessageWindow("N wrong format");
            return;
        }

        this.rsa=new RSA(
                new BigInteger(this.keyEField.getText(),16),
                new BigInteger(this.keyDField.getText(),16),
                new BigInteger(this.keyKField.getText(),16),
                new BigInteger(this.keyModNField.getText(),16)
        );

    }

    @FXML
    void generateKeys(ActionEvent event) {
        this.rsa=new RSA();
        this.keyKField.setText(this.rsa.getK().toString(16));
        this.keyEField.setText(this.rsa.getE().toString(16));
        this.keyDField.setText(this.rsa.getD().toString(16));
        this.keyModNField.setText(this.rsa.getN().toString(16));
    }

    @FXML
    void loadFromFile(ActionEvent event) throws IOException {
        if (!fileCheck) {
            MessageWindow.errorMessageWindow("File checkbox not checked");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "Text Files", event.getSource() == messageTextLoader ? "*.*" : "*.rsa_sign"
        ));
        File file = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

        if (file != null) {
            try {
                if (event.getSource() == messageTextLoader) {
                    this.messageBytes = Files.readAllBytes(file.toPath());
                    String content = new String(this.messageBytes);
                    setTextArea(messageTextArea, content);
                } else if (event.getSource() == signatureTextLoader) {
                    this.signatureBytes = Files.readAllBytes(file.toPath());
                    String content = new String(this.signatureBytes);
                    setTextArea(signatureTextArea, content);

                }
            } catch (IOException e) {
                MessageWindow.errorMessageWindow(e.getMessage());
            }
        }
    }

    @FXML
    void saveToFile(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save to File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "Text Files", event.getSource() == messageTextSaver ? "*.*" : "*.rsa_sign"
        ));
        File file = fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());

        if (file != null) {
            try {
                if (event.getSource() == messageTextSaver) {
                    Files.write(file.toPath(), this.messageBytes);

                } else if (event.getSource() == signatureTextSaver) {
                    Files.write(file.toPath(), this.signatureBytes);
                }
            } catch (IOException e) {
                MessageWindow.errorMessageWindow(e.getMessage());
            }
        }
    }

    @FXML
    void loadKeyFromFile(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("RSA Keys", "*.rsa_key"));
        File file = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

        if (file != null) {
            try {
                List<String> keys = Files.readAllLines(file.toPath());

                if (keys.size() != 4) {
                    MessageWindow.errorMessageWindow("Incorrect number of keys in file");
                    return;
                }

                keyEField.setText(keys.get(0));
                keyDField.setText(keys.get(1));
                keyKField.setText(keys.get(2));
                keyModNField.setText(keys.get(3));
            } catch (IOException e) {
                MessageWindow.errorMessageWindow(e.getMessage());
            }
        }
    }

    @FXML
    void saveKeyToFile(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save to File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("RSA Keys", "*.rsa_key"));
        File file = fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());

        if (file != null) {
            try {
                List<String> keys = new ArrayList<>();

                keys.add(keyEField.getText());
                keys.add(keyDField.getText());
                keys.add(keyKField.getText());
                keys.add(keyModNField.getText());

                Files.write(file.toPath(), keys, StandardOpenOption.CREATE);
            } catch (IOException e) {
                MessageWindow.errorMessageWindow(e.getMessage());
            }
        }
    }

    private void setTextArea(TextArea ta, String str) {
        if (str == null) {
            return;
        }
        if (str.length() > this.maxTextAreaLength) {
            str = str.substring(0, this.maxTextAreaLength);
        }
        ta.setText(str);
    }
}
