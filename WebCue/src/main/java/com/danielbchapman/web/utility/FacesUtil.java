package com.danielbchapman.web.utility;

import javax.faces.FactoryFinder;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * http://balusc.blogspot.com/2006/06/communication-in-jsf.html#AccessingTheFacesContextInsideHttpServletOrFilter
 * 
 * @author chapm_000
 * @since Sep 26, 2013
 * @copyright Copyright 2013 Daniel B. Chapman/Harrison Fortier. All rights reserved.
 *            PROPRIETARY/CONFIDENTAL. Use is subject to license terms.
 * 
 */
public class FacesUtil
{

	// Getters -----------------------------------------------------------------------------------

	public static FacesContext getFacesContext(HttpServletRequest request, HttpServletResponse response)
	{
		// Get current FacesContext.
		FacesContext facesContext = FacesContext.getCurrentInstance();

		// Check current FacesContext.
		if(facesContext == null)
		{

			// Create new Lifecycle.
			LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
					.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
			Lifecycle lifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);

			// Create new FacesContext.
			FacesContextFactory contextFactory = (FacesContextFactory) FactoryFinder
					.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
			facesContext = contextFactory.getFacesContext(request.getSession().getServletContext(), request, response,
					lifecycle);

			// Create new View.
			UIViewRoot view = facesContext.getApplication().getViewHandler().createView(facesContext, "");
			facesContext.setViewRoot(view);

			// Set current FacesContext.
			FacesContextWrapper.setCurrentInstance(facesContext);
		}

		return facesContext;
	}

	// Helpers -----------------------------------------------------------------------------------

	// Wrap the protected FacesContext.setCurrentInstance() in a inner class.
	private static abstract class FacesContextWrapper extends FacesContext
	{
		protected static void setCurrentInstance(FacesContext facesContext)
		{
			FacesContext.setCurrentInstance(facesContext);
		}
	}

}