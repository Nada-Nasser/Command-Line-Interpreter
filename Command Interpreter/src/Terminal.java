import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;



public class Terminal{

	public static String outPutStringified = new String();
	
	public void cd(String cmd , String sourcePath)
	{
		if(cmd.equals("cd")) {
			if(!sourcePath.equals("NULL")) {
				
				sourcePath =  shortPath(sourcePath);
				
				if(new File(sourcePath).isDirectory()) {
					System.setProperty("user.dir", sourcePath);
					CLI.Directory = System.getProperty("user.dir");
				}
				else {
					System.out.println("Cannot find path \'" + sourcePath + "\' \nbecause it does not exist.");
				}
			}
		}
		else{
			System.setProperty("user.dir", "C:");
			CLI.Directory = System.getProperty("user.dir");
		}
	}
	
	public void ls(String path)
	{
		//PrintWriter out;
		
		if(path.equals("NULL")) {
			path = CLI.Directory;
		}
		path = shortPath(path);
				
		ArrayList<String> names = new ArrayList<String>(Arrays.asList((new File(path)).list()));
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String filedata = "";
		filedata += " mode         Last Date Modified       length            Name" + "\n" + 
				    "---------------------------------------------------------------------";
		for(int i = 0 ; i < names.size() ; i++ )
		{
			String mode;
			if(new File(path + "\\" + names.get(i)).isDirectory())
				mode = "d-----";
			else
				mode = "-a----";
			
			filedata += "\r\n" + mode + "        " //mode 
					+sdf.format((new File(path+ "\\" + names.get(i))).lastModified()) + "        " 
					+new File(path + "\\" + names.get(i)).length() + "        "
					+ names.get(i);	
		}
			
		if(Parser.OverwriteOperator)
		{
			WriteOnFile(Parser.OperatorFileName, filedata);	
			Parser.OverwriteOperator = false;
		}
		else if (Parser.AppendOperator) {
			
			AppendOnFile(Parser.OperatorFileName, filedata);
			Parser.AppendOperator = false;
		}
		else if(Parser.pipeOperator) {
			outPutStringified = filedata;
		}
		else {
			System.out.println(filedata);
		}
	}	
	
	/*
	 * Copy files not Directories
	 */
	public void cp(String sourcePath, String destinationPath )
	{
		sourcePath = shortPath(sourcePath);
		destinationPath = shortPath(destinationPath);
		
		if(new File(sourcePath).isDirectory())
		{
			ArrayList<String> names = new ArrayList<String>(Arrays.asList((new File(sourcePath)).list()));
			
			if(new File(destinationPath).isDirectory())
			{
				for(int i = 0 ; i < names.size() ; i++ ) {
					
					if(new File(sourcePath + "\\" + names.get(i)).isFile())
					{
						String source = sourcePath + "\\" + names.get(i);
						String destination = destinationPath + "\\" + names.get(i);
					
						copyFiles(source , destination);
						System.out.println(source);
					}
				}
				System.out.println(names.size() + " file(s) copied.");
			}
			else // if the Destination is a file  (folder to file)
			{
				String Lines = "";
				BufferedReader br;
				// read the content of all files in the sourcePath
				for(int i = 0 ; i < names.size() ; i++ ) {
					String FilePath = sourcePath + "\\" + names.get(i);
					try {
						br = new BufferedReader(new FileReader(FilePath));
						String line;
						while ((line = br.readLine()) != null) {
							Lines+=line;
						}
						System.out.println(FilePath);
						Lines+="\n";
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						System.out.println(e.getMessage());
					}
					catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.println(e.getMessage());
					} 
				}
				System.out.println(names.size() + " file(s) copied.");
				// write the Lines in the Destination File 
				PrintWriter out;
				try {
					out = new PrintWriter(destinationPath);
					out.println(Lines);
					out.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					System.out.println(e.getMessage());
				}
			}
		}
		else 
		{
			if(new File(destinationPath).isDirectory())
			{
				String Filename = new File(sourcePath).getName();
				destinationPath= destinationPath + "\\" + Filename;
			}
			copyFiles(sourcePath ,destinationPath);
			
			System.out.println(sourcePath + "\n 1 file(s) copied.");
		}
	}


	public void cat(String[] paths) throws FileNotFoundException 
	{
		for(int i = 0 ; i < paths.length;i++) {
			paths[i] = shortPath(paths[i]);
			String Lines = PrintFile(paths[i]);				
			if(Parser.OverwriteOperator) // >
			{
				WriteOnFile(Parser.OperatorFileName, (new File(paths[i])).getName() + "\r\n" + Lines);
				Parser.OverwriteOperator = false;
			}
			else if(Parser.AppendOperator){
				AppendOnFile(Parser.OperatorFileName, (new File(paths[i])).getName() + "\r\n" + Lines);
				Parser.AppendOperator = false;
			}
			else if(Parser.pipeOperator)
				outPutStringified = Lines;
			else {
		//		System.out.println((new File(paths[i])).getName());
				System.out.println(Lines);
			}
		}
	}
	
	
	
	// with pipes
	public void more(String buffer)
	{
		String[] lines = buffer.split("\\\n");
		for(int i = 0 ; i < lines.length ; i++) {
			
			System.out.println(lines[i]);
			
			if(i%5 == 0) {
				System.out.println("\n--More-- [press any char to continue, or q to quit]");
				 Scanner keybord = new Scanner(System.in);
				 String choice= keybord.nextLine();
				 if(choice.equals("q"))
					break; 
			}
		}
	}
	
	// 3,40 char = 1 page
	public void more(int notused ,String FilePath)
	{
		FilePath = shortPath(FilePath);
		BufferedReader br;
		String Lines = "";
		try {
			// print the file name
			if(new File(FilePath).isFile()){
				// print the content
				br = new BufferedReader(new FileReader(FilePath));
				 String line = ""; 
				 int nLines = 0;
				 while ((line = br.readLine()) != null) {
					 Lines+= line;	
					 System.out.println(line);
					 nLines++;
					 if(nLines > 30) {
						 System.out.println("\n--More-- [press any char to continue, or q to quit]");
						 Scanner keybord = new Scanner(System.in);
						 String choice= keybord.nextLine();
						 if(choice.equals("q"))
							break; 
						 
						 nLines = 0;
						 System.out.println('\n');
					 }
				}
			}
			else
			{
				throw new FileNotFoundException();
			}
			
		} catch (FileNotFoundException e ) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		
	}
	
	 public void date()
	    {
	        Date date = new Date();
	        String Date = date.toString();
	        if(Parser.OverwriteOperator)
	        {
	        	WriteOnFile(Parser.OperatorFileName, Date);
	        	Parser.OverwriteOperator = false;
	        }
	        else if(Parser.AppendOperator)
	        {
	        	AppendOnFile(Parser.OperatorFileName, Date);
	        	Parser.AppendOperator = false;
	        }
	        else if(Parser.pipeOperator) {///FOR PIPE
	        	outPutStringified = date.toString();
	        }
	        else
		        // display time and date using toString()
		        System.out.println(date.toString());
	    }
	    
	 public void args()
	    {
			String com;
	          
			for (int i = 0; i < 8; i++)
			{  
				System.out.println("\nEnter a command, press X to exit");
	            Scanner input = new Scanner(System.in);
	            com = input.next();
	            String argData = "";
	           if(com.equals("cd") || com.equals("is") || com.equals("rmdir"))
	           {
	        	   argData = "arg 1 : NameOfDirectory";
	           }	           
	           else if (com.equals("mv"))
	           {
	        	   argData = "arg 1 : oldDirectory , arg 2 : newDirectory";
	           }
	           else if (com.equals("rm"))
	           {
	        	   argData = "arg 1 : Name of file or directory";
	           }
	           else if (com.equals("cat"))
	           {
	        	   argData = "arg 1 : File name";
	           }
	           else if (com.equals("cp"))
	           {
	        	   argData = "arg 1 : SourcePath , arg 2 : DestinationPath";
	           }
	           else if (com.equals("mkdir"))
	           {
	        	   argData = "arg 1 : NewDirectory path";
	           }
	           else if (com.equals("date") || com.equals("help") || com.equals("more") || com.equals("args") || com.equals("pwd") || com.equals("clear"))
	           {
	        	   argData = "No arguments for such command";
	           }
	          
	           else if(com.equals("X") || com.equals("x") )
	           {
	               break;      
	           }
	           else {
	        	   argData = "\'" + com + "\' is not recognized as an internal or external command,\r\n"+ 
	        			   	"operable program or batch file.";
	           }
	           argData = com + "		" +argData + "\r\n";
	           if(Parser.OverwriteOperator)
		        {
		        	WriteOnFile(Parser.OperatorFileName, argData);
		        	Parser.AppendOperator = true;
		        	Parser.OverwriteOperator = false;
		        }
		        else if(Parser.AppendOperator)
		        {
		        	AppendOnFile(Parser.OperatorFileName, argData);
		        }
		        else {
		        	System.out.println(argData);
		        }
			}
			Parser.OverwriteOperator = false;
			Parser.AppendOperator = false;
	    }
	    
    
	 public void help()// throws IOException
	    {	
	        File file = new File("Help.txt");
	        FileInputStream fileInput;
	        try {
	    		fileInput = new FileInputStream(file);
				byte[] fileValue = new byte[(int) file.length()];
				fileInput.read(fileValue);
				fileInput.close();

				String fileContent = new String(fileValue, "UTF-8");
				
				if(Parser.OverwriteOperator)
		        {
		        	WriteOnFile(Parser.OperatorFileName, fileContent);
		        	Parser.OverwriteOperator = false;
		        }
		        else if(Parser.AppendOperator)
		        {
		        	AppendOnFile(Parser.OperatorFileName, fileContent);
		        	Parser.AppendOperator = false;
		        }
		        else if(Parser.pipeOperator)
		        	outPutStringified = fileContent;
			        
				// display time and date using toString()
		        else
		        	System.out.println(fileContent);
	    	}
	        catch (IOException e) {
			// TODO Auto-generated catch block
	    		System.out.println(e.getMessage());
			}
	    }
	 
    public void clear()
    {
        for (int i = 0; i < 50; i++)
            System.out.println();
    }
    
    public void pwd()
    {
    	String PwdResult = "Working Directory = " + System.getProperty("user.dir");
    	if(Parser.OverwriteOperator)
        {
        	WriteOnFile(Parser.OperatorFileName, PwdResult);
        	Parser.OverwriteOperator = false;
        }
        else if(Parser.AppendOperator)
        {
        	AppendOnFile(Parser.OperatorFileName, PwdResult);
        	Parser.AppendOperator = false;
        }
        else if(Parser.pipeOperator) {///FOR PIPE
          	outPutStringified = "Working Directory = " + System.getProperty("user.dir");
          }
        else
	        // display time and date using toString()
        	System.out.println("Working Directory = " + System.getProperty("user.dir"));
    }
     
 
    
    public void mkdir(String[] args) {
		if(!args[0].equals("-p") && !args[0].equals("-m") && !args[0].equals("-v") ) {
			new File(System.getProperty("user.dir")+"\\"+args[0]).mkdir();
		}
		else if(args[0].equals("-p"))
			new File(System.getProperty("user.dir")+"\\" +args[1]).mkdirs();
		
		else if(args[0].equals("-v")) {
			System.out.println("mkdir: created directory" + args[1]);
		}
	
	}
	
	
	public void rmdir(String[] args) {
		
		if(!args[0].equals("-p")  && !args[0].equals("-v") ) {
			new File(System.getProperty("user.dir")+"\\"+args[0]).delete();
		}
		else if(args[0].equals("-p")) {	
	//////////////////////////////////////////////////////
		}
		else if(args[0].equals("-v")) {
			System.out.println("rmdir: removing directory" + args[1]);
		}
	}
	
	
	public void rm(String file) {
		 new File(System.getProperty("user.dir") +"\\" + file).delete();
	}
	
	
	public void mv(String source ,String destination ) throws IOException {
		File src = new File(System.getProperty("user.dir") + "\\"+ source);
		File destn = new File(System.getProperty("user.dir") +"\\" +destination);
		
		if(src.isFile()|| src.isDirectory()) {
			if(!destn.isDirectory()) {
				Files.move(src.toPath(), destn.toPath() , StandardCopyOption.REPLACE_EXISTING);			}
			
			else
				src.renameTo(new File(System.getProperty("user.dir")+"\\" + destination + "\\" + src.getName()));
		}
				
	}

    
    
  ///***************************************************************************************


	 private String shortPath(String Path)
	    {
	    	if(Path.charAt(0) != 'C')
	    		Path = CLI.Directory +"\\" + Path;
	    	return Path;
	    }
	    
	    private void AppendOnFile(String FilePath , String Lines)
	    {
	    	try {
			    Files.write(Paths.get(FilePath), Lines.getBytes(), StandardOpenOption.APPEND);
			}catch (IOException e) {
			    //exception handling left as an exercise for the reader
				System.out.println(e.getMessage());
				//e.getStackTrace();
			}
	    }
		  
	    private void WriteOnFile(String FilePath , String Lines)
	    {
	    	PrintWriter out;		
	    	try {
				//System.out.println(Parser.OperatorFileName);
				out = new PrintWriter(FilePath);
				out.println(Lines);
				out.close();
			} 
			catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println(e.getMessage());
			}
	    }
		
	    
	    private void copyFiles(String source, String destinationPath) {
			// TODO Auto-generated method stub
			
			Path sourceDirectory = Paths.get(source);
	        Path targetDirectory = Paths.get(destinationPath);

	        //copy source to target using Files Class
	        try {
				Files.copy(sourceDirectory, targetDirectory );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			}
		}

	    private String PrintFile(String path)
		{
			BufferedReader br;
			String Lines = "";
				
			try {
				// print the file name
				if(new File(path).isFile()) {
					
					// print the content
					br = new BufferedReader(new FileReader(path));
					 String line;
					 while ((line = br.readLine()) != null) {
						 Lines+= line + '\n';
					}
				}
				else
				{
					throw new FileNotFoundException();
				}
			} catch (FileNotFoundException e ) {
				// TODO Auto-generated catch block
				System.out.println("File Not Found");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			}
			
			return Lines;
		}

	

	public void nextCommand(Parser parseObj) throws FileNotFoundException {
	
		
		System.out.println();
		if(parseObj.cmd.equals("help"))
			help();
		else if(parseObj.cmd.equals("date"))
			date();
		else if(parseObj.cmd.equals("pwd"))
			pwd();
		else if(parseObj.cmd.contains("ls")) 
			ls(parseObj.args[0]);
		else if(parseObj.cmd.equals("cat")&& parseObj.IsValid())
			cat(parseObj.args);
		else if(parseObj.cmd.equals("more") && parseObj.IsValid()) {
			more(0,parseObj.args[0]);
		}
		else if(parseObj.cmd.equals("more")) {
			more(outPutStringified);
		}
		if(!parseObj.cmd.equals("more") || parseObj.cmd.equals("cat"))
			System.out.println(outPutStringified);
	
	}
}