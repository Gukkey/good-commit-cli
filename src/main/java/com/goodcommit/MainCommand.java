package com.goodcommit;

import java.io.IOException;
import java.util.Scanner;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "", description = "Good commit, a CLI alternative for commitzen")
public class MainCommand implements Runnable {

    final String scopePrompt = "Enter the type of the scope\n1. build: Changes that affect the build system or external dependencies (example scopes: gulp, broccoli, npm) \n2. ci: Changes to CI configuration files and scripts (example scopes: Travis, Circle, BrowserStack, SauceLabs) \n3. chore: Changes which doesn't change source code or tests (e.g. changes to the build process, auxiliary tools, libraries) \n4. docs: Documentation only changes \n5. feat: A new feature \n6. fix: A bug fix \n7. perf: A code change that improves performance \n8. refactor: A code change that neither fixes a bug nor adds a feature \n9. style: Changes that do not affect the meaning of the code (white-space, formatting, missing semi-colons, etc) \n10. test: Adding missing tests or correcting existing tests \nEnter the scope number: ";
    final String[] scopes = { "build", "ci", "chore", "docs", "feat", "fix", "perf", "refactor", "style", "test" };

    @Option(names = {"-t", "--type"}, arity = "0..1", description = "Scope type")
    String scopeType;

    @Option(names = {"-s", "--scope"}, arity = "0..1", description = "Scope")
    String scope;

    @Option(names = {"-d", "--description"}, arity = "0..1", description = "Commit description")
    String description;

    @Option(names = {"-m", "--message"}, arity = "0..1", description = "Commit message")
    String message;

    @Option(names = {"-b", "--breaking-changes"}, arity = "0..1", description = "Breaking changes")
    String breakingChanges;

    @Option(names = {"-a", "--draw-attention"}, arity = "0..1", description = "Draw Attention")
    boolean drawAttention;

    @Option(names = {"-i", "--issue-references"}, arity = "0..1", description = "Issue Reference(s)")
    String issueReferences;

    @Override
    public void run() {
        String logo = 
        " ██████╗  ██████╗  ██████╗ ██████╗      ██████╗ ██████╗ ███╗   ███╗███╗   ███╗██╗████████╗     ██████╗██╗     ██╗\n" +
        "██╔════╝ ██╔═══██╗██╔═══██╗██╔══██╗    ██╔════╝██╔═══██╗████╗ ████║████╗ ████║██║╚══██╔══╝    ██╔════╝██║     ██║\n" +
        "██║  ███╗██║   ██║██║   ██║██║  ██║    ██║     ██║   ██║██╔████╔██║██╔████╔██║██║   ██║       ██║     ██║     ██║\n" +
        "██║   ██║██║   ██║██║   ██║██║  ██║    ██║     ██║   ██║██║╚██╔╝██║██║╚██╔╝██║██║   ██║       ██║     ██║     ██║\n" +
        "╚██████╔╝╚██████╔╝╚██████╔╝██████╔╝    ╚██████╗╚██████╔╝██║ ╚═╝ ██║██║ ╚═╝ ██║██║   ██║       ╚██████╗███████╗██║\n" +
        " ╚═════╝  ╚═════╝  ╚═════╝ ╚═════╝      ╚═════╝ ╚═════╝ ╚═╝     ╚═╝╚═╝     ╚═╝╚═╝   ╚═╝        ╚═════╝╚══════╝╚═╝\n" +
        "                                                                                                                 ";

        System.out.println("\n" + logo);
        System.out.println("A CLI alternative for commitzen \n");
        Scanner sc = new Scanner(System.in);
        if (scopeType == null) {
            System.out.print(scopePrompt);
            String value = sc.nextLine();
            while (value.isEmpty()) {
                System.out.print("Scope type is required, please enter valid scope type: ");
                value = sc.nextLine();
            }
            int index = Integer.valueOf(value) - 1;
            if (index >= 0 && index < scopes.length) {
                scopeType = scopes[index];
            } else {
                while (index < 0 || index >= scopes.length) {
                    System.out.print("Invalid scope, please enter a valid scope: ");
                    value = sc.nextLine();
                    index = Integer.valueOf(value) - 1;
                }
                scopeType = scopes[index];
            }

        }
        if (scope == null) {
            System.out.print("What is the scope of this change (e.g. component or file name): (press enter to skip): ");
            scope = sc.nextLine();
        }

        if (description == null) {
            System.out.print(
                    "Enter the commit description, the description is a short summary of the code changes (maximum 92 characters): ");
            description = sc.nextLine();
            while (description.length() > 92 || description.length() == 0) {
                System.out.print("The description is too long or empty, please enter a valid description: ");
                description = sc.nextLine();
            }
        }
        if (message == null) {
            System.out.print(
                    "Enter the commit message, the message is a longer description of the commit (press enter to skip): ");
            message = sc.nextLine();
        }
        if (breakingChanges == null) {
            System.out.print("Does this commit have breaking changes? (y/n): ");
            String hasBreakingChanges = sc.nextLine();
            if (hasBreakingChanges.isEmpty()) {
                hasBreakingChanges = "n";
            }
            while (!(hasBreakingChanges.equals("y") || hasBreakingChanges.equals("n"))) {
                System.out.print("Invalid input, please enter y or n: ");
                breakingChanges = sc.nextLine();
            }
            if (hasBreakingChanges.equals("y")) {
                System.out.print("Draw Attention? (y/n): ");
                drawAttention = sc.nextLine().equals("y");
                if (message.isEmpty() && !drawAttention) {
                    System.out.print(
                            "A BREAKING CHANGE commit requires a body. Please enter a longer description of the commit itself: ");
                    message = sc.nextLine();
                    while (message.isEmpty()) {
                        System.out.print("The message is empty, please enter a valid message: ");
                        message = sc.nextLine();
                    }
                }
                System.out.print("Describe the breaking changes: ");
                breakingChanges = sc.nextLine();
                while (breakingChanges.isEmpty() && !drawAttention) {
                    System.out.print("The breaking changes is empty, please enter a valid breaking changes: ");
                    breakingChanges = sc.nextLine();
                }

            }
            if (issueReferences == null) {
                System.out.print("Enter the issue reference(s) (press enter to skip): ");
                issueReferences = sc.nextLine();
                sc.close();
            }

        }
        String commit = "";
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
            commit += ": " + description + "\n";
        }
        if (message != null && !message.isEmpty()) {
            commit += message + "\n";
        }
        if (breakingChanges != null && !breakingChanges.isEmpty()) {
            commit += "BREAKING CHANGE: " + breakingChanges + "\n";
        }
        if (issueReferences != null && !issueReferences.isEmpty()) {
            commit += issueReferences;
        }
        Process gitProcess;
        try {
            gitProcess = new ProcessBuilder("git", "commit", "-m", commit).start();
            gitProcess.waitFor();
            String res = new String(gitProcess.getInputStream().readAllBytes());
            System.out.println(res);
            if (gitProcess.exitValue() == 0) {
                System.out.println("Commit created successfully");
            } else {
                System.out.println(new String(gitProcess.getErrorStream().readAllBytes()));
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Failed to create commit");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new CommandLine(new MainCommand()).execute(args);
    }
}