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
package fr.paris.lutece.portal.service.file.implementation;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.service.file.IFileDownloadUrlService;
import fr.paris.lutece.portal.service.file.IFileRBACService;
import fr.paris.lutece.portal.service.file.IFileStoreServiceProvider;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;


import fr.paris.lutece.portal.service.util.AppException;
import java.io.IOException;
import org.apache.commons.fileupload.FileItem;

/**
 * 
 * DatabaseBlobStoreService.
 * 
 */
public class LocalDatabaseBlobStoreService implements IFileStoreServiceProvider
{
    private static final long serialVersionUID = 1L;

    /**
     * name defaulted to databaseBlobstore - only one can be supported by webapp
     */
    private static final String FILE_STORE_PROVIDER_NAME = "defaultDatabaseFileStoreProvider";

    private IFileDownloadUrlService _fileDownloadUrlService ;
    private IFileRBACService _fileRBACService ;

    /**
     * get the FileRBACService
     * 
     * @return the FileRBACService
     */
    public IFileRBACService getFileRBACService() {
        return _fileRBACService;
    }

    /**
     * set the FileRBACService
     * 
     * @param fileRBACService 
     */
    public void setFileRBACService(IFileRBACService fileRBACService) {
        this._fileRBACService = fileRBACService;
    }

    /**
     * Get the downloadService
     * 
     * @return the downloadService
     */
    public IFileDownloadUrlService getDownloadUrlService( )
    {
        return _fileDownloadUrlService;
    }

    /**
     * Sets the downloadService
     * 
     * @param downloadUrlService
     *            downloadService
     */
    public void setDownloadUrlService( IFileDownloadUrlService downloadUrlService )
    {
        _fileDownloadUrlService = downloadUrlService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName( )
    {
        return FILE_STORE_PROVIDER_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( String strKey )
    {
        int nfileId = Integer.parseInt( strKey );
        FileHome.remove( nfileId );
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getFile( String strKey )
    {
        if ( StringUtils.isNotBlank( strKey ) )
        {
            int nfileId = Integer.parseInt( strKey );
            File file = FileHome.findByPrimaryKey( nfileId );
            
            if ( file != null )
            {
                return file;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String storeBytes( byte [ ] blob )
    {
        File file = new File( );
        PhysicalFile physicalFile = new PhysicalFile( );
        physicalFile.setValue( blob );
        file.setPhysicalFile( physicalFile );
        
        int nFileId = FileHome.create( file );

        return String.valueOf( nFileId );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String storeInputStream( InputStream inputStream )
    {
        File file = new File( );
        PhysicalFile physicalFile = new PhysicalFile( );
        
        byte[] buffer;
        
        try {
            buffer = new byte[ inputStream.available( ) ];
        } catch (IOException ex) {
            throw new AppException( ex.getMessage( ) , ex);
        }
        
        physicalFile.setValue( buffer );
        file.setPhysicalFile( physicalFile );
        
        int nFileId = FileHome.create( file );

        return String.valueOf( nFileId );
    }


    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.portal.service.blobstore.BlobStoreService#storeFileItem (java.io.InputStream)
     */
    @Override
    public String storeFileItem( FileItem fileItem )
    {
        
        File file = new File( );
        file.setTitle( fileItem.getName( ) );
        file.setSize( (int)fileItem.getSize( ) );
        file.setMimeType( fileItem.getContentType( ) );
        
        PhysicalFile physicalFile = new PhysicalFile( );
        
        byte[] buffer;
        
        try {
            buffer = new byte[ fileItem.getInputStream( ).available( )];
        } catch (IOException ex) {
            throw new AppException( ex.getMessage( ) , ex);
        }
        
        physicalFile.setValue( buffer );
        file.setPhysicalFile( physicalFile );
        
        int nFileId = FileHome.create( file );

        return String.valueOf( nFileId );
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.portal.service.blobstore.BlobStoreService#storeFileItem (java.io.InputStream)
     */
    @Override
    public String storeFile( File file )
    {
        int nFileId = FileHome.create( file );

        return String.valueOf( nFileId );
    }

    @Override
    public boolean isDefault() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public InputStream getInputStream(String strKey) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getFileDownloadUrlFO(String strKey) {
        return _fileDownloadUrlService.getFileDownloadUrlFO( strKey );
    }

    @Override
    public String getFileDownloadUrlBO(String strKey) {
        return _fileDownloadUrlService.getFileDownloadUrlBO( strKey );
    }

    @Override
    public boolean isAuthorized(String strKey, User user) {
        return _fileRBACService.isAuthorized( strKey, user );
    }
}
