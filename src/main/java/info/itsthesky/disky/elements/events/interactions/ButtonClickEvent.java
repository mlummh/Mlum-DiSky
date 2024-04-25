package info.itsthesky.disky.elements.events.interactions;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.api.events.specific.ComponentInteractionEvent;
import info.itsthesky.disky.api.events.specific.ModalEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class ButtonClickEvent extends DiSkyEvent<ButtonInteractionEvent> {

	static {
		register("Button Click", ButtonClickEvent.class, BukkitButtonClickEvent.class,
				"[:global] button click[ed]")
				.description("Fired when any button sent by the button is clicked.",
						"Use 'event-button' to get the button id. Don't forget to either reply or defer the interaction.",
						"Modal can be shown in this interaction.");

		SkriptUtils.registerBotValue(BukkitButtonClickEvent.class);

		SkriptUtils.registerValue(BukkitButtonClickEvent.class, Message.class,
				event -> event.getJDAEvent().getMessage());
		SkriptUtils.registerValue(BukkitButtonClickEvent.class, Guild.class,
				event -> event.getJDAEvent().getGuild());
		SkriptUtils.registerValue(BukkitButtonClickEvent.class, Member.class,
				event -> event.getJDAEvent().getMember());
		SkriptUtils.registerValue(BukkitButtonClickEvent.class, User.class,
				event -> event.getJDAEvent().getUser());
		SkriptUtils.registerValue(BukkitButtonClickEvent.class, String.class,
				event -> event.getJDAEvent().getButton().getId());
		SkriptUtils.registerValue(BukkitButtonClickEvent.class, Button.class,
				event -> event.getJDAEvent().getButton());
		SkriptUtils.registerValue(BukkitButtonClickEvent.class, MessageChannel.class,
				event -> event.getJDAEvent().getChannel());

		SkriptUtils.registerValue(BukkitButtonClickEvent.class, GuildChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getGuildChannel() : null);
		SkriptUtils.registerValue(BukkitButtonClickEvent.class, TextChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asTextChannel() : null);
		SkriptUtils.registerValue(BukkitButtonClickEvent.class, NewsChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asNewsChannel() : null);
		SkriptUtils.registerValue(BukkitButtonClickEvent.class, ThreadChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asThreadChannel() : null);

		SkriptUtils.registerValue(BukkitButtonClickEvent.class, PrivateChannel.class,
				event -> !event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asPrivateChannel() : null);
	}

	private boolean globalEvent;

	@Override
	public boolean init(Literal<?> @NotNull [] exprs, int matchedPattern, SkriptParser.@NotNull ParseResult parser) {
		globalEvent = parser.hasTag("global");
		return super.init(exprs, matchedPattern, parser);
	}

	@Override
	public boolean check(@NotNull Event event) {
		if (!super.check(event)) return false;
		if (!((BukkitButtonClickEvent) event).isFromGuild()) return globalEvent;
		if (!((BukkitButtonClickEvent) event).getJDAEvent().getGuild().getId().equals(DiSky.getConfiguration().getString("GuildID"))) {
			return globalEvent;
		}
		return !globalEvent;
	}

	public static class BukkitButtonClickEvent extends SimpleDiSkyEvent<ButtonInteractionEvent> implements ModalEvent, ComponentInteractionEvent {
		public BukkitButtonClickEvent(ButtonClickEvent event) {}

		@Override
		public GenericInteractionCreateEvent getInteractionEvent() {
			return getJDAEvent();
		}

		@Override
		public ModalCallbackAction replyModal(@NotNull Modal modal) {
			return getJDAEvent().replyModal(modal);
		}
	}
}