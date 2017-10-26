package com.dzg.gank.module;

import java.util.List;

/**
 * Created by dengzhouguang on 2017/10/11.
 */

public class GankBean {

    /**
     * error : false
     * results : [{"_id":"59dc7149421aa94e07d18490","createdAt":"2017-10-10T15:05:45.902Z","desc":"使用 Kotlin 实现的一个 Dribbble 客户端","images":["http://img.gank.io/05d6552f-97ba-4d52-ad33-3caeba5cb327","http://img.gank.io/84594f1b-d10e-42a3-afc1-c7d2bf9ac0cf"],"publishedAt":"2017-10-11T12:40:42.545Z","source":"web","type":"Android","url":"https://github.com/armcha/Ribble","used":true,"who":" Thunder Bouble"},{"_id":"59dcca81421aa94e0053bddf","createdAt":"2017-10-10T21:26:25.797Z","desc":"最近在给某某银行做项目的时，涉及到了数据埋点，性能监控等问题，那我们起先想到的有两种方案，方案之一就是借助第三方，比如友盟、Bugly等，由于项目是部署在银行的网络框架之内的，所以该方案不可行。","publishedAt":"2017-10-11T12:40:42.545Z","source":"web","type":"Android","url":"https://mp.weixin.qq.com/s?__biz=MzIyMjQ0MTU0NA==&mid=2247484445&idx=1&sn=8eef04a7932b58ef0012643db228fb32&chksm=e82c3d3adf5bb42c88333160a88c7b05fb5f45798434afa956fe1f1a58c7713ef121c7ea0af4&scene=0&key=7460e137ddd94f92f668e812cfc0aef8fde2bdf7943c7409875cce12a3baed3526f31e4a707ed86896ee8ddbbf761bb2f09b2d7406c3b9016589495240d835d967a2141231c43d084635a7df11647fb0&ascene=0&uin=MjMzMzgwOTEwMQ%3D%3D&devicetype=iMac+MacBookPro12%2C1+OSX+OSX+10.10.5+build(14F27)&version=11020201&pass_ticket=54ym37fDoXgDZm7nzjGt6KNDR9%2F9ZIU8%2Bo5kNcGEXqi8GKijls6et5TXcXxbERi%2F","used":true,"who":"Tamic (码小白)"},{"_id":"59dd6bde421aa90fe50c0151","createdAt":"2017-10-11T08:54:54.500Z","desc":"这可能是世界上最小的 Android APK，作者教你如何疯狂缩小 APK 体积。","publishedAt":"2017-10-11T12:40:42.545Z","source":"chrome","type":"Android","url":"https://fractalwrench.co.uk/posts/playing-apk-golf-how-low-can-an-android-app-go/","used":true,"who":"代码家"},{"_id":"59dd7794421aa90fef20346d","createdAt":"2017-10-11T09:44:52.573Z","desc":"Jenkins实现自动化打包并集成fir平台（超详细）","publishedAt":"2017-10-11T12:40:42.545Z","source":"web","type":"Android","url":"https://mp.weixin.qq.com/s?__biz=MzIwMzYwMTk1NA==&mid=2247487305&idx=1&sn=e9bdff7cb91b0eabbe67ec965f3be660","used":true,"who":"陈宇明"},{"_id":"59db479d421aa94e032b1ea6","createdAt":"2017-10-09T17:55:41.830Z","desc":"Kotlin 语言下设计模式的不同实现","publishedAt":"2017-10-10T12:41:34.882Z","source":"web","type":"Android","url":"http://johnnyshieh.me/posts/kotlin-design-pattern-impl/","used":true,"who":"Johnny Shieh"},{"_id":"59db4f71421aa94e04c2adc4","createdAt":"2017-10-09T18:29:05.420Z","desc":"Kotlin 1.2 Beta 发布了","publishedAt":"2017-10-10T12:41:34.882Z","source":"web","type":"Android","url":"https://mp.weixin.qq.com/s/INbaqAjjY5AlmGvKg7_6pw","used":true,"who":" Thunder Bouble"},{"_id":"59db52ab421aa94e032b1ea7","createdAt":"2017-10-09T18:42:51.991Z","desc":"一个用 Kotlin 实现的简单 Material design  的\u201c滑动解锁\u201d小部件","publishedAt":"2017-10-10T12:41:34.882Z","source":"web","type":"Android","url":"https://github.com/cortinico/slidetoact","used":true,"who":" Thunder Bouble"},{"_id":"59db6938421aa94e0053bdd9","createdAt":"2017-10-09T20:19:04.768Z","desc":"See what the GitHub community is most excited about today.","images":["http://img.gank.io/daf8a302-11e2-45a5-a54b-448b7847fba0"],"publishedAt":"2017-10-10T12:41:34.882Z","source":"web","type":"Android","url":"https://github.com/Werb/G-Trending","used":true,"who":"Werb"},{"_id":"59dc3296421aa94e07d1848d","createdAt":"2017-10-10T10:38:14.886Z","desc":"Android 彻底组件化 demo 发布","publishedAt":"2017-10-10T12:41:34.882Z","source":"web","type":"Android","url":"http://mp.weixin.qq.com/s/aWUixHMhU8g0W4dapr0HPg","used":true,"who":null},{"_id":"59dc4dd0421aa94e07d1848e","createdAt":"2017-10-10T12:34:24.701Z","desc":"Android 展开型布局菜单。","images":["http://img.gank.io/b39a4adf-b48a-4f2e-990f-b672027bf643","http://img.gank.io/a7b25e90-63ba-4703-bd6c-dc4483fa5172"],"publishedAt":"2017-10-10T12:41:34.882Z","source":"chrome","type":"Android","url":"https://github.com/iammert/ScalingLayout","used":true,"who":"代码家"}]
     */

    private boolean error;
    private List<ResultsBean> results;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean {
        /**
         * _id : 59dc7149421aa94e07d18490
         * createdAt : 2017-10-10T15:05:45.902Z
         * desc : 使用 Kotlin 实现的一个 Dribbble 客户端
         * images : ["http://img.gank.io/05d6552f-97ba-4d52-ad33-3caeba5cb327","http://img.gank.io/84594f1b-d10e-42a3-afc1-c7d2bf9ac0cf"]
         * publishedAt : 2017-10-11T12:40:42.545Z
         * source : web
         * type : Android
         * url : https://github.com/armcha/Ribble
         * used : true
         * who :  Thunder Bouble
         */

        private String _id;
        private String createdAt;
        private String desc;
        private String publishedAt;
        private String source;
        private String type;
        private String url;
        private boolean used;
        private String who;
        private List<String> images;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getPublishedAt() {
            return publishedAt;
        }

        public void setPublishedAt(String publishedAt) {
            this.publishedAt = publishedAt;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public boolean isUsed() {
            return used;
        }

        public void setUsed(boolean used) {
            this.used = used;
        }

        public String getWho() {
            return who;
        }

        public void setWho(String who) {
            this.who = who;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }

        @Override
        public String toString() {
            return "ResultsBean{" +
                    "_id='" + _id + '\'' +
                    ", createdAt='" + createdAt + '\'' +
                    ", desc='" + desc + '\'' +
                    ", publishedAt='" + publishedAt + '\'' +
                    ", source='" + source + '\'' +
                    ", type='" + type + '\'' +
                    ", url='" + url + '\'' +
                    ", used=" + used +
                    ", who='" + who + '\'' +
                    ", images=" + images +
                    '}';
        }
    }
}
