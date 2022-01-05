package net.tomatentum.musicbot.music;

import net.tomatentum.musicbot.TomatenMusic;
import net.tomatentum.musicbot.music.messagemanagers.PanelManager;

import java.util.Timer;
import java.util.TimerTask;

public class UpdateTimer {

    private Timer timer;

    UpdateTimer() {
        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
               for (GuildMusicManager musicManager : TomatenMusic.getInstance().getAudioManager().getMusicManagers()) {
                   musicManager.quitIfEmpty();

                   PanelManager panelManager = musicManager.getPanelManager();
                   if (panelManager.getMessage() == null)
                       return;

                   if (musicManager.getPlayer().getPlayingTrack() != null) {

                           if (musicManager.getPlayer().isPaused()) {
                               panelManager.setPaused();
                               panelManager.update();
                           } else if (musicManager.getPlayer().getPlayingTrack() != null) {
                               panelManager.setPlaying(musicManager.getPlayer().getPlayingTrack());
                               panelManager.update();
                           } else {
                               panelManager.setIdle();
                               panelManager.update();
                           }
                   }
               }

            }
        }, 10000L, 10000L);
    }
}
