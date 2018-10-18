import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-09-22 10:38
 **/
public class TestCallPython {

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        String address = System.getProperty("user.dir");
        System.out.println(address);
        String exe = "python";
        String command = System.getProperty("user.dir") + "\\eil-project-dev\\src\\main\\resources\\py\\Water_Model.py";
        List<String> list = Arrays.asList(exe, command, "Thomas_Mode", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "12");

        String[] parmass = (String[]) list.toArray();
        Process process = Runtime.getRuntime().exec(parmass);
        InputStream is = process.getInputStream();
        DataInputStream dis = new DataInputStream(is);
        String str = dis.readLine();
        process.waitFor();
        System.out.println(str);
    }
}
