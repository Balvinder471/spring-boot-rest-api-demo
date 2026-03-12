package com.example.springbootrestapi.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "reviews")
public class Review {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // BAD: inconsistent indentation (2-space vs 4-space mixed)
    private Long bookId;
  private Long userId;

    // BAD: vague single-letter field names
    private int r;        // rating (1-5)
    private String t;    // text/review content
    private String n;    // reviewer name

  // BAD: magic string status values instead of enum
    private String s;    // status: "PENDING", "APPROVED", "REJECTED"

    // BAD: vague name - should be "helpfulnessCount"
  private int h;

    // BAD: using deprecated java.util.Date instead of LocalDateTime
    private Date d;      // review date

  // BAD: unused field leftover from old design
    private String tmp;

    // BAD: another unused field
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
        // BAD: status logic embedded in constructor - should be in service
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
