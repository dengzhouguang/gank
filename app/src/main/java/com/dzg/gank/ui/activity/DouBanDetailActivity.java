package com.dzg.gank.ui.activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.dzg.gank.ItemDecoration.DividerItemDecoration;
import com.dzg.gank.R;
import com.dzg.gank.adapter.DoubanDetailAdapter;
import com.dzg.gank.module.Movie;
import com.dzg.gank.module.MovieDetail;
import com.dzg.gank.ui.statusbar.StatusBarUtil;
import com.dzg.gank.util.CommonUtils;
import com.dzg.gank.util.HttpUtil;
import com.dzg.gank.util.StatusBarUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;


public class DouBanDetailActivity extends AppCompatActivity {
    @BindView(R.id.rate)
    TextView mRateTv;
    @BindView(R.id.title)
    TextView mTitleTv;
    @BindView(R.id.cast)
    TextView mCastTv;
    @BindView(R.id.direction)
    TextView mDirectionTv;
    @BindView(R.id.imageview)
    ImageView mImageView;
    @BindView(R.id.year)
    TextView mYearTv;
    @BindView(R.id.genres)
    TextView mGenresTv;
    @BindView(R.id.summary)
    TextView mSummaryTv;
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    private DoubanDetailAdapter mAdapter;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.img_item_bg)
    ImageView mBackgroundIv;
    @BindView(R.id.nestedScrollView)
    MyNestedScrollView mNestedScrollView;
    @BindView(R.id.titlebar_bg)
    ImageView mTitleBarBgIv;
    private int imageBgHeight;
    private int slidingDistance;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doubandetail);
        ButterKnife.bind(this);
        Bundle bundle=getIntent().getExtras();
        Movie.SubjectsBean bean= (Movie.SubjectsBean) bundle.getSerializable("SubjectsBean");
        show(bean);
        initSlideShapeTheme(bean.getImages().getLarge());
        }


        public void show(Movie.SubjectsBean bean){
            mRateTv.setText("评分： "+bean.getRating().getStars());
            mTitleTv.setText(bean.getTitle());
            StringBuffer sb=new StringBuffer();
            sb.append("演员： ");
            List<Movie.SubjectsBean.CastsBean> casts = bean.getCasts();
            int i=0;
            for (Movie.SubjectsBean.CastsBean castBean: casts) {
                sb.append(castBean.getName() +"  ");
                i++;
                if (i%3==0)
                    sb.append("\r\n");
            }
            mCastTv.setText(sb.toString());
            StringBuffer sb2=new StringBuffer();
            sb2.append("类型：  ");
            for (String str:bean.getGenres()){
                sb2.append(str+"/");
            }
            mYearTv.setText("上映日期：  "+bean.getYear());
            mGenresTv.setText(sb2.toString());
            mDirectionTv.setText("导演： "+bean.getDirectors().get(0).getName());
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setHasFixedSize(false);
            mRecyclerView.setNestedScrollingEnabled(false);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(DouBanDetailActivity.this,DividerItemDecoration.VERTICAL_LIST));
            Glide.with(this).load(bean.getImages().getLarge()).into(mImageView);
            getDetailData(bean.getId());
            setSupportActionBar(mToolbar);
            ActionBar actionBar=getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back);
            actionBar.setTitle(bean.getTitle());
            mToolbar.setSubtitle(sb.toString().replace("\r\n","  "));
            mToolbar.setTitleTextAppearance(this, R.style.ToolBar_Title);
            mToolbar.setSubtitleTextAppearance(this, R.style.Toolbar_SubTitle);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
        public void getDetailData(String id){
            HttpUtil.getDouBanService().getMovieDetail(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<MovieDetail>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }
                        @Override
                        public void onNext(@NonNull MovieDetail movieDetail) {
                            mSummaryTv.setText(movieDetail.getSummary().substring(0,movieDetail.getSummary().length()-3));
                            mAdapter=new DoubanDetailAdapter(DouBanDetailActivity.this,movieDetail);
                            mAdapter.notifyDataSetChanged();
                            mRecyclerView.setAdapter(mAdapter);
                        }
                        @Override
                        public void onError(@NonNull Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    protected void initSlideShapeTheme(String imgUrl) {
        setImgHeaderBg(imgUrl);

        // toolbar 的高
        int toolbarHeight = mToolbar.getLayoutParams().height;
        final int headerBgHeight = toolbarHeight + StatusBarUtil.getStatusBarHeight(this);

        // 使背景图向上移动到图片的最低端，保留（titlebar+statusbar）的高度
        ViewGroup.LayoutParams params = mTitleBarBgIv.getLayoutParams();
        ViewGroup.MarginLayoutParams ivTitleHeadBgParams = (ViewGroup.MarginLayoutParams) mTitleBarBgIv.getLayoutParams();
        int marginTop = params.height - headerBgHeight;
        ivTitleHeadBgParams.setMargins(0, -marginTop, 0, 0);

        mTitleBarBgIv.setImageAlpha(0);
        StatusBarUtils.setTranslucentImageHeader(this, 0, mToolbar);

        // 上移背景图片，使空白状态栏消失(这样下方就空了状态栏的高度)
        if (mBackgroundIv != null) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mBackgroundIv.getLayoutParams();
            layoutParams.setMargins(0, -StatusBarUtil.getStatusBarHeight(this), 0, 0);

            ViewGroup.LayoutParams imgItemBgparams = mBackgroundIv.getLayoutParams();
            // 获得高斯图背景的高度
            imageBgHeight = imgItemBgparams.height;
        }
        // 变色
        initScrollViewListener();
        initNewSlidingParams();
    }
    private void setImgHeaderBg(String imgUrl) {
        if (!TextUtils.isEmpty(imgUrl)) {
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.mipmap.stackblur_default)
                    .error(R.mipmap.stackblur_default)
                    .priority(Priority.HIGH)
                    /*.bitmapTransform(new BlurTransformation(this, 23, 4))*/;
            // 高斯模糊背景 原来 参数：12,5  23,4
            Glide.with(this).load(imgUrl).apply(options).apply(bitmapTransform(new BlurTransformation(23,5))).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    mToolbar.setBackgroundColor(Color.TRANSPARENT);
                    mTitleBarBgIv.setImageAlpha(0);
                    mTitleBarBgIv.setVisibility(View.VISIBLE);
                    return false;
                }
            }).into(mTitleBarBgIv);

            // 高斯模糊背景 原来 参数：12,5  23,4
            Glide.with(this).load(imgUrl).apply(options).apply(bitmapTransform(new BlurTransformation(23,5))).into(mBackgroundIv);
        }
    }
    private void initScrollViewListener() {
        // 为了兼容23以下
        mNestedScrollView.setOnScrollChangeListener(new MyNestedScrollView.ScrollInterface() {
            @Override
            public void onScrollChange(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                scrollChangeHeader(scrollY);
            }
        });
    }
    private void scrollChangeHeader(int scrolledY) {
        if (scrolledY < 0) {
            scrolledY = 0;
        }
        float alpha = Math.abs(scrolledY) * 1.0f / (slidingDistance);

        Drawable drawable = mTitleBarBgIv.getDrawable();

        if (drawable == null) {
            return;
        }
        if (scrolledY <= slidingDistance) {
            // title部分的渐变
            drawable.mutate().setAlpha((int) (alpha * 255));
            mTitleBarBgIv.setImageDrawable(drawable);
        } else {
            drawable.mutate().setAlpha(255);
            mTitleBarBgIv.setImageDrawable(drawable);
        }
    }
    private void initNewSlidingParams() {
        int titleBarAndStatusHeight = (int) (CommonUtils.getDimens(R.dimen.nav_bar_height) + StatusBarUtil.getStatusBarHeight(this));
        // 减掉后，没到顶部就不透明了
        slidingDistance = imageBgHeight - titleBarAndStatusHeight - (int) (CommonUtils.getDimens(R.dimen.base_header_activity_slide_more));
    }

}
