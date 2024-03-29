package self.discordbottest;

import java.nio.ByteBuffer;

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;

import discord4j.voice.AudioProvider;

public final class LavaPlayerAudioProvider extends AudioProvider{

    private final AudioPlayer player;
    private final MutableAudioFrame frame = new MutableAudioFrame();

    public LavaPlayerAudioProvider(final AudioPlayer player){
        super(
            ByteBuffer.allocate(
                StandardAudioDataFormats.DISCORD_OPUS.maximumChunkSize()
            )
        );

        frame.setBuffer(getBuffer());
        this.player = player;
        
    }

    @Override
    public boolean provide() {
        final boolean didProvide= player.provide(frame);

        if(didProvide){
            getBuffer().flip();
        }
        else{
            throw new UnsupportedOperationException("Unimplemented method 'provide'");
        }
        return didProvide;
    }
    
}
