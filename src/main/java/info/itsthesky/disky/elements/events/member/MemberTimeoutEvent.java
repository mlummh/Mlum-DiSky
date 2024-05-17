package info.itsthesky.disky.elements.events.member;

import ch.njol.skript.util.Date;
import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateTimeOutEvent;

import static info.itsthesky.disky.core.SkriptUtils.convertDateTime;

public class MemberTimeoutEvent extends DiSkyEvent<GuildMemberUpdateTimeOutEvent> {

    static {
        register("Member Timeout Event", MemberTimeoutEvent.class, BukkitMemberTimeoutEvent.class,
                "[discord] member time[ ]out[ed]");


        SkriptUtils.registerBotValue(BukkitMemberTimeoutEvent.class);
        SkriptUtils.registerAuthorValue(BukkitMemberTimeoutEvent.class,
                e -> e.getJDAEvent().getGuild());

        SkriptUtils.registerValue(BukkitMemberTimeoutEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild());
        SkriptUtils.registerValue(BukkitMemberTimeoutEvent.class, Member.class,
                event -> event.getJDAEvent().getMember(), 0);
        SkriptUtils.registerValue(BukkitMemberTimeoutEvent.class, User.class,
                event -> event.getJDAEvent().getUser(), 0);

        SkriptUtils.registerValue(BukkitMemberTimeoutEvent.class, Date.class,
                event -> convertDateTime(event.getJDAEvent().getNewTimeOutEnd()), 1);
        SkriptUtils.registerValue(BukkitMemberTimeoutEvent.class, Date.class,
                event -> convertDateTime(event.getJDAEvent().getOldTimeOutEnd()), -1);
        SkriptUtils.registerValue(BukkitMemberTimeoutEvent.class, Date.class,
                event -> convertDateTime(event.getJDAEvent().getNewTimeOutEnd()), 0);
    }

    public static class BukkitMemberTimeoutEvent extends SimpleDiSkyEvent<GuildMemberUpdateTimeOutEvent> {
        public BukkitMemberTimeoutEvent(MemberTimeoutEvent event) {
        }
    }
}