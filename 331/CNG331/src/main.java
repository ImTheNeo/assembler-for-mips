import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;



public class main {
	@SuppressWarnings("unused")
	static String convert_tobinary(String x) { // we use this function for immediate calculations simply fills the number with 0's and adds the sign bit 
		String sign = "0";
		int number = Integer.parseInt(x);
		if(number<0) {
			number=number*-1;
			sign = "1";
		}
			String bin = Integer.toBinaryString(number);
			
			int x1=bin.length();
			for(int i=0;i<15-x1;i++) {
				bin = "0" + bin;
			}
			bin = sign + bin;
			
			return bin;
		}
	
	static void get_labels(String filename) throws IOException { // this function is where we find the labels
		String PC = "80001000";// inital address to calculate label addresses
		int line = 1;
		long decimal = Long.parseLong(PC,16);
		String Instruction = "";
		BufferedReader rr = new BufferedReader(new FileReader(filename));
		StringBuilder bb = new StringBuilder();
		while ((Instruction = rr.readLine()) != null) {//we find a simple way to detect the labels if a line contains ":" that means there is a label 
			if(Instruction.contains(":")) {
				String[] Instruction_splitted = Instruction.split("[, :#]+");
				String a = Long.toHexString(decimal+4*(line-1));// we calculate the label address here
				Labels new_label = new Labels(Instruction_splitted[0],a,line);
        		labels.add(new_label);// and add the label with line number and address
			}
			else line++;
		}
	}	
	
	static String Batch_Mod() throws IOException{// batch mode is designed to take txt file as input and prints the hexadecimal form of the instructions in that txt file
		String filename;
		String sr;
		String Instruction = "";
		String type = "";
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter the filePath: ");// we ask for the pathfile here 
		filename = sc.nextLine();
		BufferedReader br = new BufferedReader(new FileReader(filename));
		StringBuilder sb = new StringBuilder();
		get_labels(filename);// before we start we call this function to detecet labels 
		int line=1;
		BufferedReader rr = new BufferedReader(new FileReader(filename));
		StringBuilder bb = new StringBuilder();
		while ((Instruction = rr.readLine()) != null) {
			
			type = "";
			
			if(Instruction.contains(":")) { //in case there is a situation where the label and the instruction is given in the same line we put that little control system here
				String[] Instruction_with_label = Instruction.split("[, :#]+");
				Instruction = Instruction.substring(Instruction_with_label[0].length() + 1, Instruction.length());// this ignores the label part and rearrange the line so that it only contains the instruction
			}
			if(Instruction.contains("\t"))
				Instruction = Instruction.substring(1, Instruction.length());// if there is a tab before the instruction we handle it so that the compiler is not confused
			String[] Instruction_splitted = Instruction.split("[, #]+");
			for(int i=0;i<i_type.size();i++) // we find the instruction type and send it to corresponding function to handle 
				if(Instruction_splitted[0].equals(i_type.get(i).name))
					type = i_type.get(i).type;

			if(type.equals("I"))
				System.out.println(handle_i_type(Instruction,line));
			
			else if(type.equals("R")) 
				System.out.println( handle_r_type(Instruction));
			
			else if(type.equals("M"))
				System.out.println( handle_m_type(Instruction));
			
			else if(type.equals("J"))
				System.out.println(handle_j_type(Instruction));
			else continue;
		line++;// then we keep reading the file untill it is over
		}
		return null;
	}
	
	
	
	
	private static String handle_j_type(String instruction) { // this function handles jump instructions 
		String[] Instruction_splitted = instruction.split("[, #]+");
		String ins_32b;
		int i;
		String ins_opcode="";
		String offset="";
		for(i=0;i<j_instructions.size();i++) { // we check the j_instructions list to find the instruction and get the opcode
			if(Instruction_splitted[0].equals(j_instructions.get(i).name)) {
				ins_opcode=j_instructions.get(i).opcode;
			}
		}
		for(i=0;i<labels.size();i++) {
			if(Instruction_splitted[1].equals(labels.get(i).label.substring(0, labels.get(i).label.length() - 1))) { // we try to find the label here and get its address
				String hex = labels.get(i).Adress;
				long decimal = Long.parseLong(hex,16);
				hex = Long.toBinaryString(decimal);
				offset = hex.substring(2, hex.length() - 4);// if we get the address we get rid of first 4 bits and last 2 bits for offset
			}
		}
		ins_32b = ins_opcode + offset;
		long decimal = Long.parseLong(ins_32b,2);
		String hexa = Long.toHexString(decimal);
		int l = hexa.length();
		for(i=0;i<8-l;i++) hexa = "0" + hexa;
		hexa="0x"+hexa;
		return hexa;
	}

	static String find_register_address(String name1) { //this function finds the register address and returns it 
		int i;
		for(i=0;i<registers.size();i++) {
			if(registers.get(i).Register.equals(name1))
			return registers.get(i).Adress;
		}
		
		return "Register is not found ";
	}
	public static ArrayList<Registers> registers = new ArrayList<Registers>();// these are our arraylists where we keep all kind of relative information
	public static ArrayList<R_Instructions> r_instructions = new ArrayList<R_Instructions>();
	public static ArrayList <I_Instructions> i_instructions = new ArrayList <I_Instructions>();
	public static ArrayList <Memory_Instructions> m_instructions = new ArrayList <Memory_Instructions>();
	public static ArrayList <J_Instructions> j_instructions = new ArrayList <J_Instructions>();
	public static ArrayList <Instruction_type> i_type = new ArrayList <Instruction_type>();
	public static ArrayList <Labels> labels = new ArrayList <Labels>();

	public static void main(String[] args) throws IOException {// This part is simply the menu
		int cnt;
		int i;
		get_table();
        String selection = "";
        String result = "";
		Scanner sc = new Scanner(System.in);
		System.out.println("Do you want to use (B)atch Mode or (I)nteractive Mode?\n or do you want to (E)xit?\n Selection: ");
		selection = sc.nextLine();
		while(selection.equals("E")==false ){//unless user types "e" or "E" we keep the user inside the program
			if (selection.equals("B") || selection.equals("b")) {
				Batch_Mod();
			}
			
			else if(selection.equals("I") || selection.equals("i")) {
				System.out.println(Interactive_Mod());
			
			}
			else if(selection.equals("E") || selection.equals("e")) break;
			
			else {
				System.out.println("You entered an invalid selection!!\nPlease choose a valid option");
				
			}
			System.out.println("Do you want to use (B)atch Mode or (I)nteractive Mode?\n or do you want to (E)xit?\n Selection: ");
			selection = sc.nextLine();
		}
		
	}
	
	
	

	public static String handle_i_type(String ins,int line) { //this function handles I types instructions. Takes the instruction and line number of that instruction just in case if it is a branch instruction  
		String[] Instruction_splitted = ins.split("[, #]+");
		String ins_32b;
		int i;
		String ins_opcode="";
		String ins_r1="";
		String ins_r2="";
		String ins_imm;
		int flag1=0;
		int flag2=0;
		int flag3=0;
		int flag4=0;
		for(i=0;i<i_instructions.size();i++) {
			if(Instruction_splitted[0].equals(i_instructions.get(i).name)) { // we split the instruction and try to find which instruction is it then if we find it in our array we get the corresponding opcode
				flag1++;
				ins_opcode=i_instructions.get(i).opcode;
			}
			
			
		}
		if(flag1==0) {
			return "There is no such an instruction as you typed!!!";// if the instruction is not found in our array we give this error and terminate the program
		}
		for(i=0;i<registers.size();i++) { // this function finds the 5bit register address by finding it in registers array
			if(Instruction_splitted[1].equals(registers.get(i).Register)) {
				flag2++;
				ins_r1=registers.get(i).Adress;
				
			}
		}
		if(flag2==0) {
			return "It seems like one of your registers is wrong!!\\n Please check your registers and enter again"; // if the register is not found throws that error and terminate the program
		}
		
		for(i=0;i<registers.size();i++) {// this function finds the 5bit register address by finding it in registers array
			if(Instruction_splitted[2].equals(registers.get(i).Register)) {
				flag3++;
				ins_r2=registers.get(i).Adress;
				
			}
			
		}
		if(flag3==0) {
			return "It seems like one of your registers is wrong!!\\n Please check your registers and enter again";// if the register is not found throws that error and terminate the program
		}
		
		if(Instruction_splitted[0].equals("bne") || Instruction_splitted[0].equals("beq") ) {// if the instruction is bne or beq we do the offset calculation 
		int offset = 0;
		int flag_label = 0	;
			for(i=0;i<labels.size();i++) {
				if(Instruction_splitted[3].equals(labels.get(i).label.substring(0, labels.get(i).label.length()))) { // we take the label and try to find it in our labels table then we use the line number of that label to calculate the offset
					offset = labels.get(i).line - line;
					flag_label = 1;
				}
			}
			if(flag_label == 0) {
				return "the label you are trying to reach is wrong please check your labels and instructions!!"; // if we cant find the label we throw this error
			}
			String bin = Integer.toBinaryString(offset);
			int x=bin.length();
			for(i=0;i<16-x;i++) bin = "0" + bin;
			
			if(offset < 0) bin = TwosComplement(bin); // if offset is negative we used twos complement for offset calculation
			
			ins_32b= ins_opcode + ins_r2 + ins_r1 + bin;
			long decimal = Long.parseLong(ins_32b,2);
			String hexa = Long.toHexString(decimal);
			hexa = "0x"+hexa;
			return hexa;
		}
		if(Instruction_splitted[3].startsWith("$")) {
			return "Your immediate can not be register";
		}
		int test =Integer.parseInt(Instruction_splitted[3]);
		if(test>32768||test<-32768)
			return "Your immediate number should be between -32768 and 32768!!!"; //if the immediate number is now in range we give this error and terminate the program
		
		
		ins_imm=convert_tobinary(Instruction_splitted[3]);
		ins_32b= ins_opcode  + ins_r2 + ins_r1 + ins_imm;// there we have our instruction in binary 
		int decimal = Integer.parseInt(ins_32b,2);
		String hexStr = Integer.toString(decimal,16); // and there we have it in hexadecimal 
		hexStr = "0x"+hexStr;
		return hexStr;
	}
	
	static String Interactive_Mod(){ // Interactive mode is where the user enters an instruction through the console it handles only 1 instruction at a time
		String Instruction = "";
		String type ="";
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter the instruction you want to convert it to hexadecimal : ");// we take the instruction here in the function
		Instruction = sc.nextLine();
		String[] Instruction_splitted = Instruction.split("[, #]+");
		
		for(int i=0;i<i_type.size();i++)
			if(Instruction_splitted[0].equals(i_type.get(i).name)) // we get the instruction type here and pass it to corresponding function
				type = i_type.get(i).type;
		
		if(type.equals("I"))
			if(Instruction_splitted[0].equals("beq") || Instruction_splitted[0].equals("bne")) // since it is a 1-line mode we dont take branch or jump instructions since they need address or offset calculation
				return ("Interactive mode is designed to handle a single command at a time\nYou can not use branch or jump instructions since they need address calculations\nPlease use Batch mode!!");
			else
				return handle_i_type(Instruction,0);
		
		else if(type.equals("R")) 
			return handle_r_type(Instruction);
		
		else if(type.equals("M"))
			return handle_m_type(Instruction);
		
		else if(type.equals("J")) return ("Interactive mode is designed to handle a single command at a time\nYou can not use branch or jump instructions since they need address calculations\nPlease use Batch mode!!");
		
		else return("There is no such instruction as you typed!!\n please modify the 'Instructions.txt' if you want to use a new instruction.");
	}
	
	public static String handle_m_type(String ins) {//this function handles instructions  that need  memory access like lw or sw
		String[] Instruction_splitted = ins.split("[,() #]+");
		int i;
		int flag1 = 0;
		int flag2 = 0;
		int flag3 = 0;
		String ins_opcode = null;
		String reg1 = null;
		String reg2 = null;
		String offset = "";
		String ins_32b;
		for(i=0;i<m_instructions.size();i++) { // if we find the the instruction in our array we get the opcode of that instruction 
			if(Instruction_splitted[0].equals(m_instructions.get(i).name)) {
				flag1++;
				ins_opcode=m_instructions.get(i).opcode;
			}		
		}
		if(flag1==0) { // if we cant find the instruction we give an error and terminate
			return "There is no such an instruction as you typed!!!";
		}
		
		for(i=0;i<registers.size();i++) { // 
			if(Instruction_splitted[1].equals(registers.get(i).Register)) {// we try to find the register and get its address
				flag2++;
				reg1=registers.get(i).Adress;
				
			}
		}
		if(flag2==0) {
			return "It seems like one of your registers is wrong!!\\n Please check your registers and enter again";// if the register is not found throws that error and terminate the program
		}
		for(i=0;i<registers.size();i++) {
			if(Instruction_splitted[3].equals(registers.get(i).Register)) {// we try to find the register and get its address
				flag3++;
				reg2=registers.get(i).Adress;
				
			}
		}
		if(flag3==0) {
			return "It seems like one of your registers is wrong!!\\n Please check your registers and enter again";// if the register is not found throws that error and terminate the program
		}
		int test =Integer.parseInt(Instruction_splitted[2]);
		if(test>32768||test<-32768)
			return "Your immediate number should be between -32768 and 32768!!!";
		offset = Instruction_splitted[2];
		int number = Integer.parseInt(offset);
		String bin = Integer.toBinaryString(number);
		int x = bin.length();
		for(int a=0;a<16-x;a++) {
			bin = "0" + bin;
		}
		offset = bin;
		if(test<0) {
			offset = TwosComplement(offset);
		}
		
		ins_32b= ins_opcode + reg2 + reg1 + offset ;
		long decimal = Long.parseLong(ins_32b,2);
		String hexa = Long.toHexString(decimal);
		hexa = "0x"+hexa;
		return hexa;
	}

	static String handle_r_type(String ins) {//this function handles R types instructions
		String[] Instruction_splitted = ins.split("[, #]+");
		String ins_32b;
		int i;
		String ins_opcode="";
		String ins_r1="";
		String ins_r2="";
		String ins_imm;
		int flag1=0;
		int flag2=0;
		int flag3=0;
		int flag4=0;
		int flag_shamt=0;
		String ins_shamt = "";
		String ins_func = "";
		String ins_r3="";
		for(i=0;i<r_instructions.size();i++) { // we try to find the instruction and get its opcode and function code
			if(Instruction_splitted[0].equals(r_instructions.get(i).name)) {
				flag1++;
				ins_opcode=r_instructions.get(i).opcode;
				ins_shamt="00000"; // shift amount is 0 by default if the instruction is a shift instruction we change it 
				ins_func=r_instructions.get(i).funccode;	
			}	
		}
		if(flag1==0) {
			return "There is no such an instruction as you typed!!!";// giving error and terminate the program if the instruction is not found
		}
		if(Instruction_splitted[0].equals("sll") || Instruction_splitted[0].equals("srl") ) { // if the instruction is a shifting instruction we handle it here
			int dec = Integer.parseInt(Instruction_splitted[3]);
			String bin = Integer.toBinaryString(dec);
			if(dec<0||dec>32) { // we check the shifting amount if it is in range
				return "You should shift between 0 and 32 bits!!!";
			}
			int x=bin.length();
			for(i=0;i<5-x;i++) {
				bin= "0" + bin;
			}
			
			flag_shamt=1;
			ins_r3="00000"; // since it is a shifting instruction it does not use a 3rd register 
			ins_shamt=bin;
		}
		if(Instruction_splitted[0].equals("jr")){// if the instruction is a jump register instruction we handle it here
			for(i=0;i<registers.size();i++) {// we get the register address here
				if(Instruction_splitted[1].equals(registers.get(i).Register)) {
					ins_r1=registers.get(i).Adress;
				}
			}
			ins_r2="00000"; // in jump register instructions there is no 2nd or 3rd register so we change the addresses to zero
			ins_r3="00000";
			ins_32b= ins_opcode + ins_r2 + ins_r3 +ins_r1+ ins_shamt + ins_func;
			long decimal = Long.parseLong(ins_32b,2);
			String hexStr = Long.toHexString(decimal);
			int x=hexStr.length();
			for(i=0;i<8-x;i++) {
				hexStr="0"+hexStr;
			}
			hexStr = "0x"+hexStr;
			return hexStr;	
		}
		
		for(i=0;i<registers.size();i++) { // we find the register here and get its address 
			if(Instruction_splitted[1].equals(registers.get(i).Register)) {
				flag2++;
				ins_r1=registers.get(i).Adress;
				
			}
		}
		if(flag2==0) {
			return "It seems like one of your registers is wrong!!\n Please check your registers and enter again";// we give and error if we cant find the register 
		}
		
		for(i=0;i<registers.size();i++) {// we find the register here and get its address 
			if(Instruction_splitted[2].equals(registers.get(i).Register)) {
				flag3++;
				ins_r2=registers.get(i).Adress;
				
			}
			
		}
		if(flag3==0) {
			return "It seems like one of your registers is wrong!!\\n Please check your registers and enter again";// we give and error if we cant find the register 
		}
		if(Instruction_splitted[0].equals("move")) {
			ins_r3="00000";
			ins_32b= ins_opcode + ins_r2 + ins_r3 +ins_r1+ ins_shamt + ins_func;
			int decimal = Integer.parseInt(ins_32b,2);
			String hexStr = Integer.toString(decimal,16);
			int x=hexStr.length();
			for(i=0;i<8-x;i++) {
				hexStr="0"+hexStr;
			}
			hexStr = "0x"+hexStr;
			return hexStr;
		}
		
		if(flag_shamt==0) {
			for(i=0;i<registers.size();i++) {// we find the register here and get its address 
				if(Instruction_splitted[3].equals(registers.get(i).Register)) {
					flag4++;
					ins_r3=registers.get(i).Adress;		
				}	
			}
			if(flag4==0) {
				return "It seems like one of your registers is wrong!!\\n Please check your registers and enter again";// we give and error if we cant find the register 
			}
		}
				
		
		ins_32b= ins_opcode + ins_r2 + ins_r3 +ins_r1+ ins_shamt + ins_func;
		//System.out.println("opcode =  "+ins_opcode+"   ins_r1=  "+ins_r1+"   ins_r2="+ins_r2+"   ins_r3= "+ins_r3+"   ins_shamt= "+ins_shamt+" ins_func = "+ins_func);
		
		
		int decimal = Integer.parseInt(ins_32b,2);
		String hexStr = Integer.toString(decimal,16);
		int x=hexStr.length();
		for(i=0;i<8-x;i++) {
			hexStr="0"+hexStr;
		}
		hexStr = "0x"+hexStr;
		return hexStr;
		
	}
	
	 static String TwosComplement(String offset){ //this function find the two's complement of a number
		 	
	        int i;
	        String twos="";
	        
	        for(i=0;i<offset.length();i++) {
	        	if(offset.charAt(i)=='0') {
	        		twos=twos+"1";
	        	}
	        		
	        	
	        	if(offset.charAt(i)=='1') {
	        		twos=twos+"0";
	        		
	        	}
	        }
	        System.out.println(twos);
	    	long l=Long.parseLong(twos);
	    	int c=0;
	    	int t=0;
	    	while(l%2==1) {
	    		t++;
	    		l=l/10;
	    		c++;
	    		if(l%2==0) {
	    			l=l+1;
	    			l=(int) (l*Math.pow(10,c));
	    			break;
	    		}
	    	}
	    	if(t==0)
	    	l=l+1;
	    	
	    	
	    	
	    	twos=Long.toString(l);
	    	int k =twos.length();
	    	for(i=0;i<offset.length()-k;i++) {
				twos = "0" + twos;
			}
	    	
	    	
	    	
	    	return twos;
	    } 
	
	public static void get_table() throws IOException { // this is the function that takes the Instructions and registers as a txt file and fills the array with the relevant information
		
		String filename;
		String sr;
        String line = "";
		Scanner sc = new Scanner(System.in);
 		System.out.println("Enter file path of Table: "); 
 		filename = sc.nextLine();
 		BufferedReader br = new BufferedReader(new FileReader(filename));	
        StringBuilder sb = new StringBuilder();
        System.out.println(line);
        while ((line = br.readLine()) != null) {// everytime we check what the line starts with and find out what kind of information it contains by checking the first part of the line
        	String[] Instruction_splitted = line.split("[,#]+");//then we split the line and get the information like opcode, funccode, Instruction name etc.
        	if(line.startsWith("I,")) {
        		I_Instructions new_ins = new I_Instructions(Instruction_splitted[1],Instruction_splitted[2]);
        		i_instructions.add(new_ins);
        		Instruction_type ins_type = new Instruction_type(Instruction_splitted[1],Instruction_splitted[0]);
        		i_type.add(ins_type);
        	}
        	else if(line.startsWith("M,")) {
        		Memory_Instructions new_ins = new Memory_Instructions(Instruction_splitted[1],Instruction_splitted[2]);
        		m_instructions.add(new_ins);
        		Instruction_type ins_type = new Instruction_type(Instruction_splitted[1],Instruction_splitted[0]);
        		i_type.add(ins_type);
        	}
        	else if(line.startsWith("R,")) {
        		R_Instructions new_ins = new R_Instructions(Instruction_splitted[1],Instruction_splitted[2],Instruction_splitted[3],Instruction_splitted[4]);
        		r_instructions.add(new_ins);
        		Instruction_type ins_type = new Instruction_type(Instruction_splitted[1],Instruction_splitted[0]);
        		i_type.add(ins_type);
        	}
        	else if (line.startsWith("J,")) {
        		J_Instructions new_ins = new J_Instructions(Instruction_splitted[1],Instruction_splitted[2],Instruction_splitted[3]);
        		j_instructions.add(new_ins);
        		Instruction_type ins_type = new Instruction_type(Instruction_splitted[1],Instruction_splitted[0]);
        		i_type.add(ins_type);
        	}
        	else if (line.startsWith("register,")) {
        		Registers new_reg = new Registers(Instruction_splitted[1],Instruction_splitted[2]);
        		registers.add(new_reg);
        	}
        	else continue;
        }
	}

}


	
	
	


