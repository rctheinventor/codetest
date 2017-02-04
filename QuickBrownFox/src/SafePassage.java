import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by rchase on 10/3/2015.
 */
public class SafePassage {

    public static void main(String[] args) {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));


        String sequence = "";
        try {
            sequence = bf.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        SafePassage passage = new SafePassage(sequence);
    }

    String passage = null;
    public SafePassage (String p) {
        this.passage = p;
    }
}
