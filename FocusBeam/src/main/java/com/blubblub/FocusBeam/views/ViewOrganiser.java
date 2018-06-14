package com.blubblub.FocusBeam.views;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.blubblub.FocusBeam.GameState;
import com.blubblub.FocusBeam.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Inflater;


public class ViewOrganiser {
    private List<View> views;
    private RelativeLayout inner;

    public ViewOrganiser(Context c, GameState state) {
        Activity act = (Activity)c;
        LayoutInflater inflater = LayoutInflater.from(c);
        inner = act.findViewById(R.id.app_view);
        views = Arrays.asList(
                new GameFieldView(c, null, state),
                new ElectrodesView(c, null, state),
                inflater.inflate(R.layout.view_table, null)
        );

        //views.

        // add first view at the beginning
        inner.addView(views.get(0));
    }

    public void switchView(String name) {
        inner.removeAllViews();

        for(View view : views) {
            String view_name = (String)view.getTag();

            if(view_name.equals(name)) {
                inner.addView(view);
                return;
            }

        }
    }

    public List<String> getNames() {
        List<String> names = new ArrayList<>();

        for(View view : views)
            names.add((String)view.getTag());

        return names;
    }

    public void update() {
        inner.getChildAt(0).invalidate();
    }
}
