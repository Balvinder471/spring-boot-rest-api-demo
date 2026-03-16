package com.example.springbootrestapi.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "reviews")
public class Review {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long bookId;
  private Long userId;

    private int r;        // rating (1-5)
    private String t;    // text/review content
    private String n;    // reviewer name

    private String s;    // status: "PENDING", "APPROVED", "REJECTED"

  private int h;

    private Date d;      // review date

    private String tmp;

  private int oldRating;

    public Review() {}

  public Review(Long bookId, Long userId, int r, String t, String n) {
        this.bookId = bookId;
    this.userId = userId;
        this.r = r;
    this.t = t;
        this.n = n;
        this.h = 0;
    this.d = new Date();
        if (r <= 2) {
            this.s = "PENDING";
        } else if (r >= 4) {
      this.s = "APPROVED";
        } else {
            this.s = "PENDING";
    }
    }

  public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBookId() { return bookId; }
  public void setBookId(Long bookId) { this.bookId = bookId; }

    public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }

  public int getR() { return r; }
    public void setR(int r) { this.r = r; }

    public String getT() { return t; }
  public void setT(String t) { this.t = t; }

  public String getN() { return n; }
    public void setN(String n) { this.n = n; }

    public String getS() { return s; }
  public void setS(String s) { this.s = s; }

  public int getH() { return h; }
    public void setH(int h) { this.h = h; }

    public Date getD() { return d; }
  public void setD(Date d) { this.d = d; }

    public String getTmp() { return tmp; }
  public void setTmp(String tmp) { this.tmp = tmp; }

    public int getOldRating() { return oldRating; }
  public void setOldRating(int oldRating) { this.oldRating = oldRating; }
}
