import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 运行其他程序（比如危险木马）0
 */
public class Main{
    public static void main(String[] args) throws IOException {
        String userDir = System.getProperty("user.dir");
        String filePath = userDir + File.separator + "src/main/resources/horse.bat";
        Process process = Runtime.getRuntime().exec(filePath);
        String compileOutLine;
        while( (compileOutLine = bufferedReader.readLine()) != null){
            System.out.println(compileOutLine);
        }
    }
}

