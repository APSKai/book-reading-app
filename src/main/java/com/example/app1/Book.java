package com.example.app1;

public class Book {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    private String genres;
    private String author;

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    private String publisher;

    public String getFile_pdf() {
        return file_pdf;
    }

    public void setFile_pdf(String file_pdf) {
        this.file_pdf = file_pdf;
    }

    private String file_pdf;

    public Book(String name, String author, String publisher, String genres) {
        this.author = author;
        this.name = name;
        this.publisher = publisher;
        this.genres = genres;
        //this.file_pdf = file_pdf;
    }
}
