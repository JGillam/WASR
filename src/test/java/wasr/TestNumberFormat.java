package wasr;

import java.text.DecimalFormat;

/**
 * User: Jason Gillam
 * Date: 7/21/13
 * Time: 8:51 AM
 */
public class TestNumberFormat {
    public static void main(String[] args) {
        DecimalFormat df = new DecimalFormat("000");

        for(int i=0;i<120;i++){
            String stringFormat = df.format(i);
            System.out.println(stringFormat+" : "+Integer.parseInt(stringFormat));
        }
    }
}
