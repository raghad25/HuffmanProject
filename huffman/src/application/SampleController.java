package application;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;

import java.util.PriorityQueue;
import java.util.Stack;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SampleController {

	String inputName;
	String outputPathC;
	String outputPathD;

    @FXML
    private Label lcBtn;
	File inFile;
	File outFile;
	int buffer;
	////// bufferedSteam is used to write data into .huf file 
	BufferedInputStream bufferin1;
	BufferedInputStream bufferin2;
	BufferedOutputStream bufferout1;
	BufferedOutputStream bufferout2;
	ArrayList<Boolean>[] aCoBool;// 2
	String[] ar;
	int chcount;

	int allChars = 256;

	int[] countArr;

	int numOfChars; // in the inFile

	String header;
	String statics;
	int n;
	String headerH;
	int uniqueChars;
	ArrayList<Boolean> outBitStreem;

	@FXML
	TextArea staticsArea;

	@FXML
	TableView<DataTable> tableview;
	@FXML
	TableColumn<DataTable, String> c1;

	@FXML
	TableColumn<DataTable, String> c2;
	@FXML
	TableColumn<DataTable, Integer> c3;

	@FXML

	void readFile(ActionEvent event) {

		FileChooser file = new FileChooser();
		file.setTitle("Choose a file to De/COMPRESS");
		inFile = file.showOpenDialog(new Stage());

		if (inFile == null) {
			Alert alert=new Alert(AlertType.ERROR);
			
			alert.setContentText("Login Failed,please enter File");
			alert.show();
			
		} else {
			inputName = inFile.getName();

		}

	}

	
	//______________________Compress_______________________
	@FXML
	void compressF(ActionEvent event) throws IOException {

		outputPathC = inFile.getPath().substring(0, inFile.getPath().lastIndexOf('.')) + ".huff";
		Alert alert2=new Alert(AlertType.INFORMATION);
		
		alert2.setContentText("Compress Done. ");
		alert2.show();
	

		
		if (inputName.substring(inputName.indexOf(".") + 1).equals("huff")) {

			
			Alert alert=new Alert(AlertType.ERROR);
			
			alert.setContentText("Opsss,it impossible to compress a file with a .huff exe");
			alert.show();
		
			return;
		}

		buffer = 0;

		// read input file byte by byte ..Scanner
		bufferin1 = new BufferedInputStream(new FileInputStream(inFile));
		// write the file after the compression process ...PrintWriter
		bufferout1 = new BufferedOutputStream(new FileOutputStream(outputPathC));

		// initialize the char of repeat ;
		countArr = new int[allChars];

		numOfChars = 0;

		/**
		 * Chars Counting :-
		 */

		/// find the frequency of each character
		while (bufferin1.available() != 0) {
			byte[] ar = new byte[1];

			bufferin1.read(ar); // return the next 8bits
////set byte value which is from (-128 - 127 )
			// xff 11111111
		/*	The value 0xff is equivalent to 255 in unsigned decimal, -127 in signed decimal, and 11111111 in binary.
			So, if we define an int variable with a value of 0xff, since Java represents integer numbers using 32 bits, the value of 0xff is 255*/
			countArr[ar[0] & 0xFF]++;// b bits --128 choice , bitwise and operation

			numOfChars++; // increase the total number of characters in the file
			if (countArr[ar[0] & 0xFF] == 1) {
				uniqueChars++;
			}

		}

		bufferin1.close();


		
		//___________ Build tree and Huffman ________________

		Tree root = makeTree(countArr);

		writeString(inputName.substring(inputName.indexOf(".") + 1));
		
		writeInt(uniqueChars);
		
		writeInt(numOfChars);
		
		String x = printInorder(root);
		for (int k = 0; k < x.length(); k++) {
			
			if (x.charAt(k) == '1') {
				writeBit(true);
				chcount++;
				k++;

				char q = x.charAt(k);
				chcount += 8;

				byte b = (byte) q;
				//In particular, %8s means that this format will transform the corresponding argument into a string (using .toString() method),
				//and if the result is fewer than eight characters wide,
				//it will be padded to eight characters with spaces.
				String stra = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');


				for (int y = 0; y < stra.length(); y++) {
					if (stra.charAt(y) == '1') {
						writeBit(true);
					} else if (stra.charAt(y) == '0') {
						writeBit(false);

					}

				}
			} else if (x.charAt(k) == '0') {
				writeBit(false);
				chcount++;

			}
		}

		boolean c1 = checkBit(chcount);
		
	
		/**
		 * construction of the huffman code ar:array of string...sized 256 s:huff code
		 */

		ar = new String[allChars];
		String s = "";

		// root >> counting arr ,ar >> array of huff codes ,, s:huffman code
		makeHuffCodes(root, ar, s);

		aCoBool = new ArrayList[allChars];
		stToBool(ar, aCoBool);
		header=("<<<<<<<<<<<More Detailes>>>>>>>>>>>>"+"\n");
		header += ("File Extention:" + inputName.substring(inputName.indexOf(".") + 1) + "\n");
		header += ("Exisited chars in this file=" + uniqueChars + "\n");
		header += ("All chars = " + numOfChars + "\n");
		header += ("Header key:" + x + "\n");
		
	
		int headLength;
		if (c1) {

			headLength = (((chcount + (8 - chcount % 8)) / 8) + 8 + inputName.substring(inputName.indexOf(".") + 1).length());

		} else {

			headLength = (((chcount) / 8) + 8 + inputName.substring(inputName.indexOf(".") + 1).length());
		}

		outBitStreem = new ArrayList<Boolean>();

		/// writing the content of the file by huff codes
		bufferin1 = new BufferedInputStream(new FileInputStream(inFile));
		while (bufferin1.available() != 0) {
			byte[] nxt = new byte[1];
			bufferin1.read(nxt);
			for (int i = 0; i < aCoBool[(nxt[0] & 0xFF)].size(); i++) {
				outBitStreem.add(aCoBool[(nxt[0] & 0xFF)].get(i));
			}
		}

		for (int i = 0; i < outBitStreem.size(); i++) {
			writeBit(outBitStreem.get(i));

		}

		boolean v = checkBit(outBitStreem.size());
		int outDataSize;
		double ra;
		if (v) {
			outDataSize = (outBitStreem.size() + (8 - outBitStreem.size() % 8)) / 8;

			ra = (headLength + outDataSize) * 100 / numOfChars;
			
			statics = "Bytes after compression:" + headLength + "\nSize of the output data:" + outDataSize + "\n ";
			statics += "compression ratio:" + ra + "%  \n";
		} else {
			outDataSize = (outBitStreem.size()) / 8;
			//number of byte after compress/number of bit befor compress

			ra = (headLength + outDataSize) * 100 / numOfChars;
			//((int)(orignallFileSize/(double)compressFileSize))*100+ 
			statics = "Header length: " + headLength + "\n Size of the output data:" + outDataSize + "\n ";
			statics +="Compression ratio:" + ra + "%  \n";

		}
		bufferout1.flush();
		bufferin1.close();
		bufferout1.close();
		
	}

	// check bit to make it 8*x -x is an int -

	private boolean checkBit(int chcount2) throws IOException {

		if (chcount2 % 8 == 0) {
			return false;
		} else {
			int j = (8 - (chcount2 % 8));
		
			for (int k = j; k > 0; k--) {
				writeBit(false);
				
			}
			return true;
		}

	}

	// Preorder traversal ..
	String printInorder(Tree node) throws IOException {

		Stack<Tree> stk = new Stack<>();
		stk.push(node);
		headerH = "";

		Tree temp;
		while (!stk.isEmpty()) {
			temp = stk.pop();
			if (temp.isLeaf()) {

				headerH += "1";
				headerH += temp.getC();

			} else {

				headerH += "0";
			}

			if (temp.getRight() != null) {
				stk.push(temp.getRight());
			}
			// if code has left, then push to the stack
			if (temp.getLeft() != null) {
				stk.push(temp.getLeft());
			}
		}

		return headerH;
	}

	@FXML
	void writeStatics(ActionEvent event) {
		try {
		
			try {
			

				c1.setCellValueFactory(new PropertyValueFactory<DataTable, String>("c1"));
				c3.setCellValueFactory(new PropertyValueFactory<DataTable, Integer>("c3"));
				c2.setCellValueFactory(new PropertyValueFactory<DataTable, String>("c2"));
				ObservableList<DataTable> dataTable = FXCollections.observableArrayList();

				for (int i = 0; i < allChars; i++) {
					if (countArr[i] != 0)

						dataTable.add(new DataTable(Character.toString(((char) (i & 0xFF))), countArr[i], ar[i]));
				}

				tableview.setItems(dataTable);
			} catch (Exception e) {

			}
		} catch (Exception e1) {

		}
	}
	@FXML
    void headerOnAction(ActionEvent event) {
		staticsArea.appendText("File name:" + inputName + "\n");
		staticsArea.appendText("File size:" + numOfChars + "\n");
		staticsArea.appendText(statics);
		staticsArea.appendText(header);

    }
	@FXML
	void writeHeader(ActionEvent event) {

		staticsArea.appendText(header);
	}

	/**
	 * huffman  code by recursion:
	 */

	private void makeHuffCodes(Tree root, String[] ar, String s) {

		if (!root.isLeaf()) {

			if (root.left != null) {
				makeHuffCodes(root.left, ar, s + '0');
			}
			makeHuffCodes(root.right, ar, s + '1');

		} else {
			ar[root.getC()] = s;
		}

	}
//build tree
	private Tree makeTree(int[] countArr2) {

		PriorityQueue<Tree> pq = new PriorityQueue<Tree>();
		for (char i = 0; i < allChars; i++) {
			if (countArr2[i] > 0)
			//	method is used to insert a particular element into the Priority Queue
				pq.offer(new Tree(i, countArr2[i]));
			// construct group of nodes
		}

		if (pq.size() == 1) {

			// if there is only one char repeating in the file
			Tree right = pq.poll();

			Tree parent = new Tree('\0', right.getFreq(), new Tree('\0'), right);
			pq.offer(parent);

		}

		// construct the tree using the  pq -- >
		/**
		 * choose 2 mins and merge them ... repeat
		 */
		//remove two nodes with lowest frequency
	//	combine into a single node
		//put that node back in the PQ .
		while (pq.size() > 1) {

			Tree left = pq.poll();
			Tree right = pq.poll();

			Tree parent = new Tree('\0', left.getFreq() + right.getFreq(), left, right);

			pq.offer(parent);

		}

		return pq.poll(); // return the root ;
		
	}

	public static void stToBool(String[] c, ArrayList<Boolean>[] Bool) {
		for (int i = 0; i < c.length; i++) {
			if (c[i] != null) {
				Bool[i] = new ArrayList<Boolean>();
				char[] ch = c[i].toCharArray();
				for (int j = 0; j < ch.length; j++) {
					if (ch[j] == '1')
					Bool[i].add(true);
					else
						Bool[i].add(false);
				}
			}
		}
	}

	//_______________________________Decompress
	@FXML
	void deComp(ActionEvent event) throws IOException {

		bufferin2 = new BufferedInputStream(new FileInputStream(inFile));

		if (!inputName.substring(inputName.indexOf(".") + 1).equals("huff")) {
            Alert alert=new Alert(AlertType.ERROR);
			alert.setContentText("Opsssssss,it is impossible to decompress a file with not .huf exe");
			alert.show();
			
			return;
		}

		outputPathD = inFile.getPath().substring(0, inFile.getPath().lastIndexOf('.')) + ".";
		
		byte[] bytes = new byte[4];
		byte[] finalDD = new byte[1];
		boolean bool = true;
		
		int qq = 0;
		Tree ratoie = null;
		int bitInHuff = 0;
		int bytoe = 0;
		boolean x = false;
		boolean headaro = true;
		boolean path = true;
		boolean nofch = true;
		String hexd = "";
		int numOfOriginalChar = 0;
		String kayto = "";
		String kayto2 = "";

		int numberOfCharaters = 0;

		while (bufferin2.available() != 0) {

			if (path) {
				outputPathD += (char) bufferin2.read();
				outputPathD += (char) bufferin2.read();
				outputPathD += (char) bufferin2.read();
				bufferout2 = new BufferedOutputStream(new FileOutputStream(outputPathD));
//C:\Users\Acer\AppData\Roaming\Microsoft\Windows\Network Shortcuts\Raghhhhh.txt
		System.out.println("outputPathD>>>>>" + outputPathD);
				path = false;
			}

			if (bool) {

				bufferin2.read(bytes);
				int e = readInt(bytes);
				System.out.println(e);
				qq = e;

				bitInHuff = qq * 10 - 1;

				System.out.println("bitInHuff>>>>>>" + bitInHuff); // 29

				x = findClosestByte(bitInHuff);
				
				if (x) {
					bytoe = (bitInHuff + (8 - bitInHuff % 8));
				} else {
					bytoe = bitInHuff;

				}

				bool = false;

			}

			if (nofch) {

				bufferin2.read(bytes);
				numberOfCharaters = readInt(bytes);

				System.out.println("numberOfCharaters>>>" + numberOfCharaters);

				nofch = false;

			}

			else {

				if (headaro) {
					byte[] ae = new byte[1];

					for (int i = 0; i < bytoe / 8; i++) {

						bufferin2.read(ae);

						hexd += String.format("%8s", Integer.toBinaryString(ae[0] & 0xFF)).replace(' ', '0');
						 System.out.println(i + " : " + hexd);

					}

		System.out.println("step4>>>>>>>>>>>>>>>>>>>>" + hexd);

					numOfOriginalChar = hexd.length() - bitInHuff;

				System.out.println("fake=" + numOfOriginalChar);
					System.out.println("hexd" + hexd);//
					ratoie = reBuildTree(hexd, numOfOriginalChar);

					headaro = false;

				}

				bufferin2.read(finalDD);

				kayto += String.format("%8s", Integer.toBinaryString(finalDD[0] & 0xFF)).replace(' ', '0');

				kayto2 = kayto;
				Tree cu = ratoie;

				for (int j = 0; j < kayto.length() && numberOfCharaters != 0; j++) {

					if (kayto.charAt(j) == '0') {

						cu = cu.getLeft();
					} else {
						cu = cu.getRight();

					}

					// reached leaf node
					if (cu.left == null && cu.right == null) {

						bufferout2.write(cu.getC());
						numberOfCharaters--;

						if (j + 1 <= kayto.length()) {
							kayto2 = kayto.substring(j + 1);
						}
						cu = ratoie;

					}

				}

				if (kayto2 != null) {
					kayto = kayto2;

				}
			}

		}
		
		  Alert alert=new Alert(AlertType.CONFIRMATION);
			alert.setContentText("Done for Decompress");
			alert.show();

		bufferout2.flush();
		bufferin2.close();
		bufferout2.close();

	}
//re Build tree for decompreess
	private Tree reBuildTree(String hexen, int x) throws IOException {

		Tree root = new Tree('\0');

		String hekodbufferout2 = "";

		for (int i = 0; i < hexen.length() - x;) {

			if (hexen.charAt(i) == '0') {
				hekodbufferout2 += "0";
				i++;

			} else {

				hekodbufferout2 += "1";

				i++;

				if (i + 8 <= hexen.length() - x) {
					int chcount = Integer.parseInt(hexen.substring(i, i + 8), 2);

					hekodbufferout2 += (char) (chcount & 0xFF);

					i = i + 8;
				}

			}
		}

		Stack<Tree> stack = new Stack<>();
		stack.push(root);

		for (int i = 0; i < hekodbufferout2.length(); i++) {
			Tree curr = new Tree('\0');

			// pop the stack and put in current
			if (!stack.isEmpty()) {
				curr = stack.pop();
			}

			// if bit is 0 then build a subtree and push right and left of it to stack
			if (hekodbufferout2.charAt(i) == '0') {
				curr.setRight(new Tree('\0'));

				curr.setLeft(new Tree('\0'));

				stack.push(curr.getRight());

				stack.push(curr.getLeft());
			} else {
				if (i + 1 <= hekodbufferout2.length())
					curr.setC(hekodbufferout2.charAt(i + 1));
				i++;

			}
		}

		// put the root as class global huffman tree root

		// inOrder(root) ;
		return root;

	}

	protected int readInt(byte[] bytes) {
		if (bytes != null) {
			//convert to int 
			int value = 0;
			value += (bytes[3] & 0x000000FF) << 24;//data is a 32 bit number represented as 0x12345678 (each number is 4 bits in hex) Data & 0x000000FF means keep only the last 8 bits
			value += (bytes[2] & 0x000000FF) << 16;
			value += (bytes[1] & 0x000000FF) << 8;
			value += (bytes[0] & 0x000000FF);
			return value;
		}
		return 0;
	}

	private boolean findClosestByte(int bitInHuff) {

		if (bitInHuff % 8 == 0)
			return false;
		else {
			return true;
		}

	}

	//

	public void writeString(String str) throws IOException {
		for (int i = 0; i < str.length(); i++)
			bufferout1.write((byte) str.charAt(i));

	}

	public void writeInt(int ii) throws IOException {
		/*
		 * bufferedSteam the output buffer steam 
		 * output[] an array will contain 4 bytes of huffman code, when its full will be written on output file
		 * outputByte: string contain huffman code, it used to make spliting bits easier 
		 * */
		
		for (int i = 0; i < 4; i++) { // L>8
			bufferout1.write((byte) ii);
			ii >>= 8;
		}
	}

	public void writeByte(byte b) throws IOException {
		bufferout1.write(b);
	}

	public void writeBit(boolean bit) throws IOException {
		// add bit to buffer
		buffer <<= 1;
		if (bit)
			buffer |= 1;

		// if buffer is full (8 bits), write out as a single byte
		n++;
		if (n == 8) {
			bufferout1.write((byte) buffer);
			buffer = 0;
			n = 0;
		}
	}

	void inOrder(Tree node) {
		if (node == null) {
			return;
		}

	System.out.println(node.getC() + "ff");

		inOrder(node.left);
		inOrder(node.right);
	}
	
	@FXML
	// method close the user interface
	void cancelBtnAction(ActionEvent event) {

		System.exit(1);

	}

}