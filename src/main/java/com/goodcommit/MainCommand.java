package com.goodcommit;

import java.io.IOException;
import java.util.Scanner;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "", description = "Good commit, a CLI alternative for commitzen")
public class MainCommand implements Runnable {

  final String logo = 
  " ██████╗  ██████╗  ██████╗ ██████╗      ██████╗ ██████╗ ███╗   ███╗███╗   ███╗██╗████████╗     ██████╗██╗     ██╗\n" +
  "██╔════╝ ██╔═══██╗██╔═══██╗██╔══██╗    ██╔════╝██╔═══██╗████╗ ████║████╗ ████║██║╚══██╔══╝    ██╔════╝██║     ██║\n" +
  "██║  ███╗██║   ██║██║   ██║██║  ██║    ██║     ██║   ██║██╔████╔██║██╔████╔██║██║   ██║       ██║     ██║     ██║\n" +
  "██║   ██║██║   ██║██║   ██║██║  ██║    ██║     ██║   ██║██║╚██╔╝██║██║╚██╔╝██║██║   ██║       ██║     ██║     ██║\n" +
  "╚██████╔╝╚██████╔╝╚██████╔╝██████╔╝    ╚██████╗╚██████╔╝██║ ╚═╝ ██║██║ ╚═╝ ██║██║   ██║       ╚██████╗███████╗██║\n" +
  " ╚═════╝  ╚═════╝  ╚═════╝ ╚═════╝      ╚═════╝ ╚═════╝ ╚═╝     ╚═╝╚═╝     ╚═╝╚═╝   ╚═╝        ╚═════╝╚══════╝╚═╝\n" +
  "                                                                                                                 ";

  final String scopePrompt = "Enter the type of the scope\n"
      + "1. build: Changes that affect the build system or external dependencies (example"
      + " scopes: gulp, broccoli, npm) \n"
      + "2. ci: Changes to CI configuration files and scripts (example scopes: Travis, Circle,"
      + " BrowserStack, SauceLabs) \n"
      + "3. chore: Changes which doesn't change source code or tests (e.g. changes to the build"
      + " process, auxiliary tools, libraries) \n"
      + "4. docs: Documentation only changes \n"
      + "5. feat: A new feature \n"
      + "6. fix: A bug fix \n"
      + "7. perf: A code change that improves performance \n"
      + "8. refactor: A code change that neither fixes a bug nor adds a feature \n"
      + "9. style: Changes that do not affect the meaning of the code (white-space, formatting,"
      + " missing semi-colons, etc) \n"
      + "10. test: Adding missing tests or correcting existing tests \n"
      + "Enter the scope number: ";

  final String[] scopes = {
      "build", "ci", "chore", "docs", "feat", "fix", "perf", "refactor", "style", "test"
  };
  
  final String helpMessage = "For Good commit cli to work, you just have to type \"good-commit\" and the process will"
      + " start and the CLI will ask necessary questions for the commit. If you want to speed"
      + " up the process and give some manual inputs via options, here are they.\n"
      + //
      "\n"
      + //
      "-t, --type: scope type\n"
      + //
      "-s, --scope: scope\n"
      + //
      "\n"
      + //
      "-d, --description: commit description\n"
      + //
      "-m, --message: commit message\n"
      + //
      "\n"
      + //
      "-b, --breaking-changes: breaking changes description\n"
      + //
      "-a, --draw-attention: draw attention\n"
      + //
      "-i, --issue-references: (github) issue references\n"
      + //
      "\n"
      + //
      "Reminder: These options are not mandatory. The CLI will ask inputs for these if you"
      + " initalize it via \"good-commit\" command.";

  @Option(names = { "-t", "--type" }, arity = "0..1", description = "Scope type")
  String scopeType;

  @Option(names = { "-s", "--scope" }, arity = "0..1", description = "Scope")
  String scope;

  @Option(names = { "-d", "--description" }, arity = "0..1", description = "Commit description")
  String description;

  @Option(names = { "-m", "--message" }, arity = "0..1", description = "Commit message")
  String message;

  @Option(names = { "-b", "--breaking-changes" }, arity = "0..1", description = "Breaking changes")
  String breakingChanges;

  @Option(names = { "-a", "--draw-attention" }, arity = "0..1", description = "Draw Attention")
  boolean drawAttention;

  @Option(names = { "-i", "--issue-references" }, arity = "0..1", description = "Issue Reference(s)")
  String issueReferences;

  @Option(names = { "-h", "--help" }, description = "Display help message")
  boolean help = false;

  Scanner sc = new Scanner(System.in); 

  @Override
  public void run() {

    System.out.println("\n" + logo + "A CLI alternative for commitzen \n");

    if (help) {
      System.out.println(helpMessage);
      sc.close();
      return;
    }

    if (scopeType == null) {
      try {
        scopeType = selectScopeType();
      } catch (IOException e) {
        System.out.println("Error reading input: " + e.getMessage());
        sc.close();
        return;
      }
    }

    if (scope == null) {
      System.out.print(
          "What is the scope of this change (e.g. component or file name): (press enter to skip):"
              + " ");
      scope = sc.nextLine();
    }

    if (description == null) {
      description = getDescription();
      System.out.println("Description: " + description);
    }

    if (message == null) {
      System.out.print(
          "Enter the commit message, the message is a longer description of the commit (press enter"
              + " to skip): ");
      message = sc.nextLine();
    }

    if (breakingChanges == null) {
      breakingChanges = getBreakingChanges();
    }

    if (issueReferences == null) {
      System.out.print("Enter the issue reference(s) (press enter to skip): ");
      issueReferences = sc.nextLine();
    }
    sc.close();

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

  private String selectScopeType() throws IOException {
    Terminal terminal = null;
    try {
      terminal = TerminalBuilder.builder().system(true).build();
    } catch (Exception e) {
      System.out.println("Failed to create terminal");
      e.printStackTrace();
    }

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

  private String interactiveSelect(Terminal terminal, String[] options) {
    int selected = 0;
    while (true) {
      terminal.puts(org.jline.utils.InfoCmp.Capability.clear_screen);
      terminal.flush();
      terminal
          .writer()
          .println(
              "\n"
                  + logo
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
            return scopes[selected];
        }
      } catch (IOException e) {
        e.printStackTrace();
      } 
    }
  }

  private String getBreakingChanges() {
    System.out.print("Does this commit have breaking changes? (y/n): ");
    String hasBreakingChanges = sc.nextLine();
    while (!(hasBreakingChanges.equals("y") || hasBreakingChanges.equals("n") || hasBreakingChanges.isEmpty())) {
      System.out.print("Invalid input, please enter y or n: ");
      hasBreakingChanges = sc.nextLine();
    }
    if (hasBreakingChanges.equals("y")) {
      System.out.print("Draw Attention? (y/n): ");
      drawAttention = sc.nextLine().equals("y");
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

  private String getDescription() {
    System.out.print(
        "Enter the commit description, the description is a short summary of the code changes"
            + " (maximum 92 characters): ");
    description = sc.nextLine();
    while (description.length() > 92 || description.length() == 0) {
      System.out.print(
          "The description is too long or empty, please enter a valid description: ");
      description = sc.nextLine();
    }
    return description;
  }

}
