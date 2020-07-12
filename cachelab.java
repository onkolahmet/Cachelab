


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
public class ahmetonkol {

	private static Cache L1DataCache, L1InstructionCache, L2Cache;
	private static String[] ram;
	private static long start = System.nanoTime();
	public static String addressHex;
	public static String instructionName;
	public static String operationSize;
	public static String dataToUse = "";
	public static String traceFileName = "";
	public static String addressBinary;
	public static int addressDecimal;
	public static int addressHextLength;
	public static int addressBinaryLength;
	public static int lengthDiff;
	public static int removeFromAddress;
	public static String tagL1;
	public static String setIndexBinary;
	public static int setIndexL1;
	public static String blockIndex;
	public static int removeFromAddressL2;
	public static String tagL2;
	public static String setIndexL2Binary;
	public static int setIndexL2;
	public static String blockIndexL2;
	public static int missL1I = 0;
	public static int hitL1I = 0;
	public static int evictionsL1I = 0;
	public static int missL1D = 0;
	public static int hitL1D = 0;
	public static int evictionsL1D = 0;
	public static int missL2 = 0;
	public static int hitL2 = 0;
	public static int evictionsL2 = 0;
	public static boolean L1Ihas;
	public static boolean L1Dhas;
	public static boolean L2has;
	public static String dataL1 = "";
	public static String dataL2 = "";
	public static int emptyLine;
	public static int timer = 0;
	public static int lowestTimer = 0;
	public static String[][][] L1I;
	public static String[][][] L1D;
	public static String[][][] L2t;
	public static String Places;
	static int r = 0 ;
	public static void main(String[] args) throws Exception {

		// initilazing Caches with names
		L1DataCache = new Cache("L1D");
		L1InstructionCache = new Cache("L1I");
		L2Cache = new Cache("L2");
		File file = new File("ram.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String st = "";
		while ((st = br.readLine()) != null) {
			ram = (st.split(" "));
		}
		br.close();
		initCache(args, L1DataCache, L1InstructionCache, L2Cache);// function to initilize caches base on given program argumants
																	

		String s = "traces/" + traceFileName;
		startReadingFile(s, L1InstructionCache, L1DataCache, L2Cache);// after initilazing cache begins reading tracefile
																		
		printCache(L1InstructionCache);
		printCache(L1DataCache);
		printCache(L2Cache);
		// prints total miss hit and eviction of each cache
		System.out.println("Caches are written to corresponding text files.\n");
		printTotalHitMissAndEviction(L1InstructionCache);
		printTotalHitMissAndEviction(L1DataCache);
		printTotalHitMissAndEviction(L2Cache);
	}

	private static void initCache(String argv[], Cache L1Data, Cache L1Instruction, Cache L2) {
		// initilazing caches base on given program arguments
		// initilazing cache table with given s e and b values
		for(int i = 0; i < argv.length;i++) {
            if (argv[i].equalsIgnoreCase( "-L2s" ))
                L2.setNumberOfSets((int)Math.pow(2,Integer.parseInt(argv[i+1])));
            else if (argv[i].equalsIgnoreCase("-L2E"))
                L2.setLinePerSet(Integer.parseInt(argv[i + 1]));
            else if (argv[i].equalsIgnoreCase("-L2b"))
                L2.setBlockSize((int)Math.pow(2,Integer.parseInt(argv[i+1])));
            else if (argv[i].equalsIgnoreCase("-L1s")) {
                L1Data.setNumberOfSets((int)Math.pow(2,Integer.parseInt(argv[i+1])));
                L1Instruction.setNumberOfSets((int)Math.pow(2,Integer.parseInt(argv[i+1])));
            }else if (argv[i].equalsIgnoreCase("-L1E")){
                L1Data.setLinePerSet(Integer.parseInt(argv[i+1]));
                L1Instruction.setLinePerSet(Integer.parseInt(argv[i+1]));
            }else if(argv[i].equalsIgnoreCase("-L1b")) {
                L1Instruction.setBlockSize((int)Math.pow(2,Integer.parseInt(argv[i+1])));
                L1Data.setBlockSize((int)Math.pow(2,Integer.parseInt(argv[i+1])));
            }
            else if(argv[i].equalsIgnoreCase("-t")) {
            	traceFileName = argv[i+1] ;
            }
        
		}
		
		L1I = new String[L1Data.getNumberOfSets()][L1Data.getLinePerSet()][4];
		L1D = new String[L1Data.getNumberOfSets()][L1Data.getLinePerSet()][4];
		L2t = new String[L2.getNumberOfSets()][L2.getLinePerSet()][4];
		for (int i = 0; i < L1Data.getNumberOfSets(); i++) {
			for (int j = 0; j < L1Data.getLinePerSet(); j++) {
				for (int k = 0; k < 4; k++) {
					L1I[i][j][k] = "0";
					L1D[i][j][k] = "0";
				}
			}
		}

		for (int i = 0; i < L2.getNumberOfSets(); i++) {
			for (int j = 0; j < L2.getLinePerSet(); j++) {
				for (int k = 0; k < 4; k++) {
					L2t[i][j][k] = "0";
				}
			}
		}
		L1Data.setDataTable(L1D);
		L1Instruction.setDataTable(L1I);
		L2.setDataTable(L2t);
	}

	private static void printCache(Cache cache) throws IOException {
		FileWriter myWriter = new FileWriter(cache.getName());
		System.out.println("--------------------------------" + cache.getName() + "----------------------\n");
		myWriter.write(cache.getName() + "-----------\n");
		if(cache.getName().equals("L2"))
		System.out.println("\t\tTag value   Valid bit\t      Data\t\t Time");
		if(cache.getName().equals("L1I") ||cache.getName().equals("L1D") )
			System.out.println("\t\tTag value  \t   Valid bit\t     Data\t\t Time");
		for (int i = 0; i < cache.getNumberOfSets(); i++) {
			System.out.print("Set:" + i + "----\n");
			myWriter.write("Set:" + i + "----\n");
			for (int j = 0; j < cache.getLinePerSet(); j++) {
				System.out.print("Line:" + j + "\n");
				myWriter.write("Line:" + j + "\n");
				for (int k = 0; k < 4; k++) {
					if (!cache.getDataTable()[i][j][k].equals("0")) {
						System.out.print("\t" + cache.getDataTable()[i][j][k]);
					myWriter.write("\t" + cache.getDataTable()[i][j][k]);
					}	
				}
				System.out.println("\n");
				myWriter.write("\n") ;
			}
		}
		myWriter.close(); 
	}

	private static void startReadingFile(String arg, Cache L1I, Cache L1D, Cache L2) throws Exception {
		// beginning of reading trace file
		File traceFile = new File(arg);
		BufferedReader br2 = new BufferedReader(new FileReader(traceFile));
		String st2 = "";
		while ((st2 = br2.readLine()) != null) {
			System.out.println(st2);
			getLine(st2, L1I, L1D, L2);
			checkCaches(instructionName, operationSize, tagL1, tagL2, setIndexL1, setIndexL2, addressDecimal, L1I, L1D,
					L2);// after each line caches will be checked
			System.out.println("LINE FINISHED -----------------------------------------");
		}
		br2.close();

	}

	public static void checkCaches(String instrucName, String operationSize, String tagL1, String tagL2, int setIndexL1,
			int setIndexL2, int addressDecimal, Cache L1I, Cache L1D, Cache L2) throws Exception {
		if (Integer.parseInt(operationSize) != 0) {
			switch (instrucName.charAt(0)) {
			case 'I':
				Places = "";
				checkDataTable(tagL1, L1InstructionCache, setIndexL1, Integer.parseInt(operationSize), instrucName,
						addressDecimal);
				checkDataTable(tagL2, L2Cache, setIndexL2, Integer.parseInt(operationSize), instrucName,
						addressDecimal);
				if(!Places.equals(""))
				System.out.println("\nPlace in " + Places);
				if(Places.equals(""))
				System.out.println();
				break;
			case 'L':
				Places = "";
				checkDataTable(tagL1, L1DataCache, setIndexL1, Integer.parseInt(operationSize), instrucName,
						addressDecimal);
				checkDataTable(tagL2, L2Cache, setIndexL2, Integer.parseInt(operationSize), instrucName,
						addressDecimal);
				if(!Places.equals(""))
				System.out.println("\nPlace in " + Places);
				if(Places.equals(""))
				System.out.println();
				break;
			case 'S':
				Places = "";
				checkDataTable(tagL1, L1DataCache, setIndexL1, Integer.parseInt(operationSize), instrucName,
						addressDecimal);
				checkDataTable(tagL2, L2Cache, setIndexL2, Integer.parseInt(operationSize), instrucName,
						addressDecimal);
				if(!Places.equals(""))
				System.out.println("\nPlace in " + Places);
				if(Places.equals(""))
				System.out.println();
				r = 0 ;
				break;
			case 'M':
				Places = "";
				checkDataTable(tagL1, L1DataCache, setIndexL1, Integer.parseInt(operationSize), "M", addressDecimal);
				checkDataTable(tagL2, L2Cache, setIndexL2, Integer.parseInt(operationSize), "M", addressDecimal);
				if(!Places.equals(""))
				System.out.println("\nPlace in " + Places);
				if(Places.equals(""))
				System.out.println();
				r = 0 ;
				break;
			}
		}
		if (Integer.parseInt(operationSize) == 0) { 
			System.out.println("We can not do this operation since operation size is 0");
		}
	
	}

	private static void checkDataTable(String tagValue, Cache cache, int setIndexVal, int operationSize, String type,
			int addressDecim) throws Exception {
		// // getting tag bit as binaryform of adress - blockoffset-setbits
		String tag = tagValue;
		String data = "";
		int address = (int) Math.floor((double) addressDecim / 8);
		int e = 0;
		if(ram.length < address) {
			System.out.println("RAM does not contain valid information.");
		return ;
		}
		for (int s = 0; s < cache.getBlockSize();) {
			data += ram[address + e];
			s += ram[address].length() - 1;
			e++;
		}
		// set index as part between tagbit and blockoffset
		int setIndex = setIndexVal;
			for (int i = 0; i < cache.getLinePerSet(); i++) {
				if ((cache.getDataTable()[setIndex][i][0].equalsIgnoreCase(tag)
						&& cache.getDataTable()[setIndex][i][1].equalsIgnoreCase("1") && cache.getDataTable()[setIndex][i][2].equals(data) )) {// if tag bit are equal and valid bit  is 1 its hit
					System.out.print(cache.getName() + " hit ");
					cache.setHits(cache.getHits() + 1);
					if (type.equals("S")) {// if instruction store we follow write-through rule
						String s = dataToUse + cache.getDataTable()[setIndex][i][2].substring(dataToUse.length());
						cache.getDataTable()[setIndex][i][2] = s;// updating value in case of this store comes from Modify instruction
						if (cache.getNumberOfSets() != 1) {
							if (Places.equals("")) {
								Places += cache.getName() + " set " + setIndex;
							}
							if (!Places.equals("")) {
								Places += ", " + cache.getName() + " set " + setIndex;
							}

						}
						if (cache.getNumberOfSets() == 1) {
							if (!Places.equals("")) {
								Places += "," + cache.getName();
							}
							if (Places.equals("")) {
								Places += cache.getName();
							}

						}			
						writeRam(addressDecim, dataToUse, operationSize, cache);
						return;
					}
					loadTocache(cache, tag, setIndex, addressDecim, operationSize);
					return;
				}
			}
		if(!type.equals("S")) {
		System.out.print(cache.getName() + " miss ");
		cache.setMiss(cache.getMiss() + 1);
		// for other instructions we load data from ram to cache
		loadTocache(cache, tag, setIndex, addressDecim, operationSize);
			if(type.contains("S")) {
			writeRam(addressDecim, dataToUse, operationSize, cache);
			}
		}
		if (type.equals("S")) {// if instruction store we follow no write allocate rule
			writeRam(addressDecim, dataToUse, operationSize, cache);
			return;
		}
		
		
	}

	private static void loadTocache(Cache cache, String tag, int setIndex, int adressVal, int size) {
		String data = "";
		int address = (int) Math.floor((double) adressVal / 8);
		int e = 0;
		for (int s = 0; s < cache.getBlockSize();) {
			data += ram[address + e];
			s += ram[address].length() - 1;
			e++;
		}

		for (int j = 0; j <= cache.getNumberOfSets(); j++) {
			for (int i = 0; i < cache.getLinePerSet(); i++) {
				if (setIndex == j && cache.getDataTable()[j][i][1].equalsIgnoreCase("0")) {// checking is there any empty line on given set
																			
					timer++;
					cache.getDataTable()[j][i][0] = tag;// sets tag bits
					cache.getDataTable()[j][i][1] = "1";// sets valid bit
					cache.getDataTable()[j][i][2] = data;// sets data
					cache.getDataTable()[j][i][3] = String.valueOf(timer);// insertion time to follow first in first out
					if (cache.getNumberOfSets() != 1) {
						if (Places.equals("")) {
							Places += cache.getName() + " set " + j;
						}
						if (!Places.equals("")) {
							Places += ", " + cache.getName() + " set " + j;
						}

					}
					if (cache.getNumberOfSets() == 1) {
						if (!Places.equals("")) {
							Places += "," + cache.getName();
						}
						if (Places.equals("")) {
							Places += cache.getName();
						}

					}
					return;
				}
			}
		}
		// if given set is full we need eviction
		System.out.print(cache.getName() + " eviction ");
		cache.setEviction(cache.getEviction() + 1);
		// here finding first inserted element among all element
		int minLineIndex = 0;
		String min = (cache.getDataTable()[setIndex][0][3]), temp;
		for (int i = 0; i < cache.getLinePerSet(); i++) {
				temp = (cache.getDataTable()[setIndex][i][3]);
				if (Integer.parseInt(temp) < Integer.parseInt(min)) {
					min = temp;
					minLineIndex = i;
				}
		}
		timer++;
		// after finding first inserted element we do eviction and set new values to  table
		
		cache.getDataTable()[setIndex][minLineIndex][0] = tag;
		cache.getDataTable()[setIndex][minLineIndex][1] = "1";
		data = dataToUse + data.substring(dataToUse.length());
		cache.getDataTable()[setIndex][minLineIndex][2] = data;
		cache.getDataTable()[setIndex][minLineIndex][3] = String.valueOf(timer);

	}

	private static void writeRam(int adressBinary, String data, int operationSize, Cache cache) {
		int adressVal = (int) Math.floor((double) adressBinary / 8);// calculating adress value as integer from binary array
		r++ ;															
		operationSize *= 2;
		int j = 0;
		for (int i = 0; i < data.length();) {
			if (i < cache.getBlockSize() * 2) {

				if (i + 3 > data.length()) {
					String n = data.substring(i);
					ram[adressVal + j] = n;
				}
				if (i + 3 <= data.length()) {
					String s = data.substring(i, i + 2);
					ram[adressVal + j] = s;
				}
				i += 2;
				j++;
			}
		}
		if(r == 1) {
			if (!Places.equals("")) {
				Places += ", RAM" ; 
			}	
			if (Places.equals("")) {
				Places += " RAM" ; 
			}
			
		
		}		
	}

	public static void getLine(String st, Cache L1I, Cache L1D, Cache L2) {
		dataToUse = "";
		instructionName = st.split(" ")[0];
		addressHex = st.split(" ")[1];
		operationSize = st.split(" ")[2];
		if (operationSize.length() > 1) {
			operationSize = removeLastChar(operationSize);
		}
		if (st.split(" ")[0].equals("M") || st.split(" ")[0].equals("S")) {
			dataToUse = st.split(" ")[3];
		}

		addressHex = removeLastChar(addressHex);
		addressHextLength = addressHex.length();
		addressBinary = new BigInteger(addressHex, 16).toString(2);
		addressDecimal = Integer.parseInt(addressHex, 16);
		addressBinaryLength = addressBinary.length();
		lengthDiff = addressHextLength * 4 - addressBinaryLength;
		if (lengthDiff != 0) {
			String zeros = "";
			for (int i = 0; i < lengthDiff; i++) {
				zeros = zeros + "0";
			}
			addressBinary = zeros + addressBinary;
		}

		blockIndex = addressBinary.substring(addressBinary.length() - L1I.getBlockSize());
		if (L1I.getNumberOfSets() == 1) {
			setIndexBinary = "0";
			setIndexL1 = 0;
			tagL1 = addressBinary;
			removeFromAddress =  L1I.getBlockSize();
			for (int j = 0; j < removeFromAddress; j++) {
				tagL1 = removeLastChar(tagL1);
			}
			
		} else {
			setIndexBinary = addressBinary.substring(
					addressBinary.length() - L1I.getBlockSize() - L1I.getNumberOfSets(),
					addressBinary.length() - L1I.getBlockSize());
			setIndexL1 = setIndexBinary.length() - 1;
			tagL1 = addressBinary;
			removeFromAddress = L1I.getNumberOfSets() + L1I.getBlockSize();
			for (int k = 0; k < removeFromAddress; k++) {
				tagL1 = removeLastChar(tagL1);
			}
			
		}
	
		blockIndexL2 = addressBinary.substring(addressBinary.length() - L2.getBlockSize());
		if (L2.getNumberOfSets() == 1) {
			setIndexL2Binary = "0";
			setIndexL2 = 0;
			tagL2 = addressBinary;
			removeFromAddressL2 =  L2.getBlockSize();
			for (int j = 0; j < removeFromAddressL2; j++) {
				tagL2 = removeLastChar(tagL2);
			}
			
		} 
		else {
			setIndexL2Binary = addressBinary.substring(
					addressBinary.length() - L2.getBlockSize() - L2.getNumberOfSets(),
					addressBinary.length() - L2.getBlockSize()-1);
			setIndexL2 = setIndexL2Binary.length() - 1;
			tagL2 = addressBinary;
			removeFromAddressL2 = L2.getNumberOfSets() + L2.getBlockSize();
			for (int j = 0; j < removeFromAddressL2; j++) {
				tagL2 = removeLastChar(tagL2);
			}
		}
	}

	public static String removeLastChar(String s) {
		return (s == null || s.length() == 0) ? null : (s.substring(0, s.length() - 1));
	}

	public static void printTotalHitMissAndEviction(Cache cache) {
		System.out.println(cache.getName() + "----\n" + "Total Hits: " + cache.getHits() + "\nTotal Misses:"
				+ cache.getMiss() + "\nTotal Evictions:" + cache.getEviction() + "\n\n");
	}

	public static String[] getRam() {
		return ram;
	}

	public static void setRam(String[] ram) {
		ahmetonkol.ram = ram;
	}

	public static long getStart() {
		return start;
	}

	public static void setStart(long start) {
		ahmetonkol.start = start;
	}

	private static class Cache {
		private String name;
		private String[][][] dataTable;
		private int hits;
		private int miss;
		private int eviction;
		private int numberOfSets;
		private int linePerSet;
		private int blockSize;

		public Cache(String name) {
			this.setName(name);
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String[][][] getDataTable() {
			return dataTable;
		}

		public void setDataTable(String[][][] dataTable) {
			this.dataTable = dataTable;
		}

		public int getHits() {
			return hits;
		}

		public void setHits(int hits) {
			this.hits = hits;
		}

		public int getMiss() {
			return miss;
		}

		public void setMiss(int miss) {
			this.miss = miss;
		}

		public int getEviction() {
			return eviction;
		}

		public void setEviction(int eviction) {
			this.eviction = eviction;
		}

		public int getNumberOfSets() {
			return numberOfSets;
		}

		public void setNumberOfSets(int numberOfSets) {
			this.numberOfSets = numberOfSets;
		}

		public int getLinePerSet() {
			return linePerSet;
		}

		public void setLinePerSet(int linePerSet) {
			this.linePerSet = linePerSet;
		}

		public int getBlockSize() {
			return blockSize;
		}

		public void setBlockSize(int blockSize) {
			this.blockSize = blockSize;
		}
	}
}

