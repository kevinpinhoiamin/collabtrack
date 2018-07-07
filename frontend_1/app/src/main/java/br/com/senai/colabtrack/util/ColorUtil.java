package br.com.senai.colabtrack.util;

import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;

import java.util.Map;
import java.util.Random;

import br.com.senai.colabtrack.ColabTrackApplication;
import br.com.senai.colabtrack.R;

/**
 * Created by kevin on 31/05/17.
 */

public class ColorUtil {

    public static int getColor(int color){
        return Color.parseColor(String.format("#%06X", 0xFFFFFF & color));
    }

    public static int getOpacityColor(int color){
        return Color.parseColor(String.format("#%06X", 0x55FFFFFF & color));
    }

    public static int getRandomColor(){

        Integer colors[] = new Integer[]{R.color.randomColor1,
                                         R.color.randomColor2,
                                         R.color.randomColor3,
                                         R.color.randomColor4,
                                         R.color.randomColor5,
                                         R.color.randomColor6,
                                         R.color.randomColor7,
                                         R.color.randomColor8,
                                         R.color.randomColor9,
                                         R.color.randomColor10,
                                         R.color.randomColor11,
                                         R.color.randomColor12,
                                         R.color.randomColor13,
                                         R.color.randomColor14,
                                         R.color.randomColor15,
                                         R.color.randomColor16,
                                         R.color.randomColor17,
                                         R.color.randomColor18,
                                         R.color.randomColor19};

        int randomPosition = new Random().nextInt(colors.length);
        return ResourcesCompat.getColor(ColabTrackApplication.getContext().getResources(), colors[randomPosition], null);
    }

}
