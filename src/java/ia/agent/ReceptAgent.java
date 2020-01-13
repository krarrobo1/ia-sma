/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ia.agent;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import org.json.simple.*;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.UnreadableException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReceptAgent extends Agent {

    int conversationId = 0;
    MessageTemplate mtemp;
    ACLMessage message;
    String keyWords;
    int step = 0;
    Boolean finished = false;

    protected void setup() {
        System.out.println(this.getAID());
        this.addBehaviour(new ReceptBahaviour());
    }

    class ReceptBahaviour extends CyclicBehaviour {

        @Override
        public void action() {

            switch (step) {
                case 0:
                    MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
                    message = receive(mt);

                    if (message != null) {
                        keyWords = message.getContent();

                        // Enviar a FB Bot Agent
                        ACLMessage msg = new ACLMessage(ACLMessage.CFP);
                        AID fbAgent = new AID("FbAgent", false);

                        msg.addReceiver(fbAgent);
                        msg.setContent(keyWords);
                        msg.setConversationId("fb-consult");
                        msg.setReplyWith("cfp" + System.currentTimeMillis());
                        myAgent.send(msg);

                        mtemp = MessageTemplate.and(MessageTemplate.MatchConversationId("fb-consult"), MessageTemplate.MatchInReplyTo(msg.getReplyWith()));
                        step++;

                    } else {
                        block();
                    }

                    break;

                case 1:
                    ACLMessage fbmsg = myAgent.receive(mtemp);
                    if (fbmsg != null) {
                        try {
                            System.out.println("Recibiendo respuesta de FBBOTs");
                            JSONArray json = (JSONArray) fbmsg.getContentObject();
                            
                            System.out.println(json.toJSONString());

                            ACLMessage reply = message.createReply();
                            reply.setContentObject(fbmsg.getContentObject());

                            myAgent.send(reply);
                            
                            step++;
                            
                        

                        } catch (UnreadableException ex) {
                            ex.printStackTrace();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        block();
                    }
                    
                    break;
            }

        }

        /*@Override
        public boolean done() {
            return false;
        }*/
    }

}
