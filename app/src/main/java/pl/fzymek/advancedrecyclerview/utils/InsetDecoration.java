package pl.fzymek.advancedrecyclerview.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import pl.fzymek.advancedrecyclerview.R;

/**
 * Created by Filip Zymek on 2015-06-22.
 */
public class InsetDecoration extends RecyclerView.ItemDecoration {

	private int margin;

	public InsetDecoration(Context context) {
		margin = context.getResources().getDimensionPixelOffset(R.dimen.grid_item_margin);
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		outRect.set(margin, margin, margin, margin);
	}
}
