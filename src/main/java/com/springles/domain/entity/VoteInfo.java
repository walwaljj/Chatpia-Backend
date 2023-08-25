package com.springles.domain.entity;

import com.springles.domain.constants.GameRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@Builder
public class VoteInfo {
    private int phaseCount;
    private Map<String, GameRole> votersMap;

    public static VoteInfo builder(int phaseCount, Map<String, GameRole> votersMap) {
        return new VoteInfoBuilder().phaseCount(phaseCount).votersMap(votersMap).build();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("VoteInfo [phaseCount=" + phaseCount + ", voters={ ");
        votersMap.keySet().forEach(key -> {
            sb.append(key.substring(0, 4) + "|");
        });
        sb.deleteCharAt(sb.length()-1);
        sb.append(" } ]");
        return sb.toString();
    }
}
