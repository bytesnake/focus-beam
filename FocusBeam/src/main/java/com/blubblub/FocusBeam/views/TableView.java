package com.blubblub.FocusBeam.views;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.blubblub.FocusBeam.GameState;

public class TableView extends RelativeLayout {
    private GameState state;
    private Context context;

    public TableView(Context c, AttributeSet attrs) {
        super(c, attrs);

        context = c;

        // set a name
        setTag("Table View");

        //state = _state;

    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        TableLayout table = this.findViewWithTag("player1");
        for(int i = 0; i < table.getChildCount(); i++) {
            TableRow child = (TableRow)table.getChildAt(i);

            for(int j = 0; j < 7; j++) {
                TextView text1 = new TextView(context);
                text1.setText("0");
                text1.setTag(child.getTag() + ":" + j);

                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                p.weight = 1;
                p.width = 0;

                text1.setLayoutParams(p);
                child.addView(text1);
            }
        }

        table = this.findViewWithTag("player2");
        for(int i = 0; i < table.getChildCount(); i++) {
            TableRow child = (TableRow)table.getChildAt(i);

            for(int j = 0; j < 7; j++) {
                TextView text1 = new TextView(context);
                text1.setText("0");
                text1.setTag(child.getTag() + ":" + j);

                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                p.weight = 1;
                p.width = 0;

                text1.setLayoutParams(p);
                child.addView(text1);
            }
        }
    }

    /*@Override
    public void onLayout (boolean changed,
                          int l,
                          int t,
                          int r,
                          int b) {
    }*/
}
