package com.kartoflane.superluminal2.utils;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.vhati.modmanager.core.SloppyXMLOutputProcessor;
import net.vhati.modmanager.core.SloppyXMLParser;

import org.jdom2.Document;
import org.jdom2.input.JDOMParseException;

public class IOUtils {

	private static final Pattern PROTOCOL_PTRN = Pattern.compile("^[^:]+:");

	public static String trimProtocol(String input) {
		Matcher m = PROTOCOL_PTRN.matcher(input);
		if (m.find())
			return input.replace(m.group(), "");
		else
			return input;
	}

	public static String getProtocol(String input) {
		Matcher m = PROTOCOL_PTRN.matcher(input);
		if (m.find())
			return m.group();
		else
			return "";
	}

	public static String readFileText(File f) throws FileNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(f);
		DecodeResult dr = decodeText(fis, f.getName());
		fis.close();
		return dr.text;
	}

	public static Document readFileXML(File f) throws FileNotFoundException, IOException, JDOMParseException {
		String contents = readFileText(f);
		return parseXML(contents);
	}

	/**
	 * Reads the stream supplied in argument, and decodes it as text.<br>
	 * This method fully reads the stream, and as such after this method has been invoked,
	 * the stream will have reached EOF.<br>
	 * This method does not close the stream.
	 * 
	 * @param is
	 *            The stream to be read.
	 * @param label
	 *            How error messages should refer to the stream, or null.
	 */
	public static String readStreamText(InputStream is, String label) throws IOException {
		DecodeResult dr = decodeText(is, label);
		return dr.text;
	}

	/**
	 * Reads the stream supplied in argument, decodes it as text, and interprets it as XML.<br>
	 * This method fully reads the stream, and as such after this method has been invoked,
	 * the stream will have reached EOF.<br>
	 * This method does not close the stream.
	 * 
	 * @param is
	 *            The stream to be read.
	 * @param label
	 *            How error messages should refer to the stream, or null.
	 */
	public static Document readStreamXML(InputStream is, String label) throws IOException, JDOMParseException {
		String contents = readStreamText(is, label);
		return parseXML(contents);
	}

	/**
	 * Clones the stream supplied in argument.<br>
	 * This method fully reads the stream, and as such after this method has been invoked,
	 * the stream will have reached EOF.<br>
	 * This method does not close the stream.
	 */
	public static InputStream cloneStream(InputStream is) throws IOException {
		return new ByteArrayInputStream(readStream(is));
	}

	/**
	 * Reads the stream supplied in argument.<br>
	 * This method fully reads the stream, and as such after this method has been invoked,
	 * the stream will have reached EOF.<br>
	 * This method does not close the stream.
	 */
	public static byte[] readStream(InputStream is) throws IOException {
		int read = 0;
		byte[] bytes = new byte[1024 * 1024];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while ((read = is.read(bytes)) != -1)
			baos.write(bytes, 0, read);
		return baos.toByteArray();
	}

	public static Document parseXML(String contents) throws JDOMParseException {
		if (contents == null)
			throw new IllegalArgumentException("Parsed string must not be null.");

		SloppyXMLParser parser = new SloppyXMLParser();

		return parser.build(contents);
	}

	/**
	 * Writes the contents of the input stream to the output stream.<br>
	 * This method fully reads the input stream, and as such after this method has been invoked,
	 * the stream will have reached EOF.<br>
	 * This method does not close the streams.
	 */
	public static void write(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024 * 10];
		int len;
		while ((len = in.read(buffer)) != -1) {
			out.write(buffer, 0, len);
		}
	}

	/**
	 * Writes the Document to the file in XML format.<br>
	 * This method uses the {@link SloppyXMLOutputProcessor}, which
	 * omitts the root element when writing the document.
	 * 
	 * @param doc
	 *            the document to be written
	 * @param f
	 *            file in which the document will be saved
	 * @return true if operation was completed successfully, false otherwise
	 */
	public static boolean writeFileXML(Document doc, File f) throws IOException {
		if (doc == null)
			throw new IllegalArgumentException("Document must not be null.");
		if (f == null)
			throw new IllegalArgumentException("File must not be null.");
		if (f.isDirectory())
			throw new IllegalArgumentException("File must not be a directory.");

		BufferedWriter writer = null;

		try {
			f.getAbsoluteFile().getParentFile().mkdirs();
			writer = new BufferedWriter(new FileWriter(f));
			SloppyXMLOutputProcessor.sloppyPrint(doc, writer, null);

			return true;
		} finally {
			if (writer != null)
				writer.close();
		}
	}

	/**
	 * @return string representation of the Document's XML code
	 */
	public static String readDocument(Document doc) throws IOException {
		if (doc == null)
			throw new IllegalArgumentException("Document must not be null.");

		String result = null;
		StringWriter writer = null;
		try {
			writer = new StringWriter();
			SloppyXMLOutputProcessor.sloppyPrint(doc, writer, null);

			result = writer.toString();
		} finally {
			if (writer != null)
				writer.close();
		}

		return result;
	}

	/**
	 * Encodes a string (throwing an exception on bad chars) to bytes in a stream.<br>
	 * Line endings will not be normalized.
	 * 
	 * @param text
	 *            a String to encode
	 * @param encoding
	 *            the name of a Charset
	 * @param description
	 *            how error messages should refer to the string, or null
	 * 
	 * @author Vhati
	 */
	public static InputStream encodeText(String text, String encoding, String description) throws IOException {
		CharsetEncoder encoder = Charset.forName(encoding).newEncoder();

		ByteArrayOutputStream tmpData = new ByteArrayOutputStream();
		Writer writer = new OutputStreamWriter(tmpData, encoder);
		writer.write(text);
		writer.flush();

		InputStream result = new ByteArrayInputStream(tmpData.toByteArray());
		return result;
	}

	/**
	 * Determines text encoding for an InputStream and decodes its bytes as a string.<br>
	 * 
	 * CR and CR-LF line endings will be normalized to LF.<br>
	 * This method does not close the stream.
	 * 
	 * @param is
	 *            a stream to read
	 * @param description
	 *            how error messages should refer to the stream, or null
	 * 
	 * @author Vhati
	 */
	public static DecodeResult decodeText(InputStream is, String description) throws IOException {
		String result = null;

		byte[] buf = new byte[4096];
		int len;
		ByteArrayOutputStream tmpData = new ByteArrayOutputStream();
		while ((len = is.read(buf)) >= 0) {
			tmpData.write(buf, 0, len);
		}
		byte[] allBytes = tmpData.toByteArray();
		tmpData.reset();

		Map<byte[], String> boms = new LinkedHashMap<byte[], String>();
		boms.put(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF }, "UTF-8");
		boms.put(new byte[] { (byte) 0xFF, (byte) 0xFE }, "UTF-16LE");
		boms.put(new byte[] { (byte) 0xFE, (byte) 0xFF }, "UTF-16BE");

		String encoding = null;
		byte[] bom = null;

		for (Map.Entry<byte[], String> entry : boms.entrySet()) {
			byte[] tmpBom = entry.getKey();
			byte[] firstBytes = Arrays.copyOfRange(allBytes, 0, tmpBom.length);
			if (Arrays.equals(tmpBom, firstBytes)) {
				encoding = entry.getValue();
				bom = tmpBom;
				break;
			}
		}

		if (encoding != null) {
			// This may throw CharacterCodingException.
			CharsetDecoder decoder = Charset.forName(encoding).newDecoder();
			ByteBuffer byteBuffer = ByteBuffer.wrap(allBytes, bom.length, allBytes.length - bom.length);
			result = decoder.decode(byteBuffer).toString();
			allBytes = null; // GC hint.
		}
		else {
			ByteBuffer byteBuffer = ByteBuffer.wrap(allBytes);

			Map<String, Exception> errorMap = new LinkedHashMap<String, Exception>();
			for (String guess : new String[] { "UTF-8", "windows-1252" }) {
				try {
					byteBuffer.rewind();
					byteBuffer.limit(allBytes.length);
					CharsetDecoder decoder = Charset.forName(guess).newDecoder();
					result = decoder.decode(byteBuffer).toString();
					encoding = guess;
					break;
				} catch (CharacterCodingException e) {
					errorMap.put(guess, e);
				}
			}
			if (encoding == null) {
				// All guesses failed!?
				String msg = String.format("Could not guess encoding for %s.", (description != null ? "\"" + description + "\"" : "a file"));
				for (Map.Entry<String, Exception> entry : errorMap.entrySet()) {
					msg += String.format("\nFailed to decode as %s: %s", entry.getKey(), entry.getValue());
				}
				throw new IOException(msg);
			}
			allBytes = null; // GC hint.
		}

		// Determine the original line endings.
		int eol = DecodeResult.EOL_NONE;
		Matcher m = Pattern.compile("(\r(?!\n))|((?<!\r)\n)|(\r\n)").matcher(result);
		if (m.find()) {
			if (m.group(3) != null)
				eol = DecodeResult.EOL_CRLF;
			else if (m.group(2) != null)
				eol = DecodeResult.EOL_LF;
			else if (m.group(1) != null)
				eol = DecodeResult.EOL_CR;
		}

		result = result.replaceAll("\r(?!\n)|\r\n", "\n");
		return new DecodeResult(result, encoding, eol, bom);
	}

	/**
	 * A holder for results from decodeText().
	 * 
	 * text - The decoded string.
	 * encoding - The encoding used.
	 * eol - A constant describing the original line endings.
	 * bom - The BOM bytes found, or null.
	 * 
	 * @author Vhati
	 */
	public static class DecodeResult {
		public static final int EOL_NONE = 0;
		public static final int EOL_CRLF = 1;
		public static final int EOL_LF = 2;
		public static final int EOL_CR = 3;

		public final String text;
		public final String encoding;
		public final int eol;
		public final byte[] bom;

		public DecodeResult(String text, String encoding, int eol, byte[] bom) {
			this.text = text;
			this.encoding = encoding;
			this.eol = eol;
			this.bom = bom;
		}

		public String getEOLName() {
			if (eol == EOL_CRLF)
				return "CR-LF";
			if (eol == EOL_LF)
				return "LF";
			if (eol == EOL_CR)
				return "CR";
			return "None";
		}
	}

}
