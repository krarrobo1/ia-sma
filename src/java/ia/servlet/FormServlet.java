package ia.servlet;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import jade.core.AID;
import jade.core.Profile;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.util.leap.Properties;
import jade.wrapper.ControllerException;
import jade.wrapper.gateway.JadeGateway;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

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
 * @author baesparza
 */
@WebServlet(name = "FormServlet", urlPatterns = "/search")
public class FormServlet extends HttpServlet {

    public void init(ServletConfig config) throws ServletException {
        Properties pp = new Properties();
        pp.setProperty(Profile.MAIN_HOST, "localhost");
        pp.setProperty(Profile.MAIN_PORT, "2000");
        pp.setProperty(Profile.CONTAINER_NAME, "Main-Container");

        JadeGateway.init(null, pp);

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String rawParams = request.getParameter("keyword");

        String[] keyWords = rawParams.split(";");
        System.out.println("Palabras clave: " + Arrays.toString(keyWords));

        ProcessBehaviour behaviour = new ProcessBehaviour(rawParams);

        try {
            JadeGateway.execute(behaviour);
            PrintWriter out = response.getWriter();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.print(behaviour.getAnswer());
            out.flush();
        } catch (ControllerException | InterruptedException ex) {
            System.out.println(ex.getMessage());
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
