package com.codeoftheweb.salvo.entities;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

import com.codeoftheweb.salvo.entities.GamePlayer;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")

    //--------------Attributes--------------

    private long id;
    private String userName;
    private String firstName;
    private String lastName;
    private String password;
    private boolean admin;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    private Set<GamePlayer> gamePlayers = new HashSet<>();
    
    @OneToMany(mappedBy="player", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    private Set<Score> scores = new HashSet<>();

    //--------------Constructors--------------
    public Player() {}

    public Player(String userName, String firstName, String lastName, String password) {
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.admin = false;
    }
    
    public Player(String userName, String firstName, String lastName, String password, boolean admin) {
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.admin = admin;
    }

    //--------------Getters & Setters --------------


    public long getId() {
        return id;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getPassword () {
        return password;
    }
    
    public void setPassword (String password) {
        this.password = password;
    }
    
    public Score getScoreByGame (Game game){
        return this.scores.stream().filter(score -> score.getGame().getId() == game.getId()).findFirst().orElse(null);
    }
    
    public boolean isAdmin () {
        return admin;
    }
    
    //-------------[DTO]--------------
public Map<String, Object> playerDTO(){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("playerid", this.getId ());
        dto.put("username", this.getUserName());
        dto.put("firstname", this.getFirstName());
        dto.put("lastname", this.getLastName());
        return dto;
};
    
    public Map<String, Object> playerDTO2(){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("playerid", this.getId ());
        dto.put("username", this.getUserName());
        dto.put("firstname", this.getFirstName());
        dto.put("lastname", this.getLastName());
        dto.put("gamesids", this.getGamePlayers().stream().map(gp -> gp.getGame().getId()).collect(Collectors.toList()));
        return dto;
    };

}
