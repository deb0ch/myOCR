/**
 * Created by sal on 02/11/15.
 */

import org.opencv.core.Core;
import org.opencv.core.Mat;
import processing.pre.ImageManipulator;

import java.time.LocalTime;

public class OCRApplication
{
    private static final String USAGE = "java OCRApplication pathToFile";

    /**
     * The function that is call to start the java program
     * @param args the arguments given to the program
     */
    public static void main(String... args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        if (args.length >= 1)
        {
            String filepath = args[0];
            Mat m = ImageManipulator.loadImage(filepath);
            if (!m.empty())
            {
                ImageManipulator.writeImage("resources/res"+ LocalTime.now() +"jpg", ImageManipulator.noiseSuppression(m));
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
