package xenich.kursnbu;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Dedal_ on 23.10.2016.
 */

public class UserControlValuta extends GridLayout
{
    private TextView countryName;
    private TextView nameOfValute;
    private TextView course;
    private ImageView flag;
    private ImageView updown;
        // дефолтный конструктор
    public UserControlValuta(Context context)
    {
        super(context);
        initComponent();
    }

        // Для того, что бы соединить наш класс и лейаут channel_layout, используем LayoutInflater.
        // Так же мы внутри класса определяем переменные для всех полей что бы связать поля класса с UI.
    private void initComponent()
    {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.usercontrolvaluta, this);

        nameOfValute = (TextView) findViewById(R.id.valutaName);
        countryName = (TextView) findViewById(R.id.countryName);
        course = (TextView) findViewById(R.id.course);
        flag = (ImageView)findViewById(R.id.flag);
        updown= (ImageView)findViewById(R.id.up_or_down);
    }

    public void SetValutaName(String name)
    {
        nameOfValute.setText(name);
    }

    public void SetCountryName(String name)
    {
        countryName.setText(name);
    }

    public void SetCourse(Double courseD)
    {
        course.setText(String.format("%.5f", courseD));
    }

    public void SetFlag(String valutaName)
    {
       try
        {
            int res = getResources().getIdentifier(valutaName, "drawable", "xenich.kursnbu");
            flag.setImageResource(res);

        }
        finally
        {

        }
    }

    public void SetUpOrDown(String name)
    {
        try
        {
            int res = getResources().getIdentifier(name, "drawable", "xenich.kursnbu");
            updown.setImageResource(res);
        }
        finally
        {

        }
    }
}
