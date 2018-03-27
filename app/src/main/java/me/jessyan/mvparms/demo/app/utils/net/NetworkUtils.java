package me.jessyan.mvparms.demo.app.utils.net;

import android.os.Message;
import android.text.TextUtils;

import com.jess.arms.base.BaseApplication;
import com.jess.arms.http.HttpException;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.utils.FastJsonUtils;

import org.simple.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.mvparms.demo.app.ApiConfiguration;
import me.jessyan.mvparms.demo.app.utils.net.sign.ParameterSignUtils;
import me.jessyan.mvparms.demo.mvp.model.api.service.CommonService;
import me.jessyan.mvparms.demo.mvp.model.entity.BaseJson;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import top.zibin.luban.Luban;

import static com.jess.arms.integration.AppManager.APPMANAGER_MESSAGE;
import static com.jess.arms.integration.AppManager.HIDE_LOADING;
import static com.jess.arms.integration.AppManager.SHOW_LOADING;

/**
 * Created by czw on 2017/11/21.
 */
public class NetworkUtils {

    /**
     * 请求默认BASE_UR
     *
     * @param mRepositoryManager
     * @param maps
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> Observable<T> requestToObject(IRepositoryManager mRepositoryManager, Map<String, String> maps, Class<T> tClass) {
        return requestToObject(mRepositoryManager, ApiConfiguration.Domain.BASE_URL, maps, tClass);
    }

    /**
     * 请求默认BASE_URL
     *
     * @param mRepositoryManager
     * @param maps
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> Observable<List<T>> requestObjectToList(IRepositoryManager mRepositoryManager, Map<String, String> maps, Class<T> tClass) {
        return requestObjectToList(mRepositoryManager, ApiConfiguration.Domain.BASE_URL, maps, tClass);
    }

    /**
     * 解析对象
     *
     * @param mRepositoryManager
     * @param baseUrl            网络请求url
     * @param maps               参数
     * @param tClass             转换对象
     * @param <T>                需转换泛型
     * @return 解析后的对象
     */
    @SuppressWarnings("unchecked")
    public static <T> Observable<T> requestToObject(IRepositoryManager mRepositoryManager, String baseUrl, Map<String, String> maps, Class<T> tClass) {
//        RetrofitUrlManager.getInstance().putDomain("baidu", "https://www.baidu.com"); //单独请求需要修改BaseUrl,需要在RetrofitService添加@Headers({DOMAIN_NAME_HEADER + "baidu"})
//        RetrofitUrlManager.getInstance().setGlobalDomain(baseUrl); //修改全局的BaseUrl
        Map<String, String> parameterMaps = ParameterSignUtils.buildCommonParameter(baseUrl, maps); //拼接公共参数
        return mRepositoryManager.obtainRetrofitService(CommonService.class)
                .executeEncodePost(baseUrl, parameterMaps)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(responseBody -> Observable.create((ObservableOnSubscribe<T>) e -> {
                    try {
                        if (tClass.equals(BaseJson.class)) {
                            //基类
                            BaseJson baseJson = FastJsonUtils.stringToObject(responseBody.string(), BaseJson.class);
                            if (baseJson.getCode() == 200 && baseJson.isSuccess()) {
                                e.onNext((T) baseJson);
                            } else {
                                e.onError(new HttpException(baseJson.getMsg(), baseJson.getCode())); //非200状态抛出给ResponseErrorListenerImpl统一处理
                            }
                        } else if (tClass.equals(String.class)) {
                            //JSON字符串
                            BaseJson baseJson = FastJsonUtils.stringToObject(responseBody.string(), BaseJson.class);
                            if (baseJson.getCode() == 200 && baseJson.isSuccess()) {
                                e.onNext((T) baseJson.getData().toString());
                            } else {
                                e.onError(new HttpException(baseJson.getMsg(), baseJson.getCode())); //非200状态抛出给ResponseErrorListenerImpl统一处理
                            }
                        } else {
                            //对象
                            BaseJson<T> baseJson = FastJsonUtils.stringToObject(responseBody.string(), BaseJson.class);
                            if (baseJson.getCode() == 200 && baseJson.isSuccess()) {
                                T t = FastJsonUtils.stringToObject(baseJson.getData().toString(), tClass);
                                e.onNext(t);
                            } else {
                                e.onError(new HttpException(baseJson.getMsg(), baseJson.getCode())); //非200状态抛出给ResponseErrorListenerImpl统一处理
                            }
                        }
                    } catch (Exception error) {
                        e.onError(error);
                    }
                    hideLoading();
                }));
    }

    /**
     * 解析对象 非本应用url
     *
     * @param mRepositoryManager
     * @param baseUrl            网络请求url
     * @param maps               参数
     * @param tClass             转换对象
     * @param <T>                需转换泛型
     * @return 解析后的对象
     */
    @SuppressWarnings("unchecked")
    public static <T> Observable<T> requestNormalObject(IRepositoryManager mRepositoryManager, String baseUrl, Map<String, String> maps, Class<T> tClass) {
        return mRepositoryManager.obtainRetrofitService(CommonService.class)
                .executeEncodePost(baseUrl, maps)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(responseBody -> Observable.create((ObservableOnSubscribe<T>) e -> {
                    try {
                        //基类
                        T t = FastJsonUtils.stringToObject(responseBody.string(), tClass);
                        e.onNext(t);
                    } catch (Exception error) {
                        e.onError(error);
                    }
                    hideLoading();
                }));
    }

    /**
     * 解析列表对象
     *
     * @param mRepositoryManager
     * @param baseUrl            网络请求url
     * @param maps               参数
     * @param tClass             转换对象
     * @param <T>                需转换泛型
     * @return 解析后的列表对象
     */
    @SuppressWarnings("unchecked")
    public static <T> Observable<List<T>> requestObjectToList(IRepositoryManager mRepositoryManager, String baseUrl, Map<String, String> maps, Class<T> tClass) {
//        RetrofitUrlManager.getInstance().setGlobalDomain(baseUrl); //修改全局的BaseUrl
        Map<String, String> parameterMaps = ParameterSignUtils.buildCommonParameter(baseUrl, maps); //拼接公共参数
        return mRepositoryManager.obtainRetrofitService(CommonService.class)
                .executeEncodePost(baseUrl, parameterMaps)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(responseBody -> Observable.create(e -> {
                    try {
                        //列表对象
                        BaseJson<T> baseJson = FastJsonUtils.stringToObject(responseBody.string(), BaseJson.class);
                        if (baseJson.getCode() == 200 && baseJson.isSuccess()) {
                            List<T> t = FastJsonUtils.stringToArrayList(baseJson.getData().toString(), tClass);
                            e.onNext(t);
                        } else {
                            e.onError(new HttpException(baseJson.getMsg(), baseJson.getCode())); //非200状态抛出给ResponseErrorListenerImpl统一处理
                        }
                    } catch (Exception error) {
                        e.onError(error);
                    }
                    hideLoading();
                }));
    }

    /**
     * 上传文件
     *
     * @param mRepositoryManager
     * @param baseUrl            网络请求url
     * @param parameterMap       参数
     * @param fileMap            文件路径
     * @param tClass             转换对象
     * @param <T>                需转换泛型
     * @return 解析后的列表对象
     */
//    @SuppressWarnings("unchecked")
//    public static <T> Observable<List<T>> uploadFile(IRepositoryManager mRepositoryManager, String baseUrl, Map<String, String> parameterMap, Class<T> tClass, Map<String, String> fileMap) {
////        RetrofitUrlManager.getInstance().setGlobalDomain(baseUrl); //修改全局的BaseUrl
//        Map<String, String> parameterMaps = ParameterSignUtils.buildCommonParameter(baseUrl, parameterMap); //拼接公共参数
//        MultipartBody.Builder builder = new MultipartBody.Builder();
//        builder.setType(MultipartBody.FORM); //表单类型
//
//        if (parameterMap != null && parameterMap.size() > 0) {
//            //传入参数
//            Set<Map.Entry<String, String>> parameterSet = parameterMaps.entrySet();
//            for (Map.Entry<String, String> entry : parameterSet) {
//                builder.addFormDataPart(entry.getKey(), entry.getValue());
//            }
//        }
//
//        if (fileMap != null && fileMap.size() > 0) {
//            //传入图片
//            Set<Map.Entry<String, String>> parameterSet = fileMap.entrySet();
//            for (Map.Entry<String, String> entry : parameterSet) {
//                File file = new File(entry.getValue()); //filePath 图片地址
//                RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), file); //RequestBody.create(MediaType.parse("application/octet-stream"), file)
//                builder.addFormDataPart(entry.getKey(), file.getName(), imageBody); //"imgfile"+i 后台接收图片流的参数名
//            }
//        }
//
//        return mRepositoryManager.obtainRetrofitService(CommonService.class)
//                .executePost(baseUrl, builder.build())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .flatMap(responseBody -> Observable.create(e -> {
//                    try {
//                        BaseJson baseJson = FastJsonUtils.stringToObject(responseBody.string(), BaseJson.class);
//                        if (baseJson.getCode() == 200 && baseJson.isSuccess()) {
//                            List<T> t = FastJsonUtils.stringToArrayList(baseJson.getData().toString(), tClass);
//                            e.onNext(t);
//                        } else {
//                            e.onError(new HttpException(baseJson.getMsg(), baseJson.getCode())); //非200状态抛出给ResponseErrorListenerImpl统一处理
//                        }
//                    } catch (Exception error) {
//                        e.onError(error);
//                    }
//                    hideLoading();
//                }));
//    }

    /**
     * 上传文件 LuBan进行图片压缩
     *
     * @param mRepositoryManager
     * @param baseUrl            网络请求url
     * @param parameterMap       参数
     * @param pathList           文件路径
     * @return 解析后的列表对象
     */
    @SuppressWarnings("unchecked")
    public static Observable<List<String>> uploadPicture(IRepositoryManager mRepositoryManager, String baseUrl, Map<String, String> parameterMap, List<String> pathList) {
        Map<String, String> parameterMaps = ParameterSignUtils.buildCommonParameter(baseUrl, parameterMap); //拼接公共参数
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM); //表单类型

        if (parameterMap != null && parameterMap.size() > 0) {
            //传入参数
            Set<Map.Entry<String, String>> parameterSet = parameterMaps.entrySet();
            for (Map.Entry<String, String> entry : parameterSet) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }

        return Observable.just(pathList).subscribeOn(Schedulers.io()).map(new Function<List<String>, List<File>>() {
            @Override
            public List<File> apply(List<String> list) throws Exception {
                //LuBan进行图片压缩
                return Luban.with(BaseApplication.getInstance()).load(list).get();
            }
        }).flatMap(new Function<List<File>, ObservableSource<List<String>>>() {
            @Override
            public ObservableSource<List<String>> apply(List<File> files) throws Exception {
                for (int i = 0; i < files.size(); i++) {
                    File file = files.get(i);
                    builder.addFormDataPart("file" + i, file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file));
                }
                return mRepositoryManager.obtainRetrofitService(CommonService.class)
                        .executePost(baseUrl, builder.build())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(responseBody -> Observable.create(e -> {
                            try {
                                BaseJson baseJson = FastJsonUtils.stringToObject(responseBody.string(), BaseJson.class);
                                if (baseJson.getCode() == 200 && baseJson.isSuccess()) {
                                    List<String> t = FastJsonUtils.stringToArrayList(baseJson.getData().toString(), String.class);
                                    e.onNext(t);
                                } else {
                                    e.onError(new HttpException(baseJson.getMsg(), baseJson.getCode())); //非200状态抛出给ResponseErrorListenerImpl统一处理
                                }
                            } catch (Exception error) {
                                e.onError(error);
                            }
                            hideLoading();
                        }));
            }
        });
    }


    /**
     * 原文件上传
     *
     * @param mRepositoryManager
     * @param baseUrl
     * @param parameterMap
     * @param pathList
     * @return
     */
    public static Observable<List<String>> uploadFile(IRepositoryManager mRepositoryManager, String baseUrl, Map<String, String> parameterMap, List<String> pathList) {
        Map<String, String> parameterMaps = ParameterSignUtils.buildCommonParameter(baseUrl, parameterMap); //拼接公共参数
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM); //表单类型
        //传入参数
        if (parameterMap != null && parameterMap.size() > 0) {
            Set<Map.Entry<String, String>> parameterSet = parameterMaps.entrySet();
            for (Map.Entry<String, String> entry : parameterSet) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }

        return Observable.just(pathList).subscribeOn(Schedulers.io()).map(new Function<List<String>, List<File>>() {
            @Override
            public List<File> apply(List<String> list) throws Exception {
                List<File> fileList = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    File file = new File(list.get(i));
                    if (file.exists()) {
                        fileList.add(file);
                    }
                }
                return fileList;
            }
        }).flatMap(new Function<List<File>, ObservableSource<List<String>>>() {
            @Override
            public ObservableSource<List<String>> apply(List<File> files) throws Exception {
                for (int i = 0; i < files.size(); i++) {
                    File file = files.get(i);
                    builder.addFormDataPart("file" + i, file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file));
                }
                return mRepositoryManager.obtainRetrofitService(CommonService.class)
                        .executePost(baseUrl, builder.build())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(responseBody -> Observable.create(e -> {
                            try {
                                BaseJson baseJson = FastJsonUtils.stringToObject(responseBody.string(), BaseJson.class);
                                if (baseJson.getCode() == 200 && baseJson.isSuccess()) {
                                    List<String> t = FastJsonUtils.stringToArrayList(baseJson.getData().toString(), String.class);
                                    e.onNext(t);
                                } else {
                                    e.onError(new HttpException(baseJson.getMsg(), baseJson.getCode())); //非200状态抛出给ResponseErrorListenerImpl统一处理
                                }
                            } catch (Exception error) {
                                e.onError(error);
                            }
                            hideLoading();
                        }));
            }
        });
    }

    /**
     * 过滤空的参数则不传
     *
     * @param map
     * @param key
     * @param value
     * @return
     */
    private static Map<String, String> filterNull(Map<String, String> map, String key, String value) {
        if (!TextUtils.isEmpty(value)) {
            map.put(key.trim(), value.trim());
        }
        return map;
    }

    /**
     * 网络请求 显示Loading
     */
    public static void showLoading() {
        Message message = new Message();
        message.what = SHOW_LOADING;
        EventBus.getDefault().post(message, APPMANAGER_MESSAGE);
    }

    /**
     * 网络请求 隐藏loading
     */
    public static void hideLoading() {
        Message message = new Message();
        message.what = HIDE_LOADING;
        EventBus.getDefault().post(message, APPMANAGER_MESSAGE);
    }

}
