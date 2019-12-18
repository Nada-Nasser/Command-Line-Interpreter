import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 
 */

/**
 * @author 
 *
 */
public class Parser {
	
	String[] args; // Will be filled by arguments extracted by parse method
	String cmd; // Will be filled by the command extracted by parse method
	
	public static boolean OverwriteOperator = false , AppendOperator = false , pipeOperator = false;
	public static String OperatorFileName = "";
	public static String[] commands;
	public static boolean isValid;
	/*
	* Returns true if it was able to parse user input correctly. Otherwise false
	* In case of success, it should save the extracted command and arguments
	* to args and cmd variables
	* It should also print error messages in case of too few arguments for a commands
	* eg. “cp requires 2 arguments”
	*/

	
	
	public boolean parse(String input) // parse the input to args and cmd
	{
		isValid = false;
		
		if(input.indexOf(">") != -1)
		{
			OverwriteOperator = true;
			String[] Commands = input.split(">");
			input = Commands[0];
			OperatorFileName = Commands[1];
			OperatorFileName = OperatorFileName.substring(1);
			if(OperatorFileName.charAt(0) != 'C')
				OperatorFileName = CLI.Directory + "\\"+ OperatorFileName;
		}
		else if(input.indexOf(">>") != -1)
		{
			System.out.println(">>");
			AppendOperator = true;
			String[] Commands = input.split(">>");
			input = Commands[0];
			OperatorFileName = Commands[1];
			OperatorFileName = OperatorFileName.substring(1); // to delete the space 
			
			//OperatorFileName = shortPath(OperatorFileName); // handling short paths
		}
		
		else if (input.indexOf("|") != -1 ||input.contains(" | ")){
			pipeOperator = true;
			
			commands = input.split("\\|"); 	
			input  = commands[0];
		}
		
		String[] SplitedInput = input.split(" ");
		cmd = SplitedInput[0];
		args = new String[SplitedInput.length - 1];
		for(int i = 0 , j = 1 ; i < args.length ; i++  , j++)
			args[i] = SplitedInput[j];
		
		EraseQuotationMarks();
		
		// If condition to validate each command <------
		// isVaid = Your own validate function <------
		if(cmd.equals("cd") || cmd.equals("cd\\")) // Validation for cd command
			isValid = checkCd();
		
		else if(cmd.equals("ls")) // Validation for ls command
			isValid = checkCd();
		
		else if(cmd.equals("cp")) // Validation for cp command
			isValid = checkCp();
		else if(cmd.equals("cat")) // Validation for cat command
			isValid = CheckMoreThanOneArg();
		else if(cmd.equals("more")) // Validation for more command
			isValid = CheckOneArg();
		//*****************************************
		
		else if(cmd.equals("mkdir")) {
			isValid = checkMkdir();
		}
		
		else if(cmd.equals("rmdir")) {
			isValid = checkRmdir();
		}
		
		else if(cmd.equals("rm")){
			isValid = checkRM();
		}
		else if(cmd.equals("mv")) {
			isValid = checkMV();
		}
		//*****************************************
		else if(cmd.equals("help"))
			isValid = CheckNoArgs();
		else if(cmd.equals("arg"))
			isValid = CheckNoArgs();
		else if(cmd.equals("date"))
			isValid = CheckNoArgs();
		else if(cmd.equals("clear"))
			isValid = CheckNoArgs();
		else if(cmd.equals("pwd"))
			isValid = CheckNoArgs();
		//**********************************
		else if(new File(cmd).isFile() || new File(CLI.Directory + '\\' + cmd).isFile() ) 
			isValid = OpenFile(); 	
		else if (cmd.equals("exit"))
			isValid = true;
		
		return isValid;
	}
	
	
	
	
	private void EraseQuotationMarks() {
		// TODO Auto-generated method stub
		for(int i = 0 ; i < args.length ; i++)
		{	
			if(args[i].charAt(0)=='"'){
				args[i] = args[i].substring(1 , args[i].length()-1);
			}
		}
		/* ERRORRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
		if(OperatorFileName.charAt(0) == '"')
			OperatorFileName = OperatorFileName.substring(1 , OperatorFileName.length()-1);
	*/	
	}



	private boolean CheckNoArgs() {
		// TODO Auto-generated method stub
		if(args.length == 0 )
			return true;
		return false;
	}
	
	private boolean CheckOneArg() {
		// TODO Auto-generated method stub
		if(args.length == 1 )
			return true;
		return false;
	}
	
	// when no commands
	private boolean OpenFile() {
		// TODO Auto-generated method stub
		boolean isValid = false;
		Desktop desktop = Desktop.getDesktop();
		String path = cmd;
		if(path.charAt(0) != 'C')
			path = CLI.Directory + '\\' + path;
		
		System.out.println(path);
		
		File file = new File(path);
		if(file.exists()) {
			try {
				desktop.open(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			}
			isValid = true;
		}		
		return isValid;
	}

	// more than 1 arg
	private boolean CheckMoreThanOneArg() {
		// TODO Auto-generated method stub
		
		if(args.length >= 1)
			return true;
		
		return false;
	}

	// two args
	private boolean checkCp() {
		// TODO Auto-generated method stub
		boolean valid = false;
		
		if(args.length == 2 )
		{
			if((new File(args[0]).exists()) && (new File(args[1]).exists()) );
				valid =  true;
		}
		return valid;
	}

	// one or no args
	private boolean checkCd() {
		// TODO Auto-generated method stub
		boolean valid = false;
		if(args.length == 1)
		{
			if((new File(args[0]).exists()));
				valid =  true;
		}
		else if(args.length == 0) {
			args =  new String[1];
			args[0] = "NULL";
			valid = true;
		}
		
		return valid;
	}
	
	private boolean checkMkdir() {
		boolean valid = false;
		
		if(args.length ==  1 && !args[0].equals("-p") && !args[0].equals("-v") && !args[0].equals("-m"))
			valid = true;
		
		else if (args.length > 1 && args[0].equals("-p") || args.length > 1 && args[0].equals("-m") || args.length > 1 && args[0].equals("-v"))
			valid = true;
		
		return valid;
	}

	private boolean checkRmdir() {
		boolean valid = false;
		if(args.length == 1)
		{
			if((new File(System.getProperty("user.dir")+args[0]).exists()));
				valid =  true;
		}
		else if (args.length > 1 && args[0].equals("-p") || args.length > 1 && args[0].equals("-v"))
			valid = true;
		
		return valid;
	}

	private boolean checkMV() {
		boolean valid = false;
		if(args.length == 2) {
			if(new File(System.getProperty("user.dir")+"//"+args[0]).exists())
				valid =  true;
		}
		return valid;	
	}
	
	private boolean checkRM() {
		boolean valid = false;
		if(args.length == 1)
		{
			if(new File(System.getProperty("user.dir")+"\\"+args[0]).exists())
				valid =  true;
		}
		
		return valid;
	}
	
	
	public String getCmd() {
		return cmd;
	}
	
	public void PrintArgs()
	{
		for(int i = 0 ; i < args.length ; i++ )
			System.out.println(args[i]);
	}
	
	public String[] getArguments() {
		
		return args;
	}

	public boolean IsValid() {
		return isValid;
	}
}
