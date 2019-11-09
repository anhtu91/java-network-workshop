package workshop.homework.buffer;

import java.util.Arrays;

public class Buffer implements IBuffer{
	
	//private final int MIN_LENGTH = 10;
	//private final int MAX_LENGTH = 100;
	
	private int length = 0;
	private int offset = 0;
	private int number = 0;
	private boolean overwrite = false;
	private int [] b = null;
	
	//Constructor
	public Buffer(int length, boolean overwrite) {
		this.length = length; 
		this.overwrite = overwrite;
		b =  new int[length];
	}
	
	
	@Override
	public void add(int value) {
		//FIFO 
		if(!this.overwrite) {
			if(this.offset < this.length) {
				b[this.offset] = value;
			}else {
				throw new java.lang.IndexOutOfBoundsException();
			}
			this.offset++;
		}else { //Ring Buffer
			
			if(this.offset < this.length) {
				b[this.offset] = value;
			}else if(this.offset >= this.length) {
				//Reset offset 
				this.offset = this.offset - this.length;
				b[this.offset] = value;
			}
			this.offset++;
		}
	}

	@Override
	public int get() {
		
		int return_value = 0;
		boolean check_empty_array = false;
		
		//Loop through array to check empty array
		for(int i=0;i<b.length;i++) {
			if(b[i] == 0 && i != 1) {
				check_empty_array = true;
			}
		}
		
		//FIFO
		if(!this.overwrite) {
			
			//If array is empty/ No element
			if(check_empty_array) {
				throw new java.lang.NullPointerException();
			}else {
				return_value = b[this.offset];			
				this.offset++;
			}
		}else { //Ring Buffer
			//If array is empty/ No element
			if(check_empty_array) {
				throw new java.lang.NullPointerException();
			}else {		
				
				if(this.offset<this.length) {
					return_value = b[this.offset];
				}else if(this.offset >= this.length){
					this.offset = this.offset - this.length;
					return_value = b[this.offset];		
				}
				this.offset++;
			}
		}
		
		return return_value;
	}
	
	//Getter functions
	@Override
	public int getLength() {
		return this.length;
	}

	public int getNumber() {
		number = b[this.offset]; //What is number here??? Don't understand
		return this.number;
	}

	@Override
	public int getOffset() {
		return this.offset;
	}

	@Override
	public boolean getOverwrite() {
		return this.overwrite;
	}
	
	@Override
	public String toString() {
		return "Buffer [length=" + length + ", overwrite=" + overwrite + ", arr=" + Arrays.toString(b) + "]";
	}

}
