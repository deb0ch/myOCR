package processing.classify;

import com.sun.istack.internal.NotNull;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.KNearest;
import org.opencv.ml.Ml;
import processing.pre.ImageManipulator;
import processing.pre.MatManipulator;
import utils.ErrorHandling;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Created by sal on 11/11/15.
 */
public class Classifier
{
    private KNearest                _knn;
    private Map<String, List<Mat>>  _dataset = new HashMap<>();
    private Map<String, List<Mat>>  _trainingSet = new HashMap<>();
    private Map<String, List<Mat>>  _testSet = new HashMap<>();
    private String dataSetDirectoryPath;
    private ProgressBar generalProgressBar = new ProgressBar();
    private ProgressBar detailsProgressBar = new ProgressBar();
    private Label generalStatus = new Label();
    private Label detailsStatus = new Label();
    private final float NB_BUILD_DATA_SET = 1f;
    private final float NB_SPLIT_DATA_SET = 2f;
    private final float NB_KNN = 3f;
    private final float NB_GENERAL_STEPS = NB_KNN; // last values of steps

    public ProgressBar getGeneralProgressBar()
    {
        return generalProgressBar;
    }

    public ProgressBar getDetailsProgressBar()
    {
        return detailsProgressBar;
    }

    public Classifier(@NotNull File dataSetDirectory)
    {
        this.dataSetDirectoryPath = dataSetDirectory.getPath();
        generalProgressBar.setProgress(0f);
        generalProgressBar.setPrefSize(Double.MAX_VALUE, Double.MIN_NORMAL);
        detailsProgressBar.setPrefSize(Double.MAX_VALUE, Double.MIN_NORMAL);
    }

    private void setProgress(float value, @NotNull ProgressBar p)
    {
        Platform.runLater(() -> p.setProgress(value));
        try
        {
            Thread.sleep(5);
        } catch (InterruptedException ignored)
        {
        }
    }

    private void setText(@NotNull String msg, @NotNull Label sp)
    {
        Platform.runLater(() -> sp.setText(msg));
        try
        {
            Thread.sleep(5);
        } catch (InterruptedException ignored)
        {
        }
    }

    public void train()
    {
        Mat trainingSamples = new Mat();
        Mat trainingResponses = new Mat(1, 0, CvType.CV_8U);

        this.buildDataset();
        this.splitDataset(0.7f);

        setText("Creating KNN", generalStatus);
        _knn = KNearest.create();

        float totalTraining = _trainingSet.size();
        float indexTraining = 1f;
        setText("...", detailsStatus);
        setProgress(0f, detailsProgressBar);
        for (Map.Entry<String, List<Mat>> entry : _trainingSet.entrySet())
        {
            setText("Prepare training sets: " + entry.getKey(), detailsStatus);
            for (Mat img : entry.getValue())
            {
                Mat tmp = new Mat(1, 1, CvType.CV_8U);

                tmp.put(0, 0, entry.getKey().charAt(0));
                trainingSamples.push_back(img.reshape(1, 1));
                trainingResponses.push_back(tmp);
            }
            setProgress(indexTraining++/totalTraining, detailsProgressBar);
        }

        setText("...", detailsStatus);
        trainingResponses = trainingResponses.reshape(1, 1);
        trainingSamples.convertTo(trainingSamples, CvType.CV_32F);
        trainingResponses.convertTo(trainingResponses, CvType.CV_32F);
        setText("Training KNN", generalStatus);
        _knn.train(trainingSamples, Ml.ROW_SAMPLE, trainingResponses);
        setProgress(NB_KNN/NB_GENERAL_STEPS, generalProgressBar);
    }

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

    public void start(@NotNull Mat img, @NotNull Pane root)
    {
        if (!img.empty() && img.size().area() > 0)
        {
            img = this.preProc(img);
//            this.train();
            ImageManipulator.showMat(root, img);
        }
        else
            ErrorHandling.log(Level.WARNING, "Not an image");
    }

    private Mat preProc(@NotNull Mat m)
    {
        m = ImageManipulator.applyOtsuBinarysation(m);
        Rect r = MatManipulator.findBounds(m);
        m = m.submat(r.y, r.y + r.height, r.x, r.x + r.width);
        m = MatManipulator.squareMat(m);
        Imgproc.resize(m, m, new Size(24, 24));
        m = ImageManipulator.applyOtsuBinarysation(m); // Imgproc.resize may break binarization
        return m;
    }

    /**
     * Splits _dataset into _trainingSet and _testSet.
     * @param ratio Size ratio of _trainingSet over _testSet.
     */
    private void splitDataset(float ratio)
    {
        setText("Splitting Data Set", generalStatus);
        _dataset.forEach((key, value) ->
        {
            setText("...", detailsStatus);
            _trainingSet.put(key, new LinkedList<>());
            _testSet.put(key, new LinkedList<>());
            float nbValues = value.size();
            setProgress(0f, detailsProgressBar);
            int i;
            for (i = 0; i < value.size() * ratio; i++)
            {
                _trainingSet.get(key).add(value.get(i));
                setProgress((float)i / nbValues, detailsProgressBar);
            }
            while (i < value.size())
            {
                _testSet.get(key).add(value.get(i));
                setProgress((float)i / nbValues, detailsProgressBar);
                ++i;
            }
        });
        setProgress(NB_SPLIT_DATA_SET / NB_GENERAL_STEPS, generalProgressBar);
    }

    /**
     * Iterate through dataset directories, each directory being named according to the character
     * it refers to. Opens every image in these directories and stores them in the _dataset class attribute.
     * Each matrix generated is stored in that map with its label as a key.
     */
    private void buildDataset()
    {
        setText("Building Data Set", generalStatus);
        for (char c : _charClasses)
        {
            List<File> tmp = this.getDirContents(String.format("%s/%s", dataSetDirectoryPath, c));

            setText("Transforming images", detailsStatus);
            setProgress(0f, detailsProgressBar);
            float nb_details_steps = tmp.size();
            float current = 0f;

            _dataset.put(String.valueOf(c), new LinkedList<>());
            for (File file: tmp)
            {
                Mat smp = Imgcodecs.imread(file.getPath(), Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                if (!smp.empty())
                    _dataset.get(String.valueOf(c)).add(this.preProc(smp));
                setProgress(current/ nb_details_steps, detailsProgressBar);
            }
        }
        setProgress(NB_BUILD_DATA_SET/NB_GENERAL_STEPS, generalProgressBar);
    }

    private @NotNull List<File> getDirContents(String path)
    {
        setText("Loading files under'" + path + "'", detailsStatus);
        File folder = new File(path);
        File[] tmp = folder.listFiles();
        if (tmp == null)
            return new LinkedList<>();
        return Arrays.stream(tmp).filter(File::isFile).collect(Collectors.toList());
    }

    public void classify()
    {

    }

    public Label generalStatus()
    {
        return generalStatus;
    }

    public Label detailsStatus()
    {
        return detailsStatus;
    }
}
