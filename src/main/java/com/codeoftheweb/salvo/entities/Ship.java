package com.codeoftheweb.salvo.entities;

import javax.persistence.*;
import java.util.*;

import com.codeoftheweb.salvo.entities.GamePlayer;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    
    //----------Attributes------------
    
    private long id;
    private String type;
    
    @ElementCollection
    private List<String> locations = new ArrayList<>();
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;
    
    
    //----------Constructors-----------
    
    public Ship (){};
    
    public Ship (String type, List<String> locations) {
        this.type = type;
        this.locations = locations;
    };
    
    
    //---------Getters & Setters-----------
    
    
    public String getType () {
        return type;
    }
    
    public void setType (String type) {
        this.type = type;
    }
    
    public List<String> getLocations () {
        return locations;
    }
    
    public void setLocations (List<String> locations) {
        this.locations = locations;
    }
    
    public long getId () {
        return id;
    }
    
    public GamePlayer getGamePlayer () {
        return gamePlayer;
    }
    
    public void setGamePlayer (GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }
    
    
    
    //-------------[DTO]--------------
    
    public Map<String, Object> shipDTO(){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("type", this.getType());
        dto.put("locations", this.getLocations());
        return dto;
    }
    
}


