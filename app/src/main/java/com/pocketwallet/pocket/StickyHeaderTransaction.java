package com.pocketwallet.pocket;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.TimeZone;

public class StickyHeaderTransaction extends RecyclerView.ItemDecoration {

    private final int headerOffset;
    private final boolean sticky;
    private final SectionCallback sectionCallback;

    private View headerView;
    private TextView header;
    CharSequence previousHeader = "";

    public StickyHeaderTransaction(int headerHeight, boolean sticky, @NonNull SectionCallback sectionCallback) {
        headerOffset = headerHeight;
        this.sticky = sticky;
        this.sectionCallback = sectionCallback;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int pos = parent.getChildAdapterPosition(view);
        if (sectionCallback.isSection(pos)) {
            outRect.top = headerOffset;
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c,
                parent,
                state);

        if (headerView == null) {
            headerView = inflateHeaderView(parent);
            header = (TextView) headerView.findViewById(R.id.list_item_section_text);
            fixLayoutSize(headerView,
                    parent);
        }


        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            final int position = parent.getChildAdapterPosition(child);
            CharSequence title = sectionCallback.getSectionHeader(position);

            header.setText(title);
           // title = when[monthNum];
            if (!previousHeader.equals(title) /*|| sectionCallback.isSection(position)*/) {
                drawHeader(c, child, headerView);
                previousHeader = title;
                System.out.println();
                //System.out.println("Hello1");
               // System.out.println("Title: " + title + " | PrevHeader: " + previousHeader.toString());

            }else {
               // System.out.print("Hello2");
              //  System.out.println("");
               // System.out.println("Title: " + title + " | PrevHeader: " + previousHeader.toString());
            }
        }
    }

    private void drawHeader(Canvas c, View child, View headerView) {
        c.save();
        if (sticky) {
            c.translate(0,
                    Math.max(0,
                            child.getTop() - headerView.getHeight()));
           // System.out.println("                If sticky translate where getTop is " + child.getTop() + " and getHeight " + headerView.getHeight());
        } else {
            c.translate(0,
                    child.getTop() - headerView.getHeight());
            //System.out.println("                else translate child.getTop");
        }
        headerView.draw(c);
        c.restore();
    }

    private View inflateHeaderView(RecyclerView parent) {
        return LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_section_header,
                        parent,
                        false);
    }

    /**
     * Measures the header view to make sure its size is greater than 0 and will be drawn
     * https://yoda.entelect.co.za/view/9627/how-to-android-recyclerview-item-decorations
     */
    private void fixLayoutSize(View view, ViewGroup parent) {
        int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(),
                View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(),
                View.MeasureSpec.UNSPECIFIED);

        int childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                parent.getPaddingLeft() + parent.getPaddingRight(),
                view.getLayoutParams().width);
        int childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
                parent.getPaddingTop() + parent.getPaddingBottom(),
                view.getLayoutParams().height);

        view.measure(childWidth,
                childHeight);

        view.layout(0,
                0,
                view.getMeasuredWidth(),
                view.getMeasuredHeight());
    }

    public interface SectionCallback {

        boolean isSection(int position);

        CharSequence getSectionHeader(int position);
    }
}