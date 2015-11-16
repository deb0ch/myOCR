package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;

/**
 * Created by sal on 15/11/15.
 * CDSD stands for Choose DataSet Directory
 */
public class CDSDController
{
    @FXML
    private BorderPane root;
    @FXML
    public Button cDSDButton;
    @FXML
    public Button trainButton;
    @FXML
    public Slider ratioSlider;

    @FXML
    private void initialize()
    {
        // configure Button
        cDSDButton.setText("Choose Directory");
        trainButton.setText("Train");
        // configure Slider
        ratioSlider.setMin(0d);
        ratioSlider.setMax(1d);
        ratioSlider.setBlockIncrement(0.1d);
        ratioSlider.setValue(0.5d);
        ratioSlider.setShowTickLabels(true);
        ratioSlider.setShowTickMarks(true);
    }
}
