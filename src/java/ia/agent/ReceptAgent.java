/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ia.agent;

import jade.core.Agent;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import org.json.simple.*;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.UnreadableException;
import java.io.IOException;


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

                       
                        step = 1;
                    } else {
                        block();
                    }

                    break;

                case 1:
                     mtemp = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
                    ACLMessage fbmsg = myAgent.receive(mtemp);
                   
                    ACLMessage reply = message.createReply();

                    if (fbmsg != null) {
                        try {
                            System.out.println("Recibiendo respuesta de FBBOTs");
                            reply.setContentObject(fbmsg.getContentObject());
                            myAgent.send(reply);
                            step++;
                            
                        } catch (UnreadableException | IOException ex) {
                            System.out.println(ex.getMessage());
                        }
                    } else {
                        block();
                    }
                    
                    break;
                    
                case 2:
                    step = 0;
                    break;
            }
        }
    }
}
