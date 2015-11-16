/**
 * Created by sal on 11/11/15.
 */

package processing.classify;

import com.sun.istack.internal.NotNull;
import javafx.scene.control.ProgressBar;
import javafx.util.Pair;
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

    public static final int KNN_K_VALUE = 3;

    public Classifier(@NotNull ProgressBar _progressBar)
    {
        this._progressBar = _progressBar;
    }

    public Map<String, List<Mat>> get_dataset()  { return _dataset; }

    public Map<String, List<Mat>> get_trainingSet()  { return _trainingSet; }

    public Map<String, List<Mat>> get_testSet()  { return _testSet; }

    public void buildDatasetAndTrain(File dataSetDirectory)
    {
        _trainingSamples = new Mat();
        _trainingResponses = new Mat(1, 0, CvType.CV_8U);
        this.buildDataset(dataSetDirectory);
        this.splitDataset(0.7f);
        _knn = KNearest.create();
        for (Map.Entry<String, List<Mat>> entry : _trainingSet.entrySet())
        {
            for (Mat img : entry.getValue())
            {
                Mat tmp = new Mat(1, 1, CvType.CV_8U);

                tmp.put(0, 0, entry.getKey().charAt(0));
                _trainingSamples.push_back(img.reshape(1, 1));
                _trainingResponses.push_back(tmp);
            }
        }
        _trainingResponses = _trainingResponses.reshape(1, 1);
        this.doTrain();
    }

    public Character classify(Mat m)
    {
        Mat results = new Mat();
        Mat responses = new Mat();
        Mat distances = new Mat();

        m = this.preProc(m);
        m = m.reshape(1, 1);
        m.convertTo(m, CvType.CV_32F);
        _knn.findNearest(m, KNN_K_VALUE, results, responses, distances);
        return (char)results.get(0, 0)[0];
    }

    public void save(String pathName)
    {
        Imgcodecs.imwrite(pathName + "_samp", _trainingSamples);
        Imgcodecs.imwrite(pathName + "_resp", _trainingResponses);
    }

    /**
     * Load previously saved dataset and trains knn
     * @param pathName
     */
    public void loadAndTrain(String pathName)
    {
        _trainingSamples = Imgcodecs.imread(pathName + "_samp");
        _trainingResponses = Imgcodecs.imread(pathName + "_resp");
        this.doTrain();
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
    private ProgressBar             _progressBar;

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
        _dataset.forEach((key, value) ->
        {
            int i;

            _trainingSet.put(key, new LinkedList<>());
            _testSet.put(key, new LinkedList<>());
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
    private void buildDataset(File dataSetDirectoryPath)
    {
        List<Pair<Character, List<File>>> subDirectories = new LinkedList<>();
        int totalElements = 0;
        for (char c : _charClasses)
        {
            List<File> tmp = this.getDirContents(String.format("%s/%s", dataSetDirectoryPath, c));
            if (!tmp.isEmpty())
            {
                Pair<Character, List<File>> tmpPair = new Pair<>(c, tmp);
                totalElements += tmp.size();
                subDirectories.add(tmpPair);
            }
        }
        if (totalElements == 0) return;
        int currentElement = 0;
        for (Pair<Character, List<File>> subDirectory: subDirectories)
        {
            _dataset.put(String.valueOf(subDirectory.getKey()), new LinkedList<>());
            for (File file: subDirectory.getValue())
            {
                Mat smp = Imgcodecs.imread(file.getPath(), Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                if (!smp.empty())
                    _dataset.get(String.valueOf(subDirectory.getKey())).add(this.preProc(smp));
                final int actualNbElements = currentElement++;
                final int totalNbElements = totalElements;
                new Thread(() -> {
                    System.out.println("maj progress Bar: " + (double)actualNbElements/ (double)totalNbElements);
                    _progressBar.setProgress((double) actualNbElements / (double) totalNbElements);
                }).start();
            }
        }
    }

    private @NotNull List<File> getDirContents(String path)
    {
        File folder = new File(path);
        File[] tmp = folder.listFiles();
        if (tmp == null)
            return new LinkedList<>();

        return Arrays.stream(tmp).filter(File::isFile).collect(Collectors.toList());
    }

    private void doTrain()
    {
        _trainingSamples.convertTo(_trainingSamples, CvType.CV_32F);
        _trainingResponses.convertTo(_trainingResponses, CvType.CV_32F);
        _knn.train(_trainingSamples, Ml.ROW_SAMPLE, _trainingResponses);
    }
}
