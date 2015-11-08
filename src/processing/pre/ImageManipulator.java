package processing.pre;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sal on 02/11/15.
 */
public class ImageManipulator
{
    /**
     * This function aims to verify that a filepath is valid to load an image.
     * @param filepath the path to verify
     * @return true if the path is not null, the file exists and it's an image, false otherwise.
     */
    private static boolean isAValidFilePath(String filepath)
    {
        if (filepath == null) return false;
        File f = new File(filepath);
        return f.isFile() && f.exists();
    }

    /**
     *
     * @param src
     * @return
     */
    public static Mat applyGaussianBlur(Mat src)
    {
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
    public static Mat applyOtsuBinarysation(Mat src)
    {
        assert src != null : "Invalid matrice: null value";

        Mat dest = new Mat();
        int thresh = 0;
        int maxValue = 255;
        Imgproc.threshold(src, dest, thresh, maxValue, Imgproc.THRESH_OTSU + Imgproc.THRESH_BINARY);
        return dest;
    }

    public static Mat noiseSuppression(Mat src)
    {
        assert src != null : "Invalid matrice: null value";
        return applyOtsuBinarysation(applyGaussianBlur(src));
    }

    public static Mat calculateHistogram(Mat src)
    {
        Mat tmp = new Mat();
        List<Mat> imgs = new LinkedList<>();
        imgs.add(src);
        int histSize = 256;
        Imgproc.calcHist(imgs, new MatOfInt(0), new Mat(), tmp, new MatOfInt(histSize), new MatOfFloat(0,256));
        int hist_w = 512, hist_h = 400;
        int bin_w = hist_w/histSize;

        Mat histImage = new Mat(hist_h, hist_w, CvType.CV_8UC3, new Scalar(255, 255, 255));
        Core.normalize(tmp, tmp, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());
        for (int i = 1; i < histSize; i++)
        {
            Imgproc.line(histImage,
                    new Point(bin_w * (i-1), hist_h - tmp.get(i-1, 0)[0]),
                    new Point(bin_w * (i), hist_h - tmp.get(i, 0)[0]),
                    new Scalar(0, 0, 0), 2, 8, 0);
        }
        return histImage;
    }

    public static Mat manualCalculationHistogramColumns(Mat src)
    {
        int[] counts = new int[src.cols()];
        for (int row = 0; row < src.rows(); row++)
        {
            for (int col = 0; col < src.cols(); col++) {
                if (src.get(row, col)[0] == 0d) // is black
                {
                    counts[col] += 1;
                }
            }
        }

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int count : counts) {
            min = Math.min(min, count);
            max = Math.max(max, count);
        }

//        StringBuilder sb = new StringBuilder();
        Mat histogram = new Mat(max, counts.length, CvType.CV_8UC3, new Scalar(255d, 255d, 255d));
        for (int i = 0; i < counts.length; i++) {
            Imgproc.line(histogram,
                    new Point(i, 0),
                    new Point(i, counts[i]),
                    new Scalar(0, 0, 0),
                    2, 8, 0);
//            sb.append(counts[i]);
//            if (counts.length -1 != i) sb.append(',');
        }
//        System.out.println(sb.toString());
        return histogram;
    }

    public static Mat manualCalculationHistogramRows(Mat src)
    {
        int[] counts = new int[src.rows()];

        for (int col = 0; col < src.cols(); col++) {
            for (int row = 0; row < src.rows(); row++)
            {
                if (src.get(row, col)[0] == 0d) // is black
                {
                    counts[row] += 1;
                }
            }
        }
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int count : counts) {
            min = Math.min(min, count);
            max = Math.max(max, count);
        }

//        StringBuilder sb = new StringBuilder();
        Mat histogram = new Mat(counts.length, max, CvType.CV_8UC3, new Scalar(255d, 255d, 255d));
        for (int i = 0; i < counts.length; i++) {
            Imgproc.line(histogram,
                    new Point(0, i),
                    new Point(counts[i], i),
                    new Scalar(0, 0, 0),
                    2, 8, 0);
//            sb.append(counts[i]);
//            if (counts.length -1 != i) sb.append(',');
        }
//        System.out.println(sb.toString());
        return histogram;
    }
//        Imgproc.adaptiveThreshold(m, m2, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);
}
