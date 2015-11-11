/**
 * Created by sal on 02/11/15.
 */

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import processing.pre.ImageManipulator;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

public class OCRApplication extends Application
{
    private static final String APPLICATION_TITLE = "MyOCR";
    private static final String USAGE = "java OCRApplication pathToFile";

    /**
     * The function that is call to start the java program
     * @param args the arguments given to the program
     */
    public static void main(String... args) throws InterruptedException
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        primaryStage.setTitle(APPLICATION_TITLE);

        if (getParameters().getUnnamed().size() >= 1)
        {
            Mat m = Imgcodecs.imread(getParameters().getUnnamed().get(0), Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE); // threshold cannot apply on colored img
            if (!m.empty())
            {
                Imgproc.resize(m, m, new Size(150, 150));

                m = ImageManipulator.applyOtsuBinarysation(m);

                Rect r = findLetterBounds(m);

                m = m.submat(r.y, r.y + r.height, r.x, r.x + r.width);

                showMat(primaryStage, m);
                System.out.println("elem = " + Arrays.toString(m.get(0, 0)));
            }
            else
                System.err.println("File is not an image");
        }
        else
            System.out.println(USAGE);
    }

    private Rect findLetterBounds(Mat m)
    {
        int start_x = -1;
        int end_x = -1;
        int start_y = -1;
        int end_y = -1;

        for (int i = 0; i < m.width() && start_x == -1; ++i)
        {
            for (int j = 0; j < m.height() && start_x == -1; ++j)
            {
                if (m.get(j, i)[0] != 255)
                    start_x = i;
            }
        }
        for (int i = m.width() - 1; i >= 0 && end_x == -1; --i)
        {
            for (int j = 0; j < m.height() && end_x == -1; ++j)
            {
                if (m.get(j, i)[0] != 255)
                    end_x = i;
            }
        }
        for (int i = 0; i < m.height() && start_y == -1; ++i)
        {
            for (int j = 0; j < m.width() && start_y == -1; ++j)
            {
                if (m.get(i, j)[0] != 255)
                    start_y = i;
            }
        }
        for (int i = m.height() - 1; i >= 0 && end_y == -1; --i)
        {
            for (int j = 0; j < m.width() && end_y == -1; ++j)
            {
                if (m.get(i, j)[0] != 255)
                    end_y = i;
            }
        }
        return new Rect(start_x, start_y, end_x - start_x, end_y - start_y);
    }

    private Image matToImage(Mat mat)
    {
        MatOfByte byteMat = new MatOfByte();
        Imgcodecs.imencode(".bmp", mat, byteMat);
        return new Image(new ByteArrayInputStream(byteMat.toArray()));
    }

    private void showMat(Stage primaryStage, Mat mat)
    {
        Image image = matToImage(mat);
        ImageView imv = new ImageView();
        imv.setImage(image);
        imv.setFitWidth(300);
        imv.setPreserveRatio(true);
        imv.setSmooth(false);
        StackPane root = new StackPane();
        root.getChildren().add(imv);
        Scene scene = new Scene(root, 1024, 720, Color.LIGHTGOLDENRODYELLOW);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
