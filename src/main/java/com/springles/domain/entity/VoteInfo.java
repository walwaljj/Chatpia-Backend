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
    private Map<Long, GameRole> votersMap;

    public static VoteInfo builder(int phaseCount, Map<Long, GameRole> votersMap) {
        return new VoteInfoBuilder().phaseCount(phaseCount).votersMap(votersMap).build();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("VoteInfo [phaseCount=" + phaseCount + ", voters={ ");
        votersMap.keySet().forEach(key -> {
            sb.append(key + "|");
        });
        sb.deleteCharAt(sb.length()-1);
        sb.append(" } ]");
        return sb.toString();
    }
}
