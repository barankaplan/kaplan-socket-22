package com.kaplan.client.runner;


import com.kaplan.client.command.CommandPromptCommand;
import org.csystem.util.commandprompt.CommandPrompt;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
@PropertySource("file:client/src/main/resources/application-client.properties")

public class ConsoleClientRunner implements ApplicationRunner {
    private final CommandPrompt m_commandPrompt;
    private final ExecutorService m_executorService;
    private final CommandPromptCommand m_commandPromptCommand;

    private void runCommandPrompt()
    {
        m_commandPrompt.register(m_commandPromptCommand).setPrompt("kaplan client").run();
    }

    public ConsoleClientRunner(CommandPrompt commandPrompt, ExecutorService executorService, CommandPromptCommand commandPromptCommand)
    {
        m_commandPrompt = commandPrompt;
        m_executorService = executorService;
        m_commandPromptCommand = commandPromptCommand;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception
    {
        m_executorService.execute(this::runCommandPrompt);
    }
}
