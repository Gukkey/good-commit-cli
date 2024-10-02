package com.goodcommit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import static com.goodcommit.helpers.Helpers.getBreakingChanges;
import static com.goodcommit.helpers.Helpers.getDescription;
import static com.goodcommit.helpers.Helpers.getString;
import static com.goodcommit.helpers.Helpers.selectScopeType;
import static com.goodcommit.helpers.Helpers.validateArgs;

@Command(name = "", mixinStandardHelpOptions = true, description = "Good commit, a CLI alternative for commitzen")
public class MainCommand implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(MainCommand.class);

  private static final String LOGO =
  " ██████╗  ██████╗  ██████╗ ██████╗      ██████╗ ██████╗ ███╗   ███╗███╗   ███╗██╗████████╗     ██████╗██╗     ██╗\n" +
  "██╔════╝ ██╔═══██╗██╔═══██╗██╔══██╗    ██╔════╝██╔═══██╗████╗ ████║████╗ ████║██║╚══██╔══╝    ██╔════╝██║     ██║\n" +
  "██║  ███╗██║   ██║██║   ██║██║  ██║    ██║     ██║   ██║██╔████╔██║██╔████╔██║██║   ██║       ██║     ██║     ██║\n" +
  "██║   ██║██║   ██║██║   ██║██║  ██║    ██║     ██║   ██║██║╚██╔╝██║██║╚██╔╝██║██║   ██║       ██║     ██║     ██║\n" +
  "╚██████╔╝╚██████╔╝╚██████╔╝██████╔╝    ╚██████╗╚██████╔╝██║ ╚═╝ ██║██║ ╚═╝ ██║██║   ██║       ╚██████╗███████╗██║\n" +
  " ╚═════╝  ╚═════╝  ╚═════╝ ╚═════╝      ╚═════╝ ╚═════╝ ╚═╝     ╚═╝╚═╝     ╚═╝╚═╝   ╚═╝        ╚═════╝╚══════╝╚═╝\n" +
  "                                                                                                                                                                   ";

  private static final String HELP_MESSAGE = "For Good commit cli to work, you just have to type \"good-commit\" and the process will"
      + " start and the CLI will ask necessary prompts for the commit. If you want to speed"
      + " up the process and give some manual inputs via options, below are the options. You can add the git flags after these flags like \"good-commit <good-commit-flags> <git commit flags>\" \n"
      + //
      "\n"
      + //
      "     --type              scope type\n"
      + //
      "     --scope             scope\n"
      + //
      "\n"
      + //
      "     --description       commit description\n"
      + //
      "     --message           commit message\n"
      + //
      "\n"
      + //
      "     --breaking-changes  breaking changes description\n"
      + //
      "     --draw-attention    draw attention\n"
      + //
      "     --issue-references  issue references\n"
      + //
      "     --safe              safe mode\n"
      + //
      "\n"
      + //
      "Reminder: These options are not mandatory. The CLI will ask inputs for these if you"
      + " initalize it via \"good-commit\" command.";

  @Option(names = { "--scope-type" }, arity = "0..1", description = "Scope type")
  private String scopeType;

  @Option(names = { "--scope" }, arity = "0..1", description = "Scope")
  private String scope;

  @Option(names = { "--description" }, arity = "0..1", description = "Commit description")
  private String description;

  @Option(names = { "--commit-message" }, arity = "0..1", description = "Commit message")
  private String message;

  @Option(names = { "--breaking-changes" }, arity = "0..1", description = "Breaking changes")
  private String breakingChanges;

  @Option(names = { "--draw-attention" }, arity = "0..1", description = "Draw Attention")
  private boolean drawAttention;

  @Option(names = { "--issue-references" }, arity = "0..1", description = "Issue Reference(s)")
  private String issueReferences;

  @Option(names = { "--safe" }, arity = "0..1", description = "Safe mode, whether to display the commit message before commiting")
  private boolean safeMode = true;

  @Option(names = { "--help" }, description = "Display help message")
  private boolean help;

  @Parameters(description = "Git flags", arity = "0..*")
  private String[] gitFlags = new String[0];
  private Scanner sc = new Scanner(System.in);

  @Override
  public void run() {

    if (help) {
      System.out.println(HELP_MESSAGE);
      sc.close();
      return;
    }

    System.out.println("\n\n" + LOGO + "\n\nA CLI alternative for commitzen \n");

    if (scopeType == null) {
      try {
        scopeType = selectScopeType();
        LOGGER.info("Scope Type: {}", scopeType);
      } catch (IOException e) {
        LOGGER.error("Error reading input: {}", e.getMessage());
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
      LOGGER.info("Description: {}", description);
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

    String commit = getString(scopeType, scope, drawAttention, description, message, breakingChanges, issueReferences);
    LOGGER.info(commit);

    if (safeMode) {
      try (Terminal terminal = TerminalBuilder.terminal()) {
        LineReader lineReader = LineReaderBuilder.builder().terminal(terminal).build();
        String prompt = "\n\nBelow is the generated commit message, please make changes if you want or press ENTER to continue to start the commit process\n";
        commit = lineReader.readLine(prompt, null, commit);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    sc.close();
    Process gitProcess;

    try {
      StringBuilder sb = new StringBuilder();

      for (String s : gitFlags) {
        sb.append(s + " ");
      }

      String gitFlagsString = sb.toString().trim();
      commit = commit.trim();

      List<String> processBuilderList = new ArrayList<>();
      processBuilderList.add("git");
      processBuilderList.add("commit");
      processBuilderList.add("-m");
      processBuilderList.add(commit);

      LOGGER.info("SB LENGTH: {}", sb.length());
      if (sb.length() != 0) {
        processBuilderList.add(gitFlagsString);
      }

      gitProcess = new ProcessBuilder(processBuilderList).start();
      gitProcess.waitFor();
      String res = new String(gitProcess.getInputStream().readAllBytes());
      System.out.println("\n" + res);

      if (gitProcess.exitValue() == 0) {
        LOGGER.info("Commit created successfully");
        System.out.println(String.format(
            "Commit has been created successfully. Commit message: %n%s%nIncase if you want to edit the commit message use the following, git commit --amend -m \"New commit message\"",
            commit));
      } else {
        String errorMessage = new String(gitProcess.getErrorStream().readAllBytes());
        LOGGER.error("Error while trying to create commit: {}", errorMessage);
      }
    } catch (IOException | InterruptedException e) {
      LOGGER.error(String.format("Failed to create commit: %s", e.getMessage()));
    }
  }

  public static void main(String[] args) {
    if (!validateArgs(args)) {
      System.out.println(
          "Wrong arguments, please go through help message so see how to construct proper args by entering \"git commit --help\"");
      System.exit(0);
    } else {
      int exitCode = new CommandLine(new MainCommand())
          .setUnmatchedOptionsArePositionalParams(true)
          .execute(args);
      System.exit(exitCode);
    }
  }
}
