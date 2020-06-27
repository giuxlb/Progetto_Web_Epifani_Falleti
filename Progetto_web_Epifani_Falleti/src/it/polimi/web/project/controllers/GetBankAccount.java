package it.polimi.web.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
import it.polimi.web.project.DAO.TransferDao;
import it.polimi.web.project.beans.BankAccount;
import it.polimi.web.project.beans.Transfer;
import it.polimi.web.project.beans.User;
import it.polimi.web.project.utils.ConnectionHandler;

/**
 * Servlet implementation class GetConto
 */
@WebServlet("/GetBankAccount")
public class GetBankAccount extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetBankAccount() {
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
		// TODO Auto-generated method stub
		String indexPath = getServletContext().getContextPath() + "/GoToIndex";
		HttpSession session = request.getSession();

		if (session.getAttribute("user") == null || session.isNew()) {
			response.sendRedirect(indexPath);
		}

		Integer bankAccountID = null;
		Integer balance = null;
		try {
			bankAccountID = Integer.parseInt(request.getParameter("bankAccountID"));
		} catch (NumberFormatException | NullPointerException e) {
			// only for debugging e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}
		BankAccountDao cdao = new BankAccountDao(connection);
		User u = (User) session.getAttribute("user");
		List<BankAccount> bankAccounts = new ArrayList<BankAccount>();
		try {
			bankAccounts = cdao.findBankAccountsByUser(u.getId());
		} catch (SQLException e) {
			// for debugging only e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover bank accounts");
			return;
		}
		boolean ok = false;
		for (int i = 0; i < bankAccounts.size(); i++) {
			if (bankAccounts.get(i).getID() == bankAccountID) {
				ok = true;
				balance = bankAccounts.get(i).getBalance();
				break;
			}

		}
		if (!ok) {
			response.sendRedirect(indexPath);
		}

		session.setAttribute("bankAccountID", bankAccountID);
		session.setAttribute("balance", balance);

		TransferDao tDao = new TransferDao(connection);

		List<Transfer> transfers = new ArrayList<Transfer>();
		try {
			transfers = tDao.findTransfersByBankAccount(bankAccountID);
		} catch (SQLException e) {
			// for debugging only e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover transfers");
			return;
		}
		System.out.println(transfers.size());
		session.setAttribute("transfers", transfers);
		String path = "/StatoConto.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("transfers", transfers);
		ctx.setVariable("balance", balance);
		ctx.setVariable("bankAccountID", bankAccountID);
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
