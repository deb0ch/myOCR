package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.opencv.core.Mat;
import processing.classify.Classifier;
import processing.pre.ImageManipulator;
import processing.pre.LettersSplitter;
import processing.pre.LinesSplitter;
import processing.pre.WordsSplitter;

import java.io.File;
import java.io.IOException;

/**
 * Created by sal on 11/11/15.
 */
public class HomeController
{
    @FXML
    private BorderPane root;

    private File selectedFile;
    private String dataSetPath;
    private TextField colLimitField;
    private TextField rowLimitField;
    private Button classifyButton;
    private Button debugPreProcessButton;
    private Classifier classifier;

    @FXML
    private void initialize() throws IOException
    {
//        VBox center = new VBox();
//        Label rowLimitLabel = new Label("row limit");
//        Label colLimitLabel = new Label("col limit");
//        colLimitField = new TextField("0");
//        rowLimitField = new TextField("0");
//        HBox rowLimitBox = new HBox(rowLimitLabel, rowLimitField);
//        HBox colLimitBox = new HBox(colLimitLabel, colLimitField);
//        classifyButton = addClassifyButton();
//        classifyButton.setVisible(false);
//        debugPreProcessButton = addDebugPreProcessButton();
//        debugPreProcessButton.setVisible(false);
//        center.getChildren().addAll(addSelectTrainingSetDirectory(), classifyButton, debugPreProcessButton, rowLimitBox, colLimitBox);
//        root.setCenter(center);
        FXMLLoader chooseDatasetLoader = new FXMLLoader(getClass().getResource("./../views/chooseDataSet.fxml"));
        BorderPane chooseDataSet = chooseDatasetLoader.load();
        ChooseDataSetDirectoryController chooseDataSetDirectoryController = chooseDatasetLoader.getController();

        FXMLLoader loadingTrainingDataSetLoader = new FXMLLoader(getClass().getResource("../views/loadingTrainingDataSet.fxml"));
        BorderPane loadingTrainingDataSetBorderPane = loadingTrainingDataSetLoader.load();
        LoadingTrainingDataSetController loadingTrainingDataSetController = loadingTrainingDataSetLoader.getController();

        Button chooseDataSetDirectoryButton = chooseDataSetDirectoryController.getChooseDataSetDirectoryButton();
        chooseDataSetDirectoryButton.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
            File trainningSetDirectory = directoryChooser.showDialog(null);
            if (trainningSetDirectory != null)
            {
                root.setCenter(loadingTrainingDataSetBorderPane);
                classifier =
                        new Classifier(
                                loadingTrainingDataSetController.getLoadingTrainingSetProgressBar()
                                );
//                Platform.runLater(() -> classifier.train());
                new Thread(() -> {
                    classifier.buildDatasetAndTrain(trainningSetDirectory);
                }).start();
            }
        });
        root.setCenter(chooseDataSet);
    }



    private Button addDebugPreProcessButton()
    {
        FileChooser fileChooser = addFileChooser();
        Button button = new Button("Debug Preprocess");
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
                Mat m = ImageManipulator.loadGreyImage(selectedFile);
                //detach lines
                int colLimit = Integer.valueOf(colLimitField.getText());
                int rowLimit = Integer.valueOf(rowLimitField.getText());
                LinesSplitter linesSplitter = new LinesSplitter(m, imgsBox, colLimit, rowLimit);
                VBox tmpBox = new VBox();
                for (Mat line: linesSplitter.split())
                {
                    WordsSplitter wordsSplitter = new WordsSplitter(line, tmpBox, colLimit, rowLimit);
                    VBox tmpBox2 = new VBox();
                    for (Mat word: wordsSplitter.split())
                    {
                        LettersSplitter lettersSplitter = new LettersSplitter(word, tmpBox2, colLimit, rowLimit);
                    }
                    tmpBox.getChildren().add(tmpBox2);
                }
                imgsBox.getChildren().add(tmpBox);
            }
        });
        return button;
    }

    private FileChooser addFileChooser()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open an Image File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All images", "*.jpg", "*.png", "*.jp2", ".jpeg", ".pmg")
        );
        return fileChooser;
    }

    private Button addClassifyButton()
    {
        FileChooser fileChooser = addFileChooser();
        Button button = new Button("Classify");
        button.setOnAction(event ->
        {
            selectedFile = fileChooser.showOpenDialog(null);
            event.consume();
            if (selectedFile != null && dataSetPath != null && !dataSetPath.isEmpty())
            {
                Platform.runLater(() -> {
                    root.getChildren().clear();
                    HBox box = new HBox();
                    box.getChildren().addAll(addReturnButton(), new Label("Loading and training, please wait"));
                    root.setCenter(box);
//                    if (classifier == null)
//                        classifier = new Classifier(ImageManipulator.loadGreyImage(selectedFile), dataSetPath, box);
//                    classifier.classify();
                });
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
//            this.initialize();
        });
        return button;
    }
}
