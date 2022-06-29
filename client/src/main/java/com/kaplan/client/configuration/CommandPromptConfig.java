package com.kaplan.client.configuration;

import org.csystem.util.commandprompt.CommandPrompt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandPromptConfig {
    @Bean
    public CommandPrompt getCommandPrompt()
    {
        return new CommandPrompt();
    }
}
