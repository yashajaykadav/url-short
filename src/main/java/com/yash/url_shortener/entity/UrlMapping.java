package com.yash.url_shortener.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "url_mappings")
public class UrlMapping{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false , unique = true)
    private String shortCode;
    
    @Column(nullable = false , length = 2048)
    private String longUrl;
    
    private LocalDateTime createAt;
    
    private Integer clickCount = 0 ; 
    
    public UrlMapping() {}
    
    public UrlMapping(String shortCode , String longUrl) {
    	this.shortCode = shortCode;
    	this.longUrl = longUrl;
    	this.clickCount = 0;
    	this.createAt = LocalDateTime.now();
    }
    
    public void incrementClickCount() {
    	this.clickCount++;
    }
    
}