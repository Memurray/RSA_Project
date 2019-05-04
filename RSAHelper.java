import java.math.BigInteger;
import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileReader;

//This class has no direct inputs or outputs and is not explicitly run by the user
//This file exists to allow for all functions and logics to exist in one place and be reused by the various other files in this project

public class RSAHelper {	
	
	//Performs modular exponentiation on BigIntegers
	//Use modular exponentiation by decomposition of exponent into binary notation
	//Multiplying powers of 2 with a 1 in the bit position as it increases to final exponent
	public BigInteger modExp(BigInteger x,BigInteger a,BigInteger n) {
		BigInteger result = new BigInteger("1");
		for(int i = 0; i < a.bitLength(); i++) {  //for each bit in exponent
			if (a.testBit(i)) {  //if bit is a 1
				result = result.multiply(x); //multiply result by current power of x
				result = result.mod(n);   //adjust result based on mod
			}
			x = x.multiply(x);	//square x	
			x = x.mod(n);  //find mod adjusted power of x
		}		
		return result;
	}

	//Extended Euclid's Algorithm
	//Argument of form EEA(<value to be inverted>, <mod this value>)
	public BigInteger EEA(BigInteger t2,BigInteger t1) {
		BigInteger n = t1;   //save original mod value
		//preset base variables
		BigInteger q = new BigInteger("0");
		BigInteger r = new BigInteger("0");
		BigInteger x = new BigInteger("0");
		BigInteger y = new BigInteger("0");
		BigInteger x1 = new BigInteger("1");
		BigInteger x2 = new BigInteger("0");
		BigInteger y1 = new BigInteger("0");
		BigInteger y2 = new BigInteger("1");
		while (true) {  
			if (t2.bitLength() < 32 && t2.intValue() == 0)  //if remainder reaches 0 before reaches 1, no inverse
				return new BigInteger("0");
			q = t1.divide(t2);  //get quotient of current iteration
			r = t1.mod(t2);  //get remainder of current iteration
			x = x1.subtract(x2.multiply(q));   //calculate multiples tally  (x is a temp variable)
			y = y1.subtract(y2.multiply(q));   //calculate multiples tally  (y is a temp variable)
			//shift inspection frame down
			x1 = x2;  
			y1 = y2;  
			x2 = x;
			y2 = y;
			//gcd(a,b) -> gcd(b,remainder)
			t1 = t2;  
			t2 = r;			
			if (r.bitLength() < 32 && r.intValue() == 1){		//if remainder is 1, we're done		
				break;
				}
		}
		y2=y2.add(n).mod(n); //return multiplier value for value to be inverted, (adding n to ensure that modulo return is a positive value)
		return y2;
	}
	
	//Given a size, generate a "random" number of that many digits, only producing odd values
	public BigInteger genLargeOddNumber(int size) {
		if(size ==0)
			return new BigInteger("0");
		String intString = new String();
		if(size > 1) 
			intString = intString + (new Random().nextInt(9)+1);  //First number is 1-9 when size > 1
		for(int i = 0; i < size-2; i++)
			intString = intString + new Random().nextInt(10); //middle numbers 0-9
		intString = intString + (new Random().nextInt(5)*2+1); //last number is odd (0-4)*2 + 1 = 1,3,5,7,9
		return new BigInteger(intString);
	}
	
	//Given a size, generate a "random" number of that many digits, produces odd and even values
	public BigInteger genLargeNumber(int size) {
		if(size ==0)
			return new BigInteger("0");
		String intString = new String();
		if(size > 1)
			intString = intString + (new Random().nextInt(9)+1);  //First number is 1-9 when size > 1
		for(int i = 0; i < size-2; i++) 
			intString = intString + new Random().nextInt(10); //middle numbers 0-9	
		intString = intString + (new Random().nextInt(10)); //last number is 0-9
		return new BigInteger(intString);
	}
	
	//Given a range (min and max) generate and return "random" number in this range
	public BigInteger genLargeNumberRange(BigInteger min, BigInteger max) {
		BigInteger range = max.subtract(min);  //find the size of the gap between min and max
		int size = range.toString().length();
		BigInteger result = genLargeNumber(size);  //generate large numbered of the same size as this range
		while(result.compareTo(range)==1) {   	//if the number generated is larger than the range
			result = genLargeNumber(size);		//try again
		}
		result.add(min);  //return minimum value + value in min/max gap
		return result;
	}
	
	//Use miller rabin algorithm to test for primality
	//true if probably prime
	//false if composite
	public boolean miller_rabin(BigInteger n) {
		outerloop:
		for(int its=0; its < 100; its++) {  //Runs 100 tests, only passes as prime if all tests pass
			BigInteger n1 = n.subtract(new BigInteger("1"));  //calculate n-1
			BigInteger m = n.subtract(new BigInteger("1"));   //initialize second value that is equal to n-1
			int k = 0;
			while(m.mod(new BigInteger("2")).intValue() == 0) { //while value is still even, divide by 2, tracking number of times division was required
				k++;
				m = m.divide(new BigInteger("2"));
			}
			BigInteger a = genLargeNumberRange(new BigInteger("2"),n1);  //generate a value for a that is selected "randomly" from between 2 and n-1
			BigInteger b = modExp(a,m,n);  //find a^m mod n
			if(b.mod(n).compareTo(new BigInteger("1")) == 0 ||b.mod(n).compareTo(n1) == 0)  //if b is equiv to +/- 1 (mod n), this iteration of test passes
				continue outerloop;
			for(int i = 1; i < k; i++) {  	//for each power of 2 extracted from n-1
				b = b.multiply(b);  		//set b = b^2
				b = b.mod(n);				//find new b (mod n)
				if(b.mod(n).compareTo(new BigInteger("1")) == 0)  //if result is equiv to +1 (mod n), not prime
					return false;
				if(b.mod(n).compareTo(n1) == 0)  //if result is equiv to -1 (mod n), this iteration passes
					continue outerloop;
			}		
			return false;  //if reaches end of loops equal to power of 2 extracted from n-1, test fails
		}
		return true;  //if return statement has yet to be called after all 100 iteration, probably prime
	}
	
	//Combined function that generates a large number and tests if it's prime
	//keeps generating numbers until primality passes
	//returning the large prime
	public BigInteger makeLargePrime(int size) {
		BigInteger retval = genLargeOddNumber(size);
		while(!miller_rabin(retval)) {
        	retval = genLargeOddNumber(size);
        }
		return retval;
	}
	
	//This is a novelty function I implemented to allow me to have non-numeric message
	//NOT REQUIRED FOR THIS ASSIGNMENT, but should not conflict with assignment directive
	//Message turned into number of form 
	// "1" + <3 digit ascii representation of character> + ......
	// Technically triples the size requirement of pure integer
	public BigInteger stringToBig (String input) {
		String number = new String("1");
		for(int i = 0; i < input.length(); i++) {
			int val = (int)input.charAt(i);
			if(val < 10)
				number = number + "00";
			else if(val < 100)
				number = number + "0";
			number = number + Integer.toString(val);
		}		
		return new BigInteger(number);
	}
	
	//Reversing function for my stringToBig function
	//Ignore the first number, segment out next 3 characters, convert to char, repeat
	public String bigToString (BigInteger input) {
		String number = input.toString();
		String output = new String();
		for(int i = 1; i < number.length(); i+=3) {
			output = output +  (char) Integer.parseInt(number.substring(i,i+3));
		}
		return output;
	}
	
	//Takes two numbers and returns true if the absolute value of their difference is more than 10^95
	public Boolean largeDifference(BigInteger input1,BigInteger input2) {
		BigInteger dif = input1.subtract(input2);
		dif = dif.abs();
		BigInteger goal = new BigInteger("10").pow(95);
		if (dif.compareTo(goal) == 1)
			return true;
		return false;
	}
	
	//Takes two numbers and the size argument (same as the size used to generate number in the first place)
	//Keeps regenerating second integer value until it passes the 10^95 difference test implemented above
	public BigInteger ensureLargeDifference(BigInteger input1,BigInteger input2, int size) {
		if (largeDifference(input1,input2))
			return input2;
		input2 = makeLargePrime(size);
		while(!largeDifference(input1,input2)) {
			input2 = makeLargePrime(size);
		}
		return input2;
	}
	
	//Checks if 65537 is relatively prime with input (which should be (p-1)(q-1) )
	//If 65537 isnt relatively prime, keep generating prime numbers of similar size (chose 6 even though default e would be size 5) until relatively prime
	public BigInteger generate_e(BigInteger p1q1) {
		BigInteger e = new BigInteger("65537");
		if (p1q1.mod(e).compareTo(new BigInteger("0"))!= 0)  //since e is prime, the only way that they're not relatively prime is if e is factor of p1q1
				return e;
		e = makeLargePrime(6);
		while(p1q1.mod(e).compareTo(new BigInteger("0"))== 0) {  //keep making new primes until not relatively prime with p1q1
			e = makeLargePrime(6);
		}
		return e;
	}
	
	//Purely a more clear name for makeLargePrime of arg size
	public BigInteger generate_p(int size) {
		BigInteger p = makeLargePrime(size);
		return p;
	}

	//Combined function of generating q using makeLargePrime and ensuring that p and q are not within 10^95
	public BigInteger generate_q(BigInteger p,int size) {
		BigInteger q = makeLargePrime(size);
		q = ensureLargeDifference(p, q,size);
		return q;
	}
	
		
	//Given a file name and a string to write, makes a file by this name containing this string
	public static void printToFile(String file, String contents) throws IOException{    
	file = "./" + file;  //file made in same directory as program run
    FileWriter fileWriter = new FileWriter(file);
    fileWriter.write(contents);
    fileWriter.close();
	}

	//Given file name, read each line to a String array
	//preallocated to size 10, but uses at most 2 elements for the entire project
	public static String[] readFromFile(String file) throws IOException{
		String[] retString = new String[10];
		file = "./" + file;
		BufferedReader reader = new BufferedReader(new FileReader(file));
		int i =0;
		retString[i] = reader.readLine();  //first array element is first line
		while (retString[i] != null) {	//as long as the line just written wasnt blank, keep reading and writing lines to new array indices
			i++;
			retString[i] = reader.readLine();
		}
		return retString;  //return String array
	}
	
}