package com.codeoftheweb.salvo.entities;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    
    //----------Attributes------------
    
    private long id;
    private double score;
    private LocalDateTime finishDate;
    
    @ManyToOne(fetch = FetchType.EAGER)     //Relación ManyToOne con Game
    @JoinColumn(name="game_id")
    private Game game;
    
    @ManyToOne(fetch = FetchType.EAGER)     //Relación ManyToOne con Player
    @JoinColumn(name="player_id")
    private Player player;
    
    
    
    
    //--------------Constructors-------------
    
    public Score(){
        this.finishDate = LocalDateTime.now();
    }
    
    public Score (double score, Game game, Player player, LocalDateTime finishDate) {
        this.score = score;
        this.finishDate = finishDate;
        this.game = game;
        this.player = player;
    }
    
    public Score (double score, Game game, Player player) {
        this.score = score;
        this.finishDate = LocalDateTime.now();
        this.game = game;
        this.player = player;
    }
    
    
    
    //------------Getters & Setters------------
    
    
    public long getId () {
        return id;
    }
    
    public double getScore () {
        return score;
    }
    
    public void setScore (double score) {
        this.score = score;
    }
    
    public LocalDateTime getFinishDate () {
        return finishDate;
    }
    
    public void setFinishDate (LocalDateTime finishDate) {
        this.finishDate = finishDate;
    }
    
    public Game getGame () {
        return game;
    }
    
    public void setGame (Game game) {
        this.game = game;
    }
    
    public Player getPlayer () {
        return player;
    }
    
    public void setPlayer (Player player) {
        this.player = player;
    }
    
    
    
    //-------------[DTO]--------------
    
}
