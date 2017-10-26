package com.dzg.gank.module;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/7.
 */

public class DianYingBean implements Serializable{
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }
    

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }


    private String translation;
    private String name;
    private String country;
    private String type;
    private String language;
    private String score;
    private String subtitle;
    private String format;
    private String measure;
    private String size;
    private String time;

    private String actors;

    @Override
    public String toString() {
        return "DianYingBean{" +
                "translation='" + translation + '\'' +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", type='" + type + '\'' +
                ", language='" + language + '\'' +
                ", score='" + score + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", format='" + format + '\'' +
                ", measure='" + measure + '\'' +
                ", size='" + size + '\'' +
                ", time='" + time + '\'' +
                ", actors='" + actors + '\'' +
                ", director='" + director + '\'' +
                ", url='" + url + '\'' +
                ", story='" + story + '\'' +
                ", title='" + title + '\'' +
                ", downUrl='" + downUrl + '\'' +
                '}';
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    private String director;
    private String url;
    private String story;
    private String title;
    private String downUrl;

}
