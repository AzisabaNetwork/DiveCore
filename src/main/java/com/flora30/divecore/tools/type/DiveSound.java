package com.flora30.divecore.tools.type;

import org.bukkit.Sound;

public enum DiveSound{
    LootOpen{
        @Override
        public Sound getSound() {
            return Sound.BLOCK_CHEST_OPEN;
        }
    },
    LootEmpty{
        @Override
        public Sound getSound() {
            return Sound.BLOCK_CHEST_LOCKED;
        }
    },
    MissionStart{
        @Override
        public Sound getSound() {
            return Sound.ITEM_LODESTONE_COMPASS_LOCK;
        }
    },
    MissionComplete{
        @Override
        public Sound getSound() {
            return Sound.BLOCK_NOTE_BLOCK_BELL;
        }
    },
    GuiClick{
        @Override
        public Sound getSound() {
                return Sound.UI_BUTTON_CLICK;
            }
    };

    public abstract Sound getSound();
}
