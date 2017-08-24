import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class trial {

	public static void main(String[] args)
	{
		String fname="/home/user/Documents/Course_Books/NLP/Project/small";
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(new File(fname)));
			String line;
			int count=0;
			ArrayList<String> current_sentence=new ArrayList<String>();
			while((line=br.readLine())!=null)
			{
				if(line=="")
					continue;
				System.out.println(line);
				System.out.println("Line size is "+line.length());
			}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
