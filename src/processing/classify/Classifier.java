package processing.classify;

import javafx.scene.layout.Pane;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.KNearest;
import org.opencv.ml.Ml;
import processing.pre.ImageManipulator;
import utils.ErrorHandling;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

/**
 * Created by sal on 11/11/15.
 */
public class Classifier
{
    public Classifier(File img, Pane root)
    {
        this.start(img, root);
    }

    public void train()
    {
        Mat trainingSamples = new Mat();
        Mat trainingResponses = new Mat(1, 0, CvType.CV_8U);

        this.buildDataset();
        this.splitDataset(0.7f);

        _knn = KNearest.create();

        for (Map.Entry<String, List<Mat>> entry : _trainingSet.entrySet())
        {
            for (Mat img : entry.getValue())
            {
                Mat tmp = new Mat(1, 1, CvType.CV_8U);

                tmp.put(0, 0, entry.getKey().charAt(0));
                trainingSamples.push_back(img.reshape(1, 1));
                trainingResponses.push_back(tmp);
            }
        }
        trainingResponses = trainingResponses.reshape(1, 1);
        trainingSamples.convertTo(trainingSamples, CvType.CV_32F);
        trainingResponses.convertTo(trainingResponses, CvType.CV_32F);

        _knn.train(trainingSamples, Ml.ROW_SAMPLE, trainingResponses);
    }

    private KNearest                _knn;
    private Map<String, List<Mat>>  _dataset = new HashMap<>();
    private Map<String, List<Mat>>  _trainingSet = new HashMap<>();
    private Map<String, List<Mat>>  _testSet = new HashMap<>();

    private char[]                  _charClasses =
            {
                    'A', 'a',
//                    'B', 'b',
//                    'C', 'c',
//                    'D', 'd',
//                    'E', 'e',
//                    'F', 'f',
//                    'G', 'g',
//                    'H', 'h',
//                    'I', 'i',
//                    'J', 'j',
//                    'K', 'k',
//                    'L', 'l',
//                    'M', 'm',
//                    'N', 'n',
//                    'O', 'o',
//                    'P', 'p',
//                    'Q', 'q',
//                    'R', 'r',
//                    'S', 's',
//                    'T', 't',
//                    'U', 'u',
//                    'V', 'v',
//                    'X', 'x',
//                    'Y', 'y',
//                    'Z', 'z',
//                    '0',
//                    '1',
//                    '2',
//                    '3',
//                    '4',
//                    '5',
//                    '6',
//                    '7',
//                    '8',
                    '9'
            };

    private void start(File img, Pane root)
    {
        Mat m = null;

        try
        {
            m = Imgcodecs.imread(img.getCanonicalPath(), Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        }
        catch (IOException ioe)
        {
            ErrorHandling.logAndExit(Level.SEVERE, ioe.getMessage());
        }
        if (m != null && !m.empty())
        {
            m = this.preProc(m);
            this.train();
            ImageManipulator.showMat(root, m);
        }
        else
            ErrorHandling.log(Level.WARNING, "Not an image");
    }

    private Mat preProc(Mat m)
    {
        m = ImageManipulator.applyOtsuBinarysation(m);
        Rect r = this.findLetterBounds(m);
        m = m.submat(r.y, r.y + r.height, r.x, r.x + r.width);
        m = this.squareMat(m);
        Imgproc.resize(m, m, new Size(24, 24));
        m = ImageManipulator.applyOtsuBinarysation(m); // Imgproc.resize may break binarization
        return m;
    }

    private List<File> getDirContents(String path)
    {
        File        folder = new File(path);
        File[]      contents = folder.listFiles();
        List<File>  files = new ArrayList<>();

        if (contents == null)
            return files;
        for (File f : contents)
        {
            if (f.isFile())
                files.add(f);
        }
        return files;
    }

    /**
     * Splits _dataset into _trainingSet and _testSet.
     * @param ratio Size ratio of _trainingSet over _testSet.
     */
    private void splitDataset(float ratio)
    {
        _dataset.forEach((key, value) ->
        {
            int i;

            _trainingSet.put(key, new ArrayList<>());
            _testSet.put(key, new ArrayList<>());
            for (i = 0; i < value.size() * ratio; i++)
            {
                _trainingSet.get(key).add(value.get(i));
            }
            while (i < value.size())
            {
                _testSet.get(key).add(value.get(i));
                ++i;
            }
        });
    }

    /**
     * Iterate through dataset directories, each directory being named according to the character
     * it refers to. Opens every image in these directories and stores them in the _dataset class attribute.
     * Each matrix generated is stored in that map with its label as a key.
     */
    private void buildDataset()
    {
        String datasetPath = Classifier.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "resources/datasets/Hnd/";

        for (char c : _charClasses)
        {
            List<File> samples = this.getDirContents(datasetPath + c);

            if (!samples.isEmpty())
            {
                _dataset.put(String.valueOf(c), new ArrayList<>());
                samples.forEach(file ->
                {
                    Mat smp = Imgcodecs.imread(file.getPath(), Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

                    if (!smp.empty())
                        _dataset.get(String.valueOf(c)).add(this.preProc(smp));
                });
            }
        }
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
