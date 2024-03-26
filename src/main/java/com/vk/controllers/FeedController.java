package com.vk.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feed")
public class FeedController {

    @GetMapping
    public ResponseEntity<String> getFeed(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok("requested feed by [" + userDetails.getUsername() + "]");
    }
}
