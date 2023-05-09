package com.example.app1;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

public class CSVLoader {
    private FileReader reader;
    private CSVParser csvParser;

    public Vector<Book> res;

    public CSVLoader(String csvFilePath) throws IOException {
        FileReader reader = new FileReader(csvFilePath, StandardCharsets.UTF_8);
        try {
            csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withDelimiter(',').withHeader());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createList() {
        res = new Vector<>();
        for (CSVRecord record : csvParser) {
            String name = record.get("book_name");
            String author = record.get("author");
            String publisher = record.get("publisher");
            String genres = record.get("genres");
            String pdf_file = record.get("file_name");
            Book tmp = new Book(name,author,publisher,genres, pdf_file);
            res.add(tmp);
        }
    }

    public Vector<Book> filterByName(String name, Vector<Book> inp) {
        for(int i = 0; i<inp.size(); i++) {
            if(!inp.get(i).getName().contains(name)) {
                inp.remove(i);
                i--;
            }
        }
        return inp;
    }

    public Vector<Book> filterByAuthor(String author, Vector<Book> inp) {
        for(int i = 0; i<inp.size(); i++) {
            if(!inp.get(i).getAuthor().contains(author)) {
                inp.remove(i);
                i--;
            }
        }
        return inp;
    }

    public Vector<Book> filterByPublisher(String publisher, Vector<Book> inp) {
        for(int i = 0; i<inp.size(); i++) {
            if(!inp.get(i).getPublisher().contains(publisher)) {
                inp.remove(i);
                i--;
            }
        }
        return inp;
    }

    public Vector<Book> filterByGenres(String genres, Vector<Book> inp) {
        for(int i = 0; i<inp.size(); i++) {
            if(!inp.get(i).getGenres().contains(genres)) {
                inp.remove(i);
                i--;
            }
        }
        return inp;
    }
}
