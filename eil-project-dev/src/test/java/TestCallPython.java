import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

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
    public static void main(String[] args) throws IOException,InterruptedException

    {
       String address = System.getProperty("user.dir");
        System.out.println(address);
        String exe = "python";
        String command = System.getProperty("user.dir")+"\\eil-project-dev\\src\\main\\resources\\py\\test.py";
        String num1 = "1";
        String num2 = "2";
        String[] cmdArr = new String[]{exe,command,num1,num2};
        Process process = Runtime.getRuntime().exec(cmdArr);
        InputStream is = process.getInputStream();
        DataInputStream dis = new DataInputStream(is);
        String str = dis.readLine();
        process.waitFor();
        System.out.println(str);
    }
}
