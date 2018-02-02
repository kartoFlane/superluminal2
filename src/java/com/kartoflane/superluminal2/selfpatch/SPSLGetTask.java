package com.kartoflane.superluminal2.selfpatch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kartoflane.common.selfpatch.SPGetTask;
import com.kartoflane.superluminal2.components.enums.OS;


public class SPSLGetTask extends SPGetTask
{
	private static final int bufferSize = 1024 * 16;


	@Override
	public void run()
	{
		File tmpFile = null;
		Exception exception = null;
		HttpURLConnection connection = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;

		try {
			if ( observer != null )
				observer.taskStatus( "Connecting..." );

			URL url = new URL( "https://github.com/kartoFlane/superluminal2/releases/latest" );

			connection = (HttpURLConnection)url.openConnection();
			int responseCode = connection.getResponseCode();
			String responseText = connection.getResponseMessage();

			if ( !isInterrupted() ) {
				if ( responseCode == HttpURLConnection.HTTP_OK ) {
					BufferedReader in = null;
					try {
						OS os = OS.identifyOS();
						String regex = Pattern.quote( "<a href=\"/" ) + "(" + Pattern.quote( "kartoFlane/superluminal2/releases/download/" ) + ".*?";
						if ( os.isWindows() )
							regex += Pattern.quote( "/Superluminal.Win-" );
						else if ( os.isLinux() )
							regex += Pattern.quote( "/Superluminal.Linux-" );
						else if ( os.isMac() )
							regex += Pattern.quote( "/Superluminal.Mac" );

						if ( os.isMac() ) {
							regex += "\\.";
						}
						else {
							if ( os.is32Bit() )
								regex += "32\\.";
							else if ( os.is64Bit() )
								regex += "64\\.";
						}

						if ( os.isWindows() )
							regex += ".*?" + Pattern.quote( ".zip" ) + ")" + "\"";
						else
							regex += ".*?" + Pattern.quote( ".tar.gz" ) + ")" + "\"";

						Pattern ptrn = Pattern.compile( regex );

						in = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
						String inputLine;
						while ( ( inputLine = in.readLine() ) != null ) {
							Matcher m = ptrn.matcher( inputLine );

							if ( m.find() ) {
								String address = m.group( 1 );
								url = new URL( "https://github.com/" + address );
								break;
							}
						}
					}
					finally {
						in.close();
						connection.disconnect();
					}

					connection = (HttpURLConnection)url.openConnection();
					responseCode = connection.getResponseCode();
					responseText = connection.getResponseMessage();

					if ( responseCode == HttpURLConnection.HTTP_OK ) {
						String fileName = "";
						String disposition = connection.getHeaderField( "Content-Disposition" );
						int contentLength = connection.getContentLength();

						if ( disposition != null ) {
							int index = disposition.indexOf( "filename=" );
							if ( index > 0 ) {
								fileName = disposition.substring( index + 9, disposition.length() );
							}
						}
						else {
							// extract file name from URL
							String fileURL = url.toString();
							fileName = fileURL.substring( fileURL.lastIndexOf( "/" ), fileURL.length() );
						}

						inputStream = connection.getInputStream();
						String saveFilePath = "." + "/" + fileName;
						downloadedFile = new File( saveFilePath );
						tmpFile = new File( saveFilePath + ".tmp" );

						outputStream = new FileOutputStream( tmpFile );

						if ( observer != null )
							observer.taskStatus( "Downloading " + fileName + "..." );
						int totalBytes = 0;
						int bytesRead = -1;
						byte[] buffer = new byte[bufferSize];
						while ( ( bytesRead = inputStream.read( buffer ) ) != -1 ) {
							outputStream.write( buffer, 0, bytesRead );
							totalBytes += bytesRead;
							if ( observer != null )
								observer.taskProgress( totalBytes, contentLength );

							if ( isInterrupted() ) {
								throw new InterruptedException();
							}
						}

						outputStream.close();
						inputStream.close();

						if ( downloadedFile.exists() )
							downloadedFile.delete();
						tmpFile.renameTo( downloadedFile );

						success = true;
						if ( observer != null )
							observer.taskStatus( "Download finished successfully." );
					}
					else {
						if ( observer != null )
							observer.taskStatus( "Server replied with code " + responseCode + ": " + responseText );
					}
				}
				else {
					if ( observer != null )
						observer.taskStatus( "Server replied with code " + responseCode + ": " + responseText );
				}
			}

			if ( observer != null )
				observer.taskFinished( success, null );
		}
		catch ( MalformedURLException e ) {
			exception = e;
		}
		catch ( IOException e ) {
			exception = e;
		}
		catch ( InterruptedException e ) {
		}
		finally {
			try {
				if ( connection != null )
					connection.disconnect();
				if ( outputStream != null )
					outputStream.close();
				if ( inputStream != null )
					inputStream.close();
			}
			catch ( Exception e ) {
				// Never happens.
			}

			if ( tmpFile != null && tmpFile.exists() )
				tmpFile.delete();
			if ( downloadedFile != null && downloadedFile.exists() )
				downloadedFile.deleteOnExit();

			if ( exception != null ) {
				if ( observer != null ) {
					observer.taskStatus( "Error occured." );
					observer.taskFinished( false, exception );
				}
			}
		}
	}
}
