import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * Created by rchase on 10/3/2015.
 */
public class BobbyBet {

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
            BobbyBet bet = new BobbyBet(word);
        }
    }

    public int rval,sval,xval,yval,wval;

    public BobbyBet(String line) {
        StringTokenizer tk = new StringTokenizer(line);
        rval = Integer.parseInt(tk.nextToken());
        sval = Integer.parseInt(tk.nextToken());
        xval = Integer.parseInt(tk.nextToken());
        yval = Integer.parseInt(tk.nextToken());
        wval = Integer.parseInt(tk.nextToken());

        double bobbychance = ((double)sval - (double)rval + 1) / sval;
        System.err.println("bobbychance = " + bobbychance);

        int numberOfWins = (int)(bobbychance * yval);
        System.err.println("numberOfWins = " + numberOfWins);

        int payout = numberOfWins * wval;
        System.err.println("payout = " + payout);

        if (payout > wval) {
            System.out.println("yes");
        }
        else {
            System.out.println("no");
        }
    }

}
