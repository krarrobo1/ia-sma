/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ia.agent;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import ia.controller.PostController;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.MessageTemplate;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.*;

/**
 *
 * @author richard
 */
public class FbAgent extends Agent {

    @Override
    protected void setup() {
        System.out.println("FbAgent iniciado...");
        this.addBehaviour(new Listen());
    }

    class Listen extends CyclicBehaviour {

        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);

            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                System.out.println("Recibido: " + msg);
                String raw = msg.getContent();
                String[] keyWords = raw.split(",");
                JSONArray results = PostController.getPosts();
                JSONArray saved = new JSONArray();
                Iterator it = results.iterator();
                while (it.hasNext()) {
                    Object temp = it.next();
                    JSONObject objTemp = (JSONObject) temp;
                    String topic = (String) objTemp.get("page");
                    
                    for (int i = 0; i < keyWords.length; i++) {
                        if (topic.equals(keyWords[i])) {
                            saved.add(objTemp);
                            break;
                        }
                    }
                    
                }
                System.out.println("SAVED" + saved.toJSONString());
                
                ACLMessage reply = msg.createReply();
                
                
                if(saved.size() > 0){
                    reply.setPerformative(ACLMessage.PROPOSE);
                    try {
                        reply.setContentObject(saved);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }else{
                    reply.setPerformative(ACLMessage.REFUSE);
                    reply.setContent("no-disponible");
                }
               
                
                
                
                myAgent.send(reply);
                System.out.println("enviando respuesta...");
                
                //super.reset();
                
                // Enviar info al PARSER BOT y retornar a la web
            } else {
                block();
            }
        }

        /*@Override
        public boolean done() {
            return false;
        }*/

    }
}
