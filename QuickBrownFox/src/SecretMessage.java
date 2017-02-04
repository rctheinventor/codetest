import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by rchase on 10/3/2015.
 */
public class SecretMessage {

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
                word = bf.readLine().toLowerCase();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
            SecretMessage message = new SecretMessage(word);
        }
    }

    public String originalMessage = "";
    public String paddedMessage = "";
    ArrayList<ArrayList<String>> originalGrid = null;
    public int lvalue = 0;
    public int mvalue = 0;
    public int kvalue = 0;
    public SecretMessage(String message) {
        this.originalMessage = message;
        System.err.println("originalMessage = " + originalMessage);
        lvalue = originalMessage.length();
        System.err.println("lvalue = " + lvalue);
        mvalue = findMValue();
        System.err.println("mvalue = " + mvalue);
        buildPaddedMessage();
        System.err.println("paddedMessage = " + paddedMessage);
        buildOriginalGrid();
        readSecretMessage();
    }

    public int findMValue() {
        int testM = 1;
        while ((testM * testM) < lvalue) { testM++; }
        kvalue = testM;
        return (testM * testM);
    }

    public String buildPaddedMessage() {
        paddedMessage = new String(originalMessage);
        for (int i = 0; i < (mvalue - lvalue); i++) {
            paddedMessage = paddedMessage + "*";
        }
        return paddedMessage;
    }

    public void buildOriginalGrid() {
        originalGrid = new ArrayList<>(kvalue);
        int totalStringCounter = 0;
        for (int i = 0; i < kvalue; i++) {
            ArrayList<String> newList = new ArrayList<>(kvalue);
            // start inner for loop
            for (int j = 0; j < kvalue; j++) {
                newList.add(Character.toString(paddedMessage.charAt(totalStringCounter)));
                totalStringCounter++;
            }
            // end inner for loop
            originalGrid.add(newList);
        }
    }

    public void readSecretMessage () {
        String secretMessage = "";
        for (int i = 0; i < kvalue; i++) {
            for (int j = 0; j < kvalue; j++) {
                String stringOfInterest = originalGrid.get(kvalue - 1 - j).get(i);
                if (!stringOfInterest.equals("*")) {
                    secretMessage = secretMessage + stringOfInterest;
                }
            }
        }
        System.out.println(secretMessage);
    }
}
