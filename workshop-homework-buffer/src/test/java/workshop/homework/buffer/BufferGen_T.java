package workshop.homework.buffer;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BufferGen_T {
	private static final Logger logger = LogManager.getFormatterLogger(Buffer_T.class);
	
	/*
	 * Test case FIFO
	 * array length = 10, overwrite = false
	 * put 10 element in FIFO
	 * 
	 */
	
	public static void testCaseOneFIFO() {
		/** Defines a buffer */
		BufferGen<Integer> b = null;

		try {
			/** Creates a buffer with 10 elements */
			b = new BufferGen<Integer>(10, false);

			/** Trace content */
			logger.info(String.format("Starting the test case 1 - overwrite '%s'\n", b.getOverwrite()));

			/** Trace content */
			logger.info(String.format("CASE 1: Writes 10 elements into this '%s'\n", b.toString()));

			/** Writes 10 elements */
			for (int i = 0; i < 10; i++) {
				b.add(i);

				/** Trace content */
				logger.info(String.format("Content of '%s'", b.toString()));
			}
		} catch (Throwable T) {
			/** Trace error */
			logger.catching(T);
		}
	}

	/*
	 * Test case FIFO 
	 * array length = 10, overwrite = false
	 * put 11 element in FIFO => IndexOutOfBoundsExeption
	 * 
	 */
	
	public static void testCaseTwoFIFO() {
		/** Defines a buffer */
		BufferGen<Integer> b = null;

		try {
			/** Creates a buffer with 10 elements */
			b = new BufferGen<Integer>(10, false);

			/** Trace content */
			logger.info(String.format("Starting the test case 2 - overwrite '%s'\n", b.getOverwrite()));

			/** Trace content */
			logger.info(String.format("CASE 2: Writes 11 elements into this '%s'\n", b.toString()));

			/** Writes 11 elements */
			for (int i = 0; i < 11; i++) {
				b.add(i);

				/** Trace content */
				logger.info(String.format("Content of '%s'", b.toString()));
			}
		} catch (Throwable T) {
			/** Trace error */
			logger.catching(T);
		}

	}
	
	
	/*
	 * Test case FIFO
	 * array length = 10, overwrite = false
	 * FIFO is empty => get function return NullPointerException
	 * 
	 */
	
	public static void testCaseThreeFIFO() {
		BufferGen<Integer> b = null;
		
		try {
			/*Create a buffer with 10 elements*/
			b = new BufferGen<Integer>(10, false);
			
			/** Trace content */
			logger.info(String.format("Starting the test case 3 - overwrite '%s'\n", b.getOverwrite()));

			/** Trace content */
			logger.info(String.format("CASE 3: Writes 10 elements into this '%s'\n", b.toString()));
			
			for(int i = 0;i<10;i++) {
				b.get(); //Buffer is empty => Expect return NullPointerException
			}
			
		}catch(Throwable T) {
			logger.catching(T);
		}
				
	}
	
	/*
	 * Test case Ring Buffer
	 * array length = 10, overwrite = true
	 * put 15 elements in buffer => offset = 5 
	 * 
	 */
	
	public static void testCaseOneRingBuffer() {
		BufferGen<Integer> b = null;
		
		try {
			b = new BufferGen<Integer>(10, true);
			logger.info(String.format("Starting the test case 1 - overwrite '%s'\n", b.getOverwrite()));
			logger.info(String.format("CASE 1: Writes 15 elements into this '%s'\n", b.toString()));
			
			for(int i=0;i<15;i++) {
				b.add(i);
				logger.info(String.format("Content of '%s'", b.toString()));
			}
			
		}catch(Throwable T) {
			logger.catching(T);
		}finally {
			logger.info(String.format("Offset '%s'", b.getOffset()));
			logger.info(String.format("Number '%s'", b.getNumber()));
		}
	}
	
	
	/*
	 * Test case Ring Buffer
	 * array length = 10, overwrite = true
	 * put 12 elements in buffer 
	 * read 9 elements
	 * 
	 */
	
	public static void testCaseTwoRingBuffer() {
		BufferGen<Integer> b = null;
		
		//Put 12 elements in buffer
		try {
			b = new BufferGen<Integer>(10, true);
			logger.info(String.format("Starting the test case 2 - overwrite '%s'\n", b.getOverwrite()));
			logger.info(String.format("CASE 2: Writes 12 elements into this '%s'\n", b.toString()));
			
			for(int i=0;i<12;i++) {
				b.add(i);
				logger.info(String.format("Content of '%s'", b.toString()));
			}
			
		}catch(Throwable T) {
			logger.catching(T);
		}finally {
			logger.info(String.format("Offset '%s'", b.getOffset()));
			logger.info(String.format("Number '%s'", b.getNumber()));
		}
		
		System.out.println();
		
		//Read 9 elements
		try {
			logger.info(String.format("Read 9 elements in buffer '%s'", b.toString()));
			
			for(int i=0;i<9;i++) {
				int ret = b.get();
				
				logger.info(String.format("==> get(Counter - '%d') value '%d' with offset '%d', Content of '%s'", i,
						ret, b.getOffset() - 1, b.toString()));
			}
			
		}catch(Throwable T) {
			logger.catching(T);
		}finally {
			logger.info(String.format("Offset '%s'", b.getOffset()));
			logger.info(String.format("Number '%s'", b.getNumber()));
		}
	}
	
	public static void main(String [] args) {
		//Test cases FIFO
		testCaseOneFIFO();
		System.out.println();
		testCaseTwoFIFO();
		System.out.println();
		testCaseThreeFIFO();
		
		//Test cases Ring Buffer
		testCaseOneRingBuffer();
		System.out.println();
		testCaseTwoRingBuffer();
		
	}
}
