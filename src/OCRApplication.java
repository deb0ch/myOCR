/**
 * Created by sal on 02/11/15.
 */

import javafx.application.Application;
import javafx.stage.Stage;
import org.opencv.core.Core;

public class OCRApplication extends Application {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

    }
}
