package com.example.demo.bookmarks;

import java.net.URI;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.ws.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/bookmarks")
public class BookmarkRestController {

  private final BookmarkRepository bookmarkRepository;
  private final AccountRepository accountRepository;

  @Autowired
  BookmarkRestController(BookmarkRepository bookmarkRepository,
      AccountRepository accountRepository) {
    this.bookmarkRepository = bookmarkRepository;
    this.accountRepository = accountRepository;
  }

  @RequestMapping(method = RequestMethod.GET)
  Resources<BookmarkResource> readBookmarks(Principal principal) {
    this.validateUser(principal);
    List<BookmarkResource> bookmarkResourceList = bookmarkRepository.findByAccountUsername(principal.getName()).stream().map(BookmarkResource::new).collect(Collectors.toList());
    return new Resources<>(bookmarkResourceList);
  }


  @RequestMapping(method = RequestMethod.POST)
  ResponseEntity<?> add(Principal principal, @RequestBody Bookmark input) {
    this.validateUser(principal);

    return this.accountRepository.findByUsername(principal.getName()).map(account -> {
      Bookmark bookmark =
          bookmarkRepository.save(new Bookmark(account, input.uri, input.description));

      Link forOneBookmark = new BookmarkResource(bookmark).getLink(Link.REL_SELF);

      return ResponseEntity.created(URI.create(forOneBookmark.getHref())).build();
    }).orElse(ResponseEntity.noContent().build());
  }

  @RequestMapping(method = RequestMethod.GET, value = "/{bookmarkId}")
  BookmarkResource readBookmark(Principal principal, @PathVariable Long bookmarkId) {
    this.validateUser(principal);
    return new BookmarkResource(this.bookmarkRepository.findOne(bookmarkId));
  }

  private void validateUser(Principal principal) {
    String userId = principal.getName();
    this.accountRepository.findByUsername(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));
  }
}
