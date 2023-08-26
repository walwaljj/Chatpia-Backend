package com.springles.game;

import com.springles.domain.constants.GameRole;
import com.springles.domain.constants.GameRoleNum;
import com.springles.domain.entity.Player;
import com.springles.repository.PlayerRedisRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Transactional
@RequiredArgsConstructor
@Service
@Slf4j
public class RoleManager {

    private final PlayerRedisRepository playerRedisRepository;

    public void assignRole(List<Player> players) {
        int playerCount = players.size();
        List<GameRole> gameRoleList = shuffleRole(GameRoleNum.getRoleNum(playerCount));
        for (int i = 0; i < playerCount; i++) players.get(i).updateRole(gameRoleList.get(i));
        playerRedisRepository.saveAll(players);
    }

    public List<GameRole> shuffleRole(GameRoleNum roleNum) {
        List<GameRole> roles = new ArrayList<>();
        for (int i = 0; i < roleNum.getCivilian(); i++) roles.add(GameRole.CIVILIAN);
        for (int i = 0; i < roleNum.getPolice(); i++) roles.add(GameRole.POLICE);
        for (int i = 0; i < roleNum.getDoctor(); i++) roles.add(GameRole.DOCTOR);
        for (int i = 0; i < roleNum.getMafia(); i++) roles.add(GameRole.MAFIA);
        Collections.shuffle(roles);
        log.info("직업 리스트 사이즈: "+roles.size());
        return roles;
    }
}
