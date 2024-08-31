package info.itsthesky.disky.elements.events.interactions;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.api.events.specific.InteractionEvent;
import info.itsthesky.disky.core.SkriptUtils;
import info.itsthesky.disky.managers.ConfigManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class ModalSendEvent extends DiSkyEvent<ModalInteractionEvent> {

	static {
		register("Modal Receive", ModalSendEvent.class, BukkitModalSendEvent.class,
				"modal (click[ed]|receive[d])")
				.description("Fired when a modal has been sent to the bot from any user.",
						"Use 'event-string' to get the modal id. Don't forget to either reply or defer the interaction.",
						"Modal can NOT be shown in this interaction.");

		SkriptUtils.registerBotValue(BukkitModalSendEvent.class);

		SkriptUtils.registerValue(BukkitModalSendEvent.class, Guild.class,
				event -> event.getJDAEvent().getGuild());
		SkriptUtils.registerValue(BukkitModalSendEvent.class, Member.class,
				event -> event.getJDAEvent().getMember());
		SkriptUtils.registerValue(BukkitModalSendEvent.class, User.class,
				event -> event.getJDAEvent().getUser());
		SkriptUtils.registerValue(BukkitModalSendEvent.class, String.class,
				event -> event.getJDAEvent().getModalId());
		SkriptUtils.registerValue(BukkitModalSendEvent.class, MessageChannel.class,
				event -> event.getJDAEvent().getMessageChannel());

		SkriptUtils.registerValue(BukkitModalSendEvent.class, GuildChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getGuildChannel() : null);
		SkriptUtils.registerValue(BukkitModalSendEvent.class, TextChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? ((MessageChannelUnion) event.getJDAEvent().getChannel()).asTextChannel() : null);
		SkriptUtils.registerValue(BukkitModalSendEvent.class, NewsChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? ((MessageChannelUnion) event.getJDAEvent().getChannel()).asNewsChannel() : null);
		SkriptUtils.registerValue(BukkitModalSendEvent.class, ThreadChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? ((MessageChannelUnion) event.getJDAEvent().getChannel()).asThreadChannel() : null);

		SkriptUtils.registerValue(BukkitModalSendEvent.class, PrivateChannel.class,
				event -> !event.getJDAEvent().isFromGuild() ? ((MessageChannelUnion) event.getJDAEvent().getChannel()).asPrivateChannel() : null);
	}

	@Override
	public boolean check(@NotNull Event event) {
		if (!((BukkitModalSendEvent) event).getInteractionEvent().isFromGuild()) return false;
		if (!((BukkitModalSendEvent) event).getInteractionEvent().getGuild().getId().equals(ConfigManager.get("GuildID", null))) {
			return false;
		}
		return super.check(event);
	}

	public static class BukkitModalSendEvent extends SimpleDiSkyEvent<ModalInteractionEvent> implements InteractionEvent {
		public BukkitModalSendEvent(ModalSendEvent event) {}

		@Override
		public GenericInteractionCreateEvent getInteractionEvent() {
			return getJDAEvent();
		}

	}
}