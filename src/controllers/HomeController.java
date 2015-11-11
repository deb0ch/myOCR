package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import processing.classify.Classifier;
import processing.pre.LettersSplitter;

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
        VBox center = new VBox();
        center.getChildren().addAll(addOpenFileButton(), addOpenWordButton());
        root.setCenter(center);
    }

    private Button addOpenWordButton()
    {
        FileChooser fileChooser = addFileChooser();
        Button button = new Button("Open word img");
        button.setOnAction(event ->
        {
            selectedFile = fileChooser.showOpenDialog(null);
            event.consume();
            if (selectedFile != null)
            {
                root.getChildren().clear();
                VBox center = new VBox();
                HBox imgsBox = new HBox();
                center.getChildren().addAll(addReturnButton(), imgsBox);
                root.setCenter(center);
                new LettersSplitter(selectedFile, imgsBox);
            }
        });
        return button;
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
            this.initialize();
        });
        return button;
    }
}
