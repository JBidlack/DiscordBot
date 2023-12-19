package self.discordbottest;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public final class TrackScheduler implements AudioLoadResultHandler {

    private final AudioPlayer player;

    public TrackScheduler(final AudioPlayer player) {
        this.player = player;
    }

    @Override
    public void loadFailed(FriendlyException arg0) {
        
    }

    @Override
    public void noMatches() {
        
        
    }

    @Override
    public void playlistLoaded(AudioPlaylist arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        player.playTrack(track);
        
    }
    
}
