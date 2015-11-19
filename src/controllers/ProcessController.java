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
    public Button testClassifyButton;
    @FXML
    public Button pPDButton; // pre process debug
    @FXML
    public Label colLimitLabel;
    @FXML
    public Label rowLimitLabel;
    @FXML
    public Label rowLimitValueLabel;
    @FXML
    public Label colLimitValueLabel;
    @FXML
    public Slider colLimitSlider;
    @FXML
    public Slider rowLimitSlider;

    @FXML
    private void initialize()
    {
        // configure Sliders
        configSlider(rowLimitSlider, 0d, 20d, 0.1d, 0d, true, true);
        configSlider(colLimitSlider, 0d, 20d, 0.1d, 0d, true, true);
        colLimitSlider.valueProperty().addListener((observable, oldValue, newValue) ->
        {
            colLimitValueLabel.setText(newValue.toString());
        });
        rowLimitSlider.valueProperty().addListener((observable, oldValue, newValue) ->
        {
            rowLimitValueLabel.setText(newValue.toString());
        });
        // configure Buttons

        // configure Labels
        colLimitLabel.setText("columns limit:");
        rowLimitLabel.setText("rows limit:");
//        rowLimitLabel.setPrefWidth(colLimitLabel.getPrefWidth());
        colLimitValueLabel.setText(String.valueOf(colLimitSlider.getValue()));
        rowLimitValueLabel.setText(String.valueOf(rowLimitSlider.getValue()));
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
