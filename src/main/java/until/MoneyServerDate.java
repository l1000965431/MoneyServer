package until;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by liumin on 15/7/7.
 */
public class MoneyServerDate {

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

     public static String getStringCurDate(){

        String timeStr = sdf.format(new Date());

        return timeStr;
    }

    public static Date getDateCurDate() throws ParseException {
        String timeStr = sdf.format(new Date());
        return sdf.parse( timeStr );
    }

    public static Date StrToDate(String str) {
        Date date = null;
        try {
            date = sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static SimpleDateFormat getSdf(){
        return sdf;
    }

}
