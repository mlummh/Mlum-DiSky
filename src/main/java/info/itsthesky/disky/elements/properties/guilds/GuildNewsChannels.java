package info.itsthesky.disky.elements.properties.guilds;

import ch.njol.skript.doc.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.NewsChannel;

@Name("Guild News Channels")
@Description("Gets all news channels of a guild.")
@Examples("all news channels of event-guild")
public class GuildNewsChannels extends MultipleGuildProperty<NewsChannel> {

    static {
        register(GuildNewsChannels.class,
                NewsChannel.class,
                "[all] news[( |-)]channels");
    }

    @Override
    public NewsChannel[] converting(Guild guild) {
        return guild.getNewsChannels().toArray(new NewsChannel[0]);
    }

}
