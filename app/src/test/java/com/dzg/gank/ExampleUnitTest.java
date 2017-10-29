package com.dzg.gank;

import com.dzg.gank.api.BaiQiuService;
import com.dzg.gank.module.DianYingBean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
                new Retrofit.Builder()
                        .baseUrl("http://www.qiushibaike.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build().create(BaiQiuService.class).getData(1 + "")
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull ResponseBody responseBody) {
                        Document doc = null;
                        try {
                            doc = Jsoup.parse(responseBody.string());
                            /*Elements elements = doc.select("div.article");
                            for (int i = 0; i < elements.size(); i++) {
                                if (elements.get(i).select("div.thumb").size()==0){
                                    continue;
                                }
                                Elements el = elements.get(i).select("a.contentHerf");
                                String txt = el.get(0).text();
                                System.out.println(i);
                                System.out.println("1.标题     "+el.text());
                                String href = el.get(0).attr("href");
                                System.out.println("2.链接   "+ href);
                            }*/



                           /* Elements elstext = doc.select("div.thumb");*/
                            Elements els = doc.select("a.contentHerf");
                            for (int i = 0; i < els.size(); i++) {
                                System.out.println(i);
                                Element el = els.get(i);
                                String txt = el.text();
                                System.out.println("1.标题     "+el.text());
                                String href = el.attr("href");
                                System.out.println("2.链接   "+ href);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
    @Test
    public void test() throws Exception{
        Observable<DianYingBean> dianYingBeanObservable = Observable.create(new ObservableOnSubscribe<DianYingBean>() {
            @Override
            public void subscribe(ObservableEmitter<DianYingBean> e) throws Exception {

            }
        });
    }
}