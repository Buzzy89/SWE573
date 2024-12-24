package com.yusuf.mysticalObject.controller;

import com.yusuf.mysticalObject.dto.UserProfileDTO;
import com.yusuf.mysticalObject.entity.User;
import com.yusuf.mysticalObject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable Long userId) {
        UserProfileDTO profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/{userId}/avatar")
    public ResponseEntity<User> updateAvatar(
            @PathVariable Long userId,
            @RequestParam("avatar") MultipartFile file) {
        try {
            User updatedUser = userService.updateAvatar(userId, file);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 