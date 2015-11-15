package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

/**
 * Created by sal on 15/11/15.
 */
public class ChooseDataSetDirectoryController
{
    @FXML
    private BorderPane root;
    @FXML
    private Button chooseDataSetDirectoryButton;

    @FXML
    private void initialize()
    {
//        chooseDataSetDirectoryButton.setOnAction(event -> {


//                dataSetPath = trainningSetDirectory.getPath();
//                classifyButton.setVisible(true);
//                debugPreProcessButton.setVisible(true);
//                value.setText(trainningSetDirectory.getPath());
//        });
    }

    public Button getChooseDataSetDirectoryButton()
    {
        return chooseDataSetDirectoryButton;
    }

//    private void addSelectTrainingSetDirectory()
//    {
//        chooseDataSetDirectoryButton.setOnAction(event -> {
//            DirectoryChooser directoryChooser = new DirectoryChooser();
//            directoryChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
//            File trainningSetDirectory = directoryChooser.showDialog(null);
//            if (trainningSetDirectory != null)
//            {
////                dataSetPath = trainningSetDirectory.getPath();
////                classifyButton.setVisible(true);
////                debugPreProcessButton.setVisible(true);
//                value.setText(trainningSetDirectory.getPath());
//            }
//        });
//    }
}
