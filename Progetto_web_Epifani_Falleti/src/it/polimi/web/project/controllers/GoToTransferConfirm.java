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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.web.project.DAO.BankAccountDao;
import it.polimi.web.project.beans.BankAccount;
import it.polimi.web.project.utils.ConnectionHandler;

/**
 * Servlet implementation class GoToConfermaTrasferimento
 */
@WebServlet("/GoToTransferConfirm")
public class GoToTransferConfirm extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GoToTransferConfirm() {
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String indexPath = getServletContext().getContextPath() + "/GoToIndex";
		HttpSession session = request.getSession();
		if (session.getAttribute("user") == null || session.isNew()) {
			response.sendRedirect(indexPath);
		}

		Integer destBankAccountID = (Integer) session.getAttribute("destBankAccountID");
		Integer bankAccountID = (Integer) session.getAttribute("bankAccountID");

		BankAccountDao cdao = new BankAccountDao(connection);
		BankAccount bankAccount = new BankAccount();
		try {
			bankAccount = cdao.findBankAccountByID(destBankAccountID);
		} catch (SQLException e) {
			// for debugging only e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover transfers");
			return;
		}

		String path = "/ConfermaTrasferimento.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("balanceDest", bankAccount.getBalance());
		ctx.setVariable("destBankAccountID", bankAccount.getID());
		ctx.setVariable("destuserid", bankAccount.getUserID());

		try {
			bankAccount = cdao.findBankAccountByID(bankAccountID);
		} catch (SQLException e) {
			// for debugging only e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover transfers");
			return;
		}

		ctx.setVariable("balance", bankAccount.getBalance());
		ctx.setVariable("bankAccountID", bankAccount.getID());
		ctx.setVariable("userid", bankAccount.getUserID());

		templateEngine.process(path, ctx, response.getWriter());

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
