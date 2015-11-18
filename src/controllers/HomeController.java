package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import processing.classify.Classifier;

import java.io.File;
import java.io.IOException;

/**
 * Created by sal on 11/11/15.
 */
public class HomeController
{
    @FXML
    private BorderPane root;

    private Classifier classifier;
    private File dSD;

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
        String workingDirectory = System.getProperty("user.dir");

        FXMLLoader cDSDLoader = new FXMLLoader(getClass().getResource("../views/cDSD.fxml"));
        BorderPane cDSDBorderPane = cDSDLoader.load();
        CDSDController cDSDController = cDSDLoader.getController();

        FXMLLoader tDSLoader = new FXMLLoader(getClass().getResource("../views/loadingView.fxml"));
        BorderPane tDSBorderPane = tDSLoader.load();
        LoadingController tDSController = tDSLoader.getController();

        FXMLLoader processLoader = new FXMLLoader(getClass().getResource("../views/process.fxml"));
        BorderPane processBP = processLoader.load();
        ProcessController processController = processLoader.getController();

        tDSController.nextButton.setDisable(true);
        tDSController.saveButton.setDisable(true);
        tDSController.saveButton
                .setOnAction(event ->
                {
                    classifier.save(workingDirectory);
                    tDSController.nextButton.setDisable(false);
                });

        tDSController.nextButton.setOnAction(event ->
        {
            root.setCenter(processBP);
        });

        File savedSamplesFile =
                new File(String.format("%s%s%s",
                        workingDirectory,
                        System.getProperty("file.separator"),
                        Classifier.sampleFileName));
        File savedResponsesFile =
                new File(String.format("%s%s%s",
                        workingDirectory,
                        System.getProperty("file.separator"),
                        Classifier.responseFileName));
        classifier =
                new Classifier(tDSController.generalProgressBar,
                        tDSController.detailsProgressBar,
                        tDSController.generalLabel,
                        tDSController.detailsLabel);
        cDSDController.trainButton.setDisable(true);
        cDSDController.cDSDButton.setOnAction(event ->
        {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
            setdSD(directoryChooser.showDialog(null));
            cDSDController.trainButton.setDisable(dSD == null);
        });
        if (savedResponsesFile.exists() && savedSamplesFile.exists())
        {
            classifier.loadPreviousDataSet(workingDirectory);
            cDSDController.trainButton.setDisable(false);
        } else
        {
            cDSDController.cDSDButton.fire();
        }

        cDSDController
                .trainButton
                .setOnAction(event ->
                {
                    root.setCenter(tDSBorderPane);
                    tDSController
                            .generalProgressBar
                            .progressProperty()
                            .addListener((observable, oldValue, newValue) ->
                            {
                                if (newValue.floatValue() >= 1f)
                                {
                                    if (savedResponsesFile.exists() && savedSamplesFile.exists())
                                        tDSController.nextButton.setDisable(false);
                                    else
                                        tDSController.saveButton.setDisable(false);
                                }
                            });
                    new Thread(() ->
                    {
                        if (dSD != null)
                            classifier.buildDataSet(dSD, cDSDController.ratioSlider.getValue());
                        else
                            classifier.doTrain();
                    }).start();
                });
        root.setCenter(cDSDBorderPane);
    }



//    private Button addDebugPreProcessButton()
//    {
//        FileChooser fileChooser = addFileChooser();
//        Button button = new Button("Debug Preprocess");
//        button.setOnAction(event ->
//        {
//            selectedFile = fileChooser.showOpenDialog(null);
//            event.consume();
//            if (selectedFile != null)
//            {
//                root.getChildren().clear();
//                VBox center = new VBox();
//                HBox imgsBox = new HBox();
//                center.getChildren().addAll(addReturnButton(), imgsBox);
//                ScrollPane scrollPane = new ScrollPane(center);
//                root.setCenter(scrollPane);
//                Mat m = ImageManipulator.loadGreyImage(selectedFile);
//                //detach lines
//                int colLimit = Integer.valueOf(colLimitField.getText());
//                int rowLimit = Integer.valueOf(rowLimitField.getText());
//                LinesSplitter linesSplitter = new LinesSplitter(m, imgsBox, colLimit, rowLimit);
//                VBox tmpBox = new VBox();
//                for (Mat line: linesSplitter.split())
//                {
//                    WordsSplitter wordsSplitter = new WordsSplitter(line, tmpBox, colLimit, rowLimit);
//                    VBox tmpBox2 = new VBox();
//                    for (Mat word: wordsSplitter.split())
//                    {
//                        LettersSplitter lettersSplitter = new LettersSplitter(word, tmpBox2, colLimit, rowLimit);
//                    }
//                    tmpBox.getChildren().add(tmpBox2);
//                }
//                imgsBox.getChildren().add(tmpBox);
//            }
//        });
//        return button;
//    }

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

//    private Button addClassifyButton()
//    {
//        FileChooser fileChooser = addFileChooser();
//        Button button = new Button("Classify");
//        button.setOnAction(event ->
//        {
//            selectedFile = fileChooser.showOpenDialog(null);
//            event.consume();
//            if (selectedFile != null && dataSetPath != null && !dataSetPath.isEmpty())
//            {
//                Platform.runLater(() -> {
//                    root.getChildren().clear();
//                    HBox box = new HBox();
//                    box.getChildren().addAll(addReturnButton(), new Label("Loading and training, please wait"));
//                    root.setCenter(box);
//                });
//            }
//        });
//        return button;
//    }

    private Button addReturnButton()
    {
        Button button = new Button("Return");
        button.setOnAction(event ->
        {
            event.consume();
            root.getChildren().clear();
            try
            {
                this.initialize();
            } catch (IOException ignored)
            {
//            e.printStackTrace();
            }
        });
        return button;
    }

    public void setdSD(File dSD)
    {
        this.dSD = dSD;
    }
}
