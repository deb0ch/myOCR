/**
 * Created by sal on 11/11/15.
 */

package processing.classify;

import com.sun.istack.internal.NotNull;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
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

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Classifier
{
    /*
     * Public
     */

    public static final int         KNN_K_VALUE = 3;
    public static final String      saveFileName        = "save";
    public static final String      sampleFileName      = "_samp.bmp";
    public static final String      responseFileName    = "_resp.bmp";

    public Classifier(@NotNull ProgressBar generalProgressBar, @NotNull ProgressBar detailsProgressBar,
                      @NotNull Label generalStatus, @NotNull Label detailsStatus)
    {
        this.generalStatus = generalStatus;
        this.detailsStatus = detailsStatus;
        this.generalProgressBar = generalProgressBar;
        this.detailsProgressBar = detailsProgressBar;
    }

    public Character classify(Mat m)
    {
        Mat results = new Mat();
        Mat responses = new Mat();
        Mat distances = new Mat();

        m = preProc(m);
        m = m.reshape(1, 1);
        m.convertTo(m, CvType.CV_32F);
        assert _knn != null;
        _knn.findNearest(m, KNN_K_VALUE, results, responses, distances);
        return (char)results.get(0, 0)[0];
    }

    public void save(String pathName)
    {
        Imgcodecs.imwrite(String.format("%s%s%s%s", pathName, System.getProperty("file.separator"),
                saveFileName, sampleFileName),
                _trainingSamples);
        Imgcodecs.imwrite(String.format("%s%s%s%s", pathName, System.getProperty("file.separator"),
                saveFileName, responseFileName),
                _trainingResponses);
        saveTestSet(saveFileName);
    }

    /**
     * Load previously saved dataset and trains knn
     * @param pathName
     */
    public void loadPreviousDataSet(String pathName)
    {
        setText("Loading Previous Data Set", generalStatus);
        _trainingSamples = Imgcodecs.imread(String.format("%s%s%s%s", pathName, System.getProperty("file.separator"),
                saveFileName,
                sampleFileName),
                Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        _trainingResponses = Imgcodecs.imread(String.format("%s%s%s%s", pathName, System.getProperty("file.separator"),
                saveFileName,
                responseFileName),
                Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        loadTestSet(saveFileName);
    }

    public void buildDataSet(File dataSetDirectory, double ratio)
    {
        _trainingSamples = new Mat();
        _trainingResponses = new Mat(1, 0, CvType.CV_8U);
        buildDataset(dataSetDirectory);
        splitDataset(ratio);
        createKNN();
    }

    public void doTrain()
    {
        _knn = KNearest.create();
        setText("Start training", generalStatus);
        setText("...", detailsStatus);

        _trainingSamples.convertTo(_trainingSamples, CvType.CV_32FC1);
        _trainingResponses.convertTo(_trainingResponses, CvType.CV_32FC1);

        _knn.train(_trainingSamples, Ml.ROW_SAMPLE, _trainingResponses);
        setProgress(1f, generalProgressBar);
        setText("Done", generalStatus);
    }

    public float test()
    {
        float sum = 0;
        float total = 0;

        for (Character c : _charClasses)
        {
            total += _testSet.get(String.valueOf(c)).size();
            int i = 0;
            for (Mat smp : _testSet.get(String.valueOf(c)))
            {
                System.out.println(i++);
                sum += classify(smp) == c ? 1 : 0;
            }
        }
        return sum / total;
    }

    /*
     * Private
     */

    private KNearest                _knn;
    private Map<String, List<Mat>>  _dataset = new HashMap<>();
    private Map<String, List<Mat>>  _trainingSet = new HashMap<>();
    private Map<String, List<Mat>>  _testSet = new HashMap<>();

    private Mat                     _trainingSamples = new Mat();
    private Mat                     _trainingResponses = new Mat(1, 0, CvType.CV_8U);

    private char[]                  _charClasses =
            {
                    'A', 'a',
                    'B', 'b',
                    'C', 'c',
                    'D', 'd',
                    'E', 'e',
                    'F', 'f',
                    'G', 'g',
                    'H', 'h',
                    'I', 'i',
                    'J', 'j',
                    'K', 'k',
                    'L', 'l',
                    'M', 'm',
                    'N', 'n',
                    'O', 'o',
                    'P', 'p',
                    'Q', 'q',
                    'R', 'r',
                    'S', 's',
                    'T', 't',
                    'U', 'u',
                    'V', 'v',
                    'X', 'x',
                    'Y', 'y',
                    'Z', 'z',
                    '0',
                    '1',
                    '2',
                    '3',
                    '4',
                    '5',
                    '6',
                    '7',
                    '8',
                    '9'
            };

    private ProgressBar generalProgressBar;
    private ProgressBar detailsProgressBar;

    private Label generalStatus;
    private Label detailsStatus;

    private final float NB_BUILD_DATA_SET = 1f;
    private final float NB_SPLIT_DATA_SET = NB_BUILD_DATA_SET + 1f;
    private final float NB_KNN = NB_SPLIT_DATA_SET + 1f;
    private final float NB_GENERAL_STEPS = NB_KNN; // last values of steps

    private void setProgress(float value, @NotNull ProgressBar p)
    {
        Platform.runLater(() -> p.setProgress(value));
    }

    private void setText(@NotNull String msg, @NotNull Label sp)
    {
        Platform.runLater(() -> sp.setText(msg));
    }

    private void createKNN()
    {
        setText("Creating Training set", generalStatus);

        float totalTraining = _trainingSet.size();
        float indexTraining = 1f;

        setText("...", detailsStatus);
        setProgress(0f, detailsProgressBar);

        for (Map.Entry<String, List<Mat>> entry : _trainingSet.entrySet())
        {
            setText(String.format("Prepare training sets: %s", entry.getKey()), detailsStatus);
            for (Mat img : entry.getValue())
            {
                Mat tmp = new Mat(1, 1, CvType.CV_8U);

                tmp.put(0, 0, entry.getKey().charAt(0));
                _trainingSamples.push_back(img.reshape(1, 1));
                _trainingResponses.push_back(tmp);
            }
            setProgress(indexTraining++/totalTraining, detailsProgressBar);
        }
        _trainingResponses = _trainingResponses.reshape(1, 1);
        setProgress(NB_KNN / NB_GENERAL_STEPS, generalProgressBar);
    }

    private Mat preProc(@NotNull Mat m)
    {
        m = ImageManipulator.applyOtsuBinarysation(m);
        Rect r = MatManipulator.findBounds(m);
        // rowStart, rowEnd, colStart, colEnd
        m = m.submat(r.x, r.width, r.y, r.height);
        m = MatManipulator.squareMat(m);
//        Imgproc.resize(m, m, new Size(24, 24), 0, 0, Imgproc.INTER_AREA);
//        Imgcodecs.imwrite("/tmp/array.bmp", m);
//        Imgproc.resize(m, m, new Size(24, 24), 0, 0, Imgproc.INTER_CUBIC);
//        Imgcodecs.imwrite("/tmp/cubic.bmp", m);
//        Imgproc.resize(m, m, new Size(24, 24), 0, 0, Imgproc.INTER_LANCZOS4);
//        Imgcodecs.imwrite("/tmp/lanczos4.bmp", m);
//        Imgproc.resize(m, m, new Size(24, 24), 0, 0, Imgproc.INTER_NEAREST);
//        Imgcodecs.imwrite("/tmp/nearest.bmp", m);
        Imgproc.resize(m, m, new Size(24, 24), 0, 0, Imgproc.INTER_LINEAR);
        Imgcodecs.imwrite("/tmp/linear.bmp", m);
        m = ImageManipulator.applyOtsuBinarysation(m); // Imgproc.resize may break binarization
        Imgcodecs.imwrite("/tmp/end.bmp", m);
        return m;
    }

    /**
     * Splits _dataset into _trainingSet and _testSet.
     * @param ratio Size ratio of _trainingSet over _testSet.
     */
    private void splitDataset(double ratio)
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
    private void buildDataset(File dataSetDirectoryPath)
    {
        setText("Building Data Set", generalStatus);
        for (char c : _charClasses)
        {
            List<File> tmp = this.getDirContents(String.format("%s%s%s", dataSetDirectoryPath, System.getProperty("file.separator"), c));

            setText(String.format("Transforming images from '%s'", c), detailsStatus);
            setProgress(0f, detailsProgressBar);

            float nb_details_steps = tmp.size();
            float current = 0f;

            _dataset.put(String.valueOf(c), new LinkedList<>());
            for (File file: tmp)
            {
                Mat smp = Imgcodecs.imread(file.getPath(), Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                if (!smp.empty())
                {
                    Mat img = preProc(smp);
                    _dataset.get(String.valueOf(c)).add(img);
                }
                setProgress(current++ / nb_details_steps, detailsProgressBar);
            }
        }
        setProgress(NB_BUILD_DATA_SET / NB_GENERAL_STEPS, generalProgressBar);
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

    private void saveTestSet(String name)
    {
        name = name + "_test";
        new File(name).mkdirs();
        for (Character c : _charClasses)
        {
            new File(name + "/" + c).mkdirs();
            for (int i = 0; i < _testSet.get(String.valueOf(c)).size(); ++i)
            {
                Imgcodecs.imwrite(name + "/" + c + "/" + i + ".bmp", _testSet.get(String.valueOf(c)).get(i));
            }
        }
    }

    private void loadTestSet(String name)
    {
        name = name + "_test";
        for (char c : _charClasses)
        {
            List<File> tmp = this.getDirContents(String.format("%s%s%s", name, System.getProperty("file.separator"), c));

            _testSet.put(String.valueOf(c), new LinkedList<>());
            for (File file: tmp)
            {
                Mat smp = Imgcodecs.imread(file.getPath(), Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                if (!smp.empty())
                {
                    _testSet.get(String.valueOf(c)).add(smp);
                }
            }
        }
    }
}
