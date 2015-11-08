/**
 * Created by sal on 02/11/15.
 */

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import processing.pre.ImageManipulator;

import java.time.LocalTime;

public class OCRApplication
{
    private static final String USAGE = "java OCRApplication pathToFile";

    /**
     * The function that is call to start the java program
     * @param args the arguments given to the program
     */
    public static void main(String... args) throws InterruptedException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        if (args.length >= 1)
        {
            String filepath = args[0];
            Mat m = Imgcodecs.imread(filepath, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE); // threshold cannot apply on colored img
            if (!m.empty())
            {
                String extension = args[0].substring(args[0].length() - 4);

//                Imgcodecs.imwrite("/tmp/res" + LocalTime.now() + extension, m);
//                Thread.sleep(1L);
//                Imgcodecs.imwrite("/tmp/res" + LocalTime.now() + extension, ImageManipulator.calculateHistogram(m));
                Mat tmp = ImageManipulator.noiseSuppression(m);
//                Thread.sleep(1L);
                Imgcodecs.imwrite("/tmp/res" + LocalTime.now() + extension, tmp);
                Thread.sleep(1000L);
                Imgcodecs.imwrite("/tmp/res" + LocalTime.now() + extension, ImageManipulator.calculateHistogram2(tmp));
            }
            else
            {
                System.err.println("File is not an image");
            }
        }
        else
        {
            System.out.println(USAGE);
        }
    }
}
