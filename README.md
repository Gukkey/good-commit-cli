![image](https://github.com/user-attachments/assets/0799573c-25f2-4396-9d3d-68904fd85382)

If you like this project or want this CLI to be in scoop please consider giving a ⭐.

# Usage

Currently, approval is pending on Chocolatey (and this project does not fit the criteria to make a request for scoop extras nor main bucket) so the only way you can try is by downloading the exe and trying it on the terminal.

# Local Development

To try in the terminal, create a jar using maven or gradle.

`mvn clean package`

Then run the jar which will be on the target repository.

`java -jar target/JAR-NAME.jar`

This command is substitute for the `good-commit` command. So if you want to use `-h` flag or other options it will be like, `java -jar target/JAR-NAME.jar -h`

Currently .exe file is built using [launch4j](https://launch4j.sourceforge.net/). There are some good youtube videos out there if you want a tutorial to know how to convert the jar to an executable.

# FAQ

1. Why should I use this CLI?
  
  Ans: Good commit CLI can help you to learn and get into [Conventional Commits](https://www.conventionalcommits.org). The purpose of this CLI is to make people understand how to create git commits following conventional commits.
  
2. Using this CLI is way annoying
  
  Ans: If you find this CLI annoying that means you might have a gist of conventional commits by now. You are no longer in the need of this CLI and it has served it's purpose (I hope). You can now create git commits following conventional commits faster by typing the commit message yourself.
  
3. Why should I follow conventional commits?
  
  Ans: Anything that follows a standard or specification can be automated to an extend. For an instance, you can generate automated commit messages by hooks or tools, or use your favorite model to create commits following this standard. Also other developers can understand what your commit does.

4. When are you going to automate .exe builds or release this CLI to other CLI package managers?

  Ans: Currently I am trying to automate builds using GraalVM. It is not complete yet since I am getting lot of errors and I am new to it. Once it is setup and works fine, I will automate executable releases and submit this CLI to other CLI package managers.

