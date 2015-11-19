package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 * Created by sal on 18/11/15.
 */
public class ClassifyController
{
    @FXML
    private BorderPane root;
    @FXML
    public Label resultLabel;

    @FXML
    private void initialize()
    {
        resultLabel.setText("");
    }
}
