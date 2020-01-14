package com.codeoftheweb.salvo.entities;
import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")

    //-----------Attributes-----------
    private long id;
    private LocalDateTime creationDate;

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    private Set<GamePlayer> gamePlayers = new HashSet<>();
    
    @OneToMany(mappedBy="game", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    private Set<Score> scores = new HashSet<>();
    
    


    //-----------Constructors-----------
    public Game() {
        this.creationDate = LocalDateTime.now();
    };

    public Game(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }


    //---------Getters & Setters-----------

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {

        this.creationDate = creationDate;
    }

    public long getId() {
        return id;
    }

    public Set<GamePlayer> getGamePlayers() {

        return gamePlayers;
    }

    
    //-----------Methods-----------

    public List<Player> getPlayers(){
      return this.gamePlayers.stream().map(gp -> gp.getPlayer()).collect(Collectors.toList());
    };


    //------------[DTO]------------
    
    public Map<String, Object> gameDTO(){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("gameid", this.getId());
        dto.put("created", this.getCreationDate());
        dto.put("gamePlayers", this.getGamePlayers().stream().map(GamePlayer::gamePlayerDTO).collect(Collectors.toList()));
        return dto;
    }

}


