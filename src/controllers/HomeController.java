package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
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

    private Classifier classifier;
    private File dSD;

    @FXML
    private void initialize() throws IOException
    {
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

        FXMLLoader classifyLoader = new FXMLLoader(getClass().getResource("../views/classify.fxml"));
        BorderPane classifyBorderPane = classifyLoader.load();
        ClassifyController classifyController = classifyLoader.getController();

        processController.classifyButton.setOnAction(event ->
        {
            FileChooser fileChooser = addFileChooser();
            File choosedFile = fileChooser.showOpenDialog(null);
            if (choosedFile != null)
            {
                Mat m = ImageManipulator.loadGreyImage(choosedFile);
                if (!m.empty())
                {
                    classifyController.resultLabel.setText("");
                    LinesSplitter linesSplitter = new LinesSplitter(m);
                    for (Mat line: linesSplitter.split())
                    {
                        WordsSplitter wordsSplitter = new WordsSplitter(line);
                        for (Mat word: wordsSplitter.split())
                        {
                            LettersSplitter lettersSplitter = new LettersSplitter(word);
                            for (Mat letter: lettersSplitter.split())
                            {
                                Character c = classifier.classify(letter);
                                classifyController.resultLabel.setText(String.format("%s %s", classifyController.resultLabel.getText(), c.toString()));
                                root.setBottom(classifyBorderPane);
                            }
                        }
                    }
                }
            }
        });

        processController.testClassifyButton.setOnAction(event ->
        {
            float result = classifier.test();
            classifyController.resultLabel.setText(String.format("Accuracy: %f%%", result * 100));
            root.setBottom(classifyBorderPane);
        });

        processController.pPDButton.setOnAction(event ->
        {
            FileChooser fileChooser = addFileChooser();
            File chosenFile = fileChooser.showOpenDialog(null);
            if (chosenFile != null)
            {
                Mat m = ImageManipulator.loadGreyImage(chosenFile);
                if (!m.empty())
                {
                    HBox box = new HBox();
                    box.setSpacing(2d);
                    ScrollPane sp = new ScrollPane(box);
                    sp.setMaxSize(700, 720);
                    root.setRight(sp);

                    double colLimit = processController.colLimitSlider.getValue();
                    double rowLimit = processController.rowLimitSlider.getValue();
                    classifyController.resultLabel.setText("");
                    LinesSplitter linesSplitter =
                            new LinesSplitter(m, box, colLimit, rowLimit);
                    VBox tmpBox = new VBox();
                    tmpBox.setSpacing(2d);
                    for (Mat line: linesSplitter.split())
                    {
                        WordsSplitter wordsSplitter = new WordsSplitter(line, tmpBox, colLimit, rowLimit);
                        HBox tmpBox2 = new HBox();
                        tmpBox2.setSpacing(2d);
                        for (Mat word: wordsSplitter.split())
                        {
                            LettersSplitter lettersSplitter = new LettersSplitter(word, tmpBox2, colLimit, rowLimit);
                            for (Mat letter: lettersSplitter.split())
                            {
                                Character c = classifier.classify(letter);
                                classifyController.resultLabel.setText(String.format("%s %s", classifyController.resultLabel.getText(), c.toString()));
                                root.setBottom(classifyBorderPane);
                            }
                        }
                        tmpBox.getChildren().add(tmpBox2);
                    }
                    box.getChildren().add(tmpBox);
                }
            }
        });

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
                new File(String.format("%s%s%s%s",
                        workingDirectory,
                        System.getProperty("file.separator"),
                        Classifier.saveFileName,
                        Classifier.sampleFileName));
        File savedResponsesFile =
                new File(String.format("%s%s%s%s",
                        workingDirectory,
                        System.getProperty("file.separator"),
                        Classifier.saveFileName,
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
                                    tDSController.saveButton.setDisable(false);
                                }
                            });
                    new Thread(() ->
                    {
                        if (dSD != null)
                            classifier.buildDataSet(dSD, cDSDController.ratioSlider.getValue());
                        classifier.doTrain();
                    }).start();
                });
        root.setCenter(cDSDBorderPane);
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

    public void setdSD(File dSD)
    {
        this.dSD = dSD;
    }
}
