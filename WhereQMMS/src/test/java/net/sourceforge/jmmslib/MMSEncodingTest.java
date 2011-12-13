package net.sourceforge.jmmslib;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.junit.After;
import org.junit.Before;

public class MMSEncodingTest extends TestCase {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for MMEncoding");
		// $JUnit-BEGIN$

		suite.addTest(new MMSEncodingTest("testMMSEncoding"));

		// $JUnit-END$
		return suite;
	}

	public MMSEncodingTest(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	public void testMMSEncoding() {
		// Create the message bean
		MmsMessage mms = new MmsMessage();
		Random r = new Random();
		try {
			// Set the sender and receiver addresses (xxxxxxxxxxx should be a
			// valid phone number)
			mms.setMessageSender("xxxxxxxxxxxxx",
					MmsMessage.MMS_ADDRESS_TYPE_MOBILE_NUMBER);
			mms.addMessageReceiver("xxxxxxxxxxxxx",
					MmsMessage.MMS_ADDRESS_TYPE_MOBILE_NUMBER);

			// Set the transaction id
			mms.setTransactionID(String.valueOf(r.nextInt(999999999)));

			// Set the date of the MMS
			mms.setMessageDate(System.currentTimeMillis());

			// Set the subject
			mms.setMessageSubject("This is a cool subject!");

			// Set the content type... usually MULTIPART
			mms.setMessageContentType(MmsMessage.CTYPE_APPLICATION_MULTIPART_MIXED);

			// Set the content type...
			mms.setMessageType(MmsMessage.MMS_MESSAGE_TYPE_SEND_REQUEST);

			// Set the MMS version used
			mms.setVersion(MmsMessage.MMS_VERSION_1);

			// Create a part object and fill it
			MmsPart p = new MmsPart();
			p.setPartContent(new String("This is a text part")
					.getBytes("US-ASCII"));
			p.setPartContentType(MmsMessage.CTYPE_TEXT_PLAIN);

			// Add the part to the message
			mms.addPart(p);

			// Create a part object and fill it
			MmsPart gif = new MmsPart();
			gif.setPartContent(readFile("test_collaterals/cat_water.gif"));
			gif.setPartContentType(MmsMessage.CTYPE_TEXT_PLAIN);

			// Add the part to the message
			mms.addPart(gif);
		} catch (MmsMessageException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String encodedMMSString = null;
		// Create the encoder for the specified MMS.
		MmsEncoder encoder = new MmsEncoder(mms);
		try {
			// Encode the message
			byte encodedMMS[] = encoder.encodeMessage();
			encodedMMSString = new String(encodedMMS);
		} catch (MmsEncodingException e) {
			e.printStackTrace();
		}

		System.out.println(encodedMMSString);
	}

	private byte[] readFile(String filename) {
		int fileSize = 0;
		RandomAccessFile fileH = null;

		// Opens the file for reading.
		try {
			fileH = new RandomAccessFile(filename, "r");
			fileSize = (int) fileH.length();
		} catch (IOException ioErr) {
			System.err.println("Cannot find " + filename);
			System.err.println(ioErr);
			System.exit(200);
		}

		// allocates the buffer large enough to hold entire file
		byte[] buf = new byte[fileSize];

		// reads all bytes of file
		int i = 0;
		try {
			while (true) {
				try {
					buf[i++] = fileH.readByte();
				} catch (EOFException e) {
					break;
				}
			}
		} catch (IOException ioErr) {
			System.out.println("ERROR in reading of file" + filename);
		}

		return buf;
	}

}
