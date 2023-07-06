package com.example.mydiary;

public class Diary {
    private long id;//主键
    private String title;
    private String author;
    private String content;//笔记内容
    private String time;//笔记时间
    private int tog;//笔记标签

    public Diary() {
    }

    public Diary(String title, String author, String content, String time, int tog) {
        this.title = title;
        this.author = author;
        this.content = content;
        this.time = time;
        this.tog = tog;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public int getTog() {
        return tog;
    }
    public void setTog(int tog) {
        this.tog = tog;
    }
    @Override
    public String toString() {
        return title + "\n" + author + "\n" + content + "\n" + time.substring(5, 16) + " " + id;
    }

}
