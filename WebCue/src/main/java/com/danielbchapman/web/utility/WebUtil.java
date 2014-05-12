package com.danielbchapman.web.utility;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * A utility class that has functional methods for repeated tasks to streamline them. This can
 * and should be further streamlined to support internationalization.
 * 
 * @author Daniel B. Chapman
 * @since Mar 17, 2012
 * @copyright Copyright 2012 Daniel B. Chapman/Harrison Fortier. All rights reserved.
 *            PROPRIETARY/CONFIDENTAL. Use is subject to license terms.
 */
public class WebUtil
{
	public static String ID = "target";
	private static Logger FACES_MESSAGE_LOGGER = java.util.logging.Logger.getLogger("Faces Message Logger");
	private static final String detail = "Detail";

	/**
	 * This is a string/null safe method. Depending on the converter
	 * used sometimes JSF will return a string, other times correctly
	 * casing to a long. This saves the headaches.
	 * 
	 * @param ui the UIComponent to query
	 * @param key the key to query against
	 * @return the BigInteger associated with this attribute
	 */
	public static BigInteger attribute(UIComponent ui, String key)
	{
		Object obj = ui.getAttributes().get(key);
		if(obj == null)
			return null;

		if(obj instanceof String)
			return new BigInteger((String) obj);

		if(obj instanceof Long)
			return BigInteger.valueOf(((Number) obj).longValue());

		if(obj instanceof BigInteger)
			return (BigInteger) obj;

		return null;
	}

	public static BigInteger attributeId(ActionEvent evt)
	{
		if(evt == null)
			return null;

		return attribute(evt.getComponent(), ID);
	}

	/**
	 * @param value the value to search for
	 * @param values the values to be searched
	 * @return true if the collection contains the value, false otherwise
	 */
	public static boolean contains(String value, Collection<String> values)
	{
		if(values == null)
			return false;

		for(String x : values)
			if(x.contains(value))
				return true;

		return false;
	}

	/**
	 * @param value the value to search for
	 * @param values the values to be searched ignorming case
	 * @return true if the collection contains the value, false otherwise
	 */
	public static boolean containsIgnoreCase(String value, Collection<String> values)
	{
		if(values == null)
			return false;

		value = value.toLowerCase();
		for(String x : values)
			if(x.toLowerCase().contains(value))
				return true;

		return false;
	}

	/**
	 * @param url the url (eg. folder/index.xhtml)
	 * @return the context path for this URL e.g. "/server/application/folder/index.html"
	 */
	public static String contextUrl(String url)
	{
		if(FacesContext.getCurrentInstance() == null)
		{
			// FIXME -> I smell a Jboss problem here
			String value = "/betaweb/" + url;
			return value;
		}
		else
		{
			String value = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/" + url;
			return value;
		}

	}

	public static String encodeUrlUTF8(String location, Map<String, String> parameters)
	{
		String utf8 = "UTF-8";
		StringBuilder builder = new StringBuilder();
		builder.append(location);
		builder.append("?");
		boolean first = true;
		try
		{
			for(Map.Entry<String, String> entry : parameters.entrySet())
			{
				if(!first)
					builder.append("&");
				builder.append(URLEncoder.encode(entry.getKey(), utf8));
				builder.append("=");
				builder.append(URLEncoder.encode(entry.getValue(), utf8));
			}
		}
		catch(UnsupportedEncodingException e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}

		return builder.toString();
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(Class<T> clazz)
	{
		char[] characters = clazz.getSimpleName().toCharArray();
		characters[0] = Character.toLowerCase(characters[0]);
		return (T) getBean(new String(characters));
	}

	/**
	 * <em>WARNING</em>
	 * <p>
	 * This method will automatically cast a ServeletRequest to an HttpServlet request.
	 * </p>
	 * Normally I would find this offensive, but we should never actually
	 * run into a raw ServletRequest as all our filters will be HttpServletRequests
	 * 
	 * @param request
	 * @param clazz
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(ServletRequest request, Class<T> clazz)
	{
		HttpServletRequest cast = (HttpServletRequest) request;
		char[] characters = clazz.getSimpleName().toCharArray();
		characters[0] = Character.toLowerCase(characters[0]);

		Object bean = cast.getSession().getAttribute(new String(characters));
		return (T) bean;
	}

	/**
	 * Get an object from the ELContext and return it. This
	 * is primarily used for getting a bean, but theoretically it
	 * could resolve anything in the context.
	 * 
	 * @param name
	 *          the managed bean name
	 * @return a link to the object in the web
	 */
	public static Object getBean(String name)
	{
		return FacesContext.getCurrentInstance().getELContext().getELResolver().getValue(
				FacesContext.getCurrentInstance().getELContext(), null, name);
	}

	public static Locale getCurrentLocale()
	{
		return Locale.US;
	}

	public static String getFullPath(String url)
	{
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
				.getRequest();

		try
		{
			String base = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), request
					.getContextPath()).toString();

			return base + url;
		}
		catch(MalformedURLException e)
		{
			e.printStackTrace(); // FFS Really?
		}
		return null;
	}

	public static HashMap<String, String> getParameters(HttpServletRequest request)
	{
		HashMap<String, String> map = new HashMap<>();
		Enumeration<String> req = request.getParameterNames();
		if(req != null)
			while(req.hasMoreElements())
			{
				String key = req.nextElement();
				map.put(key, request.getParameter(key));
			}

		return map;
	}

	public static HttpSession getSession()
	{
		if(FacesContext.getCurrentInstance() != null)
			return (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		else
			return null;
	}

	/**
	 * Raise an error with SEVERE to the application.
	 * 
	 * @param clazz the class to manage the resource bundle
	 * @param key the key for the bundle
	 * @see {@link #raiseMessage(javax.faces.application.FacesMessage.Severity, Class, String, String, Object...)}
	 */
	public static void raiseError(Class<?> clazz, String key)
	{
		raiseError(clazz, key, null, (Object[]) null);
	}
	/**
	 * Raise an error with SEVERE to the application.
	 * 
	 * @param clazz the class to manage the resource bundle
	 * @param key the key for the bundle
	 * @param params the parameters for the message bundle
	 * @see {@link #raiseMessage(javax.faces.application.FacesMessage.Severity, Class, String, String, Object...)}
	 */
	public static void raiseError(Class<?> clazz, String key, Object... params)
	{
		raiseError(clazz, key, null, params);
	}

	/**
	 * Raise an error with SEVERE to the application.
	 * 
	 * @param clazz the class to manage the resource bundle
	 * @param key the key for the bundle
	 * @param id the ID of the faces component
	 * @see {@link #raiseMessage(javax.faces.application.FacesMessage.Severity, Class, String, String, Object...)}
	 */
	public static void raiseError(Class<?> clazz, String key, String id)
	{
		raiseError(clazz, key, id, (Object[]) null);
	}

	/**
	 * Raise an error with SEVERE to the application.
	 * 
	 * @param clazz the class to manage the resource bundle
	 * @param key the key for the bundle
	 * @param id the ID of the faces component
	 * @param params the parameters for the message bundle
	 * @see {@link #raiseMessage(javax.faces.application.FacesMessage.Severity, Class, String, String, Object...)}
	 */
	public static void raiseError(Class<?> clazz, String key, String id, Object... params)
	{
		raiseMessage(FacesMessage.SEVERITY_ERROR, clazz, key, id, params);
	}

	/**
	 * Raise an error directly to a component (for binding error messages)
	 * 
	 * @param component the component to bind to
	 * @param key the message to display (title
	 * @param params parameters to fill the phrase with @see {@link Pattern}
	 */
	public static void raiseError(Class<?> clazz, UIComponent component, String key, Object... params)
	{
		raiseError(clazz, key, component.getClientId(), params);
	}

	/**
	 * Raise an error with WARNING to the application.
	 * 
	 * @param clazz the class to manage the resource bundle
	 * @param key the key for the bundle
	 * @see {@link #raiseMessage(javax.faces.application.FacesMessage.Severity, Class, String, String, Object...)}
	 */
	public static void raiseFatal(Class<?> clazz, String key)
	{
		raiseFatal(clazz, key, null, (Object[]) null);
	}

	/**
	 * Raise an error with WARNING to the application.
	 * 
	 * @param clazz the class to manage the resource bundle
	 * @param key the key for the bundle
	 * @param params the parameters for the message bundle
	 * @see {@link #raiseMessage(javax.faces.application.FacesMessage.Severity, Class, String, String, Object...)}
	 */
	public static void raiseFatal(Class<?> clazz, String key, Object... params)
	{
		raiseFatal(clazz, key, null, params);
	}

	/**
	 * Raise an error with WARNING to the application.
	 * 
	 * @param clazz the class to manage the resource bundle
	 * @param key the key for the bundle
	 * @param id the ID of the faces component
	 * @see {@link #raiseMessage(javax.faces.application.FacesMessage.Severity, Class, String, String, Object...)}
	 */
	public static void raiseFatal(Class<?> clazz, String key, String id)
	{
		raiseFatal(clazz, key, id, (Object[]) null);
	}

	/**
	 * Raise an error with FATAL to the application.
	 * 
	 * @param clazz the class to manage the resource bundle
	 * @param key the key for the bundle
	 * @param id the ID of the faces component
	 * @param params the parameters for the message bundle
	 * @see {@link #raiseMessage(javax.faces.application.FacesMessage.Severity, Class, String, String, Object...)}
	 */
	public static void raiseFatal(Class<?> clazz, String key, String id, Object... params)
	{
		raiseMessage(FacesMessage.SEVERITY_FATAL, clazz, key, id, params);
	}

	/**
	 * Raise an error with INFO to the application.
	 * 
	 * @param clazz the class to manage the resource bundle
	 * @param key the key for the bundle
	 * @see {@link #raiseMessage(javax.faces.application.FacesMessage.Severity, Class, String, String, Object...)}
	 */
	public static void raiseInfo(Class<?> clazz, String key)
	{
		raiseInfo(clazz, key, null, (Object[]) null);
	}

	/**
	 * Raise an error with INFO to the application.
	 * 
	 * @param clazz the class to manage the resource bundle
	 * @param key the key for the bundle
	 * @param params the parameters for the message bundle
	 * @see {@link #raiseMessage(javax.faces.application.FacesMessage.Severity, Class, String, String, Object...)}
	 */
	public static void raiseInfo(Class<?> clazz, String key, Object... params)
	{
		raiseInfo(clazz, key, null, params);
	}

	/**
	 * Raise an error with INFO to the application.
	 * 
	 * @param clazz the class to manage the resource bundle
	 * @param key the key for the bundle
	 * @param id the ID of the faces component
	 * @see {@link #raiseMessage(javax.faces.application.FacesMessage.Severity, Class, String, String, Object...)}
	 */
	public static void raiseInfo(Class<?> clazz, String key, String id)
	{
		raiseInfo(clazz, key, id, (Object[]) null);
	}

	/**
	 * Raise an error with INFO to the application.
	 * 
	 * @param clazz the class to manage the resource bundle
	 * @param key the key for the bundle
	 * @param id the ID of the faces component
	 * @param params the parameters for the message bundle
	 * @see {@link #raiseMessage(javax.faces.application.FacesMessage.Severity, Class, String, String, Object...)}
	 */
	public static void raiseInfo(Class<?> clazz, String key, String id, Object... params)
	{
		raiseMessage(FacesMessage.SEVERITY_INFO, clazz, key, id, params);
	}

	/**
	 * Raises a faces message with no target (good for global messages). This method assumes no
	 * parameters for the message bundle.
	 * 
	 * <pre>
	 * Example use:
	 * raiseMessage(SEVERE, foo.bar, "badUsername");
	 * 
	 *   might raise a message of ->
	 *   SEVERE
	 *     Summary: "Bad username or password"
	 *     Detail: "The username or password was invalid, please try again,
	 *     after 3 attempts your house will explode"
	 * </pre>
	 * 
	 * @see {@link WebUtil#raiseMessage(javax.faces.application.FacesMessage.Severity, Class, String, String)}
	 * @param severity the severity of this error.
	 * @param clazz the class to locate the resource bundle
	 * @param key the key (prefix) for this error message
	 * 
	 */
	@SuppressWarnings("unused")
	private static void raiseMessage(FacesMessage.Severity severity, Class<?> clazz, String key)
	{
		raiseMessage(severity, clazz, key, (Object[]) null);
	}

	/**
	 * Raises a faces message with no target (good for global messages).
	 * 
	 * <pre>
	 * Example use:
	 * raiseMessage(SEVERE, foo.bar, "badUsername", username, password);
	 * 
	 *   might raise a message of ->
	 *   SEVERE
	 *     Summary: "Bad username or password"
	 *     Detail: "The username 'taco' was invalid for the password 'bell'"
	 * </pre>
	 * 
	 * @see {@link WebUtil#raiseMessage(javax.faces.application.FacesMessage.Severity, Class, String, String)}
	 * @param severity the severity of this error.
	 * @param clazz the class to locate the resource bundle
	 * @param key the key (prefix) for this error message
	 * @param params an array of objects to pass to the message bundle for specific errors
	 * 
	 */
	private static void raiseMessage(FacesMessage.Severity severity, Class<?> clazz, String key, Object... params)
	{
		raiseMessage(severity, clazz, key, null, (Object[]) null);
	}

	@SuppressWarnings("unused")
	private static void raiseMessage(FacesMessage.Severity severity, Class<?> clazz, String key, String id)
	{
		raiseMessage(severity, clazz, key, id, (Object[]) null);
	}

	/**
	 * <b>MASTER METHOD</b><br />
	 * Raise an internationalized message to the application.
	 * 
	 * @see {@link com.danielbchapman.web.utility.Internationalization}
	 * 
	 *      Raises a faces message with no target (good for global messages).
	 * 
	 *      <pre>
	 * Example use:
	 * raiseMessage(SEVERE, foo.bar, "badUsername", "faces_id", username, password);
	 * 
	 *   might raise a message of ->
	 *   SEVERE
	 *     Summary: "Bad username or password"
	 *     Detail: "The username 'taco' was invalid for the password 'bell'"
	 *   with a target of "faces_id" for the message system
	 * </pre>
	 * 
	 * @see {@link WebUtil#raiseMessage(javax.faces.application.FacesMessage.Severity, Class, String, String)}
	 * @param severity the severity of this error.
	 * @param clazz the class to locate the resource bundle
	 * @param key the key (prefix) for this error message
	 * @param id the target component to report on
	 * @param params an array of objects to pass to the message bundle for specific errors
	 */
	private static void raiseMessage(FacesMessage.Severity severity, Class<?> clazz, String key, String id,
			Object... params)
	{
		String detail = key + WebUtil.detail;
		String keyValue = Internationalization.getMessage(clazz, key, params);
		String detailValue = Internationalization.getMessage(clazz, detail, params);

		FacesContext.getCurrentInstance().addMessage(id, new FacesMessage(severity, keyValue, detailValue));
	}

	public static void raiseMessage(String summary, String detail, FacesMessage.Severity severity, String forComponent)
	{
		FacesMessage message = new FacesMessage(severity, summary, detail);
		FacesContext.getCurrentInstance().addMessage(forComponent, message);
	}

	/**
	 * Raise an error with WARNING to the application.
	 * 
	 * @param clazz the class to manage the resource bundle
	 * @param key the key for the bundle
	 * @see {@link #raiseMessage(javax.faces.application.FacesMessage.Severity, Class, String, String, Object...)}
	 */
	public static void raiseWarning(Class<?> clazz, String key)
	{
		raiseWarning(clazz, key, null, (Object[]) null);
	}

	/**
	 * Raise an error with WARNING to the application.
	 * 
	 * @param clazz the class to manage the resource bundle
	 * @param key the key for the bundle
	 * @param params the parameters for the message bundle
	 * @see {@link #raiseMessage(javax.faces.application.FacesMessage.Severity, Class, String, String, Object...)}
	 */
	public static void raiseWarning(Class<?> clazz, String key, Object... params)
	{
		raiseWarning(clazz, key, null, params);
	}

	/**
	 * Raise an error with WARNING to the application.
	 * 
	 * @param clazz the class to manage the resource bundle
	 * @param key the key for the bundle
	 * @param id the ID of the faces component
	 * @see {@link #raiseMessage(javax.faces.application.FacesMessage.Severity, Class, String, String, Object...)}
	 */
	public static void raiseWarning(Class<?> clazz, String key, String id)
	{
		raiseWarning(clazz, key, id, (Object[]) null);
	}

	/**
	 * Raise an error with WARNING to the application.
	 * 
	 * @param clazz the class to manage the resource bundle
	 * @param key the key for the bundle
	 * @param id the ID of the faces component
	 * @param params the parameters for the message bundle
	 * @see {@link #raiseMessage(javax.faces.application.FacesMessage.Severity, Class, String, String, Object...)}
	 */
	public static void raiseWarning(Class<?> clazz, String key, String id, Object... params)
	{
		raiseMessage(FacesMessage.SEVERITY_WARN, clazz, key, id, params);
	}

	/**
	 * Redirect the user to this path. Only use this from FacesContext aware (beans), filters
	 * require the other methods.
	 * 
	 * @param urlPart
	 */
	public static void redirect(String urlPart)
	{
		String path = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + urlPart;
		try
		{
			FacesContext.getCurrentInstance().getExternalContext().redirect(path);
		}
		catch(IOException e)
		{
			throw new RuntimeException("Unable to redirect to: " + urlPart + "\n " + e.getMessage(), e);
		}
	}

	public static void redirect(String urlPart, HttpServletRequest request, HttpServletResponse response)
	{
		String path = request.getContextPath();
		if(!urlPart.startsWith("/"))
			path = path + "/" + urlPart;
		else
			path = path + urlPart;

		try
		{
			response.sendRedirect(path);
		}
		catch(IOException e)
		{
			throw new RuntimeException("Unable to redirect to: " + urlPart + "\n " + e.getMessage(), e);
		}
	}

	public static void redirect(String urlPart, ServletRequest req, ServletResponse resp)
	{
		redirect(urlPart, (HttpServletRequest) req, (HttpServletResponse) resp);
	}
}
