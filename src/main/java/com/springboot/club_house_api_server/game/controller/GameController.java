package com.springboot.club_house_api_server.game.controller;

import com.springboot.club_house_api_server.game.dto.EndGameDto;
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
    public ResponseEntity<?> startGame(@RequestParam Long gameId) {
        return gameService.startGame(gameId);
    }

    @PostMapping("/end")
    public ResponseEntity<?> endGame(@RequestBody EndGameDto endGameDto) {
        return gameService.endGame(endGameDto);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteGame(@RequestParam long gameId) {
        return gameService.deleteGame(gameId);
    }
}
