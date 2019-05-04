import java.math.BigInteger;
import java.io.IOException;

//Input: No arguments, automatically loads files, "public_key", "private_key" and "ciphertext"
//Output: File "decrypted_message" containing the decrypted version of the ciphertext, which is the same as the original message

public class Decryption {
	public static void main(String[] argv) {
        RSAHelper tools = new RSAHelper();  
        String[] public_key = new String[10];	//preallocate String array for file read
        String[] private_key = new String[10];	//preallocate String array for file read
        String[] ciphertext = new String[10];	//preallocate String array for file read

        try {
        	public_key = tools.readFromFile("public_key");		//Save to String array contents of public_key file
        	private_key = tools.readFromFile("private_key");  	//Save to String array contents of private_key file
        	ciphertext = tools.readFromFile("ciphertext");      //Save to String array contents of ciphertext file
        }
        catch(IOException ex) {
            ex.printStackTrace();	
        }  

        BigInteger n = new BigInteger(public_key[0]);	//n is the first line from public_key file
        BigInteger d = new BigInteger(private_key[0]);	//d is the first line from private_key file
        BigInteger c = new BigInteger(ciphertext[0]);	//c is the first line from ciphertext file

        BigInteger m_numbers = tools.modExp(c, d, n);  	//Use tools object to find message

        String m_text = tools.bigToString(m_numbers);	//Since I decided to have fun and allow full string messages, not just pure numbers,
        												//Use method from tools object to revert numbers to original string
        												//Note: This still works if message is just pure numbers too
        try {
          	tools.printToFile("decrypted_message",m_text);	//Print final message to file decrypyted_message
        }
        catch(IOException ex) {
          	ex.printStackTrace();
        }
	}
}