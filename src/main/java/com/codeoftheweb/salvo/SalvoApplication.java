package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.entities.*;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import com.codeoftheweb.salvo.repositories.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}
	
	@Autowired
	PasswordEncoder passwordEncoder;

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gpRepository, ScoreRepository scoreRepository) {
		return (args) -> {

			//PLAYERS
			Player richie = playerRepository.save(new Player("Richie.tozier", "Richard", "Tozier", passwordEncoder.encode("richie123")));
			Player bevs = playerRepository.save(new Player("BevsMarsh", "Beverly", "Marsh", passwordEncoder.encode("bevs123")));
			Player billy = playerRepository.save(new Player("Billy.D", "Bill", "Denbrough", passwordEncoder.encode("billy123")));
			Player georgie = playerRepository.save(new Player("GeorgieD", "George", "Denbrough", passwordEncoder.encode("georgie123")));
			Player leo = playerRepository.save(new Player("vagoleonardo@gmail.com", "Leo", "Vago", passwordEncoder.encode("oxhkzoge100"), true));

			//GAMES
			Game game1 = gameRepository.save(new Game());
			Game game2 = gameRepository.save(new Game(LocalDateTime.now().plusHours(2)));
			Game game3 = gameRepository.save(new Game(LocalDateTime.now().plusHours(3)));
			Game game4 = gameRepository.save(new Game(LocalDateTime.now().plusHours(1)));
			Game game5 = gameRepository.save(new Game(LocalDateTime.now().plusHours(1)));
			
			
			//GAMEPLAYERS
			GamePlayer gp1 =gpRepository.save(new GamePlayer (richie, game1));
			GamePlayer gp2 =gpRepository.save(new GamePlayer (georgie, game1));
			GamePlayer gp3 =gpRepository.save(new GamePlayer (bevs, game2));
			GamePlayer gp4 =gpRepository.save(new GamePlayer (billy, game2));
			GamePlayer gp5 =gpRepository.save(new GamePlayer (richie, game3));
			GamePlayer gp6 =gpRepository.save(new GamePlayer (georgie, game3));
			GamePlayer gp7 =gpRepository.save(new GamePlayer (richie, game4));
			GamePlayer gp8 =gpRepository.save(new GamePlayer (bevs, game4));
			GamePlayer gp9 =gpRepository.save(new GamePlayer (bevs, game5));
			GamePlayer gp10 =gpRepository.save(new GamePlayer (billy, game5));
			
			
			//SHIPS
			gp1.addShip (new Ship ("destroyer", Arrays.asList ("B2", "B3", "B4")));
			gp1.addShip (new Ship ("submarine", Arrays.asList ("A1", "B1", "C1", "D1")));
			gp1.addShip (new Ship ("patrol_boat", Arrays.asList ("J5", "J6")));
			
			gp2.addShip (new Ship ("destroyer", Arrays.asList ("H1", "I1", "J1")));
			gp2.addShip (new Ship ("submarine", Arrays.asList ("D4", "D5", "D6", "D7")));
			gp2.addShip (new Ship ("patrol_boat", Arrays.asList ("H5", "H6")));
			
			gp3.addShip (new Ship ("destroyer", Arrays.asList ("B2", "B3", "B4")));
			gp3.addShip (new Ship ("submarine", Arrays.asList ("A1", "B1", "C1", "D1")));
			gp3.addShip (new Ship ("patrol_boat", Arrays.asList ("J5", "J6")));
			
			gp4.addShip (new Ship ("destroyer", Arrays.asList ("H1", "I1", "J1")));
			gp4.addShip (new Ship ("submarine", Arrays.asList ("D4", "D5", "D6", "D7")));
			gp4.addShip (new Ship ("patrol_boat", Arrays.asList ("H5", "H6")));
			
			
			//SALVOES
			gp1.addSalvo(new Salvo (1, Arrays.asList ("H1", "H2")));
			gp1.addSalvo(new Salvo (2, Arrays.asList ("F6", "J7")));
			gp1.addSalvo(new Salvo (3, Arrays.asList ("E8", "E6")));
			gp1.addSalvo(new Salvo (4, Arrays.asList ("H5", "H6")));
			
			gp2.addSalvo(new Salvo (1, Arrays.asList ("F1", "F2")));
			gp2.addSalvo(new Salvo (2, Arrays.asList ("E4", "A2")));
			gp2.addSalvo(new Salvo (3, Arrays.asList ("A8", "B9")));
			gp2.addSalvo(new Salvo (4, Arrays.asList ("B4", "J6")));
			
			gp3.addSalvo(new Salvo (1, Arrays.asList ("H1", "H2")));
			gp3.addSalvo(new Salvo (2, Arrays.asList ("F6", "J7")));
			gp3.addSalvo(new Salvo (3, Arrays.asList ("E8", "E6")));
			gp3.addSalvo(new Salvo (4, Arrays.asList ("H5", "H6")));
			
			gp4.addSalvo(new Salvo (1, Arrays.asList ("F1", "F2")));
			gp4.addSalvo(new Salvo (2, Arrays.asList ("E4", "A2")));
			gp4.addSalvo(new Salvo (3, Arrays.asList ("A8", "B9")));
			gp4.addSalvo(new Salvo (4, Arrays.asList ("B4", "J6")));
			
			//SCORES
			Score score1 = new Score (10, game1, richie);
			Score score2 = new Score (0, game1, georgie);
			Score score3 = new Score (5, game2, bevs);
			Score score4 = new Score (5, game2, billy);
			Score score5 = new Score (10, game3, richie);
			Score score6 = new Score (0, game3, georgie);
			Score score7 = new Score (5, game4, richie);
			Score score8 = new Score (5, game4, bevs);
			Score score9 = new Score (10, game5, bevs);
			Score score10 = new Score (0, game5, billy);
			
			
			scoreRepository.save(score1);
			scoreRepository.save(score2);
			scoreRepository.save(score3);
			scoreRepository.save(score4);
			scoreRepository.save(score5);
			scoreRepository.save(score6);
			scoreRepository.save(score7);
			scoreRepository.save(score8);
			scoreRepository.save(score9);
			scoreRepository.save(score10);
			
			
			gpRepository.save(gp1);
			gpRepository.save(gp2);
			gpRepository.save(gp3);
			gpRepository.save(gp4);
			gpRepository.save(gp5);
			gpRepository.save(gp6);
			gpRepository.save(gp7);
			gpRepository.save(gp8);
			gpRepository.save(gp9);
			gpRepository.save(gp10);
			
		};
	};



}

