package org.twdata.proxy;

import com.atlassian.templaterenderer.RenderingException;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.RequestConstants;
import com.opensymphony.module.sitemesh.util.OutputConverter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class DecoratorFilter implements Filter
{
    private static final String ADMIN_PATH = "/admin.vmd";
    private final TemplateRenderer templateRenderer;

    public DecoratorFilter(TemplateRenderer templateRenderer)
    {
        this.templateRenderer = templateRenderer;
    }

    public void init(FilterConfig filterConfig) throws ServletException
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException
    {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)res;
        Page page = (Page) request.getAttribute(RequestConstants.PAGE);
        if (page != null)
        {
            applyDecoratorUsingVelocity(request, page, response);
        }
        else
        {
            String servletPath = (String) request.getAttribute("javax.servlet.include.servlet_path");
            if (servletPath == null)
            {
                servletPath = request.getServletPath();
            }
            throw new ServletException("No sitemesh page to decorate. This servlet should not be invoked directly. " +
                "The path invoked was " + servletPath);
        }
    }

    private void applyDecoratorUsingVelocity(HttpServletRequest request, Page page, HttpServletResponse response) throws
        IOException
    {
        String template;
        String pathInfo = (String) request.getAttribute("javax.servlet.include.path_info");
        if (pathInfo == null)
        {
            pathInfo = request.getPathInfo();
        }
        if (pathInfo != null && pathInfo.endsWith(ADMIN_PATH))
        {
            template = "/templates/admin.vmd";
        }
        else
        {
            template = "/templates/general.vm";
        }

        Map<String, Object> velocityParams = getVelocityParams(request, page, response);

        final PrintWriter writer = response.getWriter();
        try
        {
            response.setContentType("text/html");
            templateRenderer.render(template,  velocityParams, writer);
        }
        catch (RenderingException e)
        {
            writer.write("Exception rendering velocity file " + template);
            writer.write("<br><pre>");
            e.printStackTrace(writer);
            writer.write("</pre>");
        }
    }

    private Map<String, Object> getVelocityParams(HttpServletRequest request, Page page, HttpServletResponse response)
        throws IOException
    {
        Map<String, Object> velocityParams = new HashMap<String, Object>();

        velocityParams.put("page", page);
        velocityParams.put("titleHtml", page.getTitle());

        StringWriter bodyBuffer = new StringWriter();
        page.writeBody(OutputConverter.getWriter(bodyBuffer));
        velocityParams.put("bodyHtml", bodyBuffer);

        if (page instanceof HTMLPage)
        {
            HTMLPage htmlPage = (HTMLPage) page;
            StringWriter buffer = new StringWriter();
            htmlPage.writeHead(OutputConverter.getWriter(buffer));
            velocityParams.put("headHtml", buffer.toString());
        }

        velocityParams.put("request", request);
        velocityParams.put("response", response);   // as of 2.5.0 both req and resp must be available in all apps
        return velocityParams;
    }

    public void destroy()
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
