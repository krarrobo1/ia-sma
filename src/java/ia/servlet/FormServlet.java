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
import jade.lang.acl.UnreadableException;
import jade.util.leap.Properties;
import jade.wrapper.ControllerException;
import jade.wrapper.gateway.JadeGateway;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.*;

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
    public void init(ServletConfig config) throws ServletException {
        Properties pp = new Properties();
        pp.setProperty(Profile.MAIN_HOST, "localhost");
        pp.setProperty(Profile.MAIN_PORT, "2000");
        pp.setProperty(Profile.CONTAINER_NAME, "Main-Container");
        
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

        String[] keyWords = rawParams.split(",");
        System.out.println("Palabras clave: " + Arrays.toString(keyWords));

        ProcessBehaviour behaviour = new ProcessBehaviour(rawParams);

        try {
            JadeGateway.execute(behaviour);
            request.setAttribute("fb-response", behaviour.getAnswer());
            request.getRequestDispatcher("result.jsp").forward(request, response);

            /*PrintWriter out = response.getWriter();
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<style>\n"
                    + "table, th, td {\n"
                    + "  border: 1px solid black;\n"
                    + "  border-collapse: collapse;\n"
                    + "}\n"
                    + "th, td {\n"
                    + "  padding: 5px;\n"
                    + "  text-align: left;\n"
                    + "}\n"+
                    "body{"
                    + "font-family: Verdana, Geneva, sans-serif;"
                    + "}"
                    + "</style>");
            out.println("<title>Servlet SearchServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1> Resultado de la búsqueda </h1>");

            //out.println("<h1>Servlet SearchServlet at " + behaviour.getAnswer() + "</h1>");
            JSONArray jsonArr = behaviour.getAnswer();

            Iterator itemp = jsonArr.iterator();

            while (itemp.hasNext()) {
                Object temp = itemp.next();
                JSONObject objTemp = (JSONObject) temp;
                String topic = (String) objTemp.get("page");
                out.println("<table>");
                out.println("<caption>" + topic + "</caption>");

                out.println("<tr>");
                out.println("<th>ID</th>");
                out.println("<th>Content</th>");
                out.println("</tr>");

                JSONArray arrTemp = (JSONArray) objTemp.get("posts");

                Iterator jtemp = arrTemp.iterator();

                while (jtemp.hasNext()) {
                   
                    Object jobject = jtemp.next();

                    JSONObject obj = (JSONObject) jobject;

                    String id = (String) obj.get("id");
                    String msj = (String) obj.get("message");
                    out.println("<tr>");
                    out.println("<td>" + id + "</td>");
                    out.println("<td>" + msj + "</td>");
                    out.println("</tr>");
                   

                }
                out.println("<br>");
                out.println("<table>");
            }
            
            out.println("<h1><a href='http://localhost:8080/FbSma/index.html'>Volver al Buscador</a><h1>");

            out.println("</body>");
            out.println("</html>");

            /*RequestDispatcher rd = request.getRequestDispatcher("newjsp.jsp");
           rd.forward(request, response);*/
        } catch (ControllerException | InterruptedException ex) {
            System.out.println(ex.getMessage());
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

    public class SendBehaviour extends OneShotBehaviour {

        String content;

        SendBehaviour(String content) {
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
        JSONArray answer;

        public ProcessBehaviour(String content) {
            super();
            this.content = content;
            convId = String.valueOf(System.currentTimeMillis());
        }

        public JSONArray getAnswer() {
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
                        try {
                            JSONArray jsonResponse = (JSONArray) answer.getContentObject();
                            stopProcess(jsonResponse);
                            // stopProcess(convId + " - " + jsonResponse.toJSONString());
                        } catch (UnreadableException ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        block();
                    }
                    break;
            }
        }

        private void stopProcess(JSONArray ans) {
            answer = ans;
            //System.out.println(answer);
            stop = true;
        }

        @Override
        public boolean done() {
            return stop;
        }
    }

}
