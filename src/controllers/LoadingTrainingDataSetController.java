package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;

/**
 * Created by sal on 15/11/15.
 */
public class LoadingTrainingDataSetController
{
    @FXML
    private BorderPane root;
    @FXML
    private ProgressBar loadingTrainingSetProgressBar;

    public ProgressBar getLoadingTrainingSetProgressBar()
    {
        return loadingTrainingSetProgressBar;
    }

    @FXML
    private void initialize()
    {
//        loadingTrainingSetProgressBar.setBorder(new Border(new BorderStroke(Color.BLACK, null, null, new BorderWidths(15d))));
    }
}
