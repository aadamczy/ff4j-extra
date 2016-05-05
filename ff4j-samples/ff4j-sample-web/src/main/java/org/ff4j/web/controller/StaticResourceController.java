package org.ff4j.web.controller;

/*
 * #%L
 * ff4j-sample-web
 * %%
 * Copyright (C) 2013 - 2016 FF4J
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import static org.ff4j.web.embedded.ConsoleConstants.CONTENT_TYPE_CSS;
import static org.ff4j.web.embedded.ConsoleConstants.CONTENT_TYPE_FONT;
import static org.ff4j.web.embedded.ConsoleConstants.CONTENT_TYPE_JS;
import static org.ff4j.web.embedded.ConsoleConstants.CONTENT_TYPE_TEXT;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ff4j.web.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;

/**
 * Load static resource and create response, overriding content type.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class StaticResourceController extends AbstractController {
	
	/** Logger for this class. */
    public static final Logger LOGGER = LoggerFactory.getLogger(StaticResourceController.class);
    
    /** Eternal cache for css. */
	private Map < String, String > cssFiles = new HashMap< String, String >();
	
	/** Eternal cache for js. */
	private Map < String, String > jsFiles = new HashMap< String, String >();
	
	/** Eternal cache for js. */
	private Map < String, String > fontFiles = new HashMap< String, String >();
	
	/** Eternal cache for images. */
	private Map < String, String > images = new HashMap< String, String >();
	
	/** {@inheritDoc} */
	public void process(HttpServletRequest req, HttpServletResponse res, TemplateEngine engine)
	throws IOException {
		
		// static/{type}/{fileName}
		String pathInfo = req.getPathInfo();
		String[] pathParts = pathInfo.split("/");
        
		// By Convention the fileSystem will follow the same pattern
    	if (pathParts.length >=3) {
			String resourceType = pathParts[2];
			String resourceName = pathParts[pathParts.length - 1];

			if ("css".equalsIgnoreCase(resourceType)) {
				serveCss(res, pathInfo, resourceName);
			} else if ("js".equalsIgnoreCase(resourceType)) {
				serveJs(res, pathInfo, resourceName);
				
			} else if ("font".equalsIgnoreCase(resourceType)) {
				serveFont(res, pathInfo, resourceName);
				
			} else if ("img".equalsIgnoreCase(resourceType)) {
				serveImage(res, pathInfo, resourceName);
			} else {
				notFound(res, pathInfo);
			}
    	} else {
    		notFound(res, pathInfo);
    	}
	}

	/* 
	 * Load CSS Files
	 */
	private void serveCss(HttpServletResponse res, String pathInfo, String resourceName)
	throws IOException {
		// if first access put it into cache
		if (!cssFiles.containsKey(resourceName)) {
        	cssFiles.put(resourceName,FileUtils.loadFileAsString(pathInfo));
		}
		if (null == cssFiles.get(resourceName)) {
			notFound(res, pathInfo);
		} else {
			res.setContentType(CONTENT_TYPE_CSS);
	        res.getWriter().println(cssFiles.get(resourceName));
		}
	}
	
	/*
	 * Load font files
	 */
	private void serveFont(HttpServletResponse res, String pathInfo, String resourceName)
	throws IOException {
		if (!fontFiles.containsKey(resourceName)) {
			fontFiles.put(resourceName,FileUtils.loadFileAsString(pathInfo));
		}
		if (null == fontFiles.get(resourceName)) {
			notFound(res, pathInfo);
		} else if (null != FileUtils.getFileExtension(resourceName)) {
			res.setContentType(CONTENT_TYPE_FONT);
	        res.getWriter().println(fontFiles.get(resourceName));
		} else {
			// List files in the directory
			res.setContentType(CONTENT_TYPE_TEXT);
	        res.getWriter().println(fontFiles.get(resourceName));
		}
	}
	
	/*
	 * Load Js files
	 */
	private void serveJs(HttpServletResponse res, String pathInfo, String resourceName)
	throws IOException {
		if (!jsFiles.containsKey(resourceName)) {
			jsFiles.put(resourceName, FileUtils.loadFileAsString(pathInfo));
		}
		if (null == jsFiles.get(resourceName)) {
			notFound(res, pathInfo);
		} else {
			res.setContentType(CONTENT_TYPE_JS);
			res.getWriter().println(jsFiles.get(resourceName));
		}
	}
	
	/*
	 * Load Images
	 */
	private void serveImage(HttpServletResponse res, String pathInfo, String resourceName)
	throws IOException {
		if (!images.containsKey(resourceName)) {
			images.put(resourceName, "data:image/"  //
					+ FileUtils.getFileExtension(resourceName) //
					+ ";base64," + FileUtils.loadFileAsBase64(pathInfo));
		}
		if (null == images.get(resourceName)) {
			notFound(res, pathInfo);
		} else {
			res.setContentType(CONTENT_TYPE_TEXT);
			res.getWriter().println(images.get(resourceName));
		}
		
	}
	
	private void notFound(HttpServletResponse res, String pathInfo)
	throws IOException {
		res.setContentType(CONTENT_TYPE_TEXT);
        res.getWriter().println("Ressource [" + pathInfo + "] not found");
	}
}
