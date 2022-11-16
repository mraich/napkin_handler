package com.mrichard.napkin_handler.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class
FontManager
{

    protected static final String ROOT = "fonts/";
    public static final String FONT_AWESOME = ROOT + "fa-solid-900.ttf"; // v5.2.0 solid.

    public static Typeface GetTypeface(Context context, String font)
    {
        return Typeface.createFromAsset(context.getAssets(), font);
    }

    public static void MarkAsIconContainer(View view, String font)
    {
        Typeface typeface = GetTypeface(view.getContext(), font);
        MarkAsIconContainer(view, typeface);
    }

    public static void MarkAsIconContainer(View v, Typeface typeface)
    {
        if (v instanceof ViewGroup)
        {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++)
            {
                View child = vg.getChildAt(i);
                MarkAsIconContainer(child, typeface);
            }
        } else if (v instanceof TextView)
        {
            ((TextView) v).setTypeface(typeface);
        }
    }

}
