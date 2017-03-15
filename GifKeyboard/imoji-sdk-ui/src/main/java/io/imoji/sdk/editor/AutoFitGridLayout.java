/*
 * Imoji Android SDK UI
 * Created by sajjadtabib
 *
 * Copyright (C) 2016 Imoji
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KID, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 */

package io.imoji.sdk.editor;

import android.content.Context;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sajjadtabib on 9/30/14.
 */
public class AutoFitGridLayout  extends android.support.v7.widget.GridLayout{

    private boolean mFirstPass;

    public AutoFitGridLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mFirstPass = true;
    }

    public AutoFitGridLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoFitGridLayout(Context context) {
        this(context, null);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        mFirstPass = true;
        super.addView(child, index, params);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);

        if(mFirstPass){
            mFirstPass = false;
            int width = getMeasuredWidth();
            int columnCount = getColumnCount();

            //update each of the child views
            int column = 0; //currentColumn
            int row = 0;    //currentRow
            int spaceAvailable = width;

            int specWidth = spaceAvailable / columnCount; //The spec width

            int childCount = getChildCount();

            for(int i = 0; i < childCount; i++){
                View child = getChildAt(i);
                if(child.getVisibility() == GONE)
                    continue;

                int childWidth = child.getMeasuredWidth(); //get the child's measure width

                //figure out the spec & whether we have available space
                int spec = Math.min(Math.max((int)Math.ceil((float)childWidth / (float)specWidth), 1), columnCount);

                //do we have childWidth
                if(childWidth > spaceAvailable || (column + spec) > columnCount){
                    ++row;
                    column = 0;
                    spaceAvailable = width;
                }

                LayoutParams params = (LayoutParams)child.getLayoutParams();

                //column spec
                Spec columnSpec = GridLayout.spec(column, spec);
                params.columnSpec = columnSpec;

                //row spec
                Spec rowSpec = GridLayout.spec(row);
                params.rowSpec = rowSpec;

                child.setLayoutParams(params);

                //update the avialable space
                spaceAvailable -= childWidth;
                column += spec;

                if(column == columnCount){
                    column = 0; //reset the column to the first column
                    spaceAvailable = width; //reset the amount of space available
                    ++row; //increase the row number
                }

            }
            requestLayout();
        }
    }

}
