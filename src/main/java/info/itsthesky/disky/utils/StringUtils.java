package info.itsthesky.disky.utils;

import ch.njol.skript.util.SkriptColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class StringUtils {

    private final static String TEMP_REPLACE = "¬UNDERSCORE¬";
    private final static String BACKTICK = Pattern.quote("`");
    private final static String CODE_BACKTICK = Pattern.quote("```");

    public static String escapeString(String s) {
        if (s == null) {
            return null;
        }

        String[] codeBlocks = s.split(CODE_BACKTICK, -1);
//        DiSky.getInstance().getLogger().info(Arrays.toString(codeBlocks));
        int i = 0;
        for (String block: codeBlocks) {
            // Ignore the inside of code blocks
            if (++i % 2 == 0) {
//                DiSky.getInstance().getLogger().info("Inside CodeBlock " + block + " (i:" + i + ")");
                continue;
            }
//            DiSky.getInstance().getLogger().info("Outside CodeBlock " + block + " (i:" + i + ")");

            block = block.replaceAll("``","`");

            String[] inlineBlocks = block.split(BACKTICK, -1);
//            DiSky.getInstance().getLogger().info(Arrays.toString(inlineBlocks));
            int j = 0;
            for (String inlineBlock : inlineBlocks) {
                // Ignore the inside of inline code blocks
                if (++j % 2 == 0) {
//                    DiSky.getInstance().getLogger().info("Inside CodeLine " + inlineBlock + " (j:" + j + ")");
                    continue;
                }
//                DiSky.getInstance().getLogger().info("Outside CodeLine " + inlineBlock + " (j:" + j + ")");


                inlineBlock = inlineBlock.replaceAll("\\\\_", TEMP_REPLACE);
                inlineBlock = inlineBlock.replaceAll("_", "\\\\_");
                inlineBlock = inlineBlock.replaceAll(TEMP_REPLACE, "_");
//                DiSky.getInstance().getLogger().info("Replace: " + inlineBlock);

                inlineBlocks[j-1] = inlineBlock;
            }

            block = ch.njol.util.StringUtils.join(inlineBlocks,"`");
            codeBlocks[i-1] = block;
        }

        s = ch.njol.util.StringUtils.join(codeBlocks,"```");
        return SkriptColor.replaceColorChar(s);
    }

    public static EmbedBuilder escapeEmbed(EmbedBuilder embed) {
        MessageEmbed builtEmbed = embed.build();

        embed.setTitle(escapeString(builtEmbed.getTitle()));
        embed.setDescription(escapeString(builtEmbed.getDescription()));
        // if (builtEmbed.getFooter() != null) {
        //     embed.setFooter(escapeString(builtEmbed.getFooter().getText()));
        // }

        Class<MessageEmbed.Field> clazz = MessageEmbed.Field.class;
        Field name;
        Field value;
        try {
            name = clazz.getDeclaredField("name");
            value  = clazz.getDeclaredField("value");
            name.setAccessible(true);
            value.setAccessible(true);
        } catch (NoSuchFieldException e) {
            return embed;
        }
        for (MessageEmbed.Field field : embed.getFields()) {
            try {
                name.set(field, escapeString(field.getName()));
                value.set(field, escapeString(field.getValue()));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return embed;
    }

    public static MessageCreateBuilder escapeMessage(MessageCreateBuilder message) {
        message.setContent(escapeString(message.getContent()));

        List<MessageEmbed> embeds = new ArrayList<>();
        for (MessageEmbed embed: message.getEmbeds()) {
            EmbedBuilder newEmbed = new EmbedBuilder(embed);
            embeds.add(escapeEmbed(newEmbed).build());
        }

        message.setEmbeds(embeds);

        return message;
    }
}
