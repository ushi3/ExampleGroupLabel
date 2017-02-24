package com.ushi.example.groupLabel.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.View;

import com.ushi.example.groupLabel.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * TODO for LinearLayoutManager with vertical orientation.
 *
 * @author ushi
 * @see com.ushi.example.groupLabel.R.styleable#GroupingItemDecoration_android_background
 * @see com.ushi.example.groupLabel.R.styleable#GroupingItemDecoration_android_textSize
 * @see com.ushi.example.groupLabel.R.styleable#GroupingItemDecoration_android_textColor
 * @see com.ushi.example.groupLabel.R.styleable#GroupingItemDecoration_android_padding
 * @see com.ushi.example.groupLabel.R.styleable#GroupingItemDecoration_android_paddingLeft
 * @see com.ushi.example.groupLabel.R.styleable#GroupingItemDecoration_android_paddingRight
 * @see com.ushi.example.groupLabel.R.styleable#GroupingItemDecoration_android_paddingTop
 * @see com.ushi.example.groupLabel.R.styleable#GroupingItemDecoration_android_paddingBottom
 * @see com.ushi.example.groupLabel.R.styleable#GroupingItemDecoration_fontPath
 */
public class GroupingItemDecoration extends RecyclerView.ItemDecoration {

    private final TextPaint mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

    private final Drawable mBackgroundDrawable;

    private final RectF mPadding = new RectF();

    public GroupingItemDecoration(Context context) {
        this(context, R.style.GroupingItemDecoration_Label);
    }

    public GroupingItemDecoration(Context context, @StyleRes int styleRes) {
        TypedArray ta = context.obtainStyledAttributes(styleRes, R.styleable.GroupingItemDecoration);

        mBackgroundDrawable = ta.getDrawable(R.styleable.GroupingItemDecoration_android_background);

        int textSize = ta.getDimensionPixelSize(R.styleable.GroupingItemDecoration_android_textSize, 0);
        mTextPaint.setTextSize(textSize);

        int textColor = ta.getColor(R.styleable.GroupingItemDecoration_android_textColor, 0);
        mTextPaint.setColor(textColor);

        if (ta.hasValue(R.styleable.GroupingItemDecoration_fontPath)) {
            String fontPath = ta.getString(R.styleable.GroupingItemDecoration_fontPath);
            Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontPath);
            mTextPaint.setTypeface(typeface);
        }

        if (ta.hasValue(R.styleable.GroupingItemDecoration_android_padding)) {
            int padding = ta.getDimensionPixelSize(R.styleable.GroupingItemDecoration_android_padding, 0);
            mPadding.set(padding, padding, padding, padding);

        } else {
            mPadding.set(
                    ta.getDimensionPixelSize(R.styleable.GroupingItemDecoration_android_paddingLeft, 0),
                    ta.getDimensionPixelSize(R.styleable.GroupingItemDecoration_android_paddingTop, 0),
                    ta.getDimensionPixelSize(R.styleable.GroupingItemDecoration_android_paddingRight, 0),
                    ta.getDimensionPixelSize(R.styleable.GroupingItemDecoration_android_paddingBottom, 0));
        }

        ta.recycle();
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        Categorizable categorizable = getCategorizableFrom(parent.getAdapter());
        if (categorizable == null) {
            return;
        }

        List<Label> labels = makeLabels(parent, categorizable);
        if (labels.isEmpty()) {
            return;
        }

        int labelHeight = getLabelHeight();

        if (!labels.isEmpty()) {
            // fixed firstLabel in parent top
            Label topLabel = labels.get(0);
            topLabel.top = Math.max(topLabel.top, 0);

            if (labels.size() >= 2) {
                // push up Top-Label position, if Below-Label collides with Top-Label.
                Label bellowLabel = labels.get(1);

                float bottomOfTopLabel = topLabel.top + labelHeight;
                if (bottomOfTopLabel > bellowLabel.top) {
                    topLabel.top -= bottomOfTopLabel - bellowLabel.top;
                }
            }
        }

        if (mBackgroundDrawable != null) {
            mBackgroundDrawable.setBounds(0, 0, canvas.getWidth(), labelHeight);
        }

        float textCanvasWidth = canvas.getWidth() - mPadding.left - mPadding.right;

        // correct text position (text drawing positionY is aligned to leading line)
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float textBaseY = fontMetrics.leading - fontMetrics.top;

        for (Label label : labels) {
            // draw labels
            int save = canvas.save();

            canvas.translate(0, label.top);
            if (mBackgroundDrawable != null) {
                mBackgroundDrawable.draw(canvas);
            }

            // padding
            canvas.translate(mPadding.left, mPadding.top);
            canvas.clipRect(0, 0, textCanvasWidth, labelHeight);  // TODO end chars are cut.

            canvas.drawText(label.name.toString(), 0, textBaseY, mTextPaint);

            canvas.restoreToCount(save);
        }
    }

    private List<Label> makeLabels(RecyclerView parent, Categorizable categorizable) {
        int labelHeight = getLabelHeight();
        ArrayList<Label> labels = new ArrayList<>();
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(child);

            CharSequence c1 = categorizable.getItemGroupName(position);
            if (c1 == null) {
                continue;
            }

            if (i > 0 && position > 0) {
                CharSequence c2 = categorizable.getItemGroupName(position - 1);
                if (Objects.equals(c1, c2)) {
                    // previous item has duplicate label
                    continue;
                }
            }

            labels.add(new Label(c1, child.getTop() - labelHeight));
        }

        return labels;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        Categorizable categorizable = getCategorizableFrom(parent.getAdapter());
        if (categorizable == null) {
            return;
        }

        int position = parent.getChildAdapterPosition(view);
        CharSequence c1 = categorizable.getItemGroupName(position);

        if (position > 0) {
            CharSequence c2 = categorizable.getItemGroupName(position - 1);
            if (Objects.equals(c1, c2)) {
                return;
            }
        }

        outRect.top = getLabelHeight();
    }

    private int getLabelHeight() {
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float textHeight = fontMetrics.bottom - fontMetrics.top;
        return (int) (textHeight + mPadding.top + mPadding.bottom);
    }

    @Nullable
    private static Categorizable getCategorizableFrom(Object object) {
        if (object instanceof Categorizable) {
            return (Categorizable) object;
        }

        return null;
    }

    public interface Categorizable {

        /**
         * @param position Adapter's item position (>=0)
         * @return category name for grouping item. if null, mean no-group.
         */
        @Nullable
        CharSequence getItemGroupName(int position);
    }

    static final class Label {

        // top of label position
        float top;

        CharSequence name;

        Label(@NonNull CharSequence name, float top) {
            this.name = name;
            this.top = top;
        }
    }
}
