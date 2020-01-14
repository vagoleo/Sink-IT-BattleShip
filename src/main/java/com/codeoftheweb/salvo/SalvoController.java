package com.codeoftheweb.salvo;


import com.codeoftheweb.salvo.entities.*;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import com.codeoftheweb.salvo.repositories.ScoreRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {
    
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    GamePlayerRepository gamePlayerRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    ScoreRepository scoreRepository;
    
    @RequestMapping("/games")
    public Map<String, Object> getAllGames(Authentication auth){
        Map<String, Object> games = new LinkedHashMap<>();
       if (!isGuest(auth))
           games.put("currentPlayer", playerRepository.findByUserName(auth.getName()).playerDTO2());
       else
           games.put("currentPlayer", "guest");
       games.put("games", getGames());
        return games;
    }
    
    private boolean isGuest (Authentication auth){
        return auth == null || auth instanceof AnonymousAuthenticationToken;
    }
    
    public List<Map<String, Object>> getGames () {
        return gameRepository.findAll().stream().map(Game::gameDTO).collect(Collectors.toList());
    }
    
    @RequestMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String,Object>> getGameView(@PathVariable long gamePlayerId, Authentication auth) {
        ResponseEntity<Map<String, Object>> response;
        if(isGuest(auth)){
            response = new ResponseEntity<>(makeMap("error", "You must Log in first"), HttpStatus.UNAUTHORIZED);
        }else{
            GamePlayer gp = gamePlayerRepository.findById(gamePlayerId).orElse(null);
            Player player = playerRepository.findByUserName(auth.getName());
            if(gp == null){
                response = new ResponseEntity<>(makeMap("error", "Not existing game"), HttpStatus.NOT_FOUND);
            }else if(gp.getPlayer().getId() != player.getId()){
                response = new ResponseEntity<>(makeMap("error", "This is not your game"), HttpStatus.UNAUTHORIZED);
            }else{
                response = new ResponseEntity<>(this.gameViewDTO(gp), HttpStatus.OK);
            }
        }
        return response;
    }
    
    private Map<String, Object> gameViewDTO (GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<>();
        
        if (gamePlayer != null) {
            dto.put("gameid", gamePlayer.getGame().getId());
            dto.put("creationDate", gamePlayer.getGame().getCreationDate());
            dto.put("gameState", gamePlayer.getGameState());
            dto.put("gamePlayers", gamePlayer.getGame().getGamePlayers().stream().map(GamePlayer::gamePlayerDTO));
            dto.put("playerview_id", gamePlayer.getPlayer().getId());
            dto.put("ships", gamePlayer.getShips().stream().map(Ship::shipDTO));
            dto.put("salvoes", gamePlayer.getGame().getGamePlayers().stream().flatMap(gp -> gp.getSalvoes().stream().map(Salvo::salvoDTO)));
        } else {
            dto.put("Error", "Not existing game");
        }
        return dto;
    }
    
    
    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createUser (@RequestParam String username, @RequestParam String firstName, @RequestParam String lastName, @RequestParam String password) {
        ResponseEntity<Map<String, Object>> response;
        Player player = playerRepository.findByUserName(username);
        if ((username.isEmpty() || username.contains(" ")) || (password.isEmpty() || password.contains(" "))) {
            response = new ResponseEntity<>(makeMap("message", "Invalid Username or Password"), HttpStatus.FORBIDDEN);
        } else if (player != null) {
            response = new ResponseEntity<>(makeMap("message", "Username already exists"), HttpStatus.FORBIDDEN);
        } else {
            Player newPlayer = playerRepository.save(new Player(username, firstName, lastName, passwordEncoder.encode(password)));
            response = new ResponseEntity<>(makeMap("message", "Registration Successful " + newPlayer.getUserName()), HttpStatus.CREATED);
        }
        return response;
    }
    
    
    @RequestMapping(path = "/games/{gameId}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame (Authentication auth, @PathVariable long gameId){
        ResponseEntity<Map<String, Object>> response;
        if (isGuest(auth)){
            response = new ResponseEntity<>(makeMap("message", "You must Log in first"), HttpStatus.UNAUTHORIZED);
        }else{
            Game game = gameRepository.findById(gameId).orElse(null);
            if(game == null){
                response = new ResponseEntity<>(makeMap("message", "No existing game"), HttpStatus.NOT_FOUND);
            }else if (game.getGamePlayers().size() > 1){
                response = new ResponseEntity<>(makeMap("message", "Game is full"), HttpStatus.FORBIDDEN);
            }else{
                Player player = playerRepository.findByUserName(auth.getName());
                if(game.getGamePlayers().stream().anyMatch(gp -> gp.getPlayer().getId() == player.getId())){
                    response = new ResponseEntity<>(makeMap("message", "You can't play against yourself!"), HttpStatus.FORBIDDEN);
                }else{
                    GamePlayer newGP = gamePlayerRepository.save(new GamePlayer(player, game));
                    response = new ResponseEntity<>(makeMap("gpid", newGP.getId()), HttpStatus.CREATED);
                }
            }
        }
        return response;
    }
    
    
    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame (Authentication auth){
        ResponseEntity<Map<String, Object>> response;
        if (isGuest(auth)){
            response = new ResponseEntity<>(makeMap("error", "You must Log in first"), HttpStatus.UNAUTHORIZED);
        }else {
            Player player = playerRepository.findByUserName(auth.getName());
            Game newGame = gameRepository.save(new Game());
            GamePlayer newGP = gamePlayerRepository.save(new GamePlayer(player, newGame, newGame.getCreationDate()));
            response = new ResponseEntity<>(makeMap("gpid", newGP.getId()), HttpStatus.CREATED);
        }
        return response;
    }
    
    
    @RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> addShips(Authentication auth, @PathVariable long gamePlayerId, @RequestBody List<Ship> ships){
        ResponseEntity<Map<String, Object>> response;
        if(isGuest(auth)){
            response = new ResponseEntity<>(makeMap("message", "You must Log in first"), HttpStatus.UNAUTHORIZED);
        } else {
            GamePlayer gp = gamePlayerRepository.findById(gamePlayerId).orElse(null);
            Player player = playerRepository.findByUserName(auth.getName());
            
            if(gp == null){
                response = new ResponseEntity<>(makeMap("message", "No existing game"), HttpStatus.NOT_FOUND);
            } else if(gp.getPlayer().getId() != player.getId()) {
                response = new ResponseEntity<>(makeMap("message", "This is not your game"), HttpStatus.UNAUTHORIZED);
            } else if(gp.getShips().size() > 0){
                response = new ResponseEntity<>(makeMap("message", "You already have ships"), HttpStatus.FORBIDDEN);
            } else if(ships == null || ships.size() < 5){
                response = new ResponseEntity<>(makeMap("message", "You must add all the ships"), HttpStatus.FORBIDDEN);
            } else {
                if(ships.stream().anyMatch(ship -> this.isOutOfRange(ship))){
                    response = new ResponseEntity<>(makeMap("message", "Your ships are out of range"), HttpStatus.FORBIDDEN);
                } else if(ships.stream().anyMatch(ship -> this.isNotConsecutive(ship))){
                    response = new ResponseEntity<>(makeMap("message", "Your ships are not consecutive"), HttpStatus.FORBIDDEN);
                } else if(this.areOverlapped(ships)){
                    response = new ResponseEntity<>(makeMap("message", "Your ships are overlapped"), HttpStatus.FORBIDDEN);
                } else {
                    ships.forEach(ship -> gp.addShip(ship));
                    gamePlayerRepository.save(gp);
                    response = new ResponseEntity<>(makeMap("message", "Success, ships added"), HttpStatus.CREATED);
                }
            }
        }
        return response;
    }
    
    
    @RequestMapping(path = "/games/players/{gamePlayerId}/salvoes", method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> addSalvo(Authentication auth, @PathVariable long gamePlayerId, @RequestBody List<String> shots){
        ResponseEntity<Map<String, Object>> response;
        if(isGuest(auth)){
            response = new ResponseEntity<>(makeMap("message", "You must Log in first"), HttpStatus.UNAUTHORIZED);
        }else {
            GamePlayer gp = gamePlayerRepository.findById(gamePlayerId).orElse(null);
            Player player = playerRepository.findByUserName(auth.getName());
            
                if(gp == null){
                response = new ResponseEntity<>(makeMap("message", "No existing game"), HttpStatus.NOT_FOUND);
                } else if(gp.getPlayer().getId() != player.getId()){
                    response = new ResponseEntity<>(makeMap("message", "This is not your game"), HttpStatus.UNAUTHORIZED);
                } else if(shots.size() != 2){
                    response = new ResponseEntity<>(makeMap("message", "Invalid shots"), HttpStatus.FORBIDDEN);
                } else {

                    GamePlayer.GameState gameState = gp.getGameState();

                    if(!gameState.equals(GamePlayer.GameState.FIRE)){
                        response = new ResponseEntity<>(makeMap("message", gp.getGameState()), HttpStatus.FORBIDDEN);
                    } else {
                        int turn = gp.getSalvoes().size() + 1;
                        Salvo salvo = new Salvo(turn, shots);
                        gp.addSalvo(salvo);
                        gamePlayerRepository.save(gp);
                        response = new ResponseEntity<>(makeMap("message", "Shots fired!"), HttpStatus.CREATED);


                        if (gp.getGameState().equals(GamePlayer.GameState.WON)){
                            scoreRepository.save (new Score(10, gp.getGame(), gp.getPlayer(), LocalDateTime.now()));
                            scoreRepository.save (new Score(0, gp.getGame(), gp.getOpponent().getPlayer(), LocalDateTime.now()));
                        } else if (gp.getGameState().equals(GamePlayer.GameState.LOST)){
                            scoreRepository.save (new Score(0, gp.getGame(), gp.getPlayer(), LocalDateTime.now()));
                            scoreRepository.save (new Score(10, gp.getGame(), gp.getOpponent().getPlayer(), LocalDateTime.now()));
                        } else if (gp.getGameState().equals(GamePlayer.GameState.TIED)){
                            scoreRepository.save (new Score(5, gp.getGame(), gp.getPlayer(), LocalDateTime.now()));
                            scoreRepository.save (new Score(5, gp.getGame(), gp.getOpponent().getPlayer(), LocalDateTime.now()));
                        }
                    }
                }
            }
        return response;
    }
    
    
    
    
    
    private boolean isOutOfRange(Ship ship){
        
        for(String cell : ship.getLocations()){
            if(!(cell instanceof String) || cell.length() < 2){
                return true;
            }
            char y = cell.substring(0,1).charAt(0);
            Integer x;
            try{
                x = Integer.parseInt(cell.substring(1));
            }catch(NumberFormatException e){
                x = 99;
            };
            
            if(x < 1 || x > 10 || y < 'A' || y > 'J'){
                return true;
            }
        }
        
        return false;
    }
    
    private boolean isNotConsecutive(Ship ship){
        
        List<String> cells = ship.getLocations();
        
        boolean isVertical = cells.get(0).charAt(0) != cells.get(1).charAt(0);
        
        for(int i = 0; i < cells.size(); i ++){
            
            if(i < cells.size() - 1){
                if(isVertical){
                    char yChar = cells.get(i).substring(0,1).charAt(0);
                    char compareChar = cells.get(i + 1).substring(0,1).charAt(0);
                    if(compareChar - yChar != 1){
                        return true;
                    }
                } else {
                    Integer xInt = Integer.parseInt(cells.get(i).substring(1));
                    Integer compareInt = Integer.parseInt(cells.get(i + 1).substring(1));
                    if(compareInt - xInt != 1){
                        return true;
                    }
                }
            }
            
            
            
            for(int j = i + 1; j < cells.size(); j ++){
                
                if(isVertical){
                    if(!cells.get(i).substring(1).equals(cells.get(j).substring(1))){
                        return true;
                    }
                    
                }else{
                    if(!cells.get(i).substring(0,1).equals(cells.get(j).substring(0,1))){
                        return true;
                    }
                    
                }
            }
        }
        
        return false;
    }
    
    private boolean areOverlapped(List<Ship> ships){
        List<String> allCells = new ArrayList<>();
        
        ships.forEach(ship -> allCells.addAll(ship.getLocations()));
        
        for(int i = 0; i < allCells.size(); i ++){
            for(int j = i + 1; j < allCells.size(); j ++){
                if(allCells.get(i).equals(allCells.get(j))){
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private Map<String,Object> makeMap(String key, Object value){
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
    
    
}
