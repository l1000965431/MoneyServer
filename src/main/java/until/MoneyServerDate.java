package until;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    public static Date getDateCurDate() {
        String timeStr = sdf.format(new Date());
        try {
            return sdf.parse( timeStr );
        } catch (ParseException e) {
            return new Date();
        }
    }

    public static Date getDatePreDate(){
        Date dNow = new Date();
        Date dBefore;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dNow);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        dBefore = calendar.getTime();
        String defaultStartDate = sdf.format(dBefore);
        try {
            return sdf.parse( defaultStartDate );
        } catch (ParseException e) {
            return null;
        }
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
