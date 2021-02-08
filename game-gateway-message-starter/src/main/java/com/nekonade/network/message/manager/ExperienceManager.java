package com.nekonade.network.message.manager;

import com.nekonade.dao.db.entity.Experience;
import com.nekonade.network.message.event.function.ExperienceEvent;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;

public class ExperienceManager {

    private final PlayerManager playerManager;
    private final ApplicationContext context;
    @Getter
    @Setter
    private Experience experience;

    public ExperienceManager(PlayerManager playerManager) {
        this.context = playerManager.getContext();
        this.playerManager = playerManager;
        this.experience = playerManager.getPlayer().getExperience();
    }

    public void addExperience(int exp) {
        experience.setExp(experience.getExp() + exp);
        ExperienceEvent event = new ExperienceEvent(this, playerManager);
        context.publishEvent(event);
    }
}
