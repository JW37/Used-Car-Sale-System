package com.automart.controller;

import com.automart.dto.CarResponse;
import com.automart.security.UserPrincipal;
import com.automart.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public ResponseEntity<List<CarResponse>> getFavorites(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(favoriteService.getFavorites(principal.getId()));
    }

    @PostMapping("/{carId}")
    public ResponseEntity<Void> addFavorite(@PathVariable Long carId,
                                             @AuthenticationPrincipal UserPrincipal principal) {
        favoriteService.addFavorite(principal.getId(), carId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{carId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long carId,
                                                @AuthenticationPrincipal UserPrincipal principal) {
        favoriteService.removeFavorite(principal.getId(), carId);
        return ResponseEntity.noContent().build();
    }
}
