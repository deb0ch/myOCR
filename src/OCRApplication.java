/**
 * Created by sal on 02/11/15.
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.opencv.core.Core;

public class OCRApplication extends Application
{
    private static final String APPLICATION_TITLE = "MyOCR";

    /**
     * The function that is call to start the java program
     * @param args the arguments given to the program
     */
    public static void main(String... args) throws InterruptedException
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }

    private void addExitListener(Scene scene)
    {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) ->
        {
            if (event.getCode().equals(KeyCode.Q) && event.isControlDown())
            {
                Platform.exit();
            }
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(this.getClass().getResource("views/home.fxml"));
        Scene scene = new Scene(root, 1280, 720, Color.DARKGREY);
        addExitListener(scene);

        primaryStage.setTitle(APPLICATION_TITLE);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
