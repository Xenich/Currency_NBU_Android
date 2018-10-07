package xenich.kursnbu;

import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * Created by Dedal_ on 30.10.2016.
 */

public class FragmentValutaList extends Fragment
{
    View fragm;
    LinearLayout frame_container;
    Button button;
    ProgressBar progBar;
    TextView dateText;
    Handler h;

        //@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

        //@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        fragm = inflater.inflate(R.layout.fragment_list_of_valutes, container, false);
        button = (Button)fragm.findViewById(R.id.buttonRequest);
        button.setOnClickListener(new View.OnClickListener()        // для фрагментов в XML нельзя назначать событие. только в коде
                                    {
                                        @Override
                                        public void onClick(View v)
                                        {
                                            try {
                                                ButtonClickGetCources(v);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                );
        progBar = (ProgressBar)fragm.findViewById(R.id.progressBar);
        frame_container =(LinearLayout)fragm.findViewById(R.id.frames_container);
        dateText = (TextView)fragm.findViewById(R.id.textDate);
        return fragm;
    }

    public void ButtonClickGetCources(View v) throws Exception
    {
        progBar.setVisibility(View.VISIBLE);

        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected())
        {
            DownloadWebpageTask downloadJSON = new DownloadWebpageTask();
            downloadJSON.execute();
        }
        else
        {
            button.setText("Немає підключення до інтернету");
        }
    }

    // AsyncTask<[Input Parameter Type], [Progress Report Type], [Result Type]>
    private class DownloadWebpageTask extends AsyncTask<Void, Void,Void>
    {
        GetJSON getJSONToday;
        GetJSON getJSONYesterday;
            // выполняется перед doInBackground, имеет доступ к UI
        @Override
        protected void onPreExecute()
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            Integer year = cal.get(Calendar.YEAR);
            Integer month = cal.get(Calendar.MONTH) + 1;
            Integer day = cal.get(Calendar.DAY_OF_MONTH);
            String date = year.toString()+month.toString()+day.toString();
            dateText.setText(date);
            getJSONToday= new GetJSON();
            getJSONToday.SetURL("http://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json");
            getJSONYesterday= new GetJSON();
            getJSONYesterday.SetURL("http://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?date="+ date +"&json");
        }
            //будет выполнен в новом потоке, здесь решаем все свои тяжелые задачи. Т.к. поток не основной - не имеет доступа к UI. Реализуется обязательно
        @Override
        protected Void doInBackground(Void... params)
        {
            Thread tToday = new Thread(getJSONToday);
            Thread tYester = new Thread(getJSONYesterday);
            tToday.start();
            tYester.start();
            try
            {
                tToday.join();
                tYester.join();
            }
            catch(Exception e)
            {
                Log.d("TAG",e.getMessage());
            }
            return null;
        }

        // выполняется после doInBackground (не срабатывает в случае, если AsyncTask был отменен), имеет доступ к UI
        @Override
        protected void onPostExecute (Void result)
        {

            TreeMap<String,Currency> currencyDictionary = new TreeMap<String,Currency>();       // словарь: (код вылюты, экземпляр класса Currency)
            HashMap<String,Currency> prevCurrencyDictionary = new HashMap<String,Currency>();   // словарь со вчерашними значениями: (код вылюты, экземпляр класса Currency)

            String strJson = "{\"currency\":" + getJSONToday.Get()+"}";
            String strJsonPrev = "{\"currency\":" + getJSONYesterday.Get()+"}";

            JSONArray currencyList = null;
            JSONArray currencyListPrev = null;
            try
            {
                JSONObject dataJsonObj = new JSONObject(strJson);
                currencyList = dataJsonObj.getJSONArray("currency");

                JSONObject dataJsonObjPrev = new JSONObject(strJsonPrev);
                currencyListPrev = dataJsonObjPrev.getJSONArray("currency");
                    // заполняем сегодняшние курсы
                for(int i=0; i<currencyList.length(); i++)
                {
                    Currency curr = new Currency();
                    curr.txt = currencyList.getJSONObject(i).getString("txt");
                    curr.cc = currencyList.getJSONObject(i).getString("cc");
                    curr.exchangedate = currencyList.getJSONObject(i).getString("exchangedate");
                    curr.r030 =currencyList.getJSONObject(i).getString("r030");
                    curr.rate = currencyList.getJSONObject(i).getDouble("rate");
                    currencyDictionary.put(curr.cc,curr);
                }
                    // заполняем вчерашние курсы
                for(int i=0; i<currencyListPrev.length(); i++)
                {
                    Currency curr = new Currency();
                    curr.txt = currencyListPrev.getJSONObject(i).getString("txt");
                    curr.cc = currencyListPrev.getJSONObject(i).getString("cc");
                    curr.exchangedate = currencyListPrev.getJSONObject(i).getString("exchangedate");
                    curr.r030 =currencyListPrev.getJSONObject(i).getString("r030");
                    curr.rate = currencyListPrev.getJSONObject(i).getDouble("rate");
                    prevCurrencyDictionary.put(curr.cc,curr);
                }

                frame_container.removeAllViews();
                Iterator<String> itr = currencyDictionary.keySet().iterator();
                String key;
                while (itr.hasNext())
                {
                    key = itr.next();
                    UserControlValuta valuta = new UserControlValuta(getActivity().getApplicationContext());
                    valuta.SetValutaName(currencyDictionary.get(key).cc);
                    valuta.SetCountryName(currencyDictionary.get(key).txt);
                    valuta.SetCourse(currencyDictionary.get(key).rate);
                    valuta.SetFlag("f"+currencyDictionary.get(key).r030);
                    try
                    {
                        if ((currencyDictionary.get(key).rate) < (prevCurrencyDictionary.get(key).rate)) {
                            valuta.SetUpOrDown("down");
                        }
                        if ((currencyDictionary.get(key).rate) > (prevCurrencyDictionary.get(key).rate)) {
                            valuta.SetUpOrDown("up");
                        }
                        if ((currencyDictionary.get(key).rate) == (prevCurrencyDictionary.get(key).rate)) {
                            valuta.SetUpOrDown("nogrow");
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    frame_container.addView(valuta);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            progBar.setVisibility(View.INVISIBLE);
        }
    }
}
