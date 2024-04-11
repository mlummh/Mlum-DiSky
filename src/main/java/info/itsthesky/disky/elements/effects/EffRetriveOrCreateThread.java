package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class EffRetriveOrCreateThread extends Effect {

    static {
        Skript.registerEffect(EffRetriveOrCreateThread.class,
                "retrieve thread named %string% from %object% and store it in %object%");
    }

    private Expression<String> threadName;
    private Expression<Object> channelString;
    private Variable<Object> varStore;

    /**
     * Called just after the constructor.
     *
     * @param expressions    all %expr%s included in the matching pattern in the order they appear in the pattern. If an optional value was left out, it will still be included in this list
     *                       holding the default value of the desired type, which usually depends on the event.
     * @param matchedPattern The index of the pattern which matched
     * @param isDelayed      Whether this expression is used after a delay or not (i.e. if the event has already passed when this expression will be called)
     * @param parseResult    Additional information about the match.
     * @return Whether this expression was initialised successfully. An error should be printed prior to returning false to specify the cause.
     * @see ParserInstance#isCurrentEvent(Class...)
     */
    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        threadName = (Expression<String>) expressions[0];
        channelString = (Expression<Object>) expressions[1];
        varStore = (Variable<Object>) expressions[2];
        return true;
    }

    /**
     * Executes this effect.
     *
     * @param event The event with which this effect will be executed
     */
    @Override
    protected void execute(Event event) {
        final Bot bot = getBot();
        if (bot == null) {
            DiSky.getErrorHandler().exception(event, new RuntimeException("No bot is currently loaded on the server. You cannot use any DiSky syntaxes without least one loaded."));
            return;
        }

        String threadName = Objects.requireNonNull(this.threadName.getSingle(event));

        Object channelExpr = channelString.getSingle(event);
        String channelID;
        if (channelExpr == null) {
            DiSky.getErrorHandler().exception(event, new RuntimeException("Channel was null"));
            return;
        } else if (channelExpr instanceof String) {
            channelID = (String) channelExpr;
        } else if (channelExpr.getClass().isAssignableFrom(TextChannel.class)) {
            channelID = ((TextChannel) channelExpr).getId();
        } else {
            DiSky.getErrorHandler().exception(event, new RuntimeException("Channel expression was not a Text Channel or Discord ID"));
            return;
        }

        TextChannel channel = bot.getInstance().getTextChannelById(channelID);

        if (channel == null) {
            DiSky.getErrorHandler().exception(event, new RuntimeException("No channel with id " + channelString.getSingle(event)));
            return;
        }

        ThreadChannel thread = getAndOpenThread(threadName, channel);

        if (thread != null) {
            saveThread(event, thread);
            return;
        }

        thread = channel.createThreadChannel(threadName).complete();
        saveThread(event, thread);
    }

    public @Nullable Bot getBot() {
        return DiSky.getManager().findAny();
    }

    private void saveThread(Event event, ThreadChannel thread) {
        varStore.change(event, new Object[] {thread}, Changer.ChangeMode.SET);
    }

    private @Nullable ThreadChannel getAndOpenThread(@NotNull String threadName, @NotNull TextChannel channel) {
        ThreadChannel thread = getThread(threadName, channel);
        if (thread == null) {
            return null;
        }
        thread.getManager().setLocked(false).setArchived(false).queue();
        return thread;
    }
    private @Nullable ThreadChannel getThread(@NotNull String threadName, @NotNull TextChannel channel) {
        for (ThreadChannel t: channel.getThreadChannels()) {
            if (t.getName().equals(threadName)) {
                return t;
            } else if (t.getId().equals(threadName)) {
                return t;
            }
        }
        for (ThreadChannel t: channel.retrieveArchivedPublicThreadChannels()) {
            if (t.getName().equals(threadName)) {
                return t;
            } else if (t.getId().equals(threadName)) {
                return t;
            }
        }
        for (ThreadChannel t: channel.retrieveArchivedPrivateThreadChannels()) {
            if (t.getName().equals(threadName)) {
                return t;
            } else if (t.getId().equals(threadName)) {
                return t;
            }
        }
        return null;
    }

    /**
     * @param event The event to get information from. This is always null if debug == false.
     * @param debug If true this should print more information, if false this should print what is shown to the end user
     * @return String representation of this object
     */
    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return null;
    }
}
