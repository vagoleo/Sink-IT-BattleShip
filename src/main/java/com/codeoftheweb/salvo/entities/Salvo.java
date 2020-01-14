package com.codeoftheweb.salvo.entities;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

import com.codeoftheweb.salvo.entities.GamePlayer;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class Salvo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    
    //----------Attributes------------
    
    private long id;
    private int turn;
    
    @ElementCollection
    private List<String> locations = new ArrayList<>();
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;
    
    
    //----------Constructors-----------
    
    public Salvo (){};
    
    public Salvo (int turn, List<String> locations) {
        this.turn = turn;
        this.locations = locations;
    }
    
    public Salvo (int turn, GamePlayer gamePlayer, List<String> locations) {
        this.turn = turn;
        this.gamePlayer = gamePlayer;
        this.locations = locations;
    }
    
    //---------Getters & Setters-----------
    
    public long getId () {
        return id;
    }
    
    public List<String> getLocations () {
        return locations;
    }
    
    public GamePlayer getGamePlayer () {
        return gamePlayer;
    }
    
    public int getTurn () {
        return turn;
    }
    
    public void setGamePlayer (GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }
    
    public void setLocations (List<String> locations) {
        this.locations = locations;
    }
    
    private List<String> getHitted(List<String> myShots, Set<Ship> oppShips){
        List<String> EnemyLocs = new ArrayList<>();
        oppShips.forEach(ship -> EnemyLocs.addAll(ship.getLocations()));
        
        return myShots.stream().filter(shot -> EnemyLocs.stream().anyMatch(loc -> loc.equals(shot))).collect(Collectors.toList());
    }
    
    
    private List<Ship> getSunkenShips (Set<Salvo> mySalvoes, Set<Ship> oppShips){
        List<String> allShots = new ArrayList<>();
        mySalvoes.forEach(salvo -> allShots.addAll(salvo.getLocations()));
        
        return oppShips.stream().filter(ship -> allShots.containsAll(ship.getLocations())).collect(Collectors.toList());
    }
    
    
    //-------------[DTO]--------------
    public Map<String, Object> salvoDTO(){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("playerid", this.getGamePlayer().getPlayer().getId());
        dto.put("turn", this.getTurn());
        dto.put("locations", this.getLocations());
        
        GamePlayer opponent = this.getGamePlayer().getOpponent();
        if(opponent != null){
            Set<Ship> enemyShips = opponent.getShips();
            
            dto.put("hits", this.getHitted(this.getLocations(), enemyShips));
            
            Set<Salvo> mySalvoes = this.getGamePlayer().getSalvoes().stream().filter(salvo -> salvo.getTurn() <= this.getTurn()).collect(Collectors.toSet());
            
            dto.put("sunkenShips", this.getSunkenShips(mySalvoes, enemyShips).stream().map(Ship::shipDTO));
        }
        
        
        return dto;
    }
    
}
