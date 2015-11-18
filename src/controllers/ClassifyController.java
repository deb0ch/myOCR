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
    // add stuff here

    @FXML
    private void initialize()
    {
        // do stuff here
        System.out.println("ClassifyController.initialize");
        resultLabel.setText("");
    }
}
