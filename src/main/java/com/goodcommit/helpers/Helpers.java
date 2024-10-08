package com.goodcommit.helpers;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Helpers {

    private static final Logger LOGGER = LoggerFactory.getLogger(Helpers.class);

    private static final String LOGO =
    " ██████╗  ██████╗  ██████╗ ██████╗      ██████╗ ██████╗ ███╗   ███╗███╗   ███╗██╗████████╗     ██████╗██╗     ██╗\n" +
    "██╔════╝ ██╔═══██╗██╔═══██╗██╔══██╗    ██╔════╝██╔═══██╗████╗ ████║████╗ ████║██║╚══██╔══╝    ██╔════╝██║     ██║\n" +
    "██║  ███╗██║   ██║██║   ██║██║  ██║    ██║     ██║   ██║██╔████╔██║██╔████╔██║██║   ██║       ██║     ██║     ██║\n" +
    "██║   ██║██║   ██║██║   ██║██║  ██║    ██║     ██║   ██║██║╚██╔╝██║██║╚██╔╝██║██║   ██║       ██║     ██║     ██║\n" +
    "╚██████╔╝╚██████╔╝╚██████╔╝██████╔╝    ╚██████╗╚██████╔╝██║ ╚═╝ ██║██║ ╚═╝ ██║██║   ██║       ╚██████╗███████╗██║\n" +
    " ╚═════╝  ╚═════╝  ╚═════╝ ╚═════╝      ╚═════╝ ╚═════╝ ╚═╝     ╚═╝╚═╝     ╚═╝╚═╝   ╚═╝        ╚═════╝╚══════╝╚═╝\n" +
    "                                                                                                                                                                   ";

    private static final String[] scopes = {
            "build", "ci", "chore", "docs", "feat", "fix", "perf", "refactor", "style", "test"
    };

    public static boolean validateArgs(String[] args) {
        // blacklist "-h, m"
        String[] deniedFlags = { "-h", "-m" };
        Set<String> argsSet = new HashSet<>();
        Arrays.stream(args).forEach(argsSet::add);
        return Arrays.stream(deniedFlags).noneMatch(argsSet::contains);
    }

    public static String selectScopeType() throws IOException {
        Terminal terminal = null;
        terminal = TerminalBuilder.builder().system(true).build();

        String[] options = {
                "build: Changes that affect the build system or external dependencies",
                "ci: Changes to CI configuration files and scripts",
                "chore: Changes which doesn't change source code or tests",
                "docs: Documentation only changes",
                "feat: A new feature",
                "fix: A bug fix",
                "perf: A code change that improves performance",
                "refactor: A code change that neither fixes a bug nor adds a feature",
                "style: Changes that do not affect the meaning of the code",
                "test: Adding missing tests or correcting existing tests"
        };

        if (terminal != null) {
            return interactiveSelect(terminal, options);
        } else {
            throw new RuntimeException("Failed to create terminal");
        }
    }

    private static String interactiveSelect(Terminal terminal, String[] options) {
        int selected = 0;
        while (true) {
            terminal.puts(org.jline.utils.InfoCmp.Capability.clear_screen);
            terminal.flush();
            terminal
                    .writer()
                    .println(
                            "\n"
                                    + LOGO
                                    + "\nA CLI alternative for commitzen \n"
                                    + "\nSelect the type of scope: ");
            for (int i = 0; i < options.length; i++) {
                if (i == selected) {
                    terminal
                            .writer()
                            .println(
                                    new AttributedString(
                                            "> " + options[i],
                                            AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN))
                                            .toAnsi());
                } else {
                    terminal.writer().println(" " + options[i]);
                }
            }
            terminal.writer().println("\nUse arrow keys to navigate, Enter to select");
            terminal.flush();

            // Read input
            int c;
            try {
                c = terminal.reader().read();
                switch (c) {
                    case 65: // Up arrow
                        selected = (selected - 1 + options.length) % options.length;
                        break;
                    case 66: // Down arrow
                        selected = (selected + 1) % options.length;
                        break;
                    case 13: // Enter
                        terminal.close();
                        return scopes[selected];
                }
            } catch (IOException e) {
                LOGGER.error(String.format("Failed to read input: %s", e.getMessage()));
            }
        }
    }

    public static String getDescription() {
        Scanner sc = new Scanner(System.in);
        String description = null;
        System.out.print(
                "Enter the commit description, the description is a short summary of the code changes"
                        + " (maximum 92 characters): ");
        description = sc.nextLine();
        while (description.length() > 92 || description.isEmpty()) {
            System.out.print(
                    "The description is too long or empty, please enter a valid description: ");
            description = sc.nextLine();
        }
        // sc.close();
        return description;
    }

    public static String getBreakingChanges(String message, boolean drawAttention) throws IOException {
        Scanner sc = new Scanner(System.in);
        String breakingChanges = null;
        System.out.print("Does this commit have breaking changes? (y/n): ");
        String hasBreakingChanges = sc.nextLine();
        while (!("y".equals(hasBreakingChanges) || "n".equals(hasBreakingChanges)
                || hasBreakingChanges.isEmpty())) {
            System.out.print("Invalid input, please enter y or n: ");
            hasBreakingChanges = sc.nextLine();
        }
        if ("y".equals(hasBreakingChanges)) {
            if (!drawAttention) {
                System.out.print("Draw Attention? (y/n): ");
                drawAttention = "y".equals(sc.nextLine());
            }
            if (message.isEmpty() && !drawAttention) {
                System.out.print(
                        "A BREAKING CHANGE commit requires a body. Please enter a longer description of the"
                                + " commit itself: ");
                message = sc.nextLine();
                while (message.isEmpty()) {
                    System.out.print("The message is empty, please enter a valid message: ");
                    message = sc.nextLine();
                }
            }
            System.out.print("Describe the breaking changes: ");
            breakingChanges = sc.nextLine();
            while (breakingChanges.isEmpty() && !drawAttention) {
                System.out.print(
                        "The breaking changes is empty, please enter a valid breaking changes: ");
                breakingChanges = sc.nextLine();
            }
        }

        return breakingChanges;
    }

    private static String breakingChangesResult() throws IOException {
        Terminal terminal = null;
        terminal = TerminalBuilder.builder().system(true).build();
        String[] options = { "yes", "no" };
        if (terminal != null) {
            int selected = 0;
            while (true) {
                terminal.puts(org.jline.utils.InfoCmp.Capability.clear_screen);
                terminal.flush();
                for (int i = 0; i < options.length; i++) {
                    if (i == selected) {
                        terminal
                                .writer()
                                .print(
                                        new AttributedString(
                                                "✔️  " + options[i],
                                                AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN))
                                                .toAnsi());
                    } else {
                        terminal.writer().print(" " + options[i]);
                    }
                }
                terminal.writer().println();
                terminal.flush();
                // terminal.puts(Capability.clear_screen);

                // Read input
                int c;
                try {
                    c = terminal.reader().read();
                    switch (c) {
                        case 68: // Left arrow
                            selected = (selected - 1 + options.length) % options.length;
                            break;
                        case 67: // Right arrow
                            selected = (selected + 1) % options.length;
                            break;
                        case 13: // Enter
                            terminal.close();
                            return scopes[selected];
                    }
                } catch (IOException e) {
                    LOGGER.error(String.format("Failed to read input: %s", e.getMessage()));
                }
            }
        } else {
            throw new RuntimeException("Failed to create terminal");
        }
    }

    public static String getString(String scopeType, String scope, boolean drawAttention, String description,
            String message, String breakingChanges, String issueReferences) {
        String commit = "\"";

        if (scopeType != null && !scopeType.isEmpty()) {
            commit += scopeType;
        }
        if (scope != null && !scope.isEmpty()) {
            commit += " (" + scope + ")";
        }
        if (drawAttention) {
            commit += "!";
        }
        if (description != null && !description.isEmpty()) {
            commit += ": " + description;
        }
        if (message != null && !message.isEmpty()) {
            commit += "\n" + message;
        }
        if (breakingChanges != null && !breakingChanges.isEmpty()) {
            commit += "\n" + "BREAKING CHANGE: " + breakingChanges;
        }
        if (issueReferences != null && !issueReferences.isEmpty()) {
            commit += "\nIssue ref(s):" + issueReferences;
        }
        commit += "\"";
        return commit;
    }
}
