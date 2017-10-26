package android.support.v7.widget;

import android.graphics.Rect;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import static android.support.v7.widget.RecyclerView.NO_POSITION;
/**
 * Created by xmuSistone on 2017/9/20.
 */
public class VegaLayoutManager extends RecyclerView.LayoutManager {
    public static final int HORIZONTAL = OrientationHelper.HORIZONTAL;
    private int mDecoratedMeasuredWidth;
    private int mDecoratedMeasuredHeight;
    private int scroll = 0;
    private SparseArray<Rect> locationRects = new SparseArray<>();
    private SparseArray<Boolean> attachedItems = new SparseArray<>();
    private boolean needSnap = false;
    private int lastDy = 0;
    private int maxScroll = -1;
    private LayoutState mLayoutState;
    private OrientationHelper mOrientationHelper;
    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        int itemCount = getItemCount();
        if (itemCount <= 0 || state.isPreLayout()) {
            return;
        }

        if (getChildCount() == 0) {
            // 通过第一个itemView，获取一些中间变量
            View itemView = recycler.getViewForPosition(0);
            addView(itemView);
            measureChildWithMargins(itemView, 0, 0);
            mDecoratedMeasuredWidth = getDecoratedMeasuredWidth(itemView);
            mDecoratedMeasuredHeight = getDecoratedMeasuredHeight(itemView);
        }

        int tempPosition = getPaddingTop();
        for (int i = 0; i < itemCount; i++) {
            Rect rect = new Rect();
            rect.left = getPaddingLeft();
            rect.top = tempPosition;
            rect.right = mDecoratedMeasuredWidth - getPaddingRight();
            rect.bottom = rect.top + mDecoratedMeasuredHeight;
            locationRects.put(i, rect);
            attachedItems.put(i, false);

            tempPosition = tempPosition + mDecoratedMeasuredHeight;
        }

        // 得到中间变量后，第一个View先回收放到缓存，后面会再次统一layout
        detachAndScrapAttachedViews(recycler);
        layoutItemsOnCreate(recycler);
        computeMaxScroll();
    }


    /**
     * 对外提供接口，找到第一个可视view的index
     */
    public int findFirstVisibleItemPosition() {
        int count = locationRects.size();
        Rect displayRect = new Rect(0, scroll, getWidth(), getHeight() + scroll);
        for (int i = 0; i < count; i++) {
            if (Rect.intersects(displayRect, locationRects.get(i)) &&
                    attachedItems.get(i)) {
                return i;
            }
        }
        return 0;
    }

    /**
     * 计算可滑动的最大值
     */
    private void computeMaxScroll() {
        maxScroll = locationRects.get(locationRects.size() - 1).bottom - getHeight();
        if (maxScroll < 0) {
            maxScroll = 0;
            return;
        }

        int childCount = getChildCount();
        int screenFilledHeight = 0;
        for (int i = childCount - 1; i >= 0; i--) {
            Rect rect = locationRects.get(i);
            screenFilledHeight = screenFilledHeight + (rect.bottom - rect.top);
            if (screenFilledHeight > getHeight()) {
                int extraSnapHeight = getHeight() - (screenFilledHeight - (rect.bottom - rect.top));
                maxScroll = maxScroll + extraSnapHeight;
                break;
            }
        }
    }

    /**
     * 初始化的时候，layout子View
     */
    private void layoutItemsOnCreate(RecyclerView.Recycler recycler) {
        int itemCount = getItemCount();

        for (int i = 0; i < itemCount; i++) {
            View childView = recycler.getViewForPosition(i);
            addView(childView);
            measureChildWithMargins(childView, 0, 0);
            layoutItem(childView, locationRects.get(i));
            attachedItems.put(i, true);
            childView.setPivotY(0);
            childView.setPivotX(childView.getMeasuredWidth() / 2);

            if (locationRects.get(i).top > getHeight()) {
                break;
            }
        }
    }


    /**
     * 初始化的时候，layout子View
     */
    private void layoutItemsOnScroll(RecyclerView.Recycler recycler, RecyclerView.State state, int dy) {
        int childCount = getChildCount();
        if (state.isPreLayout() || childCount == 0) {
            return;
        }

        // 1. 已经在屏幕上显示的child
        int itemCount = getItemCount();
        Rect displayRect = new Rect(0, scroll, getWidth(), getHeight() + scroll);
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child == null) {
                continue;
            }
            int position = getPosition(child);
            if (!Rect.intersects(displayRect, locationRects.get(position))) {
                // 回收滑出屏幕的View
                removeAndRecycleView(child, recycler);
                attachedItems.put(position, false);
            } else {
                // Item还在显示区域内，更新滑动后Item的位置
                layoutItem(child, locationRects.get(position)); //更新Item位置
            }
        }

        // 2. 复用View添加
        for (int i = 0; i < itemCount; i++) {
            if (Rect.intersects(displayRect, locationRects.get(i)) &&
                    !attachedItems.get(i)) {
                // 重新加载可见范围内的Item
                View scrap = recycler.getViewForPosition(i);
                measureChildWithMargins(scrap, 0, 0);
                scrap.setPivotY(0);
                scrap.setPivotX(scrap.getMeasuredWidth() / 2);
                if (dy > 0) {
                    addView(scrap);
                } else {
                    addView(scrap, 0);
                }
                // 将这个Item布局出来
                layoutItem(scrap, locationRects.get(i));
                attachedItems.put(i, true);
            }
        }
    }

    private void layoutItem(View child, Rect rect) {
        int topDistance = scroll - rect.top;
        int layoutTop, layoutBottom;
        if (topDistance < mDecoratedMeasuredHeight && topDistance >= 0) {
            float rate1 = (float) topDistance / mDecoratedMeasuredHeight;
            float rate2 = 1 - rate1 * rate1 / 3;
            float rate3 = 1 - rate1 * rate1;
            child.setScaleX(rate2);
            child.setScaleY(rate2);
            child.setAlpha(rate3);

            layoutTop = 0;
            layoutBottom = mDecoratedMeasuredHeight;
        } else {
            child.setScaleX(1);
            child.setScaleY(1);
            child.setAlpha(1);

            layoutTop = rect.top - scroll;
            layoutBottom = rect.bottom - scroll;
        }
        layoutDecorated(child, rect.left, layoutTop, rect.right, layoutBottom);
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() == 0 || dy == 0) {
            return 0;
        }
        int travel = dy;
        if (dy + scroll < 0) {
            travel = -scroll;
        } else if (dy + scroll > maxScroll) {
            travel = maxScroll - scroll;
        }
        scroll += travel; //累计偏移量
        lastDy = dy;
        layoutItemsOnScroll(recycler, state, dy);
        return travel;
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        new StartSnapHelper().attachToRecyclerView(view);
    }

    @Override
    public void onScrollStateChanged(int state) {
        if (state == RecyclerView.SCROLL_STATE_DRAGGING) {
            needSnap = true;
        }
        super.onScrollStateChanged(state);
    }

    public int getSnapHeight() {
        if (!needSnap) {
            return 0;
        }
        needSnap = false;

        Rect displayRect = new Rect(0, scroll, getWidth(), getHeight() + scroll);
        int itemCount = getItemCount();
        for (int i = 0; i < itemCount; i++) {
            Rect itemRect = locationRects.get(i);
            if (displayRect.intersect(itemRect)) {

                if (lastDy > 0) {
                    // scroll变大，属于列表往下走，往下找下一个为snapView
                    if (i < itemCount - 1) {
                        Rect nextRect = locationRects.get(i + 1);
                        return nextRect.top - displayRect.top;
                    }
                }
                return itemRect.top - displayRect.top;
            }
        }
        return 0;
    }

    public View findSnapView() {
        if (getChildCount() > 0) {
            return getChildAt(0);
        }
        return null;
    }

    public int findLastCompletelyVisibleItemPosition() {
        final View child = findOneVisibleChild(getChildCount() - 1, -1, true, false);
        return child == null ? NO_POSITION : getPosition(child);
    }

    // Returns the first child that is visible in the provided index range, i.e. either partially or
    // fully visible depending on the arguments provided. Completely invisible children are not
    // acceptable by this method, but could be returned
    // using #findOnePartiallyOrCompletelyInvisibleChild
    View findOneVisibleChild(int fromIndex, int toIndex, boolean completelyVisible,
                             boolean acceptPartiallyVisible) {
        ensureLayoutState();
        @ViewBoundsCheck.ViewBounds int preferredBoundsFlag = 0;
        @ViewBoundsCheck.ViewBounds int acceptableBoundsFlag = 0;
        if (completelyVisible) {
            preferredBoundsFlag = (ViewBoundsCheck.FLAG_CVS_GT_PVS | ViewBoundsCheck.FLAG_CVS_EQ_PVS
                    | ViewBoundsCheck.FLAG_CVE_LT_PVE | ViewBoundsCheck.FLAG_CVE_EQ_PVE);
        } else {
            preferredBoundsFlag = (ViewBoundsCheck.FLAG_CVS_LT_PVE
                    | ViewBoundsCheck.FLAG_CVE_GT_PVS);
        }
        if (acceptPartiallyVisible) {
            acceptableBoundsFlag = (ViewBoundsCheck.FLAG_CVS_LT_PVE
                    | ViewBoundsCheck.FLAG_CVE_GT_PVS);
        }

        return (HORIZONTAL == HORIZONTAL) ? mHorizontalBoundCheck
                .findOneViewWithinBoundFlags(fromIndex, toIndex, preferredBoundsFlag,
                        acceptableBoundsFlag) : mVerticalBoundCheck
                .findOneViewWithinBoundFlags(fromIndex, toIndex, preferredBoundsFlag,
                        acceptableBoundsFlag);
    }

    void ensureLayoutState() {
        if (mLayoutState == null) {
            mLayoutState = createLayoutState();
        }
        if (mOrientationHelper == null) {
            mOrientationHelper = OrientationHelper.createOrientationHelper(this, HORIZONTAL);
        }
    }

    LayoutState createLayoutState() {
        return new LayoutState();
    }
    static class LayoutState {

        static final String TAG = "LLM#LayoutState";

        static final int LAYOUT_START = -1;

        static final int LAYOUT_END = 1;

        static final int INVALID_LAYOUT = Integer.MIN_VALUE;

        static final int ITEM_DIRECTION_HEAD = -1;

        static final int ITEM_DIRECTION_TAIL = 1;

        static final int SCROLLING_OFFSET_NaN = Integer.MIN_VALUE;

        /**
         * We may not want to recycle children in some cases (e.g. layout)
         */
        boolean mRecycle = true;

        /**
         * Pixel offset where layout should start
         */
        int mOffset;

        /**
         * Number of pixels that we should fill, in the layout direction.
         */
        int mAvailable;

        /**
         * Current position on the adapter to get the next item.
         */
        int mCurrentPosition;

        /**
         * Defines the direction in which the data adapter is traversed.
         * Should be {@link #ITEM_DIRECTION_HEAD} or {@link #ITEM_DIRECTION_TAIL}
         */
        int mItemDirection;

        /**
         * Defines the direction in which the layout is filled.
         * Should be {@link #LAYOUT_START} or {@link #LAYOUT_END}
         */
        int mLayoutDirection;

        /**
         * Used when LayoutState is constructed in a scrolling state.
         * It should be set the amount of scrolling we can make without creating a new ui.
         * Settings this is required for efficient ui recycling.
         */
        int mScrollingOffset;

        /**
         * Used if you want to pre-layout items that are not yet visible.
         * The difference with {@link #mAvailable} is that, when recycling, distance laid out for
         * {@link #mExtra} is not considered to avoid recycling visible children.
         */
        int mExtra = 0;

        /**
         * Equal to {@link RecyclerView.State#isPreLayout()}. When consuming scrap, if this value
         * is set to true, we skip removed views since they should not be laid out in post layout
         * step.
         */
        boolean mIsPreLayout = false;

        /**
         * The most recent {@link #(int, RecyclerView.Recycler, RecyclerView.State)}
         * amount.
         */
        int mLastScrollDelta;

        /**
         * When LLM needs to layout particular views, it sets this list in which case, LayoutState
         * will only return views from this list and return null if it cannot find an item.
         */
        List<RecyclerView.ViewHolder> mScrapList = null;

        /**
         * Used when there is no limit in how many views can be laid out.
         */
        boolean mInfinite;

        /**
         * @return true if there are more items in the data adapter
         */
        boolean hasMore(RecyclerView.State state) {
            return mCurrentPosition >= 0 && mCurrentPosition < state.getItemCount();
        }

        /**
         * Gets the ui for the next element that we should layout.
         * Also updates current item index to the next item, based on {@link #mItemDirection}
         *
         * @return The next element that we should layout.
         */
        View next(RecyclerView.Recycler recycler) {
            if (mScrapList != null) {
                return nextViewFromScrapList();
            }
            final View view = recycler.getViewForPosition(mCurrentPosition);
            mCurrentPosition += mItemDirection;
            return view;
        }

        /**
         * Returns the next item from the scrap list.
         * <p>
         * Upon finding a valid VH, sets current item position to VH.itemPosition + mItemDirection
         *
         * @return View if an item in the current position or direction exists if not null.
         */
        private View nextViewFromScrapList() {
            final int size = mScrapList.size();
            for (int i = 0; i < size; i++) {
                final View view = mScrapList.get(i).itemView;
                final RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) view.getLayoutParams();
                if (lp.isItemRemoved()) {
                    continue;
                }
                if (mCurrentPosition == lp.getViewLayoutPosition()) {
                    assignPositionFromScrapList(view);
                    return view;
                }
            }
            return null;
        }

        public void assignPositionFromScrapList() {
            assignPositionFromScrapList(null);
        }

        public void assignPositionFromScrapList(View ignore) {
            final View closest = nextViewInLimitedList(ignore);
            if (closest == null) {
                mCurrentPosition = NO_POSITION;
            } else {
                mCurrentPosition = ((RecyclerView.LayoutParams) closest.getLayoutParams())
                        .getViewLayoutPosition();
            }
        }

        public View nextViewInLimitedList(View ignore) {
            int size = mScrapList.size();
            View closest = null;
            int closestDistance = Integer.MAX_VALUE;
            if (mIsPreLayout) {
                throw new IllegalStateException("Scrap list cannot be used in pre layout");
            }
            for (int i = 0; i < size; i++) {
                View view = mScrapList.get(i).itemView;
                final RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) view.getLayoutParams();
                if (view == ignore || lp.isItemRemoved()) {
                    continue;
                }
                final int distance = (lp.getViewLayoutPosition() - mCurrentPosition)
                        * mItemDirection;
                if (distance < 0) {
                    continue; // item is not in current direction
                }
                if (distance < closestDistance) {
                    closest = view;
                    closestDistance = distance;
                    if (distance == 0) {
                        break;
                    }
                }
            }
            return closest;
        }

        void log() {
            Log.d(TAG, "avail:" + mAvailable + ", ind:" + mCurrentPosition + ", dir:"
                    + mItemDirection + ", offset:" + mOffset + ", layoutDir:" + mLayoutDirection);
        }
    }
}
