package com.dzg.gank;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
       /* OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        cookieStore.put(url, cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(url);
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
//                .cache(new Cache(FileUtil.getHttpCacheDir(App.getInstance()), Constants.HTTP_CACHE_SIZE))
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();

                        Request request = original.newBuilder()
                               .header("Cache-Control", "max-stale=0")
                                .header("Accept-Encoding", "gzip")
                                .header("User-Agent","okhttp/3.4.1")
                                .header("Connection","Keep-Alive")
                               .header("Cookie", "install_id=17503554218")
                                .header("Cookie", "qh[360]=1")
                                .header("Cookie","ttreq=1$73e829ecd18e58fe34843bcda620775922fdaf21")
                                .method(original.method(), original.body())
                                .build();
                        return chain.proceed(request);
                    }
                })
                .connectTimeout(Constants.HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(Constants.HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .build();
//        http://hotsoon.snssdk.com/hotsoon/feed/?type=video&max_time=1510465185565&count=20&req_from=feed_loadmore&live_sdk_version=280&iid=17503554218&device_id=41117308918&ac=wifi&channel=xiaomi&aid=1112&app_name=live_stream&version_code=280&version_name=2.8.0&device_platform=android&ssmix=a&device_type=MI+5&device_brand=Xiaomi&os_api=24&os_version=7.0&uuid=862155038066584&openudid=3b29e2b0374408b5&manifest_version_code=280&resolution=1080*1920&dpi=480&update_version_code=2802&ts=1510465207&as=a2750d90370baade47&cp=dbb1a959737004efe2
        new Retrofit.Builder()
                .client(client)
                .baseUrl("https://hotsoon.snssdk.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(VideoService.class).getVideoData(String.valueOf((new Date().getTime())/1000)).subscribe(new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
//                Log.e("nihao",responseBody.toString());
                try {
                    System.out.println(responseBody.string()+"GGGGGG");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onError(Throwable e) {
                System.out.println(e.getMessage()+"GGGGGG");
            }

            @Override
            public void onComplete() {

            }
        });*/
    }
    @Test
    public void test() throws Exception{
    }
}