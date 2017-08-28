package com.example.demo.bookmarks;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Bookmark {

  @JsonIgnore
  @ManyToOne
  private Account account;


  @Id
  @GeneratedValue
  private Long id;

  Bookmark() { // jpa only
  }

  public Bookmark(Account account, String uri, String description) {
    this.uri = uri;
    this.description = description;
    this.account = account;
  }

  public String uri;
  public String description;

  public Account getAccount() {
    return account;
  }

  public Long getId() {
    return id;
  }

  public String getUri() {
    return uri;
  }

  public String getDescription() {
    return description;
  }


}
