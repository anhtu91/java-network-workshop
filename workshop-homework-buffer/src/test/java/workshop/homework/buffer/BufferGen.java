package workshop.homework.buffer;

import java.util.ArrayList;

public class BufferGen<T> implements IBufferGen<T>{

	private int length = 0;
	private int offset = 0;
	private int number = 0;
	private boolean overwrite = false;
	private ArrayList<T> b = null;
	
	public BufferGen(int length, boolean overwrite) {
		this.length = length;
		this.overwrite = overwrite;
		b = new ArrayList<T>(length);
	}


	@Override
	public void add(T value) {
		//FIFO
		if(!this.overwrite) {
			if(this.offset < this.length) {
				b.add(this.offset, value);
			}else {
				throw new java.lang.IndexOutOfBoundsException();
			}
			this.offset++;
		}else { //Ring Buffer
			
			if(this.offset < this.length) {
				//b[this.offset] = (int) value;
				b.add(this.offset, value);
			}else if(this.offset >= this.length) {
				//Reset offset 
				this.offset = this.offset - this.length;
				b.add(this.offset, value);
			}
			this.offset++;
		}
	}

	@Override
	public T get() {
		
		T return_value = null;
		boolean check_empty_array = false;
		
		//Loop through array to check empty array
		if(b.size() != 0){
			check_empty_array = true;
		}
		
		//FIFO
		if(!this.overwrite){
			//If array is empty/ No element
			if(!check_empty_array) {
				throw new java.lang.NullPointerException();
			}else {
				return_value = b.get(this.offset);
				this.offset++;
			}
		}else{
			//If array is empty/ No element
			if(!check_empty_array) {
				throw new java.lang.NullPointerException();
			}else {		
				
				if(this.offset<this.length) {
					return_value = b.get(this.offset);
				}else if(this.offset >= this.length){
					this.offset = this.offset - this.length;
					return_value = b.get(this.offset);		
				}
				this.offset++;
			}
		}
		
		return return_value;
	}

	@Override
	public int getLength() {
		// TODO Auto-generated method stub
		return this.length;
	}

	@Override
	public int getNumber() {
		this.number = 0;
		//Loop through elements in array list to count
		for(T ele: b){
			if(ele != null){
				this.number++;
			}
		}
		
		return this.number;
	}

	@Override
	public int getOffset() {
		// TODO Auto-generated method stub
		return this.offset;
	}
	
	@Override
	public boolean getOverwrite() {
		// TODO Auto-generated method stub
		return this.overwrite;
	}

	@Override
	public String toString() {
		return "BufferGen [length=" + length + ", offset=" + offset + ", number=" + number + ", overwrite=" + overwrite
				+ ", b=" + b.toString() + "]";
	}

}
