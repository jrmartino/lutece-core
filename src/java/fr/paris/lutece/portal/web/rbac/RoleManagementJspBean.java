/*
 * Copyright (c) 2002-2010, Mairie de Paris
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
package fr.paris.lutece.portal.web.rbac;

import fr.paris.lutece.portal.business.rbac.AdminRole;
import fr.paris.lutece.portal.business.rbac.AdminRoleHome;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.rbac.RBACHome;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.rbac.ResourceType;
import fr.paris.lutece.portal.service.rbac.ResourceTypeManager;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.admin.AdminFeaturesPageJspBean;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.string.StringUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * This class provides methods for role management.
 */
public class RoleManagementJspBean extends AdminFeaturesPageJspBean
{
    //////////////////////////////////////////////////////////////////////////////////
    // Contants
    public static final String RIGHT_MANAGE_ROLES = "CORE_RBAC_MANAGEMENT";

    // parameters
    private static final String PARAMETER_METHOD_SELECTION_ALL = "all";
    private static final String PARAMETER_METHOD_SELECTION_CHOOSE = "choose";
    private static final String PARAMETER_SELECTION_METHOD_CHOOSE = "choose";
    private static final String PARAMETER_ROLE_KEY = "role_key";
    private static final String PARAMETER_ROLE_DESCRIPTION = "role_description";
    private static final String PARAMETER_ROLE_KEY_PREVIOUS = "role_key_previous";
    private static final String PARAMETER_RESOURCE_TYPE = "resource_type";
    private static final String PARAMETER_SELECT_RESOURCES_METHOD = "select_resources";
    private static final String PARAMETER_RESOURCE_ID = "resource_id";
    private static final String PARAMETER_PERMISSION_KEY = "permission_key";
    private static final String PARAMETER_SELECT_PERMISSIONS_METHOD = "select_permissions";
    private static final String PARAMETER_RBAC_ID = "rbac_id";

    //markers
    private static final String MARK_PERMISSIONS_LIST = "permissions_list";
    private static final String MARK_RESOURCE_ID_LIST = "resource_id_list";
    private static final String MARK_RESOURCE_TYPE_LIST = "resource_type_list";
    private static final String MARK_CONTROLED_RESOURCE_LIST = "controled_resource_list";
    private static final String MARK_ROLE = "role";
    private static final String MARK_ROLE_LIST = "role_list";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_ROLE_KEY = "role_key";
    private static final String MARK_RESOURCE_TYPE = "resource_type";
    private static final String MARK_SELECT_RESOURCES_METHOD = "select_resources";
    private static final String MARK_RESOURCE_LIST_AVAILABLE = "resource_list_available";

    // properties
    private static final String PROPERTY_CONFIRM_DELETE_ROLE = "portal.rbac.message.confirmDeleteRole";
    private static final String PROPERTY_CONFIRM_DELETE_CONTROL = "portal.rbac.message.confirmDeleteControl";
    private static final String PROPERTY_ROLE_ALREADY_EXISTS = "portal.rbac.message.roleAlreadyExists";
    private static final String PROPERTY_ROLE_ATTRIBUTED = "portal.rbac.message.roleAttributed";
    private static final String PROPERTY_ROLE_CREATION_PAGETITLE = "portal.rbac.pageTitle.createRole";
    private static final String PROPERTY_ROLE_MODIFICATION_PAGETITLE = "portal.rbac.pageTitle.modifyRole";
    private static final String PROPERTY_ROLE_DESCRIPTION_PAGETITLE = "portal.rbac.pageTitle.viewRoleDescription";
    private static final String PROPERTY_CHOOSE_RESOURCES_PAGETITLE = "portal.rbac.pageTitle.chooseResources";
    private static final String PROPERTY_SELECT_RESOURCES_IDS_PAGETITLE = "portal.rbac.pageTitle.selectResourceIds";
    private static final String PROPERTY_SELECT_PERMISSIONS_PAGETITLE = "portal.rbac.pageTitle.selectPermissions";
    private static final String PROPERTY_MESSAGE_NO_ID_SELECTION_METHOD = "portal.rbac.message.resourceIdSelectionMethod";
    private static final String PROPERTY_MESSAGE_ID_LIST_EMPTY = "portal.rbac.message.resourceIdListEmpty";
    private static final String PROPERTY_MESSAGE_NO_PERMISSION_SELECTION_METHOD = "portal.rbac.message.permissionSelectionMethod";
    private static final String PROPERTY_MESSAGE_PERMISSION_LIST_EMPTY = "portal.rbac.message.permissionListEmpty";
    private static final String MESSAGE_ROLE_SPECIAL_CHARACTER = "portal.rbac.message.specialCharacters";
    private static final String PROPERTY_ROLES_PER_PAGE = "paginator.roles.itemsPerPage";

    // templates
    private static final String TEMPLATE_MANAGE_ROLES = "admin/rbac/manage_roles.html";
    private static final String TEMPLATE_CREATE_ROLE = "admin/rbac/create_role.html";
    private static final String TEMPLATE_MODIFY_ROLE = "admin/rbac/modify_role.html";
    private static final String TEMPLATE_VIEW_ROLE_DESCRIPTION = "admin/rbac/view_role_description.html";
    private static final String TEMPLATE_ADD_CONTROL_TO_ROLE = "admin/rbac/add_control_to_role.html";
    private static final String TEMPLATE_SELECT_PERMISSIONS = "admin/rbac/select_permissions.html";
    private static final String TEMPLATE_SELECT_RESOURCE_IDS = "admin/rbac/select_resource_ids.html";

    // jsp
    private static final String JSP_URL_ROLES_MANAGEMENT = "ManageRoles.jsp";
    private static final String JSP_URL_SELECT_PERMISSIONS = "SelectPermissions.jsp";
    private static final String JSP_URL_ROLE_DESCRIPTION = "ViewRoleDescription.jsp";
    private static final String JSP_URL_SELECT_SPECIFIC_IDS = "SelectSpecificIds.jsp";
    private static final String JSP_URL_REMOVE_ROLE = "jsp/admin/rbac/DoRemoveRole.jsp";
    private static final String JSP_URL_REMOVE_CONTROL_FROM_ROLE = "jsp/admin/rbac/DoRemoveControlFromRole.jsp";
    private int _nItemsPerPage;
    private int _nDefaultItemsPerPage;
    private String _strCurrentPageIndex;

    /**
     * Get the roles management page.
     * This page provides the list of all existing roles.
     * @param request the http request
     * @return the html code for the role management page
     */
    public String getManageRoles( HttpServletRequest request )
    {
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_ROLES_PER_PAGE, 10 );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        List<AdminRole> listRole = (List<AdminRole>) AdminRoleHome.findAll(  );
        Paginator paginator = new Paginator( listRole, _nItemsPerPage, getHomeUrl( request ),
                Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_ROLE_LIST, paginator.getPageItems(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_ROLES, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Get the role creation page.
     * This page provides a form to enter basic information about
     * the new role.
     * @param request the http request
     * @return the html code for the role creation page
     */
    public String getCreateRole( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_ROLE_CREATION_PAGETITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_ROLE, getLocale(  ) );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Perform the role creation.
     * The role key entered should not already exist.
     * The role key is mandatory.
     * @param request the http request
     * @return the url to forward to
     */
    public String doCreateRole( HttpServletRequest request )
    {
        String strRoleKey = request.getParameter( PARAMETER_ROLE_KEY );
        String strRoleDescription = request.getParameter( PARAMETER_ROLE_DESCRIPTION );

        if ( ( strRoleKey == null ) || ( strRoleKey.trim(  ).equals( "" ) ) || ( strRoleDescription == null ) ||
                ( strRoleDescription.trim(  ).equals( "" ) ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }
        else if ( AdminRoleHome.checkExistRole( strRoleKey ) )
        {
            return AdminMessageService.getMessageUrl( request, PROPERTY_ROLE_ALREADY_EXISTS, AdminMessage.TYPE_STOP );
        }
        else if ( !StringUtil.checkCodeKey( strRoleKey ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ROLE_SPECIAL_CHARACTER, AdminMessage.TYPE_STOP );
        }
        else
        {
            AdminRole role = new AdminRole(  );
            role.setKey( strRoleKey.trim(  ) );
            role.setDescription( strRoleDescription );
            AdminRoleHome.create( role );

            return JSP_URL_ROLE_DESCRIPTION + "?" + PARAMETER_ROLE_KEY + "=" + strRoleKey;
        }
    }

    /**
     * Get the role modification page.
     * This corresponds to the modification of the basic information linked to the role : key and description
     * @param request the http request
     * @return the html code for the modification page
     */
    public String getModifyRole( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_ROLE_MODIFICATION_PAGETITLE );

        String strRoleKey = request.getParameter( PARAMETER_ROLE_KEY );

        HashMap<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_ROLE, AdminRoleHome.findByPrimaryKey( strRoleKey ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_ROLE, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Performs the modification of the role's key and description.
     * The role key entered should not already exist.
     * The role key is mandatory.
     * It should update the key for all the entries in the role-resource association list.
     * @param request the http request
     * @return the url to forward to
     */
    public String doModifyRole( HttpServletRequest request )
    {
        String strOldRoleKey = request.getParameter( PARAMETER_ROLE_KEY_PREVIOUS );
        String strNewRoleKey = request.getParameter( PARAMETER_ROLE_KEY );
        String strRoleDescription = request.getParameter( PARAMETER_ROLE_DESCRIPTION );

        // check that new role key is valid
        if ( ( strNewRoleKey == null ) || ( strNewRoleKey.trim(  ).equals( "" ) ) || ( strRoleDescription == null ) ||
                ( strRoleDescription.trim(  ).equals( "" ) ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }
        else
        {
            if ( strOldRoleKey.equals( strNewRoleKey ) ) // if the key doesn't change, update the description
            {
                // update the role
                AdminRole role = AdminRoleHome.findByPrimaryKey( strOldRoleKey );
                role.setKey( strNewRoleKey );
                role.setDescription( strRoleDescription );
                AdminRoleHome.update( strOldRoleKey, role );
            }
            else // if the key changes, first check that the new key doesn't exist
            {
                if ( AdminRoleHome.checkExistRole( strNewRoleKey ) )
                {
                    return AdminMessageService.getMessageUrl( request, PROPERTY_ROLE_ALREADY_EXISTS,
                        AdminMessage.TYPE_STOP );
                }
                else
                {
                    // update the role
                    AdminRole role = AdminRoleHome.findByPrimaryKey( strOldRoleKey );
                    role.setKey( strNewRoleKey );
                    role.setDescription( strRoleDescription );
                    AdminRoleHome.update( strOldRoleKey, role );
                    AdminUserHome.updateUsersRole( strOldRoleKey, role );

                    //  update the role key in the role-resource associations
                    RBACHome.updateRoleKey( strOldRoleKey, strNewRoleKey );
                }
            }
        }

        return JSP_URL_ROLE_DESCRIPTION + "?" + PARAMETER_ROLE_KEY + "=" + strNewRoleKey;
    }

    /**
     * Get the confirmation page before deletion of a role
     * @param request the http request
     * @return the url of the confirmation page
     */
    public String doConfirmRemoveRole( HttpServletRequest request )
    {
        String strRoleKey = request.getParameter( PARAMETER_ROLE_KEY );

        String strDeleteUrl = JSP_URL_REMOVE_ROLE + "?" + PARAMETER_ROLE_KEY + "=" + strRoleKey;
        String strUrl = AdminMessageService.getMessageUrl( request, PROPERTY_CONFIRM_DELETE_ROLE, strDeleteUrl,
                AdminMessage.TYPE_CONFIRMATION );

        return strUrl;
    }

    /**
     * Perform the role deletion.
     * Also delete the resources linked to this role
     * @param request the http request
     * @return the url of the role management page
     */
    public String doRemoveRole( HttpServletRequest request )
    {
        String strRoleKey = request.getParameter( PARAMETER_ROLE_KEY );

        // check that no user has this role
        if ( AdminUserHome.checkRoleAttributed( strRoleKey ) )
        {
            return AdminMessageService.getMessageUrl( request, PROPERTY_ROLE_ATTRIBUTED, AdminMessage.TYPE_STOP );
        }
        else
        {
            // remove role
            AdminRoleHome.remove( strRoleKey );

            // remove resources entries for that role
            RBACHome.removeForRoleKey( strRoleKey );

            return JSP_URL_ROLES_MANAGEMENT;
        }
    }

    /**
     * Get the page describing a role and the resource associated
     * @param request the http request
     * @return the html code for the description page
     */
    public String getViewRoleDescription( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_ROLE_DESCRIPTION_PAGETITLE );

        String strRoleKey = request.getParameter( PARAMETER_ROLE_KEY );

        Collection<RBAC> listResources = RBACHome.findResourcesByCode( strRoleKey );
        I18nService.localizeCollection( listResources, getLocale(  ) );

        Collection<ResourceType> listResourceTypes = ResourceTypeManager.getResourceTypeList(  );
        I18nService.localizeCollection( listResourceTypes, getLocale(  ) );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_ROLE, AdminRoleHome.findByPrimaryKey( strRoleKey ) );
        model.put( MARK_CONTROLED_RESOURCE_LIST, listResources );
        model.put( MARK_RESOURCE_TYPE_LIST, listResourceTypes );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_VIEW_ROLE_DESCRIPTION, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Get the confirmation page before deletion of a control
     * @param request the http request
     * @return the url of the confirmation page
     */
    public String doConfirmRemoveControlFromRole( HttpServletRequest request )
    {
        String strIdControl = request.getParameter( PARAMETER_RBAC_ID );
        String strDeleteUrl = JSP_URL_REMOVE_CONTROL_FROM_ROLE + "?" + PARAMETER_RBAC_ID + "=" + strIdControl;
        String strUrl = AdminMessageService.getMessageUrl( request, PROPERTY_CONFIRM_DELETE_CONTROL, strDeleteUrl,
                AdminMessage.TYPE_CONFIRMATION );

        return strUrl;
    }

    /**
     * Perform the role deletion.
     * Also delete the resources linked to this role
     * @param request the http request
     * @return the url of the role management page
     */
    public String doRemoveControlFromRole( HttpServletRequest request )
    {
        String strIdControl = request.getParameter( PARAMETER_RBAC_ID );
        int nId = Integer.parseInt( strIdControl );

        RBAC rbac = RBACHome.findByPrimaryKey( nId );

        // remove control
        RBACHome.remove( nId );

        return JSP_URL_ROLE_DESCRIPTION + "?" + PARAMETER_ROLE_KEY + "=" + rbac.getRoleKey(  );
    }

    /**
     * Get the first page of the control addition to a role.
     * This page provides choice for resource selection.
     * 2 methods are provided :
     * <ul>
     * <li>wildcard selection : all resources of this type are selected.</li>
     * <li>specific selection : a page with the list of resource ids available is provided next for user choice.</li>
     * </ul>
     * @param request the http request
     * @return the html content for the resource selection method choice
     */
    public String getAddControlToRole( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_CHOOSE_RESOURCES_PAGETITLE );

        Map<String, Object> model = new HashMap<String, Object>(  );

        String strRoleKey = request.getParameter( PARAMETER_ROLE_KEY );
        String strResourceType = request.getParameter( PARAMETER_RESOURCE_TYPE );

        ResourceType resourceType = ResourceTypeManager.getResourceType( strResourceType );

        boolean bResourceListAvailable = true;
        List listResources = resourceType.getResourceIdService(  ).getResourceIdList( getLocale(  ) );

        if ( ( listResources == null ) || ( listResources.size(  ) == 0 ) )
        {
            bResourceListAvailable = false;
        }

        model.put( MARK_ROLE_KEY, strRoleKey );
        model.put( MARK_RESOURCE_TYPE, strResourceType );
        model.put( MARK_RESOURCE_LIST_AVAILABLE, bResourceListAvailable );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_ADD_CONTROL_TO_ROLE, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Perform the checks on the resource selection method and perform the suitable redirection.
     * <ul>
     * <li>If selection method is global (wilcard selection - parameter "all"), the user is redirected to the permission selection page.</li>
     * <li>If selection method is specific (id selection - parameter "choose"), the user is redirected to the resource id selection page.</li>
     * <li>If no selection method is found, or if it's neither parameter "all" nor "choose", the user is redirected to a error page.</li>
     * </ul>
     * @param request the http request
     * @return the url of the page to be redirected to
     */
    public String doSelectResources( HttpServletRequest request )
    {
        String strRoleKey = request.getParameter( PARAMETER_ROLE_KEY );
        String strResourceType = request.getParameter( PARAMETER_RESOURCE_TYPE );
        String strSelectionMethod = request.getParameter( PARAMETER_SELECT_RESOURCES_METHOD );

        if ( ( strSelectionMethod == null ) || ( strSelectionMethod.trim(  ).equals( "" ) ) )
        {
            return AdminMessageService.getMessageUrl( request, PROPERTY_MESSAGE_NO_ID_SELECTION_METHOD,
                AdminMessage.TYPE_STOP );
        }
        else if ( strSelectionMethod.equals( PARAMETER_SELECTION_METHOD_CHOOSE ) )
        {
            return JSP_URL_SELECT_SPECIFIC_IDS + "?" + PARAMETER_RESOURCE_TYPE + "=" + strResourceType + "&" +
            PARAMETER_ROLE_KEY + "=" + strRoleKey + "&" + PARAMETER_SELECT_RESOURCES_METHOD + "=" + strSelectionMethod;
        }
        else if ( strSelectionMethod.equals( PARAMETER_METHOD_SELECTION_ALL ) )
        {
            return JSP_URL_SELECT_PERMISSIONS + "?" + PARAMETER_RESOURCE_TYPE + "=" + strResourceType + "&" +
            PARAMETER_ROLE_KEY + "=" + strRoleKey + "&" + PARAMETER_SELECT_RESOURCES_METHOD + "=" + strSelectionMethod;
        }
        else
        {
            return AdminMessageService.getMessageUrl( request, PROPERTY_MESSAGE_NO_ID_SELECTION_METHOD,
                AdminMessage.TYPE_STOP );
        }
    }

    /**
     * Get the list of ids corresponding to the current resource type.
     * This allows to provide the selection of resource ids that should be controled for the current role.
     * @param request the http request
     * @return the html code for the list of ids to select
     */
    public String getSelectSpecificIds( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_SELECT_RESOURCES_IDS_PAGETITLE );

        String strRoleKey = request.getParameter( PARAMETER_ROLE_KEY );
        String strResourceType = request.getParameter( PARAMETER_RESOURCE_TYPE );
        String strSelectionMethod = request.getParameter( PARAMETER_SELECT_RESOURCES_METHOD );

        ResourceType resourceType = ResourceTypeManager.getResourceType( strResourceType );

        Map<String, Object> model = new HashMap<String, Object>(  );

        model.put( MARK_RESOURCE_ID_LIST, resourceType.getResourceIdService(  ).getResourceIdList( getLocale(  ) ) );
        model.put( MARK_ROLE_KEY, strRoleKey );
        model.put( MARK_RESOURCE_TYPE, strResourceType );
        model.put( MARK_SELECT_RESOURCES_METHOD, strSelectionMethod );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SELECT_RESOURCE_IDS, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Perform the check on the resource id list selected on the specific id selection page perform the suitable redirection.
     * <ul>
     * <li>If selection method is specific (id selection - parameter "choose"),
     *         <ul>
     *                 <li>if at least one id as been selected, the user is redirected to the permission selection page.</li>
     *                 <li>if no id as been selected, the user is redirected to an error page.</li>
     *         </ul>
     * <li>If selection method is global (wilcard selection - parameter "all"), the user is redirected to the permission selection page (This check could be avoided as is should not happen).</li>
     * <li>If no selection method is found, or if it's neither parameter "all" nor "choose", the user is redirected to a error page.</li>
     * </ul>
     * @param request the http request
     * @return the url of the page to be redirected to
     */
    public String doSelectResourcesFromIdsList( HttpServletRequest request )
    {
        String strRoleKey = request.getParameter( PARAMETER_ROLE_KEY );
        String strSelectionMethod = request.getParameter( PARAMETER_SELECT_RESOURCES_METHOD );
        String strResourceType = request.getParameter( PARAMETER_RESOURCE_TYPE );
        String[] strArrayResourceIds = request.getParameterValues( PARAMETER_RESOURCE_ID );

        String strUrl;

        // check that the selection method is "all" or "choose"
        // if method is "choose", check that we have at least one id checked
        if ( strSelectionMethod == null )
        {
            strUrl = AdminMessageService.getMessageUrl( request, PROPERTY_MESSAGE_NO_ID_SELECTION_METHOD,
                    AdminMessage.TYPE_STOP );
        }
        else if ( strSelectionMethod.equals( PARAMETER_METHOD_SELECTION_CHOOSE ) )
        {
            if ( ( strArrayResourceIds == null ) || ( strArrayResourceIds.length == 0 ) )
            {
                strUrl = AdminMessageService.getMessageUrl( request, PROPERTY_MESSAGE_ID_LIST_EMPTY,
                        AdminMessage.TYPE_STOP );
            }
            else
            {
                strUrl = JSP_URL_SELECT_PERMISSIONS + "?" + PARAMETER_RESOURCE_TYPE + "=" + strResourceType + "&" +
                    PARAMETER_ROLE_KEY + "=" + strRoleKey + "&" + PARAMETER_SELECT_RESOURCES_METHOD + "=" +
                    strSelectionMethod;

                for ( int i = 0; i < strArrayResourceIds.length; i++ )
                {
                    strUrl = strUrl + "&" + PARAMETER_RESOURCE_ID + "=" + strArrayResourceIds[i];
                }
            }
        }
        else if ( strSelectionMethod.equals( PARAMETER_METHOD_SELECTION_ALL ) )
        {
            strUrl = JSP_URL_SELECT_PERMISSIONS + "?" + PARAMETER_RESOURCE_TYPE + "=" + strResourceType + "&" +
                PARAMETER_ROLE_KEY + "=" + strRoleKey + "&" + PARAMETER_SELECT_RESOURCES_METHOD + "=" +
                strSelectionMethod;
        }
        else
        {
            strUrl = AdminMessageService.getMessageUrl( request, PROPERTY_MESSAGE_NO_ID_SELECTION_METHOD,
                    AdminMessage.TYPE_STOP );
        }

        return strUrl;
    }

    /**
     * Get the permission selection page.
     * 2 methods are provided :
     * <ul>
     * <li>wildcard selection : all permissions for this type are selected.</li>
     * <li>specific selection : the choice is to be made by the user in the list of available permissions.</li>
     * </ul>
     * @param request the http request
     * @return the html code for the permission selection page.
     */
    public String getSelectPermissions( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_SELECT_PERMISSIONS_PAGETITLE );

        String strRoleKey = request.getParameter( PARAMETER_ROLE_KEY );
        String strResourceType = request.getParameter( PARAMETER_RESOURCE_TYPE );
        String strSelectionMethod = request.getParameter( PARAMETER_SELECT_RESOURCES_METHOD );

        String[] strArrayResourceIds = request.getParameterValues( PARAMETER_RESOURCE_ID );

        // load the permission list for permission selection
        ReferenceList listPermissions = ResourceTypeManager.getPermissionsList( strResourceType, getLocale(  ) );

        Map<String, Object> model = new HashMap<String, Object>(  );

        // forward the resource id  list
        model.put( MARK_RESOURCE_ID_LIST, strArrayResourceIds );

        // load the permission list
        model.put( MARK_PERMISSIONS_LIST, listPermissions );

        // forward the role key, the resource type and the resource id selection method
        model.put( MARK_ROLE_KEY, strRoleKey );
        model.put( MARK_RESOURCE_TYPE, strResourceType );
        model.put( MARK_SELECT_RESOURCES_METHOD, strSelectionMethod );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SELECT_PERMISSIONS, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Perform the checks on the permission selection and redirects to the description of the role if ok.
     * <ul>
     * <li>If selection method is global (wilcard selection - parameter "all"), the correspondind entry is stored as a control for all resources previously selected. The user is redirected to the role description page.</li>
     * <li>If selection method is specific (id selection - parameter "choose"),
     *         <ul>
     *                 <li>if no permission is found, the user is redirected to an error page.</li>
     *                 <li>if at least one permission is found, the correspondind entry is stored as a control for all resources previously selected. The user is redirected to the role description page.</li>
     *         </ul>
     * </li>
     * <li>If no selection method is found, or if it's neither parameter "all" nor "choose", the user is redirected to a error page.</li>
     * </ul>
     * @param request the http request
     * @return the url of the page to be redirected to
     */
    public String doSelectPermissions( HttpServletRequest request )
    {
        String strRoleKey = request.getParameter( PARAMETER_ROLE_KEY );
        String strResourceType = request.getParameter( PARAMETER_RESOURCE_TYPE );
        String strResourcesSelectionMethod = request.getParameter( PARAMETER_SELECT_RESOURCES_METHOD );
        String strPermissionsSelectionMethod = request.getParameter( PARAMETER_SELECT_PERMISSIONS_METHOD );

        String[] strArrayResourceIds;
        String[] strArrayPermissionKeys;

        //  get the list of  resource ids selected (forward from previous screen, so no need for extensive checks)
        if ( ( strResourcesSelectionMethod != null ) &&
                strResourcesSelectionMethod.equals( PARAMETER_METHOD_SELECTION_ALL ) )
        {
            strArrayResourceIds = new String[1];
            strArrayResourceIds[0] = RBAC.WILDCARD_RESOURCES_ID;
        }
        else
        {
            strArrayResourceIds = request.getParameterValues( PARAMETER_RESOURCE_ID );
        }

        // check that the permission selection method is "all" or "choose"
        // if method is "choose", check that we have at least one id checked
        if ( strPermissionsSelectionMethod == null )
        {
            return AdminMessageService.getMessageUrl( request, PROPERTY_MESSAGE_NO_PERMISSION_SELECTION_METHOD,
                AdminMessage.TYPE_STOP );
        }
        else if ( strPermissionsSelectionMethod.equals( PARAMETER_METHOD_SELECTION_CHOOSE ) )
        {
            strArrayPermissionKeys = request.getParameterValues( PARAMETER_PERMISSION_KEY );

            if ( ( strArrayPermissionKeys == null ) || ( strArrayPermissionKeys.length == 0 ) )
            {
                return AdminMessageService.getMessageUrl( request, PROPERTY_MESSAGE_PERMISSION_LIST_EMPTY,
                    AdminMessage.TYPE_STOP );
            }
        }
        else if ( strPermissionsSelectionMethod.equals( PARAMETER_METHOD_SELECTION_ALL ) )
        {
            strArrayPermissionKeys = new String[1];
            strArrayPermissionKeys[0] = RBAC.WILDCARD_PERMISSIONS_KEY;
        }
        else
        {
            return AdminMessageService.getMessageUrl( request, PROPERTY_MESSAGE_NO_PERMISSION_SELECTION_METHOD,
                AdminMessage.TYPE_STOP );
        }

        // store the selected elements in database
        for ( int i = 0; i < strArrayResourceIds.length; i++ )
        {
            for ( int j = 0; j < strArrayPermissionKeys.length; j++ )
            {
                RBAC rbac = new RBAC(  );
                rbac.setRoleKey( strRoleKey );
                rbac.setResourceTypeKey( strResourceType );
                rbac.setResourceId( strArrayResourceIds[i] );
                rbac.setPermissionKey( strArrayPermissionKeys[j] );
                RBACHome.create( rbac );
            }
        }

        return JSP_URL_ROLE_DESCRIPTION + "?" + PARAMETER_ROLE_KEY + "=" + strRoleKey;
    }
}
