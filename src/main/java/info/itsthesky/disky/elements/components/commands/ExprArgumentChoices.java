package info.itsthesky.disky.elements.components.commands;

import ch.njol.skript.classes.Changer;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class ExprArgumentChoices extends MultiplyPropertyExpression<OptionData, Command.Choice> {

	static {
		register(
				ExprArgumentChoices.class,
				Command.Choice.class,
				"[option] choices",
				"slashoption"
		);
	}

	@Override
	public void change(@NotNull Event e, Object @NotNull [] delta, Changer.@NotNull ChangeMode mode) {
		final Command.Choice choice = (Command.Choice) delta[0];
		final OptionData optionData = EasyElement.parseSingle(getExpr(), e, null);
		if (EasyElement.anyNull(choice, optionData))
			return;
		try {
			optionData.addChoices(choice);
		} catch (Exception ex) {
			DiSky.getErrorHandler().exception(e, ex);
		}
	}

	@Override
	public Class<?> @NotNull [] acceptChange(Changer.@NotNull ChangeMode mode) {
		if (mode == Changer.ChangeMode.ADD)
			return new Class[] {Command.Choice.class};
		return new Class[0];
	}

	@Override
	public @NotNull Class<? extends Command.Choice> getReturnType() {
		return Command.Choice.class;
	}

	@Override
	protected String getPropertyName() {
		return "choices";
	}

	@Override
	protected Command.Choice[] convert(OptionData option) {
		return option.getChoices().toArray(new Command.Choice[0]);
	}
}
