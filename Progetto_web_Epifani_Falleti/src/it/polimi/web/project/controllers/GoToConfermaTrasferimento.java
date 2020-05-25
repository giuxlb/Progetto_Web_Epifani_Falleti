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

import it.polimi.web.project.DAO.ContoDao;
import it.polimi.web.project.beans.Conto;
import it.polimi.web.project.utils.ConnectionHandler;

/**
 * Servlet implementation class GoToConfermaTrasferimento
 */
@WebServlet("/GoToConfermaTrasferimento")
public class GoToConfermaTrasferimento extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GoToConfermaTrasferimento() {
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

		String indexPath = getServletContext().getContextPath() + "/index.html";
		HttpSession session = request.getSession();
		if (session.getAttribute("user") == null || session.isNew()) {
			response.sendRedirect(indexPath);
		}

		Integer destcontoid = (Integer) session.getAttribute("destContoID");
		Integer contoid = (Integer) session.getAttribute("ContoID");

		ContoDao cdao = new ContoDao(connection);
		Conto conto = new Conto();
		try {
			conto = cdao.findContoByContoID(destcontoid);
		} catch (SQLException e) {
			// for debugging only e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover transfers");
			return;
		}

		String path = "/ConfermaTrasferimento.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("saldoDest", conto.getSaldo());
		ctx.setVariable("destContoID", conto.getID());
		ctx.setVariable("destuserid", conto.getUserID());

		try {
			conto = cdao.findContoByContoID(contoid);
		} catch (SQLException e) {
			// for debugging only e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover transfers");
			return;
		}

		ctx.setVariable("saldo", conto.getSaldo());
		ctx.setVariable("ContoID", conto.getID());
		ctx.setVariable("userid", conto.getUserID());

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
