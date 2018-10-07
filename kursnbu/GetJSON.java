package xenich.kursnbu;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Dedal_ on 04.11.2016.
 */

public class GetJSON implements Runnable
{
    private String url;
    private String JSON;
    public void SetURL(String url)
    {
        this.url=url;
    }
    public String Get()
    {
        return JSON;
    }
    @Override
    public void run()
    {
        try
        {
            URLConnection connection = new URL(url).openConnection();
            InputStream is = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(is);
            char[] buffer = new char[256];
            int len=0;
            StringBuilder sb = new StringBuilder();
            while ((len = reader.read(buffer)) != -1)        // возвращает число символов, добавленных к буферу, или-1, если конец
                sb.append(buffer, 0, len);
            reader.close();
            JSON = sb.toString();
        }
        catch (IOException e)
        {
            Log.d("TAG",e.getMessage());
        }

    }


}
