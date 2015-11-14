package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import processing.classify.Classifier;
import processing.pre.ImageManipulator;
import processing.pre.LettersSplitter;
import processing.pre.LinesSplitter;
import processing.pre.WordsSplitter;
import utils.ErrorHandling;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

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
                ScrollPane scrollPane = new ScrollPane(center);
                root.setCenter(scrollPane);
                Mat m = null;
                try
                {
                    m = Imgcodecs.imread(selectedFile.getCanonicalPath(), Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                }
                catch (IOException ioe)
                {
                    ErrorHandling.logAndExit(Level.SEVERE, ioe.getMessage());
                }
                if (m != null)
                {
                    //detach lines
                    LinesSplitter linesSplitter = new LinesSplitter(m, imgsBox);
                    VBox tmpBox = new VBox();
                    for (Mat line: linesSplitter.split())
                    {
                        WordsSplitter wordsSplitter = new WordsSplitter(line, tmpBox);
                        HBox tmpBox2 = new HBox();
                        for (Mat word: wordsSplitter.split())
                        {
                            LettersSplitter lettersSplitter = new LettersSplitter(word, tmpBox2);
                        }
                        tmpBox.getChildren().add(tmpBox2);
                    }
                    imgsBox.getChildren().add(tmpBox);
                }
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
