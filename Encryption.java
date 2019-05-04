import java.math.BigInteger;
import java.io.IOException;

//Input: No arguments, automatically loads files, "public_key" and "message"
//Output: File "ciphertext" containing the encrypted version of the message

public class Encryption {
	public static void main(String[] argv) {
        RSAHelper tools = new RSAHelper();  //make instance of RSAHelper object  
        String[] public_key = new String[10];   //preallocate String array for file read
        String[] message = new String[10];		//preallocate String array for file read

        try {
        	public_key = tools.readFromFile("public_key");  //Save to String array contents of public_key file
        	message = tools.readFromFile("message");        //Save to String array contents of message file 
        }
        catch(IOException ex) {
            ex.printStackTrace();	
        }  

        BigInteger n = new BigInteger(public_key[0]);	//n is the first line from public_key file
        BigInteger e = new BigInteger(public_key[1]);	//e is the second line from public_key file
        BigInteger m = tools.stringToBig(message[0]);	//n is the first line from message file
        BigInteger c = tools.modExp(m, e, n);			//Use tools object to find c

		try {
          	tools.printToFile("ciphertext",c.toString()); 	//print c to the file ciphertext
        }
        catch(IOException ex) {
          	ex.printStackTrace();
        }  
	}
}