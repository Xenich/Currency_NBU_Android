package xenich.kursnbu;

/**
 * Created by Dedal_ on 04.11.2016.
 */

public class JSONAnswer
{
    public static String todayCurrency;
    public static String yesterdayCurrency;

    public synchronized static void SetTodayCurrency(String JSON)
    {
        todayCurrency = JSON;
    }
    public synchronized static void SetYesterdayCurrency(String JSON)
    {
        yesterdayCurrency=JSON;
    }
}
