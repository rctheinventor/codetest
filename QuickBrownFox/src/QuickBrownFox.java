import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class QuickBrownFox {

    public static void main(String[] args) {
	    BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        Integer numLines = 0;
        try {
            numLines = Integer.parseInt(bf.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < numLines; i++) {
            String word = "";
            try {
                word=bf.readLine().toLowerCase();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }

            QuickBrownFox fox = new QuickBrownFox(word);
        }


    }

    public List<String> alphabetV = new CopyOnWriteArrayList<>();
    public String word;
    public QuickBrownFox(String word) {
        initializeAlphabetHash();
        this.word = word;
        findNonUsedChars();
        printResult();
    }

    private void initializeAlphabetHash() {
        String alphabet ="a b c d e f g h i j k l m n o p q r s t u v w x y z";
        StringTokenizer st = new StringTokenizer(alphabet);
        while (st.hasMoreTokens()) {
            String currentToken = st.nextToken();
           // System.err.println(currentToken);
            alphabetV.add(currentToken);
        }
    }

    private void findNonUsedChars() {
        for (String currentChar : alphabetV) {
            if (word.contains(currentChar)) {
               //System.err.println("Removing" + currentChar);
                alphabetV.remove(currentChar);
            }
        }
    }

    private void printResult() {
        if (alphabetV.isEmpty()) {
            System.out.println("pangram");
            }
        else {
            System.out.print("missing  ");
            for (String currentChar : alphabetV) {
                System.out.print (currentChar);
            }
            System.out.println();
        }
    }

}
