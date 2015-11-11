package controllers;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import processing.classify.Classifier;

import java.io.File;

/**
 * Created by sal on 11/11/15.
 */
public class HomeController
{
    @FXML
    private BorderPane root;

    private File selectedFile;

    @FXML
    private void initialize()
    {
        root.setCenter(new HBox(addOpenFileButton()));
    }

    private FileChooser addFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open an Image");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All images", "*.jpg", "*.png", "*.jp2", "*.jpeg")
        );
        return fileChooser;
    }

    private Button addOpenFileButton()
    {
        FileChooser fileChooser = addFileChooser();
        Button button = new Button("Open file");
        button.setOnAction(event ->
            {
                selectedFile = fileChooser.showOpenDialog(null);
                event.consume();
                if (selectedFile != null)
                {
                    root.getChildren().clear();
                    HBox box = new HBox();
                    box.getChildren().add(addReturnButton());
                    root.setCenter(box);
                    Classifier c = new Classifier(selectedFile, box);
                    c.start();
                }
            });
        return button;
    }

    private Button addReturnButton()
    {
        Button button = new Button("Return");
        button.setOnAction(event ->
        {
            event.consume();
            root.getChildren().clear();
            root.setCenter(new HBox(addOpenFileButton()));
        });
        return button;
    }
}
