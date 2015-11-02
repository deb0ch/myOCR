package sample;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Main {

    public static void main(String... args) {
        System.out.println(System.getProperty("java.library.path"));

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat m = Imgcodecs.imread("/home/sal/img1.jpg", Imgcodecs.IMREAD_GRAYSCALE);
        Imgcodecs.imwrite("/home/sal/img1_gray.jpg", m);
        Mat m2 = new Mat();
        Imgproc.adaptiveThreshold(m, m2, 255,Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);
        Imgcodecs.imwrite("/home/sal/img1_binary.jpg", m2);
    }
}
