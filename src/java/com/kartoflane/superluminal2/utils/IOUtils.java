package com.kartoflane.superluminal2.utils;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import net.vhati.modmanager.core.SloppyXMLOutputProcessor;
import net.vhati.modmanager.core.SloppyXMLParser;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.JDOMParseException;

import com.kartoflane.superluminal2.core.DatabaseEntry;
import com.kartoflane.superluminal2.ftl.ShipObject;
import com.kartoflane.superluminal2.ui.ShipContainer;

/**
 * This class contains methods used to read and write files, as well as interpret content
 * of files as XML or encoded text.
 * 
 * @author kartoFlane
 * @author Vhati
 * 
 */
public class IOUtils {

	private static final Pattern PROTOCOL_PTRN = Pattern.compile("^[^:]+:");

	/**
	 * Removes the protocol from the argument and returns the resulting string.<br>
	 * Protocol is the text from the start of the string until the first : , eg.
	 * 
	 * <pre>
	 * <tt>file:example</tt>
	 * </pre>
	 * 
	 * Protocols used in the editor are listed in {@link com.kartoflane.superluminal2.core.Manager#getInputStream(String)
	 * Manager.getInputStream(String)}<br>
	 * <br>
	 * 
	 * @param input
	 *            string to be trimmed
	 * @return the trimmed string, or the argument if no protocol was found.
	 */
	public static String trimProtocol(String input) {
		Matcher m = PROTOCOL_PTRN.matcher(input);
		if (m.find())
			return input.replace(m.group(), "");
		else
			return input;
	}

	/**
	 * Retrieves the protocol from the argument and returns it.
	 * Protocol is the text from the start of the string until the first : , eg.
	 * 
	 * <pre>
	 * <tt>file:example</tt>
	 * </pre>
	 * 
	 * Protocols used in the editor are listed in {@link com.kartoflane.superluminal2.core.Manager#getInputStream(String)
	 * Manager.getInputStream(String)}<br>
	 * <br>
	 * 
	 * @return the argument's protocol, or an empty string if no protocol was found.
	 */
	public static String getProtocol(String input) {
		Matcher m = PROTOCOL_PTRN.matcher(input);
		if (m.find())
			return m.group();
		else
			return "";
	}

	/**
	 * Merges the file-byte map with the specified ShipContainer, effectively saving
	 * the ship in the file-byte map.
	 */
	public static void merge(Map<String, byte[]> base, ShipContainer container)
			throws JDOMParseException, IOException {
		ShipObject ship = container.getShipController().getGameObject();
		Map<String, byte[]> fileMap = ShipSaveUtils.saveShip(container);

		for (String file : fileMap.keySet()) {
			if (base.containsKey(file)) {
				// Mod already contains that file; need to consider further
				if (file.endsWith(".png") || file.equals(ship.getLayoutTXT()) || file.equals(ship.getLayoutXML())) {
					// Overwrite graphics and layout files
					base.put(file, fileMap.get(file));
				}
				else if (file.endsWith(".xml") || file.endsWith(".append") ||
						file.endsWith(".rawappend") || file.endsWith(".rawclobber")) {
					// Merge XML files, while removing obscured elements
					Document docBase = IOUtils.parseXML(new String(base.get(file)));
					Document docAdd = IOUtils.parseXML(new String(fileMap.get(file)));

					Element root = docBase.getRootElement();

					List<Content> addList = docAdd.getContent();
					for (int i = 0; i < addList.size(); ++i) {
						Content c=  addList.get(i);
						if ( c instanceof Element == false)
							continue;
						Element e = (Element)c;

						String name = e.getAttributeValue("name");
						if (name == null) {
							// Can't identify; just add it.
							e.detach();
							root.addContent(e);
						}
						else {
							// Remove elements that are obscured, in order to prevent bloating
							List<Element> baseList = root.getChildren(e.getName(), e.getNamespace());
							for (int j = 0; j < baseList.size(); ++j) {
								Element el = baseList.get(j);
								String name2 = el.getAttributeValue("name");
								if (name2 != null && name2.equals(name)) {
									el.detach();
								}
							}
							e.detach();
							root.addContent(e);
						}
					}

					base.put(file, readDocument(docBase).getBytes());
				}
			}
			else {
				// Doesn't exist; add it
				base.put(file, fileMap.get(file));
			}
		}
	}

	/**
	 * Decodes the contents of the specified file as text and returns them as a string.
	 * 
	 * @param f
	 *            the file to be read
	 * @return the contents of the file
	 * 
	 * @throws FileNotFoundException
	 *             when the file is not found
	 * @throws IOException
	 *             when an IO exception occurs while reading the file
	 */
	public static String readFileText(File f) throws FileNotFoundException, IOException {
		if (f == null)
			throw new IllegalArgumentException("Argument must not be null.");
		FileInputStream fis = new FileInputStream(f);
		DecodeResult dr = decodeText(fis, f.getName());
		fis.close();
		return dr.text;
	}

	/**
	 * Decodes the contents of the specified file as text, and then interprets the text as XML.
	 * 
	 * @param f
	 *            the file to be read
	 * @return the XML document representing the contents of the file
	 * 
	 * @throws FileNotFoundException
	 *             when the file is not found
	 * @throws IOException
	 *             when an IO exception occurs while reading the file
	 * @throws JDOMParseException
	 *             when an exception occurs while parsing XML
	 */
	public static Document readFileXML(File f) throws FileNotFoundException, IOException, JDOMParseException {
		String contents = readFileText(f);
		return parseXML(contents);
	}

	/**
	 * Reads the stream supplied in argument, and decodes it as text.<br>
	 * This method fully reads the stream, and as such after this method has been invoked,
	 * the stream will have reached EOF.<br>
	 * <b>This method does not close the stream.</b>
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
	 * <b>This method does not close the stream.</b>
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
	 * <b>This method does not close the stream.</b>
	 */
	public static InputStream cloneStream(InputStream is) throws IOException {
		return new ByteArrayInputStream(readStream(is));
	}

	/**
	 * Reads the stream supplied in argument.<br>
	 * This method fully reads the stream, and as such after this method has been invoked,
	 * the stream will have reached EOF.<br>
	 * <b>This method does not close the stream.</b>
	 */
	public static byte[] readStream(InputStream is) throws IOException {
		int read = 0;
		byte[] bytes = new byte[1024 * 1024];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while ((read = is.read(bytes)) != -1)
			baos.write(bytes, 0, read);
		return baos.toByteArray();
	}
	
	/**
	 * Reads contents of the DatabaseEntry into a filename-byte map.
	 */
	public static HashMap<String, byte[]> readEntry(DatabaseEntry entry) throws IOException {
		HashMap<String, byte[]> result = new HashMap<String, byte[]>();

		for (String fileName : entry.list()) {
			InputStream is = null;
			try {
				is = entry.getInputStream(fileName);
				result.put(fileName, readStream(is));
			} finally {
				if (is != null)
					is.close();
			}
		}

		return result;
	}

	/**
	 * Interprets the string as XML.
	 * 
	 * @param contents
	 *            the string to be interpreted
	 * @return the XML document representing the string
	 * 
	 * @throws JDOMParseException
	 *             when an exception occurs while parsing XML
	 */
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
	 * <b>This method does not close the streams.</b>
	 * 
	 * @param in
	 *            the source stream
	 * @param out
	 *            the destination stream
	 */
	public static void write(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024 * 1024];
		int len;
		while ((len = in.read(buffer)) != -1) {
			out.write(buffer, 0, len);
		}
	}

	/**
	 * Writes the file-byte map as a hierarchy of files.
	 */
	public static void writeDir(Map<String, byte[]> files, File destination)
			throws IOException {
		for (String fileName : files.keySet()) {
			ByteArrayInputStream in = null;
			FileOutputStream out = null;

			File file = new File(destination.getAbsolutePath() + "/" + fileName);
			file.getParentFile().mkdirs();

			try {
				in = new ByteArrayInputStream(files.get(fileName));
				out = new FileOutputStream(file);
				IOUtils.write(in, out);
			} finally {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			}
		}
	}

	/**
	 * Writes the file-byte map as a single zip file.
	 */
	public static void writeZip(Map<String, byte[]> files, File destination)
			throws IOException {
		ZipInputStream in = null;
		ZipOutputStream out = null;
		try {
			in = new ZipInputStream(new ByteArrayInputStream(createZip(files)));
			out = new ZipOutputStream(new FileOutputStream(destination));

			ZipEntry entry = null;
			while ((entry = in.getNextEntry()) != null) {
				out.putNextEntry(entry);

				byte[] byteBuff = new byte[4096];
				int bytesRead = 0;
				while ((bytesRead = in.read(byteBuff)) != -1)
					out.write(byteBuff, 0, bytesRead);

				in.closeEntry();
			}
		} finally {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
		}
	}

	private static byte[] createZip(Map<String, byte[]> files)
			throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ZipOutputStream zf = new ZipOutputStream(bos);
		Iterator<String> it = files.keySet().iterator();
		String fileName = null;
		ZipEntry ze = null;

		while (it.hasNext()) {
			fileName = it.next();
			ze = new ZipEntry(fileName);
			zf.putNextEntry(ze);
			zf.write(files.get(fileName));
		}
		zf.close();

		return bos.toByteArray();
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
	 * Reads the specified XML document, and returns its textual representation.
	 * 
	 * @param doc
	 *            the XML document to be read
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
	 * <b>This method does not close the stream.</b>
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
