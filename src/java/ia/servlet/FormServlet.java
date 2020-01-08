package ia.servlet;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import jade.core.AID;
import jade.core.Profile;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.leap.Properties;
import jade.wrapper.ControllerException;
import jade.wrapper.gateway.JadeGateway;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;


import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author richard
 */
@WebServlet(name = "FormServlet", urlPatterns = "/search")
public class FormServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    public void init(ServletConfig config) throws ServletException{
        Properties pp = new Properties();
        pp.setProperty(Profile.MAIN_HOST, "localhost");
        pp.setProperty(Profile.MAIN_PORT, "2000");
        JadeGateway.init(null, pp);
     
    
    }
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet SearchServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet SearchServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        
        String rawParams = request.getParameter("keyword");
        
        String [] keyWords = rawParams.split(",");
        System.out.println("Palabras clave: " + Arrays.toString(keyWords));
        
        ProcessBehaviour behaviour = new ProcessBehaviour(rawParams);
       
    
        try {
            JadeGateway.execute(behaviour);
       
            PrintWriter out = response.getWriter();
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet SearchServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet SearchServlet at " + behaviour.getAnswer() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        } catch (ControllerException ex) {
            Logger.getLogger(FormServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(FormServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
    
    public class SendBehaviour extends OneShotBehaviour{
		String content;
		SendBehaviour(String content){
			this.content = content;
		}
		@Override
		public void action() {
			// TODO Auto-generated method stub
			final ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			AID receiver = new AID("Reception", false);
			msg.addReceiver(receiver);
			msg.setContent(content);
			myAgent.send(msg);
		}
		
	}
	
	private class ProcessBehaviour extends Behaviour {
		private boolean stop = false;
		int step = 0;
		String content;
		String convId;
		String answer;

		public ProcessBehaviour(String content) {
			super();
			this.content = content;
			convId = String.valueOf(System.currentTimeMillis());
		}


		public String getAnswer() {
			return answer;
		}

		@Override
		public void action() {
			switch (step) {
			case 0:
			
				final ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				AID receiver = new AID("Reception", false);
				msg.addReceiver(receiver);
				msg.setContent(content);
				msg.setConversationId(convId);
				myAgent.send(msg);
				step = 1;
				break;
			case 1:
				MessageTemplate mt = MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.INFORM),
						MessageTemplate.MatchConversationId(convId));
				ACLMessage answer = myAgent.receive(mt);
				if (answer != null) {
					stopProcess(convId + " - " + answer.getContent());
				} else
					block();
				break;
			}
		}

		private void stopProcess(String ans) {
			answer = ans;
			System.out.println(answer);
			stop = true;
		}

		@Override
		public boolean done() {
			return stop;
		}
	}

}
