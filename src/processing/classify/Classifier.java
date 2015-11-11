package processing.classify;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import processing.pre.ImageManipulator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by sal on 11/11/15.
 */
public class Classifier
{

    private File img;
    private Pane root;

    public Classifier(File img, Pane root)
    {
        this.setImg(img);
        this.setRoot(root);
    }

    public void setImg(File img)
    {
        this.img = img;
    }

    public void setRoot(Pane root)
    {
        this.root = root;
    }

    public void start()
    {

        Mat m = null;
        try
        {
            m = Imgcodecs.imread(img.getCanonicalPath(), Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        }
        catch (IOException ioe)
        {
            Logger.getGlobal().log(Level.SEVERE, ioe.getMessage());
        }
        if (m != null && !m.empty())
        {
            Imgproc.resize(m, m, new Size(150, 150));

            m = ImageManipulator.applyOtsuBinarysation(m);

            Rect r = findLetterBounds(m);

            m = m.submat(r.y, r.y + r.height, r.x, r.x + r.width);

            m = squareMat(m);

            showMat(m);
            System.out.println("elem = " + Arrays.toString(m.get(0, 0)));
        }
        else
            Logger.getGlobal().log(Level.SEVERE, "File is not an image");
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

    private void showMat(Mat mat)
    {
        Image image = matToImage(mat);
        ImageView imv = new ImageView();
        imv.setImage(image);
        imv.setFitWidth(300);
        imv.setPreserveRatio(true);
        imv.setSmooth(false);
        root.getChildren().add(imv);
    }

    private Mat squareMat(Mat m)
    {
        Mat nm = new Mat(Math.max(m.width(), m.height()), Math.max(m.width(), m.height()), CvType.CV_8U, new Scalar(255));

        if (m.width() == nm.width())
        {
            for (int i = 0; i < nm.width(); ++i)
            {
                for (int j = 0; j < m.height(); ++j)
                {
                    nm.put(j + (nm.height() - m.height()) / 2, i, m.get(j, i));
                }
            }
        }
        else if (m.height() == nm.height())
        {
            for (int i = 0; i < nm.height(); ++i)
            {
                for (int j = 0; j < m.width(); ++j)
                {
                    nm.put(i, j + (nm.width() - m.width()) / 2, m.get(i, j));
                }
            }
        }
        return nm;
    }
}
