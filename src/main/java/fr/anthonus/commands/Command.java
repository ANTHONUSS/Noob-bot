package fr.anthonus.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public abstract class Command {
    protected SlashCommandInteractionEvent currentEvent;

    protected Command(SlashCommandInteractionEvent event) {
        currentEvent = event;
    }

    public abstract void run();
}
