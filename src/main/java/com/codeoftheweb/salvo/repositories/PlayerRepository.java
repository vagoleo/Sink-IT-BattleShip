package com.codeoftheweb.salvo.repositories;

import com.codeoftheweb.salvo.entities.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.*;
@RepositoryRestResource

public interface PlayerRepository extends JpaRepository<Player, Long> {
    Player findByUserName(String userName);
    List<Player> findByLastName(String lastName);
}

