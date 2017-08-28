package me.jessyan.mvparms.demo.mvp.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.mvparms.demo.R;
import me.jessyan.mvparms.demo.mvp.ui.widget.update.UpdateDialogFragment;
import zlc.season.rxdownload2.RxDownload;

/**
 * Created by chan on 2017/8/26.
 */
public class DownloadActivity extends BaseActivity {

    @BindView(R.id.show_dialog)
    Button mShowDialog;
    @BindView(R.id.delete_file)
    Button mDeleteFile;

    public final static String url = "http://s1.music.126.net/download/android/CloudMusic_official_3.7.3_153912.apk"; //下载链接
    public final static String versionCode = "1.0.0"; //更新版本号
    public final static String updateInfo = "1，新增xx功能\n2，优化用户体验\n3，遇到在线更新不成功，请卸载后再安装，感谢用户一直的支持和体谅\n4，修复若干个bug";


    private UpdateDialogFragment mUpdateDialogFragment;
    private RxDownload mRxDownload;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    public int initView(Bundle saanceState) {
        return R.layout.activity_download;
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @OnClick({R.id.show_dialog, R.id.delete_file})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.show_dialog:
                UpdateDialogFragment.newInstance(url, versionCode, updateInfo).show(getFragmentManager(), "UpdateDialogFragment");
                break;
            case R.id.delete_file:
                if (mRxDownload == null)
                    mRxDownload = RxDownload.getInstance(this);
                //暂停地址为url的下载并从数据库中删除记录，deleteFile为true会同时删除该url 下载产生的所有文件
                mRxDownload.deleteServiceDownload(url, true).subscribe();
                ArmsUtils.makeText(this, "删除成功");
                break;
        }
    }

}