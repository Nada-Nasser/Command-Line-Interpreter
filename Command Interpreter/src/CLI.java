import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * 
 */

/**
 * @author 
 *
 */

public class CLI {

	/**
	 * @param args
	 */
	
	public static Parser parser = new Parser();
	public static Parser parser1 = new Parser();

	public static Scanner keyboard= new Scanner(System.in);
	public static String Directory; 
	public static Terminal terminal = new Terminal();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		System.setProperty("user.dir", "C:\\Users");
		Directory = System.getProperty("user.dir"); 
		String input = new String();
		System.getProperty("line.separator");
		System.lineSeparator();
		String.format("%n");
		

		while(true)
		{
			System.out.print("\nPS " + Directory + "> ");
			input = keyboard.nextLine();
			try {
				if(parser.parse(input))
				{
					
					if(parser.cmd.equals("cd") || parser.cmd.equals("cd\\"))	{
						String[] arg = parser.getArguments();
						terminal.cd(parser.cmd , arg[0]);
					}
					
					else if(parser.cmd.equals("ls")){
						String[] arg = parser.getArguments();
						terminal.ls(arg[0]);
					}
					else if(parser.cmd.equals("cp")) // must be 2 args 
					{
						String[] arg = parser.getArguments();
						terminal.cp(arg[0] , arg[1]);
					}
					else if(parser.cmd.equals("cat")) 
					{
						String[] arg = parser.getArguments();
						terminal.cat(arg);
					}
					else if(parser.cmd.equals("more"))
					{
						String[] arg = parser.getArguments();
						terminal.more(0, arg[0]);
					}					
					
					//******************************************
					
					else if(parser.cmd.equals("mkdir"))
					{
						terminal.mkdir(parser.args);
					}
					else if(parser.cmd.equals("rmdir"))
					{
						terminal.rmdir(parser.args);
					}
					else if(parser.cmd.equals("rm"))
					{
						String[] arg = parser.getArguments();
						terminal.rm(arg[0]);
					}
					else if(parser.cmd.equals("mv"))
					{
						String[] arg = parser.getArguments();
						terminal.mv(arg[0], arg[1]);
					}
					
					//******************************************
					else if(parser.cmd.equals("clear")) 
					{
						terminal.clear();
					}
					
					else if(parser.cmd.equals("date"))
					{
						terminal.date();
					}
					
					else if(parser.cmd.equals("arg"))
					{
						terminal.args();
					}
					
					else if(parser.cmd.equals("help")) 
					{
						terminal.help();
					}
					
					else if(parser.cmd.equals("pwd")) 
					{
						terminal.pwd();
					}
					
					if(Parser.pipeOperator)
					{
						if(parser1.parse(Parser.commands[1]) || Parser.commands[1].equals("more") || Parser.commands[1].equals("cat")) {
							terminal.nextCommand(parser1);
							Parser.pipeOperator = false;
						}
		
					}
					
				}
				else {
					System.out.println("\'" + input + "\' is not recognized as an internal or external command,\r\n" + 
							"operable program or batch file.");
				}
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
			
		}
		
		
	}


	

}
