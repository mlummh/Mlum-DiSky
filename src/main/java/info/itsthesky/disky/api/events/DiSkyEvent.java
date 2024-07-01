package info.itsthesky.disky.api.events;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.*;
import ch.njol.skript.log.SkriptLogger;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Made by Blitz, minor edit by Sky for DiSky
 */
public abstract class DiSkyEvent<D extends net.dv8tion.jda.api.events.Event> extends SkriptEvent {

    /**
     * The ending appended to patterns if no custom ending is specified
     */
    public static final String APPENDED_ENDING = "[seen by %-string%]";
    private final Map<Class<?>, Object> valueMap = new HashMap<>();
    private String stringRepresentation;
    private EventListener<D> listener;
    private String bot;
    private Class<? extends Event> bukkitClass;
    private Class<D> jdaClass;
    private String originalName;
    private Class<? extends Event>[] originalEvents;
    private Constructor<?> constructor;

    /**
     * @param name     The name of the event used for ScriptLoader#setCurrentEvents
     * @param type     The class holding the event
     * @param clazz    The class holding the Bukkit event
     * @param patterns The patterns for the event
     */
    public static SkriptEventInfo<?> register(String name, Class type, Class clazz, String... patterns) {
        return register(name, APPENDED_ENDING, type, clazz, patterns);
    }

    /**
     * @param name     The name of the event used for ScriptLoader#setCurrentEvents
     * @param ending   The ending applied for checking the bot (which can be grabbed via BaseEvent.APPENDED_ENDING)
     * @param type     The class holding the event
     * @param clazz    The class holding the Bukkit event
     * @param patterns The patterns for the event
     */
    @SuppressWarnings("unchecked")
    public static <T extends SimpleDiSkyEvent<?>> SkriptEventInfo<?> register(String name, String ending, Class type, Class<T> clazz, String... patterns) {
        for (int i = 0; i < patterns.length; i++) {
            patterns[i] += " " + ending;
        }
        return Skript.registerEvent(name, type, clazz, patterns);
    }

    /**
     * If you wanna check by yourself the event through a predicate.
     * <br> Return true by default and execute the event given either it complete specifics conditions or not.
     */
    protected Predicate<D> checker() {
        return e -> true;
    }

    protected Predicate<GuildAuditLogEntryCreateEvent> logChecker() {
        return e -> true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Literal<?> @NotNull [] exprs, int matchedPattern, @NotNull SkriptParser.ParseResult parser) {
        bot = (String) (exprs[0] == null ? null : exprs[0].getSingle());

        bukkitClass = (Class<? extends Event>) Arrays.stream(this.getClass().getDeclaredClasses())
                .filter(innerClass -> innerClass.getSuperclass() == SimpleDiSkyEvent.class)
                .findFirst()
                .orElse(null);

        if (bukkitClass == null) {
            throw new RuntimeException(this.getClass().getCanonicalName() + " doesn't have an inner SimpleDiSkyEvent " +
                    "class to be instantiated. Report this at https://github.com/SkyCraft78/DiSky3/issues!");
        }

        try {
            jdaClass = (Class<D>) ((ParameterizedType) bukkitClass.getGenericSuperclass()).getActualTypeArguments()[0];
        } catch (ClassCastException e) {
            throw new RuntimeException(this.getClass().getCanonicalName() + "'s inner class doesn't use the same JDA" +
                    " event as it's parent class in it's SimpleDiSkyEvent. Report this at https://github.com/SkyCraft78/DiSky3/issues!");
        }

        try {
            constructor = bukkitClass.getDeclaredConstructor(this.getClass());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        stringRepresentation = ScriptLoader.replaceOptions(SkriptLogger.getNode().getKey()) + ":";
        originalName = ScriptLoader.getCurrentEventName();
        originalEvents = ScriptLoader.getCurrentEvents();

        String name = null;
        for (SkriptEventInfo<?> event : Skript.getEvents()) {
            if (bukkitClass.equals(event.getElementClass())) {
                name = event.getName();
            }
        }

        ScriptLoader.setCurrentEvent(name == null ? "DiSky event" : name, bukkitClass);
        return true;
    }

    @Override
    public boolean postLoad() {
        listener = new EventListener<>(jdaClass, (JDAEvent, auditLogEntryCreateEvent) -> {
            if (check(JDAEvent)) {

                /* !? */
                SimpleDiSkyEvent<D> eventWorkaround = null;
                SimpleDiSkyEvent<D> event;
                try {
                    eventWorkaround = (SimpleDiSkyEvent<D>) constructor.newInstance(DiSkyEvent.this);
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                event = eventWorkaround;

                event.setJDAEvent(JDAEvent);

                if (check(event)) {
                    event.setLogEvent(auditLogEntryCreateEvent);

                    SkriptUtils.sync(() -> {
                        if (getTrigger() != null) {
                            getTrigger().execute(event);
                        }
                    });
                }

            }
        }, checker(), logChecker(), getLogType());
        EventListener.addListener(listener);
        return super.postLoad();
    }

    @Override
    public void unload() {
        if (listener != null) {
            listener.enabled = false;
            EventListener.removeListener(listener);
        }

        listener = null;
        trigger = null;
    }

    @Override
    public @NotNull String toString(@NotNull Event e, boolean debug) {
        return stringRepresentation;
    }

    public String getBot() {
        return bot;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    /**
     * Used to check whether the event is valid for the trigger to run.
     *
     * @param event The JDA event to be checked
     */
    public boolean check(D event) {
        return bot == null || bot.equalsIgnoreCase(DiSky.getManager().getJDAName(event.getJDA()));
    }

    @Override
    public boolean check(@NotNull Event event) {
        return true;
    }

    public @Nullable ActionType getLogType() {
        return null;
    }

    public Class<? extends Event> getBukkitClass() {
        return bukkitClass;
    }

    public Class<D> getJDAClass() {
        return jdaClass;
    }


}