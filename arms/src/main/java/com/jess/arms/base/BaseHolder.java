/**
 * Copyright 2017 JessYan
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jess.arms.base;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.jess.arms.http.imageloader.glide.ImageConfigImpl;
import com.jess.arms.integration.AppManager;
import com.jess.arms.utils.RxDataToolUtils;
import com.jess.arms.utils.ThirdViewUtil;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 基类 {@link RecyclerView.ViewHolder}
 * <p>
 * Created by JessYan on 2015/11/24.
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class BaseHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener {
    protected OnViewClickListener mOnViewClickListener = null;
    protected final String TAG = this.getClass().getSimpleName();
    private SparseArray<View> mViews;//集合类，layout里包含的View,以view的id作为key，value是view对象
    private View mConvertView;
    private AppComponent mAppComponent;
    //用于加载图片的管理类,默认使用glide,使用策略模式,可替换框架
    private List<ImageView> mImageViewList;
    private ImageLoader mImageLoader;
    private AppManager mAppManager;

    public BaseHolder(View itemView) {
        super(itemView);
        //可以在任何可以拿到Application的地方,拿到AppComponent,从而得到用Dagger管理的单例对象
        mAppComponent = ((App) itemView.getContext().getApplicationContext()).getAppComponent();
        mImageLoader = mAppComponent.imageLoader();
        mAppManager = mAppComponent.appManager();
        mViews = new SparseArray<>();
        mConvertView = itemView;

        itemView.setOnClickListener(this);//点击事件
        if (ThirdViewUtil.USE_AUTOLAYOUT == 1) AutoUtils.autoSize(itemView);//适配
        ThirdViewUtil.bindTarget(this, itemView);//绑定
    }


    private <T extends View> T findViewById(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getView(int viewId) {
        return findViewById(viewId);
    }

    public TextView getTextView(int viewId) {
        return (TextView) getView(viewId);
    }

    public Button getButton(int viewId) {
        return (Button) getView(viewId);
    }

    public ImageView getImageView(int viewId) {
        return (ImageView) getView(viewId);
    }

    public ImageButton getImageButton(int viewId) {
        return (ImageButton) getView(viewId);
    }

    public EditText getEditText(int viewId) {
        return (EditText) getView(viewId);
    }

    public BaseHolder<T> setText(int viewId, String value) {
        TextView view = findViewById(viewId);
        view.setText(TextUtils.isEmpty(value) ? "" : value);
        return this;
    }

    /**
     * 释放资源
     */
    protected void onRelease() {
        if (mImageViewList != null && mImageViewList.size() > 0) {
            mImageLoader.clear(mAppManager.getCurrentActivity(), ImageConfigImpl.builder()
                    .imageViews(mImageViewList.toArray(new ImageView[mImageViewList.size()]))
                    .build());
        }

    }

    @Override
    public void onClick(View view) {
        if (mOnViewClickListener != null) {
            mOnViewClickListener.onViewClick(view, this.getAdapterPosition());
        }
    }

    public interface OnViewClickListener {
        void onViewClick(View view, int position);
    }

    public void setOnItemClickListener(OnViewClickListener listener) {
        this.mOnViewClickListener = listener;
    }

    public BaseHolder setBackground(int viewId, int resId) {
        View view = findViewById(viewId);
        view.setBackgroundResource(resId);
        return this;
    }

    public BaseHolder setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = findViewById(viewId);
        RxDataToolUtils.twoClick(view, listener);

        return this;
    }

    public BaseHolder setImageByUrl(int viewId, String url, int errorId) {
        if (mImageViewList == null) {
            mImageViewList = new ArrayList<>();
        }
        mImageViewList.add(getImageView(viewId));
        try {
            if (TextUtils.isEmpty(url)) {
                GlideArms.with(mConvertView.getContext())
                        .load(errorId)
                        .priority(Priority.HIGH)
                        .into(getImageView(viewId));
                return this;
            }
            GlideArms.with(mConvertView.getContext())
                    .load(url)
                    .error(errorId)
                    .priority(Priority.HIGH)
                    .into(getImageView(viewId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public BaseHolder setImageByUrl(int viewId, String url) {
        if (TextUtils.isEmpty(url)) {
            return this;
        }
        if (mImageViewList == null) {
            mImageViewList = new ArrayList<>();
        }
        mImageViewList.add(getImageView(viewId));
        try {
            if (url.toLowerCase().endsWith(".gif")) {
                GlideArms.with(mConvertView.getContext())
                        .asGif()
                        .load(url)
                        .priority(Priority.HIGH)
                        .into(getImageView(viewId));
            } else {
                GlideArms.with(mConvertView.getContext())
                        .load(url)
                        .priority(Priority.NORMAL)
                        .into(getImageView(viewId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public BaseHolder setImageByUrlHigh(int viewId, String url) {
        if (mImageViewList == null) {
            mImageViewList = new ArrayList<>();
        }
        mImageViewList.add(getImageView(viewId));
        try {
            if (url.toLowerCase().endsWith(".gif")) {
                GlideArms.with(mConvertView.getContext())
                        .asGif()
                        .load(url)
                        .priority(Priority.HIGH)
                        .into(getImageView(viewId));
            } else {
                GlideArms.with(mConvertView.getContext())
                        .load(url)
                        .priority(Priority.HIGH)
                        .into(getImageView(viewId));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public BaseHolder setImageFromFile(int viewId, String path) {
        if (mImageViewList == null) {
            mImageViewList = new ArrayList<>();
        }
        mImageViewList.add(getImageView(viewId));
        Glide.with(mAppManager.getCurrentActivity() == null ? mAppComponent.application() : mAppManager.getCurrentActivity())
                .load(path)
                .into(getImageView(viewId));
        return this;
    }

    public BaseHolder setImageFromDrawable(int viewId, int drawable) {
        if (mImageViewList == null) {
            mImageViewList = new ArrayList<>();
        }
        mImageViewList.add(getImageView(viewId));
        Glide.with(mAppManager.getCurrentActivity() == null ? mAppComponent.application() : mAppManager.getCurrentActivity())
                .load(drawable)
                .into((ImageView) getView(viewId));
        return this;
    }

    public View getConvertView() {
        return mConvertView;
    }

    public BaseHolder setViewBackground(int viewId, String path) {
        GlideArms.with(mConvertView.getContext())
                .load(path)
                .priority(Priority.HIGH)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        getView(viewId).setBackground(resource);
                    }
                });
        return this;
    }
}
