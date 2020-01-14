package com.codeoftheweb.salvo.entities;
import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")

    
    //-------------Attributes-------------
    private long id;
    private LocalDateTime joinDate;

    @ManyToOne(fetch = FetchType.EAGER)     //Relación ManyToOne con Player
    @JoinColumn(name="player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)     //Relación ManyToOne con Game
    @JoinColumn(name="game_id")
    private Game game;

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Ship> ships = new HashSet<>();
    
    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Salvo> salvoes = new HashSet<>();
    
    



    //--------------Constructors-------------
    public GamePlayer() {
        this.joinDate = LocalDateTime.now();
    };

    public GamePlayer(Player player, Game game) {
        this.player = player;
        this.game = game;
        this.joinDate = LocalDateTime.now();
    }
    
    public GamePlayer(Player player, Game game, LocalDateTime joindate) {
        this.player = player;
        this.game = game;
        this.joinDate = joindate;
    }
    
    
    //------------Getters & Setters------------
    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDateTime joinDate) {
        this.joinDate = joinDate;
    }

    public long getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
    
    public Set<Ship> getShips () {
        return ships;
    }
    
    public Set<Salvo> getSalvoes () {
        return salvoes;
    }
    
    public GamePlayer getOpponent(){
        return this.getGame().getGamePlayers().stream().filter(gp -> gp.getId() != this.getId()).findFirst().orElse(null);
    }
    
    public void addShip(Ship ship){
        this.ships.add(ship);
        ship.setGamePlayer (this);
    }
    
    public void addSalvo(Salvo salvo){
        this.salvoes.add(salvo);
        salvo.setGamePlayer (this);
    }



    private List<Ship> getSunkenShips (Set<Salvo> mySalvoes, Set<Ship> oppShips){
        List<String> allShots = new ArrayList<>();
        mySalvoes.forEach(salvo -> allShots.addAll(salvo.getLocations()));

        return oppShips.stream().filter(ship -> allShots.containsAll(ship.getLocations())).collect(Collectors.toList());
    }
    


    //-----------GAME LOGIC--------------//

    public enum GameState{
        UNDEFINED,
        ENTER_SHIPS,
        WAIT_OPPONENT,
        WAIT_OPPONENT_SHIPS,
        FIRE,
        WAIT,
        WON,
        LOST,
        TIED
    }

    public GameState getGameState(){
        GameState gameState = GameState.UNDEFINED;

        GamePlayer opponent = this.getOpponent();

        if(opponent == null){
            gameState = GameState.WAIT_OPPONENT;
        } else {
            if(this.getShips().size() == 0){
                gameState = GameState.ENTER_SHIPS;
            } else if(opponent.getShips().size() == 0){
                gameState = GameState.WAIT_OPPONENT_SHIPS;
            } else {
                boolean firstPlayer = this.getId() < opponent.getId();
                int myTurn = this.getSalvoes().size() + 1;
                int opponentTurn = opponent.getSalvoes().size() + 1;

                if (firstPlayer & myTurn == opponentTurn){
                    gameState = GameState.FIRE;
                } else if(!firstPlayer & myTurn < opponentTurn){
                    gameState = GameState.FIRE;
                } else {
                    gameState = GameState.WAIT;
                }

                int mySunkenShips = this.getSunkenShips(this.getSalvoes(), opponent.getShips()).size();
                int opponentSunkenShips = opponent.getSunkenShips(opponent.getSalvoes(), this.getShips()).size();

                if (myTurn == opponentTurn){
                    if(mySunkenShips == 5 & opponentSunkenShips < 5){
                        gameState = GameState.WON;
                    } else if (opponentSunkenShips == 5 & mySunkenShips < 5){
                        gameState = GameState.LOST;
                    } else if (opponentSunkenShips == 5 & mySunkenShips == 5){
                        gameState = GameState.TIED;
                    }
                }
            }
        }

        return gameState;
    }
    
    
    //-------------[DTO]--------------
    public Map<String, Object> gamePlayerDTO(){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("gpid", this.getId());
        dto.put("player", this.getPlayer().playerDTO());
        
        Score score = this.getPlayer().getScoreByGame(this.getGame());
        if(score != null)
            dto.put("score", score.getScore());
        else
            dto.put("score", null);
        
        return dto;
    }


}
