package info.itsthesky.disky.elements.properties.attachments;

import info.itsthesky.disky.api.skript.PropertyCondition;
import net.dv8tion.jda.api.entities.Message;

public class CondIsAudio extends PropertyCondition<Message.Attachment> {

	static {
		register(
				CondIsAudio.class,
				PropertyType.BE,
				"[an] audio",
				"attachment"
		);
	}

	@Override
	public boolean check(Message.Attachment attachment) {
		return attachment.getDuration() > 0;
	}

	@Override
	protected String getPropertyName() {
		return "attachment is audio";
	}

}