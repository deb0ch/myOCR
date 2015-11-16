package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;

/**
 * Created by sal on 15/11/15.
 */
public class LoadingController
{
    @FXML
    private BorderPane root;
    @FXML
    public Label generalLabel;
    @FXML
    public Label detailsLabel;
    @FXML
    public ProgressBar generalProgressBar;
    @FXML
    public ProgressBar detailsProgressBar;
    @FXML
    public Button saveButton;
    @FXML
    public Button nextButton;

    @FXML
    private void initialize()
    {
        // configure Progress Bars
        generalProgressBar.setProgress(0f);
        detailsProgressBar.setProgress(0f);
        generalProgressBar.setPrefSize(Double.MAX_VALUE, Double.MIN_NORMAL);
        detailsProgressBar.setPrefSize(Double.MAX_VALUE, Double.MIN_NORMAL);
        // configure Labels
        // configure Buttons
    }
}
