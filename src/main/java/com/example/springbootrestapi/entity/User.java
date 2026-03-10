package com.example.springbootrestapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

// TODO: add validation annotations later
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    // BAD: vague field name - should be "password" or "passwordHash"
    private String pwd;

    // BAD: single-letter field names - unreadable
    private String r;   // role
    private int s;      // status: 0=inactive, 1=active, 2=suspended
    private String e;   // email

    // BAD: storing plain text token - should never be persisted
    private String tkn;

    public User() {}

    public User(String username, String pwd, String r, String e) {
        this.username = username;
        this.pwd = pwd;
        this.r = r;
        this.e = e;
        this.s = 1;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPwd() { return pwd; }
    public void setPwd(String pwd) { this.pwd = pwd; }

    public String getR() { return r; }
    public void setR(String r) { this.r = r; }

    public int getS() { return s; }
    public void setS(int s) { this.s = s; }

    public String getE() { return e; }
    public void setE(String e) { this.e = e; }

    public String getTkn() { return tkn; }
    public void setTkn(String tkn) { this.tkn = tkn; }
}
