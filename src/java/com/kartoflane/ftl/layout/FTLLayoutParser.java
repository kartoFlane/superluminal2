package com.kartoflane.ftl.layout;

import java.util.Scanner;
import java.util.regex.Pattern;


/**
 * Parser for FTL .txt layout files.
 * 
 * @author kartoFlane
 *
 */
public class FTLLayoutParser {

	private FTLLayoutFactory factory;

	public FTLLayoutParser() {
		this(null);
	}

	public FTLLayoutParser(FTLLayoutFactory factory) {
		if (factory == null)
			factory = new DefaultFTLLayoutFactory();
		this.factory = factory;
	}

	/**
	 * Attempts to parse the content and construct a viable FTL
	 * Layout object out of it.
	 * 
	 * @param source
	 *            the string representing file contents
	 * @return the Layout that represents the .txt layout file
	 * @throws FTLLayoutParseException
	 *             when an error occurs while parsing
	 */
	public ShipLayout build(String source) throws FTLLayoutParseException {
		Scanner sc = new Scanner(source);
		ShipLayout layout = new ShipLayout();

		int line = -1;

		try {
			String lineContent = null;

			while (sc.hasNextLine()) {
				++line;
				lineContent = sc.nextLine();

				if (lineContent == null || lineContent.trim().equals("")) {
					if (sc.hasNextLine()) {
						// throw new FTLLayoutParseException("Double line break only allowed at the end of the file.", layout, line);
					}
				}
				else if (Pattern.matches("\\w*", lineContent)) {
					LOType objectType = null;
					try {
						objectType = LOType.valueOf(lineContent);

						switch (objectType) {
							case X_OFFSET:
								layout.addLayoutObject(factory.xOffset(line, sc.nextInt()));
								++line;
								break;
							case Y_OFFSET:
								layout.addLayoutObject(factory.yOffset(line, sc.nextInt()));
								++line;
								break;
							case HORIZONTAL:
								layout.addLayoutObject(factory.horizontal(line, sc.nextInt()));
								++line;
								break;
							case VERTICAL:
								layout.addLayoutObject(factory.vertical(line, sc.nextInt()));
								++line;
								break;
							case ELLIPSE:
								layout.addLayoutObject(factory.ellipse(line, sc.nextInt(), sc.nextInt(), sc.nextInt(), sc.nextInt()));
								line += 4;
								break;
							case ROOM:
								layout.addLayoutObject(factory.room(line, sc.nextInt(), sc.nextInt(), sc.nextInt(), sc.nextInt(), sc.nextInt()));
								line += 5;
								break;
							case DOOR:
								layout.addLayoutObject(factory.door(line, sc.nextInt(), sc.nextInt(), sc.nextInt(), sc.nextInt(), sc.nextInt() == 0));
								line += 5;
								break;
							default:
								throw new RuntimeException("Implementation error. Should never happen.");
						}
					}
					catch (IllegalArgumentException e) {
						throw new FTLLayoutParseException("Unrecognised layout object: " + lineContent, layout, line);
					}
				}
				else {
					throw new FTLLayoutParseException("Unexpected characters: " + lineContent, layout, line);
				}
			}

			if (!lineContent.equals("")) {
				throw new FTLLayoutParseException("No empty line at the end of the file", layout, line);
			}
		}
		finally {
			sc.close();
		}

		return layout;
	}
}
