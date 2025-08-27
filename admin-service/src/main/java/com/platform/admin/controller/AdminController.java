package com.platform.admin.controller;

import com.platform.admin.dto.UserDto;
import com.platform.admin.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('admin')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable String userId) {
        UserDto user = adminService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{userId}/enable")
    public ResponseEntity<String> enableUser(@PathVariable String userId) {
        adminService.enableUser(userId);
        return ResponseEntity.ok("User enabled successfully");
    }

    @PutMapping("/users/{userId}/disable")
    public ResponseEntity<String> disableUser(@PathVariable String userId) {
        adminService.disableUser(userId);
        return ResponseEntity.ok("User disabled successfully");
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }

    @GetMapping("/ads/all")
    public ResponseEntity<Object> getAllAds() {
        Object ads = adminService.getAllAds();
        return ResponseEntity.ok(ads);
    }

    @PutMapping("/ads/{adId}/status")
    public ResponseEntity<String> updateAdStatus(@PathVariable Long adId, @RequestParam String status) {
        adminService.updateAdStatus(adId, status);
        return ResponseEntity.ok("Ad status updated successfully");
    }

    @GetMapping("/stats/dashboard")
    public ResponseEntity<Object> getDashboardStats() {
        Object stats = adminService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }
}