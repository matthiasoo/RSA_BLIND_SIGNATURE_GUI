package org.example.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MessageWindow {
    static void errorMessageWindow(String errorMessage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MessageWindow.class.getResource("/message-layout.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 350);
        scene.setFill(Color.TRANSPARENT);

        MessageWindowController controller = fxmlLoader.getController();
        controller.setTitle("Error");
        controller.setMessage(errorMessage);
        controller.getMessageLabel().setStyle("-fx-text-fill: red;");

        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        Image icon = new Image("icons/error_icon.png");
        stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.show();
    }

    static void authorsWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MessageWindow.class.getResource("/message-layout.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 350);
        scene.setFill(Color.TRANSPARENT);

        String authors = "Maciej Miazek\nKacper Błaszczyk";

        MessageWindowController controller = fxmlLoader.getController();
        controller.setTitle("Authors");
        controller.setMessage(authors);

        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        Image icon = new Image("icons/info_icon.png");
        stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.show();
    }
}