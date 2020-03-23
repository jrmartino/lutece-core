/*
 * Copyright (c) 2002-2020, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.portal.service.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class provides writing services in the application logs files
 */
public final class AppTraceService
{
	// constants
	public static final String EVENT_TYPE_READ = "READ" ;
	public static final String EVENT_TYPE_CREATE = "CREATE" ;
    public static final String EVENT_TYPE_DELETE = "DELETE" ;
    public static final String EVENT_TYPE_MODIFY = "MODIFY" ;

    public static final String EVENT_TYPE_CONNECT = "CONNECT" ;
    public static final String EVENT_TYPE_DISCONNECT = "DISCONNECT" ;
    public static final String EVENT_TYPE_RIGHTS = "RIGHTS" ;
    
        
    private static final String EVENT_LOG_SEPARATOR = "|" ;
    private static final String LOGGER_TRACE = "lutece-trace";

    private static org.slf4j.Logger _logger = LoggerFactory.getLogger( LOGGER_TRACE );

    /**
     * Creates a new AppTraceService object.
     */
    private AppTraceService( )
    {
    }

    /**
     * Log action
     * 
     * @param eventType
     * @param description
     * @param connectedUserLogin
     * @param data 
     */
    public static void trace( String strAppId, String strEventType, String strAppEventCode, String strConnectedUserLogin, Object data )
    {
        String logMessage = getLogMessage( strAppId, strEventType, strAppEventCode, strConnectedUserLogin, data );
                
        String hash = getHash( logMessage );
        
        // log
        _logger.info( EVENT_LOG_SEPARATOR + hash + EVENT_LOG_SEPARATOR +  logMessage );
        
    }

    /**
     * build log message 
     * 
     * @param eventType
     * @param description
     * @param connectedUserLogin
     * @param data
     * @return the log message
     */
    private static String getLogMessage( String strAppId, String strEventType, String strAppEventCode, String strConnectedUserLogin, Object data )
    {
      	String jsonData ;
    	ObjectMapper obj = new ObjectMapper( ); 
        try { 
        	jsonData = obj.writeValueAsString( data ); 
        }
        catch (JsonProcessingException e) {
        	jsonData = "error : unable to create json from data" ;
		}
    	
    	return getLogMessage( strAppId, strEventType, strAppEventCode, strConnectedUserLogin, jsonData);
    }
    
    
    /**
     * build log message 
     * 
     * @param eventType
     * @param description
     * @param connectedUserLogin
     * @param data
     * @return the log message
     */
    private static String getLogMessage(  String strAppId, String strEventType, String strAppEventCode, String strConnectedUserLogin, String strData )
    {
    	StringBuilder sb = new StringBuilder( );
    	
    	sb.append( strAppId ).append( EVENT_LOG_SEPARATOR );
    	sb.append( strEventType ).append( EVENT_LOG_SEPARATOR );
    	sb.append( strAppEventCode ).append( EVENT_LOG_SEPARATOR );
    	sb.append( strConnectedUserLogin ).append( EVENT_LOG_SEPARATOR );
        sb.append( strData ).append( EVENT_LOG_SEPARATOR );
         
    	return sb.toString( );
    }
    
	/**
	 * get hash 
	 * 
	 * @param message
	 * @param last hash
	 * 
	 * @return the hash in String
	 */
	private static String getHash( String message )
	{

		byte[] byteChaine;
		try {
			byteChaine = message.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] hash = md.digest(byteChaine);

			//convert byte array to Hexadecimal String
			StringBuilder sb = new StringBuilder(2*hash.length);
			for(byte b : hash){
				sb.append(String.format("%02x", b&0xff));
			}

			return  sb.toString();


		} catch ( UnsupportedEncodingException | NoSuchAlgorithmException e ) 
		{
			return "Hash ERROR : " + e.getLocalizedMessage( );
		} 

	}

	/**
	 * verify hash 
	 * 
	 * @param message
	 * 
	 * @return true if the hash contained in the message is valid
	 */
	public static boolean verifyMessageHash( String message )
	{
		try {

			int idx =  message.indexOf( "|" , message.indexOf( LOGGER_TRACE ) );
			String hash = message.substring( idx + 1, idx + 33 );
			String data = message.substring( idx + 34 );

			return  ( hash != null && hash.equals( getHash( data ) ) );
		}
		catch (StringIndexOutOfBoundsException e) {
			return false;
		}
	}
}
