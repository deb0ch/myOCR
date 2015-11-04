/**
 * Created by sal on 02/11/15.
 */

import org.opencv.core.Core;
import org.opencv.core.Mat;
import processing.pre.ImageManipulator;

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
            if (m == null) {
                System.out.println("1");

            } else {
                System.out.println(m);
                System.out.println(m.dump());
                System.out.println(m.dataAddr() == 0x0);
                System.out.println(m.empty());
            }
        }
        else
        {
            System.out.println(USAGE);
        }
    }
}
