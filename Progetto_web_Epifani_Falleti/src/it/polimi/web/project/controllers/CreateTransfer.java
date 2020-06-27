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

import java.util.List;

import it.polimi.web.project.DAO.TransferDao;
import it.polimi.web.project.beans.*;
import it.polimi.web.project.utils.ConnectionHandler;

/**
 * Servlet implementation class CreateTrasferimento
 */
@WebServlet("/CreateTransfer")
public class CreateTransfer extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Connection connection = null;
	private TemplateEngine templateEngine;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreateTransfer() {
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
			String loginpath = getServletContext().getContextPath() + "/GoToIndex";
			response.sendRedirect(loginpath);
			return;
		}

		boolean isRequestBad = false;
		Integer destUserID = null;
		Integer destBankAccountID = null;
		Integer amount = null;
		String causal = null;
		try {
			destUserID = Integer.parseInt(request.getParameter("destUserID"));
			destBankAccountID = Integer.parseInt(request.getParameter("destBankAccountID"));
			amount = Integer.parseInt(request.getParameter("amount"));
			causal = StringEscapeUtils.escapeJava(request.getParameter("causal"));
			isRequestBad = causal.isEmpty() || destUserID < 0 || destBankAccountID < 0 || amount < 0;
		} catch (NumberFormatException | NullPointerException e) {
			isRequestBad = true;
			e.printStackTrace();
		}
		if (isRequestBad) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
			return;
		}

		User user = (User) session.getAttribute("user");
		TransferDao transferDao = new TransferDao(connection);
		Integer userID = user.getId();
		Integer bankAccountID = (Integer) session.getAttribute("bankAccountID");
		// Date data = System.currentTimeMillis();
		int transferValue = -1;
		try {
			transferValue = transferDao.createTransfer(destUserID, destBankAccountID, userID, bankAccountID, amount,
					causal);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad request");
		}

		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		String path;
		List<Transfer> t = (List<Transfer>) session.getAttribute("transfers");
		ctx.setVariable("transfers", t);
		switch (transferValue) {

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
			path = getServletContext().getContextPath() + "/GoToTransferConfirm";
			session.setAttribute("destBankAccountID", destBankAccountID);
			response.sendRedirect(path);
		}

	}

}
