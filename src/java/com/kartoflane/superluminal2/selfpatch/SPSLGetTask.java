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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kartoflane.common.selfpatch.SPGetTask;
import com.kartoflane.superluminal2.components.enums.OS;


public class SPSLGetTask extends SPGetTask
{
	public static final Logger log = LogManager.getLogger( SPSLGetTask.class );

	private static final int bufferSize = 1024 * 16;

	private String latestUrl;
	private File outputDir;


	public SPSLGetTask( String latestUrl, File outputDir )
	{
		this.latestUrl = latestUrl;
		this.outputDir = outputDir;
	}

	@Override
	public void run()
	{
		File tmpFile = null;
		Exception exception = null;

		try {
			if ( observer != null )
				observer.taskStatus( "Connecting..." );

			URL url = new URL( latestUrl );

			if ( isInterrupted() ) {
				// See if we were interrupted before we even start.
				throw new InterruptedException();
			}

			url = findPlatformDistributionLink( url );

			if ( url == null ) {
				if ( observer != null )
					observer.taskStatus( "Error: Could not find platform distribution archive." );
				log.error( "Error: Could not find platform distribution archive." );
				throw new InterruptedException();
			}

			downloadFileFromUrl( url, tmpFile );

			success = true;
			if ( observer != null ) {
				observer.taskStatus( "Download finished successfully." );
				observer.taskFinished( success, null );
			}

			log.info( "Download finished successfully." );
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
			// If download got interrupted, then the .tmp file will linger.
			// But if it completed successfully, the .tmp file will be renamed
			// Clean it up if it still exists at this point.
			if ( tmpFile != null && tmpFile.exists() )
				tmpFile.delete();

			// Schedule the downloaded file for deletion after we've unpacked it.
			if ( downloadedFile != null && downloadedFile.exists() )
				downloadedFile.deleteOnExit();

			if ( exception != null ) {
				String msg = String.format( "Error occurred: %s%n%s", exception.getClass().getSimpleName(), exception.getMessage() );
				if ( observer != null ) {
					observer.taskStatus( msg );
					observer.taskFinished( false, exception );
				}
				log.error( msg );
			}
		}
	}

	/**
	 * Cralws the response from the URL looking for a link to the appropriate release of the editor for
	 * the platform we're currently running on.
	 * 
	 * @param url
	 *            the releases page URL
	 * @return download link for the specific platform's release
	 * @throws IOException
	 */
	private URL findPlatformDistributionLink( URL url ) throws IOException
	{
		HttpURLConnection connection = null;
		BufferedReader in = null;

		try {
			connection = (HttpURLConnection)url.openConnection();
			int responseCode = connection.getResponseCode();
			String responseText = connection.getResponseMessage();

			if ( responseCode == HttpURLConnection.HTTP_OK ) {
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
						return new URL( "https://github.com/" + address );
					}
				}
			}
			else {
				String msg = String.format( "Error: Server replied with code %n: %n", responseCode, responseText );
				if ( observer != null )
					observer.taskStatus( msg );
				log.error( msg );
			}
		}
		finally {
			if ( connection != null ) {
				try {
					connection.disconnect();
				}
				catch ( Exception e ) {
				}
			}
			if ( in != null ) {
				try {
					in.close();
				}
				catch ( Exception e ) {
				}
			}
		}

		return null;
	}

	private void downloadFileFromUrl( URL url, File outTmpFile ) throws IOException, InterruptedException
	{
		InputStream inputStream = null;
		OutputStream outputStream = null;
		HttpURLConnection connection = null;

		try {
			connection = (HttpURLConnection)url.openConnection();
			int responseCode = connection.getResponseCode();
			String responseText = connection.getResponseMessage();

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
				downloadedFile = new File( outputDir, fileName );
				outTmpFile = new File( outputDir, fileName + ".tmp" );

				outputStream = new FileOutputStream( outTmpFile );

				if ( observer != null )
					observer.taskStatus( String.format( "Downloading %s...", fileName ) );

				int totalBytes = 0;
				int bytesRead = -1;
				byte[] buffer = new byte[bufferSize];
				while ( ( bytesRead = inputStream.read( buffer ) ) != -1 ) {
					outputStream.write( buffer, 0, bytesRead );
					totalBytes += bytesRead;
					if ( observer != null ) {
						observer.taskProgress( totalBytes, contentLength );
						observer.taskStatus( String.format( "Downloading %s... %.1f%%", fileName, totalBytes * 100 / (double)contentLength ) );
					}

					if ( isInterrupted() ) {
						// Keep checking the interrupt flag while downloading.
						connection.disconnect();
						outputStream.close();
						inputStream.close();
						throw new InterruptedException();
					}
				}

				outputStream.close();
				inputStream.close();

				if ( downloadedFile.exists() )
					downloadedFile.delete();
				outTmpFile.renameTo( downloadedFile );
			}
			else {
				String msg = String.format( "Error: Server replied with code %n: %n", responseCode, responseText );
				if ( observer != null )
					observer.taskStatus( msg );
				log.error( msg );
			}
		}
		finally {
			if ( connection != null ) {
				try {
					connection.disconnect();
				}
				catch ( Exception e ) {
				}
			}
			if ( outputStream != null ) {
				try {
					outputStream.close();
				}
				catch ( Exception e ) {
				}
			}
			if ( inputStream != null ) {
				try {
					inputStream.close();
				}
				catch ( Exception e ) {
				}
			}
		}
	}
}
