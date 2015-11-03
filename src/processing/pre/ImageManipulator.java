package processing.pre;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;

/**
 * Created by sal on 02/11/15.
 */
public class ImageManipulator {

    /**
     * This function aims to verify that the path is about loading an image
     * @param filepath the path to verify
     * @return true if the string ends with supported formats, false otherwise
     */
    private boolean isAnImage(String filepath) {
        assert filepath != null && !filepath.isEmpty() : "Invalid file path";

        filepath = filepath.trim();
        return filepath.endsWith("png") ||
                filepath.endsWith("bmp") ||
                filepath.endsWith("jpg") ||
                filepath.endsWith("jpeg")
                ;
    }

    /**
     * This function aims to verify that a filepath is valid to load an image.
     * @param filepath the path to verify
     * @return true if the path is not null, the file exists and it's an image, false otherwise.
     */
    private boolean isAValidFilePath(String filepath) {
        if (filepath == null) return false;
        File f = new File(filepath);
        return f.isFile() && f.exists() && isAnImage(filepath);
    }

    /**
     * This function aims to verify the validity of an Imgcodecs.IMREAD_* code gave to a function
     * @param imReadCode the code to verify
     * @return true if the code is valid, false otherwise.
     */
    private boolean isAValidImReadCode(int imReadCode) {
        return imReadCode == Imgcodecs.IMREAD_UNCHANGED ||
                imReadCode == Imgcodecs.IMREAD_GRAYSCALE ||
                imReadCode == Imgcodecs.IMREAD_COLOR ||
                imReadCode == Imgcodecs.IMREAD_ANYDEPTH ||
                imReadCode == Imgcodecs.IMREAD_ANYCOLOR ||
                imReadCode == Imgcodecs.IMREAD_LOAD_GDAL;
    }

    /**
     * This function aims to return an Opencv Matrice that represent an image. The image will be loaded as it is.
     * @param filepath the path to the image to be load.
     * @return a Matrice that can be used with OpenCv functions.
     */
    public Mat loadImage(String filepath) {
        return this.loadImage(filepath, Imgcodecs.IMREAD_UNCHANGED);
    }

    /**
     *
     * This function aims to return an Opencv Matrice that represent an image. The image will be loaded with the given
     * specifications pass with the code argument.
     * @param filepath the path to the image to be load.
     * @param imReadCode the Imgcodecs.IMREAD_* code to change the properties of the loaded image.
     * @return a Matrice that can be used with OpenCv functions.
     */
    public Mat loadImage(String filepath, int imReadCode) {
        assert isAValidFilePath(filepath): "filepath is invalid: '" + filepath + "'";
        assert isAValidImReadCode(imReadCode): "imReadCode is invalid: '" + imReadCode + "'";

        return Imgcodecs.imread(filepath, imReadCode);
    }

    /**
     * This function aims to write a Matrice as an image.
     * @param filepath the path and name of the file to save in.
     * @param img the Matrice that represent the image to be save.
     * @return true if the image is successfully written, false otherwise.
     */
    public boolean writeImage(String filepath, Mat img) {
        return Imgcodecs.imwrite(filepath, img);
    }

    /**
     *
     * @param src
     * @return
     */
    public Mat applyGaussianBlur(Mat src) {
        assert src != null : "Invalid matrice: null value";

        Mat dest = new Mat();
        Size ksize = new Size(5,5);
        int sigmaX = 0;
        int sigmaY = 0;
        Imgproc.GaussianBlur(src, dest, ksize, sigmaX, sigmaY);
        return dest;
    }

    /**
     *
     * @param src
     * @return
     */
    public Mat applyOtsuBinarysation(Mat src) {
        assert src != null : "Invalid matrice: null value";

        Mat dest = new Mat();
        int thresh = 0;
        int maxValue = 255;
        Imgproc.threshold(src, dest, thresh, maxValue, Imgproc.THRESH_OTSU + Imgproc.THRESH_BINARY);
        return dest;
    }

    public Mat noiseSuppression(Mat src) {
        assert src != null : "Invalid matrice: null value";
        return applyOtsuBinarysation(applyGaussianBlur(src));
    }

//        Imgproc.adaptiveThreshold(m, m2, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);
}
