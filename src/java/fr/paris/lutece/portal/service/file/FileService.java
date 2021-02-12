/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.paris.lutece.portal.service.file;

import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author SLE
 */
public class FileService {
    
    private final Map<String,IFileStoreServiceProvider> _fileStoreServiceProviders = new HashMap<>( );
    private IFileStoreServiceProvider _currentFileStoreServiceProvider ;
    private static final String MSG_NO_FILE_SERVICE= "No file service Available";

    private static final FileService _instance = new FileService( );
    

    /**
     * init
     */
    private FileService( )
    {
        List<IFileStoreServiceProvider> fileStoreServiceProviderList = SpringContextService.getBeansOfType(IFileStoreServiceProvider.class);
        if ( !fileStoreServiceProviderList.isEmpty( ) )
        {
            for ( IFileStoreServiceProvider fss : fileStoreServiceProviderList )
            {
                _fileStoreServiceProviders.put( fss.getName( ), fss );
            }
        }
        
        _currentFileStoreServiceProvider = getDefaultServiceProvider( );
    }

    /**
     * getter
     * 
     * @return the instance
     */
    public static FileService getInstance( )
    {
        return _instance;
    }

    /**
     * get the current FileStoreService provider
     * 
     * @return the current FileStoreService provider
     */
    public IFileStoreServiceProvider getFileStoreServiceProvider( )
    {
        return _currentFileStoreServiceProvider;
    }

    /**
     * get the FileStoreService provider
     * 
     * @param strFileStoreServiceProviderName
     * @return the current FileStoreService provider
     */
    public IFileStoreServiceProvider getFileStoreServiceProvider( String strFileStoreServiceProviderName )
    {
        IFileStoreServiceProvider fss = _fileStoreServiceProviders.get( strFileStoreServiceProviderName) ;
        if (fss != null)
        {
            return fss;
        }
        else
        {
            throw new AppException( MSG_NO_FILE_SERVICE );
        }
    }
 
    /**
     * get the current FileStoreService provider
     * 
     * @param strFileStoreServiceProviderName
     */
    public void setFileStoreServiceProvider( String strFileStoreServiceProviderName )
    {
        IFileStoreServiceProvider fss = _fileStoreServiceProviders.get( strFileStoreServiceProviderName) ;
        if (fss != null)
        {
            _currentFileStoreServiceProvider = fss;
        }
        else
        {
            throw new AppException( MSG_NO_FILE_SERVICE );
        }
    }

    /**
     * get default File Store Service Provider
     * 
     * @return the provider
     */
    private IFileStoreServiceProvider getDefaultServiceProvider( ) 
    {
        for (String keyName : _fileStoreServiceProviders.keySet()) 
        {
            IFileStoreServiceProvider fss = _fileStoreServiceProviders.get( keyName );
            if ( fss.isDefault( ) )
            {
                return fss;
            }
        }
        
        // otherwise
        throw new AppException( MSG_NO_FILE_SERVICE );      
    }
}
