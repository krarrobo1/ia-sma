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
import jade.lang.acl.MessageTemplate;
import java.io.IOException;
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
                String[] keyWords = raw.split(";");
                JSONArray results = PostController.getPosts();
                JSONArray saved = new JSONArray();
                Iterator it = results.iterator();

                while (it.hasNext()) {
                    Object rawObject = it.next();
                    JSONObject object = (JSONObject) rawObject;

                    JSONArray tags = (JSONArray) object.get("tags");
                    Object[] topics = (Object[]) tags.toArray();

                    for (int i = 0; i < keyWords.length; i++) {
                        for (int j = 0; j < topics.length; j++) {
                            if (keyWords[i].equals(String.valueOf(topics[j]))) {
                                saved.add(object);
                            }
                        }
                    }

                }
                System.out.println("SAVED" + saved.toJSONString());

                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.PROPOSE);
                try {
                    reply.setContentObject(saved);
                    myAgent.send(reply);
                    System.out.println("enviando respuesta...");
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }

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
