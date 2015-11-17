package controllers;

import com.sun.istack.internal.NotNull;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;

/**
 * Created by sal on 17/11/15.
 */
public class ProcessController
{
    @FXML
    private BorderPane root;
    @FXML
    public Button classifyButton;
    @FXML
    public Button pPDButton; // pre process debug
    @FXML
    public Label colLimitLabel;
    @FXML
    public Label rowLimitLabel;
    @FXML
    public Slider colLimitSlider;
    @FXML
    public Slider rowLimitSlider;

    @FXML
    private void initialize()
    {
        // configure Buttons
        // configure Labels
        colLimitLabel.setText("columns limit:");
        rowLimitLabel.setText("rows limit:");
        // configure Sliders
        configSlider(rowLimitSlider, 0d, 50d, 0.1d, 0d, true, true);

    }

    public static void configSlider(@NotNull Slider aSlider, double min, double max,
                                    double increment, double initValue, boolean showLabels, boolean showMarks)
    {
        aSlider.setMin(min);
        aSlider.setMax(max);
        aSlider.setBlockIncrement(increment);
        aSlider.setValue(initValue);
        aSlider.setShowTickLabels(showLabels);
        aSlider.setShowTickMarks(showMarks);
    }
}
