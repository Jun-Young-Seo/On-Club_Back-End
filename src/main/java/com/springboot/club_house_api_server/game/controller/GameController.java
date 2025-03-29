package com.springboot.club_house_api_server.game.controller;

import com.springboot.club_house_api_server.game.dto.GameStartDto;
import com.springboot.club_house_api_server.game.service.GameService;
import com.springboot.club_house_api_server.game.dto.CreateGameRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/game")
public class GameController {
    private final GameService gameService;

    @GetMapping("/all_games")
    public ResponseEntity<?> getAllGamesByGameId(@RequestParam("eventId") Long eventId) {
        return gameService.getAllGamesByEventId(eventId);
    }
    @PostMapping("/make")
    public ResponseEntity<?> makeGame(@RequestBody CreateGameRequestDto createGameRequestDto) {
        return gameService.makeGame(createGameRequestDto);
    }

    @PostMapping("/start")
    public ResponseEntity<?> startGame(@RequestBody GameStartDto gameStartDto) {
        return gameService.startGame(gameStartDto);
    }

    @PostMapping("/end")
    public ResponseEntity<?> endGame(@RequestParam long gameId, @RequestParam int score1, @RequestParam int score2) {
        return gameService.endGame(gameId, score1, score2);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteGame(@RequestParam long gameId) {
        return gameService.deleteGame(gameId);
    }
}
