import java.math.BigInteger;
import java.io.IOException;

//Input: No arguments, loads no files automatically
//Output: File "public_key" containing variables (n,e) and file "private_key" containing variable (d) 
//        n is the product of two large primes, 
//        e is 65537 or a random 6 digit number that is coprime with the product of the first prime minus one and the second prime minus one "p1q1"
//        d is the modular inverse of e(mod p1q1)
//        These values will be used for RSA encryption/decryption
public class Key_setup {
        public static void main(String[] argv) {
                RSAHelper tools = new RSAHelper();  //make instance of RSAHelper object        
                int size = 250;         //size for each random prime set to 250  (n will be up to 500 digits)
                
                BigInteger p = tools.generate_p(size); 		//runs generate prime function for size arg, creating first large prime         
                BigInteger q = tools.generate_q(p,size);  	//runs generate prime function for size arg until size difference of 10^95 is assured, creating second large prime           
                BigInteger n = p.multiply(q);               //n = p*q
                BigInteger p1 = p.subtract(new BigInteger("1"));  	//p1 defined as p-1
                BigInteger q1 = q.subtract(new BigInteger("1"));  	//q1 defined as q-1    
                BigInteger p1q1 = p1.multiply(q1); 			//p1q1 defined as (p-1)(q-1)
                
                BigInteger e = tools.generate_e(p1q1);	//starts with a value of 65537, but keeps searching if not co-prime to p1q1
                BigInteger d = tools.EEA(e,p1q1);		//Uses Euclid Extended Alg to find e^-1 (mod p1q1)

                try {
                  tools.printToFile("public_key",n.toString()+"\n"+e.toString());       //sends value of n and e off to file "public_key"
                  tools.printToFile("private_key",d.toString());                        //sends value of d off to file "private_key"   
                }
                catch(IOException ex) {
                  ex.printStackTrace();
                }          
        }
}        