package sample.ble.sensortag.ui.utils;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

/** Scroll behavior. */
public class ScrollBehavior extends CoordinatorLayout.Behavior<View> {
    /** FAB animation interpolator. */
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();

    /** Animation duration. */
    private final long animationDuration;

    /** Indicates whether FAB animating out, */
    private boolean animatingOut = false;

    public ScrollBehavior(Context context, AttributeSet attrs) {
        super();
        animationDuration = 300;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout,
            View child, View directTargetChild, View target, int nestedScrollAxes) {
        // Ensure we react to vertical scrolling
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
                || super.onStartNestedScroll(coordinatorLayout,
                child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout,
            View child, View target, int dxConsumed, int dyConsumed,
            int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target,
                dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (dyConsumed > 0 && !this.animatingOut && child.getVisibility() == View.VISIBLE) {
            // User scrolled down and the FAB is currently visible -> hide the FAB
            animateOut(child);
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            // User scrolled up and the FAB is currently not visible -> show the FAB
            animateIn(child);
        }
    }

    /**
     * Same animation that FloatingActionButton.Behavior uses to hide
     * the FAB when the AppBarLayout exits. */
    private void animateOut(View v) {
        final CoordinatorLayout.LayoutParams lp =
                (CoordinatorLayout.LayoutParams) v.getLayoutParams();
        final int fabBottom = lp.bottomMargin;
        ViewCompat.animate(v).setDuration(animationDuration)
                .translationY(v.getHeight() + fabBottom)
                .setInterpolator(INTERPOLATOR)
                .setListener(new ViewPropertyAnimatorListener() {
                    public void onAnimationStart(View view) {
                        ScrollBehavior.this.animatingOut = true;
                    }

                    public void onAnimationCancel(View view) {
                        ScrollBehavior.this.animatingOut = false;
                    }

                    public void onAnimationEnd(View view) {
                        ScrollBehavior.this.animatingOut = false;
                        view.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    /**
     * Same animation that FloatingActionButton.Behavior uses
     * to show the FAB when the AppBarLayout enters.
     */
    private void animateIn(View v) {
        v.setVisibility(View.VISIBLE);
        ViewCompat.animate(v).setDuration(animationDuration)
                .translationY(0.0F)
                .scaleX(1.0F)
                .scaleY(1.0F)
                .alpha(1.0F)
                .setInterpolator(INTERPOLATOR).withLayer().setListener(null)
                .start();
    }
}
