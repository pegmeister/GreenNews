package com.example.pegmeister.greennews;

public class News {

    // setup private variables
    private String newsTitle;
    private String newsCategory;
    private String newsAuthor;
    private String newsPubDate;
    private String newsUrl;

    // create News constructor to take the following param
    public News(String newsTitle, String newsCategory, String newsAuthor, String newsPubDate, String newsUrl){
        this.newsTitle = newsTitle;
        this.newsCategory = newsCategory;
        this.newsAuthor = newsAuthor;
        this.newsPubDate = newsPubDate;
        this.newsUrl = newsUrl;
    }

    // getter method to return news title
    public String getNewsTitle(){
        return newsTitle;
    }

    // getter method to return news category
    public String getNewsCategory() {
        return newsCategory;
    }

    // getter method to return news author
    public String getNewsAuthor() {
        return newsAuthor;
    }

    // getter method to return news publication date
    public String getNewsPubDate() {
        return newsPubDate;
    }

    // getter method to return web URL
    public String getNewsUrl() {
        return newsUrl;
    }
}
