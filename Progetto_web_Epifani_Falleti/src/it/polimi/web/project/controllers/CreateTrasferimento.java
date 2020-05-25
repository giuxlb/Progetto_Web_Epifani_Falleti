package it.polimi.web.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.web.project.DAO.TrasferimentoDao;
import it.polimi.web.project.beans.User;
import it.polimi.web.project.utils.ConnectionHandler;

/**
 * Servlet implementation class CreateTrasferimento
 */
@WebServlet("/CreateTrasferimento")
public class CreateTrasferimento extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Connection connection = null;
	private TemplateEngine templateEngine;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreateTrasferimento() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Redirect the user to home if not logged in
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			String loginpath = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(loginpath);
			return;
		}

		boolean isRequestBad = false;
		Integer DestUserID = null;
		Integer DestContoID = null;
		Integer amount = null;
		String causale = null;
		try {
			DestUserID = Integer.parseInt(request.getParameter("destUserID"));
			DestContoID = Integer.parseInt(request.getParameter("destContoID"));
			amount = Integer.parseInt(request.getParameter("importo"));
			causale = StringEscapeUtils.escapeJava(request.getParameter("causale"));
			isRequestBad = causale.isEmpty() || DestUserID < 0 || DestContoID < 0 || amount < 0;
		} catch (NumberFormatException | NullPointerException e) {
			isRequestBad = true;
			e.printStackTrace();
		}
		if (isRequestBad) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
			return;
		}

		User user = (User) session.getAttribute("user");
		TrasferimentoDao trasferimentoDao = new TrasferimentoDao(connection);
		Integer UserID = user.getId();
		Integer ContoID = (Integer) session.getAttribute("ContoID");
		// Date data = System.currentTimeMillis();
		System.out.println("Sono qui");
		int trasferimentoValue = -1;
		try {
			trasferimentoValue = trasferimentoDao.createTrasferimento(DestUserID, DestContoID, UserID, ContoID, amount,
					causale);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad request");
		}

		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		String path;
		switch (trasferimentoValue) {

		case (0):
			ctx.setVariable("errorMsg", "The User ID you entered doesn't own that bank account");
			path = "/StatoConto.html";
			templateEngine.process(path, ctx, response.getWriter());
			break;
		case (1):
			ctx.setVariable("errorMsg", "The amount you entered isn't available in your balance");
			path = "/StatoConto.html";
			templateEngine.process(path, ctx, response.getWriter());
			break;
		case (2):
			path = getServletContext().getContextPath() + "/GoToConfermaTrasferimento";
			session.setAttribute("destContoID", DestContoID);
			response.sendRedirect(path);
		}

	}

}
