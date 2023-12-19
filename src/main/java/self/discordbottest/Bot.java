package self.discordbottest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.voice.AudioProvider;
import java.util.logging.Level;
import java.util.logging.Logger;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public class Bot {
    // Properties prop = new Properties();
    // String prof = "config.properties";
    InputStream input;
    private String TOKEN = "";
    private static final Logger log = Logger.getLogger("log" );
    private static final Map<String, Command> commands = new HashMap<>();
    static AudioProvider provider;

    public Bot() throws IOException{
        AudioPlayerTest();
        Connect();
    }

    private void Connect() throws IOException{
        
            Properties prop = new Properties();
            input = new FileInputStream("src\\main\\java\\self\\resources\\config.properties");
        if(input != null){
            prop.load(input);
            TOKEN = prop.getProperty("token");
            ClientConnect(TOKEN);
        }
        else {
            throw new FileNotFoundException("File " + input + " not found. Please double check.");
        }
    }

    private void ClientConnect(String TOKEN){
        GatewayDiscordClient client = DiscordClient.create(TOKEN)
                .gateway()
                .withEventDispatcher(dispatch -> 
                        dispatch.on(ReadyEvent.class))                                
                .login()
                .doOnError(e -> log.log(Level.ALL, "Failed to Authenticate", e))
                .doOnSuccess(result -> log.info("Connected on Discord"))
                .block();
        assert client != null;

        client.getEventDispatcher().on(MessageCreateEvent.class).flatMap(event -> 
            Mono.justOrEmpty(event.getMessage()
                                .getContent())
                .flatMap(content -> Flux.fromIterable(commands.entrySet())
                            .filter(entry -> content.startsWith('!' + entry.getKey()))
                            .flatMap(entry -> entry.getValue().execute(event))
                                .next()))
            .subscribe();

        client.onDisconnect().block();


    }

    public static void AudioPlayerTest(){
        final AudioPlayerManager player = new DefaultAudioPlayerManager();

        player.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        AudioSourceManagers.registerRemoteSources(player);

        final AudioPlayer audio = player.createPlayer();

        provider = new LavaPlayerAudioProvider(audio);
    }

    static {
        commands.put("ping", event -> 
            event.getMessage()
            .getChannel()
            .flatMap(channel -> 
                channel.createMessage("Pong!"))
                .then());
        commands.put("join", event -> Mono.justOrEmpty(event.getMember())
            .flatMap(Member::getVoiceState)
            .flatMap(VoiceState::getChannel)
            .flatMap(channel -> channel.join().withProvider(provider)).then());
    }

}
